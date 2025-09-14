package com.manutd.ronaldo.practicemakesperfect.presentation.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.manutd.ronaldo.practicemakesperfect.R
import com.manutd.ronaldo.practicemakesperfect.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPagerAdapter = HomeViewPagerAdapter(this)

        // 2. Gán adapter cho ViewPager2
        binding.pager.adapter = viewPagerAdapter

        // 3. Kết nối TabLayout với ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            // Đặt tên cho từng tab dựa vào vị trí
            tab.text = when (position) {
                0 -> "Theo dõi"
                1 -> "Cho bạn"
                2 -> "Bóng đá"
                3 -> "Công nghệ"
                4 -> "Đời sống"
                else -> null
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private class HomeViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        // Số lượng tab/trang
        override fun getItemCount(): Int = 5

        // Tạo Fragment cho từng vị trí
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> NewsFeedFragment()
                1 -> NewsFeedFragment()
                2 -> NewsFeedFragment()
                3 -> NewsFeedFragment()
                4 -> NewsFeedFragment()
                else -> throw IllegalStateException("Vị trí không hợp lệ: $position")
            }
        }
    }
}