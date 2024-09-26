package com.csye6225.cloud.webapp.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.csye6225.cloud.webapp.model.Student;
import com.csye6225.cloud.webapp.repository.StudentDAO;

@Service
@Transactional
public class StudentService {

    @Autowired
    private StudentDAO StudentDAO;

    public void saveStudent(Student student) {
        StudentDAO.save(student);
    }

    public Student getStudentById(int id) {
        return StudentDAO.getById(id);
    }

    public List<Student> getAllStudents() {
        return StudentDAO.getAll();
    }
}
