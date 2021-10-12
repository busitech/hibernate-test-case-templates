package biz.bitech.hibernate.search6.lucene.bugs;

import javax.persistence.*;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import java.util.Collection;
import java.util.Set;

@Entity
@Indexed
public class Item extends BusinessEntity{

	private String name;
	private Manufacturer manufacturer;
	private Set<ItemVendorInfo> vendorInfos;

	protected Item() {
	}

	public Item(Long id, String name) {
		super(id);
		this.name = name;
	}

	@FullTextField(analyzer = "nameAnalyzer")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(mappedBy = "item", targetEntity = ItemVendorInfo.class)
	@IndexedEmbedded(includeEmbeddedObjectId = true)
	public Set<ItemVendorInfo> getVendorInfos() {
		return this.vendorInfos;
	}

	public void setVendorInfos(Set<ItemVendorInfo> vendorInfo) {
		this.vendorInfos = vendorInfo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@IndexedEmbedded
	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}

	private Collection<SalesOrderDetail> salesOrderDetails;

	@OneToMany(mappedBy = "item")
	public Collection<SalesOrderDetail> getSalesOrderDetails() {
		return salesOrderDetails;
	}

	public void setSalesOrderDetails(Collection<SalesOrderDetail> salesOrderDetails) {
		this.salesOrderDetails = salesOrderDetails;
	}
}
