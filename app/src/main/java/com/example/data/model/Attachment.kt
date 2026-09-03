package com.example.data.model

data class Attachment(
    val id: String = java.util.UUID.randomUUID().toString(),
    val uriString: String,
    val fileName: String,
    val mimeType: String,
    val sizeBytes: Long = 0L,
    val base64Data: String? = null,
    val textContent: String? = null,
    val isImage: Boolean = mimeType.startsWith("image/")
) {
    val formattedSize: String
        get() {
            if (sizeBytes <= 0) return ""
            return if (sizeBytes < 1024) {
                "$sizeBytes B"
            } else if (sizeBytes < 1024 * 1024) {
                "${sizeBytes / 1024} KB"
            } else {
                String.format(java.util.Locale.US, "%.1f MB", sizeBytes / (1024.0 * 1024.0))
            }
        }
}
