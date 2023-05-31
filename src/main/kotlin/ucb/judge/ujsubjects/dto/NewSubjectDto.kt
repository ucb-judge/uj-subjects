package ucb.judge.ujsubjects.dto

import java.sql.Date


data class NewSubjectDto (
    val name: String? = null,
    val code: String? = null,
    val campusMajorId: Long? = null,
    val dateFrom: Date? = null,
    val dateTo: Date? = null
    )