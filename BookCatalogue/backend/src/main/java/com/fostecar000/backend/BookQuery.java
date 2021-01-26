package com.fostecar000.backend;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

public class BookQuery {
    private static CriteriaBuilder builder;
    private CriteraQuery<Book> query;
    private Root<Book> book;
    private Join<Book, Tag> tag;
    private boolean joinedTags;

    private static void getBuilder() throws IllegalStateException {
        if (cb == null) {
            Session session = HibernateUtils.getSessionFactory().openSession()) {
            cb = session.getCriteriaBuilder();
        }
    }

    public BookQuery() {
        getBuilder();
        query = builder.createQuery(Book.class);
        book = query.from(Book.class);
        query.select(book);
        joinedTags = false;
    }

    public BookQuery hasTag(String t) {
        if (!joinedTags) {
            tag = book.join(Book_.tags);
            joinedTags = true;
        }

        
    }
}