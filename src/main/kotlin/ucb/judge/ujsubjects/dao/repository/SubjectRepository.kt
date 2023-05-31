package ucb.judge.ujsubjects.dao.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ucb.judge.ujsubjects.dao.Subject

@Repository
interface SubjectRepository : JpaRepository<Subject, Long> {
    fun findBySubjectId(subjectId: Long): Subject?
}