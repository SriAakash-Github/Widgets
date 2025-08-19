package com.example.widgettypesdemo.models

/**
 * Data models for widget state management
 */

/**
 * Represents the state of a widget instance
 */
data class WidgetState(
    val widgetId: Int,
    val widgetType: WidgetType,
    val lastUpdate: Long,
    val isActive: Boolean,
    val errorState: String? = null
)

/**
 * Enum representing different widget types
 */
enum class WidgetType {
    INFORMATION,
    CONTROL,
    COLLECTION,
    HYBRID
}

/**
 * Represents media playback state for hybrid widget
 */
data class MediaState(
    val songTitle: String,
    val artist: String,
    val isPlaying: Boolean,
    val position: Int = 0,
    val duration: Int = 100,
    val playlistIndex: Int = 0
) {
    fun getProgressPercentage(): Int {
        return if (duration > 0) (position * 100) / duration else 0
    }
}

/**
 * Represents an item in a collection widget
 */
data class CollectionItem(
    val id: String,
    val title: String,
    val subtitle: String?,
    val iconRes: Int? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Represents control widget state
 */
data class ControlState(
    val widgetId: Int,
    val isFeatureEnabled: Boolean = false,
    val lastActionTime: Long = 0,
    val actionCount: Int = 0
)

/**
 * Widget configuration data
 */
data class WidgetConfig(
    val widgetId: Int,
    val widgetType: WidgetType,
    val updateInterval: Long = 60000L, // Default 1 minute
    val isAutoUpdateEnabled: Boolean = true,
    val customSettings: Map<String, Any> = emptyMap()
)