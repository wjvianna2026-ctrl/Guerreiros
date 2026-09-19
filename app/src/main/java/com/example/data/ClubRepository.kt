package com.example.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClubRepository(private val dao: ClubDao) {

    val members: Flow<List<MemberEntity>> = dao.getAllMembers()
    val monthlyDues: Flow<List<MonthlyDueEntity>> = dao.getAllMonthlyDues()
    val loans: Flow<List<LoanRequestEntity>> = dao.getAllLoans()
    val loanInstallments: Flow<List<LoanInstallmentEntity>> = dao.getAllLoanInstallments()
    val collectivePurchases: Flow<List<CollectivePurchaseEntity>> = dao.getAllPurchases()
    val purchaseQuotas: Flow<List<PurchaseQuotaEntity>> = dao.getAllPurchaseQuotas()
    val transactions: Flow<List<CashTransactionEntity>> = dao.getAllTransactions()
    val clubSettings: Flow<ClubSettingsEntity?> = dao.getClubSettings()

    private fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }

    suspend fun addMember(member: MemberEntity) {
        dao.insertMember(member)
    }

    suspend fun updateMember(member: MemberEntity) {
        dao.updateMember(member)
    }

    suspend fun deleteMember(member: MemberEntity) {
        dao.deleteMember(member)
    }

    suspend fun payMonthlyDue(due: MonthlyDueEntity, method: String, notes: String?, operatorName: String) {
        dao.setDueStatus(
            id = due.id,
            status = "Paga",
            paidDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            method = method,
            notes = notes
        )
        // Log transaction
        dao.insertTransaction(
            CashTransactionEntity(
                type = "Entrada",
                category = "Mensalidade",
                amount = due.amount,
                description = "Baixa de Mensalidade ${due.monthYear} - ${due.memberNickname} (via $method)",
                memberName = due.memberNickname,
                date = getCurrentTimestamp(),
                operator = operatorName
            )
        )
        reconcileCashBalance()
    }

    suspend fun reopenMonthlyDue(due: MonthlyDueEntity, operatorName: String) {
        if (due.status == "Paga") {
            dao.insertTransaction(
                CashTransactionEntity(
                    type = "Saída",
                    category = "Estorno Mensalidade",
                    amount = due.amount,
                    description = "Estorno / Reabertura de Mensalidade ${due.monthYear} - ${due.memberNickname}",
                    memberName = due.memberNickname,
                    date = getCurrentTimestamp(),
                    operator = operatorName
                )
            )
        }
        dao.setDueStatus(due.id, "Pendente", null, null, null)
        reconcileCashBalance()
    }

    suspend fun generateMonthlyDues(members: List<MemberEntity>, monthYear: String, dueDate: String, amount: Double) {
        val newDues = members.filter { it.status == "Ativo" }.map { member ->
            MonthlyDueEntity(
                memberId = member.id,
                memberName = member.fullName,
                memberNickname = member.roadNickname,
                monthYear = monthYear,
                amount = amount,
                dueDate = dueDate,
                status = "Pendente"
            )
        }
        if (newDues.isNotEmpty()) {
            dao.insertMonthlyDues(newDues)
            dao.insertTransaction(
                CashTransactionEntity(
                    type = "Lançamento",
                    category = "Mensalidade",
                    amount = newDues.size * amount,
                    description = "Geração de lote de mensalidades $monthYear para ${newDues.size} irmãos",
                    date = getCurrentTimestamp(),
                    operator = "Tesoureiro"
                )
            )
        }
    }

    suspend fun createLoan(
        member: MemberEntity,
        amount: Double,
        ratePercent: Double,
        installmentsCount: Int,
        purpose: String,
        firstDueDate: String,
        operatorName: String
    ) {
        val totalInterest = amount * (ratePercent / 100.0) * installmentsCount
        val totalPayable = amount + totalInterest
        val installmentPrincipal = amount / installmentsCount
        val installmentInterest = totalInterest / installmentsCount
        val installmentTotal = totalPayable / installmentsCount

        val loan = LoanRequestEntity(
            memberId = member.id,
            memberName = member.fullName,
            memberNickname = member.roadNickname,
            totalAmount = amount,
            monthlyInterestRate = ratePercent,
            installmentsCount = installmentsCount,
            purpose = purpose,
            requestDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            firstDueDate = firstDueDate,
            status = "Ativo",
            totalInterest = totalInterest,
            totalPayable = totalPayable,
            notes = "Aprovado pelo conselho do Moto Clube"
        )
        val loanId = dao.insertLoan(loan)

        // Generate installments
        val installmentsList = mutableListOf<LoanInstallmentEntity>()
        for (i in 1..installmentsCount) {
            val dueStr = calculateNextDueDate(firstDueDate, i - 1)
            installmentsList.add(
                LoanInstallmentEntity(
                    loanId = loanId,
                    memberId = member.id,
                    memberNickname = member.roadNickname,
                    installmentNumber = i,
                    dueDate = dueStr,
                    principalAmount = installmentPrincipal,
                    interestAmount = installmentInterest,
                    totalAmount = installmentTotal,
                    status = "Pendente"
                )
            )
        }
        dao.insertLoanInstallments(installmentsList)

        // Deduct from cash balance (fund release)
        dao.insertTransaction(
            CashTransactionEntity(
                type = "Saída",
                category = "Fundo Rotativo",
                amount = amount,
                description = "Liberação de Empréstimo Social para ${member.roadNickname} - $purpose ($installmentsCount x R$ ${"%.2f".format(Locale.US, installmentTotal)})",
                memberName = member.roadNickname,
                date = getCurrentTimestamp(),
                operator = operatorName
            )
        )
        reconcileCashBalance()
    }

    suspend fun payLoanInstallment(installment: LoanInstallmentEntity, method: String, operatorName: String) {
        dao.setInstallmentStatus(
            id = installment.id,
            status = "Paga",
            paidDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            method = method
        )
        dao.insertTransaction(
            CashTransactionEntity(
                type = "Entrada",
                category = "Parcela Empréstimo",
                amount = installment.totalAmount,
                description = "Recebimento Parcela ${installment.installmentNumber} Empréstimo - ${installment.memberNickname} (Principal R$ ${"%.2f".format(Locale.US, installment.principalAmount)} + Juros Social R$ ${"%.2f".format(Locale.US, installment.interestAmount)})",
                memberName = installment.memberNickname,
                date = getCurrentTimestamp(),
                operator = operatorName
            )
        )
        reconcileCashBalance()
    }

    suspend fun createCollectivePurchase(
        title: String,
        supplier: String,
        totalCost: Double,
        installmentsCount: Int,
        firstDueDate: String,
        targetType: String,
        selectedMembers: List<MemberEntity>,
        operatorName: String
    ) {
        if (selectedMembers.isEmpty()) return

        val namesStr = selectedMembers.joinToString(", ") { it.roadNickname }
        val purchase = CollectivePurchaseEntity(
            title = title,
            supplier = supplier,
            totalCost = totalCost,
            installmentsCount = installmentsCount,
            purchaseDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            firstDueDate = firstDueDate,
            targetType = targetType,
            assignedMemberNames = namesStr,
            status = "Ativo"
        )
        val purchaseId = dao.insertPurchase(purchase)

        // Total divided equally among selected members and installments
        val totalShares = selectedMembers.size * installmentsCount
        val amountPerQuota = totalCost / totalShares

        val quotas = mutableListOf<PurchaseQuotaEntity>()
        for (member in selectedMembers) {
            for (i in 1..installmentsCount) {
                val dueStr = calculateNextDueDate(firstDueDate, i - 1)
                quotas.add(
                    PurchaseQuotaEntity(
                        purchaseId = purchaseId,
                        purchaseTitle = title,
                        memberId = member.id,
                        memberName = member.fullName,
                        memberNickname = member.roadNickname,
                        installmentNumber = i,
                        dueDate = dueStr,
                        amount = amountPerQuota,
                        status = "Pendente"
                    )
                )
            }
        }
        dao.insertPurchaseQuotas(quotas)

        // Deduct full cost from club cash for up-front supplier payment
        dao.insertTransaction(
            CashTransactionEntity(
                type = "Saída",
                category = "Compra Coletiva",
                amount = totalCost,
                description = "Pagamento Fornecedor $supplier - $title ($namesStr)",
                date = getCurrentTimestamp(),
                operator = operatorName
            )
        )
        reconcileCashBalance()
    }

    suspend fun payPurchaseQuota(quota: PurchaseQuotaEntity, operatorName: String) {
        dao.setQuotaStatus(
            id = quota.id,
            status = "Paga",
            paidDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        )
        dao.insertTransaction(
            CashTransactionEntity(
                type = "Entrada",
                category = "Rateio Compra",
                amount = quota.amount,
                description = "Recebimento Parcela ${quota.installmentNumber} Rateio ${quota.purchaseTitle} - ${quota.memberNickname}",
                memberName = quota.memberNickname,
                date = getCurrentTimestamp(),
                operator = operatorName
            )
        )
        reconcileCashBalance()
    }

    suspend fun updateSettings(settings: ClubSettingsEntity) {
        dao.updateSettings(settings)
    }

    suspend fun addMonthlyDue(due: MonthlyDueEntity) {
        dao.insertMonthlyDue(due)
        dao.insertTransaction(
            CashTransactionEntity(
                type = "Lançamento",
                category = "Mensalidade",
                amount = due.amount,
                description = "Lançamento avulso de mensalidade ${due.monthYear} para ${due.memberNickname}",
                memberName = due.memberNickname,
                date = getCurrentTimestamp(),
                operator = "Tesouraria"
            )
        )
    }

    suspend fun updateMonthlyDue(due: MonthlyDueEntity) {
        dao.updateMonthlyDue(due)
    }

    suspend fun deleteMonthlyDue(due: MonthlyDueEntity) {
        if (due.status == "Paga") {
            dao.insertTransaction(
                CashTransactionEntity(
                    type = "Saída",
                    category = "Estorno Mensalidade",
                    amount = due.amount,
                    description = "Estorno por exclusão de mensalidade paga ${due.monthYear} - ${due.memberNickname}",
                    memberName = due.memberNickname,
                    date = getCurrentTimestamp(),
                    operator = "Diretoria"
                )
            )
        }
        dao.deleteMonthlyDue(due)
        reconcileCashBalance()
    }

    suspend fun deleteLoan(loan: LoanRequestEntity) {
        dao.deleteInstallmentsByLoanId(loan.id)
        dao.deleteLoan(loan)
        dao.insertTransaction(
            CashTransactionEntity(
                type = "Entrada",
                category = "Estorno Fundo Rotativo",
                amount = loan.totalAmount,
                description = "Estorno por cancelamento de empréstimo de ${loan.memberNickname}",
                memberName = loan.memberNickname,
                date = getCurrentTimestamp(),
                operator = "Diretoria"
            )
        )
        reconcileCashBalance()
    }

    suspend fun updateLoan(loan: LoanRequestEntity) {
        dao.updateLoan(loan)
    }

    suspend fun deleteCollectivePurchase(purchase: CollectivePurchaseEntity) {
        dao.deleteQuotasByPurchaseId(purchase.id)
        dao.deletePurchase(purchase)
        dao.insertTransaction(
            CashTransactionEntity(
                type = "Entrada",
                category = "Estorno Compra Coletiva",
                amount = purchase.totalCost,
                description = "Estorno por cancelamento de compra coletiva: ${purchase.title}",
                date = getCurrentTimestamp(),
                operator = "Diretoria"
            )
        )
        reconcileCashBalance()
    }

    suspend fun updateCollectivePurchase(purchase: CollectivePurchaseEntity) {
        dao.updatePurchase(purchase)
    }

    suspend fun addCashTransaction(
        type: String, // "Entrada" or "Saída"
        category: String,
        amount: Double,
        description: String,
        memberName: String? = null,
        operator: String
    ) {
        dao.insertTransaction(
            CashTransactionEntity(
                type = type,
                category = category,
                amount = amount,
                description = description,
                memberName = memberName,
                date = getCurrentTimestamp(),
                operator = operator
            )
        )
        reconcileCashBalance()
    }

    suspend fun updateCashTransaction(transaction: CashTransactionEntity) {
        dao.updateTransaction(transaction)
        reconcileCashBalance()
    }

    suspend fun deleteCashTransaction(transaction: CashTransactionEntity) {
        dao.deleteTransaction(transaction)
        reconcileCashBalance()
    }

    suspend fun reconcileCashBalance(): Double {
        val txs = dao.getAllTransactionsList()
        val balance = if (txs.isEmpty()) {
            0.0
        } else {
            val income = txs.filter { it.type == "Entrada" }.sumOf { it.amount }
            val expense = txs.filter { it.type == "Saída" }.sumOf { it.amount }
            income - expense
        }
        dao.setCashBalance(balance)
        return balance
    }

    suspend fun resetAllFinancialData() {
        dao.deleteAllTransactions()
        dao.deleteAllMonthlyDues()
        dao.deleteAllLoanInstallments()
        dao.deleteAllLoans()
        dao.deleteAllPurchaseQuotas()
        dao.deleteAllPurchases()
        dao.setCashBalance(0.0)
    }

    suspend fun clearCashBook() {
        dao.deleteAllTransactions()
        dao.setCashBalance(0.0)
    }

    private fun calculateNextDueDate(startDueDate: String, monthOffset: Int): String {
        try {
            val parts = startDueDate.split("/")
            if (parts.size == 3) {
                val day = parts[0].toInt()
                val month = parts[1].toInt()
                val year = parts[2].toInt()

                var newMonth = month + monthOffset
                var newYear = year
                while (newMonth > 12) {
                    newMonth -= 12
                    newYear += 1
                }
                return "%02d/%02d/%04d".format(Locale.US, day, newMonth, newYear)
            }
        } catch (_: Exception) {}
        return startDueDate
    }
}
