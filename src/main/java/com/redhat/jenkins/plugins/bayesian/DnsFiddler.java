package com.redhat.jenkins.plugins.bayesian;


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
