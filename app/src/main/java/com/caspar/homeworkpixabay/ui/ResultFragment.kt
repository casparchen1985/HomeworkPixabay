package com.caspar.homeworkpixabay.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.caspar.homeworkpixabay.R
import com.caspar.homeworkpixabay.databinding.FragmentResultBinding
import com.caspar.homeworkpixabay.model.SharedPrefManager
import com.caspar.homeworkpixabay.ui.adapter.ImagesAdapter
import com.caspar.homeworkpixabay.ui.adapter.ImagesDecoration
import com.caspar.homeworkpixabay.ui.adapter.ImagesListDecoration
import com.caspar.homeworkpixabay.ui.enumClass.ImageDisplayType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultFragment : Fragment() {
    private val args: ResultFragmentArgs by navArgs()
    private val viewModel: ResultViewModel by viewModels()
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: ImagesAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    private var displayType = ImageDisplayType.GRID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO update DisplayType from remote config

        // 讀取 sharedPref 中 ImageDisplayType 的設定值
        SharedPrefManager().apply {
            val record = this.readInt("ImageDisplayType")
            if (record > 0) {
                displayType = ImageDisplayType.from(record) ?: return@apply
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)

        linearLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        staggeredGridLayoutManager = StaggeredGridLayoutManager(3, LinearLayout.VERTICAL)
//        staggeredGridLayoutManager = StaggeredGridLayoutManager(5, LinearLayout.HORIZONTAL)

        recyclerAdapter = ImagesAdapter(displayType)
        recyclerAdapter.apply {
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    binding.loadingText.visibility = View.GONE
                }
            })
            setImageClickListener { url ->
                ImageDialogFragment(url).show(childFragmentManager, "ImageDialogFragment")
            }
        }

        recyclerView = binding.recyclerView
        recyclerView.apply {
            layoutManager =
                if (displayType == ImageDisplayType.GRID) staggeredGridLayoutManager else linearLayoutManager
            adapter = recyclerAdapter
            itemAnimator = null
            addItemDecoration(
                if (displayType == ImageDisplayType.GRID) ImagesDecoration(10) else ImagesListDecoration()
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val maxPosition = recyclerAdapter.itemCount - 1
                    if (
                        (layoutManager as? LinearLayoutManager)?.findLastCompletelyVisibleItemPosition() == maxPosition ||
                        (layoutManager as? StaggeredGridLayoutManager)?.findLastCompletelyVisibleItemPositions(null)
                            ?.max() == maxPosition
                    ) {
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
        binding.fab.setOnClickListener {
            changeDisplayType()
        }

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

    private fun changeDisplayType() {
        // 更新最新設定值
        displayType = when (displayType) {
            ImageDisplayType.GRID -> ImageDisplayType.LIST
            ImageDisplayType.LIST -> ImageDisplayType.GRID
        }

        // 設定值存擋
        SharedPrefManager().saveInt("ImageDisplayType", displayType.value)

        // 更新 FAB icon
        binding.fab.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                if (displayType == ImageDisplayType.GRID) R.drawable.ic_list else R.drawable.ic_grid
            )
        )

        // 更新 Adapter ViewHolder
        recyclerAdapter.setDisplayType(displayType)

        // RecyclerView 套用新設定
        recyclerView.apply {
            layoutManager =
                if (displayType == ImageDisplayType.GRID) staggeredGridLayoutManager else linearLayoutManager
            adapter = recyclerAdapter
            if (itemDecorationCount > 0) removeItemDecorationAt(0)
            addItemDecoration(
                if (displayType == ImageDisplayType.GRID) ImagesDecoration(10) else ImagesListDecoration()
            )
        }
    }
}
