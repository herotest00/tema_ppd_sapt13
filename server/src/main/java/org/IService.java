package org;

import java.util.List;

public interface IService {
    List<Spectacol> getAllSpectacole();

    List<Integer> getAllLocuriDisponibile(Spectacol spectacol);

    Vanzare rezerva(List<Integer> locuri);
}
