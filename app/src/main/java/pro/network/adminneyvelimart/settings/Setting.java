package pro.network.adminneyvelimart.settings;

import java.io.Serializable;

/**
 * Created by ravi on 16/11/17.
 */

public class Setting implements Serializable {
    int shippingCost,packingCost;

    public Setting(int shippingCost,int packingCost) {
        this.shippingCost = shippingCost;
        this.packingCost=packingCost;
    }

    public int getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(int shippingCost) {
        this.shippingCost = shippingCost;
    }

    public int getPackingCost() {
        return packingCost;
    }

    public void setPackingCost(int packingCost) {
        this.packingCost = packingCost;
    }
}