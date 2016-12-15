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

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = TestStep.class
)
public class TestStepRepository {

    public List<TestStep> listAll() {
        return repositoryService.allInstances(TestStep.class);
    }

    public List<TestStep> findByName(final String name) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        TestStep.class,
                        "findByName",
                        "name", name));
    }

    public List<TestStep> findByTestAuftrag(final TestAuftrag testAuftrag) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        TestStep.class,
                        "findByTestAuftrag",
                        "testAuftrag", testAuftrag));
    }

    public TestStep create(final String name, final TestType testType, final TestAuftrag testAuftrag) {
        final TestStep object = new TestStep(name, testType, testAuftrag);
        serviceRegistry.injectServicesInto(object);
        repositoryService.persist(object);
        return object;
    }

    @javax.inject.Inject
    RepositoryService repositoryService;
    @javax.inject.Inject
    ServiceRegistry2 serviceRegistry;

}
