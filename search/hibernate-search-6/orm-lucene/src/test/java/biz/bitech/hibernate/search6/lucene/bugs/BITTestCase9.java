package biz.bitech.hibernate.search6.lucene.bugs;

import org.hibernate.LazyInitializationException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.jpa.test.BaseTestCase;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.bytecode.enhancement.EnhancementOptions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(BytecodeEnhancerRunner.class)
@EnhancementOptions(lazyLoading = true, biDirectionalAssociationManagement = false)
public class BITTestCase9 extends BaseTestCase {

    @Test
    public void testYourBug() {

        {
            startTransaction();
            SharedSessionContractImplementor s = getDelegate().unwrap(SharedSessionContractImplementor.class);
            MetamodelImplementor entityMetamodel = s.getFactory().getMetamodel();
            final EntityPersister persister = entityMetamodel.entityPersister(Item.class.getName());

            Vendor vendor = new Vendor(1L, "Motor Distributor");
            em.persist(vendor);

            Manufacturer manufacturer = new Manufacturer(1L, "Motor Manufacturer");
            em.persist(manufacturer);

            Item item = (Item) persister.instantiate(1L, s);
            item.setManufacturer(manufacturer);
            item.setName("New Item");
            item.setVersion(0);
            em.persist(item);
            
            ItemVendorInfo itemVendorInfo1 = new ItemVendorInfo(1L, item, vendor, new BigDecimal("2000"));
            em.persist(itemVendorInfo1);

            em.flush();

            Set<ItemVendorInfo> vi = item.getVendorInfos();
            assertThat(vi).hasSize(1);

            endTransaction();
        }


        {
            Item detachedItem;
            {
                startTransaction();
                detachedItem = em.find(Item.class, 1L);
                endTransaction();
            }
            {

                Assert.assertThrows(
                        "Expected getVendorInfos() to throw, but it did not.",
                        LazyInitializationException.class,
                        () -> detachedItem.getVendorInfos()
                );

                startTransaction();
                SharedSessionContractImplementor s = getDelegate().unwrap(SharedSessionContractImplementor.class);
                MetamodelImplementor entityMetamodel = s.getFactory().getMetamodel();
                final EntityPersister persister = entityMetamodel.entityPersister(Item.class.getName());

                Item i = (Item) persister.instantiate(1L, s);
                i.setManufacturer(detachedItem.getManufacturer());
                i.setName(detachedItem.getName());
                i.setVersion(detachedItem.getVersion());
                i = em.merge(i);
                assertThat(i.getVendorInfos()).hasSize(1);
                endTransaction();
            }
        }
        {
            startTransaction();
            SharedSessionContractImplementor s = getDelegate().unwrap(SharedSessionContractImplementor.class);
            MetamodelImplementor entityMetamodel = s.getFactory().getMetamodel();
            final EntityPersister persister = entityMetamodel.entityPersister(Item.class.getName());

            Manufacturer manufacturer1 = em.find(Manufacturer.class, 1L);
            Item item2 = (Item) persister.instantiate(2L, s);
            item2.setManufacturer(manufacturer1);
            item2.setName("New Item 2");
            item2.setVersion(0);
            em.persist(item2);

            Vendor vendor1 = em.find(Vendor.class, 1L);
            ItemVendorInfo itemVendorInfo1 = new ItemVendorInfo(2L, item2, vendor1, new BigDecimal("2000"));
            em.persist(itemVendorInfo1);

            em.flush();

            Set<ItemVendorInfo> vi = item2.getVendorInfos();
            assertThat(vi).hasSize(1);

            endTransaction();
        }
        {
            startTransaction();
            SharedSessionContractImplementor s = getDelegate().unwrap(SharedSessionContractImplementor.class);
            MetamodelImplementor entityMetamodel = s.getFactory().getMetamodel();
            final EntityPersister persister = entityMetamodel.entityPersister(Item.class.getName());
            
            Manufacturer manufacturer1 = em.find(Manufacturer.class, 1L);
            Item item3 = (Item) persister.instantiate(3L, s);
            item3.setManufacturer(manufacturer1);
            item3.setName("New Item 3");
            item3.setVersion(0);
            em.persist(item3);

            Vendor vendor1 = em.find(Vendor.class, 1L);
            ItemVendorInfo itemVendorInfo1 = new ItemVendorInfo(3L, item3, vendor1, new BigDecimal("2000"));
            em.persist(itemVendorInfo1);

            em.flush();

            Set<ItemVendorInfo> vi = item3.getVendorInfos();
            assertThat(vi).hasSize(1);

            endTransaction();
        }
        {
            startTransaction();

            SearchSession searchSession = Search.session(em);

            List<Item> hits = searchSession.search( Item.class )
                    .where( f -> f.match().field( "vendorInfos.vendor.id" ).matching( 1L ) )
                    .fetchHits( 20 );

            assertThat( hits ).hasSize( 3 );

            endTransaction();
        }
    }
}
