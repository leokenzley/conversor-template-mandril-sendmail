package com.example.conversor.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tracking_rule", schema = "esprinter_data")
public class TrackingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tr_id")
    private Integer id;

    @Column(name = "tr_name")
    private String name;

    @Column(name = "tr_client_id")
    private Integer clientId;

    @Column(name = "tr_conditions")
    private String conditions;

    @Column(name = "tr_actions")
    private String actions;

    @Column(name = "tr_created")
    private LocalDateTime created;

    @Column(name = "tr_modified")
    private LocalDateTime modified;

    @Column(name = "tr_enabled")
    private Boolean enabled;

    @Column(name = "tr_execution_order")
    private Integer executionOrder;
}
