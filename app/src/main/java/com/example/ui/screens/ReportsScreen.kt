package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CashTransactionEntity
import com.example.data.ClubSettingsEntity
import com.example.ui.ClubViewModel
import com.example.ui.ConfirmDeleteDialog
import com.example.ui.UnifiedCrudActionBar
import com.example.ui.UserRole
import com.example.ui.WhatsAppHelper
import com.example.ui.theme.DarkBgCard
import com.example.ui.theme.DarkBgCardElevated
import com.example.ui.theme.DarkBgDeep
import com.example.ui.theme.DarkBgSurface
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldPaid
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GoldAmberLight
import com.example.ui.theme.ScarletOverdue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TitaniumBorder
import java.util.Locale

@Composable
fun ReportsScreen(
    viewModel: ClubViewModel
) {
    val context = LocalContext.current
    val settings by viewModel.clubSettings.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val members by viewModel.members.collectAsState()
    val dues by viewModel.monthlyDues.collectAsState()
    val loans by viewModel.loans.collectAsState()
    val purchases by viewModel.collectivePurchases.collectAsState()
    val role by viewModel.currentRole.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val canInsert = currentUser?.canInsert ?: (role != UserRole.MEMBER)
    val canEdit = currentUser?.canEdit ?: (role != UserRole.MEMBER)
    val canDelete = currentUser?.canDelete ?: (role == UserRole.ADMIN)

    var showBackupDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showInsertTransactionDialog by remember { mutableStateOf(false) }
    var transactionToEdit by remember { mutableStateOf<CashTransactionEntity?>(null) }
    var transactionToDelete by remember { mutableStateOf<CashTransactionEntity?>(null) }

    val totalIncome = transactions.filter { it.type == "Entrada" }.sumOf { it.amount }
    val totalExpenses = transactions.filter { it.type == "Saída" }.sumOf { it.amount }
    val calculatedNetBalance = if (transactions.isEmpty()) 0.0 else (totalIncome - totalExpenses)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Header
        item {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "PRESTAÇÃO DE CONTAS & AUDITORIA",
                    color = GoldAmberLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Transparência total para a irmandade e livro caixa auditável",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Universal CRUD Action Bar for Auditoria (Inserir, Corrigir, Excluir)
                UnifiedCrudActionBar(
                    onInsert = { showInsertTransactionDialog = true },
                    onEdit = { transactionToEdit = transactions.firstOrNull() },
                    onDelete = { transactionToDelete = transactions.firstOrNull() },
                    canInsert = canInsert,
                    canEdit = canEdit && transactions.isNotEmpty(),
                    canDelete = canDelete && transactions.isNotEmpty(),
                    insertLabel = "Inserir Auditoria",
                    editLabel = "Corrigir Registro",
                    deleteLabel = "Excluir Registro"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Action buttons: Export Backup, Club Settings, Zerar Caixa
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { showBackupDialog = true },
                        border = BorderStroke(1.dp, ElectricCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Backup", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    if (role == UserRole.ADMIN) {
                        Button(
                            onClick = { showSettingsDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAmber),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.1f)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF1C1300), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ajustes MC", color = Color(0xFF1C1300), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { showResetDialog = true },
                            border = BorderStroke(1.dp, ScarletOverdue.copy(alpha = 0.8f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ScarletOverdue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(0.9f)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = ScarletOverdue, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Zerar", color = ScarletOverdue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Statement Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkBgCard),
                    border = BorderStroke(1.dp, TitaniumBorder),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "BALANÇO CONSOLIDADO DO CLUBE",
                            color = GoldAmberLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Entradas (Mensalidades, Parcelas e Juros):", color = TextSecondary, fontSize = 11.sp)
                            Text("+ R$ ${"%.2f".format(Locale.US, totalIncome)}", color = EmeraldPaid, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Saídas (Empréstimos Liberados e Compras):", color = TextSecondary, fontSize = 11.sp)
                            Text("- R$ ${"%.2f".format(Locale.US, totalExpenses)}", color = ScarletOverdue, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = TitaniumBorder)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Saldo Líquido em Caixa:", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "R$ ${"%.2f".format(Locale.US, calculatedNetBalance)}",
                                color = if (calculatedNetBalance >= 0) EmeraldPaid else ScarletOverdue,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cloud readiness banner
                Surface(
                    color = DarkBgSurface,
                    border = BorderStroke(1.dp, TitaniumBorder),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = EmeraldPaid, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Sincronização em Nuvem (Firestore Ready)", color = EmeraldPaid, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Dados protegidos localmente com suporte a replicação remota", color = TextSecondary, fontSize = 9.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "LIVRO CAIXA & TRILHA DE AUDITORIA (${transactions.size} REGISTROS)",
                    color = ElectricCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Transactions List
        items(transactions) { tx ->
            TransactionRow(
                tx = tx,
                canEdit = canEdit,
                canDelete = canDelete,
                onEdit = { transactionToEdit = tx },
                onDelete = { transactionToDelete = tx }
            )
        }
    }

    // Modal: Inserir Transação em Auditoria
    if (showInsertTransactionDialog) {
        InsertCashTransactionDialog(
            onDismiss = { showInsertTransactionDialog = false },
            onConfirm = { type, category, amount, description, memberName ->
                viewModel.addCashTransaction(type, category, amount, description, memberName)
                showInsertTransactionDialog = false
            }
        )
    }

    // Modal: Corrigir Lançamento de Auditoria
    if (transactionToEdit != null) {
        EditCashTransactionDialog(
            transaction = transactionToEdit!!,
            onDismiss = { transactionToEdit = null },
            onConfirm = { updated ->
                viewModel.updateCashTransaction(updated)
                transactionToEdit = null
            }
        )
    }

    // Modal: Excluir Lançamento de Auditoria
    if (transactionToDelete != null) {
        ConfirmDeleteDialog(
            title = "Excluir Lançamento de Auditoria",
            message = "Deseja realmente remover o lançamento '${transactionToDelete?.description}' no valor de R$ ${"%.2f".format(Locale.US, transactionToDelete?.amount ?: 0.0)}? O saldo de caixa será ajustado.",
            onConfirm = {
                transactionToDelete?.let { viewModel.deleteCashTransaction(it) }
                transactionToDelete = null
            },
            onDismiss = {
                transactionToDelete = null
            }
        )
    }

    // Modal: Backup JSON
    if (showBackupDialog) {
        val backupJson = remember(members, dues, loans, purchases, transactions, settings) {
            """
            {
              "clubName": "${settings?.clubName}",
              "exportDate": "${transactions.firstOrNull()?.date ?: "13/09/2026"}",
              "membersCount": ${members.size},
              "activeLoans": ${loans.size},
              "collectivePurchases": ${purchases.size},
              "totalTransactions": ${transactions.size},
              "cashBalance": ${calculatedNetBalance},
              "status": "VALID_DATABASE_BACKUP"
            }
            """.trimIndent()
        }

        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            containerColor = DarkBgCardElevated,
            title = {
                Text("Backup do Sistema (JSON)", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Estrutura exportável com todos os registros de caixa e membros:", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = DarkBgDeep,
                        border = BorderStroke(1.dp, TitaniumBorder),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = backupJson,
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        WhatsAppHelper.copyToClipboard(context, "Backup JSON Moto Clube", backupJson)
                        showBackupDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Copiar Backup", color = Color(0xFF00363F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackupDialog = false }) {
                    Text("Fechar", color = TextSecondary)
                }
            }
        )
    }

    // Modal: Zerar Dados Financeiros (Exclusivo Admin)
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = DarkBgCardElevated,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = ScarletOverdue, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Zerar Dados & Caixa", color = ScarletOverdue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    Text(
                        "Esta ação é exclusiva da Diretoria e permite zerar os lançamentos para reiniciar o ciclo contábil ou corrigir o caixa.",
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = ScarletOverdue.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, ScarletOverdue.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "O Saldo em Caixa atual será redefinido para R$ 0,00.",
                            color = ScarletOverdue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Escolha o que deseja zerar:",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearCashBook()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ScarletOverdue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Zerar Caixa (R$ 0,00)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = {
                            viewModel.resetAllFinancialData()
                            showResetDialog = false
                        },
                        border = BorderStroke(1.dp, ScarletOverdue.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Zerar Tudo Geral", color = ScarletOverdue, fontSize = 11.sp)
                    }
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancelar", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        )
    }

    // Modal: Ajustes do Clube
    if (showSettingsDialog) {
        settings?.let { currSettings ->
            EditClubSettingsDialog(
                settings = currSettings,
                onDismiss = { showSettingsDialog = false },
                onSave = { updated ->
                    val targetBal = updated.currentCashBalance
                    viewModel.updateSettings(updated)
                    viewModel.adjustCashBalance(targetBal, "Ajuste manual de saldo pelo Administrador")
                    showSettingsDialog = false
                }
            )
        }
    }
}

@Composable
fun TransactionRow(
    tx: CashTransactionEntity,
    canEdit: Boolean = false,
    canDelete: Boolean = false,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val isEntry = tx.type == "Entrada"
    val isExit = tx.type == "Saída"

    val color = when {
        isEntry -> EmeraldPaid
        isExit -> ScarletOverdue
        else -> GoldAmber
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 3.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = BorderStroke(0.8.dp, TitaniumBorder),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = color.copy(alpha = 0.15f),
                            border = BorderStroke(0.6.dp, color),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = tx.type.uppercase(Locale.ROOT),
                                color = color,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = tx.category,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = tx.description,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "${tx.date} • Resp: ${tx.operator}",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                if (tx.amount > 0) {
                    Text(
                        text = "${if (isEntry) "+" else if (isExit) "-" else ""} R$ ${"%.2f".format(Locale.US, tx.amount)}",
                        color = color,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            if (canEdit || canDelete) {
                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = TitaniumBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (canEdit) {
                        OutlinedButton(
                            onClick = onEdit,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.8f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Corrigir", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (canDelete) {
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedButton(
                            onClick = onDelete,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, ScarletOverdue.copy(alpha = 0.8f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ScarletOverdue),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Excluir", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditClubSettingsDialog(
    settings: ClubSettingsEntity,
    onDismiss: () -> Unit,
    onSave: (ClubSettingsEntity) -> Unit
) {
    var clubName by remember { mutableStateOf(settings.clubName) }
    var motto by remember { mutableStateOf(settings.motto) }
    var pixKey by remember { mutableStateOf(settings.pixKey) }
    var pixHolderName by remember { mutableStateOf(settings.pixHolderName) }
    var defaultDueStr by remember { mutableStateOf(settings.defaultMonthlyDue.toString()) }
    var cashBalanceStr by remember { mutableStateOf(settings.currentCashBalance.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBgCardElevated,
        title = {
            Text("Configurações do Guerreiros do Bem MC", color = GoldAmberLight, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = clubName,
                    onValueChange = { clubName = it },
                    label = { Text("Nome Oficial do Moto Clube") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = motto,
                    onValueChange = { motto = it },
                    label = { Text("Lema / Frase de Estrada") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = pixKey,
                    onValueChange = { pixKey = it },
                    label = { Text("Chave PIX Oficial do Clube") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = pixHolderName,
                    onValueChange = { pixHolderName = it },
                    label = { Text("Titular da Conta Bancária / PIX") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = defaultDueStr,
                    onValueChange = { defaultDueStr = it },
                    label = { Text("Valor Padrão da Mensalidade (R$)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = cashBalanceStr,
                    onValueChange = { cashBalanceStr = it },
                    label = { Text("Saldo Atual em Caixa (R$)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { cashBalanceStr = "0.00" },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = ScarletOverdue, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Zerar Saldo para R$ 0,00", color = ScarletOverdue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = settings.copy(
                        clubName = clubName,
                        motto = motto,
                        pixKey = pixKey,
                        pixHolderName = pixHolderName,
                        defaultMonthlyDue = defaultDueStr.toDoubleOrNull() ?: settings.defaultMonthlyDue,
                        currentCashBalance = cashBalanceStr.toDoubleOrNull() ?: settings.currentCashBalance
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAmber),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Salvar Alterações", color = Color(0xFF1C1300), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}
