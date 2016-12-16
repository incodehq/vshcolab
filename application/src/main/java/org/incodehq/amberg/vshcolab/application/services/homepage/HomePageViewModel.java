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
package org.incodehq.amberg.vshcolab.application.services.homepage;

import java.util.List;

import javax.inject.Inject;

import org.incodehq.amberg.vshcolab.modules.work.dom.impl.baustelle.Baustelle;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.baustelle.BaustelleRepository;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.kunde.Kunde;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.kunde.KundeRepository;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.procedure.VerfahrenRepository;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.projekt.Projekt;
import org.incodehq.amberg.vshcolab.modules.work.dom.impl.projekt.ProjektRepository;

import org.apache.isis.applib.annotation.ViewModel;
import org.apache.isis.applib.services.i18n.TranslatableString;

@ViewModel
public class HomePageViewModel {

    public TranslatableString title() {
        return TranslatableString.tr("Wilkommen");
    }

    public List<Kunde> getKunden() {
        return kundeRepository.listAll();
    }

    public List<Projekt> getProjekte() { return projektRepository.listAll(); }

    public List<Baustelle> getBaustellen() {
        return baustelleRepository.listAll();
    }

    @Inject
    KundeRepository kundeRepository;

    @Inject
    BaustelleRepository baustelleRepository;

    @Inject
    ProjektRepository projektRepository;

    @Inject
    VerfahrenRepository verfahrenRepository;





    //endregion
}
