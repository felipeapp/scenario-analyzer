/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 11/06/2008
 */
package br.ufrn.arq.seguranca.autenticacao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Classe que controla a bloqueio para a emissão de documentos autenticados.
 * Os documentos podem ser bloqueados por tipo e/ou nível de ensino.
 *
 * @author Ricardo Wendell
 * @author Gleydson Lima
 *
 */
@Entity
@Table(name = "bloqueio_documento_autenticado", schema="comum")
public class BloqueioDocumentoAutenticado {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;

	/** Tipo do documento autenticado a ser bloqueado. (Definido na classe TipoDocumentoAutenticado) */
	@Column(name = "tipo_documento")
	private int tipoDocumento;

	/** Nível de ensino do documento a ser bloqueado (opcional) */
	@Column(name = "nivel_ensino")
	private char nivelEnsino;

	/** Flag que define se um determinado tipo de documento está bloqueado */
	private boolean bloqueado;

	public BloqueioDocumentoAutenticado() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(int tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public char getNivelEnsino() {
		return nivelEnsino;
	}

	public void setNivelEnsino(char nivelEnsino) {
		this.nivelEnsino = nivelEnsino;
	}

	public boolean isBloqueado() {
		return bloqueado;
	}

	public void setBloqueado(boolean bloqueado) {
		this.bloqueado = bloqueado;
	}

}
