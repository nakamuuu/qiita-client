package net.divlight.qiita.ui.common

import android.content.Context
import net.divlight.qiita.R
import java.util.*
import java.util.concurrent.TimeUnit

class DateDiffStringGenerator(val context: Context, val date: Date) {
    companion object {
        private val ONE_DAY_IN_MILLS = TimeUnit.DAYS.toMillis(1)
        private val ONE_HOUR_IN_MILLS = TimeUnit.HOURS.toMillis(1)
        private val ONE_MINUTE_IN_MILLS = TimeUnit.MINUTES.toMillis(1)
    }

    fun toCreatedAtDiffString(): String {
        val diff = Date().time - date.time
        return when {
            (diff > ONE_DAY_IN_MILLS) -> {
                context.getString(
                    R.string.item_created_at_days_format,
                    (diff / ONE_DAY_IN_MILLS).toInt()
                )
            }
            (diff > ONE_HOUR_IN_MILLS) -> {
                context.getString(
                    R.string.item_created_at_hours_format,
                    (diff / ONE_HOUR_IN_MILLS).toInt()
                )
            }
            (diff > ONE_MINUTE_IN_MILLS) -> {
                context.getString(
                    R.string.item_created_at_minutes_format,
                    (diff / ONE_MINUTE_IN_MILLS).toInt()
                )
            }
            else -> context.getString(R.string.item_created_at_just_now)
        }
    }
}