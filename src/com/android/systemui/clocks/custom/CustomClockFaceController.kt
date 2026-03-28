/*
 * Copyright (C) 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.android.systemui.clocks.custom

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import com.android.systemui.customization.clocks.DefaultClockFaceLayout
import com.android.systemui.customization.clocks.DigitalTimeFormatter
import com.android.systemui.customization.clocks.TimeKeeper
import com.android.systemui.plugins.keyguard.ui.clocks.ClockAnimations
import com.android.systemui.plugins.keyguard.ui.clocks.ClockAxisStyle
import com.android.systemui.plugins.keyguard.ui.clocks.ClockFaceConfig
import com.android.systemui.plugins.keyguard.ui.clocks.ClockFaceController
import com.android.systemui.plugins.keyguard.ui.clocks.ClockFaceEvents
import com.android.systemui.plugins.keyguard.ui.clocks.ClockPositionAnimationArgs
import com.android.systemui.plugins.keyguard.ui.clocks.ClockSettings
import com.android.systemui.plugins.keyguard.ui.clocks.ClockViewIds
import com.android.systemui.plugins.keyguard.ui.clocks.ThemeConfig
import kotlin.math.roundToInt

class CustomClockFaceController(
    private val hostCtx: Context,
    private val pluginCtx: Context,
    private val settings: ClockSettings,
    private val style: ClockStyle,
    private val hourFormatter: DigitalTimeFormatter,
    private val minuteFormatter: DigitalTimeFormatter,
    private val fullFormatter: DigitalTimeFormatter,
    private val timeKeeper: TimeKeeper,
    private val isLargeClock: Boolean,
) : ClockFaceController {

    private val styleView = ClockStyleViewFactory.create(pluginCtx, style, isLargeClock)
    private val baseOffsetX = if (isLargeClock) styleView.largeOffsetX else 0f
    private val baseOffsetY = if (isLargeClock) styleView.largeOffsetY else 0f
    private var dozeFraction = 0f

    override val config = ClockFaceConfig()

    override var theme = ThemeConfig(isDarkTheme = true, settings.seedColor)

    override val view: View =
        styleView.root.apply {
            id =
                if (isLargeClock) ClockViewIds.LOCKSCREEN_CLOCK_VIEW_LARGE
                else ClockViewIds.LOCKSCREEN_CLOCK_VIEW_SMALL
            visibility = View.VISIBLE
            alpha = 1f
            if (isLargeClock) {
                translationX = baseOffsetX
                translationY = baseOffsetY
            }
        }

    override val layout = DefaultClockFaceLayout(view)

    fun refreshTime() {
        timeKeeper.updateTime()
        styleView.render(
            hour = hourFormatter.getText(),
            minute = minuteFormatter.getText(),
            full = fullFormatter.getText(),
            contentDescription = fullFormatter.getContentDescription(),
        )
    }

    override val events =
        object : ClockFaceEvents {
            override fun onTimeTick() = refreshTime()

            override fun onThemeChanged(theme: ThemeConfig) {
                this@CustomClockFaceController.theme = theme
                applyRenderColor()
            }

            override fun onFontSettingChanged(fontSizePx: Float) {
                styleView.applyFontSize(fontSizePx)
            }

            override fun onTargetRegionChanged(targetRegion: Rect?) {}

            override fun onSecondaryDisplayChanged(onSecondaryDisplay: Boolean) {}
        }

    override val animations =
        object : ClockAnimations {
            override fun enter() {
                view.animate().cancel()
                view.alpha = 0f
                view.animate().alpha(1f).setDuration(220).start()
            }

            override fun doze(fraction: Float) {
                dozeFraction = fraction.coerceIn(0f, 1f)
                applyRenderColor()
                view.alpha = 1f - (0.12f * dozeFraction)
            }

            override fun fold(fraction: Float) {
                view.translationY = if (isLargeClock) baseOffsetY else 8f * fraction
            }

            override fun charge() {
                view.animate().cancel()
                view.animate().scaleX(1.04f).scaleY(1.04f).setDuration(140).withEndAction {
                    view.animate().scaleX(1f).scaleY(1f).setDuration(160).start()
                }.start()
            }

            override fun onPositionAnimated(args: ClockPositionAnimationArgs) {}

            override fun onPickerCarouselSwiping(swipingFraction: Float) {
                val clamped = swipingFraction.coerceIn(0f, 1f)
                val scale = 0.82f + (0.18f * clamped)
                view.scaleX = scale
                view.scaleY = scale
            }

            override fun onFidgetTap(x: Float, y: Float) {}

            override fun onFontAxesChanged(style: ClockAxisStyle) {}
        }

    private fun applyRenderColor() {
        val lockscreenColor = theme.getDefaultColor(hostCtx)
        val aodColor = theme.getAodColor(hostCtx)
        styleView.applyColor(blendColor(lockscreenColor, aodColor, dozeFraction))
    }

    @ColorInt
    private fun blendColor(@ColorInt from: Int, @ColorInt to: Int, fraction: Float): Int {
        val f = fraction.coerceIn(0f, 1f)
        val a = (Color.alpha(from) + (Color.alpha(to) - Color.alpha(from)) * f).roundToInt()
        val r = (Color.red(from) + (Color.red(to) - Color.red(from)) * f).roundToInt()
        val g = (Color.green(from) + (Color.green(to) - Color.green(from)) * f).roundToInt()
        val b = (Color.blue(from) + (Color.blue(to) - Color.blue(from)) * f).roundToInt()
        return Color.argb(a, r, g, b)
    }
}
