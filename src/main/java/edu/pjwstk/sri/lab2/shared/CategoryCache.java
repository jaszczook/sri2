package edu.pjwstk.sri.lab2.shared;

import edu.pjwstk.sri.lab2.model.Category;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@Startup
public class CategoryCache {

	@PersistenceContext(unitName = "sri2-persistence-unit")
	private EntityManager em;

	@Resource
	private TimerService timerService;

	private ConcurrentHashMap<Long, Category> categoryHashMap;


	@PostConstruct
	private void init() {
		timerService.createTimer(0, 10 * 1000, "Category Cache Timer");
	}

	@Timeout
	public void update(Timer timer) {
		List<Category> categoryList = updateCategoryList(null, null);
		categoryHashMap = buildCategoryMap(categoryList);

		System.out.println("Category Cache has been updated. Timer Service : " + timer.getInfo() + timer.getNextTimeout());
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private List<Category> updateCategoryList(Integer startPosition, Integer maxResult) {
		TypedQuery<Category> findAllQuery = em
				.createQuery(
						"SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.parentCategory LEFT JOIN FETCH c.childCategories ORDER BY c.id",
						Category.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		return findAllQuery.getResultList();
	}

	private ConcurrentHashMap<Long, Category> buildCategoryMap(List<Category> categoryList) {
		categoryHashMap = new ConcurrentHashMap<Long, Category>();

		for (Category category : categoryList) {
			categoryHashMap.put(category.getId(), category);
		}

		return categoryHashMap;
	}

	public Category getCategory(Long index) {
		return categoryHashMap.get(index);
	}

	public List<Category> getCategoryList() {
		return (List<Category>) categoryHashMap.values();
	}
}
