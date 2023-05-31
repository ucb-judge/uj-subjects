package ucb.judge.ujsubjects.bl

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ucb.judge.ujsubjects.dao.repository.SubjectRepository
import ucb.judge.ujsubjects.dto.KeycloakUserDto
import ucb.judge.ujsubjects.dto.ProfessorDto
import ucb.judge.ujsubjects.dto.SubjectDto
import ucb.judge.ujsubjects.mapper.SubjectMapper
import ucb.judge.ujsubjects.service.UjUsersService

@Service
class SubjectsBl @Autowired constructor(
    private val subjectRepository: SubjectRepository,
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
}