/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.incodehq.amberg.vshcolab.modules.work.dom.impl.measurement;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.incodehq.amberg.vshcolab.modules.work.dom.impl.norm.Norm;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.execution.Durchfuehrung;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.util.ObjectContracts;

import lombok.Getter;
import lombok.Setter;

/**
 * The value of a measurement"
 */
@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "test"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
         column="id")
@javax.jdo.annotations.Version(
        strategy= VersionStrategy.DATE_TIME,
        column="version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByDurchfuehrung",
                value = "SELECT "
                        + "FROM org.incodehq.amberg.vshcolab.modules.work.dom.impl.measurement.Messwert "
                        + "WHERE durchfuehrung == :durchfuehrung ")
})
@javax.jdo.annotations.Unique(name="Messwert_durchfuehrung_norm_UNQ", members = {"durchfuehrung", "norm"})
@DomainObject(
        auditing = Auditing.ENABLED,
        publishing = Publishing.ENABLED
)
public class Messwert implements Comparable<Messwert> {

    //region > title
    public TranslatableString title() {
        return TranslatableString.tr("{durchfuehrung}: {norm}", "durchfuehrung", getDurchfuehrung(), "norm", this.getNorm());
    }
    //endregion

    //region > constructor
    public Messwert(final Durchfuehrung durchfuehrung, final Norm norm, final LocalDateTime measuredAt, final BigDecimal value) {
        this.durchfuehrung = durchfuehrung;
        this.norm = norm;
        this.measuredAt = measuredAt;
        this.value = value;
    }
    //endregion

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private Durchfuehrung durchfuehrung;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private Norm norm;

    @Column(allowsNull = "true")
    @Property()
    @Getter @Setter
    private BigDecimal value;

    @Column(allowsNull = "true")
    @Property()
    @Getter @Setter
    private LocalDateTime measuredAt;


    //region > toString, compareTo
    @Override
    public String toString() {
        return ObjectContracts.toString(this, "durchfuehrung", "norm");
    }

    @Override
    public int compareTo(final Messwert other) {
        return ObjectContracts.compare(this, other, "durchfuehrung", "norm");
    }

    //endregion

}