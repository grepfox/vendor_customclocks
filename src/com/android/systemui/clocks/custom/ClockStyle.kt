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

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.android.systemui.plugins.keyguard.ui.clocks.ClockMetadata

enum class ClockStyle(
    val id: String,
    @StringRes val nameRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val thumbnailRes: Int,
) {
    OXYGENOS(
        id = "CUSTOM_CLOCK_OXYGENOS",
        nameRes = R.string.custom_clock_oxygenos_name,
        descriptionRes = R.string.custom_clock_oxygenos_description,
        thumbnailRes = R.drawable.oxygen_clock_thumbnail,
    ),
    IOS26(
        id = "CUSTOM_CLOCK_IOS26",
        nameRes = R.string.custom_clock_ios26_name,
        descriptionRes = R.string.custom_clock_ios26_description,
        thumbnailRes = R.drawable.ios26_clock_thumbnail,
    ),
    ONEUI8(
        id = "CUSTOM_CLOCK_ONEUI8",
        nameRes = R.string.custom_clock_oneui8_name,
        descriptionRes = R.string.custom_clock_oneui8_description,
        thumbnailRes = R.drawable.oneui8_clock_thumbnail,
    );

    companion object {
        fun getMetadata(): List<ClockMetadata> = values().map { ClockMetadata(it.id) }

        fun fromId(clockId: String?): ClockStyle? = values().firstOrNull { it.id == clockId }
    }
}
