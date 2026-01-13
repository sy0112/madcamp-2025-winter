package com.example.androidlab.ui.register

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlab.R

/**
 * [ImageSelectAdapter] 클래스 설명:
 * 사용자가 등록 화면에서 선택한 사진들(Uri 목록)을 리사이클러뷰(RecyclerView)에 그려주기 위한 "중간 연결 다리"입니다.
 * 리사이클러뷰는 이 어댑터가 시키는 대로 화면에 아이템을 배치합니다.
 *
 * @param imageUris: 표시할 사진들의 주소(Uri)가 담긴 리스트입니다.
 */
class ImageSelectAdapter(private val imageUris: List<Uri>) :
    RecyclerView.Adapter<ImageSelectAdapter.ViewHolder>() {

    /**
     * [ViewHolder] 클래스 설명:
     * 리스트의 각 한 칸(아이템)을 구성하는 뷰(View)들을 담고 있는 "바구니"입니다.
     * findViewById를 매번 호출하면 성능이 떨어지므로, 처음에 한 번 찾아서 이 바구니에 보관해둡니다.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 아이템 디자인(item_selected_image.xml) 안에 있는 이미지 뷰를 찾아 변수에 저장합니다.
        val ivSelectedImage: ImageView = view.findViewById(R.id.ivSelectedImage)
    }

    /**
     * [onCreateViewHolder] 함수 설명:
     * 아이템을 담을 "틀(ViewHolder)"을 처음으로 만드는 단계입니다. (공사 단계)
     * 딱 필요한 개수만큼만 틀을 생성하며, 화면에 보일 새로운 칸이 필요할 때 안드로이드가 호출합니다.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // LayoutInflater를 이용해 XML 디자인 파일(item_selected_image.xml)을 실제 메모리에 올립니다.
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selected_image, parent, false)
        
        // 만들어진 뷰를 바구니(ViewHolder)에 담아서 반환합니다.
        return ViewHolder(view)
    }

    /**
     * [onBindViewHolder] 함수 설명:
     * 만들어진 "틀"에 실제 "데이터"를 끼워 넣는 단계입니다. (전시 단계)
     * 사용자가 스크롤을 할 때마다 해당 순서(position)에 맞는 사진을 칸(holder)에 그려줍니다.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 리스트에서 현재 순서에 맞는 이미지 주소(Uri)를 꺼냅니다.
        val currentUri = imageUris[position]
        
        // 바구니에 들어있던 이미지 뷰에 해당 사진 주소를 설정하여 화면에 사진이 나오게 합니다.
        holder.ivSelectedImage.setImageURI(currentUri)
    }

    /**
     * [getItemCount] 함수 설명:
     * 리사이클러뷰에게 "보여줄 아이템이 총 몇 개인지" 알려주는 함수입니다.
     * 이 숫자만큼 리스트의 칸이 만들어집니다.
     * 이거 약속이라서 넣는 거임.
     */
    override fun getItemCount() = imageUris.size
}
