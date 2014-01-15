/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
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
	 * Mensagem exibida quando o identificador do discente n�o � passado corretamente
	 * no momento da emiss�o do hist�rico.
	 * 
	 * Conte�do: N�o foi selecionado um discente v�lido para a emiss�o do hist�rico. Por favor, reinicie a opera��o.
	 */
	public static final String DISCENTE_INVALIDO_HISTORICO = PREFIX + "01";
	
	/**
	 * Mensagem exibida quando a emiss�o do hist�rico est� indispon�vel.
	 * 
	 * Conte�do: A emiss�o de hist�ricos encontra-se temporariamente indispon�vel.
	 */
	public static final String HISTORICO_INDISPONIVEL = PREFIX + "02";
	
	/**
	 * Mensagem exibida quando � detectado algum erro nas express�es de equival�ncia, pr�-requisitos ou co-requisitos de um componente 
	 * curricular no momento da emiss�o do hist�rico.
	 * 
	 * Conte�do: Devido a um erro em uma das express�es de equival�ncia, pr�-requisitos ou co-requisitos de um componente, n�o foi poss�vel gerar o hist�rico.
	 */
	public static final String ERRO_EXPRESSOES_HISTORICO = PREFIX + "03";
	
	/**
	 * Mensagem exibida quando ao popular um CPF para cadastrar dados pessoais, o munic�pio n�o est� definido.
	 * 
	 * Conte�do: Por Favor, atualize o munic�pio de naturalidade.
	 */
	public static final String ATUALIZE_MUNICIPIO = PREFIX + "04";
	
	/**
	 * Mensagem exibida quando n�o s�o encontrados docentes externos ativos para a pessoa que se deseja atualizar os dados pessoais.
	 * 
	 * Conte�do: Voc� s� pode alterar dados pessoais de docentes externos ativos.
	 */
	public static final String SOMENTE_ATUALIZAR_DADOS_PESSOAIS_DOCENTE_EXTERNO_ATIVO = PREFIX + "05";
	
	/**
	 * Mensagem exibida o usu�rio n�o possui permiss�o para realizar a opera��o.
	 * 
	 * Conte�do: Usu�rio n�o autorizado a realizar esta opera��o.
	 */
	public static final String USUARIO_NAO_AUTORIZADO = PREFIX + "06";
	
	/**
	 * Mensagem exibida a opera��o de cadastro de dados pessoais � completada.
	 * 
	 * Conte�do: Dados pessoais de %s cadastrados com sucesso!
	 */
	public static final String DADOS_PESSOAIS_CADASTRADOS_SUCESSO = PREFIX + "07";
	
	/**
	 * Mensagem exibida a opera��o de atualiza��o de dados pessoais � completada.
	 * 
	 * Conte�do: Dados pessoais de %s atualizados com sucesso!
	 */
	public static final String DADOS_PESSOAIS_ATUALIZADOS_SUCESSO = PREFIX + "08";
	
	/**
	 * Mensagem exibida a opera��o de remo��o de dados pessoais � completada.
	 * 
	 * Conte�do: Dados pessoais de %s removidos com sucesso!
	 */
	public static final String DADOS_PESSOAIS_REMOVIDOS_SUCESSO = PREFIX + "09";
	
	/**
	 * Mensagem exibida quando o usu�rio tenta efetuar uma atualiza��o de dados pessoais
	 * sem ter conclu�do outra iniciada na mesma sess�o anteriormente.
	 * 
	 * Conte�do: J� existe outra opera��o de cadastro ou atualiza��o de dados pessoais ativa. Por favor, reinicie os procedimentos.
	 */
	public static final String JA_EXISTE_OUTRA_OPERACAO_DADOS_PESSOAIS_ATIVA = PREFIX + "10";
	
	/**
	 * Mensagem exibida quando o usu�rio tenta gerar um relat�rio que n�o possui dados, isto �, um relat�rio vazio.<br>
	 * Parametro: nome do relat�rio.<br>
	 * Conte�do: O %s gerado n�o possui p�ginas.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String RELATORIO_VAZIO = PREFIX + "11";

	/**
	 * Mensagem exibida quando o usu�rio tenta cadastrar um livro que possui o mesmo t�tulo de outro livro j� cadastrado.<br>
	 * Conte�do: Existe um livro com o mesmo t�tulo. Por favor, escolha outro t�tulo para este livro.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String CONFLITO_TITULO_LIVRO = PREFIX + "12";
	
	/**
	 * Mensagem exibida quando o usu�rio tenta registrar o diploma de um discente que j� tem diploma registrado.<br>
	 * Parametros: discente, livro, folha, n�mero de registro. <br>
	 * Conte�do: O discente %s j� possui diploma registrado no livro %s, folha %d, n� %d.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String DISCENTE_COM_DIPLOMA_REGISTRADO = PREFIX + "13";
	
	/**
	 * Mensagem exibida quando o usu�rio informa o n�mero de um processo inv�lido.<br>
	 * Parametros: curso.<br>
	 * Conte�do: Processo n�o Encontrado: verifique se o n�mero do protocolo est� digitado corretamente.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String NUMERO_PROCESSO_NAO_ENCONTRADO = PREFIX + "14";
	
	/**
	 * Mensagem exibida quando o usu�rio tenta imprimir a segunda via de diploma de um discente
	 * que ainda n�o possui registro de diploma.<br>
	 * 
	 * Conte�do: O discente selecionado n�o possui registro de diploma.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String DISCENTE_SEM_REGISTRO_DIPLOMA = PREFIX + "15";

	/**
	 * Mensagem exibida quando o usu�rio tenta gerar um relat�rio � o relat�rio apresenta um erro.
	 * 
	 * Conte�do: Ocorreu um erro durante a gera��o deste relat�rio. Por favor,contacte o suporte atrav�s do \"Abrir Chamado\"".<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String ERRO_GERACAO_RELATORIO = PREFIX + "16";
	
	/**
	 * Essa mensagem � exibida quando ao entrar na lista dos resumos do avaliador e o usu�rio 
	 * logado n�o for do quadro efetivo da institui��o.<br>
	 * 
	 * Conte�do: Apenas docentes do quadro efetivo da %s podem avaliar apresenta��es de resumos.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String APENAS_DOCENTE_QUADRO_EFETIVO = PREFIX + "20";
	
	/**
	 * Essa mensagem � exibida quando um discente tenta realizar o trancamento de matr�cula para uma turma
	 * na qual j� houve um trancamento no mesmo semestre solicitado.<br>
	 * 
	 * Conte�do: N�o � poss�vel trancar a matr�cula da Turma %s porque j� possui um trancamento para ela nesse semestre. Maiores informa��es consulte o DAE.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String TRANCAMENTO_MATRICULA_MESMA_TURMA_NO_MESMO_SEMESTRE = PREFIX + "21";
	
	/**
	 * Mensagem exibida quando o discente n�o tem hist�rico. Ex: Alunos exclu�dos.
	 */
	public static final String DISCENTE_SEM_HISTORICO = PREFIX + "22";
	
	/**
	 * Essa mensagem � exibida quando um candidato tenta recuperar a senha por�m o email n�o � encontrado na base de dados.
	 * 
	 * Conte�do: E-Mail do usu�rio n�o coincide com o cadastrado na base de dados. Por favor, contacte a %s.
	 * Tipo: ERROR. <br>
	 */
	public static final String EMAIL_DISTINTO = PREFIX + "23";
	
	/**
	 * Essa mensagem � exibida quando um usu�rio deseja adicionar atividades espec�ficas manualmente e estas atividades
	 * N�O est�o na lista de atividades dispon�veis para serem selecionadas por checkbox.
	 * 
	 * Conte�do: Conforme resolu��o 250/2009 as atividades deste grupo devem ser autorizadas pelo CONSEPE.
	 * Tipo: ERROR. <br>
	 */
	public static final String NAO_AUTORIZADO_PELO_CONSEPE = PREFIX + "24";

	/**
	 * Essa mensagem � exibida quando um usu�rio deseja imprimir um Certificado e o discente n�o possui Trabalho de 
	 * Conclus�o de Curso cadastrado.
	 * 
	 * Conte�do: O discente n�o possui Trabalho de Conclus�o de Curso Cadastrado.
	 * Tipo: ERROR. <br>
	 */
	public static final String DISCENTE_SEM_TRABALHO_CONCLUSAO_CURSO_CADASTRADO = PREFIX + "25";
	
	/**
	 * Essa mensagem � exibida quando no formul�rio dos dados pessoais do discente
	 * foi selecionado a op��o estrangeiro mas o pa�s de naturalidade se manteve como BRASIL.
	 * 
	 * Conte�do: Pessoas estrangeiras n�o podem possuir o Brasil como pa�s de naturalidade.
	 * Tipo: ERROR. <br>
	 */
	public static final String DISCENTE_ESTRANGEIRO_PAIS_BRASIL = PREFIX + "26";

	/**
	 * Mensagem exibida durante a verifica��o de regulariza��o do CPF do discente..
	 * 
	 * Conte�do: Prezado Aluno, seu CPF %s n�o � v�lido. " +
	 *			 O seu cadastro errado pode acarretar problemas na sua vida acad�mica. " +
	 *			 Compare�a %s para regularizar seu cadastro.
	 * Tipo: WARNING. <br>					
	 */
	public static final String CPF_DISCENTE_INVALIDO = PREFIX + "27";
	
	/**
	 * Mensagem exibida durante a matr�cula, comsolida��o e altera��o das atividades espec�ficas.
	 * Conte�do: O discente %s n�o est� matriculado em atividades acad�micas espec�ficas.
	 * Tipo: ERROR. <br>					
	 */
	public static final String DISCENTE_SEM_MATRICULA_ATIVIDADE = PREFIX + "28";
	
	/**
	 * Mensagem exibida durante a matr�cula, comsolida��o e altera��o das atividades espec�ficas.
	 * Conte�do: O discente %s n�o est� matriculado em atividades acad�micas espec�ficas 
	 *  do tipo %s.
	 * Tipo: ERROR. <br>					
	 */
	public static final String DISCENTE_SEM_MATRICULA_TIPO_ATIVIDADE = PREFIX + "29";
	
	/**
	 * Mensagem exibida ao iniciar uma das opera��es no registro de atividade.  
	 * Conte�do: N�o foi poss�vel definir a opera��o ativa. Por favor reinicie os procedimentos. 
	 * Tipo: ERROR. <br>					
	 */
	public static final String OPERACAO_ATIVA_INDEFINIDA = PREFIX + "30";
	
	/**
	 * Mensagem exibida ao iniciar a busca ou listagem das atividades.  
	 * Conte�do: O Discente n�o foi selecionado. Por favor informe um Discente. 
	 * Tipo: ERROR. <br>					
	 */
	public static final String DISCENTE_NAO_SELECIONADO = PREFIX + "31";
	
	/**
	 * Essa mensagem ser� exibida quando ao realizar o cadastro do discente e j�
	 * possui um vinculo ativo para o n�vel de ensino.
	 * Conte�do: O discente possui j� um vinculo ativo em um curso de n�vel %s.
	 *  
	 */
	public static final String DISCENTE_JA_POSSUI_VINCULO_ATIVO = PREFIX + "32";
	
	/**
	 * Mensagem exibida quando ao substituir uma coordena��o � verificado se existe uma j� ativa para o curso ou unidade selecionado.<br/><br/>
	 *  
	 * Conte�do: "J� existe uma %s ativa para o %s selecionado."<br/>
	 * Tipo: ERROR
	 */
	public static final String CURSO_UNIDADE_JA_POSSUI_COORDENACAO = PREFIX + "33";
	
	/**
	 * Mensagem exibida quando o usu�rio tenta consolidar uma turma j� consolidada.<br/><br/>
	 *  
	 * Conte�do: "Essa turma j� foi consolidada anteriormente."<br/>
	 * Tipo: ERROR
	 */
	public static final String TURMA_JA_CONSOLIDADA = PREFIX + "34";
	/**
	 * Mensagem exibida quando o usu�rio tenta consolidar uma turma quando seu calend�rio ainda n�o est� definido.<br/><br/>
	 *  
	 * Conte�do: "Calend�rio para a turma n�o est� definido, por favor entre em contato com a administra��o do sistema."<br/>
	 * Tipo: ERROR
	 */
	public static final String CALENDARIO_NAO_DEFINIDO = PREFIX + "35";
	
	/**
	 * Mensagem exibida quando o usu�rio tenta consolidar uma turma fora do per�odo de consolida��o.<br/><br/>
	 *  
	 * Conte�do: "N�o est� no per�odo oficial de consolida��o de turmas. O per�odo registrado no sistema est� de %s a %s"<br/>
	 * Tipo: ERROR
	 */
	public static final String NAO_ESTA_EM_PERIODO_DE_CONSOLIDACAO = PREFIX + "36";
	
	/**
	 * Mensagem exibida quando o usu�rio tenta consolidar parcialmente uma turma fora do per�odo de consolida��o parcial.<br/><br/>
	 *  
	 * Conte�do: "N�o est� no per�odo oficial de consolida��o parcial de turmas. O per�odo registrado no sistema est� de %s a %s"<br/>
	 * Tipo: ERROR
	 */
	public static final String NAO_ESTA_EM_PERIODO_DE_CONSOLIDACAO_PARCIAL = PREFIX + "37";
	
	/**
	 * Mensagem exibida quando o docente tenta consolidar uma turma que ainda n�o foi avaliada.<br/><br/>
	 *  
	 * Conte�do: "Para efetivar a consolida��o de sua turma � necess�rio primeiro avali�-la. As suas notas foram salvas mas a turma n�o foi consolidada. Avalie-a no Portal Docente em Ensino -> Avalia��o Institucional."<br/>
	 * Tipo: ERROR
	 */
	public static final String TURMA_NAO_AVALIADA = PREFIX + "38";
	
	/**
	 * Mensagem exibida quando h� alguma nota inv�lida na consolida��o.<br/><br/>
	 *  
	 * Conte�do: "Existem notas com valores inv�lidos (menores que 0 ou maiores que 10)!"<br/>
	 * Tipo: ERROR
	 */
	public static final String EXISTEM_NOTAS_INVALIDAS = PREFIX + "39";

	/**
	 * Mensagem exibida quando h� alguma nota inv�lida na subturma na consolida��o.<br/><br/>
	 *  
	 * Conte�do: "Existem notas com valores inv�lidos em outra subturma (menores que 0 ou maiores que 10)! Para corrig�-las, selecione a subturma."<br/>
	 * Tipo: ERROR
	 */
	public static final String EXISTEM_NOTAS_INVALIDAS_SUBTURMA = PREFIX + "40";
	
	/**
	 * Mensagem exibida quando algum discente est� aprovado por m�dia mas uma nota de recupera��o foi cadastrada para ele.<br/><br/>
	 *  
	 * Conte�do: "Voc� digitou uma nota de recupera��o para o discente %s mas ele n�o est� em recupera��o."<br/>
	 * Tipo: ERROR
	 */
	public static final String DISCENTE_APROVADO_COM_NOTA_DE_RECUPERACAO = PREFIX + "41";
	
	/**
	 * Mensagem exibida quando algum discente est� em recupera��o mas n�o h� nota de recupera��o cadastrada para ele.<br/><br/>
	 *  
	 * Conte�do: "Existe(m) aluno(s) em recupera��o sem nota de recupera��o cadastrada ( Turma %s - %s )"<br/>
	 * Tipo: ERROR
	 */
	public static final String DISCENTE_EM_RECUPERACAO_SEM_NOTA_DE_RECUPERACAO_TURMA = PREFIX + "42";
	
	/**
	 * Mensagem exibida quando algum discente est� em recupera��o mas n�o h� nota de recupera��o cadastrada para ele.<br/><br/>
	 *  
	 * Conte�do: "Existe(m) aluno(s) em recupera��o sem nota de recupera��o cadastrada ( %s )"<br/>
	 * Tipo: ERROR
	 */
	public static final String DISCENTE_EM_RECUPERACAO_SEM_NOTA_DE_RECUPERACAO = PREFIX + "43";
	
	/**
	 * Mensagem exibida quando o discente est� com mais faltas que a quantidade de aulas na consolida��o.<br/><br/>
	 *  
	 * Conte�do: "O n�mero de faltas n�o pode ser maior que o n�mero total de aulas definidas para o componente curricular: %s horas"<br/>
	 * Tipo: ERROR
	 */
	public static final String NUMERO_DE_FALTAS_MAIOR_QUE_TOTAL_DE_AULAS = PREFIX + "44";
	
	/**
	 * Mensagem exibida quando o discente est� com notas faltando na consolida��o.<br/><br/>
	 *  
	 * Conte�do: "Voc� ainda n�o cadastrou todas as notas de todas as avalia��es dos alunos"<br/>
	 * Tipo: ERROR
	 */
	public static final String FALTAM_NOTAS = PREFIX + "45";
	
	/**
	 * Mensagem exibida quando o discente est� com notas faltando na consolida��o.<br/><br/>
	 *  
	 * Conte�do: "Voc� ainda n�o cadastrou todas as notas do(a) aluno(a) %s!"<br/>
	 * Tipo: ERROR
	 */
	public static final String FALTAM_NOTAS_DISCENTE = PREFIX + "46";
	
	/**
	 * Mensagem exibida quando o discente est� com notas faltando na consolida��o.<br/><br/>
	 *  
	 * Conte�do: "N�o foram cadastradas todas as notas do aluno %s ( Turma %s )"<br/>
	 * Tipo: ERROR
	 */
	public static final String FALTAM_NOTAS_DISCENTE_TURMA = PREFIX + "47";
	
	/**
	 * Mensagem exibida quando falta cadastrar o conceito de algum discente.<br/><br/>
	 *  
	 * Conte�do: "Voc� ainda n�o cadastrou os conceitos de todos os alunos!"<br/>
	 * Tipo: ERROR
	 */
	public static final String FALTAM_CONCEITOS = PREFIX + "48";
	
	/**
	 * Mensagem exibida quando falta cadastrar a competencia de algum discente.<br/><br/>
	 *  
	 * Conte�do: "Voc� ainda n�o cadastrou as compet�ncias de todos os alunos!"<br/>
	 * Tipo: ERROR
	 */
	public static final String FALTAM_COMPETENCIAS = PREFIX + "49";
	
	/**
	 * Mensagem exibida quando a soma de uma das avalia��es est� com valor maior que dez consolida��o.<br/><br/>
	 *  
	 * Conte�do: "A soma das notas m�ximas das avalia��es da unidade %s � menor do que 10. � nescess�rio cadastrar uma quantidade de avalia��es tal que a soma de suas notas m�ximas seja igual a 10."<br/>
	 * Tipo: ERROR
	 */
	public static final String SOMA_AVALIACOES_MAIOR_QUE_DEZ = PREFIX + "50";

	/**
	 * Mensagem exibida quando o docente cadastrou menos frequ�ncias que o necess�rio.<br/><br/>
	 *  
	 * Conte�do: "� necess�rio lan�ar a frequ�ncia dos alunos para as aulas desta turma, para poder efetuar sua consolida��o."<br/>
	 * Tipo: ERROR
	 */
	public static final String POUCAS_FREQUENCIAS_REGISTRADAS = PREFIX + "51";
	
	/**
	 * Mensagem exibida quando o docente cadastrou menos t�picos de aula que o necess�rio.<br/><br/>
	 *  
	 * Conte�do: "� necess�rio lan�ar os t�picos de aulas ministrados para esta turma, para poder efetuar sua consolida��o."<br/>
	 * Tipo: ERROR
	 */
	public static final String POUCAS_AULAS_REGISTRADAS = PREFIX + "52";
	
	/**
	 * Mensagem exibida quando o docente est� tentando consolidar uma turma sem discentes.<br/><br/>
	 *  
	 * Conte�do: "N�o � poss�vel consolidar turmas que n�o tem alunos!"<br/>
	 * Tipo: ERROR
	 */
	public static final String CONSOLIDANDO_TURMA_SEM_DISCENTES = PREFIX + "53";
	
	/**
	 * Mensagem exibida quando o docente est� tentando consolidar uma turma sem discentes.<br/><br/>
	 *  
	 * Conte�do: "N�o � poss�vel consolidar parcialmente, pois n�o existe nenhum discente aprovado por m�dia para ser consolidado!"<br/>
	 * Tipo: ERROR
	 */
	public static final String CONSOLIDANDO_PARCIALMENTE_SEM_DISCENTES_APROVADOS = PREFIX + "54";
	
	/**
	 * Mensagem exibida quando o docente est� tentando consolidar uma turma que n�o est� com os pesos configurados corretamente.<br/><br/>
	 *  
	 * Conte�do: "O peso das notas n�o foram configurados corretamente. Por favor entre em contado com a superintend�ncia de inform�tica!"<br/>
	 * Tipo: ERROR
	 */
	public static final String PESOS_UNIDADES_NAO_CONFIGURADOS = PREFIX + "55";
	
	/**
	 * Mensagem exibida quando o docente est� tentando consolidar uma turma de EAD cuja disciplina n�o est� com os itens do programa cadastrados.<br/><br/>
	 *  
	 * Conte�do: "N�o h� itens do programa cadastrados para a disciplina desta turma. Contate a administra��o da %s."<br/>
	 * Tipo: ERROR
	 */
	public static final String DISCIPLINA_SEM_ITENS_DO_PROGRAMA = PREFIX + "56";
	
	/**
	 * Mensagem exibida quando um dos discentes est� sem notas.<br/><br/>
	 *  
	 * Conte�do: "O discente %s n�o possui notas cadastradas. Se os campos para digita��o n�o est�o aparecendo, por favor, contacte o suporte atrav�s de um chamado."<br/>
	 * Tipo: ERROR
	 */
	public static final String DISCENTE_SEM_NOTAS = PREFIX + "57";
	
	/**
	 * Mensagem exibida quando um dos discentes est� sem notas.<br/><br/>
	 *  
	 * Conte�do: "O discente %s n�o possui notas cadastradas. Se os campos para digita��o n�o est�o aparecendo, por favor, contacte o suporte atrav�s de um chamado ( Turma %s ) "<br/>
	 * Tipo: ERROR
	 */
	public static final String DISCENTE_SEM_NOTAS_TURMA = PREFIX + "58";
}