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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBgCard
import com.example.ui.theme.DarkBgCardElevated
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GoldAmberLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TitaniumBorder
import java.util.Calendar
import java.util.Locale

/**
 * Campo interativo para Mês/Ano de Referência (ex: 09/2026).
 * Ao clicar no campo ou no ícone de calendário, abre o seletor dedicado de Mês/Ano.
 * Preenche automaticamente no formato MM/yyyy.
 */
@Composable
fun MonthYearPickerField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Mês/Ano de Referência *"
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = GoldAmber) },
            supportingText = {
                Text(
                    text = "Clique para abrir o calendário de Mês/Ano (formato: MM/aaaa)",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = GoldAmber,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Selecionar Mês/Ano",
                        tint = GoldAmber,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkBgCard,
                unfocusedContainerColor = DarkBgCard,
                focusedBorderColor = GoldAmber,
                unfocusedBorderColor = GoldAmber.copy(alpha = 0.6f),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedLabelColor = GoldAmberLight,
                unfocusedLabelColor = GoldAmber
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Camada transparente clicável para acionamento em 100% da área do campo
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showDialog = true }
        )
    }

    if (showDialog) {
        MonthYearPickerDialog(
            currentMonthYear = value,
            onMonthYearSelected = { selected ->
                onValueChange(selected)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

/**
 * Diálogo modal para selecionar exclusivamente o Mês e o Ano de referência.
 */
@Composable
fun MonthYearPickerDialog(
    currentMonthYear: String,
    onMonthYearSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val currentCal = Calendar.getInstance()
    val parsedMonthYear = remember(currentMonthYear) {
        try {
            val parts = currentMonthYear.split("/")
            if (parts.size == 2) {
                val m = parts[0].toIntOrNull() ?: (currentCal.get(Calendar.MONTH) + 1)
                val y = parts[1].toIntOrNull() ?: currentCal.get(Calendar.YEAR)
                Pair(m, y)
            } else {
                Pair(currentCal.get(Calendar.MONTH) + 1, currentCal.get(Calendar.YEAR))
            }
        } catch (_: Exception) {
            Pair(currentCal.get(Calendar.MONTH) + 1, currentCal.get(Calendar.YEAR))
        }
    }

    var selectedYear by remember { mutableIntStateOf(parsedMonthYear.second) }
    var selectedMonth by remember { mutableIntStateOf(parsedMonthYear.first) }

    val months = listOf(
        Pair(1, "Jan - Janeiro"),
        Pair(2, "Fev - Fevereiro"),
        Pair(3, "Mar - Março"),
        Pair(4, "Abr - Abril"),
        Pair(5, "Mai - Maio"),
        Pair(6, "Jun - Junho"),
        Pair(7, "Jul - Julho"),
        Pair(8, "Ago - Agosto"),
        Pair(9, "Set - Setembro"),
        Pair(10, "Out - Outubro"),
        Pair(11, "Nov - Novembro"),
        Pair(12, "Dez - Dezembro")
    )

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
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = GoldAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Mês de Referência",
                            color = GoldAmberLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        val formattedPreview = "%02d/%04d".format(Locale.US, selectedMonth, selectedYear)
                        Text(
                            text = "Selecionado: $formattedPreview",
                            color = TextSecondary,
                            fontSize = 11.5.sp
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
                // Navegador de Ano
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkBgCard, RoundedCornerShape(8.dp))
                        .border(1.dp, TitaniumBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedYear-- }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Ano Anterior",
                            tint = GoldAmber
                        )
                    }

                    Text(
                        text = "$selectedYear",
                        color = GoldAmberLight,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )

                    IconButton(onClick = { selectedYear++ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Próximo Ano",
                            tint = GoldAmber
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Atalhos rápidos: Mês Atual e Próximo Mês
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val actualM = currentCal.get(Calendar.MONTH) + 1
                    val actualY = currentCal.get(Calendar.YEAR)

                    Surface(
                        color = DarkBgCard,
                        border = BorderStroke(1.dp, TitaniumBorder),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedMonth = actualM
                                selectedYear = actualY
                            }
                    ) {
                        Text(
                            text = "Mês Atual (%02d/%04d)".format(Locale.US, actualM, actualY),
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }

                    val nextCal = Calendar.getInstance().apply { add(Calendar.MONTH, 1) }
                    val nextM = nextCal.get(Calendar.MONTH) + 1
                    val nextY = nextCal.get(Calendar.YEAR)

                    Surface(
                        color = DarkBgCard,
                        border = BorderStroke(1.dp, TitaniumBorder),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedMonth = nextM
                                selectedYear = nextY
                            }
                    ) {
                        Text(
                            text = "Próx. Mês (%02d/%04d)".format(Locale.US, nextM, nextY),
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Grade dos 12 Meses
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp)
                ) {
                    items(months) { (mNum, mLabel) ->
                        val isSelected = (mNum == selectedMonth)
                        Surface(
                            color = if (isSelected) GoldAmber else DarkBgCard,
                            border = BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) GoldAmberLight else TitaniumBorder
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMonth = mNum }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "%02d".format(Locale.US, mNum),
                                    color = if (isSelected) Color(0xFF1C1300) else GoldAmber,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = mLabel.substringBefore(" -"),
                                    color = if (isSelected) Color(0xFF1C1300) else TextPrimary,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val formatted = "%02d/%04d".format(Locale.US, selectedMonth, selectedYear)
                    onMonthYearSelected(formatted)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAmber),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Confirmar (%02d/%04d)".format(Locale.US, selectedMonth, selectedYear), color = Color(0xFF1C1300), fontWeight = FontWeight.Bold)
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
 * Campo interativo para Data de Vencimento (ex: 10/09/2026).
 * Ao clicar no campo ou no ícone de calendário, abre o calendário completo.
 * Preenche automaticamente no formato dd/MM/yyyy.
 */
@Composable
fun DueDatePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Data de Vencimento *"
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = GoldAmber) },
            supportingText = {
                Text(
                    text = "Clique para abrir o calendário (formato: dd/MM/aaaa)",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = GoldAmber,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Selecionar Data",
                        tint = GoldAmber,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkBgCard,
                unfocusedContainerColor = DarkBgCard,
                focusedBorderColor = GoldAmber,
                unfocusedBorderColor = GoldAmber.copy(alpha = 0.6f),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedLabelColor = GoldAmberLight,
                unfocusedLabelColor = GoldAmber
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Camada transparente clicável para acionamento em 100% da área do campo
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showDialog = true }
        )
    }

    if (showDialog) {
        DueDatePickerDialog(
            currentDueDate = value,
            onDateSelected = { selected ->
                onValueChange(selected)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

/**
 * Diálogo com Calendário Interativo Completo para seleção da Data de Vencimento (dd/MM/yyyy).
 */
@Composable
fun DueDatePickerDialog(
    currentDueDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val todayCal = Calendar.getInstance()
    val parsedDate = remember(currentDueDate) {
        try {
            val parts = currentDueDate.split("/")
            if (parts.size == 3) {
                val d = parts[0].toIntOrNull() ?: 10
                val m = parts[1].toIntOrNull() ?: (todayCal.get(Calendar.MONTH) + 1)
                val y = parts[2].substringBefore(" ").toIntOrNull() ?: todayCal.get(Calendar.YEAR)
                Triple(d, m, y)
            } else {
                Triple(10, todayCal.get(Calendar.MONTH) + 1, todayCal.get(Calendar.YEAR))
            }
        } catch (_: Exception) {
            Triple(10, todayCal.get(Calendar.MONTH) + 1, todayCal.get(Calendar.YEAR))
        }
    }

    var selectedDay by remember { mutableIntStateOf(parsedDate.first) }
    var displayMonth by remember { mutableIntStateOf(parsedDate.second) }
    var displayYear by remember { mutableIntStateOf(parsedDate.third) }

    // Calcula propriedades do mês exibido
    val monthCal = Calendar.getInstance().apply {
        set(Calendar.YEAR, displayYear)
        set(Calendar.MONTH, displayMonth - 1)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = monthCal.get(Calendar.DAY_OF_WEEK) // 1 = Domingo, 2 = Segunda ...
    val offsetDays = firstDayOfWeek - 1 // Quantos espaços vazios antes do dia 1

    val monthNames = listOf(
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    )

    // Ajusta o dia selecionado se exceder os dias do mês atual
    if (selectedDay > daysInMonth) {
        selectedDay = daysInMonth
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
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = GoldAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Data de Vencimento",
                            color = GoldAmberLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        val formattedDate = "%02d/%02d/%04d".format(Locale.US, selectedDay, displayMonth, displayYear)
                        Text(
                            text = "Selecionada: $formattedDate",
                            color = TextSecondary,
                            fontSize = 11.5.sp
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
                // Navegador do Mês/Ano
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkBgCard, RoundedCornerShape(8.dp))
                        .border(1.dp, TitaniumBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (displayMonth == 1) {
                            displayMonth = 12
                            displayYear--
                        } else {
                            displayMonth--
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Mês Anterior",
                            tint = GoldAmber
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${monthNames[displayMonth - 1]} $displayYear",
                            color = GoldAmberLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    IconButton(onClick = {
                        if (displayMonth == 12) {
                            displayMonth = 1
                            displayYear++
                        } else {
                            displayMonth++
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Próximo Mês",
                            tint = GoldAmber
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Atalhos Comuns de Vencimento de Moto Clube (Dia 10, Dia 15, Dia 20)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    QuickDueButton(
                        label = "Dia 10",
                        isSelected = selectedDay == 10,
                        onClick = { selectedDay = 10 },
                        modifier = Modifier.weight(1f)
                    )
                    QuickDueButton(
                        label = "Dia 15",
                        isSelected = selectedDay == 15,
                        onClick = { selectedDay = 15 },
                        modifier = Modifier.weight(1f)
                    )
                    QuickDueButton(
                        label = "Dia 20",
                        isSelected = selectedDay == 20,
                        onClick = { selectedDay = 20 },
                        modifier = Modifier.weight(1f)
                    )
                    QuickDueButton(
                        label = "Hoje",
                        isSelected = selectedDay == todayCal.get(Calendar.DAY_OF_MONTH) &&
                                displayMonth == (todayCal.get(Calendar.MONTH) + 1) &&
                                displayYear == todayCal.get(Calendar.YEAR),
                        onClick = {
                            selectedDay = todayCal.get(Calendar.DAY_OF_MONTH)
                            displayMonth = todayCal.get(Calendar.MONTH) + 1
                            displayYear = todayCal.get(Calendar.YEAR)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Cabeçalho dos Dias da Semana (Dom a Sáb)
                val weekDays = listOf("D", "S", "T", "Q", "Q", "S", "S")
                Row(modifier = Modifier.fillMaxWidth()) {
                    weekDays.forEach { day ->
                        Text(
                            text = day,
                            color = GoldAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Grade do Calendário (Offset + Dias 1 a 31)
                val totalCells = offsetDays + daysInMonth
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 220.dp)
                ) {
                    // Células vazias de offset
                    items(offsetDays) {
                        Box(modifier = Modifier.aspectRatio(1f))
                    }

                    // Dias reais do mês
                    items(daysInMonth) { index ->
                        val dayNumber = index + 1
                        val isSelected = (dayNumber == selectedDay)
                        val isToday = dayNumber == todayCal.get(Calendar.DAY_OF_MONTH) &&
                                displayMonth == (todayCal.get(Calendar.MONTH) + 1) &&
                                displayYear == todayCal.get(Calendar.YEAR)

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .background(
                                    color = if (isSelected) GoldAmber else if (isToday) GoldAmber.copy(alpha = 0.15f) else DarkBgCard,
                                    shape = CircleShape
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else if (isToday) 1.dp else 0.5.dp,
                                    color = if (isSelected) GoldAmberLight else if (isToday) GoldAmber else TitaniumBorder,
                                    shape = CircleShape
                                )
                                .clickable { selectedDay = dayNumber },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$dayNumber",
                                color = if (isSelected) Color(0xFF1C1300) else if (isToday) GoldAmberLight else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val formatted = "%02d/%02d/%04d".format(Locale.US, selectedDay, displayMonth, displayYear)
                    onDateSelected(formatted)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAmber),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Confirmar (%02d/%02d/%04d)".format(Locale.US, selectedDay, displayMonth, displayYear),
                    color = Color(0xFF1C1300),
                    fontWeight = FontWeight.Bold
                )
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
private fun QuickDueButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) GoldAmber else DarkBgCard,
        border = BorderStroke(1.dp, if (isSelected) GoldAmberLight else TitaniumBorder),
        shape = RoundedCornerShape(6.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (isSelected) Color(0xFF1C1300) else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 5.dp)
        )
    }
}
