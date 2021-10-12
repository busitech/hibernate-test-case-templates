package biz.bitech.hibernate.search6.lucene.bugs;

import org.hibernate.LazyInitializationException;
import org.hibernate.jpa.test.BaseTestCase;
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
public class BITTestCase10 extends BaseTestCase {

    @Test
    public void testYourBug() {

        {
            startTransaction();

            Vendor vendor = new Vendor(1L, "Motor Distributor");
            em.persist(vendor);

            Manufacturer manufacturer = new Manufacturer(1L, "Motor Manufacturer");
            em.persist(manufacturer);
            
            Item item = new Item(1L, "New Item");
            item.setManufacturer(manufacturer);
            item.setVersion(0);

            item = applyInterception(item);

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

                Item i = new Item();
                i.setId(1L);
                i.setManufacturer(detachedItem.getManufacturer());
                i.setName(detachedItem.getName());
                i.setVersion(detachedItem.getVersion());

                i = applyInterception(i);

                i = em.merge(i);
                assertThat(i.getVendorInfos()).hasSize(1);
                endTransaction();
            }
        }
        {
            startTransaction();

            Manufacturer manufacturer1 = em.find(Manufacturer.class, 1L);
            Item item2 = new Item(2L, "New Item 2");
            item2.setManufacturer(manufacturer1);
            item2.setVersion(0);

            item2 = applyInterception(item2);

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

            Manufacturer manufacturer1 = em.find(Manufacturer.class, 1L);
            Item item3 = new Item(3L, "New Item 3");
            item3.setManufacturer(manufacturer1);
            item3.setVersion(0);

            item3 = applyInterception(item3);

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

