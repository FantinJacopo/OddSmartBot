package com.database.objects;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = (SessionFactory) new Configuration().configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Bookmaker.class)
                    .addAnnotatedClass(League.class)
                    .addAnnotatedClass(Endpoint.class)
                    .addAnnotatedClass(ArbitrageOpportunity.class)
                    .addAnnotatedClass(Match.class)
                    .addAnnotatedClass(Odd.class)
                    .addAnnotatedClass(OddType.class)
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(UserFavorites.class)
                    .addAnnotatedClass(UserRequest.class)
                    .buildSessionFactory();

        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}