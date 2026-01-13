package com.example.androidlab.ui.grid

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidlab.R
import com.example.androidlab.models.Project
import com.example.androidlab.ui.detail.DetailFragment

/**
 * [GridAdapter] 클래스 설명:
 * 메인 화면의 그리드(격자) 목록에 각 프로젝트 데이터를 뿌려주는 어댑터입니다.
 * 사진, 제목, 팀원, 좋아요 개수를 화면에 표시합니다.
 */
class GridAdapter(
    private var items: List<Project>, // 화면에 표시할 프로젝트 리스트
    private val onClick: (DetailFragment) -> Unit // 항목 클릭 시 실행할 동작 (상세화면 이동)
) : RecyclerView.Adapter<GridAdapter.VH>() {

    /**
     * [VH] (ViewHolder) 클래스 설명:
     * 그리드의 한 칸(아이템)에 들어가는 뷰들을 보관하는 바구니입니다.
     */
    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgProject)       // 프로젝트 대표 이미지
        val title: TextView = view.findViewById(R.id.tvTitle)         // 프로젝트 제목
        val team: TextView = view.findViewById(R.id.tvTeam)           // 팀원 정보
        val heartCount: TextView = view.findViewById(R.id.tvHeartCount) // 좋아요 개수 텍스트
    }

    /**
     * 아이템 하나가 들어갈 '틀'을 만드는 함수입니다. (item_grid.xml 사용)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grid, parent, false)
        return VH(view)
    }

    /**
     * 만들어진 '틀'에 실제 프로젝트 데이터를 바인딩(연결)하는 함수입니다.
     */
    override fun onBindViewHolder(holder: VH, position: Int) {
        val project = items[position] // 현재 순서에 맞는 프로젝트 데이터 꺼내기
        
        // 1. 이미지 표시 (Glide 라이브러리 사용)
        if (project.imageUrls.isNotEmpty()) {
            // 이미지 URL 리스트 중 첫 번째 사진을 대표 이미지로 사용
            Glide.with(holder.img.context)
                .load(project.imageUrls.first())
                .placeholder(ColorDrawable(Color.WHITE)) // 로딩 중에는 흰색 배경 표시
                .error(ColorDrawable(Color.WHITE))       // 에러 발생 시 흰색 배경 표시
                .centerCrop()                            // 이미지 중앙을 기준으로 꽉 채우기
                .into(holder.img)                        // 최종적으로 이미지 뷰에 넣기
        } else {
            // 등록된 사진이 없는 경우 흰색 배경으로 처리
            holder.img.setImageDrawable(ColorDrawable(Color.WHITE))
        }
        
        // 2. 텍스트 정보 표시
        holder.title.text = project.title        // 제목 설정
        holder.team.text = project.members       // 팀원 정보 설정
        holder.heartCount.text = project.likedBy.size.toString() // 좋아요 리스트의 크기를 숫자로 표시

        // 3. 클릭 이벤트 설정
        holder.itemView.setOnClickListener {
            // 클릭 시 DetailFragment의 새 인스턴스를 만들어 콜백 함수로 전달
            onClick(DetailFragment.newInstance(project))
        }
    }

    /**
     * 리스트의 총 개수를 반환합니다.
     */
    override fun getItemCount() = items.size

    /**
     * 새로운 데이터 리스트가 들어왔을 때 화면을 갱신하는 함수입니다.
     */
    fun updateItems(newItems: List<Project>) {
        this.items = newItems
        notifyDataSetChanged() // 리사이클러뷰에게 데이터가 바뀌었으니 다시 그리라고 명령
    }
}
