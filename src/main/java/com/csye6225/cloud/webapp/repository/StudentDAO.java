package com.csye6225.cloud.webapp.repository;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.csye6225.cloud.webapp.model.Student;

@Repository
public class StudentDAO{

    @Autowired
    private SessionFactory sessionFactory;

    public void save(Student student) {
        Session session = sessionFactory.getCurrentSession();
        session.save(student);
    }

    public Student getById(int id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Student.class, id);
    }

    public List<Student> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Student", Student.class).list();
    }
}
