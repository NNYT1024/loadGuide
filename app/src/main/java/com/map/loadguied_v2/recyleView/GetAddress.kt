package com.map.loadguied_v2.recyleView

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.map.loadguied_v2.R
import com.map.loadguied_v2.apiPackage.callApi
import com.map.loadguied_v2.apiPackage.findAddress_Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class getAddress  : AppCompatActivity() {
    val apiCaller = callApi()

    var AddressList = arrayListOf<AddressItem>(
        AddressItem("주소 01","도로명주소"),
        AddressItem("주소 02","도로명주소"),
        AddressItem("주소 03","도로명주소"),
        AddressItem("주소 04","도로명주소"),
        AddressItem("주소 05","도로명주소")

    )
    override fun onCreate(savedInstanceState: Bundle?) {
        //endData = findAddress_Json(async { apiCaller.getAddress("인천광역시 부평구 무네미로448번길 56") }.await())
        Log.d("getAddress onCreate","시작")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_address_list)
        var Address_RecyclerView = findViewById<RecyclerView>(R.id.Address_RecyclerView)

//        val mAdapter = AddressRvAdapter(this, AddressList)

        val mAdapter = AddressRvAdapter(this, AddressList) { item ->
            Toast.makeText(this, "주소 이름은 ${item.place_name} 이며, 도로명 주소는 ${item.road_address_name}이다.", Toast.LENGTH_SHORT).show()
        }

        var serch_Btn = findViewById<Button>(R.id.serch_Btn)
        serch_Btn.setOnClickListener {
            runBlocking(Dispatchers.Default) {

                var result = findAddress_Json(async { apiCaller.getAddressList("인천광역시 부평구") }.await())
                for (i :Int in 1..result.documents.size) {
                    Log.d("findAddress_Json","${result.documents.get(i-1).road_address_name} : ${result.documents.get(i-1).place_name}")
                }
            }
        }

        Address_RecyclerView.adapter = mAdapter
        Address_RecyclerView.adapter = mAdapter

        val lm = LinearLayoutManager(this)
        Address_RecyclerView.layoutManager = lm
        Address_RecyclerView.setHasFixedSize(true)
    }
}