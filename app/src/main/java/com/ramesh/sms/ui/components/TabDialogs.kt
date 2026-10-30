package com.ramesh.sms.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ramesh.sms.R
import com.ramesh.sms.data.model.ConversationThread
import com.ramesh.sms.data.model.SenderTab
import com.ramesh.sms.data.util.PhoneNumberKeys
import com.ramesh.sms.theme.PillShape
import com.ramesh.sms.theme.SquircleCardShape
import com.ramesh.sms.theme.SquircleMediumShape
import com.ramesh.sms.ui.util.contentAware
import com.ramesh.sms.ui.util.senderDisplayName

@Composable
fun CreateTabDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var tabName by remember { mutableStateOf("") }
    val isBlank = tabName.trim().isBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = SquircleCardShape,
        title = {
            Text(
                text = stringResource(R.string.tab_create_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.tab_create_sub),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = tabName,
                    onValueChange = { tabName = it },
                    label = { Text(stringResource(R.string.tab_name_label)) },
                    placeholder = { Text(stringResource(R.string.tab_name_hint)) },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.contentAware(),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {
                    if (!isBlank) {
                        onCreate(tabName.trim())
                    }
                },
                enabled = !isBlank,
                shape = PillShape
            ) {
                Text(stringResource(R.string.tab_create), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
fun DeleteTabDialog(
    tabName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = SquircleCardShape,
        title = {
            Text(
                text = stringResource(R.string.tab_delete_title, tabName),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = stringResource(R.string.tab_delete_body),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    "Delete",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
fun AddToTabDialog(
    selectedCount: Int,
    tabs: List<SenderTab>,
    onDismiss: () -> Unit,
    onAssignToTabs: (List<Long>) -> Unit,
    onCreateNewTab: () -> Unit
) {
    val selectedTabIds = remember { mutableStateMapOf<Long, Boolean>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = SquircleCardShape,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.tab_add_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onCreateNewTab) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.tab_new_title))
                }
            }
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.tab_add_body, selectedCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (tabs.isEmpty()) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.tab_none_yet),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.tab_none_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 240.dp)) {
                        items(tabs, key = { it.id }) { tab ->
                            val isChecked = selectedTabIds[tab.id] == true
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.small)
                                    .clickable { selectedTabIds[tab.id] = !isChecked }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DriveFileMove,
                                    contentDescription = null,
                                    tint = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tab.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = stringResource(R.string.tab_sender_count, tab.senderAddresses.size),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { selectedTabIds[tab.id] = it }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            val checkedIds = selectedTabIds.filterValues { it }.keys.toList()
            FilledTonalButton(
                onClick = { onAssignToTabs(checkedIds) },
                enabled = checkedIds.isNotEmpty(),
                shape = PillShape
            ) {
                Text(stringResource(R.string.action_add), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

