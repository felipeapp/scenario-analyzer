/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 01/12/2009
 *
 */	
package br.ufrn.sigaa.parametros.dominio;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/** 
 * Interface para as constantes de mensagens referentes � opera��es de Stricto Sensu e que ser�o exibidas ao usu�rio.
 * @author �dipo Elder F. Melo
 *
 */
public interface MensagensStrictoSensu {
	
	/** Prefixo para estas mensagens. Valor atual: "2_10700_". */
	public static final String prefix = Sistema.SIGAA + "_" + SigaaSubsistemas.STRICTO_SENSU.getId() + "_";
	
	/**
	 * Mensagem exibida quando o discente n�o possui data de homologa��o de diploma registrado.<br>
	 * Conte�do: O discente selecionado n�o tem data de homologa��o de diploma registrada.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String DISCENTE_SEM_DATA_HOMOLOGACAO_DIPLOMA = prefix + "001";
	
	/**
	 * Mensagem exibida quando se tenta registrar um diploma e n�o h� livro para registro aberto.<br>
	 * Conte�do: N�o h� livro aberto para registro de diplomas stricto sensu.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String NAO_HA_LIVRO_REGISTRO_DIPLOMA_ABERTO = prefix + "002";

	/**
	 * Mensagem exibida quando o usu�rio tenta cadastrar um livro de stricto sensu quando h� outro aberto.<br>
	 * Conte�do: N�o pode haver dois livros abertos para registro de diplomas de cursos stricto sensu. 
	 * Por favor, feche o livro aberto antes de abrir um novo.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String CONFLITO_LIVRO_STRICTO_ABERTO = prefix + "003";
	
	/**
	 * Mensagem exibida quando o usu�rio informa nos par�metros do programa de p�s.<br>
	 * Conte�do: O N�mero m�ximo de disciplinas que alunos especiais pode pagar n�o podem ser maior que %s.<br>
	 * Tipo: WARNING.
	 */
	public static final String MAXIMO_DISCIPLINAS_ALUNO_ESPECIAL_ESTOURADO = prefix + "004";
	
	/**
	 * Mensagem exibida quando um Coordenador ou a secretaria da P�s tenta editar alguma observa��o no hist�rico do discente.<br>
	 * Conte�do: Somente a PPG pode adicionar observa��es no hist�rico do discente.<br>
	 * Tipo: WARNING.
	 */
	public static final String PERMISSAO_EDICAO_HISTORICO_DISCENTE = prefix + "005";	
	
	/**
	 * Mensagem exibida quando vai cadastrar uma banca de defesa que extrapolou o prazo definido em <b>ParametrosStrictoSensu.PRAZO_MAXIMO_CADASTRO_BANCA</b>
	 */
	public static final String MENSAGEM_PRAZO_MAXIMO_CADASTRO_BANCA = prefix + "006";
	
	
	/**
	 * Mensagem exibida para informar que um componente curricular s� pode ser aproveitado pela PPG(Pr�-Reitoria de P�s-Gradua��o)
	 * Conte�do: Este componente curricular s� pode ser aproveitado pela PPG.
	 * Tipo: ERROR.
	 */
	public static final String APROVEITAMENTO_COMPONENTE_APENAS_PPG = prefix + "007";
	
	
	/**
	 * Mensagem exibida para informar ao usuario que � necess�rio entrar em contato com a PPG(Pr�-Reitoria de P�s-Gradua��o) para efetuar o cadastro de docente externo.
	 * Conte�do: Entre em contato com a PPG(Pr�-Reitoria de P�s-Gradua��o) para cadastrar este docente externo.
	 * Tipo: WARNING.
	 */
	public static final String CADASTRO_DOCENTE_EXTERNO_APENAS_GESTOR = prefix + "008";
	
	
	/**
	 * Mensagem exibida para informar ao usuario que � necess�rio entrar em contato com a PPG para solucionar problema de haver
	 * dois discentes ativos associados a mesma pessoa.
	 * Conte�do: N�o � poss�vel realizar esta opera��o pois h� dois discentes ativos associados a mesma pessoa. Por favor, entre em contato com a PPG.
	 * Tipo: ERROR.
	 */
	public static final String PROBLEMA_DOIS_DISCENTE_ATIVOS_ASSOCIADOS_A_PESSOA = prefix + "009";
	
	/**
	 * Mensagem exibida para informar ao usu�rio que o per�odo de an�lise de solicita��es de matr�cula � pertinente ao registrado no calend�rio 
	 * acad�mico do programa, que pertence o discente.
	 * Conte�do: N�o � permitido realizar an�lise de solicita��es de matr�culas fora dos per�odos determinados no calend�rio do programa do aluno.
	 * Tipo: ERROR.
	 */
	public static final String ANALISE_SOLICITACAO_MATRICULA_MEDIANTE_CALENDARIO_PROGRAMA_DISCENTE = prefix + "010";
	
	
	/**
	 * Texto que ir� aparecer ao emitir a TEDE. 
	 * Conte�do:
	 * 
	 * '<p align="justify">Na qualidade de titular dos direitos de autor da
	 *	publica��o, autorizo a UFRN a disponibilizar atrav�s do site
	 *	http://bdtd.bczm.ufrn.br/tedesimplificado sem ressarcimento dos
	 *	direitos autorais, de acordo com a Lei n� 9610/98, o texto integral da
	 *	obra abaixo citada, conforme permiss�es assinaladas, para fins de
	 *	leitura, impress�o e/ou download, a t�tulo de divulga��o da produ��o
	 *	cient�fica brasileira, a partir do dia %s</p>'
	 * 
	 * Tipo: INFORMATION.
	 * 
	 */
	public static final String TEXTO_EMISSAO_TEDE = prefix + "011";
	
	/**
	 * Mensagem exibida para informar ao coordenador de programa de p�s-gradua��o, que n�o � mais poss�vel cadastrar 
	 * novos discentes regulares pela opera��o de cadastrar novo discente.
	 * Conte�do: Caro Coordenador(a),<br/><br/>
						 nesta opera��o ser� permitido apenas o cadastro de discentes ESPECIAIS. 
						 Para cadastrar discentes regulares utilize o gerenciamento	de candidatos do processo seletivo.
	 * Tipo: WARNING.
	 */
	public static final String NEGAR_CADASTRO_DISCENTE_REGULAR_FORA_OP_VESTIBULAR = prefix + "012";
	
}
