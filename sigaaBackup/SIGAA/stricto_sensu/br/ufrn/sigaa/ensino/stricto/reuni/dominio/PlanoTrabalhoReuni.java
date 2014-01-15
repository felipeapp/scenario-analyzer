/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 26/01/2009
 */
package br.ufrn.sigaa.ensino.stricto.reuni.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.AreaConhecimentoCienciasTecnologia;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.FormaAtuacaoDocenciaAssistida;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Entidade que representa um plano de trabalho de um discente de pós-graduação
 * para uma bolsa REUNI de assistência ao ensino.
 * 
 * @author wendell
 *
 */
@Entity
@Table(name="plano_trabalho_reuni", schema="stricto_sensu")
public class PlanoTrabalhoReuni implements Validatable{

	/** Indica o Status CADASTRADO */
	public static final int CADASTRADO = 1;
	/** Indica o Status SUBMETIDO */
	public static final int SUBMETIDO = 2;
	/** Indica o Status APROVADO */
	public static final int APROVADO = 3;
	
	/** Descrições dos Status */ 
	private static final Map<Integer, String> DESCRICAO_STATUS;
	static {
		DESCRICAO_STATUS = new HashMap<Integer, String>();
		DESCRICAO_STATUS.put(CADASTRADO, "CADASTRADA");
		DESCRICAO_STATUS.put(SUBMETIDO, "SUBMETIDA");
		DESCRICAO_STATUS.put(APROVADO, "APROVADO");
	}	
	/** Indica a linha de ação do tipo 1 */
	public static final int LINHA_ACAO_1 = 1;
	/** Indica a linha de ação do tipo 2 */
	public static final int LINHA_ACAO_2 = 2;
	/** ID */
	private int id;
	/** Solicitação de Bolsa Reuni vinculado ao plano de trabalho */
	private SolicitacaoBolsasReuni solicitacao;
	/** Linha de Ação Selecionada (1 ou 2)*/
	private Integer linhaAcao;
	/** Área de Conhecimento do Plano de Trabalho */
	private AreaConhecimentoCienciasTecnologia areaConhecimento;
	/** Componente Curricular que será vinculado ao plano de trabalho */
	private ComponenteCurricular componenteCurricular;
	/** Justificativa da escola do componente curricular */
	private String justificativaComponenteCurricular;
	/** Cursos do plano de trabalho */
	private Collection<Curso> cursos;
	/** Docentes do plano de trabalho */
	private Collection<Servidor> docentes;
	/** Formas de Atuação do plano de trabalho */
	private Collection<FormaAtuacaoDocenciaAssistida> formasAtuacao;
	/** Outra forma, caso não for nenhum da lista */
	private String outrasFormasAtuacao;
	/** Objetivos do plano de trabalho */
	private String objetivos;
	/** Quantidade de alunos beneficiados de graduação */
	private Integer numeroAlunosGraduacaoBeneficiados;
    /** Status do plano de trabalho */
	private int status;
	/** Nível Ensino do plano de trabalho */
	private Character nivel;
	/** Registro de Entrada que indica quem realizou o cadastro do plano de trabalho */
	private RegistroEntrada registroCadastro;
	/** Data de Cadastro do plano de trabalho */
	private Date dataCadastro;

	/** Construtor da Classe */
	public PlanoTrabalhoReuni() {
		status = CADASTRADO;
	}
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_plano_trabalho_reuni")
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_solicitacao_bolsas_reuni")
	public SolicitacaoBolsasReuni getSolicitacao() {
		return solicitacao;
	}
	public void setSolicitacao(SolicitacaoBolsasReuni solicitacao) {
		this.solicitacao = solicitacao;
	}

	@Column(name="linha_acao")
	public Integer getLinhaAcao() {
		return linhaAcao;
	}
	public void setLinhaAcao(Integer linha) {
		this.linhaAcao = linha;
	}

	public String getObjetivos() {
		return objetivos;
	}
	public void setObjetivos(String objetivos) {
		this.objetivos = objetivos;
	}

	@Column(name="numero_alunos_graduacao_beneficiados")
	public Integer getNumeroAlunosGraduacaoBeneficiados() {
		return numeroAlunosGraduacaoBeneficiados;
	}
	public void setNumeroAlunosGraduacaoBeneficiados(
			Integer numeroAlunosGraduacaoBeneficiados) {
		this.numeroAlunosGraduacaoBeneficiados = numeroAlunosGraduacaoBeneficiados;
	}

	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	@ManyToMany
	@JoinTable(name = "curso_plano_trabalho_reuni", schema="stricto_sensu",
		    joinColumns = @JoinColumn (name="id_plano_trabalho_reuni"),
		    inverseJoinColumns = @JoinColumn(name="id_curso"))	
	public Collection<Curso> getCursos() {
		return cursos;
	}
	public void setCursos(Collection<Curso> cursos) {
		this.cursos = cursos;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_area_conhecimento_ciencias_tecnologia")
	public AreaConhecimentoCienciasTecnologia getAreaConhecimento() {
		return areaConhecimento;
	}
	public void setAreaConhecimento(
			AreaConhecimentoCienciasTecnologia areaConhecimento) {
		this.areaConhecimento = areaConhecimento;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_componente_curricular")
	public ComponenteCurricular getComponenteCurricular() {
		return componenteCurricular;
	}
	public void setComponenteCurricular(ComponenteCurricular componenteCurricular) {
		this.componenteCurricular = componenteCurricular;
	}

	@ManyToMany
	@JoinTable(name = "docente_plano_trabalho_reuni", schema="stricto_sensu",
		    joinColumns = @JoinColumn (name="id_plano_trabalho_reuni"),
		    inverseJoinColumns = @JoinColumn(name="id_servidor"))	
	public Collection<Servidor> getDocentes() {
		return docentes;
	}
	public void setDocentes(Collection<Servidor> docentes) {
		this.docentes = docentes;
	}

	@ManyToMany
	@JoinTable(name = "forma_atuacao_plano_trabalho_reuni", schema="stricto_sensu",
		    joinColumns = @JoinColumn (name="id_plano_trabalho_reuni"),
		    inverseJoinColumns = @JoinColumn(name="id_forma_atuacao_reuni"))	
	public Collection<FormaAtuacaoDocenciaAssistida> getFormasAtuacao() {
		return formasAtuacao;
	}
	public void setFormasAtuacao(Collection<FormaAtuacaoDocenciaAssistida> formasAtuacao) {
		this.formasAtuacao = formasAtuacao;
	}

	@Column(name="outras_formas_atuacao")
	public String getOutrasFormasAtuacao() {
		return outrasFormasAtuacao;
	}
	public void setOutrasFormasAtuacao(String outrasFormasAtuacao) {
		this.outrasFormasAtuacao = outrasFormasAtuacao;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}
	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	public Date getDataCadastro() {
		return dataCadastro;
	}
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	
	@Column(name="justificativa_componente_curricular")
	public String getJustificativaComponenteCurricular() {
		return justificativaComponenteCurricular;
	}

	public void setJustificativaComponenteCurricular(
			String justificativaComponenteCurricular) {
		this.justificativaComponenteCurricular = justificativaComponenteCurricular;
	}

	public Character getNivel() {
		return nivel;
	}

	/**
	 * Retorna a descrição do nível
	 * @return
	 */
	@Transient
	public String getNivelDescricao() {
		if( nivel != null )
			return NivelEnsino.getDescricao(nivel);
		return "";
	}

	public void setNivel(Character nivel) {
		this.nivel = nivel;
	}

	/**
	 * Adicionar um curso à lista de cursos envolvidos
	 * 
	 * @param curso
	 */
	public void adicionarCurso(Curso curso) {
		if (cursos == null) {
			cursos = new ArrayList<Curso>();
		}
		if (!cursos.contains(curso)) {
			cursos.add(curso);
			Collections.sort((List<Curso>) cursos);
		}
		
	}
	
	/**
	 * Remove um curso da lista de cursos envolvidos
	 * 
	 * @param curso
	 */
	public void removerCurso(Curso curso) {
		if (cursos != null) {
			cursos.remove(curso);
		}
	}
	
	/**
	 * Adiciona um docente à lista de docentes responsáveis pelo componente curricular
	 * 
	 * @param docente
	 */
	public void adicionarDocente(Servidor docente) {
		if (docentes == null) {
			docentes = new ArrayList<Servidor>();	
		}
		if (!docentes.contains(docente)) {
			docentes.add(docente);
			Collections.sort((List<Servidor>) docentes);
		}
	}

	/**
	 * Remove um docente da lista de docentes responsáveis pelo componente curricular
	 * 
	 * @param docente
	 */
	public void removerDocente(Servidor docente) {
		if (docentes != null) {
			docentes.remove(docente);
		}
	}
	
	@Transient
	public  EditalBolsasReuni getEdital() {
		return solicitacao.getEdital();
	}

	@Transient
	public String getDescricaoNivel() {
		return nivel == null ? "NÃO DEFINIDO" : NivelEnsino.getDescricao(nivel);
	}
	
	/**
	 * Indica se o plano de trabalho está com status igual a APROVADO.
	 * @return
	 */
	@Transient
	public boolean isAprovado(){
		return status == APROVADO;
	}
	
	@Transient
	public String getDescricaoStatus() {
		return DESCRICAO_STATUS.get(status);
	}	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		if ( !NivelEnsino.isAlgumNivelStricto(nivel) ) {
			lista.addErro("Nível do Discente do Plano: Campo obrigatório não informado");
		}

		ValidatorUtil.validateRequired(numeroAlunosGraduacaoBeneficiados, "Número de Alunos de Graduação Beneficiados", lista);
		ValidatorUtil.validateMinValue(numeroAlunosGraduacaoBeneficiados, 1, "Número de Alunos de Graduação Beneficiados", lista);

		ValidatorUtil.validateRequired(linhaAcao, "Linha de Ação", lista);
		
		if (linhaAcao != null) {
			if (linhaAcao == LINHA_ACAO_1) {
				ValidatorUtil.validateEmptyCollection("Docentes: É necessário informar, no mínimo, um docente responsável pelo componente curricular.", docentes, lista);
				ValidatorUtil.validateEmptyCollection("Cursos: É necessário definir, no mínimo, um curso de graduação envolvido com o plano de trabalho.", cursos, lista);
			}
			
			if (linhaAcao == LINHA_ACAO_2) {
				ValidatorUtil.validateRequired(areaConhecimento, "Área de Ensino em Ciências e Tecnologia", lista);
			}
		}
		
		ValidatorUtil.validateRequired(objetivos, "Objetivos", lista);
		ValidatorUtil.validateEmptyCollection("Formas de Atuação do Bolsista: É necessário selecionar, no mínimo, uma área de atuação do bolsista.", formasAtuacao, lista);
		
		return lista;
	}


}
