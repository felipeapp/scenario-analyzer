/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 23/07/2007
 * Autor:     Gleydson Lima 
 */
package br.ufrn.comum.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Collection;
import java.util.Date;
import java.util.List;

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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.rh.dominio.CompetenciasSetor;
import br.ufrn.rh.dominio.ProcessoTrabalho;
import br.ufrn.rh.dominio.Servidor;


/**
 *
 * As unidade orcamentárias são facções da instituição que recebem recursos para
 * movimentação.
 *
 * @author Gleydson Lima
 *
 */
@Entity
@Table(name = "unidade", schema="comum", uniqueConstraints = { @UniqueConstraint(columnNames = { "codigo_unidade" }) })
public class Unidade extends UnidadeGeral implements Validatable {

	/** Maior código a ser sugerido */
	public static final long CODIGO_MAXIMO = 920000000000000000L;
	
	/** Município onde está a unidade, conforme base de dados dos correios */
	protected Municipio municipio;


	public Unidade() { }
	
	public Unidade(int id) {
		this.id = id;
	}
	
	
	public Unidade(int id, Long codigo, String nome, String sigla, String nomeCapa, String hierarquia) {
		this.id = id;
		this.codigo = codigo;
		this.nome = nome;
		this.sigla = sigla;
		this.nomeCapa = nomeCapa;
		this.hierarquia = hierarquia;
		
	}
	
	public Unidade(int id, Long codigo, String nome, String sigla, String nomeCapa, String hierarquia, boolean organizacional) {
		this.id = id;
		this.codigo = codigo;
		this.nome = nome;
		this.sigla = sigla;
		this.nomeCapa = nomeCapa;
		this.hierarquia = hierarquia;
		this.organizacional = organizacional;
		
	}
	
	public Unidade(UnidadeGeral unidade) {
		this.id = unidade.getId();
		
		this.avaliacao = unidade.isAvaliacao();
		this.categoria = unidade.getCategoria();
		this.cnpj = unidade.getCnpj();
		this.codigoSiapecad = unidade.getCodigoSiapecad();
		this.codigo = unidade.getCodigo();
		this.compradora = unidade.getCompradora();
		this.compradoraEngenharia = unidade.getCompradoraEngenharia();
		this.dataCadastro = unidade.getDataCadastro();
		this.dataCriacao = unidade.getDataCriacao();
		this.dataExtincao  = unidade.getDataExtincao();
		this.email = unidade.getEmail();
		this.funcaoRemunerada = unidade.isFuncaoRemunerada();
		this.gestoraFrequencia = unidade.isGestoraFrequencia();
		this.hierarquia = unidade.getHierarquia();
		this.hierarquiaOrganizacional = unidade.getHierarquiaOrganizacional();
		this.ambienteOrganizacional = unidade.getAmbienteOrganizacional();
		this.areaAtuacao = unidade.getAreaAtuacao();
		this.classificacaoUnidade = unidade.getClassificacaoUnidade();
		this.gestora = new Unidade (unidade.getGestora().getId());
		if (unidade.getGestoraAcademica() != null)
			this.gestoraAcademica = new Unidade (unidade.getGestoraAcademica().getId());
		this.nivelOrganizacional = unidade.getNivelOrganizacional();
		this.idResponsavelPatrimonial = unidade.getIdResponsavelPatrimonial();
		this.tipoOrganizacional = unidade.getTipoOrganizacional();
		this.responsavelOrganizacional = new Unidade(unidade.getResponsavelOrganizacional().getId()); 
		this.idUsuarioCadastro = unidade.getIdUsuarioCadastro();
		this.nome = unidade.getNome();
		this.nomeAscii = unidade.getNomeAscii();
		this.nomeCapa = unidade.getNomeCapa();
		this.organizacional = unidade.isOrganizacional();
		this.prazoEnvioBolsaFim = unidade.getPrazoEnvioBolsaFim();
		this.prazoEnvioBolsaInicio = unidade.getPrazoEnvioBolsaInicio();
		this.presidenteComissao = unidade.getPresidenteComissao();
		this.sequenciaModalidadeCompra = unidade.getSequenciaModalidadeCompra();
		this.sigla = unidade.getSigla();
		this.siglaAcademica = unidade.getSiglaAcademica();
		this.submetePropostaExtensao = unidade.getSubmetePropostaExtensao();
		this.templateParecerDL = unidade.getTemplateParecerDL();
		this.unidadeAcademica = unidade.isUnidadeAcademica();
		this.unidadeSipac = unidade.isUnidadeSipac();
		this.unidadeOrcamentaria = unidade.isUnidadeOrcamentaria();
		this.unidadeResponsavel = new Unidade(unidade.getUnidadeResponsavel().getId());
		this.metas = unidade.getMetas();
		this.tipo = unidade.getTipo();
		this.tipoAcademica = unidade.getTipoAcademica();
		this.tipoFuncaoRemunerada = unidade.getTipoFuncaoRemunerada();
		this.telefone = unidade.getTelefone();
		this.endereco = unidade.getEndereco();
		this.uf = unidade.getUf();
		this.cep = unidade.getCep();
		this.codigoUnidadeGestoraSIAFI = unidade.getCodigoUnidadeGestoraSIAFI();
		this.codigoGestaoSIAFI = unidade.getCodigoGestaoSIAFI();
		this.protocolizadora = unidade.isProtocolizadora();
		this.radical = unidade.getRadical();
	}
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="comum.unidade_seq") })
	@Column(name = "id_unidade", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return id;
	}

	private List<CompetenciasSetor> competenciasSetor;

	/** Atributo transiente, usado apenas para auxiliar o relatório de setores de trabalho */
	private ProcessoTrabalho processoTrabalho;
	
	/** Atributo transiente, usado apenas para auxiliar o relatório de setores de trabalho */
	private Collection<Servidor> responsaveisAvaliacao;
	
	@Column(name = "codigo_unidade")
	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		super.setCodigo(codigo);
	}

	@Column(name = "nome")
	public String getNome() {
		return nome;
	}
	
	@Column(name = "nome_ascii")
	public String getNomeAscii() {
		return nomeAscii;
	}

	@Column(name = "nome_capa")
	public String getNomeCapa() {
		return nomeCapa;
	}

	@Column(name = "sigla")
	public String getSigla() {
		return sigla;
	}

	@Column(name = "tipo")
	public int getTipo() {
		return tipo;
	}

	@Column(name = "categoria")
	public int getCategoria() {
		return categoria;
	}
	@Column(name = "hierarquia")
	public String getHierarquia() {
		return hierarquia;
	}
	@Column(name = "cnpj")
	public Long getCnpj() {
		return cnpj;
	}
	@Column(name = "codigo_unidade_gestora_siafi")
	public Integer getCodigoUnidadeGestoraSIAFI() {
		return super.getCodigoUnidadeGestoraSIAFI();
	}
	
	@Column(name = "codigo_gestao_siafi")
	public Integer getCodigoGestaoSIAFI() {
		return super.getCodigoGestaoSIAFI();
	}

	@ManyToOne
	@JoinColumn(name="unidade_responsavel")
	public Unidade getUnidadeResponsavel() {
		return (Unidade) unidadeResponsavel;
	}

	public void setUnidadeResponsavel(Unidade unidadeResponsavel) {
		this.unidadeResponsavel = unidadeResponsavel;
	}

	@ManyToOne
	@JoinColumn(name = "id_gestora", unique = false, nullable = false, insertable = true, updatable = true)
	public Unidade getGestora() {
		return (Unidade)gestora;
	}

	public void setGestora(Unidade gestora) {
		this.gestora = gestora;
	}

	@Column(name = "UNIDADE_ORCAMENTARIA")
	@Override
	public boolean isUnidadeOrcamentaria() {
		return unidadeOrcamentaria;
	}

	@Override
	@Column(name = "permite_gestao_centros_gestora_superior")
	public boolean isPermiteGestaoCentrosPelaGestoraSuperior() {
		return permiteGestaoCentrosPelaGestoraSuperior;
	}
	
	@Column(name = "ativo")
	public boolean isAtivo() {
		return ativo;
	}

	@Column(name = "sipac")
	public boolean isUnidadeSipac() {
		return unidadeSipac;
	}

	@Column(name = "unidade_academica")
	public boolean isUnidadeAcademica() {
		return unidadeAcademica;
	}
	@Column(name = "organizacional")
	public boolean isOrganizacional() {
		return organizacional;
	}
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_unid_resp_org")
	public Unidade getResponsavelOrganizacional() {
		return (Unidade) responsavelOrganizacional;
	}

	public void setResponsavelOrganizacional(Unidade responsavelOrganizacional) {
		this.responsavelOrganizacional = responsavelOrganizacional;
	}

	@Column(name = "hierarquia_organizacional")
	public String getHierarquiaOrganizacional() {
		return hierarquiaOrganizacional;
	}

	@ManyToOne
	@JoinColumn(name = "id_tipo_organizacional")
	public TipoUnidadeOrganizacional getTipoOrganizacional() {
		return tipoOrganizacional;
	}

	@Column(name = "codigo_siapecad")
	public Long getCodigoSiapecad() {
		return codigoSiapecad;
	}

	@Override
	public void setCodigoSiapecad(Long codigoSiapecad) {
		super.setCodigoSiapecad(codigoSiapecad);
	}
	
	@Column(name = "email")
	public String getEmail() {
		return email;
	}
		
	@ManyToOne
	@JoinColumn(name = "id_municipio")
	public Municipio getMunicipio() {
		return municipio;
	}
	
	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	@Column(name = "telefones")
	public String getTelefone() {
		return telefone;
	}
	
	@Column(name = "endereco")
	public String getEndereco() {
		return endereco;
	}
		
	@Column(name = "uf")
	public String getUf() {
		return uf;
	}
	
	@Column(name = "cep")
	public String getCep() {
		return cep;
	}
		
	@Temporal(TemporalType.DATE)
	@Column(name = "data_criacao")
	public Date getDataCriacao() {
		return dataCriacao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_extincao")
	public Date getDataExtincao() {
		return dataExtincao;
	}

	@ManyToOne
	@JoinColumn(name = "id_area_atuacao")
	public AreaAtuacaoUnidade getAreaAtuacao() {
		return areaAtuacao;
	}

	@ManyToOne
	@JoinColumn(name = "id_ambiente_organizacional")
	public AmbienteOrganizacionalUnidade getAmbienteOrganizacional() {
		return ambienteOrganizacional;
	}
	
	@ManyToOne
	@JoinColumn(name = "id_classificacao_unidade")
	public ClassificacaoUnidade getClassificacaoUnidade() {
		return classificacaoUnidade;
	}
	
	@Column(name = "avaliacao")
	public boolean isAvaliacao() {
		return avaliacao;
	}

	@Column(name = "funcao_remunerada")
	public boolean isFuncaoRemunerada() {
		return funcaoRemunerada;
	}

	@Column(name = "gestora_frequencia")
	public boolean isGestoraFrequencia() {
		return gestoraFrequencia;
	}


	@Column(name = "tipo_funcao_remunerada")
	public int getTipoFuncaoRemunerada() {
		return tipoFuncaoRemunerada;
	}


	@Column(name = "tipo_academica")
	public Integer getTipoAcademica() {
		return tipoAcademica;
	}

	@Column(name = "sigla_academica")
	public String getSiglaAcademica() {
		return siglaAcademica;
	}
	
	@Column(name = "compradora")
	public Boolean getCompradora() {
		return super.getCompradora();
	}
	
	@Column(name = "compradora_engenharia")
	public Boolean getCompradoraEngenharia() {
		return super.getCompradoraEngenharia();
	}

	
	@ManyToOne
	@JoinColumn(name = "id_gestora_academica")
	public Unidade getGestoraAcademica() {
		return (Unidade)gestoraAcademica;
	}

	public void setGestoraAcademica(Unidade gestoraAcademica) {
		this.gestoraAcademica = gestoraAcademica;
	}
	
	@Column(name = "data_cadastro")
	public Date getDataCadastro() {
		return super.getDataCadastro();
	}
	
	@Column(name = "id_usuario_cadastro")
	public Integer getIdUsuarioCadastro() {
		return super.getIdUsuarioCadastro();
	}

	@Transient
	public List<CompetenciasSetor> getCompetenciasSetor() {
		return competenciasSetor;
	}
	public void setCompetenciasSetor(List<CompetenciasSetor> competenciasSetor) {
		this.competenciasSetor = competenciasSetor;
	}
	
	@ManyToOne
	@JoinColumn(name = "id_nivel_organizacional")
	public NivelOrganizacional getNivelOrganizacional() {
		return super.getNivelOrganizacional();
	}
	
	@Column(name = "submete_proposta_extensao")
	public Boolean getSubmetePropostaExtensao() {
		return super.getSubmetePropostaExtensao();
	}
	
	@Column(name = "id_responsavel")
	public Integer getIdResponsavelPatrimonial() {
		return super.getIdResponsavelPatrimonial();
	}
	
	
	@Column(name = "prazo_envio_bolsa_fim")
	public Integer getPrazoEnvioBolsaFim() {
		return super.getPrazoEnvioBolsaFim();
	}
	
	@Column(name = "prazo_envio_bolsa_inicio")
	public Integer getPrazoEnvioBolsaInicio() {
		return super.getPrazoEnvioBolsaInicio();
	}
	
	@Column(name = "presidente_comissao")
	public String getPresidenteComissao() {
		return super.getPresidenteComissao();
	}
	
	@Column(name = "sequencia_modalidade_compra")
	public Integer getSequenciaModalidadeCompra() {
		return super.getSequenciaModalidadeCompra();
	}
	
	@Column(name = "template_parecer_dl")
	public Integer getTemplateParecerDL() {
		return super.getTemplateParecerDL();
	}
	
	@Column(name="metas")
	public Boolean getMetas() {
		return super.getMetas();
	}
	
	@Column(name="protocolizadora")
	public boolean isProtocolizadora() {
		return protocolizadora;
	}

	@Column(name="radical")
	public Integer getRadical() {
		return radical;
	}
	
	/** Representa qual o turno de tabalho da unidade. */
	public TipoTurno tipoTurno;
	
	/**
	 * Validação para a tela de cadastro
	 */
	public ListaMensagens validate() {
		
		ListaMensagens mensagens = new ListaMensagens();

		validateRequired(getNome(), "Nome", mensagens);
		validateRequired(getSigla(), "Sigla", mensagens);
		validateRequired(getUnidadeResponsavel(), "Unidade Responsável Orçamentária", mensagens);
		validateRequired(getCodigo(), "Código", mensagens);
		
		if (getCodigo() != null){
			ValidatorUtil.validaLong(getCodigo(), "Código", mensagens);
		}
		
		if (getCodigo() != null && getCodigo() >= CODIGO_MAXIMO) {
			mensagens.addErro("O Código não pode ser maior que " + CODIGO_MAXIMO);
		}
		
		validateRequired(getCategoria(), "Categoria", mensagens);
		
		if (isOrganizacional()){			
			validateRequired(getResponsavelOrganizacional(), "Unidade Responsável Organizacional", mensagens);			
			if(getMunicipio() == null || getMunicipio().getId() == 0) {
				mensagens.addErro("Município: Campo obrigatório não informado para Unidades do tipo Organizacional.");
			}			
		}
		
		if (isUnidadeOrcamentaria()){
			validateRequired(getTipo(), "Tipo Orçamentária", mensagens);
			
			if (tipo == UnidadeGeral.UNIDADE_GESTORA && unidadeResponsavel.getTipo() == UnidadeGeral.UNIDADE_FATO) {
				mensagens.addErro("Não é possível cadastrar uma unidade gestora como filha de uma unidade de fato.");
			}
			
		}
		
		if (isUnidadeAcademica()){
			validateRequired(getTipoAcademica(), "Tipo Acadêmica", mensagens);
			validateRequired(getSiglaAcademica(), "Sigla Acadêmica", mensagens);
			validateRequired(getGestoraAcademica(), "Gestora Acadêmica", mensagens);
		}
		
		if (isProtocolizadora()){
			validateRequired(getRadical(), "Radical", mensagens);
		}
		
		return mensagens;
	}
	
	
	/**
	 * Validação para a tela de cadastro/atualização de unidades organizacionais
	 */
	public ListaMensagens validateUniOrganizacional() {
		
		ListaMensagens mensagens = new ListaMensagens();

		ValidatorUtil.validateRequired(getSigla(), "Sigla", mensagens);
		ValidatorUtil.validateRequired(getUnidadeResponsavel(), "Unidade Responsável Orçamentária", mensagens);
		ValidatorUtil.validateRequired(getCodigo(), "Código", mensagens);
		ValidatorUtil.validateRequired(getResponsavelOrganizacional(), "Unidade Responsável Organizacional", mensagens);
		ValidatorUtil.validateRequired(getAmbienteOrganizacional(), "Ambiente Organizacional", mensagens);
		ValidatorUtil.validateRequired(getClassificacaoUnidade(), "Classificação da Unidade", mensagens);
		ValidatorUtil.validateRequired(getNivelOrganizacional(), "Nível Organizacional", mensagens);
		ValidatorUtil.validateRequired(getDataCriacao(), "Data de Criação", mensagens);
		
		return mensagens;
	}

	@Transient
	public boolean isGestora() {
		return tipo == UNIDADE_GESTORA;
	}

	public void setProcessoTrabalho(ProcessoTrabalho processoTrabalho) {
		this.processoTrabalho = processoTrabalho;
	}
	
	@Transient
	public ProcessoTrabalho getProcessoTrabalho() {
		return processoTrabalho;
	}

	public void setResponsaveisAvaliacao(Collection<Servidor> responsaveisAvaliacao) {
		this.responsaveisAvaliacao = responsaveisAvaliacao;
	}
	
	@Transient
	public Collection<Servidor> getResponsaveisAvaliacao() {
		return responsaveisAvaliacao;
	}
	
	/** Retorna a descrição do tipo de unidade acadêmica: Departamento, centro, PPG 
	 * @return
	 */
	@Transient
	public String getTipoAcademicaDesc() {
		if ( tipoAcademica != null ) {
			return TipoUnidadeAcademica.getDescricao(tipoAcademica);
		} else {
			return "NAO DEFINIDA";
		}
	}

	@ManyToOne (fetch = FetchType.EAGER)
	@JoinColumn (name="id_tipo_turno", nullable=true)
	public TipoTurno getTipoTurno() {
		return tipoTurno;
	}

	public void setTipoTurno(TipoTurno tipoTurno) {
		this.tipoTurno = tipoTurno;
	}

	@Column(name="codigo_siorg")
	public Integer getCodigoSIORG() {
		return codigoSIORG;
	}
	
	public void setCodigoSIORG(Integer codigoSIORG) {
		this.codigoSIORG = codigoSIORG;
	}

}
