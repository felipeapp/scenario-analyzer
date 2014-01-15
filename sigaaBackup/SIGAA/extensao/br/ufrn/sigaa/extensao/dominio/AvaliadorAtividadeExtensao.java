/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Avaliador das a��es de extens�o. Formado por membros da comunidade acad�mica
 * (docentes). <br/>
 * Existem 2 grupos de avaliadores de a��es de extens�o.<br/>
 * 
 * Um grupo � formado por docentes selecionados pela Pr�-Reitoria para
 * avaliarem propostas de a��es e s�o conhecidos como avaliadores Ad Hoc
 * (Pareceristas). As avalia��es dos pareceristas servem para auxiliar a
 * avalia��o dos membros do comit� de extens�o analisando o m�rito acad�mico da 
 * proposta.<br/>
 * 
 * O outro grupo de avaliadores � formado por membros do comit� de extens�o.
 * S� o presidente do comit� de extens�o tem poderes para decidir
 * definitivamente o total de recursos recebidos pela proposta de a��o de
 * extens�o.
 * 
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "avaliador_atividade_extensao", schema = "extensao")
public class AvaliadorAtividadeExtensao implements Validatable {

	/** Atributo chave prim�ria */
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

	/** O avaliador s� pode avaliar a��es sobre duas �reas tem�ticas: Esta � a �rea tem�tica principal. */ 
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_area_tematica")
	private AreaTematica areaTematica = new AreaTematica();

	/** O avaliador s� pode avaliar a��es sobre duas �reas tem�ticas: Esta � a �rea tem�tica secund�ria. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_area_tematica2")
	private AreaTematica areaTematica2 = new AreaTematica();

	/** Atributo utilizado pare representar se o Avaliador est� ou n�o ativo no sistema */
	@Column(name = "ativo")
	private Boolean ativo = true;

	/** Data a partir do qual � avaliador */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_inicio")
	private Date dataInicio;

	/** Data a partir do qual deixou de ser avaliador. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_fim")
	private Date dataFim;

	/** Conjunto de avalia��es deste avaliador. */
	@OneToMany(mappedBy = "avaliadorAtividadeExtensao")
	private Set<AvaliacaoAtividade> avaliacoes = new HashSet<AvaliacaoAtividade>();

	/** Atributo utilizado para informar se o Avaliador est� ou n�o selecionado */
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

		ValidatorUtil.validateRequired(dataInicio, "Data In�cio", lista);
		ValidatorUtil.validateRequired(servidor, "Servidor", lista);
		ValidatorUtil.validateRequired(areaTematica, "�rea Tem�tica", lista);

		if (((dataFim != null) && (dataInicio != null)) && (dataFim.before(dataInicio))) {
			lista.addErro("Data de fim deve ser maior que a data de in�cio.");
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
	 * M�todo que retorna todas as avalia��es ativas
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
