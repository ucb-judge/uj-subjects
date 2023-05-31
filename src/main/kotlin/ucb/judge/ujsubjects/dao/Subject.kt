package ucb.judge.ujsubjects.dao

import java.sql.Date
import javax.persistence.*

@Entity
@Table(name = "subject")
class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    var subjectId: Long = 0;

    @Column(name = "name")
    var name: String = "";

    @Column(name = "code")
    var code: String = "";

    @Column(name = "date_from")
    var dateFrom: Date = Date(0);

    @Column(name = "date_to")
    var dateTo: Date = Date(0);

    @Column(name = "status")
    var status: Boolean = true;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "subject")
    var studentSubjects: List<StudentSubject>? = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campus_major_id")
    var campusMajor: CampusMajor? = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    var professor: Professor? = null;

}