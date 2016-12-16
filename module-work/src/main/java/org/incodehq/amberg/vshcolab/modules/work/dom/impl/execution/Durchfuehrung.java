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
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.incodehq.amberg.vshcolab.modules.work.dom.WorkModuleDomSubmodule;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.measurement.Messwert;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.norm.Norm;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.order.Auftrag;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.procedure.PruefVerfahren;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.procedure.Verfahren;
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
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.services.user.UserService;
import org.apache.isis.applib.util.ObjectContracts;

import org.isisaddons.wicket.fullcalendar2.cpt.applib.CalendarEvent;
import org.isisaddons.wicket.fullcalendar2.cpt.applib.CalendarEventable;
import org.isisaddons.wicket.fullcalendar2.cpt.applib.Calendarable;

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
public class Durchfuehrung implements Comparable<Durchfuehrung>, Calendarable {

    //region > title
    public TranslatableString title() {
        return TranslatableString.tr(
                "{number}: {type} {description} ({when})",
                "number", getNumber(),
                "type", this.getVerfahren().getCode(),
                "description", this.getVerfahren().getDescription(),
                "when", getWhenElseProjected());
    }
    //endregion

    //region > constructor
    public Durchfuehrung(final Integer number, final Verfahren verfahren, final Auftrag auftrag) {
        setNumber(number);
        setVerfahren(verfahren);
        setAuftrag(auftrag);
    }
    //endregion


    //region > auftrag (property)
    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private Auftrag auftrag;
    //endregion

    //region > verfahren
    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private Verfahren verfahren;

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

    //region > executeAfterDays (property)
    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private Integer executeAfterDays;
    //endregion

    //region > whenElseProjected (property)
    @Property()
    public LocalDate getWhenElseProjected() {
        return getWhen() != null
                ? getWhen()
                : getAuftrag().getWhen().plusDays(getExecuteAfterDays());
    }
    //endregion

    //region > calendarable (programmatic)
    @Programmatic
    @Override
    public Set<String> getCalendarNames() {
        List<String> calendarNames = Lists.newArrayList();
        calendarNames.add(calendarName());
        return Sets.newTreeSet(calendarNames);
    }

    @Programmatic
    @Override
    public ImmutableMap<String, CalendarEventable> getCalendarEvents() {
        final LocalDate whenElseProjected = getWhenElseProjected();

        String calendarName = calendarName();
        return ImmutableMap.of(calendarName, new CalendarEventable() {
            @Override public String getCalendarName() {
                return calendarName;
            }

            @Override public CalendarEvent toCalendarEvent() {
                return whenElseProjected != null
                        ? new CalendarEvent(whenElseProjected.toDateTimeAtStartOfDay(),
                        getCalendarName(),
                        titleService.titleOf(Durchfuehrung.this))
                        : null;
            }
        });
    }

    private String calendarName() {
        return calendarName(getWhen() != null ? "actual" : "projected");
    }

    private String calendarName(final String prefix) {
        return String.format("%s %s:%s", prefix, getAuftrag().getBaustelle().getName(), getAuftrag().getName());
    }

    //endregion


    //region > execute
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Durchfuehrung execute(final LocalDate when, final String who) {
        setWhen(when);
        setWho(who);
        return this;
    }

    public LocalDate default0Execute() {
        return getWhenElseProjected();
    }

    public String default1Execute() {
        return userService.getUser().getName();
    }
    //endregion

    //region > when (property)
    @Column(allowsNull = "true")
    @Property()
    @Getter @Setter
    private LocalDate when;
    //endregion

    //region > who (property)
    @Column(allowsNull = "true", length = 30)
    @Property()
    @Getter @Setter
    private String who;
    //endregion


    //region > messwerte (collection)
    @Persistent(mappedBy = "durchfuehrung", dependentElement = "false")
    @Collection()
    @Getter @Setter
    private SortedSet<Messwert> messwerte = new TreeSet<>();

    public boolean hideMesswerte() {
        return cannotMeasure();
    }

    private boolean cannotMeasure() {
        return !(getVerfahren() instanceof PruefVerfahren) || getWhen() == null;
    }

    //endregion

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
        @ActionLayout(contributed = Contributed.AS_ACTION, cssClassFa = "fa-plus", named = "Zufuegen")
        @MemberOrder(name = "messwerte", sequence = "1")
        public Durchfuehrung act(final Norm norm, final LocalDateTime measuredAt, final BigDecimal value) {
            final Messwert messwert = new Messwert(durchfuehrung, norm, measuredAt, value);
            repositoryService.persist(messwert);
            return durchfuehrung;
        }

        public boolean hideAct() {
            return durchfuehrung.cannotMeasure();
        }

        public SortedSet<Norm> choices0Act() {
            PruefVerfahren pruefVerfahren = (PruefVerfahren) durchfuehrung.getVerfahren();
            return pruefVerfahren.getNorms();
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
        @ActionLayout(contributed = Contributed.AS_ACTION, cssClassFa = "fa-minus", named = "Entfernen")
        @MemberOrder(name = "messwerte", sequence = "2")
        public Durchfuehrung act(final Messwert messwert) {
            repositoryService.remove(messwert);
            return durchfuehrung;
        }

        public boolean hideAct() {
            return durchfuehrung.cannotMeasure();
        }

        public SortedSet<Messwert> choices0Act() {
            return durchfuehrung.getMesswerte();
        }

        @Inject
        RepositoryService repositoryService;
    }
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

    //region > injected services
    @Inject
    TitleService titleService;

    @Inject
    ClockService clockService;

    @Inject
    UserService userService;
    //endregion

}