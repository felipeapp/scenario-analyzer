/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Mai 14, 2007
 *
 */
package br.ufrn.sigaa.dominio;

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

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * Bolsista das entidades
 *
 * @author Gleydson
 *
 */
@Deprecated
@Entity
@Table(name = "bolsista")
public class Bolsista implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_bolsista", nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_bolsa")
	private TipoBolsa tipoBolsa = new TipoBolsa();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_discente")
	private Discente discente = new Discente();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_entidade_financiadora")
	private EntidadeFinanciadora entidadeFinanciadora;

	@Column(name = "data_inicio")
	@Temporal(TemporalType.DATE)
	private Date inicio;

	@Column(name = "data_fim")
	@Temporal(TemporalType.DATE)
	private Date fim;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada registroEntrada;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, insertable = true, updatable = true)
	private Date dataCadastro;

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

 	public EntidadeFinanciadora getEntidadeFinanciadora() {
		return entidadeFinanciadora;
	}

	public void setEntidadeFinanciadora(EntidadeFinanciadora entidadeFinanciadora) {
		this.entidadeFinanciadora = entidadeFinanciadora;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TipoBolsa getTipoBolsa() {
		return tipoBolsa;
	}

	public void setTipoBolsa(TipoBolsa tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequiredId(tipoBolsa.getId(), "Campo Obrigatório não informado: Tipo de Bolsa", lista.getMensagens());
		ValidatorUtil.validateRequiredId(discente.getId(), "Campo Obrigatório não informado: Discente", lista.getMensagens());
		ValidatorUtil.validateRequired(inicio, "Data de Inicio", lista.getMensagens());
		
		if( fim != null ){
			if( inicio.compareTo(fim) >= 0 )
				lista.addErro("A data de inicio deve ser anterior a data de fim.");
		}

		return lista;
	}

}
