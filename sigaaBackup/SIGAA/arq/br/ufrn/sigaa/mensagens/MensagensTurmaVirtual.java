 /*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 17/11/2009
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface para as constantes de mensagens do M�dulo de Turma Virtual que ser�o exibidas para o usu�rio.
 * 
 * @author Arlindo Rodrigues
 *
 */
public interface MensagensTurmaVirtual {

	/**
	 * Prefixo para estas mensagens.
	 */
	static final String prefix = Sistema.SIGAA + "_" + SigaaSubsistemas.PORTAL_TURMA.getId() + "_";
		
	/**
	 * Mensagem exibida quando usu�rio tentar remover um t�pico de aula sem remover os itens.<br/><br/>
	 *  
	 * Conte�do: Antes de remover o T�pico de Aula, primeiro voc� precisa remover os itens que est�o associados a ele.<br/>
	 * Tipo: WARNING
	 */
	public static final String ANTES_REMOVER_TOPICO_REMOVER_ITENS = prefix + "1";
	
	/**
	 * Mensagem exibida quando usu�rio for cadastrar todos os t�picos de aula para uma turma.<br/><br/>
	 *  
	 * Conte�do: � necess�rio que a turma possua um hor�rio definido ou que tenha aulas extras cadastradas para t�picos seus em lote.<br/>
	 * Tipo: WARNING
	 */
	public static final String TURMA_DEVE_POSSUIR_HORARIO_OU_AULAS_EXTRAS = prefix + "2";
	
	/**
	 * Mensagem exibida quando o usu�rio digitar um t�tulo com tamanho superior ao tamanho m�ximo.<br/><br/>
	 * 
	 * Par�metros: Tamanho do t�tulo e Tamanho m�ximo.<br/>
	 * Conte�do: "%s est� com %s caracteres, mas s� pode ter no m�ximo %s."<br/>
	 * Tipo: Error
	 */
	public static final String TAMANHO_TEXTO_MAIOR_MAXIMO = prefix + "3";

	/**
	 * Mensagem exibida quando o usu�rio n�o seleciona uma turma virtual.<br/><br/>
	 * 
	 * Conte�do: "Nenhuma turma foi selecionada."<br/>
	 * Tipo: Error
	 */
	public static final String NENHUMA_TURMA_SELECIONADA = prefix + "4";

	/**
	 * Mensagem exibida quando n�o h� par�metros acad�micos para o n�vel solicitado para a unidade do usu�rio.<br/><br/>
	 * 
	 * Conte�do: "Os par�metros acad�micos para o n�vel %s n�o est�o configurados em sua unidade. Por favor, entre em contato com a administra��o do sistema."<br/>
	 * Tipo: Error
	 */
	public static final String PARAMEROS_PARA_NIVEL_NAO_CONFIGURADOS = prefix + "5";

	/**
	 * Mensagem exibida quando a unidade do usu�rio n�o est� com os pesos das avalia��es definidos.<br/><br/>
	 * 
	 * Conte�do: "A sua unidade n�o est� com os pesos das avalia��es definidos. Por favor, entre em contato com a administra��o do sistema."<br/>
	 * Tipo: Error
	 */
	public static final String UNIDADE_SEM_PESOS_AVALIACOES = prefix + "6";

	/**
	 * Mensagem exibida quando o usu�rio digita a senha errada ao consolidar uma turma.<br/><br/>
	 * 
	 * Conte�do: "Senha inv�lida. Digite a senha correta para consolidar a turma."<br/>
	 * Tipo: Error
	 */
	static final String SENHA_INVALIDA_CONSOLIDAR_TURMA = prefix + "7";

	/**
	 * Mensagem exibida quando um usu�rio tenta responder a uma enquete sem escolher uma resposta<br/><br/>
	 * 
	 * Conte�do: "� necess�rio escolher uma resposta!"<br/>
	 * Tipo: Error
	 */
	static final String ESCOLHER_RESPOSTA = prefix + "8";

	/**
	 * Mensagem Exibida quando se est� tentando cadastrar a frequ�ncia para uma turma que n�o possui datas das unidades.<br/><br/>
	 * 
	 * Conte�do: "As datas das unidades n�o est�o configuradas para esta turma!"<br/>
	 * Tipo: Error
	 */
	static final String DATAS_UNIDADES_NAO_CONFIGURADAS = prefix + "9";

	/**
	 * Mensagem exibida quando o arquivo enviado possui tamanho superior ao m�ximo permitido.<br/><br/>
	 * 
	 * Conte�do: "O tamanho do arquivo n�o pode ultrapassar %s MB."<br/>
	 * Tipo: Error
	 */
	static final String TAMANHO_ARQUIVO_EXCEDEU_MAXIMO = prefix + "10";

	/**
	 * Mensagem exibida quando uma turma n�o � selecionada.<br/><br/>
	 * 
	 * Conte�do: "Nenhuma tarefa selecionada!"<br/>
	 * Tipo: Error
	 */
	static final String NENHUMA_TAREFA_SELECIONADA = prefix + "11";

	
	/**
	 * Mensagem exibida quando o prazo para envio da resposta expirou.<br/><br/>
	 * 
	 * Conte�do: "A data de entrega para essa tarefa j� expirou. Resposta n�o enviada!"<br/>
	 * Tipo: Error
	 */
	static final String PRAZO_EXPIRADO_TAREFA = prefix + "12";

	/**
	 * Mensagem exibida quando o coordenador do curso tenta cadastrar uma not�cia para uma turma sem alunos.<br/><br/>
	 * 
	 * Conte�do: "N�o existem alunos do curso de %s matriculados na turma selecionada."<br/>
	 * Tipo: Error
	 */
	static final String NAO_HA_ALUNOS_DO_CURSO_MATRICULADOS = prefix + "13";

	/**
	 * Mensagem exibida quando um discente tenta cadastrar um f�rum sem permiss�o.<br/><br/>
	 * 
	 * Conte�do: "O(s) docente(s) dessa turma n�o permitiu(ram) a cria��o de f�runs por discentes."<br/>
	 * Tipo: Error
	 * 
	 */
	static final String PERMISSAO_NEGACA_DISCENTE_CRIAR_FORUM = prefix + "14";

	/**
	 * Mensagem exibida quando se tenta remover a primeira mensagem de um f�rum.<br/><br/>
	 * 
	 * Conte�do: "Essa mensagem n�o pode ser removida pois � a primeira mensagem cadastrada nesse t�pico. Para remover essa mensagem � necess�rio remover o t�pico inteiro (acesse o menu Aluno -> F�rum de Cursos e em seguida remova o t�pico)."<br/>
	 * Tipo: Warning
	 */
	static final String REMOVER_PRIMEIRA_MENSAGEM = prefix + "15";

	/**
	 * Mensagem exibida quando n�o � poss�vel identificar o curso.<br/><br/>
	 * 
	 * Conte�do: "N�o foi poss�vel identificar o curso"<br/>
	 * Tipo: Error
	 */
	static final String CURSO_NAO_IDENTIFICADO = prefix + "16";

	/**
	 * Mensagem exibida quando a aula extra que se est� tentando remover est� associada a um plano de reposi��o de aula.<br/><br/>
	 * 
	 * Conte�do: "Esta aula n�o pode ser removida, pois foi definida no seu Plano de Reposi��o de Aula."<br/>
	 * Tipo: Error
	 */
	static final String AULA_EXTRA_ASSOCIADA_AO_PLANO_DE_AULA = prefix + "17";

	/**
	 * Mensagem exibida quando o docente tenta cadastrar uma enquete sem respostas.<br/><br/>
	 * 
	 * Conte�do: "Uma enquete deve ter ao menos duas repostas"<br/>
	 * Tipo: Error
	 */
	static final String ENQUETE_UMA_OU_MAIS_RESPOSTAS = prefix + "18";

	/**
	 * Mensagem exibida quando o docente tenta exibir a planilha de frequ�ncia de uma turma sem matr�culas.<br/><br/>
	 * 
	 * Conte�do: "N�o h� matr�culas para esta turma."<br/>
	 * Tipo: Warning
	 */
	static final String NAO_HA_ALUNOS_MATRICULADOS = prefix + "19";

	/**
	 * Mensagem exibida quando o docente tenta cadastrar um t�pico de aula para uma turma de EAD cujo componente curricular n�o possui itens de projeto cadastrados.<br/><br/>
	 * 
	 * Conte�do: "N�o h� itens do programa cadastrados para a disciplina desta turma. Contate a adminsitra��o da EAD."<br/>
	 * Tipo: Erro
	 */
	static final String NENHUM_ITEM_PROGRAMA = prefix + "20";
	
	/**
	 * Mensagem exibida quando o docente tenta entrar em uma turma que n�o possui plano de curso cadastrado.<br/><br/>
	 * 
	 * Conte�do: "Esta turma ainda n�o possui um plano cadastrado."<br/>
	 * Tipo: Warning
	 */
	public static final String TURMA_NAO_POSSUI_PLANO = prefix + "21";
	
	/**
	 * Mensagem exibida quando o docente est� alterando o plano de curso da turma.<br/><br/>
	 * 
	 * Conte�do: "Esta turma j� possui um plano cadastrado, voc� pode realizar a altera��o dos dados."<br/>
	 * Tipo: Warning
	 */
	public static final String TURMA_JA_POSSUI_PLANO_CADASTRADO = prefix + "22";
	
	/**
	 * Mensagem exibida quando o docente tenta salvar o plano de curso mas a turma possui poucos t�picos de aula.<br/><br/> 
	 * 
	 * Conte�do: "Caro Professor, cadastre no m�nimo %s porcento das aulas no cronograma para submiss�o do plano de curso."<br/>
	 * Tipo: Error
	 */
	public static final String TURMA_COM_POUCOS_TOPICOS = prefix + "23";

	/**
	 * Mensagem exibida quando o docente tenta cadastrar um plano de curso em uma turma que n�o cont�m indica��es de refer�ncias.<br/><br/>
	 * 
	 * Conte�do: "Pelo menos uma indica��o de refer�ncia deve ser cadastrada."<br/>
	 * Tipo: Error
	 */
	public static final String TURMA_SEM_REFERENCIAS = prefix + "24";

	/**
	 * Mensagem exibida quando o docente tenta cadastar um plano de curso em uma turma que n�o cont�m avalia��es.<br/><br/>
	 * 
	 * Conte�do: "Pelo menos uma avalia��o deve ser cadastrada."<br/>
	 * Tipo: Error
	 */
	public static final String TURMA_SEM_AVALIACOES = prefix + "25";

	/**
	 * Mensagem exibida quando o docente tenta cadastar um plano de curso em uma turma que n�o cont�m a quantidade correta de avalia��es.<br/><br/>
	 * 
	 * Conte�do: "Esta turma deve ter %s avalia��es."<br/>
	 * Tipo: Error
	 */
	static final String TURMA_COM_POUCAS_AVALIACOES = prefix + "26";

	/**
	 * Mensagem exibida quando o docente tenta cadastar uma avalia��o a uma turma que j� possui o n�mero m�ximo de avalia��es.<br/><br/>
	 * 
	 * Conte�do: "Esta turma j� tem o n�mero m�ximo de avalia��es."<br/>
	 * Tipo: Error
	 */
	static final String TURMA_LIMITE_AVALIACOES = prefix + "27";
	
	
	/**
	 * Mensagem exibida quando na �rea p�blica tenta acessar os detalhes de uma turma que n�o foi dado acesso pelo docente<br/><br/>
	 * 
	 * Conte�do: O docente n�o permitiu a visualiza��o da turma.<br/>
	 * Tipo: Error
	 */
	public static final String PERMISSAO_VISUALIZACAO_EXTERNA = prefix + "28";
	
	/**
	 * Mensagem exibida quando o usu�rio n�o n�o possui permiss�o para acessar a turma e esta n�o est� dispon�vel para o portal p�blico.<br/><br/>
	 * 
	 * Conte�do: Esta turma � restrita a seus participantes.<br/>
	 * Tipo: Error
	 */
	public static final String TURMA_RESTRITA_A_PARTICIPANTES = prefix + "29";
	
}