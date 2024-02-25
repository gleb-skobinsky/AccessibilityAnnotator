import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.discourse.annotator.common.uuid
import org.discourse.annotator.domain.AnnotationProject
import org.discourse.annotator.domain.DiscourseEntity
import org.discourse.annotator.domain.Paragraph
import org.discourse.annotator.domain.Segment
import kotlin.test.Test

class SerializerTest {
    @Test
    fun test1() {
        val coref = DiscourseEntity.Coreference(uuid())
        val asString = Json.encodeToString(DiscourseEntity.Coreference.serializer(), coref)
        println(asString)
    }

    @Test
    fun test2() {
        val json = Json {
            this.serializersModule = SerializersModule {
                this.polymorphic(DiscourseEntity::class, DiscourseEntity.Coreference::class, DiscourseEntity.Coreference.serializer())
            }
        }
        val coref = DiscourseEntity.Coreference(uuid())
        val seg = Segment("Some shit", entity = coref)
        val par = Paragraph(segments = mutableListOf(seg))
        val proj = AnnotationProject(paragraphs = listOf(par))
        val asString = json.encodeToString(AnnotationProject.serializer(), proj)
        println(asString)
    }
}