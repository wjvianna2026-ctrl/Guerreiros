package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.LoanInstallmentEntity
import com.example.data.LoanRequestEntity
import com.example.data.MemberEntity
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LoansScreen(
    viewModel: ClubViewModel
) {
    val context = LocalContext.current
    val loans by viewModel.loans.collectAsState()
    val installments by viewModel.loanInstallments.collectAsState()
    val members by viewModel.members.collectAsState()
    val settings by viewModel.clubSettings.collectAsState()
    val role by viewModel.currentRole.collectAsState()
    val selectedMemberId by viewModel.selectedMemberId.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val canInsert = currentUser?.canInsert ?: (role != UserRole.MEMBER)
    val canEdit = currentUser?.canEdit ?: (role != UserRole.MEMBER)
    val canDelete = currentUser?.canDelete ?: (role == UserRole.ADMIN)

    var showNewLoanDialog by remember { mutableStateOf(false) }
    var loanToEdit by remember { mutableStateOf<LoanRequestEntity?>(null) }
    var loanToDelete by remember { mutableStateOf<LoanRequestEntity?>(null) }
    var activeWhatsAppDialog by remember { mutableStateOf<LoanInstallmentEntity?>(null) }
    var installmentToPay by remember { mutableStateOf<LoanInstallmentEntity?>(null) }

    // Filter loans by role
    val displayedLoans = loans.filter { loan ->
        if (role == UserRole.MEMBER) {
            loan.memberId == (selectedMemberId ?: -1L)
        } else true
    }

    val totalActiveAmount = displayedLoans.sumOf { it.totalAmount }
    val totalInterestAmount = displayedLoans.sumOf { it.totalInterest }

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
                // Header
                item {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "FUNDO ROTATIVO DE APOIO",
                                color = ElectricCyan,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Auxílio fraterno entre irmãos com juros sociais para o caixa",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Universal Action Bar (Inserir, Corrigir, Excluir)
                        UnifiedCrudActionBar(
                            onInsert = { showNewLoanDialog = true },
                            onEdit = { loanToEdit = displayedLoans.firstOrNull() },
                            onDelete = { loanToDelete = displayedLoans.firstOrNull() },
                            canInsert = canInsert,
                            canEdit = canEdit && displayedLoans.isNotEmpty(),
                            canDelete = canDelete && displayedLoans.isNotEmpty(),
                            insertLabel = "Inserir Empréstimo",
                            editLabel = "Corrigir Empréstimo",
                            deleteLabel = "Excluir Empréstimo"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Metric Card
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
                                    Text("EMPRESTADO ATIVO", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text("R$ ${"%.2f".format(Locale.US, totalActiveAmount)}", color = ElectricCyan, fontSize = 13.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("JUROS SOCIAIS GERADOS", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text("R$ ${"%.2f".format(Locale.US, totalInterestAmount)}", color = EmeraldPaid, fontSize = 13.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("EMPRÉSTIMOS", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text("${displayedLoans.size} operações", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

        // Loans list
        if (displayedLoans.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum empréstimo ativo no momento.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            items(displayedLoans) { loan ->
                val loanInsts = installments.filter { it.loanId == loan.id }

                LoanCard(
                    loan = loan,
                    installments = loanInsts,
                    userRole = role,
                    canEdit = canEdit,
                    canDelete = canDelete,
                    viewModel = viewModel,
                    onEdit = { loanToEdit = loan },
                    onDelete = { loanToDelete = loan },
                    onPayInstallment = { installmentToPay = it },
                    onWhatsAppInstallment = { activeWhatsAppDialog = it }
                )
            }
        }
            }
        }
    }

    // Modal: Novo Empréstimo
    if (showNewLoanDialog) {
        NewLoanDialog(
            members = members.filter { it.status == "Ativo" },
            onDismiss = { showNewLoanDialog = false },
            onConfirm = { member, amount, rate, count, purpose, firstDue ->
                viewModel.createLoan(member, amount, rate, count, purpose, firstDue)
                showNewLoanDialog = false
            }
        )
    }

    // Modal: Corrigir Empréstimo
    loanToEdit?.let { loan ->
        EditLoanDialog(
            loan = loan,
            onDismiss = { loanToEdit = null },
            onSave = { updated ->
                viewModel.updateLoan(updated)
                loanToEdit = null
            }
        )
    }

    // Modal: Confirmar Exclusão de Empréstimo
    loanToDelete?.let { loan ->
        ConfirmDeleteDialog(
            title = "Excluir Empréstimo",
            message = "Deseja realmente cancelar e excluir o empréstimo de ${loan.memberNickname} no valor de R$ ${"%.2f".format(Locale.US, loan.totalAmount)}? Todas as parcelas associadas serão removidas.",
            onConfirm = {
                viewModel.deleteLoan(loan)
                loanToDelete = null
            },
            onDismiss = { loanToDelete = null }
        )
    }

    // Modal: Baixa de Parcela
    installmentToPay?.let { inst ->
        PayInstallmentDialog(
            installment = inst,
            onDismiss = { installmentToPay = null },
            onConfirm = { method ->
                viewModel.payLoanInstallment(inst, method)
                installmentToPay = null
            }
        )
    }

    // Modal: WhatsApp Cobrança
    activeWhatsAppDialog?.let { inst ->
        val member = members.find { it.id == inst.memberId }
        WhatsAppPaymentDialog(
            recipientName = inst.memberNickname,
            recipientPhone = member?.phone ?: "",
            description = "Parcela #${inst.installmentNumber} Fundo Rotativo MC",
            amount = inst.totalAmount,
            dueDate = inst.dueDate,
            pixKey = settings?.pixKey ?: "financeiro@guerreirosdobem.mc.br",
            pixHolder = settings?.pixHolderName ?: "Guerreiros do Bem MC",
            onDismiss = { activeWhatsAppDialog = null }
        )
    }
}

@Composable
fun LoanCard(
    loan: LoanRequestEntity,
    installments: List<LoanInstallmentEntity>,
    userRole: UserRole,
    canEdit: Boolean,
    canDelete: Boolean,
    viewModel: ClubViewModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPayInstallment: (LoanInstallmentEntity) -> Unit,
    onWhatsAppInstallment: (LoanInstallmentEntity) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val paidCount = installments.count { it.status == "Paga" }
    val totalCount = installments.size
    val progress = if (totalCount > 0) paidCount.toFloat() / totalCount.toFloat() else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = BorderStroke(1.dp, TitaniumBorder),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = loan.memberNickname.uppercase(Locale.ROOT),
                            color = GoldAmberLight,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = ElectricCyan.copy(alpha = 0.15f),
                            border = BorderStroke(0.8.dp, ElectricCyan),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${loan.monthlyInterestRate}% a.m. juro social",
                                color = ElectricCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "Finalidade: ${loan.purpose}",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "R$ ${"%.2f".format(Locale.US, loan.totalPayable)}",
                        color = GoldAmberLight,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Principal: R$ ${"%.2f".format(Locale.US, loan.totalAmount)}",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            // Loan CRUD action buttons
            if (canEdit || canDelete) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (canEdit) {
                        OutlinedButton(
                            onClick = onEdit,
                            border = BorderStroke(1.dp, ElectricCyan),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Corrigir", tint = ElectricCyan, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Corrigir", color = ElectricCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    if (canDelete) {
                        OutlinedButton(
                            onClick = onDelete,
                            border = BorderStroke(1.dp, ScarletOverdue),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = ScarletOverdue, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Excluir", color = ScarletOverdue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Quitação: $paidCount de $totalCount parcelas pagas",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        color = if (progress >= 1f) EmeraldPaid else ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = EmeraldPaid,
                    trackColor = DarkBgDeep
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Toggle Expand Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expanded) "Ocultar Cronograma de Parcelas" else "Ver Cronograma de Parcelas (${installments.size})",
                    color = ElectricCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Expanded installments
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = TitaniumBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    installments.forEach { inst ->
                        val daysUntil = viewModel.calculateDaysUntilDueDate(inst.dueDate)
                        val isPaid = inst.status == "Paga"
                        val isOverdue = !isPaid && daysUntil < 0
                        val statusText = when {
                            isPaid -> "Paga"
                            daysUntil < 0 -> "Atrasada há ${-daysUntil} dias"
                            daysUntil == 0 -> "Vence hoje"
                            daysUntil == 1 -> "Vence amanhã"
                            else -> "Vence em $daysUntil dias"
                        }
                        val statusColor = when {
                            isPaid -> EmeraldPaid
                            isOverdue -> ScarletOverdue
                            daysUntil in 0..30 -> GoldAmberLight
                            else -> TextSecondary
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Parcela ${inst.installmentNumber}/${loan.installmentsCount} • Venc: ${inst.dueDate}",
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• $statusText",
                                        color = statusColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Principal R$ ${"%.2f".format(Locale.US, inst.principalAmount)} + Juros R$ ${"%.2f".format(Locale.US, inst.interestAmount)}",
                                    color = TextMuted,
                                    fontSize = 9.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "R$ ${"%.2f".format(Locale.US, inst.totalAmount)}",
                                    color = if (inst.status == "Paga") EmeraldPaid else GoldAmberLight,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                if (inst.status != "Paga") {
                                    IconButton(
                                        onClick = { onWhatsAppInstallment(inst) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = "WhatsApp", tint = EmeraldPaid, modifier = Modifier.size(14.dp))
                                    }

                                    if (userRole != UserRole.MEMBER) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Button(
                                            onClick = { onPayInstallment(inst) },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPaid),
                                            shape = RoundedCornerShape(4.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                            modifier = Modifier.height(24.dp)
                                        ) {
                                            Text("Baixar", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                } else {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Paga", tint = EmeraldPaid, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewLoanDialog(
    members: List<MemberEntity>,
    onDismiss: () -> Unit,
    onConfirm: (member: MemberEntity, amount: Double, rate: Double, count: Int, purpose: String, firstDue: String) -> Unit
) {
    var selectedMember by remember { mutableStateOf(members.firstOrNull()) }
    var memberDropdownExpanded by remember { mutableStateOf(false) }

    var amountStr by remember { mutableStateOf("1000.00") }
    var rateStr by remember { mutableStateOf("1.0") }
    var installmentsCountStr by remember { mutableStateOf("6") }
    var purpose by remember { mutableStateOf("Conserto de moto") }
    var firstDueDate by remember { mutableStateOf("15/10/2026") }

    val purposesList = listOf("Conserto de moto", "Viagem / Encontro Nacional", "Socorro pessoal", "Pneus / Troca", "Documentação / IPVA")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBgCardElevated,
        title = {
            Text(
                text = "Solicitação de Fundo de Apoio Social",
                color = ElectricCyan,
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
                // Member Selector
                Text("Irmão Solicitante *", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = "${selectedMember?.roadNickname ?: "Selecionar"} (${selectedMember?.role ?: ""})",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { memberDropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = memberDropdownExpanded,
                        onDismissRequest = { memberDropdownExpanded = false },
                        modifier = Modifier.background(DarkBgCardElevated)
                    ) {
                        members.forEach { m ->
                            DropdownMenuItem(
                                text = { Text("${m.roadNickname} - ${m.fullName}", color = TextPrimary) },
                                onClick = {
                                    selectedMember = m
                                    memberDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Valor Solicitado (R$) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = rateStr,
                        onValueChange = { rateStr = it },
                        label = { Text("Juros Social (% a.m.)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = installmentsCountStr,
                        onValueChange = { installmentsCountStr = it },
                        label = { Text("Nº Parcelas (1 a 24)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = purpose,
                    onValueChange = { purpose = it },
                    label = { Text("Finalidade do Empréstimo *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                DueDatePickerField(
                    value = firstDueDate,
                    onValueChange = { firstDueDate = it },
                    label = "Data de Vencimento da 1ª Parcela *"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val m = selectedMember
                    val amount = amountStr.toDoubleOrNull() ?: 0.0
                    val rate = rateStr.toDoubleOrNull() ?: 1.0
                    val count = installmentsCountStr.toIntOrNull() ?: 6
                    if (m != null && amount > 0 && count > 0) {
                        onConfirm(m, amount, rate, count, purpose, firstDueDate)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Aprovar e Liberar", color = Color(0xFF00363F), fontWeight = FontWeight.Bold)
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
fun PayInstallmentDialog(
    installment: LoanInstallmentEntity,
    onDismiss: () -> Unit,
    onConfirm: (method: String) -> Unit
) {
    var method by remember { mutableStateOf("PIX") }
    val methods = listOf("PIX", "Dinheiro", "Depósito")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBgCardElevated,
        title = {
            Text(
                text = "Baixa de Parcela de Empréstimo",
                color = EmeraldPaid,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "Irmão: ${installment.memberNickname} • Parcela #${installment.installmentNumber}",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Valor Total: R$ ${"%.2f".format(Locale.US, installment.totalAmount)}",
                    color = GoldAmberLight,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "(Principal: R$ ${"%.2f".format(Locale.US, installment.principalAmount)} + Juros Social: R$ ${"%.2f".format(Locale.US, installment.interestAmount)})",
                    color = TextSecondary,
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Forma de Pagamento:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    methods.forEach { m ->
                        val isSel = method == m
                        Surface(
                            color = if (isSel) EmeraldPaid else DarkBgDeep,
                            border = BorderStroke(1.dp, if (isSel) EmeraldPaid else TitaniumBorder),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable { method = m }
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
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(method) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPaid),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Confirmar Recebimento", color = Color.White, fontWeight = FontWeight.Bold)
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
fun EditLoanDialog(
    loan: LoanRequestEntity,
    onDismiss: () -> Unit,
    onSave: (LoanRequestEntity) -> Unit
) {
    var purpose by remember { mutableStateOf(loan.purpose) }
    var notes by remember { mutableStateOf(loan.notes) }
    var status by remember { mutableStateOf(loan.status) }
    val statuses = listOf("Ativo", "Quitado", "Cancelado")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBgCardElevated,
        title = {
            Text("Corrigir Empréstimo", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Irmão: ${loan.memberNickname} (${loan.memberName})", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Valor: R$ ${"%.2f".format(Locale.US, loan.totalAmount)} • Juros: ${loan.monthlyInterestRate}%", color = GoldAmberLight, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = purpose,
                    onValueChange = { purpose = it },
                    label = { Text("Finalidade") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observações") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Status:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    statuses.forEach { st ->
                        val isSel = status == st
                        Surface(
                            color = if (isSel) ElectricCyan else DarkBgDeep,
                            border = BorderStroke(1.dp, if (isSel) ElectricCyan else TitaniumBorder),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable { status = st }
                        ) {
                            Text(
                                text = st,
                                color = if (isSel) Color(0xFF00363F) else TextSecondary,
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
                    onSave(
                        loan.copy(
                            purpose = purpose,
                            notes = notes,
                            status = status
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Salvar Correção", color = Color(0xFF00363F), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}
