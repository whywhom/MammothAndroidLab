package com.mammoth.androidlab.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.Player
import com.mammoth.androidlab.media.RadioPlayer

@SuppressLint("StaticFieldLeak")
class MediaPlayerViewModel(
    private val context: Context,
    private val radioPlayer: RadioPlayer,
) : ViewModel() {
    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val playListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
            // This is triggered whenever the playing state changes
            if (isPlaying) {
                // The media is now playing
                println("Playback started")
            } else {
                // The media is paused or stopped
                println("Playback paused")
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            // You can also listen to detailed playback state changes
            when (playbackState) {
                Player.STATE_IDLE -> {
                    // The player is idle (not prepared yet)
                }
                Player.STATE_BUFFERING -> {
                    // The player is buffering (loading data)
                }
                Player.STATE_READY -> {
                    // The player is ready to play
                }
                Player.STATE_ENDED -> {
                    // The playback has finished
                    println("Playback ended")
                }
            }
        }
    }

    fun addListener() {
        radioPlayer.addListener(playListener)
    }

    fun gerPlayer(): RadioPlayer {
        return radioPlayer
    }

    fun playOrPause() {
        radioPlayer.playPause()
    }

    fun isPlaying(): Boolean {
        return radioPlayer.isPlaying()
    }

    fun playRadio(url: String) {
        radioPlayer.playRadio(url)
        radioPlayer.addListener(playListener)
    }

    fun playPause() {
        radioPlayer.playPause()
    }

    fun playerRelease() {
        radioPlayer.release()
        radioPlayer.removeListener()
    }

    // Release the player when the ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        radioPlayer.release()
    }

    fun onNext() {
        // nothing to do
    }
}