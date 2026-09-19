package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ClubSettingsDialog
import com.example.ui.ClubTopBar
import com.example.ui.ClubViewModel
import com.example.ui.NavigationTab
import com.example.ui.PromptAuditDialog
import com.example.ui.PwaGuideDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DuesScreen
import com.example.ui.screens.LoansScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MembersScreen
import com.example.ui.screens.PurchasesScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.theme.DarkBgCardElevated
import com.example.ui.theme.DarkBgDeep
import com.example.ui.theme.DarkBgSurface
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GoldAmberLight
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {
    private val viewModel: ClubViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                GuerreirosApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun GuerreirosApp(viewModel: ClubViewModel) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    // Gate app behind Login Screen
    if (!isAuthenticated) {
        LoginScreen(viewModel = viewModel)
        return
    }

    val currentTab by viewModel.currentTab.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val allMembers by viewModel.members.collectAsState()
    val selectedMemberId by viewModel.selectedMemberId.collectAsState()
    val settings by viewModel.clubSettings.collectAsState()
    val userPermissions by viewModel.userPermissions.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showPromptModal by remember { mutableStateOf(false) }
    var showPwaGuide by remember { mutableStateOf(false) }
    var showSettingsModal by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBgDeep,
        topBar = {
            ClubTopBar(
                currentRole = currentRole,
                onRoleSelect = { viewModel.setRole(it) },
                allMembers = allMembers,
                selectedMemberId = selectedMemberId,
                onMemberSelect = { viewModel.setSelectedMember(it) },
                onOpenPromptModal = { showPromptModal = true },
                onOpenPwaGuide = { showPwaGuide = true },
                onOpenSettings = { showSettingsModal = true },
                onLogout = { viewModel.logout() }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkBgSurface,
                tonalElevation = 8.dp
            ) {
                NavigationTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    val icon = when (tab) {
                        NavigationTab.DASHBOARD -> Icons.Default.Dashboard
                        NavigationTab.MEMBERS -> Icons.Default.People
                        NavigationTab.DUES -> Icons.Default.Payment
                        NavigationTab.LOANS -> Icons.Default.Handshake
                        NavigationTab.PURCHASES -> Icons.Default.ShoppingBag
                        NavigationTab.REPORTS -> Icons.Default.AccountBalance
                    }

                    val labelText = when (tab) {
                        NavigationTab.DASHBOARD -> "Caixa"
                        NavigationTab.MEMBERS -> "Irmãos"
                        NavigationTab.DUES -> "Mensal."
                        NavigationTab.LOANS -> "Fundo"
                        NavigationTab.PURCHASES -> "Compras"
                        NavigationTab.REPORTS -> "Auditoria"
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(tab) },
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}"),
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = labelText,
                                fontSize = 10.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1C1300),
                            selectedTextColor = GoldAmberLight,
                            indicatorColor = GoldAmber,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextMuted
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavigationTab.DASHBOARD -> DashboardScreen(viewModel = viewModel, onNavigateToTab = { viewModel.setTab(it) })
                NavigationTab.MEMBERS -> MembersScreen(viewModel = viewModel)
                NavigationTab.DUES -> DuesScreen(viewModel = viewModel)
                NavigationTab.LOANS -> LoansScreen(viewModel = viewModel)
                NavigationTab.PURCHASES -> PurchasesScreen(viewModel = viewModel)
                NavigationTab.REPORTS -> ReportsScreen(viewModel = viewModel)
            }
        }
    }

    // Modal: Configurações & Gestão de Permissões
    if (showSettingsModal) {
        ClubSettingsDialog(
            settings = settings,
            onSaveSettings = { viewModel.updateSettings(it) },
            members = allMembers,
            userPermissions = userPermissions,
            onUpdateUserPermission = { memberId, targetRole, canInsert, canEdit, canDelete, status ->
                viewModel.updateUserPermission(memberId, targetRole, canInsert, canEdit, canDelete, status)
            },
            syncStatus = syncStatus,
            isSyncing = isSyncing,
            onSyncFirestore = { viewModel.syncWithFirestore() },
            currentUser = currentUser,
            onLogout = {
                showSettingsModal = false
                viewModel.logout()
            },
            onDismiss = { showSettingsModal = false }
        )
    }

    // Modal: Auditoria de Prompt IA
    if (showPromptModal) {
        PromptAuditDialog(onDismiss = { showPromptModal = false })
    }

    // Modal: Guia PWA & Instalação
    if (showPwaGuide) {
        PwaGuideDialog(onDismiss = { showPwaGuide = false })
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
