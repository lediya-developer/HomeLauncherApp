package com.lediya.appmanager.model

import android.graphics.drawable.Drawable

class AppDetail {
    var label: CharSequence? = null
    var packageName: CharSequence? = null
    var icon: Drawable? = null
    var launcherClass:String?= null
    var versionCode:Long?= 0
    var versionName:String?= null
}

