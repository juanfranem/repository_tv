package es.jfechevarria.domain

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Channel(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val url: String?,
    val name: String?,
    val checked: String?
): Parcelable {
    constructor() : this(0, "", "", "")
}