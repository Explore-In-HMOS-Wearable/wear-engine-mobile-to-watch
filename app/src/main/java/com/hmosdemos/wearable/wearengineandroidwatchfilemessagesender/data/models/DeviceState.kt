package com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.data.models

import com.huawei.wearengine.device.Device

data class DeviceState(
    val devices: List<Device> = emptyList(),
    val selectedDevice: Device? = null,
    val isLoading: Boolean = false,
    val permissionsGranted: Boolean = false
)