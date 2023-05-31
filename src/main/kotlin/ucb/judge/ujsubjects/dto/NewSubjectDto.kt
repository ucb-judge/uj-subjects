package ucb.judge.ujsubjects.dto

import java.util.*

data class NewSubjectDto (
    val name: String = "",
    val code: String = "",
    val campusMajorId: Long? = null,
    val dateFrom: Date? = null,
    val dateTo: Date? = null
    )