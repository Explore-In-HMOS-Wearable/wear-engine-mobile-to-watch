package com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huawei.wearengine.device.Device
import com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.domain.models.UiState
import com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.managers.AuthManager
import com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.managers.DeviceManager
import com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.managers.P2pManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import kotlin.coroutines.resume

class MainViewModel(
    private val authManager: AuthManager,
    private val deviceManager: DeviceManager,
    private val p2pManager: P2pManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var currentConnectedDevice: Device? = null

    init {
        p2pManager.setPeerPkgName()
        checkPermissionsAndLoadDevices()
    }

    private fun checkPermissionsAndLoadDevices() {
        authManager.checkPermissions(object : AuthManager.AuthCheckCallback {
            override fun onResult(allPermissionsGranted: Boolean) {
                if (allPermissionsGranted) {
                    loadDevices()
                    return
                }

                requestDevicePermission()
            }

            override fun onError(e: Exception?) {
                requestDevicePermission()
            }
        })
    }

    fun requestDevicePermission(onSuccess: Runnable? = null, onCancel: Runnable? = null) {
        authManager.requestPermission(onSuccess, onCancel)
    }

    fun selectDevice(device: Device) {
        _uiState.value = _uiState.value.copy(
            deviceState = _uiState.value.deviceState.copy(selectedDevice = device)
        )

        p2pManager.registerReceiver(device, object : P2pManager.MessageListener {
            override fun onMessageReceived(message: String?) {
                message?.let { msg ->
                    val currentReceivedMessageList = _uiState.value.receivedMessages.toMutableList()
                    currentReceivedMessageList.add(msg)
                    _uiState.update {
                        it.copy(receivedMessages = currentReceivedMessageList.toList())
                    }
                }
            }

            override fun onMessageSent(message: String?) {
                // TODO add log.
            }
        })

        addLogMessage("Selected device: ${device.name}")
        viewModelScope.launch {
            connectToDevice(device)
        }
    }

    fun updateMessageText(text: String) {
        _uiState.value = _uiState.value.copy(
            messageState = _uiState.value.messageState.copy(text = text)
        )
    }

    fun sendPing() {
        val selectedDevice = _uiState.value.deviceState.selectedDevice
        if (selectedDevice == null) {
            addLogMessage("No device selected for ping")
            return
        }
        viewModelScope.launch {
            val peerPackageName: String? = "com.sample.trdtse.payment.lite"

            p2pManager.pingDevice(selectedDevice, peerPackageName, { result ->
                addLogMessage("Send Ping Result: $result")
            }, { error ->
                addLogMessage("Ping failed: ${error.message}")
            })
        }
    }

    fun sendMessage(message: String) {
        val selectedDevice = _uiState.value.deviceState.selectedDevice
        if (selectedDevice == null) {
            addLogMessage("No device selected for message")
            return
        }

        _uiState.value = _uiState.value.copy(
            messageState = _uiState.value.messageState.copy(isSending = true)
        )

        viewModelScope.launch {
            addLogMessage("selectedDevice: $selectedDevice")

            p2pManager.sendMessage(selectedDevice, message, { result ->
                _uiState.value = _uiState.value.copy(
                    messageState = _uiState.value.messageState.copy(
                        isSending = false,
                        text = ""
                    )
                )
                addLogMessage("Message sent: $message")
            }, { error ->
                _uiState.value = _uiState.value.copy(
                    messageState = _uiState.value.messageState.copy(isSending = false)
                )
                Log.d(
                    "Send Message Error",
                    "sendMessage: ${error.message} ${error.hashCode()} ${error.cause}"
                )
                addLogMessage("Message failed: ${error.message}")
            })
        }
    }

    fun selectFile(uri: Uri, fileName: String?) {
        _uiState.value = _uiState.value.copy(
            fileState = _uiState.value.fileState.copy(
                selectedUri = uri,
                selectedFileName = fileName
            )
        )
        addLogMessage("File selected: ${fileName ?: "Unknown"}")
    }

    fun sendFile(context: Context) {
        val selectedDevice = _uiState.value.deviceState.selectedDevice

        if (selectedDevice == null) {
            addLogMessage("Cannot send file: device or file not selected")
            return
        }

        _uiState.value = _uiState.value.copy(
            fileState = _uiState.value.fileState.copy(isSending = true)
        )

        viewModelScope.launch {
            p2pManager.sendFile(
                context,
                selectedDevice,
                {
                    _uiState.value = _uiState.value.copy(
                        fileState = _uiState.value.fileState.copy(
                            isSending = false,
                            selectedUri = null,
                            selectedFileName = null
                        )
                    )
                },
                { error ->
                    _uiState.value = _uiState.value.copy(
                        fileState = _uiState.value.fileState.copy(isSending = false)
                    )
                    addLogMessage("File failed: ${error.message}")
                }
            )
        }
    }

    fun refreshDevices() {
        loadDevices()
    }

    fun clearLogs() {
        _uiState.value = _uiState.value.copy(
            receivedMessages = emptyList(),
            logMessages = emptyList()
        )
    }

    private suspend fun connectToDevice(device: Device): String =
        suspendCancellableCoroutine { continuation ->
            updateConnectedDevice(device)
            continuation.resume("Connected to ${device.name}")
        }

    private fun updateConnectedDevice(device: Device?) {
        currentConnectedDevice = device
    }

    private fun loadDevices() {
        _uiState.value = _uiState.value.copy(
            deviceState = _uiState.value.deviceState.copy(isLoading = true)
        )

        viewModelScope.launch {
            deviceManager.getBondedDevices({ deviceList ->
                _uiState.value = _uiState.value.copy(
                    deviceState = _uiState.value.deviceState.copy(
                        devices = deviceList?.filterNotNull() ?: emptyList(),
                        isLoading = false,
                        permissionsGranted = true
                    )
                )
                if (deviceList?.filterNotNull().isNullOrEmpty()) {
                    addLogMessage("No bonded devices found")
                } else {
                    addLogMessage("Found ${deviceList.filterNotNull().size} bonded devices")
                }
            }, { error ->
                _uiState.value = _uiState.value.copy(
                    deviceState = _uiState.value.deviceState.copy(
                        isLoading = false,
                        permissionsGranted = false
                    )
                )
                addLogMessage("Device loading failed: ${error.message}")
            })
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun addLogMessage(message: String) {
        val timestamp = System.currentTimeMillis()
        val timestampedMessage =
            "[${SimpleDateFormat("HH:mm:ss").format(timestamp)}] $message"

        _uiState.value = _uiState.value.copy(
            logMessages = _uiState.value.logMessages + timestampedMessage
        )
    }
}