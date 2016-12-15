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
package org.incodehq.amberg.vshcolab.modules.work.dom.impl.testtype;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.incodehq.amberg.vshcolab.modules.work.dom.WorkModuleDomSubmodule;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.norm.Norm;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.norm.NormRepository;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CommandReification;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Programmatic;
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
        schema = "simple",
        table = "TestType"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
         column="id")
@javax.jdo.annotations.Version(
        strategy= VersionStrategy.DATE_TIME,
        column="version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByCode", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incodehq.amberg.vshcolab.modules.work.dom.impl.testtype.TestType "
                        + "WHERE code.indexOf(:code) >= 0 ")
})
@javax.jdo.annotations.Unique(name="TestType_code_UNQ", members = {"code"})
@DomainObject(
        objectType = "simple.TestType",
        auditing = Auditing.ENABLED,
        publishing = Publishing.ENABLED
)
public class TestType implements Comparable<TestType> {


    //region > title
    public TranslatableString title() {
        return TranslatableString.tr("{code}", "code", getCode());
    }
    //endregion

    //region > constructor
    public TestType(final String code, final String description) {
        setCode(code);
        setDescription(description);
    }

    @Programmatic
    public void addNorm(final String normName) {
        if(normName != null) {
            final Norm norm = normRepository.findOrCreateByName(normName);
            getNorms().add(norm);
        }
    }
    //endregion

    //region > code (editable property)
    public static class CodeType {
        private CodeType() {
        }

        public static class Meta {
            public static final int MAX_LEN = 40;

            private Meta() {
            }
        }

        public static class PropertyDomainEvent
                extends WorkModuleDomSubmodule.PropertyDomainEvent<TestType, String> { }
    }


    @javax.jdo.annotations.Column(allowsNull = "false", length = CodeType.Meta.MAX_LEN)
    @Property(
            editing = Editing.ENABLED,
            domainEvent = CodeType.PropertyDomainEvent.class
    )
    @Getter @Setter
    private String code;

    // endregion

    //region > description (editable property)
    public static class DescriptionType {
        private DescriptionType() {
        }

        public static class Meta {
            public static final int MAX_LEN = 40;

            private Meta() {
            }
        }

        public static class PropertyDomainEvent
                extends WorkModuleDomSubmodule.PropertyDomainEvent<TestType, String> { }
    }


    @javax.jdo.annotations.Column(allowsNull = "false", length = DescriptionType.Meta.MAX_LEN)
    @Property(
            editing = Editing.ENABLED,
            domainEvent = DescriptionType.PropertyDomainEvent.class
    )
    @Getter @Setter
    private String description;

    // endregion

    @Persistent(table = "TestTypeNorm")
    @Join(column = "testType")
    @Element(column = "norm")
    @Collection()
    @Getter @Setter
    private SortedSet<Norm> norms = new TreeSet<Norm>();

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
                extends WorkModuleDomSubmodule.PropertyDomainEvent<TestType, String> { }
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

        public static class ActionDomainEvent extends WorkModuleDomSubmodule.ActionDomainEvent<TestType> {
        }

        private final TestType client;
        public delete(final TestType client) {
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
        return ObjectContracts.toString(this, "code");
    }

    @Override
    public int compareTo(final TestType other) {
        return ObjectContracts.compare(this, other, "code");
    }

    //endregion


    @Inject
    NormRepository normRepository;

}