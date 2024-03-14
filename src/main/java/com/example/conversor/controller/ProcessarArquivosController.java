package com.example.conversor.controller;

import com.example.conversor.servico.ProcessarTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/processa-arquivo")
public class ProcessarArquivosController {

    @Autowired
    private ProcessarTemplateService processarTemplateService;
    @GetMapping
    public void processaArquivo(){
        try {
           processarTemplateService.processaArquivosExtraidosHtml();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
