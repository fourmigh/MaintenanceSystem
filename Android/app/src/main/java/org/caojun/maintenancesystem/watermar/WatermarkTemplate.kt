package org.caojun.maintenancesystem.watermar

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 水印模板与占位符枚举类
 */
enum class WatermarkTemplate {
    // 占位符定义
    PLACEHOLDER_DATE_TIME {
        override val placeholder: String = "yyyy-MM-dd HH:mm"
        override val description: String = "时间"
    },
    PLACEHOLDER_LONGITUDE {
        override val placeholder: String = "#longitude#"
        override val description: String = "经度"
    },
    PLACEHOLDER_LATITUDE {
        override val placeholder: String = "#latitude#"
        override val description: String = "纬度"
    },
    PLACEHOLDER_ADDRESS {
        override val placeholder: String = "#address#"
        override val description: String = "地址"
    },
    PLACEHOLDER_WEATHER {
        override val placeholder: String = "#weather#"
        override val description: String = "天气"
    };

    // 抽象属性
    abstract val description: String
    abstract val placeholder: String

    companion object {
        /**
         * 获取所有占位符
         */
        fun getAll(): Map<WatermarkTemplate, String> {
            return entries.associateWith { it.description }
        }

        /**
         * 生成默认格式的当前日期时间
         */
        fun getCurrentDateTime(): String {
            return SimpleDateFormat(PLACEHOLDER_DATE_TIME.placeholder, Locale.getDefault())
                .format(Date())
        }
    }
}