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

import javax.inject.Inject;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Publishing;

/**
 * A business "procedure".
 */
@javax.jdo.annotations.PersistenceCapable(schema = "test")
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(value="test.BusinessVerfahren")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByCode",
                value = "SELECT "
                        + "FROM org.incodehq.amberg.vshcolab.modules.work.dom.impl.procedure.BusinessVerfahren "
                        + "WHERE code.indexOf(:code) >= 0 ")
})
@DomainObject(
        auditing = Auditing.ENABLED,
        publishing = Publishing.ENABLED
)
public class BusinessVerfahren extends Verfahren {

    public BusinessVerfahren(final Integer code, final String description, final Verfahren parentIfAny) {
        super(code, description, parentIfAny);
    }

    @Action()
    @MemberOrder(name = "children", sequence = "1")
    public Verfahren addChild(final Integer code, final String description ) {
        BusinessVerfahren businessVerfahren = repository.create(code, description, this);
        getChildren().add(businessVerfahren);
        return this;
    }

    @Inject
    BusinessVerfahrenRepository repository;


}