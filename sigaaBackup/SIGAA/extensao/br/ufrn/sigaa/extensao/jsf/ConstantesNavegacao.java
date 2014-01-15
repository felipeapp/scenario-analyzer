/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/12/2009
 *
 */
package br.ufrn.sigaa.extensao.jsf; 

import java.util.HashMap;
import java.util.Map;

/*******************************************************************************
 * Classe utilizada para as constantes de navega��o do sistema de extens�o
 * 
 * @author Gleydson
 * @author Victor Hugo
 * 
 ******************************************************************************/
public class ConstantesNavegacao {

	/** ATIVIDADE DE EXTENS�O */
	/** Visualiza dados da a��o de extens�o */
	public static final String ATIVIDADE_VIEW						= "/extensao/Atividade/view.jsp";
	/** Visualiza dados da a��o de extens�o para impress�o */
	public static final String ATIVIDADE_PRINT						= "/extensao/Atividade/print.jsp";
	/** Visualiza dados da a��o de extens�o na �rea publica do sistama. */
	public static final String ATIVIDADE_VIEW_PUBLIC				= "/public/extensao/view.jsp";
	/** Visualiza dados da sub-atividade na �rea publica do sistema. */
	public static final String SUB_ATIVIDADE_VIEW_PUBLIC				= "/public/extensao/view_sub_atividade.jsp";
	/** Visualiza dados do or�amento da a��o de extens�o */
	public static final String ATIVIDADE_ORCAMENTO_APROVADO_VIEW	= "/extensao/Atividade/orcamento_aprovado.jsp";

	/** P�gina que permite ao docente selecionar quais a��es est�o vinculadas a a��o que est� sendo cadastrada. */
	public static final String SELECIONA_ATIVIDADE					= "/extensao/Atividade/seleciona_atividade.jsp";
	/** P�gina que permite ao docente informar os dados gerais da a��o que est� sendo cadastrada. */
	public static final String DADOS_GERAIS							= "/extensao/Atividade/dados_gerais.jsp";
	/** P�gina que permite ao docente informar os membros da equipe da a��o que est� sendo cadastrada. */
	public static final String MEMBROS_EQUIPE						= "/extensao/Atividade/membros_equipe.jsp";
	/** P�gina que permite ao docente informar o or�amento que ser� utilizado na a��o que est� sendo cadastrada. */
	public static final String ORCAMENTO_DETALHADO					= "/extensao/Atividade/orcamento.jsp";
	/** P�gina que permite ao docente informar a forma como o or�amento ser�/foi captado para a��o que est� sendo cadastrada. */	
	public static final String ORCAMENTO_CONSOLIDADO				= "/extensao/Atividade/orcamento_consolidado.jsp";
	/** P�gina que permite ao docente anexar arquivos a a��o que est� sendo cadastrada. */	
	public static final String ANEXAR_ARQUIVOS 						= "/extensao/Atividade/anexar_arquivos.jsp";
	/** P�gina que permite ao docente anexar fotos a a��o que est� sendo cadastrada. */
	public static final String ANEXAR_FOTOS 						= "/extensao/Atividade/anexar_fotos.jsp";
	/** P�gina que exibe um resumo da a��o que est� sendo cadastrada. */
	public static final String RESUMO								= "/extensao/Atividade/resumo.jsp";
	/** P�gina que permite ao remover uma a��o de extens�o. */
	public static final String REMOVER								= "/extensao/Atividade/remover.jsp";
	/** P�gina que permite ao docente reativar a a��o que foi inativada por cadastro expirado. */
	public static final String REATIVAR								= "/extensao/AlterarAtividade/form_reativar_acao.jsp";
	/** P�gina que permite ao docente confirmar a execu��o da a��o que foi aprovada. */
	public static final String CONFIRMAR_EXECUCAO					= "/extensao/Atividade/confirmar_execucao.jsp";
	/** P�gina que permite ao docente seguir com o cadastro da a��o junto a proplan para registro de conv�nio. */
	public static final String CONTINUAR_PROPLAM					= "/projetos/ProjetoBase/continuar_proplan.jsp";
	
	/** P�gina que permite ao docente informar dados espec�ficos da a��o projeto que est� sendo cadastrada. */
	public static final String PROJETO						= "/extensao/Atividade/projeto.jsp";
	/** P�gina que permite ao docente informar os objetivos a a��o que est� sendo cadastrada. */
	public static final String OBJETIVOS_ESPERADOS 			= "/extensao/Atividade/objetivos_esperados.jsp";
	/** P�gina que permite ao docente informar os objetivos a a��o que est� sendo cadastrada. */
	public static final String OBJETIVOS_ESPERADOS_EXTENSAO	= "/extensao/Atividade/objetivos_esperados_extensao.jsp";
	/** P�gina que permite ao docente o cadastro do objetivo e atividades do projeto */
	public static final String CADASTRO_OBJETIVO            = "/extensao/Atividade/include/cadastrar_objetivo.jsp";
	/** P�gina que permite ao docente informar dados espec�ficos da a��o programa que est� sendo cadastrada. */
	public static final String PROGRAMA						= "/extensao/Atividade/programa.jsp";
	/** P�gina que permite ao docente informar quais as atividades realizadas na a��o que est� sendo cadastrada. */
	public static final String ATIVIDADES 					= "/extensao/Atividade/atividades.jsp";
	/** P�gina que permite ao docente informar dados espec�ficos da a��o curso ou evento que est� sendo cadastrada. */
	public static final String CURSO_EVENTO					= "/extensao/Atividade/curso_evento.jsp";
	/** P�gina que permite ao docente informar dados espec�ficos da a��o curso ou evento que est� sendo cadastrada. */
	public static final String SUB_ATIVIDADES					= "/extensao/Atividade/sub_atividades.jsp";
	/** P�gina que permite ao docente informar dados espec�ficos da a��o produto que est� sendo cadastrada. */
	public static final String PRODUTO						= "/extensao/Atividade/produto.jsp";
	/** P�gina que permite ao docente visualizar dados do participante da a��o. */
	public static final String VIEW_PARTICIPANTES						= "/extensao/Atividade/view_participantes.jsp";

	/** Sequ�ncia de passos necess�rios para o cadastro de uma a��o de extens�o. Utilizado na execu��o do wizard do cadastro. */
	public static final Map<String, String> PASSOS_ATIVIDADES_EXTENSAO = new HashMap<String, String>() ;
	static {
		PASSOS_ATIVIDADES_EXTENSAO.put(SELECIONA_ATIVIDADE, "Sele��o do tipo da a��o de extens�o");
		PASSOS_ATIVIDADES_EXTENSAO.put(DADOS_GERAIS, "Dados gerais da a��o");
		PASSOS_ATIVIDADES_EXTENSAO.put(MEMBROS_EQUIPE, "Membros da equipe da a��o");
		PASSOS_ATIVIDADES_EXTENSAO.put(ORCAMENTO_DETALHADO, "Or�amento detalhado");
		PASSOS_ATIVIDADES_EXTENSAO.put(ORCAMENTO_CONSOLIDADO, "Or�amento consolidado");
		PASSOS_ATIVIDADES_EXTENSAO.put(ANEXAR_ARQUIVOS, "Anexar arquivos");
		PASSOS_ATIVIDADES_EXTENSAO.put(ANEXAR_FOTOS, "Anexar fotos");
		PASSOS_ATIVIDADES_EXTENSAO.put(RESUMO, "Resumo da a��o");
		
		PASSOS_ATIVIDADES_EXTENSAO.put(PROJETO, "Dados do projeto");
		PASSOS_ATIVIDADES_EXTENSAO.put(OBJETIVOS_ESPERADOS, "Objetivos e resultados esperados");
		PASSOS_ATIVIDADES_EXTENSAO.put(OBJETIVOS_ESPERADOS_EXTENSAO, "Equipe Executora");

		PASSOS_ATIVIDADES_EXTENSAO.put(PROGRAMA, "Dados do programa");
		PASSOS_ATIVIDADES_EXTENSAO.put(ATIVIDADES, "Atividades vinculadas");

		PASSOS_ATIVIDADES_EXTENSAO.put(CURSO_EVENTO, "Dados do curso/evento");
		PASSOS_ATIVIDADES_EXTENSAO.put(SUB_ATIVIDADES, "Mini Atividades");

		PASSOS_ATIVIDADES_EXTENSAO.put(PRODUTO, "Dados do produto");
	}

	/** LISTAGEM DE ATIVIDADES DE EXTENS�O */
	/** Lista de a��es de extens�o sem planos de trabalho. */
	public static final String LISTA_ATIVIDADES_SEM_PLANO					= "/extensao/Atividade/lista_atividades_sem_plano.jsp";
	/** Lista de a��es de extens�o. */
	public static final String LISTA 										= "/extensao/Atividade/lista.jsp";
	/** Lista de a��es de extens�o do usu�rio logado. */
	public static final String LISTA_MINHAS_ATIVIDADES						= "/extensao/Atividade/lista_minhas_atividades.jsp";
	/** Lista de a��es de extens�o do usu�rio logado pendentes de envio. */
	public static final String LISTA_ATIVIDADES_PENDENTES					= "/extensao/Atividade/lista_atividades_pendentes.jsp";
	/** Lista de a��es de extens�o. */
	public static final String LISTA_ATIVIDADES_PERIODO_CONCLUSAO			= "/extensao/Atividade/lista_atividades_periodo_conclusao.jsp";

	/** ALTERAR ATIVIDADE PROEX */
	/** Lista de a��es de extens�o que podem ser alteradas. */
	public static final String ALTERAR_ATIVIDADE_LISTA						= "/extensao/AlterarAtividade/lista.jsp";
	/** Fomul�rio que permite altera��o da situa��o da a��o de extens�o. */
	public static final String ALTERAR_SITUACAO_ATIVIDADE_FORM				= "/extensao/AlterarAtividade/form_situacao_atividade.jsp";
	/** Formul�rio que permite a altera��o da quantidade de bolsas da a��o de extens�o. */
	public static final String ALTERAR_BOLSAS_ATIVIDADE_FORM				= "/extensao/AlterarAtividade/form_bolsas.jsp";

	/** RENOVA��O DE ATIVIDADE DE EXTENS�O */
	/** Formul�rio para renova��o da a��o de extens�o. */
	public static final String RENOVACAO_FORM	= "/extensao/Atividade/renovar.jsp";

	/** PRESTACAO DE SERVI�O */
	/** Fluxo de cadastro de propostas de presta��o de servi�os - contratante. */	
	public static final String CONTRATANTE 										= "/extensao/PrestacaoServico/contratante.jsp";
	/** Fluxo de cadastro de propostas de presta��o de servi�os - equipe */
	public static final String EQUIPE 											= "/extensao/PrestacaoServico/equipe.jsp";
	/** Fluxo de cadastro de propostas de presta��o de servi�os - servi�os */
	public static final String SERVICO 											= "/extensao/PrestacaoServico/servico.jsp";
	/** Fluxo de cadastro de propostas de presta��o de servi�os - descri��o */
	public static final String DESCRICAO			 							= "/extensao/PrestacaoServico/descricao.jsp";
	/** Fluxo de cadastro de propostas de presta��o de servi�os - resumo */
	public static final String RESUMO_PS		 								= "/extensao/PrestacaoServico/resumo.jsp";

	
	/** RELATORIO BOLSISTA EXTENS�O */
	/** Cadastro de relat�rio de bolsistas - formul�rio de cadastros. */
	public static final String RELATORIOBOLSISTAEXTENSAO_FORM					= "/extensao/RelatorioBolsistaExtensao/form.jsp";
	/** Cadastro de relat�rio de bolsistas - lista de relat�rios. */
	public static final String RELATORIOBOLSISTAEXTENSAO_LISTA	 				= "/extensao/RelatorioBolsistaExtensao/lista.jsp";
	/** Cadastro de relat�rio de bolsistas - formul�rio de valida��o. */
	public static final String RELATORIOBOLSISTAEXTENSAO_AVALIAR_FORM			= "/extensao/RelatorioBolsistaExtensao/avaliar_relatorio_form.jsp";
	/** Cadastro de relat�rio de bolsistas - lista de relat�rios para valida��o. */
	public static final String RELATORIOBOLSISTAEXTENSAO_AVALIAR_LISTA			= "/extensao/RelatorioBolsistaExtensao/avaliar_relatorio_lista.jsp";
	/** Cadastro de relat�rio de bolsistas - visualiza��o do relat�rio. */
	public static final String RELATORIOBOLSISTAEXTENSAO_VIEW	 				= "/extensao/RelatorioBolsistaExtensao/view.jsp";	

	
	/** RELATORIO DE ACAO DE EXTENS�O */
	/** Cadastro de relat�rio de a��o de extens�o - lista de relat�rios */
	public static final String RELATORIOACAOEXTENSAO_LISTA						= "/extensao/RelatorioAcaoExtensao/lista.jsp";
	/** Cadastro de relat�rio de a��o de extens�o - lista de a��es de extens�o pendentes de envio de relat�rios. */
	public static final String NOTIFICACAO_AUSENCIA_RELATORIO					= "/extensao/RelatorioAcaoExtensao/notificacao_ausencia_rel.jsp";
	
	/** RELATORIO PROJETOS EXTENS�O */
	/** Cadastro de relat�rio de a��o de extens�o - relat�rio de projetos */
	public static final String RELATORIOPROJETO_FORM 							= "/extensao/RelatorioProjeto/form.jsp";
	/** Cadastro de relat�rio de a��o de extens�o - visualiza��o do relat�rio */
	public static final String RELATORIOPROJETO_VIEW 							= "/extensao/RelatorioProjeto/view.jsp";
	/** Cadastro de relat�rio de a��o de extens�o - exclus�o do relat�rio */
	public static final String RELATORIOPROJETO_DELETE							= "/extensao/RelatorioProjeto/delete.jsp";

	/** Novo Cadastro de relat�rio de a��o de extens�o - relat�rio de projetos */
	public static final String RELATORIO_EXTENSAO_FORM 							= "/extensao/RelatorioProjeto/form_relatorio.jsp";

	/** RELATORIO CURSOS EVENTOS */
	/** Cadastro de relat�rio de a��o de extens�o - relat�rio de cursos e eventos */
	public static final String RELATORIOCURSOSEVENTOS_FORM 						= "/extensao/RelatorioCursoEvento/form.jsp";
	/** Cadastro de relat�rio de a��o de extens�o - visualiza��o do relat�rio de cursos e eventos */
	public static final String RELATORIOCURSOSEVENTOS_VIEW 						= "/extensao/RelatorioCursoEvento/view.jsp";
	/** Cadastro de relat�rio de a��o de extens�o - exclus�o do relat�rio */
	public static final String RELATORIOCURSOSEVENTOS_DELETE				= "/extensao/RelatorioCursoEvento/delete.jsp";

	/** RELATORIO PROGRAMAS */
	/** Cadastro de relat�rio de a��o de extens�o - relat�rio de programa */
	public static final String RELATORIO_PROGRAMA_FORM 						= "/extensao/RelatorioPrograma/form.jsp";
	/** Cadastro de relat�rio de a��o de extens�o - visualiza��o de relat�rio de programa */
	public static final String RELATORIO_PROGRAMA_VIEW 						= "/extensao/RelatorioPrograma/view.jsp";
	/** Cadastro de relat�rio de a��o de extens�o - exclus�o do relat�rio */
	public static final String RELATORIO_PROGRAMA_DELETE				= "/extensao/RelatorioPrograma/delete.jsp";
	/** RELATORIO PRODUTO */
	/** Cadastro de relat�rio de a��o de extens�o - relat�rio de produtos */
	public static final String RELATORIO_PRODUTO_FORM 						= "/extensao/RelatorioProduto/form.jsp";
	/** Cadastro de relat�rio de a��o de extens�o - visualiza��o de relat�rio de produtos */
	public static final String RELATORIO_PRODUTO_VIEW 						= "/extensao/RelatorioProduto/view.jsp";
	/** Cadastro de relat�rio de a��o de extens�o - exclus�o do relat�rio */
	public static final String RELATORIO_PRODUTO_DELETE 						= "/extensao/RelatorioProduto/delete.jsp";
	

	
	
	/** RELATORIOS PARA IMPRESS�O */
	/** Relat�rios de a��es de extens�o - por edital */
	public static final String RELATORIO_ACOES_POR_EDITAL_FORM					= "/extensao/Relatorios/relatorio_por_edital_form.jsp";
	/** Relat�rios de a��es de extens�o - busca de dados banc�rios dos discentes */
	public static final String RELATORIO_DADOS_BANCARIOS_DISCENTES_FORM			= "/extensao/Relatorios/dados_bancarios_discentes_form.jsp";
	/** Relat�rios de a��es de extens�o - visualiza��o dos dados banc�rios dos discentes */
	public static final String RELATORIO_DADOS_BANCARIOS_DISCENTES_REL			= "/extensao/Relatorios/dados_bancarios_discentes_rel.jsp";
	/** Relat�rios de a��es de extens�o - por publico estimado - busca */
	public static final String RELATORIO_PUBLICO_ESTIMADO_FORM					= "/extensao/Relatorios/publico_estimado_form.jsp";
	/** Relat�rios de a��es de extens�o - membros da equipe por modalidade */
	public static final String RELATORIO_EQUIPE_POR_MODALIDADE					= "/extensao/Relatorios/relatorio_equipe_por_modalidade.jsp";
	/** Relat�rios de a��es de extens�o - discentes por modalidade */
	public static final String RELATORIO_DISCENTE_POR_MODALIDADE				= "/extensao/Relatorios/relatorio_discente_por_modalidade.jsp";
	/** Relat�rios de a��es de extens�o - por tipos de financiamento */
	public static final String RELATORIO_FINANCIAMENTO_INTERNO_EXTERNO			= "/extensao/Relatorios/relatorio_financiamento_interno_externo.jsp";
	/** Relat�rios de a��es de extens�o - por local de realiza��o */
	public static final String RELATORIO_ACOES_LOCAL_REALIZACAO					= "/extensao/Relatorios/relatorio_acoes_local_realizacao.jsp";
	/** Relat�rios de a��es de extens�o - com nomes das a��es de extens�o */
	public static final String RELATORIO_NOMINAL_ACOES_LOCAL_REALIZACAO			= "/extensao/Relatorios/busca_relatorio_nominal_acoes_localidade.jsp";
	/** Relat�rios de a��es de extens�o - por p�blico estimado */
	public static final String RELATORIO_PUBLICO_ESTIMADO_REL					= "/extensao/Relatorios/publico_estimado_rel.jsp";
	/** Relat�rios de a��es de extens�o - por participantes - busca */
	public static final String RELATORIO_TOTAL_PARTICIPANTES_FORM				= "/extensao/Relatorios/relatorio_participantes_form.jsp";
	/** Relat�rios de a��es de extens�o - por participantes - visualiza��o */	
	public static final String RELATORIO_TOTAL_PARTICIPANTES_REL				= "/extensao/Relatorios/relatorio_participantes_rel.jsp";
	/** Relat�rios de a��es de extens�o - descentraliza��o de recursos - busca */
	public static final String RELATORIO_DESCENTRALIZACAO_RECURSOS_FAEX_FORM	= "/extensao/Relatorios/descentralizacao_recursos_faex_form.jsp";
	/** Relat�rios de a��es de extens�o - descentraliza��o de recursos - visualiza��o */
	public static final String RELATORIO_DESCENTRALIZACAO_RECURSOS_FAEX_REL		= "/extensao/Relatorios/descentralizacao_recursos_faex_rel.jsp";
	/** Relat�rios de a��es de extens�o - por localidade - busca */
	public static final String RELATORIO_ACOES_POR_LOCALIDADE					= "/extensao/Relatorios/relatorio_acoes_por_localidade.jsp";
	/** Relat�rios de a��es de extens�o - por localidade - visualiza��o */
	public static final String RELATORIO_NOMINAL_ACOES_POR_LOCALIDADE				= "/extensao/Relatorios/relatorio_nominal_acoes_por_localidade.jsp";
	/** Relat�rios de a��es de extens�o - exibe o total de participantes - busca */
	public static final String RELATORIO_TOTAL_PARTICIPANTES_ACAO_EXTENSAO_FORM		= "/extensao/Relatorios/relatorio_total_participantes_acao_extensao_form.jsp";
	/** Relat�rios de a��es de extens�o - exibe o total de participantes - visializa��o */
	public static final String RELATORIO_TOTAL_PARTICIPANTES_ACAO_EXTENSAO_REL		= "/extensao/Relatorios/relatorio_total_participantes_acao_extensao_rel.jsp";
	/** Relat�rios de a��es de extens�o - por n�vel de escolaridade - busca */
	public static final String RELATORIO_DOCENTES_POR_NIVEL					= "/extensao/Relatorios/relatorio_docentes_por_nivel.jsp";
	/** Relat�rios de a��es de extens�o - por n�vel de escolaridade - visualiza��o */
	public static final String BUSCA_RELATORIO_DOCENTES_POR_NIVEL			= "/extensao/Relatorios/busca_relatorio_docentes_por_nivel.jsp";
	

	
	/** AUTORIZA��O DOS DEPARTAMENTOS */
	/** Valida��o de a��es de extens�o pelo chefe do departamento. */
	public static final String AUTORIZACAOATIVIDADE_FORM 						= "/extensao/AutorizacaoDepartamento/form.jsp";
	/** Lista de a��es para valida��o de a��es de extens�o pelo chefe do departamento. */
	public static final String AUTORIZACAOATIVIDADE_LISTA						= "/extensao/AutorizacaoDepartamento/lista.jsf";
	/** Busca de autoriza��es de a��es de extens�o pelo chefe do departamento. */
	public static final String AUTORIZACAOATIVIDADE_BUSCA						= "/extensao/AutorizacaoDepartamento/form_busca_autorizar_acoes.jsf";
	/** Busca de autoriza��es de a��es de extens�o pelo chefe do departamento realizada pelo gestor de extens�o. */
	public static final String AUTORIZACAOATIVIDADE_MONITORIA_BUSCA				= "/monitoria/AutorizacaoDepartamento/form_busca_autorizacoes.jsf";
	/** Recibo da  autoriza��es de a��es de extens�o pelo chefe do departamento. */
	public static final String AUTORIZACAOATIVIDADE_RECIBO						= "/extensao/AutorizacaoDepartamento/recibo_autorizacao.jsp";

	/** Busca de autoriza��es de relat�rio de a��es de extens�o pelo chefe do departamento. */
	public static final String AUTORIZACAORELATORIO_BUSCA						= "/extensao/AutorizacaoDepartamento/form_busca_autorizar_relatorios.jsf";
	/** Formul�rio de autoriza��es de relat�rio de a��es de extens�o pelo chefe do departamento. */
	public static final String AUTORIZACAORELATORIO_FORM_PROJETO 				= "/extensao/AutorizacaoDepartamento/form_relatorio_projeto.jsp";
	/** Busca de autoriza��es de relat�rio de a��es de extens�o pelo chefe do departamento. */
	public static final String AUTORIZACAORELATORIO_FORM_CURSO_EVENTO			= "/extensao/AutorizacaoDepartamento/form_relatorio_curso_evento.jsp";
	/** Lista de autoriza��es de relat�rio de a��es de extens�o pelo chefe do departamento. */
	public static final String AUTORIZACAORELATORIO_LISTA						= "/extensao/AutorizacaoDepartamento/lista_relatorio.jsp";
	/** Formul�rio de autoriza��es de relat�rio de a��es de extens�o pelo chefe do departamento para produto. */
	public static final String AUTORIZACAO_RELATORIO_FORM_PRODUTO				= "/extensao/AutorizacaoDepartamento/form_relatorio_produto.jsp";
	/** Formul�rio de autoriza��es de relat�rio de a��es de extens�o pelo chefe do departamento para programa. */
	public static final String AUTORIZACAO_RELATORIO_FORM_PROGRAMA				= "/extensao/AutorizacaoDepartamento/form_relatorio_programa.jsp";
	
	/** VALIDA��O DOS RELAT�RIOS DE EXTENS�O PELA PROEX */
	public static final String VALIDACAORELATORIO_PROJETO_PROEX_FORM 			= "/extensao/ValidacaoRelatorioProex/form_projeto.jsp";
	public static final String VALIDACAORELATORIO_CURSO_EVENTO_PROEX_FORM 		= "/extensao/ValidacaoRelatorioProex/form_curso_evento.jsp";
	public static final String VALIDACAORELATORIO_PROEX_LISTA					= "/extensao/ValidacaoRelatorioProex/lista.jsp";
	public static final String VALIDACAO_RELATORIO_PRODUTO_PROEX_FORM 			= "/extensao/ValidacaoRelatorioProex/form_produto.jsp";	
	public static final String VALIDACAO_RELATORIO_PROGRAMA_PROEX_FORM 			= "/extensao/ValidacaoRelatorioProex/form_programa.jsp";


	
	/** CADASTRO DOS PARTICIPANTES DE A��ES DE EXTENS�O */ 
	public static final String PARTICIPANTESEXTENSAO_FORM 						= "/extensao/ParticipanteAcaoExtensao/form.jsp";
	public static final String PARTICIPANTESEXTENSAO_LISTA						= "/extensao/ParticipanteAcaoExtensao/lista.jsp";
	public static final String PARTICIPANTES_SUB_EXTENSAO_LISTA					= "/extensao/ParticipanteAcaoExtensao/lista_sub.jsp";
	public static final String PARTICIPANTESEXTENSAO_LISTA_PRINT				= "/extensao/ParticipanteAcaoExtensao/lista_participantes_extensao_print.jsp";
	public static final String PARTICIPANTES_EXTENSAO_LISTA_PRESENCA_PRINT		= "/extensao/ParticipanteAcaoExtensao/lista_presenca_print.jsp";
	public static final String PARTICIPANTESEXTENSAO_VIEW						= "/extensao/ParticipanteAcaoExtensao/view.jsp";
	public static final String PARTICIPANTESEXTENSAO_ACOES_LISTA				= "/extensao/ParticipanteAcaoExtensao/acoes_lista.jsp";
	public static final String PARTICIPANTESEXTENSAO_NOTIFICA					= "/extensao/ParticipanteAcaoExtensao/notificacao_participantes.jsp";
	public static final String PARTICIPANTEEXTENSAO_CONTATO_PRINT				= "/extensao/ParticipanteAcaoExtensao/lista_contatos_print.jsp";
	
	
	/** PLANO DE TRABALHO DO BOLSISTA */
	public static final String PLANOTRABALHOBOLSISTA_FORM 					= "/extensao/PlanoTrabalho/form.jsp";
	public static final String PLANOTRABALHOBOLSISTA_LISTA					= "/extensao/PlanoTrabalho/lista.jsp";
	public static final String PLANOTRABALHOBOLSISTA_ATIVIDADE_LISTA			= "/extensao/PlanoTrabalho/atividades_lista.jsp";	
	public static final String PLANOTRABALHOBOLSISTA_CRONOGRAMA				= "/extensao/PlanoTrabalho/cronograma.jsp";
	public static final String PLANOTRABALHOBOLSISTA_RESUMO					= "/extensao/PlanoTrabalho/resumo.jsp";	
	public static final String PLANOTRABALHOBOLSISTA_PLANOS_COORDENADOR			= "/extensao/PlanoTrabalho/planos_coordenador.jsp";
	public static final String PLANOTRABALHOBOLSISTA_VIEW					= "/extensao/PlanoTrabalho/view.jsp";
	public static final String PLANOTRABALHOBOLSISTA_ALTERAR_PLANO_LISTA			= "/extensao/PlanoTrabalho/alterar_plano_lista.jsp";	
	public static final String PLANOTRABALHOBOLSISTA_FINALIZAR_DISCENTE			= "/extensao/PlanoTrabalho/finalizar_discente.jsp";
	public static final String PLANOTRABALHOBOLSISTA_INDICAR_DISCENTE			= "/extensao/PlanoTrabalho/indicar_discente.jsp";
	public static final String PLANOTRABALHOBOLSISTA_DISCENTE				= "/extensao/PlanoTrabalho/planos_discente.jsp";
	public static final String PLANOTRABALHOBOLSISTA_CONTATO				= "/extensao/PlanoTrabalho/form_contato.jsp";
	
	
	public static final String AVALIACAO_PROPOSTA_FORM 					= "/extensao/AvaliacaoAtividade/form.jsp";
	public static final String AVALIACAO_PROPOSTA_LISTA					= "/extensao/AvaliacaoAtividade/lista.jsp";	
	public static final String AVALIACAO_PROPOSTA_VIEW 					= "/extensao/AvaliacaoAtividade/view.jsp";	
	public static final String AVALIACAO_PROPOSTA_LISTA_PARECER 				= "/extensao/AvaliacaoAtividade/lista_parecerista.jsp";	
	public static final String AVALIACAO_PROPOSTA_FORM_PARECER				= "/extensao/AvaliacaoAtividade/form_parecerista.jsp";
	public static final String AVALIACAO_PROPOSTA_CONFIRMAR_AVALIACAO	   		= "/extensao/AvaliacaoAtividade/confirmar_avaliacao.jsp";
	public static final String AVALIACAO_PROPOSTA_FORM_PRESIDENTE				= "/extensao/AvaliacaoAtividade/form_presidente.jsp";
	public static final String AVALIACAO_PROPOSTA_LISTA_PRESIDENTE				= "/extensao/AvaliacaoAtividade/lista_presidente.jsp";	
	public static final String AVALIACAO_CONSULTAR_AVALIADORES				= "/extensao/AvaliacaoAtividade/consultar_avaliadores.jsp";
	public static final String AVALIACAO_CONSULTAR_AVALIACOES				= "/extensao/AvaliacaoAtividade/consultar_avaliacoes.jsp";
	public static final String AVALIACAO_LISTA_COORDENADOR					= "/extensao/AvaliacaoAtividade/lista_coordenador.jsp";
	public static final String AVALIACAO_LISTA_AD_HOC					= "/extensao/AvaliacaoAtividade/lista_avaliacoes_ad_hoc.jsp";
	public static final String INICIAR_HOMOLOGACAO_BOLSAS					= "/extensao/DiscenteExtensao/homologacao_bolsa_discente.jsp";
	
	public static final String EXPORTAR_LISTA_FAEX_AVALIACAO_FINAL				= "/arquivoFaex";
	
	
	public static final String CLASSIFICAR_ACOES_CLASSIFICAR				= "/extensao/ClassificarAcoes/classificar.jsp";
	public static final String CLASSIFICAR_ACOES_LISTA					= "/extensao/ClassificarAcoes/lista.jsp";
	public static final String CLASSIFICAR_ACOES_VIEW					= "/extensao/ClassificarAcoes/view.jsp";

	/** DISCENTE EXTENS�O */
	public static final String DISCENTEEXTENSAO_FORM 							= "/extensao/DiscenteExtensao/form.jsp";
	public static final String DISCENTEEXTENSAO_LISTA 							= "/extensao/DiscenteExtensao/lista.jsp";
	public static final String DISCENTEEXTENSAO_INSCRICAO_DISCENTE 				= "/extensao/DiscenteExtensao/inscricao_discente.jsp";
	public static final String DISCENTEEXTENSAO_VIEW			 				= "/extensao/DiscenteExtensao/view.jsp";
	
	/** VER DETALHES DA SELE��O */
	public static final String DETALHESSELECAOEXTENSAO_LISTA_PROJETOS 							= "/extensao/DetalhesSelecaoExtensao/lista_projetos.jsp";
	public static final String DETALHESSELECAOEXTENSAO_VISUALIZAR_CANDIDATOS 					= "/extensao/DetalhesSelecaoExtensao/visualizar_candidatos.jsp";	

	/** VISUALIZAR RESULTADO SELE��O */
	public static final String DISCENTEEXTENSAO_VISUALIZAR_RESULTADO_SELECAO 					= "/extensao/DiscenteExtensao/visualizar_resultado.jsp";
	public static final String DISCENTEEXTENSAO_VISUALIZAR_RESULTADO_SELECAO_LISTA_PROJETOS 	= "/extensao/DiscenteExtensao/atividades_visualizar_resultado.jsp";
	
	/** DISTRIBUIR PARECERISTA ATIVIDADE EXTENS�O */
	public static final String DISTRIBUICAOPARECERISTA_FORM 									= "/extensao/DistribuicaoPareceristaExtensao/form.jsp";
	public static final String DISTRIBUICAOPARECERISTA_LISTA 									= "/extensao/DistribuicaoPareceristaExtensao/lista.jsp";
	public static final String DISTRIBUICAOPARECERISTA_AUTO_FORM								= "/extensao/DistribuicaoPareceristaExtensao/form_auto.jsp";
	public static final String DISTRIBUICAOPARECERISTA_AUTO_LISTA								= "/extensao/DistribuicaoPareceristaExtensao/lista_auto.jsp";
	public static final String DISTRIBUICAOPARECERISTA_LISTA_ATIVIDADES_VINCULADAS				= "/extensao/DistribuicaoPareceristaExtensao/lista_atividades_vinculadas.jsp";	

	/** DISTRIBUIR COMITE ATIVIDADE EXTENS�O */
	public static final String DISTRIBUICAOCOMITE_FORM 											= "/extensao/DistribuicaoComiteExtensao/form.jsp";
	public static final String DISTRIBUICAOCOMITE_LISTA 										= "/extensao/DistribuicaoComiteExtensao/lista.jsp";
	public static final String DISTRIBUICAOCOMITE_LISTA_ATIVIDADES_VINCULADAS					= "/extensao/DistribuicaoComiteExtensao/lista_atividades_vinculadas.jsp";

	/** SOLICITAR RECONSIDERACAO DE AVALIA��O */
	public static final String SOLICITACAORECONSIDERACAO_FORM 								= "/extensao/SolicitacaoReconsideracao/form.jsp";
	public static final String SOLICITACAORECONSIDERACAO_LISTA 								= "/extensao/SolicitacaoReconsideracao/lista.jsp";
	public static final String SOLICITACAORECONSIDERACAO_MINHAS_SOLICITACOES						= "/extensao/SolicitacaoReconsideracao/minhas_solicitacoes.jsp";
	public static final String SOLICITACAORECONSIDERACAO_VIEW 								= "/extensao/SolicitacaoReconsideracao/view.jsp";
	
	public static final String ANALISARSOLICITACAORECONSIDERACAO_LISTA 							= "/extensao/AnalisarSolicitacaoReconsideracao/lista.jsp";
	public static final String ANALISARSOLICITACAORECONSIDERACAO_FORM 							= "/extensao/AnalisarSolicitacaoReconsideracao/form.jsp";
	

	public static final String FOTOS_FORM 														= "/extensao/Fotos/form.jsp";
	
	
	public static final String CONTATO_COORDENADOR_FORM											= "/extensao/Atividade/form_contato.jsp";
	
	/** CADASTRO GRUPO DE ITENS DE AVALIACAO */
	public static final String CADASTRARGRUPOITEMAVALIACAO_LISTA		                        = "/extensao/GrupoItemAvaliacao/lista.jsp";
	public static final String CADASTRARGRUPOITEMAVALIACAO_FORM		                        = "/extensao/GrupoItemAvaliacao/form.jsp";
	
	/** CADASTRAR ITEM AVALIA�AO */
	public static final String CADASTRARITEMAVALIACAO_FORM				                = "/extensao/ItemAvaliacaoExtensao/form.jsp";
	public static final String CADASTRARITEMAVALIACAO_LISTA						= "/extensao/ItemAvaliacaoExtensao/lista.jsp";
	
	public static final String FINALIZACAO_BOLSISTA							= "/extensao/DiscenteExtensao/finalizacao_bolsa_discente.jsp";
	
	/** MOVIMENTA��O DE COTAS DE BOLSA */
	public static final String MOVIMENTAR_COTA_FORM							= "/extensao/MovimentacaoCota/form.jsp";
	public static final String MOVIMENTAR_COTA_LISTA						= "/extensao/MovimentacaoCota/lista.jsp";
	
	/** CONSTANTES RELACIONADAS A V�NCULO DE UMA A��O A UNIDADE OR�AMENT�RIA */
	public static final String VINCULAR_UNIDADE_ORCAMENTARIA_FORM	= "/extensao/VincularUnidadeOrcamentaria/form.jsp";
	public static final String VINCULAR_UNIDADE_ORCAMENTARIA_LISTA	= "/extensao/VincularUnidadeOrcamentaria/lista.jsp";
	
	/** CONSTANTES RELACIONADAS AO PROCEDIMENTO DE ALTERA��O DE VAGAS DO CURSO PELO GESTOR DE EXTENS�O*/
	public static final String ALTERAR_VAGAS_CURSO_EVENTO_FORM	= "/extensao/AlterarVagas/form.jsp";
	public static final String ALTERAR_VAGAS_CURSO_EVENTO_LISTA	= "/extensao/AlterarVagas/lista.jsp";

}
