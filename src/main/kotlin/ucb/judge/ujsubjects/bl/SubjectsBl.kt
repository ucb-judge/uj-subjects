package ucb.judge.ujsubjects.bl

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ucb.judge.ujsubjects.dao.Professor
import ucb.judge.ujsubjects.dao.Student
import ucb.judge.ujsubjects.dao.StudentSubject
import ucb.judge.ujsubjects.dao.Subject
import ucb.judge.ujsubjects.dao.repository.*
import ucb.judge.ujsubjects.dto.*
import ucb.judge.ujsubjects.exception.SubjectsException
import ucb.judge.ujsubjects.mapper.SubjectMapper
import ucb.judge.ujsubjects.service.UjUsersService
import ucb.judge.ujsubjects.util.KeycloakSecurityContextHolder

@Service
class SubjectsBl @Autowired constructor(
    private val subjectRepository: SubjectRepository,
    private val professorRepository: ProfessorRepository,
    private val studentRepository: StudentRepository,
    private val studentSubjectRepository: StudentSubjectRepository,
    private val campusMajorRepository: CampusMajorRepository,
    private val ujUsersService: UjUsersService,
    private val keycloakBl: KeycloakBl,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(SubjectsBl::class.java.name)
    }

    fun findAllSubjects(): List<SubjectDto> {
        logger.info("Starting the call to find all subjects")
        val subjects = subjectRepository.findAll()
        val subjectsDto: List<SubjectDto> = subjects.map { subject ->
            subjectToSubjectDto(subject)
        }
        logger.info("Finishing the call to find all subjects")
        return subjectsDto
    }

    fun findBySubjectId(subjectId: Long) : SubjectDto {
        logger.info("Starting the call to find subject by id")
        val token = "Bearer ${keycloakBl.getToken()}"
        val subject = subjectRepository.findById(subjectId)
        if (subject.isEmpty) {
            throw SubjectsException(HttpStatus.NOT_FOUND, "Subject not found")
        }
        val keycloakUserDto: KeycloakUserDto = ujUsersService.getProfile(subject.get().professor!!.kcUuid, token).data!!
        val subjectDto: SubjectDto = SubjectMapper.entityToDto(subject.get())
        subjectDto.professor = ProfessorDto(keycloakUserDto.id, keycloakUserDto.firstName, keycloakUserDto.lastName)
        logger.info("Finishing the call to find subject by id")
        return subjectDto
    }

    fun updateSubject(subjectId: Long, newSubjectDto: NewSubjectDto): SubjectDto {
        logger.info("Starting the call to update subject by id")
        val professor = checkProfessor()
        if (subjectRepository.findBySubjectIdAndProfessorAndStatusIsTrue(subjectId, professor) == null) {
            throw SubjectsException(HttpStatus.FORBIDDEN, "You are not the owner of this subject")
        }
        logger.info("Subject updated by professor: ${professor.kcUuid}")
        val subject = subjectRepository.findBySubjectIdAndStatusIsTrue(subjectId) ?: throw SubjectsException(HttpStatus.NOT_FOUND, "Subject not found")

        val campusMajor = if (newSubjectDto.campusMajorId != null) {
            campusMajorRepository.findByCampusMajorIdAndStatusIsTrue(newSubjectDto.campusMajorId) ?: throw SubjectsException(HttpStatus.NOT_FOUND, "Campus major not found")
        } else {
            null
        }
        subject.name = newSubjectDto.name ?: subject.name
        subject.code = newSubjectDto.code ?: subject.code
        subject.campusMajor = campusMajor ?: subject.campusMajor
        subject.dateFrom = newSubjectDto.dateFrom ?: subject.dateFrom
        subject.dateTo = newSubjectDto.dateTo ?: subject.dateTo
        subjectRepository.save(subject)
        val subjectDto = subjectToSubjectDto(subject)
        logger.info("Finishing the call to update subject by id")
        return subjectDto
    }

    fun createSubject(newSubjectDto: NewSubjectDto): Long {
        logger.info("Starting the call to create subject")
        val professor: Professor = checkProfessor()
        logger.info("Subject created by professor: ${professor.kcUuid}")
        val campusMajorId = newSubjectDto.campusMajorId ?: throw SubjectsException(HttpStatus.BAD_REQUEST, "Campus major id is required")
        val campusMajor = campusMajorRepository.findByCampusMajorIdAndStatusIsTrue(campusMajorId) ?: throw SubjectsException(HttpStatus.NOT_FOUND, "Campus major not found")
        val subject = Subject ()
        subject.name = newSubjectDto.name ?: throw SubjectsException(HttpStatus.BAD_REQUEST, "Name is required")
        subject.code = newSubjectDto.code ?: throw SubjectsException(HttpStatus.BAD_REQUEST, "Code is required")
        subject.campusMajor = campusMajor
        subject.dateFrom = newSubjectDto.dateFrom ?: throw SubjectsException(HttpStatus.BAD_REQUEST, "Date from is required")
        subject.dateTo = newSubjectDto.dateTo ?: throw SubjectsException(HttpStatus.BAD_REQUEST, "Date to is required")
        subject.professor = professor
        subject.status = true
        val savedSubject = subjectRepository.save(subject)
        logger.info("Finishing the call to create subject")
        return savedSubject.subjectId
    }

    fun deleteSubject(subjectId: Long) {
        logger.info("Starting the call to delete subject by id")
        val professor = checkProfessor()
        if (subjectRepository.findBySubjectIdAndProfessorAndStatusIsTrue(subjectId, professor) == null) {
            throw SubjectsException(HttpStatus.FORBIDDEN, "You are not the owner of this subject")
        }
        logger.info("Subject deleted by professor: ${professor.kcUuid}")
        val subject = subjectRepository.findBySubjectIdAndStatusIsTrue(subjectId) ?: throw SubjectsException(HttpStatus.NOT_FOUND, "Subject not found")
        subject.status = false
        subjectRepository.save(subject)
        logger.info("Finishing the call to delete subject by id")
    }

    fun checkProfessor(kcUuid:String = ""): Professor {
        val token = "Bearer ${keycloakBl.getToken()}"
        val pKcUuid = kcUuid.ifEmpty {
            KeycloakSecurityContextHolder.getSubject() ?: throw SubjectsException(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized"
            )
        }
        val professorId = ujUsersService.getProfessorByKcUuid(pKcUuid, token).data ?: throw SubjectsException(
            HttpStatus.NOT_FOUND,
            "Professor not found in Keycloak"
        )
        return professorRepository.findByProfessorIdAndStatusIsTrue(professorId) ?: throw SubjectsException(
            HttpStatus.NOT_FOUND,
            "Professor not found"
        )
    }

    fun findAllStudentsBySubjectId (subjectId: Long): List<StudentDto> {
        logger.info("Starting the call to find all students by subject id")
        val subject = subjectRepository.findBySubjectIdAndStatusIsTrue(subjectId) ?: throw SubjectsException(HttpStatus.NOT_FOUND, "Subject not found")
        val studentSubjects = studentSubjectRepository.findAllBySubjectAndStatusIsTrue(subject)
        val studentsDto: List<StudentDto> = studentSubjects.map { studentSubject ->
            val student = studentSubject.student
            studentToStudentDto(student!!)
        }
        logger.info("Finishing the call to find all students by subject id")
        return studentsDto
    }
    fun addStudentToSubject(subjectId: Long, kcUuid: String?): Long {
        logger.info("Starting the call to add student to subject")
        if (kcUuid == null) {
            throw SubjectsException(HttpStatus.BAD_REQUEST, "KcUuid is required")
        }
        val student = checkStudent(kcUuid)
        val subject = subjectRepository.findBySubjectIdAndStatusIsTrue(subjectId) ?: throw SubjectsException(HttpStatus.NOT_FOUND, "Subject not found")
        studentSubjectRepository.findByStudentAndSubjectAndStatusIsTrue(student, subject)?.let {
            throw SubjectsException(HttpStatus.BAD_REQUEST, "Student already in subject")
        }
        val studentSubject = StudentSubject()
        studentSubject.student = student
        studentSubject.subject = subject
        studentSubject.status = true
        val savedStudentSubject = studentSubjectRepository.save(studentSubject)
        logger.info("Finishing the call to add student to subject")
        return savedStudentSubject.studentSubjectId
    }

    fun deleteStudentFromSubject(subjectId: Long, kcUuid: String?) {
        logger.info("Starting the call to delete student from subject")
        if (kcUuid == null) {
            throw SubjectsException(HttpStatus.BAD_REQUEST, "KcUuid is required")
        }
        val student = checkStudent(kcUuid)
        val subject = subjectRepository.findBySubjectIdAndStatusIsTrue(subjectId) ?: throw SubjectsException(HttpStatus.NOT_FOUND, "Subject not found")
        val studentSubject = studentSubjectRepository.findByStudentAndSubjectAndStatusIsTrue(student, subject) ?: throw SubjectsException(HttpStatus.NOT_FOUND, "Student not found in subject")
        studentSubject.status = false
        studentSubjectRepository.save(studentSubject)
        logger.info("Finishing the call to delete student from subject")
    }

    fun checkStudent(kcUuid: String): Student {
        val token = "Bearer ${keycloakBl.getToken()}"
        val studentId = ujUsersService.getStudentByKcUuid(kcUuid, token).data ?: throw SubjectsException(
            HttpStatus.NOT_FOUND,
            "Student not found in Keycloak"
        )
        return studentRepository.findByStudentIdAndStatusIsTrue(studentId) ?: throw SubjectsException(
            HttpStatus.NOT_FOUND,
            "Student not found"
        )
    }

    fun updateSubjectProfessor(subjectId: Long, kcUuid: String?): SubjectDto{
        logger.info("Starting the call to update subject professor")
        if (kcUuid == null) {
            throw SubjectsException(HttpStatus.BAD_REQUEST, "KcUuid is required")
        }
        val subject = subjectRepository.findBySubjectIdAndStatusIsTrue(subjectId) ?: throw SubjectsException(HttpStatus.NOT_FOUND, "Subject not found")
        val professor = checkProfessor(kcUuid)
        logger.info("Subject updated for professor: ${professor.kcUuid}")
        subject.professor = professor
        subjectRepository.save(subject)
        val subjectDto = subjectToSubjectDto(subject)
        logger.info("Finishing the call to update subject professor")
        return subjectDto
    }

    fun subjectToSubjectDto(subject: Subject): SubjectDto {
        val subjectDto: SubjectDto = SubjectMapper.entityToDto(subject)
        val keycloakUserDto: KeycloakUserDto = ujUsersService.getProfile(subject.professor!!.kcUuid, "Bearer ${keycloakBl.getToken()}").data!!
        subjectDto.professor = ProfessorDto(keycloakUserDto.id, keycloakUserDto.firstName, keycloakUserDto.lastName)
        return subjectDto
    }

    fun studentToStudentDto(student: Student): StudentDto {
        val studentDto = StudentDto(student.kcUuid, "", "", "")
        val keycloakUserDto: KeycloakUserDto = ujUsersService.getProfile(student.kcUuid, "Bearer ${keycloakBl.getToken()}").data!!
        studentDto.firstName = keycloakUserDto.firstName
        studentDto.lastName = keycloakUserDto.lastName
        studentDto.email = keycloakUserDto.email
        return studentDto
    }
}