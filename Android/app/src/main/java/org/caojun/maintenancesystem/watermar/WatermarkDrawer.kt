package org.caojun.maintenancesystem.watermar

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

class WatermarkDrawer {
    private val paint = Paint()
    private var padding = 20 // 水印与边缘的间距
    private var lineSpacing = 10 // 行间距

    init {
        paint.isAntiAlias = true
        paint.textSize = 96f // 默认文字大小
        paint.color = -0x55000001 // 默认半透明白色
    }

    /**
     * 设置水印文字颜色
     * @param color 颜色值 (建议使用带透明度的颜色)
     */
    fun setTextColor(color: Int) {
        paint.color = color
    }

    /**
     * 设置水印文字大小
     * @param size 文字大小(px)
     */
    fun setTextSize(size: Float) {
        paint.textSize = size
    }

    /**
     * 设置水印与边缘的间距
     * @param padding 间距(px)
     */
    fun setPadding(padding: Int) {
        this.padding = padding
    }

    /**
     * 设置行间距
     * @param lineSpacing 行间距(px)
     */
    fun setLineSpacing(lineSpacing: Int) {
        this.lineSpacing = lineSpacing
    }

    /**
     * 在Canvas上绘制水印
     * @param canvas 画布
     * @param watermarkText 水印文本(多行文本，用\n分隔)
     * @param position 水印位置 (TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT)
     */
    fun drawWatermark(canvas: Canvas?, watermarkText: String?, position: WatermarkPosition?) {
        if (canvas == null || watermarkText.isNullOrEmpty()) {
            return
        }

        // 分割多行文本
        val lines = watermarkText.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()

        // 计算每行文本的高度
        val textBounds = Rect()
        paint.getTextBounds("A", 0, 1, textBounds)
        val lineHeight = (textBounds.height() + lineSpacing).toFloat()

        // 计算水印总高度
        val totalHeight = lines.size * lineHeight - lineSpacing

        // 根据位置计算绘制起始点
        val x: Float
        var y: Float

        when (position) {
            WatermarkPosition.TOP_RIGHT -> {
                x = (canvas.width - padding).toFloat()
                y = (padding + textBounds.height()).toFloat()
                paint.textAlign = Paint.Align.RIGHT
            }

            WatermarkPosition.BOTTOM_LEFT -> {
                x = padding.toFloat()
                y = canvas.height - padding - totalHeight + textBounds.height()
                paint.textAlign = Paint.Align.LEFT
            }

            WatermarkPosition.BOTTOM_RIGHT -> {
                x = (canvas.width - padding).toFloat()
                y = canvas.height - padding - totalHeight + textBounds.height()
                paint.textAlign = Paint.Align.RIGHT
            }

            WatermarkPosition.TOP_LEFT -> {
                x = padding.toFloat()
                y = (padding + textBounds.height()).toFloat()
                paint.textAlign = Paint.Align.LEFT
            }

            else -> {
                x = padding.toFloat()
                y = (padding + textBounds.height()).toFloat()
                paint.textAlign = Paint.Align.LEFT
            }
        }

        // 逐行绘制文本
        for (line in lines) {
            canvas.drawText(line, x, y, paint)
            y += lineHeight
        }
    }

    enum class WatermarkPosition {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }
}