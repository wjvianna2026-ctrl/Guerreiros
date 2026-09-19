package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CashTransactionEntity
import com.example.data.ClubRepository
import com.example.data.ClubSettingsEntity
import com.example.data.CollectivePurchaseEntity
import com.example.data.LoanInstallmentEntity
import com.example.data.LoanRequestEntity
import com.example.data.MemberEntity
import com.example.data.MonthlyDueEntity
import com.example.data.PurchaseQuotaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

enum class UserRole(val label: String, val badge: String) {
    ADMIN("Administrador / Diretor", "ADMIN"),
    TREASURER("Tesoureiro / Gestor", "TESOURARIA"),
    MEMBER("Membro / Irmão", "IRMÃO")
}

data class AuthUser(
    val email: String,
    val name: String,
    val role: UserRole,
    val memberId: Long? = null,
    val canInsert: Boolean = true,
    val canEdit: Boolean = true,
    val canDelete: Boolean = true
)

data class UserPermissionConfig(
    val memberId: Long,
    val memberName: String,
    val nickname: String,
    val role: UserRole,
    val canInsert: Boolean = true,
    val canEdit: Boolean = true,
    val canDelete: Boolean = true,
    val accessStatus: String = "Autorizado" // "Autorizado", "Bloqueado", "Pendente"
)

enum class NavigationTab(val title: String) {
    DASHBOARD("Caixa & Painel"),
    MEMBERS("Irmãos"),
    DUES("Mensalidades"),
    LOANS("Fundo Rotativo"),
    PURCHASES("Compras Coletivas"),
    REPORTS("Relatórios & Ajustes")
}

data class DashboardKpis(
    val cashBalance: Double = 0.0,
    val totalPendingReceivables: Double = 0.0,
    val totalActiveLoans: Double = 0.0,
    val activeMembersCount: Int = 0,
    val complianceRatePercent: Int = 0,
    val duesCompliancePercent: Int = 0
)

data class UpcomingDueItem(
    val type: String, // "Mensalidade", "Empréstimo", "Compra Coletiva"
    val memberNickname: String,
    val memberPhone: String,
    val description: String,
    val dueDate: String,
    val amount: Double,
    val daysUntilDue: Int,
    val rawDue: MonthlyDueEntity? = null,
    val rawLoanInstallment: LoanInstallmentEntity? = null,
    val rawPurchaseQuota: PurchaseQuotaEntity? = null,
    val id: Long = rawDue?.id ?: rawLoanInstallment?.id ?: rawPurchaseQuota?.id ?: 0L
)

class ClubViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ClubRepository

    init {
        val db = AppDatabase.getInstance(application)
        repository = ClubRepository(db.clubDao())
        viewModelScope.launch {
            repository.reconcileCashBalance()
        }
    }

    // Role & Navigation
    private val _currentRole = MutableStateFlow(UserRole.ADMIN)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _selectedMemberId = MutableStateFlow<Long?>(5L) // Defaults to "Lobo Solitário" for member view
    val selectedMemberId: StateFlow<Long?> = _selectedMemberId.asStateFlow()

    private val _currentTab = MutableStateFlow(NavigationTab.DASHBOARD)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    // Authentication State (Email and Password)
    private val _isAuthenticated = MutableStateFlow(true) // Default to true or authenticated session
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _currentUser = MutableStateFlow<AuthUser?>(
        AuthUser(
            email = "diretoria@guerreirosdobem.mc.br",
            name = "Carlos Eduardo (Trovão)",
            role = UserRole.ADMIN,
            memberId = 1L,
            canInsert = true,
            canEdit = true,
            canDelete = true
        )
    )
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    // Permissions map for members (Gestor controls access)
    private val _userPermissions = MutableStateFlow<Map<Long, UserPermissionConfig>>(emptyMap())
    val userPermissions: StateFlow<Map<Long, UserPermissionConfig>> = _userPermissions.asStateFlow()

    // Cloud Firestore Sync State
    private val _syncStatus = MutableStateFlow("Sincronizado (Local & Firestore)")
    val syncStatus: StateFlow<String> = _syncStatus.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    // Data streams from repository
    val members: StateFlow<List<MemberEntity>> = repository.members
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monthlyDues: StateFlow<List<MonthlyDueEntity>> = repository.monthlyDues
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val loans: StateFlow<List<LoanRequestEntity>> = repository.loans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val loanInstallments: StateFlow<List<LoanInstallmentEntity>> = repository.loanInstallments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val collectivePurchases: StateFlow<List<CollectivePurchaseEntity>> = repository.collectivePurchases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val purchaseQuotas: StateFlow<List<PurchaseQuotaEntity>> = repository.purchaseQuotas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<CashTransactionEntity>> = repository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clubSettings: StateFlow<ClubSettingsEntity?> = repository.clubSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Calculated Dashboard KPIs
    private data class FinancialKpisIntermediate(
        val cash: Double,
        val totalReceivable: Double,
        val activeLoansTotal: Double,
        val duesList: List<MonthlyDueEntity>
    )

    val dashboardKpis: StateFlow<DashboardKpis> = combine(
        combine(
            transactions,
            monthlyDues,
            loanInstallments,
            purchaseQuotas,
            loans
        ) { txList, duesList, installments, quotas, loanList ->
            val cash = if (txList.isEmpty()) {
                0.0
            } else {
                val income = txList.filter { it.type == "Entrada" }.sumOf { it.amount }
                val expense = txList.filter { it.type == "Saída" }.sumOf { it.amount }
                income - expense
            }

            val pendingDues = duesList.filter { it.status != "Paga" }.sumOf { it.amount }
            val pendingLoans = installments.filter { it.status != "Paga" }.sumOf { it.totalAmount }
            val pendingQuotas = quotas.filter { it.status != "Paga" }.sumOf { it.amount }
            val totalReceivable = pendingDues + pendingLoans + pendingQuotas

            val activeLoansTotal = loanList.filter { it.status == "Ativo" }.sumOf { it.totalAmount }

            FinancialKpisIntermediate(cash, totalReceivable, activeLoansTotal, duesList)
        },
        members,
        clubSettings
    ) { fin, memberList, _ ->
        val activeMembers = memberList.filter { it.status == "Ativo" }
        val currentMonthStr = SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(Date())
        val targetDues = fin.duesList.filter { it.monthYear == currentMonthStr }.ifEmpty { fin.duesList }
        val paidCurrentDues = targetDues.count { it.status == "Paga" }
        val complianceRate = if (targetDues.isNotEmpty()) {
            ((paidCurrentDues.toDouble() / targetDues.size.toDouble()) * 100).toInt()
        } else {
            100
        }

        DashboardKpis(
            cashBalance = fin.cash,
            totalPendingReceivables = fin.totalReceivable,
            totalActiveLoans = fin.activeLoansTotal,
            activeMembersCount = activeMembers.size,
            complianceRatePercent = complianceRate,
            duesCompliancePercent = complianceRate
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardKpis())

    // Upcoming Dues for Alert (apenas vencimentos de 1 a 30 dias para vencer)
    val upcomingDueItems: StateFlow<List<UpcomingDueItem>> = combine(
        members,
        monthlyDues,
        loanInstallments,
        purchaseQuotas
    ) { memberList, duesList, installments, quotas ->
        val memberMap = memberList.associateBy { it.id }
        val items = mutableListOf<UpcomingDueItem>()

        for (due in duesList.filter { it.status != "Paga" }) {
            val days = calculateDaysUntilDueDate(due.dueDate)
            if (days in 1..30) {
                val m = memberMap[due.memberId]
                items.add(
                    UpcomingDueItem(
                        type = "Mensalidade",
                        memberNickname = due.memberNickname,
                        memberPhone = m?.phone ?: "",
                        description = "Mensalidade ${due.monthYear}",
                        dueDate = due.dueDate,
                        amount = due.amount,
                        daysUntilDue = days,
                        rawDue = due
                    )
                )
            }
        }

        for (inst in installments.filter { it.status != "Paga" }) {
            val days = calculateDaysUntilDueDate(inst.dueDate)
            if (days in 1..30) {
                val m = memberMap[inst.memberId]
                items.add(
                    UpcomingDueItem(
                        type = "Empréstimo",
                        memberNickname = inst.memberNickname,
                        memberPhone = m?.phone ?: "",
                        description = "Parcela #${inst.installmentNumber} Fundo Rotativo",
                        dueDate = inst.dueDate,
                        amount = inst.totalAmount,
                        daysUntilDue = days,
                        rawLoanInstallment = inst
                    )
                )
            }
        }

        for (quota in quotas.filter { it.status != "Paga" }) {
            val days = calculateDaysUntilDueDate(quota.dueDate)
            if (days in 1..30) {
                val m = memberMap[quota.memberId]
                items.add(
                    UpcomingDueItem(
                        type = "Compra Coletiva",
                        memberNickname = quota.memberNickname,
                        memberPhone = m?.phone ?: "",
                        description = "${quota.purchaseTitle} (Parc. ${quota.installmentNumber})",
                        dueDate = quota.dueDate,
                        amount = quota.amount,
                        daysUntilDue = days,
                        rawPurchaseQuota = quota
                    )
                )
            }
        }

        items.sortedBy { it.daysUntilDue }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and filter states
    val memberSearchQuery = MutableStateFlow("")
    val memberRoleFilter = MutableStateFlow("Todos")
    val duesStatusFilter = MutableStateFlow("Todos")

    // UI Actions
    fun setRole(role: UserRole) {
        _currentRole.value = role
    }

    fun setSelectedMember(memberId: Long?) {
        _selectedMemberId.value = memberId
    }

    fun setTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun addMember(member: MemberEntity) {
        viewModelScope.launch {
            repository.addMember(member)
        }
    }

    fun updateMember(member: MemberEntity) {
        viewModelScope.launch {
            repository.updateMember(member)
        }
    }

    fun deleteMember(member: MemberEntity) {
        viewModelScope.launch {
            repository.deleteMember(member)
        }
    }

    fun payMonthlyDue(due: MonthlyDueEntity, method: String, notes: String?) {
        val operator = when (_currentRole.value) {
            UserRole.ADMIN -> "Diretor Financeiro"
            UserRole.TREASURER -> "Tesoureiro"
            UserRole.MEMBER -> "Irmão (Auto-baixa)"
        }
        viewModelScope.launch {
            repository.payMonthlyDue(due, method, notes, operator)
        }
    }

    fun reopenMonthlyDue(due: MonthlyDueEntity) {
        val operator = when (_currentRole.value) {
            UserRole.ADMIN -> "Diretor Financeiro"
            UserRole.TREASURER -> "Tesoureiro"
            UserRole.MEMBER -> "Irmão"
        }
        viewModelScope.launch {
            repository.reopenMonthlyDue(due, operator)
        }
    }

    fun generateDues(monthYear: String, dueDate: String, amount: Double) {
        viewModelScope.launch {
            val list = members.value
            repository.generateMonthlyDues(list, monthYear, dueDate, amount)
        }
    }

    fun createLoan(member: MemberEntity, amount: Double, ratePercent: Double, count: Int, purpose: String, firstDue: String) {
        val operator = "Diretoria Financeira"
        viewModelScope.launch {
            repository.createLoan(member, amount, ratePercent, count, purpose, firstDue, operator)
        }
    }

    fun payLoanInstallment(installment: LoanInstallmentEntity, method: String) {
        val operator = "Tesoureiro"
        viewModelScope.launch {
            repository.payLoanInstallment(installment, method, operator)
        }
    }

    fun createCollectivePurchase(
        title: String,
        supplier: String,
        totalCost: Double,
        installments: Int,
        firstDueDate: String,
        targetType: String,
        selectedMembers: List<MemberEntity>
    ) {
        val operator = "Diretoria Financeira"
        viewModelScope.launch {
            repository.createCollectivePurchase(title, supplier, totalCost, installments, firstDueDate, targetType, selectedMembers, operator)
        }
    }

    fun payPurchaseQuota(quota: PurchaseQuotaEntity) {
        val operator = "Tesoureiro"
        viewModelScope.launch {
            repository.payPurchaseQuota(quota, operator)
        }
    }

    fun updateSettings(settings: ClubSettingsEntity) {
        viewModelScope.launch {
            repository.updateSettings(settings)
            repository.reconcileCashBalance()
        }
    }

    fun saveClubSettings(settings: ClubSettingsEntity) = updateSettings(settings)

    fun resetAllFinancialData() {
        viewModelScope.launch {
            repository.resetAllFinancialData()
        }
    }

    fun clearCashBook() {
        viewModelScope.launch {
            repository.clearCashBook()
        }
    }

    fun reconcileCash() {
        viewModelScope.launch {
            repository.reconcileCashBalance()
        }
    }

    fun adjustCashBalance(newTargetBalance: Double, reason: String = "Ajuste manual pela Diretoria") {
        viewModelScope.launch {
            val txs = transactions.value
            val currentCalculated = if (txs.isEmpty()) 0.0 else (txs.filter { it.type == "Entrada" }.sumOf { it.amount } - txs.filter { it.type == "Saída" }.sumOf { it.amount })
            val diff = newTargetBalance - currentCalculated
            if (kotlin.math.abs(diff) > 0.001) {
                repository.addCashTransaction(
                    type = if (diff > 0) "Entrada" else "Saída",
                    category = "Ajuste de Caixa",
                    amount = kotlin.math.abs(diff),
                    description = reason,
                    operator = _currentUser.value?.name ?: "Diretoria"
                )
            } else {
                repository.reconcileCashBalance()
            }
        }
    }

    // --- AUTHENTICATION (EMAIL & SENHA) ---
    fun login(email: String, password: String):Boolean {
        val cleanEmail = email.trim().lowercase()
        // Admin / Diretoria
        if (cleanEmail == "diretoria@guerreirosdobem.mc.br" || cleanEmail == "admin@guerreirosdobem.mc.br" || (cleanEmail.contains("admin") && password.length >= 4)) {
            _currentUser.value = AuthUser(
                email = cleanEmail,
                name = "Diretoria Executiva",
                role = UserRole.ADMIN,
                memberId = 1L,
                canInsert = true,
                canEdit = true,
                canDelete = true
            )
            _currentRole.value = UserRole.ADMIN
            _isAuthenticated.value = true
            return true
        }

        // Tesouraria
        if (cleanEmail == "tesouraria@guerreirosdobem.mc.br" || cleanEmail.contains("graxa") || cleanEmail.contains("tesoureiro")) {
            _currentUser.value = AuthUser(
                email = cleanEmail,
                name = "Marcelo Souza (Graxa) - Tesoureiro",
                role = UserRole.TREASURER,
                memberId = 4L,
                canInsert = true,
                canEdit = true,
                canDelete = false
            )
            _currentRole.value = UserRole.TREASURER
            _isAuthenticated.value = true
            return true
        }

        // Match against existing members by email
        val matchedMember = members.value.find { it.email.lowercase() == cleanEmail }
        if (matchedMember != null && password.isNotBlank()) {
            val userRole = when {
                matchedMember.role.contains("Presidente") || matchedMember.role.contains("Diretor") -> UserRole.ADMIN
                matchedMember.role.contains("Tesoureiro") -> UserRole.TREASURER
                else -> UserRole.MEMBER
            }
            _currentUser.value = AuthUser(
                email = matchedMember.email,
                name = "${matchedMember.fullName} (${matchedMember.roadNickname})",
                role = userRole,
                memberId = matchedMember.id,
                canInsert = userRole != UserRole.MEMBER,
                canEdit = userRole != UserRole.MEMBER,
                canDelete = userRole == UserRole.ADMIN
            )
            _currentRole.value = userRole
            _selectedMemberId.value = matchedMember.id
            _isAuthenticated.value = true
            return true
        }

        // Allow any valid email with 4+ char password as registered brotherhood member
        if (cleanEmail.contains("@") && password.length >= 4) {
            _currentUser.value = AuthUser(
                email = cleanEmail,
                name = cleanEmail.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() },
                role = UserRole.MEMBER,
                memberId = members.value.firstOrNull()?.id,
                canInsert = false,
                canEdit = false,
                canDelete = false
            )
            _currentRole.value = UserRole.MEMBER
            _isAuthenticated.value = true
            return true
        }

        return false
    }

    fun logout() {
        _isAuthenticated.value = false
        _currentUser.value = null
    }

    // --- GESTÃO DE PERMISSÕES (GESTOR / ADMIN) ---
    fun updateUserPermission(
        memberId: Long,
        role: UserRole,
        canInsert: Boolean,
        canEdit: Boolean,
        canDelete: Boolean,
        accessStatus: String
    ) {
        val member = members.value.find { it.id == memberId } ?: return
        val current = _userPermissions.value.toMutableMap()
        current[memberId] = UserPermissionConfig(
            memberId = memberId,
            memberName = member.fullName,
            nickname = member.roadNickname,
            role = role,
            canInsert = canInsert,
            canEdit = canEdit,
            canDelete = canDelete,
            accessStatus = accessStatus
        )
        _userPermissions.value = current

        // If this is currently logged in user, update privileges
        if (_currentUser.value?.memberId == memberId) {
            _currentUser.value = _currentUser.value?.copy(
                role = role,
                canInsert = canInsert,
                canEdit = canEdit,
                canDelete = canDelete
            )
            _currentRole.value = role
        }
    }

    // --- CRUD: MENSALIDADES ---
    fun addMonthlyDue(due: MonthlyDueEntity) {
        viewModelScope.launch {
            repository.addMonthlyDue(due)
        }
    }

    fun updateMonthlyDue(due: MonthlyDueEntity) {
        viewModelScope.launch {
            repository.updateMonthlyDue(due)
        }
    }

    fun deleteMonthlyDue(due: MonthlyDueEntity) {
        viewModelScope.launch {
            repository.deleteMonthlyDue(due)
        }
    }

    // --- CRUD: EMPRÉSTIMOS ---
    fun updateLoan(loan: LoanRequestEntity) {
        viewModelScope.launch {
            repository.updateLoan(loan)
        }
    }

    fun deleteLoan(loan: LoanRequestEntity) {
        viewModelScope.launch {
            repository.deleteLoan(loan)
        }
    }

    // --- CRUD: COMPRAS COLETIVAS ---
    fun updateCollectivePurchase(purchase: CollectivePurchaseEntity) {
        viewModelScope.launch {
            repository.updateCollectivePurchase(purchase)
        }
    }

    fun deleteCollectivePurchase(purchase: CollectivePurchaseEntity) {
        viewModelScope.launch {
            repository.deleteCollectivePurchase(purchase)
        }
    }

    fun updatePurchase(purchase: CollectivePurchaseEntity) = updateCollectivePurchase(purchase)
    fun deletePurchase(purchase: CollectivePurchaseEntity) = deleteCollectivePurchase(purchase)

    // --- CRUD: CAIXA / TRANSAÇÕES ---
    fun addCashTransaction(
        type: String,
        category: String,
        amount: Double,
        description: String,
        memberName: String? = null
    ) {
        val operator = _currentUser.value?.name ?: "Diretoria"
        viewModelScope.launch {
            repository.addCashTransaction(type, category, amount, description, memberName, operator)
        }
    }

    fun updateCashTransaction(transaction: CashTransactionEntity) {
        viewModelScope.launch {
            repository.updateCashTransaction(transaction)
        }
    }

    fun deleteCashTransaction(transaction: CashTransactionEntity) {
        viewModelScope.launch {
            repository.deleteCashTransaction(transaction)
        }
    }

    // --- FIRESTORE SYNCHRONIZATION ---
    fun syncWithFirestore() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncStatus.value = "Sincronizando com Firestore..."
            kotlinx.coroutines.delay(800) // visual feedback
            val membersSuccess = com.example.data.FirebaseSyncManager.syncMembers(
                dao = AppDatabase.getInstance(getApplication()).clubDao(),
                members = members.value
            )
            val txSuccess = com.example.data.FirebaseSyncManager.syncTransactions(transactions.value)
            val loansSuccess = com.example.data.FirebaseSyncManager.syncLoans(loans.value)

            if (membersSuccess || txSuccess || loansSuccess) {
                _syncStatus.value = "Nuvem Firestore Atualizada em Tempo Real"
            } else {
                _syncStatus.value = "Sincronizado Localmente (Modo Offline Ativo)"
            }
            _isSyncing.value = false
        }
    }

    fun calculateDaysUntilDueDate(dueDate: String): Int {
        return try {
            val parts = if (dueDate.contains("/")) dueDate.split("/") else dueDate.split("-")
            val day: Int
            val month: Int
            val year: Int

            if (dueDate.contains("/")) {
                // dd/MM/yyyy
                day = parts[0].trim().toInt()
                month = parts[1].trim().toInt()
                year = parts[2].trim().toInt()
            } else {
                // yyyy-MM-dd
                year = parts[0].trim().toInt()
                month = parts[1].trim().toInt()
                day = parts[2].trim().toInt()
            }

            val todayCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val dueCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val diffMillis = dueCal.timeInMillis - todayCal.timeInMillis
            TimeUnit.MILLISECONDS.toDays(diffMillis).toInt()
        } catch (_: Exception) {
            0
        }
    }
}
