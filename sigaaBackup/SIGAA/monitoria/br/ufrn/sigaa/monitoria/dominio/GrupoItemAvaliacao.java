package br.ufrn.sigaa.monitoria.dominio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 *<p> Agrupa vários item de uma avaliação de projeto de monitoria</p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "grupo_item_avaliacao", schema = "monitoria")
public class GrupoItemAvaliacao implements Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_grupo")
	private int id;

	private String denominacao;

	/**
	 * P - Projeto de Ensino de Monitoria
	 * R - Relatório do projeto
	 * E - Projeto de Extensão
	 */
	private char tipo;

	@Column(name = "ativo")
	@CampoAtivo
	private boolean ativo;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "grupo")
	private List<ItemAvaliacaoMonitoria> itens = new ArrayList<ItemAvaliacaoMonitoria>(0);

	@Transient
	private List<NotaItemMonitoria> notas = new ArrayList<NotaItemMonitoria>(0);

	@Transient
	private double totalGrupo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<NotaItemMonitoria> getNotas() {
		return notas;
	}

	public void setNotas(List<NotaItemMonitoria> notas) {
		this.notas = notas;
	}

	public List<ItemAvaliacaoMonitoria> getItens() {
		return itens;
	}

	public void setItens(List<ItemAvaliacaoMonitoria> itens) {
		this.itens = itens;
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	public char getTipo() {
		return tipo;
	}

	public void setTipo(char tipo) {
		this.tipo = tipo;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(denominacao, "Descrição", lista);
		ValidatorUtil.validateRequired(tipo, "Tipo de Grupo", lista);		
		
		return lista;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final GrupoItemAvaliacao other = (GrupoItemAvaliacao) obj;
		if (id != other.id)
			return false;
		return true;
	}

	/**
	 * @return the ativo
	 */
	public boolean isAtivo() {
		return ativo;
	}

	/**
	 * @param ativo
	 *            the ativo to set
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public double getTotalGrupo() {
		return totalGrupo;
	}

	public void addNota(double nota) {
		this.totalGrupo += nota;
	}

}
