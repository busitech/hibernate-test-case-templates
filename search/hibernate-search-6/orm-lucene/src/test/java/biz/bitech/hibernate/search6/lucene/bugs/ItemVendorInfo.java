package biz.bitech.hibernate.search6.lucene.bugs;

import javax.persistence.*;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import java.math.BigDecimal;

@Entity
@Indexed
public class ItemVendorInfo extends BusinessEntity {

    private Item item;
    private Vendor vendor;
    private BigDecimal cost;

    protected ItemVendorInfo() {
    }

    public ItemVendorInfo(Long id, Item item, Vendor vendor, BigDecimal cost) {
        super(id);
        this.item = item;
        this.vendor = vendor;
        this.cost = cost;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @IndexedEmbedded(includeEmbeddedObjectId = true)
    public Vendor getVendor() {
        return this.vendor;
    }

    public void setVendor(Vendor Vendor) {
        this.vendor = Vendor;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
}
