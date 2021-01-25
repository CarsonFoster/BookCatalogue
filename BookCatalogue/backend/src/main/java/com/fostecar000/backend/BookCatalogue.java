package com.fostecar000.backend;

public abstract class BookCatalogue {
    
    public static void addBook(Book b, String[] tags) {
        for (String tag : tags) {
            b.addTag(new Tag(tag));
        }
        
        HibernateUtils.executeTask("'add book " + b.getTitle() + "'", s -> {
            s.persist(b);
        });
    }

}