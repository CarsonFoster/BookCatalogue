package com.fostecar000.backend;

import javax.persistence.*;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table( name = "books" )
public class Book {

	@Id
    @Column( name = "book_id" )
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

	@Column( name = "title" )
    private String title;

	@Column( name = "author_first" )
    private String authorFirst;

	@Column( name = "author_last" )
    private String authorLast;
    
	@Column( name = "genre" )
	private String genre;
    
	@Column( name = "series" )
	private String series;
    
	@Column( name = "number_in_series" )
	private int numberInSeries;
    
	@Column( name = "original_publication_date" )
	private int originalPublicationDate;

	@OneToMany( mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true )
    private Set<Tag> tags;

    public Book() {

    }

    public Book(String title, String authorFirst, String authorLast, String genre, String series, int numberInSeries, int originalPublicationDate) {
		this.title = title;
		this.authorFirst = authorFirst;
		this.authorLast = authorLast;
		this.genre = genre;
		this.series = series;
		this.numberInSeries = numberInSeries;
		this.originalPublicationDate = originalPublicationDate;
		tags = new HashSet<>();
    }

	public boolean equals(Object o) {
		if (o == null) return false;
		if (o == this) return true;
		if (o.getClass() != this.getClass()) return false;
		Book other = (Book) o;
		return title.equals(other.getTitle()) && authorFirst.equals(other.getAuthorFirst()) && authorLast.equals(other.getAuthorLast())
			&& genre.equals(other.getGenre()) && series.equals(other.getSeries()) && numberInSeries == other.getNumberInSeries()
			&& originalPublicationDate == other.getOriginalPublicationDate();
	}
    
	public String toString() {
		return String.format("\"%s\" (%s #%d) by %s %s (published %d), genre: %s, tags: %s", title, series, numberInSeries, authorFirst, authorLast, originalPublicationDate, genre, tags.toString());
	}

    public Long getId() {
        return id;
    }

	public void setId(Long id) {
		this.id = id;
	}
    
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
    
	public String getAuthorFirst() {
		return this.authorFirst;
	}

	public void setAuthorFirst(String authorFirst) {
		this.authorFirst = authorFirst;
	}
    
	public String getAuthorLast() {
		return this.authorLast;
	}

	public void setAuthorLast(String authorLast) {
		this.authorLast = authorLast;
	}

	public String getGenre() {
		return this.genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getSeries() {
		return this.series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public int getNumberInSeries() {
		return this.numberInSeries;
	}

	public void setNumberInSeries(int numberInSeries) {
		this.numberInSeries = numberInSeries;
	}

    
	public int getOriginalPublicationDate() {
		return this.originalPublicationDate;
	}

	public void setOriginalPublicationDate(int originalPublicationDate) {
		this.originalPublicationDate = originalPublicationDate;
	}
	
    protected Iterable<Tag> getTags() {
        return tags;
    }

	protected boolean addTag(Tag t) {
		if (tags.contains(t)) return false;
		tags.add(t);
		t.setBook(this);
		return true;
	}

	protected void removeTag(Tag t) {
		boolean contained = tags.contains(t);
		tags.remove(t); // does nothing if tag isn't in set
		if (contained) t.setBook(null); // if a tag that wasn't in the tags set is passed, don't null the book
	}

	public List<String> getTagNames() {
		return tags.stream()
				.map(t -> t.getTag())
				.collect(Collectors.toList());
	}
}