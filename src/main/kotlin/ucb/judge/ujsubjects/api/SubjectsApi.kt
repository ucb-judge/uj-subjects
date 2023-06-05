package ucb.judge.ujsubjects.api

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import ucb.judge.ujsubjects.bl.SubjectsBl
import ucb.judge.ujsubjects.dto.*

@Service
@RestController
@RequestMapping("/api/v1/subjects")
class SubjectsApi @Autowired constructor(private val subjectsBl: SubjectsBl) {

    companion object {
        private val logger = LoggerFactory.getLogger(SubjectsApi::class.java.name)
    }

    /** Get all subjects
     * @return ResponseEntity<ResponseDto<List<SubjectDto>>>
     */
    @GetMapping()
    fun findAll(): ResponseEntity<ResponseDto<List<SubjectDto>>> {
        logger.info("Starting the API call to find all subjects")
        val result: List<SubjectDto> = subjectsBl.findAllSubjects()
        logger.info("Finishing the API call to find all subjects")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }

    /** Get subject by id
     * @param subjectId
     * @return ResponseEntity<ResponseDto<SubjectDto>>
     */
    @GetMapping("/{subjectId}")
    fun findBySubjectId(@PathVariable subjectId: Long): ResponseEntity<ResponseDto<SubjectDto>> {
        logger.info("Starting the API call to find subject by id")
        val result: SubjectDto = subjectsBl.findBySubjectId(subjectId)
        logger.info("Finishing the API call to find subject by id")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }

    /** Update subject by id
     * @param subjectId
     * @param newSubjectDto
     * @return ResponseEntity<ResponseDto<SubjectDto>>
     */
    @PutMapping("/{subjectId}")
    fun updateSubject(
        @PathVariable subjectId: Long,
        @RequestBody newSubjectDto: NewSubjectDto
    ): ResponseEntity<ResponseDto<SubjectDto>> {
        logger.info("Starting the API call to update subject by id")
        val result: SubjectDto = subjectsBl.updateSubject(subjectId, newSubjectDto)
        logger.info("Finishing the API call to update subject by id")
        return ResponseEntity.ok(ResponseDto(result, "Subject updated successfully", true))
    }

    /** Create subject
     * @param newSubjectDto
     * @return ResponseEntity<ResponseDto<Long>>
     */
    @PostMapping()
    fun createSubject(
        @RequestBody newSubjectDto: NewSubjectDto
    ): ResponseEntity<ResponseDto<Long>> {
        logger.info("Starting the API call to create subject")
        val newSubjectId: Long = subjectsBl.createSubject(newSubjectDto)
        logger.info("Finishing the API call to create subject")
        return ResponseEntity.ok(ResponseDto(newSubjectId, "Subject created successfully", true))
    }

    /** Delete subject by id
     * @param subjectId
     * @return ResponseEntity<ResponseDto<SubjectDto>>
     */
    @DeleteMapping("/{subjectId}")
    fun deleteSubject(@PathVariable subjectId: Long): ResponseEntity<ResponseDto<SubjectDto>> {
        logger.info("Starting the API call to delete subject by id")
        subjectsBl.deleteSubject(subjectId)
        logger.info("Finishing the API call to delete subject by id")
        return ResponseEntity.ok(ResponseDto(null, "Subject deleted successfully", true))
    }


    @GetMapping("/{subjectId}/student")
    fun findAllStudentsBySubjectId(@PathVariable subjectId: Long): ResponseEntity<ResponseDto<List<StudentDto>>> {
        logger.info("Starting the API call to find all students by subject id")
        val result: List<StudentDto> = subjectsBl.findAllStudentsBySubjectId(subjectId)
        logger.info("Finishing the API call to find all students by subject id")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }

    @PostMapping("/{subjectId}/student")
    fun addStudentToSubject(
        @PathVariable subjectId: Long,
        @RequestBody studentDto: StudentDto
    ): ResponseEntity<ResponseDto<Long>> {
        logger.info("Starting the API call to add user to subject")
        val newStudentSubjectId: Long = subjectsBl.addStudentToSubject(subjectId, studentDto.kcUuid)
        logger.info("Finishing the API call to add user to subject")
        return ResponseEntity.ok(ResponseDto(newStudentSubjectId, "User added to subject successfully", true))
    }

    @DeleteMapping("/{subjectId}/student")
    fun deleteStudentFromSubject(
        @PathVariable subjectId: Long,
        @RequestBody studentDto: StudentDto
    ): ResponseEntity<ResponseDto<Long>> {
        logger.info("Starting the API call to delete student from subject")
        subjectsBl.deleteStudentFromSubject(subjectId, studentDto.kcUuid)
        logger.info("Finishing the API call to delete student from subject")
        return ResponseEntity.ok(ResponseDto(null, "Student deleted from subject successfully", true))
    }

    @PutMapping("/{subjectId}/professor")
    fun updateSubjectProfessor(
        @PathVariable subjectId: Long,
        @RequestBody professorDto: ProfessorDto
    ): ResponseEntity<ResponseDto<SubjectDto>> {
        logger.info("Starting the API call to update subject professor")
        val subjectDto: SubjectDto = subjectsBl.updateSubjectProfessor(subjectId, professorDto.kcUuid)
        logger.info("Finishing the API call to update subject professor")
        return ResponseEntity.ok(ResponseDto(subjectDto, "Subject professor updated successfully", true))
    }
}