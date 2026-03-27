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
import com.android.internal.annotations.Keep
import com.android.systemui.customization.clocks.TimeKeeperImpl
import com.android.systemui.plugins.annotations.Requires
import com.android.systemui.plugins.keyguard.ui.clocks.ClockController
import com.android.systemui.plugins.keyguard.ui.clocks.ClockMessageBuffers
import com.android.systemui.plugins.keyguard.ui.clocks.ClockPickerConfig
import com.android.systemui.plugins.keyguard.ui.clocks.ClockProviderPlugin
import com.android.systemui.plugins.keyguard.ui.clocks.ClockSettings

@Keep // Proguard should not remove this class as it's the plugin entrypoint.
@Requires(target = ClockProviderPlugin::class, version = ClockProviderPlugin.VERSION)
class CustomClockProvider : ClockProviderPlugin {
    private lateinit var pluginCtx: Context

    override fun onCreate(hostCtx: Context, pluginCtx: Context) {
        this.pluginCtx = pluginCtx
    }

    override fun initialize(buffers: ClockMessageBuffers?) {}

    override fun getClocks() = ClockStyle.getMetadata()

    override fun createClock(ctx: Context, settings: ClockSettings): ClockController {
        val style =
            ClockStyle.fromId(settings.clockId)
                ?: throw IllegalArgumentException("${settings.clockId} unsupported by this provider")

        return CustomClockController(
            hostCtx = ctx,
            pluginCtx = pluginCtx,
            settings = settings,
            style = style,
            timeKeeper = TimeKeeperImpl(),
        )
    }

    override fun getClockPickerConfig(settings: ClockSettings): ClockPickerConfig {
        val style =
            ClockStyle.fromId(settings.clockId)
                ?: throw IllegalArgumentException("${settings.clockId} unsupported by this provider")

        val res = pluginCtx.resources
        return ClockPickerConfig(
            id = style.id,
            name = res.getString(style.nameRes),
            description = res.getString(style.descriptionRes),
            thumbnail = res.getDrawable(style.thumbnailRes, null),
        )
    }
}
