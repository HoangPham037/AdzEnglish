package com.adzenglish.adzenglish.base.media

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import androidx.lifecycle.MutableLiveData
import com.adzenglish.adzenglish.utils.Constants
import java.io.IOException


open class MediaManager {
    val STATE_IDLE: Int = 1
    val STATE_PLAYING: Int = 2
    val STATE_PAUSED: Int = 3
    var statePlay = STATE_IDLE

    companion object {
        var state: MutableLiveData<Int> = MutableLiveData()
        private var player: MediaPlayer? = null
        private var instance: MediaManager? = null
        fun getInstance(): MediaManager? {
            if (instance == null) {
                instance = MediaManager()
                player?.setOnCompletionListener {
                    state.postValue(Constants.INDEX_1)
                }
            }
            return instance
        }
    }

    init {
        player = MediaPlayer()
    }

    open fun playWithPath(path: String) {
        player?.reset()
        try {
            player?.setDataSource(path)
            player?.prepareAsync()
            player?.setOnPreparedListener {
                it.start()
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (p: IllegalStateException) {
            p.printStackTrace()
        }
    }

    fun playSoundPath(path: String, event: (Int) -> Unit) {
        when (statePlay) {
            STATE_IDLE -> {
                player?.stop()
                player?.reset()
                try {
                    player?.setDataSource(path)
                    player?.prepareAsync()
                    player?.setOnPreparedListener {
                        it.start()
                        statePlay = STATE_PLAYING
                    }
                } catch (e: IOException) {
                    throw RuntimeException(e)
                } catch (p: IllegalStateException) {
                    p.printStackTrace()
                }
            }

            STATE_PLAYING -> {
                player?.pause()
                statePlay = STATE_PAUSED
            }

            STATE_PAUSED -> {
                player?.start()
                statePlay = STATE_PLAYING
            }
        }
        event.invoke(statePlay)
    }

    open fun playAssetSound(context: Context, soundFileName: String) {
        player?.reset()
        try {
            val descriptor: AssetFileDescriptor = context.assets.openFd(soundFileName)
            player?.setDataSource(
                descriptor.fileDescriptor,
                descriptor.startOffset,
                descriptor.length
            )
            descriptor.close()
            player?.prepare()
            player?.start()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun setSpeedAudio(playbackSpeed: Float) {
        if (player?.isPlaying == true) player?.playbackParams?.setSpeed(playbackSpeed)
            ?.let { player?.setPlaybackParams(it) }
    }

    fun setSeekTo() {
        player?.let {
            val targetPosition = it.currentPosition - 5000
            it.seekTo(targetPosition)
        }
    }

    fun setSate() {
        statePlay = STATE_IDLE
    }

    fun pause() {
        player?.let {
            if (it.isPlaying) {
                it.stop()
                statePlay = STATE_PAUSED
            }
        }
    }

    fun playSound(path: String, event: (Int) -> Unit) {
        player?.stop()
        player?.reset()
        try {
            player?.setDataSource(path)
            player?.prepareAsync()
            player?.setOnPreparedListener {
                it.start()
                statePlay = STATE_PLAYING
                event.invoke(statePlay)
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (p: IllegalStateException) {
            p.printStackTrace()
        }
    }
}