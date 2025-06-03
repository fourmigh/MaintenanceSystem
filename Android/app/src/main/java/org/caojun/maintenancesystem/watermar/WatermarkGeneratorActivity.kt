package org.caojun.maintenancesystem.watermar

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import org.caojun.library.nettools.ip.IpLocationResponse
import org.caojun.library.nettools.weather.CurrentWeather
import org.caojun.library.nettools.weather.CurrentWeatherUnits
import org.caojun.library.nettools.weather.WeatherCode
import org.caojun.library.nettools.weather.WindDirection
import org.caojun.maintenancesystem.R

class WatermarkGeneratorActivity : ComponentActivity() {

    private lateinit var checkboxContainer: LinearLayout
    private lateinit var generateButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var customTextInput: EditText

    // 存储用户输入的值
    private val watermarkTemplateValues = LinkedHashMap<WatermarkTemplate, String>()
    private val watermarkTemplateEditText = LinkedHashMap<WatermarkTemplate, EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watermark_generator)

        // 初始化视图
        checkboxContainer = findViewById(R.id.checkboxContainer)
        generateButton = findViewById(R.id.generateButton)
        resultTextView = findViewById(R.id.resultTextView)
        customTextInput = findViewById(R.id.customTextInput)

        // 设置水印选项
        setupWatermarkOptions()

        // 设置生成按钮点击事件
        generateButton.setOnClickListener {
            generateWatermark()
        }

        WatermarkHelper.get(this, this, object : WatermarkHelper.GetListener {
            @SuppressLint("SetTextI18n")
            override fun onResult(
                location: IpLocationResponse?,
                weather: CurrentWeather?,
                units: CurrentWeatherUnits?,
                weatherCode: WeatherCode?,
                wind: WindDirection?
            ) {
                var editText = watermarkTemplateEditText[WatermarkTemplate.PLACEHOLDER_LONGITUDE]
                editText?.setText(WatermarkHelper.translate(WatermarkTemplate.PLACEHOLDER_LONGITUDE, location, weather, units, weatherCode, wind))

                editText = watermarkTemplateEditText[WatermarkTemplate.PLACEHOLDER_LATITUDE]
                editText?.setText(WatermarkHelper.translate(WatermarkTemplate.PLACEHOLDER_LATITUDE, location, weather, units, weatherCode, wind))

                editText = watermarkTemplateEditText[WatermarkTemplate.PLACEHOLDER_ADDRESS]
                editText?.setText(WatermarkHelper.translate(WatermarkTemplate.PLACEHOLDER_ADDRESS, location, weather, units, weatherCode, wind))

                editText = watermarkTemplateEditText[WatermarkTemplate.PLACEHOLDER_WEATHER]
                editText?.setText(WatermarkHelper.translate(WatermarkTemplate.PLACEHOLDER_WEATHER, location, weather, units, weatherCode, wind))
            }

        })
    }



    private fun setupWatermarkOptions() {
        // 获取所有占位符
        val watermarkTemplates = WatermarkTemplate.getAll()

        // 为每个占位符创建CheckBox和输入框
        watermarkTemplates.forEach { (watermarkTemplate, description) ->
            // 创建CheckBox
            val checkBox = CheckBox(this).apply {
                text = description
                tag = watermarkTemplate
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        // 如果是日期时间，自动填充当前时间
                        if (watermarkTemplate == WatermarkTemplate.PLACEHOLDER_DATE_TIME) {
                            watermarkTemplateValues[watermarkTemplate] = WatermarkTemplate.getCurrentDateTime()
                        } else {
                            watermarkTemplateValues[watermarkTemplate] = ""
                        }
                    } else {
                        watermarkTemplateValues.remove(watermarkTemplate)
                    }
                }
            }

            // 创建输入框（日期时间不需要）
            if (watermarkTemplate != WatermarkTemplate.PLACEHOLDER_DATE_TIME) {
                val editText = EditText(this).apply {
                    hint = "请输入$description"
                    tag = "${watermarkTemplate}_input"
                }
                watermarkTemplateEditText[watermarkTemplate] = editText

                // 添加到布局
                checkboxContainer.addView(checkBox)
                checkboxContainer.addView(editText)
            } else {
                checkboxContainer.addView(checkBox)
            }
        }
    }

    private fun generateWatermark() {

        val watermarkTemplates = WatermarkTemplate.getAll()
        watermarkTemplates.forEach { (watermarkTemplate, _) ->
            if (watermarkTemplateValues.containsKey(watermarkTemplate) && watermarkTemplateEditText.containsKey(watermarkTemplate)) {
                val editText = watermarkTemplateEditText[watermarkTemplate]
                watermarkTemplateValues[watermarkTemplate] = editText?.text.toString()
            }
        }

        // 获取自定义文本
        val customText = customTextInput.text.toString()

        // 构建水印文本
        val watermarkBuilder = StringBuilder()

        val sortedMap = LinkedHashMap<WatermarkTemplate, String>().apply {
            WatermarkTemplate.entries.forEach { template ->
                watermarkTemplateValues[template]?.let { value ->
                    put(template, value)
                }
            }
        }
        val list = ArrayList<WatermarkTemplate>()
        // 添加用户选择的水印元素
        sortedMap.forEach { (watermarkTemplate, value) ->
            list.add(watermarkTemplate)
            watermarkBuilder.append("${watermarkTemplate.description}: $value\n")
        }
        WatermarkHelper.save(this, this, list)

        // 添加自定义文本
        if (customText.isNotEmpty()) {
            watermarkBuilder.append("\n$customText")
        }

        // 显示结果
        resultTextView.text = watermarkBuilder.toString()
    }
}