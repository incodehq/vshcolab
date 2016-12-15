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

import javax.inject.Inject;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Persistent;

import org.incodehq.amberg.vshcolab.modules.work.dom.impl.norm.Norm;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.norm.NormRepository;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Publishing;

import lombok.Getter;
import lombok.Setter;

/**
 * A test "procedure".
 */
@javax.jdo.annotations.PersistenceCapable(schema = "test")
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(value="test.PruefVerfahren")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByCode",
                value = "SELECT "
                        + "FROM org.incodehq.amberg.vshcolab.modules.work.dom.impl.procedure.PruefVerfahren "
                        + "WHERE code.indexOf(:code) >= 0 ")
})
@DomainObject(
        auditing = Auditing.ENABLED,
        publishing = Publishing.ENABLED
)
public class PruefVerfahren extends Verfahren {

    //region > constructor
    public PruefVerfahren(final String code, final String description, final Verfahren parentIfAny) {
        super(code, description, parentIfAny);
    }

    //endregion

    @Action()
    @MemberOrder(name = "children", sequence = "1")
    public Verfahren addChild(final String code, final String description ) {
        PruefVerfahren pruefVerfahren = repository.create(code, description, this, null);
        getChildren().add(pruefVerfahren);
        return this;
    }

    //region > norms (collection); addNorm (action)

    @Persistent( table = "PruefVerfahrenNorm")
    @Join(column = "pruefVerfahren")
    @Element(column = "norm")
    @Collection()
    @Getter @Setter
    private SortedSet<Norm> norms = new TreeSet<Norm>();

    @Programmatic
    public void addNorm(final String normName) {
        if(normName != null) {
            final Norm norm = normRepository.findOrCreateByName(normName);
            getNorms().add(norm);
        }
    }
    //endregion

    @Inject
    NormRepository normRepository;

    @Inject
    PruefVerfahrenRepository repository;

}