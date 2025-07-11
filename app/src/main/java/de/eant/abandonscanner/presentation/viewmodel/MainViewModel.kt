package de.eant.abandonscanner.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.eant.abandonscanner.data.local.QRCode
import de.eant.abandonscanner.domain.usecase.ClearBarcodesUseCase
import de.eant.abandonscanner.domain.usecase.GetAbandonedBarcodesUseCase
import de.eant.abandonscanner.domain.usecase.ImportBarcodesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAbandonedBarcodesUseCase: GetAbandonedBarcodesUseCase,
    private val clearBarcodesUseCase: ClearBarcodesUseCase,
    private val importBarcodesUseCase: ImportBarcodesUseCase
) : ViewModel() {

    private val _qrCodes = MutableStateFlow<List<QRCode>>(emptyList())
    val qrCodes: StateFlow<List<QRCode>> = _qrCodes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        loadQRCodes()
    }

    private fun loadQRCodes() {
        viewModelScope.launch {
            getAbandonedBarcodesUseCase().collect { codes ->
                _qrCodes.value = codes
            }
        }
    }

    fun clearAllBarcodes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                clearBarcodesUseCase()
                _message.value = "Alle entsorgten Barcodes wurden gelöscht"
            } catch (e: Exception) {
                _message.value = "Fehler beim Löschen: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun importBarcodesFromTxt(uri: Uri, contentResolver: android.content.ContentResolver) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val inputStream = contentResolver.openInputStream(uri)
                    ?: throw Exception("Datei kann nicht geöffnet werden")
                
                val lines = inputStream.bufferedReader().readLines()
                inputStream.close()
                
                val codes = lines.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
                if (codes.isEmpty()) throw Exception("Datei ist leer oder enthält keine gültigen Barcodes")
                
                importBarcodesUseCase(codes)
                _message.value = "Import erfolgreich, ${codes.size} Barcodes importiert"
            } catch (e: Exception) {
                _message.value = "Import fehlgeschlagen: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
} 