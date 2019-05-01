package com.jd.notificationscollector.model

data class Notification(
    var id: Int?,
    var title: String?,
    var text: String?,
    var bigText: String?,
    var timestamp: Long?
) {
    constructor(title: String?, text: String?, bigText: String?, timestamp: Long?) : this(null, title, text, bigText, timestamp)
}
