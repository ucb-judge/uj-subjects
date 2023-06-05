package ucb.judge.ujsubjects.api

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import ucb.judge.ujsubjects.bl.PracticesBl
import ucb.judge.ujsubjects.dto.*

@Service
@RestController
@RequestMapping("/api/v1/practices")
class PracticesApi @Autowired constructor(private val practicesBl: PracticesBl) {
    companion object {
        private val logger = LoggerFactory.getLogger(PracticesApi::class.java)
    }

    /** Create a practice for a subject
     * @param subjectId
     * @param practiceDto
     * @return ResponseEntity<ResponseDto<Long>>
     */
    @PostMapping("/subjects/{subjectId}")
    fun createPractice(
        @PathVariable subjectId: Long,
        @RequestBody practiceDto: ContestDto
    ): ResponseEntity<ResponseDto<Long>> {
        logger.info("Starting the API call to create a practice for a subject")
        val newPracticeId: Long = practicesBl.createPractice(subjectId, practiceDto)
        logger.info("Finishing the API call to find all subjects")
        return ResponseEntity.ok(ResponseDto(newPracticeId, "Practice created successfully", true))
    }

    /** Add a problem to a practice
     * @param practiceId
     * @param problemId
     * @return ResponseEntity<ResponseDto<Long>>
     */
    @PostMapping("/{practiceId}/problems/{problemId}")
    fun addProblemToPractice(
        @PathVariable practiceId: Long,
        @PathVariable problemId: Long
    ): ResponseEntity<ResponseDto<Long>> {
        logger.info("Starting the API call to add a problem to a practice")
        val newPracticeId: Long = practicesBl.addProblemToPractice(practiceId, problemId)
        logger.info("Finishing the API call to add a problem to a practice")
        return ResponseEntity.ok(ResponseDto(newPracticeId, "Problem added successfully", true))
    }

    /** Get all problems from a practice
     * @param practiceId
     * @return ResponseEntity<ResponseDto<List<SubjectDto>>>
     */
    @GetMapping("/{practiceId}/problems")
    fun getProblemsFromPractice(
        @PathVariable practiceId: Long
    ): ResponseEntity<ResponseDto<List<ProblemDto>>> {
        logger.info("Starting the API call to get all problems from a practice")
        val result: List<ProblemDto> = practicesBl.getProblemsFromPractice(practiceId)
        logger.info("Finishing the API call to get all problems from a practice")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }

    /** Get all practices from a subject
     * @param subjectId
     * @return ResponseEntity<ResponseDto<List<SubjectDto>>>
     */
    @GetMapping("/subjects/{subjectId}")
    fun getPracticesFromSubject(
        @PathVariable subjectId: Long
    ): ResponseEntity<ResponseDto<List<ContestDto>>> {
        logger.info("Starting the API call to get all practices from a subject")
        val result: List<ContestDto> = practicesBl.getPracticesFromSubject(subjectId)
        logger.info("Finishing the API call to get all practices from a subject")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }

    /** Get score from a practice
     * @param practiceId
     * @return ResponseEntity<ResponseDto<List<SubjectDto>>>
     */
    @GetMapping("/{practiceId}/score")
    fun getScoreFromPractice(
        @PathVariable practiceId: Long
    ): ResponseEntity<ResponseDto<List<ContestScoreboardDto>>> {
        logger.info("Starting the API call to get score from a practice")
        val result: List<ContestScoreboardDto> = practicesBl.getScoreFromPractice(practiceId)
        logger.info("Finishing the API call to get score from a practice")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }

}