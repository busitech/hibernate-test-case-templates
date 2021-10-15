package biz.bitech.hibernate.search6.lucene.bugs;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import javax.persistence.*;
import java.util.Set;

@Entity
@Indexed
public class SalesOrderDetail extends BusinessEntity {

    Item item;
    SalesOrder salesOrder;

    public SalesOrderDetail() {

    }

    public SalesOrderDetail(Long id) {
        super(id);
    }

    public SalesOrderDetail(Long id, SalesOrder salesOrder, Item item) {
        super(id);
        this.salesOrder = salesOrder;
        this.item = item;
    }

    private Set<SerialNumber> serialNumbers;

    @ManyToOne(fetch = FetchType.LAZY)
    @IndexedEmbedded(includeEmbeddedObjectId = true)
    public SalesOrder getSalesOrder() {
        return this.salesOrder;
    }

    public void setSalesOrder(SalesOrder salesOrder) {
        this.salesOrder = salesOrder;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @IndexedEmbedded
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @OneToMany(mappedBy = "salesOrderDetail")
    @IndexedEmbedded(includeDepth = 1)
    public Set<SerialNumber> getSerialNumbers() {
        return serialNumbers;
    }

    public void setSerialNumbers(Set<SerialNumber> serialNumbers) {
        this.serialNumbers = serialNumbers;
    }

}
