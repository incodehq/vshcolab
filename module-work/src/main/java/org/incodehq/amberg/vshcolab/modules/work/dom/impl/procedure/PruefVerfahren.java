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

import java.math.BigDecimal;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Persistent;

import com.google.common.collect.Lists;

import org.incodehq.amberg.vshcolab.modules.work.dom.impl.norm.Norm;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.norm.NormRepository;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;

import lombok.Getter;
import lombok.Setter;

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
    public PruefVerfahren(final Integer code, final String description, final Verfahren parentIfAny) {
        super(code, description, parentIfAny);
    }

    //endregion

    //region > norms (collection); addNorm (action); removeNorm (action)

    @Persistent( table = "PruefVerfahrenNorm")
    @Join(column = "verfahren")
    @Element(column = "norm")
    @Collection()
    @Getter @Setter
    private SortedSet<Norm> norms = new TreeSet<Norm>();

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(cssClassFa = "fa-plus", named = "Add")
    @MemberOrder(name = "norms", sequence = "1")
    public PruefVerfahren addNorm(final Norm norm) {
        getNorms().add(norm);
        return this;
    }

    public List<Norm> choices0AddNorm() {
        List<Norm> norms = Lists.newArrayList(normRepository.listAll());
        norms.removeAll(getNorms());
        return norms;
    }

    public String disableAddNorm() {
        return choices0AddNorm().isEmpty() ? "No norms to add": null;
    }

    @Programmatic
    public void addNormIfAny(final String normNameIfAny, final String normUnitOfMeasurementIfAny) {
        if (normNameIfAny == null) {
            return;
        }
        final Norm norm = normRepository.findOrCreateByName(normNameIfAny, normUnitOfMeasurementIfAny);
        addNorm(norm);
    }

    @Action
    @MemberOrder(name = "norms", sequence = "2")
    @ActionLayout(cssClassFa = "fa-minus", named = "Remove")
    public PruefVerfahren removeNorm(final Norm norm) {
        getNorms().remove(norm);
        return this;
    }
    public SortedSet<Norm> choices0RemoveNorm() {
        return getNorms();
    }
    public String disableRemoveNorm() {
        return getNorms().isEmpty() ? "Nothing to remove" : null;
    }

    //endregion

    //region > price
    @Column(allowsNull = "true")
    @Property()
    @Getter @Setter
    private BigDecimal price;

    public boolean hidePrice() {
        return getPriceAufAnfrage() != null && getPriceAufAnfrage().booleanValue();
    }
    //endregion

    //region > priceAufAnfrage
    @Column(allowsNull = "true")
    @Property()
    @Getter @Setter
    private Boolean priceAufAnfrage;
    //endregion

    //region > injected services
    @Inject
    NormRepository normRepository;

    @Inject
    PruefVerfahrenRepository repository;
    //endregion

}