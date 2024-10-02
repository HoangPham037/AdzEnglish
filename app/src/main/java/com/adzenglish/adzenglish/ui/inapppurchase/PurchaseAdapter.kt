package com.adzenglish.adzenglish.ui.inapppurchase

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.utils.Constants
import com.android.billingclient.api.ProductDetails

class PurchaseAdapter(val context: Context, val event: (ProductDetails) -> Unit) :
    RecyclerView.Adapter<PurchaseAdapter.ViewHolder>() {
    private var productDetailsListc: List<ProductDetails> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(productDetailsList: List<ProductDetails>) {
        this.productDetailsListc = productDetailsList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView =
            LayoutInflater.from(context).inflate(R.layout.item_subscription, parent, false)
        return ViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.display(productDetailsListc[position]  as ProductDetails )
    }

    override fun getItemCount(): Int {
        return productDetailsListc.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSubName: TextView = itemView.findViewById(R.id.tvSubName)
        private val item: ConstraintLayout = itemView.findViewById(R.id.item)

        fun display(productDetails: ProductDetails?) {
                 productDetails?.oneTimePurchaseOfferDetails?.formattedPrice?.let {price->
                     tvSubName.text = setTitleValue(productDetails.productId, price)
                     item.setOnClickListener {
                         event.invoke(productDetails)
                 }
            }
        }

        private fun setTitleValue(productId: String, price: String): String {
            return when (productId) {
                Constants.KEY_NOTE_1 -> String.format(
                    context.resources.getString(R.string.message_purchase_one), "$price/1 coin"
                )

                Constants.KEY_NOTE_2 -> String.format(
                    context.resources.getString(R.string.message_purchase_one), "$price/5 coin"
                )

                Constants.KEY_NOTE_3 -> String.format(
                    context.resources.getString(R.string.message_purchase_one), "$price/30 coin"
                )

                Constants.KEY_NOTE_4 -> String.format(
                    context.resources.getString(R.string.message_purchase_one), "$price/50 coin"
                )

                Constants.KEY_NOTE_5 -> String.format(
                    context.resources.getString(R.string.message_purchase_one), "$price/80 coin"
                )

                Constants.KEY_NOTE_6 -> String.format(
                    context.resources.getString(R.string.message_purchase_one), "$price/120 coin"
                )

                Constants.KEY_NOTE_7 -> String.format(
                    context.resources.getString(R.string.message_purchase_one), "$price/150 coin"
                )

                Constants.KEY_NOTE_8 -> String.format(
                    context.resources.getString(R.string.message_purchase_one), "$price/250 coin"
                )

                else -> String.format(
                    context.resources.getString(R.string.message_purchase_one),
                    "/0 coin"
                )
            }
        }
    }
}
