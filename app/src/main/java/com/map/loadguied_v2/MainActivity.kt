package com.map.loadguied_v2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.map.loadguied_v2.apiPackage.*
import com.map.loadguied_v2.createGuide.createGuideMainActivity

import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    val apiCaller = callApi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        var createGuideBtn = findViewById<Button>(R.id.createGuide_Btn)
        createGuideBtn.setOnClickListener {
//            Toast.makeText(applicationContext, "클릭이벤트", Toast.LENGTH_SHORT).show()

            
            runBlocking(Dispatchers.Default) {
                val result = async { apiCaller.getAddress("주소") }.await()
                var data = findAddress_Json(result)
            }
            val intent = Intent(this, createGuideMainActivity::class.java)
            startActivity(intent)

            //Toast.makeText(applicationContext, "클릭이벤트", Toast.LENGTH_SHORT).show()
        }
//        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
//        var url = "https://apis-navi.kakaomobility.com/v1/waypoints/directions"
//        val client = OkHttpClient()
//        var apiKey ="83d8db42662bbd2af86846795bcfb600"
//        val json = JSONObject()
//        json.put("Authorization", "KakaoAK ${apiKey}")
//        json.put("Content-Type", "application/json")
//
//        var startJson = JSONObject()
//        startJson.put("x","127.11024293202674")
//        startJson.put("y","37.394348634049784")
//
//        var endJson = JSONObject()
//        startJson.put("x","127.10860518470294")
//        startJson.put("y","37.401999820065534")
//        json.put("destination", endJson)
//
//        val body = RequestBody.create(JSON, json.toString())

        //post
        var startData : findAddress_Json
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
        //findAddress_Json(apiCaller.getAddress("망우3동"))
//        apiCaller.getAddress("망우3동")

    }
}