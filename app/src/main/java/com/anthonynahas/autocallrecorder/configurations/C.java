package com.anthonynahas.autocallrecorder.configurations;

import javax.inject.Inject;

/**
 * Created by anahas on 23.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 23.06.17
 */

public class C {

    @Inject
    public C() {
    }

    public String uri = "uri";
    public String projection = "projection";
    public String selection = "selection";
    public String selectionArguments = "selectionArguments";
    public String sort = "sort";
    public String limit = "limit";
    public String offset = "offset";
    public String groupBy = "groupBy";

}
