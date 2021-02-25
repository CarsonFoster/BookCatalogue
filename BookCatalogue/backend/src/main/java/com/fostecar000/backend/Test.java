package com.fostecar000.backend;

import org.hibernate.Session;
import java.util.function.Consumer;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.io.*;
import javax.persistence.criteria.*;


public class Test {
    private String label;
    private Runnable code;

    public static void main(String args[]) {
        try {
            Database db = new Database();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //bookTests();
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

        Test createDummies = new Test("createDummies", () -> {
            Book[] dummies = createDummyBooks(50);
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
            }
        });

        Test subqueryTest = new Test("subquery", () -> {
            HibernateUtils.sessionWrapper("subquery", s -> {
                CriteriaBuilder builder = s.getCriteriaBuilder();
                CriteriaQuery query = builder.createQuery(Book.class);
                Root root = query.from(Book.class);

                Subquery sub = query.subquery(Long.class);
                Root subRoot = sub.from(Tag.class);
                sub.select(builder.count(subRoot.get(Tag_.id)));
                sub.where(builder.and(
                    builder.equal(root.get(Book_.id), subRoot.get(Tag_.book)),
                    builder.equal(subRoot.get(Tag_.tag), "awesome")
                ));

                Subquery sub2 = query.subquery(Long.class);
                Root sub2Root = sub2.from(Tag.class);
                sub2.select(builder.count(sub2Root.get(Tag_.id)))
                    .where(builder.and(
                        builder.equal(root.get(Book_.id), sub2Root.get(Tag_.book)),
                        builder.equal(sub2Root.get(Tag_.tag), "bad")
                    ));

                query.where(builder.and(
                    builder.ge(sub, 1L),
                    builder.lessThan(sub2, 1L)
                ));
                List<Book> results = s.createQuery(query).getResultList();
                for (Book b : results) System.out.println(b);
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

        Test queryBasic = new Test("queryBasic", () -> {
            List<Book> results = new BookQuery()
                .and()
                    .hasTag("dummy")
                    .not()
                        .isNumberInSeries(1)
                    .not()
                        .hasTag("second")
                .endAnd()
                .query();
            
            for (Book b : results) System.out.println(b);
        });

        Test addMultipleBooks = new Test("addMultipleBooks", () -> {
            BookCatalogue.addBook(new Book("Dummy Book #1", "Carson", "Foster", "dummy", "Dummy", 1, 2021), "dummy", "first", "to_delete");
            BookCatalogue.addBook(new Book("Dummy Book #2", "Carson", "Foster", "dummy", "Dummy", 2, 2021), "dummy", "second", "to_delete");
            BookCatalogue.addBook(new Book("Dummy Book #3", "Carson", "Foster", "dummy", "Dummy", 3, 2021), "dummy", "third", "to_delete");
        });

        Test removeQuery = new Test("removeQuery", () -> {
            new BookQuery()
                .hasTag("to_delete")
                .query(false)
                .stream()
                .forEach(b -> System.out.println(b));
            new BookQuery()
                .hasTag("to_delete")
                .removeAll();
        });

        //tests.add(createBook);
        //tests.add(readBook);
        //tests.add(updateBook);
        //tests.add(readBook);
        //tests.add(deleteBook);
        //tests.add(createDummies);
        //tests.add(queryBasic);
        //tests.add(subqueryTest);
        //tests.add(addMultipleBooks);
        tests.add(removeQuery);

        runTests(tests);
    }

    private static void runTests(List<Test> tests) {
        for (Test t : tests)
            t.getCode().run();
    }
}