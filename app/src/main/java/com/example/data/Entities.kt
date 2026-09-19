package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class MemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val roadNickname: String, // "Vulgo de estrada" (ex: Trovão, Lobo Solitário, Caveira)
    val role: String, // Presidente, Vice-Presidente, Diretor Financeiro, Tesoureiro, Escudo Fechado, Meio Escudo, Próspero
    val phone: String, // WhatsApp with DDD
    val email: String,
    val pixKey: String,
    val joinDate: String,
    val status: String = "Ativo", // Ativo, Afastado, Inadimplente
    val notes: String = ""
)

@Entity(tableName = "monthly_dues")
data class MonthlyDueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val memberId: Long,
    val memberName: String,
    val memberNickname: String,
    val monthYear: String, // ex: "09/2026"
    val amount: Double = 50.0,
    val dueDate: String, // ex: "10/09/2026"
    val status: String = "Pendente", // Paga, Pendente, Em Atraso
    val paidDate: String? = null,
    val paymentMethod: String? = null, // PIX, Dinheiro, Transferência
    val receiptNotes: String? = null
)

@Entity(tableName = "loan_requests")
data class LoanRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val memberId: Long,
    val memberName: String,
    val memberNickname: String,
    val totalAmount: Double,
    val monthlyInterestRate: Double = 1.0, // Taxa social (ex: 0%, 1%, 2%)
    val installmentsCount: Int, // ex: 6
    val purpose: String, // Conserto de moto, Viagem / Encontro, Socorro Pessoal
    val requestDate: String,
    val firstDueDate: String,
    val status: String = "Ativo", // Ativo, Quitado, Cancelado
    val totalInterest: Double,
    val totalPayable: Double,
    val notes: String = ""
)

@Entity(tableName = "loan_installments")
data class LoanInstallmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val loanId: Long,
    val memberId: Long,
    val memberNickname: String,
    val installmentNumber: Int,
    val dueDate: String,
    val principalAmount: Double,
    val interestAmount: Double,
    val totalAmount: Double,
    val status: String = "Pendente", // Pendente, Paga, Em Atraso
    val paidDate: String? = null,
    val paymentMethod: String? = null
)

@Entity(tableName = "collective_purchases")
data class CollectivePurchaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String, // ex: "Coletes de Couro Bordados", "Pneus Metzeler 180"
    val supplier: String,
    val totalCost: Double,
    val installmentsCount: Int = 1,
    val purchaseDate: String,
    val firstDueDate: String,
    val targetType: String = "Rateio Geral", // "Rateio Geral" ou "Individual"
    val assignedMemberNames: String = "",
    val status: String = "Ativo", // Ativo, Quitado
    val notes: String = ""
)

@Entity(tableName = "purchase_quotas")
data class PurchaseQuotaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val purchaseId: Long,
    val purchaseTitle: String,
    val memberId: Long,
    val memberName: String,
    val memberNickname: String,
    val installmentNumber: Int,
    val dueDate: String,
    val amount: Double,
    val status: String = "Pendente", // Pendente, Paga, Em Atraso
    val paidDate: String? = null
)

@Entity(tableName = "cash_transactions")
data class CashTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "Entrada" ou "Saída"
    val category: String, // "Mensalidade", "Parcela Empréstimo", "Juros Social", "Rateio Compra", "Despesa Clube", "Doação / Fundo", "Ajuste Caixa"
    val amount: Double,
    val description: String,
    val memberName: String? = null,
    val date: String,
    val operator: String = "Diretoria Financeira"
)

@Entity(tableName = "club_settings")
data class ClubSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val clubName: String = "Guerreiros do Bem Moto Clube",
    val motto: String = "Honra, Respeito e Irmandade nas Estradas",
    val cnpjOrRegister: String = "48.291.802/0001-94",
    val pixKey: String = "financeiro@guerreirosdobem.mc.br",
    val pixKeyType: String = "E-mail",
    val pixHolderName: String = "Guerreiros do Bem MC - Caixa Social",
    val defaultMonthlyDue: Double = 50.0,
    val currentCashBalance: Double = 0.0,
    val rotatingFundBalance: Double = 3200.0,
    val cloudSyncStatus: String = "Sincronizado Localmente (Pronto para Firebase)"
)
