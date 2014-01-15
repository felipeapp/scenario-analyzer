/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/12/2008
 *
 */
package br.ufrn.sigaa.espacofisico.jsf;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.espacofisico.dominio.EspacoFisico;

/**
 * Interface que os mBeans devem implementar caso desejem fazer a busca geral de
 * espaços físicos
 * 
 * @author Henrique André
 * 
 */
public interface RealizarBusca {

	public void setEspacoFisico(EspacoFisico espacoFisico) throws ArqException;

	public String selecionaEspacoFisico() throws ArqException;
}
