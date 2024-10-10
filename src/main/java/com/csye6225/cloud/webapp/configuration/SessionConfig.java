package com.csye6225.cloud.webapp.configuration;

import org.hibernate.SessionFactory;

import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.csye6225.cloud.webapp.model.User;

@Component
public class SessionConfig {
	
	private static SessionFactory sessionFactory;

	@Value("${spring.datasource.driver}")
    private static String driver;

    @Value("${spring.datasource.url}")
    private static String url;

    @Value("${spring.datasource.username}")
    private static String username;

    @Value("${spring.datasource.password}")
    private static String password;

    @Value("${spring.jpa.properties.hibernate.dialect}")
    private static String dialect;

    @Value("${spring.datasource.show_sql}")
    private static String showSql;

    @Value("${spring.jpa.hibernate.hbm2ddl-auto}")
    private static String hbm2ddlAuto;
	
	public static SessionFactory getSessionFactory() {
		if(sessionFactory  == null) {
			try {
				StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
						.applySetting(Environment.DRIVER, driver)
						.applySetting(Environment.URL, url)
						.applySetting(Environment.USER, username)
						.applySetting(Environment.PASS, password)
						.applySetting(Environment.DIALECT, dialect)
						.applySetting(Environment.SHOW_SQL, showSql)
						.applySetting(Environment.HBM2DDL_AUTO,hbm2ddlAuto)
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