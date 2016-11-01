package br.com.speedy.ipapp_me.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by henrique on 05/05/2015.
 */
public class Produto {

    private Long id;

    private BigDecimal peso;

    private Peixe peixe;

    private TipoPeixe tipoPeixe;

    private TamanhoPeixe tamanhoPeixe;

    private Camara camara;

    private PosicaoCamara posicaoCamara;

    private Pedido pedido;

    private List<Romaneio> romaneios;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public Peixe getPeixe() {
        return peixe;
    }

    public void setPeixe(Peixe peixe) {
        this.peixe = peixe;
    }

    public TipoPeixe getTipoPeixe() {
        return tipoPeixe;
    }

    public void setTipoPeixe(TipoPeixe tipoPeixe) {
        this.tipoPeixe = tipoPeixe;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public List<Romaneio> getRomaneios() {
        return romaneios;
    }

    public void setRomaneios(List<Romaneio> romaneios) {
        this.romaneios = romaneios;
    }

    public Camara getCamara() {
        return camara;
    }

    public void setCamara(Camara camara) {
        this.camara = camara;
    }

    public PosicaoCamara getPosicaoCamara() {
        return posicaoCamara;
    }

    public void setPosicaoCamara(PosicaoCamara posicaoCamara) {
        this.posicaoCamara = posicaoCamara;
    }

    public TamanhoPeixe getTamanhoPeixe() {
        return tamanhoPeixe;
    }

    public void setTamanhoPeixe(TamanhoPeixe tamanhoPeixe) {
        this.tamanhoPeixe = tamanhoPeixe;
    }
}
