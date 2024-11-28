package com.example.login_portal.ui.feedback_course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R

class AdapterForListQuestion(val questionList: List<Question>) :
    RecyclerView.Adapter<AdapterForListQuestion.QuestionViewHolder>() {

    // ViewHolder class để chứa các view của mỗi câu hỏi
    class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questionContentTextView: TextView = itemView.findViewById(R.id.question_content)   // TextView cho nội dung câu hỏi
        val ratingRadioGroup: RadioGroup = itemView.findViewById(R.id.rating_radio_group)       // RadioGroup cho các lựa chọn đánh giá
    }

    // Inflate layout cho mỗi item câu hỏi
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.question_item_list, parent, false)
        return QuestionViewHolder(itemView)
    }

    // Bind dữ liệu câu hỏi vào các view
    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questionList[position]

        // Gán nội dung câu hỏi
        holder.questionContentTextView.text = question.content

        // Đặt radio button theo đánh giá hiện tại
        when (question.rating) {
            1 -> holder.ratingRadioGroup.check(R.id.radioButton)
            2 -> holder.ratingRadioGroup.check(R.id.radioButton2)
            3 -> holder.ratingRadioGroup.check(R.id.radioButton3)
            4 -> holder.ratingRadioGroup.check(R.id.radioButton4)
            5 -> holder.ratingRadioGroup.check(R.id.radioButton5)
            else -> holder.ratingRadioGroup.clearCheck()
        }

        // Lắng nghe sự thay đổi khi người dùng chọn đánh giá khác
        holder.ratingRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButton -> question.rating = 1
                R.id.radioButton2 -> question.rating = 2
                R.id.radioButton3 -> question.rating = 3
                R.id.radioButton4 -> question.rating = 4
                R.id.radioButton5 -> question.rating = 5
            }
            question.isAnswered = true
        }
    }

    // Trả về kích thước của danh sách câu hỏi
    override fun getItemCount() = questionList.size
}
