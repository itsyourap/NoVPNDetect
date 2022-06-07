package me.hoshino.novpndetect.hooks

import android.net.NetworkCapabilities
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import me.hoshino.novpndetect.XHook

class HookNetworkCapabilities : XHook {

    override val targetKlass: String
        get() = "android.os.NetworkCapabilities"

    override fun injectHook() {
        hookHasTransport()
        hookGetCapabilities()
        hookHasCapability()
    }

    private fun hookHasTransport() {
        XposedHelpers.findAndHookMethod(NetworkCapabilities::class.java, "hasTransport", Int::class.java, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                XposedBridge.log("[NVD] NetworkCapabilities.hasTransport (${param.args[0]})")
                if (param.args[0] == NetworkCapabilities.TRANSPORT_VPN) {
                    param.result = false
                }
            }
        })
    }

    private fun hookGetCapabilities() {
        XposedHelpers.findAndHookMethod(NetworkCapabilities::class.java, "getCapabilities", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                XposedBridge.log("[NVD] NetworkCapabilities.getCapabilities")
                param.result ?: return
                val result = param.result as IntArray
                if (!result.contains(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)) {
                    val newResult = IntArray(result.size + 1)
                    result.forEachIndexed { index, i ->
                        newResult[index] = i
                    }
                    newResult[newResult.size - 1] = NetworkCapabilities.NET_CAPABILITY_NOT_VPN
                    param.result = newResult
                }
            }
        })
    }

    private fun hookHasCapability() {
        XposedHelpers.findAndHookMethod(NetworkCapabilities::class.java, "hasCapability", Int::class.java, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                XposedBridge.log("[NVD] NetworkCapabilities.hasCapability (${param.args[0]})")
                if (param.args[0] == NetworkCapabilities.NET_CAPABILITY_NOT_VPN) {
                    param.result = true
                }
            }
        })
    }
}