package com.fostecar000.backend;

public abstract class BookCatalogue {
    
    public static void addBook(Book b, String... tags) {
        for (String tag : tags) {
            b.addTag(new Tag(tag));
        }
        
        HibernateUtils.sessionWrapper("add book '" + b.getTitle() + "'", s -> {
            s.persist(b);
        });
    }

    public static void addBook(Book b, Iterable<String> tags) {
        for (String tag : tags) {
            b.addTag(new Tag(tag));
        }

        HibernateUtils.sessionWrapper("add book '" + b.getTitle() + "'", s -> {
            s.persist(b);
        });
    }

    public static void updateBook(Book b, String... newTags) {
        for (String tag : tags)
            b.addTag(new Tag(tag));
        
        HibernateUtils.sessionWrapper("update book '" + b.getTitle() + "'", s -> {
            s.update(b);
        });
    }

    public static void updateBook(Book b, Iterable<String> newTags) {
        for (String tag : newTags)
            b.addTag(new Tag(tag));
        
        HibernateUtils.sessionWrapper("update book '" + b.getTitle() + "'", s -> {
            s.update(b);
        });
    }

    public static void removeTags(Book b, String... tagsToRemove) {
        for (String tag : tagsToRemove)
            b.removeTag(new Tag(tag)); // this works because Tag.hashCode() is its string's hashcode, and the equals method only checks the strings

        HibernateUtils.sessionWrapper("remove tags from book '" + b.getTitle() + "'", s -> {
            s.update(b);
        });
    }

}