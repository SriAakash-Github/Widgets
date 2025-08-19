package com.example.widgettypesdemo.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.example.widgettypesdemo.MainActivity
import com.example.widgettypesdemo.R
import com.example.widgettypesdemo.services.CollectionWidgetService
import com.example.widgettypesdemo.utils.WidgetUtils

/**
 * Collection Widget - displays a scrollable list of items
 */
class CollectionWidget : BaseWidget() {
    
    companion object {
        const val ACTION_ITEM_CLICK = "com.example.widgettypesdemo.ITEM_CLICK"
        const val ACTION_REFRESH = "com.example.widgettypesdemo.REFRESH_COLLECTION"
        const val EXTRA_ITEM_ID = "item_id"
        const val EXTRA_WIDGET_ID = "widget_id"
    }
    
    override fun getWidgetType(): String = "COLLECTION"
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateCollectionWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        when (intent.action) {
            ACTION_ITEM_CLICK -> {
                val itemId = intent.getStringExtra(EXTRA_ITEM_ID)
                handleItemClick(context, itemId)
            }
            ACTION_REFRESH -> {
                val widgetId = intent.getIntExtra(EXTRA_WIDGET_ID, -1)
                refreshWidget(context, widgetId)
            }
        }
    }
    
    private fun updateCollectionWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        try {
            val views = RemoteViews(context.packageName, R.layout.collection_widget)
            
            // Set up the collection view with RemoteViewsService
            val serviceIntent = Intent(context, CollectionWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }
            
            views.setRemoteAdapter(R.id.collection_list, serviceIntent)
            views.setEmptyView(R.id.collection_list, R.id.empty_view)
            
            // Set up item click template
            val itemClickIntent = Intent(context, CollectionWidget::class.java).apply {
                action = ACTION_ITEM_CLICK
            }
            val itemClickPendingIntent = WidgetUtils.createPendingIntent(
                context, 
                itemClickIntent, 
                appWidgetId
            )
            views.setPendingIntentTemplate(R.id.collection_list, itemClickPendingIntent)
            
            // Set up refresh button
            val refreshIntent = Intent(context, CollectionWidget::class.java).apply {
                action = ACTION_REFRESH
                putExtra(EXTRA_WIDGET_ID, appWidgetId)
            }
            val refreshPendingIntent = WidgetUtils.createPendingIntent(
                context, 
                refreshIntent, 
                R.id.btn_refresh + appWidgetId
            )
            views.setOnClickPendingIntent(R.id.btn_refresh, refreshPendingIntent)
            
            // Set up title click to open app
            val titleClickIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val titleClickPendingIntent = WidgetUtils.createPendingIntent(
                context, 
                titleClickIntent, 
                R.id.widget_title + appWidgetId
            )
            views.setOnClickPendingIntent(R.id.widget_title, titleClickPendingIntent)
            
            appWidgetManager.updateAppWidget(appWidgetId, views)
            
        } catch (e: Exception) {
            handleError(context, e)
            WidgetUtils.updateWidgetWithError(
                context,
                appWidgetManager,
                appWidgetId,
                R.layout.collection_widget,
                R.id.widget_title,
                "Collection Error"
            )
        }
    }
    
    private fun handleItemClick(context: Context, itemId: String?) {
        // Handle item click - could open detailed view or perform action
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("selected_item", itemId)
        }
        context.startActivity(intent)
    }
    
    private fun refreshWidget(context: Context, widgetId: Int) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        
        // Notify the collection that data has changed
        appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.collection_list)
        
        // Update the widget
        updateCollectionWidget(context, appWidgetManager, widgetId)
    }
}