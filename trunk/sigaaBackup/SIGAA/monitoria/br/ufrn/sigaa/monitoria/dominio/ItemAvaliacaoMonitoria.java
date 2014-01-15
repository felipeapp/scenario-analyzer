package br.ufrn.sigaa.monitoria.dominio;

// Generated 09/10/2006 10:44:38 by Hibernate Tools 3.1.0.beta5

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * <p>
 * Representa todos os ítens de uma avaliação de projeto de monitoria.
 * </p>
 * 
 * 
 ******************************************************************************/
@Entity
@Table(name = "item_avaliacao", schema = "monitoria")
public class ItemAvaliacaoMonitoria implements Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_item_avaliacao")
	private int id;

	@Column(name = "descricao")
	private String descricao;
		
	@Temporal(TemporalType.DATE)
	@Column(name = "data_desativacao")
	private Date dataDesativacao;

	@Column(name = "nota_maxima")
	private double notaMaxima;

	@Column(name = "peso")
	private double peso;

	@CampoAtivo
	private boolean ativo;

	@ManyToOne
	@JoinColumn(name = "id_grupo")
	private GrupoItemAvaliacao grupo = new GrupoItemAvaliacao();


	public GrupoItemAvaliacao getGrupo() {
		return grupo;
	}

	public void setGrupo(GrupoItemAvaliacao grupo) {
		this.grupo = grupo;
	}

	/** default constructor */
	public ItemAvaliacaoMonitoria() {
	}

	/** full constructor */
	public ItemAvaliacaoMonitoria(int idItemAvaliacao, String descricao, Date dataDesativacao, int notaMaxima) {
		this.id = idItemAvaliacao;
		this.descricao = descricao;
		this.dataDesativacao = dataDesativacao;
		this.notaMaxima = notaMaxima;
	}

	// Property accessors
	public int getId() {
		return this.id;
	}

	public void setId(int idItemAvaliacao) {
		this.id = idItemAvaliacao;
	}

	
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDataDesativacao() {
		return dataDesativacao;
	}

	public void setDataDesativacao(Date dataDesativacao) {
		this.dataDesativacao = dataDesativacao;
	}

	
	public double getNotaMaxima() {
		return this.notaMaxima;
	}

	public void setNotaMaxima(double notaMaxima) {
		this.notaMaxima = notaMaxima;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição do Item de Avaliação", lista);
		ValidatorUtil.validateRequired(notaMaxima, "Nota Máxima do Item de Avaliação", lista);
		ValidatorUtil.validaDouble(notaMaxima, "Nota Máxima", lista);
		return lista;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
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
		final ItemAvaliacaoMonitoria other = (ItemAvaliacaoMonitoria) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public double getPeso() {
	    return peso;
	}

	public void setPeso(double peso) {
	    this.peso = peso;
	}		
}
