package com.map.loadguied_v2

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapOverlay
import com.kakao.vectormap.MapView
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles

class MainActivity : AppCompatActivity() {
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
                Toast.makeText(this@MainActivity, "에러 발생{$error}", Toast.LENGTH_SHORT).show()
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
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

                // 라벨 생성
                val pos = LatLng.from(37.394660,127.111182)
                val label = kakaoMap.labelManager!!.layer!!.addLabel(
                    LabelOptions.from(pos).setStyles(styles).setTexts("★맛있는 치킨★", "123-4567")
                )
            }
        })
    }
}