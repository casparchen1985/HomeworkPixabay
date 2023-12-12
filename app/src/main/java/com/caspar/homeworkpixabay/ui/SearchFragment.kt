package com.caspar.homeworkpixabay.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnPreDrawListener
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.caspar.homeworkpixabay.R
import com.caspar.homeworkpixabay.databinding.FragmentSearchBinding
import com.caspar.homeworkpixabay.model.enumClass.SearchType
import com.caspar.homeworkpixabay.ui.customized.DimensionCalculator.Companion.toPX
import com.caspar.homeworkpixabay.ui.customized.collectLatestLifecycleFlow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private var abnormalExecuting = false
    private var historyAdapter: ArrayAdapter<String>? = null
    private var isDropDownShowing = false
    private var hasHistoryRecord = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        with(binding) {

            // 取得 searchLayout 寬度 並設定到 dropDown 上
            val vto = searchLayout.viewTreeObserver
            vto.addOnPreDrawListener(
                object : OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        searchAutoComplete.dropDownWidth = searchLayout.measuredWidth
                        searchLayout.viewTreeObserver.removeOnPreDrawListener(this)
                        return true
                    }
                }
            )

            searchAutoComplete.apply {
                dropDownHeight = 150.toPX
                threshold = 1
                dropDownVerticalOffset = 5.toPX
            }
        }

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var searchKeyword = ""
        var searchType = SearchType.ALL

        with(binding) {

            searchBtn.setOnClickListener {
                closeKB(it)
                searchKeyword = searchAutoComplete.text.toString().trim()

                if (searchKeyword.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.message_search_without_keyword),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                viewModel.saveSearchHistory(searchKeyword)

                val btnId = radioGroup.checkedRadioButtonId
                val checkBtn = radioGroup.findViewById<RadioButton>(btnId)
                searchType = (SearchType from (checkBtn.tag as String?)) ?: return@setOnClickListener
                viewModel.searchImages(searchKeyword, searchType.value)
                abnormalLayout.visibility = View.VISIBLE
                changeAbnormalContent(true)
            }

            searchAutoComplete.apply {
                // 點擊順序: autoComplete focus 有改變時
                setOnFocusChangeListener { view, focus ->
                    (view as? AutoCompleteTextView)?.let {
                        isDropDownShowing =
                            if (focus && hasHistoryRecord) {
                                it.showDropDown()
                                true
                            } else {
                                it.dismissDropDown()
                                false
                            }
                    }
                }

                // 點擊順序: 最後觸發
                setOnClickListener {
                    if (isDropDownShowing && hasHistoryRecord) {
                        (it as AutoCompleteTextView).showDropDown()
                    }
                }
            }

            abnormalBtn.setOnClickListener {
                binding.abnormalLayout.visibility = View.GONE
            }
        }

        collectLatestLifecycleFlow(viewModel.searchHistorySharedFlow) { records ->
            hasHistoryRecord = records.isNotEmpty()
            historyAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, records)
            binding.searchAutoComplete.setAdapter(historyAdapter)
        }

        collectLatestLifecycleFlow(viewModel.searchResultSharedFlow) { result ->
            binding.abnormalLayout.visibility = if (result) View.GONE else View.VISIBLE
            changeAbnormalContent(false)
            if (result) {
                binding.searchAutoComplete.text?.clear()
                this@SearchFragment.findNavController().navigate(
                    SearchFragmentDirections.actionNavSearchFragmentToNavResultFragment(searchKeyword, searchType)
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.readSearchHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun closeKB(view: View) {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        requireActivity().currentFocus?.clearFocus()
    }

    private fun changeAbnormalContent(loading: Boolean) {
        if (abnormalExecuting) return

        abnormalExecuting = true

        with(binding) {
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE

            message.apply {
                visibility = if (loading) View.GONE else View.VISIBLE
                text = resources.getString(R.string.message_search_without_response)
            }

            abnormalBtn.apply {
                visibility = if (loading) View.GONE else View.VISIBLE
                text = resources.getString(R.string.button_confirm)
                setOnClickListener {
                    binding.abnormalLayout.visibility = View.GONE
                }
            }
        }

        abnormalExecuting = false
    }
}