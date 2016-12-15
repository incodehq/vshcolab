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
package org.incodehq.amberg.vshcolab.application.fixture.scenarios;

import org.incodehq.amberg.vshcolab.application.fixture.teardown.DomainAppTearDown;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.baustelle.Baustelle;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.client.Client;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.client.ClientRepository;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.order.Auftrag;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.procedure.Verfahren;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.procedure.VerfahrenRepository;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.resourcetype.ResourceType;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.resourcetype.ResourceTypeRepository;
import org.incodehq.amberg.vshcolab.modules.work.fixture.viewmodel.VshImport;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.factory.FactoryService;

public class DomainAppDemo extends FixtureScript {

    public DomainAppDemo() {
        withDiscoverability(Discoverability.DISCOVERABLE);
    }

    @Override
    protected void execute(final ExecutionContext ec) {

        ec.executeChild(this, new DomainAppTearDown());

        final VshImport vshImport = new VshImport();
        ec.executeChild(this, vshImport);

        final ResourceType resourceType1 = resourceTypeRepository.create("Skill #1");
        final ResourceType skill2 = resourceTypeRepository.create("Skill #2");
        final ResourceType equipmentTypeA = resourceTypeRepository.create("Equipment type A");
        final ResourceType equipmentTypeB = resourceTypeRepository.create("Equipment type B");

        Verfahren procedure13412 = verfahrenRepository.findByCode(13412);
        Verfahren procedure13414 = verfahrenRepository.findByCode(13414);
        Verfahren procedure13416 = verfahrenRepository.findByCode(13416);
        Verfahren procedure13418 = verfahrenRepository.findByCode(13418);

        final Client kappl = clientRepository.create("Kappl");

        final Baustelle tamina = kappl.addBaustelle("Tamina");
        kappl.addBaustelle("Baustelle #1");
        kappl.addBaustelle("Baustelle #2");

        final Auftrag taminaTest1 = factoryService.mixin(Baustelle.addTest.class, tamina).act("Order #1");
        //taminaTest1.setWhen(now);
        taminaTest1.setWhen(clockService.now());

        factoryService.mixin(Auftrag.addExecution.class, taminaTest1).act(1, procedure13412, 0);
        factoryService.mixin(Auftrag.addExecution.class, taminaTest1).act(2, procedure13414, 7);
        factoryService.mixin(Auftrag.addExecution.class, taminaTest1).act(3, procedure13416, 14);

        factoryService.mixin(Baustelle.addTest.class, tamina).act("Order #2");
        factoryService.mixin(Baustelle.addTest.class, tamina).act("Order #3");

        final Client meier = clientRepository.create("Meier");
        final Client logbau = clientRepository.create("Logbau");

    }


    @javax.inject.Inject
    ClientRepository clientRepository;

    @javax.inject.Inject ResourceTypeRepository resourceTypeRepository;

    @javax.inject.Inject VerfahrenRepository verfahrenRepository;

    @javax.inject.Inject ClockService clockService;

    @javax.inject.Inject FactoryService factoryService;


}
