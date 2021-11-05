package biz.bitech.hibernate.search6.lucene.bugs;

import org.hibernate.annotations.Cascade;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Indexed
public class PurchaseOrder extends BusinessEntity {
    public PurchaseOrder() {

    }

    public PurchaseOrder(long id) {
        super(id);
    }

    private String poDescription;
    private Set<PurchaseOrderDetail> poDetails;

    public String getPoDescription() {
        return poDescription;
    }

    public void setPoDescription(String poDescription) {
        this.poDescription = poDescription;
    }

    @OneToMany(mappedBy = "po")
    @Cascade({org.hibernate.annotations.CascadeType.DETACH})
    @IndexedEmbedded(includePaths = {"serialNumbers.serialNumber", "vendorRmaNumber"})
    public Set<PurchaseOrderDetail> getPoDetails() {
        return poDetails;
    }

    public void setPoDetails(Set<PurchaseOrderDetail> poDetails) {
        this.poDetails = poDetails;
    }
}
