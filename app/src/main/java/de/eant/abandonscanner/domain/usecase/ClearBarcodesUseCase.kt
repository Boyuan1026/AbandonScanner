package de.eant.abandonscanner.domain.usecase

import de.eant.abandonscanner.domain.repository.QRCodeRepository
import javax.inject.Inject

class ClearBarcodesUseCase @Inject constructor(
    private val repository: QRCodeRepository
) {
    suspend operator fun invoke() {
        repository.deleteAll()
    }
} 