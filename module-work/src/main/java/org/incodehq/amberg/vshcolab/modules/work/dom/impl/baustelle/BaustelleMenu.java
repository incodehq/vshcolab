package org.incodehq.amberg.vshcolab.modules.work.dom.impl.baustelle;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(named = "Baustellen")
public class BaustelleMenu {

    public List<Baustelle> alleBaustellen() {
        return baustelleRepository.listAll();
    }

    @Inject
    private BaustelleRepository baustelleRepository;
}

