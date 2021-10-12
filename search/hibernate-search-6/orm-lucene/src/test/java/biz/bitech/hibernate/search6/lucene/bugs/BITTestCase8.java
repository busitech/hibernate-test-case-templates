package biz.bitech.hibernate.search6.lucene.bugs;

import org.hibernate.jpa.test.BaseTestCase;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.bytecode.enhancement.EnhancementOptions;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(BytecodeEnhancerRunner.class)
@EnhancementOptions(lazyLoading = true, biDirectionalAssociationManagement = true)
public class BITTestCase8 extends BaseTestCase {

    @Test
    public void testYourBug() {
        {
            startTransaction();

            Manufacturer manufacturer = new Manufacturer(1L, "Motor Manufacturer");
            em.persist(manufacturer);

            Item item1 = new Item(1L, "Electric Motor");
            item1.setManufacturer(manufacturer);
            em.persist(item1);

            assertThat(item1.getManufacturer().getItems()).hasSize(1);

            Vendor vendor1 = new Vendor(1L, "Motor Distributor");
            em.persist(vendor1);

            endTransaction();
        }
        {
            startTransaction();

            Item item1 = em.find(Item.class, 1L);

            assertThat(item1.getVendorInfos()).hasSize(0);
            
            Vendor vendor1 = em.find(Vendor.class, 1L);

            ItemVendorInfo itemVendorInfo1 = new ItemVendorInfo(1L, item1, vendor1, new BigDecimal("1000"));
            em.persist(itemVendorInfo1);

            assertThat(item1.getVendorInfos()).hasSize(1);
            
            endTransaction();
        }

        {
            em = getOrCreateEntityManager();
            SearchSession searchSession = Search.session(em);

            List<Item> hits = searchSession.search( Item.class )
                    .where( f -> f.match().field( "vendorInfos.vendor.id" ).matching( 1L ) )
                    .fetchHits( 20 );

            assertThat( hits )
                    .hasSize( 1 )
                    .element( 0 ).extracting( Item::getId )
                    .isEqualTo( 1L );
            em.close();
        }

        {
            startTransaction();

            Item item1 = em.find(Item.class, 1L);
            item1.setName("5HP Electric Motor");

            item1 = em.merge(item1);

            assertThat(item1.getVendorInfos()).hasSize(1);

            endTransaction();
        }

        {
            em = getOrCreateEntityManager();
            SearchSession searchSession = Search.session(em);

            List<Item> hits = searchSession.search( Item.class )
                    .where( f -> f.match().field( "vendorInfos.vendor.id" ).matching( 1L ) )
                    .fetchHits( 20 );

            assertThat( hits )
                    .hasSize( 1 )
                    .element( 0 ).extracting( Item::getId )
                    .isEqualTo( 1L );
            em.close();
        }
    }
}
