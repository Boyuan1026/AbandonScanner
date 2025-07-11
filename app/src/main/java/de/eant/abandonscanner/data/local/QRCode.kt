package de.eant.abandonscanner.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_codes")
data class QRCode(
    @PrimaryKey
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
) 