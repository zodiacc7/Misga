package com.ramesh.sms.ui.screens

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
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.app.Activity
import com.ramesh.sms.R
import com.ramesh.sms.data.util.AppLanguage
import com.ramesh.sms.data.util.LockScreenPrivacy
import com.ramesh.sms.theme.AccentColor
import com.ramesh.sms.theme.Appearance
import com.ramesh.sms.theme.AppearanceStore
import com.ramesh.sms.theme.BubbleStyle
import com.ramesh.sms.theme.BubbleTone
import com.ramesh.sms.theme.ChatBackdrop
import com.ramesh.sms.theme.ChatFontFamily
import com.ramesh.sms.theme.ChatFontSize
import com.ramesh.sms.theme.ThemeMode
import com.ramesh.sms.theme.SquircleCardShape
import com.ramesh.sms.theme.bubbleBackground
import com.ramesh.sms.theme.bubbleContentBottomPadding
import com.ramesh.sms.theme.chatBubbleShape
import com.ramesh.sms.theme.bubbleContent
import com.ramesh.sms.ui.components.AccentSwatchPicker
import com.ramesh.sms.ui.components.ChatDateHeader
import com.ramesh.sms.ui.components.SettingsActionRow
import com.ramesh.sms.ui.components.SettingsChoiceRow
import com.ramesh.sms.ui.components.SettingsColorChoiceRow
import com.ramesh.sms.ui.components.SettingsGroup
import com.ramesh.sms.ui.components.SettingsRowDivider
import com.ramesh.sms.ui.components.SettingsSectionHeader
import com.ramesh.sms.ui.components.SettingsSwitchRow
import com.ramesh.sms.ui.viewmodel.ConversationsViewModel

private fun sendDelayOptions(context: android.content.Context): List<Pair<Int, String>> = listOf(
    0 to context.getString(R.string.settings_off),
    3 to context.getString(R.string.settings_seconds_3),
    5 to context.getString(R.string.settings_seconds_5),
    10 to context.getString(R.string.settings_seconds_10)
)

private fun autoDeleteOptions(context: android.content.Context): List<Pair<Int, String>> = listOf(
    0 to context.getString(R.string.settings_off),
    500 to context.getString(R.string.settings_500),
    1_000 to context.getString(R.string.settings_1000),
    2_000 to context.getString(R.string.settings_2000),
    5_000 to context.getString(R.string.settings_5000)
)

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
                            text = stringResource(R.string.settings_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = stringResource(R.string.settings_subtitle),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.settings_back))
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
                    title = stringResource(R.string.settings_theme_header),
                    subtitle = stringResource(R.string.settings_theme_sub)
                )
            }
            item(key = "theme-group") {
                SettingsGroup {
                    SettingsChoiceRow(
                        title = stringResource(R.string.settings_theme_mode),
                        subtitle = stringResource(R.string.settings_theme_mode_sub),
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
                                text = stringResource(R.string.settings_accent),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = stringResource(R.string.settings_accent_sub),
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
                                text = stringResource(R.string.settings_accent_system),
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
                    title = stringResource(R.string.settings_bubbles_header),
                    subtitle = stringResource(R.string.settings_bubbles_sub)
                )
            }
            item(key = "bubbles-group") {
                SettingsGroup {
                    SettingsChoiceRow(
                        title = stringResource(R.string.settings_bubble_style),
                        subtitle = stringResource(R.string.settings_bubble_style_sub),
                        options = BubbleStyle.values().map { it.label },
                        selectedIndex = look.bubbleStyle.ordinal,
                        onSelect = { index ->
                            update { it.copy(bubbleStyle = BubbleStyle.values()[index]) }
                        }
                    )
                    SettingsSwitchRow(
                        icon = Icons.Default.ChatBubbleOutline,
                        title = stringResource(R.string.settings_bubble_tail),
                        subtitle = stringResource(R.string.settings_bubble_tail_sub),
                        checked = look.bubbleTail,
                        onCheckedChange = { enabled -> update { it.copy(bubbleTail = enabled) } },
                        showDivider = true
                    )
                    SettingsColorChoiceRow(
                        title = stringResource(R.string.settings_sent_color),
                        options = ToneDotColors,
                        selectedIndex = look.sentTone.ordinal,
                        onSelect = { index ->
                            update { it.copy(sentTone = BubbleTone.values()[index]) }
                        }
                    )
                    SettingsColorChoiceRow(
                        title = stringResource(R.string.settings_received_color),
                        options = ToneDotColors,
                        selectedIndex = look.receivedTone.ordinal,
                        onSelect = { index ->
                            update { it.copy(receivedTone = BubbleTone.values()[index]) }
                        }
                    )
                    SettingsChoiceRow(
                        title = stringResource(R.string.settings_text_size),
                        options = ChatFontSize.values().map { it.label },
                        selectedIndex = look.chatFontSize.ordinal,
                        onSelect = { index ->
                            update { it.copy(chatFontSize = ChatFontSize.values()[index]) }
                        }
                    )
                    SettingsChoiceRow(
                        title = stringResource(R.string.settings_message_font),
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
                    title = stringResource(R.string.settings_background_header),
                    subtitle = stringResource(R.string.settings_background_sub)
                )
            }
            item(key = "background-group") {
                SettingsGroup {
                    SettingsColorChoiceRow(
                        title = stringResource(R.string.settings_background),
                        options = BackdropDotColors,
                        selectedIndex = look.backdrop.ordinal,
                        onSelect = { index ->
                            update { it.copy(backdrop = ChatBackdrop.values()[index]) }
                        }
                    )
                    SettingsActionRow(
                        title = if (look.wallpaperUri == null) stringResource(R.string.settings_choose_image) else stringResource(R.string.settings_change_image),
                        subtitle = stringResource(R.string.settings_pick_photo),
                        icon = Icons.Default.Wallpaper,
                        onClick = { wallpaperPicker.launch(arrayOf("image/*")) }
                    )
                    if (look.wallpaperUri != null) {
                        SettingsActionRow(
                            title = stringResource(R.string.settings_remove_wallpaper),
                            subtitle = stringResource(R.string.settings_remove_wallpaper_sub),
                            icon = Icons.Default.DeleteOutline,
                            onClick = { update { it.copy(wallpaperUri = null) } }
                        )
                    }
                }
            }

            item(key = "chat-header") {
                SettingsSectionHeader(title = stringResource(R.string.settings_chat_header), subtitle = stringResource(R.string.settings_chat_sub))
            }
            item(key = "chat-group") {
                SettingsGroup {
                    SettingsSwitchRow(
                        icon = Icons.Default.People,
                        title = stringResource(R.string.settings_show_photos),
                        subtitle = stringResource(R.string.settings_show_photos_sub),
                        checked = look.showChatAvatars,
                        onCheckedChange = { enabled -> update { it.copy(showChatAvatars = enabled) } },
                        showDivider = true
                    )
                    SettingsSwitchRow(
                        icon = Icons.Default.CalendarMonth,
                        title = stringResource(R.string.settings_date_dividers),
                        subtitle = stringResource(R.string.settings_date_dividers_sub),
                        checked = look.showDateHeaders,
                        onCheckedChange = { enabled -> update { it.copy(showDateHeaders = enabled) } },
                        showDivider = true
                    )
                    SettingsSwitchRow(
                        icon = Icons.Default.Keyboard,
                        title = stringResource(R.string.settings_enter_sends),
                        subtitle = stringResource(R.string.settings_enter_sends_sub),
                        checked = look.enterToSend,
                        onCheckedChange = { enabled -> update { it.copy(enterToSend = enabled) } },
                        showDivider = true
                    )
                    SettingsSwitchRow(
                        icon = Icons.Default.TextSnippet,
                        title = stringResource(R.string.settings_counter),
                        subtitle = stringResource(R.string.settings_counter_sub),
                        checked = look.showCharacterCounter,
                        onCheckedChange = { enabled -> update { it.copy(showCharacterCounter = enabled) } },
                        showDivider = true
                    )
                    SettingsSwitchRow(
                        icon = Icons.Default.CalendarMonth,
                        title = stringResource(R.string.settings_date_tap),
                        subtitle = stringResource(R.string.settings_date_tap_sub),
                        checked = inboxState.enableDateTapShamsiToggle,
                        onCheckedChange = { enabled -> inboxViewModel.setDateTapShamsiEnabled(enabled) },
                        showDivider = true
                    )
                    SettingsChoiceRow(
                        title = stringResource(R.string.settings_send_delay),
                        subtitle = stringResource(R.string.settings_send_delay_sub),
                        options = sendDelayOptions(context).map { it.second },
                        selectedIndex = sendDelayOptions(context).indexOfFirst {
                            it.first == inboxState.sendDelaySeconds
                        }.coerceAtLeast(0),
                        onSelect = { index ->
                            inboxViewModel.setSendDelaySeconds(sendDelayOptions(context)[index].first)
                        }
                    )
                }
            }

            item(key = "inbox-header") {
                SettingsSectionHeader(title = stringResource(R.string.settings_inbox_header), subtitle = stringResource(R.string.settings_inbox_sub))
            }
            item(key = "inbox-group") {
                SettingsGroup {
                    SettingsSwitchRow(
                        icon = Icons.Default.Tab,
                        title = stringResource(R.string.settings_tabs),
                        subtitle = stringResource(R.string.settings_tabs_sub),
                        checked = inboxState.showTabs,
                        onCheckedChange = { enabled -> inboxViewModel.setShowTabsEnabled(enabled) },
                        showDivider = true
                    )
                    SettingsSwitchRow(
                        icon = Icons.Default.SimCard,
                        title = stringResource(R.string.settings_sim_filter),
                        subtitle = stringResource(R.string.settings_sim_filter_sub),
                        checked = inboxState.showSimFilter,
                        onCheckedChange = { enabled -> inboxViewModel.setShowSimFilterEnabled(enabled) },
                        showDivider = true
                    )
                    SettingsSwitchRow(
                        icon = Icons.Default.People,
                        title = stringResource(R.string.settings_contact_photos),
                        subtitle = stringResource(R.string.settings_contact_photos_sub),
                        checked = look.showInboxAvatars,
                        onCheckedChange = { enabled -> update { it.copy(showInboxAvatars = enabled) } },
                        showDivider = true
                    )
                    SettingsSwitchRow(
                        icon = Icons.Default.Shield,
                        title = stringResource(R.string.settings_contacts_only),
                        subtitle = stringResource(R.string.settings_contacts_only_sub),
                        checked = inboxState.showContactsOnly,
                        onCheckedChange = { inboxViewModel.toggleContactsOnly() }
                    )
                }
            }

            item(key = "notifications-header") {
                SettingsSectionHeader(title = stringResource(R.string.settings_notifications_header), subtitle = stringResource(R.string.settings_notifications_sub))
            }
            item(key = "notifications-group") {
                SettingsGroup {
                    SettingsSwitchRow(
                        icon = Icons.Default.DeleteOutline,
                        title = stringResource(R.string.settings_delete_action),
                        subtitle = stringResource(R.string.settings_delete_action_sub),
                        checked = inboxState.showNotificationDeleteAction,
                        onCheckedChange = { enabled ->
                            inboxViewModel.setNotificationDeleteActionEnabled(enabled)
                        },
                        showDivider = true
                    )
                    SettingsSwitchRow(
                        icon = Icons.Default.ContentCopy,
                        title = stringResource(R.string.settings_copy_codes),
                        subtitle = stringResource(R.string.settings_copy_codes_sub),
                        checked = look.otpCopyAction,
                        onCheckedChange = { enabled -> update { it.copy(otpCopyAction = enabled) } },
                        showDivider = true
                    )
                    SettingsSwitchRow(
                        icon = Icons.Default.CheckCircle,
                        title = stringResource(R.string.settings_delivery),
                        subtitle = stringResource(R.string.settings_delivery_sub),
                        checked = inboxState.deliveryReports,
                        onCheckedChange = { enabled -> inboxViewModel.setDeliveryReportsEnabled(enabled) },
                        showDivider = true
                    )
                    SettingsChoiceRow(
                        title = stringResource(R.string.settings_lock_screen),
                        subtitle = stringResource(R.string.settings_lock_screen_sub),
                        options = listOf(
                            LockScreenPrivacy.FULL.label,
                            LockScreenPrivacy.SENDER_ONLY.label,
                            LockScreenPrivacy.HIDDEN.label
                        ),
                        selectedIndex = inboxState.lockScreenPrivacy.ordinal,
                        onSelect = { index ->
                            inboxViewModel.setLockScreenPrivacy(LockScreenPrivacy.entries[index])
                        }
                    )
                }
            }

            item(key = "filtering-header") {
                SettingsSectionHeader(title = stringResource(R.string.settings_filtering_header), subtitle = stringResource(R.string.settings_filtering_sub))
            }
            item(key = "filtering-group") {
                SettingsGroup {
                    SettingsSwitchRow(
                        icon = Icons.Default.Shield,
                        title = stringResource(R.string.settings_no_spam_contacts),
                        subtitle = stringResource(R.string.settings_no_spam_contacts_sub),
                        checked = inboxState.noSpamForContacts,
                        onCheckedChange = { enabled -> inboxViewModel.setNoSpamForContactsEnabled(enabled) },
                        showDivider = true
                    )
                    SettingsActionRow(
                        title = stringResource(R.string.settings_filter_studio),
                        subtitle = stringResource(R.string.settings_filter_studio_sub),
                        icon = Icons.Default.FilterAlt,
                        onClick = onOpenFilterStudio
                    )
                }
            }

            item(key = "language-header") {
                SettingsSectionHeader(
                    title = stringResource(R.string.settings_language_header),
                    subtitle = stringResource(R.string.settings_language_sub)
                )
            }
            item(key = "language-group") {
                SettingsGroup {
                    SettingsChoiceRow(
                        title = stringResource(R.string.settings_app_language),
                        subtitle = stringResource(R.string.settings_app_language_sub),
                        options = AppLanguage.entries.map { it.label },
                        selectedIndex = inboxState.appLanguage.ordinal,
                        onSelect = { index ->
                            inboxViewModel.setAppLanguage(AppLanguage.entries[index])
                            (context as? Activity)?.recreate()
                        }
                    )
                }
            }

            item(key = "storage-header") {
                SettingsSectionHeader(
                    title = stringResource(R.string.settings_storage_header),
                    subtitle = stringResource(R.string.settings_storage_sub)
                )
            }
            item(key = "storage-group") {
                SettingsGroup {
                    SettingsChoiceRow(
                        title = stringResource(R.string.settings_auto_delete),
                        subtitle = stringResource(R.string.settings_auto_delete_sub),
                        options = autoDeleteOptions(context).map { it.second },
                        selectedIndex = autoDeleteOptions(context).indexOfFirst {
                            it.first == inboxState.autoDeleteLimit
                        }.coerceAtLeast(0),
                        onSelect = { index ->
                            inboxViewModel.setAutoDeleteLimit(autoDeleteOptions(context)[index].first)
                        }
                    )
                }
            }

            item(key = "reset-header") {
                SettingsSectionHeader(title = stringResource(R.string.settings_reset_header))
            }
            item(key = "reset-group") {
                SettingsGroup {
                    SettingsActionRow(
                        title = stringResource(R.string.settings_restore_look),
                        subtitle = stringResource(R.string.settings_restore_look_sub),
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
                    text = stringResource(R.string.settings_restore_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.settings_restore_body),
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
                    Text(stringResource(R.string.settings_restore), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(stringResource(R.string.settings_cancel))
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
            ChatDateHeader(text = stringResource(R.string.settings_today))
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
                        text = stringResource(R.string.settings_preview_in),
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
                        text = stringResource(R.string.settings_preview_out),
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
