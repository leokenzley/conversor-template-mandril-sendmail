package com.example.conversor.repository.sql;

public interface TemplateAtivosSQL {
    String SQL = """
            select distinct(tab.tr_action -> 'template') as template from
                (select *, unnest(tr_actions) tr_action from esprinter_data.tracking_rule) tab
                where tab.tr_enabled = true and tab.tr_action -> 'type' = 'EMAIL' or tab.tr_action -> 'type' = 'email'
                and tab.tr_client_id in (select cl_id from esprinter_data.client c where cl_client_status_id = 3)
                and tab.tr_enabled = true;
            """;
}
