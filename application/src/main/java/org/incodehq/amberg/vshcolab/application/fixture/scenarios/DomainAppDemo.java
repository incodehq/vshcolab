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
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.resourcetype.ResourceType;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.resourcetype.ResourceTypeRepository;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.procedure.PruefVerfahren;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.procedure.PruefVerfahrenRepository;
import org.incodehq.amberg.vshcolab.modules.work.fixture.viewmodel.ProjektImport;

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

        final ProjektImport projektImport = new ProjektImport();
        ec.executeChild(this, projektImport);

        final ResourceType resourceType1 = resourceTypeRepository.create("Skill #1");
        final ResourceType skill2 = resourceTypeRepository.create("Skill #2");
        final ResourceType equipmentTypeA = resourceTypeRepository.create("Equipment type A");
        final ResourceType equipmentTypeB = resourceTypeRepository.create("Equipment type B");

        PruefVerfahren type13412 = pruefVerfahrenRepository
                .create("13412", "Wassergehalt von Frischbeton", null, "SN EN 12350-6");
        PruefVerfahren type13414 = pruefVerfahrenRepository.create("13414", "Konsistenz", null,
                "SN EN 12350-2 bzw"
        );
        PruefVerfahren type13416 = pruefVerfahrenRepository.create("13416", "Frischbetonrohdichte", null,
                "SN EN 12350-6"
        );
        PruefVerfahren procedure13418 = pruefVerfahrenRepository
                .create("13418", "Luftgehalt von Frischbeton", null, "SN EN 12350-7");
        procedure13418.addNorm("SN EN 12350-9");
        procedure13418.addNorm("SN EN 12350-11");

        final Client kappl = clientRepository.create("Kappl");

        final Baustelle tamina = factoryService.mixin(Client.addBaustelle.class, kappl).act("Tamina");
        factoryService.mixin(Client.addBaustelle.class, kappl).act("Baustelle #1");
        factoryService.mixin(Client.addBaustelle.class, kappl).act("Baustelle #2");

        final Auftrag taminaTest1 = factoryService.mixin(Baustelle.addTest.class, tamina).act("Order #1");
        //taminaTest1.setWhen(now);
        taminaTest1.setWhen(clockService.now());

        factoryService.mixin(Auftrag.addStep.class, taminaTest1).act(1, type13412, 0);
        factoryService.mixin(Auftrag.addStep.class, taminaTest1).act(2, type13414, 7);
        factoryService.mixin(Auftrag.addStep.class, taminaTest1).act(3, type13416, 14);

        factoryService.mixin(Baustelle.addTest.class, tamina).act("Order #2");
        factoryService.mixin(Baustelle.addTest.class, tamina).act("Order #3");

        final Client meier = clientRepository.create("Meier");
        final Client logbau = clientRepository.create("Logbau");

    }


    @javax.inject.Inject
    ClientRepository clientRepository;

    @javax.inject.Inject ResourceTypeRepository resourceTypeRepository;

    @javax.inject.Inject PruefVerfahrenRepository pruefVerfahrenRepository;

    @javax.inject.Inject ClockService clockService;

    @javax.inject.Inject FactoryService factoryService;


}
