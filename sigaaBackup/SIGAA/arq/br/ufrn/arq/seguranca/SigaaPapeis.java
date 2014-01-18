/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '05/03/2007'
 *
 */
package br.ufrn.arq.seguranca;

/**
 * Constante com todos os papéis do sistema acadêmico.
 *
 * @author Gleydson Lima
 *
 */
public class SigaaPapeis {

	/**
	 * -------------------  PAPÉIS GERAIS ----------------------
	 * ---------------------------------------------------------
	*/
	
	/** Papel representativo do Administrador do Sistema Acadêmico. */
	public static final int ADMINISTRADOR_SIGAA = SigaaSubsistemas.ADMINISTRACAO.getId() + 1;
	
	
	/***************************************************************************
	 * ENSINO:
	 **************************************************************************/

	/** Papel representativo do Gestor do Ensino Técnico. */
	public static final int GESTOR_TECNICO = SigaaSubsistemas.TECNICO.getId() + 1;

	/** Papel representativo do Coordenador de um curso do Ensino Técnico. */	
	public static final int COORDENADOR_TECNICO = SigaaSubsistemas.TECNICO.getId() + 2;
	
	/** Papel representativo da Secretaria de um curso do Ensino Técnico. */
	public static final int SECRETARIA_TECNICO = SigaaSubsistemas.TECNICO.getId() + 3;
	
	/** Papel representativo para permitir consultas gerais no Ensino técnico. */
	public static final int PEDAGOGICO_TECNICO = SigaaSubsistemas.TECNICO.getId() + 4;
	
	/** Papel representativo para permitir que faça importação do processo seletivo. */
	public static final int GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO = SigaaSubsistemas.TECNICO.getId() + 5;
	
	/** Papel representativo para permitir que faça convocação do processo seletivo. */
	public static final int GESTOR_CONVOCACAO_PROCESSO_SELETIVO_TECNICO = SigaaSubsistemas.TECNICO.getId() + 6;
	
	/** Papel representativo para permitir que faça cadastramento do processo seletivo. */
	public static final int GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_TECNICO = SigaaSubsistemas.TECNICO.getId() + 7;
	
	/** Papel representativo do Gestor de uma Escola Acadêmica Especializada. */
	public static final int GESTOR_FORMACAO_COMPLEMENTAR = SigaaSubsistemas.FORMACAO_COMPLEMENTAR.getId() + 1;
	
	/** Papel representativo do Gestor do Ensino Infantil. */
	public static final int GESTOR_INFANTIL = SigaaSubsistemas.INFANTIL.getId() + 1;

	/** Papel representativo do Gestor da Pós-Graduação em Lato Sensu (Especialização). */
	public static final int GESTOR_LATO = SigaaSubsistemas.LATO_SENSU.getId() + 1;

	/** Papel representativo do Coordenador de um curso de Pós-Graduação em Lato Sensu (Especialização). */
	public static final int COORDENADOR_LATO = SigaaSubsistemas.LATO_SENSU.getId() + 2;

	/** Papel representativo da Secretaria de um curso de Pós-Graduação em Lato Sensu (Especialização). */
	public static final int SECRETARIA_LATO = SigaaSubsistemas.LATO_SENSU.getId() + 3;

	

	/***************************************************************************
	 * MÓDULO PESQUISA:
	 **************************************************************************/

	/** Papel representativo do Gestor do Módulo Pesquisa. */
	public static final int GESTOR_PESQUISA = SigaaSubsistemas.PESQUISA.getId() + 1;
	
	/** Papel representativo de Consultor dentro do Módulo Pesquisa. */
	public static final int CONSULTOR = SigaaSubsistemas.PESQUISA.getId() + 2;
	
	/** Papel representativo de Membro de Comitê de Pesquisa. */
	public static final int MEMBRO_COMITE_PESQUISA = SigaaSubsistemas.PESQUISA.getId() + 3;
	
	/** Papel representativo do Núcleo de Inovação Tecnológica. */
	public static final int NIT = SigaaSubsistemas.PESQUISA.getId() + 4;
	
	/** Papel representativo do Administrador do Módulo Pesquisa. */
	public static final int ADMINISTRADOR_PESQUISA = SigaaSubsistemas.PESQUISA.getId() + 5;
	
	/** Papel representativo de Técnicos Administrativos com participação em Projetos de Pesquisa e Notificações de Invenção. */
	public static final int PESQUISA_TECNICO_ADMINISTRATIVO = SigaaSubsistemas.PESQUISA.getId() + 6;

	
	/***************************************************************************
	 * MÓDULO MONITORIA:
	 **************************************************************************/
	
	/** Gestor do módulo de monitoria no SIGAA. Libera acesso ao módulo Monitoria no menu de módulos do sistema. menu de acesso às operações: SIGAA > Monitoria */
	public static final int GESTOR_MONITORIA 					= SigaaSubsistemas.MONITORIA.getId() + 1;
	
	/** Papel representativo de Membro de Comitê de Monitoria. É a comissão constituída por um docente de cada CA (Centro Acadêmico) 
	 * e um docente por Unidade Acadêmica Especializada, cujo mandato dos membros é de dois anos, sendo permitida uma prorrogação. 
	 * Membros desta comissão são responsáveis por avaliarem os projetos e os relatórios SID. 
	 * Menu de acesso às operações: SIGAA > Menu Docente > Ensino >Projetos > Projeto de Monitoria > Comissão Monitoria > */
	public static final int MEMBRO_COMITE_MONITORIA 			= SigaaSubsistemas.MONITORIA.getId() + 2;
	
	/** Papel representativo de Membro de Comitê Científico de Monitoria. 
	 * Comissão que é composta por docentes do quadro permanente da UFRN, com o objetivo de auxiliar a 
	 * comissão de monitoria no tangente avaliação dos resumos submetidos ao Seminário de Iniciação à Docência (SID). 
	 * Menu de acesso às operações: SIGAA > Menu Docente > Ensino >Projetos > Projeto de Monitoria > Comissão Monitoria >  */
	public static final int MEMBRO_COMITE_CIENTIFICO_MONITORIA 	= SigaaSubsistemas.MONITORIA.getId() + 3;

	
	/****************************************************************************
	 * MÓDULO EXTENSÃO:
	 ****************************************************************************/

	/** Papel representativo do Gestor do Módulo Extensão. */
	public static final int GESTOR_EXTENSAO 								= SigaaSubsistemas.EXTENSAO.getId() + 1;
	
	/** Papel representativo de Membro de Comitê de Extensão. */
	public static final int MEMBRO_COMITE_EXTENSAO 							= SigaaSubsistemas.EXTENSAO.getId() + 2;
	
	/** Papel representativo de parceiros da Extensão. */
	public static final int PARECERISTA_EXTENSAO 							= SigaaSubsistemas.EXTENSAO.getId() + 3;
	
	/** Papel representativo de Presidente de Comitê de Extensão. */
	public static final int PRESIDENTE_COMITE_EXTENSAO 						= SigaaSubsistemas.EXTENSAO.getId() + 4;
	
	/** Papel representativo de Técnicos Administrativos em projetos de Extensão. */
	public static final int EXTENSAO_TECNICO_ADMINISTRATIVO 				= SigaaSubsistemas.EXTENSAO.getId() + 5;
	
	/** Papel representativo de Coordenador de Programas e/ou Projetos de Extensão. */
	public static final int COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO 		= SigaaSubsistemas.EXTENSAO.getId() + 6;
	
	/** Papel representativo de Coordenador de Cursos,Eventos e Produtos em projetos de Extensão. */
	public static final int COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO 	= SigaaSubsistemas.EXTENSAO.getId() + 7;
	
	/** Papel representativo de Apoio Técnico da Coordenação em projetos de Extensão. */
	public static final int APOIO_TECNICO_COORDENACAO_EXTENSAO				= SigaaSubsistemas.EXTENSAO.getId() + 8;
	
	
	/****************************************************************************
	 * MÓDULO AÇÕES ACADÊMICAS ASSOCIADAS:
	 ****************************************************************************/
	
	/** Papel representativo de Avalidor de Ações Associadas. */	
	public static final int AVALIADOR_ACOES_ASSOCIADAS			= SigaaSubsistemas.ACOES_ASSOCIADAS.getId() + 1;

	/** Papel representativo do Gestor de bolsas de Ações Associadas. */	
	public static final int GESTOR_BOLSAS_ACOES_ASSOCIADAS		= SigaaSubsistemas.ACOES_ASSOCIADAS.getId() + 2;

	
	/** Membro da comissão integrada de ensino, pesquisa e extensão */
	public static final int MEMBRO_COMITE_INTEGRADO						= SigaaSubsistemas.PROJETOS.getId() + 1;
	
	
	/****************************************************************************
	 * PAPEIS TEMPORÁRIOS DO DOCENTE:
	 ****************************************************************************/
	
	/** Papel temporário e representativo de Chefe de Departamento. */
	public static final int CHEFE_DEPARTAMENTO = SigaaSubsistemas.PORTAL_DOCENTE.getId() + 1;
	
	/** Papel temporário e representativo de Coordenador de Curso de Graduação. */
	public static final int COORDENADOR_CURSO = SigaaSubsistemas.PORTAL_DOCENTE.getId() + 2;
	
	/** Papel temporário e representativo de Coordenador de Curso de Stricto Sensu (Pós-Graduação). */
	public static final int COORDENADOR_CURSO_STRICTO = SigaaSubsistemas.PORTAL_DOCENTE.getId() + 3;
	
	/** Papel temporário e representativo de Diretor de Centro. */
	public static final int DIRETOR_CENTRO = SigaaSubsistemas.PORTAL_DOCENTE.getId() + 4;
	
	/** Papel temporário e representativo de Secretaria de Curso de Graduação. */
	public static final int SECRETARIA_COORDENACAO = SigaaSubsistemas.PORTAL_DOCENTE.getId() + 5;
	
	/** Papel temporário e representativo de Secretaria de Departamento. */
	public static final int SECRETARIA_DEPARTAMENTO = SigaaSubsistemas.PORTAL_DOCENTE.getId() + 6;
	
	/** Papel temporário e representativo de Secretaria de Centro. */
	public static final int SECRETARIA_CENTRO = SigaaSubsistemas.PORTAL_DOCENTE.getId() + 7;
	
	/** Papel temporário e representativo de Secretaria de Curso de Pós-Graduação. */
	public static final int SECRETARIA_POS = SigaaSubsistemas.PORTAL_DOCENTE.getId() + 8;
	
	/** Papel temporário e representativo de Chefe de Unidade. */
	public static final int CHEFE_UNIDADE = SigaaSubsistemas.PORTAL_DOCENTE.getId() + 9;

	/** Grupo de papéis que são responsáveis pela gestão das turmas na unidade que ele está vinculado. */
	public static final int[] GESTOR_TURMAS_UNIDADE = { CHEFE_DEPARTAMENTO, COORDENADOR_CURSO,  SECRETARIA_COORDENACAO,  SECRETARIA_DEPARTAMENTO };
	
	/***************************************************************************
	 * MÓDULO PRODOCENTE (Produção Intelectual do Docente):
	 * **************************************************************************/
	
	/** Papel representativo de Administrador da Produção Intelectual do Docente. */
	public static final int ADMINISTRADOR_PRODOCENTE = SigaaSubsistemas.PROD_INTELECTUAL.getId() + 1;
	
	/** Papel relativo ao Prodocente da Pró-Reitoria de Recursos Humanos. */
	public static final int PRODOCENTE_PRH = SigaaSubsistemas.PROD_INTELECTUAL.getId() + 2;
	
	/** Papel relativo ao Prodocente da Pró-Reitoria de Extensão. */
	public static final int PRODOCENTE_PROEX = SigaaSubsistemas.PROD_INTELECTUAL.getId() + 3;
	
	/** Papel relativo ao Prodocente da Pró-Reitoria de Pesquisa. */
	public static final int PRODOCENTE_PROPESQ = SigaaSubsistemas.PROD_INTELECTUAL.getId() + 4;
	
	/** Papel relativo ao Prodocente da Pró-Reitoria de Pós-Graduação. */
	public static final int PRODOCENTE_PPG = SigaaSubsistemas.PROD_INTELECTUAL.getId() + 5;
	
	/** Papel relativo ao Prodocente da Pró-Reitoria de Graduação. */
	public static final int PRODOCENTE_PROGRAD = SigaaSubsistemas.PROD_INTELECTUAL.getId() + 6;
	
	/** Grupo de papéis relacionados a produção intelectual do docente. */
	public static final int[] GRUPO_PRODOCENTE = new int[] { ADMINISTRADOR_PRODOCENTE, PRODOCENTE_PRH, PRODOCENTE_PROEX, PRODOCENTE_PROPESQ, PRODOCENTE_PPG, PRODOCENTE_PROGRAD };


	/******************************************
	 * MÓDULO GRADUAÇÃO:
	 *****************************************/
	
	/** Papel representativo do Departamento de Administração Escolar. */
	public static final int DAE = SigaaSubsistemas.GRADUACAO.getId() + 1;
	
	/** Papel representativo da Coordenação Didático Pedagógica. */
	public static final int CDP = SigaaSubsistemas.GRADUACAO.getId() + 2;
	
	// O PAPEL REFERENTE A COORDENACAO UNICA É SECRETARIA_CENTRO
	//public static final int COORDENACAO_UNICA = SigaaSubsistemas.GRADUACAO.getId() + 3;
	
	/** Papel representativo do Administrador da Coordenação Didático Pedagógica. */
	public static final int ADMINISTRADOR_DAE = SigaaSubsistemas.GRADUACAO.getId() + 4;
	
	/** Papel representativo de Orientador Acadêmico (realiza orientações aos discentes). */
	public static final int ORIENTADOR_ACADEMICO = SigaaSubsistemas.GRADUACAO.getId() + 5;
	
	/** Papel representativo de Secretaria de Graduação. */
	public static final int SECRETARIA_GRADUACAO = SigaaSubsistemas.GRADUACAO.getId() + 6;
	
	/** Papel indicador de Consulta a Dados da Gaaduação. */
	public static final int CONSULTA_DADOS_GRADUACAO = SigaaSubsistemas.GRADUACAO.getId() + 7;
	
	/** Papel representativo de Gestor dos convênios de ensino do tipo Probásica. */
	public static final int GESTOR_PROBASICA = SigaaSubsistemas.GRADUACAO.getId() + 8;	
	
	/** Papel intermediário entre os papeis DAE e ADMINISTRADOR_DAE  */
	public static final int CHEFIA_DAE = SigaaSubsistemas.GRADUACAO.getId() + 10;
	
	/** Papel responsável consultar informações sobre ofertas de vagas das turmas. */
	public static final int CONSULTOR_OFERTAS_TURMAS = SigaaSubsistemas.GRADUACAO.getId() + 12;
	
	/** Papel responsável pelo cadastro de discentes aprovados em processos seletivos. */
	public static final int CADASTRA_DISCENTE_GRADUACAO = SigaaSubsistemas.GRADUACAO.getId() + 13;

	
	/*********************************************************
	 * MÓDULO RESIDÊNCIAS EM SAÚDE:
	 *******************************************************/
	
	/** Papel representativo de Gestor do Complexo Hospitalar. */
	public static final int GESTOR_COMPLEXO_HOSPITALAR = SigaaSubsistemas.COMPLEXO_HOSPITALAR.getId() + 1;
	
	/** Papel representativo da Secretaria de Residência Médica. */
	public static final int SECRETARIA_RESIDENCIA = SigaaSubsistemas.COMPLEXO_HOSPITALAR.getId() + 2;
	
	/** Papel representativo do Coordenador de Programa de Residência Médica. */
	public static final int COORDENADOR_PROGRAMA_RESIDENCIA = SigaaSubsistemas.COMPLEXO_HOSPITALAR.getId() + 3;

	
	/*********************************************************
	 * PORTAL DA AVALIAÇÃO INSTITUCIONAL:
	 *******************************************************/
	
	/** Papel representativo da Comissão de Avaliação da Avaliação Institucional. */
	public static final int COMISSAO_AVALIACAO = SigaaSubsistemas.PORTAL_AVALIACAO_INSTITUCIONAL.getId() + 1;
	/** Papel específico para bolsista que ajuda nos trabalhos da comissão de avaliação. */
	public static final int BOLSISTA_AVALIACAO_INSTITUCIONAL = SigaaSubsistemas.PORTAL_AVALIACAO_INSTITUCIONAL.getId() + 2;

	
	/*******************************************************
	 * MÓDULO STRICTO SENSU(Pós-graduação):
	 *******************************************************/
	
	/** Papel representativo de um Programa de Pós-Graduação em Stricto Sensu. */
	public static final int PPG = SigaaSubsistemas.STRICTO_SENSU.getId() + 1;
	
	/** Papel representativo de um Orientador de discentes de Stricto Sensu  . */
	public static final int ORIENTADOR_STRICTO = SigaaSubsistemas.STRICTO_SENSU.getId() + 2;
	
	/** Papel representativo do Stricto Sensu (Pós-graduação). */
	public static final int ADMINISTRADOR_STRICTO = SigaaSubsistemas.STRICTO_SENSU.getId() + 3;
	
	/** Papel representativo de um Co-Orientador de discentes de Stricto Sensu. */
	public static final int CO_ORIENTADOR_STRICTO = SigaaSubsistemas.STRICTO_SENSU.getId() + 4;
	
	/** Papel representativo Membro da Coordenação de Apoio Técnico-Pedagógico da Docência Assistida Stricto Sensu. */
	public static final int MEMBRO_APOIO_DOCENCIA_ASSISTIDA = SigaaSubsistemas.STRICTO_SENSU.getId() + 5;

	
	/********************************************************
	 * MÓDULO EDUCAÇÃO A DISTÂNCIA:
	 *********************************************************/
	
	/** Papel representativo da Secretaria de Educação a Distância. */
	public static final int SEDIS = SigaaSubsistemas.SEDIS.getId() + 1;
	
	/** Papel representativo do Coordenador Pedagógico do Ensino a Distância. */
	public static final int COORDENADOR_PEDAGOGICO_EAD = SigaaSubsistemas.SEDIS.getId() + 2;
	
	/** Papel representativo do Coordenador de Tutoria do Ensino a Distância. */
	public static final int COORDENADOR_TUTORIA_EAD = SigaaSubsistemas.SEDIS.getId() + 3;
	
	/** Papel representativo do Coordenador Geral do Ensino a Distância. */
	public static final int COORDENADOR_GERAL_EAD = SigaaSubsistemas.SEDIS.getId() + 4;

	/** Papel representativo do Coordenador Geral do Ensino a Distância. */
	public static final int TUTOR_EAD = SigaaSubsistemas.SEDIS.getId() + 5;
	
	/** Papel representativo do usuário que será utilizado para as consultas ao WebService da Ead */
	public static final int CONSULTOR_DADOS_EAD = SigaaSubsistemas.SEDIS.getId() + 6;

	
	/**********************************************************
	 * COMUNIDADES VIRTUAIS
	********************************************************/
	
	/** Papel representativo do Gestor de Comunidades Virtuais. */
	public static final int GESTOR_COMUNIDADADES_VIRTUAIS = SigaaSubsistemas.COMUNIDADE_VIRTUAL.getId() + 1;
	

	/********************************************************************************
	 * MÓDULOS BIBLIOTECA E AFINS (Esse perfil não é do sistema de bibliotecas da UFRN ):
	 ********************************************************************************/
	public static final int CONSULTADOR_ACADEMICO = SigaaSubsistemas.CONSULTA.getId() + 1;

	
	/********************************************************
	 * PLANEJAMENTO e RH:
	 *********************************************************/
	
	/** Papel representativo do Portal do Planejamento. */
	public static final int PORTAL_PLANEJAMENTO = SigaaSubsistemas.PORTAL_PLANEJAMENTO.getId() + 1;
	/** Papel que permite o acesso ao relatório de desempenho acadêmico dos servidores-alunos. */
	public static final int ACOMPANHA_DESEMPENHO_SERVIDORES_NA_POSGRADUACAO = SigaaSubsistemas.PORTAL_PLANEJAMENTO.getId() + 2;

	
	/********************************************************
	 * Portal do CPDI (Comissão Permanente de Desenvolvimento Institucional):
	 *********************************************************/
	
	/** Papel representativo do Membro de Comissão Permanente de Desenvolvimento Institucional */
	public static final int MEMBRO_CPDI = SigaaSubsistemas.PORTAL_CPDI.getId() + 1;

	
	/********************************************************
	 * MÓDULO VESTIBULAR:
	 *********************************************************/
	
	/** Papel representativo do Processo Seletivo Vestibular. */
	public static final int VESTIBULAR           = SigaaSubsistemas.VESTIBULAR.getId() + 1;
	
	/** Papel representativo do Bolsista ou Auxiliar em tarefas administrativas do Vestibular. */
	public static final int BOLSISTA_VESTIBULAR  = SigaaSubsistemas.VESTIBULAR.getId() + 2;
	
	/** Papel representativo do Docente. */
	public static final int DOCENTE              = SigaaSubsistemas.VESTIBULAR.getId() + 3;

	/** Papel responsável por gerenciar o cadastramento dos discentes aprovados no vestibular e gerar as novas convocações. */
	public static final int GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS = SigaaSubsistemas.VESTIBULAR.getId() + 4;
	
	
	/********************************************************
	 * MÓDULO ASSISTÊNCIA AO ESTUDANTE:
	 *********************************************************/

	/** Habilita o Módulo Assistência ao Estudante. */
	public static final int SAE = SigaaSubsistemas.SAE.getId() + 1;
	
	/** Papel representativo do Coordenador do Módulo Assistência ao Estudante, acesso a tudo. */
	public static final int SAE_COORDENADOR = SigaaSubsistemas.SAE.getId() + 2;
	
	/** Usado pelo pessoal do R.U. (app desktop). */
	public static final int CONTROLADOR_ACESSO_RESTAURANTE = SigaaSubsistemas.SAE.getId() + 3;
	
	/** Indica quem vai poder definir os dias que os discentes terão direito a acessar o R.U.*/
	public static final int SAE_DEFINIR_DIAS_ALIMENTACAO = SigaaSubsistemas.SAE.getId() + 5;
	
	/** Permite o usuário consultar as adesões que o discente fez ao cadastro único. */
	public static final int SAE_VISUALIZAR_CADASTRO_UNICO = SigaaSubsistemas.SAE.getId() + 6;
	
	/** Permite o usuário acessar o ranking de pontuação dos discentes. */
	public static final int SAE_VISUALIZAR_RANKING = SigaaSubsistemas.SAE.getId() + 7;

	/** Permite o usuário gerenciar os cartões de benefício. */
	public static final int SAE_GESTOR_CARTAO_ALIMENTACAO = SigaaSubsistemas.SAE.getId() + 8;
	
	/** Permite o usuário gere relatórios de acesso ao RU. */
	public static final int SAE_VISUALIZA_ACESSO_RU = SigaaSubsistemas.SAE.getId() + 9;
	
	/*********************************************************
	 * SISTEMA DE CADASTRO DE PESSOAS:
	 *********************************************************/
	
	/** Papel representativo do Gestor de Cadastro de Identificação de Pessoas. */
	public static final int GESTOR_CADASTRO_IDENTIFICACAO_PESSOA = SigaaSubsistemas.SAE.getId() + 4;


	/*********************************************************
	 * MÓDULO AMBIENTES VIRTUAIS:
	 *********************************************************/
	
	/** Papel representativo do Módulo Ambientes Virtuais. */
	public static final int AMBIENTE_VIRTUAL = SigaaSubsistemas.AMBIENTES_VIRTUAIS.getId() + 1;
	
	/** Papel representativo do Gestor do Módulo Ambientes Virtuais. */
	public static final int GESTOR_AMBIENTES_VIRTUAIS = SigaaSubsistemas.AMBIENTES_VIRTUAIS.getId() + 2;


	/********************************************************
	 * SISTEMA DE BIBLIOTECAS DA UFRN:
	 *********************************************************/

	/** Papel representativo para acessar as operações gerais da catalogação  (na maioria das vezes só visualizar as informações da catalogação). */
	public static final int BIBLIOTECA_SETOR_CATALOGACAO = SigaaSubsistemas.BIBLIOTECA.getId() + 1;
	
	/** Papel representativo para acessar todas as operações de catalogação (pode catalogar). */
	public static final int BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO = SigaaSubsistemas.BIBLIOTECA.getId() + 2;
	
	/** Papel representativo para catalogar exemplares sem tombamento. Uma maneira de restringir a catalogação sem tombamento. */
	public static final int BIBLIOTECA_SETOR_CATALOGACAO_SEM_TOMBAMENTO = SigaaSubsistemas.BIBLIOTECA.getId() + 3;
	
	/** Papel que dá permissão aos usuário de incluírem e alterar as informações de materiais do sistema. Não é permitido a esse papel alterar
	 * os dados das catalogações.
	 */
	public static final int BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS = SigaaSubsistemas.BIBLIOTECA.getId() + 4;
	

	
	/** Permite acessar as operações gerais da circulação. */
	public static final int BIBLIOTECA_SETOR_CIRCULACAO = SigaaSubsistemas.BIBLIOTECA.getId() + 11;
	
	/** Permite ao usuário além das operações básicas, realizar algumas operações especiais dentro de circulação.*/
	public static final int BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO = SigaaSubsistemas.BIBLIOTECA.getId() + 12;
	
	/** Papel específico para quem trabalha apenas fazendo o checkout na biblioteca. */
	public static final int BIBLIOTECA_SETOR_CIRCULACAO_CHECKOUT = SigaaSubsistemas.BIBLIOTECA.getId() + 13;

	/** Papel específico para quem precisa etimir do documento de quitação do usuário. A aba de circulação vai está ativada apenas com essa opção */
	public static final int BIBLIOTECA_EMITE_DECLARACAO_QUITACAO =  SigaaSubsistemas.BIBLIOTECA.getId() + 14;
	
	
	
	/** Papel quer permite ao usuário visualizar os dados do setor de aquisição. */
	public static final int BIBLIOTECA_SETOR_AQUISICAO = SigaaSubsistemas.BIBLIOTECA.getId() + 21;
	
	/** Papel quer permite ao usuário realizar todas as operações  do setor de aquisição.  */
	public static final int BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO = SigaaSubsistemas.BIBLIOTECA.getId() + 22;

	
	
	/** Pode visualizar os dados do setor de informação e referência.  */
	public static final int BIBLIOTECA_SETOR_INFO_E_REF = SigaaSubsistemas.BIBLIOTECA.getId() + 31;
	
	/** Pode realizar todas as operações dentro do setor de informação e referência.  */
	public static final int BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO = SigaaSubsistemas.BIBLIOTECA.getId() + 32;

	
	
	/** Pode ver os relatórios do sistema de bibliotecas. */
	public static final int BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO = SigaaSubsistemas.BIBLIOTECA.getId() + 41;
	
	
	
	/** Papel representativo para realizar operações que não são de um setor específico para a sua biblioteca. */
	public static final int BIBLIOTECA_ADMINISTRADOR_LOCAL = SigaaSubsistemas.BIBLIOTECA.getId() + 51;

	/** Papel representativo para realizar operações que não são de um setor específico em TODAS as biblioteca.  */
	public static final int BIBLIOTECA_ADMINISTRADOR_GERAL = SigaaSubsistemas.BIBLIOTECA.getId() + 52;

	/** Papel especial que realiza alguns configurações no sistema, papel não atribuido a bibliotecários e sim ao setor de informática . */
	public static final int BIBLIOTECA_ADMINISTRADOR_SISTEMA = SigaaSubsistemas.BIBLIOTECA.getId() + 53;
	
	
	
	/** Papel representativo para visualizar os dados dentro do setor de intercâmbio. */
	public static final int BIBLIOTECA_SETOR_INTERCAMBIO = SigaaSubsistemas.BIBLIOTECA.getId() + 61;
	
	/** Papel representativo para realizar todas as operações dentro do setor de intercâmbio. */
	public static final int BIBLIOTECA_SETOR_INTERCAMBIO_BIBLIOTECARIO = SigaaSubsistemas.BIBLIOTECA.getId() + 62;
	
	
	
	/** Papel representativo para operações com a BDTD. */
	public static final int BIBLIOTECA_GESTOR_BDTD = SigaaSubsistemas.BIBLIOTECA.getId() + 63;
	
	
	
	
	
	/********************************************************************
	 *  Outros papéis relacionados às funcionalidades das bibliotecas:
	 ********************************************************************/
	
	/** Grupo de papéis que dão permissão ao usuário de solicitar levantamento de infra-estrutura. */
	public static final int[] BIBLIOTECA_SOLICITAR_LEVANTAMENTO_INFRA = new int[] {
			CHEFE_DEPARTAMENTO, DIRETOR_CENTRO};
	
	
	/********************************************************
	 * INFRA-ESTRUTURA FÍSICA:
	 *********************************************************/
	
	/** Papel representativo para gerenciar os espaços físicos e as reservas para sua utilização de qualquer espaço físico da instituíção. */
	public static final int GESTOR_INFRA_ESTRUTURA_FISICA_GLOBAL = SigaaSubsistemas.INFRA_ESTRUTURA_FISICA.getId() + 1;
	
	/** Papel representativo para gerenciar os espaços físicos e as reservas para a unidade onde tem permissão. */
	public static final int GESTOR_INFRA_ESTRUTURA_FISICA = SigaaSubsistemas.INFRA_ESTRUTURA_FISICA.getId() + 2;
	
	/** Papel representativo do Responsável das reservas para utilização de um espaço físico onde tem permissão. */
	public static final int RESPONSAVEL_RESERVA_ESPACO_FISICO = SigaaSubsistemas.INFRA_ESTRUTURA_FISICA.getId() + 3;

	
	/********************************************************
	 * PORTAL DE RELATÓRIOS:
	 *********************************************************/
	
	/** Papel representativo para visualizar os relatórios do sistema. */
	public static final int PORTAL_RELATORIOS = SigaaSubsistemas.PORTAL_RELATORIOS.getId() + 1;
	
		
	/********************************************************
	 * MÓDULO DIPLOMAS:
	 *********************************************************/
	
	/** Papel representativo para registrar diplomas de graduação. */
	public static final int GESTOR_DIPLOMAS_GRADUACAO = SigaaSubsistemas.REGISTRO_DIPLOMAS.getId() + 1;
	
	/** Papel representativo para registrar diplomas de stricto sensu. */
	public static final int GESTOR_DIPLOMAS_STRICTO = SigaaSubsistemas.REGISTRO_DIPLOMAS.getId() + 2;
	
	/** Papel representativo para registrar diplomas de lato sensu. */
	public static final int GESTOR_DIPLOMAS_LATO = SigaaSubsistemas.REGISTRO_DIPLOMAS.getId() + 3;
	
	/** Papel representativo para administrar o módulo de registro de diplomas. */
	public static final int ADMINISTRADOR_DIPLOMAS = SigaaSubsistemas.REGISTRO_DIPLOMAS.getId() + 4;
	
	/*********************************************************
	 * MÓDULO DE CONVÊNIOS DE ESTÁGIOS:
	 *********************************************************/
	
	/** Papel representativo do Gestor de convênios de estágios da Pró Reitoria de Planejamento e Coordenação Geral. */
	public static final int GESTOR_CONVENIOS_ESTAGIO_PROPLAN = SigaaSubsistemas.CONVENIOS_ESTAGIO.getId() + 1;
	
	/** Papel representativo do Coordenador de Estágios. */
	public static final int COORDENADOR_ESTAGIOS = SigaaSubsistemas.CONVENIOS_ESTAGIO.getId() + 2;
	
	
	/*********************************************************
	 * MÓDULO DE NECESSIDADES EDUCACIONAIS ESPECIAIS - NEE:
	 *********************************************************/
	
	/** Papel representativo do Gestor de Necessidades Especiais. */
	public static final int GESTOR_NEE = SigaaSubsistemas.NEE.getId() + 1;
	
	
	/*********************************************************
	 * PORTAL DE ESTÁGIOS:
	 *********************************************************/

	/** Papel representativo do Portal Concedente de Estágio. */
	public static final int PORTAL_CONCEDENTE_ESTAGIO = SigaaSubsistemas.PORTAL_CONCEDENTE_ESTAGIO.getId() + 1;
	
	
	/*********************************************************
	 * PROGRAMA DE ATUALIZAÇÃO PEDAGÓGICA:
	 *********************************************************/

	/** Papel representativo do Portal Concedente de Estágio. */
	public static final int GESTOR_PAP = SigaaSubsistemas.PROGRAMA_ATUALIZACAO_PEDAGOGICA.getId() + 1;
	
	
	/*********************************************************
	 * MÓDULO DE OUVIDORIA:
	 *********************************************************/
	
	/** Papel representativo do Módulo de Ouvidoria. */
	public static final int OUVIDOR = SigaaSubsistemas.OUVIDORIA.getId() + 1;
	
	/** Papel representativo da secretaria da Ouvidoria. */
	public static final int SECRETARIO_OUVIDOR = SigaaSubsistemas.OUVIDORIA.getId() + 2;
	
	/** Papel temporário representativo de uma pessoa designada a responder uma manifestação através do módulo de ouvidoria. */
	public static final int DESIGNADO_OUVIDORIA = SigaaSubsistemas.OUVIDORIA.getId() + 3;
	
	
	
	/******************************************
	 * MÓDULO MÉDIO:
	 *****************************************/
	/** Papel representativo do Gestor do Ensino Médio. */
	public static final int GESTOR_MEDIO = SigaaSubsistemas.MEDIO.getId() + 1;

	/** Papel representativo do Coordenador de Ensino médio. */
	public static final int COORDENADOR_MEDIO = SigaaSubsistemas.MEDIO.getId() + 2;
	
	/** Papel representativo da secretaria de Ensino médio. */
	public static final int SECRETARIA_MEDIO = SigaaSubsistemas.MEDIO.getId() + 3;
	
	/** Papel temporário representativo do familiar de Ensino médio. */
	public static final int FAMILIAR_MEDIO = SigaaSubsistemas.MEDIO.getId() + 4;
	
	/** Papel representativo para permitir consultas gerais no Ensino médio. */
	public static final int PEDAGOGICO_MEDIO = SigaaSubsistemas.MEDIO.getId() + 5;
	
	
	/******************************************
	 * MÓDULO TRADUÇÃO DE DOCUMENTOS:
	 *****************************************/
	/** Papel representativo do Gestor do módulo de Tradução de Documentos. */
	public static final int GESTOR_TRADUCAO_DOCUMENTOS = SigaaSubsistemas.RELACOES_INTERNACIONAIS.getId() + 1;
	
	/** Papel representativo do tradutor de dados acadêmicos. */
	public static final int TRADUTOR_DADOS_ACADEMICOS = SigaaSubsistemas.RELACOES_INTERNACIONAIS.getId() + 2;
	
	
	/******************************************
	 * MÓDULO DO METRÓPOLE DIGITAL 
	 *****************************************/
	
	/**Papel representativo do Gestor do módulo do Instituto Metrópole Digital **/
	public static final int GESTOR_METROPOLE_DIGITAL = SigaaSubsistemas.METROPOLE_DIGITAL.getId() + 1;  // 11601
	
	/**Papel representativo ao Tutor do IMD **/
	public static final int TUTOR_IMD = SigaaSubsistemas.METROPOLE_DIGITAL.getId() + 2; // 11602
	
	/**Papel representativo ao coordenador de tutores do IMD **/
	public static final int COORDENADOR_TUTOR_IMD = SigaaSubsistemas.METROPOLE_DIGITAL.getId() + 3; // 11603
	
	/**Papel representativo ao coordenador de pólo do IMD **/
	public static final int COORDENADOR_POLO_IMD = SigaaSubsistemas.METROPOLE_DIGITAL.getId() + 4; // 11604
	
	/**Papel representativo ao Assistente Social do IMD **/
	public static final int ASSISTENTE_SOCIAL_IMD = SigaaSubsistemas.METROPOLE_DIGITAL.getId() + 5; // 11605
	
	/** Papel representativo para permitir que faça convocação do processo seletivo do IMD. */
	public static final int GESTOR_CONVOCACAO_PROCESSO_SELETIVO_IMD = SigaaSubsistemas.METROPOLE_DIGITAL.getId() + 8; //11608 
	
	/** Papel representativo para permitir que faça cadastramento do processo seletivo do IMD. */
	public static final int GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_IMD = SigaaSubsistemas.METROPOLE_DIGITAL.getId() + 9; //11609
	

	/******************************************
	 * MÓDULO DO ENSINO EM REDE
	 *****************************************/
	
	public static final int COORDENADOR_GERAL_REDE = SigaaSubsistemas.ENSINO_REDE.getId() + 1;  // 30001
	// Papel temporário
	public static final int COORDENADOR_UNIDADE_REDE = SigaaSubsistemas.ENSINO_REDE.getId() + 2;  // 30001
		
}