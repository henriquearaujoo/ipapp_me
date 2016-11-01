package br.com.speedy.ipapp_me.model;

import java.math.BigDecimal;

/**
 * Created by henrique on 05/05/2015.
 */
public class Romaneio {

    private Long id;

    private String lote;

    private Integer qtdeEmbalagens;

    private String observacoes;

    private Produto produto;

    private Camara camara;

    private PosicaoCamara posicaoCamara;

    private TipoPeixe tipoPeixe;

    private TamanhoPeixe tamanhoPeixe;

    private BigDecimal peso;

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public Integer getQtdeEmbalagens() {
        return qtdeEmbalagens;
    }

    public void setQtdeEmbalagens(Integer qtdeEmbalagens) {
        this.qtdeEmbalagens = qtdeEmbalagens;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public TipoPeixe getTipoPeixe() {
        return tipoPeixe;
    }

    public void setTipoPeixe(TipoPeixe tipoPeixe) {
        this.tipoPeixe = tipoPeixe;
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
