package com.api.hotelurbano.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.hotelurbano.models.Quarto;
import com.api.hotelurbano.repositories.QuartoRepository;

@Service
public class QuartoService {
    
    @Autowired
    private QuartoRepository quartoRepository;

    @Transactional(readOnly = true)
    public Quarto buscarQuartoPorId(Long id) {
        Optional<Quarto> quarto = this.quartoRepository.findById(id);        
        return quarto.orElseThrow(() -> new RuntimeException(
            "Quarto não encontrado! Id: " + id
        ));
    }

    @Transactional(readOnly = true)
    public List<Quarto> buscarTodos() {
        return this.quartoRepository.findAll();
    }

    @Transactional
    public Quarto criar(Quarto obj) {
        obj.setIdQuarto(null);
        obj.setDisponivel(true);
        obj = this.quartoRepository.save(obj);    
        return obj;
    }

    @Transactional
    public Quarto atualizar(Quarto obj) {
        Quarto novoObj = buscarQuartoPorId(obj.getIdQuarto());
        novoObj.setPrecoDiaria(obj.getPrecoDiaria());
        novoObj.setTipoQuarto(obj.getTipoQuarto());
        return this.quartoRepository.save(novoObj);
    }

    @Transactional
    public void atualizarStatus(Quarto quarto, Boolean status) {
        quarto.setDisponivel(status);
        this.quartoRepository.save(quarto);
    }

    @Transactional
    public void deletar(long id) {
        buscarQuartoPorId(id);
        try {
            this.quartoRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Não é possível excluir pois há reservas para esse quarto!");
        }
    }
    

}
