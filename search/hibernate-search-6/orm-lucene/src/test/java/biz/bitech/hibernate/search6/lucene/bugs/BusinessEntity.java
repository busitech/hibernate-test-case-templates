package biz.bitech.hibernate.search6.lucene.bugs;

import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

@MappedSuperclass
public class BusinessEntity {

    private Long id;
    private int version;

    public BusinessEntity() {
        version = 0;
    }

    public BusinessEntity(Long id) {
        this.id = id;
        version = 0;
    }

    @Id
    @DocumentId
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Version
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
