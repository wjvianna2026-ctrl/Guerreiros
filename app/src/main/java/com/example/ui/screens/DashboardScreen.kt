package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CashTransactionEntity
import com.example.ui.BikerHeroBanner
import com.example.ui.ClubViewModel
import com.example.ui.ConfirmDeleteDialog
import com.example.ui.FinancialFlowChart
import com.example.ui.NavigationTab
import com.example.ui.PortfolioDonutChart
import com.example.ui.StatCard
import com.example.ui.UnifiedCrudActionBar
import com.example.ui.UpcomingDueItem
import com.example.ui.UserRole
import com.example.ui.WhatsAppHelper
import com.example.ui.WhatsAppPaymentDialog
import com.example.ui.theme.DarkBgCard
import com.example.ui.theme.DarkBgCardElevated
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
fun DashboardScreen(
    viewModel: ClubViewModel,
    onNavigateToTab: (NavigationTab) -> Unit
) {
    val context = LocalContext.current
    val settings by viewModel.clubSettings.collectAsState()
    val kpis by viewModel.dashboardKpis.collectAsState()
    val upcomingItems by viewModel.upcomingDueItems.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val monthlyDues by viewModel.monthlyDues.collectAsState()
    val loans by viewModel.loans.collectAsState()
    val collectivePurchases by viewModel.collectivePurchases.collectAsState()
    val role by viewModel.currentRole.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var activeWhatsAppItem by remember { mutableStateOf<UpcomingDueItem?>(null) }
    var showInsertTransactionDialog by remember { mutableStateOf(false) }
    var showEditTransactionDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var transactionToEdit by remember { mutableStateOf<CashTransactionEntity?>(null) }
    var transactionToDelete by remember { mutableStateOf<CashTransactionEntity?>(null) }

    val canInsert = currentUser?.canInsert ?: (role != UserRole.MEMBER)
    val canEdit = currentUser?.canEdit ?: (role != UserRole.MEMBER)
    val canDelete = currentUser?.canDelete ?: (role == UserRole.ADMIN)

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWide = maxWidth > 720.dp

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero with Custom Motorcycle and Motto
            item {
                Box(modifier = Modifier.widthIn(max = 1200.dp)) {
                    BikerHeroBanner(
                        settings = settings,
                        onCopyPix = {
                            val pix = settings?.pixKey ?: "financeiro@guerreirosdobem.mc.br"
                            WhatsAppHelper.copyToClipboard(context, "Chave PIX", pix)
                        }
                    )
                }
            }

            // UNIVERSAL CRUD ACTION BAR: Inserir, Corrigir, Excluir
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 1200.dp)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    UnifiedCrudActionBar(
                        onInsert = { showInsertTransactionDialog = true },
                        onEdit = {
                            transactionToEdit = transactions.firstOrNull()
                            showEditTransactionDialog = true
                        },
                        onDelete = {
                            transactionToDelete = transactions.firstOrNull()
                            showDeleteConfirmDialog = true
                        },
                        canInsert = canInsert,
                        canEdit = canEdit && transactions.isNotEmpty(),
                        canDelete = canDelete && transactions.isNotEmpty(),
                        insertLabel = "Inserir Caixa",
                        editLabel = "Corrigir Lançamento",
                        deleteLabel = "Excluir Último"
                    )
                }
            }

            // ATALHOS RÁPIDOS PARA CRUD EM OUTROS MÓDULOS
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 1200.dp)
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "ATALHOS RÁPIDOS DE CADASTRO & GESTÃO",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = false,
                            onClick = { onNavigateToTab(NavigationTab.MEMBERS) },
                            label = { Text("+ Irmão", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(14.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = DarkBgCardElevated,
                                labelColor = GoldAmberLight,
                                iconColor = GoldAmber
                            ),
                            border = BorderStroke(1.dp, TitaniumBorder),
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = false,
                            onClick = { onNavigateToTab(NavigationTab.DUES) },
                            label = { Text("+ Mensalidade", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(14.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = DarkBgCardElevated,
                                labelColor = EmeraldPaid,
                                iconColor = EmeraldPaid
                            ),
                            border = BorderStroke(1.dp, TitaniumBorder),
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = false,
                            onClick = { onNavigateToTab(NavigationTab.LOANS) },
                            label = { Text("+ Fundo", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Handshake, contentDescription = null, modifier = Modifier.size(14.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = DarkBgCardElevated,
                                labelColor = ElectricCyan,
                                iconColor = ElectricCyan
                            ),
                            border = BorderStroke(1.dp, TitaniumBorder),
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = false,
                            onClick = { onNavigateToTab(NavigationTab.PURCHASES) },
                            label = { Text("+ Compra", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(14.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = DarkBgCardElevated,
                                labelColor = GoldAmberLight,
                                iconColor = GoldAmberLight
                            ),
                            border = BorderStroke(1.dp, TitaniumBorder),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Section Title: KPIs Principais
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 1200.dp)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "VISÃO GERAL DO CAIXA & FUNDOS",
                        color = GoldAmberLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Saldo consolidado e métricas do Moto Clube em tempo real",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Responsive 4 KPI Cards (Row on Desktop, 2x2 on Mobile)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 1200.dp)
                        .padding(horizontal = 12.dp)
                ) {
                    if (isWide) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatCard(
                                title = "Saldo em Caixa",
                                value = "R$ ${"%.2f".format(Locale.US, kpis.cashBalance)}",
                                subtitle = "Disponível na conta do MC",
                                accentColor = EmeraldPaid,
                                icon = Icons.Default.AccountBalance,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Total a Receber",
                                value = "R$ ${"%.2f".format(Locale.US, kpis.totalPendingReceivables)}",
                                subtitle = "Mensalidades + Empréstimos",
                                accentColor = GoldAmber,
                                icon = Icons.Default.MonetizationOn,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Fundo Rotativo",
                                value = "R$ ${"%.2f".format(Locale.US, kpis.totalActiveLoans)}",
                                subtitle = "Empréstimos de apoio ativos",
                                accentColor = ElectricCyan,
                                icon = Icons.Default.Handshake,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Adimplência Mês",
                                value = "${kpis.complianceRatePercent}%",
                                subtitle = "${kpis.activeMembersCount} irmãos ativos",
                                accentColor = if (kpis.complianceRatePercent >= 70) EmeraldPaid else ScarletOverdue,
                                icon = Icons.Default.TrendingUp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                StatCard(
                                    title = "Saldo em Caixa",
                                    value = "R$ ${"%.2f".format(Locale.US, kpis.cashBalance)}",
                                    subtitle = "Disponível na conta do MC",
                                    accentColor = EmeraldPaid,
                                    icon = Icons.Default.AccountBalance,
                                    modifier = Modifier.weight(1f)
                                )

                                StatCard(
                                    title = "Total a Receber",
                                    value = "R$ ${"%.2f".format(Locale.US, kpis.totalPendingReceivables)}",
                                    subtitle = "Mensalidades + Empréstimos",
                                    accentColor = GoldAmber,
                                    icon = Icons.Default.MonetizationOn,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                StatCard(
                                    title = "Fundo Rotativo",
                                    value = "R$ ${"%.2f".format(Locale.US, kpis.totalActiveLoans)}",
                                    subtitle = "Empréstimos de apoio ativos",
                                    accentColor = ElectricCyan,
                                    icon = Icons.Default.Handshake,
                                    modifier = Modifier.weight(1f)
                                )

                                StatCard(
                                    title = "Adimplência Mês",
                                    value = "${kpis.complianceRatePercent}%",
                                    subtitle = "${kpis.activeMembersCount} irmãos ativos",
                                    accentColor = if (kpis.complianceRatePercent >= 70) EmeraldPaid else ScarletOverdue,
                                    icon = Icons.Default.TrendingUp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // Charts Section (Side-by-side on wide screen)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 1200.dp)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    if (isWide) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1.2f)) {
                                FinancialFlowChart(transactions = transactions)
                            }
                            Box(modifier = Modifier.weight(0.8f)) {
                                PortfolioDonutChart(
                                    transactions = transactions,
                                    monthlyDues = monthlyDues,
                                    loans = loans,
                                    collectivePurchases = collectivePurchases
                                )
                            }
                        }
                    } else {
                        Column {
                            FinancialFlowChart(transactions = transactions)
                            Spacer(modifier = Modifier.height(12.dp))
                            PortfolioDonutChart(
                                transactions = transactions,
                                monthlyDues = monthlyDues,
                                loans = loans,
                                collectivePurchases = collectivePurchases
                            )
                        }
                    }
                }
            }

            // Section Title: Avisos de Vencimento
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 1200.dp)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = GoldAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AVISOS DE VENCIMENTO (1 A 30 DIAS)",
                                    color = GoldAmberLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Text(
                                text = "Pagamentos pendentes de 1 a 30 dias para vencer (mensalidades, compras e fundo)",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // List of Upcoming Items (1 a 30 dias)
            if (upcomingItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 1200.dp)
                            .padding(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
                        border = BorderStroke(1.dp, TitaniumBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldPaid,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Nenhum pagamento vencendo entre 1 e 30 dias",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Não há parcelas pendentes com vencimento nos próximos 30 dias.",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                items(upcomingItems) { item ->
                    Box(modifier = Modifier.widthIn(max = 1200.dp)) {
                        UpcomingDueCard(
                            item = item,
                            userRole = role,
                            onDirectPay = {
                                item.rawDue?.let { due ->
                                    viewModel.payMonthlyDue(due, "PIX", "Baixa rápida pelo Dashboard")
                                }
                                item.rawLoanInstallment?.let { inst ->
                                    viewModel.payLoanInstallment(inst, "PIX")
                                }
                                item.rawPurchaseQuota?.let { quota ->
                                    viewModel.payPurchaseQuota(quota)
                                }
                            },
                            onWhatsApp = {
                                activeWhatsAppItem = item
                            }
                        )
                    }
                }
            }
        }
    }

    // WhatsApp Dialog
    activeWhatsAppItem?.let { item ->
        WhatsAppPaymentDialog(
            recipientName = item.memberNickname,
            recipientPhone = item.memberPhone,
            description = item.description,
            amount = item.amount,
            dueDate = item.dueDate,
            pixKey = settings?.pixKey ?: "financeiro@guerreirosdobem.mc.br",
            pixHolder = settings?.pixHolderName ?: "Guerreiros do Bem MC",
            onDismiss = { activeWhatsAppItem = null }
        )
    }

    // Modal: Inserir Transação no Caixa
    if (showInsertTransactionDialog) {
        InsertCashTransactionDialog(
            onDismiss = { showInsertTransactionDialog = false },
            onConfirm = { type, category, amount, description, memberName ->
                viewModel.addCashTransaction(type, category, amount, description, memberName)
                showInsertTransactionDialog = false
            }
        )
    }

    // Modal: Corrigir Lançamento de Caixa
    if (showEditTransactionDialog && transactionToEdit != null) {
        EditCashTransactionDialog(
            transaction = transactionToEdit!!,
            onDismiss = {
                showEditTransactionDialog = false
                transactionToEdit = null
            },
            onConfirm = { updated ->
                viewModel.updateCashTransaction(updated)
                showEditTransactionDialog = false
                transactionToEdit = null
            }
        )
    }

    // Modal: Confirmar Exclusão de Transação
    if (showDeleteConfirmDialog && transactionToDelete != null) {
        ConfirmDeleteDialog(
            title = "Excluir Lançamento de Caixa",
            message = "Deseja realmente excluir o lançamento '${transactionToDelete?.description}' no valor de R$ ${"%.2f".format(Locale.US, transactionToDelete?.amount ?: 0.0)}? O saldo do caixa será estornado automaticamente.",
            onConfirm = {
                transactionToDelete?.let { viewModel.deleteCashTransaction(it) }
                showDeleteConfirmDialog = false
                transactionToDelete = null
            },
            onDismiss = {
                showDeleteConfirmDialog = false
                transactionToDelete = null
            }
        )
    }
}

@Composable
fun UpcomingDueCard(
    item: UpcomingDueItem,
    userRole: UserRole,
    onDirectPay: () -> Unit,
    onWhatsApp: () -> Unit
) {
    val isOverdue = item.daysUntilDue < 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 5.dp)
            .testTag("upcoming_due_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = BorderStroke(
            1.dp,
            if (isOverdue) ScarletOverdue.copy(alpha = 0.6f) else TitaniumBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.memberNickname,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = when (item.type) {
                            "Mensalidade", "DUES" -> EmeraldPaid.copy(alpha = 0.2f)
                            "Empréstimo", "LOAN" -> ElectricCyan.copy(alpha = 0.2f)
                            else -> GoldAmber.copy(alpha = 0.2f)
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = when (item.type) {
                                "Mensalidade", "DUES" -> "Mensalidade"
                                "Empréstimo", "LOAN" -> "Empréstimo"
                                else -> "Compra"
                            },
                            color = when (item.type) {
                                "Mensalidade", "DUES" -> EmeraldPaid
                                "Empréstimo", "LOAN" -> ElectricCyan
                                else -> GoldAmberLight
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.description,
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Text(
                    text = when {
                        item.daysUntilDue < 0 -> "Atrasado há ${-item.daysUntilDue} dias (${item.dueDate})"
                        item.daysUntilDue == 0 -> "Vence hoje (${item.dueDate})"
                        item.daysUntilDue == 1 -> "Vence amanhã (${item.dueDate})"
                        else -> "Vence em ${item.daysUntilDue} dias (${item.dueDate})"
                    },
                    color = if (isOverdue) ScarletOverdue else GoldAmberLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "R$ ${"%.2f".format(Locale.US, item.amount)}",
                    color = TextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // WhatsApp Button
                    OutlinedButton(
                        onClick = onWhatsApp,
                        border = BorderStroke(1.dp, EmeraldPaid),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Cobrar WhatsApp",
                            tint = EmeraldPaid,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cobrar", color = EmeraldPaid, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Direct Pay button for Admin/Treasurer
                    if (userRole != UserRole.MEMBER) {
                        Button(
                            onClick = onDirectPay,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAmber),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Baixar", color = Color(0xFF1A1202), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dialog for Inserting a New Cash Transaction (Entrada ou Saída)
 */
@Composable
fun InsertCashTransactionDialog(
    onDismiss: () -> Unit,
    onConfirm: (type: String, category: String, amount: Double, description: String, memberName: String?) -> Unit
) {
    var type by remember { mutableStateOf("ENTRADA") }
    var category by remember { mutableStateOf("Mensalidade Avulsa") }
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var memberName by remember { mutableStateOf("") }

    val categories = if (type == "ENTRADA") {
        listOf("Mensalidade Avulsa", "Doação / Patrocínio", "Venda de Patch / Camisa", "Confraternização", "Outras Entradas")
    } else {
        listOf("Combustível Coletivo", "Aluguel da Sede", "Confraternização / Evento", "Manutenção e Ferramentas", "Outras Saídas")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBgCard,
        title = {
            Text(
                text = "Inserir Lançamento de Caixa",
                color = GoldAmberLight,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Type Selector: Entrada vs Saída
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            type = "ENTRADA"
                            category = "Mensalidade Avulsa"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == "ENTRADA") EmeraldPaid else DarkBgCardElevated,
                            contentColor = if (type == "ENTRADA") Color.Black else TextSecondary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("➕ Entrada", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            type = "SAÍDA"
                            category = "Combustível Coletivo"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == "SAÍDA") ScarletOverdue else DarkBgCardElevated,
                            contentColor = if (type == "SAÍDA") Color.White else TextSecondary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("➖ Saída", fontWeight = FontWeight.Bold)
                    }
                }

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Valor (R$)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAmber,
                        unfocusedBorderColor = TitaniumBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição do Lançamento") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAmber,
                        unfocusedBorderColor = TitaniumBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Member Name (optional)
                OutlinedTextField(
                    value = memberName,
                    onValueChange = { memberName = it },
                    label = { Text("Nome / Apelido do Irmão (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAmber,
                        unfocusedBorderColor = TitaniumBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.replace(",", ".").toDoubleOrNull() ?: 0.0
                    if (amt > 0 && description.isNotBlank()) {
                        onConfirm(type, category, amt, description, memberName.ifBlank { null })
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAmber, contentColor = Color(0xFF1C1300))
            ) {
                Text("Inserir Registro", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}

/**
 * Dialog for Correcting / Editing an Existing Cash Transaction
 */
@Composable
fun EditCashTransactionDialog(
    transaction: CashTransactionEntity,
    onDismiss: () -> Unit,
    onConfirm: (CashTransactionEntity) -> Unit
) {
    var amountText by remember { mutableStateOf(transaction.amount.toString()) }
    var description by remember { mutableStateOf(transaction.description) }
    var category by remember { mutableStateOf(transaction.category) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBgCard,
        title = {
            Text(
                text = "Corrigir Lançamento de Caixa",
                color = ElectricCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Tipo: ${transaction.type} • Data: ${transaction.date}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Valor Corrigido (R$)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = TitaniumBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição Corrigida") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = TitaniumBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoria") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = TitaniumBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.replace(",", ".").toDoubleOrNull() ?: transaction.amount
                    onConfirm(transaction.copy(amount = amt, description = description, category = category))
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = Color(0xFF00363F))
            ) {
                Text("Salvar Correção", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}
