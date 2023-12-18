package org.dop.getbluetoothlist;

import android.widget.Toast;

public class Item {
    private String bltName;
    private String bltAddress;

    public Item(String bltName, String bltAddress) {
        this.bltName = bltName;
        this.bltAddress = bltAddress;
    }
    public String getBltName() {
        return bltName;
    }

    public void setBltName(String bltName) {
        this.bltName = bltName;
    }

    public String getBltAddress() {
        return bltAddress;
    }

    public void setBltAddress(String bltAddress) {
        this.bltAddress = bltAddress;
    }
}
