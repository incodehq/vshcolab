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
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.Baustelle;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.Client;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.ClientRepository;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.TestAuftrag;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.TestType;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.TestTypeRepository;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.factory.FactoryService;

public class DomainAppDemo extends FixtureScript {

    public DomainAppDemo() {
        withDiscoverability(Discoverability.DISCOVERABLE);
    }

    @Override
    protected void execute(final ExecutionContext ec) {

        ec.executeChild(this, new DomainAppTearDown());

        TestType type13412 = testTypeRepository.create("13412", "Wassergehalt von Frischbeton", "SN EN 12350-6");
        TestType type13414 = testTypeRepository.create("13414", "Konsistenz", "SN EN 12350-2 bzw");
        TestType type13416 = testTypeRepository.create("13416", "Frischbetonrohdichte", "SN EN 12350-6");
        TestType type13418 = testTypeRepository.create("13418", "Luftgehalt von Frischbeton", "SN EN 12350-7");

        final Client kappl = clientRepository.create("Kappl");

        final Baustelle tamina = factoryService.mixin(Client.addBaustelle.class, kappl).act("Tamina");
        factoryService.mixin(Client.addBaustelle.class, kappl).act("Baustelle #1");
        factoryService.mixin(Client.addBaustelle.class, kappl).act("Baustelle #2");

        final TestAuftrag taminaTest1 = factoryService.mixin(Baustelle.addTest.class, tamina).act("Test #1");
        factoryService.mixin(TestAuftrag.addStep.class, taminaTest1).act("Step #1", type13412);
        factoryService.mixin(TestAuftrag.addStep.class, taminaTest1).act("Step #2", type13414);
        factoryService.mixin(TestAuftrag.addStep.class, taminaTest1).act("Step #3", type13416);

        factoryService.mixin(Baustelle.addTest.class, tamina).act("Test #2");
        factoryService.mixin(Baustelle.addTest.class, tamina).act("Test #3");

        final Client meier = clientRepository.create("Meier");
        final Client logbau = clientRepository.create("Logbau");

    }


    @javax.inject.Inject
    ClientRepository clientRepository;

    @javax.inject.Inject TestTypeRepository testTypeRepository;

    @javax.inject.Inject FactoryService factoryService;


}
