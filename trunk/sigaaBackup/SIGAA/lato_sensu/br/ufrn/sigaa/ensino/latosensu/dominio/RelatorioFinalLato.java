/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '09/03/2007'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Classe que registra as informações do relatório final
 * de um curso lato sensu.
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name = "relatorio_final_lato", schema = "lato_sensu")
public class RelatorioFinalLato implements Validatable {
		
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_relatorio_final_lato", nullable = false)
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "id_curso_lato", nullable = false)
	private CursoLato curso;
	
	@Column(name = "numero_processo")
	private Integer numeroProcesso;
	
	@Column(name = "ano_processo")
	private Integer anoProcesso;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio_realizado")
	private Date dataInicioRealizado;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim_realizado")
	private Date dataFimRealizado;
	
	@Column(name = "ch_realizada")
	private Integer chRealizada;
	
	@Column(name = "introducao")
	private String introducao;
	
	@Column(name = "realizacao")
	private String realizacao;
	
	@Column(name = "conclusao")
	private String conclusao;
	
	@Column(name = "numero_portaria")
	private Integer numeroPortaria;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "data_portaria")
	private Date dataPortaria;
	
	@Column(name = "qtd_inscritos")
	private Integer numeroInscritos;
	
	@Column(name = "qtd_selecionados")
	private Integer numeroSelecionados;
	
	@Column(name = "qtd_matriculados")
	private Integer numeroMatriculados;
	
	@Column(name = "qtd_concluintes")
	private Integer numeroConcluintes;
	
	@Column(name = "qtd_outra_ies")
	private Integer numeroOutraIes;
	
	@Column(name = "qtd_prof_basica")
	private Integer numeroProfEdBasica;
	
	@Column(name = "qtd_profissional_liberal")
	private Integer numeroProfissionalLiberal;
	
	@Column(name = "qtd_executivos")
	private Integer numeroExecutivos;
	
	@Column(name = "selecao")
	private String selecao;
	
	@Column(name = "meios_divulgacao")
	private String meiosDivulgacao;
	
	@Column(name = "instituicoes")
	private String instituicoes;
	
	@Column(name = "disciplinas")
	private String disciplinas;
	
	private Integer status;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "relatorio")
	private Collection<ArquivoLato> arquivos = new ArrayList<ArquivoLato>();

	@Transient
	private String parecer = "";
	
	public RelatorioFinalLato(){
		
	}
	
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(numeroProcesso, "Número do Processo", erros);
		ValidatorUtil.validateRequired(anoProcesso, "Ano do Processo", erros);
		ValidatorUtil.validateRequired(dataInicioRealizado, "Período de realização: Início", erros);
		ValidatorUtil.validateRequired(dataFimRealizado, "Período de realização: Fim", erros);
		ValidatorUtil.validateRequired(chRealizada, "Carga Horária Realizada", erros);
		ValidatorUtil.validateRequired(numeroInscritos, "Número de Inscritos", erros);
		ValidatorUtil.validateRequired(numeroSelecionados, "Número de Selecionados", erros);
		ValidatorUtil.validateRequired(numeroMatriculados, "Número de Matriculados", erros);
		ValidatorUtil.validateRequired(numeroConcluintes, "Número de Concluintes", erros);
		ValidatorUtil.validateRequired(numeroOutraIes, "Número de Outra IES", erros);
		ValidatorUtil.validateRequired(numeroProfEdBasica, "Número de Prof. Ed. Básica", erros);
		ValidatorUtil.validateRequired(numeroProfissionalLiberal, "Número de Profissional Liberal", erros);
		ValidatorUtil.validateRequired(numeroExecutivos, "Número de Executivos", erros);
		return erros;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getChRealizada() {
		return chRealizada;
	}

	public void setChRealizada(Integer chRealizada) {
		this.chRealizada = chRealizada;
	}

	public String getConclusao() {
		return conclusao;
	}

	public void setConclusao(String conclusao) {
		this.conclusao = conclusao;
	}

	public CursoLato getCurso() {
		return curso;
	}

	public void setCurso(CursoLato curso) {
		this.curso = curso;
	}

	public Date getDataFimRealizado() {
		return dataFimRealizado;
	}

	public void setDataFimRealizado(Date dataFimRealizado) {
		this.dataFimRealizado = dataFimRealizado;
	}

	public Date getDataInicioRealizado() {
		return dataInicioRealizado;
	}

	public void setDataInicioRealizado(Date dataInicioRealizado) {
		this.dataInicioRealizado = dataInicioRealizado;
	}

	public String getIntroducao() {
		return introducao;
	}

	public void setIntroducao(String introducao) {
		this.introducao = introducao;
	}

	public Integer getNumeroProcesso() {
		return numeroProcesso;
	}

	public void setNumeroProcesso(Integer numeroProcesso) {
		this.numeroProcesso = numeroProcesso;
	}

	public String getRealizacao() {
		return realizacao;
	}

	public void setRealizacao(String realizacao) {
		this.realizacao = realizacao;
	}

	public Date getDataPortaria() {
		return dataPortaria;
	}

	public void setDataPortaria(Date dataPortaria) {
		this.dataPortaria = dataPortaria;
	}

	public Integer getNumeroPortaria() {
		return numeroPortaria;
	}

	public void setNumeroPortaria(Integer numeroPortaria) {
		this.numeroPortaria = numeroPortaria;
	}

	public Integer getNumeroConcluintes() {
		return numeroConcluintes;
	}

	public void setNumeroConcluintes(Integer numeroConcluintes) {
		this.numeroConcluintes = numeroConcluintes;
	}

	public Integer getNumeroExecutivos() {
		return numeroExecutivos;
	}

	public void setNumeroExecutivos(Integer numeroExecutivos) {
		this.numeroExecutivos = numeroExecutivos;
	}

	public Integer getNumeroInscritos() {
		return numeroInscritos;
	}

	public void setNumeroInscritos(Integer numeroInscritos) {
		this.numeroInscritos = numeroInscritos;
	}

	public Integer getNumeroMatriculados() {
		return numeroMatriculados;
	}

	public void setNumeroMatriculados(Integer numeroMatriculados) {
		this.numeroMatriculados = numeroMatriculados;
	}

	public Integer getNumeroOutraIes() {
		return numeroOutraIes;
	}

	public void setNumeroOutraIes(Integer numeroOutraIes) {
		this.numeroOutraIes = numeroOutraIes;
	}

	public Integer getNumeroProfEdBasica() {
		return numeroProfEdBasica;
	}

	public void setNumeroProfEdBasica(Integer numeroProfEdBasica) {
		this.numeroProfEdBasica = numeroProfEdBasica;
	}

	public Integer getNumeroProfissionalLiberal() {
		return numeroProfissionalLiberal;
	}

	public void setNumeroProfissionalLiberal(Integer numeroProfissionalLiberal) {
		this.numeroProfissionalLiberal = numeroProfissionalLiberal;
	}

	public Integer getNumeroSelecionados() {
		return numeroSelecionados;
	}

	public void setNumeroSelecionados(Integer numeroSelecionados) {
		this.numeroSelecionados = numeroSelecionados;
	}

	public String getInstituicoes() {
		return instituicoes;
	}

	public void setInstituicoes(String instituicoes) {
		this.instituicoes = instituicoes;
	}

	public String getMeiosDivulgacao() {
		return meiosDivulgacao;
	}

	public void setMeiosDivulgacao(String meiosDivulgacao) {
		this.meiosDivulgacao = meiosDivulgacao;
	}

	public String getSelecao() {
		return selecao;
	}

	public void setSelecao(String selecao) {
		this.selecao = selecao;
	}

	public String getDisciplinas() {
		return disciplinas;
	}

	public void setDisciplinas(String disciplinas) {
		this.disciplinas = disciplinas;
	}

	public Integer getAnoProcesso() {
		return anoProcesso;
	}

	public void setAnoProcesso(Integer anoProcesso) {
		this.anoProcesso = anoProcesso;
	}

	public Collection<ArquivoLato> getArquivos() {
		return arquivos;
	}

	public void setArquivos(Collection<ArquivoLato> arquivos) {
		this.arquivos = arquivos;
	}
	
	/**
	 * Adiciona um Arquivo a lista de anexos do relatório final
	 *
	 * @param arquivo anexo ao relatório
	 * @return
	 */
	public boolean addArquivo(ArquivoLato arquivo){
		arquivo.setRelatorio(this);
		return arquivos.add(arquivo);
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@Transient
	public String getStatusString(){
		return TipoStatusRelatorioFinalLato.getDescricao(status);
	}

	public String getParecer() {
		return parecer;
	}

	public void setParecer(String parecer) {
		this.parecer = parecer;
	}

}