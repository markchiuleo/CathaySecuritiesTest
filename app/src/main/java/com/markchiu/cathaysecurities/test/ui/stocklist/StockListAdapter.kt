package com.markchiu.cathaysecurities.test.ui.stocklist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.markchiu.cathaysecurities.test.R
import com.markchiu.cathaysecurities.test.databinding.ItemStockCardBinding
import com.markchiu.cathaysecurities.test.domain.model.PriceColor
import com.markchiu.cathaysecurities.test.domain.model.StockInfo
import com.markchiu.cathaysecurities.test.util.AutoFitText
import com.markchiu.cathaysecurities.test.util.DisplayFormatter

class StockListAdapter(
    private val onItemClick: (StockInfo) -> Unit,
) : ListAdapter<StockInfo, StockListAdapter.StockViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val binding = ItemStockCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return StockViewHolder(binding, onItemClick).also { it.setupAutoFit() }
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    override fun onViewAttachedToWindow(holder: StockViewHolder) {
        super.onViewAttachedToWindow(holder)
        val position = holder.bindingAdapterPosition
        if (position != RecyclerView.NO_POSITION) {
            holder.applyAlternatingCardStyle(position)
        }
    }

    class StockViewHolder(
        private val binding: ItemStockCardBinding,
        private val onItemClick: (StockInfo) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun setupAutoFit() {
            AutoFitText.applyMetric(binding.tvOpeningPrice)
            AutoFitText.applyMetric(binding.tvClosingPrice)
            AutoFitText.applyMetric(binding.tvHighestPrice)
            AutoFitText.applyMetric(binding.tvLowestPrice)
            AutoFitText.applyMetric(binding.tvChange)
            AutoFitText.applyMetric(binding.tvMonthlyAvg)
            AutoFitText.applyFooter(binding.tvTransaction)
            AutoFitText.applyFooter(binding.tvVolume)
            AutoFitText.applyFooter(binding.tvValue)
        }

        fun bind(item: StockInfo, position: Int) {
            binding.tvStockCode.text = item.code
            binding.tvStockName.text = item.name.ifBlank { DisplayFormatter.placeholder() }

            setMetricText(binding.tvOpeningPrice, DisplayFormatter.formatNumber(item.openingPrice))
            setMetricText(binding.tvClosingPrice, DisplayFormatter.formatNumber(item.closingPrice))
            setMetricText(binding.tvHighestPrice, DisplayFormatter.formatNumber(item.highestPrice))
            setMetricText(binding.tvLowestPrice, DisplayFormatter.formatNumber(item.lowestPrice))
            setMetricText(binding.tvChange, DisplayFormatter.formatNumber(item.change))
            setMetricText(binding.tvMonthlyAvg, DisplayFormatter.formatNumber(item.monthlyAveragePrice))
            setFooterText(binding.tvTransaction, DisplayFormatter.formatLong(item.transaction))
            setFooterText(binding.tvVolume, DisplayFormatter.formatLong(item.tradeVolume))
            setFooterText(binding.tvValue, DisplayFormatter.formatLong(item.tradeValue))

            applyPriceColor(binding.tvClosingPrice, item.closingPriceColor)
            applyPriceColor(binding.tvChange, item.changeColor)
            applyAlternatingCardStyle(position)

            binding.root.setOnClickListener { onItemClick(item) }
        }

        private fun setMetricText(view: TextView, text: String) {
            view.text = text
            AutoFitText.applyMetric(view)
        }

        private fun setFooterText(view: TextView, text: String) {
            view.text = text
            AutoFitText.applyFooter(view)
        }

        fun applyAlternatingCardStyle(position: Int) {
            val context = binding.root.context
            val even = position % 2 == 0
            val surface = ContextCompat.getColor(
                context,
                if (even) R.color.card_surface_a else R.color.card_surface_b,
            )
            val footer = ContextCompat.getColor(
                context,
                if (even) R.color.card_footer_a else R.color.card_footer_b,
            )
            (binding.root as MaterialCardView).setCardBackgroundColor(surface)
            binding.layoutFooter.setBackgroundColor(footer)
        }

        private fun applyPriceColor(view: TextView, color: PriceColor) {
            val colorRes = when (color) {
                PriceColor.UP -> R.color.price_up
                PriceColor.DOWN -> R.color.price_down
                PriceColor.NEUTRAL -> R.color.price_neutral
            }
            view.setTextColor(ContextCompat.getColor(view.context, colorRes))
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<StockInfo>() {
        override fun areItemsTheSame(oldItem: StockInfo, newItem: StockInfo): Boolean =
            oldItem.code == newItem.code

        override fun areContentsTheSame(oldItem: StockInfo, newItem: StockInfo): Boolean =
            oldItem == newItem
    }
}
