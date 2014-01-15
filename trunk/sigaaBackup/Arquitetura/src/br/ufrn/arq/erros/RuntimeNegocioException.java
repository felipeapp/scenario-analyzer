/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 02/09/2004
 */
package br.ufrn.arq.erros;

import java.util.Collection;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;

/**
 * Esta exceção é emitida sempre que o erro em questão for um erro de regra de
 * negócio. Igual a NegocioException, mas do tipo unchecked.
 *
 * @author David Pereira
 * @author Gleydson Lima
 */
public class RuntimeNegocioException extends RuntimeException {

	/**
	 * Esta coleção serve para armazenar um conjunto de objetos que se dejeja
	 * que o cliente conheça.
	 */
	private Collection<?> objetos;

	private ListaMensagens listaMensagens = new ListaMensagens();

	/** Caso queira-se passar apenas um objeto ou em conjunto com a coleção acima */
	private Object objeto;

	public RuntimeNegocioException(Exception e) {
		super(e);
	}

	public RuntimeNegocioException(String msg) {
		super(msg);
		addErro(msg);
	}

	public RuntimeNegocioException() {
		super("");
	}

	/**
	 * @return Retorna objetos.
	 */
	public Collection<?> getObjetos() {
		return objetos;
	}

	/**
	 * @param objetos
	 *            The objetos to set.
	 */
	public void setObjetos(Collection<?> objetos) {
		this.objetos = objetos;
	}

	/**
	 * @return Returns the objeto.
	 */
	public Object getObjeto() {
		return objeto;
	}

	/**
	 * @param objeto
	 *            The objeto to set.
	 */
	public void setObjeto(Object objeto) {
		this.objeto = objeto;
	}

	public void addErro(String erro) {
		listaMensagens.addMensagem(new MensagemAviso(erro,
				TipoMensagemUFRN.ERROR));
	}

	public void addMensagens(Collection<MensagemAviso> mensagens) {
		listaMensagens.addAll(mensagens);
	}

	public ListaMensagens getListaMensagens() {
		return listaMensagens;
	}

	public void setListaMensagens(ListaMensagens listaMensagens) {
		this.listaMensagens = listaMensagens;
	}


	@Override
	public String getMessage() {
		if ( listaMensagens != null && listaMensagens.getMensagens().size() > 0 ) {
			String result = "";
			for ( MensagemAviso msg : listaMensagens.getMensagens() ) {
				result += msg.getMensagem() + "\n";
			}
			return result;
		} else {
			return super.getMessage();
		}
	}
}