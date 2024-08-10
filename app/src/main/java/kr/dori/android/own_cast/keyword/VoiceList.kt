data class VoiceList(
    val language: String,
    val accent: String,
    val styleBusiness: Style,
    val styleCasual: Style
)

data class Style(
    val dataType: List<String>,
    val gender: List<String>
)

class VoiceListRepository {
    companion object {
        val voiceLists: List<List<VoiceList>> by lazy {
            val voiceListEnglishUS = VoiceList(
                language = "영어",
                accent = "미국",
                styleBusiness = Style(
                    dataType = listOf("en-US-Neural2-C", "en-US-News-L", "en-US-Neural2-E", "en-US-Neural2-D", "en-US-News-N", "en-US-Polyglot-1"),
                    gender = listOf("비즈니스여성1", "여성2", "여성3", "남성1", "남성2", "남성3")
                ),
                styleCasual = Style(
                    dataType = listOf("en-US-Neural2-G", "en-US-Neural2-H", "en-US-Standard-F", "en-US-Standard-I", "en-US-Wavenet-B", "en-US-Wavenet-J"),
                    gender = listOf("캐주얼여성1", "여성2", "여성3", "남성1", "남성2", "남성3")
                )
            )

            val voiceListEnglishGB = VoiceList(
                language = "영어",
                accent = "영국",
                styleBusiness = Style(
                    dataType = listOf("en-GB-News-H", "en-GB-News-I", "en-GB-Wavenet-A", "en-GB-Neural2-D", "en-GB-News-J", "en-GB-News-K"),
                    gender = listOf("여성1", "여성2", "여성3", "남성1", "남성2", "남성3")
                ),
                styleCasual = Style(
                    dataType = listOf("en-GB-Neural2-A", "en-GB-Neural2-F", "en-GB-Standard-C", "en-GB-Neural2-B", "en-GB-Neural2-D", "en-GB-News-K"),
                    gender = listOf("여성1", "여성2", "여성3", "남성1", "남성2", "남성3")
                )
            )

            val voiceListEnglishIN = VoiceList(
                language = "영어",
                accent = "인도",
                styleBusiness = Style(
                    dataType = listOf("en-IN-Neural2-D", "en-IN-Wavenet-A", "en-IN-Wavenet-D", "en-IN-Neural2-B", "en-IN-Wavenet-C", "en-IN-Wavenet-B"),
                    gender = listOf("여성1", "여성2", "여성3", "남성1", "남성2", "남성3")
                ),
                styleCasual = Style(
                    dataType = listOf("en-IN-Neural2-A", "en-IN-Standard-D", "en-IN-Standard-A", "en-IN-Neural2-C", "en-IN-Standard-B", "en-IN-Standard-C"),
                    gender = listOf("여성1", "여성2", "여성3", "남성1", "남성2", "남성3")
                )
            )

            val voiceListEnglishAU = VoiceList(
                language = "영어",
                accent = "호주",
                styleBusiness = Style(
                    dataType = listOf("en-AU-News-E", "en-AU-News-F", "en-AU-Wavenet-C", "en-AU-Neural2-B", "en-AU-News-G", "en-AU-Polyglot-1"),
                    gender = listOf("여성1", "여성2", "여성3", "남성1", "남성2", "남성3")
                ),
                styleCasual = Style(
                    dataType = listOf("en-AU-Standard-C", "en-AU-News-F", "en-AU-Wavenet-A", "en-AU-Polyglot-1", "en-AU-Standard-D", "en-AU-Wavenet-B"),
                    gender = listOf("여성1", "여성2", "여성3", "남성1", "남성2", "남성3")
                )
            )

            val voiceListJapanese = VoiceList(
                language = "일본어",
                accent = "일본",
                styleBusiness = Style(
                    dataType = listOf("ja-JP-Neural2-B", "ja-JP-Wavenet-A", "ja-JP-Wavenet-B", "ja-JP-Neural2-D", "ja-JP-Wavenet-C", "ja-JP-Standard-D"),
                    gender = listOf("여성1", "여성2", "여성3", "남성1", "남성2", "남성3")
                ),
                styleCasual = Style(
                    dataType = listOf("ja-JP-Wavenet-A", "ja-JP-Neural2-B", "ja-JP-Standard-A", "ja-JP-Neural2-C", "ja-JP-Wavenet-D", "ja-JP-Standard-C"),
                    gender = listOf("여성1", "여성2", "여성3", "남성1", "남성2", "남성3")
                )
            )

            val voiceListSpanishES = VoiceList(
                language = "스페인어",
                accent = "스페인",
                styleBusiness = Style(
                    dataType = listOf("es-ES-Neural2-C", "es-ES-Neural2-D", "es-ES-Neural2-E", "es-ES-Neural2-B", "es-ES-Neural2-F", "es-ES-Polyglot-1"),
                    gender = listOf("비즈니스여성1", "여성2", "여성3", "남성1", "남성2", "남성3")
                ),
                styleCasual = Style(
                    dataType = listOf("es-ES-Neural2-A", "es-ES-Neural2-E", "es-ES-Standard-C", "es-ES-Neural2-B", "es-ES-Neural2-F", "es-ES-Polyglot-1"),
                    gender = listOf("캐주얼여성1", "여성2", "여성3", "남성1", "남성2", "남성3")
                )
            )

            val voiceListSpanishUS = VoiceList(
                language = "스페인어",
                accent = "미국",
                styleBusiness = Style(
                    dataType = listOf("es-US-Neural2-A", "es-US-News-F", "es-US-News-G", "es-US-News-D", "es-US-News-E", "es-US-Polyglot-1"),
                    gender = listOf("비즈니스여성1", "여성2", "여성3", "남성1", "남성2", "남성3")
                ),
                styleCasual = Style(
                    dataType = listOf("es-US-Wavenet-A", "es-US-News-F", "es-US-News-G", "es-US-Neural2-B", "es-US-News-E", "es-US-Wavenet-C"),
                    gender = listOf("캐주얼여성1", "여성2", "여성3", "남성1", "남성2", "남성3")
                )
            )

            listOf(//영-미, 영-영, 영-인, 영-호, 일-일, 스-스, 스-영
                listOf(voiceListEnglishUS, voiceListEnglishGB, voiceListEnglishIN, voiceListEnglishAU),//영어
                listOf(voiceListJapanese), //일본어
                listOf(voiceListSpanishES, voiceListSpanishUS)//스페인어
            )
        }
    }
}
