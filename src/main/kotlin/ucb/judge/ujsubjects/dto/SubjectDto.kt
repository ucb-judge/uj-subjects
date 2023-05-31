package ucb.judge.ujsubjects.dto

import java.util.*

data class SubjectDto (
    val id: Long? = null,
    var professor: ProfessorDto? = null,
    val name: String = "",
    val code: String = "",
    val campusMajor: CampusMajorDto,
    val dateFrom: Date? = null,
    val dateTo: Date? = null
)