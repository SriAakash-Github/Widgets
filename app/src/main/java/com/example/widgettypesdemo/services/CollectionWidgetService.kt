package com.example.widgettypesdemo.services

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.widgettypesdemo.R
import com.example.widgettypesdemo.widgets.CollectionWidget

/**
 * RemoteViewsService for Collection Widget
 */
class CollectionWidgetService : RemoteViewsService() {
    
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return CollectionRemoteViewsFactory(this.applicationContext, intent)
    }
}

/**
 * RemoteViewsFactory for creating collection items
 */
class CollectionRemoteViewsFactory(
    private val context: Context,
    intent: Intent
) : RemoteViewsService.RemoteViewsFactory {
    
    private val appWidgetId = intent.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    )
    
    private val items = mutableListOf<CollectionItem>()
    
    data class CollectionItem(
        val id: String,
        val title: String,
        val subtitle: String,
        val iconRes: Int = android.R.drawable.ic_menu_info_details
    )
    
    override fun onCreate() {
        // Initialize data
        loadData()
    }
    
    override fun onDataSetChanged() {
        // Reload data when requested
        loadData()
    }
    
    override fun onDestroy() {
        items.clear()
    }
    
    override fun getCount(): Int = items.size
    
    override fun getViewAt(position: Int): RemoteViews? {
        if (position >= items.size) return null
        
        val item = items[position]
        val views = RemoteViews(context.packageName, R.layout.collection_item)
        
        // Set item data
        views.setTextViewText(R.id.item_title, item.title)
        views.setTextViewText(R.id.item_subtitle, item.subtitle)
        views.setImageViewResource(R.id.item_icon, item.iconRes)
        
        // Set up click intent for this item
        val clickIntent = Intent().apply {
            putExtra(CollectionWidget.EXTRA_ITEM_ID, item.id)
        }
        views.setOnClickFillInIntent(R.id.item_container, clickIntent)
        
        return views
    }
    
    override fun getLoadingView(): RemoteViews? {
        // Return a loading view while data is being fetched
        val views = RemoteViews(context.packageName, R.layout.collection_item)
        views.setTextViewText(R.id.item_title, "Loading...")
        views.setTextViewText(R.id.item_subtitle, "Please wait")
        views.setImageViewResource(R.id.item_icon, android.R.drawable.ic_popup_sync)
        return views
    }
    
    override fun getViewTypeCount(): Int = 1
    
    override fun getItemId(position: Int): Long = position.toLong()
    
    override fun hasStableIds(): Boolean = true
    
    private fun loadData() {
        items.clear()
        
        // Sample data - in a real app, this would come from a database or API
        val sampleItems = listOf(
            CollectionItem("1", "Item One", "First collection item", android.R.drawable.ic_menu_agenda),
            CollectionItem("2", "Item Two", "Second collection item", android.R.drawable.ic_menu_call),
            CollectionItem("3", "Item Three", "Third collection item", android.R.drawable.ic_menu_camera),
            CollectionItem("4", "Item Four", "Fourth collection item", android.R.drawable.ic_menu_compass),
            CollectionItem("5", "Item Five", "Fifth collection item", android.R.drawable.ic_menu_edit),
            CollectionItem("6", "Item Six", "Sixth collection item", android.R.drawable.ic_menu_gallery),
            CollectionItem("7", "Item Seven", "Seventh collection item", android.R.drawable.ic_menu_help),
            CollectionItem("8", "Item Eight", "Eighth collection item", android.R.drawable.ic_menu_info_details)
        )
        
        items.addAll(sampleItems)
    }
}