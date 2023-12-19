package com.map.loadguied_v2.recyleView

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.map.loadguied_v2.R

class AddressRvAdapter(val context: Context, val addressList: ArrayList<AddressItem>) :
    RecyclerView.Adapter<AddressRvAdapter.Holder>(), Parcelable {
    constructor(parcel: Parcel) : this(
        TODO("context"),
        TODO("addressList")
    ) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.address_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val currentItem = addressList[position]

        // 데이터를 뷰에 바인딩하기 위해 bind 메서드 호출
        holder.bind(currentItem, context)

        holder.itemView.setOnClickListener {
            // 아이템 클릭 이벤트
            val clickedAddress = addressList[position].place_name
            Log.d("AddressRvAdapter", "클릭한 주소: $clickedAddress")
        }
    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val place_name = itemView?.findViewById<TextView>(R.id.place_name)

        // 데이터를 뷰에 바인딩하기 위한 메서드
        fun bind(item: AddressItem, context: Context) {
            place_name?.text = item.place_name
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddressRvAdapter> {
        override fun createFromParcel(parcel: Parcel): AddressRvAdapter {
            return AddressRvAdapter(parcel)
        }

        override fun newArray(size: Int): Array<AddressRvAdapter?> {
            return arrayOfNulls(size)
        }
    }

}
