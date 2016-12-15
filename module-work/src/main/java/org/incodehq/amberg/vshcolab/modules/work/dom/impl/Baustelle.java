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

import java.util.Collections;
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
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.ObjectContracts;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "simple",
        table = "Baustelle"
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
                        + "FROM org.incodehq.amberg.vshcolab.modules.work.dom.impl.Baustelle "
                        + "WHERE name.indexOf(:name) >= 0 "),
        @javax.jdo.annotations.Query(
                name = "findByClient", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incodehq.amberg.vshcolab.modules.work.dom.impl.Baustelle "
                        + "WHERE client == :client ")
})
@javax.jdo.annotations.Unique(name="Baustelle_name_UNQ", members = {"name"})
@DomainObject(
        objectType = "simple.Baustelle",
        auditing = Auditing.ENABLED,
        publishing = Publishing.ENABLED
)
public class Baustelle implements Comparable<Baustelle> {

    //region > title
    public TranslatableString title() {
        return TranslatableString.tr("Site: {name}", "name", getName());
    }
    //endregion

    //region > constructor
    public Baustelle(final String name, final Client client) {
        setName(name);
        setClient(client);
    }
    //endregion

    //region > name (read-only property)
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
            editing = Editing.DISABLED,
            domainEvent = NameType.PropertyDomainEvent.class
    )
    @Getter @Setter
    private String name;

    // endregion

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private Client client;

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

    //region > updateName (action)
    @Mixin(method = "exec")
    public static class updateName {

        public static class ActionDomainEvent extends WorkModuleDomSubmodule.ActionDomainEvent<Baustelle> {
        }

        private final Baustelle client;

        public updateName(final Baustelle client) {
            this.client = client;
        }

        @Action(
                command = CommandReification.ENABLED,
                publishing = Publishing.ENABLED,
                semantics = SemanticsOf.IDEMPOTENT,
                domainEvent = ActionDomainEvent.class
        )
        @ActionLayout(
                contributed = Contributed.AS_ACTION
        )
        public Baustelle exec(
                @Parameter(maxLength = Baustelle.NameType.Meta.MAX_LEN)
                final String name) {
            client.setName(name);
            return client;
        }

        public String default0Exec() {
            return client.getName();
        }

        public TranslatableString validate0Exec(final String name) {
            return name != null && name.contains("!") ? TranslatableString.tr("Exclamation mark is not allowed") : null;
        }

    }
    //endregion

    //region > testAuftragen (derived collection)
    @Mixin(method="coll")
    public static class testAuftragen {
        private final Baustelle baustelle;
        public testAuftragen(final Baustelle baustelle) {
            this.baustelle = baustelle;
        }
        public static class DomainEvent extends ActionDomainEvent<Baustelle> {
        }
        @Action(semantics = SemanticsOf.SAFE, domainEvent = DomainEvent.class)
        @ActionLayout(contributed=Contributed.AS_ASSOCIATION)
        public List<TestAuftrag> coll() {
            return testAuftragRepository.findByBaustelle(baustelle);
        }
        public boolean hideColl() {
            return false;
        }

        @javax.inject.Inject
        TestAuftragRepository testAuftragRepository;
    }
    //endregion

    //region > addTest (action)
    @Mixin(method="act")
    public static class addTest {
        private final Baustelle baustelle;
        public addTest(final Baustelle baustelle) {
            this.baustelle = baustelle;
        }
        public static class DomainEvent extends ActionDomainEvent<Baustelle> {
        }
        @Action(semantics = SemanticsOf.NON_IDEMPOTENT, domainEvent = DomainEvent.class)
        @ActionLayout(contributed=Contributed.AS_ACTION)
        public Baustelle act(final String name, final TestType testType) {
            final TestAuftrag testAuftrag = testAuftragRepository.create(name, testType, baustelle);
            return baustelle;
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
        TestAuftragRepository testAuftragRepository;
    }
    //endregion


    //region > delete (action)
    @Mixin(method = "exec")
    public static class delete {

        public static class ActionDomainEvent extends WorkModuleDomSubmodule.ActionDomainEvent<Baustelle> {
        }

        private final Baustelle client;
        public delete(final Baustelle client) {
            this.client = client;
        }

        @Action(
                domainEvent = ActionDomainEvent.class,
                semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE
        )
        @ActionLayout(
                contributed = Contributed.AS_ACTION
        )
        public void exec() {
            final String title = titleService.titleOf(client);
            messageService.informUser(String.format("'%s' deleted", title));
            repositoryService.remove(client);
        }

        @javax.inject.Inject
        RepositoryService repositoryService;

        @javax.inject.Inject
        TitleService titleService;

        @javax.inject.Inject
        MessageService messageService;
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