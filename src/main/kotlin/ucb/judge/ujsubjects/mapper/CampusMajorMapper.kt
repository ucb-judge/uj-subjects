package ucb.judge.ujsubjects.mapper

import ucb.judge.ujsubjects.dao.CampusMajor
import ucb.judge.ujsubjects.dto.CampusMajorDto

class CampusMajorMapper {
    companion object {
        fun entityToDto(campusMajor: CampusMajor): CampusMajorDto {
            return CampusMajorDto(
                campus = CampusMapper.entityToDto(campusMajor.campus!!).name,
                major = MajorMapper.entityToDto(campusMajor.major!!).name
            )
        }
    }
}