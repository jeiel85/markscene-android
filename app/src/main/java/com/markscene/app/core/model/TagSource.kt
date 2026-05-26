package com.markscene.app.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class TagSource {
    Mock,
    LocalImageLabel,
    LocalObjectDetection,
    User,
    AdvancedAi,
    LocalVlm
}
