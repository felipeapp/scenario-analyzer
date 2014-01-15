/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '05/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Date;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.TipoTrabalhoConclusao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * Art. 72. O trabalho de conclusão de curso corresponde a uma produção
 * acadêmica que expresse as competências e habilidades desenvolvidas pelos
 * alunos, assim como os conhecimentos por estes adquiridos durante o curso de
 * graduação, e tem sua regulamentação em cada colegiado de curso, podendo ser
 * realizado nas formas de monografia, memorial, artigo científico para
 * publicação ou outra forma definida pelo colegiado de curso.
 * Art. 73. O trabalho de conclusão de curso deve ser desenvolvido individualmente, sob a
 * orientação de um professor designado para esse fim.
 * Art.74. É facultada aos cursos, na elaboração dos projetos político-pedagógicos, a previsão de
 * contabilização de carga horária para o trabalho de conclusão de curso.
 *
 * @author Gleydson
 */
@Entity
@Table(name = "trabalho_fim_curso", schema = "prodocente")
public class TrabalhoFimCurso implements Validatable, ViewAtividadeBuilder {

	/** Define o tipo de orientação - orientador */
	public static final char ORIENTADOR = 'O';
	/** Define o tipo de orientação - coorientador */
	public static final char CO_ORIENTADOR = 'C';

	/**
	 * Atributo que define a unicidade do TCC.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
						parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_trabalho_fim_curso", nullable = false)
	private int id;

	/**
	 * Atributo que define o ano de referência do TCC
	 */
	private Integer ano;

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da
	 * base de dados, apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo = true;

	/**
	 * Atributo que define a área de conhecimento CNPQ do TCC
	 */
	@JoinColumn(name = "id_area", referencedColumnName = "id_area_conhecimento_cnpq")
	@ManyToOne(fetch=FetchType.EAGER)
	private AreaConhecimentoCnpq area;

	/**
	 * Atributo que define o título do TCC
	 */
	@Column(name = "titulo")
	private String titulo;

	/**
	 * Atributo que define a sub-área de conhecimento CNPQ do TCC
	 */
	@JoinColumn(name = "id_subarea", referencedColumnName = "id_area_conhecimento_cnpq")
	@ManyToOne(fetch=FetchType.EAGER)
	private AreaConhecimentoCnpq subArea;

	/**
	 * Atributo que define a instituição associado ao TCC
	 */
	@Column(name = "instituicao")
	private String instituicao;

	/**
	 * Atributo que define o nome do orientando do TCC. 
	 */
	@Column(name = "orientando")
	private String orientandoString;

	/**
	 * Atributo que define o departamento associado ao TCC.
	 */
	@JoinColumn(name = "id_departamento", referencedColumnName = "id_unidade")
	@ManyToOne(fetch=FetchType.EAGER)
	private Unidade departamento;

	/**
	 * Atributo que define IES associada ao TCC
	 */
	@JoinColumn(name = "id_instituicao", referencedColumnName = "id")
	@ManyToOne(fetch=FetchType.EAGER)
	private InstituicoesEnsino ies;

	/**
	 * Atributo que define o orientando do TCC
	 */
	@JoinColumn(name = "id_orientando", referencedColumnName = "id_discente")
	@ManyToOne(fetch=FetchType.EAGER)
	private Discente orientando;

	/**
	 * Atributo que define a data da defesa.
	 */
	@Column(name = "data_defesa")
	@Temporal(TemporalType.DATE)
	private Date dataDefesa;

	/**
	 * Atributo que define o número de páginas do TCC
	 */
	@Column(name = "paginas")
	private Integer paginas;

	/**
	 * Atributo que define informações adicionais do TCC
	 */
	@Column(name = "informacao")
	private String informacao;

	/**
	 * Atributo que define o orientandor caso seja servidor
	 */
	@JoinColumn(name = "id_servidor", referencedColumnName = "id_servidor")
	@ManyToOne(fetch=FetchType.EAGER)
	private Servidor servidor;
	
	/**
	 * Atributo que define o orientador caso não seja servidor
	 */
	@JoinColumn(name = "id_docente_externo")
	@ManyToOne(fetch=FetchType.EAGER)
	private DocenteExterno docenteExterno;

	/**
	 * Atributo que define se o TCC foi validado pela PPG.
	 */
	@Column(name = "validacao")
	private Boolean validacao;

	/**
	 * Atributo que define a data de início da orientação. 
	 */
	@Column(name = "data_inicio")
	@Temporal(TemporalType.DATE)
	private Date dataInicio;

	/**
	 * Atributo que define a entidade financiadora quando existir.
	 */
	@JoinColumn(name = "id_agencia_financiadora", referencedColumnName = "id_entidade_financiadora")
	@ManyToOne(fetch=FetchType.EAGER)
	private EntidadeFinanciadora entidadeFinanciadora;

	/**
	 * Atributo que define se o orientado é externo a instituição de ensino.
	 */
	@Column(name = "discente_externo")
	private Boolean discenteExterno;

	/**
	 * Atributo que define o tipo de trabalho de conclusão. 
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_trabalho_conclusao")
	private TipoTrabalhoConclusao tipoTrabalhoConclusao;

	/**
	 * Atributo que define o tipo de orientação.
	 */
    @Column(name= "orientacao")
    private Character orientacao;

    /**
     * Atributo que define o arquivo do TCC anexado.
     */
    @Column(name= "id_arquivo")
    private Integer idArquivo;
    
    /**
     * Atributo que define a matrícula do discente no componente curricular correspondente.
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_matricula_componente")
    private MatriculaComponente matricula;
    
	/** Registro de entrada do usuário que cadastrou a turma. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Data de alteração da turma. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_alteracao")
	@AtualizadoEm
	private Date dataAlteracao;

	/** Creates a new instance of TrabalhoFimCurso */
	public TrabalhoFimCurso() {}

	/**
	 * Creates a new instance of TrabalhoFimCurso with the specified values.
	 *
	 * @param id
	 *            the id of the TrabalhoFimCurso
	 */
	public TrabalhoFimCurso(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the id of this TrabalhoFimCurso.
	 *
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Sets the id of this TrabalhoFimCurso to the specified value.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da
	 * base de dados, apenas o campo ativo será marcado como FALSE
	 *
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public Boolean getAtivo() {
		return this.ativo;
	}

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da
	 * base de dados, apenas o campo ativo será marcado como FALSE
	 *
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * Gets the area of this TrabalhoFimCurso.
	 *
	 * @return the area
	 */
	public AreaConhecimentoCnpq getArea() {
		return this.area;
	}

	/**
	 * Sets the area of this TrabalhoFimCurso to the specified value.
	 *
	 * @param area
	 *            the new area
	 */
	public void setArea(AreaConhecimentoCnpq area) {
		this.area = area;
	}

	/**
	 * Gets the titulo of this TrabalhoFimCurso.
	 *
	 * @return the titulo
	 */
	public String getTitulo() {
		return this.titulo;
	}

	/**
	 * Sets the titulo of this TrabalhoFimCurso to the specified value.
	 *
	 * @param titulo
	 *            the new titulo
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/**
	 * Gets the subArea of this TrabalhoFimCurso.
	 *
	 * @return the subArea
	 */
	public AreaConhecimentoCnpq getSubArea() {
		return this.subArea;
	}

	/**
	 * Sets the subArea of this TrabalhoFimCurso to the specified value.
	 *
	 * @param subArea
	 *            the new subArea
	 */
	public void setSubArea(AreaConhecimentoCnpq subArea) {
		this.subArea = subArea;
	}

	/**
	 * Gets the instituicao of this TrabalhoFimCurso.
	 *
	 * @return the instituicao
	 */
	public String getInstituicao() {
		return this.instituicao;
	}

	/**
	 * Sets the instituicao of this TrabalhoFimCurso to the specified value.
	 *
	 * @param instituicao
	 *            the new instituicao
	 */
	public void setInstituicao(String instituicao) {
		this.instituicao = instituicao;
	}

	/**
	 * Gets the departamento of this TrabalhoFimCurso.
	 *
	 * @return the departamento
	 */
	public Unidade getDepartamento() {
		return this.departamento;
	}

	/**
	 * Sets the departamento of this TrabalhoFimCurso to the specified value.
	 *
	 * @param departamento
	 *            the new departamento
	 */
	public void setDepartamento(Unidade departamento) {
		this.departamento = departamento;
	}

	/**
	 * Gets the dataDefesa of this TrabalhoFimCurso.
	 *
	 * @return the dataDefesa
	 */
	public Date getDataDefesa() {
		return this.dataDefesa;
	}

	/**
	 * Sets the dataDefesa of this TrabalhoFimCurso to the specified value.
	 *
	 * @param dataDefesa
	 *            the new dataDefesa
	 */
	public void setDataDefesa(Date dataDefesa) {
		this.dataDefesa = dataDefesa;
	}

	/**
	 * Gets the paginas of this TrabalhoFimCurso.
	 *
	 * @return the paginas
	 */
	public Integer getPaginas() {
		return this.paginas;
	}

	/**
	 * Sets the paginas of this TrabalhoFimCurso to the specified value.
	 *
	 * @param paginas
	 *            the new paginas
	 */
	public void setPaginas(Integer paginas) {
		this.paginas = paginas;
	}

	/**
	 * Gets the informacao of this TrabalhoFimCurso.
	 *
	 * @return the informacao
	 */
	public String getInformacao() {
		return this.informacao;
	}

	/**
	 * Sets the informacao of this TrabalhoFimCurso to the specified value.
	 *
	 * @param informacao
	 *            the new informacao
	 */
	public void setInformacao(String informacao) {
		this.informacao = informacao;
	}

	/**
	 * Gets the servidor of this TrabalhoFimCurso.
	 *
	 * @return the servidor
	 */
	public Servidor getServidor() {
		return this.servidor;
	}

	/**
	 * Sets the servidor of this TrabalhoFimCurso to the specified value.
	 *
	 * @param servidor
	 *            the new servidor
	 */
	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	/**
	 * Gets the validacao of this TrabalhoFimCurso.
	 *
	 * @return the validacao
	 */
	public Boolean getValidacao() {
		return this.validacao;
	}

	/**
	 * Sets the validacao of this TrabalhoFimCurso to the specified value.
	 *
	 * @param validacao
	 *            the new validacao
	 */
	public void setValidacao(Boolean validacao) {
		this.validacao = validacao;
	}

	/**
	 * Gets the dataInicio of this TrabalhoFimCurso.
	 *
	 * @return the dataInicio
	 */
	public Date getDataInicio() {
		return this.dataInicio;
	}

	/**
	 * Sets the dataInicio of this TrabalhoFimCurso to the specified value.
	 *
	 * @param dataInicio
	 *            the new dataInicio
	 */
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	/**
	 * Gets the entidadeFinanciadora of this TrabalhoFimCurso.
	 *
	 * @return the entidadeFinanciadora
	 */
	public EntidadeFinanciadora getEntidadeFinanciadora() {
		return this.entidadeFinanciadora;
	}

	/**
	 * Sets the entidadeFinanciadora of this TrabalhoFimCurso to the specified
	 * value.
	 *
	 * @param entidadeFinanciadora
	 *            the new entidadeFinanciadora
	 */
	public void setEntidadeFinanciadora(EntidadeFinanciadora entidadeFinanciadora) {
		this.entidadeFinanciadora = entidadeFinanciadora;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	/**
	 * Determines whether another object is equal to this TrabalhoFimCurso. The
	 * result is <code>true</code> if and only if the argument is not null and
	 * is a TrabalhoFimCurso object that has the same id field values as this
	 * object.
	 *
	 * @param object
	 *            the reference object with which to compare
	 * @return <code>true</code> if this object is the same as the argument;
	 *         <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object object) {
		// not set
		if (!(object instanceof TrabalhoFimCurso)) {
			return false;
		}
		TrabalhoFimCurso other = (TrabalhoFimCurso) object;
		if (this.id != other.id && (this.id == 0 || this.id != other.id))
			return false;
		return true;
	}

	/**
	 * Returns a string representation of the object. This implementation
	 * constructs that representation based on the id fields.
	 *
	 * @return a string representation of the object.
	 */
	@Override
	public String toString() {
		return "br.ufrn.sigaa.prodocente.dominio.TrabalhoFimCurso[id=" + id	+ "]";
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validaInt(getAno(), "Ano de Referência", lista);
		ValidatorUtil.validateRequired(getTipoTrabalhoConclusao(), "Tipo de Trabalho de Conclusão", lista);
		ValidatorUtil.validateRequired(getOrientacao(), "Tipo de Orientação", lista);
		ValidatorUtil.validateRequired(getTitulo(), "Título", lista);
		ValidatorUtil.validateMaxLength(getTitulo(), 255, "Título", lista);
		ValidatorUtil.validateMaxLength(getInstituicao(), 255, "Instituição", lista);
		
		// Validações do orientando
		if ( getDiscenteExterno() == null || !getDiscenteExterno() ) {
			if (getOrientando() != null) {
				ValidatorUtil.validateRequired(getOrientando().getPessoa().getNome(), "Orientando", lista);	
			}else {
				ValidatorUtil.validateRequired(getOrientando(), "Orientando", lista);	
			}
		}
		ValidatorUtil.validateRequired(getArea(), "Área de conhecimento", lista);
		ValidatorUtil.validateRequired(getSubArea(), "Sub-área", lista);
		ValidatorUtil.validateRequired(getIes(), "Instituição", lista);

		ValidatorUtil.validateRequired(getDataInicio(), "Data de Início", lista);
		ValidatorUtil.validateRequired(getDataDefesa(), "Data de Defesa", lista);
		
		if ( lista.isEmpty() ) {
			if (this.getDiscenteExterno())
				this.setOrientando(null);

			if ( ValidatorUtil.isEmpty( this.getEntidadeFinanciadora() ) ) {
				this.setEntidadeFinanciadora( null );
			}
		}
		return lista;
	}

	public String getOrientandoString() {
		return orientandoString;
	}

	public void setOrientandoString(String orientandoString) {
		this.orientandoString = orientandoString;
	}

	public String getItemView() {
		return "  <td>" + titulo + "</td>" + "  <td>TFC</td>" + "  <td style=\"text-align:center\">"
				+ Formatador.getInstance().formatarData(dataDefesa) + "</td>";

	}

	public String getTituloView() {
		return "    <td>Nome do Projeto</td>" + "    <td>Tipo</td>"
				+ "    <td style=\"text-align:center\">Período</td>";
	}

	/**
	 * Método que retorna uma mapa.
	 */
	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("titulo", null);
		itens.put("dataDefesa", null);
		itens.put("dataInicio", null);
		return itens;
	}

	/**
	 * Método que retorna o discente interno associado ao estágio.
	 * @return ies
	 */
	public InstituicoesEnsino getIes() {
		return ies;
	}

	/**
	 * Método que popula o discente interno para o estágio.
	 * @param ies
	 */
	public void setIes(InstituicoesEnsino ies) {
		this.ies = ies;
	}

	/**
	 * Método que retorna o orientando do TCC.
	 * @return the orientando
	 */
	public Discente getOrientando() {
		return orientando;
	}

	/**
	 * Método que popula o orientando do TCC
	 * @param orientando
	 */
	public void setOrientando(Discente orientando) {
		this.orientando = orientando;
	}

	public Boolean getDiscenteExterno() {
		return this.discenteExterno;
	}

	public void setDiscenteExterno(Boolean discenteExterno) {
		this.discenteExterno = discenteExterno;
	}

	public float getQtdBase() {
		return 1;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public TipoTrabalhoConclusao getTipoTrabalhoConclusao() {
		return tipoTrabalhoConclusao;
	}

	public void setTipoTrabalhoConclusao(
			TipoTrabalhoConclusao tipoTrabalhoConclusao) {
		this.tipoTrabalhoConclusao = tipoTrabalhoConclusao;
	}

	public Character getOrientacao() {
		return this.orientacao;
	}

	public void setOrientacao(Character orientacao) {
		this.orientacao = orientacao;
	}

	public Integer getIdArquivo() {
		return this.idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}

	public void setDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
	}

	/**
	 * Método que retorna o nome do orientador do TCC
	 * @return
	 */
	public String getOrientadorNome() {
		if( !isEmpty(servidor) )
			return servidor.getNome();
		else if( !isEmpty(docenteExterno) )  
			return docenteExterno.getNome();
		else return null;
	}
	
	/**
	 * Inicializa os atributos 
	 */
	public void iniciarAtributosTransient(){
		
		if (getOrientando() == null) {
			setDiscenteExterno(true);
			setOrientando(new Discente());
		}
		if (getIes() == null) {
			setIes(new InstituicoesEnsino());
		}
		if (getEntidadeFinanciadora() == null) {
			setEntidadeFinanciadora(new EntidadeFinanciadora());
		}
		if (getTipoTrabalhoConclusao() == null) {
			setTipoTrabalhoConclusao(new TipoTrabalhoConclusao());
		}
		if (getArea() == null) {
			setArea(new AreaConhecimentoCnpq());
		}
		if (getSubArea() == null) {
			setSubArea(new AreaConhecimentoCnpq());
		}
		
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(getId());
	}

	public void setMatricula(MatriculaComponente matricula) {
		this.matricula = matricula;
	}

	public MatriculaComponente getMatricula() {
		return matricula;
	}
}
