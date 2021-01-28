package com.fostecar000.backend;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import java.util.LinkedList;
import java.util.Deque;

public class BookQuery {
    private static CriteriaBuilder builder;

    private CriteraQuery<Book> query;
    private Root<Book> book;
    private Join<Book, Tag> tag;
    private boolean joinedTags,
                    notNext;
    //private Predicate predicate;
    private int currentNumberOfPredicates;
    private Deque<Integer> numberOfPredicates;
    private Deque<Boolean> isAnAndBlock;
    private Deque<Predicate> predicates;


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
        notNext = false;
        currentNumberOfPredicates = 0;
        numberOfPredicates = new LinkedList<>();
        isAnAndBlock = new LinkedList<>();
        predicates = new LinkedList<>();
    }

    private void reusableOperatorCode(boolean isAnAnd) {
        if (notNext) throw new BookQueryException("'not' only negates conditions, cannot negate operators");
        
        currentNumberOfPredicates++;                        // the 'and' result is also a predicate
        numberOfPredicates.push(currentNumberOfPredicates); // push previous number of predicates
        currentNumberOfPredicates = 0;                      // reset current number

        isAnAndBlock.push(isAnAnd);                         // this is/isn't an 'and' block
    }

    private void reusableEndOperatorCode(boolean isAnAnd) {
        if (notNext) throw new BookQueryException("'not' only negates conditions, cannot negate ending functions");
        
        if (isAnAndBlock.isEmpty() || isAnAnd != isAnAndBlock.pop()) 
            throw new BookQueryException("end" + (isAnAnd ? "And" : "Or") + "() called unexpectedly"); // if this isn't the correct block, throw an exception

        Predicate[] predicatesInBlock = new Predicate[currentNumberOfPredicates];
        for (int i = 0; i < currentNumberOfPredicates; i++) {
            predicatesInBlock[i] = predicates.pop();
        }
        Predicate resultOfOperation = (isAnAnd ? builder.and(predicatesInBlock) : builder.or(predicatesInBlock));
        predicates.push(resultOfOperation); // push result back onto stack

        currentNumberOfPredicates = numberOfPredicates.pop(); // restore parent's number
    }

    public BookQuery and() {
        reusableOperatorCode(true);
        return this; // chaining
    }

    public BookQuery endAnd() {
        reusableEndOperatorCode(true);
        return this; // chaining
    }

    public BookQuery or() {
        reusableOperatorCode(false);
        return this; // chaining
    }

    public BookQuery endOr() {
        reusableEndOperatorCode(false);
        return this; // chaining
    }

    public BookQuery not() {
        if (notNext) throw new BookQueryException("why are you negating your 'not'??? I have explicitly disallowed this");
        notNext = true;
        return this; // chaining
    }

    public BookQuery hasTag(String t) {
        if (!joinedTags) {
            tag = book.join(Book_.tags);
            joinedTags = true;
        }
        Predicate p = builder.equal(Tag_.tag, t);
        applyNotAndPushPredicate(p);
    }

    private void applyNotAndPushPredicate(Predicate p) {
        if (notNext) {
            p = p.not();
            notNext = false;
        }
        currentNumberOfPredicates++;
        predicates.push(p);
    }
}