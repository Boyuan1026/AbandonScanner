package de.eant.abandonscanner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.eant.abandonscanner.domain.usecase.AddAbandonedBarcodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntryScanViewModel @Inject constructor(
    private val addAbandonedBarcodeUseCase: AddAbandonedBarcodeUseCase
) : ViewModel() {

    private val _scanResult = MutableStateFlow<String?>(null)
    val scanResult: StateFlow<String?> = _scanResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun addBarcode(content: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                addAbandonedBarcodeUseCase(content.trim())
                _scanResult.value = "Entsorgter Barcode erfasst: $content"
            } catch (e: Exception) {
                _scanResult.value = "Fehler beim Erfassen: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearScanResult() {
        _scanResult.value = null
    }
} 