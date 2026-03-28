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
    override val largeOffsetX: Float = 0f
    override val largeOffsetY: Float = if (isLargeClock) -context.dpF(338f) else 0f

    private val dateView =
        buildText(
            context = context,
            sizePx = if (isLargeClock) context.dpF(20f) else context.dpF(13f),
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL),
            letterSpacing = 0f,
        ).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            alpha = 0.95f
        }

    private val timeView =
        buildText(
            context = context,
            sizePx = if (isLargeClock) context.dpF(118f) else context.dpF(62f),
            typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD),
            letterSpacing = -0.025f,
        ).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(0, context.dp(4), 0, 0)
        }

    init {
        root.setBackgroundColor(Color.TRANSPARENT)
        root.clipChildren = false
        root.clipToPadding = false

        val col =
            LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
                clipChildren = false
                clipToPadding = false
            }
        col.addView(dateView)
        col.addView(timeView)

        root.addView(
            col,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL,
            ),
        )
    }

    override fun render(hour: String, minute: String, full: String, contentDescription: String?) {
        dateView.text = nowDateLabel()
        timeView.text = hour + minute
        root.contentDescription = contentDescription ?: full
        root.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
    }

    override fun applyColor(color: Int) {
        dateView.setTextColor(color)
        timeView.setTextColor(color)
    }

    override fun applyFontSize(fontSizePx: Float) {
        val core =
            if (isLargeClock) {
                (fontSizePx * 1.2f).coerceAtLeast(context.dpF(84f))
            } else {
                (fontSizePx * 0.76f).coerceAtLeast(context.dpF(36f))
            }
        timeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, core)
        dateView.setTextSize(TypedValue.COMPLEX_UNIT_PX, core * 0.2f)
    }
}

private class IOS26StyleClockView(
    private val context: Context,
    private val isLargeClock: Boolean,
) : StyleClockView {
    override val root = FrameLayout(context)
    override val largeOffsetX: Float = 0f
    override val largeOffsetY: Float = if (isLargeClock) -context.dpF(324f) else 0f

    private val dateView =
        buildText(
            context = context,
            sizePx = if (isLargeClock) context.dpF(21f) else context.dpF(13f),
            typeface = Typeface.create("sans-serif", Typeface.NORMAL),
            letterSpacing = 0f,
        ).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            alpha = 0.96f
        }

    private val timeView =
        buildText(
            context = context,
            sizePx = if (isLargeClock) context.dpF(92f) else context.dpF(52f),
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL),
            letterSpacing = -0.01f,
        ).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(0, context.dp(2), 0, 0)
        }

    init {
        root.setBackgroundColor(Color.TRANSPARENT)
        root.clipChildren = false
        root.clipToPadding = false

        val col =
            LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
                clipChildren = false
                clipToPadding = false
            }
        col.addView(dateView)
        col.addView(timeView)

        root.addView(
            col,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL,
            ),
        )
    }

    override fun render(hour: String, minute: String, full: String, contentDescription: String?) {
        dateView.text = nowDateLabel()
        timeView.text = full
        root.contentDescription = contentDescription ?: full
        root.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
    }

    override fun applyColor(color: Int) {
        dateView.setTextColor(color)
        timeView.setTextColor(color)
    }

    override fun applyFontSize(fontSizePx: Float) {
        val core =
            if (isLargeClock) {
                (fontSizePx * 1.04f).coerceAtLeast(context.dpF(68f))
            } else {
                (fontSizePx * 0.72f).coerceAtLeast(context.dpF(34f))
            }
        timeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, core)
        dateView.setTextSize(TypedValue.COMPLEX_UNIT_PX, core * 0.23f)
    }
}

private class OneUI8StyleClockView(
    private val context: Context,
    private val isLargeClock: Boolean,
) : StyleClockView {
    override val root = FrameLayout(context)
    override val largeOffsetX: Float = 0f
    override val largeOffsetY: Float = if (isLargeClock) -context.dpF(304f) else 0f

    private val timeView =
        buildText(
            context = context,
            sizePx = if (isLargeClock) context.dpF(74f) else context.dpF(46f),
            typeface = Typeface.create("sans-serif", Typeface.BOLD),
            letterSpacing = 0f,
        ).apply { gravity = Gravity.CENTER_HORIZONTAL }

    private val dateView =
        buildText(
            context = context,
            sizePx = if (isLargeClock) context.dpF(20f) else context.dpF(13f),
            typeface = Typeface.create("sans-serif", Typeface.NORMAL),
            letterSpacing = 0f,
        ).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            alpha = 0.96f
            setPadding(0, context.dp(2), 0, 0)
        }

    init {
        root.setBackgroundColor(Color.TRANSPARENT)
        root.clipChildren = false
        root.clipToPadding = false

        val col =
            LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
                clipChildren = false
                clipToPadding = false
            }

        col.addView(timeView)
        col.addView(dateView)

        root.addView(
            col,
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
                (fontSizePx * 0.92f).coerceAtLeast(context.dpF(54f))
            } else {
                (fontSizePx * 0.65f).coerceAtLeast(context.dpF(30f))
            }
        timeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, core)
        dateView.setTextSize(TypedValue.COMPLEX_UNIT_PX, core * 0.29f)
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
        setBackgroundColor(Color.TRANSPARENT)
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
