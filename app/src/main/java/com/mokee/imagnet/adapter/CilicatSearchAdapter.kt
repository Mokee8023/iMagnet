package com.mokee.imagnet.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.mokee.imagnet.R
import com.mokee.imagnet.activity.cilicat.CilicatDetailActivity
import com.mokee.imagnet.activity.cilicat.CilicatSearchDetailActivity
import com.mokee.imagnet.constrant.MagnetConstrant
import com.mokee.imagnet.model.CilicatSearchItem
import com.mokee.imagnet.utils.DownloadUtil
import com.mokee.imagnet.webview.WebViewActivity

class CilicatSearchAdapter : RecyclerView.Adapter<CilicatSearchAdapter.CilicatSearchHolder> {
    private var mContext: Context
    private var mItemList: MutableList<CilicatSearchItem>

    constructor(context: Context, homeItemList: MutableList<CilicatSearchItem>): super() {
        this.mItemList = homeItemList
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CilicatSearchHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.cilicat_search_item, parent, false)
        return CilicatSearchHolder(view)
    }

    override fun getItemCount(): Int {
        return mItemList.size
    }

    override fun onBindViewHolder(holder: CilicatSearchHolder, position: Int) {
        holder.title.text = mItemList[position].title

        val item = mItemList[position]
        val sb = StringBuilder()
        sb.append("文件大小: ").append(item.attrs.fileSize)
        sb.append("   文件数量: ").append(item.attrs.fileCount)
        sb.append("   创建时间: ").append(item.attrs.createTime)
        holder.attrs.text = sb.toString()

        holder.card.setOnClickListener {
            if(mItemList[position].url.contains(MagnetConstrant.CILICAT_HOME_URL)) {
                val cilicatSearchDetail = Intent(mContext, CilicatSearchDetailActivity::class.java)
                cilicatSearchDetail.putExtra(CilicatSearchDetailActivity.CILICAT_SEARCH_ITEM_DETAIL, mItemList[position].url)
                mContext.startActivity(cilicatSearchDetail)
            } else {
                DownloadUtil.openWeb(mItemList[position].url, mContext)
            }
        }

        holder.type.apply {
            if(mItemList[position].url.contains(MagnetConstrant.CILICAT_HOME_URL)) {
                this.setImageResource(R.drawable.magnet_icon)
            } else {
                this.setImageResource(R.drawable.baidu_yun)
            }
        }
    }

    class CilicatSearchHolder(view: View) : RecyclerView.ViewHolder(view) {
        var card: CardView = view.findViewById(R.id.cilicat_search_card)
        var title: TextView = view.findViewById(R.id.cilicat_search_title)
        var attrs: TextView = view.findViewById(R.id.cilicat_search_attribute)
        var type: ImageButton = view.findViewById(R.id.cilicat_search_type)
    }
}