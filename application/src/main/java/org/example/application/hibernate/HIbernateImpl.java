package org.example.application.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaRoot;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class HIbernateImpl {


    private SessionFactory sessionFactory;

    public HIbernateImpl(LocalSessionFactoryBean sessionFactory) {
        this.sessionFactory = sessionFactory.getObject();
    }

    public Session getSession() {
        try {
            // Пытаемся получить текущую сессию
            // всегда бросает исключениие при неполучении
            return sessionFactory.getCurrentSession();
        } catch (HibernateException e) {
            // Если нет текущей сессии - открываем новую
            return sessionFactory.openSession();
        }
    }

    @Transactional
    public List<AccountEntity> findAll() throws Exception{

        try {
            Session session = sessionFactory.openSession();
            HibernateCriteriaBuilder builder = session.getCriteriaBuilder();
            JpaCriteriaQuery<AccountEntity> query = builder.createQuery(AccountEntity.class);
            JpaRoot <AccountEntity> root = query.from(AccountEntity.class);
            query.select(root);
            return session.createQuery(query).list();

        }catch (HibernateException e) {
            System.out.println("ОШибка " + e.getMessage());
            throw new Exception("Ошибка в findAll in HIbernateImpl: " + e.getMessage());
        }
    }


    public void insertAccounts( List<AccountEntity> list)throws Exception{
        // так как в вызывается в PostConstruct, там нет никакой транзакции, поэтому нужно создавать самому
        // создаю не в @PostCOnstruct у сервиса, а здесь
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {


            int batchSize = 10;
            int i= 0;
            for (AccountEntity accountEntity : list) {
                // для тех кто, без id
                // всегда требует транзакцию
                session.persist(accountEntity);

                if (i%batchSize == 0){
                    session.flush();
                    session.clear();
                }
            }

            transaction.commit();
        }catch (Exception e){
            transaction.rollback();
            System.out.println("Ошибка insertAccounts: " + e.getMessage());
            throw  new Exception("Ошибка insertAccounts: " + e.getMessage());
        }

    }


}
