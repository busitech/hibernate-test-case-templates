package biz.bitech.hibernate.search6.lucene.bugs;

import javax.persistence.*;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import java.util.Collection;
import java.util.HashSet;
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

	private Set<SalesOrderDetail> salesOrderDetails = new HashSet<>();

	@OneToMany(mappedBy = "item")
	public Set<SalesOrderDetail> getSalesOrderDetails() {
		return salesOrderDetails;
	}

	private Set<ItemText> itemTexts;

	public void setSalesOrderDetails(Set<SalesOrderDetail> salesOrderDetails) {
		this.salesOrderDetails = salesOrderDetails;
	}

	@OneToMany(mappedBy = "item")
	public Set<ItemText> getItemTexts() {
		return this.itemTexts;
	}

	public void setItemTexts(Set<ItemText> itemTexts) {
		this.itemTexts = itemTexts;
	}
}
