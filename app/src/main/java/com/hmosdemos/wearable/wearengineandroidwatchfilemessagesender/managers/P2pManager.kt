package com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.managers

import android.content.Context
import android.util.Log
import com.huawei.hmf.tasks.OnFailureListener
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.wearengine.HiWear
import com.huawei.wearengine.device.Device
import com.huawei.wearengine.p2p.Message
import com.huawei.wearengine.p2p.P2pClient
import com.huawei.wearengine.p2p.Receiver
import com.huawei.wearengine.p2p.SendCallback
import java.io.File
import java.nio.charset.StandardCharsets


class P2pManager(context: Context) {
    private val p2pClient: P2pClient = HiWear.getP2pClient(context)
    private var messageReceiver: Receiver? = null
    private var messageListener: MessageListener? = null

    interface MessageListener {
        fun onMessageReceived(message: String?)

        fun onMessageSent(message: String?)
    }

    fun registerReceiver(device: Device?, listener: MessageListener?) {
        messageListener = listener

        messageReceiver = Receiver { message ->
            when (message.type) {
                Message.MESSAGE_TYPE_DATA -> {
                    val receivedText = String(message.data, StandardCharsets.UTF_8)
                    messageListener?.onMessageReceived(receivedText)
                    Log.d(TAG, "New Message received: $receivedText")
                }
                Message.MESSAGE_TYPE_FILE -> {
                    val filePath = message.file
                    val fileName = message.file.name
                    Log.d(TAG, "New file received: $fileName, path: $filePath")
                    messageListener?.onMessageReceived("New file received: $fileName, path: $filePath")
                }
                else -> {
                }
            }
        }

        p2pClient.registerReceiver(device, messageReceiver)
            .addOnFailureListener { e ->
                Log.e(TAG, "Register error: ", e)
            }
    }

    fun unregisterReceiver() {
        if (messageReceiver != null) {
            p2pClient.unregisterReceiver(messageReceiver)
            messageListener = null
        }
    }


    fun setPeerPkgName() {
        val peerPackageName: String? = "YOUR_WEARABLE_PACKAGE_NAME"
        val peerFingerPrint: String? = "YOUR_WEARABLE_FINGERPRINT"

        p2pClient.setPeerPkgName(peerPackageName)
        p2pClient.setPeerFingerPrint(peerFingerPrint)

    }

    fun pingDevice(
        device: Device,
        peerPackageName: String?,
        successListener: OnSuccessListener<String?>,
        failureListener: OnFailureListener
    ) {
        p2pClient.ping(device) { result ->
            val message =
                """- Connected Device Name: ${device.name}
                   - Peer Package Name: $peerPackageName
                   - Ping Result: ${STRING_RESULT}${result}
                """.trimIndent()
            successListener.onSuccess(message)
        }.addOnSuccessListener { aVoid: Void? ->
            successListener.onSuccess("${device.name}${DEVICE_NAME_OF}${peerPackageName}")
        }.addOnFailureListener { e: Exception? ->
            failureListener.onFailure(Exception("Ping Fail:$FAILURE$e", e))
        }
    }

    fun sendMessage(
        device: Device?,
        message: String,
        successListener: OnSuccessListener<Void?>?,
        failureListener: OnFailureListener
    ) {
        try {

            val msg = Message.Builder()
                .setPayload(message.toByteArray(StandardCharsets.UTF_8))
                .build()
            val sendCallback: SendCallback = object : SendCallback {
                override fun onSendResult(resultCode: Int) {
                    if (resultCode == 207) {
                        Log.d(TAG, "Message send successfully")
                        successListener?.onSuccess(null)
                    } else {
                        Log.e(TAG, "Message send failed. Error Code: $resultCode")
                        failureListener.onFailure(Exception("Send failed with code $resultCode"))
                    }
                }


                override fun onSendProgress(progress: Long) {
                    Log.d(TAG, "Progress: %$progress")
                }
            }

            p2pClient.send(device, msg, sendCallback)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener)

            messageListener?.onMessageSent(message)
        } catch (e: Exception) {
            failureListener.onFailure(e)
        }
    }


    fun sendFile(
        context: Context,
        device: Device,
        successListener: OnSuccessListener<Void?>?,
        failureListener: OnFailureListener
    ) {
        try {
            val defaultContent = "Test file content!"
            val fileName = "default_message.txt"
            val tempFile = File(context.cacheDir, fileName)

            tempFile.writeText(defaultContent)

            val fileMessage = Message.Builder()
                .setPayload(tempFile)
                .build()

            val sendCallback: SendCallback = object : SendCallback {
                override fun onSendResult(resultCode: Int) {
                    if (resultCode == 207) {
                        Log.d(TAG, "File send successfully")
                        messageListener?.onMessageSent("File send successfully")
                    } else {
                        Log.e(TAG, "File send failed. Error Code: $resultCode")
                        messageListener?.onMessageSent("File send failed. Error Code: $resultCode")

                    }
                }

                override fun onSendProgress(progress: Long) {
                    Log.d(TAG, "Progress: $progress bytes")
                }
            }

            p2pClient.send(device, fileMessage, sendCallback)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener)

            messageListener?.onMessageSent("$fileName have been sent.")
        } catch (e: Exception) {
            Log.e(TAG, "Exception sending file: ${e.message}")
            failureListener.onFailure(e)
        }
    }

    companion object {
        private const val TAG = "P2pManager"
        private const val DEVICE_NAME_OF = "'s "
        private const val STRING_RESULT = " result:"
        private const val FAILURE = " task failure"
    }
}