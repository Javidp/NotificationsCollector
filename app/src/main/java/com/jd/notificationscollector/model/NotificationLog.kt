package com.jd.notificationscollector.model

data class NotificationLog (var id: Long?, var notificationId: Long?, var sbn: String?, var notification: String?, var extras: String?) {
    constructor(notificationId: Long?, sbn: String?, notification: String?, extras: String?) : this(null, notificationId, sbn, notification, extras)
}
