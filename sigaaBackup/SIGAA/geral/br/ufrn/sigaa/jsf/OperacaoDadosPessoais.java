/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 20/03/2007
 * 
 */
package br.ufrn.sigaa.jsf;

import java.util.HashMap;

/**
 * Classe auxiliar para controle da utiliza��o do Managed Bean de submiss�o de
 * dados pessoais.
 *
 * @author Andre M Dantas
 *
 */
public class OperacaoDadosPessoais {

	/** Nome do Managed Bean respons�vel pela opera��o */
	private String mBean;

	/**
	 * Nome da opera��o (Para utiliza��o no t�tulo da p�gina de submiss�o dos
	 * dados pessoais)
	 */
	private String nome;

	/** Campos que devem ser suprimidos na exibi��o do formul�rio */
	private String[] camposEscondidos;

	// C�digos das Opera��es
	/** Cadastro de Pessoas para EAD */
	public static final int PESSOAS_EAD = 1;
	/** Cadastro de Docente Externo */
	public static final int DOCENTE_EXTERNO = 2;
	/** Cadastro de Discente de Gradua��o */
	public static final int DISCENTE_GRADUACAO = 3;
	/** Cadastro de Discente de P�s-Gradua��o */
	public static final int DISCENTE_STRICTO = 4;
	/** Altera��o de Dados do Discente */
	public static final int ALTERACAO_DADOS_DISCENTE = 5;
	/** Altera��o de Dados do Tutor */
	public static final int ALTERACAO_DADOS_TUTOR = 6;
	/** Altera��o de Dados do Coordenador de Polo */
	public static final int ALTERACAO_DADOS_COORDENADOR_POLO = 7;
	/** Dados pessoais */
	public static final int INSCRICAO_VESTIBULAR = 8;
	/** Dados do Usu�rio Externo */
	public static final int USUARIO_EXTERNO_BIBLIOTECA = 9;
	/** Cadastro de Discentes Resid�ncia M�dica */
	public static final int DISCENTE_RESIDENCIA = 10;
	/** Cadastro de Discentes T�cnico */
	public static final int DISCENTE_TECNICO = 11;
	/** Cadastro de Discentes Lato-Sensu */
	public static final int DISCENTE_LATO = 12;
	/** Cadastro de Discente de Ensino M�dio */
	public static final int DISCENTE_MEDIO = 13;
	/** Cadastramento de Discentes Convocados */
	public static final int CADASTRAMENTO_DISCENTE_CONVOCADO = 14;
	/** Cadastro de docente de programa em rede */
	public static final int CADASTRO_DOCENTE_REDE = 15;
	/** Cadastro de discente de programa em rede */
	public static final int CADASTRO_DISCENTE_REDE = 16;
	/** Cadastro de docente de programa em rede */
	public static final int SOLICITACAO_CADASTRO_DOCENTE_REDE = 17;
	/** Altera��o dos dados pessoais do docente de programa em rede */
	public static final int ALTERACAO_DADOS_DOCENTE_REDE = 18;
	/** Cadastro de discente de programa em rede */
	public static final int ALTERAR_DADOS_DISCENTE_REDE = 19;
	/** Cadastro de Secretario(a) de programa em rede */
	public static final int CADASTRAR_SECRETARIA_UNIDADE_REDE = 20;
	
	/** HashMap com as Opera��es Dispon�veis */
	private static HashMap<Integer, OperacaoDadosPessoais> mapa;

	static {
		mapa = new HashMap<Integer, OperacaoDadosPessoais>();
		mapa.put(PESSOAS_EAD, new OperacaoDadosPessoais("dadosPessoais", "Cadastro de Pessoas para EAD", new String[] {""}));
		mapa.put(DOCENTE_EXTERNO, new OperacaoDadosPessoais("docenteExterno", "Cadastro de Docente Externo", new String[] {""}));
		mapa.put(DISCENTE_GRADUACAO, new OperacaoDadosPessoais("discenteGraduacao", "Cadastro de Discente de Gradua��o", new String[] {""}));
		mapa.put(DISCENTE_STRICTO, new OperacaoDadosPessoais("discenteStricto", "Cadastro de Discente de P�s-Gradua��o", new String[] {""}));
		mapa.put(ALTERACAO_DADOS_DISCENTE, new OperacaoDadosPessoais("alteracaoDadosDiscente", "Altera��o de Dados do Discente", new String[] {""}));
		mapa.put(ALTERACAO_DADOS_TUTOR, new OperacaoDadosPessoais("dadosPessoaisTutor", "Altera��o de Dados do Tutor", new String[] {""}));
		mapa.put(ALTERACAO_DADOS_COORDENADOR_POLO, new OperacaoDadosPessoais("dadosPessoaisCoordenador", "Altera��o de Dados do Coordenador de Polo", new String[] {""}));
		mapa.put(INSCRICAO_VESTIBULAR, new OperacaoDadosPessoais("inscricaoVestibular", "Dados pessoais", new String[] {""}));
		mapa.put(USUARIO_EXTERNO_BIBLIOTECA, new OperacaoDadosPessoais("usuarioExternoBibliotecaMBean", "Dados do Usu�rio Externo", new String[] {""}));
		mapa.put(DISCENTE_RESIDENCIA, new OperacaoDadosPessoais("discenteResidenciaMedica", "Cadastro de Discentes Resid�ncia M�dica", new String[] {""}));
		mapa.put(DISCENTE_TECNICO, new OperacaoDadosPessoais("discenteTecnico", "Cadastro de Discentes T�cnico", new String[] {""}));
		mapa.put(DISCENTE_LATO, new OperacaoDadosPessoais("discenteLato", "Cadastro de Discentes Lato-Sensu", new String[] {""}));
		mapa.put(DISCENTE_MEDIO, new OperacaoDadosPessoais("discenteMedio", "Cadastro de Discente de Ensino M�dio", new String[] {""}));
		mapa.put(CADASTRAMENTO_DISCENTE_CONVOCADO, new OperacaoDadosPessoais("cadastramentoDiscenteConvocadoMBean", "Cadastramento de Discentes Convocados", new String[] {""}));
		mapa.put(CADASTRO_DOCENTE_REDE, new OperacaoDadosPessoais("docenteRedeMBean", "Cadastro de Docente", new String[] {""}));
		mapa.put(CADASTRO_DISCENTE_REDE, new OperacaoDadosPessoais("cadastroDiscenteRedeMBean", "Cadastro de Discente Associado", new String[] {""}));
		mapa.put(SOLICITACAO_CADASTRO_DOCENTE_REDE, new OperacaoDadosPessoais("solicitacaoDocenteRedeMBean", "Cadastro de Docente", new String[] {""}));
		mapa.put(ALTERACAO_DADOS_DOCENTE_REDE, new OperacaoDadosPessoais("docenteRedeMBean", "Altera��o de Dados Pessois do Docente", new String[] {""}));
		mapa.put(ALTERAR_DADOS_DISCENTE_REDE, new OperacaoDadosPessoais("cadastroDiscenteRedeMBean", "Alterar Dados de Discente Associado", new String[] {""}));
		mapa.put(CADASTRAR_SECRETARIA_UNIDADE_REDE, new OperacaoDadosPessoais("coordenadorUnidadeMBean", "Cadastro de secretario(a)", new String[] {""}));

	}

	/** Retorna uma opera��o de acordo com o c�digo.
	 * @param codigoOperacao
	 * @return
	 */
	public static OperacaoDadosPessoais getOperacao(int codigoOperacao) {
		return mapa.get(codigoOperacao);
	}

	/** Construtor padr�o. */
	public OperacaoDadosPessoais() {
	}

	/** Construtor parametrizado. */
	public OperacaoDadosPessoais(String bean, String nome, String[] camposEscondidos) {
		super();
		mBean = bean;
		this.nome = nome;
		this.camposEscondidos = camposEscondidos;
	}


	public String[] getCamposEscondidos() {
		return camposEscondidos;
	}

	public void setCamposEscondidos(String[] camposEscondidos) {
		this.camposEscondidos = camposEscondidos;
	}

	public String getMBean() {
		return mBean;
	}

	public void setMBean(String bean) {
		mBean = bean;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}
