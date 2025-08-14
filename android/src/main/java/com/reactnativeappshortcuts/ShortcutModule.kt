package com.reactnativeappshortcuts // change to your package name

import android.app.Activity
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule

class ShortcutModule(private val reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext), LifecycleEventListener {

    private var initialShortcutData: String? = null

    init {
        reactContext.addLifecycleEventListener(this)
    }

    override fun getName(): String {
        return "ShortcutModule"
    }

    // ---------------- ADD SHORTCUTS ---------------- //
    @ReactMethod
    fun addShortcuts(shortcutsArray: ReadableArray?) {
        if (shortcutsArray == null || shortcutsArray.size() == 0) return

        val shortcutManager: ShortcutManager =
            reactContext.getSystemService(ShortcutManager::class.java) ?: return

        val shortcutList = mutableListOf<ShortcutInfo>()

        for (i in 0 until shortcutsArray.size()) {
            val shortcut = shortcutsArray.getMap(i) ?: continue

            val id = shortcut.getString("id") ?: continue
            val shortLabel = shortcut.getString("shortLabel") ?: continue
            val longLabel = shortcut.getString("longLabel") ?: shortLabel
            val iconName = shortcut.getString("iconName") ?: ""
            val intentAction = shortcut.getString("intentAction") ?: ""

            val intent = Intent(reactContext, currentActivity?.javaClass ?: return).apply {
                action = Intent.ACTION_VIEW
                putExtra("shortcutAction", intentAction)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }

            val iconResId = if (iconName.isNotEmpty()) {
                reactContext.resources.getIdentifier(iconName, "drawable", reactContext.packageName)
                    .takeIf { it != 0 }
                    ?: reactContext.resources.getIdentifier(iconName, "mipmap", reactContext.packageName)
            } else 0

            val icon = if (iconResId != 0) {
                Icon.createWithResource(reactContext, iconResId)
            } else {
                Icon.createWithResource(reactContext, android.R.drawable.ic_menu_view)
            }

            val shortcutInfo = ShortcutInfo.Builder(reactContext, id)
                .setShortLabel(shortLabel)
                .setLongLabel(longLabel)
                .setIcon(icon)
                .setIntent(intent)
                .build()

            shortcutList.add(shortcutInfo)
        }

        shortcutManager.dynamicShortcuts = shortcutList
    }

    // ---------------- LISTEN FOR SHORTCUT OPEN ---------------- //
    fun handleShortcutIntent(intent: Intent?) {
        if (intent == null) return
        val action = intent.getStringExtra("shortcutAction") ?: return

        // Store for initial launch
        initialShortcutData = action

        // Send immediately if JS is ready
        sendShortcutEvent(action)
    }

    private fun sendShortcutEvent(action: String) {
        if (reactContext.hasActiveCatalystInstance()) {
            reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                .emit("onShortcutOpen", action)
        }
    }

    // Called from JS to get the initial shortcut (cold start)
    @ReactMethod
    fun getInitialShortcut(promise: Promise) {
        promise.resolve(initialShortcutData)
        initialShortcutData = null // Clear after fetching
    }

    // ---------------- LIFECYCLE ---------------- //
    override fun onHostResume() {
        handleShortcutIntent(currentActivity?.intent)
        // Clear so we don't send the same shortcut multiple times
        currentActivity?.intent = Intent(currentActivity, currentActivity?.javaClass)
    }

    override fun onHostPause() {}
    override fun onHostDestroy() {}

    @ReactMethod
fun addListener(eventName: String?) {
    // Required for RN built-in Event Emitter
}

@ReactMethod
fun removeListeners(count: Int) {
    // Required for RN built-in Event Emitter
}
}
