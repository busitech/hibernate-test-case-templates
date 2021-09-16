package biz.bitech.hibernate.search5.lucene.bugs;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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

    @Field
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

}
