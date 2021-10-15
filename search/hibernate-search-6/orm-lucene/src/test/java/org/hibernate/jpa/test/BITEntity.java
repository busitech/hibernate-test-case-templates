package org.hibernate.jpa.test;

import javax.persistence.Version;

public interface BITEntity {
    public Long getId();
    public int getVersion();
}
