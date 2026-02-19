package com.api.hotelurbano.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.hotelurbano.dtos.QuartoDTO;
import com.api.hotelurbano.models.Quarto;
import com.api.hotelurbano.repositories.QuartoRepository;
import com.api.hotelurbano.services.exceptions.DataBindingViolationException;
import com.api.hotelurbano.services.exceptions.ObjectNotFoundException;

@Service
public class QuartoService {
    
    @Autowired
    private QuartoRepository quartoRepository;

    @Transactional(readOnly = true)
    public Quarto buscarQuartoPorId(Long id) {
        Optional<Quarto> quarto = this.quartoRepository.findById(id);        
        return quarto.orElseThrow(() -> new ObjectNotFoundException(
            "Quarto não encontrado! Id: " + id
        ));
    }

    @Transactional(readOnly = true)
    public List<Quarto> buscarTodos() {
        return this.quartoRepository.findAll();
    }

    @Transactional
    public Quarto criar(QuartoDTO dto) {
        Quarto obj = new Quarto();
        obj.setNumeroQuarto(dto.numeroQuarto());
        obj.setTipoQuarto(dto.tipoQuarto());
        obj.setPrecoDiaria(dto.precoDiaria());
        obj.setDisponivel(dto.disponivel() != null ? dto.disponivel() : true);
        obj.setDescricao(dto.descricao());
        obj.setUrlImagem(dto.urlImagem());
        obj = this.quartoRepository.save(obj);    
        return obj;
    }

    @Transactional
    public Quarto atualizar(QuartoDTO dto, Long id) {
        Quarto obj = this.buscarQuartoPorId(id);        
        obj.setNumeroQuarto(dto.numeroQuarto());
        obj.setTipoQuarto(dto.tipoQuarto());
        obj.setPrecoDiaria(dto.precoDiaria());
        obj.setDisponivel(dto.disponivel());
        obj.setDescricao(dto.descricao());
        obj.setUrlImagem(dto.urlImagem());
        return this.quartoRepository.save(obj);
    }

    @Transactional
    public void atualizarStatus(Long id, Boolean status) {
        Quarto obj = this.buscarQuartoPorId(id);
        obj.setDisponivel(status);
        this.quartoRepository.save(obj);
    }

    @Transactional
    public void deletar(long id) {
        buscarQuartoPorId(id);
        try {
            this.quartoRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possível excluir pois há reservas para esse quarto!");
        }
    }
    

}
