package de.eant.abandonscanner.data.repository

import de.eant.abandonscanner.data.local.QRCode
import de.eant.abandonscanner.data.local.QRCodeDao
import de.eant.abandonscanner.domain.repository.QRCodeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QRCodeRepositoryImpl @Inject constructor(
    private val qrCodeDao: QRCodeDao
) : QRCodeRepository {
    
    override fun getAllQRCodes(): Flow<List<QRCode>> {
        return qrCodeDao.getAllQRCodes()
    }
    
    override suspend fun insert(qrCode: QRCode) {
        qrCodeDao.insert(qrCode)
    }
    
    override suspend fun getQRCodeByContent(content: String): QRCode? {
        return qrCodeDao.getQRCodeByContent(content)
    }
    
    override suspend fun deleteAll() {
        qrCodeDao.deleteAll()
    }
} 