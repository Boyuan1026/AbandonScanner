package de.eant.abandonscanner.presentation.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import de.eant.abandonscanner.R
import de.eant.abandonscanner.presentation.viewmodel.EntryScanViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EntryScanActivity : AppCompatActivity() {
    private val viewModel: EntryScanViewModel by viewModels()

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            // 录入废弃条形码
            viewModel.addBarcode(result.contents)
        } else {
            Toast.makeText(this@EntryScanActivity, "Scan fehlgeschlagen, bitte versuchen Sie es erneut", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_scan)
        
        observeViewModel()
        startZXingScan()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.scanResult.collect { result ->
                result?.let {
                    Toast.makeText(this@EntryScanActivity, it, Toast.LENGTH_LONG).show()
                    viewModel.clearScanResult()
                    // 返回主页面
                    finish()
                }
            }
        }
    }

    private fun startZXingScan() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
        options.setPrompt("Bitte richten Sie die Kamera auf den Barcode")
        options.setBeepEnabled(true)
        options.setCameraId(0)
        options.setOrientationLocked(true)
        barcodeLauncher.launch(options)
    }
} 