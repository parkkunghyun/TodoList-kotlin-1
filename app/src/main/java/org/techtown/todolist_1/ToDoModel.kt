package org.techtown.todolist_1

// 데이터 저장
data class ToDoModel(
    var id: Int,
    var task: String, // 할일
    var status: Int, // 체크상태
)
