package io.github.evilsloth.amazfitplayer.mediaplayer.headphones

import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

typealias OnHeadphonesConnectedListener = () -> Unit

typealias OnHeadphonesDisconnectedListener = () -> Unit

private const val TAG = "HeadphonesConnection"

class HeadphonesConnectionManager(private val launcherContext: Context) {

    init {
        launcherContext.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val state = intent?.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1)

                if (state == BluetoothA2dp.STATE_CONNECTED) {
                    Log.d(TAG, "bluetooth headphones connected")
                    onHeadphonesDisconnectedListeners.forEach { it() }
                }

                if (state == BluetoothA2dp.STATE_DISCONNECTED) {
                    Log.d(TAG, "bluetooth headphones disconnected")
                    onHeadphonesDisconnectedListeners.forEach { it() }
                }
            }
        }, IntentFilter(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED));
    }

    val isConnected
        get() = BluetoothProfile.STATE_CONNECTED == bluetoothManager.adapter.getProfileConnectionState(BluetoothProfile.A2DP)

    private val bluetoothManager = launcherContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    private val onHeadphonesDisconnectedListeners = mutableSetOf<OnHeadphonesDisconnectedListener>()

    private val onHeadphonesConnectedListeners = mutableSetOf<OnHeadphonesConnectedListener>()

    fun addOnHeadphonesDisconnectedListener(listener: OnHeadphonesDisconnectedListener) {
        onHeadphonesDisconnectedListeners.add(listener)
    }

    fun addOnHeadphonesConnectedListener(listener: OnHeadphonesConnectedListener) {
        onHeadphonesConnectedListeners.add(listener)
    }

}