package com.krosnu.mate

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private var allChannels = ChannelRepository.getDefaultChannels().toMutableList()
    private var activeGroup = "AZ"
    private lateinit var channelRecycler: RecyclerView
    private lateinit var groupContainer: LinearLayout

    private val groups = listOf(
        "ALL" to "Bütün kanallar",
        "AZ" to "AZ | AZƏRBAYCAN",
        "TR" to "TR | TÜRKİYƏ",
        "RU" to "RU | RUSİYA",
        "MEDIABAY" to "MEDIABAY.TV",
        "OPERATIV" to "OPERATIVMM.AZ",
        "TVSTREAM" to "TVSTREAM.AZ",
        "M3U8" to "Canlı M3U8 (AZ/TR/RU)"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.parseColor("#141B22"))
        }

        val navRail = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#2A3642"))
            setPadding(20, 32, 20, 24)
            layoutParams = LinearLayout.LayoutParams(320, -1)
        }
        val logo = TextView(this).apply {
            text = "krosnumate"
            textSize = 20f
            setTextColor(Color.parseColor("#38BDF8"))
            setPadding(12, 0, 12, 28)
        }
        val btnTv = Button(this).apply {
            text = "TV Kanalları"
            isFocusable = true
            isFocusableInTouchMode = true
            background = createTvFocusDrawable(Color.WHITE, Color.parseColor("#38BDF8"))
            setTextColor(Color.parseColor("#1B242C"))
            setOnClickListener { activeGroup = "AZ"; renderGroups(); refreshList() }
        }
        val btnRefreshM3u = Button(this).apply {
            text = "AZ / TR / RU Yenilə"
            isFocusable = true
            isFocusableInTouchMode = true
            background = createTvFocusDrawable(Color.parseColor("#1F2933"), Color.parseColor("#38BDF8"))
            setTextColor(Color.parseColor("#34D399"))
            setOnClickListener { loadLiveM3uPlaylists() }
        }
        val btnSync = Button(this).apply {
            text = "Xüsusi JSON Linki"
            isFocusable = true
            isFocusableInTouchMode = true
            background = createTvFocusDrawable(Color.parseColor("#1F2933"), Color.parseColor("#38BDF8"))
            setTextColor(Color.WHITE)
            setOnClickListener { openRemoteSyncDialog() }
        }
        navRail.addView(logo)
        navRail.addView(btnTv)
        navRail.addView(btnRefreshM3u)
        navRail.addView(btnSync)

        groupContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#1F2933"))
            setPadding(16, 24, 16, 24)
            layoutParams = LinearLayout.LayoutParams(380, -1)
        }

        channelRecycler = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            layoutParams = LinearLayout.LayoutParams(0, -1, 1f)
            setBackgroundColor(Color.parseColor("#141B22"))
            descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
        }

        root.addView(navRail)
        root.addView(groupContainer)
        root.addView(channelRecycler)
        setContentView(root)

        renderGroups()
        refreshList()
        loadLiveM3uPlaylists()
        channelRecycler.requestFocus()
    }

    private fun loadLiveM3uPlaylists() {
        CoroutineScope(Dispatchers.IO).launch {
            val az = ChannelRepository.fetchM3UChannels(ChannelRepository.M3U_AZ, "AZ")
            val tr = ChannelRepository.fetchM3UChannels(ChannelRepository.M3U_TR, "TR")
            val ru = ChannelRepository.fetchM3UChannels(ChannelRepository.M3U_RU, "RU")
            val combined = az + tr + ru
            if (combined.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    val existing = ChannelRepository.getDefaultChannels()
                    allChannels.clear()
                    allChannels.addAll(combined)
                    allChannels.addAll(existing)
                    refreshList()
                    Toast.makeText(
                        this@MainActivity,
                        "Canlı M3U8 yeniləndi: AZ(${az.size}) TR(${tr.size}) RU(${ru.size})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun createTvFocusDrawable(normalColor: Int, focusStrokeColor: Int): StateListDrawable {
        val focused = GradientDrawable().apply {
            setColor(Color.parseColor("#2A3947"))
            setStroke(5, focusStrokeColor)
            cornerRadius = 8f
        }
        val normal = GradientDrawable().apply {
            setColor(normalColor)
            cornerRadius = 8f
        }
        return StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_focused), focused)
            addState(intArrayOf(), normal)
        }
    }

    private fun renderGroups() {
        groupContainer.removeAllViews()
        groups.forEach { (id, label) ->
            val btn = Button(this).apply {
                text = label
                isFocusable = true
                isFocusableInTouchMode = true
                gravity = Gravity.START or Gravity.CENTER_VERTICAL
                setPadding(24, 14, 24, 14)
                setTextColor(if (id == activeGroup) Color.WHITE else Color.parseColor("#CBD5E1"))
                val baseBg = if (id == activeGroup) Color.parseColor("#364452") else Color.TRANSPARENT
                background = createTvFocusDrawable(baseBg, Color.parseColor("#38BDF8"))
                setOnClickListener {
                    activeGroup = id
                    renderGroups()
                    refreshList()
                }
            }
            groupContainer.addView(btn)
        }
    }

    private fun filteredChannels(): List<Channel> = when (activeGroup) {
        "AZ" -> allChannels.filter { it.country == "AZ" }
        "TR" -> allChannels.filter { it.country == "TR" }
        "RU" -> allChannels.filter { it.country == "RU" }
        "MEDIABAY" -> allChannels.filter { it.source == "MEDIABAY" }
        "OPERATIV" -> allChannels.filter { it.source == "OPERATIV" }
        "TVSTREAM" -> allChannels.filter { it.source == "TVSTREAM" }
        "M3U8" -> allChannels.filter { it.source == "M3U8" }
        else -> allChannels
    }

    private fun refreshList() {
        val list = filteredChannels()
        channelRecycler.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val tv = TextView(parent.context).apply {
                    layoutParams = RecyclerView.LayoutParams(-1, -2)
                    setPadding(36, 26, 36, 26)
                    textSize = 17f
                    setTextColor(Color.WHITE)
                    isFocusable = true
                    isFocusableInTouchMode = true
                    isClickable = true
                }
                return object : RecyclerView.ViewHolder(tv) {}
            }

            override fun getItemCount() = list.size

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val ch = list[position]
                val tv = holder.itemView as TextView
                tv.text = String.format("%03d   %s | %s   [%s]", position + 1, ch.country, ch.name, ch.source)
                val baseColor = if (position % 2 == 0) Color.parseColor("#141B22") else Color.parseColor("#182029")
                tv.background = createTvFocusDrawable(baseColor, Color.parseColor("#38BDF8"))
                tv.setOnClickListener {
                    val intent = Intent(this@MainActivity, PlayerActivity::class.java).apply {
                        putExtra("channels", ArrayList(list))
                        putExtra("index", position)
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private fun openRemoteSyncDialog() {
        val input = EditText(this).apply {
            hint = "https://raw.githubusercontent.com/.../kanallar.json"
        }
        AlertDialog.Builder(this)
            .setTitle("Canlı Mənbə (JSON)")
            .setView(input)
            .setPositiveButton("Yenilə") { _, _ ->
                val url = input.text.toString().trim()
                if (url.startsWith("http")) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val fetched = ChannelRepository.fetchRemoteChannels(url)
                        withContext(Dispatchers.Main) {
                            if (fetched != null) {
                                allChannels.clear()
                                allChannels.addAll(fetched)
                                refreshList()
                                Toast.makeText(this@MainActivity, "${fetched.size} kanal yeniləndi", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@MainActivity, "Linkdən oxumaq alınmadı", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            .setNegativeButton("Ləğv et", null)
            .show()
    }
}
