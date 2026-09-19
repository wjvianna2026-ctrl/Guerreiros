package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClubDao {

    // --- MEMBERS ---
    @Query("""
        SELECT * FROM members ORDER BY 
        CASE role 
            WHEN 'Presidente' THEN 1 
            WHEN 'Vice-Presidente' THEN 2 
            WHEN 'Diretor Financeiro' THEN 3 
            WHEN 'Tesoureiro' THEN 3
            WHEN 'Diretor de Eventos' THEN 4
            WHEN 'Secretário Geral' THEN 5
            WHEN 'Capitão de Estrada' THEN 6
            WHEN 'Sargento de Armas' THEN 7
            WHEN 'Sub-Capitão de Estrada' THEN 8
            WHEN 'Escudo Fechado' THEN 9 
            WHEN 'Meio Escudo' THEN 10 
            WHEN 'Próspero' THEN 11
            WHEN 'Postulante' THEN 12
            WHEN 'Aspirante' THEN 13
            WHEN 'Apoio Oficial' THEN 14
            WHEN 'Membro Benemérito' THEN 15
            ELSE 16 
        END, fullName ASC
    """)
    fun getAllMembers(): Flow<List<MemberEntity>>

    @Query("SELECT * FROM members WHERE id = :id")
    suspend fun getMemberById(id: Long): MemberEntity?

    @Query("SELECT COUNT(*) FROM members")
    suspend fun getMemberCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<MemberEntity>)

    @Update
    suspend fun updateMember(member: MemberEntity)

    @Delete
    suspend fun deleteMember(member: MemberEntity)

    // --- MONTHLY DUES ---
    @Query("SELECT * FROM monthly_dues ORDER BY id DESC")
    fun getAllMonthlyDues(): Flow<List<MonthlyDueEntity>>

    @Query("SELECT * FROM monthly_dues WHERE memberId = :memberId ORDER BY id DESC")
    fun getDuesByMember(memberId: Long): Flow<List<MonthlyDueEntity>>

    @Query("SELECT * FROM monthly_dues WHERE id = :id")
    suspend fun getMonthlyDueById(id: Long): MonthlyDueEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonthlyDue(due: MonthlyDueEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonthlyDues(dues: List<MonthlyDueEntity>)

    @Update
    suspend fun updateMonthlyDue(due: MonthlyDueEntity)

    @Delete
    suspend fun deleteMonthlyDue(due: MonthlyDueEntity)

    @Query("DELETE FROM monthly_dues WHERE id = :id")
    suspend fun deleteMonthlyDueById(id: Long)

    @Query("UPDATE monthly_dues SET status = :status, paidDate = :paidDate, paymentMethod = :method, receiptNotes = :notes WHERE id = :id")
    suspend fun setDueStatus(id: Long, status: String, paidDate: String?, method: String?, notes: String?)

    // --- LOANS ---
    @Query("SELECT * FROM loan_requests ORDER BY id DESC")
    fun getAllLoans(): Flow<List<LoanRequestEntity>>

    @Query("SELECT * FROM loan_requests WHERE id = :id")
    suspend fun getLoanById(id: Long): LoanRequestEntity?

    @Query("SELECT * FROM loan_requests WHERE memberId = :memberId ORDER BY id DESC")
    fun getLoansByMember(memberId: Long): Flow<List<LoanRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanRequestEntity): Long

    @Update
    suspend fun updateLoan(loan: LoanRequestEntity)

    @Delete
    suspend fun deleteLoan(loan: LoanRequestEntity)

    @Query("DELETE FROM loan_requests WHERE id = :id")
    suspend fun deleteLoanById(id: Long)

    // --- LOAN INSTALLMENTS ---
    @Query("SELECT * FROM loan_installments ORDER BY dueDate ASC")
    fun getAllLoanInstallments(): Flow<List<LoanInstallmentEntity>>

    @Query("SELECT * FROM loan_installments WHERE loanId = :loanId ORDER BY installmentNumber ASC")
    fun getInstallmentsByLoan(loanId: Long): Flow<List<LoanInstallmentEntity>>

    @Query("SELECT * FROM loan_installments WHERE memberId = :memberId ORDER BY dueDate ASC")
    fun getInstallmentsByMember(memberId: Long): Flow<List<LoanInstallmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoanInstallments(installments: List<LoanInstallmentEntity>)

    @Update
    suspend fun updateLoanInstallment(installment: LoanInstallmentEntity)

    @Delete
    suspend fun deleteLoanInstallment(installment: LoanInstallmentEntity)

    @Query("DELETE FROM loan_installments WHERE loanId = :loanId")
    suspend fun deleteInstallmentsByLoanId(loanId: Long)

    @Query("UPDATE loan_installments SET status = :status, paidDate = :paidDate, paymentMethod = :method WHERE id = :id")
    suspend fun setInstallmentStatus(id: Long, status: String, paidDate: String?, method: String?)

    // --- COLLECTIVE PURCHASES ---
    @Query("SELECT * FROM collective_purchases ORDER BY id DESC")
    fun getAllPurchases(): Flow<List<CollectivePurchaseEntity>>

    @Query("SELECT * FROM collective_purchases WHERE id = :id")
    suspend fun getPurchaseById(id: Long): CollectivePurchaseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: CollectivePurchaseEntity): Long

    @Update
    suspend fun updatePurchase(purchase: CollectivePurchaseEntity)

    @Delete
    suspend fun deletePurchase(purchase: CollectivePurchaseEntity)

    @Query("DELETE FROM collective_purchases WHERE id = :id")
    suspend fun deletePurchaseById(id: Long)

    // --- PURCHASE QUOTAS ---
    @Query("SELECT * FROM purchase_quotas ORDER BY dueDate ASC")
    fun getAllPurchaseQuotas(): Flow<List<PurchaseQuotaEntity>>

    @Query("SELECT * FROM purchase_quotas WHERE memberId = :memberId ORDER BY dueDate ASC")
    fun getQuotasByMember(memberId: Long): Flow<List<PurchaseQuotaEntity>>

    @Query("SELECT * FROM purchase_quotas WHERE purchaseId = :purchaseId ORDER BY installmentNumber ASC")
    fun getQuotasByPurchase(purchaseId: Long): Flow<List<PurchaseQuotaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchaseQuotas(quotas: List<PurchaseQuotaEntity>)

    @Update
    suspend fun updatePurchaseQuota(quota: PurchaseQuotaEntity)

    @Delete
    suspend fun deletePurchaseQuota(quota: PurchaseQuotaEntity)

    @Query("DELETE FROM purchase_quotas WHERE purchaseId = :purchaseId")
    suspend fun deleteQuotasByPurchaseId(purchaseId: Long)

    @Query("UPDATE purchase_quotas SET status = :status, paidDate = :paidDate WHERE id = :id")
    suspend fun setQuotaStatus(id: Long, status: String, paidDate: String?)

    // --- CASH TRANSACTIONS (AUDITORIA / LIVRO CAIXA) ---
    @Query("SELECT * FROM cash_transactions ORDER BY id DESC")
    fun getAllTransactions(): Flow<List<CashTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: CashTransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: CashTransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: CashTransactionEntity)

    @Query("DELETE FROM cash_transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)

    @Query("SELECT * FROM cash_transactions")
    suspend fun getAllTransactionsList(): List<CashTransactionEntity>

    @Query("DELETE FROM cash_transactions")
    suspend fun deleteAllTransactions()

    @Query("DELETE FROM monthly_dues")
    suspend fun deleteAllMonthlyDues()

    @Query("DELETE FROM loan_installments")
    suspend fun deleteAllLoanInstallments()

    @Query("DELETE FROM loan_requests")
    suspend fun deleteAllLoans()

    @Query("DELETE FROM purchase_quotas")
    suspend fun deleteAllPurchaseQuotas()

    @Query("DELETE FROM collective_purchases")
    suspend fun deleteAllPurchases()

    // --- CLUB SETTINGS ---
    @Query("SELECT * FROM club_settings WHERE id = 1")
    fun getClubSettings(): Flow<ClubSettingsEntity?>

    @Query("SELECT * FROM club_settings WHERE id = 1")
    suspend fun getClubSettingsSync(): ClubSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: ClubSettingsEntity)

    @Update
    suspend fun updateSettings(settings: ClubSettingsEntity)

    @Query("UPDATE club_settings SET currentCashBalance = currentCashBalance + :delta WHERE id = 1")
    suspend fun adjustCashBalance(delta: Double)

    @Query("UPDATE club_settings SET currentCashBalance = :balance WHERE id = 1")
    suspend fun setCashBalance(balance: Double)
}
