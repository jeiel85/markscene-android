package com.markscene.app.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class AnalysisStatus {
    None,
    LocalComplete,
    AdvancedComplete,
    Failed
}
