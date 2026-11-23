package com.markchiu.cathaysecurities.test.util

// 解析證交所字串數值：處理千分位逗號、正負號、空白與無效值。
// 無效值回傳 null，不以 0 代替。
object NumberParser {

    fun parseDouble(raw: String?): Double? {
        val normalized = normalize(raw) ?: return null
        return normalized.toDoubleOrNull()
    }

    fun parseLong(raw: String?): Long? {
        val normalized = normalize(raw) ?: return null
        // 部分欄位可能帶小數，先以 Double 再轉 Long
        val asDouble = normalized.toDoubleOrNull() ?: return null
        return asDouble.toLong()
    }

    private fun normalize(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        var value = raw.trim()
            .replace(",", "")
            .replace("，", "")
            .replace(" ", "")

        if (value.isEmpty() ||
            value == "-" ||
            value == "--" ||
            value.equals("null", ignoreCase = true) ||
            value.equals("N/A", ignoreCase = true) ||
            value.equals("na", ignoreCase = true)
        ) {
            return null
        }

        // 去除前導 '+'；保留 '-'
        if (value.startsWith("+")) {
            value = value.substring(1)
        }

        // 不比價等特殊符號（例如 X0.00）
        if (value.startsWith("X") || value.startsWith("x")) {
            return null
        }

        return value.ifBlank { null }
    }
}
