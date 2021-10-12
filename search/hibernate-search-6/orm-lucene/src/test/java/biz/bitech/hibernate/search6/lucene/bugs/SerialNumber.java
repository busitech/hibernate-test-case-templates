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

    public SerialNumber() {

    }

    public SerialNumber(Long id, String serialNumber) {
        super(id);
        this.serialNumber = serialNumber;
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

}
