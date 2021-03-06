package com.mitrais.core.data.daoimpl;

import com.mitrais.core.common.util.HibernateUtil;
import com.mitrais.core.data.dao.GenericDao;
import org.hibernate.*;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by duongtuan1211 on 3/30/2018.
 */
public class AbtstractDao<ID extends Serializable,T> implements GenericDao<ID,T> {
    private Class<T> persistenceClass;
    public AbtstractDao(){
        this.persistenceClass= (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }
    public String getPersistenceClassName(){
        return persistenceClass.getSimpleName();
    }

    public List findAll() {
        List<T> list=new ArrayList<T>();

            Session session=HibernateUtil.getSessionFactory().openSession();
            Transaction transaction=session.beginTransaction();
        try{
            //HQL
            StringBuilder sql=new StringBuilder("from ");
            sql.append(this.getPersistenceClassName());
            Query query=session.createQuery(sql.toString());
            list=query.list();
            transaction.commit();

        } catch (HibernateException e){
            transaction.rollback();
            throw e;
        }
        finally {
            session.close();
        }
        return list;
    }

    public T update(T entity) {
        T result=null;
        Session session=HibernateUtil.getSessionFactory().openSession();
        Transaction transaction=session.beginTransaction();
        try{
            Object object=session.merge(entity);
            result =(T)object;
            transaction.commit();
        }catch (HibernateException e){
            transaction.rollback();
            throw e;
        }
        finally {
            session.close();
        }
        return result;
    }

    public void save(T entity) {
        Session session=HibernateUtil.getSessionFactory().openSession();
        Transaction transaction=session.beginTransaction();
        try{
            session.persist(entity);
            transaction.commit();
        }catch (HibernateException e){
            transaction.rollback();
            throw e;
        }
        finally {
            session.close();
        }
    }

    public T findById(ID id) {
        T result=null;
        Session session=HibernateUtil.getSessionFactory().openSession();
        Transaction transaction=session.beginTransaction();
        try{
            result = (T) session.get(persistenceClass,id);
            if(result==null){
                throw new ObjectNotFoundException(" Not Found "+id,null);
            }
        }catch(HibernateException e){
            transaction.rollback();
            throw e;
        }
        finally {
            session.close();
        }
        return result;
    }

    public Integer delete(List<ID> ids) {
        Integer count=0;
        Session session=HibernateUtil.getSessionFactory().openSession();
        Transaction transaction=session.beginTransaction();
        try{
            for(ID item:ids){
                T t=(T)session.get(persistenceClass,item);
                session.delete(t);
                count++;
            }
            transaction.commit();
        }catch (HibernateException e){
            transaction.rollback();
            throw e;
        }
        finally {
            session.close();
        }
        return count;
    }
}
