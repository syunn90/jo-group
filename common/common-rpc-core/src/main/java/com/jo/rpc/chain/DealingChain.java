package com.jo.rpc.chain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jo
 * @date 2024/7/9
 */
public class DealingChain {

    private List<Dealing> dealings = new ArrayList<>(4);

    private Iterator<Dealing> iterator;

    public void deal(DealingContext context) {
        if (iterator == null) {
            iterator = dealings.iterator();
        }
        if (iterator.hasNext()) {
            Dealing next = iterator.next();
            next.deal(context);
        }
    }

    public void addDealing(Dealing dealing) {
        this.dealings.add(dealing);
    }

    public List<Dealing> getDealings() {
        return dealings;
    }

    public void setDealings(List<Dealing> dealings) {
        this.dealings = dealings;
    }

}
