package com.redhat.jenkins.plugins.bayesian;

/*
 * Copyright 2017 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *limitations under the License.
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public class DnsFiddler {

    private static final Logger LOGGER = Logger.getLogger(DnsFiddler.class.getName());

    public static List<String> getActualCNAME(String cname) {
        Set<String> cnames = new HashSet<String>();
        Record[] records;
        try {
            records = new Lookup(cname, Type.CNAME).run();
            if (records != null) {
                for (int i = 0; i < records.length; i++) {
                    CNAMERecord cnameRecord = (CNAMERecord) records[i];
                    cnames.add(cnameRecord.getTarget().toString());
                }
            }
        } catch (TextParseException e) {
            LOGGER.log(Level.WARNING, "Unable to parse CNAME", e);
        }

        return new ArrayList<String>(cnames);
    }
}
