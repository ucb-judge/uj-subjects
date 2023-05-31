package ucb.judge.ujsubjects.dao.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ucb.judge.ujsubjects.dao.StudentSubject

@Repository
interface StudentSubjectRepository : JpaRepository<StudentSubject, Long> {
    fun findByStudentSubjectId(studentSubjectId: Long): StudentSubject?
}
