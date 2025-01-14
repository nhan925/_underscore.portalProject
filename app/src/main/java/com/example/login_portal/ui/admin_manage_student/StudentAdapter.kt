package com.example.login_portal.ui.admin_manage_student
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.databinding.AdminStudentItemBinding

class StudentAdapter(
    private val onStudentClick: (StudentInfo) -> Unit
) : ListAdapter<StudentInfo, StudentAdapter.StudentViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StudentInfo>() {
            override fun areItemsTheSame(oldItem: StudentInfo, newItem: StudentInfo): Boolean {
                return oldItem.studentId == newItem.studentId
            }

            override fun areContentsTheSame(oldItem: StudentInfo, newItem: StudentInfo): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class StudentViewHolder(private val binding: AdminStudentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(student: StudentInfo) {
            binding.studentItemName.text = student.fullName
            binding.studentItemId.text = student.studentId
            binding.studentItemGender.text = student.gender
            binding.root.setOnClickListener { onStudentClick(student) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = AdminStudentItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
