package de.eant.abandonscanner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.eant.abandonscanner.domain.usecase.CheckBarcodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckScanViewModel @Inject constructor(
    private val checkBarcodeUseCase: CheckBarcodeUseCase
) : ViewModel() {

    private val _checkResult = MutableStateFlow<CheckResult?>(null)
    val checkResult: StateFlow<CheckResult?> = _checkResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun checkBarcode(content: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val isAbandoned = checkBarcodeUseCase(content.trim())
                _checkResult.value = CheckResult(
                    content = content,
                    isAbandoned = isAbandoned,
                    message = if (isAbandoned) {
                        "Dieser Barcode ist entsorgt!"
                    } else {
                        "Dieser Barcode kann verwendet werden"
                    }
                )
            } catch (e: Exception) {
                _checkResult.value = CheckResult(
                    content = content,
                    isAbandoned = false,
                    message = "Fehler beim Prüfen: ${e.message}"
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearCheckResult() {
        _checkResult.value = null
    }

    data class CheckResult(
        val content: String,
        val isAbandoned: Boolean,
        val message: String
    )
} 