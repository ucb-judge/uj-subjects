package ucb.judge.ujsubjects.dao.repository

import org.springframework.data.jpa.repository.JpaRepository
import ucb.judge.ujsubjects.dao.CampusMajor

interface CampusMajorRepository : JpaRepository<CampusMajor, Long> {
    fun findByCampusMajorIdAndStatusIsTrue (campusMajorId: Long): CampusMajor?
}