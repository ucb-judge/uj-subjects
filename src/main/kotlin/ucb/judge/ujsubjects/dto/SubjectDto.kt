package ucb.judge.ujsubjects.dto

import java.util.*

data class SubjectDto (
    val subjectId: Long? = null,
    var professor: ProfessorDto? = null,
    val name: String = "",
    val code: String = "",
    val campusMajor: CampusMajorDto? = null,
    val dateFrom: Date? = null,
    val dateTo: Date? = null
)