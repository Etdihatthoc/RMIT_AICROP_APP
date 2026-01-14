package com.example.ai_crop_doctor.presentation.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ai_crop_doctor.R
import com.example.ai_crop_doctor.databinding.FragmentMapBinding
import com.example.ai_crop_doctor.domain.model.EpidemicAlert
import com.example.ai_crop_doctor.util.LocationData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private var selectedAlert: EpidemicAlert? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMap()
        setupBottomSheet()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        Timber.d("Google Map is ready")

        // Setup map settings
        map.apply {
            uiSettings.apply {
                isZoomControlsEnabled = true
                isCompassEnabled = true
                isMyLocationButtonEnabled = false // Use custom button
            }

            // Move camera to Vietnam (An Giang)
            moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(10.5, 105.5), 8f))
        }

        // Load epidemic alerts
        viewModel.loadEpidemicAlerts()
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun setupClickListeners() {
        binding.fabMyLocation.setOnClickListener {
            viewModel.getCurrentLocation()
        }

        binding.fabFilter.setOnClickListener {
            // TODO: Show filter dialog
            Toast.makeText(context, "Filter coming soon", Toast.LENGTH_SHORT).show()
        }

        binding.btnViewDetails.setOnClickListener {
            selectedAlert?.let { alert ->
                // TODO: Navigate to alert details
                Toast.makeText(context, "Details for ${alert.diseaseType}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.epidemicAlerts.observe(viewLifecycleOwner) { alerts ->
            displayEpidemicMarkers(alerts)
            binding.tvAlertsCount.text = "${alerts.size} cảnh báo dịch bệnh"
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.currentLocation.observe(viewLifecycleOwner) { location ->
            location?.let {
                googleMap?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(it.latitude, it.longitude),
                        12f
                    )
                )
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    private fun displayEpidemicMarkers(alerts: List<EpidemicAlert>) {
        googleMap?.clear()

        alerts.forEach { alert ->
            val position = LatLng(alert.latitude, alert.longitude)

            // Add circle for affected area
            googleMap?.addCircle(
                CircleOptions()
                    .center(position)
                    .radius(alert.radius * 1000) // Convert km to meters
                    .strokeColor(alert.getSeverityColor())
                    .strokeWidth(3f)
                    .fillColor(alert.getSeverityColor() and 0x40FFFFFF.toInt()) // 25% opacity
            )

            // Add marker
            val marker = googleMap?.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(alert.getMarkerTitle())
                    .snippet(alert.getMarkerSnippet())
                    .icon(BitmapDescriptorFactory.defaultMarker(
                        when (alert.severity.lowercase()) {
                            "high" -> BitmapDescriptorFactory.HUE_RED
                            "medium" -> BitmapDescriptorFactory.HUE_ORANGE
                            else -> BitmapDescriptorFactory.HUE_YELLOW
                        }
                    ))
            )

            marker?.tag = alert
        }

        // Set marker click listener
        googleMap?.setOnMarkerClickListener { marker ->
            val alert = marker.tag as? EpidemicAlert
            alert?.let { showAlertDetails(it) }
            true
        }
    }

    private fun showAlertDetails(alert: EpidemicAlert) {
        selectedAlert = alert

        binding.apply {
            tvAlertTitle.text = alert.getMarkerTitle()
            tvAlertArea.text = alert.affectedArea
            tvAlertCases.text = "${alert.caseCount} ca nhiễm"
            tvAlertDescription.text = alert.description
            bottomSheet.visibility = View.VISIBLE
        }

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
