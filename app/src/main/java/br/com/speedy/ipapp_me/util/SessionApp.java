package br.com.speedy.ipapp_me.util;

import java.util.List;

import br.com.speedy.ipapp_me.model.Foto;
import br.com.speedy.ipapp_me.model.Pedido;
import br.com.speedy.ipapp_me.model.Produto;
import br.com.speedy.ipapp_me.model.Romaneio;
import br.com.speedy.ipapp_me.model.TipoPeixe;
import br.com.speedy.ipapp_me.model.Transportadora;
import br.com.speedy.ipapp_me.model.UltimoLogin;
import br.com.speedy.ipapp_me.model.Usuario;

/**
 * Created by henrique on 2015-03-01.
 */
public class SessionApp {

    static private Usuario usuario;

    static private UltimoLogin ultimoLogin;

    static private Pedido pedido;

    static private List<Produto> itens;

    static private Produto produto;

    static private Romaneio romaneio;

    static private String filtroPeixe;

    static private TipoPeixe filtroTipo;

    static private String filtroDataInicial;

    static private String filtroDataFinal;

    static private String placaCarro;

    static private Transportadora transportadora;

    static private List<Foto> fotosPedido;

    public static String getFiltroPeixe() {
        return filtroPeixe;
    }

    public static void setFiltroPeixe(String filtroPeixe) {
        SessionApp.filtroPeixe = filtroPeixe;
    }

    public static TipoPeixe getFiltroTipo() {
        return filtroTipo;
    }

    public static void setFiltroTipo(TipoPeixe filtroTipo) {
        SessionApp.filtroTipo = filtroTipo;
    }

    public static String getFiltroDataInicial() {
        return filtroDataInicial;
    }

    public static void setFiltroDataInicial(String filtroDataInicial) {
        SessionApp.filtroDataInicial = filtroDataInicial;
    }

    public static String getFiltroDataFinal() {
        return filtroDataFinal;
    }

    public static void setFiltroDataFinal(String filtroDataFinal) {
        SessionApp.filtroDataFinal = filtroDataFinal;
    }

    public static Usuario getUsuario() {
        return usuario;
    }

    public static void setUsuario(Usuario usuario) {
        SessionApp.usuario = usuario;
    }

    public static UltimoLogin getUltimoLogin() {
        return ultimoLogin;
    }

    public static void setUltimoLogin(UltimoLogin ultimoLogin) {
        SessionApp.ultimoLogin = ultimoLogin;
    }

    public static Pedido getPedido() {
        return pedido;
    }

    public static void setPedido(Pedido pedido) {
        SessionApp.pedido = pedido;
    }

    public static List<Produto> getItens() {
        return itens;
    }

    public static void setItens(List<Produto> itens) {
        SessionApp.itens = itens;
    }

    public static Romaneio getRomaneio() {
        return romaneio;
    }

    public static void setRomaneio(Romaneio romaneio) {
        SessionApp.romaneio = romaneio;
    }

    public static Produto getProduto() {
        return produto;
    }

    public static void setProduto(Produto produto) {
        SessionApp.produto = produto;
    }

    public static String getPlacaCarro() {
        return placaCarro;
    }

    public static void setPlacaCarro(String placaCarro) {
        SessionApp.placaCarro = placaCarro;
    }

    public static Transportadora getTransportadora() {
        return transportadora;
    }

    public static void setTransportadora(Transportadora transportadora) {
        SessionApp.transportadora = transportadora;
    }

    public static List<Foto> getFotosPedido() {
        return fotosPedido;
    }

    public static void setFotosPedido(List<Foto> fotosPedido) {
        SessionApp.fotosPedido = fotosPedido;
    }
}
