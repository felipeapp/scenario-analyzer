/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 12/11/2004
 */
package br.ufrn.arq.dominio;

import br.ufrn.arq.negocio.ArqListaComando;


/**
 * Movimento de Desbloqueio de um comando
 * 
 * @author Gleydson Lima
 *  
 */
public class PrepareMov extends AbstractMovimento {

    private int codMovimentoDesbloqueio;

    public PrepareMov() {
        setCodMovimento(ArqListaComando.PREPARE_MOVIMENTO);
    }

    public int getId() {
        return codMovimentoDesbloqueio;
    }

    public void setId(int id) {
        codMovimentoDesbloqueio = id;
    }

}