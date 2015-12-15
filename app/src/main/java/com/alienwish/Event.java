package com.alienwish;

import java.util.Date;

/**
 * Created by Freyman on 15.12.2015.
 */
public interface Event {
    String getText();
    Date getCreationDate();
    Date getAlertDate();
    long getId();
}
