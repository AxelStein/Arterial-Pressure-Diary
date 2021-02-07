package com.axel_stein.ap_diary.data.room.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Suppress("unused")
@Entity(tableName = "pulse_log")
data class PulseLog(
    var value: Int = 0,

    @ColumnInfo(name = "date_time")
    var dateTime: DateTime = DateTime(),

    var comment: String? = null,
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id = 0L

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<PulseLog> {
            override fun createFromParcel(source: Parcel?) = PulseLog(source)
            override fun newArray(size: Int) = arrayOfNulls<PulseLog>(size)
        }
    }

    private constructor(parcel: Parcel?) : this(
        value = parcel?.readInt() ?: 0,
        dateTime = DateTime(parcel?.readString()),
        comment = parcel?.readString(),
    ) {
        id = parcel?.readLong() ?: 0L
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(value)
        dest?.writeString(dateTime.toString())
        dest?.writeString(comment)
        dest?.writeLong(id)
    }
}