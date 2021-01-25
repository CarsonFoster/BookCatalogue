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
        final long id = 4L;

        Test createBookBasic = new Test("createBookBasic", s -> {
            Book b = new Book("Dune", "Frank", "Herbert", "scifi", "Dune", 1, 1965);
            s.persist(b);
        });

        Test createBook = new Test("createBook", s -> {
            Book b = new Book("Dune", "Frank", "Herbert", "scifi", "Dune", 1, 1965);
            BookCatalogue.addBook(b, new String[] {"awesome", "great", "religion", "politics", "Muad'Dib"});
        });
        
        Test readBook = new Test("readBook", s -> {
            Book b = (Book) s.get(Book.class, id);
            System.out.println(b);
        });
        
        Test updateBook = new Test("updateBook", s -> {
            Book b = (Book) s.get(Book.class, id);
            //b.addTag(new Tag("awesome"));
            System.out.println("duplicate add entry return: " + b.addTag(new Tag("awesome")));
            s.update(b);
        });
        
        Test deleteBook = new Test("deleteBook", s -> {
            Book b = (Book) s.get(Book.class, id);
            s.delete(b);
        });

        //tests.add(createBook);
        tests.add(readBook);
        //tests.add(updateBook);
        //tests.add(readBook);
        //tests.add(deleteBook);

        runTests(tests);
    }

    private static void runTests(List<Test> tests) {
        int numberOfFailures = 0;
        for (Test t : tests) {
            String label = t.getLabel();
            Consumer<Session> code = t.getCode();
            numberOfFailures += HibernateUtils.executeTask(label, code);
        }
        System.out.println("[I] Total number of test failures: " + numberOfFailures);
    }
}