package com.mammoth.androidlab.media

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class RadioPlayer(private val context: Context) {
    private var player: ExoPlayer? = null
    private var listener: Player.Listener? = null
    fun playRadio(url: String) {
        if (player == null) {
            player = ExoPlayer.Builder(context).build()
        }
        player?.let {
            if (it.isPlaying) it.stop()
            val mediaItem = MediaItem.fromUri(url)
            it.setMediaItem(mediaItem)
            it.prepare()
            it.play()
        }
    }

    // Play or pause the music
    fun playPause() {
        player?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }

    fun release() {
        player?.release()
        player = null
    }

    fun getPlayWhenReady(): Boolean {
        return player?.playWhenReady?:false
    }

    fun isPlaying(): Boolean {
        return player?.isPlaying?:false
    }

    fun addListener(listener: Player.Listener) {
        this.listener = listener
        player?.addListener(listener)
    }

    fun removeListener() {
        listener?.let {
            player?.removeListener(it)
        }
    }
}