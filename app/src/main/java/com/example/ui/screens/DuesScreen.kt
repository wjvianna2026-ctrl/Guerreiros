package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
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
import com.example.data.MemberEntity
import com.example.data.MonthlyDueEntity
import com.example.ui.ClubViewModel
import com.example.ui.ConfirmDeleteDialog
import com.example.ui.UnifiedCrudActionBar
import com.example.ui.UserRole
import com.example.ui.WhatsAppPaymentDialog
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
fun DuesScreen(
    viewModel: ClubViewModel
) {
    val context = LocalContext.current
    val monthlyDues by viewModel.monthlyDues.collectAsState()
    val members by viewModel.members.collectAsState()
    val settings by viewModel.clubSettings.collectAsState()
    val role by viewModel.currentRole.collectAsState()
    val selectedMemberId by viewModel.selectedMemberId.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val canInsert = currentUser?.canInsert ?: (role != UserRole.MEMBER)
    val canEdit = currentUser?.canEdit ?: (role != UserRole.MEMBER)
    val canDelete = currentUser?.canDelete ?: (role == UserRole.ADMIN)

    var statusFilter by remember { mutableStateOf("Todos") }
    var showGenerateBatchDialog by remember { mutableStateOf(false) }
    var showAddSingleDueDialog by remember { mutableStateOf(false) }
    var dueToPay by remember { mutableStateOf<MonthlyDueEntity?>(null) }
    var dueToEdit by remember { mutableStateOf<MonthlyDueEntity?>(null) }
    var dueToDelete by remember { mutableStateOf<MonthlyDueEntity?>(null) }
    var activeWhatsAppDue by remember { mutableStateOf<MonthlyDueEntity?>(null) }

    val filterOptions = listOf("Todos", "Pendente", "Paga", "Em Atraso")

    // Filter dues based on user role and filters
    val displayedDues = monthlyDues.filter { due ->
        val matchesRoleConstraint = if (role == UserRole.MEMBER) {
            due.memberId == (selectedMemberId ?: -1L)
        } else true

        val matchesStatus = if (statusFilter == "Todos") true else due.status == statusFilter
        matchesRoleConstraint && matchesStatus
    }

    val totalDuesAmount = displayedDues.sumOf { it.amount }
    val totalPaidAmount = displayedDues.filter { it.status == "Paga" }.sumOf { it.amount }
    val totalPendingAmount = displayedDues.filter { it.status != "Paga" }.sumOf { it.amount }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 1100.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Top Header
                item {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "MENSALIDADES DO CLUBE",
                                color = GoldAmberLight,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Acompanhamento mês a mês e baixa de contribuições",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Universal Action Bar (Inserir, Corrigir, Excluir)
                        UnifiedCrudActionBar(
                            onInsert = { showAddSingleDueDialog = true },
                            onEdit = { dueToEdit = displayedDues.firstOrNull() },
                            onDelete = { dueToDelete = displayedDues.firstOrNull() },
                            canInsert = canInsert,
                            canEdit = canEdit && displayedDues.isNotEmpty(),
                            canDelete = canDelete && displayedDues.isNotEmpty(),
                            insertLabel = "Inserir Mensalidade",
                            editLabel = "Corrigir Mensalidade",
                            deleteLabel = "Excluir Mensalidade"
                        )

                        if (canInsert) {
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedButton(
                                onClick = { showGenerateBatchDialog = true },
                                border = BorderStroke(1.dp, GoldAmber.copy(alpha = 0.6f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(32.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = GoldAmber, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Gerar Lote Mensal para Todos os Irmãos", color = GoldAmberLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Summary Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DarkBgCard),
                            border = BorderStroke(1.dp, TitaniumBorder),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("TOTAL EMITIDO", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text("R$ ${"%.2f".format(Locale.US, totalDuesAmount)}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("TOTAL PAGO", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text("R$ ${"%.2f".format(Locale.US, totalPaidAmount)}", color = EmeraldPaid, fontSize = 13.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("PENDENTE", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text("R$ ${"%.2f".format(Locale.US, totalPendingAmount)}", color = if (totalPendingAmount > 0) ScarletOverdue else EmeraldPaid, fontSize = 13.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Status Filter Chips
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(filterOptions) { opt ->
                                val isSelected = statusFilter == opt
                                Surface(
                                    color = if (isSelected) GoldAmber else DarkBgCard,
                                    border = BorderStroke(1.dp, if (isSelected) GoldAmberLight else TitaniumBorder),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.clickable { statusFilter = opt }
                                ) {
                                    Text(
                                        text = opt,
                                        color = if (isSelected) Color(0xFF1C1300) else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

        // List of Dues
        if (displayedDues.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhuma mensalidade encontrada com o filtro selecionado.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            items(displayedDues) { due ->
                val member = members.find { it.id == due.memberId }

                DueItemCard(
                    due = due,
                    userRole = role,
                    canEdit = canEdit,
                    canDelete = canDelete,
                    viewModel = viewModel,
                    onPay = { dueToPay = due },
                    onEdit = { dueToEdit = due },
                    onDelete = { dueToDelete = due },
                    onReopen = { viewModel.reopenMonthlyDue(due) },
                    onWhatsApp = { activeWhatsAppDue = due }
                )
            }
        }
            }
        }
    }

    // Modal: Confirmar Baixa com Comprovante
    dueToPay?.let { due ->
        PayDueDialog(
            due = due,
            onDismiss = { dueToPay = null },
            onConfirm = { method, notes ->
                viewModel.payMonthlyDue(due, method, notes)
                dueToPay = null
            }
        )
    }

    // Modal: Inserir Mensalidade Individual
    if (showAddSingleDueDialog) {
        AddSingleDueDialog(
            members = members.filter { it.status == "Ativo" },
            defaultAmount = settings?.defaultMonthlyDue ?: 50.0,
            onDismiss = { showAddSingleDueDialog = false },
            onSave = { member, monthYear, dueDate, amount ->
                viewModel.addMonthlyDue(
                    MonthlyDueEntity(
                        memberId = member.id,
                        memberName = member.fullName,
                        memberNickname = member.roadNickname,
                        monthYear = monthYear,
                        amount = amount,
                        dueDate = dueDate,
                        status = "Pendente"
                    )
                )
                showAddSingleDueDialog = false
            }
        )
    }

    // Modal: Corrigir / Editar Mensalidade
    dueToEdit?.let { due ->
        EditDueDialog(
            due = due,
            onDismiss = { dueToEdit = null },
            onSave = { updated ->
                viewModel.updateMonthlyDue(updated)
                dueToEdit = null
            }
        )
    }

    // Modal: Confirmar Exclusão de Mensalidade
    dueToDelete?.let { due ->
        ConfirmDeleteDialog(
            title = "Excluir Mensalidade",
            message = "Deseja realmente excluir a mensalidade do irmão ${due.memberNickname} referente ao mês ${due.monthYear} no valor de R$ ${"%.2f".format(Locale.US, due.amount)}?",
            onConfirm = {
                viewModel.deleteMonthlyDue(due)
                dueToDelete = null
            },
            onDismiss = { dueToDelete = null }
        )
    }

    // Modal: Gerar Lote de Mensalidades
    if (showGenerateBatchDialog) {
        GenerateBatchDuesDialog(
            defaultAmount = settings?.defaultMonthlyDue ?: 50.0,
            activeMembersCount = members.count { it.status == "Ativo" },
            onDismiss = { showGenerateBatchDialog = false },
            onGenerate = { monthYear, dueDate, amount ->
                viewModel.generateDues(monthYear, dueDate, amount)
                showGenerateBatchDialog = false
            }
        )
    }

    // Modal: WhatsApp Cobrança
    activeWhatsAppDue?.let { due ->
        val member = members.find { it.id == due.memberId }
        WhatsAppPaymentDialog(
            recipientName = due.memberNickname,
            recipientPhone = member?.phone ?: "",
            description = "Mensalidade referente a ${due.monthYear}",
            amount = due.amount,
            dueDate = due.dueDate,
            pixKey = settings?.pixKey ?: "financeiro@guerreirosdobem.mc.br",
            pixHolder = settings?.pixHolderName ?: "Guerreiros do Bem MC",
            onDismiss = { activeWhatsAppDue = null }
        )
    }
}

@Composable
fun DueItemCard(
    due: MonthlyDueEntity,
    userRole: UserRole,
    canEdit: Boolean,
    canDelete: Boolean,
    viewModel: ClubViewModel,
    onPay: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onReopen: () -> Unit,
    onWhatsApp: () -> Unit
) {
    val daysUntil = viewModel.calculateDaysUntilDueDate(due.dueDate)
    val isPaid = due.status == "Paga"
    val isOverdue = !isPaid && daysUntil < 0
    val statusColor = when {
        isPaid -> EmeraldPaid
        isOverdue -> ScarletOverdue
        else -> GoldAmber
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = BorderStroke(1.dp, TitaniumBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = due.memberNickname.uppercase(Locale.ROOT),
                        color = GoldAmberLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• Mês ${due.monthYear}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                val displayStatus = when {
                    isPaid -> "PAGA"
                    isOverdue -> "ATRASADA (${-daysUntil}d)"
                    daysUntil == 0 -> "VENCE HOJE"
                    daysUntil == 1 -> "VENCE AMANHÃ"
                    daysUntil in 2..30 -> "VENCE EM ${daysUntil}d"
                    else -> "ABERTA"
                }

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    border = BorderStroke(0.8.dp, statusColor),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = displayStatus,
                        color = statusColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Vencimento: ${due.dueDate}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    if (due.status == "Paga" && due.paidDate != null) {
                        Text(
                            text = "Pago em: ${due.paidDate} (${due.paymentMethod ?: "PIX"})",
                            color = EmeraldPaid,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(
                    text = "R$ ${"%.2f".format(Locale.US, due.amount)}",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = TitaniumBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons: Inserir, Corrigir, Excluir, Dar Baixa, WhatsApp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Secondary edit/delete actions
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (canEdit) {
                        OutlinedButton(
                            onClick = onEdit,
                            border = BorderStroke(1.dp, ElectricCyan),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Corrigir", tint = ElectricCyan, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Corrigir", color = ElectricCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    if (canDelete) {
                        OutlinedButton(
                            onClick = onDelete,
                            border = BorderStroke(1.dp, ScarletOverdue),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = ScarletOverdue, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Excluir", color = ScarletOverdue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Primary payment & whatsapp actions
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (due.status != "Paga") {
                        OutlinedButton(
                            onClick = onWhatsApp,
                            border = BorderStroke(1.dp, EmeraldPaid),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = EmeraldPaid, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cobrar", color = EmeraldPaid, fontSize = 10.sp)
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Button(
                            onClick = onPay,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPaid),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Dar Baixa", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        if (userRole != UserRole.MEMBER) {
                            TextButton(
                                onClick = onReopen,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reabrir", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddSingleDueDialog(
    members: List<MemberEntity>,
    defaultAmount: Double,
    onDismiss: () -> Unit,
    onSave: (member: MemberEntity, monthYear: String, dueDate: String, amount: Double) -> Unit
) {
    var selectedMember by remember { mutableStateOf(members.firstOrNull()) }
    var monthYear by remember { mutableStateOf("10/2026") }
    var dueDate by remember { mutableStateOf("10/10/2026") }
    var amountStr by remember { mutableStateOf(defaultAmount.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBgCardElevated,
        title = {
            Text("Inserir Mensalidade Individual", color = GoldAmberLight, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Selecione o Irmão:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(members) { m ->
                        val isSel = selectedMember?.id == m.id
                        Surface(
                            color = if (isSel) GoldAmber else DarkBgCard,
                            border = BorderStroke(1.dp, if (isSel) GoldAmberLight else TitaniumBorder),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable { selectedMember = m }
                        ) {
                            Text(
                                text = "${m.roadNickname} (${m.fullName})",
                                color = if (isSel) Color(0xFF1C1300) else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                MonthYearPickerField(
                    value = monthYear,
                    onValueChange = { monthYear = it },
                    label = "Mês/Ano de Referência *"
                )

                Spacer(modifier = Modifier.height(8.dp))

                DueDatePickerField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = "Data de Vencimento *"
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Valor (R$)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val m = selectedMember ?: return@Button
                    val amt = amountStr.toDoubleOrNull() ?: defaultAmount
                    onSave(m, monthYear, dueDate, amt)
                },
                enabled = selectedMember != null,
                colors = ButtonDefaults.buttonColors(containerColor = GoldAmber),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Salvar Mensalidade", color = Color(0xFF1C1300), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}

@Composable
fun EditDueDialog(
    due: MonthlyDueEntity,
    onDismiss: () -> Unit,
    onSave: (MonthlyDueEntity) -> Unit
) {
    var monthYear by remember { mutableStateOf(due.monthYear) }
    var dueDate by remember { mutableStateOf(due.dueDate) }
    var amountStr by remember { mutableStateOf(due.amount.toString()) }
    var status by remember { mutableStateOf(due.status) }

    val statusOptions = listOf("Pendente", "Paga", "Em Atraso")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBgCardElevated,
        title = {
            Text("Corrigir Mensalidade", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Irmão: ${due.memberNickname} (${due.memberName})", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                Spacer(modifier = Modifier.height(10.dp))

                MonthYearPickerField(
                    value = monthYear,
                    onValueChange = { monthYear = it },
                    label = "Mês/Ano de Referência *"
                )

                Spacer(modifier = Modifier.height(8.dp))

                DueDatePickerField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = "Data de Vencimento *"
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Valor (R$)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Status:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    statusOptions.forEach { opt ->
                        val isSel = status == opt
                        Surface(
                            color = if (isSel) ElectricCyan else DarkBgCard,
                            border = BorderStroke(1.dp, if (isSel) ElectricCyan else TitaniumBorder),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable { status = opt }
                        ) {
                            Text(
                                text = opt,
                                color = if (isSel) Color(0xFF0D1B2A) else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: due.amount
                    onSave(
                        due.copy(
                            monthYear = monthYear,
                            dueDate = dueDate,
                            amount = amt,
                            status = status
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Salvar Correção", color = Color(0xFF0D1B2A), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}

@Composable
fun PayDueDialog(
    due: MonthlyDueEntity,
    onDismiss: () -> Unit,
    onConfirm: (method: String, notes: String?) -> Unit
) {
    var paymentMethod by remember { mutableStateOf("PIX") }
    var notes by remember { mutableStateOf("") }
    val methods = listOf("PIX", "Dinheiro", "Transferência")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBgCardElevated,
        title = {
            Text(
                text = "Confirmar Baixa de Mensalidade",
                color = EmeraldPaid,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Irmão: ${due.memberNickname} (${due.memberName})",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Referência: Mês ${due.monthYear} • Valor: R$ ${"%.2f".format(Locale.US, due.amount)}",
                    color = GoldAmberLight,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Forma de Pagamento:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    methods.forEach { m ->
                        val isSel = paymentMethod == m
                        Surface(
                            color = if (isSel) EmeraldPaid else DarkBgDeep,
                            border = BorderStroke(1.dp, if (isSel) EmeraldPaid else TitaniumBorder),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable { paymentMethod = m }
                        ) {
                            Text(
                                text = m,
                                color = if (isSel) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Comprovante / Autenticação (opcional)") },
                    placeholder = { Text("Ex: Código Tx PIX #84920") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(paymentMethod, notes.ifBlank { null }) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPaid),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Confirmar Baixa", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}

@Composable
fun GenerateBatchDuesDialog(
    defaultAmount: Double,
    activeMembersCount: Int,
    onDismiss: () -> Unit,
    onGenerate: (monthYear: String, dueDate: String, amount: Double) -> Unit
) {
    var monthYear by remember { mutableStateOf("10/2026") }
    var dueDate by remember { mutableStateOf("10/10/2026") }
    var amountStr by remember { mutableStateOf(defaultAmount.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBgCardElevated,
        title = {
            Text(
                text = "Gerar Lote de Mensalidades",
                color = GoldAmberLight,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Serão geradas mensalidades para todos os $activeMembersCount integrantes ativos do clube.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                MonthYearPickerField(
                    value = monthYear,
                    onValueChange = { monthYear = it },
                    label = "Mês/Ano de Referência *"
                )

                Spacer(modifier = Modifier.height(8.dp))

                DueDatePickerField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = "Data de Vencimento *"
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Valor da Mensalidade (R$)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.toDoubleOrNull() ?: defaultAmount
                    onGenerate(monthYear, dueDate, amount)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAmber),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Gerar Lote", color = Color(0xFF1C1300), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}
