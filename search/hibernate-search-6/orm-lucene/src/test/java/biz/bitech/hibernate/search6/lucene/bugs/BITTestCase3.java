package biz.bitech.hibernate.search6.lucene.bugs;

import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.mapper.orm.work.SearchIndexingPlan;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class BITTestCase3 {

	@Test
	public void testYourBug() {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");
		EntityManager em = entityManagerFactory.createEntityManager();

		{
			EntityTransaction userTransaction = em.getTransaction();
			userTransaction.begin();

			Vendor vendor1 = new Vendor(1L, "Motor Company");
			Item item1 = new Item(1L, "Electric Motor");
			ItemVendorInfo itemVendorInfo1 = new ItemVendorInfo(1L, item1, vendor1, new BigDecimal("1000"));

			//User adds an item
			em.persist(item1);

			//User adds an vendor
			em.persist(vendor1);

			//User adds an item cost to purchase from vendor 1
			em.persist(itemVendorInfo1);

			// we would prefer this not to be the case, but
			// at this point, vendorInfos is still null after persist
			assertNull(item1.getVendorInfos());

			// these two lines prepares item1 for indexing
			// by adding a proxy to vendorInfos that indexing will use
			// to traverse the @OneToMany
			em.flush();
			em.refresh(item1);

			// vendorInfos is now alive
			assertNotNull(item1.getVendorInfos());

			// ****************************
			//  new for Hibernate Search 6
			// ****************************
			//
			// if the "other side" is updated here, it will
			// still be invisible in the index because the flush above caused
			// early buffering, and this statement won't cause another flush...
			//
			// since objects actually come in pre-populated from the client,
			// it is not feasible for this kind of logic to be hard coded
			// on the server side
			// item1.getVendorInfos().add(itemVendorInfo1);   // would have no effect

			// therefore, resubmit refreshed object for a second indexing pass
			SearchSession searchSession = Search.session(em);
			SearchIndexingPlan indexingPlan = searchSession.indexingPlan();
			indexingPlan.addOrUpdate( item1 );

			userTransaction.commit();
		}

		{
			SearchSession searchSession = Search.session(em);

			List<Item> hits = searchSession.search( Item.class )
					.where( f -> f.match().field( "vendorInfos.vendor.id" ).matching( 1L ) )
					.fetchHits( 20 );

			assertThat( hits )
					.hasSize( 1 )
					.element( 0 ).extracting( Item::getId )
					.isEqualTo( 1L );
		}

		{
			EntityTransaction userTransaction = em.getTransaction();
			userTransaction.begin();

			//user updates description of item1 by sending back a detached object
			Item item1 = new Item(1L, "5HP Electric Motor");  // simulate detached item with updated property

			item1 = em.merge(item1);

			// we would prefer this not to be the case, but
			// at this point, vendorInfos is still null
			assertNull(item1.getVendorInfos());

			// this early flush will cause next search to fail <-- BUG
			em.flush();

			// refresh item1 to add proxy to vendorInfos
			// that indexing will use to traverse the @OneToMany
			em.refresh(item1);

			// vendorInfos is now alive
			assertNotNull(item1.getVendorInfos());

			userTransaction.commit();
		}

		{
			SearchSession searchSession = Search.session(em);

			List<Item> hits = searchSession.search( Item.class )
					.where( f -> f.match().field( "vendorInfos.vendor.id" ).matching( 1L ) )
					.fetchHits( 20 );

			assertThat( hits )
					.hasSize( 1 )
					.element( 0 ).extracting( Item::getId )
					.isEqualTo( 1L );
		}
	}

}
