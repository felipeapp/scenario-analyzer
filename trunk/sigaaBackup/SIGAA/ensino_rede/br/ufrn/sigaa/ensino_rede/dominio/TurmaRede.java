/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 09/08/2013
*/
package br.ufrn.sigaa.ensino_rede.dominio;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;

/**
 * Representa uma turma do ensino em rede.
 * @author Henrique
 *
 */
@Entity
@Table(schema = "ensino_rede", name = "turma_rede")
public class TurmaRede implements PersistDB {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="ensino_rede.turma_seq") })
	@Column(name = "id_turma_rede", nullable = false)
	private int id;
	
	/** Disciplina da turma */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_componente_curricular")
	private ComponenteCurricularRede componente;
	
	/** Ano letivo da turma */
	@Column(name = "ano")
	private int ano;
	
	/** Período letivo da turma */
	@Column(name = "periodo")
	private int periodo;
	
	/** Data Início da turma */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio")
	private Date dataInicio;
	
	/** Data Fim da turma */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim")
	private Date dataFim;
	
	/** Código gerado pelo sistema para identificar a turma. */
	@Column(name = "codigo")
	private String codigo;

	/** Docentes que lecionarão na turma */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "turma")
	private Set<DocenteTurmaRede> docentesTurmas = new HashSet<DocenteTurmaRede>(0);
	
	/** Indica a situação da turma. Pode está aberta, consolidada e outras. */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_situacao_turma", unique = false, nullable = true, insertable = true, updatable = true)
	private SituacaoTurma situacaoTurma;
	
	/** Dados do curso da turma */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_dados_curso_rede")
	private DadosCursoRede dadosCurso;
	
	@Transient
	private DocenteTurmaRede docenteTurma;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ComponenteCurricularRede getComponente() {
		return componente;
	}

	public void setComponente(ComponenteCurricularRede componente) {
		this.componente = componente;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
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

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	public Set<DocenteTurmaRede> getDocentesTurmas() {
		return docentesTurmas;
	}

	public void setDocentesTurmas(Set<DocenteTurmaRede> docentesTurmas) {
		this.docentesTurmas = docentesTurmas;
	}

	/**
	 * Retorna uma descrição completa da turma, apenas sem os nomes dos docentes.
	 * 
	 * @return
	 */
	@Transient
	public String getDescricaoSemDocente() {
		String descricao = (getComponente() != null ? getComponente().getNome() : "")
				+ (getCodigo() != null ? " - Turma: " + getCodigo() : "")
				+ " ("
				+ getAnoPeriodo()
				+ ")";
		return descricao;
	}
	
	/** Retorna o nome da discipina e o código da turma.
	 * @return
	 */
	@Transient
	public String getNome() {
		return getAno() + "." + getPeriodo() + " - "
				+ getComponente().getNome() + " - Turma " + getCodigo();
	}

	
	/**
	 * Retorna uma String com os nomes dos docentes de uma turma.
	 * @return
	 */
	@Transient
	public String getDocentesNomes() {
		if (getDocentesTurmas() == null)
			return null;

		StringBuffer nomes = new StringBuffer();
		Iterator<DocenteTurmaRede> it = getDocentesTurmas().iterator();
		if (it != null) {
			int tamanho = getDocentesTurmas().size();
			for (int i = 1; i <= tamanho; i++) {
				DocenteTurmaRede dt = it.next();
				DocenteRede d = dt.getDocente();
				if (d != null) {
					nomes.append(d.getNome());
					if (i + 1 == tamanho)
						nomes.append(" e ");
					else if (i < tamanho)
						nomes.append(", ");
				}
			}
			if (!nomes.toString().equals("null"))
				return nomes.toString();
		}
		return "";
	}
	
	/** Retorna o ano-período da turma.
	 * @return
	 */
	@Transient
	public String getAnoPeriodo() {
		return ano + ((periodo == 0) ? "" : "." + periodo);
	}

	/** Retorna se a turma foi consolidada.
	 * @return
	 */
	public boolean isConsolidada() {
		return situacaoTurma != null && situacaoTurma.getId() == SituacaoTurma.CONSOLIDADA ? true : false;
	}

	/** Indica se a turma está aberta, ou seja, se a situação dela é ABERTA ou A DEFINIR DOCENTE.
	 * @return
	 */
	public boolean isAberta() {
		return this.getSituacaoTurma().getId() == SituacaoTurma.ABERTA || this.getSituacaoTurma().getId() == SituacaoTurma.A_DEFINIR_DOCENTE;
	}
	
	public void setSituacaoTurma(SituacaoTurma situacaoTurma) {
		this.situacaoTurma = situacaoTurma;
	}

	public SituacaoTurma getSituacaoTurma() {
		return situacaoTurma;
	}

	public void setDadosCurso(DadosCursoRede dadosCurso) {
		this.dadosCurso = dadosCurso;
	}

	public DadosCursoRede getDadosCurso() {
		return dadosCurso;
	}

	public DocenteTurmaRede getDocenteTurma() {
		return docenteTurma;
	}

	public void setDocenteTurma(DocenteTurmaRede docenteTurma) {
		this.docenteTurma = docenteTurma;
	}

}