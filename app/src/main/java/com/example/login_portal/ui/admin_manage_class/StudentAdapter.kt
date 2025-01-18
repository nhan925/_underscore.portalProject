package com.example.login_portal.ui.admin_manage_class

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.databinding.AdminStudentItemBinding

class StudentAdapter(private val onDeleteClick: (Student) -> Unit) :
    RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    private var students = listOf<Student>()

    inner class StudentViewHolder(private val binding: AdminStudentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(student: Student) {
            binding.apply {
                studentItemId.text = student.studentId
                studentItemName.text = student.fullName
                // If you need to handle click events, use the root view
                btnDelete.setOnClickListener {
                    onDeleteClick(student)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = AdminStudentItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(students[position])
    }

    override fun getItemCount() = students.size

    fun updateStudents(newStudents: List<Student>) {
        students = newStudents.sortedBy { it.studentId }
        notifyDataSetChanged()
    }
}