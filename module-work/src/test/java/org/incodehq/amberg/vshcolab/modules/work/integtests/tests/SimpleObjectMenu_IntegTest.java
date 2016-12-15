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
package org.incodehq.amberg.vshcolab.modules.work.integtests.tests;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Throwables;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.Client;
import org.incodehq.amberg.vshcolab.modules.work.fixture.teardown.WorkModuleTearDown;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.xactn.TransactionService;

import org.incodehq.amberg.vshcolab.modules.work.dom.impl.ClientMenu;
import org.incodehq.amberg.vshcolab.modules.work.fixture.scenario.RecreateClients;
import org.incodehq.amberg.vshcolab.modules.work.integtests.SimpleModuleIntegTestAbstract;
import static org.assertj.core.api.Assertions.assertThat;

public class SimpleObjectMenu_IntegTest extends SimpleModuleIntegTestAbstract {

    @Inject
    FixtureScripts fixtureScripts;
    @Inject
    TransactionService transactionService;
    @Inject
    ClientMenu menu;
    @Inject
    RepositoryService repositoryService;

    public static class ListAll extends SimpleObjectMenu_IntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            RecreateClients fs = new RecreateClients();
            fixtureScripts.runFixtureScript(fs, null);
            transactionService.nextTransaction();

            // when
            final List<Client> all = wrap(menu).listAll();

            // then
            assertThat(all).hasSize(fs.getClients().size());

            Client client = wrap(all.get(0));
            assertThat(client.getName()).isEqualTo(fs.getClients().get(0).getName());
        }

        @Test
        public void whenNone() throws Exception {

            // given
            FixtureScript fs = new WorkModuleTearDown();
            fixtureScripts.runFixtureScript(fs, null);
            transactionService.nextTransaction();

            // when
            final List<Client> all = wrap(menu).listAll();

            // then
            assertThat(all).hasSize(0);
        }
    }

    public static class Create extends SimpleObjectMenu_IntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            FixtureScript fs = new WorkModuleTearDown();
            fixtureScripts.runFixtureScript(fs, null);
            transactionService.nextTransaction();

            // when
            wrap(menu).create("Faz");

            // then
            final List<Client> all = wrap(menu).listAll();
            assertThat(all).hasSize(1);
        }

        @Test
        public void whenAlreadyExists() throws Exception {

            // given
            FixtureScript fs = new WorkModuleTearDown();
            fixtureScripts.runFixtureScript(fs, null);
            transactionService.nextTransaction();
            wrap(menu).create("Faz");
            transactionService.nextTransaction();

            // then
            expectedExceptions.expectCause(causalChainContains(SQLIntegrityConstraintViolationException.class));

            // when
            wrap(menu).create("Faz");
            transactionService.nextTransaction();
        }

        private static Matcher<? extends Throwable> causalChainContains(final Class<?> cls) {
            return new TypeSafeMatcher<Throwable>() {
                @Override
                protected boolean matchesSafely(Throwable item) {
                    final List<Throwable> causalChain = Throwables.getCausalChain(item);
                    for (Throwable throwable : causalChain) {
                        if(cls.isAssignableFrom(throwable.getClass())){
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public void describeTo(Description description) {
                    description.appendText("exception with causal chain containing " + cls.getSimpleName());
                }
            };
        }
    }

}