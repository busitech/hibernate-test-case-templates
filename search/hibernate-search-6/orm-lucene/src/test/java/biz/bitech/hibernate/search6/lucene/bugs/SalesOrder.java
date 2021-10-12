package biz.bitech.hibernate.search6.lucene.bugs;


import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Indexed
public class SalesOrder extends BusinessEntity {

    public SalesOrder() {
    }

    public SalesOrder(Long id) {
        super(id);
    }

    private Set<SalesOrderDetail> salesOrderDetails = new HashSet<SalesOrderDetail>(0);

    @OneToMany(mappedBy = "salesOrder")
    @IndexedEmbedded(includePaths = {"serialNumbers.serialNumber"})
    public Set<SalesOrderDetail> getSalesOrderDetails() {
        return this.salesOrderDetails;
    }

    public void setSalesOrderDetails(Set<SalesOrderDetail> SalesOrderDetails) {
        this.salesOrderDetails = SalesOrderDetails;
    }

}
