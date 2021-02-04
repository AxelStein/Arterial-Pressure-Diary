package com.axel_stein.ap_diary.data.room.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Suppress("unused")
@Entity(tableName = "ap_log")
data class ApLog(
    var systolic: Int = 0,
    var diastolic: Int = 0,

    @ColumnInfo(name = "date_time")
    var dateTime: DateTime = DateTime(),

    var comment: String? = null,
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id = 0L

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<ApLog> {
            override fun createFromParcel(source: Parcel?) = ApLog(source)

            override fun newArray(size: Int) = arrayOfNulls<ApLog>(size)
        }
    }

    private constructor(parcel: Parcel?) : this(
        systolic = parcel?.readInt() ?: 0,
        diastolic = parcel?.readInt() ?: 0,
        dateTime = DateTime(parcel?.readString()),
        comment = parcel?.readString(),
    ) {
        id = parcel?.readLong() ?: 0L
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(systolic)
        dest?.writeInt(diastolic)
        dest?.writeString(dateTime.toString())
        dest?.writeString(comment)
        dest?.writeLong(id)
    }
}