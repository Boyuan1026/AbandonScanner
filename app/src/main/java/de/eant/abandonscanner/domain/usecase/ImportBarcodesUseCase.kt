package de.eant.abandonscanner.domain.usecase

import de.eant.abandonscanner.data.local.QRCode
import de.eant.abandonscanner.domain.repository.QRCodeRepository
import javax.inject.Inject

class ImportBarcodesUseCase @Inject constructor(
    private val repository: QRCodeRepository
) {
    suspend operator fun invoke(barcodes: List<String>) {
        // 先清空现有数据
        repository.deleteAll()
        
        // 添加新的条形码
        barcodes.forEach { barcode ->
            val qrCode = QRCode(content = barcode)
            repository.insert(qrCode)
        }
    }
} 