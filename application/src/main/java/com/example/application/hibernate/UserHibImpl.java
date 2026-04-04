package com.example.application.hibernate;

import com.example.application.errors.CanNotMakeExecution;
import com.example.application.model.User;
import com.example.application.model.UserSecured;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaRoot;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Repository;

@Repository
@DependsOn("liquibase")
public class UserHibImpl extends HibernateAbstractDao<User,Long, Logger> {
    private static final Logger logger = LogManager.getLogger(UserHibImpl.class);
    public UserHibImpl() {
        super(User.class);
    }

    @Transactional
    public UserSecured getUserByLogin(String login) throws CanNotMakeExecution {
        Session session = getSessionFactory().openSession();
        try {
            System.out.println("LKogin "+  login);
            HibernateCriteriaBuilder builder = session.getCriteriaBuilder();
            JpaCriteriaQuery<User> query = session.getCriteriaBuilder().createQuery(User.class);
            JpaRoot<User> root = query.from(User.class);

            query.where(builder.equal(root.get("login"), login));
            User result = session.createQuery(query).getSingleResult();
            return new UserSecured(result);
        }
        catch (Exception e) {
            logger.error("Проблема при получение пользователя: " + e.getMessage());
            throw new CanNotMakeExecution("\nПроблема при получение пользователя: " + e.getMessage());
        }
    }
}
