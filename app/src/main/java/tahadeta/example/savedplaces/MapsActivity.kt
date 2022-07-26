package tahadeta.example.savedplaces

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import tahadeta.example.savedplaces.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var permissionLocation = false
    private lateinit var mylocation: ImageView

    var currentLocation: Location? = null
    var fusedLocationProviderClient: FusedLocationProviderClient? = null

    lateinit var actualGoogleMap: GoogleMap

    private val LOCATION_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()

        mylocation = findViewById(R.id.mylocation_iv)
        mylocation.setOnClickListener {
            fetchLocation()
            // goToThePlace()
        }

        enableMyLocation()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        actualGoogleMap = googleMap
        /*
        if (permissionLocation) map.isMyLocationEnabled = true
        Log.d(
            "MapsActivityLog",
            "######## State of Localisation =:= " +
                LocationHelper.isLocationEnabled(this.baseContext)
        )

         */

        val latLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
        Log.d("MapsActivityLog", "** task value : ${latLng.longitude}")

        val markerOptions = MarkerOptions().position(latLng).title("I Am Here!")
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        googleMap.addMarker(markerOptions)

        /*
        val intent1 = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent1)
         */
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        val task = fusedLocationProviderClient!!.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                Log.d("MapsActivityLog", "******* task value : ${location.longitude}")

                val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)
                supportMapFragment!!.getMapAsync(this@MapsActivity)
            }
        }
    }

    /**
     * Function to change place
     */
    fun goToThePlace(lat: Double, lng: Double, title: String) {
        val newlatLng = LatLng(lat, lng)
        val markerOptions = MarkerOptions().position(newlatLng).title(title)
        actualGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(newlatLng))
        actualGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newlatLng, 15f))
        actualGoogleMap.addMarker(markerOptions)
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {

        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("MapsActivityLog", "Inside on 1")
            permissionLocation = true
            return
        }

        // 2. If a permission rationale dialog should be shown
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
        ) {
            Log.d("MapsActivityLog", "Inside on 2")
            return
        }

        // 3. Otherwise, request permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )

        Log.d("MapsActivityLog", "Inside on enableMyLocation()")
    }

    /**
     * onRequestPermissionsResult
     */
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("MapsActivityLog", "Inside on permission result 111")

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // map.isMyLocationEnabled = true
            Log.d("MapsActivityLog", "Inside on permission result 222")
        }
    }
}
