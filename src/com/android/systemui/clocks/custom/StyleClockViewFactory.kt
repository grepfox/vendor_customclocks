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
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.roundToInt

internal interface StyleClockView {
    val root: View

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

private class OxygenStyleClockView(context: Context, private val isLargeClock: Boolean) : StyleClockView {
    private val rootFrame = FrameLayout(context)
    private val bubbleBackground = GradientDrawable()

    private var hourView: TextView? = null
    private var minuteView: TextView? = null

    override val root: View = rootFrame

    init {
        if (isLargeClock) {
            val stack =
                LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER_HORIZONTAL
                }

            hourView =
                buildText(
                    context = context,
                    sizePx = context.dpF(120f),
                    typeface = Typeface.create("sans-serif-medium", Typeface.BOLD),
                    letterSpacing = -0.03f,
                )

            minuteView =
                buildText(
                    context = context,
                    sizePx = context.dpF(120f),
                    typeface = Typeface.create("sans-serif-medium", Typeface.BOLD),
                    letterSpacing = -0.03f,
                )

            stack.addView(hourView)
            stack.addView(minuteView)
            rootFrame.addView(
                stack,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER,
                ),
            )
        } else {
            val bubble =
                LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER
                    setPadding(context.dp(14), context.dp(9), context.dp(14), context.dp(9))
                    background = bubbleBackground
                }

            hourView =
                buildText(
                    context = context,
                    sizePx = context.dpF(26f),
                    typeface = Typeface.create("sans-serif-medium", Typeface.BOLD),
                    letterSpacing = -0.02f,
                )

            minuteView =
                buildText(
                    context = context,
                    sizePx = context.dpF(26f),
                    typeface = Typeface.create("sans-serif-medium", Typeface.BOLD),
                    letterSpacing = -0.02f,
                )

            bubble.addView(hourView)
            bubble.addView(minuteView)
            rootFrame.addView(
                bubble,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER,
                ),
            )
        }
    }

    override fun render(hour: String, minute: String, full: String, contentDescription: String?) {
        hourView?.text = hour
        minuteView?.text = minute

        root.contentDescription = contentDescription ?: full
        root.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
    }

    override fun applyColor(color: Int) {
        hourView?.setTextColor(color)
        minuteView?.setTextColor(color)

        if (!isLargeClock) {
            bubbleBackground.shape = GradientDrawable.RECTANGLE
            bubbleBackground.cornerRadius = root.context.dpF(20f)
            bubbleBackground.setColor(withAlpha(color, 0.23f))
        }
    }

    override fun applyFontSize(fontSizePx: Float) {
        if (isLargeClock) {
            val size = (fontSizePx * 1.45f).coerceAtLeast(root.context.dpF(82f))
            hourView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
            minuteView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        } else {
            val size = (fontSizePx * 0.62f).coerceAtLeast(root.context.dpF(22f))
            hourView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
            minuteView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }
    }
}

private class IOS26StyleClockView(context: Context, private val isLargeClock: Boolean) : StyleClockView {
    private val rootFrame = FrameLayout(context)
    private val chipBackground = GradientDrawable()

    private val timeView =
        buildText(
            context = context,
            sizePx = if (isLargeClock) context.dpF(118f) else context.dpF(34f),
            typeface = Typeface.create("sans-serif-light", Typeface.NORMAL),
            letterSpacing = if (isLargeClock) -0.035f else -0.02f,
        )

    override val root: View = rootFrame

    init {
        if (isLargeClock) {
            rootFrame.addView(
                timeView,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER,
                ),
            )
        } else {
            chipBackground.shape = GradientDrawable.RECTANGLE
            chipBackground.cornerRadius = context.dpF(16f)

            val chip =
                FrameLayout(context).apply {
                    setPadding(context.dp(12), context.dp(6), context.dp(12), context.dp(6))
                    background = chipBackground
                }

            chip.addView(
                timeView,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER,
                ),
            )

            rootFrame.addView(
                chip,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER,
                ),
            )
        }
    }

    override fun render(hour: String, minute: String, full: String, contentDescription: String?) {
        timeView.text = full
        root.contentDescription = contentDescription ?: full
        root.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
    }

    override fun applyColor(color: Int) {
        timeView.setTextColor(color)
        if (!isLargeClock) {
            chipBackground.setColor(withAlpha(color, 0.17f))
            chipBackground.setStroke(root.context.dp(1), withAlpha(color, 0.36f))
        }
    }

    override fun applyFontSize(fontSizePx: Float) {
        val size =
            if (isLargeClock) {
                (fontSizePx * 1.36f).coerceAtLeast(root.context.dpF(80f))
            } else {
                (fontSizePx * 0.78f).coerceAtLeast(root.context.dpF(24f))
            }
        timeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }
}

private class OneUI8StyleClockView(context: Context, private val isLargeClock: Boolean) : StyleClockView {
    private val rootFrame = FrameLayout(context)
    private val accentBackground = GradientDrawable()

    private var hourView: TextView? = null
    private var minuteView: TextView? = null
    private var fullView: TextView? = null

    override val root: View = rootFrame

    init {
        if (isLargeClock) {
            val row =
                LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                }

            hourView =
                buildText(
                    context = context,
                    sizePx = context.dpF(106f),
                    typeface = Typeface.create("sans-serif-medium", Typeface.BOLD),
                    letterSpacing = -0.035f,
                )

            val separator =
                buildText(
                    context = context,
                    sizePx = context.dpF(80f),
                    typeface = Typeface.create("sans-serif", Typeface.NORMAL),
                    letterSpacing = -0.015f,
                ).apply { text = ":" }

            minuteView =
                buildText(
                    context = context,
                    sizePx = context.dpF(106f),
                    typeface = Typeface.create("sans-serif", Typeface.NORMAL),
                    letterSpacing = -0.02f,
                )

            row.addView(hourView)
            row.addView(separator)
            row.addView(minuteView)

            rootFrame.addView(
                row,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER,
                ),
            )
        } else {
            val container =
                LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(context.dp(10), context.dp(6), context.dp(12), context.dp(6))
                }

            val accent =
                View(context).apply {
                    background = accentBackground
                }
            accentBackground.shape = GradientDrawable.RECTANGLE
            accentBackground.cornerRadius = context.dpF(2f)

            fullView =
                buildText(
                    context = context,
                    sizePx = context.dpF(30f),
                    typeface = Typeface.create("sans-serif-medium", Typeface.BOLD),
                    letterSpacing = -0.02f,
                )

            val accentLp = LinearLayout.LayoutParams(context.dp(3), context.dp(24)).apply {
                marginEnd = context.dp(8)
            }
            container.addView(accent, accentLp)
            container.addView(fullView)

            rootFrame.addView(
                container,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER,
                ),
            )
        }
    }

    override fun render(hour: String, minute: String, full: String, contentDescription: String?) {
        if (isLargeClock) {
            hourView?.text = hour
            minuteView?.text = minute
        } else {
            fullView?.text = full
        }

        root.contentDescription = contentDescription ?: full
        root.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
    }

    override fun applyColor(color: Int) {
        hourView?.setTextColor(color)
        minuteView?.setTextColor(color)
        fullView?.setTextColor(color)

        if (!isLargeClock) {
            accentBackground.setColor(color)
            (root as FrameLayout).background =
                GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = root.context.dpF(16f)
                    setColor(withAlpha(color, 0.17f))
                }
        }
    }

    override fun applyFontSize(fontSizePx: Float) {
        if (isLargeClock) {
            val hourSize = (fontSizePx * 1.30f).coerceAtLeast(root.context.dpF(74f))
            val minuteSize = (fontSizePx * 1.20f).coerceAtLeast(root.context.dpF(70f))
            hourView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, hourSize)
            minuteView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, minuteSize)
        } else {
            val fullSize = (fontSizePx * 0.75f).coerceAtLeast(root.context.dpF(23f))
            fullView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, fullSize)
        }
    }
}

private fun buildText(
    context: Context,
    sizePx: Float,
    typeface: Typeface,
    letterSpacing: Float,
): TextView {
    return TextView(context).apply {
        gravity = Gravity.CENTER
        setSingleLine(true)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, sizePx)
        this.typeface = typeface
        this.letterSpacing = letterSpacing
        includeFontPadding = false
    }
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
