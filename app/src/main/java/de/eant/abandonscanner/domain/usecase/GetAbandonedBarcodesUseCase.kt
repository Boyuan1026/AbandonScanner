package de.eant.abandonscanner.domain.usecase

import de.eant.abandonscanner.data.local.QRCode
import de.eant.abandonscanner.domain.repository.QRCodeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAbandonedBarcodesUseCase @Inject constructor(
    private val repository: QRCodeRepository
) {
    operator fun invoke(): Flow<List<QRCode>> {
        return repository.getAllQRCodes()
    }
} 