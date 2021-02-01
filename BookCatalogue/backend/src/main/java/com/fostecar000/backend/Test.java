package com.fostecar000.backend;

import org.hibernate.Session;
import java.util.function.Consumer;
import java.util.List;
import java.util.ArrayList;


public class Test {
    private String label;
    private Runnable code;

    public static void main(String args[]) {
        bookTests();
    }

    private Test(String label, Runnable code) {
        this.label = label;
        this.code = code;
    }

    protected String getLabel() {
        return label;
    }

    protected Runnable getCode() {
        return code;
    }

    private static void bookTests() {
        List<Test> tests = new ArrayList<>();
        final long id = 4L;

        Test createBookBasic = new Test("createBookBasic", () -> {
            HibernateUtils.sessionWrapper("createBookBasic", s -> {
                Book b = new Book("Dune", "Frank", "Herbert", "scifi", "Dune", 1, 1965);
                s.persist(b);
            });
        });

        Test createBook = new Test("createBook", () -> {
            HibernateUtils.sessionWrapper("createBook", s -> {
                Book b = new Book("Dune", "Frank", "Herbert", "scifi", "Dune", 1, 1965);
                BookCatalogue.addBook(b, new String[] {"awesome", "great", "religion", "politics", "Muad'Dib"});
            });
        });
        
        Test readBook = new Test("readBook", () -> {
            HibernateUtils.sessionWrapper("readBook", s -> {
                Book b = (Book) s.get(Book.class, id);
                System.out.println(b);
            });
        });
        
        Test updateBook = new Test("updateBook", () -> {
            HibernateUtils.sessionWrapper("updateBook", s -> {
                Book b = (Book) s.get(Book.class, id);
                //b.addTag(new Tag("awesome"));
                System.out.println("duplicate add entry return: " + b.addTag(new Tag("awesome")));
                s.update(b);
            });
        });
        
        Test deleteBook = new Test("deleteBook", () -> {
            HibernateUtils.sessionWrapper("deleteBook", s -> {
                Book b = (Book) s.get(Book.class, id);
                s.delete(b);
            });
        });

        //tests.add(createBook);
        tests.add(readBook);
        //tests.add(updateBook);
        //tests.add(readBook);
        //tests.add(deleteBook);

        runTests(tests);
    }

    private static void runTests(List<Test> tests) {
        for (Test t : tests)
            t.getCode().run();
    }
}