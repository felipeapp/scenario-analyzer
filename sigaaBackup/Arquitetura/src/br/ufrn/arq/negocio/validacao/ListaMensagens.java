/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 18/09/2006
 */
package br.ufrn.arq.negocio.validacao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.ufrn.arq.util.UFRNUtils;

/**
 * Classe que encapsula a lista de mensagens de aviso. 
 *
 * @author David Ricardo
 * @author Gleydson Lima
 *
 */
public class ListaMensagens implements Serializable {

	private static final long serialVersionUID = -6633488972242587226L;

	/** Atribute que será armazenado em sessão **/
	public static final String LISTA_MENSAGEM_SESSION = "mensagensAviso";

	public static final String LISTA_TEMPORARIA_SESSION = "mensagensTemporarias";

	private Collection<MensagemAviso> mensagens = null;

	public ListaMensagens() {
		mensagens = new ArrayList<MensagemAviso>();
	}

	public ListaMensagens(Collection<MensagemAviso> mensagens) {
		this.mensagens = mensagens;
	}
	
	public ListaMensagens(ArrayList<MensagemAviso> mensagens) {
		this.mensagens = mensagens;
	}

	/**
	 * Adiciona uma mensagem de erro à lista
	 */
	public void addErro(String msg) {
		this.mensagens.add(new MensagemAviso(msg, TipoMensagemUFRN.ERROR));
	}

	/**
	 * Adiciona uma mensagem de informação à lista
	 */
	public void addInformation(String msg) {
		this.mensagens.add(new MensagemAviso(msg, TipoMensagemUFRN.INFORMATION));
	}

	/**
	 * Adiciona uma mensagem de aviso à lista
	 */
	public void addWarning(String msg) {
		this.mensagens.add(new MensagemAviso(msg, TipoMensagemUFRN.WARNING));
	}

	/**
	 * Identifica se existem mensagens de informação na lista.
	 */
	public boolean isInfoPresent() {
		for(MensagemAviso msg : mensagens) {
			if (msg != null) {
				if (msg.isInformation())
					return true;
			}
		}
		return false;
	}

	/**
	 * Identifica se existem mensagens de erro na lista.
	 */
	public boolean isErrorPresent() {
		for(MensagemAviso msg : mensagens) {
			if (msg != null) {
				if (msg.isError())
					return true;
			}
		}
		return false;
	}

	/**
	 * Identifica se existem mensagens de aviso na lista.
	 */
	public boolean isWarningPresent() {
		for(MensagemAviso msg : mensagens) {
			if (msg != null) {
				if (msg.isWarning())
					return true;
			}
		}
		return false;
	}

	/**
	 * Cria uma Lista de Mensagens a partir de uma coleção de strings
	 */
	public static ListaMensagens fromStringCollection(Collection<String> col, TipoMensagemUFRN tipo) {

		List<MensagemAviso> msgs = new ArrayList<MensagemAviso>();

		for (String erro : col) {
			MensagemAviso msg = new MensagemAviso();
			msg.setMensagem(erro);
			msg.setTipo(tipo);
			msgs.add(msg);
		}

		ListaMensagens lista = new ListaMensagens();
		lista.mensagens = msgs;

		return lista;
	}

	/**
	 * Retorna as mensagens do tipo erro que estão na lista.
	 */
	public List<MensagemAviso> getErrorMessages() {
		List<MensagemAviso> erros = new ArrayList<MensagemAviso>();

		for(MensagemAviso msg : mensagens) {
			if (msg.isError())
				erros.add(msg);
		}

		return erros;
	}

	/**
	 * Retorna as mensagens do tipo aviso que estão na lista.
	 */
	public List<MensagemAviso> getWarningMessages() {
		List<MensagemAviso> warnings = new ArrayList<MensagemAviso>();

		for(MensagemAviso msg : mensagens) {
			if (msg.isWarning())
				warnings.add(msg);
		}

		return warnings;
	}

	/**
	 * Retorna as mensagens do tipo informação que estão na lista.
	 */
	public List<MensagemAviso> getInfoMessages() {
		List<MensagemAviso> infos = new ArrayList<MensagemAviso>();

		for(MensagemAviso msg : mensagens) {
			if (msg.isInformation())
				infos.add(msg);
		}

		return infos;
	}

	public boolean isEmpty() {
		return mensagens.isEmpty();
	}

	public Collection<MensagemAviso> getMensagens() {
		return mensagens;
	}

	public void setMensagens(Collection<MensagemAviso> mensagens) {
		this.mensagens = mensagens;
	}

	/**
	 * Adiciona uma mensagem na lista
	 */
	public void addMensagem(MensagemAviso msg) {
		if (!mensagens.contains(msg)) {
			mensagens.add(msg);
		}
	}

	/**
	 * Adiciona uma mensagem à lista buscando-a no banco de dados,
	 * de acordo com o código passado como parâmetro.
	 * @param codigo
	 * @param params
	 */
	public void addMensagem(String codigo, Object... params) {
		MensagemAviso msg = UFRNUtils.getMensagem(codigo, params);
		addMensagem(msg);
	}
	
	/**
	 * Adiciona uma coleção de mensagens na lista
	 */
	public void addAll(Collection<MensagemAviso> msgs) {
		mensagens.addAll(msgs);
	}

	/**
	 * Faz a união das mensagens de duas listas
	 */
	public void addAll(ListaMensagens lista) {
		this.addAll(lista.getMensagens());
	}

	/**
	 * Identifica se a lista contém uma determinada mensagem de aviso
	 */
	public boolean containsWarning(String mensagem) {
		return getWarningMessages().contains(new MensagemAviso(mensagem, TipoMensagemUFRN.WARNING));
	}
	
	/** Retorna o tamanho da lista de mensagens.
	 * @return tamanho da lista de mensagens.
	 */
	public int size() {
		return getMensagens().size();
	}

	@Override
	public String toString() {
		if (isEmpty())
			return super.toString();
		
		StringBuilder msg = new StringBuilder();
		for (MensagemAviso msgAviso : mensagens) {
			msg.append(msgAviso.getMensagem()).append("\n");
		}
		
		return msg.toString();
	}
	
}
