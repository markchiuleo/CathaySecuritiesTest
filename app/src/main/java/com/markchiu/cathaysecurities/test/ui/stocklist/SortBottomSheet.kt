package com.markchiu.cathaysecurities.test.ui.stocklist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.markchiu.cathaysecurities.test.databinding.BottomSheetSortBinding
import com.markchiu.cathaysecurities.test.domain.model.SortType

class SortBottomSheet : BottomSheetDialogFragment() {

    interface Listener {
        fun onSortSelected(sortType: SortType)
    }

    private var _binding: BottomSheetSortBinding? = null
    private val binding get() = _binding!!

    private var listener: Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? Listener
            ?: parentFragment as? Listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetSortBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentSort = SortType.entries.getOrElse(
            arguments?.getInt(ARG_SORT, SortType.CODE_DESC.ordinal) ?: SortType.CODE_DESC.ordinal,
        ) { SortType.CODE_DESC }

        when (currentSort) {
            SortType.CODE_DESC -> binding.rbCodeDesc.isChecked = true
            SortType.CODE_ASC -> binding.rbCodeAsc.isChecked = true
        }

        // 用 click 而非 checkedChange：即使點選目前已選項目也會關閉並回傳
        binding.rbCodeDesc.setOnClickListener {
            listener?.onSortSelected(SortType.CODE_DESC)
            dismiss()
        }
        binding.rbCodeAsc.setOnClickListener {
            listener?.onSortSelected(SortType.CODE_ASC)
            dismiss()
        }
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SortBottomSheet"
        private const val ARG_SORT = "arg_sort"

        fun newInstance(current: SortType): SortBottomSheet {
            return SortBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SORT, current.ordinal)
                }
            }
        }
    }
}
