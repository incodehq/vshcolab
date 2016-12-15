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

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = PruefVerfahren.class
)
public class PruefVerfahrenRepository {

    public List<PruefVerfahren> listAll() {
        return repositoryService.allInstances(PruefVerfahren.class);
    }

    public PruefVerfahren create(
            final Integer code,
            final String name,
            final Verfahren parentIfAny,
            final String normIfAny) {
        final PruefVerfahren pruefVerfahren = new PruefVerfahren(code, name, parentIfAny);
        serviceRegistry.injectServicesInto(pruefVerfahren);
        pruefVerfahren.addNormIfAny(normIfAny);
        repositoryService.persistAndFlush(pruefVerfahren);
        return pruefVerfahren;
    }

    @javax.inject.Inject
    RepositoryService repositoryService;
    @javax.inject.Inject
    ServiceRegistry2 serviceRegistry;
}
