import android.content.Context
import android.widget.TextView
import com.example.login_portal.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlin.math.cos
import kotlin.math.sin

class PieChartMarkerView(
    context: Context,
    layoutResource: Int,
    private val totalCredit: Int
) : MarkerView(context, layoutResource) {

    private val tvContent: TextView = findViewById(R.id.tvContent)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e is PieEntry) {
            val value = e.value.toInt()
            val label = e.label
            val percentage = (value.toFloat() / totalCredit * 100)


            // Sử dụng context để lấy chuỗi từ strings.xml
            val context = tvContent.context
            val labelValueText = context.getString(R.string.pie_label_value, label, value)
            val totalCreditText = context.getString(R.string.total_credit, totalCredit)
            val percentageText = context.getString(R.string.percentage, percentage)


            // Hiển thị nội dung lên TextView
            tvContent.text = """
       $labelValueText
       $totalCreditText
       $percentageText
   """.trimIndent()
        }
        super.refreshContent(e, highlight)
    }


    override fun getOffset(): MPPointF {
        return if (this.chartView is PieChart) {
            val pieChart = this.chartView as PieChart
            val highlight = pieChart.highlighted?.firstOrNull()
            
            if (highlight != null) {
                // Lấy góc của phần được chọn
                val angle = highlight.x * 360f / pieChart.data.entryCount + 
                           pieChart.rotationAngle
                val rad = Math.toRadians(angle.toDouble())
                
                // Tính bán kính để đảm bảo tooltip nằm ngoài chart
                val radius = pieChart.radius * 1.5f  // Tăng khoảng cách
                
                // Tính offset dựa trên góc phần tư
                val normalizedAngle = (angle % 360 + 360) % 360
                val offsetX = when {
                    normalizedAngle in 0f..90f -> radius  // Phần tư 1
                    normalizedAngle in 90f..270f -> -radius - width  // Phần tư 2 & 3
                    else -> radius  // Phần tư 4
                }
                
                val offsetY = when {
                    normalizedAngle in 0f..180f -> -height / 2f  // Nửa trên
                    else -> -height / 2f  // Nửa dưới
                }
                
                MPPointF(offsetX, offsetY)
            } else {
                MPPointF((-width / 2).toFloat(), -height.toFloat())
            }
        } else {
            MPPointF((-width / 2).toFloat(), -height.toFloat())
        }
    }
} 