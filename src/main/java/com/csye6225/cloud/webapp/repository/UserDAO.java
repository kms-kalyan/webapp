package com.csye6225.cloud.webapp.repository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.csye6225.cloud.webapp.configuration.SessionConfig;
import com.csye6225.cloud.webapp.model.User;

@SuppressWarnings("deprecation")
@Repository
public class UserDAO{

    
    Session session = null;

    public User findByEmail(String email) {
        session = SessionConfig.getSessionFactory().openSession();
        Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
        query.setParameter("email", email);
        return query.uniqueResult();
    }

    
    public void save(User user) {

        Transaction transaction = null;
         try {
            session = SessionConfig.getSessionFactory().openSession();
            //logger.debug("Session opened for createlogin");
            transaction = session.beginTransaction();

            User newUser = new User();
            newUser.setFirstName(user.getFirstName());
            newUser.setPassword(user.getPassword()); 
            newUser.setEmail(user.getEmail());
            newUser.setLastName(user.getLastName());
            
            
            session.persist(newUser);
            transaction.commit();
            
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            
        } finally {
            if (session != null) {
                session.close(); 
                //logger.debug("Session closed for createlogin");
            }
        }
        // session = SessionConfig.getSessionFactory().openSession();
        // session.saveOrUpdate(user);
    }

    public User findById(String id) {
        session = SessionConfig.getSessionFactory().openSession();
        return session.get(User.class, id);
    }

    public ResponseEntity<Void> createConnection(){

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(() -> {
            try (Session ses = SessionConfig.getSessionFactory().openSession()) {
                ses.createNativeQuery("SELECT 1").getSingleResult();
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        try {
            boolean result = future.get(3, TimeUnit.SECONDS);
            if (result) {
                return ResponseEntity.ok()
                        .header("Cache-Control", "no-cache")
                        .build();
            } else {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .header("Cache-Control", "no-cache")
                        .build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .header("Cache-Control", "no-cache")
                    .build();
        } finally {
            executor.shutdownNow();
        }
    }
}
