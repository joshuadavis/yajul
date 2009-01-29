package org.yajul.micro;

import com.google.inject.Inject;
import org.yajul.micro.annotations.Component;

/**
 * Test component
 * <br>
 * User: josh
 * Date: Jan 28, 2009
 * Time: 5:50:30 PM
 */
@Component(key="TimeMachine")
public class Delorian implements TimeMachine {
    private FluxCapacitor capacitor;

    @Inject
    public Delorian(FluxCapacitor capacitor) {
        this.capacitor = capacitor;
    }

    public int getDestinationYear() {
        return 1982 + capacitor.getFuzzFactor();
    }
}
