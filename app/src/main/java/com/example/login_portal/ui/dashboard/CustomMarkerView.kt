import android.content.Context
import android.widget.TextView
import com.example.login_portal.R
import com.example.login_portal.ui.dashboard.DashboardViewModel
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class CustomMarkerView(
    context: Context,
    layoutResource: Int,
    private val semester: DashboardViewModel.Semester,
    private val rank: String
) : MarkerView(context, layoutResource) {

    private val tvContent: TextView = findViewById(R.id.tvContent)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        tvContent.text = """
            ${semester.name}
            ${context.getString(R.string.market_gpa, semester.gpa)}
            ${context.getString(R.string.market_rank, rank)}
        """.trimIndent()
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
} 