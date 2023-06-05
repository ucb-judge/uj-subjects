package ucb.judge.ujsubjects.dao.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ucb.judge.ujsubjects.dao.Student
import ucb.judge.ujsubjects.dao.StudentSubject
import ucb.judge.ujsubjects.dao.Subject

@Repository
interface StudentSubjectRepository : JpaRepository<StudentSubject, Long> {
    fun findAllBySubjectAndStatusIsTrue(subject:Subject): List<StudentSubject>
    fun findByStudentAndSubjectAndStatusIsTrue(student:Student, subject:Subject): StudentSubject?
    fun findBySubjectAndStudentAndStatusIsTrue (subject: Subject, student:Student): StudentSubject?

}
