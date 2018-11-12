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
import com.mokee.imagnet.activity.cilicat.CilicatDetailActivity
import com.mokee.imagnet.activity.nima.NimaDetailsActivity
import com.mokee.imagnet.model.CilicatItem
import java.lang.StringBuilder

class CilicatHomeAdapter : RecyclerView.Adapter<CilicatHomeAdapter.CilicatHolder> {
    private var mContext: Context
    private var mHomeItemList: MutableList<CilicatItem>

    constructor(context: Context, homeItemList: MutableList<CilicatItem>): super() {
        this.mHomeItemList = homeItemList
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CilicatHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.cilicat_list_item, parent, false)
        return CilicatHolder(view)
    }

    override fun getItemCount(): Int {
        return mHomeItemList.size
    }

    override fun onBindViewHolder(holder: CilicatHolder, position: Int) {
        holder.title.text = mHomeItemList[position].title
        val sb = StringBuilder()
        mHomeItemList[position].detailArray.forEach {
            if(!it.startsWith("简介")) {
                sb.append(it).append("\n\n")
            }
        }

        holder.attrs.text = sb.substring(0, sb.lastIndex - 2).toString()

        if(mHomeItemList[position].detailArray.last().startsWith("简介")) {
            holder.details.text = mHomeItemList[position].detailArray.last()
        } else {
            holder.details.visibility = View.GONE
        }

        val imageUrl = mHomeItemList[position].imageUrl
        if(!TextUtils.isEmpty(imageUrl) && imageUrl.startsWith("http://")) {
            Glide.with(mContext).load(imageUrl).into(holder.image)
        }

        holder.card.setOnClickListener {
            val cilicatDetail = Intent(mContext, CilicatDetailActivity::class.java)
            cilicatDetail.putExtra(CilicatDetailActivity.CILICAT_ITEM_DETAIL, mHomeItemList[position].url)
            mContext.startActivity(cilicatDetail)
        }
    }

    class CilicatHolder(view: View) : RecyclerView.ViewHolder(view) {
        var card: CardView = view.findViewById(R.id.cilicat_card)
        var title: TextView = view.findViewById(R.id.cilicat_title)
        var attrs: TextView = view.findViewById(R.id.cilicat_attribute)
        var details : TextView = view.findViewById(R.id.cilicat_details)
        var image: ImageView = view.findViewById(R.id.cilicat_image)
    }
}