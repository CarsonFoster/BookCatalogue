package com.fostecar000.backend;

import java.util.HashMap;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import java.util.function.Consumer;
import org.hibernate.Session;
import org.hibernate.Transaction;

public abstract class HibernateUtils {
    private static SessionFactory sessionFactory;
    private static StandardServiceRegistry stdServiceReg;
    private static final String showSql = "true";

    public static SessionFactory getSessionFactory() {
        if (sessionFactory != null) return sessionFactory;
        try {
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();

            HashMap<String, String> settings = new HashMap<>();

            // connection settings
            // settings.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver"); // Hibernate claims that manually loading the driver class is unnecessary
            settings.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/catalogue");
            settings.put("hibernate.connection.username", "root");
            settings.put("hibernate.connection.password", "fostecar000rule$");

            // miscellaneous
            settings.put("hibernate.show_sql", showSql);
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

    public static int sessionWrapper(String label, Consumer<Session> code) {
        System.out.println("[*] Starting task " + label);
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.getTransaction();
            transaction.begin();

            code.accept(session);

            transaction.commit();
            System.out.println("[+] Committed transaction for task " + label);
            return 0; // success
        } catch (Exception e) {
            System.out.println("[-] EXCEPTION in task " + label + " (rolling back transaction):");
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return 1; // failure
        }
    }
}