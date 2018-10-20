package com.mokee.imagnet.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mokee.imagnet.R
import com.mokee.imagnet.model.CilicatItem
import com.mokee.imagnet.model.NimaItem

class CilicatHomeAdapter : RecyclerView.Adapter<CilicatHomeAdapter.CilicatHolder> {
    private var mContext: Context
    private var mHomeItemList: MutableList<CilicatItem>

    constructor(context: Context, homeItemList: MutableList<CilicatItem>): super() {
        this.mHomeItemList = homeItemList
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CilicatHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.home_list_item, parent, false)
        return CilicatHolder(view)
    }

    override fun getItemCount(): Int {
        return mHomeItemList.size
    }

    override fun onBindViewHolder(holder: CilicatHolder, position: Int) {
        holder.title.text = mHomeItemList[position].title
        holder.details.text = mHomeItemList[position].detail
    }

    class CilicatHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.home_title)
        var details : TextView = view.findViewById(R.id.home_details)
    }
}