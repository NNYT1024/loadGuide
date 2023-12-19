package com.map.loadguied_v2.showGuide

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapOverlay
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.route.RouteLineLayer
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.shape.DotPoints
import com.kakao.vectormap.shape.Polygon
import com.kakao.vectormap.shape.PolygonOptions
import com.map.loadguied_v2.R
import com.map.loadguied_v2.apiPackage.callApi
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import java.util.Arrays

class showGuideMap  : AppCompatActivity() {
    val apiCaller = callApi()
    public lateinit var layer: RouteLineLayer
    public lateinit var label : com.kakao.vectormap.label.Label
    public lateinit var labelLayer : com.kakao.vectormap.label.LabelLayer
    public lateinit var kakaoMap : KakaoMap
    public lateinit var list : MutableList<LatLng>
    lateinit var fileName : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_load_guide)

        var intent = intent
        fileName = intent.getStringExtra("file_name").toString()



        val mapView = findViewById<MapView>(R.id.map_view)

        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API 가 정상적으로 종료될 때 호출됨
            }

            override fun onMapError(error: Exception) {
                Log.e("인증실패 에러 발생", "{${error.message}}}")
                Toast.makeText(applicationContext, "에러 발생{$error}", Toast.LENGTH_SHORT).show()
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kMap: KakaoMap) {
                kakaoMap = kMap
                layer = kakaoMap.routeLineManager!!.layer

                labelLayer = kakaoMap.labelManager!!.layer!!

                Log.d("실행 확인","1")
                list = readFileCoordinates()
                Log.d("실행 확인","2")
                drawLines(list,0)
                Log.d("실행 확인","3")

            }
        })
    }

    private fun readFileCoordinates(): MutableList<LatLng> {
        val coordinatesList = mutableListOf<LatLng>()

        try {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "roadGuide/$fileName"
            )

            if (file.exists()) {
                val bufferedReader = BufferedReader(FileReader(file))
                Log.d("좌표 추가","파일 확인")
                bufferedReader.useLines { lines ->

                    lines.forEach { line ->
                        // 각 줄을 파싱하여 LatLng 객체로 변환하여 리스트에 추가
                        val parts = line.split(",")
                        if (parts.size == 2) {
                            val latitude = parts[0].toDouble()
                            val longitude = parts[1].toDouble()
                            coordinatesList.add(LatLng.from(latitude, longitude))

                        }
                    }
                }
            } else {
                Log.e("인증실패 에러 발생", "파일 불러오기 실패")
            }
        } catch (e: Exception) {
            Log.e("파일 읽기 실패", "${e.message}")
            e.printStackTrace()
        }

        return coordinatesList
    }

    fun getCenterPosition(): LatLng {
//        현재 표시중인 라벨의 위치를 출력
//        var latitude = label.position.latitude
//        var longitude = label.position.longitude
//        Toast.makeText(applicationContext, "${latitude}\n${longitude}", Toast.LENGTH_SHORT).show()

        var camera = kakaoMap.cameraPosition //현재 카메라 위치
        var x : Double = 0.0
        var y : Double = 0.0

        if(camera != null) {
            val cameraPosition = camera.position
            x = cameraPosition.latitude
            y = cameraPosition.longitude
            //Toast.makeText( applicationContext, "${x}\n${y}", Toast.LENGTH_LONG).show()
        } else {
            x = 0.0; y = 0.0
        }
        return LatLng.from(x,y)
    }
    fun drawLine(po1 : LatLng, po2 : LatLng, styleIndex : Int) {

        val options: RouteLineOptions = RouteLineOptions.from(
            Arrays.asList(
                RouteLineSegment.from(
                    Arrays.asList( po1, po2),
                    RouteLineStyle.from(
                        baseContext,
                        R.style.BlueRouteArrowLineStyle
                    )
                )
            )
        )

        val routeLine = layer.addRouteLine(options)

        Log.d("drawLine 실행됨","${po1}:${po2}")
    }

    fun drawLines(list : MutableList<LatLng>, styleIndex : Int) {

        // 두 좌표의 중간 지점 계산
        val points: Array<LatLng> = list.toTypedArray()

        if(list == null) {
            Log.e("그리기 에러","리스트에 데이터 없음")
            return
        }
        if(list.size < 2) {
            Log.e("그리기 에러","리스트에 데이터 없음")
            return
        }
        //리스트로 저장된 좌표를 순차적으로 연결
        var po1 : LatLng = list.get(0)
        var po2 : LatLng
        for(i: Int in 1..list.size-1){
            po2 = list.get(i)

            Log.d("drawLines 실행 확인","${po1} : ${po2}")
            drawLine(po1,po2,styleIndex)
            po1 = po2
        }

        kakaoMap.moveCamera(
            CameraUpdateFactory.fitMapPoints(points,200),
            CameraAnimation.from(500, true, true)
        )
    }
}