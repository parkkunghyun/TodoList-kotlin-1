package org.techtown.todolist_1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView

class ToDoAdapter(): RecyclerView.Adapter<ToDoAdapter.ViewHolder>() {
    private lateinit var mDb: ToDoDB
    constructor(db: ToDoDB): this() {
        mDb = db
    }
    private var todoList: ArrayList<ToDoModel> = ArrayList()

    /**
     * 화면설정
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.task_item, parent,false)
        return ViewHolder(view)
    }

    // 데이터 갯수 가져오기
    override fun getItemCount(): Int = todoList.size

    // 할일 목록 담기
    fun setTask(todoList: ArrayList<ToDoModel>) {
        this.todoList = todoList
        notifyDataSetChanged()
    }
    // 할일 삭제
    fun removeTask(position: Int) {
        todoList.removeAt(position)
        notifyItemRemoved(position)
    }

    // 데이터 설정
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 아이템담기
        val item: ToDoModel = todoList[position]
        // 할일 내용
        holder.mCheckBox.text = item.task
        // check
        holder.mCheckBox.isChecked = toBoolean(item.status)

        // check event
        holder.mCheckBox.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if(isChecked) {
                    mDb.updateStatus(item.id, 1)
                } else {
                    mDb.updateStatus(item.id, 0)
                }
            }
        } )

        /**
         * holder.mCheckBox.setOnCheckedChangeListener { _, isChecked ->
         *             if (isChecked) {
         *                 mDb.updateStatus(item.id, 1)
         *             }
         *         }
         */
    }

    // 상태값으로 체크함 0 이 아니면 true
    private fun toBoolean(item: Int) = item != 0

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val mCheckBox: CheckBox = view.findViewById(R.id.m_check_box)
    }
}