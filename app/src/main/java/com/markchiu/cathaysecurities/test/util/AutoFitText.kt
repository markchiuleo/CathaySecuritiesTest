package com.markchiu.cathaysecurities.test.util

import android.util.TypedValue
import android.widget.TextView
import androidx.core.widget.TextViewCompat

// 確保數值單行顯示且依寬度自動縮小，避免被裁切。
// XML autosize 在 RecyclerView 回收／textAppearance 覆蓋下常失效，故於綁定時強制套用。
object AutoFitText {

    fun applyMetric(view: TextView) {
        apply(view, minSp = 8, maxSp = 15)
    }

    fun applyFooter(view: TextView) {
        apply(view, minSp = 7, maxSp = 13)
    }

    private fun apply(view: TextView, minSp: Int, maxSp: Int) {
        // 注意：不要設 isSingleLine=true，會讓 autosize 失效
        view.setSingleLine(false)
        view.maxLines = 1
        view.ellipsize = null
        view.includeFontPadding = false
        TextViewCompat.setAutoSizeTextTypeWithDefaults(
            view,
            TextViewCompat.AUTO_SIZE_TEXT_TYPE_NONE,
        )
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            view,
            minSp,
            maxSp,
            1,
            TypedValue.COMPLEX_UNIT_SP,
        )
        view.requestLayout()
    }
}
