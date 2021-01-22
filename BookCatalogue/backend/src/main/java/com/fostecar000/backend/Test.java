package com.fostecar000.backend;

import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.function.Consumer;
import java.util.List;
import java.util.ArrayList;


public class Test {
    private String label;
    private Consumer<Session> code;

    public static void main(String args[]) {
        bookTests();
    }

    private Test(String label, Consumer<Session> code) {
        this.label = label;
        this.code = code;
    }

    protected String getLabel() {
        return label;
    }

    protected Consumer<Session> getCode() {
        return code;
    }

    private static void bookTests() {
        List<Test> tests = new ArrayList<>();
        

        Test readBook = new Test("readBook", s -> {
            Book b = (Book) s.get(Book.class, 1L);
            System.out.println(b);
        });
        //tests.add(readBook);

        Test updateBook = new Test("updateBook", s -> {
            Book b = (Book) s.get(Book.class, 1L);
            b.addTag(new Tag("awesome"));
            s.update(b);
        });
        //tests.add(updateBook);

        //tests.add(readBook);
        Test deleteBook = new Test("deleteBook", s -> {
            Book b = (Book) s.get(Book.class, 1L);
            s.delete(b);
        });
        tests.add(deleteBook);

        runTests(tests);
    }

    private static void runTests(List<Test> tests) {
        int numberOfFailures = 0;
        for (Test t : tests) {
            String label = t.getLabel();
            Consumer<Session> code = t.getCode();
            numberOfFailures += test(label, code);
        }
        System.out.println("[I] Total number of test failures: " + numberOfFailures);
    }

    private static int test(String label, Consumer<Session> code) {
        System.out.println("[I] Starting test " + label + ":");
        Transaction transaction = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            transaction = session.getTransaction();
            transaction.begin();

            code.accept(session);

            transaction.commit();
            System.out.println("[+] Committed transaction for test " + label);
            return 0; // success
        } catch (Exception e) {
            System.out.println("[-] EXCEPTION in test " + label + " (rolling back transaction):");
            //if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return 1; // failure
        }
    }
}