package com.fostecar000.backend;

import javax.persistence.metamodel.StaticMetamodel;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

@StaticMetamodel( Book.class )
public class Book_ {
    public static volatile SingularAttribute<Book, Long> id;
    public static volatile SingularAttribute<Book, String> title;
    public static volatile SingularAttribute<Book, String> authorFirst;
    public static volatile SingularAttribute<Book, String> authorLast;
	public static volatile SingularAttribute<Book, String> genre;
	public static volatile SingularAttribute<Book, String> series;
	public static volatile SingularAttribute<Book, Integer> numberInSeries;
	public static volatile SingularAttribute<Book, Integer> originalPublicationDate;
    public static volatile SetAttribute<Book, Tag> tags;
}