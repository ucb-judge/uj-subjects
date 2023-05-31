package ucb.judge.ujsubjects.dao.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ucb.judge.ujsubjects.dao.Professor

@Repository
interface ProfessorRepository: JpaRepository<Professor, Long> {

    fun findByKcUuid(kcUUid: String): Professor?
}