package com.example.login_portal.ui.admin_review_feedback

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.example.login_portal.BaseActivity
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentAdminReviewAppFeedbackDetailBinding
import com.example.login_portal.databinding.FragmentAdminReviewFeedbackBinding
import com.google.gson.Gson



class AdminReviewAppFeedbackDetailActivity : BaseActivity() {
    private lateinit var adminRvFeedbackViewModel: AdminReviewFeedbackViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_admin_review_app_feedback_detail)

        val feedbackItem = Gson().fromJson(intent.getStringExtra("FEEDBACK_DETAIL"), FeedbackApp::class.java)
        val titleType : TextView = findViewById(R.id.feedback_app_type_title)
        val titleSubmittedAt : TextView = findViewById(R.id.feedback_app_submitted_at_title)
        val contentFeedback : TextView = findViewById(R.id.feedback_app_content)

        if (feedbackItem.type == "Error Report" || feedbackItem.type == "Báo lỗi") {
            titleType.text = getString(R.string.review_feedback_detail_category_error)
        } else if (feedbackItem.type == "Give feedback" || feedbackItem.type == "Góp ý") {
            titleType.text = getString(R.string.review_feedback_detail_category_feedback)
        }
        titleSubmittedAt.text = feedbackItem.getFormattedDate()
        contentFeedback.setText(feedbackItem.content)

        val backButton: Button = findViewById(R.id.Feedback_Detail_backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }
}