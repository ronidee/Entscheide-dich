package de.pauni.entscheide_dich;

/**
 * Created by roni on 26.04.17.
 * Manages all network stuff like down- and uploading statistics
 */

public class NetworkManager {
    int[] downloadStatistics(boolean plusone) {
        if (plusone) {
            uploadPlusone();
        }
        return new int[]{1,2,3};
    }

    private void uploadPlusone() {

    }
}
