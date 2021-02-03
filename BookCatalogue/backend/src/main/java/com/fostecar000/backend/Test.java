package com.fostecar000.backend;

import org.hibernate.Session;
import java.util.function.Consumer;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


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

    private static Book[] createDummyBooks(int num) {
        if (num <= 0) throw new RuntimeException("expected positive number greater than zero");
        Book[] dummies = new Book[num];
        for (int i = 0; i < num; i++) {
            dummies[i] = new Book("Book " + i, "first", "last", "genre", "series", i, 2021);
        }
        return dummies;
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
            Book b = new Book("Dune", "Frank", "Herbert", "scifi", "Dune", 1, 1965);
            BookCatalogue.addBook(b, "awesome", "great", "religion", "politics", "Muad'Dib");
        });
        
        Test queryBasic = new Test("createDummies", () -> {
            /*Book[] dummies = createDummyBooks(50);
            HashMap<String, Set<Integer>> hasTag = new HashMap<>();
            String[] tags = new String[] {"awesome", "great", "good", "bad", "sad", "happy", "neutral"};
            double[] freqs = new double[] {0.1, 0.2, 0.3, 0.4, 0.33, 0.33, 0.34};
            if (tags.length != freqs.length) throw new RuntimeException("tag and frequency lengths must be equal Carson");
            for (String t : tags) hasTag.put(t, new HashSet<Integer>());

            List<String> tagsToAdd = new ArrayList<>();
            for (int i = 0; i < dummies.length; i++) { // i is book index
                for (int j = 0; j < tags.length; j++) { // j is tag index
                    if (Math.random() < freqs[j]) {
                        hasTag.get(tags[j]).add(i);
                        tagsToAdd.add(tags[j]);
                    }
                }
                BookCatalogue.addBook(dummies[i], tagsToAdd);
                tagsToAdd.clear();
            }*/

            List<Book> results = new BookQuery()
                .and()
                    .hasTag("awesome")
                    .not()
                        .hasTag("bad")
                .endAnd()
                .query();


            /*Set<Integer> awesome = hasTag.get("awesome");
            for (Integer seriesNum : awesome) {
                boolean found = false;
                for (Book b : results) {
                    if (b.getNumberInSeries() == seriesNum) found = true;
                }
                if (!found) System.out.println("Book " + seriesNum + " was not found.");
            }
            for (Book b : results) {
                if (!awesome.contains(b.getNumberInSeries())) System.out.println("Extra book found: " + b.getNumberInSeries());
            }*/
            System.out.println(results);
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
        //tests.add(readBook);
        //tests.add(updateBook);
        //tests.add(readBook);
        //tests.add(deleteBook);
        tests.add(queryBasic);

        runTests(tests);
    }

    private static void runTests(List<Test> tests) {
        for (Test t : tests)
            t.getCode().run();
    }
}