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

import java.util.Collection;
import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Verfahren.class
)
public class VerfahrenRepository {

    public List<Verfahren> listAll() {
        return repositoryService.allInstances(Verfahren.class);
    }

    public Verfahren findByCode(final Integer codeIfAny) {
        if(codeIfAny == null) return null;
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        Verfahren.class,
                        "findByCode",
                        "code", codeIfAny));
    }

    public Collection<Verfahren> search(final String description) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Verfahren.class,
                        "findByDescription",
                        "description", description));
    }

    @javax.inject.Inject
    RepositoryService repositoryService;

}
