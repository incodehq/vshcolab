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
package org.incodehq.amberg.vshcolab.modules.work.dom.impl.order;

import java.util.Collection;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.incodehq.amberg.vshcolab.modules.work.dom.WorkModuleDomSubmodule;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.baustelle.Baustelle;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.execution.Durchfuehrung;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.execution.DurchfuehrungRepository;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.procedure.Verfahren;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.procedure.VerfahrenRepository;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.CommandReification;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
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
                name = "findByName",
                value = "SELECT "
                        + "FROM org.incodehq.amberg.vshcolab.modules.work.dom.impl.order.Auftrag "
                        + "WHERE name.indexOf(:name) >= 0 "),
        @javax.jdo.annotations.Query(
                name = "findByBaustelle",
                value = "SELECT "
                        + "FROM org.incodehq.amberg.vshcolab.modules.work.dom.impl.order.Auftrag "
                        + "WHERE baustelle == :baustelle ")
})
@javax.jdo.annotations.Unique(name="Auftrag_baustelle_name_UNQ", members = {"baustelle", "name"})
@DomainObject(
        auditing = Auditing.ENABLED,
        publishing = Publishing.ENABLED
)
public class Auftrag implements Comparable<Auftrag> {

    //region > title
    public TranslatableString title() {
        return TranslatableString.tr("{name}", "name", getName());
    }
    //endregion

    //region > constructor
    public Auftrag(
            final String name,
//            final TestType testType,
            final Baustelle baustelle) {
        setName(name);
//        setTestType(testType);
        setBaustelle(baustelle);
    }
    //endregion

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private Baustelle baustelle;

    @Column(allowsNull = "true")
    @Property()
    @Getter @Setter
    private LocalDate when;

    //region > durchfuehrungZufuegen (action)
    @Mixin(method="act")
    public static class durchfuehrungZufuegen {
        private final Auftrag auftrag;
        public durchfuehrungZufuegen(final Auftrag auftrag) {
            this.auftrag = auftrag;
        }
        public static class DomainEvent extends ActionDomainEvent<Auftrag> {
        }
        @Action(semantics = SemanticsOf.NON_IDEMPOTENT, domainEvent = DomainEvent.class)
        @ActionLayout(contributed=Contributed.AS_ACTION, cssClassFa = "fa-plus", named = "Zufuegen")
        @MemberOrder(name = "pruefungen", sequence = "1")
        public Durchfuehrung act(final Integer number, final Verfahren verfahren, final int waitForDays) {
            final Durchfuehrung durchfuehrung = durchfuehrungRepository.create(number, verfahren, auftrag);
            durchfuehrung.setWartenBis(waitForDays);
            return durchfuehrung;
        }

        public Collection<Verfahren> autoComplete1Act(final String search) {
            return verfahrenRepository.search(search);
        }

        @javax.inject.Inject
        DurchfuehrungRepository durchfuehrungRepository;
        @javax.inject.Inject VerfahrenRepository verfahrenRepository;
    }
    //endregion

    //region > pruefungen (derived collection)
    @Mixin(method="coll")
    public static class pruefungen {
        private final Auftrag auftrag;
        public pruefungen(final Auftrag auftrag) {
            this.auftrag = auftrag;
        }
        public static class DomainEvent extends ActionDomainEvent<Auftrag> {
        }
        @Action(semantics = SemanticsOf.SAFE, domainEvent = DomainEvent.class)
        @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
        public List<Durchfuehrung> coll() {
            return durchfuehrungRepository.findByAuftrag(auftrag);
        }
        public boolean hideColl() {
            return false;
        }

        @javax.inject.Inject
        DurchfuehrungRepository durchfuehrungRepository;
    }
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
                extends WorkModuleDomSubmodule.PropertyDomainEvent<Auftrag, String> { }
    }


    @Column(allowsNull = "false", length = NameType.Meta.MAX_LEN)
    @Property(
            editing = Editing.ENABLED,
            domainEvent = NameType.PropertyDomainEvent.class
    )
    @Getter @Setter
    private String name;

    // endregion

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
                extends WorkModuleDomSubmodule.PropertyDomainEvent<Auftrag, String> { }
    }


    @Column(
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

    //region > toString, compareTo
    @Override
    public String toString() {
        return ObjectContracts.toString(this, "name");
    }

    @Override
    public int compareTo(final Auftrag other) {
        return ObjectContracts.compare(this, other, "name");
    }

    //endregion


}