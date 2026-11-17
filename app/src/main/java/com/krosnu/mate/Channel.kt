package com.krosnu.mate

import org.json.JSONArray
import java.io.Serializable
import java.net.URL

data class Channel(
    val id: String,
    val country: String,
    val source: String,
    val name: String,
    val category: String,
    val url: String,
    val matchKeyword: String = ""
) : Serializable

object ChannelRepository {

    const val M3U_AZ = "https://iptv-org.github.io/iptv/countries/az.m3u"
    const val M3U_TR = "https://iptv-org.github.io/iptv/countries/tr.m3u"
    const val M3U_RU = "https://iptv-org.github.io/iptv/countries/ru.m3u"

    fun getDefaultChannels(): List<Channel> = listOf(
        Channel("az-itv", "AZ", "ITV", "İTV Canlı", "Ümumi", "https://live.itv.az/"),
        Channel("opr-1", "AZ", "OPERATIV", "AZTV (OperativMM)", "Ümumi", "https://operativmm.az/az/canli-tv", "AZTV"),
        Channel("opr-2", "AZ", "OPERATIV", "İctimai TV (OperativMM)", "Ümumi", "https://operativmm.az/az/canli-tv", "İctimai TV"),
        Channel("opr-3", "AZ", "OPERATIV", "ATV (OperativMM)", "Ümumi", "https://operativmm.az/az/canli-tv", "ATV"),
        Channel("opr-4", "AZ", "OPERATIV", "Xəzər TV (OperativMM)", "Ümumi", "https://operativmm.az/az/canli-tv", "Xəzər TV"),
        Channel("opr-5", "AZ", "OPERATIV", "Space TV (OperativMM)", "Ümumi", "https://operativmm.az/az/canli-tv", "Space TV"),
        Channel("opr-6", "AZ", "OPERATIV", "ARB (OperativMM)", "Ümumi", "https://operativmm.az/az/canli-tv", "ARB"),
        Channel("opr-7", "AZ", "OPERATIV", "Baku TV (OperativMM)", "Xəbər", "https://operativmm.az/az/canli-tv", "Baku TV"),
        Channel("opr-8", "AZ", "OPERATIV", "Real TV (OperativMM)", "Xəbər", "https://operativmm.az/az/canli-tv", "Real TV"),
        Channel("opr-9", "AZ", "OPERATIV", "ARB 24 (OperativMM)", "Xəbər", "https://operativmm.az/az/canli-tv", "ARB 24"),
        Channel("opr-10", "AZ", "OPERATIV", "AnewZ (OperativMM)", "Xəbər", "https://operativmm.az/az/canli-tv", "AnewZ"),
        Channel("opr-11", "AZ", "OPERATIV", "Biznes TV (OperativMM)", "Xəbər", "https://operativmm.az/az/canli-tv", "Biznes TV"),
        Channel("opr-12", "AZ", "OPERATIV", "İdman TV (OperativMM)", "İdman", "https://operativmm.az/az/canli-tv", "İdman TV"),
        Channel("opr-13", "AZ", "OPERATIV", "CBC Sport (OperativMM)", "İdman", "https://operativmm.az/az/canli-tv", "CBC Sport"),
        Channel("mb-az-1", "AZ", "MEDIABAY", "AzTV (Mediabay)", "Ümumi", "https://mediabay.tv/tv", "AzTV"),
        Channel("mb-az-2", "AZ", "MEDIABAY", "İctimai TV (Mediabay)", "Ümumi", "https://mediabay.tv/tv", "Ictimai TV"),
        Channel("mb-az-3", "AZ", "MEDIABAY", "Xəzər TV (Mediabay)", "Ümumi", "https://mediabay.tv/tv", "Xezer"),
        Channel("mb-az-4", "AZ", "MEDIABAY", "ATV Azad (Mediabay)", "Ümumi", "https://mediabay.tv/tv", "ATV"),
        Channel("mb-az-5", "AZ", "MEDIABAY", "ARB (Mediabay)", "Ümumi", "https://mediabay.tv/tv", "ARB"),
        Channel("mb-az-6", "AZ", "MEDIABAY", "ARB 24 (Mediabay)", "Xəbər", "https://mediabay.tv/tv", "ARB 24"),
        Channel("mb-az-7", "AZ", "MEDIABAY", "Real TV (Mediabay)", "Xəbər", "https://mediabay.tv/tv", "Real TV"),
        Channel("mb-az-8", "AZ", "MEDIABAY", "Mədəniyyət (Mediabay)", "Ümumi", "https://mediabay.tv/tv", "Medeniyyet"),
        Channel("mb-az-9", "AZ", "MEDIABAY", "CBC (Mediabay)", "Ümumi", "https://mediabay.tv/tv", "CBC"),
        Channel("mb-az-10", "AZ", "MEDIABAY", "BTV (Mediabay)", "Xəbər", "https://mediabay.tv/tv", "BTV"),
        Channel("mb-az-11", "AZ", "MEDIABAY", "Ayaz TV (Mediabay)", "Musiqi", "https://mediabay.tv/tv", "Ayaz TV"),
        Channel("mb-az-12", "AZ", "MEDIABAY", "Könül TV (Mediabay)", "Musiqi", "https://mediabay.tv/tv", "Konul Tv"),
        Channel("mb-tr-1", "TR", "MEDIABAY", "TRT Avaz (Mediabay)", "Ümumi", "https://mediabay.tv/tv", "TRT Avaz"),
        Channel("mb-tr-2", "TR", "MEDIABAY", "TRT World (Mediabay)", "Xəbər", "https://mediabay.tv/tv", "TRT World"),
        Channel("mb-tr-3", "TR", "MEDIABAY", "Rumeli TV (Mediabay)", "Ümumi", "https://mediabay.tv/tv", "Rumeli TV"),
        Channel("mb-ru-1", "RU", "MEDIABAY", "Россия 24 (Mediabay)", "Xəbər", "https://mediabay.tv/tv", "Россия 24"),
        Channel("mb-ru-2", "RU", "MEDIABAY", "Мир 24 (Mediabay)", "Xəbər", "https://mediabay.tv/tv", "Мир 24"),
        Channel("mb-ru-3", "RU", "MEDIABAY", "РТР-Планета (Mediabay)", "Ümumi", "https://mediabay.tv/tv", "РТР"),
        Channel("mb-ru-4", "RU", "MEDIABAY", "Россия К (Mediabay)", "Ümumi", "https://mediabay.tv/tv", "Россия К"),
        Channel("tvs-1", "AZ", "TVSTREAM", "Xəzər TV (TVStream)", "Ümumi", "https://tvstream.az/#ana-sehife", "XƏZƏR TV"),
        Channel("tvs-2", "AZ", "TVSTREAM", "AzTV HD (TVStream)", "Ümumi", "https://tvstream.az/#ana-sehife", "AZTV HD"),
        Channel("tvs-3", "AZ", "TVSTREAM", "İctimai TV (TVStream)", "Ümumi", "https://tvstream.az/#ana-sehife", "ICTIMAI TV"),
        Channel("tvs-4", "AZ", "TVSTREAM", "Real TV (TVStream)", "Xəbər", "https://tvstream.az/#ana-sehife", "REAL TV"),
        Channel("tvs-5", "AZ", "TVSTREAM", "ARB 24 (TVStream)", "Xəbər", "https://tvstream.az/#ana-sehife", "ARB 24"),
        Channel("tvs-6", "AZ", "TVSTREAM", "İdman TV (TVStream)", "İdman", "https://tvstream.az/#ana-sehife", "IDMAN TV"),
        Channel("tvs-7", "AZ", "TVSTREAM", "CBC Sport (TVStream)", "İdman", "https://tvstream.az/#ana-sehife", "CBC SPORT"),
        Channel("tvs-8", "AZ", "TVSTREAM", "ARB TV (TVStream)", "Ümumi", "https://tvstream.az/#ana-sehife", "ARB TV"),
        Channel("tvs-9", "AZ", "TVSTREAM", "ATV (TVStream)", "Ümumi", "https://tvstream.az/#ana-sehife", "ATV"),
        Channel("tvs-10", "AZ", "TVSTREAM", "Space TV (TVStream)", "Ümumi", "https://tvstream.az/#ana-sehife", "SPACE TV"),
        Channel("az-1", "AZ", "CANLITV", "Xəzər TV (CanlıTV)", "Ümumi", "https://canlitv.com/xezer-tv"),
        Channel("az-2", "AZ", "CANLITV", "Az TV (CanlıTV)", "Ümumi", "https://canlitv.com/az-tv"),
        Channel("az-3", "AZ", "CANLITV", "İctimai TV (CanlıTV)", "Ümumi", "https://canlitv.com/ictimai-tv"),
        Channel("az-4", "AZ", "CANLITV", "ATV Azad TV", "Ümumi", "https://canlitv.com/azad-tv"),
        Channel("az-5", "AZ", "CANLITV", "Space TV (CanlıTV)", "Ümumi", "https://canlitv.com/space-tv"),
        Channel("az-6", "AZ", "CANLITV", "ARB TV (CanlıTV)", "Ümumi", "https://canlitv.com/arb-tv"),
        Channel("az-7", "AZ", "CANLITV", "ARB 24 TV", "Xəbər", "https://canlitv.com/arb-24-tv"),
        Channel("az-8", "AZ", "CANLITV", "CBC Sport (CanlıTV)", "İdman", "https://canlitv.com/cbc-sport-izle"),
        Channel("az-9", "AZ", "CANLITV", "İdman TV (CanlıTV)", "İdman", "https://canlitv.com/idman-tv"),
        Channel("az-10", "AZ", "CANLITV", "Dünya TV", "Ümumi", "https://canlitv.com/dunyatv-az"),
        Channel("az-11", "AZ", "CANLITV", "ARB Günəş TV", "Uşaq", "https://canlitv.com/arb-gunes-tv"),
        Channel("tr-tvf", "TR", "TVFREKANSLAR", "TV Frekanslar (Canlı)", "Ümumi", "https://amp.tvfrekanslar.com/cntr"),
        Channel("tr-t24", "TR", "TARAFTARIUM", "Taraftarium24 (İdman)", "İdman", "https://taraftarium24turk-izle.net/"),
        Channel("tr-1", "TR", "CANLITV", "TRT 1", "Ümumi", "https://canlitv.com/trt1-izle"),
        Channel("tr-2", "TR", "CANLITV", "ATV", "Ümumi", "https://canlitv.com/atv-canli"),
        Channel("tr-3", "TR", "CANLITV", "Kanal D", "Ümumi", "https://canlitv.com/kanal-d-canli-yayin"),
        Channel("tr-4", "TR", "CANLITV", "Show TV", "Ümumi", "https://canlitv.com/show-tv-izle-2"),
        Channel("tr-5", "TR", "CANLITV", "Star TV", "Ümumi", "https://canlitv.com/star-tv-canli"),
        Channel("tr-6", "TR", "CANLITV", "TV8", "Ümumi", "https://canlitv.com/tv8-izle-4"),
        Channel("tr-7", "TR", "CANLITV", "Now TV", "Ümumi", "https://canlitv.com/now-tv-canli-yayin"),
        Channel("tr-8", "TR", "CANLITV", "Kanal 7", "Ümumi", "https://canlitv.com/kanal7"),
        Channel("tr-9", "TR", "CANLITV", "Beyaz TV", "Ümumi", "https://canlitv.com/beyaz-tv"),
        Channel("tr-10", "TR", "CANLITV", "TRT Haber", "Xəbər", "https://canlitv.com/trt-haber-izle"),
        Channel("tr-11", "TR", "CANLITV", "CNN Türk", "Xəbər", "https://canlitv.com/cnn-turk-izle-1"),
        Channel("tr-12", "TR", "CANLITV", "Habertürk TV", "Xəbər", "https://canlitv.com/haberturk"),
        Channel("tr-13", "TR", "CANLITV", "NTV", "Xəbər", "https://canlitv.com/ntv-canli-izle"),
        Channel("tr-14", "TR", "CANLITV", "Haber Global", "Xəbər", "https://canlitv.com/haber-global"),
        Channel("tr-15", "TR", "CANLITV", "Sözcü TV", "Xəbər", "https://canlitv.com/sozcu-tv-izle"),
        Channel("tr-16", "TR", "CANLITV", "TRT Spor", "İdman", "https://canlitv.com/trt-spor-izle"),
        Channel("tr-17", "TR", "CANLITV", "TRT Spor Yıldız", "İdman", "https://canlitv.com/trt-spor-2"),
        Channel("tr-18", "TR", "CANLITV", "A Spor", "İdman", "https://canlitv.com/aspor-canli-izle"),
        Channel("tr-19", "TR", "CANLITV", "TRT Belgesel", "Belgesel", "https://canlitv.com/trt-belgesel")
    )

    fun fetchM3UChannels(m3uUrl: String, countryTag: String): List<Channel> {
        return try {
            val text = URL(m3uUrl).readText()
            val lines = text.lines()
            val list = mutableListOf<Channel>()
            var currentName: String? = null
            for (raw in lines) {
                val line = raw.trim()
                if (line.startsWith("#EXTINF:")) {
                    val idx = line.lastIndexOf(',')
                    currentName = if (idx != -1) line.substring(idx + 1).trim() else "Kanal"
                } else if (line.startsWith("http") && currentName != null) {
                    list.add(
                        Channel(
                            id = "m3u-$countryTag-${list.size + 1}",
                            country = countryTag,
                            source = "M3U8",
                            name = currentName,
                            category = "Canlı",
                            url = line
                        )
                    )
                    currentName = null
                }
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun fetchRemoteChannels(jsonUrl: String): List<Channel>? {
        return try {
            val text = URL(jsonUrl).readText()
            val arr = JSONArray(text)
            val result = mutableListOf<Channel>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                result.add(
                    Channel(
                        id = obj.optString("id", "rem-$i"),
                        country = obj.optString("country", "AZ"),
                        source = obj.optString("source", "REMOTE"),
                        name = obj.optString("name", "Kanal ${i + 1}"),
                        category = obj.optString("category", "Ümumi"),
                        url = obj.getString("url"),
                        matchKeyword = obj.optString("matchKeyword", "")
                    )
                )
            }
            if (result.isNotEmpty()) result else null
        } catch (_: Exception) {
            null
        }
    }
}
