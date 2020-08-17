package com.cha.firebaseapp.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cha.firebaseapp.R
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_detail.*
import java.lang.Exception

class DetailActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    LocationListener, GoogleMap.OnMarkerClickListener {

    private lateinit var fragmentMap: SupportMapFragment
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude = 0.0
    private var longitude = 0.0
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private var idReceived        : String ?= null
    private var nameReceived      : String ?= null
    private var emailReceived     : String ?= null
    private var latitudeReceived  : String ?= null
    private var longitudeReceived : String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        fragmentMap = supportFragmentManager.findFragmentById(R.id.mapUser) as SupportMapFragment
        fragmentMap.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addApi(LocationServices.API)
            .build()

        locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval( 10 * 100 )
            .setExpirationDuration( 10 * 100 )

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for ( location in locationResult.locations) {
                    setNewLocation(location)
                }
            }
        }

        getDataReceived()

        // BottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    @SuppressLint("SetTextI18n")
    private fun getDataReceived() {
        idReceived        = intent.extras?.getString("id")
        nameReceived      = intent.extras?.getString("name")
        emailReceived     = intent.extras?.getString("email")
        latitudeReceived  = intent.extras?.getString("latitude")
        longitudeReceived = intent.extras?.getString("longitude")

        tv_name.text = nameReceived
        tv_id_user.text = idReceived
        tv_email.text = emailReceived
    }

    private fun setNewLocation(location: Location) {
        latitude = location.latitude
        longitude = location.longitude

        val currentPosition = LatLng(latitude, longitude)

        googleMap.addMarker(MarkerOptions().position(currentPosition))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 17f))
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.setOnMarkerClickListener(this)
        googleMap.setOnMapClickListener {
            try {
                val i = Intent(
                    Intent.ACTION_VIEW, Uri.parse(
                    "geo:<${it?.latitude}>,<${it?.longitude}>?q=" +
                            "<${it?.latitude}>,<${it?.longitude}>" +
                            "(${getString(R.string.app_name)})"
                ))
                i.setClassName(
                    "com.google.android.apps.maps",
                    "com.google.android.maps.MapsActivity"
                )
                startActivity(i)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        googleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragEnd(marker: Marker?) {
                println("DragEndLatitude:  ${marker?.position?.latitude}")
                println("DragEndLongitude:  ${marker?.position?.longitude}")
            }

            override fun onMarkerDragStart(marker: Marker?) {
                println("DragStartLatitude:  ${marker?.position?.latitude}")
                println("DragStartLongitude:  ${marker?.position?.longitude}")
            }

            override fun onMarkerDrag(marker: Marker?) {
                println("DragLatitude:  ${marker?.position?.latitude}")
                println("DragLongitude:  ${marker?.position?.longitude}")
            }
        })

        googleMap.uiSettings.setAllGesturesEnabled(true)

        googleMap.isBuildingsEnabled = false
        loadUserMark()
    }

    private fun loadUserMark() {
        val userPosition = LatLng(latitudeReceived?.toDouble()!!, longitudeReceived?.toDouble()!!)

        googleMap.addMarker(
            MarkerOptions()
                .position(userPosition)
                .title("$idReceived - $nameReceived")
                .draggable(true)
        )

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 12f))
        getLocationPermission()
    }


    private fun getLocationPermission(){
        if ((ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
            &&
            (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
        ) {
            updateLocationUI()
        }else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION),
                100
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            100 -> {
                if ( grantResults.isNotEmpty() ) {
                    updateLocationUI()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun updateLocationUI(){
        googleMap.isTrafficEnabled = true
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
    }

    override fun onConnected(p0: Bundle?) {
        //
    }

    override fun onConnectionSuspended(p0: Int) {
        //
    }

    override fun onLocationChanged(location: Location) {
        setNewLocation(location)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        //
    }

    override fun onProviderEnabled(provider: String?) {
        //
    }

    override fun onProviderDisabled(provider: String?) {
        //
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        println(marker?.position?.latitude)
        println(marker?.position?.longitude)

        tv_position.text = marker?.position.toString()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        return true
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, DetailActivity::class.java))
        }
    }
}