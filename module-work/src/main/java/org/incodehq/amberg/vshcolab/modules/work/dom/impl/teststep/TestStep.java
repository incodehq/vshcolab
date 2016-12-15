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
package org.incodehq.amberg.vshcolab.modules.work.dom.impl.teststep;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.incodehq.amberg.vshcolab.modules.work.dom.WorkModuleDomSubmodule;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.testtype.TestType;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.testaufrag.TestAuftrag;
import org.joda.time.DateTime;

import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.CommandReification;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.ObjectContracts;

import org.isisaddons.wicket.fullcalendar2.cpt.applib.CalendarEvent;
import org.isisaddons.wicket.fullcalendar2.cpt.applib.CalendarEventable;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "simple",
        table = "TestStep"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
         column="id")
@javax.jdo.annotations.Version(
        strategy= VersionStrategy.DATE_TIME,
        column="version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByTestAuftrag", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incodehq.amberg.vshcolab.modules.work.dom.impl.teststep.TestStep "
                        + "WHERE testAuftrag == :testAuftrag ")
})
@javax.jdo.annotations.Unique(name="TestStep_auftrag_number_UNQ", members = {"testAuftrag", "number"})
@DomainObject(
        objectType = "simple.TestStep",
        auditing = Auditing.ENABLED,
        publishing = Publishing.ENABLED
)
public class TestStep implements Comparable<TestStep>, CalendarEventable {

    //region > title
    public TranslatableString title() {
        return TranslatableString.tr("TestStep: {number}: {type}", "number", getNumber(), "type", getTestType().getCode());
    }
    //endregion

    //region > constructor
    public TestStep(final Integer number, final TestType testType, final TestAuftrag testAuftrag) {
        setNumber(number);
        setTestType(testType);
        setTestAuftrag(testAuftrag);
    }
    //endregion

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private TestAuftrag testAuftrag;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private TestType testType;

    //region > when
    @Column(allowsNull = "true")
    @Property()
    @Getter @Setter
    private DateTime when;

    @Programmatic
    @Override
    public String getCalendarName() {
        //return getTestAuftrag().getBaustelle().getName() + ":" + testAuftrag.getName();
        return testAuftrag.getName();
    }

    @Override
    public CalendarEvent toCalendarEvent() {
        return getWhen() != null ? new CalendarEvent(getWhen(), getCalendarName(), titleService.titleOf(this)): null;
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
                extends WorkModuleDomSubmodule.PropertyDomainEvent<TestStep, String> { }
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
                extends WorkModuleDomSubmodule.PropertyDomainEvent<TestStep, String> { }
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
        return ObjectContracts.toString(this, "testAuftrag", "number");
    }

    @Override
    public int compareTo(final TestStep other) {
        return ObjectContracts.compare(this, other, "testAuftrag", "number");
    }

    //endregion

    @javax.inject.Inject
    TitleService titleService;

}