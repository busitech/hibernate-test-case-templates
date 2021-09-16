package biz.bitech.hibernate.search6.lucene.bugs;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import java.util.Collection;

@Entity
@Indexed
public class Vendor {

    private Long id;
    private String name;
    private Collection<ItemVendorInfo> itemVendorInfos;

    protected Vendor() {
    }

    public Vendor(Long id, String name) {
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

    @OneToMany(mappedBy = "vendor")
    public Collection<ItemVendorInfo> getItemVendorInfos() {
        return itemVendorInfos;
    }

    public void setItemVendorInfos(Collection<ItemVendorInfo> itemVendorInfos) {
        this.itemVendorInfos = itemVendorInfos;
    }

    @GenericField(name = "id", projectable = Projectable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
    @IndexingDependency(derivedFrom = @ObjectPath(@PropertyValue(propertyName = "id")))
    @Transient
    public Long getIdForIndex() {
        return id;
    }
}
