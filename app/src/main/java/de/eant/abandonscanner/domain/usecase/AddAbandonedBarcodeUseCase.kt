package de.eant.abandonscanner.domain.usecase

import de.eant.abandonscanner.data.local.QRCode
import de.eant.abandonscanner.domain.repository.QRCodeRepository
import javax.inject.Inject

class AddAbandonedBarcodeUseCase @Inject constructor(
    private val repository: QRCodeRepository
) {
    suspend operator fun invoke(content: String) {
        val qrCode = QRCode(content = content)
        repository.insert(qrCode)
    }
} 