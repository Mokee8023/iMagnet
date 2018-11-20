package com.mokee.imagnet.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.mokee.imagnet.R
import com.mokee.imagnet.activity.nima.NimaDetailsActivity
import com.mokee.imagnet.model.NimaItem
import com.mokee.imagnet.presenter.MagnetPresenter
import com.mokee.imagnet.utils.ClipboardUtil
import com.mokee.imagnet.utils.MagnetUtil
import com.mokee.imagnet.utils.ThunderUtil

class NimaAdapter : RecyclerView.Adapter<NimaAdapter.NimaHolder> {
    private var mContext: Context
    private var mNimaItemList: MutableList<NimaItem>

    constructor(context: Context, homeItemList: MutableList<NimaItem>): super() {
        this.mNimaItemList = homeItemList
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NimaHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.nima_list_item, parent, false)
        return NimaHolder(view)
    }

    override fun getItemCount(): Int {
        return mNimaItemList.size
    }

    override fun onBindViewHolder(holder: NimaHolder, position: Int) {
        holder.title.text = mNimaItemList[position].title
        holder.details.text = mNimaItemList[position].detail

        holder.magnet.setOnClickListener {
            if(!TextUtils.isEmpty(mNimaItemList[position].magnet)) {
                MagnetUtil.startMagnet(mContext, mNimaItemList[position].magnet)
            }
        }
        holder.magnet.setOnLongClickListener {
            if(!TextUtils.isEmpty(mNimaItemList[position].magnet)) {
                ClipboardUtil.copyToClipboard(mContext, mNimaItemList[position].magnet)
                Toast.makeText(mContext, "Magnet链接已复制", Toast.LENGTH_SHORT).show()
            }
            true
        }
        arrayOf( holder.card, holder.title ).forEach {
            it.setOnClickListener {
                val nimaDetail = Intent(mContext, NimaDetailsActivity::class.java)
                nimaDetail.putExtra(NimaDetailsActivity.NIMA_ITEM_DETAIL, mNimaItemList[position].url)
                mContext.startActivity(nimaDetail)
            }
        }
    }

    class NimaHolder(view: View) : RecyclerView.ViewHolder(view) {
        var card: CardView = view.findViewById(R.id.nima_list_card)
        var title: TextView = view.findViewById(R.id.nima_title)
        var details : TextView = view.findViewById(R.id.nima_details)
        var magnet: ImageButton = view.findViewById(R.id.nima_magnet)
    }
}