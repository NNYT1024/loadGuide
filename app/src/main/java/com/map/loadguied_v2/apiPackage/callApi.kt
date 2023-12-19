package com.map.loadguied_v2.apiPackage

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder

class callApi {
    final var REST_API_KEY = "83d8db42662bbd2af86846795bcfb600"

    public fun directions(startX: Double,startY: Double,endX: Double,endY: Double) {
        //매개변수로 입력받은 주소를 이용해서 길안내 좌표 얻어오기
        //수정 필요

        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        var url = "https://apis-navi.kakaomobility.com/v1/waypoints/directions"
        val client = OkHttpClient()
        var startData : findAddress_Json
        var endData : findAddress_Json
        //body로 넘길 json에 필요한 것들 넣기
        val json = JSONObject()

        var startJson = JSONObject()

        startJson.put("x",startX)
        startJson.put("y",startY)
        json.put("origin", startJson)

        var endJson = JSONObject()
        endJson.put("x",endX)
        endJson.put("y",endY)
        json.put("destination", endJson)
        val body = RequestBody.create(JSON, json.toString())
        val request = Request.Builder()
            .header("Authorization", "KakaoAK ${REST_API_KEY}")
            .addHeader("Content-Type", "application/json")
            .url(url)
            .post(body)
            .build()

        val response = client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }
            // main thread말고 별도의 thread에서 실행해야 함.
            override fun onResponse(call: Call, response: Response) {
                Thread{
                    var str = response.body?.string()
                    if(str != null){
                        Log.d("api 길찾기 반환",str)
                        var data = findRoad_Json(str)

                        for(i: Int in 0..data.pointList.size-1){
                            for (j in 0 until data.pointList.get(i).size) {
                                Log.d("리스트", "${data.pointList.get(i)[j][0]} : ${data.pointList.get(i)[j][1]}")
                            }
                        }

                    }
                }.start()
            }
        })
    }

    suspend fun directions_syc(startX: Double, startY: Double, endX: Double, endY: Double): ArrayList<Array<DoubleArray>> {
        return runBlocking {
            val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
            val url = "https://apis-navi.kakaomobility.com/v1/waypoints/directions"
            val client = OkHttpClient()

            // body로 넘길 json에 필요한 것들 넣기
            val json = JSONObject()

            val startJson = JSONObject()
            startJson.put("x", startX)
            startJson.put("y", startY)
            json.put("origin", startJson)

            val endJson = JSONObject()
            endJson.put("x", endX)
            endJson.put("y", endY)
            json.put("destination", endJson)

            val body = RequestBody.create(JSON, json.toString())
            val request = Request.Builder()
                .header("Authorization", "KakaoAK $REST_API_KEY")
                .addHeader("Content-Type", "application/json")
                .url(url)
                .post(body)
                .build()

            val response = GlobalScope.async(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            val str = response.await().body?.string()
            val data = findRoad_Json(str)

            for (i in 0 until data.pointList.size) {
                for (j in 0 until data.pointList[i].size) {
                    println("리스트: ${data.pointList[i][j][0]} : ${data.pointList[i][j][1]}")
                }
            }

            data.pointList
        }
    }

    suspend fun getAddress(address: String): String = withContext(Dispatchers.IO) {
        val url = "https://dapi.kakao.com/v2/local/search/address.json"

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$url?query=${URLEncoder.encode(address, "UTF-8")}")
            .header("Authorization", "KakaoAK $REST_API_KEY")
            .build()

        return@withContext try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                response.body?.string() ?: ""
            }
            
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
        
    }
}