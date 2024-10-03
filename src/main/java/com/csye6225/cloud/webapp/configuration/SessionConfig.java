package com.csye6225.cloud.webapp.configuration;

import org.hibernate.SessionFactory;

import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.Environment;
import org.springframework.stereotype.Component;

import com.csye6225.cloud.webapp.model.User;

@Component
public class SessionConfig {
	
	private static SessionFactory sessionFactory;

	public static SessionFactory getSessionFactory() {
		if(sessionFactory  == null) {
			try {
				StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
						.applySetting(Environment.DRIVER, "org.postgresql.Driver")
						.applySetting(Environment.URL, "jdbc:postgresql://localhost:5432/")
						.applySetting(Environment.USER, "*****")
						.applySetting(Environment.PASS, "******")
						.applySetting(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect")
						.applySetting(Environment.SHOW_SQL, "true")
						.applySetting(Environment.HBM2DDL_AUTO,"create")
						.build();
				MetadataSources meta = new MetadataSources(serviceRegistry);
				meta.addAnnotatedClass(User.class);
				Metadata metadata = meta.getMetadataBuilder().build();
	            sessionFactory = metadata.getSessionFactoryBuilder().build();
			}catch (Exception e) {
	            e.printStackTrace(); }
		}
		return sessionFactory;
	}

}