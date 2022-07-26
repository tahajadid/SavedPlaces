package tahadeta.example.savedplaces.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import tahadeta.example.savedplaces.R
import tahadeta.example.savedplaces.databinding.ActivityMapsBinding
import tahadeta.example.savedplaces.helper.*
import tahadeta.example.savedplaces.helper.favouritePlaceActual
import tahadeta.example.savedplaces.helper.isSet
import tahadeta.example.savedplaces.helper.listFavouritePlaces
import tahadeta.example.savedplaces.model.FavouritePlace
import tahadeta.example.savedplaces.ui.demo.DemoActivity

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var listRecyclerView: RecyclerView
    private lateinit var infoImageView: ImageView
    private lateinit var addAnimation: LottieAnimationView
    private lateinit var localisationAnimation: LottieAnimationView
    lateinit var favouriteAdapter: FavouriteAdapter

    private var locationPermissionGranted = false
    private var lastKnownLocation: Location? = null

    var fusedLocationProviderClient: FusedLocationProviderClient? = null

    lateinit var actualGoogleMap: GoogleMap

    var actualLat = "0.0"
    var actualLng = "0.0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addAnimation = findViewById(R.id.add_favourite_iv)
        localisationAnimation = findViewById(R.id.mylocation_iv)
        infoImageView = findViewById(R.id.info_iv)

        listRecyclerView = findViewById(R.id.list_favourite_fragment)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Build the map.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        // Remove Data
        // ModelPreferencesManager.removeData(Constants.LIST_FAV)

        localisationAnimation.setOnClickListener {
            if (LocationHelper.isLocationEnabled(context = this.baseContext) == true)
                getDeviceLocation()
            else showActivateLocation()
        }

        addAnimation.setOnClickListener {
            if (actualLat.equals("0.0") && actualLng.equals("0.0")) getDeviceLocation()
            showAddLayout()
        }

        infoImageView.setOnClickListener {
            val intent = Intent(this, DemoActivity::class.java)
            startActivity(intent)
            finish()
        }

        initFavouritePlaces()
    }

    private fun showActivateLocation() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.enable_location)
        var goSetting = dialog.findViewById<TextView>(R.id.goSetting)
        var cancel = dialog.findViewById<TextView>(R.id.cancel)

        goSetting.setOnClickListener {
            val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            dialog.dismiss()
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        val width = resources.displayMetrics.widthPixels * 0.80
        val height = resources.displayMetrics.heightPixels * 0.40

        dialog.window?.setLayout(width.toInt(), height.toInt())
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun showAddLayout() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.add_place)
        var okBtn = dialog.findViewById<TextView>(R.id.addLabel_tv)
        var placeName = dialog.findViewById<TextView>(R.id.label_et)

        okBtn.setOnClickListener {
            addItemToList(FavouritePlace(actualLat, actualLng, placeName.text.toString()))
            dialog.dismiss()
        }

        val width = resources.displayMetrics.widthPixels * 0.80
        val height = resources.displayMetrics.heightPixels * 0.35

        dialog.window?.setLayout(width.toInt(), height.toInt())
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun addItemToList(favouritePlace: FavouritePlace) {
        listFavouritePlaces.add(favouritePlace)
        favouriteAdapter.notifyDataSetChanged()
    }

    private fun initFavouritePlaces() {

        favouriteAdapter =
            FavouriteAdapter(this.baseContext, listFavouritePlaces)

        listRecyclerView.apply {
            layoutManager = GridLayoutManager(this.context, 1)
            adapter = favouriteAdapter
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    // [START maps_current_place_update_location_ui]
    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
    // [END maps_current_place_update_location_ui]

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        actualGoogleMap = googleMap

        getLocationPermission()
        // [END_EXCLUDE]

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()

        if (isSet) {
            goToThePlace(favouritePlaceActual)
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    // [START maps_current_place_get_device_location]
    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient!!.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ),
                                    DEFAULT_ZOOM.toFloat()
                                )
                            )
                            actualLat = lastKnownLocation!!.latitude.toString()
                            actualLng = lastKnownLocation!!.longitude.toString()
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
    // [END maps_current_place_get_device_location]

    /**
     * Prompts the user for permission to use the device location.
     */
    // [START maps_current_place_location_permission]
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }
    // [END maps_current_place_location_permission]

    /**
     * Handles the result of the request for location permissions.
     */
    // [START maps_current_place_on_request_permissions_result]
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUI()
    }
    // [END maps_current_place_on_request_permissions_result]

    /**
     * Function to change place
     */
    fun goToThePlace(favouritePlace: FavouritePlace) {
        val newlatLng = LatLng(favouritePlace.lat.toDouble(), favouritePlace.lng.toDouble())
        val markerOptions = MarkerOptions().position(newlatLng).title(favouritePlace.title)
        actualGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(newlatLng))
        actualGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newlatLng, 15f))
        actualGoogleMap.addMarker(markerOptions)
    }

    companion object {
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }
}
