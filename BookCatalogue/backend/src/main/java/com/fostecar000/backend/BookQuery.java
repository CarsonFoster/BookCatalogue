package com.fostecar000.backend;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Join;
import java.util.LinkedList;
import java.util.Deque;
import java.util.List;
import org.hibernate.Session;

public class BookQuery {
    private CriteriaBuilder builder;
    private Session session;

    private CriteriaQuery<Book> query;
    private Root<Book> book;
    private Join<Book, Tag> tag;
    private boolean joinedTags,
                    notNext,
                    queriedAlready;
    private int currentNumberOfPredicates;
    private Deque<Integer> numberOfPredicates;
    private Deque<Boolean> isAnAndBlock;
    private Deque<Predicate> predicates;
    private List<Book> results; // only cached if told to by user


    private void getBuilder() throws IllegalStateException {
        getSession();
        
        if (builder == null)
            builder = session.getCriteriaBuilder();
    }

    private void getSession() throws IllegalStateException {
        if (session == null)
            session = HibernateUtils.getSessionFactory().openSession();
    }

    public BookQuery() {
        getBuilder();
        query = builder.createQuery(Book.class);
        book = query.from(Book.class);
        query.select(book);
        
        joinedTags = false;
        notNext = false;
        queriedAlready = false;
        currentNumberOfPredicates = 0;
        numberOfPredicates = new LinkedList<>();
        isAnAndBlock = new LinkedList<>();
        predicates = new LinkedList<>();
    }

    public List<Book> query() {
        return query(true);
    }

    public List<Book> query(boolean storeResult) {
        if (results != null) return results;
        if (predicates.size() != 1) throw new BookQueryException("unexpected call to query()"); // there should only be one item on the stack -- the final result
        if (!queriedAlready) query.where(predicates.pop());

        queriedAlready = true;
        getSession();
        List<Book> resultList = session.createQuery(query).getResultList();
        if (storeResult) results = resultList;
        return resultList;
    }

    private void reusableOperatorCode(boolean isAnAnd) {
        checkIfQueried();
        if (notNext) throw new BookQueryException("'not' only negates conditions, cannot negate operators");
        
        currentNumberOfPredicates++;                        // the 'and'/'or' result is also a predicate
        numberOfPredicates.push(currentNumberOfPredicates); // push previous number of predicates
        currentNumberOfPredicates = 0;                      // reset current number

        isAnAndBlock.push(isAnAnd);                         // this is/isn't an 'and' block
    }

    private void reusableEndOperatorCode(boolean isAnAnd) {
        checkIfQueried();
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

    private void checkIfQueried() {
        if (queriedAlready) throw new BookQueryException("the query has already been created; you cannot modify it now");
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
        checkIfQueried();
        if (notNext) throw new BookQueryException("why are you negating your 'not'??? I have explicitly disallowed this");
        notNext = true;
        return this; // chaining
    }

    public BookQuery hasTag(String t) {
        checkIfQueried();
        if (!joinedTags) {
            tag = book.join(Book_.tags);
            joinedTags = true;
        }
        Predicate p = builder.equal(tag.get(Tag_.tag), t);
        applyNotAndPushPredicate(p);
        return this; // chaining
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