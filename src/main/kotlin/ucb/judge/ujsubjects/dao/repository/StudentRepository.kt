package ucb.judge.ujsubjects.dao.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ucb.judge.ujsubjects.dao.Student

@Repository
interface StudentRepository: JpaRepository<Student, Long> {
    fun findByStudentIdAndStatusIsTrue(studentId: Long): Student?
    fun findByKcUuidAndStatusIsTrue(kcUuid: String): Student?
}