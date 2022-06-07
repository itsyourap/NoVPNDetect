package me.hoshino.novpndetect.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import me.hoshino.novpndetect.XHook
import java.net.NetworkInterface

class HookNetworkInterface : XHook {

    override val targetKlass: String
        get() = "android.net.NetworkInterface"

    override fun injectHook() {
        hookIsUp()
    }

    private fun hookIsUp() {
        XposedHelpers.findAndHookMethod(NetworkInterface::class.java, "isUp", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                // make apps unable to detect vpn
                val name = (param.thisObject as NetworkInterface).name
                XposedBridge.log("[NVD] NetworkInterface.isUp ($name)")
                if (name.startsWith("tun") || name.startsWith("ppp") || name.startsWith("pptp")) {
                    param.result = false
                }
            }
        })
    }
}