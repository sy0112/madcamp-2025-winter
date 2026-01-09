package com.example.androidlab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class Item(val projectName: String, val projectDescription: String, val groupMember: String)
class MainActivity : AppCompatActivity() {
    // main activity는 앱이 실행될 때 가장 먼저 실행되는 화면임.

    override fun onCreate(savedInstanceState: Bundle?) {
        // 액티비티가 생성될 때 자동으로 호출
        super.onCreate(savedInstanceState)

        // ✅ XML 연결
        setContentView(R.layout.activity_main)
        // 이 activity의 화면으로 사용하겠다. 이 xml에 있는 것들이 실제 화면에 나타남.

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        // 변수에 참조 연결. 이건 activity_main.xml에 있음.

        recyclerView.layoutManager = LinearLayoutManager(this)
        // 어떤 방향 규칙으로 배치할 지 정하는 관리자.

        val items = listOf(
            Item("아이템1", "내용1", "2023"),
            Item("아이템2", "내용2", "2023"),
            Item("아이템3", "내용3", "2023")
        )

        recyclerView.adapter = ItemAdapter(items)
    }
}


//
//class BoardAdapter(val itemList: ArrayList<BoardItem>) :
//    RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view, parent, false)
//        return BoardViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
//        holder.tv_time.text = itemList[position].time
//        holder.tv_title.text = itemList[position].title
//        holder.tv_name.text = itemList[position].name
//    }
//
//    override fun getItemCount(): Int {
//        return itemList.count()
//    }
//
//
//    inner class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val tv_project_name = itemView.findViewById<TextView>(R.id.tv_time)
//        val tv_project_description = itemView.findViewById<TextView>(R.id.tv_title)
//        val tv_name = itemView.findViewById<TextView>(R.id.tv_name)
//    }
//}