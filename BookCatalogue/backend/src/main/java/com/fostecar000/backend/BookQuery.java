package com.fostecar000.backend;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

public class BookQuery {
    private static CriteriaBuilder builder;
    private CriteraQuery<Book> query;
    private Root<Book> book;
    private Join<Book, Tag> tag;
    private boolean joinedTags,
                    andNext,
                    orNext,
                    notNext;
    private Predicate predicate;

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
        andNext = false;
        orNext = false;
        notNext = false;
    }

    public BookQuery and() {
        if (predicate == null) throw new BookQueryException("an 'and' cannot be the first method call");
        if (andNext) throw new BookQueryException("cannot have two 'and's immediately next to each other");
        if (orNext) throw new BookQueryException("cannot have an 'and' immediately after an 'or'");
        if (notNext) throw new BookQueryException("'not' only negates conditions, cannot negate operators");
        andNext = true;
        return this;
    }

    public BookQuery or() {
        if (predicate == null) throw new BookQueryException("an 'or' cannot be the first method call");
        if (orNext) throw new BookQueryException("cannot have two 'or's immediately next to each other");
        if (andNext) throw new BookQueryException("cannot have an 'or' immediately after an 'and'");
        if (notNext) throw new BookQueryException("'not' only negates conditions, cannot negate operators");
        orNext = true;
        return this;
    }

    public BookQuery not() {
        notNext = !notNext; // can have two nots next to each other (I don't know why one would want to, but you can)
        return this;
    }

    public BookQuery hasTag(String t) {
        if (!joinedTags) {
            tag = book.join(Book_.tags);
            joinedTags = true;
        }
        Predicate p = builder.equal(Tag_.tag, t);
        linkPredicates(p);
    }

    private void linkPredicates(Predicate p) {
        if (notNext) {
            p = p.not();
            notNext = false;
        }

        if (predicate == null) predicate = p;

        if (andNext) {
            predicate = builder.and(predicate, p);
        } else if (orNext) {
            predicate = builder.or(predicate, p); // how does this work with order of operations and implied parentheses?
        } else {
            throw new BookQueryException("two conditions were called back to back: no way to link them");
        }
    }
}