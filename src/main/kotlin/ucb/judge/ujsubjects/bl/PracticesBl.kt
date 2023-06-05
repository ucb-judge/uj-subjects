package ucb.judge.ujsubjects.bl

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
import ucb.judge.ujsubjects.exception.SubjectsException
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
    fun createPractice(subjectId: Long, practiceDto: ContestDto): Long {
        practiceDto.isPublic = false
        if (practiceDto.startDate.after(practiceDto.endDate)) {
            throw SubjectsException(HttpStatus.BAD_REQUEST, "Start date must be before end date")
        }
        if (practiceDto.startDate.before(java.util.Date())) {
            throw SubjectsException(HttpStatus.BAD_REQUEST, "Start date must be after today")
        }
        val professor = checkProfessor()
        if (subjectRepository.findBySubjectIdAndProfessorProfessorIdAndStatusIsTrue(subjectId, professor.professorId) == null) {
            throw SubjectsException(HttpStatus.FORBIDDEN, "You are not the owner of this subject")
        }
        val token = "Bearer ${keycloakBl.getToken()}"
        val contestId = ujContestsService.createContest(practiceDto, token).data ?: throw SubjectsException(
            HttpStatus.BAD_REQUEST,
            "Contest not created"
        )
        // Add all students to the contest
        val students = studentSubjectRepository.findAllBySubjectSubjectIdAndStatusIsTrue(subjectId)
        students.forEach {
            ujContestsService.registerToContest(it.student!!.kcUuid, contestId, token)
        }
        return contestId
    }

    fun addProblemToPractice(practiceId: Long, problemId: Long): Long {
        val token = "Bearer ${keycloakBl.getToken()}"
        val professor = checkProfessor()
        subjectRepository.findBySubjectIdAndProfessorProfessorIdAndStatusIsTrue(
            ujContestsService.getContestById(practiceId, token).data!!.subject!!.subjectId!!,
            professor.professorId
        ) ?: throw SubjectsException(HttpStatus.FORBIDDEN, "You are not the owner of this subject")
        if (ujContestsService.getContestById(practiceId, token).data!!.isPublic) {
            throw SubjectsException(HttpStatus.FORBIDDEN, "This is not a practice")
        }
        return ujContestsService.addProblemToContest(practiceId, problemId, token).data ?: throw SubjectsException(
            HttpStatus.BAD_REQUEST,
            "Problem not added to contest"
        )
    }

    fun getProblemsFromPractice(practiceId: Long): List<ProblemDto> {
        val token = "Bearer ${keycloakBl.getToken()}"
        val professor = checkProfessor()
        val student = checkStudent()
        if (subjectRepository.findBySubjectIdAndProfessorProfessorIdAndStatusIsTrue(
                ujContestsService.getContestById(practiceId, token).data!!.subject!!.subjectId!!,
                professor.professorId
            ) == null && studentSubjectRepository.findBySubjectSubjectIdAndStudentStudentIdAndStatusIsTrue(
                ujContestsService.getContestById(practiceId, token).data!!.subject!!.subjectId!!,
                student.studentId
            ) == null
        ) {
            throw SubjectsException(HttpStatus.FORBIDDEN, "You are not allowed to see this subject")
        }
        if (ujContestsService.getContestById(practiceId, token).data!!.isPublic) {
            throw SubjectsException(HttpStatus.FORBIDDEN, "This is not a practice")
        }
        return ujContestsService.getProblemsByContestId(practiceId, token).data ?: throw SubjectsException(
            HttpStatus.BAD_REQUEST,
            "Problems not found"
        )
    }

    fun getPracticesFromSubject(subjectId: Long): List<ContestDto> {
        val token = "Bearer ${keycloakBl.getToken()}"
        val professor = checkProfessor()
        val student = checkStudent()
        if (subjectRepository.findBySubjectIdAndProfessorProfessorIdAndStatusIsTrue(subjectId, professor.professorId) == null && studentSubjectRepository.findBySubjectSubjectIdAndStudentStudentIdAndStatusIsTrue(subjectId, student.studentId) == null) {
            throw SubjectsException(HttpStatus.FORBIDDEN, "You are not allowed to see this subject")
        }
        val practices = ujContestsService.getContests(token).data ?: throw SubjectsException(
            HttpStatus.BAD_REQUEST,
            "Practices not found"
        )
        return practices.filter { it.subject!!.subjectId == subjectId }
    }

    fun getScoreFromPractice(practiceId: Long): List<ContestScoreboardDto> {
        val token = "Bearer ${keycloakBl.getToken()}"
        val professor = checkProfessor()
        val student = checkStudent()
        if (subjectRepository.findBySubjectIdAndProfessorProfessorIdAndStatusIsTrue(
                ujContestsService.getContestById(practiceId, token).data!!.subject!!.subjectId!!,
                professor.professorId
            ) == null && studentSubjectRepository.findBySubjectSubjectIdAndStudentStudentIdAndStatusIsTrue(
                ujContestsService.getContestById(practiceId, token).data!!.subject!!.subjectId!!,
                student.studentId
            ) == null
        ) {
            throw SubjectsException(HttpStatus.FORBIDDEN, "You are not allowed to see this subject")
        }
        if (ujContestsService.getContestById(practiceId, token).data!!.isPublic) {
            throw SubjectsException(HttpStatus.FORBIDDEN, "This is not a practice")
        }
        return ujContestsService.getScoreboardByContestId(practiceId, token).data ?: throw SubjectsException(
            HttpStatus.BAD_REQUEST,
            "Scoreboard not found"
        )
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
    fun checkStudent(kcUuid: String = ""): Student {
        val token = "Bearer ${keycloakBl.getToken()}"
        val pKcUuid = kcUuid.ifEmpty {
            KeycloakSecurityContextHolder.getSubject() ?: throw SubjectsException(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized"
            )
        }
        val studentId = ujUsersService.getStudentByKcUuid(pKcUuid, token).data ?: throw SubjectsException(
            HttpStatus.NOT_FOUND,
            "Student not found in Keycloak"
        )
        return studentRepository.findByStudentIdAndStatusIsTrue(studentId) ?: throw SubjectsException(
            HttpStatus.NOT_FOUND,
            "Student not found"
        )
    }
}