package com.renogy.device.rdpdemo

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils

/**
 * @author Create by 17474 on 2024/11/12.
 * Email： lishuwentimor1994@163.com
 * Describe：
 */
class SendCmdEntity(
    var bleMac: String,
    var cmd: String,
    var cmdTag: String,
    var deviceAddress: String = "",
    var boSkip: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readByte() != 0.toByte()
    )

    fun getKey(): String {
        return bleMac + deviceAddress
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        other?.let {
            if (it is SendCmdEntity) {
                return  it.bleMac == this.bleMac && it.deviceAddress == this.deviceAddress && it.cmdTag == this.cmdTag
            } else {
                return false
            }
        } ?: let {
            return false
        }
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bleMac)
        parcel.writeString(cmd)
        parcel.writeString(cmdTag)
        parcel.writeString(deviceAddress)
        parcel.writeByte(if (boSkip) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun hashCode(): Int {
        var result = bleMac.hashCode()
        result = 31 * result + cmd.hashCode()
        result = 31 * result + cmdTag.hashCode()
        result = 31 * result + deviceAddress.hashCode()
        result = 31 * result + boSkip.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<SendCmdEntity> {
        override fun createFromParcel(parcel: Parcel): SendCmdEntity {
            return SendCmdEntity(parcel)
        }

        override fun newArray(size: Int): Array<SendCmdEntity?> {
            return arrayOfNulls(size)
        }
    }
}