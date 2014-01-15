/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/01/2008
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
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

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * <p>
 * Relaciona um objetivo a uma atividade desenvolvida na ação de extensão. <br/>
 * 
 * Algumas ações de extensão devem ter objetivos bem definidos. atualmente,
 * só projetos devem declarar explicitamente seus objetivos.
 * </p>
 * 
 * @author Ilueny Santos
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "objetivo_atividades")
public class ObjetivoAtividades implements Validatable {

	/** Chave primiária do atividade */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	@Column(name = "id_objetivo_atividades", nullable = false)
	private int id;

	/** Descrição da atividade */
	@Column(name = "descricao")
	private String descricao;

	/** Data de início da realização da atividade */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio")
	private Date dataInicio;

	/** Data fim da realização da atividade */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim")
	private Date dataFim;

	/** Objetivo a qual a atividade está vínculada */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_objetivo")
	private Objetivo objetivo = new Objetivo();

	/** Carga horária da atividade */
	@Column(name = "carga_horaria")
	private Integer cargaHoraria;
	
	/** Membro que participam da atividade */
	@OneToMany(mappedBy = "objetivoAtividade", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
	private Collection<MembroAtividade> membrosAtividade = new ArrayList<MembroAtividade>();

	/** Indica se o andamento é ativo ou não */
	@CampoAtivo(true)
	private boolean ativo = true;
	
	/** Armazena o membro da atividade no qual se está trabalhando */
	@Transient
	private MembroAtividade membroAtividade = new MembroAtividade();
	
	/** Utilizado apenas para remover das listas de objetos transientes. */
	@Transient
	private int posicao;

	public ObjetivoAtividades() {
	}

	public ObjetivoAtividades(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPosicao() {
		return posicao;
	}

	public void setPosicao(int posicao) {
		this.posicao = posicao;
	}

	/** Retorna o caminho e o id do objeto. */
	public String toString() {
		return "br.ufrn.sigaa.extensao.dominio.Objetivo[id=" + id + "]";
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição das Atividades", lista);
		ValidatorUtil.validateRequired(dataInicio, "Período", lista);
		ValidatorUtil.validateRequired(dataFim, "Período", lista);
		return lista;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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

	public Objetivo getObjetivo() {
		return objetivo;
	}

	public void setObjetivo(Objetivo objetivo) {
		this.objetivo = objetivo;
	}

	public Collection<MembroAtividade> getMembrosAtividade() {
		return membrosAtividade;
	}

	public void setMembrosAtividade(Collection<MembroAtividade> membrosAtividade) {
		this.membrosAtividade = membrosAtividade;
	}

	public Integer getCargaHoraria() {
		return cargaHoraria;
	}

	public void setCargaHoraria(Integer cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public MembroAtividade getMembroAtividade() {
		return membroAtividade;
	}

	public void setMembroAtividade(MembroAtividade membroAtividade) {
		this.membroAtividade = membroAtividade;
	}

}