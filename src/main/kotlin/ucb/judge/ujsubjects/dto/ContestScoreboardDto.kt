package ucb.judge.ujsubjects.dto

data class ContestScoreboardDto(
    var student: UserDetailsDto,
    var problemsSolved: Int,
)