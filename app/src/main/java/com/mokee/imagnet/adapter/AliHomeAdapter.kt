package com.mokee.imagnet.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mokee.imagnet.R
import com.mokee.imagnet.activity.ali.AliDetailActivity
import com.mokee.imagnet.activity.cilicat.CilicatDetailActivity
import com.mokee.imagnet.activity.nima.NimaDetailsActivity
import com.mokee.imagnet.model.AliItem
import com.mokee.imagnet.model.CilicatItem
import java.lang.StringBuilder

class AliHomeAdapter : RecyclerView.Adapter<AliHomeAdapter.AliHolder> {
    private var mContext: Context
    private var mHomeItemList: MutableList<AliItem>

    constructor(context: Context, homeItemList: MutableList<AliItem>): super() {
        this.mHomeItemList = homeItemList
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AliHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.ali_list_item, parent, false)
        return AliHolder(view)
    }

    override fun getItemCount(): Int {
        return mHomeItemList.size
    }

    override fun onBindViewHolder(holder: AliHolder, position: Int) {
        holder.title.text = mHomeItemList[position].title
        val sb = StringBuilder()
        mHomeItemList[position].attrs.forEach {
            sb.append(it).append("\n\n")
        }

        holder.attrs.text = sb.substring(0, sb.lastIndex - 2).toString()

        holder.card.setOnClickListener {
            val aliDetail = Intent(mContext, AliDetailActivity::class.java)
            aliDetail.putExtra(AliDetailActivity.ALI_ITEM_DETAIL, mHomeItemList[position].url)
            mContext.startActivity(aliDetail)
        }
    }

    class AliHolder(view: View) : RecyclerView.ViewHolder(view) {
        var card: CardView = view.findViewById(R.id.ali_home_item_card)
        var title: TextView = view.findViewById(R.id.ali_title)
        var attrs: TextView = view.findViewById(R.id.ali_details)
    }
}