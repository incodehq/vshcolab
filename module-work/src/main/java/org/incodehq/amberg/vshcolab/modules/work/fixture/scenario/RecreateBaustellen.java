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

package org.incodehq.amberg.vshcolab.modules.work.fixture.scenario;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import org.incodehq.amberg.vshcolab.modules.work.dom.impl.client.Client;
import org.incodehq.amberg.vshcolab.modules.work.fixture.data.ClientMenu_create;
import org.incodehq.amberg.vshcolab.modules.work.fixture.teardown.WorkModuleTearDown;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class RecreateBaustellen extends FixtureScript {

    public final List<String> NAMES = Collections.unmodifiableList(Arrays.asList(
            "Tamina", "Baustelle #2", "Baustelle #2"));

    @Getter @Setter
    private Integer number;

    @Getter
    private final List<Client> clients = Lists.newArrayList();

    @Override
    protected void execute(final ExecutionContext ec) {

        // defaults
        final int number = defaultParam("number", ec, 3);

        // validate
        if(number < 0 || number > NAMES.size()) {
            throw new IllegalArgumentException(String.format("number must be in range [0,%d)", NAMES.size()));
        }

        // execute
        ec.executeChild(this, new WorkModuleTearDown());
        for (int i = 0; i < number; i++) {
            final String name = NAMES.get(i);
            final ClientMenu_create fs = new ClientMenu_create().setName(name);
            ec.executeChild(this, fs.getName(), fs);
            clients.add(fs.getClient());
        }
    }

}
