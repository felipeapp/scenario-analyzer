 /*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 17/11/2009
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface para as constantes de mensagens do Módulo de Turma Virtual que serão exibidas para o usuário.
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
	 * Mensagem exibida quando usuário tentar remover um tópico de aula sem remover os itens.<br/><br/>
	 *  
	 * Conteúdo: Antes de remover o Tópico de Aula, primeiro você precisa remover os itens que estão associados a ele.<br/>
	 * Tipo: WARNING
	 */
	public static final String ANTES_REMOVER_TOPICO_REMOVER_ITENS = prefix + "1";
	
	/**
	 * Mensagem exibida quando usuário for cadastrar todos os tópicos de aula para uma turma.<br/><br/>
	 *  
	 * Conteúdo: É necessário que a turma possua um horário definido ou que tenha aulas extras cadastradas para tópicos seus em lote.<br/>
	 * Tipo: WARNING
	 */
	public static final String TURMA_DEVE_POSSUIR_HORARIO_OU_AULAS_EXTRAS = prefix + "2";
	
	/**
	 * Mensagem exibida quando o usuário digitar um título com tamanho superior ao tamanho máximo.<br/><br/>
	 * 
	 * Parâmetros: Tamanho do título e Tamanho máximo.<br/>
	 * Conteúdo: "%s está com %s caracteres, mas só pode ter no máximo %s."<br/>
	 * Tipo: Error
	 */
	public static final String TAMANHO_TEXTO_MAIOR_MAXIMO = prefix + "3";

	/**
	 * Mensagem exibida quando o usuário não seleciona uma turma virtual.<br/><br/>
	 * 
	 * Conteúdo: "Nenhuma turma foi selecionada."<br/>
	 * Tipo: Error
	 */
	public static final String NENHUMA_TURMA_SELECIONADA = prefix + "4";

	/**
	 * Mensagem exibida quando não há parâmetros acadêmicos para o nível solicitado para a unidade do usuário.<br/><br/>
	 * 
	 * Conteúdo: "Os parâmetros acadêmicos para o nível %s não estão configurados em sua unidade. Por favor, entre em contato com a administração do sistema."<br/>
	 * Tipo: Error
	 */
	public static final String PARAMEROS_PARA_NIVEL_NAO_CONFIGURADOS = prefix + "5";

	/**
	 * Mensagem exibida quando a unidade do usuário não está com os pesos das avaliações definidos.<br/><br/>
	 * 
	 * Conteúdo: "A sua unidade não está com os pesos das avaliações definidos. Por favor, entre em contato com a administração do sistema."<br/>
	 * Tipo: Error
	 */
	public static final String UNIDADE_SEM_PESOS_AVALIACOES = prefix + "6";

	/**
	 * Mensagem exibida quando o usuário digita a senha errada ao consolidar uma turma.<br/><br/>
	 * 
	 * Conteúdo: "Senha inválida. Digite a senha correta para consolidar a turma."<br/>
	 * Tipo: Error
	 */
	static final String SENHA_INVALIDA_CONSOLIDAR_TURMA = prefix + "7";

	/**
	 * Mensagem exibida quando um usuário tenta responder a uma enquete sem escolher uma resposta<br/><br/>
	 * 
	 * Conteúdo: "É necessário escolher uma resposta!"<br/>
	 * Tipo: Error
	 */
	static final String ESCOLHER_RESPOSTA = prefix + "8";

	/**
	 * Mensagem Exibida quando se está tentando cadastrar a frequência para uma turma que não possui datas das unidades.<br/><br/>
	 * 
	 * Conteúdo: "As datas das unidades não estão configuradas para esta turma!"<br/>
	 * Tipo: Error
	 */
	static final String DATAS_UNIDADES_NAO_CONFIGURADAS = prefix + "9";

	/**
	 * Mensagem exibida quando o arquivo enviado possui tamanho superior ao máximo permitido.<br/><br/>
	 * 
	 * Conteúdo: "O tamanho do arquivo não pode ultrapassar %s MB."<br/>
	 * Tipo: Error
	 */
	static final String TAMANHO_ARQUIVO_EXCEDEU_MAXIMO = prefix + "10";

	/**
	 * Mensagem exibida quando uma turma não é selecionada.<br/><br/>
	 * 
	 * Conteúdo: "Nenhuma tarefa selecionada!"<br/>
	 * Tipo: Error
	 */
	static final String NENHUMA_TAREFA_SELECIONADA = prefix + "11";

	
	/**
	 * Mensagem exibida quando o prazo para envio da resposta expirou.<br/><br/>
	 * 
	 * Conteúdo: "A data de entrega para essa tarefa já expirou. Resposta não enviada!"<br/>
	 * Tipo: Error
	 */
	static final String PRAZO_EXPIRADO_TAREFA = prefix + "12";

	/**
	 * Mensagem exibida quando o coordenador do curso tenta cadastrar uma notícia para uma turma sem alunos.<br/><br/>
	 * 
	 * Conteúdo: "Não existem alunos do curso de %s matriculados na turma selecionada."<br/>
	 * Tipo: Error
	 */
	static final String NAO_HA_ALUNOS_DO_CURSO_MATRICULADOS = prefix + "13";

	/**
	 * Mensagem exibida quando um discente tenta cadastrar um fórum sem permissão.<br/><br/>
	 * 
	 * Conteúdo: "O(s) docente(s) dessa turma não permitiu(ram) a criação de fóruns por discentes."<br/>
	 * Tipo: Error
	 * 
	 */
	static final String PERMISSAO_NEGACA_DISCENTE_CRIAR_FORUM = prefix + "14";

	/**
	 * Mensagem exibida quando se tenta remover a primeira mensagem de um fórum.<br/><br/>
	 * 
	 * Conteúdo: "Essa mensagem não pode ser removida pois é a primeira mensagem cadastrada nesse tópico. Para remover essa mensagem é necessário remover o tópico inteiro (acesse o menu Aluno -> Fórum de Cursos e em seguida remova o tópico)."<br/>
	 * Tipo: Warning
	 */
	static final String REMOVER_PRIMEIRA_MENSAGEM = prefix + "15";

	/**
	 * Mensagem exibida quando não é possível identificar o curso.<br/><br/>
	 * 
	 * Conteúdo: "Não foi possível identificar o curso"<br/>
	 * Tipo: Error
	 */
	static final String CURSO_NAO_IDENTIFICADO = prefix + "16";

	/**
	 * Mensagem exibida quando a aula extra que se está tentando remover está associada a um plano de reposição de aula.<br/><br/>
	 * 
	 * Conteúdo: "Esta aula não pode ser removida, pois foi definida no seu Plano de Reposição de Aula."<br/>
	 * Tipo: Error
	 */
	static final String AULA_EXTRA_ASSOCIADA_AO_PLANO_DE_AULA = prefix + "17";

	/**
	 * Mensagem exibida quando o docente tenta cadastrar uma enquete sem respostas.<br/><br/>
	 * 
	 * Conteúdo: "Uma enquete deve ter ao menos duas repostas"<br/>
	 * Tipo: Error
	 */
	static final String ENQUETE_UMA_OU_MAIS_RESPOSTAS = prefix + "18";

	/**
	 * Mensagem exibida quando o docente tenta exibir a planilha de frequência de uma turma sem matrículas.<br/><br/>
	 * 
	 * Conteúdo: "Não há matrículas para esta turma."<br/>
	 * Tipo: Warning
	 */
	static final String NAO_HA_ALUNOS_MATRICULADOS = prefix + "19";

	/**
	 * Mensagem exibida quando o docente tenta cadastrar um tópico de aula para uma turma de EAD cujo componente curricular não possui itens de projeto cadastrados.<br/><br/>
	 * 
	 * Conteúdo: "Não há itens do programa cadastrados para a disciplina desta turma. Contate a adminsitração da EAD."<br/>
	 * Tipo: Erro
	 */
	static final String NENHUM_ITEM_PROGRAMA = prefix + "20";
	
	/**
	 * Mensagem exibida quando o docente tenta entrar em uma turma que não possui plano de curso cadastrado.<br/><br/>
	 * 
	 * Conteúdo: "Esta turma ainda não possui um plano cadastrado."<br/>
	 * Tipo: Warning
	 */
	public static final String TURMA_NAO_POSSUI_PLANO = prefix + "21";
	
	/**
	 * Mensagem exibida quando o docente está alterando o plano de curso da turma.<br/><br/>
	 * 
	 * Conteúdo: "Esta turma já possui um plano cadastrado, você pode realizar a alteração dos dados."<br/>
	 * Tipo: Warning
	 */
	public static final String TURMA_JA_POSSUI_PLANO_CADASTRADO = prefix + "22";
	
	/**
	 * Mensagem exibida quando o docente tenta salvar o plano de curso mas a turma possui poucos tópicos de aula.<br/><br/> 
	 * 
	 * Conteúdo: "Caro Professor, cadastre no mínimo %s porcento das aulas no cronograma para submissão do plano de curso."<br/>
	 * Tipo: Error
	 */
	public static final String TURMA_COM_POUCOS_TOPICOS = prefix + "23";

	/**
	 * Mensagem exibida quando o docente tenta cadastrar um plano de curso em uma turma que não contém indicações de referências.<br/><br/>
	 * 
	 * Conteúdo: "Pelo menos uma indicação de referência deve ser cadastrada."<br/>
	 * Tipo: Error
	 */
	public static final String TURMA_SEM_REFERENCIAS = prefix + "24";

	/**
	 * Mensagem exibida quando o docente tenta cadastar um plano de curso em uma turma que não contém avaliações.<br/><br/>
	 * 
	 * Conteúdo: "Pelo menos uma avaliação deve ser cadastrada."<br/>
	 * Tipo: Error
	 */
	public static final String TURMA_SEM_AVALIACOES = prefix + "25";

	/**
	 * Mensagem exibida quando o docente tenta cadastar um plano de curso em uma turma que não contém a quantidade correta de avaliações.<br/><br/>
	 * 
	 * Conteúdo: "Esta turma deve ter %s avaliações."<br/>
	 * Tipo: Error
	 */
	static final String TURMA_COM_POUCAS_AVALIACOES = prefix + "26";

	/**
	 * Mensagem exibida quando o docente tenta cadastar uma avaliação a uma turma que já possui o número máximo de avaliações.<br/><br/>
	 * 
	 * Conteúdo: "Esta turma já tem o número máximo de avaliações."<br/>
	 * Tipo: Error
	 */
	static final String TURMA_LIMITE_AVALIACOES = prefix + "27";
	
	
	/**
	 * Mensagem exibida quando na área pública tenta acessar os detalhes de uma turma que não foi dado acesso pelo docente<br/><br/>
	 * 
	 * Conteúdo: O docente não permitiu a visualização da turma.<br/>
	 * Tipo: Error
	 */
	public static final String PERMISSAO_VISUALIZACAO_EXTERNA = prefix + "28";
	
	/**
	 * Mensagem exibida quando o usuário não não possui permissão para acessar a turma e esta não está disponível para o portal público.<br/><br/>
	 * 
	 * Conteúdo: Esta turma é restrita a seus participantes.<br/>
	 * Tipo: Error
	 */
	public static final String TURMA_RESTRITA_A_PARTICIPANTES = prefix + "29";
	
}