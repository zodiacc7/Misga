package com.miss.ga.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.miss.ga.theme.AccentColor
import com.miss.ga.theme.Appearance
import com.miss.ga.theme.AppearanceStore
import com.miss.ga.theme.BubbleStyle
import com.miss.ga.theme.BubbleTone
import com.miss.ga.theme.ChatBackdrop
import com.miss.ga.theme.ChatFontFamily
import com.miss.ga.theme.ChatFontSize
import com.miss.ga.theme.ThemeMode
import com.miss.ga.theme.SquircleCardShape
import com.miss.ga.theme.bubbleBackground
import com.miss.ga.theme.bubbleContentBottomPadding
import com.miss.ga.theme.chatBubbleShape
import com.miss.ga.theme.bubbleContent
import com.miss.ga.ui.components.AccentSwatchPicker
import com.miss.ga.ui.components.ChatDateHeader
import com.miss.ga.ui.components.SettingsActionRow
import com.miss.ga.ui.components.SettingsChoiceRow
import com.miss.ga.ui.components.SettingsColorChoiceRow
import com.miss.ga.ui.components.SettingsGroup
import com.miss.ga.ui.components.SettingsRowDivider
import com.miss.ga.ui.components.SettingsSectionHeader
import com.miss.ga.ui.components.SettingsSwitchRow
import com.miss.ga.ui.viewmodel.ConversationsViewModel

private val ToneDotColors: List<Pair<String, androidx.compose.ui.graphics.Color?>> =
    BubbleTone.values().map { tone ->
        tone.label to when (tone) {
            BubbleTone.ACCENT, BubbleTone.SURFACE -> null
            else -> tone.lightBg
        }
    }

private val BackdropDotColors: List<Pair<String, androidx.compose.ui.graphics.Color?>> =
    ChatBackdrop.values().map { backdrop ->
        backdrop.label to backdrop.light
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onOpenFilterStudio: () -> Unit,
    inboxViewModel: ConversationsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val look = AppearanceStore.current
    val inboxState by inboxViewModel.uiState.collectAsStateWithLifecycle()
    var showResetDialog by remember { mutableStateOf(false) }

    fun update(transform: (Appearance) -> Appearance) {
        AppearanceStore.update(context, transform)
    }

    val wallpaperPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // Some providers do not grant persistable access; the URI still works
                // for this session.
            }
            update { it.copy(wallpaperUri = uri.toString()) }
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Look & feel",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            item(key = "preview") {
                BubblePreviewCard(look)
            }

            item(key = "theme-header") {
                SettingsSectionHeader(
                    title = "Theme",
                    subtitle = "Pick a mode and an accent colour"
                )
            }
            item(key = "theme-group") {
                SettingsGroup {
                    SettingsChoiceRow(
                        title = "Theme mode",
                        subtitle = "Black uses true black panels for AMOLED screens",
                        options = ThemeMode.values().map { it.label },
                        selectedIndex = look.themeMode.ordinal,
                        onSelect = { index ->
                            update { it.copy(themeMode = ThemeMode.values()[index]) }
                        },
                        showDivider = true
                    )
                    Column(modifier = Modifier.padding(top = 12.dp, bottom = 14.dp)) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                text = "Accent colour",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Used for buttons, unread badges and the default bubble",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        AccentSwatchPicker(
                            selected = look.accent,
                            onSelect = { accent -> update { it.copy(accent = accent) } }
                        )
                        if (look.accent == AccentColor.SYSTEM) {
                            Text(
                                text = "Follows your wallpaper colours on Android 12+",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            item(key = "bubbles-header") {
                SettingsSectionHeader(
                    title = "Bubbles",
                    subtitle = "Textra-style bubbles, colours and message text"
                )
            }
            item(key = "bubbles-group") {
                SettingsGroup {
                    SettingsChoiceRow(
                        title = "Bubble style",
                        subtitle = "Textra adds a small tail to the bottom corner",
                        options = BubbleStyle.values().map { it.label },
                        selectedIndex = look.bubbleStyle.ordinal,
                        onSelect = { index ->
                            update { it.copy(bubbleStyle = BubbleStyle.values()[index]) }
                        }
                    )
                    SettingsSwitchRow(
                        icon = Icons.Default.ChatBubbleOutline,
                        title = "Bubble tail",
                        subtitle = "Pointy corner on the sender side",
                        checked = look.bubbleTail,
                        onCheckedChange = { enabled -> update { it.copy(bubbleTail = enabled) } },
                        showDivider = true
                    )
                    SettingsColorChoiceRow(
                        title = "Sent bubble colour",
                        options = ToneDotColors,
                        selectedIndex = look.sentTone.ordinal,
                        onSelect = { index ->
                            update { it.copy(sentTone = BubbleTone.values()[index]) }
                        }
                    )
                    SettingsColorChoiceRow(
                        title = "Received bubble colour",
                        options = ToneDotColors,
                        selectedIndex = look.receivedTone.ordinal,
                        onSelect = { index ->
                            update { it.copy(receivedTone = BubbleTone.values()[index]) }
                        }
                    )
                    SettingsChoiceRow(
                        title = "Message text size",
                        options = ChatFontSize.values().map { it.label },
                        selectedIndex = look.chatFontSize.ordinal,
                        onSelect = { index ->
                            update { it.copy(chatFontSize = ChatFontSize.values()[index]) }
                        }
                    )
                    SettingsChoiceRow(
                        title = "Message font",
                        options = ChatFontFamily.values().map { it.label },
                        selectedIndex = look.fontFamily.ordinal,
                        onSelect = { index ->
                            update { it.copy(fontFamily = ChatFontFamily.values()[index]) }
                        },
                        showDivider = true
                    )
                }
            }

            item(key = "background-header") {
                SettingsSectionHeader(
                    title = "Chat background",
                    subtitle = "A flat tone or your own photo"
                )
            }
            item(key = "background-group") {
                SettingsGroup {
                    SettingsColorChoiceRow(
                        title = "Background",
                        options = BackdropDotColors,
                        selectedIndex = look.backdrop.ordinal,
                        onSelect = { index ->
                            update { it.copy(backdrop = ChatBackdrop.values()[index]) }
                        }
                    )
                    SettingsActionRow(
                        title = if (look.wallpaperUri == null) "Choose image" else "Change image",
                        subtitle = "Pick a photo from your gallery",
                        icon = Icons.Default.Wallpaper,
                        onClick = { wallpaperPicker.launch(arrayOf("image/*")) }
                    )
                    if (look.wallpaperUri != null) {
                        SettingsActionRow(
                            title = "Remove wallpaper",
                            subtitle = "Go back to the plain background",
                            icon = Icons.Default.DeleteOutline,
                            onClick = { update { it.copy(wallpaperUri = null) } }
                        )
                    }
                }
            }

            item(key = "chat-header") {
                SettingsSectionHeader(title = "Conversation", subtitle = "In-chat behaviour")
            }
            item(key = "chat-group") {
                SettingsGroup {
                    SettingsSwitchRow(
                        icon = Icons.Default.People,
                        title = "Show contact photos",
                        subtitle = "Show the avatar next to received messages",
                        checked = look.showChatAvatars,
                        onCheckedChange = { enabled -> update { it.copy(showChatAvatars = enabled) } },
                        showDivider = true
                    )
                    SettingsSwitchRow(
                        icon = Icons.Default.CalendarMonth,
                        title = "Date dividers",
                        subtitle = "Today / Yesterday headers between message groups",
                        checked = look.showDateHeaders,
                        onCheckedChange = { enabled -> update { it.copy(showDateHeaders = enabled) } },
                        showDivider = true
                    )
                    SettingsSwitchRow(
                        icon = Icons.Default.Keyboard,
                        title = "Enter key sends",
                        subtitle = "Send with the keyboard action instead of tapping the button",
                        checked = look.enterToSend,
                        onCheckedChange = { enabled -> update { it.copy(enterToSend = enabled) } },
                        showDivider = true
                    )
                    SettingsSwitchRow(
                        icon = Icons.Default.TextSnippet,
                        title = "Character counter",
                        subtitle = "Show the SMS segment counter above the input bar",
                        checked = look.showCharacterCounter,
                        onCheckedChange = { enabled -> update { it.copy(showCharacterCounter = enabled) } },
                        showDivider = true
                    )
                    SettingsSwitchRow(
                        icon = Icons.Default.CalendarMonth,
                        title = "Tap date to switch calendar",
                        subtitle = "Toggle standard and Shamsi dates from a message timestamp",
                        checked = inboxState.enableDateTapShamsiToggle,
                        onCheckedChange = { enabled -> inboxViewModel.setDateTapShamsiEnabled(enabled) }
                    )
                }
            }

            item(key = "inbox-header") {
                SettingsSectionHeader(title = "Inbox", subtitle = "How conversations are listed")
            }
            item(key = "inbox-group") {
                SettingsGroup {
                    SettingsSwitchRow(
                        icon = Icons.Default.Tab,
                        title = "Conversation tabs",
                        subtitle = "Custom tabs above the inbox for grouping senders",
                        checked = inboxState.showTabs,
                        onCheckedChange = { enabled -> inboxViewModel.setShowTabsEnabled(enabled) },
                        showDivider = true
                    )
                    SettingsSwitchRow(
                        icon = Icons.Default.SimCard,
                        title = "SIM card filter",
                        subtitle = "Filter conversations by SIM card",
                        checked = inboxState.showSimFilter,
                        onCheckedChange = { enabled -> inboxViewModel.setShowSimFilterEnabled(enabled) },
                        showDivider = true
                    )
                    SettingsSwitchRow(
                        icon = Icons.Default.People,
                        title = "Contact photos",
                        subtitle = "Show avatars in the conversation list",
                        checked = look.showInboxAvatars,
                        onCheckedChange = { enabled -> update { it.copy(showInboxAvatars = enabled) } },
                        showDivider = true
                    )
                    SettingsSwitchRow(
                        icon = Icons.Default.Shield,
                        title = "Contacts only",
                        subtitle = "Hide conversations from unknown senders",
                        checked = inboxState.showContactsOnly,
                        onCheckedChange = { inboxViewModel.toggleContactsOnly() }
                    )
                }
            }

            item(key = "notifications-header") {
                SettingsSectionHeader(title = "Notifications", subtitle = "Message alerts")
            }
            item(key = "notifications-group") {
                SettingsGroup {
                    SettingsSwitchRow(
                        icon = Icons.Default.DeleteOutline,
                        title = "Delete action",
                        subtitle = "Add a Delete button next to Reply and Mark as read",
                        checked = inboxState.showNotificationDeleteAction,
                        onCheckedChange = { enabled ->
                            inboxViewModel.setNotificationDeleteActionEnabled(enabled)
                        },
                        showDivider = true
                    )
                    SettingsSwitchRow(
                        icon = Icons.Default.ContentCopy,
                        title = "Copy verification codes",
                        subtitle = "One-tap copy button when an SMS contains a login code",
                        checked = look.otpCopyAction,
                        onCheckedChange = { enabled -> update { it.copy(otpCopyAction = enabled) } }
                    )
                }
            }

            item(key = "filtering-header") {
                SettingsSectionHeader(title = "Filtering", subtitle = "Spam protection")
            }
            item(key = "filtering-group") {
                SettingsGroup {
                    SettingsSwitchRow(
                        icon = Icons.Default.Shield,
                        title = "No spam for contacts",
                        subtitle = "Never mark messages from your contacts as spam",
                        checked = inboxState.noSpamForContacts,
                        onCheckedChange = { enabled -> inboxViewModel.setNoSpamForContactsEnabled(enabled) },
                        showDivider = true
                    )
                    SettingsActionRow(
                        title = "Filter Studio",
                        subtitle = "Manage allowlist and blocklist rules",
                        icon = Icons.Default.FilterAlt,
                        onClick = onOpenFilterStudio
                    )
                }
            }

            item(key = "reset-header") {
                SettingsSectionHeader(title = "Reset")
            }
            item(key = "reset-group") {
                SettingsGroup {
                    SettingsActionRow(
                        title = "Restore default look",
                        subtitle = "Turquoise theme, Textra bubbles, no wallpaper",
                        icon = Icons.Default.RestartAlt,
                        onClick = { showResetDialog = true }
                    )
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            shape = SquircleCardShape,
            title = {
                Text(
                    text = "Restore default look?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Theme, bubbles, fonts and the chat background return to their defaults. Your messages and filters are untouched.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        AppearanceStore.reset(context)
                    }
                ) {
                    Text("Restore", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun BubblePreviewCard(look: Appearance) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = SquircleCardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = BorderStroke(
            0.5.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            ChatDateHeader(text = "Today")
            Row(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    shape = chatBubbleShape(false, look.bubbleStyle, look.bubbleTail),
                    color = look.receivedTone.bubbleBackground(false),
                    border = BorderStroke(
                        0.5.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = "Misga looks like this",
                        style = MaterialTheme.typography.bodyMedium,
                        color = look.receivedTone.bubbleContent(false),
                        modifier = Modifier
                            .widthIn(max = 240.dp)
                            .padding(
                                start = 12.dp,
                                end = 12.dp,
                                top = 9.dp,
                                bottom = 9.dp + bubbleContentBottomPadding(
                                    look.bubbleStyle,
                                    look.bubbleTail
                                )
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = chatBubbleShape(true, look.bubbleStyle, look.bubbleTail),
                    color = look.sentTone.bubbleBackground(true)
                ) {
                    Text(
                        text = "And this is your bubble",
                        style = MaterialTheme.typography.bodyMedium,
                        color = look.sentTone.bubbleContent(true),
                        modifier = Modifier
                            .widthIn(max = 240.dp)
                            .padding(
                                start = 12.dp,
                                end = 12.dp,
                                top = 9.dp,
                                bottom = 9.dp + bubbleContentBottomPadding(
                                    look.bubbleStyle,
                                    look.bubbleTail
                                )
                            )
                    )
                }
            }
        }
    }
}
