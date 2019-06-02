package com.jd.notificationscollector.dao

import androidx.room.Insert

interface BaseDao<T> {

    @Insert
    fun insert(entity: T): Long

}
