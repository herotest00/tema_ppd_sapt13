package org;

import org.domain.Spectacol;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Client implements Runnable {

    private final ServiceProxy serviceProxy = new ServiceProxy("localhost", 1234);
    private final Random random = new Random();

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("Client started");
                if (!serviceProxy.isConnected()) {
                    System.out.println("Nasol");
                    return;
                }

                List<Spectacol> spectacole = serviceProxy.getAllSpectacole();
                Spectacol spectacol = spectacole.get(random.nextInt(spectacole.size()));
                List<Integer> locuriDisponibile = serviceProxy.getAllLocuriDisponibile(spectacol);
                if (locuriDisponibile.size() == 0) {
                    Thread.sleep(2000);
                    continue;
                }
                Collections.shuffle(locuriDisponibile);
                int nrBilete = random.nextInt(Math.min(locuriDisponibile.size(), 5)) + 1;
                List<Integer> locuriRezervate = locuriDisponibile.subList(0, nrBilete);
                serviceProxy.rezerva(spectacol, locuriRezervate);
                System.out.println("Done " + nrBilete + locuriRezervate);

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
