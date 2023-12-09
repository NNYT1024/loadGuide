package com.map.loadguied_v2.recyleView

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.map.loadguied_v2.R

class AddressRvAdapter(val context: Context, val addressList: ArrayList<AddressItem>,val itemClick: (AddressItem) -> Unit) :
    RecyclerView.Adapter<AddressRvAdapter.Holder>() {
    constructor(parcel: Parcel) : this(
        TODO("context"),
        TODO("addressList"),
        TODO("itemClick")
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.address_item, parent, false)
        return Holder(view,itemClick)
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(addressList[position], context)
    }
    inner class Holder(itemView : View?, itemClick: (AddressItem) -> Unit) : RecyclerView.ViewHolder(itemView!!){
        val place_name = itemView?.findViewById<TextView>(R.id.place_name)
        val road_address_name = itemView?.findViewById<TextView>(R.id.road_address_name)

        fun bind (item : AddressItem,context: Context){
            place_name?.text = item.place_name
            road_address_name?.text = item.road_address_name


            itemView.setOnClickListener { itemClick(item) }
        }
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
