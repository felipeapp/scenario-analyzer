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

@Entity
@Table(schema = "ensino_rede", name = "turma_rede")
public class TurmaRede implements PersistDB {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="ensino_rede.hibernate_sequence") })
	@Column(name = "id_turma_rede", nullable = false)
	private int id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_componente_curricular")
	private ComponenteCurricularRede componente;
	
	@Column(name = "ano")
	private int ano;
	
	@Column(name = "periodo")
	private int periodo;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio")
	private Date dataInicio;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim")
	private Date dataFim;
	
	@Column(name = "codigo")
	private String codigo;

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "turma")
	private Set<DocenteTurmaRede> docentesTurmas = new HashSet<DocenteTurmaRede>(0);
	
	/** Indica a situação da turma. Pode está aberta, consolidada e outras. */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_situacao_turma", unique = false, nullable = true, insertable = true, updatable = true)
	private SituacaoTurma situacaoTurma;
	
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
	
}
