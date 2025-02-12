package biz.bitech.hibernate.search6.lucene.bugs;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class SerialNumber extends BusinessEntity
{
    private Item item;
    private String serialNumber;
    private SalesOrderDetail salesOrderDetail;
    private PurchaseOrderDetail purchaseOrderDetail;

    public SerialNumber() {

    }

    public SerialNumber(Long id) {
        super(id);
    }

    public SerialNumber(Long id, String serialNumber, SalesOrderDetail salesOrderDetail) {
        super(id);
        this.serialNumber = serialNumber;
        this.salesOrderDetail = salesOrderDetail;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @FullTextField
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public SalesOrderDetail getSalesOrderDetail() {
        return salesOrderDetail;
    }

    public void setSalesOrderDetail(SalesOrderDetail salesOrderDetail) {
        this.salesOrderDetail = salesOrderDetail;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public PurchaseOrderDetail getPurchaseOrderDetail() {
        return purchaseOrderDetail;
    }

    public void setPurchaseOrderDetail(PurchaseOrderDetail purchaseOrderDetail) {
        this.purchaseOrderDetail = purchaseOrderDetail;
    }

}
