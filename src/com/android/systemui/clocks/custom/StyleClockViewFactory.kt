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
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.format.DateFormat
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.roundToInt

internal interface StyleClockView {
    val root: View
    val largeOffsetX: Float
    val largeOffsetY: Float

    fun render(hour: String, minute: String, full: String, contentDescription: String?)

    fun applyColor(color: Int)

    fun applyFontSize(fontSizePx: Float)
}

internal object ClockStyleViewFactory {
    fun create(context: Context, style: ClockStyle, isLargeClock: Boolean): StyleClockView {
        return when (style) {
            ClockStyle.OXYGENOS -> OxygenStyleClockView(context, isLargeClock)
            ClockStyle.IOS26 -> IOS26StyleClockView(context, isLargeClock)
            ClockStyle.ONEUI8 -> OneUI8StyleClockView(context, isLargeClock)
        }
    }
}

private class OxygenStyleClockView(
    private val context: Context,
    private val isLargeClock: Boolean,
) : StyleClockView {
    override val root = FrameLayout(context)
    override val largeOffsetX: Float = if (isLargeClock) -context.dpF(112f) else 0f
    override val largeOffsetY: Float = if (isLargeClock) -context.dpF(206f) else 0f

    private val panelBackground = glassPanel(context, 24f)

    private val hourView =
        buildText(
            context = context,
            sizePx = if (isLargeClock) context.dpF(90f) else context.dpF(54f),
            typeface = Typeface.create("sans-serif", Typeface.BOLD),
            letterSpacing = -0.01f,
        )

    private val minuteView =
        buildText(
            context = context,
            sizePx = if (isLargeClock) context.dpF(90f) else context.dpF(54f),
            typeface = Typeface.create("sans-serif", Typeface.BOLD),
            letterSpacing = -0.01f,
        )

    private val dateView =
        buildText(
            context = context,
            sizePx = if (isLargeClock) context.dpF(20f) else context.dpF(13f),
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL),
            letterSpacing = 0f,
        ).apply {
            gravity = Gravity.START
            setPadding(0, context.dp(4), 0, 0)
        }

    init {
        val panel =
            LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.START
                setPadding(
                    if (isLargeClock) context.dp(18) else context.dp(12),
                    if (isLargeClock) context.dp(14) else context.dp(10),
                    if (isLargeClock) context.dp(18) else context.dp(12),
                    if (isLargeClock) context.dp(14) else context.dp(10),
                )
                background = panelBackground
            }

        panel.addView(hourView)
        panel.addView(minuteView)
        panel.addView(dateView)

        root.addView(
            panel,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.START,
            ),
        )
    }

    override fun render(hour: String, minute: String, full: String, contentDescription: String?) {
        hourView.text = hour
        minuteView.text = minute
        dateView.text = nowDateLabel()
        root.contentDescription = contentDescription ?: full
        root.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
    }

    override fun applyColor(color: Int) {
        hourView.setTextColor(color)
        minuteView.setTextColor(color)
        dateView.setTextColor(color)
        panelBackground.setColor(withAlpha(color, 0.20f))
        panelBackground.setStroke(context.dp(1), withAlpha(color, 0.32f))
    }

    override fun applyFontSize(fontSizePx: Float) {
        val core =
            if (isLargeClock) {
                (fontSizePx * 1.16f).coerceAtLeast(context.dpF(70f))
            } else {
                (fontSizePx * 0.72f).coerceAtLeast(context.dpF(36f))
            }
        hourView.setTextSize(TypedValue.COMPLEX_UNIT_PX, core)
        minuteView.setTextSize(TypedValue.COMPLEX_UNIT_PX, core)
        dateView.setTextSize(TypedValue.COMPLEX_UNIT_PX, core * 0.24f)
    }
}

private class IOS26StyleClockView(
    private val context: Context,
    private val isLargeClock: Boolean,
) : StyleClockView {
    override val root = FrameLayout(context)
    override val largeOffsetX: Float = if (isLargeClock) -context.dpF(86f) else 0f
    override val largeOffsetY: Float = if (isLargeClock) -context.dpF(214f) else 0f

    private val panelBackground = glassPanel(context, 20f)

    private val timeView =
        buildText(
            context = context,
            sizePx = if (isLargeClock) context.dpF(78f) else context.dpF(46f),
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL),
            letterSpacing = -0.015f,
        ).apply { gravity = Gravity.START }

    private val dateView =
        buildText(
            context = context,
            sizePx = if (isLargeClock) context.dpF(18f) else context.dpF(12f),
            typeface = Typeface.create("sans-serif", Typeface.NORMAL),
            letterSpacing = 0f,
        ).apply {
            gravity = Gravity.START
            setPadding(0, context.dp(3), 0, 0)
        }

    init {
        val panel =
            LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.START
                setPadding(
                    if (isLargeClock) context.dp(18) else context.dp(12),
                    if (isLargeClock) context.dp(12) else context.dp(8),
                    if (isLargeClock) context.dp(18) else context.dp(12),
                    if (isLargeClock) context.dp(12) else context.dp(8),
                )
                background = panelBackground
            }

        panel.addView(timeView)
        panel.addView(dateView)

        root.addView(
            panel,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.START,
            ),
        )
    }

    override fun render(hour: String, minute: String, full: String, contentDescription: String?) {
        timeView.text = full
        dateView.text = nowDateLabel()
        root.contentDescription = contentDescription ?: full
        root.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
    }

    override fun applyColor(color: Int) {
        timeView.setTextColor(color)
        dateView.setTextColor(color)
        panelBackground.setColor(withAlpha(color, 0.16f))
        panelBackground.setStroke(context.dp(1), withAlpha(color, 0.30f))
    }

    override fun applyFontSize(fontSizePx: Float) {
        val core =
            if (isLargeClock) {
                (fontSizePx * 1.06f).coerceAtLeast(context.dpF(56f))
            } else {
                (fontSizePx * 0.68f).coerceAtLeast(context.dpF(30f))
            }
        timeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, core)
        dateView.setTextSize(TypedValue.COMPLEX_UNIT_PX, core * 0.22f)
    }
}

private class OneUI8StyleClockView(
    private val context: Context,
    private val isLargeClock: Boolean,
) : StyleClockView {
    override val root = FrameLayout(context)
    override val largeOffsetX: Float = 0f
    override val largeOffsetY: Float = if (isLargeClock) -context.dpF(178f) else 0f

    private val timeView =
        buildText(
            context = context,
            sizePx = if (isLargeClock) context.dpF(86f) else context.dpF(52f),
            typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD),
            letterSpacing = 0f,
        ).apply { gravity = Gravity.CENTER_HORIZONTAL }

    private val dateView =
        buildText(
            context = context,
            sizePx = if (isLargeClock) context.dpF(20f) else context.dpF(13f),
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL),
            letterSpacing = 0f,
        ).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(0, context.dp(4), 0, 0)
        }

    init {
        val container =
            LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
            }

        container.addView(timeView)
        container.addView(dateView)

        root.addView(
            container,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL,
            ),
        )
    }

    override fun render(hour: String, minute: String, full: String, contentDescription: String?) {
        timeView.text = full
        dateView.text = nowDateLabel()
        root.contentDescription = contentDescription ?: full
        root.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
    }

    override fun applyColor(color: Int) {
        timeView.setTextColor(color)
        dateView.setTextColor(color)
    }

    override fun applyFontSize(fontSizePx: Float) {
        val core =
            if (isLargeClock) {
                (fontSizePx * 1.12f).coerceAtLeast(context.dpF(64f))
            } else {
                (fontSizePx * 0.74f).coerceAtLeast(context.dpF(34f))
            }
        timeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, core)
        dateView.setTextSize(TypedValue.COMPLEX_UNIT_PX, core * 0.24f)
    }
}

private fun buildText(
    context: Context,
    sizePx: Float,
    typeface: Typeface,
    letterSpacing: Float,
): TextView {
    return TextView(context).apply {
        setSingleLine(true)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, sizePx)
        this.typeface = typeface
        this.letterSpacing = letterSpacing
        includeFontPadding = false
        setShadowLayer(context.dpF(1f), 0f, context.dpF(1f), Color.argb(70, 0, 0, 0))
    }
}

private fun glassPanel(context: Context, radiusDp: Float): GradientDrawable {
    return GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = context.dpF(radiusDp)
    }
}

private fun nowDateLabel(): String {
    return DateFormat.format("EEE, MMM d", System.currentTimeMillis()).toString()
}

private fun Context.dp(value: Int): Int {
    return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            resources.displayMetrics,
        )
        .roundToInt()
}

private fun Context.dpF(value: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics)
}

private fun withAlpha(color: Int, alpha: Float): Int {
    val a = (alpha.coerceIn(0f, 1f) * 255f).roundToInt()
    return Color.argb(a, Color.red(color), Color.green(color), Color.blue(color))
}
