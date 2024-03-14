package com.example.conversor.servico.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TemplateModel {
    private Integer userId;
    private String slug;
    private String name;
    private String code;
    private String publishCode;
    private Integer publishedAt;
    private String hash;
    private Integer createdAt;
    private Integer updatedAt;
    private Integer draftUpdatedAt;
    private String html;
    private String publishHtml;
    private String text;
    private String publishText;
    private String subject;
    private String publishSubject;
    private String fromEmail;
    private String publishFromEmail;
    private String fromName;
    private String publishFromName;
    private Boolean hasDraftedContent;
    private Boolean hasPublishedContent;
    private List<String> labels;
    private Boolean isBrokenTemplate;
}
