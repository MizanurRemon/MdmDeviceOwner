package com.remon.mdmdeviceowner
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context

class DeviceOwnerManager(private val context: Context) {

    private val devicePolicyManager: DevicePolicyManager =
        context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    private val adminComponent: ComponentName =
        ComponentName(context, "com.remon.mdmdeviceowner.DeviceAdminReceiver")

    fun isDeviceOwner(): Boolean {
        return devicePolicyManager.isDeviceOwnerApp(context.packageName)
    }

    fun isProfileOwner(): Boolean {
        return devicePolicyManager.isProfileOwnerApp(context.packageName)
    }

    fun isAdminActive(): Boolean {
        return devicePolicyManager.isAdminActive(adminComponent)
    }

    fun getDeviceInfo(): Map<String, Any> {
        return mapOf(
            "isDeviceOwner" to isDeviceOwner(),
            "isProfileOwner" to isProfileOwner(),
            "isAdminActive" to isAdminActive(),
            "packageName" to context.packageName
        )
    }

    fun lockDevice() {
        if (isDeviceOwner() || isProfileOwner()) {
            devicePolicyManager.lockNow()
        }
    }

    fun setCameraDisabled(disabled: Boolean) {
        if (isDeviceOwner() || isProfileOwner()) {
            devicePolicyManager.setCameraDisabled(adminComponent, disabled)
        }
    }

    fun wipeData() {
        if (isDeviceOwner() || isProfileOwner()) {
            devicePolicyManager.wipeData(0)
        }
    }
}