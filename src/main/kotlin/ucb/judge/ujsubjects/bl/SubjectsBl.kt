package ucb.judge.ujsubjects.bl

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ucb.judge.ujsubjects.dao.Subject
import ucb.judge.ujsubjects.dao.repository.CampusMajorRepository
import ucb.judge.ujsubjects.dao.repository.ProfessorRepository
import ucb.judge.ujsubjects.dao.repository.SubjectRepository
import ucb.judge.ujsubjects.dto.KeycloakUserDto
import ucb.judge.ujsubjects.dto.NewSubjectDto
import ucb.judge.ujsubjects.dto.ProfessorDto
import ucb.judge.ujsubjects.dto.SubjectDto
import ucb.judge.ujsubjects.exception.SubjectsException
import ucb.judge.ujsubjects.mapper.SubjectMapper
import ucb.judge.ujsubjects.service.UjUsersService
import ucb.judge.ujsubjects.util.KeycloakSecurityContextHolder

@Service
class SubjectsBl @Autowired constructor(
    private val subjectRepository: SubjectRepository,
    private val professorRepository: ProfessorRepository,
    private val campusMajorRepository: CampusMajorRepository,
    private val ujUsersService: UjUsersService,
    private val keycloakBl: KeycloakBl,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(SubjectsBl::class.java.name)
    }

    fun findAllSubjects(): List<SubjectDto> {
        logger.info("Starting the call to find all subjects")
        val token = "Bearer ${keycloakBl.getToken()}"
        val subjects = subjectRepository.findAll()
        val subjectsDto: List<SubjectDto> = subjects.map { subject ->
            val keycloakUserDto: KeycloakUserDto = ujUsersService.getProfile(subject.professor!!.kcUuid, token).data!!
            val subjectDto: SubjectDto = SubjectMapper.entityToDto(subject)
            subjectDto.professor = ProfessorDto(keycloakUserDto.firstName, keycloakUserDto.lastName)
            subjectDto
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
        subjectDto.professor = ProfessorDto(keycloakUserDto.firstName, keycloakUserDto.lastName)
        logger.info("Finishing the call to find subject by id")
        return subjectDto
    }

    fun updateSubject(subjectId: Long, newSubjectDto: NewSubjectDto): SubjectDto {
        logger.info("Starting the call to update subject by id")
        val token = "Bearer ${keycloakBl.getToken()}"
        val kcUuid = KeycloakSecurityContextHolder.getSubject() ?: throw SubjectsException(HttpStatus.UNAUTHORIZED, "Unauthorized")
        val professor = professorRepository.findByKcUuid(kcUuid) ?: throw SubjectsException(HttpStatus.NOT_FOUND, "Professor not found")
        logger.info("Subject updated by professor: ${professor.kcUuid}")
        val subject = subjectRepository.findBySubjectId(subjectId) ?: throw SubjectsException(HttpStatus.NOT_FOUND, "Subject not found")

        val campusMajor = if (newSubjectDto.campusMajorId != null) {
            campusMajorRepository.findByCampusMajorId(newSubjectDto.campusMajorId) ?: throw SubjectsException(HttpStatus.NOT_FOUND, "Campus major not found")
        } else {
            null
        }
        subject.name = newSubjectDto.name ?: subject.name
        subject.code = newSubjectDto.code ?: subject.code
        subject.campusMajor = campusMajor ?: subject.campusMajor
        subject.dateFrom = newSubjectDto.dateFrom ?: subject.dateFrom
        subject.dateTo = newSubjectDto.dateTo ?: subject.dateTo
        subjectRepository.save(subject)
        val subjectDto: SubjectDto = SubjectMapper.entityToDto(subject)
        val keycloakUserDto: KeycloakUserDto = ujUsersService.getProfile(subject.professor!!.kcUuid, token).data!!
        subjectDto.professor = ProfessorDto(keycloakUserDto.firstName, keycloakUserDto.lastName)
        logger.info("Finishing the call to update subject by id")
        return subjectDto
    }

    fun createSubject(newSubjectDto: NewSubjectDto): Long {
        logger.info("Starting the call to create subject")
        val token = "Bearer ${keycloakBl.getToken()}"
        val kcUuid = KeycloakSecurityContextHolder.getSubject() ?: throw SubjectsException(HttpStatus.UNAUTHORIZED, "Unauthorized")
        val professor = professorRepository.findByKcUuid(kcUuid) ?: throw SubjectsException(HttpStatus.NOT_FOUND, "Professor not found")
        logger.info("Subject created by professor: ${professor.kcUuid}")
        val campusMajorId = newSubjectDto.campusMajorId ?: throw SubjectsException(HttpStatus.BAD_REQUEST, "Campus major id is required")
        val campusMajor = campusMajorRepository.findByCampusMajorId(campusMajorId) ?: throw SubjectsException(HttpStatus.NOT_FOUND, "Campus major not found")
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
}