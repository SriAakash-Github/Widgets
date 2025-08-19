package com.example.widgettypesdemo.utils

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for common widget operations
 */
object WidgetUtils {
    
    /**
     * Create a PendingIntent with appropriate flags for different Android versions
     */
    fun createPendingIntent(context: Context, intent: Intent, requestCode: Int): PendingIntent {
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getBroadcast(context, requestCode, intent, flags)
    }
    
    /**
     * Format current date and time for display
     */
    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy\nHH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }
    
    /**
     * Get current time only
     */
    fun getCurrentTime(): String {
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return timeFormat.format(Date())
    }
    
    /**
     * Get current date only
     */
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }
    
    /**
     * Update widget with error state
     */
    fun updateWidgetWithError(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        layoutId: Int,
        textViewId: Int,
        errorMessage: String
    ) {
        val views = RemoteViews(context.packageName, layoutId)
        views.setTextViewText(textViewId, errorMessage)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}