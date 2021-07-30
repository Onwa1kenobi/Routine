package com.ugochukwu.routine.data.model

import android.os.Parcel
import android.os.Parcelable

class Routine(
    var id: Int = 0,
    var title: String,
    var description: String,
    var hourOfDay: Int,
    var minuteOfHour: Int,
    var frequency: Frequency,
    var lastRunTime: Long,
    var doneCount: Int = 0,
    var missCount: Int = 0,
) : Parcelable {

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.let {
            it.writeInt(id)
            it.writeString(title)
            it.writeString(description)
            it.writeInt(hourOfDay)
            it.writeInt(minuteOfHour)
            it.writeSerializable(frequency)
            it.writeLong(lastRunTime)
            it.writeInt(doneCount)
            it.writeInt(missCount)
        }
    }

    constructor(internal: Parcel) : this(
        id = internal.readInt(),
        title = internal.readString().orEmpty(),
        description = internal.readString().orEmpty(),
        hourOfDay = internal.readInt(),
        minuteOfHour = internal.readInt(),
        frequency = (internal.readSerializable() ?: Frequency.DAILY) as Frequency,
        lastRunTime = internal.readLong(),
        doneCount = internal.readInt(),
        missCount = internal.readInt(),
    )

    companion object CREATOR : Parcelable.Creator<Routine> {
        override fun createFromParcel(parcel: Parcel): Routine {
            return Routine(parcel)
        }

        override fun newArray(size: Int): Array<Routine?> {
            return arrayOfNulls(size)
        }
    }
}