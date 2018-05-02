package com.pashkobohdan.ttsreader.ui

interface PermissionUtil {
    fun requestPermission(permissions: Array<String>, requestCode: Int, grantedCallback: () -> Unit)
}