package br.com.waltercoan.apppalestra.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.ai.contentsafety.BlocklistClient;
import com.azure.ai.contentsafety.BlocklistClientBuilder;
import com.azure.ai.contentsafety.ContentSafetyClient;
import com.azure.ai.contentsafety.ContentSafetyClientBuilder;
import com.azure.ai.contentsafety.models.AnalyzeTextOptions;
import com.azure.ai.contentsafety.models.AnalyzeTextOutputType;
import com.azure.ai.contentsafety.models.AnalyzeTextResult;
import com.azure.ai.contentsafety.models.TextBlocklistMatch;
import com.azure.ai.contentsafety.models.TextCategoriesAnalysis;
import com.azure.ai.contentsafety.models.TextCategory;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.credential.KeyCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;

import br.com.waltercoan.apppalestra.config.AppConfigProperties;
import br.com.waltercoan.apppalestra.dao.ParticipanteDAO;
import br.com.waltercoan.apppalestra.model.Participante;
import br.com.waltercoan.apppalestra.service.ParticipanteService;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@Service
public class ParticipanteServiceImpl 
        implements ParticipanteService{

    @Autowired
    private ParticipanteDAO participanteDAO;
    @Autowired
    private AppConfigProperties properties;
    private ContentSafetyClient contentSafetyClient;
    @Value("${apppalestra.clientid:#{null}}")
    private Optional<String> mngIdentityClientId;

    @Override
    public void save(Participante participante) throws Exception {
        System.out.println(
            properties.getFeatureContentSafe()
        );
        if(Boolean.parseBoolean(properties.getFeatureContentSafe())){
            if(!isContentSafe(participante)){
                throw new Exception("Content Safety Check FAIL!!!");
            }
        } 
        participanteDAO.save(participante);
    }

    @Override
    public List<Participante> getAll() {
        return participanteDAO.getAll();
    }
    

    private boolean isContentSafe(Participante participante){
        var res = true; 
        try{
            connectContentSafeAPI();
            var text = MessageFormat.format("{0} {1} {2}",participante.getNome(), participante.getCidade(), participante.getObservacao());
            System.out.println(text);
            AnalyzeTextOptions request = new AnalyzeTextOptions(text);


            Map<Category, Integer> rejectThresholds = new HashMap<>();
            rejectThresholds.put(Category.Hate, 2);
            rejectThresholds.put(Category.SelfHarm, 2);
            rejectThresholds.put(Category.Sexual, 2);
            rejectThresholds.put(Category.Violence, 2);
            
            request.setBlocklistNames(Arrays.asList("blocklist"));
            request.setHaltOnBlocklistHit(true);
            
            AnalyzeTextResult response = contentSafetyClient.analyzeText(request);

            for (TextCategoriesAnalysis result : response.getCategoriesAnalysis()) {
                System.out.println(result.getCategory() + " severity: " + result.getSeverity());
                if(result.getSeverity() > 0) res = false;
            }
            if (response.getBlocklistsMatch() != null) {
            for (TextBlocklistMatch matchResult : response.getBlocklistsMatch()) {
                System.out.println("MATCH BlocklistName: " + matchResult.getBlocklistName());
                res = false;
            }
        }
        }catch(Exception e){
            contentSafetyClient = null;
        }
        return res;
    }

    private void connectContentSafeAPI(){
        if(contentSafetyClient == null){
            
            var credential  = new DefaultAzureCredentialBuilder().build();
            if(mngIdentityClientId.isPresent())
                credential = new DefaultAzureCredentialBuilder()
                        .managedIdentityClientId(mngIdentityClientId.get()).build();

            contentSafetyClient = new ContentSafetyClientBuilder()
                    .credential(credential)
                    .endpoint(properties.getContentSafeAPI()).buildClient();
        }
    }
        
    public enum Category {
        Hate,
        SelfHarm,
        Sexual,
        Violence
    }
}

