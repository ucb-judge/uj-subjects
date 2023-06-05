package ucb.judge.ujsubjects.mapper

import ucb.judge.ujsubjects.dao.Subject
import ucb.judge.ujsubjects.dto.SubjectDto

class SubjectMapper {
    companion object {
        fun entityToDto(subject: Subject): SubjectDto {
            return SubjectDto(
                subjectId = subject.subjectId,
                name = subject.name,
                code = subject.code,
                campusMajor = CampusMajorMapper.entityToDto(subject.campusMajor!!),
                dateFrom = subject.dateFrom,
                dateTo = subject.dateTo
            )
        }
    }
}