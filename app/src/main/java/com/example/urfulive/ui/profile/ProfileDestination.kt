import android.os.Parcel
import android.os.Parcelable

data class ProfileDestination(
    val username: String? = null,
    val isOwnProfile: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt() == 1
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeInt(if (isOwnProfile) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProfileDestination> {
        override fun createFromParcel(parcel: Parcel): ProfileDestination {
            return ProfileDestination(parcel)
        }

        override fun newArray(size: Int): Array<ProfileDestination?> {
            return arrayOfNulls(size)
        }
    }
}

