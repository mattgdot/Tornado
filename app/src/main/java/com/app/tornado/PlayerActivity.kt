package com.app.tornado

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import com.app.tornado.databinding.ActivityPlayerBinding
import com.github.se_bastiaan.torrentstream.StreamStatus
import com.github.se_bastiaan.torrentstream.Torrent
import com.github.se_bastiaan.torrentstream.TorrentOptions
import com.github.se_bastiaan.torrentstream.TorrentStream
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener
import es.dmoral.toasty.Toasty
import tcking.github.com.giraffeplayer2.VideoInfo
import java.io.File
import java.lang.Exception

@UnstableApi
class PlayerActivity:AppCompatActivity(), TorrentListener {
    private lateinit var binding: ActivityPlayerBinding
    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var mediaItemIndex = 0
    private var playbackPosition = 0L
    private var torrentStream:TorrentStream?=null

    private fun initializePlayer(url:String) {
        binding.pbLoading.isVisible=false
        binding.videoView.isVisible=true

        val videoInfo = VideoInfo(url)
            .setShowTopBar(true)
            .setAspectRatio(VideoInfo.AR_ASPECT_FILL_PARENT)
            .setBgColor(Color.BLACK)

        binding.videoView.videoInfo(videoInfo).player.start()
    }

    private fun torrentSpecific(link: String) {
        val storeLocation = File(externalCacheDir,"downloads")

        val torrentOptions = TorrentOptions.Builder()
            .saveLocation(storeLocation)
            .anonymousMode(true)
            .build()

        torrentStream = TorrentStream.init(torrentOptions)
        torrentStream?.addListener(this)
        torrentStream?.startStream(link)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        binding.videoView.isVisible=false
        val magnetLink = intent.getStringExtra(PARAM_MAGNET_LINK)

        if (magnetLink != null) {
            Toasty.info(this, "Attempting to connect, this might take a while")
                .show()

            torrentSpecific(magnetLink)
        }
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.videoView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            mediaItemIndex = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        player = null
    }


    public override fun onResume() {
        super.onResume()
        hideSystemUi()
    }

    override fun onStreamPrepared(torrent: Torrent?) {
        Toasty.info(this, "Successfully connected, movie will be ready soon")
            .show()
    }

    override fun onStreamStarted(torrent: Torrent?) {

    }

    override fun onStreamError(torrent: Torrent?, e: Exception?) {
        Toasty.error(this, e.toString()).show()
    }

    override fun onStreamReady(torrent: Torrent?) {
        if (torrent != null) {
            initializePlayer(torrent.videoFile.path)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onStreamProgress(torrent: Torrent?, status: StreamStatus?) {
        binding.tvStatus.text="${"%.2f".format(status?.progress)}%"
    }

    override fun onStreamStopped() {

    }
//    public override fun onPause() {
//        super.onPause()
//        if (Util.SDK_INT <= 23) {
//            releasePlayer()
//        }
//    }
//
    public override fun onStop() {
        super.onStop()
        releasePlayer()

    }

    companion object {
        const val PARAM_MAGNET_LINK = "PARAM_MAGNET_LINK"
    }

}