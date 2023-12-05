package com.map.loadguied_v2.recyleView

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.map.loadguied_v2.R

class getAddress  : AppCompatActivity() {


    var AddressList = arrayListOf<AddressItem>(
        AddressItem("주소 이름","도로명주소"),
        AddressItem("주소 이름","도로명주소"),
        AddressItem("주소 이름","도로명주소"),
        AddressItem("주소 이름","도로명주소"),
        AddressItem("주소 이름","도로명주소")

    )
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("getAddress onCreate","시작")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_address_list)
        var Address_RecyclerView = findViewById<RecyclerView>(R.id.Address_RecyclerView)

        val mAdapter = AddressRvAdapter(this, AddressList)
        Address_RecyclerView.adapter = mAdapter

        val lm = LinearLayoutManager(this)
        Address_RecyclerView.layoutManager = lm
        Address_RecyclerView.setHasFixedSize(true)
    }
}