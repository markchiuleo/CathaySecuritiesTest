package com.markchiu.cathaysecurities.test

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.markchiu.cathaysecurities.test.data.remote.NetworkModule
import com.markchiu.cathaysecurities.test.data.repository.StockRepository
import com.markchiu.cathaysecurities.test.databinding.ActivityMainBinding
import com.markchiu.cathaysecurities.test.domain.model.SortType
import com.markchiu.cathaysecurities.test.domain.model.StockInfo
import com.markchiu.cathaysecurities.test.ui.stocklist.SortBottomSheet
import com.markchiu.cathaysecurities.test.ui.stocklist.StockListAdapter
import com.markchiu.cathaysecurities.test.ui.stocklist.StockListUiState
import com.markchiu.cathaysecurities.test.ui.stocklist.StockListViewModel
import com.markchiu.cathaysecurities.test.util.DisplayFormatter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), SortBottomSheet.Listener {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: StockListViewModel by viewModels {
        StockListViewModel.Factory(StockRepository(NetworkModule.twseApiService))
    }

    private val adapter = StockListAdapter { stock -> showDetailDialog(stock) }

    private var currentSortType: SortType = SortType.CODE_DESC
    private var lastApiWarning: String? = null
    private var scrollToTopAfterSubmit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupToolbar()
        setupList()
        setupActions()
        observeUiState()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // toolbar 加 statusBar padding，避免標題被狀態列擋到
            binding.toolbar.updatePadding(top = systemBars.top)
            binding.swipeRefresh.updatePadding(bottom = systemBars.bottom)
            insets
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        binding.toolbar.title = getString(R.string.stock_info_list)
    }

    private fun setupList() {
        binding.rvStocks.layoutManager = LinearLayoutManager(this)
        binding.rvStocks.adapter = adapter
        binding.rvStocks.itemAnimator = null
        binding.rvStocks.isVerticalScrollBarEnabled = true
        binding.rvStocks.isScrollbarFadingEnabled = false
        binding.rvStocks.scrollBarStyle = android.view.View.SCROLLBARS_OUTSIDE_OVERLAY
    }

    private fun setupActions() {
        binding.swipeRefresh.setOnRefreshListener { viewModel.loadStocks() }
        binding.btnErrorRetry.setOnClickListener { viewModel.loadStocks() }
        binding.btnEmptyRetry.setOnClickListener { viewModel.loadStocks() }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: StockListUiState) {
        when (state) {
            is StockListUiState.Loading -> {
                binding.swipeRefresh.isRefreshing = false
                binding.layoutLoading.isVisible = true
                binding.layoutError.isVisible = false
                binding.layoutEmpty.isVisible = false
                binding.rvStocks.isVisible = false
                lastApiWarning = null
                updateApiWarningBanner()
            }

            is StockListUiState.Success -> {
                currentSortType = state.sortType
                binding.swipeRefresh.isRefreshing = state.isRefreshing
                binding.layoutLoading.isVisible = false
                binding.layoutError.isVisible = false
                binding.layoutEmpty.isVisible = false
                binding.rvStocks.isVisible = true
                adapter.submitList(state.stocks) {
                    if (scrollToTopAfterSubmit) {
                        scrollToTopAfterSubmit = false
                        binding.rvStocks.scrollToPosition(0)
                    }
                }
                lastApiWarning = state.warningMessage
                updateApiWarningBanner()
            }

            is StockListUiState.Empty -> {
                currentSortType = state.sortType
                binding.swipeRefresh.isRefreshing = state.isRefreshing
                binding.layoutLoading.isVisible = false
                binding.layoutError.isVisible = false
                binding.layoutEmpty.isVisible = true
                binding.rvStocks.isVisible = false
                adapter.submitList(emptyList())
                lastApiWarning = state.warningMessage
                updateApiWarningBanner()
            }

            is StockListUiState.Error -> {
                currentSortType = state.sortType
                binding.swipeRefresh.isRefreshing = false
                binding.layoutLoading.isVisible = false
                binding.layoutError.isVisible = true
                binding.layoutEmpty.isVisible = false
                binding.rvStocks.isVisible = false
                binding.tvError.text = state.message
                lastApiWarning = null
                updateApiWarningBanner()
                adapter.submitList(emptyList())
            }
        }
    }

    private fun updateApiWarningBanner() {
        val message = lastApiWarning
        if (message.isNullOrBlank()) {
            binding.tvWarning.isVisible = false
        } else {
            binding.tvWarning.text = message
            binding.tvWarning.isVisible = true
        }
    }

    private fun showDetailDialog(stock: StockInfo) {
        val pe = DisplayFormatter.formatNumber(stock.peRatio)
        val dividend = DisplayFormatter.formatNumber(stock.dividendYield)
        val pb = DisplayFormatter.formatNumber(stock.pbRatio)
        val message = getString(R.string.detail_pe, pe) + "\n" +
            getString(R.string.detail_dividend, dividend) + "\n" +
            getString(R.string.detail_pb, pb)

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.detail_title, stock.code, stock.name))
            .setMessage(message)
            .setPositiveButton(R.string.confirm, null)
            .show()
    }

    private fun showSortBottomSheet() {
        val existing = supportFragmentManager.findFragmentByTag(SortBottomSheet.TAG)
        if (existing != null) return
        SortBottomSheet.newInstance(currentSortType)
            .show(supportFragmentManager, SortBottomSheet.TAG)
    }

    override fun onSortSelected(sortType: SortType) {
        scrollToTopAfterSubmit = true
        viewModel.setSortType(sortType)
        // DiffUtil 回呼前先捲回頂部，避免停在舊位置
        binding.rvStocks.post { binding.rvStocks.scrollToPosition(0) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_stock_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> {
                showSortBottomSheet()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
