package biz.bitech.hibernate.search5.lucene.bugs;

import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Indexed
public class ItemVendorInfo {

    private Long id;
    private Item item;
    private Vendor vendor;
    private BigDecimal cost;

    protected ItemVendorInfo() {
    }

    public ItemVendorInfo(Long id, Item item, Vendor vendor, BigDecimal cost) {
        this.id = id;
        this.item = item;
        this.vendor = vendor;
        this.cost = cost;
    }

    @Id
    @DocumentId
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @ContainedIn
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
