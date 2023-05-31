package ucb.judge.ujsubjects.mapper

import ucb.judge.ujsubjects.dao.Major
import ucb.judge.ujsubjects.dto.MajorDto

class MajorMapper {
    companion object {
        fun entityToDto(major: Major): MajorDto {
            return MajorDto(
                majorId = major.majorId,
                name = major.name
            )
        }
    }
}
