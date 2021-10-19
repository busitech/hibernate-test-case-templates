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
@EnhancementOptions(lazyLoading = true, biDirectionalAssociationManagement = true, inlineDirtyChecking = true)
public class BITTestCase10 extends BaseTestCase {

    @Test
    public void testYourBug() {

        {
            startTransaction();

            Vendor vendor = new Vendor(1L, "Motor Distributor");
            persist(vendor);

            Manufacturer manufacturer = new Manufacturer(1L, "Motor Manufacturer");
            persist(manufacturer);

            for (long i = 10; i < 100; i++) {
                Item item = new Item(i, "New Item " + i);
                item.setManufacturer(manufacturer);
                em.persist(item);
            }
            endTransaction();
        }
        {
            startTransaction();

            Manufacturer manufacturer = new Manufacturer(1L, "");
            applyInterceptor(manufacturer);

            Vendor vendor = new Vendor(1L, "");
            applyInterceptor(vendor);
            
            Item item = new Item(1L, "New Item");
            item.setManufacturer(manufacturer);

            persist(item);

            ItemVendorInfo itemVendorInfo1 = new ItemVendorInfo(1L, item, vendor, new BigDecimal("2000"));
            persist(itemVendorInfo1);

            Set<ItemVendorInfo> vi = item.getVendorInfos();
            assertThat(vi).hasSize(1);

            assertThat(item.getManufacturer().getName()).matches("Motor Manufacturer");
            assertThat(item.getManufacturer().getItems()).hasSize(91);

            endTransaction();
        }

        {
            startTransaction();

            SearchSession searchSession = Search.session(em);

            List<Item> hits = searchSession.search( Item.class )
                    .where( f -> f.match().field( "vendorInfos.vendor.id" ).matching( 1L ) )
                    .fetchHits( 20 );

            System.out.println(hits.toString());

            assertThat( hits ).hasSize( 1 );

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
            }
        }

        {
            Item detachedItem;
            {
                startTransaction();
                detachedItem = em.find(Item.class, 1L);
                endTransaction();
            }
            {
                startTransaction();

                Item i = new Item(1L, "");

                Manufacturer manufacturer = new Manufacturer(1L, "");
                applyInterceptor(manufacturer);

                i.setManufacturer(manufacturer); // simulate detached manufacturer
                i.setName("Item 1 New Name");
                i.setVersion(detachedItem.getVersion());

                int version = i.getVersion();
                i = merge(i);

                assertThat(i.getVendorInfos()).hasSize(1);

                manufacturer = i.getManufacturer();
                assertThat(manufacturer.getName()).matches("Motor Manufacturer");
                assertThat(manufacturer.getItems()).hasSize(91);

                assertThat(i.getVersion()).isEqualTo(version+1);
                em.refresh(i);
                assertThat(i.getName()).matches("Item 1 New Name");
                assertThat(i.getVersion()).isEqualTo(version+1);

                endTransaction();
            }
        }

        {
            startTransaction();

            SearchSession searchSession = Search.session(em);

            List<Item> hits = searchSession.search( Item.class )
                    .where( f -> f.match().field( "vendorInfos.vendor.id" ).matching( 1L ) )
                    .fetchHits( 20 );

            assertThat( hits ).hasSize( 1 );

            endTransaction();
        }

        {
            startTransaction();

            Manufacturer manufacturer1 = em.find(Manufacturer.class, 1L);
            Item item2 = new Item(2L, "New Item 2");
            item2.setManufacturer(manufacturer1);
            item2.setVersion(0);

            persist(item2);

            Vendor vendor1 = em.find(Vendor.class, 1L);
            ItemVendorInfo itemVendorInfo1 = new ItemVendorInfo(2L, item2, vendor1, new BigDecimal("2000"));
            persist(itemVendorInfo1);

            Set<ItemVendorInfo> vi = item2.getVendorInfos();
            assertThat(vi).hasSize(1);

            endTransaction();
        }

        {
            startTransaction();

            SearchSession searchSession = Search.session(em);

            List<Item> hits = searchSession.search( Item.class )
                    .where( f -> f.match().field( "vendorInfos.vendor.id" ).matching( 1L ) )
                    .fetchHits( 20 );

            assertThat( hits ).hasSize( 2 );

            endTransaction();
        }

        {
            startTransaction();

            Manufacturer manufacturer1 = new Manufacturer(1L, "");
            applyInterceptor(manufacturer1);
            Item item3 = new Item(3L, "New Item 3");
            item3.setManufacturer(manufacturer1);
            item3.setVersion(0);

            persist(item3);

            Vendor vendor1 = new Vendor(1L, "");
            applyInterceptor(vendor1);
            ItemVendorInfo itemVendorInfo1 = new ItemVendorInfo(3L, item3, vendor1, new BigDecimal("2000"));
            persist(itemVendorInfo1);

            Set<ItemVendorInfo> vi = item3.getVendorInfos();
            assertThat(vi).hasSize(1);

            assertThat(item3.getManufacturer().getName()).matches("Motor Manufacturer");
            assertThat(item3.getManufacturer().getItems()).hasSize(93);

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

        {
            startTransaction();

            SalesOrder salesOrder = new SalesOrder(1L);
            persist(salesOrder);

            Item item1 = new Item(1L, "");
            item1.setVersion(1);
            applyInterceptor(item1);

            SalesOrderDetail salesOrderDetail = new SalesOrderDetail(1L, salesOrder, item1);
            persist(salesOrderDetail);
            
            assertThat(item1.getSalesOrderDetails()).hasSize(1);

            endTransaction();
        }

        {
            startTransaction();

            SalesOrderDetail salesOrderDetail = new SalesOrderDetail(1L);
            applyInterceptor(salesOrderDetail);

            SerialNumber serialNumber = new SerialNumber(1L, "1", salesOrderDetail);
            persist(serialNumber);

            endTransaction();
        }

        {
            startTransaction();

            SearchSession searchSession = Search.session(em);

            List<SalesOrder> hits = searchSession.search( SalesOrder.class )
                    .where( f -> f.match().field( "salesOrderDetails.serialNumbers.serialNumber" ).matching( "1" ) )
                    .fetchHits( 20 );

            assertThat( hits ).hasSize( 1 );

            endTransaction();
        }

        {
            startTransaction();

            SerialNumber serialNumber = em.find(SerialNumber.class, 1L);
            em.remove(serialNumber);

            endTransaction();
        }

        {
            startTransaction();

            SearchSession searchSession = Search.session(em);

            List<SalesOrder> hits = searchSession.search( SalesOrder.class )
                    .where( f -> f.match().field("salesOrderDetails.serialNumbers.serialNumber" ).matching("1"))
                    .fetchHits( 20 );

            assertThat( hits ).hasSize( 0 );

            endTransaction();
        }

        {
            {
                startTransaction();

                Item item1 = new Item(1L, "");
                item1.setVersion(1);
                applyInterceptor(item1);

                Vendor vendor1 = new Vendor(1L, "");
                applyInterceptor(vendor1);

                ItemVendorInfo itemVendorInfo1 = new ItemVendorInfo(4L, item1, vendor1, new BigDecimal("2000"));
                persist(itemVendorInfo1);

                Set<ItemVendorInfo> vi = item1.getVendorInfos();
                assertThat(vi).hasSize(2);

                endTransaction();
            }
        }

        {
            startTransaction();

            Item item10 = new Item(10L, "");
            applyInterceptor(item10);

            Vendor vendor1 = new Vendor(1L, "");
            applyInterceptor(vendor1);

            ItemVendorInfo itemVendorInfo1 = new ItemVendorInfo(5L, item10, vendor1, new BigDecimal("2000"));
            persist(itemVendorInfo1);

            endTransaction();
        }

        {
            startTransaction();
            SearchSession searchSession = Search.session(em);
            List<Item> hits = searchSession.search(Item.class)
                    .where(f -> f.match().field("vendorInfos.vendor.id").matching(1L))
                    .fetchHits(20);
            assertThat(hits).hasSize(4);
            endTransaction();
        }

        {
            startTransaction();
            ItemVendorInfo itemVendorInfo1 = em.find(ItemVendorInfo.class, 5L);
            em.remove(itemVendorInfo1);
            endTransaction();
        }

        {
            startTransaction();
            SearchSession searchSession = Search.session(em);
            List<Item> hits = searchSession.search(Item.class)
                    .where(f -> f.match().field("vendorInfos.vendor.id").matching(1L))
                    .fetchHits(20);
            assertThat(hits).hasSize(3);
            endTransaction();
        }

        {
            startTransaction();

            Item i = new Item(11L, "");

            Manufacturer manufacturer = new Manufacturer(1L, "");
            applyInterceptor(manufacturer);

            i.setManufacturer(manufacturer); // simulate detached manufacturer
            i.setName("Item 10 Test update with lazy init collection");

            i = merge(i);

            Set<ItemText> list = i.getItemTexts();

            endTransaction();
        }
    }
}

