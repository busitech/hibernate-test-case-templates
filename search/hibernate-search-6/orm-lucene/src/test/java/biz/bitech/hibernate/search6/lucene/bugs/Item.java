package biz.bitech.hibernate.search6.lucene.bugs;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import java.util.Set;

@Entity
@Indexed
public class Item {

	private Long id;
	private String name;

	private Set<ItemVendorInfo> vendorInfos;

	protected Item() {
	}

	public Item(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	@Id
	@DocumentId
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@FullTextField(analyzer = "nameAnalyzer")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(mappedBy = "item", targetEntity = ItemVendorInfo.class)
	@IndexedEmbedded(includePaths = {"vendor.id"})
	public Set<ItemVendorInfo> getVendorInfos() {
		return this.vendorInfos;
	}

	public void setVendorInfos(Set<ItemVendorInfo> vendorInfo) {
		this.vendorInfos = vendorInfo;
	}

}
