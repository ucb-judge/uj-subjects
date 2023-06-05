package ucb.judge.ujsubjects.dto

import java.sql.Timestamp

data class ContestDto(
    var contestId: Long,
    var name: String,
    var description: String,
    var startDate: Timestamp,
    var endDate: Timestamp,
    var professor: ProfessorDto?,
    var subject: SubjectDto?,
    var isPublic: Boolean,
)