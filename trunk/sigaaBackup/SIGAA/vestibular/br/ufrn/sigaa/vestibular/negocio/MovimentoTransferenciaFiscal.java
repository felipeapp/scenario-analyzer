/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/06/2008
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import java.util.List;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.vestibular.dominio.Fiscal;
import br.ufrn.sigaa.vestibular.dominio.LocalAplicacaoProva;
import br.ufrn.sigaa.vestibular.dominio.ObjectSeletor;

/** Classe que encapsula dados para transfer�ncia de fiscais entre locais de aplica��o de prova.
 * @author �dipo Elder F. Melo
 *
 */
public class MovimentoTransferenciaFiscal extends MovimentoCadastro {

	/** Lista de fiscais a transferir. */
	private List<ObjectSeletor<Fiscal>> listaFiscalTransferencia = null;
	
	/** Local de aplica��o de prova de destino dos fiscais. */
	private LocalAplicacaoProva localAplicacaoDestino = null;

	/** Retorna o local de aplica��o de prova de destino dos fiscais. 
	 * @return
	 */
	public LocalAplicacaoProva getLocalAplicacaoDestino() {
		return localAplicacaoDestino;
	}

	/** Seta o local de aplica��o de prova de destino dos fiscais. 
	 * @param localAplicacaoDestino
	 */
	public void setLocalAplicacaoDestino(LocalAplicacaoProva localAplicacaoDestino) {
		this.localAplicacaoDestino = localAplicacaoDestino;
	}

	/** Retorna a lista de fiscais a transferir. 
	 * @return
	 */
	public List<ObjectSeletor<Fiscal>> getListaFiscalTransferencia() {
		return listaFiscalTransferencia;
	}

	/** Seta a lista de fiscais a transferir. 
	 * @param listaFiscalTransferencia
	 */
	public void setListaFiscalTransferencia(
			List<ObjectSeletor<Fiscal>> listaFiscalTransferencia) {
		this.listaFiscalTransferencia = listaFiscalTransferencia;
	}
}
