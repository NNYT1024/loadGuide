package com.map.loadguied_v2.createGuide

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapOverlay
import com.kakao.vectormap.MapView
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.route.RouteLineLayer
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles
import com.kakao.vectormap.route.RouteLineStylesSet
import com.map.loadguied_v2.R
import java.util.Arrays

class createMainActivity  : AppCompatActivity() {

    public lateinit var layer: RouteLineLayer
    public lateinit var label : com.kakao.vectormap.label.Label
    public lateinit var labelLayer : com.kakao.vectormap.label.LabelLayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapView = findViewById<MapView>(R.id.map_view)
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API 가 정상적으로 종료될 때 호출됨
            }

            override fun onMapError(error: Exception) {
                Log.e("인증실패 에러 발생", "{${error.message}}}")
                Toast.makeText(this@createMainActivity, "에러 발생{$error}", Toast.LENGTH_SHORT).show()
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {

                layer = kakaoMap.routeLineManager!!.layer

                // 인증 후 API 가 정상적으로 실행될 때 호출됨
                /*------------------------------------------------------------------------------------*/
//              MapOverlay
//              자전거도로
//              BICYCLE_ROAD
//              로드뷰라인
//              ROADVIEW_LINE
//              지형도
//              HILLSHADING
//              스카아뷰 도로라인
//              SKYVIEW_HYBRID

                //MapOverlay 설정 켜기
                kakaoMap.showOverlay(MapOverlay.BICYCLE_ROAD);
                //MapOverlay 설정 끄기
                kakaoMap.hideOverlay(MapOverlay.BICYCLE_ROAD);
                /*------------------------------------------------------------------------------------*/
                // Min ZoomLevel ~ 7 까지   : 스타일 안나옴
                // 8 ~ 10 까지              : red_marker 이미지 나옴
                // 11 ~ 14 까지             : blue_marker 이미지 나옴
                // 15 ~ Max ZoomLevel 까지  : blue_marker 이미지와 텍스트 나옴
                // Min ZoomLevel ~ 7 까지   : 스타일 안나옴
                // 8 ~ 10 까지              : red_marker 이미지 나옴
                // 11 ~ 14 까지             : blue_marker 이미지 나옴
                // 15 ~ Max ZoomLevel 까지  : blue_marker 이미지와 텍스트 나옴
                var styles = LabelStyles.from(
                    "myStyleId",
                    LabelStyle.from(R.drawable.blue_marker).setZoomLevel(8),
                    LabelStyle.from(R.drawable.blue_marker).setZoomLevel(11),
                    LabelStyle.from(R.drawable.blue_marker)
                        .setTextStyles(32, Color.BLACK, 1, Color.GRAY).setZoomLevel(15)
                )


                // 라벨 스타일 추가

                // 라벨 스타일 추가
                styles = kakaoMap.labelManager!!.addLabelStyles(styles!!)

                // 라벨 생성

                val pos = LatLng.from(37.40018735490742,127.10967869405424)
                labelLayer = kakaoMap.labelManager!!.layer!!
                label = labelLayer.addLabel(
                    LabelOptions.from(pos).setStyles(styles).setTexts("최초 라벨", "내용")
                )
                /*------------------------------------------------------------------------------------*/
                //라인 그리기

                // 1. RouteLineLayer 가져오기
//               val layer = layer

                // 2. RouteLineStylesSet 생성하기
                val stylesSet = RouteLineStylesSet.from(
                    "blueStyles",
                    RouteLineStyles.from(RouteLineStyle.from(16f, Color.BLUE))
                )

                // 3. RouteLineSegment 생성하기 - 세그먼트에 스타일 설정을 생략하면, RouteLineStylesSet 의 index 0 번째에 해당되는 스타일로 설정된다.
                // 3-1. index 를 통해 RouteLineStylesSet 에 있는 styles 를 가져온다.
                val segment = RouteLineSegment.from(
                    Arrays.asList(
                        LatLng.from(37.394660, 127.111182),
                        LatLng.from(37.33856778190988, 127.093663107081)
                    )
                )
                    .setStyles(stylesSet.getStyles(0))

                // // 3-2. id 를 통해 RouteLineStylesSet 에 있는 styles 를 가져온다.
                // RouteLineSegment segment = RouteLineSegment.from(Arrays.asList(
                //                 LatLng.from(37.338549743448546,127.09368565409382),
                //                 LatLng.from(37.33856778190988,127.093663107081)))
                //         .setStyles(stylesSet.getStyles("blueStyles"));

                // 4. RouteLineStylesSet 을 추가하고 RouteLineOptions 생성하기
                val options = RouteLineOptions.from(segment).setStylesSet(stylesSet)

                // 5. RouteLineLayer 에 추가하여 RouteLine 생성하기
                val routeLine = layer.addRouteLine(options)

                /*------------------------------------------------------------------------------------*/
                //화면상 좌상단 지도 좌표 얻어오기
                //카메라 이동할때마다 실행
                kakaoMap.setOnCameraMoveEndListener { kakaoMap, position, gestureType ->
                    val position = kakaoMap.fromScreenPoint(0 ,0)
                    if (position != null) {
                        //Toast.makeText(this@MainActivity, "뷰포트 이벤트\nlatitude: ${position.latitude}\nlongitude: ${position.longitude}}", Toast.LENGTH_LONG).show()
                        val camera = kakaoMap.cameraPosition
                        if(camera != null) {
                            val positionPosition = camera.position
                            Toast.makeText( this@createMainActivity, "${positionPosition.longitude}", Toast.LENGTH_LONG).show()
                            // 라벨 생성
                            val pos = LatLng.from(positionPosition.latitude,positionPosition.longitude)
                            labelLayer.remove(label)
                            label = kakaoMap.labelManager!!.layer!!.addLabel(
                                LabelOptions.from(pos).setStyles(styles).setTexts("${positionPosition.latitude}", "${positionPosition.longitude}")
                            )
                        }
                    }
                }

            }
        })


    }

}