package com.jd.notificationscollector.model

import android.graphics.drawable.Drawable

data class Notification(
    var id: Int?,
    var title: String?,
    var text: String?,
    var bigText: String?,
    var packageName: String?,
    var timestamp: Long?,
    var icon: Drawable?
) {
    constructor(title: String?, text: String?, bigText: String?, packageName: String?, timestamp: Long?, icon: Drawable?) : this(null, title, text, bigText, packageName, timestamp, icon)
}
