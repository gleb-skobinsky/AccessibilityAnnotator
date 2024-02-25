package org.discourse.annotator.common.json

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.discourse.annotator.domain.DiscourseEntity

val baseJson = Json {
    ignoreUnknownKeys = true
    serializersModule = SerializersModule {
        polymorphic(DiscourseEntity::class, DiscourseEntity.Coreference::class, DiscourseEntity.Coreference.serializer())
        polymorphic(DiscourseEntity::class, DiscourseEntity.Bridging::class, DiscourseEntity.Bridging.serializer())
    }
}