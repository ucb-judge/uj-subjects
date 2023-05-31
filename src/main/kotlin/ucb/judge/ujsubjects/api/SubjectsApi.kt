package ucb.judge.ujsubjects.api

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ucb.judge.ujsubjects.bl.SubjectsBl
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
}