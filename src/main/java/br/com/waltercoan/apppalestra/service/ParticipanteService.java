package br.com.waltercoan.apppalestra.service;

import java.util.List;

import br.com.waltercoan.apppalestra.model.Participante;

public interface ParticipanteService {
    void save(Participante participante) throws Exception;
    List<Participante> getAll();
}
