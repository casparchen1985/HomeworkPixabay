package com.caspar.homeworkpixabay.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.caspar.homeworkpixabay.databinding.FragmentSearchBinding
import com.caspar.homeworkpixabay.model.enumClass.SearchType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private var abnormalExecuting = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var searchKeyword = ""
        var searchType = SearchType.ALL

        with(binding) {
            searchBtn.setOnClickListener {
                closeKB(it)
                searchKeyword = searchEditColumn.text.toString().trim()

                if (searchKeyword.isEmpty()) {
                    Toast.makeText(requireContext(), "請輸入與圖片相關的關鍵字", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val btnId = radioGroup.checkedRadioButtonId
                val checkBtn = radioGroup.findViewById<RadioButton>(btnId)
                searchType = (SearchType from (checkBtn.tag as String?)) ?: return@setOnClickListener
                viewModel.searchImages(searchKeyword, searchType.value)
                abnormalLayout.visibility = View.VISIBLE
                changeAbnormalContent(true)
            }

            abnormalBtn.setOnClickListener {
                binding.abnormalLayout.visibility = View.GONE
            }
        }

        viewModel.searchResultLiveData.observe(viewLifecycleOwner) { result ->
            binding.abnormalLayout.visibility = if (result) View.GONE else View.VISIBLE
            changeAbnormalContent(false)
            if (result) {
                binding.searchEditColumn.text?.clear()
                Toast.makeText(requireContext(), "Image fetched!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.keyword != null) {
            binding.searchEditColumn.setText(viewModel.keyword)
        }
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
                text = "搜尋圖片發生異常，請稍後再試"
            }

            abnormalBtn.apply {
                visibility = if (loading) View.GONE else View.VISIBLE
                text = "確定"
                setOnClickListener {
                    binding.abnormalLayout.visibility = View.GONE
                }
            }
        }

        abnormalExecuting = false
    }
}