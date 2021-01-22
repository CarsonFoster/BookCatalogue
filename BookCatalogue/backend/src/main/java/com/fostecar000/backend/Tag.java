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

    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (o.getClass() != this.getClass()) return false;
        Tag other = (Tag)o;
        return tag.equals(other.getTag()) && ((book == null && other.book == null) || (book != null && book.equals(other.getBook())));
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