package com.example.androidlab.ui // ui 폴더 바로 밑이므로 패키지명 확인

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlab.R
import com.example.androidlab.Project // Project 클래스가 위치한 곳에 따라 자동 임포트될 거예요

class ProjectAdapter(private var projectList: List<Project>) :
    RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    // 유저님이 작성하신 XML 레이아웃의 ID들입니다.
    class ProjectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProject: ImageView = view.findViewById(R.id.imgProject)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvTeam: TextView = view.findViewById(R.id.tvTeam)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        // 레이아웃 파일명이 item_project가 맞는지 확인하세요!
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_project, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projectList[position]

        // 1. 제목 연결
        holder.tvTitle.text = project.title

        // 2. 팀원 정보 연결 (members 필드 사용)
        holder.tvTeam.text = project.members

        // 3. 이미지 처리 (현재는 기본 이미지 사용)
        holder.imgProject.setImageResource(R.drawable.project1_1)
    }

    override fun getItemCount() = projectList.size

    // 데이터를 갱신하는 함수
    fun updateData(newList: List<Project>) {
        projectList = newList
        notifyDataSetChanged()
    }
}