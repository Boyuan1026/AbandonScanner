package de.eant.abandonscanner.domain.repository

import de.eant.abandonscanner.data.local.QRCode
import kotlinx.coroutines.flow.Flow

interface QRCodeRepository {
    fun getAllQRCodes(): Flow<List<QRCode>>
    suspend fun insert(qrCode: QRCode)
    suspend fun getQRCodeByContent(content: String): QRCode?
    suspend fun deleteAll()
} 