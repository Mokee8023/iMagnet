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
import com.mokee.imagnet.activity.btdb.BtdbMeDetailActivity
import com.mokee.imagnet.activity.cilicat.CilicatDetailActivity
import com.mokee.imagnet.activity.nima.NimaDetailsActivity
import com.mokee.imagnet.model.BtdbMeItem
import com.mokee.imagnet.model.CilicatItem
import java.lang.StringBuilder

class BtdbMeHomeAdapter : RecyclerView.Adapter<BtdbMeHomeAdapter.BtdbMeHolder> {
    private var mContext: Context
    private var mHomeItemList: MutableList<BtdbMeItem>

    constructor(context: Context, homeItemList: MutableList<BtdbMeItem>): super() {
        this.mHomeItemList = homeItemList
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BtdbMeHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.btdb_me_list_item, parent, false)
        return BtdbMeHolder(view)
    }

    override fun getItemCount(): Int {
        return mHomeItemList.size
    }

    override fun onBindViewHolder(holder: BtdbMeHolder, position: Int) {
        holder.title.text = mHomeItemList[position].title

        val sb = StringBuilder()
        sb.append("文件大小: ").append(mHomeItemList[position].size).append("\n\n")
        sb.append("文件数量: ").append(mHomeItemList[position].fileCount).append("\n\n")
        sb.append("创建时间: ").append(mHomeItemList[position].createTime).append("\n\n")
        sb.append("种子热度: ").append(mHomeItemList[position].hot)

        holder.attrs.text = sb.toString()

        holder.card.setOnClickListener {
            val btdbDetail = Intent(mContext, BtdbMeDetailActivity::class.java)
            btdbDetail.putExtra(BtdbMeDetailActivity.BTDB_ME_ITEM_DETAIL, mHomeItemList[position].url)
            mContext.startActivity(btdbDetail)
        }
    }

    class BtdbMeHolder(view: View) : RecyclerView.ViewHolder(view) {
        var card: CardView = view.findViewById(R.id.btdb_me_home_item_card)
        var title: TextView = view.findViewById(R.id.btdb_me_title)
        var attrs: TextView = view.findViewById(R.id.btdb_me_details)
    }
}