package com.example.data

import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

/**
 * Manages bidirectional synchronization between the local Room database
 * and Firebase Firestore for members, cash transactions, loans, and dues.
 */
object FirebaseSyncManager {
    private const val TAG = "FirebaseSyncManager"

    private fun getFirestore(): FirebaseFirestore? {
        return try {
            if (FirebaseApp.getApps(FirebaseApp.getInstance().applicationContext).isNotEmpty()) {
                FirebaseFirestore.getInstance()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase not yet initialized or credentials missing: ${e.message}")
            null
        }
    }

    suspend fun syncMembers(dao: ClubDao, members: List<MemberEntity>): Boolean {
        val db = getFirestore() ?: return false
        return try {
            val batch = db.batch()
            for (member in members) {
                val doc = db.collection("members").document(member.id.toString())
                val data = mapOf(
                    "id" to member.id,
                    "fullName" to member.fullName,
                    "roadNickname" to member.roadNickname,
                    "role" to member.role,
                    "phone" to member.phone,
                    "email" to member.email,
                    "pixKey" to member.pixKey,
                    "joinDate" to member.joinDate,
                    "status" to member.status,
                    "notes" to member.notes
                )
                batch.set(doc, data, SetOptions.merge())
            }
            batch.commit().await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing members to Firestore: ${e.message}")
            false
        }
    }

    suspend fun syncTransactions(transactions: List<CashTransactionEntity>): Boolean {
        val db = getFirestore() ?: return false
        return try {
            val batch = db.batch()
            for (tx in transactions) {
                val doc = db.collection("cash_transactions").document(tx.id.toString())
                val data = mapOf(
                    "id" to tx.id,
                    "type" to tx.type,
                    "category" to tx.category,
                    "amount" to tx.amount,
                    "description" to tx.description,
                    "memberName" to (tx.memberName ?: ""),
                    "date" to tx.date,
                    "operator" to tx.operator
                )
                batch.set(doc, data, SetOptions.merge())
            }
            batch.commit().await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing transactions to Firestore: ${e.message}")
            false
        }
    }

    suspend fun syncLoans(loans: List<LoanRequestEntity>): Boolean {
        val db = getFirestore() ?: return false
        return try {
            val batch = db.batch()
            for (loan in loans) {
                val doc = db.collection("loans").document(loan.id.toString())
                val data = mapOf(
                    "id" to loan.id,
                    "memberId" to loan.memberId,
                    "memberName" to loan.memberName,
                    "memberNickname" to loan.memberNickname,
                    "totalAmount" to loan.totalAmount,
                    "monthlyInterestRate" to loan.monthlyInterestRate,
                    "installmentsCount" to loan.installmentsCount,
                    "purpose" to loan.purpose,
                    "requestDate" to loan.requestDate,
                    "status" to loan.status,
                    "totalInterest" to loan.totalInterest,
                    "totalPayable" to loan.totalPayable
                )
                batch.set(doc, data, SetOptions.merge())
            }
            batch.commit().await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing loans to Firestore: ${e.message}")
            false
        }
    }
}
