package edu.pjwstk.sri.lab2.dao;

import edu.pjwstk.sri.lab2.model.Category;
import edu.pjwstk.sri.lab2.shared.CategoryCache;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * DAO for Category
 */
@Stateless
public class CategoryDao {
	@PersistenceContext(unitName = "sri2-persistence-unit")
	private EntityManager em;

	@EJB
	CategoryCache categoryCache;

	public void create(Category entity) {
		em.persist(entity);
	}

	public void deleteById(Long id) {
		Category entity = em.find(Category.class, id);
		if (entity != null) {
			em.remove(entity);
		}
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Category findById(Long id) {
		return categoryCache.getCategory(id);
	}

	public Category update(Category entity) {
		return em.merge(entity);
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Category> listAll() {
		return categoryCache.getCategoryList();
	}
}
