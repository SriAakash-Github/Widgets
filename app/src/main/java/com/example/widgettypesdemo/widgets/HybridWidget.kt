package com.example.widgettypesdemo.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.RemoteViews
import com.example.widgettypesdemo.MainActivity
import com.example.widgettypesdemo.R
import com.example.widgettypesdemo.utils.WidgetUtils

/**
 * Hybrid Widget - combines information display with interactive controls
 * Simulates a media player with song info and playback controls
 */
class HybridWidget : BaseWidget() {
    
    companion object {
        const val ACTION_PLAY_PAUSE = "com.example.widgettypesdemo.PLAY_PAUSE"
        const val ACTION_PREVIOUS = "com.example.widgettypesdemo.PREVIOUS"
        const val ACTION_NEXT = "com.example.widgettypesdemo.NEXT"
        const val ACTION_OPEN_PLAYER = "com.example.widgettypesdemo.OPEN_PLAYER"
        
        const val EXTRA_WIDGET_ID = "widget_id"
        
        private const val PREFS_NAME = "HybridWidgetPrefs"
        private const val PREF_IS_PLAYING = "is_playing_"
        private const val PREF_CURRENT_SONG = "current_song_"
        private const val PREF_CURRENT_ARTIST = "current_artist_"
        
        // Sample playlist
        private val PLAYLIST = listOf(
            MediaTrack("Awesome Song", "Great Artist"),
            MediaTrack("Cool Track", "Amazing Band"),
            MediaTrack("Best Hit", "Top Singer"),
            MediaTrack("New Song", "Popular Artist"),
            MediaTrack("Classic Tune", "Legend Band")
        )
    }
    
    data class MediaTrack(val title: String, val artist: String)
    
    override fun getWidgetType(): String = "HYBRID"
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateHybridWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        val widgetId = intent.getIntExtra(EXTRA_WIDGET_ID, -1)
        
        when (intent.action) {
            ACTION_PLAY_PAUSE -> handlePlayPause(context, widgetId)
            ACTION_PREVIOUS -> handlePrevious(context, widgetId)
            ACTION_NEXT -> handleNext(context, widgetId)
            ACTION_OPEN_PLAYER -> openPlayerApp(context)
        }
    }
    
    private fun updateHybridWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        try {
            val views = RemoteViews(context.packageName, R.layout.hybrid_widget)
            
            // Update media information
            updateMediaInfo(context, views, appWidgetId)
            
            // Set up control button handlers
            setupControlButtons(context, views, appWidgetId)
            
            // Set up player open handler
            setupPlayerOpenHandler(context, views)
            
            appWidgetManager.updateAppWidget(appWidgetId, views)
            
        } catch (e: Exception) {
            handleError(context, e)
            WidgetUtils.updateWidgetWithError(
                context,
                appWidgetManager,
                appWidgetId,
                R.layout.hybrid_widget,
                R.id.song_title,
                "Player Error"
            )
        }
    }
    
    private fun updateMediaInfo(context: Context, views: RemoteViews, widgetId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        // Get current media state
        val isPlaying = prefs.getBoolean(PREF_IS_PLAYING + widgetId, false)
        val songTitle = prefs.getString(PREF_CURRENT_SONG + widgetId, PLAYLIST[0].title) ?: PLAYLIST[0].title
        val artist = prefs.getString(PREF_CURRENT_ARTIST + widgetId, PLAYLIST[0].artist) ?: PLAYLIST[0].artist
        
        // Update display
        views.setTextViewText(R.id.song_title, songTitle)
        views.setTextViewText(R.id.artist_name, artist)
        views.setTextViewText(R.id.playback_status, if (isPlaying) "♪ Playing" else "⏸ Paused")
        
        // Update play/pause button
        views.setTextViewText(R.id.btn_play_pause, if (isPlaying) "⏸" else "▶")
        
        // Update progress indicator (simulated)
        val progress = if (isPlaying) "●●●○○" else "○○○○○"
        views.setTextViewText(R.id.progress_indicator, progress)
    }
    
    private fun setupControlButtons(context: Context, views: RemoteViews, widgetId: Int) {
        // Play/Pause button
        val playPauseIntent = Intent(context, HybridWidget::class.java).apply {
            action = ACTION_PLAY_PAUSE
            putExtra(EXTRA_WIDGET_ID, widgetId)
        }
        val playPausePendingIntent = WidgetUtils.createPendingIntent(
            context, playPauseIntent, R.id.btn_play_pause + widgetId
        )
        views.setOnClickPendingIntent(R.id.btn_play_pause, playPausePendingIntent)
        
        // Previous button
        val previousIntent = Intent(context, HybridWidget::class.java).apply {
            action = ACTION_PREVIOUS
            putExtra(EXTRA_WIDGET_ID, widgetId)
        }
        val previousPendingIntent = WidgetUtils.createPendingIntent(
            context, previousIntent, R.id.btn_previous + widgetId
        )
        views.setOnClickPendingIntent(R.id.btn_previous, previousPendingIntent)
        
        // Next button
        val nextIntent = Intent(context, HybridWidget::class.java).apply {
            action = ACTION_NEXT
            putExtra(EXTRA_WIDGET_ID, widgetId)
        }
        val nextPendingIntent = WidgetUtils.createPendingIntent(
            context, nextIntent, R.id.btn_next + widgetId
        )
        views.setOnClickPendingIntent(R.id.btn_next, nextPendingIntent)
    }
    
    private fun setupPlayerOpenHandler(context: Context, views: RemoteViews) {
        val openIntent = Intent(context, HybridWidget::class.java).apply {
            action = ACTION_OPEN_PLAYER
        }
        val openPendingIntent = WidgetUtils.createPendingIntent(context, openIntent, 0)
        views.setOnClickPendingIntent(R.id.media_info_container, openPendingIntent)
    }
    
    private fun handlePlayPause(context: Context, widgetId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isPlaying = prefs.getBoolean(PREF_IS_PLAYING + widgetId, false)
        
        // Toggle play state
        prefs.edit().putBoolean(PREF_IS_PLAYING + widgetId, !isPlaying).apply()
        
        // Update widget
        updateWidget(context, widgetId)
    }
    
    private fun handlePrevious(context: Context, widgetId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentSong = prefs.getString(PREF_CURRENT_SONG + widgetId, PLAYLIST[0].title)
        
        // Find current song index and go to previous
        val currentIndex = PLAYLIST.indexOfFirst { it.title == currentSong }
        val previousIndex = if (currentIndex > 0) currentIndex - 1 else PLAYLIST.size - 1
        val previousTrack = PLAYLIST[previousIndex]
        
        // Update stored track
        prefs.edit()
            .putString(PREF_CURRENT_SONG + widgetId, previousTrack.title)
            .putString(PREF_CURRENT_ARTIST + widgetId, previousTrack.artist)
            .apply()
        
        // Update widget
        updateWidget(context, widgetId)
    }
    
    private fun handleNext(context: Context, widgetId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentSong = prefs.getString(PREF_CURRENT_SONG + widgetId, PLAYLIST[0].title)
        
        // Find current song index and go to next
        val currentIndex = PLAYLIST.indexOfFirst { it.title == currentSong }
        val nextIndex = if (currentIndex < PLAYLIST.size - 1) currentIndex + 1 else 0
        val nextTrack = PLAYLIST[nextIndex]
        
        // Update stored track
        prefs.edit()
            .putString(PREF_CURRENT_SONG + widgetId, nextTrack.title)
            .putString(PREF_CURRENT_ARTIST + widgetId, nextTrack.artist)
            .apply()
        
        // Update widget
        updateWidget(context, widgetId)
    }
    
    private fun openPlayerApp(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("open_player", true)
        }
        context.startActivity(intent)
    }
    
    private fun updateWidget(context: Context, widgetId: Int) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        updateHybridWidget(context, appWidgetManager, widgetId)
    }
}