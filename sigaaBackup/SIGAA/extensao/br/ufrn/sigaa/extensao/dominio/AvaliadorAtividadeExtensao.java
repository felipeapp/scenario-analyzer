/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/02/2008
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/*******************************************************************************
 * Avaliador das ações de extensão. Formado por membros da comunidade acadêmica
 * (docentes). <br/>
 * Existem 2 grupos de avaliadores de ações de extensão.<br/>
 * 
 * Um grupo é formado por docentes selecionados pela Pró-Reitoria para
 * avaliarem propostas de ações e são conhecidos como avaliadores Ad Hoc
 * (Pareceristas). As avaliações dos pareceristas servem para auxiliar a
 * avaliação dos membros do comitê de extensão analisando o mérito acadêmico da 
 * proposta.<br/>
 * 
 * O outro grupo de avaliadores é formado por membros do comitê de extensão.
 * Só o presidente do comitê de extensão tem poderes para decidir
 * definitivamente o total de recursos recebidos pela proposta de ação de
 * extensão.
 * 
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "avaliador_atividade_extensao", schema = "extensao")
public class AvaliadorAtividadeExtensao implements Validatable {

	/** Atributo chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_avaliador_atividade_extensao")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;

	/** Dados sobre o servidor avaliador */ 
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_servidor")
	private Servidor servidor = new Servidor();

	/** Registro de entrada do avaliador */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;

	/** O avaliador só pode avaliar ações sobre duas áreas temáticas: Esta é a área temática principal. */ 
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_area_tematica")
	private AreaTematica areaTematica = new AreaTematica();

	/** O avaliador só pode avaliar ações sobre duas áreas temáticas: Esta é a área temática secundária. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_area_tematica2")
	private AreaTematica areaTematica2 = new AreaTematica();

	/** Atributo utilizado pare representar se o Avaliador está ou não ativo no sistema */
	@Column(name = "ativo")
	private Boolean ativo = true;

	/** Data a partir do qual é avaliador */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_inicio")
	private Date dataInicio;

	/** Data a partir do qual deixou de ser avaliador. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_fim")
	private Date dataFim;

	/** Conjunto de avaliações deste avaliador. */
	@OneToMany(mappedBy = "avaliadorAtividadeExtensao")
	private Set<AvaliacaoAtividade> avaliacoes = new HashSet<AvaliacaoAtividade>();

	/** Atributo utilizado para informar se o Avaliador está ou não selecionado */
	@Transient
	private boolean selecionado = false;

	// Constructors

	/** default constructor */
	public AvaliadorAtividadeExtensao() {
	}

	/** minimal constructor */
	public AvaliadorAtividadeExtensao(int id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "servidor.id");
	}

	public int getId() {
		return this.id;
	}

	public Servidor getServidor() {
		return this.servidor;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public AreaTematica getAreaTematica() {
		return areaTematica;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, servidor.getId());
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(dataInicio, "Data Início", lista);
		ValidatorUtil.validateRequired(servidor, "Servidor", lista);
		ValidatorUtil.validateRequired(areaTematica, "Área Temática", lista);

		if (((dataFim != null) && (dataInicio != null)) && (dataFim.before(dataInicio))) {
			lista.addErro("Data de fim deve ser maior que a data de início.");
		}

		return lista;

	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public void setAreaTematica(AreaTematica areaTematica) {
		this.areaTematica = areaTematica;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	/**
	 * Método que retorna todas as avaliações ativas
	 * @return
	 */
	public Set<AvaliacaoAtividade> getAvaliacoes() {
		for (Iterator<AvaliacaoAtividade> iterator = avaliacoes.iterator(); iterator.hasNext();) {
		    AvaliacaoAtividade ava = iterator.next();
		    if (ava.getStatusAvaliacao().getId() == StatusAvaliacao.AVALIACAO_CANCELADA || !ava.isAtivo()) {		    
			iterator.remove();
		    }		
		}
		return avaliacoes;
	}

	public void setAvaliacoes(Set<AvaliacaoAtividade> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	public AreaTematica getAreaTematica2() {
		return areaTematica2;
	}

	public void setAreaTematica2(AreaTematica areaTematica2) {
		this.areaTematica2 = areaTematica2;
	}

}
