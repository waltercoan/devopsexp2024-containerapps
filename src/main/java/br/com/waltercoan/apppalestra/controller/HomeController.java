package br.com.waltercoan.apppalestra.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.azure.spring.cloud.appconfiguration.config.AppConfigurationRefresh;

import br.com.waltercoan.apppalestra.config.AppConfigProperties;
import br.com.waltercoan.apppalestra.model.Participante;
import br.com.waltercoan.apppalestra.service.ParticipanteService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private ParticipanteService service;

    @Autowired
    private AppConfigurationRefresh refreshAppConfig;
    @Autowired
    private AppConfigProperties properties;

    private List<Participante> listaParticipantes = new ArrayList<Participante>();
    private HashMap<String,Object> dados = new HashMap<>();

    @GetMapping
    public ModelAndView index(){
        if (refreshAppConfig != null) {
            refreshAppConfig.refreshConfigurations();
        }
        dados.clear();
        listaParticipantes = service.getAll();
        dados.put("backgroundcolor", properties.getColor());
        dados.put("participante", new Participante());
        dados.put("listaParticipantes",listaParticipantes);
        return new ModelAndView("home/index",dados);
    }

    @PostMapping
    public ModelAndView post(@Valid Participante participante, BindingResult bindingResult){
        if (refreshAppConfig != null) {
            refreshAppConfig.refreshConfigurations();
        }
        dados.clear();
        listaParticipantes = service.getAll();
        dados.put("backgroundcolor", properties.getColor());
        dados.put("participante", new Participante());
        dados.put("listaParticipantes",listaParticipantes);

        if(bindingResult.hasErrors()){
            dados.put("participante", participante);
            return new ModelAndView("home/index",dados);
        }

        try {
            service.save(participante);
            listaParticipantes = service.getAll();
            dados.put("listaParticipantes",listaParticipantes);
        } catch (Exception e) {
            dados.put("errormsg", e.getMessage());
        }
        
        return new ModelAndView("home/index",dados);
    }
}
