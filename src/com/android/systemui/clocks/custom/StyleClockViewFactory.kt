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
    override val largeOffsetY: Float = if (isLargeClock) -context.dpF(428f) else 0f

    private val dateView =
        buildText(
            context = context,
            sizePx = if (isLargeClock) context.dpF(15f) else context.dpF(12f),
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL),
            letterSpacing = 0f,
        ).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            alpha = 0.95f
        }

    private val timeView =
        buildText(
            context = context,
            sizePx = if (isLargeClock) context.dpF(136f) else context.dpF(74f),
            typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD),
            letterSpacing = -0.04f,
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
                (fontSizePx * 1.35f).coerceAtLeast(context.dpF(104f))
            } else {
                (fontSizePx * 0.9f).coerceAtLeast(context.dpF(44f))
            }
        timeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, core)
        dateView.setTextSize(TypedValue.COMPLEX_UNIT_PX, core * 0.14f)
    }
}

private class IOS26StyleClockView(
    private val context: Context,
    private val isLargeClock: Boolean,
) : StyleClockView {
    override val root = FrameLayout(context)
    override val largeOffsetX: Float = 0f
    override val largeOffsetY: Float = if (isLargeClock) -context.dpF(412f) else 0f

    private val dateView =
        buildText(
            context = context,
            sizePx = if (isLargeClock) context.dpF(19f) else context.dpF(12f),
            typeface = Typeface.create("sans-serif", Typeface.NORMAL),
            letterSpacing = 0f,
        ).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            alpha = 0.96f
        }

    private val timeView =
        buildText(
            context = context,
            sizePx = if (isLargeClock) context.dpF(104f) else context.dpF(58f),
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL),
            letterSpacing = -0.02f,
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
                (fontSizePx * 1.18f).coerceAtLeast(context.dpF(86f))
            } else {
                (fontSizePx * 0.82f).coerceAtLeast(context.dpF(44f))
            }
        timeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, core)
        dateView.setTextSize(TypedValue.COMPLEX_UNIT_PX, core * 0.18f)
    }
}

private class OneUI8StyleClockView(
    private val context: Context,
    private val isLargeClock: Boolean,
) : StyleClockView {
    override val root = FrameLayout(context)
    override val largeOffsetX: Float = if (isLargeClock) -context.dpF(108f) else 0f
    override val largeOffsetY: Float = if (isLargeClock) -context.dpF(396f) else 0f

    private val timeView =
        buildText(
            context = context,
            sizePx = if (isLargeClock) context.dpF(82f) else context.dpF(50f),
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL),
            letterSpacing = 0f,
        ).apply { gravity = Gravity.START }

    private val dateView =
        buildText(
            context = context,
            sizePx = if (isLargeClock) context.dpF(22f) else context.dpF(13f),
            typeface = Typeface.create("sans-serif", Typeface.NORMAL),
            letterSpacing = 0f,
        ).apply {
            gravity = Gravity.START
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
                gravity = Gravity.START
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
    }

    override fun applyFontSize(fontSizePx: Float) {
        val core =
            if (isLargeClock) {
                (fontSizePx * 0.96f).coerceAtLeast(context.dpF(64f))
            } else {
                (fontSizePx * 0.72f).coerceAtLeast(context.dpF(36f))
            }
        timeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, core)
        dateView.setTextSize(TypedValue.COMPLEX_UNIT_PX, core * 0.26f)
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
