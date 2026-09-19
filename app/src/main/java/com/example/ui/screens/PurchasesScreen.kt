package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.CollectivePurchaseEntity
import com.example.data.MemberEntity
import com.example.data.PurchaseQuotaEntity
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
fun PurchasesScreen(
    viewModel: ClubViewModel
) {
    val context = LocalContext.current
    val purchases by viewModel.collectivePurchases.collectAsState()
    val quotas by viewModel.purchaseQuotas.collectAsState()
    val members by viewModel.members.collectAsState()
    val settings by viewModel.clubSettings.collectAsState()
    val role by viewModel.currentRole.collectAsState()
    val selectedMemberId by viewModel.selectedMemberId.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val canInsert = currentUser?.canInsert ?: (role != UserRole.MEMBER)
    val canEdit = currentUser?.canEdit ?: (role != UserRole.MEMBER)
    val canDelete = currentUser?.canDelete ?: (role == UserRole.ADMIN)

    var showNewPurchaseDialog by remember { mutableStateOf(false) }
    var purchaseToEdit by remember { mutableStateOf<CollectivePurchaseEntity?>(null) }
    var purchaseToDelete by remember { mutableStateOf<CollectivePurchaseEntity?>(null) }
    var activeWhatsAppQuota by remember { mutableStateOf<PurchaseQuotaEntity?>(null) }

    // Filter purchases if in Member role
    val displayedPurchases = if (role == UserRole.MEMBER) {
        val memberQuotas = quotas.filter { it.memberId == (selectedMemberId ?: -1L) }
        val purchaseIds = memberQuotas.map { it.purchaseId }.toSet()
        purchases.filter { it.id in purchaseIds }
    } else {
        purchases
    }

    val totalPurchasesCost = displayedPurchases.sumOf { it.totalCost }
    val totalPendingQuotas = quotas.filter { it.status != "Paga" }.sumOf { it.amount }

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
                                text = "COMPRAS COLETIVAS & RATEIOS",
                                color = GoldAmberLight,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Coletes, pneus, peças e viagens divididas entre os irmãos",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Universal Action Bar (Inserir, Corrigir, Excluir)
                        UnifiedCrudActionBar(
                            onInsert = { showNewPurchaseDialog = true },
                            onEdit = { purchaseToEdit = displayedPurchases.firstOrNull() },
                            onDelete = { purchaseToDelete = displayedPurchases.firstOrNull() },
                            canInsert = canInsert,
                            canEdit = canEdit && displayedPurchases.isNotEmpty(),
                            canDelete = canDelete && displayedPurchases.isNotEmpty(),
                            insertLabel = "Inserir Compra",
                            editLabel = "Corrigir Compra",
                            deleteLabel = "Excluir Compra"
                        )

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
                                    Text("TOTAL COMPRADO", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text("R$ ${"%.2f".format(Locale.US, totalPurchasesCost)}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("COTAS A RECEBER", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text("R$ ${"%.2f".format(Locale.US, totalPendingQuotas)}", color = GoldAmber, fontSize = 13.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("COMPRAS ATIVAS", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text("${displayedPurchases.size} itens", color = ElectricCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // List
                if (displayedPurchases.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhuma compra coletiva em andamento.",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    items(displayedPurchases) { purchase ->
                        val purchaseQuotas = quotas.filter { it.purchaseId == purchase.id }

                        PurchaseItemCard(
                            purchase = purchase,
                            quotas = purchaseQuotas,
                            userRole = role,
                            canEdit = canEdit,
                            canDelete = canDelete,
                            viewModel = viewModel,
                            onEdit = { purchaseToEdit = purchase },
                            onDelete = { purchaseToDelete = purchase },
                            onPayQuota = { quota -> viewModel.payPurchaseQuota(quota) },
                            onWhatsAppQuota = { quota -> activeWhatsAppQuota = quota }
                        )
                    }
                }
            }
        }
    }

    // Modal: Nova Compra Coletiva
    if (showNewPurchaseDialog) {
        NewPurchaseDialog(
            members = members.filter { it.status == "Ativo" },
            onDismiss = { showNewPurchaseDialog = false },
            onConfirm = { title, supplier, cost, installments, firstDue, targetType, selectedMembers ->
                viewModel.createCollectivePurchase(title, supplier, cost, installments, firstDue, targetType, selectedMembers)
                showNewPurchaseDialog = false
            }
        )
    }

    // Modal: Corrigir Compra Coletiva
    purchaseToEdit?.let { purchase ->
        EditPurchaseDialog(
            purchase = purchase,
            onDismiss = { purchaseToEdit = null },
            onSave = { updated ->
                viewModel.updatePurchase(updated)
                purchaseToEdit = null
            }
        )
    }

    // Modal: Confirmar Exclusão de Compra
    purchaseToDelete?.let { purchase ->
        ConfirmDeleteDialog(
            title = "Excluir Compra Coletiva",
            message = "Deseja realmente cancelar e excluir a compra '${purchase.title}' de R$ ${"%.2f".format(Locale.US, purchase.totalCost)}? Todas as cotas e rateios associados serão removidos.",
            onConfirm = {
                viewModel.deletePurchase(purchase)
                purchaseToDelete = null
            },
            onDismiss = { purchaseToDelete = null }
        )
    }

    // Modal: WhatsApp Cobrança
    activeWhatsAppQuota?.let { quota ->
        val member = members.find { it.id == quota.memberId }
        WhatsAppPaymentDialog(
            recipientName = quota.memberNickname,
            recipientPhone = member?.phone ?: "",
            description = "Rateio ${quota.purchaseTitle} (Parc. #${quota.installmentNumber})",
            amount = quota.amount,
            dueDate = quota.dueDate,
            pixKey = settings?.pixKey ?: "financeiro@guerreirosdobem.mc.br",
            pixHolder = settings?.pixHolderName ?: "Guerreiros do Bem MC",
            onDismiss = { activeWhatsAppQuota = null }
        )
    }
}

@Composable
fun PurchaseItemCard(
    purchase: CollectivePurchaseEntity,
    quotas: List<PurchaseQuotaEntity>,
    userRole: UserRole,
    canEdit: Boolean,
    canDelete: Boolean,
    viewModel: ClubViewModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPayQuota: (PurchaseQuotaEntity) -> Unit,
    onWhatsAppQuota: (PurchaseQuotaEntity) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val paidCount = quotas.count { it.status == "Paga" }
    val totalCount = quotas.size
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
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = GoldAmber, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = purchase.title,
                            color = GoldAmberLight,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Fornecedor: ${purchase.supplier} • ${purchase.assignedMemberNames}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "R$ ${"%.2f".format(Locale.US, purchase.totalCost)}",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${purchase.installmentsCount}x parcelas",
                        color = ElectricCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Purchase CRUD Action buttons
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
                        text = "Progresso: $paidCount de $totalCount cotas quitadas",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        color = if (progress >= 1f) EmeraldPaid else GoldAmber,
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
                    text = if (expanded) "Ocultar Cotas Individuais" else "Ver Cotas e Parcelas dos Irmãos (${quotas.size})",
                    color = GoldAmberLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = GoldAmber,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Expanded quotas
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = TitaniumBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    quotas.forEach { quota ->
                        val daysUntil = viewModel.calculateDaysUntilDueDate(quota.dueDate)
                        val isPaid = quota.status == "Paga"
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
                                        text = "${quota.memberNickname} • Parcela ${quota.installmentNumber}/${purchase.installmentsCount}",
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
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
                                    text = "Vencimento: ${quota.dueDate}",
                                    color = TextMuted,
                                    fontSize = 9.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "R$ ${"%.2f".format(Locale.US, quota.amount)}",
                                    color = if (quota.status == "Paga") EmeraldPaid else GoldAmberLight,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                if (quota.status != "Paga") {
                                    IconButton(
                                        onClick = { onWhatsAppQuota(quota) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = "WhatsApp", tint = EmeraldPaid, modifier = Modifier.size(14.dp))
                                    }

                                    if (userRole != UserRole.MEMBER) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Button(
                                            onClick = { onPayQuota(quota) },
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
fun NewPurchaseDialog(
    members: List<MemberEntity>,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        supplier: String,
        cost: Double,
        installments: Int,
        firstDue: String,
        targetType: String,
        selectedMembers: List<MemberEntity>
    ) -> Unit
) {
    var title by remember { mutableStateOf("Novo Colete Bordado") }
    var supplier by remember { mutableStateOf("Couros & Arte Custom") }
    var costStr by remember { mutableStateOf("1200.00") }
    var installmentsCountStr by remember { mutableStateOf("3") }
    var firstDueDate by remember { mutableStateOf("20/10/2026") }

    val selectedMembersList = remember { mutableStateListOf<MemberEntity>().apply { addAll(members) } }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBgCardElevated,
        title = {
            Text(
                text = "Cadastrar Compra Coletiva / Rateio",
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
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Descrição / Item * (ex: Lote Pneus, Coletes)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = supplier,
                    onValueChange = { supplier = it },
                    label = { Text("Fornecedor / Loja *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = costStr,
                        onValueChange = { costStr = it },
                        label = { Text("Custo Total (R$) *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = installmentsCountStr,
                        onValueChange = { installmentsCountStr = it },
                        label = { Text("Parcelas (1 a 12)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                DueDatePickerField(
                    value = firstDueDate,
                    onValueChange = { firstDueDate = it },
                    label = "Data do 1º Vencimento *"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Irmãos participantes do rateio (${selectedMembersList.size}/${members.size}):",
                    color = GoldAmberLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                members.forEach { m ->
                    val isChecked = selectedMembersList.contains(m)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isChecked) selectedMembersList.remove(m) else selectedMembersList.add(m)
                            }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { check ->
                                if (check) selectedMembersList.add(m) else selectedMembersList.remove(m)
                            },
                            colors = CheckboxDefaults.colors(checkedColor = GoldAmber)
                        )
                        Text(
                            text = "${m.roadNickname} (${m.role})",
                            color = TextPrimary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cost = costStr.toDoubleOrNull() ?: 0.0
                    val inst = installmentsCountStr.toIntOrNull() ?: 1
                    if (title.isNotBlank() && cost > 0 && selectedMembersList.isNotEmpty()) {
                        onConfirm(title, supplier, cost, inst, firstDueDate, "Rateio Coletivo", selectedMembersList.toList())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAmber),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Confirmar Compra", color = Color(0xFF1C1300), fontWeight = FontWeight.Bold)
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
fun EditPurchaseDialog(
    purchase: CollectivePurchaseEntity,
    onDismiss: () -> Unit,
    onSave: (CollectivePurchaseEntity) -> Unit
) {
    var title by remember { mutableStateOf(purchase.title) }
    var supplier by remember { mutableStateOf(purchase.supplier) }
    var status by remember { mutableStateOf(purchase.status) }
    val statuses = listOf("Ativo", "Concluído", "Cancelado")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBgCardElevated,
        title = {
            Text("Corrigir Compra Coletiva", color = GoldAmberLight, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Custo Total: R$ ${"%.2f".format(Locale.US, purchase.totalCost)} • ${purchase.installmentsCount} parcelas", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Participantes: ${purchase.assignedMemberNames}", color = TextSecondary, fontSize = 11.sp)

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Descrição / Título do Item") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = supplier,
                    onValueChange = { supplier = it },
                    label = { Text("Fornecedor / Loja") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Status:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    statuses.forEach { st ->
                        val isSel = status == st
                        Surface(
                            color = if (isSel) GoldAmber else DarkBgDeep,
                            border = BorderStroke(1.dp, if (isSel) GoldAmber else TitaniumBorder),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable { status = st }
                        ) {
                            Text(
                                text = st,
                                color = if (isSel) Color(0xFF1C1300) else TextSecondary,
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
                        purchase.copy(
                            title = title,
                            supplier = supplier,
                            status = status
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAmber),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Salvar Correção", color = Color(0xFF1C1300), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}
