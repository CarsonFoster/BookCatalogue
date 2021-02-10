package com.fostecar000.backend;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.LinkedList;
import java.util.Deque;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class BookQuery {
    private CriteriaBuilder builder;
    private Session session;

    private CriteriaQuery<Book> query;
    private Root<Book> book;
    private boolean notNext,
                    queriedAlready;
    private int currentNumberOfPredicates;
    private Deque<Integer> numberOfPredicates;
    private Deque<Boolean> isAnAndBlock;
    private Deque<Predicate> predicates;
    private List<Book> results; // only cached if told to by user
    private static final boolean DEBUG = true; // enables assertions


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
        
        notNext = false;
        queriedAlready = false;
        currentNumberOfPredicates = 0;
        numberOfPredicates = new LinkedList<>();
        isAnAndBlock = new LinkedList<>();
        predicates = new LinkedList<>();
    }


    // I didn't just call removeIf(b -> true) because I wanted removeAll to not use streams (to be more efficient)
    public BookQuery removeAll() {
        List<Book> booksToRemove = query(false);
        getSession(); // make certain we have a session

        // can't use HibernateUtils.sessionWrapper because I want to use this session
        // one transaction so that either all books are deleted or none do; otherwise there could be problems with some books being deleted while others are not
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            for (Book b : booksToRemove) session.delete(b);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();

            if (DEBUG) assert (session != null);
            session.close(); // only close it when an exception is thrown
            session = null; // if an exception is thrown, this Session is now useless; must get another one later
            throw e;
        }
        
        return this; // chaining
    }

    public BookQuery removeIf(java.util.function.Predicate<Book> condition) {
        List<Book> booksToRemove = query(false);
        getSession(); // make sure we have a session
        
        // one transaction for the whole thing, so that either all of the matching books get deleted, or none of them do
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            booksToRemove.stream()
            .filter(condition)
            .forEach(b -> {
                session.delete(b);
            });
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            
            if (DEBUG) assert (session != null);
            session.close(); // only close it when an exception is thrown
            session = null; // once an exception is thrown, this session can't be used anymore, so remove it
            throw e;
        }

        return this; // chaining
    }

    public List<Book> query() {
        return query(true);
    }

    // if the results are cached, then some of the books in the list could potentially no longer be in the database, as they could have been removed afterward
    // if the results are not cached, then at the results at the time of the first query and the second query could be different, as books could have been removed in between
    // this is intentional behavior, and up to the client (me)
    public List<Book> query(boolean storeResult) {
        if (results != null) return results;
        if (!queriedAlready) {
            if (predicates.size() != 1) throw new BookQueryException("unexpected call to query()"); // there should only be one item on the stack -- the final result
            query.where(predicates.pop());
        }

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

        // The following subquery counts the number of times the given tag appears for the currently selected book
        // Thus, we can then test if the count >= 1 to see if the book has that tag; if !(count >= 1) then the book doesn't have the tag

        Subquery sub = query.subquery(Long.class);
        Root subRoot = sub.from(Tag.class);
        sub.select(builder.count(subRoot.get(Tag_.id)));
        sub.where(builder.and(
            builder.equal(book.get(Book_.id), subRoot.get(Tag_.book)),
            builder.equal(subRoot.get(Tag_.tag), t)
        ));

        Predicate p = builder.ge(sub, 1L);
        
        applyNotAndPushPredicate(p);
        return this; // chaining
    }

    public BookQuery isId(long id) {
        checkIfQueried();

        Predicate p = builder.equal(book.get(Book_.id), id);
        applyNotAndPushPredicate(p);
        return this; // chaining
    }

    public BookQuery isTitle(String title) {
        checkIfQueried();

        Predicate p = builder.equal(book.get(Book_.title), title);
        applyNotAndPushPredicate(p);
        return this; // chaining
    }

    public BookQuery isAuthorFirst(String authorFirst) {
        checkIfQueried();

        Predicate p = builder.equal(book.get(Book_.authorFirst), authorFirst);
        applyNotAndPushPredicate(p);
        return this; // chaining
    }

    public BookQuery isAuthorLast(String authorLast) {
        checkIfQueried();

        Predicate p = builder.equal(book.get(Book_.authorLast), authorLast);
        applyNotAndPushPredicate(p);
        return this; // chaining
    }

	public BookQuery isGenre(String genre) {
        checkIfQueried();

        Predicate p = builder.equal(book.get(Book_.genre), genre);
        applyNotAndPushPredicate(p);
        return this; // chaining
    }

	public BookQuery isSeries(String series) {
        checkIfQueried();

        Predicate p = builder.equal(book.get(Book_.series), series);
        applyNotAndPushPredicate(p);
        return this; // chaining
    }

	public BookQuery isNumberInSeries(int numberInSeries) {
        checkIfQueried();

        Predicate p = builder.equal(book.get(Book_.numberInSeries), numberInSeries);
        applyNotAndPushPredicate(p);
        return this; // chaining
    }

	public BookQuery isOriginalPublicationDate(int originalPublicationDate) {
        checkIfQueried();

        Predicate p = builder.equal(book.get(Book_.originalPublicationDate), originalPublicationDate);
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