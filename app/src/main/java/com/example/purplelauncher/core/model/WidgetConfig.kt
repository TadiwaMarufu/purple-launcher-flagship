package com.example.purplelauncher.core.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class WidgetType(
    val title: String,
    val description: String,
    val defaultSpan: WidgetSpan,
    val category: String
) {
    NOW_PLAYING(
        title = "Music & Media Player",
        description = "Live album art, track waveforms, playback controls & audio bar",
        defaultSpan = WidgetSpan.WIDE,
        category = "Media"
    ),
    MULTI_BATTERY(
        title = "Multi-Device Battery Rings",
        description = "Phone, Watch, Wireless Buds & Tablet charging status",
        defaultSpan = WidgetSpan.WIDE,
        category = "System"
    ),
    LIVE_WEATHER(
        title = "Live Forecast & Hourly Radar",
        description = "Current condition, dynamic temperature, UV, and hourly timeline",
        defaultSpan = WidgetSpan.WIDE,
        category = "Information"
    ),
    DEVICE_CARE(
        title = "Device Care & RAM Booster",
        description = "Live memory meter, CPU temperature & one-tap cleaning",
        defaultSpan = WidgetSpan.MEDIUM,
        category = "System"
    ),
    DIGITAL_WELLBEING(
        title = "Screen Time & Wellbeing",
        description = "Daily screen-on hours, app usage breakdown & focus stats",
        defaultSpan = WidgetSpan.WIDE,
        category = "Productivity"
    ),
    AGENDA_CALENDAR(
        title = "Calendar & Agenda Schedule",
        description = "Interactive monthly calendar and upcoming scheduled events",
        defaultSpan = WidgetSpan.WIDE,
        category = "Productivity"
    ),
    QUICK_NOTES(
        title = "Sticky Notes & Quick Tasks",
        description = "Checklist with interactive task toggle and quick note pad",
        defaultSpan = WidgetSpan.MEDIUM,
        category = "Productivity"
    ),
    PHOTO_FRAME(
        title = "Atmospheric Photo Frame",
        description = "Cycling aesthetic polaroid frames & custom album memories",
        defaultSpan = WidgetSpan.MEDIUM,
        category = "Personal"
    ),
    QUICK_TOGGLES(
        title = "Control Center Quick Toggles",
        description = "Instant Wi-Fi, Bluetooth, Flashlight, Dark Mode & Hotspot switch",
        defaultSpan = WidgetSpan.WIDE,
        category = "Tools"
    ),
    GEMINI_ASSISTANT(
        title = "Gemini AI Assistant Capsule",
        description = "Quick prompt shortcuts: Summarize, Ask Gemini, Voice & Lens",
        defaultSpan = WidgetSpan.WIDE,
        category = "AI & Tools"
    ),
    FITNESS_TRACKER(
        title = "Fitness & Step Activity",
        description = "Daily 10,000 steps progress gauge, active kcal & stand metrics",
        defaultSpan = WidgetSpan.MEDIUM,
        category = "Health"
    ),
    CRYPTO_MARKET(
        title = "Crypto & Market Tracker",
        description = "Live Bitcoin, Ethereum & Tech stock tickers with mini sparklines",
        defaultSpan = WidgetSpan.MEDIUM,
        category = "Finance"
    ),
    WORLD_CLOCK(
        title = "Dual World Clock",
        description = "Local timezone and global city time with day/night indicator",
        defaultSpan = WidgetSpan.MEDIUM,
        category = "Information"
    ),
    VIP_CONTACTS(
        title = "VIP Quick Contacts",
        description = "One-tap speed dial and WhatsApp messaging shortcuts",
        defaultSpan = WidgetSpan.MEDIUM,
        category = "Social"
    ),
    QUOTE_OF_THE_DAY(
        title = "Inspiration & Daily Quote",
        description = "Curated daily motivation and philosophical reflections",
        defaultSpan = WidgetSpan.MEDIUM,
        category = "Personal"
    )
}

enum class WidgetSpan(val displayName: String, val gridSpan: Int) {
    COMPACT("Square 2x2", 1),
    MEDIUM("Medium 2x2", 1),
    WIDE("Wide 4x2", 2),
    FULL("Large 4x4", 2)
}

data class ActiveHomeWidget(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: WidgetType,
    val span: WidgetSpan = type.defaultSpan,
    val customTitle: String? = null,
    val isEnabled: Boolean = true
)
