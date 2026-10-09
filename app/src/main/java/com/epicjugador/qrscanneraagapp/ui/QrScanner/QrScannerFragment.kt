package com.epicjugador.qrscanneraagapp.ui.QrScanner

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.epicjugador.qrscanneraagapp.databinding.FragmentQrScannerBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QrScannerFragment : Fragment() {

    private var _binding: FragmentQrScannerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QrScannerViewModel by viewModels()



    @RequiresApi(Build.VERSION_CODES.O)
    private val scanLauncher =
        registerForActivityResult(ScanContract()) { result ->
            if (result.contents != null) {
                viewModel.processQr(result.contents)
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) showCamera()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fab.setOnClickListener {
            checkPermissionCamera()
        }

        viewModel.registerDeviceIfNeeded()

        viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                val action =
                    QrScannerFragmentDirections
                        .actionQrScannerFragmentToInfoFragment(result)
                findNavController().navigate(action)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.loadingOverlay.visibility = View.VISIBLE
            } else {
                binding.loadingOverlay.visibility = View.GONE
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkPermissionCamera() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> showCamera()

            else -> requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCamera() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt("Escaneá el código QR")
            setCameraId(0)
            setBeepEnabled(false)
        }
        scanLauncher.launch(options)
    }
}
