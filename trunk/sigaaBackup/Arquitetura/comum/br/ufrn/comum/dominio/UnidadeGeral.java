/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 27/11/2003
 */
package br.ufrn.comum.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;

/**
 *
 * Uma Unidade Orçamentária são facções da instituição que recebem recursos para
 * movimentação.
 *
 * @author Gleydson Lima
 *
 */
public class UnidadeGeral implements PersistDB {

	// Id da UFRN
	public static final int UNIDADE_DIREITO_GLOBAL = ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.UNIDADE_DIREITO_GLOBAL);

	public static final int ESCOLA_MUSICA = ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.ESCOLA_MUSICA);

	public static final int UNIDADE_ACADEMICA_ALUNO_ESPECIAL = ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.UNIDADE_ACADEMICA_ALUNO_ESPECIAL);

	//Unidade pagadora de bolsas de extensão.
	public static final int BOLSAS_EXTENSAO  = ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.UNIDADE_BOLSAS_EXTENSAO); 

	//Unidade pagadora de bolsas de monitoria.
	public static final int BOLSAS_MONITORIA = ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.UNIDADE_BOLSAS_MONITORIA); 

	//Unidade da Secretaria de Educação a Distância.
	public static final int SEDIS = ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.UNIDADE_SEDIS);
	
	public static final int SECAO_ATENDIMENTO_DAP = ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.SECAO_ATENDIMENTO_DAP);

	public static final int DEPARTAMENTO_ADMINISTRACAO_PESSOAL = ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.DEPARTAMENTO_ADMINISTRACAO_PESSOAL);

	public static final int DIVISAO_DE_TREINAMENTO_E_DESENVOLVIMENTO_DE_RH = ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.ID_UNIDADE_DIVISAO_DE_TREINAMENTO_E_DESENVOLVIMENTO_DE_RH);

	// Unidade do Centro de Ensino Superior do Seridó
	public static final int CERES = ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.UNIDADE_CERES);

	/**
	 * Tipo de unidade. Indica que a unidade é unidade de fato
	 *
	 * @see #tipo
	 */
	public static final int UNIDADE_FATO = 1;

	/**
	 * Tipo de unidade. Indica que a unidade é unidade de direito
	 * 
	 * @see #tipo
	 */
	public static final int UNIDADE_GESTORA = 2;

	/*
	 * Tipos de Unidade
	 */
	public static final int PROGRAMA = 3;
	public static final int CENTRO = 4;
	public static final int ESCOLA = 5;
	

	/** Identificador */
	protected int id;

	/** Informa se a unidade é vista no SCO */
	protected boolean unidadeOrcamentaria;

	/** Informa se a unidade é vista no SIPAC */
	protected boolean unidadeSipac;

	/** Informa se a unidade é vista no SIGAA */
	protected boolean unidadeAcademica;

	/** Código da unidade */
	protected Long codigo;

	/** Nome da unidade */
	protected String nome;

	/** Nome da unidade, convertido para caracteres ASCII */
	protected String nomeAscii;

	/** Nome que aparece nas capas dos processos */
	protected String nomeCapa;

	/** Sigla da unidade */
	protected String sigla;

	/** Se de DIREITO ou DE FATO */
	protected int tipo;

	/** Se unidade, convênio, extensão, pesquisa */
	protected int categoria;


	/** Código da unidade gestora no sistema SIAFI (Sistema Integrado de Administração Financeira do
	 * Governo Federal). O SIAFI é o principal instrumento utilizado para registro, acompanhamento e
	 * controle da execução orçamentária, financeira e patrimonial do Governo Federal. */
	protected Integer codigoUnidadeGestoraSIAFI;

	/** Código da gestão no sistema SIAFI, para unidades determinadas como gestoras. */
	protected Integer codigoGestaoSIAFI;

	/** CNPJ da unidade */
	protected Long cnpj;

	/** Email oficial da unidade */
	protected String email;

	/** Telefone oficial da unidade */
	protected String telefone;

	/** Endereço oficial da unidade */
	protected String endereco;

	/** UF da unidade */
	protected String uf;

	/** CEP oficial da unidade */
	protected String cep;

	/** Data de criação da unidade. Documento legal de criação */
	protected Date dataCriacao;

	/** Data de extinção da unidade. Documento legal de extinção */
	protected Date dataExtincao;

	/** Responsável no contexto orçamentário */
	protected UnidadeGeral unidadeResponsavel;

	/** Hierarquia orçamentária*/
	protected String hierarquia;

	/** Hierarquia Organizacional */
	protected String hierarquiaOrganizacional;

	/** Unidade gestora -> UFRN, Centros ou Hospitais */
	protected UnidadeGeral gestora;

	/** Código da unidade no sistema SIAPECAD */
	protected Long codigoSiapecad;

	/** Indica se a unidade é organizacional, ou seja, se esta unidade está presente
	 * realmente no organograma da universidade. Existem unidades que são cadastradas apenas para fins de orçamento e não são unidade do organograma. */
	protected boolean organizacional;
	
	/** */
	protected boolean permiteGestaoCentrosPelaGestoraSuperior;

	/** A unidade possui uma classificação organizacional. ex.: Orgão Suplementar, Complexo Hospitalar, Acadêmica, Administrativa */
	protected ClassificacaoUnidade classificacaoUnidade;

	/** A unidade possui um Nível Organizacional. ex.: Gerencial, Operacional, Tático*/
	protected NivelOrganizacional nivelOrganizacional;

	/** Unidade responsável no contexto organizacional */
	protected UnidadeGeral responsavelOrganizacional;

	/** Tipo da unidade organizacional. Pró-Reitoria, Departamento, Laboratório, ...*/
	protected TipoUnidadeOrganizacional tipoOrganizacional;

	/** Informa a área de atuação da unidade: meio, fim,...*/
	protected AreaAtuacaoUnidade areaAtuacao;

	/** Ambiente organizacional. Administrativo, saúde, informação, etc*/
	protected AmbienteOrganizacionalUnidade ambienteOrganizacional;

	/** Informa se a unidade é gestora de frequência */
	protected boolean gestoraFrequencia;

	/** Informa se a unidade tem função remunerada */
	protected boolean funcaoRemunerada;

	/** Tipo da função remunerada. (CD/FG) */
	protected int tipoFuncaoRemunerada;

	/** Informa se a unidade é de avaliação ou não */
	protected boolean avaliacao;

	/** Informa se a unidade pode cadastrar metas */
	protected Boolean metas;

	/**
	 * os tipos de unidades acadêmicas são: Departamento, centro, PPG
	 */
	protected Integer tipoAcademica;

	/**
	 * A sigla utilizada, caso seja uma unidade acadêmica
	 */
	protected String siglaAcademica;
	/**
	 * caso a unidade seja acadêmica, a sua gestora será a unidade que rege os cursos que ela oferece.
	 */
	protected UnidadeGeral gestoraAcademica;

	private double saldo;

	/** Unidades filhas. Hierarquia orçamentária */
	protected Collection<UnidadeGeral> unidadesFilhas;

	/** Unidades filhas. Hierarquia orçamentária */
	protected Collection<UnidadeGeral> unidadesFilhasHierarquia;

	/** Se a unidade submete propostas de extensão */
	protected Boolean submetePropostaExtensao;

	/** Indica se a unidade efetua licitações de compras gerais */
	protected Boolean compradora = false;

	/** Indica se a unidade efetua licitações de obras e serviços de engenharia */
	protected Boolean compradoraEngenharia = false;

	/** Data em que a unidade foi persistida na base de dados */
	protected Date dataCadastro;

	/** Identificador do usuário que efetua o cadastro da unidade na base de dados */
	protected Integer idUsuarioCadastro;


	/**Template do relatório de parecer de dispensa de licitação
	 * */
	protected Integer templateParecerDL;

	/**
	 * Sequência da unidade compradora. Para definição do número da
	 * modalidade da licitação
	 */
	protected Integer sequenciaModalidadeCompra;

	/**
	 * Prazo inicial(dia) para o envio da frequência dos bolsistas
	 */
	protected Integer prazoEnvioBolsaInicio;

	/**
	 * Prazo final(dia) para o envio da frequência dos bolsistas
	 */
	protected Integer prazoEnvioBolsaFim;

	protected String presidenteComissao;

	/** Identificador do servidor responsável pelos termos de bens da unidade no momento */
	protected Integer idResponsavelPatrimonial;

	/** Indica se a unidade está ativa para ser associada a outros dados de outras tabelas */
	protected boolean ativo;

	/** Atributo utilizado nos autocompletes */
	protected String codigoNome;

	/** Identifica quantos servidores estão lotados nesta unidade. */
	protected int qtdServidoresLotados;

	/** 
	 * Indica se a unidade tem permissão de protocolizar processos. 
	 * Isto implica em ela possuir um radical para os processos do protocolo e este radical poder ser utilizado pelas unidades vinculadas à ela. 
	 */
	protected boolean protocolizadora;
	
	/** Radical para os processos de protocolo criados por esta unidade e unidades vinculadas. */
	protected Integer radical;
	
	/** Indica o tipo de turno de trabalho da unidade. */
	protected TipoTurno tipoTurno;
	
	/** Indica o código da unidade no SIORG. */
	protected Integer codigoSIORG;

	public UnidadeGeral(int id) {
		this.id = id;
	}

	public UnidadeGeral() {

	}

	public UnidadeGeral(UnidadeGeral unidade) {
		setId(unidade.getId());
		setCodigo(unidade.getCodigo());
		setNome(unidade.getNome());
		setNomeCapa(unidade.getNomeCapa());
		setHierarquia(unidade.getHierarquia());
		if (unidade.getUnidadeResponsavel() != null
				&& !this.equals(unidade.getUnidadeResponsavel()))
			setUnidadeResponsavel(unidade.getUnidadeResponsavel());
	}
	
	public UnidadeGeral (int id, String sigla, Long codigo) {
		this.id = id;
		this.sigla = sigla;
		this.codigo = codigo;
	}

	public UnidadeGeral(int id, Long codigo, String nome, String sigla) {
		this.id = id;
		setNome(nome);
		setSigla(sigla);
		setCodigo(codigo);
	}

	public UnidadeGeral(int id, Long codigo, String nome, String nomeAbrev,
			String nomeCapa, String hierarquia) {
		this.id = id;
		setNome(nome);
		setSigla(nomeAbrev);
		setCodigo(codigo);
		setNomeCapa(nomeCapa);
		setHierarquia(hierarquia);
	}

	public UnidadeGeral(int id, Long codigo, String nome, String nomeAbrev,
			String nomeCapa, String hierarquia, UnidadeGeral responsavel, UnidadeGeral gestora) {
		this.id = id;
		setNome(nome);
		setSigla(nomeAbrev);
		setCodigo(codigo);
		setNomeCapa(nomeCapa);
		setHierarquia(hierarquia);
		setUnidadeResponsavel(responsavel);
		setGestora(gestora);
	}

	public UnidadeGeral(int id, Long codigo, String nome, String nomeAbrev,
			String nomeCapa, String hierarquia, UnidadeGeral responsavel,
			boolean unidadeSipac, boolean organizacional,
			boolean orcamentaria, int tipo) {
		this.id = id;
		setNome(nome);
		setSigla(nomeAbrev);
		setCodigo(codigo);
		setNomeCapa(nomeCapa);
		setHierarquia(hierarquia);
		setUnidadeResponsavel(responsavel);
		setUnidadeSipac(unidadeSipac);
		setUnidadeOrcamentaria(orcamentaria);
		setOrganizacional(organizacional);
		setTipo(tipo);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String string) {
		nome = string;
		setNomeAscii(nome);
	}

	public String getNomeCapa() {
		return nomeCapa;
	}

	public void setNomeCapa(String nomeCapa) {
		this.nomeCapa = nomeCapa;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String string) {
		sigla = string;
	}

	public int getTipo() {
		return tipo;
	}
	public void setTipo(int i) {
		tipo = i;
	}

	public int getCategoria() {
		return categoria;
	}
	public void setCategoria(int categoria) {
		this.categoria = categoria;
	}
	public String getHierarquia() {
		return hierarquia;
	}

	public void setHierarquia(String hierarquia) {
		this.hierarquia = hierarquia;
	}

	public Long getCnpj() {
		return cnpj;
	}
	public void setCnpj(Long cnpj) {
		this.cnpj = cnpj;
	}
	public String getNumeroSiaf() {
		return getCodigoUnidadeGestoraSIAFI() + "/" + getCodigoGestaoSIAFI();
	}

	public Integer getCodigoUnidadeGestoraSIAFI() {
		return codigoUnidadeGestoraSIAFI;
	}

	public void setCodigoUnidadeGestoraSIAFI(Integer codigoUnidadeGestoraSIAFI) {
		this.codigoUnidadeGestoraSIAFI = codigoUnidadeGestoraSIAFI;
	}

	public Integer getCodigoGestaoSIAFI() {
		return codigoGestaoSIAFI;
	}

	public void setCodigoGestaoSIAFI(Integer codigoGestaoSIAFI) {
		this.codigoGestaoSIAFI = codigoGestaoSIAFI;
	}

	public UnidadeGeral getUnidadeResponsavel() {
		return unidadeResponsavel;
	}

	public void setUnidadeResponsavel(UnidadeGeral unidadeResponsavel) {
		this.unidadeResponsavel = unidadeResponsavel;
	}

	public UnidadeGeral getGestora() {
		return gestora;
	}


	public void setGestora(UnidadeGeral gestora) {
		this.gestora = gestora;
	}

	public boolean isUnidadeOrcamentaria() {
		return unidadeOrcamentaria;
	}

	public void setUnidadeOrcamentaria(boolean unidadeOrcamentaria) {
		this.unidadeOrcamentaria = unidadeOrcamentaria;
	}


	public boolean isUnidadeSipac() {
		return unidadeSipac;
	}

	public void setUnidadeSipac(boolean unidadeSipac) {
		this.unidadeSipac = unidadeSipac;
	}
	public boolean isUnidadeAcademica() {
		return unidadeAcademica;
	}

	public void setUnidadeAcademica(boolean unidadeAcademica) {
		this.unidadeAcademica = unidadeAcademica;
	}

	public boolean isOrganizacional() {
		return organizacional;
	}

	public void setOrganizacional(boolean organizacional) {
		this.organizacional = organizacional;
	}


	public UnidadeGeral getResponsavelOrganizacional() {
		return responsavelOrganizacional;
	}

	public void setResponsavelOrganizacional(UnidadeGeral responsavelOrganizacional) {
		this.responsavelOrganizacional = responsavelOrganizacional;
	}

	public String getHierarquiaOrganizacional() {
		return hierarquiaOrganizacional;
	}

	public void setHierarquiaOrganizacional(String hierarquiaOrganizacional) {
		this.hierarquiaOrganizacional = hierarquiaOrganizacional;
	}

	public TipoUnidadeOrganizacional getTipoOrganizacional() {
		return tipoOrganizacional;
	}

	public void setTipoOrganizacional(TipoUnidadeOrganizacional tipoOrganizacional) {
		this.tipoOrganizacional = tipoOrganizacional;
	}

	public Long getCodigoSiapecad() {
		return codigoSiapecad;
	}

	public void setCodigoSiapecad(Long codigoSiapecad) {
		this.codigoSiapecad = codigoSiapecad;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	public Date getDataExtincao() {
		return dataExtincao;
	}

	public void setDataExtincao(Date dataExtincao) {
		this.dataExtincao = dataExtincao;
	}

	public AreaAtuacaoUnidade getAreaAtuacao() {
		return areaAtuacao;
	}

	public void setAreaAtuacao(AreaAtuacaoUnidade areaAtuacao) {
		this.areaAtuacao = areaAtuacao;
	}

	public AmbienteOrganizacionalUnidade getAmbienteOrganizacional() {
		return ambienteOrganizacional;
	}

	public void setAmbienteOrganizacional(
			AmbienteOrganizacionalUnidade ambienteOrganizacional) {
		this.ambienteOrganizacional = ambienteOrganizacional;
	}

	public boolean isAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(boolean avaliacao) {
		this.avaliacao = avaliacao;
	}

	public boolean isFuncaoRemunerada() {
		return funcaoRemunerada;
	}

	public void setFuncaoRemunerada(boolean funcaoRemunerada) {
		this.funcaoRemunerada = funcaoRemunerada;
	}


	public boolean isGestoraFrequencia() {
		return gestoraFrequencia;
	}

	public void setGestoraFrequencia(boolean gestoraFrequencia) {
		this.gestoraFrequencia = gestoraFrequencia;
	}

	public int getTipoFuncaoRemunerada() {
		return tipoFuncaoRemunerada;
	}

	public void setTipoFuncaoRemunerada(int tipoFuncaoRemunerada) {
		this.tipoFuncaoRemunerada = tipoFuncaoRemunerada;
	}

	public Integer getTipoAcademica() {
		return tipoAcademica;
	}

	public void setTipoAcademica(Integer tipoAcademica) {
		this.tipoAcademica = tipoAcademica;
	}
	
	public String getSiglaAcademica() {
		return siglaAcademica;
	}

	public void setSiglaAcademica(String siglaAcademica) {
		this.siglaAcademica = siglaAcademica;
	}

	public UnidadeGeral getGestoraAcademica() {
		return gestoraAcademica;
	}

	public void setGestoraAcademica(UnidadeGeral gestoraAcademica) {
		this.gestoraAcademica = gestoraAcademica;
	}

	public double getSaldo() {
		return saldo;
	}
	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	public String getTipoDesc() {
		if (tipo == 1) {
			return "Fato";
		} else {
			return "Direito\\Gestora";
		}
	}

	/** Retorna a string código concatenado com nome da unidade.*/
	public String getCodigoNome() {
		if (getCodigo() == null || getCodigo().intValue() == 0) {
			return getNome();
		} else {
			return (getNomeCapa() == null ? getNome() : getNomeCapa())
					+ " ("
					+ Formatador.getInstance().formatarCodigoUnidade(
							getCodigo()) + ")";
		}
	}

	public void setCodigoNome(String codigoNome) {
		this.codigoNome = codigoNome;
	}

	/** Retorna a string código concatenado com a Sigla da unidade.*/
	public String getCodigoSigla() {
		if (getCodigo() == null || getCodigo().intValue() == 0) {
			return getSigla();
		} else {
			return (getSigla())
					+ " ("
					+ Formatador.getInstance().formatarCodigoUnidade(
							getCodigo()) + ")";
		}
	}
	public String getCodigoFormatado() {
		if (getCodigo() != null && getCodigo().intValue() != 0) {
			return Formatador.getInstance().formatarCodigoUnidade(getCodigo());
		} else {
			return "";
		}
	}

	public void setCodigoFormatado(String codigoFormatado) {
		if(!isEmpty(codigoFormatado))
			codigo = Long.valueOf(codigoFormatado.replaceAll("\\.", ""));
	}

	/**
	 * Retorna a Unidade Raiz na Hierarquia organizacional
	 *
	 * @return
	 */
	public int getRaiz() {

		if (hierarquia != null) {
			int pos = hierarquia.substring(1).indexOf(".");
			String raiz = hierarquia.substring(1, pos + 1);
			return Integer.parseInt(raiz);
		}
		return -1;

	}
	public boolean isUnidadeFilha(int raiz) {
		return hierarquia.indexOf("." + raiz + ".") != -1;

	}

	public boolean isUnidadeFilhaOrganizacional(int raiz) {
		return hierarquiaOrganizacional.indexOf("." + raiz + ".") != -1;

	}

	public String getNomeCategoria() {

		switch (categoria) {
		case CategoriaUnidade.UNIDADE:
			return "UNIDADE";
		case CategoriaUnidade.CONVENIO:
			return "CONVÊNIO";
		case CategoriaUnidade.PESQUISA:
			return "PESQUISA";
		case CategoriaUnidade.EXTENSAO:
			return "EXTENSÃO";
		case CategoriaUnidade.ORCAMENTO_INTERNO:
			return "ORÇAMENTO INTERNO";
		case CategoriaUnidade.ORCAMENTO_EXTERNO_DESCENTRALIZADO:
			return "ORÇAMENTO EXTERNO/DESCENTRALIZADO";
		default:
			return "SEM CATEGORIA";
		}
	}
	/**
	 * Método que retorna os ids das unidades na hierarquia da unidade this
	 * @return
	 */
	public int[] getVetorHierarquia() {
		String[] unidades = getHierarquia().split("\\.");
		int[] vetorHierarquia = new int[unidades.length - 1];

		for (int i = 1; i < unidades.length; i++) {
			vetorHierarquia[unidades.length - i - 1] = Integer
					.parseInt(unidades[i]);
		}

		return vetorHierarquia;
	}

	/**
	 * Método que retorna os ids das unidades na hierarquia organizacional da unidade this
	 * @return
	 */
	public int[] getVetorHierarquiaOrganizacional() {
		
		if(getHierarquiaOrganizacional() == null) // algumas unidades não tem hierarquia organizacioanl
			return new int[0];
		
		String[] unidades = getHierarquiaOrganizacional().split("\\.");
		int[] vetorHierarquia = new int[unidades.length - 1];

		for (int i = 1; i < unidades.length; i++) {
			vetorHierarquia[unidades.length - i - 1] = Integer
					.parseInt(unidades[i]);
		}

		return vetorHierarquia;
	}

	public String toString() {
		return getCodigoNome();
	}
	public UnidadeGeral getUnidadeGestora() {
		return gestora;
	}


	/**
	 * Retorna CNPJ formatado. Usado em relatórios do jasper
	 *
	 * @return
	 */
	public String getCnpjFormatado() {

		if (cnpj != null)
			return Formatador.getInstance().formatarCPF_CNPJ(cnpj);
		else
			return "";
	}


	/**
	 * Retorna a representação em XML de uma unidade. Usado para consultas
	 * em AJAX.
	 *
	 */
	public String toXML(boolean sigla) {
		StringBuffer xml = new StringBuffer();
		xml.append("<UNIDADE>");
		xml.append("<ID>" + getId() + "</ID>");
		xml.append("<NOME>" + "<![CDATA[" + (sigla ? getSigla() : getCodigoNome()) + "]]>"
				+ "</NOME>");
		xml.append("<CODIGO>" + getCodigo() + "</CODIGO>");
		xml.append("</UNIDADE>");

		return xml.toString();
	}

	public String getSiglaNome() {
		return getSigla() + " - " + getNome();
	}

	public Collection<UnidadeGeral> getUnidadesFilhas() {
		return unidadesFilhas;
	}
	public void setUnidadesFilhas(Collection<UnidadeGeral> unidadesFilhas) {
		this.unidadesFilhas = unidadesFilhas;
	}
	public Collection<UnidadeGeral> getUnidadesFilhasHierarquia() {
		return unidadesFilhasHierarquia;
	}
	public void setUnidadesFilhasHierarquia(Collection<UnidadeGeral> unidadesFilhasHierarquia) {
		this.unidadesFilhasHierarquia = unidadesFilhasHierarquia;
	}

	public boolean equals(Object obj) {
		UnidadeGeral unid = (UnidadeGeral) obj;
		if (unid != null) {
			if (unid.getId() == id) {
				return true;
			}
		}
		return false;
	}

	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	public Boolean getSubmetePropostaExtensao() {
		return submetePropostaExtensao;
	}

	public void setSubmetePropostaExtensao(Boolean submetePropostaExtensao) {
		this.submetePropostaExtensao = submetePropostaExtensao;
	}

	public Boolean getMetas() {
		return metas;
	}

	public void setMetas(Boolean metas) {
		this.metas = metas;
	}

	public ClassificacaoUnidade getClassificacaoUnidade() {
		return classificacaoUnidade;
	}

	public void setClassificacaoUnidade(ClassificacaoUnidade classificacaoUnidade) {
		this.classificacaoUnidade = classificacaoUnidade;
	}

	public NivelOrganizacional getNivelOrganizacional() {
		return nivelOrganizacional;
	}

	public void setNivelOrganizacional(NivelOrganizacional nivelOrganizacional) {
		this.nivelOrganizacional = nivelOrganizacional;
	}

	public String getNomeAscii() {
		return nomeAscii;
	}


	public Boolean getCompradora() {
		return compradora;
	}

	public void setCompradora(Boolean compradora) {
		this.compradora = compradora;
	}

	public Boolean getCompradoraEngenharia() {
		return compradoraEngenharia;
	}

	public void setCompradoraEngenharia(Boolean compradoraEngenharia) {
		this.compradoraEngenharia = compradoraEngenharia;
	}


	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Integer getIdUsuarioCadastro() {
		return idUsuarioCadastro;
	}

	public void setIdUsuarioCadastro(Integer idUsuarioCadastro) {
		this.idUsuarioCadastro = idUsuarioCadastro;
	}


	public Integer getTemplateParecerDL() {
		return templateParecerDL;
	}

	public void setTemplateParecerDL(Integer templateParecerDL) {
		this.templateParecerDL = templateParecerDL;
	}

	public Integer getSequenciaModalidadeCompra() {
		return sequenciaModalidadeCompra;
	}

	public void setSequenciaModalidadeCompra(Integer sequenciaModalidadeCompra) {
		this.sequenciaModalidadeCompra = sequenciaModalidadeCompra;
	}

	public Integer getPrazoEnvioBolsaInicio() {
		return prazoEnvioBolsaInicio;
	}

	public void setPrazoEnvioBolsaInicio(Integer prazoEnvioBolsaInicio) {
		this.prazoEnvioBolsaInicio = prazoEnvioBolsaInicio;
	}

	public Integer getPrazoEnvioBolsaFim() {
		return prazoEnvioBolsaFim;
	}

	public void setPrazoEnvioBolsaFim(Integer prazoEnvioBolsaFim) {
		this.prazoEnvioBolsaFim = prazoEnvioBolsaFim;
	}

	public String getPresidenteComissao() {
		return presidenteComissao;
	}

	public void setPresidenteComissao(String presidenteComissao) {
		this.presidenteComissao = presidenteComissao;
	}

	public Integer getIdResponsavelPatrimonial() {
		return idResponsavelPatrimonial;
	}

	public void setIdResponsavelPatrimonial(Integer idResponsavelPatrimonial) {
		this.idResponsavelPatrimonial = idResponsavelPatrimonial;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public void setNomeAscii(String nomeAscii) {
		this.nomeAscii = nomeAscii;

		if (nomeAscii != null)
			this.nomeAscii = StringUtils.toAscii(nomeAscii);
	}

	/**
	 * Realiza um log do status atualiza da unidade, considerando os valores de cada um dos seus atributos
	 * @return
	 */
	public String toStringUpdate(){

		StringBuffer sb = new StringBuffer();

		sb.append("id_unidade ="+getId()+"; unidade_orcamentaria ="+isUnidadeOrcamentaria()+"; unidade_responsavel ="+(getUnidadeResponsavel() != null ?getUnidadeResponsavel().getId():0)
				+";id_responsavel ="+(getIdResponsavelPatrimonial()!= null?getIdResponsavelPatrimonial():0)+";nome ="+getNome()+ ";codigo_unidade ="+getCodigo());

		sb.append(";sigla ="+getSigla() +";tipo ="+getTipo() +";compradora ="+getCompradora()
				+";compradora_engenharia ="+getCompradoraEngenharia() +";categoria ="+getCategoria() +";telefones ="+getTelefone() +";hierarquia ="+getHierarquia() +";");

		sb.append("nome_capa ="+getNomeCapa()  +";sipac ="+isUnidadeSipac()
				+";sequencia_modalidade_compra ="+getSequenciaModalidadeCompra() +"; presidente_comissao ="+getPresidenteComissao() +";");

		sb.append("prazo_envio_bolsa_inicio ="+getPrazoEnvioBolsaInicio() +"; prazo_envio_bolsa_fim ="+getPrazoEnvioBolsaFim() +";id_gestora ="+(getUnidadeGestora() != null ? getUnidadeGestora().getId():0)
				+";template_parecer_dl ="+getTemplateParecerDL() +";data_cadastro ="+getDataCriacao() +";cnpj ="+getCnpjFormatado() +";");

		sb.append("codigo_unidade_gestora_siafi ="+getCodigoUnidadeGestoraSIAFI() + "codigo_gestao_siafi ="+getCodigoGestaoSIAFI() +";id_gestora_academica ="+(getGestoraAcademica() != null ? getGestoraAcademica().getId():0) +";tipo_academica ="+getTipoAcademica()
				+";unidade_academica ="+isUnidadeAcademica() +";sigla_academica ="+getSiglaAcademica() +";codigo_siapcad ="+getCodigoSiapecad() +";email ="+getEmail() +";");

		sb.append("data_criacao ="+getDataCriacao() +";data_extincao ="+getDataExtincao() +";gestora_frequencia ="+isGestoraFrequencia() +"; organizacional ="+isOrganizacional()
				+";id_tipo_organizacional ="+(getTipoOrganizacional() != null ? getTipoOrganizacional().getId():0));

		sb.append("funcao_remunerada ="+isFuncaoRemunerada()+"; tipo_funcao_remunerada ="+getTipoFuncaoRemunerada() +";hierarquia_organizacional="+ getHierarquiaOrganizacional()
				+";id_area_atuacao ="+ (getAreaAtuacao() != null ? getAreaAtuacao().getId() : 0)+";id_ambiente_organizacional ="+(getAmbienteOrganizacional() != null ?getAmbienteOrganizacional().getId() : 0 ) );

		sb.append(";avaliacao ="+isAvaliacao() +";id_unid_resp_org ="+(getResponsavelOrganizacional() != null ? getResponsavelOrganizacional().getId():0)
				+";metas ="+getMetas() +";submete_proposta_extensao="+getSubmetePropostaExtensao()+";id_classificacao_unidade ="+(getClassificacaoUnidade() != null? getClassificacaoUnidade().getId():0)
				+";id_nivel_organizacional="+(getNivelOrganizacional() != null ? getNivelOrganizacional().getId() : 0));

		return sb.toString();
	}

	public int getQtdServidoresLotados() {
		return qtdServidoresLotados;
	}

	public void setQtdServidoresLotados(int qtdServidoresLotados) {
		this.qtdServidoresLotados = qtdServidoresLotados;
	}

	public boolean isPermiteGestaoCentrosPelaGestoraSuperior() {
		return permiteGestaoCentrosPelaGestoraSuperior;
	}

	public void setPermiteGestaoCentrosPelaGestoraSuperior(boolean permiteGestaoCentrosPelaGestoraSuperior) {
		this.permiteGestaoCentrosPelaGestoraSuperior = permiteGestaoCentrosPelaGestoraSuperior;
	}

	public boolean isProtocolizadora() {
		return protocolizadora;
	}

	public void setProtocolizadora(boolean protocolizadora) {
		this.protocolizadora = protocolizadora;
	}

	public Integer getRadical() {
		return radical;
	}

	public void setRadical(Integer radical) {
		this.radical = radical;
	}
	
	
	public String getRadicalCodigoNome() {
		return radical + " - " + getNome()+ (getCodigo() != null ? " ("+ Formatador.getInstance().formatarCodigoUnidade(getCodigo()) + ")" : "");
	}
	
	public Integer getCodigoSIORG() {
		return codigoSIORG;
	}

	public void setCodigoSIORG(Integer codigoSIORG) {
		this.codigoSIORG = codigoSIORG;
	}
	
	public TipoTurno getTipoTurno() {
		return tipoTurno;
	}

	public void setTipoTurno(TipoTurno tipoTurno) {
		this.tipoTurno = tipoTurno;
	}
}