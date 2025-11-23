package com.hmosdemos.wearable.wearengineandroidwatchfilemessagesender.data.models

import android.net.Uri

data class FileState(
    val selectedUri: Uri? = null,
    val selectedFileName: String? = null,
    val isSending: Boolean = false
)