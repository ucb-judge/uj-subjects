package ucb.judge.ujsubjects.dao.repository

import org.springframework.data.jpa.repository.JpaRepository
import ucb.judge.ujsubjects.dao.Subject

interface SubjectRepository : JpaRepository<Subject, Long> {
    fun findBySubjectId(subjectId: Long): Subject?
}