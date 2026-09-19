package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ClubPatente
import com.example.data.ClubPatentesRegistry
import com.example.ui.theme.DarkBgCard
import com.example.ui.theme.DarkBgCardElevated
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldPaid
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GoldAmberLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TitaniumBorder

/**
 * Campo interativo para seleção de Cargo / Patente no Moto Clube
 * Possui seta clicável (ArrowDropDown) e área total sensível ao toque,
 * abrindo o seletor com as 3 famílias e 15 patentes cadastradas.
 */
@Composable
fun PatenteSelectionField(
    currentRole: String,
    onRoleSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val registeredPatente = ClubPatentesRegistry.findPatente(currentRole)

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = currentRole,
            onValueChange = {},
            readOnly = true,
            label = { Text("Cargo / Patente no Clube *", color = GoldAmber) },
            supportingText = {
                if (registeredPatente != null) {
                    Text(
                        text = "${registeredPatente.familyName} • ${registeredPatente.shortBadge}",
                        color = GoldAmberLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = "Toque na seta para abrir as 3 famílias de patentes",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            },
            leadingIcon = {
                val icon = when (registeredPatente?.familyName) {
                    "Diretoria Executiva" -> Icons.Default.MilitaryTech
                    "Comando de Estrada & Disciplina" -> Icons.Default.TwoWheeler
                    else -> Icons.Default.Security
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GoldAmber,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Abrir lista de patentes",
                        tint = GoldAmber,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkBgCard,
                unfocusedContainerColor = DarkBgCard,
                focusedBorderColor = GoldAmber,
                unfocusedBorderColor = GoldAmberDarkAlpha,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedLabelColor = GoldAmberLight,
                unfocusedLabelColor = GoldAmber
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Camada transparente que garante que clicar em qualquer ponto do campo abra o seletor
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showDialog = true }
        )
    }

    if (showDialog) {
        PatenteHierarchySelectorDialog(
            currentRole = currentRole,
            onSelectPatente = { selected ->
                onRoleSelected(selected.title)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

private val GoldAmberDarkAlpha = GoldAmber.copy(alpha = 0.6f)

/**
 * Diálogo modal para selecionar uma das 15 patentes distribuídas em 3 famílias e 5 níveis hierárquicos.
 */
@Composable
fun PatenteHierarchySelectorDialog(
    currentRole: String,
    onSelectPatente: (ClubPatente) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedFamilyFilter by remember { mutableStateOf("TODAS") }

    val families = ClubPatentesRegistry.families
    val displayedFamilies = if (selectedFamilyFilter == "TODAS") {
        families
    } else {
        families.filter { it.id == selectedFamilyFilter }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBgCardElevated,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(GoldAmber.copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, GoldAmber, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MilitaryTech,
                            contentDescription = null,
                            tint = GoldAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Patentes do Moto Clube",
                            color = GoldAmberLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "3 Famílias • 15 Níveis Hierárquicos",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Filtro rápido por Família
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    item {
                        FamilyFilterChip(
                            label = "Todas (15)",
                            isSelected = selectedFamilyFilter == "TODAS",
                            onClick = { selectedFamilyFilter = "TODAS" }
                        )
                    }
                    items(families) { fam ->
                        val iconLabel = when (fam.id) {
                            "diretoria" -> "🏛️ ${fam.shortName}"
                            "estrada_disciplina" -> "🏍️ ${fam.shortName}"
                            else -> "⚡ ${fam.shortName}"
                        }
                        FamilyFilterChip(
                            label = iconLabel,
                            isSelected = selectedFamilyFilter == fam.id,
                            onClick = { selectedFamilyFilter = fam.id }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Lista de famílias e seus respectivos 5 níveis
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    displayedFamilies.forEach { family ->
                        item {
                            FamilyHeaderSection(family = family)
                        }

                        items(family.patentes) { patente ->
                            val isSelected = patente.title.equals(currentRole.trim(), ignoreCase = true)
                            PatenteItemCard(
                                patente = patente,
                                isSelected = isSelected,
                                onClick = { onSelectPatente(patente) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun FamilyFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) GoldAmber else DarkBgCard,
        border = BorderStroke(1.dp, if (isSelected) GoldAmberLight else TitaniumBorder),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (isSelected) Color(0xFF1C1300) else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun FamilyHeaderSection(family: com.example.data.PatenteFamily) {
    val (famColor, famIcon) = when (family.id) {
        "diretoria" -> Pair(GoldAmber, Icons.Default.MilitaryTech)
        "estrada_disciplina" -> Pair(ElectricCyan, Icons.Default.TwoWheeler)
        else -> Pair(EmeraldPaid, Icons.Default.Groups)
    }

    Surface(
        color = famColor.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, famColor.copy(alpha = 0.35f)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(famIcon, contentDescription = null, tint = famColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = family.name.uppercase(),
                    color = famColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = family.description,
                    color = TextSecondary,
                    fontSize = 9.5.sp
                )
            }
            Surface(
                color = famColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "5 Níveis",
                    color = famColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun PatenteItemCard(
    patente: ClubPatente,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val levelColor = when (patente.level) {
        1 -> GoldAmberLight
        2 -> GoldAmber
        3 -> ElectricCyan
        4 -> EmeraldPaid
        else -> TextSecondary
    }

    Surface(
        color = if (isSelected) GoldAmber.copy(alpha = 0.15f) else DarkBgCard,
        border = BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) GoldAmber else TitaniumBorder
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Level Badge Circle
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(levelColor.copy(alpha = 0.15f), CircleShape)
                    .border(1.dp, levelColor.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${patente.level}º",
                    color = levelColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = patente.title,
                        color = if (isSelected) GoldAmberLight else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = levelColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = patente.shortBadge,
                            color = levelColor,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = patente.description,
                    color = TextSecondary,
                    fontSize = 10.5.sp,
                    lineHeight = 13.sp
                )
            }

            if (isSelected) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selecionado",
                    tint = EmeraldPaid,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
