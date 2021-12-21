package org;

import org.domain.Spectacol;
import org.domain.Vanzare;
import org.repo.SalaRepository;
import org.repo.SpectacolRepository;
import org.repo.VanzareRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class Service implements IService {

    private final int nrTotalLocuri = 100;
    private final SalaRepository salaRepository;
    private final SpectacolRepository spectacolRepository;
    private final VanzareRepository vanzareRepository;

    public Service(SalaRepository salaRepository, SpectacolRepository spectacolRepository, VanzareRepository vanzareRepository) {
        this.salaRepository = salaRepository;
        this.spectacolRepository = spectacolRepository;
        this.vanzareRepository = vanzareRepository;
    }

    @Override
    public List<Spectacol> getAllSpectacole() {
        return spectacolRepository.findAll();
    }

    @Override
    public List<Integer> getAllLocuriDisponibile(Spectacol spectacol) {
        Optional<Spectacol> optSpectacol = spectacolRepository.findById(spectacol.getId());
        if (optSpectacol.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> locuriTotale = new ArrayList<>();
        for (int i = 1; i <= nrTotalLocuri ; i++) {
            locuriTotale.add(i);
        }
        locuriTotale.removeAll(optSpectacol.get().getLocuriVandute());
        return locuriTotale;
    }

    @Override
    public Vanzare rezerva(Spectacol spectacol, List<Integer> locuri) {

        return null;
    }
}
