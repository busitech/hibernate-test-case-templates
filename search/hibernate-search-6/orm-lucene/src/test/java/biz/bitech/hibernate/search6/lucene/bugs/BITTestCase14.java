package biz.bitech.hibernate.search6.lucene.bugs;

import org.hibernate.LazyInitializationException;
import org.hibernate.jpa.test.BaseTestCase;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class BITTestCase14 extends BaseTestCase {

    @Test
    public void testYourBug() {
        {
            startTransaction();

            Manufacturer manufacturer = new Manufacturer(1L, "Manufacturer");
            em.persist(manufacturer);

            Item item = new Item(1L, "Item");
            item.setManufacturer(manufacturer);

            em.persist(item);

            SalesOrder salesOrder = new SalesOrder(1L);
            em.persist(salesOrder);

            Item item1 = em.find(Item.class, 1L);
            SalesOrderDetail salesOrderDetail = new SalesOrderDetail(1L, salesOrder, item1);
            em.persist(salesOrderDetail);

            endTransaction();
        }

        {
            startTransaction();

            SalesOrder salesOrder = em.find(SalesOrder.class, 1L);

            for(SalesOrderDetail salesOrderDetail : salesOrder.getSalesOrderDetails()) {
                em.remove(salesOrderDetail);
            }

            endTransaction();
        }
    }
}

