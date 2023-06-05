package ucb.judge.ujsubjects.dao.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ucb.judge.ujsubjects.dao.Professor
import ucb.judge.ujsubjects.dao.Subject

@Repository
interface SubjectRepository : JpaRepository<Subject, Long> {
    fun findBySubjectIdAndStatusIsTrue(subjectId: Long): Subject?
    fun findBySubjectIdAndProfessorAndStatusIsTrue(subjectId: Long, professor: Professor): Subject?
}