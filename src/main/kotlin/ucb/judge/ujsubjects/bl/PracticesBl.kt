package ucb.judge.ujsubjects.bl

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ucb.judge.ujsubjects.dao.Professor
import ucb.judge.ujsubjects.dao.Student
import ucb.judge.ujsubjects.dao.repository.ProfessorRepository
import ucb.judge.ujsubjects.dao.repository.StudentRepository
import ucb.judge.ujsubjects.dao.repository.StudentSubjectRepository
import ucb.judge.ujsubjects.dao.repository.SubjectRepository
import ucb.judge.ujsubjects.dto.ContestDto
import ucb.judge.ujsubjects.dto.ContestScoreboardDto
import ucb.judge.ujsubjects.dto.ProblemDto
import ucb.judge.ujsubjects.dto.ProfessorDto
import ucb.judge.ujsubjects.exception.SubjectsException
import ucb.judge.ujsubjects.mapper.SubjectMapper
import ucb.judge.ujsubjects.service.UjContestsService
import ucb.judge.ujsubjects.service.UjUsersService
import ucb.judge.ujsubjects.util.KeycloakSecurityContextHolder

@Service
class PracticesBl @Autowired constructor(
    private val keycloakBl: KeycloakBl,
    private val ujContestsService: UjContestsService,
    private val ujUsersService: UjUsersService,
    private val subjectRepository: SubjectRepository,
    private val studentSubjectRepository: StudentSubjectRepository,
    private val professorRepository: ProfessorRepository,
    private val studentRepository: StudentRepository
) {

    companion object {
        private val logger = LoggerFactory.getLogger(PracticesBl::class.java)
    }
    fun createPractice(subjectId: Long, practiceDto: ContestDto): Long {
        logger.info("Create practice Business Logic initiated")
        val professor = checkProfessor()
        practiceDto.isPublic = false
        val professorDto = ProfessorDto(professor.kcUuid,null,null)
        practiceDto.professor = professorDto
        val subject = subjectRepository.findBySubjectIdAndStatusIsTrue(subjectId) ?: throw SubjectsException(
            HttpStatus.NOT_FOUND,
            "Subject not found"
        )
        practiceDto.subject = SubjectMapper.entityToDto(subject)
        if (subjectRepository.findBySubjectIdAndProfessorAndStatusIsTrue(subjectId, professor) == null) {
            throw SubjectsException(HttpStatus.FORBIDDEN, "You are not the owner of this subject")
        }
        val token = "Bearer ${keycloakBl.getToken()}"
        val contestId = ujContestsService.createContest(practiceDto, token).data ?: throw SubjectsException(
            HttpStatus.BAD_REQUEST,
            "Contest not created"
        )

        val students = studentSubjectRepository.findAllBySubjectAndStatusIsTrue(subject)
        students.forEach {
            ujContestsService.registerToContest(it.student!!.kcUuid, contestId, token)
        }
        logger.info("Create practice Business Logic finished")
        return contestId
    }

    fun addProblemToPractice(practiceId: Long, problemId: Long): Long {
        logger.info("Add problem to practice Business Logic initiated")
        val token = "Bearer ${keycloakBl.getToken()}"
        val contest = ujContestsService.getContestById(practiceId, token).data ?: throw SubjectsException(
            HttpStatus.NOT_FOUND,
            "Practice not found"
        )
        if (contest.isPublic) {
            throw SubjectsException(HttpStatus.FORBIDDEN, "This is not a practice")
        }

        val professor = checkProfessor()
        subjectRepository.findBySubjectIdAndProfessorAndStatusIsTrue(
            ujContestsService.getContestById(practiceId, token).data!!.subject!!.subjectId!!,
            professor
        ) ?: throw SubjectsException(HttpStatus.FORBIDDEN, "You are not the owner of this subject")
        logger.info("Add problem to practice Business Logic finished")
        return ujContestsService.addProblemToContest(practiceId, problemId, token).data ?: throw SubjectsException(
            HttpStatus.BAD_REQUEST,
            "Problem not added to contest"
        )
    }

    fun getProblemsFromPractice(practiceId: Long): List<ProblemDto> {
        logger.info("Get problems from practice Business Logic initiated")
        val token = "Bearer ${keycloakBl.getToken()}"
        checkPracticeAccess(practiceId, token)
        if (ujContestsService.getContestById(practiceId, token).data!!.isPublic) {
            throw SubjectsException(HttpStatus.FORBIDDEN, "This is not a practice")
        }
        logger.info("Get problems from practice Business Logic finished")
        return ujContestsService.getProblemsByContestId(practiceId, token).data ?: throw SubjectsException(
            HttpStatus.BAD_REQUEST,
            "Problems not found"
        )
    }

    fun getPracticesFromSubject(subjectId: Long): List<ContestDto> {
        logger.info("Get practices from subject Business Logic initiated")
        val token = "Bearer ${keycloakBl.getToken()}"
        val professor = professorRepository.findByKcUuidAndStatusIsTrue(KeycloakSecurityContextHolder.getSubject()!!)
        val student = studentRepository.findByKcUuidAndStatusIsTrue(KeycloakSecurityContextHolder.getSubject()!!)
        val subject = subjectRepository.findBySubjectIdAndStatusIsTrue(subjectId) ?: throw SubjectsException(
            HttpStatus.NOT_FOUND,
            "Subject not found"
        )
        if (professor != null) {
            if (subjectRepository.findBySubjectIdAndProfessorAndStatusIsTrue(subjectId, professor) == null) {
                throw SubjectsException(HttpStatus.FORBIDDEN, "You are not the owner of this subject")
            }
        } else if (student != null) {
            if (studentSubjectRepository.findBySubjectAndStudentAndStatusIsTrue(subject, student) == null) {
                throw SubjectsException(HttpStatus.FORBIDDEN, "You are not allowed to see this subject")
            }
        } else {
            throw SubjectsException(HttpStatus.FORBIDDEN, "You are not allowed to see this subject")
        }
        val practices = ujContestsService.getContests(token).data ?: throw SubjectsException(
            HttpStatus.BAD_REQUEST,
            "Practices not found"
        )
        logger.info("Get practices from subject Business Logic finished")
        return practices.filter {
            if (it.subject != null) {
                it.subject!!.subjectId == subjectId && !it.isPublic
            } else {
                false
            }
        }
    }

    fun getScoreFromPractice(practiceId: Long): List<ContestScoreboardDto> {
        logger.info("Get score from practice Business Logic initiated")
        val token = "Bearer ${keycloakBl.getToken()}"
        checkPracticeAccess(practiceId, token)
        if (ujContestsService.getContestById(practiceId, token).data!!.isPublic) {
            throw SubjectsException(HttpStatus.FORBIDDEN, "This is not a practice")
        }
        logger.info("Get score from practice Business Logic finished")
        return ujContestsService.getScoreboardByContestId(practiceId, token).data ?: throw SubjectsException(
            HttpStatus.BAD_REQUEST,
            "Scoreboard not found"
        )
    }

    fun checkProfessor(kcUuid:String = ""): Professor {
        logger.info("Check professor Business Logic initiated")
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
        logger.info("Check professor Business Logic finished")
        return professorRepository.findByProfessorIdAndStatusIsTrue(professorId) ?: throw SubjectsException(
            HttpStatus.NOT_FOUND,
            "Professor not found"
        )
    }

    fun checkPracticeAccess(practiceId: Long, token: String) {
        val professor = professorRepository.findByKcUuidAndStatusIsTrue(KeycloakSecurityContextHolder.getSubject()!!)
        val student = studentRepository.findByKcUuidAndStatusIsTrue(KeycloakSecurityContextHolder.getSubject()!!)
        if (professor == null && student == null) {
            logger.warn("User is not a professor nor a student")
            throw SubjectsException(HttpStatus.FORBIDDEN, "You are not allowed to see this subject")
        }
        val contest = ujContestsService.getContestById(practiceId, token).data ?: throw SubjectsException(
            HttpStatus.NOT_FOUND,
            "Practice not found"
        )
        if (contest.isPublic) {
            throw SubjectsException(HttpStatus.FORBIDDEN, "This is not a practice")
        }
        val subject = subjectRepository.findBySubjectIdAndStatusIsTrue(contest.subject!!.subjectId!!)
        if ((professor != null) && (subjectRepository.findBySubjectIdAndProfessorAndStatusIsTrue(subject!!.subjectId, professor) != null)
        ) {
            return
        } else if (student != null && studentSubjectRepository.findBySubjectAndStudentAndStatusIsTrue(subject!!, student) != null
        ) {
            return
        } else {
            throw SubjectsException(HttpStatus.FORBIDDEN, "You are not allowed to see this subject")
        }
    }
}