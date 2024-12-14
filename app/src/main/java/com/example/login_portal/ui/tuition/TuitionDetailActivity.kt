package com.example.login_portal.ui.tuition

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.BaseActivity
import com.example.login_portal.R
import com.example.login_portal.ui.zalo_pay.Api.CreateOrder
import org.json.JSONObject
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener
import java.text.DecimalFormat

class TuitionDetailActivity : BaseActivity() {

    private lateinit var yearTV : TextView
    private lateinit var semesterTV : TextView
    private lateinit var numberOfSubjectTV: TextView
    private lateinit var totalFeeTV: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: Button
    private lateinit var payButton: Button
    private val viewModel = TuitionViewModel.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tuition_detail)
        // ZaloPay Setup
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        ZaloPaySDK.init(2553, Environment.SANDBOX)


        yearTV = findViewById(R.id.tuition_detail_year)
        semesterTV = findViewById(R.id.tuition_detail_semester)
        numberOfSubjectTV = findViewById(R.id.tuition_detail_number_of_subject)
        totalFeeTV = findViewById(R.id.tuition_detail_total_fee)
        backButton = findViewById(R.id.tuition_detail_back_button)
        payButton = findViewById(R.id.tuition_detail_pay_button)

        yearTV.text = intent.getStringExtra("year")
        semesterTV.text = intent.getIntExtra("semester", 0).toString()
        numberOfSubjectTV.text = intent.getIntExtra("totalCourse", 0).toString()
        val totalFee = intent.getIntExtra("totalFee", 0)
        totalFeeTV.text = formatTuitionFee(totalFee)

        val adapter = AdapterForTuitionDetail(emptyList())
        recyclerView = findViewById(R.id.tuition_detail_recycler)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.tuitionDetail.observe(this) { tuitionDetailList ->
            adapter.updateData(tuitionDetailList)
        }

        viewModel.getTuitionDetailListFromDatabase(yearTV.text.toString(), semesterTV.text.toString().toInt())

        payButton.setOnClickListener {
            val orderApi: CreateOrder = CreateOrder()

            try {
                val data: JSONObject = orderApi.createOrder(totalFee.toString())
                val code = data.getString("return_code")
                if (code == "1") {
                    val token = data.getString("zp_trans_token")
                    ZaloPaySDK.getInstance().payOrder(this, token, "demozpdk://app", object : PayOrderListener {
                        override fun onPaymentSucceeded(p0: String?, p1: String?, p2: String?) {
                            Toast.makeText(applicationContext, resources.getString(R.string.tuition_detail_payment_success), Toast.LENGTH_LONG).show()
                            viewModel.updatePaymentTuition(totalFee) { isSuccess ->
                                if (isSuccess) {
                                    // TODO
                                } else {
                                    // TODO
                                }
                            }
                            finish()
                        }

                        override fun onPaymentCanceled(p0: String?, p1: String?) {
                            Toast.makeText(applicationContext, resources.getString(R.string.tuition_detail_payment_fail), Toast.LENGTH_LONG).show()
                            finish()
                        }

                        override fun onPaymentError(p0: ZaloPayError?, p1: String?, p2: String?) {
                            Toast.makeText(applicationContext, resources.getString(R.string.tuition_detail_payment_fail), Toast.LENGTH_LONG).show()
                            finish()
                        }
                    }
                    )

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        backButton.setOnClickListener {
            finish()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        ZaloPaySDK.getInstance().onResult(intent)
    }


    fun formatTuitionFee(tuitionFee: Int): String {
        val formatter = DecimalFormat("#,###")
        return formatter.format(tuitionFee)
    }

}
