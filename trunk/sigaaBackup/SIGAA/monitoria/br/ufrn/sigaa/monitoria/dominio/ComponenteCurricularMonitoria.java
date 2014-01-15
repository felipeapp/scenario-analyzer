package br.ufrn.sigaa.monitoria.dominio;

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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/*******************************************************************************
 * Componente Curricular específico de monitoria. Determina em quais componentes
 * curriculares o projeto de ensino oferece serviço de monitoria.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "componente_curricular_monitoria", schema = "monitoria")
public class ComponenteCurricularMonitoria implements Validatable,
		Comparable<ComponenteCurricularMonitoria> {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_componente_curricular_monitoria", unique = true, insertable = true, updatable = true)
	private int id;

	/** Indica se o componente terá monitoria no primeiro semestre. */
	@Column(name = "semestre1")
	private boolean semestre1;
	
	/** Indica se o componente terá monitoria no segundo semestre. */
	@Column(name = "semestre2")
	private boolean semestre2;
	
	/** Indica se a monitoria é ativa. */
	@CampoAtivo
	private boolean ativo;

	/** Indica se é obrigatório na seleção. Atualmente o coordenador informa se o componente curricular é obrigatório no cadastro da prova seletiva. */
	@Deprecated
	@Column(name = "obrigatorio_selecao")
	private boolean obrigatorioSelecao;

	/** Componente curricular da monitoria. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_disciplina", unique = false, insertable = true, updatable = true)
	private ComponenteCurricular disciplina = new ComponenteCurricular();

	/** Quantidade de bolsas solicitadas. O projeto pode reservar uma determinada quantidade de bolsas pra um
	 *  componente curricular específico atualmente não está sendo utilizado. */
	@Column(name = "bolsas_solicitadas")	
	private int bolsasSolicitadas;

	/** Plano de trabalho do projeto de monitoria. */
	@Column(name = "plano_trabalho")	
	private String planoTrabalho;

	/** Equipe de docentes envolvidos no projeto. */
	@OneToMany(mappedBy = "componenteCurricularMonitoria")
	@OrderBy(value = "equipeDocente")
	private Set<EquipeDocenteComponente> docentesComponentes = new HashSet<EquipeDocenteComponente>();

	/** Projeto de ensino associado. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_projeto_monitoria", unique = false, insertable = true, updatable = true)
	private ProjetoEnsino projetoEnsino = new ProjetoEnsino();

	/** Carga horária destinada ao projeto. */
	@Column(name = "ch_destinada_projeto")	
	private Integer chDestinadaProjeto;

	/** Avaliacao do monitor. */
	@Column(name = "avaliacao_monitor")	
	private String avaliacaoMonitor;
	
	/** Indica se o componente está selecionado.  Utilizado na view para permitir a seleção do componente e associação com o docente do projeto. */
	@Transient	
	private boolean selecionado;

	/** Indica se o componente curricular é novo, ou seja, não há registro de turma consolidada para ele. */
	@Transient
	private boolean componenteNovo = false;
	
	// Constructors

	/** default constructor */
	public ComponenteCurricularMonitoria() {
	}

	/** Construtor parametrizado.
	 * @param id
	 */
	public ComponenteCurricularMonitoria(int id) {
		this.id = id;
	}

	/** Retorna a chave primária
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return this.id;
	}

	/** Seta a chave primária 
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna o componente curricular da monitoria. 
	 * @return
	 */
	public ComponenteCurricular getDisciplina() {
		return this.disciplina;
	}

	/** Seta o componente curricular da monitoria.
	 * @param disciplina
	 */
	public void setDisciplina(ComponenteCurricular disciplina) {
		this.disciplina = disciplina;
	}

	/** Indica se o componente terá monitoria no primeiro semestre. 
	 * @return
	 */
	public boolean isSemestre1() {
		return semestre1;
	}

	/** Seta se o componente terá monitoria no primeiro semestre. 
	 * @param semestre1
	 */
	public void setSemestre1(boolean semestre1) {
		this.semestre1 = semestre1;
	}

	/** Indica se o componente terá monitoria no segundo semestre. 
	 * @return
	 */
	public boolean isSemestre2() {
		return semestre2;
	}

	/** Seta se o componente terá monitoria no segundo semestre. 
	 * @param semestre2
	 */
	public void setSemestre2(boolean semestre2) {
		this.semestre2 = semestre2;
	}

	/**
	 * Retorna a quantidade de bolsas solicitadas. O projeto pode reservar uma determinada
	 * quantidade de bolsas pra um componente curricular específico atualmente
	 * não está sendo utilizado.
	 * 
	 * @return
	 */
	public int getBolsasSolicitadas() {
		return bolsasSolicitadas;
	}

	/** Seta a quantidade de bolsas solicitadas. O projeto pode reservar uma determinada
	 * quantidade de bolsas pra um componente curricular específico atualmente
	 * não está sendo utilizado.
	 * @param bolsasSolicitadas
	 */
	public void setBolsasSolicitadas(int bolsasSolicitadas) {
		this.bolsasSolicitadas = bolsasSolicitadas;
	}

	/**
	 * Informa as atividades que serão desenvolvidas pelos monitores durante o projeto.
	 * 
	 * @return
	 */
	public String getPlanoTrabalho() {
		return planoTrabalho;
	}

	public void setPlanoTrabalho(String planoTrabalho) {
		this.planoTrabalho = planoTrabalho;
	}

	/**
	 * Retorna todas as relações entre o componente e os seus orientadores
	 * 
	 * @return
	 */
	public Set<EquipeDocenteComponente> getDocentesComponentes() {
		return docentesComponentes;
	}
	
	public Set<EquipeDocenteComponente> getDocentesComponentesValidos() {
		if (docentesComponentes != null) {
			for (Iterator<EquipeDocenteComponente> comp = docentesComponentes.iterator(); comp.hasNext();) {
				EquipeDocenteComponente compDocente = comp.next(); 
				if (!compDocente.isAtivo()) {
					comp.remove();
				}
			}
		}
		return docentesComponentes;
	}

	/** Seta todas as relações entre o componente e os seus orientadores
	 * @param docentesComponentes
	 */
	public void setDocentesComponentes(
			Set<EquipeDocenteComponente> docentesComponentes) {
		this.docentesComponentes = docentesComponentes;
	}

	/** Adiciona um docente a equipe do projeto.
	 * @param docenteComponente
	 * @return
	 */
	public boolean addDocenteComponente(
			EquipeDocenteComponente docenteComponente) {
		docenteComponente.setComponenteCurricularMonitoria(this);
		return this.getDocentesComponentes().add(docenteComponente);
	}

	/** Remove um docente da equipe do projeto.
	 * @param docenteComponente
	 * @return
	 */
	public boolean removeDocenteComponente(
			EquipeDocenteComponente docenteComponente) {
		docenteComponente.setComponenteCurricularMonitoria(null);
		return this.getDocentesComponentes().remove(docenteComponente);
	}

	/** Retorna o projeto de ensino associado.  
	 * @return
	 */
	public ProjetoEnsino getProjetoEnsino() {
		return projetoEnsino;
	}

	/** Seta o projeto de ensino associado.
	 * @param projetoEnsino
	 */
	public void setProjetoEnsino(ProjetoEnsino projetoEnsino) {
		this.projetoEnsino = projetoEnsino;
	}

	/** Compara as chaves primárias deste objeto com o informado.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		ComponenteCurricularMonitoria outro = (ComponenteCurricularMonitoria) obj;
		return disciplina.getId() == outro.getDisciplina().getId();
	}

	/**
	 * Indica se o componente está selecionado. Utilizado na view para permitir
	 * a seleção do componente e associação com o docente do projeto.
	 * 
	 * @return
	 */
	public boolean isSelecionado() {
		return selecionado;
	}

	/**
	 * Seta se o componente está selecionado. Utilizado na view para permitir
	 * a seleção do componente e associação com o docente do projeto.
	 * 
	 * @param selecionado
	 */
	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	/** Retorna uma representação gráfica dos semestres que serão ofertadas as monitorias.
	 * @return
	 */
	public String getSemetres() {
		String semestres = "";
		if (semestre1) {
			semestres += "1 ";
			if (semestre2) {
				semestres += ",";
			}
		}

		if (semestre2) {
			semestres += "2";
		}
		return semestres;
	}

	public ListaMensagens validate() {
		return null;
	}

	/***************************************************************************
	 * Indica se é obrigatório na seleção. A obrigatoriedade agora depende de cada prova cadastrada pelo coordenador
	 * do projeto ele determina quais os componentes da prova e quais são
	 * obrigatórios
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isObrigatorioSelecao() {
		return obrigatorioSelecao;
	}

	/** Seta se é obrigatório na seleção. A obrigatoriedade agora depende de cada prova cadastrada pelo coordenador
	 * do projeto ele determina quais os componentes da prova e quais são
	 * obrigatórios
	 * @param obrigatorioSelecao
	 */
	@Deprecated
	public void setObrigatorioSelecao(boolean obrigatorioSelecao) {
		this.obrigatorioSelecao = obrigatorioSelecao;
	}

	/** Compara o nome deste componente com o informado. 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ComponenteCurricularMonitoria o) {
		return this.getDisciplina().getDetalhes().getNome().compareTo(
				o.getDisciplina().getDetalhes().getNome());
	}

	/** Indica se a monitoria é ativa. 
	 * @return
	 */
	public boolean isAtivo() {
		return ativo;
	}
	
	/** Seta se a monitoria é ativa. 
	 * @param ativo
	 */
	@Column(name = "ativo")
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Indica se o componente curricular é novo, ou seja, não há registro de turma consolidada para ele. 
	 * @return
	 */
	public boolean isComponenteNovo() {
		return componenteNovo;
	}

	/** Seta se o componente curricular é novo, ou seja, não há registro de turma consolidada para ele. 
	 * @param componenteNovo
	 */
	public void setComponenteNovo(boolean componenteNovo) {
		this.componenteNovo = componenteNovo;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public void setChDestinadaProjeto(Integer chDestinadaProjeto) {
		this.chDestinadaProjeto = chDestinadaProjeto;
	}

	public Integer getChDestinadaProjeto() {
		return chDestinadaProjeto;
	}

	public void setAvaliacaoMonitor(String avaliacaoMonitor) {
		this.avaliacaoMonitor = avaliacaoMonitor;
	}

	public String getAvaliacaoMonitor() {
		return avaliacaoMonitor;
	}

}