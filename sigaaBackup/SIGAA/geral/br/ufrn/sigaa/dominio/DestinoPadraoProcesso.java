/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '15/01/2007'
 *
 */
package br.ufrn.sigaa.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Classe usada para configurar os destinos padr�es de processos nas
 * v�rios opera��es do Sistema.
 *
 * @author ricardo
 *
 */
@Entity
@Table(name = "destino_padrao_processo", uniqueConstraints = {})
public class DestinoPadraoProcesso implements PersistDB {

	private int id;

	/** Identifica��o do Comando da opera��o */
	private int codigoOperacao;

	/** Unidade Gestora da unidade de origem.
	 * A partir dela � buscado o destino do processo
	 */
	private int idUnidadeGestora;

	/** Unidade de destino do processo */
	private int idUnidade;

	public DestinoPadraoProcesso() {
	}

	@Column(name = "codigo_operacao", unique = false, nullable = true, insertable = true, updatable = true)
	public int getCodigoOperacao() {
		return codigoOperacao;
	}

	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return id;
	}

	@Column(name = "id_unidade", unique = false, nullable = false, insertable = true, updatable = true)
	public int getIdUnidade() {
		return idUnidade;
	}

	@Column(name = "id_unidade_gestora", unique = false, nullable = false, insertable = true, updatable = true)
	public int getIdUnidadeGestora() {
		return idUnidadeGestora;
	}

	public void setCodigoOperacao(int codigoOperacao) {
		this.codigoOperacao = codigoOperacao;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIdUnidade(int idUnidade) {
		this.idUnidade = idUnidade;
	}

	public void setIdUnidadeGestora(int idUnidadeGestora) {
		this.idUnidadeGestora = idUnidadeGestora;
	}



}
