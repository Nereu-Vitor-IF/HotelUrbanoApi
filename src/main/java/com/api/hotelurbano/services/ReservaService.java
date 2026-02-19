package com.api.hotelurbano.services;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.hotelurbano.dtos.ReservaDTO;
import com.api.hotelurbano.models.Quarto;
import com.api.hotelurbano.models.Reserva;
import com.api.hotelurbano.models.Usuario;
import com.api.hotelurbano.models.enums.StatusReserva;
import com.api.hotelurbano.repositories.ReservaRepository;
import com.api.hotelurbano.services.exceptions.DataBindingViolationException;
import com.api.hotelurbano.services.exceptions.ObjectNotFoundException;

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
        return reserva.orElseThrow(() -> new ObjectNotFoundException(
            "Reserva não encontrada! Id: " + id 
        ));
    }

    @Transactional(readOnly = true)
    public List<Reserva> buscarTodas() {
        return this.reservaRepository.findAll();
    }

    @Transactional
    public Reserva criar(ReservaDTO dto) {
        Usuario usuario = this.usuarioService.buscarUsuarioPorId(dto.idUsuario());
        Quarto quarto = this.quartoService.buscarQuartoPorId(dto.idQuarto());

        if (!quarto.getDisponivel()) {
            throw new RuntimeException("Este quarto não está disponível para reserva!");
        }

        // * Enum ChonoUnit foi usado para pegar as datas no banco de dados e calcular para quantos dias a reserva foi realizada
        long dias = ChronoUnit.DAYS.between(dto.dataCheckIn(), dto.dataCheckOut());

        if (dias <= 0) {
            dias = 1;
        } 

        Double valorTotal = dias * quarto.getPrecoDiaria();

        Reserva obj = new Reserva();    
        obj.setUsuario(usuario);
        obj.setQuarto(quarto);
        obj.setDataCheckIn(dto.dataCheckIn());
        obj.setDataCheckOut(dto.dataCheckOut());
        obj.setValorTotal(valorTotal);

        obj = this.reservaRepository.save(obj);

        this.quartoService.atualizarStatus(quarto.getIdQuarto(), false);

        return obj;
        
    }

    @Transactional
    public void realizarCkeckIn(Long id) {
        Reserva obj = this.buscarReservaPorId(id);
        obj.setStatus(StatusReserva.CHECKIN_REALIZADO);
        this.reservaRepository.save(obj);
    }

    @Transactional
    public void realizarCkeckOut(Long id) {
        Reserva obj = this.buscarReservaPorId(id);
        // * Liberação do quarto para que outro cliente possa fazer a reserva
        this.quartoService.atualizarStatus(obj.getQuarto().getIdQuarto(), true);

        // * Atualiza o status da reserva
        obj.setStatus(StatusReserva.CHECKOUT_REALIZADO);
        this.reservaRepository.save(obj);
    }

    @Transactional
    public void deletar(Long id) {
        Reserva obj = this.buscarReservaPorId(id);
        try {
            // * Liberação do quarto antes de apagar a reserva
            this.quartoService.atualizarStatus(obj.getQuarto().getIdQuarto(), true);
            this.reservaRepository.delete(obj);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possível excluir a reserva!");
        }
    }

}
