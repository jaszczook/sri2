package edu.pjwstk.sri.lab2.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Category")
public class Category implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

//	private Date created;
//
//	private Date updated;

	@Column(name = "name", nullable = false)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	// @Column(name = "parent_category_id")
	@JoinColumn(name = "PARENT_CATEGORY_ID")
	private Category parentCategory;
	

	@OneToMany(fetch = FetchType.LAZY, mappedBy="parentCategory")
	private Set<Category> childCategories;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Category)) {
			return false;
		}
		Category other = (Category) obj;
		if (id != null) {
			if (!id.equals(other.id)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public Category getParentCategory() {
		return this.parentCategory;
	}

	public void setParentCategory(final Category parentCategory) {
		this.parentCategory = parentCategory;
	}

	public Set<Category> getChildCategories() {
		return childCategories;
	}

	public void setChildCategories(Set<Category> childCategories) {
		this.childCategories = childCategories;
	}
	

//	public Date getCreated() {
//		return created;
//	}
//
//	public void setCreated(Date created) {
//		this.created = created;
//	}
//
//	public Date getUpdated() {
//		return updated;
//	}
//
//	public void setUpdated(Date updated) {
//		this.updated = updated;
//	}

	@Override
	public String toString() {
		return "Category [id=" + id + ", name=" + name + "]";
	}

//	@PrePersist
//	protected void onCreate() {
//		created = new Date();
//	}
//
//	@PreUpdate
//	protected void onUpdate() {
//		updated = new Date();
//	}

}