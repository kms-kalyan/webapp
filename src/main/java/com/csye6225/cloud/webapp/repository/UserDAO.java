// package com.csye6225.cloud.webapp.repository;

// import org.hibernate.HibernateException;
// import org.hibernate.Session;
// import org.hibernate.Transaction;
// import org.hibernate.query.Query;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Repository;

// import com.csye6225.cloud.webapp.configuration.SessionConfig;
// import com.csye6225.cloud.webapp.model.User;

// @Repository
// public class UserDAO extends SessionConfig{

//     @Autowired
//     SessionConfig sessionConfig;

//     public User findByEmail(String email) {
        
//         Session session = sessionConfig.getSessionFactory().openSession();
//         Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
//         query.setParameter("email", email);
//         return query.uniqueResult();
//     }

    
//     public void save(User user) {

//         Session session = null;
//         Transaction transaction = null;
//          try {
//             session = sessionConfig.getSessionFactory().openSession();
//             transaction = session.beginTransaction();

//             session.persist(user);
//             transaction.commit();
            
//         } catch (HibernateException e) {
//             if (transaction != null) {
//                 transaction.rollback();
//             }
//             e.printStackTrace();
            
//         } finally {
//             if (session != null) {
//                 session.close(); 
//             }
//         }    
//     }

//     public User findById(String id) {
//         Session session = sessionConfig.getSessionFactory().openSession();
//         return session.get(User.class, id);
//     }

//     public User merge(User user) {
//         Session session = sessionConfig.getSessionFactory().openSession();
//         return (User) session.merge(user);
//     }
    
// }
