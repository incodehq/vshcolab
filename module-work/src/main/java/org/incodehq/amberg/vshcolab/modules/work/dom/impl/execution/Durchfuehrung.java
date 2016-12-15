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
package org.incodehq.amberg.vshcolab.modules.work.dom.impl.execution;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.incodehq.amberg.vshcolab.modules.work.dom.WorkModuleDomSubmodule;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.measurement.Messwert;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.norm.Norm;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.order.Auftrag;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.procedure.PruefVerfahren;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CommandReification;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.ObjectContracts;

import org.isisaddons.wicket.fullcalendar2.cpt.applib.CalendarEvent;
import org.isisaddons.wicket.fullcalendar2.cpt.applib.CalendarEventable;

import lombok.Getter;
import lombok.Setter;

/**
 * A test "execution"
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
                name = "findByAuftrag",
                value = "SELECT "
                        + "FROM org.incodehq.amberg.vshcolab.modules.work.dom.impl.execution.Durchfuehrung "
                        + "WHERE auftrag == :auftrag ")
})
@javax.jdo.annotations.Unique(name="Durchfuehrung_auftrag_number_UNQ", members = {"auftrag", "number"})
@DomainObject(
        auditing = Auditing.ENABLED,
        publishing = Publishing.ENABLED
)
public class Durchfuehrung implements Comparable<Durchfuehrung>, CalendarEventable {

    //region > title
    public TranslatableString title() {
        return TranslatableString.tr("{number}: {type}", "number", getNumber(), "type", this.getPruefVerfahren().getCode());
    }
    //endregion


    //region > constructor
    public Durchfuehrung(final Integer number, final PruefVerfahren pruefVerfahren, final Auftrag auftrag) {
        setNumber(number);
        setPruefVerfahren(pruefVerfahren);
        setAuftrag(auftrag);
    }
    //endregion

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private Auftrag auftrag;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private PruefVerfahren pruefVerfahren;

    //region > when
    @Column(allowsNull = "true")
    @Property()
    @Getter @Setter
    private LocalDate when;

    @Programmatic
    @Override
    public String getCalendarName() {
        return getAuftrag().getBaustelle().getName() + ":" + getAuftrag().getName();
    }

    @Programmatic
    @Override
    public CalendarEvent toCalendarEvent() {
        return getWhen() != null
                ? new CalendarEvent(getWhen().toDateTimeAtStartOfDay(), getCalendarName(), titleService.titleOf(this))
                : null;
    }
    //endregion

    @Persistent(mappedBy = "durchfuehrung", dependentElement = "false")
    @Collection()
    @Getter @Setter
    private SortedSet<Messwert> messwerte = new TreeSet<>();

    //region > messwertZufuegen (action)
    @Mixin(method="act")
    public static class messwertZufuegen {
        private final Durchfuehrung durchfuehrung;
        public messwertZufuegen(final Durchfuehrung durchfuehrung) {
            this.durchfuehrung = durchfuehrung;
        }
        public static class DomainEvent extends ActionDomainEvent<Durchfuehrung> {
        }
        @Action(semantics = SemanticsOf.NON_IDEMPOTENT, domainEvent = DomainEvent.class)
        @ActionLayout(contributed = Contributed.AS_ACTION, cssClassFa = "fa-plus")
        @MemberOrder(name = "messwerte", sequence = "1")
        public Durchfuehrung act(final Norm norm, final LocalDateTime measuredAt, final BigDecimal value) {
            final Messwert messwert = new Messwert(durchfuehrung, norm, measuredAt, value);
            repositoryService.persist(messwert);
            return durchfuehrung;
        }

        public SortedSet<Norm> choices0Act() {
            return durchfuehrung.getPruefVerfahren().getNorms();
        }

        public LocalDateTime default1Act() {
            return durchfuehrung.getWhen() != null ? durchfuehrung.getWhen().toLocalDateTime(new LocalTime(9,0)): null;
        }
        @Inject
        RepositoryService repositoryService;
    }
    //endregion

    //region > messwertEntfernen (action)
    @Mixin(method="act")
    public static class messwertEntfernen {
        private final Durchfuehrung durchfuehrung;
        public messwertEntfernen(final Durchfuehrung durchfuehrung) {
            this.durchfuehrung = durchfuehrung;
        }
        public static class DomainEvent extends ActionDomainEvent<Durchfuehrung> {
        }
        @Action(semantics = SemanticsOf.NON_IDEMPOTENT, domainEvent = DomainEvent.class)
        @ActionLayout(contributed = Contributed.AS_ACTION, cssClassFa = "fa-minus")
        @MemberOrder(name = "messwerte", sequence = "2")
        public Durchfuehrung act(final Messwert messwert) {
            repositoryService.remove(messwert);
            return durchfuehrung;
        }

        public SortedSet<Messwert> choices0Act() {
            return durchfuehrung.getMesswerte();
        }

        @Inject
        RepositoryService repositoryService;
    }
    //endregion

    //region > name (editable property)
    public static class NumberType {
        private NumberType() {
        }

        public static class Meta {
            private Meta() {
            }
        }

        public static class PropertyDomainEvent
                extends WorkModuleDomSubmodule.PropertyDomainEvent<Durchfuehrung, String> { }
    }


    @Column(allowsNull = "false")
    @Property(
            editing = Editing.ENABLED,
            domainEvent = NumberType.PropertyDomainEvent.class
    )
    @Getter @Setter
    private Integer number;

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
                extends WorkModuleDomSubmodule.PropertyDomainEvent<Durchfuehrung, String> { }
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
        return ObjectContracts.toString(this, "auftrag", "number");
    }

    @Override
    public int compareTo(final Durchfuehrung other) {
        return ObjectContracts.compare(this, other, "auftrag", "number");
    }

    //endregion

    @javax.inject.Inject
    TitleService titleService;

}