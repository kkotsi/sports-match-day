package com.example.sports_match_day.ui.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.sports_match_day.R
import com.example.sports_match_day.databinding.FragmentMapsBinding
import com.example.sports_match_day.ui.home.manage.MatchesManageFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private val args: MapsFragmentArgs by navArgs()
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private var myStadium = LatLng(-34.0, 151.0)
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myStadium))
        googleMap.addMarker(MarkerOptions().position(myStadium).title("Your stadium"))
        googleMap.setOnMapClickListener {
            myStadium = it
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(it).title("Your stadium"))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSaveButton() {
        binding.fabAdd.setOnClickListener {
            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            navController.previousBackStackEntry?.savedStateHandle?.set(
                MatchesManageFragment.ADD_MATCH_STADIUM_LOCATION,
                myStadium
            )
            navController.navigateUp()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSaveButton()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        args.stadiumLocation?.let {
            myStadium = it
        }
        mapFragment?.getMapAsync(callback)
    }
}