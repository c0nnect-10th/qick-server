package connect.qick.domain.teacher.repository;

import connect.qick.domain.teacher.domain.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
}
