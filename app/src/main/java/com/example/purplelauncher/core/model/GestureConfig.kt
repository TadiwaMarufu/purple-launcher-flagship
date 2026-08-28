package com.example.purplelauncher.core.model

enum class GestureAction(val displayName: String) {
    APP_DRAWER("Open App Drawer"),
    UNIVERSAL_SEARCH("Open Universal Search"),
    LOCK_SCREEN("Lock Screen / Sleep"),
    CUSTOMIZE_HOME("Customize Home"),
    OPEN_SETTINGS("Open Launcher Settings"),
    SWITCH_NEXT_PROFILE("Switch Next Profile"),
    OPEN_DEV_SPACE("Open Developer Space"),
    OPEN_NOTIFICATIONS("Expand Notification Shade"),
    QUICK_SETTINGS("Open Quick Settings / Control Center"),
    OPEN_CONTROL_CENTER("Control Center / Quick Panel"),
    NONE("Do Nothing")
}

data class GestureConfig(
    val swipeUp: GestureAction = GestureAction.APP_DRAWER,
    val swipeDown: GestureAction = GestureAction.OPEN_CONTROL_CENTER,
    val doubleTap: GestureAction = GestureAction.LOCK_SCREEN,
    val longPress: GestureAction = GestureAction.CUSTOMIZE_HOME,
    val twoFingerSwipe: GestureAction = GestureAction.QUICK_SETTINGS,
    val pinchIn: GestureAction = GestureAction.OPEN_SETTINGS,
    val pinchOut: GestureAction = GestureAction.SWITCH_NEXT_PROFILE
)
