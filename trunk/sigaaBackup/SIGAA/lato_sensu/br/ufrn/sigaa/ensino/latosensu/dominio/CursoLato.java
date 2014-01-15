/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/09/2006'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.model.SelectItem;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.comum.gru.dominio.ConfiguracaoGRU;
import br.ufrn.sigaa.arq.dominio.SeqAno;
import br.ufrn.sigaa.arq.dominio.SeqAnoPK;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ead.dominio.PoloCurso;
import br.ufrn.sigaa.ensino.dominio.CargoAcademico;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.SecretariaUnidade;
import br.ufrn.sigaa.ensino.dominio.TipoTrabalhoConclusao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Classe que representa um curso de pós-graduação lato sensu 
 * (tipicamente especialização ou aperfeiçoamento) 
 * 
 * @author leonardo
 */

@Entity
@Table(name = "curso_lato", schema = "lato_sensu", uniqueConstraints = {})
@PrimaryKeyJoinColumn(name="id_curso")
public class CursoLato extends Curso {

	/** Serve para gerar pegar a sequencia da Disciplina Lato Sensu. */
	public static SeqAnoPK seqDisciplina = new SeqAnoPK(SeqAno.SEQUENCIA_DISCIPLINA_LATO,1);

	/** Representa a  Área de Conhecimento do Curso. */
	private AreaConhecimentoCnpq grandeAreaConhecimentoCnpq = new AreaConhecimentoCnpq();

	/** Representa a  Área de Conhecimento do Curso. */
	private AreaConhecimentoCnpq areaConhecimentoCnpq = new AreaConhecimentoCnpq();
	
	/** Representa a  SubÁrea de Conhecimento do Curso. */
	private AreaConhecimentoCnpq subAreaConhecimentoCnpq = new AreaConhecimentoCnpq();
	
	/** Representa a  Especialidade de Conhecimento do Curso. */
	private AreaConhecimentoCnpq especialidadeAreaConhecimentoCnpq = new AreaConhecimentoCnpq();

	/** Representa a Proposta de criação do Curso. */
	private PropostaCursoLato propostaCurso = new PropostaCursoLato();

	/** Indica qual será a forma do Trabalho de Conclusão do Curso. */
	private TipoTrabalhoConclusao tipoTrabalhoConclusao = new TipoTrabalhoConclusao();

	/** Indica qual será o tipo de Curso. */
	private TipoCursoLato tipoCursoLato = new TipoCursoLato();

	/** Indica se o Curso será pago ou gratuito. */
	private boolean cursoPago;

	/** Indica se o Curso possuirá banca examinadora para avaliação ou não. */
	private boolean bancaExaminadora;

	/** Caso o curso seja pago, indica o valor das mensalidades deste. */
	private Double valor;

	/** Caso o curso seja pago, indica a quantidade de mensalidades à serem pagas. */
	private Integer qtdMensalidades;

	/** Representa a Carga Horária do Curso. */
	private Integer cargaHoraria;

	/** Número de alunos inscritos para o processo seletivo do Curso. */
	private Integer numeroInscritos;

	/** Número de alunos aprovados no processo seletivo do Curso. */
	private Integer numeroSelecionados;

	/** Número de alunos efetivamente matriculados no Curso. */
	private Integer numeroMatriculados;

	/** Indica a data de início do Curso. */
	private Date dataInicio;

	/** Indica a data do término do Curso. */
	private Date dataFim;

	/** Número de vagas oferecidas pelo Curso. */
	private Integer numeroVagas;

	/** Número de vagas oferecidas pelo Curso ao Servidores Internos. */
	private Integer numeroVagasServidores;

	/** Indica se o Curso emitirá ou não certificado. */
	private boolean certificado;

	/** Indica o setor responsável pela emissão dos certificados do curso. */
	private String setorResponsavelCertificado;

	/** Caso o curso seja gratuito, indica a entidade financiadora do curso. */
	private String entidadeFinanciadora;

	/** Representa um conjunto de públicos-alvo associadas ao Curso. */
	private Set<PublicoAlvoCurso> publicosAlvoCurso = new HashSet<PublicoAlvoCurso>(0);

	/** Reprecenta o corpo docente de um Curso. */
	private Set<CorpoDocenteCursoLato> cursosServidores = new HashSet<CorpoDocenteCursoLato>(0);

	/** Representa as Turmas de Entrada do Curso.  */
	private Set<TurmaEntradaLato> turmasEntrada = new HashSet<TurmaEntradaLato>(0);

	/** Representa os Componentes Curriculares pertencentes ao Curso. */
	private Set<ComponenteCursoLato> componentesCursoLato = new HashSet<ComponenteCursoLato>(0);

	/** Indica a presença de outros documentos necessários pelo Curso. */
	private Set<OutrosDocumentos> outrosDocumentos = new HashSet<OutrosDocumentos>(0);

	/** Representa um conjunto de pessoas jurídicas associadas ao Curso. */
	private Set<ParceriaLato> parcerias = new HashSet<ParceriaLato>(0);
	
	/** Justificativa para prorrogação do Curso, quando solicitada. */
	private String justificativa;

	/** Variável Transiente. Utilizada como auxílio para alguma listagem requerida. */
	private Collection<PoloCurso> polosCursos = new ArrayList<PoloCurso>();
	
	/** Indica a cidade Pólo do Curso, caso este seja um curso de ensino à distância. */
	private PoloCurso poloCurso = new PoloCurso();
	
	/** Representa a Unidade do Curso. */
	private UnidadeCursoLato unidadeCursoLato = new UnidadeCursoLato();
	
	/** Variável Transiente. Utilizada como auxílio para alguma listagem requerida. */
	private Collection<UnidadeCursoLato> unidadesCursoLato = new  ArrayList<UnidadeCursoLato>();
	
	/** Coordenador do Curso Lato Sensu */
	private CoordenacaoCurso coordenador = new CoordenacaoCurso();

	/** Vice-Coordenador do Curso Lato Sensu */
	private CoordenacaoCurso viceCoordenador = new CoordenacaoCurso();

	/** Corpo docente da disciplina do Curso Lato Sensu */
	private CorpoDocenteDisciplinaLato equipeLato = new CorpoDocenteDisciplinaLato();
	
	/** Antigo Coordenador do Curso Lato Sensu */
	private CoordenacaoCurso coordenadorAntigo = new CoordenacaoCurso();
	
	/** Antigo Vice-Coordenador do Curso Lato Sensu */
	private CoordenacaoCurso viceCoordenadorAntigo = new CoordenacaoCurso();
	
	/** Armazena a coleção do corpo docente da disciplina do Curso Lato Sensu */					   
	private Collection<CorpoDocenteDisciplinaLato> equipesLato = new ArrayList<CorpoDocenteDisciplinaLato>();
	
	/** Disciplina do Curso Lato Sensu */
	private ComponenteCurricular disciplina = new ComponenteCurricular();
	
	/** Armazena o componente Curso Lato Sensu */
	private ComponenteCursoLato componenteCursoLato = new ComponenteCursoLato();
	
	/** Para a geração de um combo com todos os docente do Curso Lato */
	private ArrayList<SelectItem> docentes = new ArrayList<SelectItem>();
	
	/** Secretário do Curso Lato Sensu */
	private SecretariaUnidade secretario = null;

	/** Antigo Secretário do Curso Lato Sensu */
	private SecretariaUnidade secretarioAntigo = new SecretariaUnidade();

	/** Histórico das situações do Curso Lato Sensu */
	private HistoricoSituacao historicoSituacao = new HistoricoSituacao();
	
	/** Coleção dos histórico das situações do Curso Lato Sensu */
	private Collection<HistoricoSituacao> historicoSituacoes = new ArrayList<HistoricoSituacao>();
	
	/** Combo com as formas de Seleção do Curso Lato Sensu */
	private List<String> formaSelecao = new ArrayList<String>();
	
	/** Combo com as formas de Avaliação do Curso Lato Sensu */
	private List<String> formasAvaliacao = new ArrayList<String>();
	
	/** Coleção de polos de EAD do Curso Lato Sensu */
	private Collection<Polo> polosEAD = new ArrayList<Polo>();
	
	/** Coleção do Corpo Docente do Curso Lato */
	private Collection<CorpoDocenteCursoLato> corpoDocenteProposta = new ArrayList<CorpoDocenteCursoLato>();
	
	/** Passo atual do cadastro de proposta lato sensu */
	private TipoPassoPropostaLato tipoPassoPropostaLato;
	
	/** Aba atual do cadastro */
	private String aba = "";

	/** habilita ou não a edição */
	private boolean cpfEncontrado;
	
	/** Unidade Orçamentária do Curso */
	private Unidade unidadeOrcamentaria = new Unidade(); 
	
	private Double andamento;
	
	/** Financiamento curso Lato */ 
	private FinanciamentoCursoLato financiamentoCursoLato;
	
	/** ID da configuração da GRU utilizada para pagamento da taxa de inscrição no processo seletivo. */
	private Integer idConfiguracaoGRUInscricao;
	
	/** ID da configuração da GRU utilizada para pagamento da mensalidade do curso. */
	private Integer idConfiguracaoGRUMensalidade;
	
	/** Configuração da GRU utilizada para pagamento da taxa de inscrição no processo seletivo. */
	private ConfiguracaoGRU configuracaoGRUInscricao;
	
	/** Configuração da GRU utilizada para pagamento da mensalidade do curso. */
	private ConfiguracaoGRU configuracaoGRUMensalidade;
	
	/** Data de vencimento da primeira mensalidade. As mensalidades subsequentes terão a mesma data base (dia). */
	private Date dataPrimeiraMensalidade;
	
	/** Título dado ao discente em reconhecimento a conclusão do curso. */
	private String habilitacaoEspecifica;
	
	// Constructors

	/** default constructor */
	public CursoLato() {
	}

	/** minimal constructor */
	public CursoLato(int id){
		super.setId( id );
	}
	
	public CursoLato(int id, String nome, int idSituacaoProposta, String descricaoSituacaoProposta, int idCoordenador, String nomeCoordenador) {
		super.setId( id );
		super.setNome(nome);
		this.propostaCurso = new PropostaCursoLato(idSituacaoProposta, descricaoSituacaoProposta, idCoordenador, nomeCoordenador);
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_grande_area_conhecimento_cnpq", unique = false, nullable = true, insertable = true, updatable = true)
	public AreaConhecimentoCnpq getGrandeAreaConhecimentoCnpq() {
		return grandeAreaConhecimentoCnpq;
	}

	public void setGrandeAreaConhecimentoCnpq(
			AreaConhecimentoCnpq grandeAreaConhecimentoCnpq) {
		this.grandeAreaConhecimentoCnpq = grandeAreaConhecimentoCnpq;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_area_conhecimento_cnpq", unique = false, nullable = true, insertable = true, updatable = true)
	public AreaConhecimentoCnpq getAreaConhecimentoCnpq() {
		return this.areaConhecimentoCnpq;
	}

	public void setAreaConhecimentoCnpq(
			AreaConhecimentoCnpq areaConhecimentoCnpq) {
		this.areaConhecimentoCnpq = areaConhecimentoCnpq;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_subarea_conhecimento_cnpq", unique = false, nullable = true, insertable = true, updatable = true)
	public AreaConhecimentoCnpq getSubAreaConhecimentoCnpq() {
		return subAreaConhecimentoCnpq;
	}

	public void setSubAreaConhecimentoCnpq(
			AreaConhecimentoCnpq subAreaConhecimentoCnpq) {
		this.subAreaConhecimentoCnpq = subAreaConhecimentoCnpq;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_proposta", unique = false, nullable = true, insertable = true, updatable = true)
	public PropostaCursoLato getPropostaCurso() {
		return this.propostaCurso;
	}

	public void setPropostaCurso(PropostaCursoLato propostaCurso) {
		this.propostaCurso = propostaCurso;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_especialidade_conhecimento_cnpq", unique = false, nullable = true, insertable = true, updatable = true)
	public AreaConhecimentoCnpq getEspecialidadeAreaConhecimentoCnpq() {
		return especialidadeAreaConhecimentoCnpq;
	}

	public void setEspecialidadeAreaConhecimentoCnpq(
			AreaConhecimentoCnpq especialidadeAreaConhecimentoCnpq) {
		this.especialidadeAreaConhecimentoCnpq = especialidadeAreaConhecimentoCnpq;
	}
	
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_trabalho_conclusao", unique = false, nullable = true, insertable = true, updatable = true)
	public TipoTrabalhoConclusao getTipoTrabalhoConclusao() {
		return this.tipoTrabalhoConclusao;
	}

	public void setTipoTrabalhoConclusao(
			TipoTrabalhoConclusao tipoTrabalhoConclusao) {
		this.tipoTrabalhoConclusao = tipoTrabalhoConclusao;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_curso_lato", unique = false, nullable = true, insertable = true, updatable = true)
	public TipoCursoLato getTipoCursoLato() {
		return this.tipoCursoLato;
	}

	public void setTipoCursoLato(TipoCursoLato tipoCursoLato) {
		this.tipoCursoLato = tipoCursoLato;
	}

	@Column(name = "curso_pago", unique = false, nullable = true, insertable = true, updatable = true)
	public boolean isCursoPago() {
		return this.cursoPago;
	}

	public void setCursoPago(boolean cursoPago) {
		this.cursoPago = cursoPago;
	}

	@Column(name = "banca_examinadora", unique = false, nullable = true, insertable = true, updatable = true)
	public boolean isBancaExaminadora() {
		return this.bancaExaminadora;
	}

	public void setBancaExaminadora(boolean bancaExaminadora) {
		this.bancaExaminadora = bancaExaminadora;
	}

	@Column(name = "valor", unique = false, nullable = true, insertable = true, updatable = true, precision = 8)
	public Double getValor() {
		return this.valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	@Column(name = "qtd_mensalidades", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getQtdMensalidades() {
		return qtdMensalidades;
	}

	public void setQtdMensalidades(Integer qtdMensalidades) {
		this.qtdMensalidades = qtdMensalidades;
	}

	@Column(name = "ch", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getCargaHoraria() {
		return this.cargaHoraria;
	}

	public void setCargaHoraria(Integer cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	@Column(name = "numero_inscritos", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getNumeroInscritos() {
		return this.numeroInscritos;
	}

	public void setNumeroInscritos(Integer numeroInscritos) {
		this.numeroInscritos = numeroInscritos;
	}

	@Column(name = "numero_selecionados", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getNumeroSelecionados() {
		return this.numeroSelecionados;
	}

	public void setNumeroSelecionados(Integer numeroSelecionados) {
		this.numeroSelecionados = numeroSelecionados;
	}

	@Column(name = "numero_matriculados", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getNumeroMatriculados() {
		return this.numeroMatriculados;
	}

	public void setNumeroMatriculados(Integer numeroMatriculados) {
		this.numeroMatriculados = numeroMatriculados;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataInicio() {
		return this.dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataFim() {
		return this.dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	@Column(name = "numero_vagas", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getNumeroVagas() {
		return this.numeroVagas;
	}

	public void setNumeroVagas(Integer numeroVagas) {
		this.numeroVagas = numeroVagas;
	}

	@Column(name = "certificado", unique = false, nullable = true, insertable = true, updatable = true)
	public boolean isCertificado() {
		return this.certificado;
	}

	public void setCertificado(boolean certificado) {
		this.certificado = certificado;
	}

	@Column(name = "setor_responsavel_certificado", unique = false, nullable = true, insertable = true, updatable = true)
	public String getSetorResponsavelCertificado() {
		return this.setorResponsavelCertificado;
	}

	public void setSetorResponsavelCertificado(String setorResponsavelCertificado) {
		this.setorResponsavelCertificado = setorResponsavelCertificado;
	}

	@Column(name = "entidade_financiadora", unique = false, nullable = true, insertable = true, updatable = true)
	public String getEntidadeFinanciadora() {
		return entidadeFinanciadora;
	}

	public void setEntidadeFinanciadora(String entidadeFinanciadora) {
		this.entidadeFinanciadora = entidadeFinanciadora;
	}

	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "cursoLato")
	public Set<PublicoAlvoCurso> getPublicosAlvoCurso() {
		return this.publicosAlvoCurso;
	}

	public void setPublicosAlvoCurso(Set<PublicoAlvoCurso> publicoAlvoCursos) {
		this.publicosAlvoCurso = publicoAlvoCursos;
	}

	@OneToMany(cascade = {}, fetch = FetchType.LAZY, mappedBy = "cursoLato")
	@OrderBy(value="servidor")
	public Set<CorpoDocenteCursoLato> getCursosServidores() {
		return this.cursosServidores;
	}

	public void setCursosServidores(Set<CorpoDocenteCursoLato> cursoServidors) {
		this.cursosServidores = cursoServidors;
	}

	@OneToMany(cascade = {}, fetch = FetchType.LAZY, mappedBy = "cursoLato")
	public Set<TurmaEntradaLato> getTurmasEntrada() {
		return this.turmasEntrada;
	}

	public void setTurmasEntrada(Set<TurmaEntradaLato> turmaEntradas) {
		this.turmasEntrada = turmaEntradas;
	}

	@OneToMany(cascade = {}, fetch = FetchType.LAZY, mappedBy = "cursoLato")
	@OrderBy(value="disciplina")
	public Set<ComponenteCursoLato> getComponentesCursoLato() {
		return this.componentesCursoLato;
	}

	public void setComponentesCursoLato(Set<ComponenteCursoLato> componentesCursoLato) {
		this.componentesCursoLato = componentesCursoLato;
	}

	@OneToMany(cascade = { }, fetch = FetchType.LAZY, mappedBy = "curso")
	public Set<OutrosDocumentos> getOutrosDocumentos() {
		return this.outrosDocumentos;
	}

	public void setOutrosDocumentos(Set<OutrosDocumentos> outrosDocumentoses) {
		this.outrosDocumentos = outrosDocumentoses;
	}

	@OneToMany(cascade = { }, fetch = FetchType.LAZY, mappedBy = "cursoLato")
	public Set<ParceriaLato> getParcerias() {
		return parcerias;
	}

	public void setParcerias(Set<ParceriaLato> parcerias) {
		this.parcerias = parcerias;
	}

	/** Adiciona o Público Alvo do Curso*/
	public boolean addPublicoAlvoCurso(PublicoAlvoCurso obj) {
		obj.setCursoLato(this);
		return publicosAlvoCurso.add(obj);
	}

	/** Remove o público Alvo do Curso */
	public boolean removePublicoAlvoCurso(PublicoAlvoCurso obj) {
		obj.setCursoLato(null);
		return publicosAlvoCurso.remove(obj);
	}

	/** Adiciona um Servidor a um Curso  */
	public boolean addCursoServidor(CorpoDocenteCursoLato obj) {
		obj.setCursoLato(this);
		return cursosServidores.add(obj);
	}
	
	/** Remove um Servidor a um Curso  */
	public boolean removeCursoServidor(CorpoDocenteCursoLato obj) {
		obj.setCursoLato(null);
		return cursosServidores.remove(obj);
	}

	/** Adiciona uma Turma de Entrada */
	public boolean addTurmaEntrada(TurmaEntradaLato obj) {
		obj.setCursoLato(this);
		return turmasEntrada.add(obj);
	}

	/** Remove uma Turma de Entrada */
	public boolean removeTurmaEntrada(TurmaEntradaLato obj) {
		obj.setCursoLato(null);
		return turmasEntrada.remove(obj);
	}

	/** Adiciona um Componente Curso Lato */
	public boolean addComponenteCursoLato(ComponenteCursoLato obj) {
		obj.setCursoLato(this);
		return componentesCursoLato.add(obj);
	}
	
	/** Remove um Componente Curso Lato */
	public boolean removeComponenteCursoLato(ComponenteCursoLato obj) {
		obj.setCursoLato(null);
		return componentesCursoLato.remove(obj);
	}

	/** Adiciona um Outros Documentos */
	public boolean addOutrosDocumentos(OutrosDocumentos obj) {
		obj.setCurso(this);
		return outrosDocumentos.add(obj);
	}
	
	/** Remove um Outros Documentos */
	public boolean removeOutrosDocumentos(OutrosDocumentos obj) {
		obj.setCurso(null);
		return outrosDocumentos.remove(obj);
	}

	/** Adiciona um Parceria Lato */
	public boolean addParceriaLato(ParceriaLato obj) {
		obj.setCursoLato(this);
		return parcerias.add(obj);
	}
	
	/** Remove um Parceria Lato */
	public boolean removeParceriaLato(ParceriaLato obj) {
		obj.setCursoLato(null);
		return parcerias.remove(obj);

	}

	/**
	 * Implementação do método equals comparando o id do CursoLato atual com a passada como parâmetro.
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/**
	 * Implementação do método hashCode comparando o id do CursoLato atual com a passada como parâmetro.
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(getId());
	}

	/** Retorna todos os Servidores do Curso Lato */
	@Transient
	public Set<Servidor> getServidores() {
		Set<Servidor> servidores = new HashSet<Servidor>();
		for (CorpoDocenteCursoLato servidor : cursosServidores) {
			servidores.add(servidor.getServidor());
		}
		return servidores;
	}

	/** Retorna todas as Disciplinas do Curso Lato */
	@Override
	@Transient
	public Set<ComponenteCurricular> getDisciplinas(){
		Set<ComponenteCurricular> disciplinas = new HashSet<ComponenteCurricular>();
		for(ComponenteCursoLato ccl: componentesCursoLato)
			disciplinas.add(ccl.getDisciplina());
		return disciplinas;
	}

	/** Adiciona uma disciplina ao Componente Curso Lato */
	public boolean addDisciplina(ComponenteCurricular disciplina){
		ComponenteCursoLato ccl = new ComponenteCursoLato();
		ccl.setDisciplina(disciplina);
		return this.addComponenteCursoLato(ccl);
	}

	/** Remove uma disciplina ao Componente Curso Lato */
	public boolean removeDisciplina(ComponenteCurricular disciplina){
		for(ComponenteCursoLato ccl: componentesCursoLato){
			if(ccl.getDisciplina().equals( disciplina ))
				return this.removeComponenteCursoLato(ccl);
		}
		return false;
	}


	@Transient
	public boolean isFinalizado(){
		return getDataFim().before(new Date(System.currentTimeMillis()));
	}
	
	/** Retorna a data Limite do Relatório */
	@Transient
	public Date getDataLimiteRelatorio() {
		Calendar c = Calendar.getInstance();
		c.setTime(getDataFim());
		c.add(Calendar.DATE, 45);
		return c.getTime();
	}
	
	/** Retorna a coordenação do Curso */
	@Transient
	public CoordenacaoCurso getCoordenacao(){
		CoordenacaoCurso ativa = null;
		for (CoordenacaoCurso cc : getCoordenacoesCursos()) {
			if (cc.isAtivo() && cc.getCargoAcademico().getId() == CargoAcademico.COORDENACAO) {
				ativa = cc;
				break;
			}
		}
		return ativa;
	}
	
	/** Retorna a vice coordenação do Curso */
	@Transient
	public CoordenacaoCurso getViceCoordenacao(){
		CoordenacaoCurso ativa = null;
		for (CoordenacaoCurso cc : getCoordenacoesCursos()) {
			if (cc.isAtivo() && cc.getCargoAcademico().getId() == CargoAcademico.VICE_COORDENACAO) {
				ativa = cc;
				break;
			}
		}
		return ativa;
	}
	
	@Column(name = "numero_vagas_servidores", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getNumeroVagasServidores() {
		return numeroVagasServidores;
	}

	public void setNumeroVagasServidores(Integer numeroVagasServidores) {
		this.numeroVagasServidores = numeroVagasServidores;
	}

	/** Retorna o período do Curso */
	@Transient
	public String getPeriodoCurso(){
		StringBuffer sb = new StringBuffer();
		Calendar c = Calendar.getInstance();
		c.setTime(dataInicio);
		sb.append("("+c.get(Calendar.YEAR));
		c.setTime(dataFim);
		sb.append("-"+c.get(Calendar.YEAR)+")");
		return sb.toString();
	}

	@Transient
	public Collection<PoloCurso> getPolosCursos() {
		return polosCursos;
	}

	public void setPolosCursos(Collection<PoloCurso> polosCursos) {
		this.polosCursos = polosCursos;
	}

	@Transient
	public PoloCurso getPoloCurso() {
		return poloCurso;
	}

	public void setPoloCurso(PoloCurso poloCurso) {
		this.poloCurso = poloCurso;
	}

	@Column(name = "justificativa", unique = false, nullable = true, insertable = true, updatable = true)
	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}
	
	@Transient	
	public UnidadeCursoLato getUnidadeCursoLato() {
		return unidadeCursoLato;
	}

	public void setUnidadeCursoLato(UnidadeCursoLato unidadeCursoLato) {
		this.unidadeCursoLato = unidadeCursoLato;
	}
	
	@Transient
	public Collection<UnidadeCursoLato> getUnidadesCursoLato() {
		return unidadesCursoLato;
	}

	public void setUnidadesCursoLato(Collection<UnidadeCursoLato> unidadesCursoLato) {
		this.unidadesCursoLato = unidadesCursoLato;
	}

	@Transient
	public CoordenacaoCurso getCoordenador() {
		return coordenador;
	}

	public void setCoordenador(CoordenacaoCurso coordenador) {
		this.coordenador = coordenador;
	}

	@Transient
	public CoordenacaoCurso getViceCoordenador() {
		return viceCoordenador;
	}

	public void setViceCoordenador(CoordenacaoCurso viceCoordenador) {
		this.viceCoordenador = viceCoordenador;
	}

	@Transient
	public CorpoDocenteDisciplinaLato getEquipeLato() {
		return equipeLato;
	}

	public void setEquipeLato(CorpoDocenteDisciplinaLato equipeLato) {
		this.equipeLato = equipeLato;
	}

	@Transient
	public CoordenacaoCurso getCoordenadorAntigo() {
		return coordenadorAntigo;
	}

	public void setCoordenadorAntigo(CoordenacaoCurso coordenadorAntigo) {
		this.coordenadorAntigo = coordenadorAntigo;
	}

	@Transient
	public CoordenacaoCurso getViceCoordenadorAntigo() {
		return viceCoordenadorAntigo;
	}

	public void setViceCoordenadorAntigo(CoordenacaoCurso viceCoordenadorAntigo) {
		this.viceCoordenadorAntigo = viceCoordenadorAntigo;
	}

	@Transient
	public Collection<CorpoDocenteDisciplinaLato> getEquipesLato() {
		return equipesLato;
	}

	public void setEquipesLato(Collection<CorpoDocenteDisciplinaLato> equipesLato) {
		this.equipesLato = equipesLato;
	}

	@Transient
	public ComponenteCurricular getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(ComponenteCurricular disciplina) {
		this.disciplina = disciplina;
	}

	@Transient
	public ComponenteCursoLato getComponenteCursoLato() {
		return componenteCursoLato;
	}

	public void setComponenteCursoLato(ComponenteCursoLato componenteCursoLato) {
		this.componenteCursoLato = componenteCursoLato;
	}

	@Transient
	public ArrayList<SelectItem> getDocentes() {
		return docentes;
	}

	public void setDocentes(ArrayList<SelectItem> docentes) {
		this.docentes = docentes;
	}
	
	@Transient
	public SecretariaUnidade getSecretario() {
		return secretario;
	}

	public void setSecretario(SecretariaUnidade secretario) {
		this.secretario = secretario;
	}

	@Transient
	public SecretariaUnidade getSecretarioAntigo() {
		return secretarioAntigo;
	}

	public void setSecretarioAntigo(SecretariaUnidade secretarioAntigo) {
		this.secretarioAntigo = secretarioAntigo;
	}

	@Transient
	public HistoricoSituacao getHistoricoSituacao() {
		return historicoSituacao;
	}

	public void setHistoricoSituacao(HistoricoSituacao historicoSituacao) {
		this.historicoSituacao = historicoSituacao;
	}

	@Transient
	public Collection<HistoricoSituacao> getHistoricoSituacoes() {
		return historicoSituacoes;
	}

	public void setHistoricoSituacoes(
			Collection<HistoricoSituacao> historicoSituacoes) {
		this.historicoSituacoes = historicoSituacoes;
	}

	@Transient
	public List<String> getFormaSelecao() {
		return formaSelecao;
	}
	
	public void setFormasAvaliacao(List<String> formasAvaliacao) {
		this.formasAvaliacao = formasAvaliacao;
	}

	@Transient
	public List<String> getFormasAvaliacao() {
		return formasAvaliacao;
	}
	
	public void setFormaSelecao(List<String> formaSelecao) {
		this.formaSelecao = formaSelecao;
	}


	@Transient
	public Collection<Polo> getPolosEAD() {
		return polosEAD;
	}

	public void setPolosEAD(Collection<Polo> polosEAD) {
		this.polosEAD = polosEAD;
	}

	@Transient
	public Collection<CorpoDocenteCursoLato> getCorpoDocenteProposta() {
		return corpoDocenteProposta;
	}

	public void setCorpoDocenteProposta(
			Collection<CorpoDocenteCursoLato> corpoDocenteProposta) {
		this.corpoDocenteProposta = corpoDocenteProposta;
	}

	@Transient
	public TipoPassoPropostaLato getTipoPassoPropostaLato() {
		return tipoPassoPropostaLato;
	}

	public void setTipoPassoPropostaLato(TipoPassoPropostaLato tipoPassoPropostaLato) {
		this.tipoPassoPropostaLato = tipoPassoPropostaLato;
	}

	@Transient
	public String getAba() {
		return aba;
	}

	public void setAba(String aba) {
		this.aba = aba;
	}
	
	@Transient
	public boolean isCpfEncontrado() {
		return cpfEncontrado;
	}

	public void setCpfEncontrado(boolean cpfEncontrado) {
		this.cpfEncontrado = cpfEncontrado;
	}

	/**
	 * Validação feita na hora de cadastrar os dados gerais da proposta
	 * 
	 */
	@Override
	public ListaMensagens validate() {
		return null;
	}
	
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_unidade_orcamentaria", unique = false, nullable = true, insertable = true, updatable = true)
	public Unidade getUnidadeOrcamentaria() {
		return unidadeOrcamentaria;
	}

	public void setUnidadeOrcamentaria(Unidade unidadeOrcamentaria) {
		this.unidadeOrcamentaria = unidadeOrcamentaria;
	}

	/**
	 * Indica se deve ser exibido ou não o Despacho.
	 * @return
	 */
	public boolean exibirDespacho(){
		return (!propostaCurso.getSituacaoProposta().isValida() && !propostaCurso.getHistorico().getObservacoes().equals(""));
	}

	/** Compara o curso, a unidade, a data de Inicio do Mandato, a data Fim do Mandato, o email para Contato, o servidor, 
	 * o cargo Academico deste Curso Lato com o passado por parâmetro. */
	public boolean equalsCoordenador(Object obj) {
		return EqualsUtil.testTransientEquals(this, obj, "curso", "unidade", "dataInicioMandato", "dataFimMandato", "emailContato",
				"servidor", "cargoAcademico");
	}

	@Transient
	public String getNomeCurso() {
		return ("Curso de " + tipoCursoLato.getDescricao() + " em").toUpperCase();
	}

	@Transient
	public Double getAndamento() {
		return andamento;
	}

	public void setAndamento(Double andamento) {
		this.andamento = andamento;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_financiamento", unique = false, nullable = true, insertable = true, updatable = true)
	public FinanciamentoCursoLato getFinanciamentoCursoLato() {
		return financiamentoCursoLato;
	}

	public void setFinanciamentoCursoLato(
			FinanciamentoCursoLato financiamentoCursoLato) {
		this.financiamentoCursoLato = financiamentoCursoLato;
	}

	@Column(name = "id_configuracao_gru_inscricao")
	public Integer getIdConfiguracaoGRUInscricao() {
		return idConfiguracaoGRUInscricao;
	}

	public void setIdConfiguracaoGRUInscricao(Integer idConfiguracaoGRUInscricao) {
		this.idConfiguracaoGRUInscricao = idConfiguracaoGRUInscricao;
	}

	@Column(name = "id_configuracao_gru_mensalidade")
	public Integer getIdConfiguracaoGRUMensalidade() {
		return idConfiguracaoGRUMensalidade;
	}

	public void setIdConfiguracaoGRUMensalidade(Integer idConfiguracaoGRUMensalidade) {
		this.idConfiguracaoGRUMensalidade = idConfiguracaoGRUMensalidade;
	}

	@Transient
	public ConfiguracaoGRU getConfiguracaoGRUInscricao() {
		return configuracaoGRUInscricao;
	}
	
	public void setConfiguracaoGRUInscricao(ConfiguracaoGRU configuracaoGRUInscricao) {
		this.configuracaoGRUInscricao = configuracaoGRUInscricao;
	}

	@Transient
	public ConfiguracaoGRU getConfiguracaoGRUMensalidade() {
		return configuracaoGRUMensalidade;
	}

	public void setConfiguracaoGRUMensalidade(
			ConfiguracaoGRU configuracaoGRUMensalidade) {
		this.configuracaoGRUMensalidade = configuracaoGRUMensalidade;
	}

	@Column(name = "data_primeira_mensalidade")
	public Date getDataPrimeiraMensalidade() {
		return dataPrimeiraMensalidade;
	}

	public void setDataPrimeiraMensalidade(Date dataPrimeiraMensalidade) {
		this.dataPrimeiraMensalidade = dataPrimeiraMensalidade;
	}

	@Column(name = "habilitacao_especifica")
	public String getHabilitacaoEspecifica() {
		return habilitacaoEspecifica;
	}

	public void setHabilitacaoEspecifica(String habilitacaoEspecifica) {
		this.habilitacaoEspecifica = habilitacaoEspecifica;
	}

}