/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 06/11/2007
 */
package br.ufrn.arq.negocio.validacao;

import java.io.Serializable;

import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.SubSistema;

/**
 * Mensagem padrão do sistema. As mensagens podem ser de vários tipos: warning, error, information.
 * 
 * @author Gleydson Lima
 * @author David Pereira
 *
 */
public class MensagemAviso implements Serializable {

	private static final long serialVersionUID = -4167664447008755331L;
	
	/** Sistema do qual a mensagem faz parte. */
	private Sistema sistema;
	
	/** SubSistema do qual a mensagem faz parte. */
	private SubSistema subSistema;
	
	/** Código da mensagem de aviso. */
	private String codigo;
	
	/** Classe que contém a constante que representa a mensagem. */
	private String classeConstantes;
	
	/** Tipo de mensagem (Erro, Aviso, Informação). */
	private TipoMensagemUFRN tipo;

	/** Texto da mensagem de aviso. */
	private String mensagem;

	public MensagemAviso() {

	}

	public MensagemAviso(String mensagem, TipoMensagemUFRN tipo) {
		this.mensagem = mensagem;
		this.tipo = tipo;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public TipoMensagemUFRN getTipo() {
		return tipo;
	}

	public void setTipo(TipoMensagemUFRN tipo) {
		this.tipo = tipo;
	}

	/**
	 * Identifica se a mensagem é uma mensagem de erro
	 */
	public boolean isError() {
		return TipoMensagemUFRN.ERROR.equals(tipo);
	}

	/**
	 * Identifica se a mensagem é uma mensagem de aviso
	 */
	public boolean isWarning() {
		return TipoMensagemUFRN.WARNING.equals(tipo);
	}

	/**
	 * Identifica se a mensagem é uma mensagem de informação
	 */
	public boolean isInformation() {
		return TipoMensagemUFRN.INFORMATION.equals(tipo);
	}

	/**
	 * Factory method para criar uma mensagem com um texto e um tipo
	 * passados como parâmetro.
	 */
	public static MensagemAviso valueOf(String msg, TipoMensagemUFRN tipo) {
		MensagemAviso msgAviso = new MensagemAviso();
		msgAviso.setMensagem(msg);
		msgAviso.setTipo(tipo);
		return msgAviso;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mensagem == null) ? 0 : mensagem.hashCode());
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final MensagemAviso other = (MensagemAviso) obj;
		if (mensagem == null) {
			if (other.mensagem != null)
				return false;
		} else if (!mensagem.equals(other.mensagem))
			return false;
		if (tipo == null) {
			if (other.tipo != null)
				return false;
		} else if (!tipo.equals(other.tipo))
			return false;
		return true;
	}

	public Sistema getSistema() {
		return sistema;
	}

	public void setSistema(Sistema sistema) {
		this.sistema = sistema;
	}

	public SubSistema getSubSistema() {
		return subSistema;
	}

	public void setSubSistema(SubSistema subSistema) {
		this.subSistema = subSistema;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getClasseConstantes() {
		return classeConstantes;
	}

	public void setClasseConstantes(String classeConstantes) {
		this.classeConstantes = classeConstantes;
	}

}
