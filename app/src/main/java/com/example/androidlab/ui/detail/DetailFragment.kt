package com.example.androidlab.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.androidlab.R
import com.example.androidlab.Project

class DetailFragment : Fragment(R.layout.fragment_detail) {

    companion object {
        fun newInstance(project: Project): DetailFragment {
            val f = DetailFragment()
            f.arguments = Bundle().apply {
                putString("title", project.title)
                putString("description", project.description)
                putString("members", project.members)
                putIntegerArrayList("images", ArrayList(project.images))
            }
            return f
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val pager = view.findViewById<ViewPager2>(R.id.viewPager)
        val title = view.findViewById<TextView>(R.id.tvTitle)
        val desc = view.findViewById<TextView>(R.id.tvDescription)
        val members = view.findViewById<TextView>(R.id.tvMembers)

        title.text = arguments?.getString("title")
        desc.text = arguments?.getString("description")
        members.text = "팀원: ${arguments?.getString("members")}"

        val images = arguments?.getIntegerArrayList("images") ?: arrayListOf()
        pager.adapter = ImagePagerAdapter(images)
    }
}
