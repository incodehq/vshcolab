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

package org.incodehq.amberg.vshcolab.modules.work.fixture.teardown;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

public class WorkModuleTearDown extends FixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {
        isisJdoSupport.executeUpdate("delete from \"test\".\"Messwert\"");
        isisJdoSupport.executeUpdate("delete from \"test\".\"Durchfuehrung\"");
        isisJdoSupport.executeUpdate("delete from \"test\".\"Auftrag\"");

        isisJdoSupport.executeUpdate("delete from \"test\".\"Baustelle\"");
        isisJdoSupport.executeUpdate("delete from \"test\".\"Kunde\"");

        isisJdoSupport.executeUpdate("delete from \"test\".\"PruefVerfahren\"");
        isisJdoSupport.executeUpdate("delete from \"test\".\"Verfahren\"");
        isisJdoSupport.executeUpdate("delete from \"test\".\"ResourceType\"");
    }


    @javax.inject.Inject
    private IsisJdoSupport isisJdoSupport;

}
