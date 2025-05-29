package org.caojun.library.nettools.weather

/**
 * Open-Meteo 风向角度枚举类
 * 将0-360度的风向角度转换为16方位表示
 */
enum class WindDirection(
    val degrees: IntRange,
    val shortName: String,
    val fullName: String,
    val emoji: String
) {
    NORTH(348..360, "N", "北", "⬇️"),
    NORTH_NORTH_EAST(0..11, "NNE", "北东北", "↙️"),
    NORTH_EAST(12..33, "NE", "东北", "↙️"),
    EAST_NORTH_EAST(34..56, "ENE", "东北东", "⬅️"),
    EAST(57..78, "E", "东", "⬅️"),
    EAST_SOUTH_EAST(79..101, "ESE", "东南东", "⬅️"),
    SOUTH_EAST(102..123, "SE", "东南", "↖️"),
    SOUTH_SOUTH_EAST(124..146, "SSE", "南东南", "⬆️"),
    SOUTH(147..168, "S", "南", "⬆️"),
    SOUTH_SOUTH_WEST(169..191, "SSW", "南西南", "⬆️"),
    SOUTH_WEST(192..213, "SW", "西南", "↗️"),
    WEST_SOUTH_WEST(214..236, "WSW", "西南西", "➡️"),
    WEST(237..258, "W", "西", "➡️"),
    WEST_NORTH_WEST(259..281, "WNW", "西北西", "➡️"),
    NORTH_WEST(282..303, "NW", "西北", "↘️"),
    NORTH_NORTH_WEST(304..326, "NNW", "北西北", "↙️"),
    CALM(327..347, "C", "无风", "🌀"); // 专门处理Open-Meteo在风速为0时的特殊范围

    companion object {
        /**
         * 根据角度值获取风向枚举
         */
        fun fromDegrees(degrees: Int): WindDirection {
            // Open-Meteo在风速为0时可能返回特定值
            if (degrees == 0) return CALM

            val normalized = (degrees % 360).let {
                if (it < 0) it + 360 else it
            }

            return entries.firstOrNull { normalized in it.degrees } ?: NORTH
        }

        /**
         * 获取风向的16方位缩写列表
         */
        fun getDirectionAbbreviations(): List<String> {
            return entries.map { it.shortName }
        }

        /**
         * 获取角度对应的箭头符号（用于UI显示）
         */
        fun getArrowSymbol(degrees: Int): String {
            return fromDegrees(degrees).emoji
        }
    }
}