package ucb.judge.ujsubjects.dao

import javax.persistence.*

@Entity
@Table(name = "student_subject")
class StudentSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_subject_id")
    var studentSubjectId: Long = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    var student: Student? = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    var subject: Subject? = null;

    @Column(name = "status")
    var status: Boolean = true;
}