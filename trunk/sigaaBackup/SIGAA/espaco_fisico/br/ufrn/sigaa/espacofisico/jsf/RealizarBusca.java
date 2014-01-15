/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * espa�os f�sicos
 * 
 * @author Henrique Andr�
 * 
 */
public interface RealizarBusca {

	public void setEspacoFisico(EspacoFisico espacoFisico) throws ArqException;

	public String selecionaEspacoFisico() throws ArqException;
}
