package biz.bitech.hibernate.search6.lucene.bugs;

import javax.persistence.*;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

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
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @IndexedEmbedded
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
