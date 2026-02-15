package com.api.hotelurbano.services;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.hotelurbano.models.Quarto;
import com.api.hotelurbano.models.Reserva;
import com.api.hotelurbano.models.Usuario;
import com.api.hotelurbano.repositories.ReservaRepository;

@Service
public class ReservaService {
    
    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private QuartoService quartoService;

    @Transactional(readOnly = true)
    public Reserva buscarReservaPorId(Long id) {
        Optional<Reserva> reserva = this.reservaRepository.findById(id);
        return reserva.orElseThrow(() -> new RuntimeException(
            "Reserva não encontrada! Id: " + id 
        ));
    }

    @Transactional(readOnly = true)
    public List<Reserva> buscarTodas() {
        return this.reservaRepository.findAll();
    }

    @Transactional
    public Reserva criar(Reserva obj) {
        Usuario usuario = this.usuarioService.buscarUsuarioPorId(obj.getUsuario().getIdUsuario());
        Quarto quarto = this.quartoService.buscarQuartoPorId(obj.getQuarto().getIdQuarto());

        if (!quarto.getDisponivel()) {
            throw new RuntimeException("Este quarto não está disponível para reserva!");
        }

        // * Enum ChonoUnit foi usado para pegar as datas no banco de dados e calcular para quantos dias a reserva foi realizada
        long dias = ChronoUnit.DAYS.between(obj.getDataCheckin(), obj.getDataCheckout());

        if (dias <= 0) {
            dias = 1;
        } 

        obj.setValorTotal(dias * obj.getQuarto().getPrecoDiaria());

        obj.setIdReserva(null);
        obj.setUsuario(usuario);
        obj.setQuarto(quarto);

        obj = this.reservaRepository.save(obj);

        this.quartoService.atualizarStatus(quarto, false);

        return obj;
        
    }

    @Transactional
    public void deletar(Long id) {
        Reserva obj = buscarReservaPorId(id);
        try {
            // * Liberação do quarto antes de apagar a reserva
            this.quartoService.atualizarStatus(obj.getQuarto(), true);
            this.reservaRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Não é possível excluir a reserva!");
        }
    }

}
