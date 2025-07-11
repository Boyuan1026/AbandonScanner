package de.eant.abandonscanner.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QRCodeDao {
    @Query("SELECT * FROM qr_codes")
    fun getAllQRCodes(): Flow<List<QRCode>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qrCode: QRCode)

    @Query("SELECT * FROM qr_codes WHERE content = :content LIMIT 1")
    suspend fun getQRCodeByContent(content: String): QRCode?

    @Query("DELETE FROM qr_codes")
    suspend fun deleteAll()
} 