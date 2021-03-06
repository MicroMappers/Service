package qa.qcri.mm.api.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import qa.qcri.mm.api.dao.AbstractDao;

public abstract class AbstractDaoImpl<E, I extends Serializable> implements AbstractDao<E,I> {

    private final Class<E> entityClass;

    protected AbstractDaoImpl(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    @Autowired
    private SessionFactory sessionFactory;

    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public E findById(I id) {
        return (E) getCurrentSession().get(entityClass, id);
    }

    @Override
    public void saveOrUpdate(E e) {
        Session session = getCurrentSession();
        session.saveOrUpdate(e);
    }

    @Override
    public void saveOrMerge(E e) {
        Session session = getCurrentSession();
        session.merge(e);
    }

    @Override
    public I save(E e) {
        Session session = getCurrentSession();
        return (I) session.save(e);
    }

    @Override
    public List<E> findAll() {
        Criteria criteria = getCurrentSession().createCriteria(entityClass);
        criteria.setProjection(Projections.distinct(Projections.property("id")));
        return criteria.list();
    }

    @Override
    public void delete(E e) {
        Session session = getCurrentSession();
        session.delete(e);
    }

    @Override
    public List<E> findByCriteria(Criterion criterion) {
        Criteria criteria = getCurrentSession().createCriteria(entityClass);
        criteria.add(criterion);
        return criteria.list();
    }

    @Override
    public List<E> findByProjection(ProjectionList projectionList, Criterion criterion) {
        Criteria criteria = getCurrentSession().createCriteria(entityClass);
        criteria.add(criterion);
        criteria.setProjection(projectionList);
        criteria.setResultTransformer(Transformers.aliasToBean(entityClass));
        return criteria.list();
    }

    @Override
    public List<E> getMaxOrderByCriteria(Criterion criterion, String orderBy) {
        Criteria criteria = getCurrentSession().createCriteria(entityClass);
        criteria.add(criterion);
        criteria.addOrder(Order.desc(orderBy));
        criteria.setMaxResults(1);

        return criteria.list();
    }

    @Override
     public List<E> findByCriteriaByOrder(Criterion criterion, String[] orderBy, Integer count) {
        Criteria criteria = getCurrentSession().createCriteria(entityClass);
        criteria.add(criterion);
        for(int i = 0; i< orderBy.length; i++){
            criteria.addOrder(Order.desc(orderBy[i]));
        }
        if(count != null){
            criteria.setMaxResults(count);
        }
        return criteria.list();
    }

    @Override
    public List<E> findByCriteriaWithAliasByOrder(Criterion criterion, String[] orderBy, Integer count, String aliasTable, Criterion aliasCriterion) {
        Criteria criteria = getCurrentSession().createCriteria(entityClass);
        criteria.add(criterion);
        criteria.createAlias(aliasTable, aliasTable, CriteriaSpecification.LEFT_JOIN).add(aliasCriterion);

        for(int i = 0; i< orderBy.length; i++){
            criteria.addOrder(Order.desc(orderBy[i]));
        }
        if(count != null){
            criteria.setMaxResults(count);
        }
        return criteria.list();
    }

    @Override
    public List<E> findByCriteria(Criterion criterion, Integer count) {
        Criteria criteria = getCurrentSession().createCriteria(entityClass);
        criteria.add(criterion);

        if(count != null){
            criteria.setMaxResults(count);
        }
        return criteria.list();
    }

    @Override
    public E findByCriterionID(Criterion criterion) {
        Criteria criteria = getCurrentSession().createCriteria(entityClass);
        criteria.add(criterion);
        return (E) criteria.uniqueResult();
    }

    @Override
    public List<E> getAll() {
        Criteria criteria = getCurrentSession().createCriteria(entityClass);
        return criteria.list();
    }
}
