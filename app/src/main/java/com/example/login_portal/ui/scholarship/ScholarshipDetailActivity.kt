package com.example.login_portal.ui.scholarship

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.login_portal.BaseActivity
import com.example.login_portal.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import java.text.NumberFormat
import java.util.Locale

class ScholarshipDetailActivity : BaseActivity() {
    lateinit var scholarship: Scholarship
    lateinit var detailText: TextInputEditText
    lateinit var scholarship_detail_backBtn: Button
    lateinit var applyFab: ExtendedFloatingActionButton
    lateinit var loadingOverlay: FrameLayout

    private fun formatCurrencyWithoutSymbol(amount: Double, locale: Locale = resources.configuration.locales[0]): String {
        val formatter = NumberFormat.getCurrencyInstance(locale)
        val formatted = formatter.format(amount)
        return formatted.replace(Regex("[^\\d.,]"), "")
    }

    private fun formatScholarshipDetails(scholarship: Scholarship): Spanned {
        // Create the base detail string without formatting
        var detailString = """
            |${scholarship.Description}
            |${getString(R.string.scholarship_detail_value)} ${formatCurrencyWithoutSymbol(scholarship.Amount)} ${scholarship.Currency}
            |${getString(R.string.scholarship_detail_slot)} ${scholarship.Slot}
            |${getString(R.string.scholarship_detail_criteria)} ${scholarship.Criteria}
            |${getString(R.string.scholarship_detail_deadline)} ${scholarship.getFormattedDate(scholarship.Deadline)}
            |${getString(R.string.scholarship_detail_papers)}
            |${scholarship.requiredDocumentsString}
            |${getString(R.string.scholarship_detail_note)}
        """.trimMargin()

        // Conditionally add 3 more rows if the scholarship is registered
        if (scholarship.Applied) {
            detailString += """
                |
                |
                |${getString(R.string.scholarship_detail_attachment)} ${getString(R.string.scholarship_detail_click_url)}
                |${getString(R.string.scholarship_detail_submitted_date)} ${scholarship.getFormattedDate(scholarship.SubmittedDate)}
                |${getString(R.string.scholarship_detail_status)} ${scholarship.ApplicationStatus}
            """.trimMargin()
        }

        // Create a SpannableString from the detail string
        val spannable = SpannableString(detailString)

        // Apply the bold style to the titles
        val titleBoldStyle = arrayOf(
            getString(R.string.scholarship_detail_value),
            getString(R.string.scholarship_detail_slot),
            getString(R.string.scholarship_detail_criteria),
            getString(R.string.scholarship_detail_deadline),
            getString(R.string.scholarship_detail_papers)
        )

        for (title in titleBoldStyle) {
            val start = detailString.indexOf(title)
            if (start != -1) {
                val end = start + title.length
                spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        // Apply the bold style to the new rows if the scholarship is applied
        if (scholarship.Applied) {
            val appliedRowsBoldStyle = arrayOf(
                getString(R.string.scholarship_detail_attachment),
                getString(R.string.scholarship_detail_submitted_date),
                getString(R.string.scholarship_detail_status)
            )

            for (title in appliedRowsBoldStyle) {
                val start = detailString.indexOf(title)
                if (start != -1) {
                    val end = start + title.length
                    spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }

            // Make the attachment URL clickable
            val attachmentStart = detailString.indexOf(getString(R.string.scholarship_detail_click_url))
            if (attachmentStart != -1) {
                val attachmentEnd = attachmentStart + getString(R.string.scholarship_detail_click_url).length

                spannable.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        // Handle the URL click here, e.g., open the URL in a browser
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scholarship.Attachment))
                        startActivity(intent)
                    }
                }, attachmentStart, attachmentEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        // Apply the style for the note (red and italic)
        val noteStart = detailString.indexOf(getString(R.string.scholarship_detail_note))
        if (noteStart != -1) {
            val noteEnd = noteStart + getString(R.string.scholarship_detail_note).length
            spannable.setSpan(StyleSpan(Typeface.ITALIC), noteStart, noteEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(ForegroundColorSpan(0xFFFF0000.toInt()), noteStart, noteEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) // Red color
        }

        return spannable
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scholarship_detail)

        scholarship = Gson().fromJson(intent.getStringExtra("SCHOLARSHIP_DETAIL"), Scholarship::class.java)

        loadingOverlay = findViewById(R.id.scholarship_loading_overlay)

        detailText = findViewById(R.id.scholarship_detailTV)
        detailText.setText(formatScholarshipDetails(scholarship))
        detailText.movementMethod = LinkMovementMethod.getInstance()

        scholarship_detail_backBtn = findViewById(R.id.scholarship_detail_backBtn)
        scholarship_detail_backBtn.setOnClickListener { finish() }

        applyFab = findViewById(R.id.scholarship_detail_applyFAB)
        when (scholarship.canApply) {
            false -> applyFab.visibility = View.GONE
            else -> applyFab.visibility = View.VISIBLE
        }

        applyFab.setOnClickListener {
            val bottomSheet = ScholarshipBottomSheetFragment(scholarship)
            bottomSheet.show(supportFragmentManager, ScholarshipBottomSheetFragment.TAG)
        }
    }
}