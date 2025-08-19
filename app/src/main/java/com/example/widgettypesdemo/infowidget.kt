package com.example.widgettypesdemo

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews
import com.example.widgettypesdemo.utils.WidgetUtils
import com.example.widgettypesdemo.widgets.BaseWidget

/**
 * Information Widget - displays real-time date and time information
 */
class InfoWidget : BaseWidget() {
    
    companion object {
        private const val UPDATE_INTERVAL = 1000L // Update every second
        const val ACTION_UPDATE_TIME = "com.example.widgettypesdemo.UPDATE_TIME"
        const val ACTION_WIDGET_CLICK = "com.example.widgettypesdemo.WIDGET_CLICK"
    }
    
    override fun getWidgetType(): String = "INFORMATION"
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Update all active widgets
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        
        // Set up periodic updates
        setupPeriodicUpdates(context)
    }

    override fun onEnabled(context: Context) {
        // Start periodic updates when first widget is created
        setupPeriodicUpdates(context)
    }

    override fun onDisabled(context: Context) {
        // Cancel periodic updates when last widget is removed
        cancelPeriodicUpdates(context)
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        when (intent.action) {
            ACTION_UPDATE_TIME -> {
                // Handle time update
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    android.content.ComponentName(context, InfoWidget::class.java)
                )
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
            ACTION_WIDGET_CLICK -> {
                // Handle widget click - open main activity
                val mainIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(mainIntent)
            }
        }
    }
    
    private fun setupPeriodicUpdates(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, InfoWidget::class.java).apply {
            action = ACTION_UPDATE_TIME
        }
        val pendingIntent = WidgetUtils.createPendingIntent(context, intent, 0)
        
        // Set repeating alarm for updates
        alarmManager.setRepeating(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + UPDATE_INTERVAL,
            UPDATE_INTERVAL,
            pendingIntent
        )
    }
    
    private fun cancelPeriodicUpdates(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, InfoWidget::class.java).apply {
            action = ACTION_UPDATE_TIME
        }
        val pendingIntent = WidgetUtils.createPendingIntent(context, intent, 0)
        alarmManager.cancel(pendingIntent)
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    try {
        // Get current date and time
        val dateTimeText = WidgetUtils.getCurrentDateTime()
        
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.infowidget)
        views.setTextViewText(R.id.appwidget_text, dateTimeText)
        
        // Set up click handler to open main activity
        val clickIntent = Intent(context, InfoWidget::class.java).apply {
            action = InfoWidget.ACTION_WIDGET_CLICK
        }
        val clickPendingIntent = WidgetUtils.createPendingIntent(context, clickIntent, appWidgetId)
        views.setOnClickPendingIntent(R.id.appwidget_text, clickPendingIntent)
        
        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
        
    } catch (e: Exception) {
        // Handle error by showing fallback content
        WidgetUtils.updateWidgetWithError(
            context,
            appWidgetManager,
            appWidgetId,
            R.layout.infowidget,
            R.id.appwidget_text,
            "Unable to load time"
        )
    }
}