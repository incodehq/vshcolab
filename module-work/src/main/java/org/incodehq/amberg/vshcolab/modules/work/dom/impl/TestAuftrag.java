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
package org.incodehq.amberg.vshcolab.modules.work.dom.impl;

import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.incodehq.amberg.vshcolab.modules.work.dom.WorkModuleDomSubmodule;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.CommandReification;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
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
        schema = "simple",
        table = "TestAuftrag"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
         column="id")
@javax.jdo.annotations.Version(
        strategy= VersionStrategy.DATE_TIME,
        column="version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incodehq.amberg.vshcolab.modules.work.dom.impl.TestAuftrag "
                        + "WHERE name.indexOf(:name) >= 0 "),
        @javax.jdo.annotations.Query(
                name = "findByBaustelle", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incodehq.amberg.vshcolab.modules.work.dom.impl.TestAuftrag "
                        + "WHERE baustelle == :baustelle ")
})
@javax.jdo.annotations.Unique(name="TestAuftrag_baustelle_name_UNQ", members = {"baustelle", "name"})
@DomainObject(
        objectType = "simple.TestAuftrag",
        auditing = Auditing.ENABLED,
        publishing = Publishing.ENABLED
)
public class TestAuftrag implements Comparable<TestAuftrag> {

    //region > title
    public TranslatableString title() {
        return TranslatableString.tr("TestAuftrag: {name}", "name", getName());
    }
    //endregion

    //region > constructor
    public TestAuftrag(
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

//    @Column(allowsNull = "false")
//    @Property()
//    @Getter @Setter
//    private TestType testType;

    //region > addStep (action)
    @Mixin(method="act")
    public static class addStep {
        private final TestAuftrag testAuftrag;
        public addStep(final TestAuftrag testAuftrag) {
            this.testAuftrag = testAuftrag;
        }
        public static class DomainEvent extends ActionDomainEvent<TestAuftrag> {
        }
        @Action(semantics = SemanticsOf.NON_IDEMPOTENT, domainEvent = DomainEvent.class)
        @ActionLayout(contributed=Contributed.AS_ACTION)
        public TestAuftrag act(final Integer number, final TestType testType) {
            testStepRepository.create(number, testType, testAuftrag);
            return testAuftrag;
        }

        @javax.inject.Inject
        TestStepRepository testStepRepository;
    }
    //endregion

    //region > steppen (derived collection)
    @Mixin(method="coll")
    public static class steppen {
        private final TestAuftrag testAuftrag;
        public steppen(final TestAuftrag testAuftrag) {
            this.testAuftrag = testAuftrag;
        }
        public static class DomainEvent extends ActionDomainEvent<TestAuftrag> {
        }
        @Action(semantics = SemanticsOf.SAFE, domainEvent = DomainEvent.class)
        @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
        public List<TestStep> coll() {
            return testStepRepository.findByTestAuftrag(testAuftrag);
        }
        public boolean hideColl() {
            return false;
        }

        @javax.inject.Inject
        TestStepRepository testStepRepository;
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
                extends WorkModuleDomSubmodule.PropertyDomainEvent<TestAuftrag, String> { }
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
                extends WorkModuleDomSubmodule.PropertyDomainEvent<TestAuftrag, String> { }
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
    public int compareTo(final TestAuftrag other) {
        return ObjectContracts.compare(this, other, "name");
    }

    //endregion


}