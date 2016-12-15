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
package org.incodehq.amberg.vshcolab.modules.work.dom.impl.client;

import java.util.List;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.incodehq.amberg.vshcolab.modules.work.dom.WorkModuleDomSubmodule;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.baustelle.Baustelle;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.baustelle.BaustelleRepository;

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
        table = "Client"
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
                        + "FROM org.incodehq.amberg.vshcolab.modules.work.dom.impl.client.Client "
                        + "WHERE name.indexOf(:name) >= 0 ")
})
@javax.jdo.annotations.Unique(name="Client_name_UNQ", members = {"name"})
@DomainObject(
        objectType = "simple.Client",
        auditing = Auditing.ENABLED,
        publishing = Publishing.ENABLED
)
public class Client implements Comparable<Client> {


    //region > addBaustelle (action)
    @Mixin(method="act")
    public static class addBaustelle {
        private final Client client;
        public addBaustelle(final Client client) {
            this.client = client;
        }
        public static class DomainEvent extends ActionDomainEvent<Client> {
        }
        @Action(semantics = SemanticsOf.NON_IDEMPOTENT, domainEvent = DomainEvent.class)
        @ActionLayout(contributed=Contributed.AS_ACTION)
        public Baustelle act(final String name) {
            return baustelleRepository.create(name, client);
        }
        public boolean hideAct() {
            return false;
        }
        public String disableAct() {
            return null;
        }
        public String validate0Act(final String name) {
            return null;
        }
        @javax.inject.Inject
        BaustelleRepository baustelleRepository;
    }
    //endregion

    //region > removeBaustelle (action)
    @Mixin(method="act")
    public static class removeBaustelle {
        private final Client client;
        public removeBaustelle(final Client client) {
            this.client = client;
        }
        public static class DomainEvent extends ActionDomainEvent<Client> {
        }
        @Action(semantics = SemanticsOf.NON_IDEMPOTENT, domainEvent = DomainEvent.class)
        @ActionLayout(contributed=Contributed.AS_ACTION)
        public Client act(final Baustelle baustelle) {
            repositoryService.removeAndFlush(baustelle);
            return client;
        }
        public boolean hideAct() {
            return false;
        }
        public String disableAct() {
            return null;
        }
        public List<Baustelle> choices0Act() {
            return baustelleRepository.findByClient(client);
        }

        @javax.inject.Inject
        RepositoryService repositoryService;

        @javax.inject.Inject
        BaustelleRepository baustelleRepository;
    }
    //endregion


    //region > baustellen (derived collection)
    @Mixin(method="coll")
    public static class baustellen {
        private final Client client;
        public baustellen(final Client client) {
            this.client = client;
        }
        public static class DomainEvent extends ActionDomainEvent<Client> {
        }
        @Action(semantics = SemanticsOf.SAFE, domainEvent = DomainEvent.class)
        @ActionLayout(contributed=Contributed.AS_ASSOCIATION)
        public List<Baustelle> coll() {
            return baustelleRepository.findByClient(this.client);
        }
        public boolean hideColl() {
            return false;
        }

        @javax.inject.Inject
        BaustelleRepository baustelleRepository;
    }
    //endregion

    //region > title
    public TranslatableString title() {
        return TranslatableString.tr("{name}", "name", getName());
    }
    //endregion

    //region > constructor
    public Client(final String name) {
        setName(name);
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
                extends WorkModuleDomSubmodule.PropertyDomainEvent<Client, String> { }
    }


    @javax.jdo.annotations.Column(allowsNull = "false", length = NameType.Meta.MAX_LEN)
    @Property(
            editing = Editing.DISABLED,
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
                extends WorkModuleDomSubmodule.PropertyDomainEvent<Client, String> { }
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

        public static class ActionDomainEvent extends WorkModuleDomSubmodule.ActionDomainEvent<Client> {
        }

        private final Client client;

        public updateName(final Client client) {
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
        public Client exec(
                @Parameter(maxLength = Client.NameType.Meta.MAX_LEN)
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

    //region > delete (action)
    @Mixin(method = "exec")
    public static class delete {

        public static class ActionDomainEvent extends WorkModuleDomSubmodule.ActionDomainEvent<Client> {
        }

        private final Client client;
        public delete(final Client client) {
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
    public int compareTo(final Client other) {
        return ObjectContracts.compare(this, other, "name");
    }

    //endregion



}