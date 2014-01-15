/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Apr 3, 2007
 *
 */
package br.ufrn.sigaa.eleicao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Representa um voto para eleição de diretor de centro
 * @author Victor Hugo
 */
@Entity
@Table(schema="comum" , name = "voto", uniqueConstraints = {})
public class Voto extends AbstractMovimento implements Validatable {

	/**
	 * para registrar voto branco e nulo deve ser setado o candidato com os id idênticos as respectivas constantes:
	 */
	public static final int BRANCO = 1;
	public static final int NULO = 2;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_voto", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** candidato que está recebendo o voto */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_candidato", unique = false, nullable = true, insertable = true, updatable = true)
	private Candidato candidato = new Candidato();

	/** discente q está votando */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_discente", unique = false, nullable = true, insertable = true, updatable = true)
	private Discente discente = new Discente();
	
	/** eleição da votação, necessário caso o voto seja nulo ou branco, pois não terá um candidato associado */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_eleicao", unique = false, nullable = true, insertable = true, updatable = true)
	private Eleicao eleicao;

	/** data da cadastro */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	private Date dataCadastro;

	/** registro entrada do discente que está votando */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada registroEntrada;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Candidato getCandidato() {
		return candidato;
	}

	public void setCandidato(Candidato candidato) {
		this.candidato = candidato;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Eleicao getEleicao() {
		return eleicao;
	}

	public void setEleicao(Eleicao eleicao) {
		this.eleicao = eleicao;
	}

	public ListaMensagens validate() {
		return null;
	}

}
