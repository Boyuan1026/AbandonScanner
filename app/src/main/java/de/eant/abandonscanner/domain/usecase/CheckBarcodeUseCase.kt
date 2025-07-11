package de.eant.abandonscanner.domain.usecase

import de.eant.abandonscanner.domain.repository.QRCodeRepository
import javax.inject.Inject

class CheckBarcodeUseCase @Inject constructor(
    private val repository: QRCodeRepository
) {
    suspend operator fun invoke(content: String): Boolean {
        return repository.getQRCodeByContent(content) != null
    }
} 