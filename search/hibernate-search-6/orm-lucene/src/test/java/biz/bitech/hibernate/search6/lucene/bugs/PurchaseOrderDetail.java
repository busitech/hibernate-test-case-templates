package biz.bitech.hibernate.search6.lucene.bugs;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
public class PurchaseOrderDetail extends BusinessEntity {
    private PurchaseOrder po;
    private Item item;
    private String vendorRmaNumber;
    private Set<SerialNumber> serialNumbers;

    public PurchaseOrderDetail() {

    }

    public PurchaseOrderDetail(long id) {
        super(id);
    }

    public PurchaseOrderDetail(Long id, PurchaseOrder po, Item item) {
        super(id);
        this.po = po;
        this.item = item;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @IndexedEmbedded(includeEmbeddedObjectId = true)
    public PurchaseOrder getPo() {
        return this.po;
    }

    public void setPo(PurchaseOrder Po) {
        this.po = Po;
    }


    @OneToMany(mappedBy = "purchaseOrderDetail")
    @IndexedEmbedded(includeDepth = 1)
    public Set<SerialNumber> getSerialNumbers() {
        return serialNumbers;
    }

    public void setSerialNumbers(Set<SerialNumber> serialNumbers) {
        this.serialNumbers = serialNumbers;
    }

    @FullTextField
    public String getVendorRmaNumber() {
        return vendorRmaNumber;
    }

    public void setVendorRmaNumber(String vendorRmaNumber) {
        this.vendorRmaNumber = vendorRmaNumber;
    }
}
