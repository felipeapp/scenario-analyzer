/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/10/2009
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que armazena o resumo do processamento da sele��o de fiscais
 * (quantidade por curso, ira m�nimo e m�ximo, etc.) para um determinado grupo
 * (curso, servidores, etc.) de inscritos.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Entity
@Table(name = "resumo_processamento_selecao", schema = "vestibular", uniqueConstraints = {})
public class ResumoProcessamentoSelecao implements PersistDB,
		Comparable<ResumoProcessamentoSelecao> {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_resumo_processamento", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/**
	 * Grupo de fiscais inscritos, correspondendo � fiscais de um munic�pio, por
	 * exemplo.
	 */
	@Column(name = "grupo_selecao")
	private String grupoSelecao;
	
	/**
	 * Subgrupo de fiscais inscritos, dos quais foram selecionados. Este grupo
	 * de corresponder � um curso, ou uma matriz curricular, indicar se �
	 * docente ou servidor, etc.
	 */
	@Column(name = "subgrupo_selecao")
	private String subgrupoSelecao;
	
	/** N�mero de fiscais inscritos no subgrupo. */
	private int inscritos;
	
	/** N�mero de fiscais titulares selecionados. */
	private int titulares;
	
	/** N�mero de fiscais reservas selecionados. */
	private int reservas;
	
	/**
	 * IRA m�nimo dos fiscais discentes do grupo selecionado. O IRA �
	 * contabilizado dos fiscais que foram selecionados. Exclui-se, portanto, os
	 * fiscais que foram selecionados automaticamente (residentes, re-cadastros,
	 * etc.).
	 */
	@Column(name = "ira_minimo")
	private Double iraMinimo;
	
	/**
	 * IRA M�ximo dos fiscais discentes do grupo selecionado. O IRA �
	 * contabilizado dos fiscais que foram selecionados. Exclui-se, portanto, os
	 * fiscais que foram selecionados automaticamente (residentes, re-cadastros,
	 * etc.).
	 */
	@Column(name = "ira_maximo")
	private Double iraMaximo;

	/** Processo Seletivo da sele��o de fiscais. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_processo_seletivo")
	private ProcessoSeletivoVestibular processoSeletivoVestibular;

	/** Construtor padr�o. */
	public ResumoProcessamentoSelecao() {
		titulares = 0;
		reservas = 0;
	}

	/** Construtor parametrizado. */
	public ResumoProcessamentoSelecao(int id) {
		this.id = id;
	}

	/**
	 * Retorna a chave prim�ria.
	 * 
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/**
	 * Seta a chave prim�ria.
	 * 
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Retorna o grupo de fiscais inscritos, correspondendo � fiscais de um
	 * munic�pio, por exemplo.
	 * 
	 * @return
	 */
	public String getGrupoSelecao() {
		return grupoSelecao;
	}

	/**
	 * Seta o grupo de fiscais inscritos, correspondendo � fiscais de um
	 * munic�pio, por exemplo.
	 * 
	 * @param grupoSelecao
	 */
	public void setGrupoSelecao(String grupoSelecao) {
		this.grupoSelecao = grupoSelecao;
	}

	/**
	 * Retorna o n�mero de fiscais titulares selecionados.
	 * 
	 * @return
	 */
	public int getTitulares() {
		return titulares;
	}

	/**
	 * Seta o n�mero de fiscais titulares selecionados.
	 * 
	 * @param titulares
	 */
	public void setTitulares(int titulares) {
		this.titulares = titulares;
	}

	/**
	 * Retorna o n�mero de fiscais reservas selecionados.
	 * 
	 * @return
	 */
	public int getReservas() {
		return reservas;
	}

	/**
	 * Seta o n�mero de fiscais reservas selecionados.
	 * 
	 * @param reservas
	 */
	public void setReservas(int reservas) {
		this.reservas = reservas;
	}

	/**
	 * Retorna o IRA m�nimo dos fiscais discentes do grupo selecionado. O IRA �
	 * contabilizado dos fiscais que foram selecionados. Exclui-se, portanto, os
	 * fiscais que foram selecionados automaticamente (residentes, re-cadastros,
	 * etc.).
	 * 
	 * @return
	 */
	public Double getIraMinimo() {
		return iraMinimo;
	}

	/**
	 * Seta o IRA m�nimo dos fiscais discentes do grupo selecionado. O IRA �
	 * contabilizado dos fiscais que foram selecionados. Exclui-se, portanto, os
	 * fiscais que foram selecionados automaticamente (residentes, re-cadastros,
	 * etc.).
	 * 
	 * @param iraMinimo
	 */
	public void setIraMinimo(Double iraMinimo) {
		if (this.iraMinimo == null || iraMinimo != null && this.iraMinimo > iraMinimo)
			this.iraMinimo = iraMinimo;
	}

	/**
	 * Retorna o IRA M�ximo dos fiscais discentes do grupo selecionado. O IRA �
	 * contabilizado dos fiscais que foram selecionados. Exclui-se, portanto, os
	 * fiscais que foram selecionados automaticamente (residentes, re-cadastros,
	 * etc.).
	 * 
	 * @return
	 */
	public Double getIraMaximo() {
		return iraMaximo;
	}

	/**
	 * Seta o IRA M�ximo dos fiscais discentes do grupo selecionado. O IRA �
	 * contabilizado dos fiscais que foram selecionados. Exclui-se, portanto, os
	 * fiscais que foram selecionados automaticamente (residentes, re-cadastros,
	 * etc.).
	 * 
	 * @param iraMaximo
	 */
	public void setIraMaximo(Double iraMaximo) {
		if (this.iraMaximo == null || iraMaximo != null && this.iraMaximo < iraMaximo)
			this.iraMaximo = iraMaximo;
	}

	/**
	 * Retorna o Processo Seletivo da sele��o de fiscais.
	 * 
	 * @return
	 */
	public ProcessoSeletivoVestibular getProcessoSeletivoVestibular() {
		return processoSeletivoVestibular;
	}

	/**
	 * Seta o Processo Seletivo da sele��o de fiscais.
	 * 
	 * @param processoSeletivoVestibular
	 */
	public void setProcessoSeletivoVestibular(
			ProcessoSeletivoVestibular processoSeletivoVestibular) {
		this.processoSeletivoVestibular = processoSeletivoVestibular;
	}

	/**
	 * Retorna o subgrupo de fiscais inscritos, dos quais foram selecionados.
	 * Este grupo de corresponder � um curso, ou uma matriz curricular, indicar
	 * se � docente ou servidor, etc.
	 * 
	 * @return
	 */
	public String getSubgrupoSelecao() {
		return subgrupoSelecao;
	}

	/**
	 * Seta o subgrupo de fiscais inscritos, dos quais foram selecionados. Este
	 * grupo de corresponder � um curso, ou uma matriz curricular, indicar se �
	 * docente ou servidor, etc.
	 * 
	 * @param subgrupoSelecao
	 */
	public void setSubgrupoSelecao(String subgrupoSelecao) {
		this.subgrupoSelecao = subgrupoSelecao;
	}

	/**
	 * Incrementa o n�mero de fiscais titulares do valor especificado.
	 * 
	 * @param valor
	 */
	public void incrementaTitulares(int valor) {
		this.titulares += valor;
	}

	/**
	 * Incrementa o n�mero de fiscais reservas do valor especificado.
	 * 
	 * @param valor
	 */
	public void incrementaReservas(int valor) {
		this.reservas += valor;
	}

	/**
	 * Verifica se este objeto � igual ao passado no par�metro, comparando o
	 * grupo e o subgrupo se s�o iguais.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ResumoProcessamentoSelecao) {
			ResumoProcessamentoSelecao other = (ResumoProcessamentoSelecao) obj;
			return this.grupoSelecao != null && other.grupoSelecao != null
					&& this.subgrupoSelecao == null
					&& other.subgrupoSelecao == null
					&& this.grupoSelecao.equals(other.grupoSelecao)
					|| this.grupoSelecao != null && other.grupoSelecao != null
					&& this.subgrupoSelecao != null
					&& other.subgrupoSelecao != null
					&& this.grupoSelecao.equals(other.grupoSelecao)
					&& this.subgrupoSelecao.equals(other.subgrupoSelecao);
		} else
			return false;
	}

	/**
	 * Compara este objeto com o passado por par�metro, comparando o grupo e
	 * subgrupo.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ResumoProcessamentoSelecao other) {
		if (this.grupoSelecao != null && other.grupoSelecao != null
				&& this.subgrupoSelecao == null
				&& other.subgrupoSelecao == null) {
			return this.grupoSelecao.compareTo(other.grupoSelecao);
		} else if (this.grupoSelecao != null && other.grupoSelecao != null
				&& this.subgrupoSelecao != null
				&& other.subgrupoSelecao != null) {
			if (this.grupoSelecao.compareTo(other.grupoSelecao) == 0) {
				return this.subgrupoSelecao.compareTo(other.subgrupoSelecao);
			} else {
				return this.grupoSelecao.compareTo(other.grupoSelecao);
			}
		} else
			return 0;
	}

	/**
	 * Retorna uma representa��o textual deste resumo de processamento no
	 * formato: grupo, seguido de v�rgula, seguido do subgrupo, seguido de
	 * v�rgula, seguido o n�mero de fiscais titulares, seguido de v�rgula,
	 * seguido do n�mero de fiscais reservas.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return grupoSelecao + ", " + subgrupoSelecao + ", " + titulares + ", " + reservas;
	}

	/** Retorna o n�mero de fiscais inscritos no grupo.
	 * @return
	 */
	public int getInscritos() {
		return inscritos;
	}

	/** Seta o n�mero de fiscais inscritos no grupo.
	 * @param inscritos
	 */
	public void setInscritos(int inscritos) {
		this.inscritos = inscritos;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

}
