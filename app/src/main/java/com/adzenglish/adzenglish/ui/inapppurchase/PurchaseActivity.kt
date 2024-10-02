package com.adzenglish.adzenglish.ui.inapppurchase

import android.health.connect.datatypes.units.Length
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import com.adzenglish.adzenglish.base.baseapp.BaseActivity
import com.adzenglish.adzenglish.databinding.ActivityPurchaseBinding
import com.adzenglish.adzenglish.utils.Constants
import com.adzenglish.adzenglish.utils.extension.goneIf
import com.adzenglish.adzenglish.utils.extension.toast
import com.adzenglish.adzenglish.viewmodel.MainActivityVM
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResult
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PurchaseActivity : BaseActivity<ActivityPurchaseBinding>() {
    private var adapterc: PurchaseAdapter? = null
    private var billingClientc: BillingClient? = null
    private val viewModel: MainActivityVM by viewModels()
    private var onPurchaseResponsec: OnPurchaseResponse? = null
    private var productDetailsListc: MutableList<ProductDetails> = arrayListOf()

    override fun init() {
        initViews()
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onResume() {
        showLoadingDialog()
        billingClientc?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP)
                .build()) { billingResult: BillingResult, list: List<Purchase> ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                hideLoadingDialog()
                for (purchase in list) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                        verifyInAppPurchase(purchase)
                    }
                }

            } else if (billingResult.responseCode != -1) {
                hideLoadingDialog()
            }
        }
        super.onResume()
    }

    private fun handlePurchase(purchase: Purchase?) {
        val consumeParams = purchase?.purchaseToken?.let {
            ConsumeParams.newBuilder()
                .setPurchaseToken(it)
                .build()
        }
        val listener =
            ConsumeResponseListener { billingResult, purchaseToken ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Handle the success of the consume operation.
                    // verifyInAppPurchase(purchase);
                }
            }
        if (consumeParams != null) {
            billingClientc?.consumeAsync(consumeParams, listener)
        }
    }

    private fun initViews() {
        adapterc = PurchaseAdapter(this) { item ->
            launchPurchaseFlow(item)
        }
        binding.rcvData.setHasFixedSize(true)
        binding.rcvData.adapter = adapterc

        billingClientc = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener { billingResult: BillingResult, list: List<Purchase?>? ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
                        for (purchase in list) {
                            handlePurchase(purchase)
                        }
                    }
                }.build()
        establishConnection()
    }

    fun establishConnection() {
        billingClientc?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    showProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                establishConnection()
            }
        })
    }

    fun showProducts() {
        val params = QueryProductDetailsParams.newBuilder().setProductList(inAppProductList).build()
        billingClientc?.queryProductDetailsAsync(params) { billingResult : BillingResult?, prodDetailsList: List<ProductDetails> ->
            productDetailsListc.clear()
            Handler(Looper.getMainLooper()).postDelayed({
                hideLoadingDialog()
                productDetailsListc.addAll(prodDetailsList)
                productDetailsListc.let { adapterc?.setData(it) }
                binding.tvError.goneIf(prodDetailsList.isNotEmpty())
                binding.ivNoData.goneIf(prodDetailsList.isNotEmpty())
                if (prodDetailsList.isEmpty()) toast("prodDetailsList, size = 0")
            }, 2000)
        }
    }

    private val inAppProductList: ImmutableList<QueryProductDetailsParams.Product>
        get() = ImmutableList.of(
            QueryProductDetailsParams.Product.newBuilder().setProductId(Constants.KEY_NOTE_1)
                .setProductType(BillingClient.ProductType.INAPP).build(),
            QueryProductDetailsParams.Product.newBuilder().setProductId(Constants.KEY_NOTE_2)
                .setProductType(BillingClient.ProductType.INAPP).build(),
            QueryProductDetailsParams.Product.newBuilder().setProductId(Constants.KEY_NOTE_3)
                .setProductType(BillingClient.ProductType.INAPP).build(),
            QueryProductDetailsParams.Product.newBuilder().setProductId(Constants.KEY_NOTE_4)
                .setProductType(BillingClient.ProductType.INAPP).build(),
            QueryProductDetailsParams.Product.newBuilder().setProductId(Constants.KEY_NOTE_5)
                .setProductType(BillingClient.ProductType.INAPP).build(),
            QueryProductDetailsParams.Product.newBuilder().setProductId(Constants.KEY_NOTE_6)
                .setProductType(BillingClient.ProductType.INAPP).build(),
            QueryProductDetailsParams.Product.newBuilder().setProductId(Constants.KEY_NOTE_7)
                .setProductType(BillingClient.ProductType.INAPP).build(),
            QueryProductDetailsParams.Product.newBuilder().setProductId(Constants.KEY_NOTE_8)
                .setProductType(BillingClient.ProductType.INAPP).build()
        )

    private fun verifyInAppPurchase(purchases: Purchase) {
        val acknowledgePurchaseParams =
            AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchases.purchaseToken).build()
            billingClientc?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult: BillingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val proId = purchases.products[Constants.INDEX_0]
                val quantity = purchases.quantity
                setPurchaseResponse(object : OnPurchaseResponse {
                    override fun onResponse(proId: String?, quantity: Int) {
                        proId?.let { setupResult(it, quantity) }
                    }
                })
                onPurchaseResponsec?.onResponse(proId, quantity)
                allowMultiplePurchases(purchases)
            }
        }
    }

    private fun allowMultiplePurchases(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        billingClientc?.consumeAsync(consumeParams) { _, _ ->
            toast(" Resume item ")
        }
    }

    private fun launchPurchaseFlow(productDetails: ProductDetails) {
        val productDetailsParamsList = ImmutableList.of(
            BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(productDetails)
                .build()
        )
        val billingFlowParams =
            BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList)
                .build()

        billingClientc?.launchBillingFlow(this, billingFlowParams)
    }

    private fun setupResult(proId: String, quantity: Int) {
        val remainCoin = getCoinFromKey(proId) * quantity
        showLoadingDialog()
        viewModel.updateCoin(remainCoin) { update ->
            hideLoadingDialog()
            toast(if (update) "Xin chúc mừng, bạn đã mua gold thành công!" else "Có lỗi kết nối đến server!")
        }
    }

    private fun getCoinFromKey(coinId: String): Int {
        return when (coinId) {
            Constants.KEY_NOTE_1 -> 1
            Constants.KEY_NOTE_2 -> 5
            Constants.KEY_NOTE_3 -> 30
            Constants.KEY_NOTE_4 -> 50
            Constants.KEY_NOTE_5 -> 80
            Constants.KEY_NOTE_6 -> 120
            Constants.KEY_NOTE_7 -> 150
            Constants.KEY_NOTE_8 -> 250
            else -> 0
        }
    }

    internal interface OnPurchaseResponse {
        fun onResponse(proId: String?, quantity: Int)
    }

    private fun setPurchaseResponse(onPurchaseResponse: OnPurchaseResponse) {
        this.onPurchaseResponsec = onPurchaseResponse
    }

    override fun getViewBinding(inflater: LayoutInflater): ActivityPurchaseBinding =
        ActivityPurchaseBinding.inflate(layoutInflater)
}