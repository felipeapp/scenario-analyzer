/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/02/2011
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxLength;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Entidade que contém os dados da banca de defesa de TCC do discente.
 * 
 * @author arlindo
 *
 */
@Entity
@Table(name = "banca", schema = "ensino")
public class BancaDefesa implements Validatable {
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="ensino.banca_seq") })
	@Column(name = "id_banca")	
	private int id;	
	
	/** Discente participante da banca. */
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=Discente.class)
	@JoinColumn(name = "id_discente")
	private DiscenteAdapter discente;	
	
	/**  
	 * Matrícula no componente de qualificação associado a esta banca.
	 * Tais como: A situação da turma, o discente, turma, média final do discente, número de faltas, dentre outros.
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_matricula_componente")
	private MatriculaComponente matriculaComponente = new MatriculaComponente();	
	
	/**
	 * Data e Hora referente a realização da banca de defesa.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_defesa")
	private Date dataDefesa;
	
	/**
	 * Título do trabalho
	 */
	@Column(columnDefinition= HibernateUtils.TEXT_COLUMN_DEFINITION)
	private String titulo;	
	
	/**
	 * Total de páginas do trabalho
	 */
	private Integer paginas;

	/**
	 * Resumo do trabalho
	 */
	@Column(columnDefinition= HibernateUtils.TEXT_COLUMN_DEFINITION)
	private String resumo;
	
	/**
	 * Palavras chaves do trabalho
	 */
	@Column(name="palavras_chave", columnDefinition= HibernateUtils.TEXT_COLUMN_DEFINITION)
	private String palavrasChave;	
	
	/** Status atual da banca */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "status")
	private StatusBanca status = new StatusBanca();
	
	/** Local onde será realizado a apresentação da banca */
	@Column(name = "local_realizacao")
	private String local;
		
	/**
	 * Área do CNPQ a qual pertence o trabalho. Pode ser área, subárea ou especialidade 
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_area")
	private AreaConhecimentoCnpq area = new AreaConhecimentoCnpq();
	
	/**
	 * Sub Área do CNPQ a qual pertence o trabalho. Pode ser área, subárea ou especialidade 
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_subarea")
	private AreaConhecimentoCnpq subArea = new AreaConhecimentoCnpq();
	
	/** Observações adicionais na banca */
	@Column(name = "observacao")
	private String observacao;	
	
	/**
	 * Lista de membros que participaram da banca, podendo conter docentes internos e externos.
	 */
	@OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "banca")
	private List<MembroBanca> membrosBanca = new ArrayList<MembroBanca>();	
	
	/**
	 * Data que foi cadastrado
	 */
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	private Date dataCadastro;
	
	/** Identifica quem criou o registro */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada criadoPor;	
	
	/** Identifica quem atualizou por último o registro. */
	@AtualizadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada registroAtualizacao;

	/**  Data da última atualização. */
	@AtualizadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atualizacao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	private Date dataAtualizacao;
	
	/** Vínculo entre o aluno e seu orientador acadêmico. */
	@Transient
	private OrientacaoAtividade orientacaoAtividade;		
	
	/**
	 * Hora.
	 * Campo não obrigatório de preenchimento;
	 */
	@Transient
	private Date hora;	
	
	/** Grande área de conhecimento do CNPq. */
	@Transient
	private AreaConhecimentoCnpq grandeArea;	
	
	/** Verifica se a banca de defesa está ativa */
	@Transient
	public boolean isAtivo(){
		if (status == null)
			return false;
		return status.getId() == StatusBanca.ATIVO;
	}
	
	/** 
	 * Retorna uma descrição textual dos membros da banca.
	 */
	@Transient
	public String getDescricaoMembros() {
		StringBuilder sb = new StringBuilder();
		if (!isEmpty(membrosBanca)) {
			for (MembroBanca membro : membrosBanca) {
				if( !isEmpty( membro.getDescricao() ) )
					sb.append(membro.getDescricao() + System.getProperty("line.separator"));
			}
		}
		return sb.toString();
	}	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	public MatriculaComponente getMatriculaComponente() {
		return matriculaComponente;
	}

	public void setMatriculaComponente(MatriculaComponente matriculaComponente) {
		this.matriculaComponente = matriculaComponente;
	}

	public Date getDataDefesa() {
		return dataDefesa;
	}

	public void setDataDefesa(Date dataDefesa) {
		this.dataDefesa = dataDefesa;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Integer getPaginas() {
		return paginas;
	}

	public void setPaginas(Integer paginas) {
		this.paginas = paginas;
	}

	public String getResumo() {
		return resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo;
	}

	public String getPalavrasChave() {
		return palavrasChave;
	}

	public void setPalavrasChave(String palavrasChave) {
		this.palavrasChave = palavrasChave;
	}

	public StatusBanca getStatus() {
		return status;
	}

	public void setStatus(StatusBanca status) {
		this.status = status;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public AreaConhecimentoCnpq getArea() {
		return area;
	}

	public void setArea(AreaConhecimentoCnpq area) {
		this.area = area;
	}

	public AreaConhecimentoCnpq getSubArea() {
		return subArea;
	}

	public void setSubArea(AreaConhecimentoCnpq subArea) {
		this.subArea = subArea;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getCriadoPor() {
		return criadoPor;
	}

	public void setCriadoPor(RegistroEntrada criadoPor) {
		this.criadoPor = criadoPor;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public List<MembroBanca> getMembrosBanca() {
		return membrosBanca;
	}

	public void setMembrosBanca(List<MembroBanca> membrosBanca) {
		this.membrosBanca = membrosBanca;
	}

	/**
	 * Retorna a orientação da atividade associada da matrícula do componente.
	 * @return
	 */
	public OrientacaoAtividade getOrientacaoAtividade() {
		if( isEmpty(orientacaoAtividade) && !isEmpty( matriculaComponente.getRegistroAtividade() ) ){
				orientacaoAtividade =  matriculaComponente.getRegistroAtividade().getOrientador();
		}
		return orientacaoAtividade;
	}


	public Date getHora() {
		return hora;
	}

	public void setHora(Date hora) {
		this.hora = hora;
	}

	public AreaConhecimentoCnpq getGrandeArea() {
		return grandeArea;
	}

	public void setGrandeArea(AreaConhecimentoCnpq grandeArea) {
		this.grandeArea = grandeArea;
	}

	/** 
	 * Esse método serve para verificar se os campos obrigatórios foram preenchidos, sendo eles os seguintes: 
	 * matriculaComponente, local, título,data da defesa, áreas.
	 * 
	 */	
	@Override
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		
		if (isEmpty(matriculaComponente))
			validateRequired(matriculaComponente, "Atividade Matriculada", erros);		
		
		validateRequired(local, "Local", erros);
		validateMaxLength(local, 100, "Local", erros);

		validateRequired(titulo, "Título", erros);
		validateRequired(paginas, "Páginas", erros);
		
		if( paginas != null)
			ValidatorUtil.validaFloatPositivo( Float.valueOf( paginas ), "Páginas", erros);
		
		validateRequired(dataDefesa, "Data", erros);

		validateRequired(resumo, "Resumo", erros);
		validateRequired(palavrasChave, "Palavra Chave", erros);		
		
		validateRequired(hora, "Hora", erros);
		
		validateRequired(grandeArea, "Grande Área", erros);
		validateRequired(area, "Área", erros);
		
		return erros;
	}
}
