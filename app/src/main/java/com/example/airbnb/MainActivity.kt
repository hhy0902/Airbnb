package com.example.airbnb

import android.content.ClipData
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.airbnb.model.HouseDto
import com.example.airbnb.model.HouseModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import com.naver.maps.map.widget.LocationButtonView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback, Overlay.OnClickListener {

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    private val currentLocationButton : LocationButtonView by lazy {
        findViewById(R.id.currentLocationButton)
    }

    private val viewPager : ViewPager2 by lazy {
        findViewById(R.id.houseViewPager)
    }

    private val viewPagerAdapter = HouseViewPagerAdapter(itemClicked = {
        val intent = Intent()
            .apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "[지금 이 가격에 예약하세요!!!] ${it.title} ${it.price} 사진보기 : ${it.imageUrl}")
                type = "text/plain"
            }
        startActivity(Intent.createChooser(intent, null))
    })
    private val recyclerViewAdapter = HouseListAdapter()

    private val mapView : MapView by lazy {
        findViewById(R.id.mapView)
    }

    private val recyclerView : RecyclerView by lazy {
        findViewById(R.id.recyclerView)
    }

    private val bottomSheetTextView : TextView by lazy {
        findViewById(R.id.bottomSheetTitleTextView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val address = "https://run.mocky.io/v3/f76e4040-abed-48c8-bfb6-5ecabc7ddb5a"
        val addres2 = "https://run.mocky.io/v3/c3e88fdb-18f1-4a80-b546-be68720859db"

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        viewPager.adapter = viewPagerAdapter
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val selectedHouseModel = viewPagerAdapter.currentList.get(position)
                val cameraUpdate = CameraUpdate.scrollTo(LatLng(selectedHouseModel.lat!!, selectedHouseModel.lon!!))
                    .animate(CameraAnimation.Easing)
                naverMap.moveCamera(cameraUpdate)

            }
        })

    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map

        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0

        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.497885, 127.027512))
        naverMap.moveCamera(cameraUpdate)

        val uiSettings = naverMap.uiSettings
        uiSettings.isLocationButtonEnabled = false

        currentLocationButton.map = naverMap

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        getHouseListFromApi()
    }

    private fun getHouseListFromApi() {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        //        retrofit.create(HouseService::class.java).also {
//            it.getHouseList().enqueue(object : Callback<HouseModel> {
//
//            })
//        }
        val retrofitService = retrofit.create(HouseService::class.java)
        retrofitService.getHouseList().enqueue(object : Callback<HouseDto> {
            override fun onResponse(call: Call<HouseDto>, response: Response<HouseDto>) {
                if(response.isSuccessful.not()) {
                    return
                }
                response.body()?.let { dto ->
                    Log.d("retrofit ", "${dto.toString()}")
                    updateMarker(dto.items)
                    viewPagerAdapter.submitList(dto.items)
                    recyclerViewAdapter.submitList(dto.items)

                    bottomSheetTextView.text = "${dto.items.size}개의 숙소 "
                }

            }

            override fun onFailure(call: Call<HouseDto>, t: Throwable) {

            }

        })
    }

    private fun updateMarker(house: List<HouseModel>?) {
        house?.forEach { house ->
            val marker = Marker()
            marker.position = LatLng(house.lat!!, house.lon!!)
            //marker.onClickListener
            marker.onClickListener = this

            marker.map = naverMap
            marker.tag = house.id
            marker.icon = MarkerIcons.BLACK
            marker.iconTintColor = Color.RED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }

        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if(!locationSource.isActivated) {
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }


    }

    override fun onClick(overlay: Overlay): Boolean {
        overlay.tag

        val selectedModel = viewPagerAdapter.currentList.firstOrNull {
            it.id == overlay.tag
        }
        selectedModel?.let {
            val position = viewPagerAdapter.currentList.indexOf(it)
            viewPager.currentItem = position
        }
        return true
    }


    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }
    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }



}












































