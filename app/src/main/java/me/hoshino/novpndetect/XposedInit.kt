package me.hoshino.novpndetect

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import me.hoshino.novpndetect.hooks.HookConnectivityManager
import me.hoshino.novpndetect.hooks.HookNetworkCapabilities
import me.hoshino.novpndetect.hooks.HookNetworkInfo
import me.hoshino.novpndetect.hooks.HookNetworkInterface

class XposedInit : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        lpparam ?: return
        XposedBridge.log("[NVD] handleLoadPackage: ${lpparam.packageName}")

        val hooks =
            arrayOf(
                HookConnectivityManager(),
                HookNetworkInterface(),
                HookNetworkCapabilities(),
                HookNetworkInfo()
            )

        hooks.forEach {
            it.injectHook()
        }
    }

//    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
//        startupParam ?: return
//        XposedBridge.log("[NVD] initZygote")
//        // initialize zygote to load resources
//    }
}