package ucb.judge.ujsubjects.service

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import ucb.judge.ujsubjects.dto.*

@FeignClient(name = "uj-contests")
interface UjContestsService {

    @GetMapping("/api/v1/contests")
    fun getContests(
        @RequestHeader("Authorization") token: String
    ): ResponseDto<List<ContestDto>>

    @GetMapping("/api/v1/contests/{contestId}")
    fun getContestById(
        @PathVariable contestId: Long,
        @RequestHeader("Authorization") token: String
    ): ResponseDto<ContestDto>

    @PostMapping("/api/v1/contests")
    fun createContest(
        @RequestBody contestDto: ContestDto,
        @RequestHeader("Authorization") token: String
    ): ResponseDto<Long>

    @PostMapping("/api/v1/contests/{contestId}/problems/{problemId}")
    fun addProblemToContest(
        @PathVariable contestId: Long,
        @PathVariable problemId: Long,
        @RequestHeader("Authorization") token: String
    ): ResponseDto<Long>

    @GetMapping("/api/v1/contests/{contestId}/problems")
    fun getProblemsByContestId(
        @PathVariable contestId: Long,
        @RequestHeader("Authorization") token: String
    ): ResponseDto<List<ProblemDto>>

    @GetMapping("/api/v1/contests/{contestId}/scoreboard")
    fun getScoreboardByContestId(
        @PathVariable contestId: Long,
        @RequestHeader("Authorization") token: String
    ): ResponseDto<List<ContestScoreboardDto>>

    @PostMapping("/api/v1/contests/{contestId}/register/{kcUuid}")
    fun registerToContest(
        @PathVariable kcUuid: String,
        @PathVariable contestId: Long,
        @RequestHeader("Authorization") token: String
    ): ResponseDto<Long>
}