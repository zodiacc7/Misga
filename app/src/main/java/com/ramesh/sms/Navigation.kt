package com.ramesh.sms

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.ramesh.sms.ui.screens.ChatScreen
import com.ramesh.sms.ui.screens.ComposeScreen
import com.ramesh.sms.ui.screens.ConversationsScreen
import com.ramesh.sms.ui.screens.FilterStudioScreen
import com.ramesh.sms.ui.screens.SettingsScreen
import com.ramesh.sms.ui.viewmodel.ConversationsViewModel

@Composable
fun MainNavigation(
    pendingChatNav: ChatNav? = null,
    onChatNavHandled: () -> Unit = {},
    pendingComposeNav: ComposeNav? = null,
    onComposeNavHandled: () -> Unit = {}
) {
    val backStack = rememberNavBackStack(ConversationsNav)
    val conversationsViewModel: ConversationsViewModel = viewModel()

    LaunchedEffect(pendingChatNav) {
        if (pendingChatNav != null) {
            conversationsViewModel.clearUnreadForThread(pendingChatNav.threadId)
            val current = backStack.lastOrNull()
            if (current !is ChatNav || current.threadId != pendingChatNav.threadId) {
                backStack.add(pendingChatNav)
            }
            onChatNavHandled()
        }
    }

    LaunchedEffect(pendingComposeNav) {
        if (pendingComposeNav != null) {
            val current = backStack.lastOrNull()
            if (current !is ComposeNav || current != pendingComposeNav) {
                backStack.add(pendingComposeNav)
            }
            onComposeNavHandled()
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<ConversationsNav> {
                ConversationsScreen(
                    onNavigateToChat = { chatNav -> backStack.add(chatNav) },
                    onNavigateToFilterStudio = { backStack.add(FilterStudioNav) },
                    onNavigateToCompose = { backStack.add(ComposeNav()) },
                    onNavigateToTestLab = { backStack.add(TestLabNav) },
                    onNavigateToSettings = { backStack.add(SettingsNav) },
                    viewModel = conversationsViewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
            entry<ChatNav> { nav ->
                ChatScreen(
                    nav = nav,
                    onBackClick = { backStack.removeLastOrNull() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            entry<SettingsNav> {
                SettingsScreen(
                    onBackClick = { backStack.removeLastOrNull() },
                    onOpenFilterStudio = { backStack.add(FilterStudioNav) },
                    inboxViewModel = conversationsViewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
            entry<FilterStudioNav> {
                FilterStudioScreen(
                    onBackClick = { backStack.removeLastOrNull() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            entry<ComposeNav> { nav ->
                ComposeScreen(
                    onBackClick = { backStack.removeLastOrNull() },
                    onNavigateToChat = { chatNav ->
                        backStack.removeLastOrNull()
                        backStack.add(chatNav)
                    },
                    initialAddress = nav.address,
                    initialBody = nav.body,
                    modifier = Modifier.fillMaxSize()
                )
            }
            entry<TestLabNav> {
                com.ramesh.sms.testing.TestLabScreen(
                    onBackClick = { backStack.removeLastOrNull() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    )
}
