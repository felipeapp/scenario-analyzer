/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on 06/11/2009
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface para as constantes de mensagens gerais do SIGAA.
 * 
 * @author Leonardo Campos
 *
 */
public interface MensagensGerais {

	/**
	 * Prefixo para estas mensagens. Valor: 2_10100_
	 */
	static final String PREFIX = Sistema.SIGAA + "_" + SigaaSubsistemas.ADMINISTRACAO.getId() + "_";
	
	/**
	 * Mensagem exibida quando o identificador do discente não é passado corretamente
	 * no momento da emissão do histórico.
	 * 
	 * Conteúdo: Não foi selecionado um discente válido para a emissão do histórico. Por favor, reinicie a operação.
	 */
	public static final String DISCENTE_INVALIDO_HISTORICO = PREFIX + "01";
	
	/**
	 * Mensagem exibida quando a emissão do histórico está indisponível.
	 * 
	 * Conteúdo: A emissão de históricos encontra-se temporariamente indisponível.
	 */
	public static final String HISTORICO_INDISPONIVEL = PREFIX + "02";
	
	/**
	 * Mensagem exibida quando é detectado algum erro nas expressões de equivalência, pré-requisitos ou co-requisitos de um componente 
	 * curricular no momento da emissão do histórico.
	 * 
	 * Conteúdo: Devido a um erro em uma das expressões de equivalência, pré-requisitos ou co-requisitos de um componente, não foi possível gerar o histórico.
	 */
	public static final String ERRO_EXPRESSOES_HISTORICO = PREFIX + "03";
	
	/**
	 * Mensagem exibida quando ao popular um CPF para cadastrar dados pessoais, o município não está definido.
	 * 
	 * Conteúdo: Por Favor, atualize o município de naturalidade.
	 */
	public static final String ATUALIZE_MUNICIPIO = PREFIX + "04";
	
	/**
	 * Mensagem exibida quando não são encontrados docentes externos ativos para a pessoa que se deseja atualizar os dados pessoais.
	 * 
	 * Conteúdo: Você só pode alterar dados pessoais de docentes externos ativos.
	 */
	public static final String SOMENTE_ATUALIZAR_DADOS_PESSOAIS_DOCENTE_EXTERNO_ATIVO = PREFIX + "05";
	
	/**
	 * Mensagem exibida o usuário não possui permissão para realizar a operação.
	 * 
	 * Conteúdo: Usuário não autorizado a realizar esta operação.
	 */
	public static final String USUARIO_NAO_AUTORIZADO = PREFIX + "06";
	
	/**
	 * Mensagem exibida a operação de cadastro de dados pessoais é completada.
	 * 
	 * Conteúdo: Dados pessoais de %s cadastrados com sucesso!
	 */
	public static final String DADOS_PESSOAIS_CADASTRADOS_SUCESSO = PREFIX + "07";
	
	/**
	 * Mensagem exibida a operação de atualização de dados pessoais é completada.
	 * 
	 * Conteúdo: Dados pessoais de %s atualizados com sucesso!
	 */
	public static final String DADOS_PESSOAIS_ATUALIZADOS_SUCESSO = PREFIX + "08";
	
	/**
	 * Mensagem exibida a operação de remoção de dados pessoais é completada.
	 * 
	 * Conteúdo: Dados pessoais de %s removidos com sucesso!
	 */
	public static final String DADOS_PESSOAIS_REMOVIDOS_SUCESSO = PREFIX + "09";
	
	/**
	 * Mensagem exibida quando o usuário tenta efetuar uma atualização de dados pessoais
	 * sem ter concluído outra iniciada na mesma sessão anteriormente.
	 * 
	 * Conteúdo: Já existe outra operação de cadastro ou atualização de dados pessoais ativa. Por favor, reinicie os procedimentos.
	 */
	public static final String JA_EXISTE_OUTRA_OPERACAO_DADOS_PESSOAIS_ATIVA = PREFIX + "10";
	
	/**
	 * Mensagem exibida quando o usuário tenta gerar um relatório que não possui dados, isto é, um relatório vazio.<br>
	 * Parametro: nome do relatório.<br>
	 * Conteúdo: O %s gerado não possui páginas.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String RELATORIO_VAZIO = PREFIX + "11";

	/**
	 * Mensagem exibida quando o usuário tenta cadastrar um livro que possui o mesmo título de outro livro já cadastrado.<br>
	 * Conteúdo: Existe um livro com o mesmo título. Por favor, escolha outro título para este livro.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String CONFLITO_TITULO_LIVRO = PREFIX + "12";
	
	/**
	 * Mensagem exibida quando o usuário tenta registrar o diploma de um discente que já tem diploma registrado.<br>
	 * Parametros: discente, livro, folha, número de registro. <br>
	 * Conteúdo: O discente %s já possui diploma registrado no livro %s, folha %d, nº %d.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String DISCENTE_COM_DIPLOMA_REGISTRADO = PREFIX + "13";
	
	/**
	 * Mensagem exibida quando o usuário informa o número de um processo inválido.<br>
	 * Parametros: curso.<br>
	 * Conteúdo: Processo não Encontrado: verifique se o número do protocolo está digitado corretamente.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String NUMERO_PROCESSO_NAO_ENCONTRADO = PREFIX + "14";
	
	/**
	 * Mensagem exibida quando o usuário tenta imprimir a segunda via de diploma de um discente
	 * que ainda não possui registro de diploma.<br>
	 * 
	 * Conteúdo: O discente selecionado não possui registro de diploma.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String DISCENTE_SEM_REGISTRO_DIPLOMA = PREFIX + "15";

	/**
	 * Mensagem exibida quando o usuário tenta gerar um relatório é o relatório apresenta um erro.
	 * 
	 * Conteúdo: Ocorreu um erro durante a geração deste relatório. Por favor,contacte o suporte através do \"Abrir Chamado\"".<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String ERRO_GERACAO_RELATORIO = PREFIX + "16";
	
	/**
	 * Essa mensagem é exibida quando ao entrar na lista dos resumos do avaliador e o usuário 
	 * logado não for do quadro efetivo da instituição.<br>
	 * 
	 * Conteúdo: Apenas docentes do quadro efetivo da %s podem avaliar apresentações de resumos.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String APENAS_DOCENTE_QUADRO_EFETIVO = PREFIX + "20";
	
	/**
	 * Essa mensagem é exibida quando um discente tenta realizar o trancamento de matrícula para uma turma
	 * na qual já houve um trancamento no mesmo semestre solicitado.<br>
	 * 
	 * Conteúdo: Não é possível trancar a matrícula da Turma %s porque já possui um trancamento para ela nesse semestre. Maiores informações consulte o DAE.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String TRANCAMENTO_MATRICULA_MESMA_TURMA_NO_MESMO_SEMESTRE = PREFIX + "21";
	
	/**
	 * Mensagem exibida quando o discente não tem histórico. Ex: Alunos excluídos.
	 */
	public static final String DISCENTE_SEM_HISTORICO = PREFIX + "22";
	
	/**
	 * Essa mensagem é exibida quando um candidato tenta recuperar a senha porém o email não é encontrado na base de dados.
	 * 
	 * Conteúdo: E-Mail do usuário não coincide com o cadastrado na base de dados. Por favor, contacte a %s.
	 * Tipo: ERROR. <br>
	 */
	public static final String EMAIL_DISTINTO = PREFIX + "23";
	
	/**
	 * Essa mensagem é exibida quando um usuário deseja adicionar atividades específicas manualmente e estas atividades
	 * NÃO estão na lista de atividades disponíveis para serem selecionadas por checkbox.
	 * 
	 * Conteúdo: Conforme resolução 250/2009 as atividades deste grupo devem ser autorizadas pelo CONSEPE.
	 * Tipo: ERROR. <br>
	 */
	public static final String NAO_AUTORIZADO_PELO_CONSEPE = PREFIX + "24";

	/**
	 * Essa mensagem é exibida quando um usuário deseja imprimir um Certificado e o discente não possui Trabalho de 
	 * Conclusão de Curso cadastrado.
	 * 
	 * Conteúdo: O discente não possui Trabalho de Conclusão de Curso Cadastrado.
	 * Tipo: ERROR. <br>
	 */
	public static final String DISCENTE_SEM_TRABALHO_CONCLUSAO_CURSO_CADASTRADO = PREFIX + "25";
	
	/**
	 * Essa mensagem é exibida quando no formulário dos dados pessoais do discente
	 * foi selecionado a opção estrangeiro mas o país de naturalidade se manteve como BRASIL.
	 * 
	 * Conteúdo: Pessoas estrangeiras não podem possuir o Brasil como país de naturalidade.
	 * Tipo: ERROR. <br>
	 */
	public static final String DISCENTE_ESTRANGEIRO_PAIS_BRASIL = PREFIX + "26";

	/**
	 * Mensagem exibida durante a verificação de regularização do CPF do discente..
	 * 
	 * Conteúdo: Prezado Aluno, seu CPF %s não é válido. " +
	 *			 O seu cadastro errado pode acarretar problemas na sua vida acadêmica. " +
	 *			 Compareça %s para regularizar seu cadastro.
	 * Tipo: WARNING. <br>					
	 */
	public static final String CPF_DISCENTE_INVALIDO = PREFIX + "27";
	
	/**
	 * Mensagem exibida durante a matrícula, comsolidação e alteração das atividades específicas.
	 * Conteúdo: O discente %s não está matriculado em atividades acadêmicas específicas.
	 * Tipo: ERROR. <br>					
	 */
	public static final String DISCENTE_SEM_MATRICULA_ATIVIDADE = PREFIX + "28";
	
	/**
	 * Mensagem exibida durante a matrícula, comsolidação e alteração das atividades específicas.
	 * Conteúdo: O discente %s não está matriculado em atividades acadêmicas específicas 
	 *  do tipo %s.
	 * Tipo: ERROR. <br>					
	 */
	public static final String DISCENTE_SEM_MATRICULA_TIPO_ATIVIDADE = PREFIX + "29";
	
	/**
	 * Mensagem exibida ao iniciar uma das operações no registro de atividade.  
	 * Conteúdo: Não foi possível definir a operação ativa. Por favor reinicie os procedimentos. 
	 * Tipo: ERROR. <br>					
	 */
	public static final String OPERACAO_ATIVA_INDEFINIDA = PREFIX + "30";
	
	/**
	 * Mensagem exibida ao iniciar a busca ou listagem das atividades.  
	 * Conteúdo: O Discente não foi selecionado. Por favor informe um Discente. 
	 * Tipo: ERROR. <br>					
	 */
	public static final String DISCENTE_NAO_SELECIONADO = PREFIX + "31";
	
	/**
	 * Essa mensagem será exibida quando ao realizar o cadastro do discente e já
	 * possui um vinculo ativo para o nível de ensino.
	 * Conteúdo: O discente possui já um vinculo ativo em um curso de nível %s.
	 *  
	 */
	public static final String DISCENTE_JA_POSSUI_VINCULO_ATIVO = PREFIX + "32";
	
	/**
	 * Mensagem exibida quando ao substituir uma coordenação é verificado se existe uma já ativa para o curso ou unidade selecionado.<br/><br/>
	 *  
	 * Conteúdo: "Já existe uma %s ativa para o %s selecionado."<br/>
	 * Tipo: ERROR
	 */
	public static final String CURSO_UNIDADE_JA_POSSUI_COORDENACAO = PREFIX + "33";
	
	/**
	 * Mensagem exibida quando o usuário tenta consolidar uma turma já consolidada.<br/><br/>
	 *  
	 * Conteúdo: "Essa turma já foi consolidada anteriormente."<br/>
	 * Tipo: ERROR
	 */
	public static final String TURMA_JA_CONSOLIDADA = PREFIX + "34";
	/**
	 * Mensagem exibida quando o usuário tenta consolidar uma turma quando seu calendário ainda não está definido.<br/><br/>
	 *  
	 * Conteúdo: "Calendário para a turma não está definido, por favor entre em contato com a administração do sistema."<br/>
	 * Tipo: ERROR
	 */
	public static final String CALENDARIO_NAO_DEFINIDO = PREFIX + "35";
	
	/**
	 * Mensagem exibida quando o usuário tenta consolidar uma turma fora do período de consolidação.<br/><br/>
	 *  
	 * Conteúdo: "Não está no período oficial de consolidação de turmas. O período registrado no sistema está de %s a %s"<br/>
	 * Tipo: ERROR
	 */
	public static final String NAO_ESTA_EM_PERIODO_DE_CONSOLIDACAO = PREFIX + "36";
	
	/**
	 * Mensagem exibida quando o usuário tenta consolidar parcialmente uma turma fora do período de consolidação parcial.<br/><br/>
	 *  
	 * Conteúdo: "Não está no período oficial de consolidação parcial de turmas. O período registrado no sistema está de %s a %s"<br/>
	 * Tipo: ERROR
	 */
	public static final String NAO_ESTA_EM_PERIODO_DE_CONSOLIDACAO_PARCIAL = PREFIX + "37";
	
	/**
	 * Mensagem exibida quando o docente tenta consolidar uma turma que ainda não foi avaliada.<br/><br/>
	 *  
	 * Conteúdo: "Para efetivar a consolidação de sua turma é necessário primeiro avaliá-la. As suas notas foram salvas mas a turma não foi consolidada. Avalie-a no Portal Docente em Ensino -> Avaliação Institucional."<br/>
	 * Tipo: ERROR
	 */
	public static final String TURMA_NAO_AVALIADA = PREFIX + "38";
	
	/**
	 * Mensagem exibida quando há alguma nota inválida na consolidação.<br/><br/>
	 *  
	 * Conteúdo: "Existem notas com valores inválidos (menores que 0 ou maiores que 10)!"<br/>
	 * Tipo: ERROR
	 */
	public static final String EXISTEM_NOTAS_INVALIDAS = PREFIX + "39";

	/**
	 * Mensagem exibida quando há alguma nota inválida na subturma na consolidação.<br/><br/>
	 *  
	 * Conteúdo: "Existem notas com valores inválidos em outra subturma (menores que 0 ou maiores que 10)! Para corrigí-las, selecione a subturma."<br/>
	 * Tipo: ERROR
	 */
	public static final String EXISTEM_NOTAS_INVALIDAS_SUBTURMA = PREFIX + "40";
	
	/**
	 * Mensagem exibida quando algum discente está aprovado por média mas uma nota de recuperação foi cadastrada para ele.<br/><br/>
	 *  
	 * Conteúdo: "Você digitou uma nota de recuperação para o discente %s mas ele não está em recuperação."<br/>
	 * Tipo: ERROR
	 */
	public static final String DISCENTE_APROVADO_COM_NOTA_DE_RECUPERACAO = PREFIX + "41";
	
	/**
	 * Mensagem exibida quando algum discente está em recuperação mas não há nota de recuperação cadastrada para ele.<br/><br/>
	 *  
	 * Conteúdo: "Existe(m) aluno(s) em recuperação sem nota de recuperação cadastrada ( Turma %s - %s )"<br/>
	 * Tipo: ERROR
	 */
	public static final String DISCENTE_EM_RECUPERACAO_SEM_NOTA_DE_RECUPERACAO_TURMA = PREFIX + "42";
	
	/**
	 * Mensagem exibida quando algum discente está em recuperação mas não há nota de recuperação cadastrada para ele.<br/><br/>
	 *  
	 * Conteúdo: "Existe(m) aluno(s) em recuperação sem nota de recuperação cadastrada ( %s )"<br/>
	 * Tipo: ERROR
	 */
	public static final String DISCENTE_EM_RECUPERACAO_SEM_NOTA_DE_RECUPERACAO = PREFIX + "43";
	
	/**
	 * Mensagem exibida quando o discente está com mais faltas que a quantidade de aulas na consolidação.<br/><br/>
	 *  
	 * Conteúdo: "O número de faltas não pode ser maior que o número total de aulas definidas para o componente curricular: %s horas"<br/>
	 * Tipo: ERROR
	 */
	public static final String NUMERO_DE_FALTAS_MAIOR_QUE_TOTAL_DE_AULAS = PREFIX + "44";
	
	/**
	 * Mensagem exibida quando o discente está com notas faltando na consolidação.<br/><br/>
	 *  
	 * Conteúdo: "Você ainda não cadastrou todas as notas de todas as avaliações dos alunos"<br/>
	 * Tipo: ERROR
	 */
	public static final String FALTAM_NOTAS = PREFIX + "45";
	
	/**
	 * Mensagem exibida quando o discente está com notas faltando na consolidação.<br/><br/>
	 *  
	 * Conteúdo: "Você ainda não cadastrou todas as notas do(a) aluno(a) %s!"<br/>
	 * Tipo: ERROR
	 */
	public static final String FALTAM_NOTAS_DISCENTE = PREFIX + "46";
	
	/**
	 * Mensagem exibida quando o discente está com notas faltando na consolidação.<br/><br/>
	 *  
	 * Conteúdo: "Não foram cadastradas todas as notas do aluno %s ( Turma %s )"<br/>
	 * Tipo: ERROR
	 */
	public static final String FALTAM_NOTAS_DISCENTE_TURMA = PREFIX + "47";
	
	/**
	 * Mensagem exibida quando falta cadastrar o conceito de algum discente.<br/><br/>
	 *  
	 * Conteúdo: "Você ainda não cadastrou os conceitos de todos os alunos!"<br/>
	 * Tipo: ERROR
	 */
	public static final String FALTAM_CONCEITOS = PREFIX + "48";
	
	/**
	 * Mensagem exibida quando falta cadastrar a competencia de algum discente.<br/><br/>
	 *  
	 * Conteúdo: "Você ainda não cadastrou as competências de todos os alunos!"<br/>
	 * Tipo: ERROR
	 */
	public static final String FALTAM_COMPETENCIAS = PREFIX + "49";
	
	/**
	 * Mensagem exibida quando a soma de uma das avaliações está com valor maior que dez consolidação.<br/><br/>
	 *  
	 * Conteúdo: "A soma das notas máximas das avaliações da unidade %s é menor do que 10. É nescessário cadastrar uma quantidade de avaliações tal que a soma de suas notas máximas seja igual a 10."<br/>
	 * Tipo: ERROR
	 */
	public static final String SOMA_AVALIACOES_MAIOR_QUE_DEZ = PREFIX + "50";

	/**
	 * Mensagem exibida quando o docente cadastrou menos frequências que o necessário.<br/><br/>
	 *  
	 * Conteúdo: "É necessário lançar a frequência dos alunos para as aulas desta turma, para poder efetuar sua consolidação."<br/>
	 * Tipo: ERROR
	 */
	public static final String POUCAS_FREQUENCIAS_REGISTRADAS = PREFIX + "51";
	
	/**
	 * Mensagem exibida quando o docente cadastrou menos tópicos de aula que o necessário.<br/><br/>
	 *  
	 * Conteúdo: "É necessário lançar os tópicos de aulas ministrados para esta turma, para poder efetuar sua consolidação."<br/>
	 * Tipo: ERROR
	 */
	public static final String POUCAS_AULAS_REGISTRADAS = PREFIX + "52";
	
	/**
	 * Mensagem exibida quando o docente está tentando consolidar uma turma sem discentes.<br/><br/>
	 *  
	 * Conteúdo: "Não é possível consolidar turmas que não tem alunos!"<br/>
	 * Tipo: ERROR
	 */
	public static final String CONSOLIDANDO_TURMA_SEM_DISCENTES = PREFIX + "53";
	
	/**
	 * Mensagem exibida quando o docente está tentando consolidar uma turma sem discentes.<br/><br/>
	 *  
	 * Conteúdo: "Não é possível consolidar parcialmente, pois não existe nenhum discente aprovado por média para ser consolidado!"<br/>
	 * Tipo: ERROR
	 */
	public static final String CONSOLIDANDO_PARCIALMENTE_SEM_DISCENTES_APROVADOS = PREFIX + "54";
	
	/**
	 * Mensagem exibida quando o docente está tentando consolidar uma turma que não está com os pesos configurados corretamente.<br/><br/>
	 *  
	 * Conteúdo: "O peso das notas não foram configurados corretamente. Por favor entre em contado com a superintendência de informática!"<br/>
	 * Tipo: ERROR
	 */
	public static final String PESOS_UNIDADES_NAO_CONFIGURADOS = PREFIX + "55";
	
	/**
	 * Mensagem exibida quando o docente está tentando consolidar uma turma de EAD cuja disciplina não está com os itens do programa cadastrados.<br/><br/>
	 *  
	 * Conteúdo: "Não há itens do programa cadastrados para a disciplina desta turma. Contate a administração da %s."<br/>
	 * Tipo: ERROR
	 */
	public static final String DISCIPLINA_SEM_ITENS_DO_PROGRAMA = PREFIX + "56";
	
	/**
	 * Mensagem exibida quando um dos discentes está sem notas.<br/><br/>
	 *  
	 * Conteúdo: "O discente %s não possui notas cadastradas. Se os campos para digitação não estão aparecendo, por favor, contacte o suporte através de um chamado."<br/>
	 * Tipo: ERROR
	 */
	public static final String DISCENTE_SEM_NOTAS = PREFIX + "57";
	
	/**
	 * Mensagem exibida quando um dos discentes está sem notas.<br/><br/>
	 *  
	 * Conteúdo: "O discente %s não possui notas cadastradas. Se os campos para digitação não estão aparecendo, por favor, contacte o suporte através de um chamado ( Turma %s ) "<br/>
	 * Tipo: ERROR
	 */
	public static final String DISCENTE_SEM_NOTAS_TURMA = PREFIX + "58";
}