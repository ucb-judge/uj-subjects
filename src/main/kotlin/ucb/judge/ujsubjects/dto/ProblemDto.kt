package ucb.judge.ujsubjects.dto

data class ProblemDto(
    val problemId: Long = 0,
    val title: String = "",
    val description: String = "",
    val sampleInputs: List<String> = listOf(),
    val sampleOutputs: List<String> = listOf(),
    val timeLimit: Double = 0.0,
    val memoryLimit: Int = 0,
    val tags: List<TagDto> = listOf(),
    val admittedLanguages: List<LanguageDto> = listOf()
)
