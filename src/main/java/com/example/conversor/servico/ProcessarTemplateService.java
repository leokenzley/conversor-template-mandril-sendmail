package com.example.conversor.servico;

import com.example.conversor.repository.sql.TemplateAtivosSQL;
import com.example.conversor.servico.modelo.TemplateHtmlModel;
import com.example.conversor.servico.modelo.TemplateModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProcessarTemplateService {
    @PersistenceContext
    private EntityManager entityManager;

    private static String DIRETORIO_ARQUIVOS_EXTRAIDOS = "/home/leo/intelipost/demo/src/main/resources/email-templates";
    private static String DIRETORIO_ARQUIVOS_TEMPLATES_ATIVOS = "/home/leo/intelipost/demo/src/main/resources/arquivos/ativos";
    private static String DIRETORIO_ARQUIVOS_TEMPLATES_INATIVOS = "/home/leo/intelipost/demo/src/main/resources/arquivos/inativos";
    private static String DIRETORIO_ARQUIVOS_TEMPLATES_PROCESSADOS = "/home/leo/intelipost/demo/src/main/resources/arquivos/processados";
    private static String DIRETORIO_ARQUIVOS_TEMPLATES_HTML = "/home/leo/intelipost/demo/src/main/resources/arquivos/html";
    private static String DIRETORIO_ARQUIVOS_TEMPLATES_HTML_ZIP = "/home/leo/intelipost/demo/src/main/resources/arquivos/templates.zip";

    /**
     * Adapta o template mail chimp para handlebars
     *
     * @throws IOException
     */

    private String convertTemplate(String publishCode) {
        publishCode = convertTagPrint(publishCode);
        return publishCode;
    }

    /**
     * @param publishCode
     * @return
     * @Descricao(troca a tag *|VARIAVEL|* por {{VARIAVEL}})
     */
    private String convertTagPrint(String publishCode) {
        List<String> ocorrencias = extrairMatchesTagsPrint(publishCode);
        for (String ocorrencia : ocorrencias) {
            if (isNotTagCondicional(ocorrencia)) {
                publishCode = publishCode.replace("*|" + ocorrencia + "|*", "{{" + ocorrencia + "}}");
            }
        }

        ocorrencias = extrairMatches(publishCode);
        for (String ocorrencia : ocorrencias) {
            publishCode = publishCode.replace("*|IF:" + ocorrencia + "|*", "{{#if " + ocorrencia + "}}");
        }
        // remove os *|END:IF|*
        publishCode = (Objects.nonNull(publishCode)) ? publishCode.replace("*|END:IF|*", "{{/if}}") : "";
        publishCode = (Objects.nonNull(publishCode)) ? publishCode.replace("*|ELSE:|*", "{{else}}") : "";
        // A tag com o histórico de eventos precisa ter um parse diferente
        publishCode = (Objects.nonNull(publishCode)) ? publishCode.replace("{{HISTORY_HTML}}", "{{{HISTORY_HTML}}}") : "";
        return publishCode;
    }

    private boolean isNotTagCondicional(String x) {
        return (
                !x.contains("END:IF") &&
                        !x.contains("IF:") &&
                        !x.contains("ELSEIF:") &&
                        !x.contains("IFNOT:") &&
                        !x.contains("ELSE:") &&
                        !x.contains("INTERESTED:")
        );
    }

    private void moveTemplateFromEmailTemplatesToAtivosPath(String template, String novoDiretorio) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destinationChannel = null;

        try {
            sourceChannel = new FileInputStream(DIRETORIO_ARQUIVOS_EXTRAIDOS + "/" + template).getChannel();
            destinationChannel = new FileOutputStream(novoDiretorio + "/" + template).getChannel();
            sourceChannel.transferTo(0, sourceChannel.size(),
                    destinationChannel);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (sourceChannel != null && sourceChannel.isOpen())
                sourceChannel.close();
            if (destinationChannel != null && destinationChannel.isOpen())
                destinationChannel.close();
        }
    }

    private TemplateModel loadTemplateModelFromJsonFile(String absolutPath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println("Arquivo sendo criado em JSON: " + absolutPath);
            TemplateModel someClassObject = mapper.readValue(new File(absolutPath), TemplateModel.class);
            return someClassObject;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private TemplateHtmlModel loadHtmlFromJsonFile(String htmlJson) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println("HTML: " + htmlJson);
            TemplateHtmlModel textoHtml = mapper.readValue(htmlJson, TemplateHtmlModel.class);
            return textoHtml;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void processaArquivosExtraidosHtml() {
        File file = new File(DIRETORIO_ARQUIVOS_TEMPLATES_PROCESSADOS);
        File afile[] = file.listFiles();
        int i = 0;
        for (int j = afile.length; i < j; i++) {
            File arquivos = afile[i];
            TemplateModel templateModel = loadTemplateModelFromJsonFile(arquivos.getAbsolutePath());
            if (Objects.nonNull(templateModel.getPublishCode()) &&
                    !templateModel.getPublishCode().equals("[]")) {
                TemplateHtmlModel htmlString = loadHtmlFromJsonFile(templateModel.getPublishCode());
                if (Objects.nonNull(htmlString.getHtml()) && !htmlString.getHtml().isBlank()) {
                    htmlString.setHtml(
                            htmlString.getHtml()
                                    .replace("\t", "")
                                    .replace("\r", "")
                                    .replace("\n", "")
                                    .replace("\n", "")
                    );
                    String templateConvertido = convertTemplate(htmlString.getHtml());
                    criarNovoTemplateHtml(afile[i].getName().replace(".json", ".html"), DIRETORIO_ARQUIVOS_TEMPLATES_HTML,
                            templateConvertido,
                            templateModel.getName() // nome do arquivo
                    );
                    removeEscapesDoHTML(DIRETORIO_ARQUIVOS_TEMPLATES_HTML + "/" + templateModel.getName() + ".html");

                    criarNovoTemplateHtml(afile[i].getName().replace(".json", ".html"), DIRETORIO_ARQUIVOS_TEMPLATES_HTML,
                            templateConvertido,
                            templateModel.getSlug() // nome do arquivo
                    );
                    removeEscapesDoHTML(DIRETORIO_ARQUIVOS_TEMPLATES_HTML + "/" + templateModel.getSlug() + ".html");
                }
            }
        }
    }

    private List<String> extrairMatchesTagsPrint(String texto) {
        List<String> matches = new ArrayList<>();
        if (Objects.nonNull(texto)) {
            Pattern padrao = Pattern.compile("\\*\\|(.*?)\\|\\*");
            Matcher matcher = padrao.matcher(texto);

            while (matcher.find()) {
                matches.add(matcher.group(1));
            }
        }
        return matches;
    }

    private List<String> extrairMatches(String texto) {
        List<String> matches = new ArrayList<>();
        Pattern padrao = Pattern.compile("\\*\\|IF:(.*?)\\|\\*");
        if (texto != null) {
            Matcher matcher = padrao.matcher(texto);
            while (matcher.find()) {
                matches.add(matcher.group(1));
            }
        }
        return matches;
    }

    private void criarNovoTemplateHtml(String arquivo, String diretorio, String templateModel, String nomeTemplate) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            try {
                File file = new File(diretorio.concat("/".concat(arquivo)));
                objectMapper.writeValue(file, templateModel.replaceAll("^\"|\"$", ""));
                File fileRename = new File(diretorio.concat("/".concat(nomeTemplate + ".html")));
                file.renameTo(fileRename);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void removeEscapesDoHTML(String fileAbsolute) {
        StringBuilder conteudo = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileAbsolute))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = replaceEscapeAspas(linha);
                conteudo.append(linha).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileAbsolute))) {
            bw.write(conteudo.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String replaceEscapeAspas(String linha) {
        linha = linha.replace("\\\"", "\"");
        linha = linha.replaceAll("^\"|\"$", "");
        return linha;
    }
}
