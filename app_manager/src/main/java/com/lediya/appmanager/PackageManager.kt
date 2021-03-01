package com.lediya.appmanager

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Build
import androidx.annotation.RequiresApi
import com.lediya.appmanager.model.AppDetail
import android.content.pm.PackageManager

class PackageManager(packageManager: PackageManager) {
    private var manager: PackageManager = packageManager
    private lateinit var apps: MutableList<AppDetail>
    @RequiresApi(Build.VERSION_CODES.P)
    fun loadApps(): MutableList<AppDetail> {
        apps = mutableListOf()
        val i = Intent(Intent.ACTION_MAIN, null)
        i.addCategory(Intent.CATEGORY_LAUNCHER)
        val availableActivities = manager.queryIntentActivities(i, 0)
        for (ri in availableActivities) {
            val app = AppDetail()
            app.label = ri.loadLabel(manager)
            app.packageName = ri.activityInfo.packageName
            app.icon = ri.activityInfo.loadIcon(manager)
            app.launcherClass = ri.activityInfo.name
            app.versionName = manager.getPackageInfo(ri.activityInfo.packageName,0).versionName
            app.versionCode = manager.getPackageInfo(ri.activityInfo.packageName,0).longVersionCode
            apps.add(app)
        }
        return apps
    }
    fun getApplicationName(context: Context,intent: Intent):String{
        val ai: ApplicationInfo?
        ai = try {
            intent.data?.schemeSpecificPart?.let { manager.getApplicationInfo(it, 0) }
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return (if (ai != null) manager.getApplicationLabel(ai) else "(unknown)") as String
    }
    fun getPackageManager():PackageManager{
        return manager
    }
}