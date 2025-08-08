package com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.managers

import android.content.Context
import com.huawei.hmf.tasks.OnFailureListener
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.wearengine.HiWear
import com.huawei.wearengine.auth.AuthCallback
import com.huawei.wearengine.auth.AuthClient
import com.huawei.wearengine.auth.Permission

class AuthManager(context: Context) {
    private val authClient: AuthClient = HiWear.getAuthClient(context)

    interface AuthCheckCallback {
        fun onResult(allPermissionsGranted: Boolean)
        fun onError(e: Exception?)
    }

    fun checkPermissions(callback: AuthCheckCallback) {
        authClient.checkPermissions(arrayOf<Permission>(Permission.DEVICE_MANAGER))
            .addOnSuccessListener(OnSuccessListener { permissions: Array<Boolean?>? ->
                var allGranted = true
                for (granted in permissions!!) {
                    if (!granted!!) {
                        allGranted = false
                        break
                    }
                }
                callback.onResult(allGranted)
            })
            .addOnFailureListener(OnFailureListener { e: Exception? -> callback.onError(e) })
    }

    fun requestPermission(onSuccess: Runnable?, onCancel: Runnable?) {
        authClient.requestPermission(object : AuthCallback {
            override fun onOk(permissions: Array<Permission?>?) {
                if (onSuccess != null) onSuccess.run()
            }

            override fun onCancel() {
                if (onCancel != null) onCancel.run()
            }
        }, Permission.DEVICE_MANAGER)
    }
}

