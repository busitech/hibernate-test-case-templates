package biz.bitech.hibernate.search6.lucene.bugs;

import javax.persistence.*;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import java.util.Set;

@Entity
@Indexed
public class Item extends BusinessEntity{

	private String name;
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

}
