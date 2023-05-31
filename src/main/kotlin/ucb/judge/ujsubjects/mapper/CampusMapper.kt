package ucb.judge.ujsubjects.mapper

import ucb.judge.ujsubjects.dao.Campus
import ucb.judge.ujsubjects.dto.CampusDto

class CampusMapper {
    companion object {
        fun entityToDto(campus: Campus): CampusDto {
            return CampusDto(
                campusId = campus.campusId,
                name = campus.name
            )
        }
    }
}