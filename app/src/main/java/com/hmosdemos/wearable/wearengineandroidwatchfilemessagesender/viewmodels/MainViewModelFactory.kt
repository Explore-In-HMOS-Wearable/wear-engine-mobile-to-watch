package com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.managers.AuthManager
import com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.managers.DeviceManager
import com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.managers.P2pManager

class MainViewModelFactory(
    private val authManager: AuthManager,
    private val deviceManager: DeviceManager,
    private val p2pManager: P2pManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(
                authManager = authManager,
                deviceManager = deviceManager,
                p2pManager = p2pManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}