package com.map.loadguied_v2.createGuide

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapOverlay
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdate
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.route.RouteLineLayer
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.shape.DotPoints
import com.kakao.vectormap.shape.Polygon
import com.kakao.vectormap.shape.PolygonOptions
import com.map.loadguied_v2.R
import com.map.loadguied_v2.apiPackage.*
import kotlinx.coroutines.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.Arrays


class createGuideMainActivity  : AppCompatActivity() {
    val apiCaller = callApi()
    public lateinit var layer: RouteLineLayer
    public lateinit var label : com.kakao.vectormap.label.Label
    public lateinit var labelLayer : com.kakao.vectormap.label.LabelLayer
    public lateinit var shapeLayer : com.kakao.vectormap.shape.ShapeLayer
    public lateinit var kakaoMap : KakaoMap
    val positionList = mutableListOf<LatLng>()
    val savePositionList = mutableListOf<LatLng>()
    val labelList = mutableListOf<Label>()
    public lateinit var startLabel : Label
    public lateinit var endLabel : Label
    public var canSave : Boolean = false


    lateinit var shapeManager :  com.kakao.vectormap.shape.ShapeManager
    private var isDragging = false
    private var initialX = 0f
    private var initialY = 0f
    lateinit var polygon: Polygon
    lateinit var backPo : LatLng

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_load_guide)
//      지도에 표시할 라인 스타일

        //최초위지 지정
//        var x = intent.getStringExtra("x")!!.toDouble()
//        var y = intent.getStringExtra("y")!!.toDouble()

        val scope = CoroutineScope(Dispatchers.Default)
        val mapView = findViewById<MapView>(R.id.map_view)
        var addPoiBtn = findViewById<Button>(R.id.addPoiBtn)

        val Address_TextFiled = findViewById<EditText>(R.id.Address_TextFiled)
        val moveMap_Btn = findViewById<Button>(R.id.moveMap_Btn)
        val setStart_Btn = findViewById<Button>(R.id.setStart_Btn)
        val setEnd_Btn = findViewById<Button>(R.id.setEnd_Btn)
        val create_Btn = findViewById<Button>(R.id.create_Btn)
        val save_Btn = findViewById<Button>(R.id.save_Btn)
        val file_Name_TextFiled = findViewById<EditText>(R.id.File_Name_TextFiled)

        var polygonRadius : Float = 1F

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
                shapeManager = kakaoMap.getShapeManager()!!
//                val cameraUpdate = CameraUpdateFactory.newCenterPosition(LatLng.from(y, x))
//                kakaoMap.moveCamera(cameraUpdate);

                val pos = getCenterPosition()
                labelLayer = kakaoMap.labelManager!!.layer!!
                shapeLayer =  shapeManager!!.getLayer()!!
                backPo = pos//최초 시작지점 설정

                positionList.clear()

                //positionList.add(LatLng.from(37.5598076696797,127.01354446474713))

                //drawLines(positionList,0)

                //positionList.add(backPo)

                val options = PolygonOptions.from(
                    DotPoints.fromCircle(pos, polygonRadius), Color.parseColor("#f55d44")
                )
                polygon = shapeLayer.addPolygon(options)

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
//                var styles = LabelStyles.from(
//                    "myStyleId",
//                    LabelStyle.from(R.drawable.blue_marker).setZoomLevel(8),
//                    LabelStyle.from(R.drawable.blue_marker).setZoomLevel(11),
//                    LabelStyle.from(R.drawable.blue_marker)
//                        .setTextStyles(32, Color.BLACK, 1, Color.GRAY).setZoomLevel(15)
//                )
//
//
//                // 라벨 스타일 추가
//
//                // 라벨 스타일 추가
//                styles = kakaoMap.labelManager!!.addLabelStyles(styles!!)

                // 라벨 생성

//                label = labelLayer.addLabel(
//                    LabelOptions.from(pos).setStyles(styles).setTexts("임의의 시작점")
//                )
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
                    val options = PolygonOptions.from(
                        DotPoints.fromCircle(pos, polygonRadius), Color.parseColor("#f55d44")
                    )
                    Log.d("폴리곤 정보","${polygon.mapPoints.size}")
                    shapeLayer.remove(polygon)
                    polygon = shapeLayer.addPolygon(options)

//                    labelLayer.remove(label)//기존 라벨 삭제
//                    label = kakaoMap.labelManager!!.layer!!.addLabel(
//                        LabelOptions.from(pos).setStyles(styles).setTexts("새로운 위치")
//                    )// 라벨 추가
                }

            }
        })
/*
* var startData : findAddress_Json
        var endData : findAddress_Json

        runBlocking(Dispatchers.Default) {
            val result : String

            startData = findAddress_Json(async { apiCaller.getAddress("용마공원로 9길 29") }.await())
            endData = findAddress_Json(async { apiCaller.getAddress("인천광역시 부평구 무네미로448번길 56") }.await())
        }

        var startX = startData.documents.get(0).getX().toDouble()
        var startY = startData.documents.get(0).getY().toDouble()
        var endX = endData.documents.get(0).getX().toDouble()
        var endY = endData.documents.get(0).getY().toDouble()

        apiCaller.directions(startX,startY,endX,endY)

        val apiCaller = callApi()

        GlobalScope.launch {
            val pointList = apiCaller.directions_syc(startX,startY,endX,endY)
            for(i: Int in 0..pointList.size-1){
                Log.d("메인에서 리스트 출력","${pointList.get(i)[0][0]}:${pointList.get(i)[0][1]}")
            }
            // pointList를 사용하는 코드 작성
        }
* */
        create_Btn.setOnClickListener {
            Log.d("api호출","실행")
            if(positionList.size >= 2){
                layer.removeAll()
                this.getPoints(getLatLngString())
//                drawLines(getPoints(getLatLngString()),0)
//                Log.d("지점 선택,경유지${positionList.size-2}","종료 : ${getLatLngString()}")
            } else{
                Log.d("라인 생성 실패","리스트 길이 미달")
            }
        }
        moveMap_Btn.setOnClickListener {
            var move : Boolean = false;
            runBlocking(Dispatchers.Default) {
                var addressText = Address_TextFiled.text.toString()
                var apiResult =async { apiCaller.getAddress(addressText) }.await()
                Log.d("API 반환값","${apiResult.length}")
                if(apiResult.length != 0 ){

                    var startData = findAddress_Json(apiResult)
                    Log.d("이동 버튼 클릭",startData.toString())
                    if(startData.documents.size > 0){
                        //if(startData.metaData.total_count > 0){
                        var startX = startData.documents.get(0).getX().toDouble()
                        var startY = startData.documents.get(0).getY().toDouble()
                        Log.d("카메라 이동","시도")
                        var camera: CameraUpdate = CameraUpdateFactory.newCenterPosition(LatLng.from(startY, startX));
                        kakaoMap.moveCamera(camera)
                        kakaoMap.moveCamera(CameraUpdateFactory.zoomTo(15))
                        Log.d("카메라 이동","성공")
                        move = true
                    }
                }
            }
            if(!move){
                Toast.makeText( applicationContext, "유효한 주소가 아닙니다.", Toast.LENGTH_SHORT).show()
                //runBlocking내부에서 사용시 앱 종료
            }
        }
        setStart_Btn.setOnClickListener {
            if(positionList.size>0) {
                layer.removeAll()
                labelLayer.removeAll()
                positionList.clear()
            }
            positionList.add(0,getCenterPosition())
            addLabel("s",getCenterPosition())

        }
        setEnd_Btn.setOnClickListener {
            if(positionList.size<2) {
                positionList.add(getCenterPosition())
            } else{
                positionList[positionList.size-1] = getCenterPosition()
            }

//            drawLine(LatLng.from(127.107525,37.405923),LatLng.from(127.146063,37.387444),0)
//            drawLine(LatLng.from(37.405923,127.107525),LatLng.from(37.387444,127.146063),0)
            addLabel("e",getCenterPosition())
        }
        addPoiBtn.setOnClickListener {
            //버튼 클릭시 함수 실행
            if(positionList.size>=2) {
                positionList.add(positionList.size-1, getCenterPosition())

            } else{
                positionList.add(getCenterPosition())
            }
            addLabel("m",getCenterPosition())
            Log.d("경유지 갯수","${positionList.size}")
            //Toast.makeText(applicationContext, "리스트 크기 : ${positionList.size}", Toast.LENGTH_SHORT).show()
        }

        save_Btn.setOnClickListener {
            var filename = sanitizeFileName(file_Name_TextFiled.text.toString())
            if(filename.length > 0) {
                saveRoadGuiedFile(filename)
                Toast.makeText(applicationContext, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            } else{

                Toast.makeText(applicationContext, "저장할 파일 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
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
    fun saveRoadGuiedString() : String{
        var result : String = ""
        for(lag in savePositionList){
            result = "${result}\n${lag.latitude},${lag.longitude}"
        }
        return result
    }
    fun saveRoadGuiedFile(name : String){
        var saveString = saveRoadGuiedString()
        if(canSave){
            Log.d("저장내용",saveString)
            val fileName = "${name}.txt"
            val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "roadGuide")
            val DirFileName = File("$dir/$fileName")
            if (!DirFileName.exists()) { DirFileName.createNewFile() }

            val writer = FileWriter(DirFileName.absolutePath)
            val buffer = BufferedWriter(writer)
            buffer.write(saveString)
            buffer.close()

        } else {
            Toast.makeText(applicationContext, "가이드 생성후 저장할수 있습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    fun sanitizeFileName(input: String): String {
        // 파일 이름으로 사용할 수 없는 문자 제거
        val invalidChars = Regex("[^a-zA-Z0-9-가-힣_]")
        return input.replace(invalidChars, "_")
    }
    fun addLabel(tag : String,lag : LatLng){
        // 1. LabelStyles 생성
        val labelStyles = kakaoMap.labelManager!!.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.blue_marker).setTextStyles(20, Color.BLACK)),
            LabelStyles.from(LabelStyle.from(R.drawable.icon_s).setTextStyles(20, Color.BLACK)),
            LabelStyles.from(LabelStyle.from(R.drawable.icon_e).setTextStyles(20, Color.BLACK)),
            LabelStyles.from(LabelStyle.from(R.drawable.icon_m).setTextStyles(20, Color.BLACK)))
        canSave = false
        var index : Int = 0
        when (tag) {
            "s" -> {
                if (::startLabel.isInitialized) {
                    labelLayer.remove(startLabel)
                }
                index = 1
            }
            "e" -> {
                if (::endLabel.isInitialized) {
                    labelLayer.remove(endLabel)
                }
                index = 2
            }
            "m" -> {
                index = 3
            }
        }
// 2. LabelOptions 생성
        val options = LabelOptions.from(lag)
            .setStyles(labelStyles.get(index))

// 4. LabelLayer 에 LabelOptions 을 넣어 Label 생성
        when (tag) {
            "s" -> {
                startLabel = labelLayer!!.addLabel(options)
            }
            "e" -> {
                endLabel = labelLayer!!.addLabel(options)
            }
            "m" -> {
                labelList.add(labelLayer!!.addLabel(options))
            }
        }

        //labelList.add(label)
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


    fun getLatLngString(ll : LatLng) : String{
        return "{\"x\":\"${ll.longitude}\",\"y\":\"${ll.latitude}\"}"
    }

    fun getLatLngString() : String{
        var nowLatLng : LatLng
        var result : String
        result = "\"origin\": ${getLatLngString(positionList.get(0))},"
        result = result + "\"destination\": ${getLatLngString(positionList.get(positionList.size-1))}"
        var dum : String = ",\"waypoints\": ["
        if(positionList.size > 2){
            if(positionList.size > 3){
                for(i: Int in 1..positionList.size-3){
                    dum = dum + "${getLatLngString(positionList.get(i))},"
                }
            }
            dum = dum + "${getLatLngString(positionList.get(positionList.size-2))}]"
            result = result + dum
        }


        result = "{${result}}"
        Log.d("getLatLngString에서 반환","${result}")

        return result
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

        Log.d("drawLine 실행됨","${po1}:${po2}")
        //positionList.add(po2)
        backPo = po2
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

//        var camera: CameraUpdate = CameraUpdateFactory.fitMapPoints(points);
//        kakaoMap.moveCamera(camera)
        kakaoMap.moveCamera(
            CameraUpdateFactory.fitMapPoints(points,200),
            CameraAnimation.from(500, true, true)
        )
        //kakaoMap.moveCamera(CameraUpdateFactory.zoomTo(13))
        //list.clear()
        //list.add(po1)
    }

    private fun getPoints(str : String) {
        Log.e("호출 에러 확인","1")
        val httpGetPoints = getPoints(this,str)
        Log.e("호출 에러 확인","2")
        httpGetPoints.execute()
        Log.e("호출 에러 확인","3")
    }

    //외부에서 함수 호출
    fun onPointsResult(points: ArrayList<LatLng>) {
        Log.e("호출 에러 확인","4")
        savePositionList.clear()

        //기존 내용 삭제 - 목적지 직선연결 방지
        if (points != null) {
            Log.e("호출 에러 확인","5")
            for (latLng in points) {
                savePositionList.add(latLng)
                Log.e("좌표 추가됨","${latLng.toString()}")
            }
        } else {
            Log.e("api반환", "반환 데이터 없음")
        }
        savePositionList.add(0,positionList.get(0))
        savePositionList.add(savePositionList.size,positionList.get(positionList.size-1))
        Log.e("호출 에러 확인","6")
        drawLines(savePositionList,0)
        canSave = true
        Log.e("호출 에러 확인","7")
        //positionList.clear()
        //함수 호출 결과
    }
}