package com.micoredu.reader.bean.rule

import java.io.Serializable

data class ExploreKind(
    val title: String,
    val url: String? = null,
    val style: Style? = null
):Serializable {

    companion object {
        val defaultStyle = Style()
    }

    fun style(): Style {
        return style ?: defaultStyle
    }

    data class Style(
        val layout_flexGrow: Float = 0F,
        val layout_flexShrink: Float = 1F,
        val layout_alignSelf: String = "auto",
        val layout_flexBasisPercent: Float = -1F,
        val layout_wrapBefore: Boolean = false,
    ) :Serializable{

        fun alignSelf(): Int {
            return when (layout_alignSelf) {
                "auto" -> -1
                "flex_start" -> 0
                "flex_end" -> 1
                "center" -> 2
                "baseline" -> 3
                "stretch" -> 4
                else -> -1
            }
        }

    }

}