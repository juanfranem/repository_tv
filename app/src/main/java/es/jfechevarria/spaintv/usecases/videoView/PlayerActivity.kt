package es.jfechevarria.spaintv.usecases.videoView

import android.content.ContentResolver
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProviders
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.google.android.gms.common.images.WebImage
import es.jfechevarria.spaintv.R
import es.jfechevarria.spaintv.databinding.ActivityPlayerBinding
import es.jfechevarria.spaintv.usecases.ExpandedControlsActivity

class PlayerActivity: AppCompatActivity() {

    private val TAG = "JFEM"
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var viewModel: PlayerViewModel
    private var castContext: CastContext? = null
    private var castSession: CastSession? = null
    private lateinit var sessionManagerListener: SessionManagerListener<CastSession>

    private lateinit var player: SimpleExoPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setupViewModel()
        setupCasting()
        setupCastListener()
        setContentView(binding.root)
        setupPlayer()
        setupActionBar()
        play()
        setupPubli()
    }

    private fun setupPubli() {
        val adRequest = AdRequest.Builder().build()
        binding.adview.loadAd(adRequest)
    }

    private fun setupPlayer() {
        player = SimpleExoPlayer.Builder(this).build()
        player.addListener(object: Player.EventListener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {

            }

            override fun onPlayerError(error: ExoPlaybackException) {
                print(error.message)
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_IDLE -> {  }
                    Player.STATE_BUFFERING -> { binding.progress.visibility = View.VISIBLE }
                    Player.STATE_READY -> { binding.progress.visibility = View.GONE }
                    Player.STATE_ENDED -> { finish() }
                }
            }
        })
        binding.videoView.setOnClickListener {
            supportActionBar?.show()
            hideActionBar()
        }
        binding.videoView.player = player
    }

    override fun onPause() {
        super.onPause()
        player.stop()
        player.release()
        castContext?.sessionManager?.removeSessionManagerListener(sessionManagerListener, CastSession::class.java)
    }

    override fun onResume() {
        super.onResume()
        castContext?.sessionManager?.addSessionManagerListener(sessionManagerListener, CastSession::class.java)
    }

    private fun play() {
        castSession?.let {
            if (it.isConnected) {
                player.stop()
                player.release()
                loadRemoteMedia()
                finish()
            } else {
                playLocal(true)
            }
        } ?: run {
          playLocal(true)
        }
    }

    private fun playLocal(playWhenReady: Boolean) {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, packageName)
        )
        val videoSource: MediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(viewModel.current?.url?.toUri()!!)
        player.playWhenReady = playWhenReady
        player.prepare(videoSource)
        hideActionBar()
    }

    private fun setupCasting() {
        castContext = CastContext.getSharedInstance(this)
        castSession = castContext?.sessionManager?.currentCastSession
    }

    private fun setupCastListener() {
        sessionManagerListener = object: SessionManagerListener<CastSession> {
            override fun onSessionStarted(p0: CastSession?, p1: String?) {
                Log.e(TAG, "onSessionStarted $p1")
                onApplicationConnected(p0)
            }

            override fun onSessionResumeFailed(p0: CastSession?, p1: Int) {
                Log.e(TAG, "onSessionResumeFailed $p1")
                onApplicationDisconnected()
            }

            override fun onSessionSuspended(p0: CastSession?, p1: Int) {
                Log.e(TAG, "onSessionSuspended $p1")

            }

            override fun onSessionEnded(p0: CastSession?, p1: Int) {
                Log.e(TAG, "onSessionEnded $p1")

                onApplicationDisconnected()
            }

            override fun onSessionResumed(p0: CastSession?, p1: Boolean) {
                Log.e(TAG, "onSessionResumed $p1")

                onApplicationConnected(p0)
            }

            override fun onSessionStarting(p0: CastSession?) {
                Log.e(TAG, "onSessionStarting")

            }

            override fun onSessionResuming(p0: CastSession?, p1: String?) {
                Log.e(TAG, "onSessionResuming $p1")

            }

            override fun onSessionEnding(p0: CastSession?) {
                Log.e(TAG, "onSessionEnding")

            }

            override fun onSessionStartFailed(p0: CastSession?, p1: Int) {
                Log.e(TAG, "onSessionStartFailed $p1")

                onApplicationDisconnected()
            }

        }
    }

    private fun onApplicationDisconnected() {
        play()
        invalidateOptionsMenu()
    }

    private fun onApplicationConnected(castSession: CastSession?) {
        castSession?.let {
            this.castSession = it
            play()
            invalidateOptionsMenu()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this)[PlayerViewModel::class.java]
            .apply {
                current = intent.extras?.getParcelable("DATA_EXTRA")
            }
    }

    private fun buildMediaInfo(): MediaInfo? {
        val movieMetadata =
            MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE)
        movieMetadata.putString(
            MediaMetadata.KEY_TITLE,
            viewModel.current?.name
        )
        movieMetadata.addImage(WebImage("https://ak0.picdn.net/shutterstock/videos/1297750/thumb/1.jpg".toUri()))
        return MediaInfo.Builder( viewModel.current?.url)
            .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
            .setContentType(MimeTypes.APPLICATION_M3U8)
            .setMetadata(movieMetadata)
            .build()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        supportActionBar?.show()
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
            binding.root.setBackgroundColor(resources.getColor(R.color.black))
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            binding.root.setBackgroundColor(resources.getColor(R.color.white))
        }
        hideActionBar()
        super.onConfigurationChanged(newConfig)
    }

    private fun hideActionBar() {
        Handler().postDelayed({
            supportActionBar?.hide()
        }, 3000)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        CastButtonFactory.setUpMediaRouteButton(
            applicationContext, menu,
            R.id.media_route_menu_item
        )
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            ActivityCompat.finishAfterTransition(this)
        }
        return true
    }

    private fun loadRemoteMedia() {
        val remoteClient = castSession?.remoteMediaClient
        remoteClient?.registerCallback(object: RemoteMediaClient.Callback() {
            override fun onStatusUpdated() {
                startActivity(Intent(this@PlayerActivity, ExpandedControlsActivity::class.java))
                remoteClient.unregisterCallback(this)
            }

            override fun onAdBreakStatusUpdated() {
                super.onAdBreakStatusUpdated()
            }

            override fun onMetadataUpdated() {
                super.onMetadataUpdated()
            }

            override fun onPreloadStatusUpdated() {
                super.onPreloadStatusUpdated()
            }

            override fun onQueueStatusUpdated() {
                super.onQueueStatusUpdated()
            }

            override fun onSendingRemoteMediaRequest() {
                super.onSendingRemoteMediaRequest()
            }

        })
        remoteClient?.load(MediaLoadRequestData.Builder()
            .setAutoplay(true)
            .setMediaInfo(buildMediaInfo())
            .build())
    }

}