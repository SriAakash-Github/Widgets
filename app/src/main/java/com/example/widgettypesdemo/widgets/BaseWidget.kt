package com.example.widgettypesdemo.widgets

import android.appwidget.AppWidgetProvider
import android.content.Context

/**
 * Base class for all widget types providing common functionality
 */
abstract class BaseWidget : AppWidgetProvider() {
    
    companion object {
        const val WIDGET_UPDATE_ACTION = "com.example.widgettypesdemo.WIDGET_UPDATE"
        const val WIDGET_CLICK_ACTION = "com.example.widgettypesdemo.WIDGET_CLICK"
    }
    
    /**
     * Get the widget type identifier
     */
    abstract fun getWidgetType(): String
    
    /**
     * Handle widget-specific error states
     */
    protected open fun handleError(context: Context, error: Exception) {
        // Default error handling - can be overridden by specific widgets
    }
}