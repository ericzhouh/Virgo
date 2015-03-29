package com.winterfarmer.virgo.database;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by yangtianhang on 15-3-3.
 */
public class JdbcTemplateFactory {
    private VirgoJdbcTemplate writeJdbcTemplate;
    private List<VirgoJdbcTemplate> readJdbcTemplates;
    private int currentReadTemplateIndex = 0;
    private final int readJdbcTemplateSize;

    public JdbcTemplateFactory(VirgoJdbcTemplate writeJdbcTemplate, List<VirgoJdbcTemplate> readJdbcTemplates) {
        setWriteJdbcTemplate(writeJdbcTemplate);
        setReadJdbcTemplates(readJdbcTemplates);
        this.readJdbcTemplateSize = this.readJdbcTemplates.size();
    }

    public VirgoJdbcTemplate getReadJdbcTemplate() {
        return readJdbcTemplates.get((currentReadTemplateIndex++) % readJdbcTemplateSize);
    }

    public VirgoJdbcTemplate getWriteJdbcTemplate() {
        return writeJdbcTemplate;
    }

    private void setWriteJdbcTemplate(VirgoJdbcTemplate writeJdbcTemplate) {
        this.writeJdbcTemplate = writeJdbcTemplate;
    }

    private void setReadJdbcTemplates(List<VirgoJdbcTemplate> readJdbcTemplates) {
        this.readJdbcTemplates = Lists.newCopyOnWriteArrayList();
        this.readJdbcTemplates.addAll(readJdbcTemplates);
    }
}
