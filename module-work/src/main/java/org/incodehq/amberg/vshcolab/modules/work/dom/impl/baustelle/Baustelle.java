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
package org.incodehq.amberg.vshcolab.modules.work.dom.impl.baustelle;

import java.util.List;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.incodehq.amberg.vshcolab.modules.work.dom.WorkModuleDomSubmodule;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.kunde.Kunde;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.order.Auftrag;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.order.AuftragRepository;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.CommandReification;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.util.ObjectContracts;

import org.isisaddons.wicket.gmap3.cpt.applib.Locatable;
import org.isisaddons.wicket.gmap3.cpt.applib.Location;
import org.isisaddons.wicket.gmap3.cpt.service.LocationLookupService;

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
                name = "findByName",
                value = "SELECT "
                        + "FROM org.incodehq.amberg.vshcolab.modules.work.dom.impl.baustelle.Baustelle "
                        + "WHERE name.indexOf(:name) >= 0 "),
        @javax.jdo.annotations.Query(
                name = "findByKunde",
                value = "SELECT "
                        + "FROM org.incodehq.amberg.vshcolab.modules.work.dom.impl.baustelle.Baustelle "
                        + "WHERE kunde == :kunde ")
})
@DomainObject(
        auditing = Auditing.ENABLED,
        publishing = Publishing.ENABLED
)
public class Baustelle implements Comparable<Baustelle>, Locatable{

    //region > title
    public TranslatableString title() {
        return TranslatableString.tr("{name}", "name", getName());
    }
    //endregion

    //region > constructor
    public Baustelle(final String name, final Kunde kunde, final String adresse) {
        setName(name);
        setKunde(kunde);
        setAddresse(adresse);
    }
    //endregion


    //region > adresse
    @Getter @Setter
    @Column(allowsNull = "false")
    public String addresse;

    @Persistent
    @Property(editing = Editing.DISABLED, optionality = Optionality.OPTIONAL, hidden = Where.ALL_TABLES)
    @Getter @Setter
    private Location position;

    @Action(semantics = SemanticsOf.IDEMPOTENT)

    public Baustelle lokalisieren(
            final @ParameterLayout(describedAs = "Example: Herengracht 469, Amsterdam, NL") String addresse) {
        setPosition(locationLookupService.lookup(addresse));
        setAddresse(addresse);
        return this;
    }

    public boolean hideLokalisieren() {
        return locationLookupService == null;
    }


    @Override
    @Programmatic
    public Location getLocation() {
        return getPosition();
    }

    @Inject
    private LocationLookupService locationLookupService;
    //endregion


    //region > name (editable property)
    public static class NameType {
        private NameType() {
        }

        public static class Meta {
            public static final int MAX_LEN = 40;

            private Meta() {
            }
        }

        public static class PropertyDomainEvent
                extends WorkModuleDomSubmodule.PropertyDomainEvent<Baustelle, String> { }
    }


    @javax.jdo.annotations.Column(allowsNull = "false", length = NameType.Meta.MAX_LEN)
    @Property(
            editing = Editing.ENABLED,
            domainEvent = NameType.PropertyDomainEvent.class
    )
    @Getter @Setter
    private String name;

    // endregion

    //region > kunde (property)
    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private Kunde kunde;
    //endregion

    //region > notes (editable property)
    public static class NotesType {
        private NotesType() {
        }

        public static class Meta {
            public static final int MAX_LEN = 4000;

            private Meta() {
            }
        }

        public static class PropertyDomainEvent
                extends WorkModuleDomSubmodule.PropertyDomainEvent<Baustelle, String> { }
    }


    @javax.jdo.annotations.Column(
            allowsNull = "true",
            length = NotesType.Meta.MAX_LEN
    )
    @Property(
            command = CommandReification.ENABLED,
            publishing = Publishing.ENABLED,
            domainEvent = NotesType.PropertyDomainEvent.class
    )
    @Getter @Setter
    private String notes;
    //endregion

    //region > auftragen (derived collection)
    @Mixin(method="coll")
    public static class auftraege {
        private final Baustelle baustelle;
        public auftraege(final Baustelle baustelle) {
            this.baustelle = baustelle;
        }
        public static class DomainEvent extends ActionDomainEvent<Baustelle> {
        }
        @Action(semantics = SemanticsOf.SAFE, domainEvent = DomainEvent.class)
        @ActionLayout(contributed=Contributed.AS_ASSOCIATION)
        public List<Auftrag> coll() {
            return auftragRepository.findByBaustelle(baustelle);
        }
        public boolean hideColl() {
            return false;
        }

        @javax.inject.Inject
        AuftragRepository auftragRepository;
    }
    //endregion

    //region > auftraegZufuegen (action)
    @Mixin(method="act")
    public static class auftraegZufuegen {
        private final Baustelle baustelle;
        public auftraegZufuegen(final Baustelle baustelle) {
            this.baustelle = baustelle;
        }
        public static class DomainEvent extends ActionDomainEvent<Baustelle> {
        }
        @Action(semantics = SemanticsOf.NON_IDEMPOTENT, domainEvent = DomainEvent.class)
        @ActionLayout(contributed=Contributed.AS_ACTION)
        public Auftrag act(final String name) {
            final Auftrag auftrag = auftragRepository.create(name,  baustelle);
            return auftrag;
        }
//        public boolean hideAct() {
//            return false;
//        }
//        public String disableAct() {
//            return null;
//        }
//        public String validate0Act(final String name) {
//            return null;
//        }
//        public List<String> choices0Act() {
//            return Collections.emptyList();
//        }
//        public String default0Act() {
//            return null;
//        }

        @javax.inject.Inject
        AuftragRepository auftragRepository;
    }
    //endregion

    //region > toString, compareTo
    @Override
    public String toString() {
        return ObjectContracts.toString(this, "name");
    }

    @Override
    public int compareTo(final Baustelle other) {
        return ObjectContracts.compare(this, other, "name");
    }

    //endregion


}