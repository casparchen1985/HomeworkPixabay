package com.caspar.homeworkpixabay.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.compose.ui.text.substring
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.caspar.homeworkpixabay.databinding.FragmentResultBinding
import com.caspar.homeworkpixabay.ui.adapter.ImagesAdapter
import com.caspar.homeworkpixabay.ui.adapter.ImagesDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultFragment : Fragment() {
    private val args: ResultFragmentArgs by navArgs()
    private val viewModel: ResultViewModel by viewModels()
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: ImagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)

        val staggeredLM = StaggeredGridLayoutManager(3, LinearLayout.VERTICAL)
//        val staggeredLM = StaggeredGridLayoutManager(5, LinearLayout.HORIZONTAL)
        recyclerAdapter = ImagesAdapter()
        recyclerAdapter.apply {
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    binding.loadingText.visibility = View.GONE
                }
            })
            setImageClickListener { url ->
                Toast.makeText(requireContext(), "Img Url: ... ${url.takeLast(15)}", Toast.LENGTH_SHORT).show()
            }
        }

        recyclerView = binding.recyclerView
        recyclerView.apply {
            layoutManager = staggeredLM
            adapter = recyclerAdapter
            addItemDecoration(ImagesDecoration(10))
            itemAnimator = null
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (staggeredLM.findLastCompletelyVisibleItemPositions(null).max() == recyclerAdapter.itemCount - 1) {
                        viewModel.fetchMoreImages(args.searchKeyword, args.searchType.value)
                        binding.loadingText.visibility = View.VISIBLE
                    }
                }
            })
        }

        binding.pullDownLayout.setOnRefreshListener {
            binding.pullDownLayout.isRefreshing = false
            recyclerAdapter.submitList(null)
            viewModel.readImagesData()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.imageContentLiveData.observe(viewLifecycleOwner) {
            recyclerAdapter.submitList(it)
        }

        viewModel.fetchMoreLiveData.observe(viewLifecycleOwner) {
            if (!it) {
                binding.loadingText.visibility = View.GONE
                Toast.makeText(requireContext(), "載入圖片失敗, 請稍後再試", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.readImagesData()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.readImagesData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}