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
package org.incodehq.amberg.vshcolab.modules.work.dom.impl.norm;

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
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.ObjectContracts;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "test",
        table = "Norm"
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
                        + "FROM org.incodehq.amberg.vshcolab.modules.work.dom.impl.norm.Norm "
                        + "WHERE name.indexOf(:name) >= 0 ")
})
@javax.jdo.annotations.Unique(name="Norm_name_UNQ", members = {"name"})
@DomainObject(
        objectType = "test.Norm",
        auditing = Auditing.ENABLED,
        publishing = Publishing.ENABLED
)
public class Norm implements Comparable<Norm> {


    //region > title
    public TranslatableString title() {
        return TranslatableString.tr("{name}", "name", getName());
    }
    //endregion

    //region > constructor
    public Norm(final String name) {
        setName(name);
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
                extends WorkModuleDomSubmodule.PropertyDomainEvent<Norm, String> { }
    }


    @javax.jdo.annotations.Column(allowsNull = "false", length = NameType.Meta.MAX_LEN)
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
                extends WorkModuleDomSubmodule.PropertyDomainEvent<Norm, String> { }
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

    //region > delete (action)
    @Mixin(method = "exec")
    public static class delete {

        public static class ActionDomainEvent extends WorkModuleDomSubmodule.ActionDomainEvent<Norm> {
        }

        private final Norm client;
        public delete(final Norm client) {
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
    public int compareTo(final Norm other) {
        return ObjectContracts.compare(this, other, "name");
    }

    //endregion


}