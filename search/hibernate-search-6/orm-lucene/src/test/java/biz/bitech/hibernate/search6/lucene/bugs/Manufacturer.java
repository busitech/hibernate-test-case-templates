package biz.bitech.hibernate.search6.lucene.bugs;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;

import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Indexed
public class Manufacturer extends BusinessEntity {

    private String name;

    public Manufacturer() {

    }

    public Manufacturer(Long id, String name) {
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

    private Collection<Item> items = new ArrayList<>();

    @OneToMany(mappedBy = "manufacturer")
    public Collection<Item> getItems() {
        return items;
    }

    public void setItems(Collection<Item> items) {
        this.items = items;
    }
}
