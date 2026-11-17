package com.krosnu.mate

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView

class PlayerActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var osdBar: LinearLayout
    private lateinit var osdTitle: TextView
    private lateinit var digitOsd: TextView
    private lateinit var loadingSpinner: ProgressBar

    private var player: ExoPlayer? = null
    private var sniffer: StreamSniffer? = null
    private val handler = Handler(Looper.getMainLooper())

    private var channelList: ArrayList<Channel> = arrayListOf()
    private var currentIndex: Int = 0
    private var digitBuffer: String = ""

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemBars()

        channelList = (intent.getSerializableExtra("channels") as? ArrayList<Channel>) ?: arrayListOf()
        currentIndex = intent.getIntExtra("index", 0)

        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
        }

        playerView = PlayerView(this).apply {
            useController = false
        }
        root.addView(playerView, FrameLayout.LayoutParams(-1, -1))

        loadingSpinner = ProgressBar(this).apply {
            visibility = View.GONE
        }
        root.addView(loadingSpinner, FrameLayout.LayoutParams(120, 120, Gravity.CENTER))

        digitOsd = TextView(this).apply {
            setBackgroundColor(Color.parseColor("#DD0B111D"))
            setTextColor(Color.parseColor("#38BDF8"))
            textSize = 28f
            setPadding(36, 18, 36, 18)
            visibility = View.GONE
        }
        root.addView(
            digitOsd,
            FrameLayout.LayoutParams(-2, -2, Gravity.TOP or Gravity.END).apply {
                setMargins(0, 36, 48, 0)
            }
        )

        osdBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(Color.parseColor("#CC141B22"))
            setPadding(36, 22, 36, 22)
        }

        osdTitle = TextView(this).apply {
            setTextColor(Color.WHITE)
            textSize = 20f
            layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        }

        val btnPrev = Button(this).apply {
            text = "▲ CH-"
            isFocusable = true
            setOnClickListener { switchChannel(-1) }
        }
        val btnNext = Button(this).apply {
            text = "▼ CH+"
            isFocusable = true
            setOnClickListener { switchChannel(1) }
        }
        val btnBack = Button(this).apply {
            text = "Liste"
            isFocusable = true
            setOnClickListener { finish() }
        }

        osdBar.addView(osdTitle)
        osdBar.addView(btnPrev)
        osdBar.addView(btnNext)
        osdBar.addView(btnBack)

        root.addView(osdBar, FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM))
        root.setOnClickListener { showOsd() }

        setContentView(root)
        sniffer = StreamSniffer(this)

        if (channelList.isNotEmpty()) {
            loadCurrentChannel()
        }
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showOsd() {
        osdBar.visibility = View.VISIBLE
        handler.removeCallbacksAndMessages("osd")
        handler.postAtTime({ osdBar.visibility = View.GONE }, "osd", android.os.SystemClock.uptimeMillis() + 4500)
    }

    private fun switchChannel(delta: Int) {
        if (channelList.isEmpty()) return
        currentIndex = (currentIndex + delta + channelList.size) % channelList.size
        loadCurrentChannel()
    }

    private fun handleRemoteDigit(digit: Int) {
        digitBuffer += digit.toString()
        if (digitBuffer.length > 3) digitBuffer = digit.toString()
        digitOsd.text = digitBuffer.padStart(3, '0')
        digitOsd.visibility = View.VISIBLE

        handler.removeCallbacksAndMessages("digit")
        handler.postAtTime({
            digitOsd.visibility = View.GONE
            val num = digitBuffer.toIntOrNull() ?: 0
            digitBuffer = ""
            if (num in 1..channelList.size) {
                currentIndex = num - 1
                loadCurrentChannel()
            } else {
                Toast.makeText(this, "$num numaralı kanal yok", Toast.LENGTH_SHORT).show()
            }
        }, "digit", android.os.SystemClock.uptimeMillis() + 1200)
    }

    private fun loadCurrentChannel() {
        val ch = channelList[currentIndex]
        osdTitle.text = String.format("%03d  %s | %s", currentIndex + 1, ch.country, ch.name)
        showOsd()
        loadingSpinner.visibility = View.VISIBLE
        player?.stop()

        sniffer?.sniffStream(
            channel = ch,
            onFound = { streamUrl, referer ->
                loadingSpinner.visibility = View.GONE
                playStream(streamUrl, referer)
            },
            onFailed = { reason ->
                loadingSpinner.visibility = View.GONE
                Toast.makeText(this, "${ch.name}: $reason", Toast.LENGTH_SHORT).show()
            }
        )
    }

    @OptIn(UnstableApi::class)
    private fun playStream(streamUrl: String, referer: String) {
        player?.release()
        val headers = mapOf(
            "Referer" to referer,
            "User-Agent" to "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36"
        )
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(headers)

        val exo = ExoPlayer.Builder(this).build()
        player = exo
        playerView.player = exo

        val mediaItem = MediaItem.fromUri(streamUrl)
        val hlsSource = HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        exo.setMediaSource(hlsSource)
        exo.prepare()
        exo.playWhenReady = true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9) {
            handleRemoteDigit(keyCode - KeyEvent.KEYCODE_0)
            return true
        }
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_CHANNEL_UP -> {
                switchChannel(-1); true
            }
            KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_CHANNEL_DOWN -> {
                switchChannel(1); true
            }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_INFO -> {
                showOsd(); true
            }
            KeyEvent.KEYCODE_BACK -> {
                finish(); true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        sniffer?.cleanup()
        player?.release()
        player = null
    }
}
