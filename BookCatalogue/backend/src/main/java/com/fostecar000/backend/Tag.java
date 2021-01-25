package com.fostecar000.backend;

import javax.persistence.*;

@Entity
@Table( name = "tags" )
public class Tag {
    
    @Id
    @Column( name = "tag_id" )
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @Column( name = "tag" )
    private String tag;

    @ManyToOne
    private Book book;

    public Tag() {

    }

    public Tag(String tag) {
        this.tag = tag;
    }

    // this equals method only checks whether the tag strings are the same
    // I want a Book to have a set of tags, and check if adding a duplicate tag: however, before the new tag is added, if you look at whether each tag's book is the same, then they won't be equal (as the new tag's book will be null)
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (o.getClass() != this.getClass()) return false;
        Tag other = (Tag)o;
        return tag.equals(other.getTag());
    }

    // this equals method also checks for equality between the books
    public boolean equalsTotal(Object o) {
        if (!equals(o)) return false;
        Tag other = (Tag) o;
        return (book == null && other.book == null) || (book != null && book.equals(other.getBook()));
    }

    public int hashCode() {
        return tag.hashCode();
    }

    public String toString() {
        return tag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public String getTag() {
		return this.tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Book getBook() {
		return book;
	}

    void setBook(Book b) {
        this.book = b;
    }
}