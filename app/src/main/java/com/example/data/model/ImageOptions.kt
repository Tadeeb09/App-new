package com.example.data.model

data class ImageOptions(
    val aspectRatio: String = "1:1",
    val quality: String = "High",
    val count: Int = 1
) {
    companion object {
        val ASPECT_RATIOS = listOf("1:1", "16:9", "9:16", "4:3")
        val QUALITIES = listOf("Standard", "High")
    }
}
