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
import android.icu.util.TimeZone
import com.android.systemui.customization.clocks.DigitalTimeFormatter
import com.android.systemui.customization.clocks.TimeKeeper
import com.android.systemui.plugins.keyguard.data.model.AlarmData
import com.android.systemui.plugins.keyguard.data.model.WeatherData
import com.android.systemui.plugins.keyguard.data.model.ZenData
import com.android.systemui.plugins.keyguard.ui.clocks.ClockConfig
import com.android.systemui.plugins.keyguard.ui.clocks.ClockController
import com.android.systemui.plugins.keyguard.ui.clocks.ClockEventListeners
import com.android.systemui.plugins.keyguard.ui.clocks.ClockEvents
import com.android.systemui.plugins.keyguard.ui.clocks.ClockSettings
import com.android.systemui.plugins.keyguard.ui.clocks.TimeFormatKind
import java.io.PrintWriter
import java.util.Locale

class CustomClockController(
    private val hostCtx: Context,
    private val pluginCtx: Context,
    private val settings: ClockSettings,
    private val style: ClockStyle,
    private val timeKeeper: TimeKeeper,
) : ClockController {
    private val hourFormatter = DigitalTimeFormatter("hh", timeKeeper)
    private val minuteFormatter = DigitalTimeFormatter("mm", timeKeeper)
    private val fullFormatter = DigitalTimeFormatter("hh:mm", timeKeeper, enableContentDescription = true)

    override val smallClock =
        CustomClockFaceController(
            hostCtx = hostCtx,
            pluginCtx = pluginCtx,
            settings = settings,
            style = style,
            hourFormatter = hourFormatter,
            minuteFormatter = minuteFormatter,
            fullFormatter = fullFormatter,
            timeKeeper = timeKeeper,
            isLargeClock = false,
        )

    override val largeClock =
        CustomClockFaceController(
            hostCtx = hostCtx,
            pluginCtx = pluginCtx,
            settings = settings,
            style = style,
            hourFormatter = hourFormatter,
            minuteFormatter = minuteFormatter,
            fullFormatter = fullFormatter,
            timeKeeper = timeKeeper,
            isLargeClock = true,
        )

    override val config =
        ClockConfig(
            id = style.id,
            name = pluginCtx.resources.getString(style.nameRes),
            description = pluginCtx.resources.getString(style.descriptionRes),
        )

    override val eventListeners = ClockEventListeners()

    override val events =
        object : ClockEvents {
            override var isReactiveTouchInteractionEnabled: Boolean = false

            override fun onTimeZoneChanged(timeZone: TimeZone) {
                timeKeeper.timeZone = timeZone
                refreshAllClocks()
            }

            override fun onTimeFormatChanged(formatKind: TimeFormatKind) {
                hourFormatter.formatKind = formatKind
                minuteFormatter.formatKind = formatKind
                fullFormatter.formatKind = formatKind
                refreshAllClocks()
            }

            override fun onLocaleChanged(locale: Locale) {
                hourFormatter.locale = locale
                minuteFormatter.locale = locale
                fullFormatter.locale = locale
                refreshAllClocks()
            }

            override fun onWeatherDataChanged(data: WeatherData) {}

            override fun onAlarmDataChanged(data: AlarmData) {}

            override fun onZenDataChanged(data: ZenData) {}
        }

    init {
        events.onTimeFormatChanged(TimeFormatKind.getFromContext(hostCtx))
        events.onLocaleChanged(Locale.getDefault())
        events.onTimeZoneChanged(TimeZone.getDefault())
    }

    override fun initialize(isDarkTheme: Boolean, dozeFraction: Float, foldFraction: Float) {
        smallClock.events.onThemeChanged(smallClock.theme.copy(isDarkTheme = isDarkTheme))
        smallClock.animations.doze(dozeFraction)
        smallClock.animations.fold(foldFraction)
        smallClock.events.onTimeTick()

        largeClock.events.onThemeChanged(largeClock.theme.copy(isDarkTheme = isDarkTheme))
        largeClock.animations.doze(dozeFraction)
        largeClock.animations.fold(foldFraction)
        largeClock.events.onTimeTick()
    }

    override fun dump(pw: PrintWriter) {
        pw.println("CustomClockController(style=${style.id})")
    }

    private fun refreshAllClocks() {
        smallClock.refreshTime()
        largeClock.refreshTime()
    }
}
