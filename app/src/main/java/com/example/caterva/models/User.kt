package com.example.caterva.models

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter.writeString

data class User (
    val id: String = "", // id пользователя
    val name: String = "", // Имя пользователя
    val email: String = "", // электронная почта
    val image: String = "", // аватарка пользователя
    val phoneNumber: Long = 0, // номер телефона
    val fcmToken: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readLong(),
            parcel.readString()!!
    ) {
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeString(email)
        writeString(image)
        writeLong(phoneNumber)
        writeString(fcmToken)

    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}