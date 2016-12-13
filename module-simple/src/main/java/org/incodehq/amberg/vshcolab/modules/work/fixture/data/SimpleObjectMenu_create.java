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

package org.incodehq.amberg.vshcolab.modules.work.fixture.data;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incodehq.amberg.vshcolab.modules.work.dom.impl.SimpleObject;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.SimpleObjectMenu;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class SimpleObjectMenu_create extends FixtureScript {

    /**
     * Name of the object (required)
     */
    @Getter @Setter
    private String name;

    /**
     * The created simple object (output).
     */
    @Getter
    private SimpleObject simpleObject;


    @Override
    protected void execute(final ExecutionContext ec) {

        String name = checkParam("name", ec, String.class);

        this.simpleObject = wrap(simpleObjectMenu).create(name);
        ec.addResult(this, simpleObject);
    }

    @javax.inject.Inject
    SimpleObjectMenu simpleObjectMenu;

}