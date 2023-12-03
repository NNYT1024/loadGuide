package com.map.loadguied_v2.createGuide

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapOverlay
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.route.RouteLineLayer
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.map.loadguied_v2.R
import java.util.Arrays


class createGuideMainActivity  : AppCompatActivity() {

    public lateinit var layer: RouteLineLayer
    public lateinit var label : com.kakao.vectormap.label.Label
    public lateinit var labelLayer : com.kakao.vectormap.label.LabelLayer
    public lateinit var kakaoMap : KakaoMap
    val positionList = mutableListOf<LatLng>()

    private var isDragging = false
    private var initialX = 0f
    private var initialY = 0f

    lateinit var backPo : LatLng

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_load_guide)
//      지도에 표시할 라인 스타일

        //최초위지 지정
        var x = intent.getStringExtra("x")!!.toDouble()
        var y = intent.getStringExtra("y")!!.toDouble()


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
                val cameraUpdate = CameraUpdateFactory.newCenterPosition(LatLng.from(y, x))
                kakaoMap.moveCamera(cameraUpdate);

                backPo = getCenterPosition()//최초 시작지점 설정

                positionList.clear()

                positionList.add(LatLng.from(37.5598076696797,127.01354446474713))

                drawLines(positionList,0)

                positionList.add(backPo)

//                Toast.makeText(applicationContext, "최초 위치 설정\n{$backPo}", Toast.LENGTH_SHORT).show()
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

                val pos = getCenterPosition()
                labelLayer = kakaoMap.labelManager!!.layer!!
                label = labelLayer.addLabel(
                    LabelOptions.from(pos).setStyles(styles).setTexts("임의의 시작점")
                )
                /*------------------------------------------------------------------------------------*/
                //라인 그리기

                // 1. RouteLineLayer 가져오기
//               val layer = layer

                // 2. RouteLineStylesSet 생성하기
//                val stylesSet = RouteLineStylesSet.from(
//                    "blueStyles",
//                    RouteLineStyles.from(RouteLineStyle.from(16f, Color.BLUE))
//                )

                // 3. RouteLineSegment 생성하기 - 세그먼트에 스타일 설정을 생략하면, RouteLineStylesSet 의 index 0 번째에 해당되는 스타일로 설정된다.
                // 3-1. index 를 통해 RouteLineStylesSet 에 있는 styles 를 가져온다.
//                val segment = RouteLineSegment.from(
//                    Arrays.asList(
//                        LatLng.from(37.394660, 127.111182),
//                        LatLng.from(37.33856778190988, 127.093663107081)
//                    )
//                ).setStyles(stylesSet.getStyles(0))
//
//                // // 3-2. id 를 통해 RouteLineStylesSet 에 있는 styles 를 가져온다.
//                // RouteLineSegment segment = RouteLineSegment.from(Arrays.asList(
//                //                 LatLng.from(37.338549743448546,127.09368565409382),
//                //                 LatLng.from(37.33856778190988,127.093663107081)))
//                //         .setStyles(stylesSet.getStyles("blueStyles"));
//
//                // 4. RouteLineStylesSet 을 추가하고 RouteLineOptions 생성하기
//                val options = RouteLineOptions.from(segment).setStylesSet(stylesSet)
//
//                // 5. RouteLineLayer 에 추가하여 RouteLine 생성하기
//                val routeLine = layer.addRouteLine(options)

                /*------------------------------------------------------------------------------------*/
                //화면상 좌상단 지도 좌표 얻어오기
                //카메라 이동할때마다 실행
                kakaoMap.setOnCameraMoveEndListener { kakaoMap, position, gestureType ->
                    val position = kakaoMap.fromScreenPoint(0 ,0)//화면상에서 특정위치 좌표
                    if (position != null) { }
                    val pos = getCenterPosition()//위치 지정

                    labelLayer.remove(label)//기존 라벨 삭제
                    label = kakaoMap.labelManager!!.layer!!.addLabel(
                        LabelOptions.from(pos).setStyles(styles).setTexts("새로운 위치")
                    )// 라벨 추가
                }

            }
        })
        var addPoiBtn = findViewById<Button>(R.id.addPoiBtn)
        addPoiBtn.setOnClickListener {
            //버튼 클릭시 함수 실행
            positionList.add(getCenterPosition())
            if(positionList.size >= 10){
                drawLines(positionList,0)
            }
            //Toast.makeText(applicationContext, "리스트 크기 : ${positionList.size}", Toast.LENGTH_SHORT).show()
        }
        val draggableButton = findViewById<Button>(R.id.addPoiBtn)

        // 터치 이벤트 처리
        draggableButton.setOnLongClickListener {
            isDragging = true
            initialX = it.x - it.width / 2
            initialY = it.y - it.height / 2
            true // true를 반환하여 이벤트 소비
        }

        // 터치 이벤트 처리
        draggableButton.setOnTouchListener { v, event ->
            if (isDragging) {
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> {
                        v.x = event.rawX - v.width / 2
                        v.y = event.rawY - v.height / 2
                    }
                    MotionEvent.ACTION_UP -> {
                        isDragging = false
                    }
                }
                true
            } else {
                false
            }
        }
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
            Toast.makeText( applicationContext, "${x}\n${y}", Toast.LENGTH_LONG).show()
        } else {
            x = 0.0; y = 0.0
        }
        return LatLng.from(x,y)
    }

    fun drawLine(po1 : LatLng, po2 : LatLng, styleIndex : Int) {
        //입력된 두 좌표를 연결


//        public val stylesSet: RouteLineStylesSet = RouteLineStylesSet.from(
//            "blue", RouteLineStyles.from(RouteLineStyle.from(16f, Color.BLUE))
//        )
//        val segment = RouteLineSegment.from(
//            Arrays.asList( po1, po2)
//        ).setStyles(stylesSet.getStyles(styleIndex))
//        //Toast.makeText(applicationContext, "${po1.longitude}\n${po1.latitude}", Toast.LENGTH_LONG).show()
//        //Toast.makeText(applicationContext, "${po2.longitude}\n${po2.latitude}", Toast.LENGTH_LONG).show()
//        //val options = RouteLineOptions.from(segment).setStylesSet(stylesSet)
//

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

        //multiStyleLine = layer.addRouteLine(options)


        // 5. RouteLineLayer 에 추가하여 RouteLine 생성하기
        val routeLine = layer.addRouteLine(options)
        //positionList.add(po2)
        backPo = po2
    }

    fun drawLines(list : MutableList<LatLng>, styleIndex : Int) {
        //리스트로 저장된 좌표를 순차적으로 연결
        var po1 : LatLng = list.get(0)
        var po2 : LatLng
        for(i: Int in 1..list.size-1){
            po2 = list.get(i)
            drawLine(po1,po2,styleIndex)
            po1 = po2;
        }
        list.clear()
        list.add(po1)
    }
    public fun connectLoadLine(){//마지막 좌표와 목표를 연결 카카오 길찾기API 사용

    }
}