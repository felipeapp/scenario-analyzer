/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 05/05/2010
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.pessoa.dominio.Pais;

/**
 * RESOLUÇÃO Nº 227/2009 - CONSEPE, de 03 de Dezembro de 2009
 * Art.279. 
 * A Mobilidade Estudantil, permite ao aluno cursar componentes curriculares 
 * em outra instituição de ensino superior, legalmente reconhecida.
 * 
 * @author Arlindo Rodrigues
 * 
 */
@Entity
@Table(name="mobilidade_estudantil", schema="ensino")
public class MobilidadeEstudantil implements Validatable {
	/* Indica se a mobilidade é do tipo Interna */
	public static final int INTERNA = 1;
	/* Indica se a mobilidade é do tipo Externa */
	public static final int EXTERNA = 2;
	
	/* Indica se a mobilidade é do sub-tipo Compulsória (apenas para interna) */
	public static final int COMPULSORIA = 1;
	/* Indica se a mobilidade é do sub-tipo Voluntária (apenas para interna) */
	public static final int VOLUNTARIA = 2;
	
	/* Indica se a mobilidade é do sub-tipo Nacional (apenas para externa) */
	public static final int NACIONAL = 1;
	/* Indica se a mobilidade é do sub-tipo Internacional (apenas para externa) */
	public static final int INTERNACIONAL = 2;
	
	/* Descrição dos Tipos  */
	private static final Map<Integer, String> descricoesTipos;
	static {
		descricoesTipos = new HashMap<Integer, String>();
		descricoesTipos.put(INTERNA, "INTERNA");
		descricoesTipos.put(EXTERNA, "EXTERNA");
	}	
	
	/* Descrição dos Sub-Tipos Interna  */
	private static final Map<Integer, String> descricoesSubTiposInterna;
	static {
		descricoesSubTiposInterna = new HashMap<Integer, String>();
		descricoesSubTiposInterna.put(COMPULSORIA, "COMPULSÓRIA");
		descricoesSubTiposInterna.put(VOLUNTARIA, "VOLUNTÁRIA");
	}		
	
	/* Descrição dos Sub-Tipos Externa  */
	private static final Map<Integer, String> descricoesSubTiposExterna;
	static {
		descricoesSubTiposExterna = new HashMap<Integer, String>();
		descricoesSubTiposExterna.put(NACIONAL, "NACIONAL");
		descricoesSubTiposExterna.put(INTERNACIONAL, "INTERNACIONAL");
	}	
	
	/**
	 * Construtor da Classe.
	 */
	public MobilidadeEstudantil() {
		this.ativo = true;
	}
	
	/**
	 * Chave primária da indicação.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_mobilidade_estudantil")
	private int id;		
	
	/**
	 * Discente que terá mobilidade estudantil.
	 */
	@ManyToOne
	@JoinColumn(name = "id_discente")
	private DiscenteGraduacao discente;
	
	/**
	 * Tipo da Mobilidade (Interna ou Externa)
	 */
	private int tipo;
	/**
	 * SubTipo da Mobilidade (Interna "Compulsória ou Voluntária", Externa "Nacional e Internacional")
	 */
	private int subtipo;
	
	/**
	 * Campus de Origem (para o tipo Interna).
	 */
	@ManyToOne
	@JoinColumn(name = "id_campus_origem")
	private CampusIes campusOrigem;
	
	/**
	 * Campus de Destino (para o tipo Externa).
	 */
	@ManyToOne
	@JoinColumn(name = "id_campus_destino")
	private CampusIes campusDestino;	
	
	/**
	 * Instituição de Ensino (para o tipo Externa).
	 */
	@Column(name="ies_externa")
	private String iesExterna;
	
	/**
	 * País de destino (para o tipo Externa)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pais_externa")
	private Pais paisExterna;
	
	/**
	 * Cidade de destino (para o tipo Externa)
	 */
	@Column(name = "cidade_externa")
	private String cidade;	

	/**
	 * Ano da mobilidade estudantil.
	 */
	private Integer ano;
	
	/**
	 * Período da mobilidade estudantil.
	 */
	private Integer periodo;
	
	/**
	 * Quantidade de Períodos que o aluno ficará afastado.
	 */
	@Column(name="numero_periodos")
	private Integer numeroPeriodos;
	
	/**
	 * Observação geral (sairá no histórico)
	 */
	private String observacao;
	
	/**
	 * Indica se a mobilidade está ou não ativa.
	 */
	private boolean ativo;
	
	/** Data do cadastro. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro entrada de quem cadastrou. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;	
	
	/** Data do cancelamento. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cancelamento")
	private Date dataCancelamento;

	/** Registro entrada de quem cancelou. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_cancelamento")
	private RegistroEntrada registroCancelamento;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DiscenteGraduacao getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteGraduacao discente) {
		this.discente = discente;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public int getSubtipo() {
		return subtipo;
	}

	public void setSubtipo(int subtipo) {
		this.subtipo = subtipo;
	}

	public CampusIes getCampusOrigem() {
		return campusOrigem;
	}

	public void setCampusOrigem(CampusIes campusOrigem) {
		this.campusOrigem = campusOrigem;
	}

	public CampusIes getCampusDestino() {
		return campusDestino;
	}

	public void setCampusDestino(CampusIes campusDestino) {
		this.campusDestino = campusDestino;
	}

	public String getIesExterna() {
		return iesExterna;
	}

	public void setIesExterna(String iesExterna) {
		this.iesExterna = iesExterna;
	}

	public Pais getPaisExterna() {
		return paisExterna;
	}

	public void setPaisExterna(Pais paisExterna) {
		this.paisExterna = paisExterna;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}
	
	@Transient
	public String getAnoPeriodoFim(){
		StringBuilder sb = new StringBuilder();
		sb.append(DiscenteHelper.somaSemestres(ano, periodo, numeroPeriodos-1));
		sb.insert(4, ".");
		return sb.toString();
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	public RegistroEntrada getRegistroCancelamento() {
		return registroCancelamento;
	}

	public void setRegistroCancelamento(RegistroEntrada registroCancelamento) {
		this.registroCancelamento = registroCancelamento;
	}
			
	public Integer getNumeroPeriodos() {
		return numeroPeriodos;
	}

	public void setNumeroPeriodos(Integer numeroPeriodos) {
		this.numeroPeriodos = numeroPeriodos;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	@Transient
	public String getDescricaoTipo() {
		return descricoesTipos.get(tipo);
	}	
	
	@Transient
	public static String getDescricaoTipo(int tipoMobilidade) {
		return descricoesTipos.get(tipoMobilidade);
	}	
	
	@Transient
	public static String getDescricaoSubTipo(int tipoMobilidade, int subtipoMobilidade) {		
		if (tipoMobilidade == INTERNA)
			return descricoesSubTiposInterna.get(subtipoMobilidade);
		else if (tipoMobilidade == EXTERNA)			
			return descricoesSubTiposExterna.get(subtipoMobilidade);
		else
			return "NÃO INFORMADO";
	}		

	@Transient
	public String getDescricoesSubTipo() {
		if (tipo == INTERNA)
			return descricoesSubTiposInterna.get(subtipo);
		else if (tipo == EXTERNA)			
			return descricoesSubTiposExterna.get(subtipo);
		else 
			return "NÃO INFORMADO";
	}
	
	@Transient
	public boolean isInterna(){
		return tipo == INTERNA;
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		ValidatorUtil.validateRequired(discente, "Discente", lista);
		
		if (tipo <= 0)
			lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tipo da Mobilidade");
		
		if (subtipo <= 0)
			lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Sub-Tipo da Mobilidade");		
		
		if (tipo == INTERNA){		
			ValidatorUtil.validateRequired(campusDestino, "Campus de Destino", lista);			
		} else if (tipo == EXTERNA){
			ValidatorUtil.validateRequired(iesExterna, "Instituição de Ensino", lista);
			if (subtipo == INTERNACIONAL){
				ValidatorUtil.validateRequired(paisExterna, "País", lista);
				ValidatorUtil.validateRequired(cidade, "Cidade", lista);
			}
		}

		if (ValidatorUtil.isEmpty(ano))
			lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		
		if (ValidatorUtil.isEmpty(periodo))
			lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Período");
		else if (periodo.intValue() > 2)
			lista.addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO,"Período");
			
		if (ValidatorUtil.isEmpty(numeroPeriodos))
			lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Número de Períodos");
		
		ValidatorUtil.validateRequired(observacao, "Observação", lista);
		
		return lista;
	}

}
