package com.byme.app.ui.chat

import platform.Foundation.*

actual fun formatTime(timestamp: Long): String {
    val date = NSDate.dateWithTimeIntervalSince1970(timestamp.toDouble() / 1000.0)
    val now = NSDate.date()
    val diff = (now.timeIntervalSinceDate(date) * 1000).toLong()

    return when {
        diff < 60_000 -> "Ahora"
        diff < 3_600_000 -> "${diff / 60_000}m"
        diff < 86_400_000 -> "${diff / 3_600_000}h"
        else -> {
            val formatter = NSDateFormatter()
            formatter.dateFormat = "dd/MM"
            formatter.stringFromDate(date)
        }
    }
}