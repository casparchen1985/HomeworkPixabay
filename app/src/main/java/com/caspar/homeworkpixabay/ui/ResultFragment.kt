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
import com.caspar.homeworkpixabay.ui.customized.collectLatestLifecycleFlow
import com.caspar.homeworkpixabay.ui.enumClass.ImageDisplayType
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

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
    private var gridSpanCount by Delegates.notNull<Int>()
    private var gridTypeOrientation by Delegates.notNull<Int>()
    private val gridDefaultSpanCount = 2
    private val gridBorderWidthDP = 10
    private var displayType: ImageDisplayType? = null
    private var displayTypeClassName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        displayTypeClassName = resources.getString(R.string.name_display_type)

        // ImageDisplayType
        // 優先讀取 sharedPref 的設定值 (手動操作)
        SharedPrefManager().apply {
            val record = this.readInt(displayTypeClassName)
            if (record > 0) {
                displayType = ImageDisplayType.from(record)
            }
        }
        // 再讀 remoteConfig 設定值 (預設)
        if (displayType == null) {
            val typeInt = Firebase.remoteConfig.getDouble(displayTypeClassName).toInt()
            displayType = ImageDisplayType.from(typeInt) ?: ImageDisplayType.GRID
        }

        // gridSpanCount
        gridSpanCount = Firebase.remoteConfig
            .getDouble(resources.getString(R.string.name_grid_span_count))
            .toInt()
            .let { if (it in 1..5) it else gridDefaultSpanCount }

        // gridTypeOrientation
        gridTypeOrientation = Firebase.remoteConfig
            .getDouble(resources.getString(R.string.name_grid_orientation))
            .toInt()
            .let { if (it == 1) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)

        linearLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        staggeredGridLayoutManager = StaggeredGridLayoutManager(gridSpanCount, gridTypeOrientation)

        displayType?.let { recyclerAdapter = ImagesAdapter(it) }
        recyclerAdapter.apply {
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    showLoadingText(false)
                }
            })
            setImageClickListener { url ->
                ImageDialogFragment(url).show(
                    childFragmentManager,
                    resources.getString(R.string.tag_image_dialog)
                )
            }
        }

        recyclerView = binding.recyclerView
        recyclerView.apply {
            layoutManager =
                if (displayType == ImageDisplayType.GRID) staggeredGridLayoutManager else linearLayoutManager
            adapter = recyclerAdapter
            itemAnimator = null
            addItemDecoration(
                if (displayType == ImageDisplayType.GRID) ImagesDecoration(gridBorderWidthDP) else ImagesListDecoration()
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
                        showLoadingText(true)
                    }
                }
            })
        }

        binding.pullDownLayout.setOnRefreshListener {
            binding.pullDownLayout.isRefreshing = false
            recyclerAdapter.submitList(null)
            viewModel.readImagesData()
        }

        setFABIcon()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener {
            changeDisplayType()
        }

        collectLatestLifecycleFlow(viewModel.imageContentSharedFlow) {
            recyclerAdapter.submitList(it)
            showLoadingText(false)
        }

        collectLatestLifecycleFlow(viewModel.fetchMoreSharedFlow) {
            showLoadingText(false)
            if (!it) {
                Toast.makeText(
                    requireContext(), resources.getString(R.string.message_fetch_image_fail), Toast.LENGTH_SHORT
                ).show()
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

    private fun setFABIcon() {
        if (displayType == null) return
        binding.fab.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                if (displayType == ImageDisplayType.GRID) R.drawable.ic_list else R.drawable.ic_grid
            )
        )
    }

    private fun changeDisplayType() {
        // 更新最新設定值
        displayType = when (displayType) {
            ImageDisplayType.GRID -> ImageDisplayType.LIST
            ImageDisplayType.LIST -> ImageDisplayType.GRID
            null -> return
        }

        // 設定值存擋
        SharedPrefManager().saveInt(displayTypeClassName, displayType!!.value)

        setFABIcon()

        // 更新 Adapter ViewHolder
        recyclerAdapter.setDisplayType(displayType!!)

        // RecyclerView 套用新設定
        recyclerView.apply {
            layoutManager =
                if (displayType == ImageDisplayType.GRID) staggeredGridLayoutManager else linearLayoutManager
            adapter = recyclerAdapter
            if (itemDecorationCount > 0) removeItemDecorationAt(0)
            addItemDecoration(
                if (displayType == ImageDisplayType.GRID) ImagesDecoration(gridBorderWidthDP) else ImagesListDecoration()
            )
        }
    }

    private fun showLoadingText(enable: Boolean) {
        binding.loadingText.visibility = if (enable) View.VISIBLE else View.GONE
    }
}
