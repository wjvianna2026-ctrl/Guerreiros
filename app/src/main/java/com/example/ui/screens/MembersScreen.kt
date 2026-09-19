package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.ClubPatentesRegistry
import com.example.data.LoanInstallmentEntity
import com.example.data.MemberEntity
import com.example.data.MonthlyDueEntity
import com.example.data.PurchaseQuotaEntity
import com.example.ui.ClubViewModel
import com.example.ui.ConfirmDeleteDialog
import com.example.ui.UnifiedCrudActionBar
import com.example.ui.UserRole
import com.example.ui.WhatsAppHelper
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
fun MembersScreen(
    viewModel: ClubViewModel
) {
    val context = LocalContext.current
    val members by viewModel.members.collectAsState()
    val monthlyDues by viewModel.monthlyDues.collectAsState()
    val loanInstallments by viewModel.loanInstallments.collectAsState()
    val purchaseQuotas by viewModel.purchaseQuotas.collectAsState()
    val settings by viewModel.clubSettings.collectAsState()
    val role by viewModel.currentRole.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf("Todos") }

    var memberForStatement by remember { mutableStateOf<MemberEntity?>(null) }
    var memberToEdit by remember { mutableStateOf<MemberEntity?>(null) }
    var memberToDelete by remember { mutableStateOf<MemberEntity?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    var activeWhatsAppDialog by remember { mutableStateOf<Triple<String, String, Double>?>(null) }

    val canInsert = currentUser?.canInsert ?: (role == UserRole.ADMIN)
    val canEdit = currentUser?.canEdit ?: (role == UserRole.ADMIN)
    val canDelete = currentUser?.canDelete ?: (role == UserRole.ADMIN)

    val rolesList = listOf(
        "Todos",
        "🏛️ Diretoria",
        "🏍️ Estrada",
        "⚡ Graduação"
    ) + ClubPatentesRegistry.allTitles

    val filteredMembers = members.filter { member ->
        val matchesSearch = member.fullName.contains(searchQuery, ignoreCase = true) ||
                member.roadNickname.contains(searchQuery, ignoreCase = true)
        val matchesRole = when (selectedRoleFilter) {
            "Todos" -> true
            "🏛️ Diretoria" -> ClubPatentesRegistry.families.first { it.id == "diretoria" }.patentes.any { it.title.equals(member.role, ignoreCase = true) } || member.role.contains("Tesoureiro", ignoreCase = true)
            "🏍️ Estrada" -> ClubPatentesRegistry.families.first { it.id == "estrada_disciplina" }.patentes.any { it.title.equals(member.role, ignoreCase = true) }
            "⚡ Graduação" -> ClubPatentesRegistry.families.first { it.id == "graduacao_ingresso" }.patentes.any { it.title.equals(member.role, ignoreCase = true) }
            else -> member.role.equals(selectedRoleFilter, ignoreCase = true) || (selectedRoleFilter == "Diretor Financeiro" && member.role == "Tesoureiro")
        }
        matchesSearch && matchesRole
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWide = maxWidth > 700.dp

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
                        Text(
                            text = "IRMÃOS DO MOTO CLUBE",
                            color = GoldAmberLight,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Quadro de integrantes, cargos na estrada e fichas financeiras",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Universal CRUD Action Bar (Inserir, Corrigir, Excluir)
                        UnifiedCrudActionBar(
                            onInsert = { showAddDialog = true },
                            onEdit = {
                                memberToEdit = filteredMembers.firstOrNull()
                            },
                            onDelete = {
                                memberToDelete = filteredMembers.firstOrNull()
                            },
                            canInsert = canInsert,
                            canEdit = canEdit && filteredMembers.isNotEmpty(),
                            canDelete = canDelete && filteredMembers.isNotEmpty(),
                            insertLabel = "Inserir Irmão",
                            editLabel = "Corrigir Irmão",
                            deleteLabel = "Excluir Irmão"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Search input
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Buscar por nome ou vulgo de estrada...", color = TextMuted, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = GoldAmber)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Limpar", tint = TextSecondary)
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkBgCard,
                                unfocusedContainerColor = DarkBgCard,
                                focusedBorderColor = GoldAmber,
                                unfocusedBorderColor = TitaniumBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Role filter chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(rolesList) { roleName ->
                                val isSelected = selectedRoleFilter == roleName
                                Surface(
                                    color = if (isSelected) GoldAmber else DarkBgCard,
                                    border = BorderStroke(1.dp, if (isSelected) GoldAmberLight else TitaniumBorder),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.clickable { selectedRoleFilter = roleName }
                                ) {
                                    Text(
                                        text = roleName,
                                        color = if (isSelected) Color(0xFF1C1300) else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Members List
                items(filteredMembers) { member ->
                    val memberDues = monthlyDues.filter { it.memberId == member.id }
                    val memberLoans = loanInstallments.filter { it.memberId == member.id }
                    val memberQuotas = purchaseQuotas.filter { it.memberId == member.id }

                    val totalPaid = memberDues.filter { it.status == "Paga" }.sumOf { it.amount } +
                            memberLoans.filter { it.status == "Paga" }.sumOf { it.totalAmount } +
                            memberQuotas.filter { it.status == "Paga" }.sumOf { it.amount }

                    val totalPending = memberDues.filter { it.status != "Paga" }.sumOf { it.amount } +
                            memberLoans.filter { it.status != "Paga" }.sumOf { it.totalAmount } +
                            memberQuotas.filter { it.status != "Paga" }.sumOf { it.amount }

                    MemberCard(
                        member = member,
                        totalPaid = totalPaid,
                        totalPending = totalPending,
                        userRole = role,
                        canEdit = canEdit,
                        canDelete = canDelete,
                        onViewStatement = { memberForStatement = member },
                        onEdit = { memberToEdit = member },
                        onDelete = { memberToDelete = member },
                        onWhatsApp = {
                            activeWhatsAppDialog = Triple(member.roadNickname, member.phone, totalPending)
                        }
                    )
                }
            }

            // FAB: Cadastrar Irmão
            if (canInsert) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = GoldAmber,
                    contentColor = Color(0xFF1C1300),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Cadastrar Irmão")
                }
            }
        }
    }

    // Modal: Ficha Individual do Irmão
    memberForStatement?.let { member ->
        val memberDues = monthlyDues.filter { it.memberId == member.id }
        val memberLoans = loanInstallments.filter { it.memberId == member.id }
        val memberQuotas = purchaseQuotas.filter { it.memberId == member.id }

        MemberStatementDialog(
            member = member,
            dues = memberDues,
            loans = memberLoans,
            quotas = memberQuotas,
            onDismiss = { memberForStatement = null }
        )
    }

    // Modal: Cadastrar Novo Irmão
    if (showAddDialog) {
        AddEditMemberDialog(
            member = null,
            onDismiss = { showAddDialog = false },
            onSave = { newMember ->
                viewModel.addMember(newMember)
                showAddDialog = false
            }
        )
    }

    // Modal: Editar Irmão
    memberToEdit?.let { member ->
        AddEditMemberDialog(
            member = member,
            onDismiss = { memberToEdit = null },
            onSave = { updated ->
                viewModel.updateMember(updated)
                memberToEdit = null
            }
        )
    }

    // WhatsApp Dialog
    activeWhatsAppDialog?.let { (nickname, phone, pendingAmount) ->
        WhatsAppPaymentDialog(
            recipientName = nickname,
            recipientPhone = phone,
            description = "Saldo em aberto no Moto Clube",
            amount = if (pendingAmount > 0) pendingAmount else 50.0,
            dueDate = "Conforme vencimentos",
            pixKey = settings?.pixKey ?: "financeiro@guerreirosdobem.mc.br",
            pixHolder = settings?.pixHolderName ?: "Guerreiros do Bem MC",
            onDismiss = { activeWhatsAppDialog = null }
        )
    }

    // Modal: Confirmar Exclusão de Irmão
    memberToDelete?.let { member ->
        ConfirmDeleteDialog(
            title = "Excluir Irmão do Moto Clube",
            message = "Deseja realmente remover o cadastro de ${member.fullName} (${member.roadNickname})? Esta ação é definitiva.",
            onConfirm = {
                viewModel.deleteMember(member)
                memberToDelete = null
            },
            onDismiss = {
                memberToDelete = null
            }
        )
    }
}

@Composable
fun MemberCard(
    member: MemberEntity,
    totalPaid: Double,
    totalPending: Double,
    userRole: UserRole,
    canEdit: Boolean,
    canDelete: Boolean,
    onViewStatement: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onWhatsApp: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 5.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = BorderStroke(1.dp, TitaniumBorder),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Avatar with Motorcycle badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(GoldAmber.copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, GoldAmber, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TwoWheeler,
                            contentDescription = null,
                            tint = GoldAmber,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = member.roadNickname.uppercase(Locale.ROOT),
                                color = GoldAmberLight,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val patenteInfo = ClubPatentesRegistry.findPatente(member.role)
                            val badgeColor = when (patenteInfo?.familyName) {
                                "Diretoria Executiva" -> GoldAmberLight
                                "Comando de Estrada & Disciplina" -> ElectricCyan
                                else -> EmeraldPaid
                            }
                            Surface(
                                color = badgeColor.copy(alpha = 0.15f),
                                border = BorderStroke(0.8.dp, badgeColor),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = member.role,
                                    color = badgeColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = member.fullName,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // WhatsApp action icon
                IconButton(onClick = onWhatsApp) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "WhatsApp",
                        tint = EmeraldPaid,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Contact info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = member.phone, color = TextSecondary, fontSize = 10.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QrCode, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "PIX: ${member.pixKey}", color = TextSecondary, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = TitaniumBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            // Totals and Statement / Action Buttons (Ficha, Corrigir, Excluir)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = "Pago: R$ ${"%.2f".format(Locale.US, totalPaid)}",
                        color = EmeraldPaid,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Em aberto: R$ ${"%.2f".format(Locale.US, totalPending)}",
                        color = if (totalPending > 0) ScarletOverdue else TextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewStatement,
                        border = BorderStroke(1.dp, GoldAmber),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Ficha", color = GoldAmberLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

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
            }
        }
    }
}

@Composable
fun MemberStatementDialog(
    member: MemberEntity,
    dues: List<MonthlyDueEntity>,
    loans: List<LoanInstallmentEntity>,
    quotas: List<PurchaseQuotaEntity>,
    onDismiss: () -> Unit
) {
    val totalPaidDues = dues.filter { it.status == "Paga" }.sumOf { it.amount }
    val totalPendingDues = dues.filter { it.status != "Paga" }.sumOf { it.amount }

    val totalPaidLoans = loans.filter { it.status == "Paga" }.sumOf { it.totalAmount }
    val totalPendingLoans = loans.filter { it.status != "Paga" }.sumOf { it.totalAmount }

    val totalPaidQuotas = quotas.filter { it.status == "Paga" }.sumOf { it.amount }
    val totalPendingQuotas = quotas.filter { it.status != "Paga" }.sumOf { it.amount }

    val grandTotalPaid = totalPaidDues + totalPaidLoans + totalPaidQuotas
    val grandTotalPending = totalPendingDues + totalPendingLoans + totalPendingQuotas

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBgCardElevated,
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = GoldAmber, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ficha: ${member.roadNickname} (${member.role})",
                        color = GoldAmberLight,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${member.fullName} • Desde ${member.joinDate}",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Summary Box
                Surface(
                    color = DarkBgDeep,
                    border = BorderStroke(1.dp, TitaniumBorder),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("TOTAL PAGO", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("R$ ${"%.2f".format(Locale.US, grandTotalPaid)}", color = EmeraldPaid, fontSize = 14.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("TOTAL EM ABERTO", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("R$ ${"%.2f".format(Locale.US, grandTotalPending)}", color = if (grandTotalPending > 0) ScarletOverdue else EmeraldPaid, fontSize = 14.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mensalidades
                Text("1. Mensalidades (${dues.size})", color = GoldAmberLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                if (dues.isEmpty()) {
                    Text("Nenhuma mensalidade registrada.", color = TextMuted, fontSize = 11.sp)
                } else {
                    dues.forEach { due ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${due.monthYear} (Venc: ${due.dueDate})", color = TextPrimary, fontSize = 11.sp)
                            Text(
                                text = "R$ ${"%.2f".format(Locale.US, due.amount)} • ${due.status}",
                                color = if (due.status == "Paga") EmeraldPaid else ScarletOverdue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Fundo Rotativo (Empréstimos)
                Text("2. Fundo Rotativo / Empréstimos (${loans.size})", color = ElectricCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                if (loans.isEmpty()) {
                    Text("Nenhum empréstimo ativo para este irmão.", color = TextMuted, fontSize = 11.sp)
                } else {
                    loans.forEach { inst ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Parc. #${inst.installmentNumber} (Venc: ${inst.dueDate})", color = TextPrimary, fontSize = 11.sp)
                            Text(
                                text = "R$ ${"%.2f".format(Locale.US, inst.totalAmount)} • ${inst.status}",
                                color = if (inst.status == "Paga") EmeraldPaid else GoldAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Compras Coletivas
                Text("3. Compras Coletivas & Rateios (${quotas.size})", color = GoldAmberLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                if (quotas.isEmpty()) {
                    Text("Nenhum rateio registrado.", color = TextMuted, fontSize = 11.sp)
                } else {
                    quotas.forEach { quota ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${quota.purchaseTitle} (P#${quota.installmentNumber})", color = TextPrimary, fontSize = 11.sp)
                            Text(
                                text = "R$ ${"%.2f".format(Locale.US, quota.amount)} • ${quota.status}",
                                color = if (quota.status == "Paga") EmeraldPaid else ScarletOverdue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = GoldAmber),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Fechar Extrato", color = Color(0xFF1C1300), fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun AddEditMemberDialog(
    member: MemberEntity?,
    onDismiss: () -> Unit,
    onSave: (MemberEntity) -> Unit
) {
    var fullName by remember { mutableStateOf(member?.fullName ?: "") }
    var roadNickname by remember { mutableStateOf(member?.roadNickname ?: "") }
    var role by remember { mutableStateOf(member?.role ?: "Escudo Fechado") }
    var phone by remember { mutableStateOf(member?.phone ?: "") }
    var email by remember { mutableStateOf(member?.email ?: "") }
    var pixKey by remember { mutableStateOf(member?.pixKey ?: "") }
    var joinDate by remember {
        mutableStateOf(member?.joinDate ?: SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()))
    }
    var notes by remember { mutableStateOf(member?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBgCardElevated,
        title = {
            Text(
                text = if (member == null) "Cadastrar Irmão no Moto Clube" else "Editar Dados do Integrante",
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
                    value = roadNickname,
                    onValueChange = { roadNickname = it },
                    label = { Text("Vulgo de Estrada * (ex: Trovão, Lobo)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nome Completo *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Seletor de Cargo / Patente (15 Patentes em 3 Famílias com seta clicável)
                PatenteSelectionField(
                    currentRole = role,
                    onRoleSelected = { role = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("WhatsApp com DDD * (ex: 11987654321)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = pixKey,
                    onValueChange = { pixKey = it },
                    label = { Text("Chave PIX do Irmão") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observações (Moto, Modelo, etc.)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (roadNickname.isNotBlank() && fullName.isNotBlank()) {
                        val newEntity = MemberEntity(
                            id = member?.id ?: 0L,
                            fullName = fullName,
                            roadNickname = roadNickname,
                            role = role,
                            phone = phone,
                            email = email,
                            pixKey = pixKey,
                            joinDate = joinDate,
                            status = member?.status ?: "Ativo",
                            notes = notes
                        )
                        onSave(newEntity)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAmber),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Salvar Irmão", color = Color(0xFF1C1300), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}
