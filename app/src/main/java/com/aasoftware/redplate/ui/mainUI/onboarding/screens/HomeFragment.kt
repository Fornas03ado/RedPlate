package com.aasoftware.redplate.ui.mainUI.onboarding.screens

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aasoftware.redplate.databinding.FragmentHomeBinding
import com.aasoftware.redplate.domain.Post
import com.aasoftware.redplate.domain.User

class HomeFragment : Fragment() {
    companion object {
        private val user1 = User("fakeId", "McQueen", "fake@email.com")
        private val user2 = User("fakeId", "Chopete", "fake@email.com")
        private val post1 = Post("post1", user1, Uri.EMPTY, "Cachaaaaun.\nTrozo mierda.\nChopo chipi chopete chupa chupete.", 124233, true, System.currentTimeMillis())
        private val post2 = Post("post1", user1, Uri.EMPTY, null, 133, false, System.currentTimeMillis() - 700000)
        private val post3 = Post("post1", user2, Uri.EMPTY, "Me gustan mucho los chopos :)", 981234233, true, System.currentTimeMillis() - 1234525890)
        val posts = listOf(post1, post2, post3, post1, post3)
    }

    /** Object that contains the reference to [HomeFragment] layout views */
    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView() and onDestroyView().
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // val homeViewModel =
        // ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.storiesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.storiesRecyclerView.visibility = View.GONE

        binding.postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.postsRecyclerView.adapter = HomePostsAdapter().apply { submitList(posts) }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}