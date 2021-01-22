package com.fostecar000.backend;

import java.util.HashMap;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtils {
    private static SessionFactory sessionFactory;
    private static StandardServiceRegistry stdServiceReg;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory != null) return sessionFactory;
        try {
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();

            HashMap<String, String> settings = new HashMap<>();

            // connection settings
            settings.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
            settings.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/catalogue");
            settings.put("hibernate.connection.username", "root");
            settings.put("hibernate.connection.password", "fostecar000rule$");

            // miscellaneous
            settings.put("hibernate.show_sql", "true");
            settings.put("hibernate.hbm2ddl.auto", "update");

            builder.applySettings(settings);
            stdServiceReg = builder.build();

            MetadataSources sources = new MetadataSources(stdServiceReg)
                .addAnnotatedClass(Tag.class)
                .addAnnotatedClass(Book.class);
            sessionFactory = sources.buildMetadata().buildSessionFactory();
            return sessionFactory;
        } catch (Exception e) {
            System.out.println("Failed to create the SessionFactory object. Printing stack trace below.");
            e.printStackTrace();
            destroyRegistry();
            return null;
        }
    }

    public static void destroyRegistry() {
        if (stdServiceReg != null) StandardServiceRegistryBuilder.destroy(stdServiceReg);
    }
}