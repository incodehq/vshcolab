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
package org.incodehq.amberg.vshcolab.modules.work.dom.impl.projekt;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.util.ObjectContracts;

import lombok.Getter;
import lombok.Setter;

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
                name = "findByLeiter", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incodehq.amberg.vshcolab.modules.work.dom.impl.projekt.Projekt "
                        + "WHERE leiter.indexOf(:leiter) >= 0 "),
        @javax.jdo.annotations.Query(
                name = "findByNummer", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incodehq.amberg.vshcolab.modules.work.dom.impl.projekt.Projekt "
                        + "WHERE nummer == :nummer ")
})
@javax.jdo.annotations.Unique(name="Projekt_nummer_UNQ", members = {"nummer"})
@DomainObject(
        auditing = Auditing.ENABLED,
        publishing = Publishing.ENABLED
)
public class Projekt implements Comparable<Projekt> {

    //region > title
    public TranslatableString title() {
        return TranslatableString.tr("{leiter}", "leiter", getLeiter());
    }
    //endregion

    //region > constructor
    public Projekt(
            final String kostenTraeger,
            final String leiter,
            final String nummer,
            final String auftraggeber,
            final String rechnung,
            final String versandRechnung,
            final String bericht) {
        this.kostenTraeger = kostenTraeger;
        this.leiter = leiter;
        this.nummer = nummer;
        this.auftraggeber = auftraggeber;
        this.rechnung = rechnung;
        this.versandRechnung = versandRechnung;
        this.bericht = bericht;
    }
    //endregion

    @Column(allowsNull = "false", length = 255)
    @Property()
    @Getter @Setter
    private String kostenTraeger;

    @Column(allowsNull = "false", length = 255)
    @Property()
    @Getter @Setter
    private String leiter;

    @Column(allowsNull = "false", length = 255)
    @Property()
    @Getter @Setter
    private String nummer;

    @Column(allowsNull = "false", length = 255)
    @Property()
    @Getter @Setter
    private String auftraggeber;

    @Column(allowsNull = "true", length = 255)
    @Property()
    @Getter @Setter
    private String rechnung;

    @Column(allowsNull = "true", length = 255)
    @Property()
    @Getter @Setter
    private String versandRechnung;

    @Column(allowsNull = "true", length = 255)
    @Property()
    @Getter @Setter
    private String bericht;

    //region > toString, compareTo
    @Override
    public String toString() {
        return ObjectContracts.toString(this, "nummer");
    }

    @Override
    public int compareTo(final Projekt other) {
        return ObjectContracts.compare(this, other, "nummer");
    }

    //endregion

}