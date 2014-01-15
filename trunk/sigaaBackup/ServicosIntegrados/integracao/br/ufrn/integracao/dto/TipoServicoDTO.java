package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
 * Classe usada para exporta��o de dados de tipos de servi�os para aplica��o desktop de atendimento.
 * 
 * @author Rafael Moreira
 *
 */
public class TipoServicoDTO implements Serializable {

    /** Atributo de serializa��o da classe */
    private static final long serialVersionUID = -1L;
    /** Identificador **/
    private int id;
    /** Descri��o do tipo do servi�o */
    private String denominacao;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDenominacao() {
        return denominacao;
    }

    public void setDenominacao(String denominacao) {
        this.denominacao = denominacao;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        return getDenominacao();
    }
}
