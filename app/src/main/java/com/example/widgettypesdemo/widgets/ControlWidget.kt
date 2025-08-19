package com.example.widgettypesdemo.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.RemoteViews
import android.widget.Toast
import com.example.widgettypesdemo.MainActivity
import com.example.widgettypesdemo.R
import com.example.widgettypesdemo.utils.WidgetUtils

/**
 * Control Widget - provides interactive buttons and toggles for quick actions
 */
class ControlWidget : BaseWidget() {
    
    companion object {
        const val ACTION_BUTTON_CLICK = "com.example.widgettypesdemo.BUTTON_CLICK"
        const val ACTION_TOGGLE_SWITCH = "com.example.widgettypesdemo.TOGGLE_SWITCH"
        const val ACTION_OPEN_APP = "com.example.widgettypesdemo.OPEN_APP"
        
        const val EXTRA_BUTTON_ID = "button_id"
        const val EXTRA_WIDGET_ID = "widget_id"
        
        const val BUTTON_ACTION = "action_button"
        const val BUTTON_SETTINGS = "settings_button"
        const val SWITCH_FEATURE = "feature_switch"
        
        private const val PREFS_NAME = "ControlWidgetPrefs"
        private const val PREF_SWITCH_STATE = "switch_state_"
    }
    
    override fun getWidgetType(): String = "CONTROL"
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateControlWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        when (intent.action) {
            ACTION_BUTTON_CLICK -> {
                val buttonId = intent.getStringExtra(EXTRA_BUTTON_ID)
                val widgetId = intent.getIntExtra(EXTRA_WIDGET_ID, -1)
                handleButtonClick(context, buttonId, widgetId)
            }
            ACTION_TOGGLE_SWITCH -> {
                val widgetId = intent.getIntExtra(EXTRA_WIDGET_ID, -1)
                handleToggleSwitch(context, widgetId)
            }
            ACTION_OPEN_APP -> {
                openMainApp(context)
            }
        }
    }
    
    private fun updateControlWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        try {
            val views = RemoteViews(context.packageName, R.layout.control_widget)
            
            // Set up button click handlers
            setupButtonClickHandler(context, views, BUTTON_ACTION, appWidgetId, R.id.btn_action)
            setupButtonClickHandler(context, views, BUTTON_SETTINGS, appWidgetId, R.id.btn_settings)
            
            // Set up toggle switch handler
            setupToggleHandler(context, views, appWidgetId)
            
            // Set up app open handler
            setupAppOpenHandler(context, views, appWidgetId)
            
            // Update switch state display
            updateSwitchState(context, views, appWidgetId)
            
            appWidgetManager.updateAppWidget(appWidgetId, views)
            
        } catch (e: Exception) {
            handleError(context, e)
            WidgetUtils.updateWidgetWithError(
                context,
                appWidgetManager,
                appWidgetId,
                R.layout.control_widget,
                R.id.widget_title,
                "Control Error"
            )
        }
    }
    
    private fun setupButtonClickHandler(
        context: Context,
        views: RemoteViews,
        buttonId: String,
        widgetId: Int,
        viewId: Int
    ) {
        val intent = Intent(context, ControlWidget::class.java).apply {
            action = ACTION_BUTTON_CLICK
            putExtra(EXTRA_BUTTON_ID, buttonId)
            putExtra(EXTRA_WIDGET_ID, widgetId)
        }
        val pendingIntent = WidgetUtils.createPendingIntent(context, intent, viewId + widgetId)
        views.setOnClickPendingIntent(viewId, pendingIntent)
    }
    
    private fun setupToggleHandler(context: Context, views: RemoteViews, widgetId: Int) {
        val intent = Intent(context, ControlWidget::class.java).apply {
            action = ACTION_TOGGLE_SWITCH
            putExtra(EXTRA_WIDGET_ID, widgetId)
        }
        val pendingIntent = WidgetUtils.createPendingIntent(context, intent, R.id.btn_toggle + widgetId)
        views.setOnClickPendingIntent(R.id.btn_toggle, pendingIntent)
    }
    
    private fun setupAppOpenHandler(context: Context, views: RemoteViews, widgetId: Int) {
        val intent = Intent(context, ControlWidget::class.java).apply {
            action = ACTION_OPEN_APP
        }
        val pendingIntent = WidgetUtils.createPendingIntent(context, intent, R.id.widget_title + widgetId)
        views.setOnClickPendingIntent(R.id.widget_title, pendingIntent)
    }
    
    private fun handleButtonClick(context: Context, buttonId: String?, widgetId: Int) {
        when (buttonId) {
            BUTTON_ACTION -> {
                Toast.makeText(context, "Action Button Clicked!", Toast.LENGTH_SHORT).show()
                // Perform action logic here
            }
            BUTTON_SETTINGS -> {
                Toast.makeText(context, "Settings Button Clicked!", Toast.LENGTH_SHORT).show()
                // Open settings or perform settings action
            }
        }
        
        // Update widget to reflect any state changes
        val appWidgetManager = AppWidgetManager.getInstance(context)
        updateControlWidget(context, appWidgetManager, widgetId)
    }
    
    private fun handleToggleSwitch(context: Context, widgetId: Int) {
        // Toggle the switch state
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentState = prefs.getBoolean(PREF_SWITCH_STATE + widgetId, false)
        val newState = !currentState
        
        prefs.edit().putBoolean(PREF_SWITCH_STATE + widgetId, newState).apply()
        
        Toast.makeText(
            context, 
            "Feature ${if (newState) "Enabled" else "Disabled"}", 
            Toast.LENGTH_SHORT
        ).show()
        
        // Update widget display
        val appWidgetManager = AppWidgetManager.getInstance(context)
        updateControlWidget(context, appWidgetManager, widgetId)
    }
    
    private fun updateSwitchState(context: Context, views: RemoteViews, widgetId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isEnabled = prefs.getBoolean(PREF_SWITCH_STATE + widgetId, false)
        
        // Update toggle button text and appearance
        views.setTextViewText(
            R.id.btn_toggle, 
            if (isEnabled) "ON" else "OFF"
        )
        
        // Update status text
        views.setTextViewText(
            R.id.status_text,
            "Feature: ${if (isEnabled) "Enabled" else "Disabled"}"
        )
    }
    
    private fun openMainApp(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}