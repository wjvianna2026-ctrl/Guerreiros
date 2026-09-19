package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CashTransactionEntity
import com.example.data.ClubSettingsEntity
import com.example.data.CollectivePurchaseEntity
import com.example.data.LoanRequestEntity
import com.example.data.MemberEntity
import com.example.data.MonthlyDueEntity
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
import java.util.Calendar
import java.util.Locale

@Composable
fun ClubTopBar(
    currentRole: UserRole,
    onRoleSelect: (UserRole) -> Unit,
    allMembers: List<MemberEntity>,
    selectedMemberId: Long?,
    onMemberSelect: (Long) -> Unit,
    onOpenPromptModal: () -> Unit,
    onOpenPwaGuide: () -> Unit,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit
) {
    var showRoleMenu by remember { mutableStateOf(false) }
    var showMemberMenu by remember { mutableStateOf(false) }

    Surface(
        color = DarkBgSurface,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Crest and Club Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, GoldAmber, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_club_crest),
                            contentDescription = "Brasão Guerreiros do Bem MC",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "GUERREIROS DO BEM",
                            color = GoldAmberLight,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "MOTO CLUBE • GESTÃO & CAIXA",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Action buttons (Settings, PWA, Prompt, Role, Logout)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.size(36.dp).testTag("topbar_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurações do Moto Clube",
                            tint = GoldAmberLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onOpenPwaGuide,
                        modifier = Modifier.size(36.dp).testTag("pwa_guide_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Guia PWA",
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onOpenPromptModal,
                        modifier = Modifier.size(36.dp).testTag("prompt_modal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Auditoria de Prompt IA",
                            tint = GoldAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Role Switcher Chip
                    Box {
                        Surface(
                            color = when (currentRole) {
                                UserRole.ADMIN -> GoldAmber.copy(alpha = 0.2f)
                                UserRole.TREASURER -> ElectricCyan.copy(alpha = 0.2f)
                                UserRole.MEMBER -> EmeraldPaid.copy(alpha = 0.2f)
                            },
                            border = BorderStroke(
                                1.dp,
                                when (currentRole) {
                                    UserRole.ADMIN -> GoldAmber
                                    UserRole.TREASURER -> ElectricCyan
                                    UserRole.MEMBER -> EmeraldPaid
                                }
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .clickable { showRoleMenu = true }
                                .testTag("role_switcher_chip")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentRole.badge,
                                    color = when (currentRole) {
                                        UserRole.ADMIN -> GoldAmberLight
                                        UserRole.TREASURER -> ElectricCyan
                                        UserRole.MEMBER -> EmeraldPaid
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "Alternar Perfil",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showRoleMenu,
                            onDismissRequest = { showRoleMenu = false },
                            modifier = Modifier.background(DarkBgCardElevated)
                        ) {
                            UserRole.entries.forEach { role ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = role.label,
                                                color = TextPrimary,
                                                fontWeight = if (role == currentRole) FontWeight.Bold else FontWeight.Normal
                                            )
                                            Text(
                                                text = when (role) {
                                                    UserRole.ADMIN -> "Acesso Total a Caixa, Membros e Ajustes"
                                                    UserRole.TREASURER -> "Gerencia Dívidas, Baixas e Cobranças"
                                                    UserRole.MEMBER -> "Transparência e Extrato Pessoal do Irmão"
                                                },
                                                color = TextSecondary,
                                                fontSize = 10.sp
                                            )
                                        }
                                    },
                                    onClick = {
                                        onRoleSelect(role)
                                        showRoleMenu = false
                                    }
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.size(34.dp).testTag("topbar_logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sair do Sistema",
                            tint = ScarletOverdue.copy(alpha = 0.85f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // If in MEMBER role, show which Brother is currently selected
            if (currentRole == UserRole.MEMBER) {
                Spacer(modifier = Modifier.height(4.dp))
                val currentMember = allMembers.find { it.id == selectedMemberId } ?: allMembers.firstOrNull()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkBgCard, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = EmeraldPaid,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Visualizando como: ${currentMember?.roadNickname ?: "Irmão"} (${currentMember?.role ?: ""})",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box {
                        TextButton(
                            onClick = { showMemberMenu = true },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Text(text = "Trocar", color = ElectricCyan, fontSize = 11.sp)
                        }

                        DropdownMenu(
                            expanded = showMemberMenu,
                            onDismissRequest = { showMemberMenu = false },
                            modifier = Modifier.background(DarkBgCardElevated)
                        ) {
                            allMembers.forEach { member ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${member.roadNickname} - ${member.role}",
                                            color = if (member.id == selectedMemberId) GoldAmberLight else TextPrimary
                                        )
                                    },
                                    onClick = {
                                        onMemberSelect(member.id)
                                        showMemberMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BikerHeroBanner(
    settings: ClubSettingsEntity?,
    onCopyPix: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = BorderStroke(1.dp, TitaniumBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Motorcycle background image
            Image(
                painter = painterResource(id = R.drawable.img_custom_motorcycle),
                contentDescription = "Moto Custom Guerreiros do Bem MC",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
            )

            // Dark gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                DarkBgDeep.copy(alpha = 0.95f),
                                DarkBgDeep.copy(alpha = 0.75f),
                                DarkBgDeep.copy(alpha = 0.4f)
                            )
                        )
                    )
            )

            // Content inside hero
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TwoWheeler,
                        contentDescription = null,
                        tint = GoldAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "FUNDO SOCIAL & CAIXA COLETIVO",
                        color = GoldAmberLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = settings?.motto ?: "Honra, Respeito e Irmandade nas Estradas",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick PIX Row
                Row(
                    modifier = Modifier
                        .background(DarkBgSurface.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                        .border(1.dp, TitaniumBorder, RoundedCornerShape(8.dp))
                        .clickable { onCopyPix() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Chave PIX Oficial:",
                            color = TextSecondary,
                            fontSize = 9.sp
                        )
                        Text(
                            text = settings?.pixKey ?: "financeiro@guerreirosdobem.mc.br",
                            color = ElectricCyan,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copiar PIX",
                        tint = GoldAmber,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = BorderStroke(1.dp, TitaniumBorder),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(Locale.ROOT),
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun FinancialFlowChart(
    transactions: List<CashTransactionEntity> = emptyList(),
    modifier: Modifier = Modifier
) {
    fun extractMonthYear(dateStr: String): String? {
        try {
            if (dateStr.contains("/")) {
                val parts = dateStr.split("/")
                if (parts.size >= 3) {
                    val m = parts[1].padStart(2, '0')
                    val y = parts[2].trim().take(4)
                    return "$m/$y"
                }
            } else if (dateStr.contains("-")) {
                val parts = dateStr.split("-")
                if (parts.size >= 3) {
                    val y = parts[0].trim().take(4)
                    val m = parts[1].padStart(2, '0')
                    return "$m/$y"
                }
            }
        } catch (_: Exception) {}
        return null
    }

    val shortMonthNames = listOf("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez")
    val fullMonthNames = listOf(
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    )

    val monthSlots = remember(transactions) {
        val cal = Calendar.getInstance()
        val latestTxMonthYear = transactions.mapNotNull { extractMonthYear(it.date) }.maxOrNull()
        if (latestTxMonthYear != null) {
            val parts = latestTxMonthYear.split("/")
            if (parts.size == 2) {
                val m = parts[0].toIntOrNull() ?: (cal.get(Calendar.MONTH) + 1)
                val y = parts[1].toIntOrNull() ?: cal.get(Calendar.YEAR)
                val txCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, y)
                    set(Calendar.MONTH, m - 1)
                    set(Calendar.DAY_OF_MONTH, 1)
                }
                if (txCal.timeInMillis > cal.timeInMillis) {
                    cal.timeInMillis = txCal.timeInMillis
                }
            }
        }

        val list = mutableListOf<Triple<String, String, String>>()
        for (i in 5 downTo 0) {
            val c = Calendar.getInstance().apply {
                timeInMillis = cal.timeInMillis
                add(Calendar.MONTH, -i)
            }
            val m = c.get(Calendar.MONTH) + 1
            val y = c.get(Calendar.YEAR)
            val key = "%02d/%04d".format(Locale.US, m, y)
            val short = shortMonthNames[m - 1]
            val full = "${fullMonthNames[m - 1]}/$y"
            list.add(Triple(key, short, full))
        }
        list
    }

    val flowData = remember(transactions, monthSlots) {
        monthSlots.map { slot ->
            val entriesSum = transactions.filter {
                it.type.equals("Entrada", ignoreCase = true) && extractMonthYear(it.date) == slot.first
            }.sumOf { it.amount }

            val exitsSum = transactions.filter {
                it.type.equals("Saída", ignoreCase = true) && extractMonthYear(it.date) == slot.first
            }.sumOf { it.amount }

            val balance = entriesSum - exitsSum
            Triple(slot, Pair(entriesSum, exitsSum), balance)
        }
    }

    val totalEntriesPeriod = flowData.sumOf { it.second.first }
    val totalExitsPeriod = flowData.sumOf { it.second.second }
    val periodNetBalance = totalEntriesPeriod - totalExitsPeriod

    val maxVal = remember(flowData) {
        val maxEntry = flowData.maxOfOrNull { it.second.first } ?: 0.0
        val maxExit = flowData.maxOfOrNull { it.second.second } ?: 0.0
        val highest = maxOf(maxEntry, maxExit)
        if (highest > 0) (highest * 1.25).toFloat() else 1000f
    }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = BorderStroke(1.dp, TitaniumBorder),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FLUXO FINANCEIRO MENSAL",
                    color = GoldAmberLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(EmeraldPaid, CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Entradas (R$ ${"%.0f".format(Locale.US, totalEntriesPeriod)})",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.size(8.dp).background(ScarletOverdue, CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Saídas (R$ ${"%.0f".format(Locale.US, totalExitsPeriod)})",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Subtítulo do período e saldo líquido consolidado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Últimos 6 meses calculados do banco de dados",
                    color = TextMuted,
                    fontSize = 10.sp
                )
                Text(
                    text = "Saldo do Período: R$ ${"%.2f".format(Locale.US, periodNetBalance)}",
                    color = if (periodNetBalance >= 0) EmeraldPaid else ScarletOverdue,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Exibição interativa ao tocar em um mês
            val activeItem = selectedIndex?.let { flowData.getOrNull(it) }
            if (activeItem != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = DarkBgCardElevated,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, GoldAmber.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = activeItem.first.third,
                            color = GoldAmberLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Entrada: R$ ${"%.2f".format(Locale.US, activeItem.second.first)}",
                                color = EmeraldPaid,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Saída: R$ ${"%.2f".format(Locale.US, activeItem.second.second)}",
                                color = ScarletOverdue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Saldo: R$ ${"%.2f".format(Locale.US, activeItem.third)}",
                                color = if (activeItem.third >= 0) ElectricCyan else ScarletOverdue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                val count = flowData.size.coerceAtLeast(1)
                val barWidth = 14.dp.toPx()
                val groupSpacing = size.width / count

                // Linhas de grade de fundo
                val gridLines = 4
                for (i in 0..gridLines) {
                    val y = size.height * (i.toFloat() / gridLines)
                    drawLine(
                        color = TitaniumBorder.copy(alpha = 0.5f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                }

                flowData.forEachIndexed { index, item ->
                    val centerX = groupSpacing * index + groupSpacing / 2f
                    val (entryVal, exitVal) = item.second

                    // Barra de Entrada (Emerald)
                    val entryHeight = if (entryVal > 0) {
                        ((entryVal.toFloat() / maxVal) * (size.height * 0.88f)).coerceAtLeast(4.dp.toPx())
                    } else {
                        2.dp.toPx() // Linha de base sutil
                    }
                    val entryX = centerX - barWidth - 2.dp.toPx()
                    val entryY = size.height - entryHeight
                    drawRoundRect(
                        color = if (entryVal > 0) EmeraldPaid else EmeraldPaid.copy(alpha = 0.25f),
                        topLeft = Offset(entryX, entryY),
                        size = Size(barWidth, entryHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                    )

                    // Barra de Saída (Scarlet)
                    val exitHeight = if (exitVal > 0) {
                        ((exitVal.toFloat() / maxVal) * (size.height * 0.88f)).coerceAtLeast(4.dp.toPx())
                    } else {
                        2.dp.toPx() // Linha de base sutil
                    }
                    val exitX = centerX + 2.dp.toPx()
                    val exitY = size.height - exitHeight
                    drawRoundRect(
                        color = if (exitVal > 0) ScarletOverdue else ScarletOverdue.copy(alpha = 0.25f),
                        topLeft = Offset(exitX, exitY),
                        size = Size(barWidth, exitHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Rótulos dos meses interativos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                flowData.forEachIndexed { index, item ->
                    val isSel = (selectedIndex == index)
                    Surface(
                        color = if (isSel) GoldAmber.copy(alpha = 0.2f) else Color.Transparent,
                        border = if (isSel) BorderStroke(1.dp, GoldAmber) else null,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.clickable {
                            selectedIndex = if (isSel) null else index
                        }
                    ) {
                        Text(
                            text = item.first.second,
                            color = if (isSel) GoldAmberLight else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PortfolioDonutChart(
    transactions: List<CashTransactionEntity> = emptyList(),
    monthlyDues: List<MonthlyDueEntity> = emptyList(),
    loans: List<LoanRequestEntity> = emptyList(),
    collectivePurchases: List<CollectivePurchaseEntity> = emptyList(),
    modifier: Modifier = Modifier
) {
    data class PortfolioItem(
        val title: String,
        val amount: Double,
        val color: Color
    )

    // 1. Mensalidades: total de entradas classificadas como mensalidades
    val duesFromTx = transactions.filter {
        it.type.equals("Entrada", ignoreCase = true) && (it.category.contains("Mensalidade", ignoreCase = true) || it.description.contains("Mensalidade", ignoreCase = true))
    }.sumOf { it.amount }
    val duesAmount = if (duesFromTx > 0) duesFromTx else monthlyDues.filter { it.status == "Paga" }.sumOf { it.amount }

    // 2. Fundo Rotativo & Juros: entradas de parcelas de empréstimo / juros sociais
    val loansFromTx = transactions.filter {
        it.type.equals("Entrada", ignoreCase = true) && (it.category.contains("Empréstimo", ignoreCase = true) ||
                it.category.contains("Juros", ignoreCase = true) ||
                it.category.contains("Fundo Rotativo", ignoreCase = true) ||
                it.description.contains("Empréstimo", ignoreCase = true))
    }.sumOf { it.amount }
    val loansAmount = if (loansFromTx > 0) loansFromTx else loans.sumOf { it.totalAmount }

    // 3. Rateio Compras Coletivas: entradas de rateios de compras conjuntas de equipamentos
    val purchasesFromTx = transactions.filter {
        it.type.equals("Entrada", ignoreCase = true) && (it.category.contains("Rateio", ignoreCase = true) ||
                it.category.contains("Compra", ignoreCase = true) ||
                it.description.contains("Colete", ignoreCase = true) ||
                it.description.contains("Compra", ignoreCase = true))
    }.sumOf { it.amount }
    val purchasesAmount = if (purchasesFromTx > 0) purchasesFromTx else collectivePurchases.sumOf { it.totalCost }

    // 4. Aportes Iniciais / Fundo Social Geral
    val othersAmount = transactions.filter {
        it.type.equals("Entrada", ignoreCase = true) &&
                !it.category.contains("Mensalidade", ignoreCase = true) &&
                !it.description.contains("Mensalidade", ignoreCase = true) &&
                !it.category.contains("Empréstimo", ignoreCase = true) &&
                !it.category.contains("Juros", ignoreCase = true) &&
                !it.category.contains("Fundo Rotativo", ignoreCase = true) &&
                !it.description.contains("Empréstimo", ignoreCase = true) &&
                !it.category.contains("Rateio", ignoreCase = true) &&
                !it.category.contains("Compra", ignoreCase = true) &&
                !it.description.contains("Colete", ignoreCase = true) &&
                !it.description.contains("Compra", ignoreCase = true)
    }.sumOf { it.amount }

    val totalAmount = duesAmount + loansAmount + purchasesAmount + othersAmount

    val items = remember(duesAmount, loansAmount, purchasesAmount, othersAmount, totalAmount) {
        val list = mutableListOf<PortfolioItem>()
        if (duesAmount > 0) {
            list.add(PortfolioItem("Mensalidades", duesAmount, EmeraldPaid))
        }
        if (loansAmount > 0) {
            list.add(PortfolioItem("Fundo Rotativo & Juros", loansAmount, ElectricCyan))
        }
        if (purchasesAmount > 0) {
            list.add(PortfolioItem("Rateio Compras Coletivas", purchasesAmount, GoldAmber))
        }
        if (othersAmount > 0) {
            list.add(PortfolioItem("Aporte & Fundo Inicial", othersAmount, Color(0xFFB388FF)))
        }
        list
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkBgCard),
        border = BorderStroke(1.dp, TitaniumBorder),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DISTRIBUIÇÃO DA CARTEIRA DO CLUBE",
                    color = ElectricCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Total: R$ ${"%.2f".format(Locale.US, totalAmount)}",
                    color = GoldAmberLight,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Donut Canvas com valor centralizado
                Box(
                    modifier = Modifier.size(110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(105.dp)
                            .padding(6.dp)
                    ) {
                        val strokeWidth = 16.dp.toPx()
                        if (totalAmount <= 0 || items.isEmpty()) {
                            drawArc(
                                color = TitaniumBorder,
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth),
                                size = Size(size.width, size.height)
                            )
                        } else {
                            var startAngle = -90f
                            items.forEach { item ->
                                val sweepAngle = (360f * (item.amount / totalAmount)).toFloat()
                                val gap = if (items.size > 1) 3f else 0f
                                drawArc(
                                    color = item.color,
                                    startAngle = startAngle,
                                    sweepAngle = (sweepAngle - gap).coerceAtLeast(2f),
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                    size = Size(size.width, size.height)
                                )
                                startAngle += sweepAngle
                            }
                        }
                    }

                    // Texto central do Donut
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TOTAL",
                            color = TextMuted,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (totalAmount >= 1000) "R$ ${"%.1fk".format(Locale.US, totalAmount / 1000)}" else "R$ ${"%.0f".format(Locale.US, totalAmount)}",
                            color = GoldAmberLight,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Legenda com valores reais em R$ e porcentagens calculadas
                Column(modifier = Modifier.weight(1f).padding(start = 14.dp)) {
                    if (items.isEmpty()) {
                        Text(
                            text = "Nenhuma movimentação registrada na carteira.",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    } else {
                        items.forEach { item ->
                            val pct = if (totalAmount > 0) ((item.amount / totalAmount) * 100.0) else 0.0
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 3.dp)
                            ) {
                                Box(modifier = Modifier.size(9.dp).background(item.color, CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = item.title,
                                        color = TextPrimary,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "R$ ${"%.2f".format(Locale.US, item.amount)} (${"%.1f".format(Locale.US, pct)}%)",
                                        color = TextSecondary,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Normal
                                    )
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
fun WhatsAppPaymentDialog(
    recipientName: String,
    recipientPhone: String,
    description: String,
    amount: Double,
    dueDate: String,
    pixKey: String,
    pixHolder: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val message = remember(recipientName, description, amount, dueDate, pixKey) {
        WhatsAppHelper.generateBrotherhoodMessage(
            nickname = recipientName,
            description = description,
            amount = amount,
            dueDate = dueDate,
            pixKey = pixKey,
            pixHolder = pixHolder
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBgCardElevated,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    tint = EmeraldPaid,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cobrança de Irmandade (WhatsApp)",
                    color = GoldAmberLight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Destinatário: $recipientName (${recipientPhone.ifEmpty { "Sem telefone" }})",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = DarkBgDeep,
                    border = BorderStroke(1.dp, TitaniumBorder),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = message,
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
                    WhatsAppHelper.openWhatsApp(context, recipientPhone, message)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPaid),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Abrir WhatsApp", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = {
                        WhatsAppHelper.copyToClipboard(context, "Mensagem Moto Clube", message)
                    }
                ) {
                    Text("Copiar Texto", color = ElectricCyan)
                }
                TextButton(onClick = onDismiss) {
                    Text("Fechar", color = TextSecondary)
                }
            }
        }
    )
}

@Composable
fun PromptAuditDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val fullPrompt = remember {
        """
            Aplicação para Gestão Financeira e Administrativa do "Guerreiros do Bem Moto Clube".
            Focado em:
            1. Controle de Caixa e Transparência
            2. Fundo Rotativo de Apoio com Juros Sociais (0%, 1%, 2%)
            3. Cobrança e Acompanhamento de Mensalidades
            4. Empréstimos e Parcelamento de Compras Coletivas (Coletes, Pneus, Peças)
            5. Cobrança Inteligente via WhatsApp com Tom de Irmandade
            6. Relatórios de Prestação de Contas, Auditoria e Backup
            7. RBAC: Administrador/Diretor, Tesoureiro/Gestor e Membro/Irmão
            8. Dark Mode Motociclista Premium (#0A0D12, Dourado, Ciano, Esmeralda, Escarlate)
        """.trimIndent()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBgCardElevated,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = GoldAmber,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Prompt do Sistema & Auditoria IA",
                    color = GoldAmberLight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "O prompt abaixo define a arquitetura, regras de negócio e identidade visual do Guerreiros do Bem MC:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = DarkBgDeep,
                    border = BorderStroke(1.dp, TitaniumBorder),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = fullPrompt,
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
                    WhatsAppHelper.copyToClipboard(context, "Prompt de IA", fullPrompt)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAmber),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Copiar Prompt", color = Color(0xFF1C1300), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar", color = TextSecondary)
            }
        }
    )
}

@Composable
fun PwaGuideDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBgCardElevated,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Instalação no Celular & Computador",
                    color = ElectricCyan,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Como instalar o app dos Guerreiros do Bem MC:",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "📱 Android (Chrome / Navegador):",
                    color = GoldAmberLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = "1. Toque nos 3 pontos do navegador.\n2. Selecione 'Instalar aplicativo' ou 'Adicionar à tela inicial'.\n3. O ícone oficial com brasão será adicionado.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "🍎 iPhone / iPad (Safari):",
                    color = GoldAmberLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = "1. Abra no navegador Safari.\n2. Toque no botão de Compartilhar (ícone com seta para cima).\n3. Escolha 'Adicionar à Tela de Início'.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "💻 Computador (Chrome / Edge):",
                    color = GoldAmberLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = "1. Clique no ícone de instalação na barra de endereços (ao lado da estrela de favoritos).\n2. Confirme em 'Instalar'.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Entendido", color = Color(0xFF00363F), fontWeight = FontWeight.Bold)
            }
        }
    )
}

/**
 * Universal Material Design 3 CRUD Action Bar
 * Guarantees "Inserir", "Corrigir", "Excluir" buttons across all modules of the app.
 */
@Composable
fun UnifiedCrudActionBar(
    onInsert: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    canInsert: Boolean = true,
    canEdit: Boolean = true,
    canDelete: Boolean = true,
    insertLabel: String = "Inserir",
    editLabel: String = "Corrigir",
    deleteLabel: String = "Excluir",
    insertIcon: ImageVector = Icons.Default.Add,
    editIcon: ImageVector = Icons.Default.Edit,
    deleteIcon: ImageVector = Icons.Default.Delete,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkBgCardElevated),
        border = BorderStroke(1.dp, TitaniumBorder),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("unified_crud_action_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // INSERIR
            Button(
                onClick = onInsert,
                enabled = canInsert,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldAmber,
                    contentColor = Color(0xFF1C1300),
                    disabledContainerColor = DarkBgCard,
                    disabledContentColor = TextMuted
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .testTag("crud_btn_insert")
            ) {
                Icon(
                    imageVector = insertIcon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = insertLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // CORRIGIR
            OutlinedButton(
                onClick = onEdit,
                enabled = canEdit,
                border = BorderStroke(1.dp, if (canEdit) ElectricCyan else TitaniumBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ElectricCyan,
                    disabledContentColor = TextMuted
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .testTag("crud_btn_edit")
            ) {
                Icon(
                    imageVector = editIcon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = editLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // EXCLUIR
            OutlinedButton(
                onClick = onDelete,
                enabled = canDelete,
                border = BorderStroke(1.dp, if (canDelete) ScarletOverdue else TitaniumBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ScarletOverdue,
                    disabledContentColor = TextMuted
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .testTag("crud_btn_delete")
            ) {
                Icon(
                    imageVector = deleteIcon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = deleteLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Universal Confirmation Modal for Deletion
 */
@Composable
fun ConfirmDeleteDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBgCardElevated,
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = ScarletOverdue,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = title,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Text(
                text = message,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ScarletOverdue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_delete_btn")
            ) {
                Text("Confirmar Exclusão", fontWeight = FontWeight.Bold, color = Color.White)
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
 * Complete Settings Dialog with Permission Management (Gestor gives permissions to users),
 * Club Parameters, and Firebase Firestore Sync.
 */
@Composable
fun ClubSettingsDialog(
    settings: ClubSettingsEntity?,
    onSaveSettings: (ClubSettingsEntity) -> Unit,
    members: List<MemberEntity>,
    userPermissions: Map<Long, UserPermissionConfig>,
    onUpdateUserPermission: (Long, UserRole, Boolean, Boolean, Boolean, String) -> Unit,
    syncStatus: String,
    isSyncing: Boolean,
    onSyncFirestore: () -> Unit,
    currentUser: AuthUser?,
    onLogout: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    // Club Form State
    var clubName by remember(settings) { mutableStateOf(settings?.clubName ?: "Guerreiros do Bem Moto Clube") }
    var motto by remember(settings) { mutableStateOf(settings?.motto ?: "Honra, Respeito e Irmandade nas Estradas") }
    var pixKey by remember(settings) { mutableStateOf(settings?.pixKey ?: "financeiro@guerreirosdobem.mc.br") }
    var pixHolder by remember(settings) { mutableStateOf(settings?.pixHolderName ?: "Guerreiros do Bem MC - Caixa Social") }
    var defaultDue by remember(settings) { mutableStateOf(settings?.defaultMonthlyDue?.toString() ?: "50.0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBgCard,
        modifier = Modifier.widthIn(max = 640.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = GoldAmber,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Configurações Gerais",
                        color = GoldAmberLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar", tint = TextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(440.dp)
            ) {
                // Tab Header
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkBgCardElevated,
                    contentColor = GoldAmber,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = GoldAmber
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "🛡️ Permissões",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) GoldAmberLight else TextSecondary
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "⚙️ Parâmetros MC",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) GoldAmberLight else TextSecondary
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                "☁️ Firestore",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 2) GoldAmberLight else TextSecondary
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Contents
                Box(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    when (selectedTab) {
                        // TAB 0: GESTÃO DE PERMISSÕES AOS OUTROS USUÁRIOS
                        0 -> {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Gestão de Acessos & Permissões dos Irmãos",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Como Gestor/Administrador, defina o nível de acesso e poderes (Inserir, Corrigir e Excluir) para cada membro:",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )

                                members.forEach { member ->
                                    val perm = userPermissions[member.id]
                                    var currentRole by remember(perm) {
                                        mutableStateOf(
                                            perm?.role ?: when {
                                                member.role.contains("Presidente") || member.role.contains("Diretor") -> UserRole.ADMIN
                                                member.role.contains("Tesoureiro") -> UserRole.TREASURER
                                                else -> UserRole.MEMBER
                                            }
                                        )
                                    }
                                    var canInsert by remember(perm) { mutableStateOf(perm?.canInsert ?: (currentRole != UserRole.MEMBER)) }
                                    var canEdit by remember(perm) { mutableStateOf(perm?.canEdit ?: (currentRole != UserRole.MEMBER)) }
                                    var canDelete by remember(perm) { mutableStateOf(perm?.canDelete ?: (currentRole == UserRole.ADMIN)) }
                                    var accessStatus by remember(perm) { mutableStateOf(perm?.accessStatus ?: "Autorizado") }

                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = DarkBgCardElevated),
                                        border = BorderStroke(1.dp, TitaniumBorder),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(
                                                        text = "${member.fullName} (${member.roadNickname})",
                                                        color = TextPrimary,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp
                                                    )
                                                    Text(
                                                        text = "Cargo: ${member.role} • ${member.email}",
                                                        color = TextSecondary,
                                                        fontSize = 10.sp
                                                    )
                                                }

                                                // Status badge
                                                Surface(
                                                    color = if (accessStatus == "Autorizado") EmeraldPaid.copy(alpha = 0.2f) else ScarletOverdue.copy(alpha = 0.2f),
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text(
                                                        text = accessStatus,
                                                        color = if (accessStatus == "Autorizado") EmeraldPaid else ScarletOverdue,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            HorizontalDivider(color = TitaniumBorder.copy(alpha = 0.5f))

                                            // Role Selector Row
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                UserRole.entries.forEach { roleOption ->
                                                    val isSelected = currentRole == roleOption
                                                    OutlinedButton(
                                                        onClick = {
                                                            currentRole = roleOption
                                                            if (roleOption == UserRole.ADMIN) {
                                                                canInsert = true; canEdit = true; canDelete = true
                                                            } else if (roleOption == UserRole.TREASURER) {
                                                                canInsert = true; canEdit = true; canDelete = false
                                                            } else {
                                                                canInsert = false; canEdit = false; canDelete = false
                                                            }
                                                            onUpdateUserPermission(member.id, currentRole, canInsert, canEdit, canDelete, accessStatus)
                                                        },
                                                        border = BorderStroke(1.dp, if (isSelected) GoldAmber else TitaniumBorder),
                                                        colors = ButtonDefaults.outlinedButtonColors(
                                                            containerColor = if (isSelected) GoldAmber.copy(alpha = 0.15f) else Color.Transparent
                                                        ),
                                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                        modifier = Modifier.weight(1f).height(32.dp)
                                                    ) {
                                                        Text(
                                                            text = roleOption.badge,
                                                            fontSize = 10.sp,
                                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                            color = if (isSelected) GoldAmberLight else TextSecondary
                                                        )
                                                    }
                                                }
                                            }

                                            // Permission checkboxes: Inserir, Corrigir, Excluir
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Checkbox(
                                                        checked = canInsert,
                                                        onCheckedChange = {
                                                            canInsert = it
                                                            onUpdateUserPermission(member.id, currentRole, canInsert, canEdit, canDelete, accessStatus)
                                                        },
                                                        colors = CheckboxDefaults.colors(checkedColor = GoldAmber)
                                                    )
                                                    Text("Inserir", fontSize = 11.sp, color = TextPrimary)
                                                }

                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Checkbox(
                                                        checked = canEdit,
                                                        onCheckedChange = {
                                                            canEdit = it
                                                            onUpdateUserPermission(member.id, currentRole, canInsert, canEdit, canDelete, accessStatus)
                                                        },
                                                        colors = CheckboxDefaults.colors(checkedColor = ElectricCyan)
                                                    )
                                                    Text("Corrigir", fontSize = 11.sp, color = TextPrimary)
                                                }

                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Checkbox(
                                                        checked = canDelete,
                                                        onCheckedChange = {
                                                            canDelete = it
                                                            onUpdateUserPermission(member.id, currentRole, canInsert, canEdit, canDelete, accessStatus)
                                                        },
                                                        colors = CheckboxDefaults.colors(checkedColor = ScarletOverdue)
                                                    )
                                                    Text("Excluir", fontSize = 11.sp, color = TextPrimary)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // TAB 1: PARÂMETROS DO MOTO CLUBE
                        1 -> {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Dados Oficiais do Moto Clube",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )

                                OutlinedTextField(
                                    value = clubName,
                                    onValueChange = { clubName = it },
                                    label = { Text("Nome do Moto Clube") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GoldAmber,
                                        unfocusedBorderColor = TitaniumBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )

                                OutlinedTextField(
                                    value = motto,
                                    onValueChange = { motto = it },
                                    label = { Text("Lema de Estrada") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GoldAmber,
                                        unfocusedBorderColor = TitaniumBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )

                                OutlinedTextField(
                                    value = pixKey,
                                    onValueChange = { pixKey = it },
                                    label = { Text("Chave PIX Oficial") },
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
                                    value = pixHolder,
                                    onValueChange = { pixHolder = it },
                                    label = { Text("Titular da Conta / PIX") },
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
                                    value = defaultDue,
                                    onValueChange = { defaultDue = it },
                                    label = { Text("Valor Padrão da Mensalidade (R$)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = EmeraldPaid,
                                        unfocusedBorderColor = TitaniumBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )

                                Button(
                                    onClick = {
                                        val dueVal = defaultDue.toDoubleOrNull() ?: 50.0
                                        settings?.let {
                                            onSaveSettings(
                                                it.copy(
                                                    clubName = clubName,
                                                    motto = motto,
                                                    pixKey = pixKey,
                                                    pixHolderName = pixHolder,
                                                    defaultMonthlyDue = dueVal
                                                )
                                            )
                                        }
                                        onDismiss()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldAmber, contentColor = Color(0xFF1C1300)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().height(44.dp)
                                ) {
                                    Text("Salvar Alterações", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // TAB 2: FIRESTORE SYNC & SESSÃO
                        2 -> {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text(
                                    text = "Google Cloud Firestore & Sincronização",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = DarkBgCardElevated),
                                    border = BorderStroke(1.dp, TitaniumBorder),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CloudSync,
                                                contentDescription = null,
                                                tint = ElectricCyan,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Column {
                                                Text(
                                                    text = "Status da Sincronização",
                                                    color = TextPrimary,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                )
                                                Text(
                                                    text = syncStatus,
                                                    color = if (syncStatus.contains("Nuvem") || syncStatus.contains("Sincronizado")) EmeraldPaid else GoldAmber,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }

                                        Text(
                                            text = "Coleções sincronizadas: members, cash_transactions, loan_requests, loan_installments, monthly_dues.",
                                            color = TextSecondary,
                                            fontSize = 10.sp
                                        )

                                        Button(
                                            onClick = onSyncFirestore,
                                            enabled = !isSyncing,
                                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = Color(0xFF00363F)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth().height(40.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(if (isSyncing) "Sincronizando..." else "Sincronizar Firestore Agora", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Session & Logout
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = DarkBgCardElevated),
                                    border = BorderStroke(1.dp, TitaniumBorder),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = "Sessão Ativa:",
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = currentUser?.name ?: "Usuário Autenticado",
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "E-mail: ${currentUser?.email} • Perfil: ${currentUser?.role?.label}",
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        OutlinedButton(
                                            onClick = {
                                                onLogout()
                                                onDismiss()
                                            },
                                            border = BorderStroke(1.dp, ScarletOverdue),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ScarletOverdue),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth().height(40.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Sair da Conta / Desconectar", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = DarkBgCardElevated),
                border = BorderStroke(1.dp, TitaniumBorder),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Fechar", color = TextPrimary)
            }
        }
    )
}
