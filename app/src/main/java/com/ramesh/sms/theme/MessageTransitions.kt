package com.ramesh.sms.theme

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

/**
 * Enter animation for a message bubble. Every style stays short (under 200 ms) so a burst of
 * incoming messages never turns into a slideshow.
 */
fun messageEnterTransition(style: MessageAnimation): EnterTransition = when (style) {
    MessageAnimation.FADE -> fadeIn(tween(170))
    MessageAnimation.SLIDE ->
        slideInVertically(animationSpec = tween(210)) { height -> height / 3 } + fadeIn(tween(150))
    MessageAnimation.POP ->
        scaleIn(animationSpec = tween(190), initialScale = 0.86f) + fadeIn(tween(140))
    MessageAnimation.NONE -> EnterTransition.None
}

/** Exit animation, used when a bubble leaves the list. */
fun messageExitTransition(style: MessageAnimation): ExitTransition = when (style) {
    MessageAnimation.FADE -> fadeOut(tween(120))
    MessageAnimation.SLIDE ->
        slideOutVertically(animationSpec = tween(150)) { height -> height / 4 } + fadeOut(tween(110))
    MessageAnimation.POP ->
        scaleOut(animationSpec = tween(140), targetScale = 0.9f) + fadeOut(tween(110))
    MessageAnimation.NONE -> ExitTransition.None
}
