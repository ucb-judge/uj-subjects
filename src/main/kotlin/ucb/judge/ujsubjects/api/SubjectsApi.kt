package ucb.judge.ujsubjects.api

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import ucb.judge.ujsubjects.bl.SubjectsBl
import ucb.judge.ujsubjects.dto.NewSubjectDto
import ucb.judge.ujsubjects.dto.ResponseDto
import ucb.judge.ujsubjects.dto.SubjectDto

@Service
@RestController
@RequestMapping("/api/v1/subjects")
class SubjectsApi @Autowired constructor(private val subjectsBl: SubjectsBl) {

    companion object {
        private val logger = LoggerFactory.getLogger(SubjectsApi::class.java.name)
    }

    @GetMapping()
    fun findAll(): ResponseEntity<ResponseDto<List<SubjectDto>>> {
        logger.info("Starting the API call to find all subjects")
        val result: List<SubjectDto> = subjectsBl.findAllSubjects()
        logger.info("Finishing the API call to find all subjects")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }

    @GetMapping("{subjectId}")
    fun findBySubjectId(@PathVariable subjectId: Long): ResponseEntity<ResponseDto<SubjectDto>> {
        logger.info("Starting the API call to find subject by id")
        val result: SubjectDto = subjectsBl.findBySubjectId(subjectId)
        logger.info("Finishing the API call to find subject by id")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }

    @PutMapping("{subjectId}")
    fun updateSubject(
        @PathVariable subjectId: Long,
        @RequestBody newSubjectDto: NewSubjectDto
    ): ResponseEntity<ResponseDto<SubjectDto>> {
        logger.info("Starting the API call to update subject by id")
        val result: SubjectDto = subjectsBl.updateSubject(subjectId, newSubjectDto)
        logger.info("Finishing the API call to update subject by id")
        return ResponseEntity.ok(ResponseDto(result, "Subject updated successfully", true))
    }

    @PostMapping()
    fun createSubject(
        @RequestBody newSubjectDto: NewSubjectDto
    ): ResponseEntity<ResponseDto<Long>> {
        logger.info("Starting the API call to create subject")
        val newSubjectId: Long = subjectsBl.createSubject(newSubjectDto)
        logger.info("Finishing the API call to create subject")
        return ResponseEntity.ok(ResponseDto(newSubjectId, "Subject created successfully", true))
    }

}