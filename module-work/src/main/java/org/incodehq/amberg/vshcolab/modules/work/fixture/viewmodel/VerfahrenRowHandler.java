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

package org.incodehq.amberg.vshcolab.modules.work.fixture.viewmodel;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.incodehq.amberg.vshcolab.modules.work.dom.impl.procedure.BusinessVerfahrenRepository;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.procedure.PruefVerfahren;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.procedure.PruefVerfahrenRepository;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.procedure.Verfahren;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import lombok.Getter;
import lombok.Setter;

public class VerfahrenRowHandler implements ExcelFixtureRowHandler {

    @Getter @Setter
    private Integer code;
    @Getter @Setter
    private String discriminator;
    @Getter @Setter
    private String description;
    @Getter @Setter
    private String norm;
    @Getter @Setter
    private BigDecimal price;

    @Override
    public List<Object> handleRow(
            final FixtureScript.ExecutionContext executionContext,
            final ExcelFixture excelFixture,
            final Object previousRow) {

        if (code == null) {
            VerfahrenRowHandler verfahrenRowHandler = asHandler(previousRow);
            Verfahren verfahren = verfahrenRowHandler.verfahren;
            if(verfahren instanceof PruefVerfahren) {
                PruefVerfahren pruefVerfahren = (PruefVerfahren) verfahren;
                pruefVerfahren.addNormIfAny(norm);
            }
        } else {
            verfahren = "P".equalsIgnoreCase(discriminator)
                    ? pruefVerfahrenRepository.create(code, description, null, null)
                    : businessVerfahrenRepository.create(code, description, null);
        }

        return Lists.newArrayList(verfahren);
    }

    private VerfahrenRowHandler asHandler(final Object previousRow) {
        return (VerfahrenRowHandler) previousRow;
    }

    @Getter @Setter
    Verfahren verfahren;

    @Inject
    PruefVerfahrenRepository pruefVerfahrenRepository;
    @Inject
    BusinessVerfahrenRepository businessVerfahrenRepository;

}
