package org.hibernate.jpa.test;

import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.spi.*;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.mapper.orm.work.SearchIndexingPlan;
import org.hibernate.testing.junit4.CustomRunner;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.jboss.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RunWith( CustomRunner.class )
public abstract class BaseTestCase  {

	protected final Logger log = Logger.getLogger( getClass() );

	private StandardServiceRegistryImpl serviceRegistry;
	private SessionFactoryImplementor entityManagerFactory;

	protected EntityManager em;
	private ArrayList<EntityManager> isolatedEms = new ArrayList<EntityManager>();
	EntityTransaction userTransaction;

	@Before
	@SuppressWarnings( {"UnusedDeclaration"})
	public void buildEntityManagerFactory() {
		log.trace( "Building EntityManagerFactory" );

		entityManagerFactory = Persistence.createEntityManagerFactory("templatePU", getConfig()).unwrap(SessionFactoryImplementor.class);
		EntityManager em = entityManagerFactory.createEntityManager();

		serviceRegistry = (StandardServiceRegistryImpl) entityManagerFactory.getServiceRegistry().getParentServiceRegistry();

	}

	protected Map getConfig() {
		Map<Object, Object> config = Environment.getProperties();

		Collection<ClassLoader> list = new ArrayList<>();
		list.add(getClass().getClassLoader());
		config.put( AvailableSettings.CLASSLOADERS, list );
		return config;
	}

	@After
	@SuppressWarnings( {"UnusedDeclaration"})
	public void releaseResources() {
		try {
			releaseUnclosedEntityManagers();
		}
		finally {
			if ( entityManagerFactory != null && entityManagerFactory.isOpen()) {
				entityManagerFactory.close();
			}
		}
	}

	private void releaseUnclosedEntityManagers() {
		releaseUnclosedEntityManager( this.em );

		for ( EntityManager isolatedEm : isolatedEms ) {
			releaseUnclosedEntityManager( isolatedEm );
		}
	}

	private void releaseUnclosedEntityManager(EntityManager em) {
		if ( em == null ) {
			return;
		}
		if ( !em.isOpen() ) {
			return;
		}

		if ( em.getTransaction().isActive() ) {
			em.getTransaction().rollback();
            log.warn("You left an open transaction! Fix your test case. For now, we are closing it for you.");
		}
		if ( em.isOpen() ) {
			em.close();
            log.warn("The EntityManager is not closed. Closing it.");
		}
	}

	protected Session getDelegate() {
		Session s = (Session) em.getDelegate();
		return s;
	}

	protected EntityManager getOrCreateEntityManager() {
		if ( em == null || !em.isOpen() ) {
			em = entityManagerFactory.createEntityManager();
		}
		return em;
	}

	protected void startTransaction() {
		em = getOrCreateEntityManager();
		userTransaction = em.getTransaction();
		userTransaction.begin();
	}

	protected void endTransaction() {
		userTransaction.commit();
		em.close();
	}

	protected void index(Object object) {
		SearchSession searchSession = Search.session( em );
		SearchIndexingPlan indexingPlan = searchSession.indexingPlan();

		try {
			indexingPlan.addOrUpdate( object );
		}
		catch (RuntimeException e) {
			System.out.println("[GenericFlexBean] Indexing issue: " + e.getMessage());
		}
	}

	protected <T> T createUninitializedProxyT(Class<T> entity, Serializable key) {
		SharedSessionContractImplementor s = getDelegate().unwrap(SharedSessionContractImplementor.class);
		MetamodelImplementor entityMetamodelImpl = s.getFactory().getMetamodel();
		final EntityPersister persister = entityMetamodelImpl.entityPersister(entity.getName());
		final EntityKey keyToLoad = s.generateEntityKey(key, persister );
		PersistentAttributeInterceptable persistentAttributeInterceptable = persister.getBytecodeEnhancementMetadata().createEnhancedProxy( keyToLoad, true, s );
		return (T) persistentAttributeInterceptable;
	}

	protected  <T extends BITEntity> T applyInterceptor(T entity) {
		return applyInterceptor(entity, true);
	}

	private <T extends BITEntity> T applyInterceptor(T entity, boolean uninitialized) {
		SharedSessionContractImplementor s = getDelegate().unwrap(SharedSessionContractImplementor.class);
		MetamodelImplementor entityMetamodelImpl = s.getFactory().getMetamodel();
		final EntityPersister persister = entityMetamodelImpl.entityPersister(entity.getClass().getName());
		final PersistenceContext persistenceContext = s.getPersistenceContext();
		final EntityKey entityKey = s.generateEntityKey(entity.getId(), persister );
		
		persistenceContext.addEnhancedProxy(entityKey, (PersistentAttributeInterceptable) entity);
		if (uninitialized) {
			persistenceContext.addEntry(entity, Status.MANAGED, null, null, entity.getId(), entity.getVersion(), LockMode.NONE, true, persister, false);
		}
		persister.getBytecodeEnhancementMetadata().injectEnhancedEntityAsProxyInterceptor(entity, entityKey, s);
		return entity;
	}

	protected <T extends BITEntity> T applyInterceptorBeforeMerge(T entity) {
		SharedSessionContractImplementor s = getDelegate().unwrap(SharedSessionContractImplementor.class);
		MetamodelImplementor entityMetamodelImpl = s.getFactory().getMetamodel();
		final EntityPersister persister = entityMetamodelImpl.entityPersister(entity.getClass().getName());
		EntityMetamodel entityMetamodel =  persister.getEntityMetamodel();
		PersistentAttributeInterceptor interceptor = new LazyAttributeLoadingInterceptor(
				entityMetamodel.getName(),
				entity.getId(),
				entityMetamodel.getBytecodeEnhancementMetadata()
						.getLazyAttributesMetadata()
						.getLazyAttributeNames(),
				s
		);
		( (PersistentAttributeInterceptable) entity ).$$_hibernate_setInterceptor(interceptor);
		return entity;
	}

	protected <T extends BITEntity> void persist(T entity) {
		em.persist(entity);
		applyInterceptor(entity, false);
		em.flush();
	}
}
