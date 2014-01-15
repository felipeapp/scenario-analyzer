/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/08/2009
 *
 */
package br.ufrn.sigaa.projetos.jsf;

import java.util.HashMap;
import java.util.Map;

/*******************************************************************************
 * Constantes de navegação do sistema projetos de ação acadêmica integrada
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
public class ConstantesNavegacaoProjetos {

	/** Constantes de navegação relacionadas ao cadastro de ações acadêmicas associadas.*/
	/** Tela que permite a seleção das dimensões acadêmicas da proposta. */
    public static final String DIMENSAO_ACADEMICA	= "/projetos/ProjetoBase/dimensao_academica.jsp";
    /** Tela para informação dos dados gerais do projeto. */
	public static final String DADOS_GERAIS			= "/projetos/ProjetoBase/dados_gerais.jsp";
	/** Tela para informação do cronograma do projeto. */
	public static final String CRONOGRAMA			= "/projetos/ProjetoBase/cronograma.jsp";
	/** Tela para informação da equipe do projeto. */
	public static final String EQUIPE				= "/projetos/ProjetoBase/equipe.jsp";
	/** Tela para informação do orçamento do projeto. */
	public static final String ORCAMENTO			= "/projetos/ProjetoBase/orcamento.jsp";
	/** Tela para informação dos arquivos do projeto. */
	public static final String ARQUIVOS 			= "/projetos/ProjetoBase/arquivos.jsp";
	/** Tela para informação das fotos do projeto. */
	public static final String FOTOS	 			= "/projetos/ProjetoBase/fotos.jsp";
	/** Tela para informação do resumo final do projeto. */
	public static final String RESUMO				= "/projetos/ProjetoBase/resumo.jsp";
	/** Tela para informação do resumo com dados das ações integradas do projeto. */
	public static final String RESUMO_INTEGRADO		= "/projetos/ProjetoBase/resumo_integrado.jsp";
	/** Tela com lista de ações associadas pendentes de conclusão do cadastro do projeto. */
	public static final String PROJETOS_PENDENTES	= "/projetos/ProjetoBase/projetos_pendentes.jsp";
	/** Tela com lista dos projetos do usuário logado. */
	public static final String MEUS_PROJETOS		= "/projetos/ProjetoBase/meus_projetos.jsp";
	/** Tela com lista de tipos de dimensões acadêmicas possíveis para o do projeto. */
	public static final String SELECIONAR_PROJETO	= "/projetos/ProjetoBase/selecionar_projeto.jsp";
	/** Tela com lista de tipos projetos externos disponíveis para cadastro. */
	public static final String SELECIONAR_TIPO_PROJETO_INTEGRADO	= "/projetos/ProjetoBase/selecionar_projeto_integrado.jsp";

	
	
	/** Tela para visualização de dados completos do projeto. */
	public static final String VIEW					   = "/projetos/ProjetoBase/view.jsp";
	/** Tela para visualização de dados completos do projeto para confirmar remoção. */
	public static final String REMOVER				   = "/projetos/ProjetoBase/remover.jsp";
	/** Tela para visualização de dados completos do projeto para confirmação da execução. */
	public static final String EXECUTAR_PROJETO        = "/projetos/ProjetoBase/executarProjeto.jsp";
	/** Tela que permite ao usuário escolher se quer realizar o cadastro do projeto também no sistema administrativo. */
	public static final String CONTINUAR_PROPLAM	   = "/projetos/ProjetoBase/continuar_proplan.jsp";
	/** Tela para visualização das avaliações do projeto */
	public static final String AVALIACOES_PROJETO	   = "/projetos/ProjetoBase/avaliacoes.jsp";
	/** Tela para visualização das avaliações do projeto */
	public static final String VIEW_IMPRESSAO	       = "/projetos/ProjetoBase/view_impressao.jsp";
	
	/** Armazena o fluxo do cadastro de ações acadêmicas associadas. */
	public static final Map<String, String> PASSOS_CADASTRO_PROJETO_BASE = new HashMap<String, String>() ;
	static {
	    PASSOS_CADASTRO_PROJETO_BASE.put(DIMENSAO_ACADEMICA, "Dimensão acadêmica da proposta");
		PASSOS_CADASTRO_PROJETO_BASE.put(DADOS_GERAIS, "Dados gerais do projeto");
		PASSOS_CADASTRO_PROJETO_BASE.put(CRONOGRAMA, "Cronograma de execução do projeto");
		PASSOS_CADASTRO_PROJETO_BASE.put(EQUIPE, "Membros da equipe do projeto");
		PASSOS_CADASTRO_PROJETO_BASE.put(ORCAMENTO, "Orçamento");
		PASSOS_CADASTRO_PROJETO_BASE.put(ARQUIVOS, "Anexar arquivos");
		PASSOS_CADASTRO_PROJETO_BASE.put(FOTOS, "Anexar fotos");
		PASSOS_CADASTRO_PROJETO_BASE.put(RESUMO, "Resumo do projeto base");
		PASSOS_CADASTRO_PROJETO_BASE.put(RESUMO_INTEGRADO, "Resumo geral do projeto integrado");
	}
	
	//CONSTANTES RELACIONADAS A ALTERAÇÃO DE PROJETOS
	/** Tela para alteração da situação projeto. */
	public static final String ALTERAR_SITUACAO_FORM		= "/projetos/AlteracaoProjeto/form_situacao.jsp";
	/** Tela para alteração do orçamento do projeto. */
	public static final String ALTERAR_ORCAMENTO_FORM		= "/projetos/AlteracaoProjeto/form_orcamento.jsp";
	/** Tela para alteração das ações vinculadas ao projeto. */
	public static final String ALTERAR_PROJETOS_VINCULADOS_LISTA	= "/projetos/AlteracaoProjeto/lista_vinculados.jsp";
	/** Tela que permite a busca de projetos para alteração dos dados. */
	public static final String ALTERAR_PROJETO_LISTA		= "/projetos/AlteracaoProjeto/lista.jsp";
	/** Tela para alteração dos membros da equipe projeto. */
	public static final String GERENCIAR_MEMBROS			= "/projetos/AlteracaoProjeto/gerenciar_membros.jsp";
	/** Tela para alteração dos recursos concedidos ao projeto. */
	public static final String CONCEDER_RECURSOS_FORM		= "/projetos/Avaliacoes/ConcederRecursos/form.jsp";
	/** Tela que permite a busca para alteração dos recursos do projeto. */
	public static final String CONCEDER_RECURSOS_LISTA		= "/projetos/Avaliacoes/ConcederRecursos/lista.jsp";
	
	//CONSTANTES RELACIONADAS A VÍNCULO DE PROJETO A UNIDADE ORÇAMENTÁRIA
	/** Tela para alteração da unidade orçamentária do projeto. */
	public static final String VINCULAR_UNIDADE_ORCAMENTARIA_FORM	= "/projetos/VincularUnidadeOrcamentaria/form.jsp";
	/** Tela para busca de projetos para alteração da unidade orçamentária. */
	public static final String VINCULAR_UNIDADE_ORCAMENTARIA_LISTA	= "/projetos/VincularUnidadeOrcamentaria/lista.jsp";
	
	//SOLICITAR RECONSIDERACAO DE AVALIAÇÃO
	/** Tela com formulário de submissão de reconsideração do projeto. */
	public static final String SOLICITAR_RECONSIDERACAO_FORM 					= "/projetos/SolicitarReconsideracao/form.jsp";
	/** Tela com busca para seleção de projetos para reconsideração. */
	public static final String SOLICITAR_RECONSIDERACAO_LISTA 					= "/projetos/SolicitarReconsideracao/lista.jsp";
	/** Tela para visualização da solicitação de reconsideração de projeto. */
	public static final String SOLICITAR_RECONSIDERACAO_VIEW 					= "/projetos/SolicitarReconsideracao/view.jsp";
	/** Tela com lista das solicitações de reconsideração dos projetos do usuário. */
	public static final String SOLICITAR_RECONSIDERACAO_MINHAS_SOLICITACOES		= "/projetos/SolicitarReconsideracao/minhas_solicitacoes.jsp";
	
	/** Tela com lista de solicitações de reconsideração de projetos para análise por membros do comitê. */
	public static final String ANALISAR_SOLICITACAO_RECONSIDERACAO_LISTA 		= "/projetos/AnalisarSolicitacaoReconsideracao/lista.jsp";
	/** Tela para aprovação da solicitação de reconsideração do projeto. */
	public static final String ANALISAR_SOLICITACAO_RECONSIDERACAO_FORM 		= "/projetos/AnalisarSolicitacaoReconsideracao/form.jsp";
	
	//AVALIAÇÃO DE PROJETOS
	/** Tela para visualização de detalhes do orçamento do projeto durante sua avaliação. */
	public static final String VIEW_ORCAMENTO     					= "/projetos/AvaliacaoProjeto/view_orcamento.jsp";
	
		
	//AVALIACAO DE PROJETOS
	/** Tela para distribuição de projetos para avaliação. */
	public static final String DISTRIBUIR_PROJETOS_FORM 				= "/projetos/Avaliacoes/Distribuicao/form.jsp";
	/** Tela de busca com lista de projetos para distribuição para avaliação. */
	public static final String DISTRIBUIR_PROJETOS_LISTA 				= "/projetos/Avaliacoes/Distribuicao/lista.jsp";
	/** Tela com lista de avaliadores de projetos para distribuição automática para avaliação. */
	public static final String DISTRIBUIR_PROJETOS_AVALIADORES_AUTO	= "/projetos/Avaliacoes/Distribuicao/avaliadores_automatica.jsp";
	/** Tela com lista de projetos para distribuição automática para avaliação. */
	public static final String DISTRIBUIR_PROJETOS_PROJETOS_AUTO		= "/projetos/Avaliacoes/Distribuicao/projetos_automatica.jsp";
	/** Tela com lista de avaliadores de projetos para distribuição manual para avaliação. */
	public static final String DISTRIBUIR_PROJETOS_AVALIADORES_MANUAL	= "/projetos/Avaliacoes/Distribuicao/avaliadores_manual.jsp";
	/** Tela com lista de projetos para distribuição manual para avaliação. */
	public static final String DISTRIBUIR_PROJETOS_PROJETOS_MANUAL		= "/projetos/Avaliacoes/Distribuicao/projetos_manual.jsp";
	/** Tela com lista de avaliadores ad hoc cadastrados. */
	public static final String CADASTRO_AVALIADOR_PROJETOS_LISTA		= "/projetos/Avaliacoes/Avaliadores/lista.jsp";
	/** Tela para cadastro de avaliadores ad hoc. */
	public static final String CADASTRO_AVALIADOR_PROJETOS_FORM			= "/projetos/Avaliacoes/Avaliadores/form.jsp";
	
	/** Tela para avaliação de projetos por membro do comitê ou avaliador ad hoc. */
	public static final String AVALIACAO_PROJETOS_FORM 					= "/projetos/Avaliacoes/form.jsp";
	/** Tela com lista de projetos para avaliação do usuário logado. */
	public static final String AVALIACAO_PROJETOS_LISTA					= "/projetos/Avaliacoes/lista.jsp";
	/** Tela para confirmação da avaliação. */
	public static final String AVALIACAO_PROJETOS_CONFIRM				= "/projetos/Avaliacoes/confirm.jsp";
	/** Tela de busca com lista de projetos para avaliação. */
	public static final String AVALIACAO_PROJETOS_BUSCA					= "/projetos/Avaliacoes/buscar.jsp";
	/** Tela com detalhes da avaliação realizada. */
	public static final String AVALIACAO_PROJETOS_VIEW					= "/projetos/Avaliacoes/view.jsp";
	
	/** Tela para emissão do certificado ou Declaração dos avaliadores projetos por membro do comitê ou avaliador ad hoc. */
	/** Tela de busca com lista de projetos para avaliação. */
	public static final String PROJETOS_AVALIADOS_AVALIADORES			= "/projetos/Avaliacoes/buscar.jsp";
	
	/** Tela com lista formulários de avaliações cadastrados. */
	public static final String CONSOLIDACAO_AVALIACAO_PROJETOS_LISTA	= "/projetos/Avaliacoes/Consolidacao/lista.jsp";
	/** Tela para cadastro do formulário de avaliação de projetos. */
	public static final String CONSOLIDACAO_AVALIACAO_PROJETOS_FORM		= "/projetos/Avaliacoes/Consolidacao/form.jsp";

	/** Tela com lista de projetos com avaliação finalizada prontos para serem classificados. */
	public static final String CLASSIFICAR_PROJETOS_LISTA				= "/projetos/Avaliacoes/Classificacao/lista.jsp";
	/** Tela para confirmar a classificação dos projetos. */
	public static final String CLASSIFICAR_PROJETOS_FORM				= "/projetos/Avaliacoes/Classificacao/form.jsp";

	
	/** CONSTANTES RELACIONADAS À MEMBROS DE PROJETOS */
	/** Tela para cadastro do membro da equipe do projeto. */
	public static final String MEMBROPROJETO_CADASTRO_FORM	 	= "/projetos/MembroProjeto/cadastro_form.jsp";
	/** Tela para alteração do membro da equipe do projeto. */
	public static final String MEMBROPROJETO_ALTERA_FORM	 	= "/projetos/MembroProjeto/altera_form.jsp";
	/** Tela com lista de membros da equipe do projeto. */
	public static final String MEMBROPROJETO_LISTA 				= "/projetos/MembroProjeto/lista.jsp";
	/** Tela com lista de membros da equipe de projetos de pesquisa. */
	public static final String MEMBROPROJETO_LISTA_PESQUISA		= "/projetos/MembroProjeto/lista_pesquisa.jsp";
	/** Tela com lista de membros da equipe de um projeto selecionado. */
	public static final String MEMBROPROJETO_LISTA_UNICA_ACAO	= "/projetos/MembroProjeto/lista_unica_acao.jsp";
	/** Tela para visualização dos dados do membro da equipe do projeto. */
	public static final String MEMBROPROJETO_VIEW 				= "/projetos/MembroProjeto/view.jsp";
	/** Tela para alteração do coordenador da equipe do projeto. */
	public static final String MEMBROPROJETO_ALTERA_COORD 		= "/projetos/MembroProjeto/altera_coordenador.jsp";
	
	/** PLANO DE TRABALHO PROJETO DO BOLSISTA */
	/** Tela para cadastro de planos de trabalho do projeto. */
	public static final String PLANO_TRABALHO_PROJETO_BOLSISTA_FORM 				= "/projetos/PlanoTrabalho/form.jsp";
	/** Tela lista de planos de trabalho do projeto. */
	public static final String PLANO_TRABALHO_PROJETO_LISTA							= "/projetos/PlanoTrabalho/projetos_lista.jsp";
	/** Tela para cadastro do cronograma do planos de trabalho do projeto. */
	public static final String PLANO_TRABALHO_PROJETO_CRONOGRAMA					= "/projetos/PlanoTrabalho/cronograma.jsp";
	/** Tela de resumo do planos de trabalho do projeto. */
	public static final String PLANO_TRABALHO_PROJETO_RESUMO						= "/projetos/PlanoTrabalho/resumo.jsp";
	/** Tela com lista de planos de trabalho de projetos coordenados pelo usuário logado. */
	public static final String PLANO_TRABALHO_PROJETO_PLANOS_COORDENADOR			= "/projetos/PlanoTrabalho/planos_coordenador.jsp";
	/** Tela para visualização do plano de trabalho do projeto selecionado. */
	public static final String PLANO_TRABALHO_PROJETO_VIEW							= "/projetos/PlanoTrabalho/view.jsp";
	/** Tela para finalização de planos de trabalho do projeto. */
	public static final String PLANO_TRABALHO_PROJETO_FINALIZAR_DISCENTE			= "/projetos/PlanoTrabalho/finalizar_discente.jsp";
	
	/** Tela para alterar o discente do planos de trabalho do projeto. */
	public static final String PLANO_TRABALHO_PROJETO_ALTERAR_DISCENTE_PLANO_LISTA	= "/projetos/PlanoTrabalho/alterar_discente_plano_lista.jsp";
	/** Tela para indicar discente no planos de trabalho do projeto. */
	public static final String PLANO_TRABALHO_PROJETO_INDICAR_DISCENTE				= "/projetos/PlanoTrabalho/indicar_discente.jsp";
	/** Tela com lista de planos de trabalho do discente. */
	public static final String PLANO_TRABALHO_PROJETO_PLANOS_DISCENTE				= "/projetos/PlanoTrabalho/planos_discente.jsp";

	
	/** GERENCIAR DISCENTES DO PROJETO */
	/** Tela para busca de discente com planos de trabalho em projetos. */
	public static final String DISCENTES_BUSCA			= "/projetos/DiscenteProjeto/busca.jsp";
	/** Tela para visualizar dados de planos de trabalho do projeto. */
	public static final String DISCENTES_VIEW			= "/projetos/DiscenteProjeto/view.jsp";
	/** INSCRIÇÃO DE DISCENTES EM AÇÕES ASSOCIADAS */
	public static final String DISCENTE_PROJETO_INSCRICAO 				= "/projetos/DiscenteProjeto/inscricao_discente.jsp";
	
	/** Tela para visualização de dados bancários do discente no projeto. */
	public static final String RELATORIOS_DADOS_BANCARIOS_DISCENTES_VIEW		= "/projetos/Relatorios/dados_bancarios_discentes_view.jsp";
	/** Tela para cadastro de dados bancários do discente no projeto. */
	public static final String RELATORIOS_DADOS_BANCARIOS_DISCENTES_FORM		= "/projetos/Relatorios/dados_bancarios_discentes_form.jsp";
	
	/** RELATORIO DE PROJETOS */
	/** Tela com lista de relatórios parciais e finais dos projetos onde o usuário logado é coordenador. */
	public static final String RELATORIO_PROJETO_LISTA						= "/projetos/RelatorioAcaoAssociada/lista.jsp";
	/** Tela para cadastro de relatórios de projeto. */
	public static final String RELATORIO_PROJETO_FORM 						= "/projetos/RelatorioAcaoAssociada/form.jsp";
	/** Tela de visualização de relatório de projeto. */
	public static final String RELATORIO_PROJETO_VIEW 						= "/projetos/RelatorioAcaoAssociada/view.jsp";
	/** Tela de busca de relatório de projeto. */
	public static final String RELATORIO_PROJETO_BUSCA 						= "/projetos/RelatorioAcaoAssociada/busca.jsp";

	
	/** Lista de relatórios para aprovação do departamento. */
	public static final String VALIDAR_RELATORIO_DEPARTAMENTO_LISTA 		= "/projetos/ValidarRelatorio/Departamento/lista.jsp";
	/** Formulário para aprovação de relatórios pelo departamento. */
	public static final String VALIDAR_RELATORIO_DEPARTAMENTO_FORM	 		= "/projetos/ValidarRelatorio/Departamento/form.jsp";

	/** Lista de relatórios para aprovação do comitê integrado. */
	public static final String VALIDAR_RELATORIO_COMITE_LISTA 				= "/projetos/ValidarRelatorio/Comite/lista.jsp";
	/** Formulário para aprovação de relatórios pelo comitê. */
	public static final String VALIDAR_RELATORIO_COMITE_FORM 				= "/projetos/ValidarRelatorio/Comite/form.jsp";

}

