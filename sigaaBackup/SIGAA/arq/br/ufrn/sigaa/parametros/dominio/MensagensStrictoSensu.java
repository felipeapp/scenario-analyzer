/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 01/12/2009
 *
 */	
package br.ufrn.sigaa.parametros.dominio;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/** 
 * Interface para as constantes de mensagens referentes à operações de Stricto Sensu e que serão exibidas ao usuário.
 * @author Édipo Elder F. Melo
 *
 */
public interface MensagensStrictoSensu {
	
	/** Prefixo para estas mensagens. Valor atual: "2_10700_". */
	public static final String prefix = Sistema.SIGAA + "_" + SigaaSubsistemas.STRICTO_SENSU.getId() + "_";
	
	/**
	 * Mensagem exibida quando o discente não possui data de homologação de diploma registrado.<br>
	 * Conteúdo: O discente selecionado não tem data de homologação de diploma registrada.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String DISCENTE_SEM_DATA_HOMOLOGACAO_DIPLOMA = prefix + "001";
	
	/**
	 * Mensagem exibida quando se tenta registrar um diploma e não há livro para registro aberto.<br>
	 * Conteúdo: Não há livro aberto para registro de diplomas stricto sensu.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String NAO_HA_LIVRO_REGISTRO_DIPLOMA_ABERTO = prefix + "002";

	/**
	 * Mensagem exibida quando o usuário tenta cadastrar um livro de stricto sensu quando há outro aberto.<br>
	 * Conteúdo: Não pode haver dois livros abertos para registro de diplomas de cursos stricto sensu. 
	 * Por favor, feche o livro aberto antes de abrir um novo.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String CONFLITO_LIVRO_STRICTO_ABERTO = prefix + "003";
	
	/**
	 * Mensagem exibida quando o usuário informa nos parâmetros do programa de pós.<br>
	 * Conteúdo: O Número máximo de disciplinas que alunos especiais pode pagar não podem ser maior que %s.<br>
	 * Tipo: WARNING.
	 */
	public static final String MAXIMO_DISCIPLINAS_ALUNO_ESPECIAL_ESTOURADO = prefix + "004";
	
	/**
	 * Mensagem exibida quando um Coordenador ou a secretaria da Pós tenta editar alguma observação no histórico do discente.<br>
	 * Conteúdo: Somente a PPG pode adicionar observações no histórico do discente.<br>
	 * Tipo: WARNING.
	 */
	public static final String PERMISSAO_EDICAO_HISTORICO_DISCENTE = prefix + "005";	
	
	/**
	 * Mensagem exibida quando vai cadastrar uma banca de defesa que extrapolou o prazo definido em <b>ParametrosStrictoSensu.PRAZO_MAXIMO_CADASTRO_BANCA</b>
	 */
	public static final String MENSAGEM_PRAZO_MAXIMO_CADASTRO_BANCA = prefix + "006";
	
	
	/**
	 * Mensagem exibida para informar que um componente curricular só pode ser aproveitado pela PPG(Pró-Reitoria de Pós-Graduação)
	 * Conteúdo: Este componente curricular só pode ser aproveitado pela PPG.
	 * Tipo: ERROR.
	 */
	public static final String APROVEITAMENTO_COMPONENTE_APENAS_PPG = prefix + "007";
	
	
	/**
	 * Mensagem exibida para informar ao usuario que é necessário entrar em contato com a PPG(Pró-Reitoria de Pós-Graduação) para efetuar o cadastro de docente externo.
	 * Conteúdo: Entre em contato com a PPG(Pró-Reitoria de Pós-Graduação) para cadastrar este docente externo.
	 * Tipo: WARNING.
	 */
	public static final String CADASTRO_DOCENTE_EXTERNO_APENAS_GESTOR = prefix + "008";
	
	
	/**
	 * Mensagem exibida para informar ao usuario que é necessário entrar em contato com a PPG para solucionar problema de haver
	 * dois discentes ativos associados a mesma pessoa.
	 * Conteúdo: Não é possível realizar esta operação pois há dois discentes ativos associados a mesma pessoa. Por favor, entre em contato com a PPG.
	 * Tipo: ERROR.
	 */
	public static final String PROBLEMA_DOIS_DISCENTE_ATIVOS_ASSOCIADOS_A_PESSOA = prefix + "009";
	
	/**
	 * Mensagem exibida para informar ao usuário que o período de análise de solicitações de matrícula é pertinente ao registrado no calendário 
	 * acadêmico do programa, que pertence o discente.
	 * Conteúdo: Não é permitido realizar análise de solicitações de matrículas fora dos períodos determinados no calendário do programa do aluno.
	 * Tipo: ERROR.
	 */
	public static final String ANALISE_SOLICITACAO_MATRICULA_MEDIANTE_CALENDARIO_PROGRAMA_DISCENTE = prefix + "010";
	
	
	/**
	 * Texto que irá aparecer ao emitir a TEDE. 
	 * Conteúdo:
	 * 
	 * '<p align="justify">Na qualidade de titular dos direitos de autor da
	 *	publicação, autorizo a UFRN a disponibilizar através do site
	 *	http://bdtd.bczm.ufrn.br/tedesimplificado sem ressarcimento dos
	 *	direitos autorais, de acordo com a Lei n° 9610/98, o texto integral da
	 *	obra abaixo citada, conforme permissões assinaladas, para fins de
	 *	leitura, impressão e/ou download, a título de divulgação da produção
	 *	científica brasileira, a partir do dia %s</p>'
	 * 
	 * Tipo: INFORMATION.
	 * 
	 */
	public static final String TEXTO_EMISSAO_TEDE = prefix + "011";
	
	/**
	 * Mensagem exibida para informar ao coordenador de programa de pós-graduação, que não é mais possível cadastrar 
	 * novos discentes regulares pela operação de cadastrar novo discente.
	 * Conteúdo: Caro Coordenador(a),<br/><br/>
						 nesta operação será permitido apenas o cadastro de discentes ESPECIAIS. 
						 Para cadastrar discentes regulares utilize o gerenciamento	de candidatos do processo seletivo.
	 * Tipo: WARNING.
	 */
	public static final String NEGAR_CADASTRO_DISCENTE_REGULAR_FORA_OP_VESTIBULAR = prefix + "012";
	
}
