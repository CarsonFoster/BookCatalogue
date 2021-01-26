package com.fostecar000.backend;

import javax.persistence.metamodel.StaticMetamodel;
import javax.persistence.metamodel.SingularAttribute;

@StaticMetamodel( Tag.class )
public class Tag_ {
    public static volatile SingularAttribute<Tag, Long> id;
    public static volatile SingularAttribute<Tag, String> tag;
    public static volatile SingularAttribute<Tag, Book> book;
}