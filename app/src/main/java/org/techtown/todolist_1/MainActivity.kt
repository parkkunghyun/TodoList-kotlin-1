package org.techtown.todolist_1

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class MainActivity : AppCompatActivity() {
    private var taskList: ArrayList<ToDoModel> = ArrayList()
    private lateinit var adapter: ToDoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var todoText: EditText
    private lateinit var addBtn: Button

    private lateinit var db: ToDoDB

    private lateinit var bottomLayout: LinearLayout

    private var gId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //DB
        db = ToDoDB(this)
        // 초기화
        taskList = ArrayList()
        bottomLayout = findViewById(R.id.bottom_section)
        todoText = findViewById(R.id.todo_text)
        addBtn = findViewById(R.id.add_btn)
        fab = findViewById(R.id.fab)

        // recyclerview설정
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // adapeter설정
        adapter = ToDoAdapter(db)
        adapter.setTask(taskList)

        // adapter적용
        recyclerView.adapter = adapter

        // 조회
        selectData()

        // 등록모드
        fab.setOnClickListener {
            viewMode("ADD")
        }

        // 추가 버튼
        addBtn.setOnClickListener {

            viewMode("FAB")
            // 입력값
            val text = todoText.text.toString()

            // ADD면 등록 아니면 수정
            if(addBtn.text.toString() == "ADD") {
                // 데이터 담기
                val task = ToDoModel(0, text, 0)

                // 할일 추가
                db.addTask(task)

                // 조회 및 리셋
                selectReset("ADD")
            } else { // 수정
                // 할일 수정
                db.updateTask(gId, text)

                // 조회 및 리셋
                selectReset("UPDATE")
            }

            // 키보드 내리기
            hideKeyboard(todoText)
        }

        // 할일 입력체크
        todoText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트 변경시 실행
                // 입력값이 없으면
                if(s.toString() == "") {
                    // 비활성화하기!
                    addBtn.isEnabled = false
                    addBtn.setTextColor(Color.GRAY)
                } else {
                    //활성화
                    addBtn.isEnabled = true
                    addBtn.setTextColor(Color.BLACK)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        // 스와이프 ( 수정 삭제 기능)
        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            // 스와이프 기능
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.absoluteAdapterPosition

                when(direction) {
                    ItemTouchHelper.LEFT -> { // 삭제
                        // 할일 변수에 담기
                        val id = taskList[position].id
                        // 아이템 삭제
                        adapter.removeTask(position)
                        // DB에서 삭제
                        db.deleteTask(id)

                    }
                    ItemTouchHelper.RIGHT -> { // 수정
                        viewMode("UPDATE")
                        // 할일 가져오기
                        val task = taskList[position].task

                        // 할일 ID 전역변수에 담기
                        gId = taskList[position].id

                        // 입력창에 수정할 할일
                        todoText.setText(task)
                        // 버튼 문구 변경
                        addBtn.text = "UPDATE"
                    }
                }
            }

            // 그리기
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                )
                    .addSwipeLeftBackgroundColor(Color.RED)
                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                    .addSwipeLeftLabel("삭제")
                    .setSwipeLeftLabelColor(Color.WHITE)

                    .addSwipeRightBackgroundColor(Color.BLUE)
                    .addSwipeRightActionIcon(R.drawable.ic_delete)
                    .addSwipeRightLabel("수정")
                    .setSwipeRightLabelColor(Color.WHITE)
                    .create()
                    .decorate()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }).attachToRecyclerView(recyclerView)

    }// onCreate()

    /**
     * 조회
     */

    private fun selectData() {
        // 조회
        taskList = db.getAllTasks()
        // 최신순정렬
        taskList.reverse()

        // 데이터 담기
        adapter.setTask(taskList)

        // 적용
        adapter.notifyDataSetChanged()
    }

    /**
     * 조회 및 리셋
     */
    private fun selectReset(type: String) {
        selectData()

        //할일 입력 초기화
        todoText.setText("")

        // 등록이 아니면 등록으로 변경
        if(type != "ADD") {
            addBtn.text = "ADD"
        }
    }
    private fun viewMode(type: String){
        // 입력하고 나면 입력창 사리지고 FAB보여줌
        if(type == "FAB") {
            // 입력창 숨김
            bottomLayout.visibility = View.GONE

            // fab 보여줌
            fab.visibility = View.VISIBLE
        } else {
            // 입력창 보여줌
            bottomLayout.visibility = View.VISIBLE

            // fab 숨김
            fab.visibility = View.INVISIBLE
        }
    }
    /**
     * 키보드 숨기기
     */
    private fun hideKeyboard(editText: EditText) {
        val manager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // 키보드로 숨김
        manager.hideSoftInputFromWindow(editText.applicationWindowToken, 0)

    }


}