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
package org.incodehq.amberg.vshcolab.modules.work.dom.impl.procedure;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.incodehq.amberg.vshcolab.modules.work.dom.WorkModuleDomSubmodule;

import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CommandReification;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.util.ObjectContracts;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "test"
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
         column="id")
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.VALUE_MAP,
        column = "discriminator")
@javax.jdo.annotations.Version(
        strategy= VersionStrategy.DATE_TIME,
        column="version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByCode",
                value = "SELECT "
                        + "FROM org.incodehq.amberg.vshcolab.modules.work.dom.impl.procedure.Verfahren "
                        + "WHERE code == :code ")
})
@javax.jdo.annotations.Unique(name="Verfahren_code_UNQ", members = {"code"})
@DomainObject(
        auditing = Auditing.ENABLED,
        publishing = Publishing.ENABLED
)
public abstract class Verfahren implements Comparable<Verfahren> {

    //region > title
    public TranslatableString title() {
        return TranslatableString.tr("{code}", "code", getCode());
    }
    //endregion

    //region > constructor
    public Verfahren(final Integer code, final String description, final Verfahren parentIfAny) {
        setCode(code);
        setDescription(description);
        setParent(parentIfAny);
    }

    //endregion

    //region > code (readonly property)
    public static class CodeType {
        private CodeType() {
        }

        public static class Meta {
            private Meta() {
            }
        }

        public static class PropertyDomainEvent
                extends WorkModuleDomSubmodule.PropertyDomainEvent<Verfahren, String> { }
    }

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Property(
            editing = Editing.DISABLED,
            domainEvent = CodeType.PropertyDomainEvent.class
    )
    @Getter @Setter
    private Integer code;

    // endregion

    //region > description (editable property)
    public static class DescriptionType {
        private DescriptionType() {
        }

        public static class Meta {
            public static final int MAX_LEN = 255;

            private Meta() {
            }
        }

        public static class PropertyDomainEvent
                extends WorkModuleDomSubmodule.PropertyDomainEvent<Verfahren, String> { }
    }


    @javax.jdo.annotations.Column(allowsNull = "false", length = DescriptionType.Meta.MAX_LEN)
    @Property(
            editing = Editing.ENABLED,
            domainEvent = DescriptionType.PropertyDomainEvent.class,
            hidden = Where.ALL_TABLES
    )
    @Getter @Setter
    private String description;

    // endregion

    @Property(hidden = Where.OBJECT_FORMS)
    @PropertyLayout(named = "Description")
    public String getDescriptionInTable() {
        int indent = getIndent();
        return "--------------------------------------".substring(0, indent) + getDescription();
    }

    private int getIndent() {
        return getParent() != null ? getParent().getIndent() + 2: 0;
    }

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
                extends WorkModuleDomSubmodule.PropertyDomainEvent<Verfahren, String> { }
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

    @Persistent(mappedBy = "parent", dependentElement = "false")
    @Collection()
    @Getter @Setter
    private SortedSet<Verfahren> children = new TreeSet<>();

    @Column(allowsNull = "true")
    @Property()
    @Getter @Setter
    private Verfahren parent;

    //region > toString, compareTo
    @Override
    public String toString() {
        return ObjectContracts.toString(this, "code");
    }

    @Override
    public int compareTo(final Verfahren other) {
        return ObjectContracts.compare(this, other, "code");
    }

    //endregion


}