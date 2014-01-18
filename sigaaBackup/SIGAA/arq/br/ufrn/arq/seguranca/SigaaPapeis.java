/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '05/03/2007'
 *
 */
package br.ufrn.arq.seguranca;

/**
 * Constante com todos os pap�is do sistema acad�mico.
 *
 * @author Gleydson Lima
 *
 */
public class SigaaPapeis {

	/**
	 * -------------------  PAP�IS GERAIS ----------------------
	 * ---------------------------------------------------------
	*/
	
	/** Papel representativo do Administrador do Sistema Acad�mico. */
	public static final int ADMINISTRADOR_SIGAA = SigaaSubsistemas.ADMINISTRACAO.getId() + 1;
	
	
	/***************************************************************************
	 * ENSINO:
	 **************************************************************************/

	/** Papel representativo do Gestor do Ensino T�cnico. */
	public static final int GESTOR_TECNICO = SigaaSubsistemas.TECNICO.getId() + 1;

	/** Papel representativo do Coordenador de um curso do Ensino T�cnico. */	
	public static final int COORDENADOR_TECNICO = SigaaSubsistemas.TECNICO.getId() + 2;
	
	/** Papel representativo da Secretaria de um curso do Ensino T�cnico. */
	public static final int SECRETARIA_TECNICO = SigaaSubsistemas.TECNICO.getId() + 3;
	
	/** Papel representativo para permitir consultas gerais no Ensino t�cnico. */
	public static final int PEDAGOGICO_TECNICO = SigaaSubsistemas.TECNICO.getId() + 4;
	
	/** Papel representativo para permitir que fa�a importa��o do processo seletivo. */
	public static final int GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO = SigaaSubsistemas.TECNICO.getId() + 5;
	
	/** Papel representativo para permitir que fa�a convoca��o do processo seletivo. */
	public static final int GESTOR_CONVOCACAO_PROCESSO_SELETIVO_TECNICO = SigaaSubsistemas.TECNICO.getId() + 6;
	
	/** Papel representativo para permitir que fa�a cadastramento do processo seletivo. */
	public static final int GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_TECNICO = SigaaSubsistemas.TECNICO.getId() + 7;
	
	/** Papel representativo do Gestor de uma Escola Acad�mica Especializada. */
	public static final int GESTOR_FORMACAO_COMPLEMENTAR = SigaaSubsistemas.FORMACAO_COMPLEMENTAR.getId() + 1;
	
	/** Papel representativo do Gestor do Ensino Infantil. */
	public static final int GESTOR_INFANTIL = SigaaSubsistemas.INFANTIL.getId() + 1;

	/** Papel representativo do Gestor da P�s-Gradua��o em Lato Sensu (Especializa��o). */
	public static final int GESTOR_LATO = SigaaSubsistemas.LATO_SENSU.getId() + 1;

	/** Papel representativo do Coordenador de um curso de P�s-Gradua��o em Lato Sensu (Especializa��o). */
	public static final int COORDENADOR_LATO = SigaaSubsistemas.LATO_SENSU.getId() + 2;

	/** Papel representativo da Secretaria de um curso de P�s-Gradua��o em Lato Sensu (Especializa��o). */
	public static final int SECRETARIA_LATO = SigaaSubsistemas.LATO_SENSU.getId() + 3;

	

	/***************************************************************************
	 * M�DULO PESQUISA:
	 **************************************************************************/

	/** Papel representativo do Gestor do M�dulo Pesquisa. */
	public static final int GESTOR_PESQUISA = SigaaSubsistemas.PESQUISA.getId() + 1;
	
	/** Papel representativo de Consultor dentro do M�dulo Pesquisa. */
	public static final int CONSULTOR = SigaaSubsistemas.PESQUISA.getId() + 2;
	
	/** Papel representativo de Membro de Comit� de Pesquisa. */
	public static final int MEMBRO_COMITE_PESQUISA = SigaaSubsistemas.PESQUISA.getId() + 3;
	
	/** Papel representativo do N�cleo de Inova��o Tecnol�gica. */
	public static final int NIT = SigaaSubsistemas.PESQUISA.getId() + 4;
	
	/** Papel representativo do Administrador do M�dulo Pesquisa. */
	public static final int ADMINISTRADOR_PESQUISA = SigaaSubsistemas.PESQUISA.getId() + 5;
	
	/** Papel representativo de T�cnicos Administrativos com participa��o em Projetos de Pesquisa e Notifica��es de Inven��o. */
	public static final int PESQUISA_TECNICO_ADMINISTRATIVO = SigaaSubsistemas.PESQUISA.getId() + 6;

	
	/***************************************************************************
	 * M�DULO MONITORIA:
	 **************************************************************************/
	
	/** Gestor do m�dulo de monitoria no SIGAA. Libera acesso ao m�dulo Monitoria no menu de m�dulos do sistema. menu de acesso �s opera��es: SIGAA > Monitoria */
	public static final int GESTOR_MONITORIA 					= SigaaSubsistemas.MONITORIA.getId() + 1;
	
	/** Papel representativo de Membro de Comit� de Monitoria. � a comiss�o constitu�da por um docente de cada CA (Centro Acad�mico) 
	 * e um docente por Unidade Acad�mica Especializada, cujo mandato dos membros � de dois anos, sendo permitida uma prorroga��o. 
	 * Membros desta comiss�o s�o respons�veis por avaliarem os projetos e os relat�rios SID. 
	 * Menu de acesso �s opera��es: SIGAA > Menu Docente > Ensino >Projetos > Projeto de Monitoria > Comiss�o Monitoria > */
	public static final int MEMBRO_COMITE_MONITORIA 			= SigaaSubsistemas.MONITORIA.getId() + 2;
	
	/** Papel representativo de Membro de Comit� Cient�fico de Monitoria. 
	 * Comiss�o que � composta por docentes do quadro permanente da UFRN, com o objetivo de auxiliar a 
	 * comiss�o de monitoria no tangente avalia��o dos resumos submetidos ao Semin�rio de Inicia��o � Doc�ncia (SID). 
	 * Menu de acesso �s opera��es: SIGAA > Menu Docente > Ensino >Projetos > Projeto de Monitoria > Comiss�o Monitoria >  */
	public static final int MEMBRO_COMITE_CIENTIFICO_MONITORIA 	= SigaaSubsistemas.MONITORIA.getId() + 3;

	
	/****************************************************************************
	 * M�DULO EXTENS�O:
	 ****************************************************************************/

	/** Papel representativo do Gestor do M�dulo Extens�o. */
	public static final int GESTOR_EXTENSAO 								= SigaaSubsistemas.EXTENSAO.getId() + 1;
	
	/** Papel representativo de Membro de Comit� de Extens�o. */
	public static final int MEMBRO_COMITE_EXTENSAO 							= SigaaSubsistemas.EXTENSAO.getId() + 2;
	
	/** Papel representativo de parceiros da Extens�o. */
	public static final int PARECERISTA_EXTENSAO 							= SigaaSubsistemas.EXTENSAO.getId() + 3;
	
	/** Papel representativo de Presidente de Comit� de Extens�o. */
	public static final int PRESIDENTE_COMITE_EXTENSAO 						= SigaaSubsistemas.EXTENSAO.getId() + 4;
	
	/** Papel representativo de T�cnicos Administrativos em projetos de Extens�o. */
	public static final int EXTENSAO_TECNICO_ADMINISTRATIVO 				= SigaaSubsistemas.EXTENSAO.getId() + 5;
	
	/** Papel representativo de Coordenador de Programas e/ou Projetos de Extens�o. */
	public static final int COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO 		= SigaaSubsistemas.EXTENSAO.getId() + 6;
	
	/** Papel representativo de Coordenador de Cursos,Eventos e Produtos em projetos de Extens�o. */
	public static final int COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO 	= SigaaSubsistemas.EXTENSAO.getId() + 7;
	
	/** Papel representativo de Apoio T�cnico da Coordena��o em projetos de Extens�o. */
	public static final int APOIO_TECNICO_COORDENACAO_EXTENSAO				= SigaaSubsistemas.EXTENSAO.getId() + 8;
	
	
	/****************************************************************************
	 * M�DULO A��ES ACAD�MICAS ASSOCIADAS:
	 ****************************************************************************/
	
	/** Papel representativo de Avalidor de A��es Associadas. */	
	public static final int AVALIADOR_ACOES_ASSOCIADAS			= SigaaSubsistemas.ACOES_ASSOCIADAS.getId() + 1;

	/** Papel representativo do Gestor de bolsas de A��es Associadas. */	
	public static final int GESTOR_BOLSAS_ACOES_ASSOCIADAS		= SigaaSubsistemas.ACOES_ASSOCIADAS.getId() + 2;

	
	/** Membro da comiss�o integrada de ensino, pesquisa e extens�o */
	public static final int MEMBRO_COMITE_INTEGRADO						= SigaaSubsistemas.PROJETOS.getId() + 1;
	
	
	/****************************************************************************
	 * PAPEIS TEMPOR�RIOS DO DOCENTE:
	 ****************************************************************************/
	
	/** Papel tempor�rio e representativo de Chefe de Departamento. */
	public static final int CHEFE_DEPARTAMENTO = SigaaSubsistemas.PORTAL_DOCENTE.getId() + 1;
	
	/** Papel tempor�rio e representativo de Coordenador de Curso de Gradua��o. */
	public static final int COORDENADOR_CURSO = SigaaSubsistemas.PORTAL_DOCENTE.getId() + 2;
	
	/** Papel tempor�rio e representativo de Coordenador de Curso de Stricto Sensu (P�s-Gradua��o). */
	public static final int COORDENADOR_CURSO_STRICTO = SigaaSubsistemas.PORTAL_DOCENTE.getId() + 3;
	
	/** Papel tempor�rio e representativo de Diretor de Centro. */
	public static final int DIRETOR_CENTRO = SigaaSubsistemas.PORTAL_DOCENTE.getId() + 4;
	
	/** Papel tempor�rio e representativo de Secretaria de Curso de Gradua��o. */
	public static final int SECRETARIA_COORDENACAO = SigaaSubsistemas.PORTAL_DOCENTE.getId() + 5;
	
	/** Papel tempor�rio e representativo de Secretaria de Departamento. */
	public static final int SECRETARIA_DEPARTAMENTO = SigaaSubsistemas.PORTAL_DOCENTE.getId() + 6;
	
	/** Papel tempor�rio e representativo de Secretaria de Centro. */
	public static final int SECRETARIA_CENTRO = SigaaSubsistemas.PORTAL_DOCENTE.getId() + 7;
	
	/** Papel tempor�rio e representativo de Secretaria de Curso de P�s-Gradua��o. */
	public static final int SECRETARIA_POS = SigaaSubsistemas.PORTAL_DOCENTE.getId() + 8;
	
	/** Papel tempor�rio e representativo de Chefe de Unidade. */
	public static final int CHEFE_UNIDADE = SigaaSubsistemas.PORTAL_DOCENTE.getId() + 9;

	/** Grupo de pap�is que s�o respons�veis pela gest�o das turmas na unidade que ele est� vinculado. */
	public static final int[] GESTOR_TURMAS_UNIDADE = { CHEFE_DEPARTAMENTO, COORDENADOR_CURSO,  SECRETARIA_COORDENACAO,  SECRETARIA_DEPARTAMENTO };
	
	/***************************************************************************
	 * M�DULO PRODOCENTE (Produ��o Intelectual do Docente):
	 * **************************************************************************/
	
	/** Papel representativo de Administrador da Produ��o Intelectual do Docente. */
	public static final int ADMINISTRADOR_PRODOCENTE = SigaaSubsistemas.PROD_INTELECTUAL.getId() + 1;
	
	/** Papel relativo ao Prodocente da Pr�-Reitoria de Recursos Humanos. */
	public static final int PRODOCENTE_PRH = SigaaSubsistemas.PROD_INTELECTUAL.getId() + 2;
	
	/** Papel relativo ao Prodocente da Pr�-Reitoria de Extens�o. */
	public static final int PRODOCENTE_PROEX = SigaaSubsistemas.PROD_INTELECTUAL.getId() + 3;
	
	/** Papel relativo ao Prodocente da Pr�-Reitoria de Pesquisa. */
	public static final int PRODOCENTE_PROPESQ = SigaaSubsistemas.PROD_INTELECTUAL.getId() + 4;
	
	/** Papel relativo ao Prodocente da Pr�-Reitoria de P�s-Gradua��o. */
	public static final int PRODOCENTE_PPG = SigaaSubsistemas.PROD_INTELECTUAL.getId() + 5;
	
	/** Papel relativo ao Prodocente da Pr�-Reitoria de Gradua��o. */
	public static final int PRODOCENTE_PROGRAD = SigaaSubsistemas.PROD_INTELECTUAL.getId() + 6;
	
	/** Grupo de pap�is relacionados a produ��o intelectual do docente. */
	public static final int[] GRUPO_PRODOCENTE = new int[] { ADMINISTRADOR_PRODOCENTE, PRODOCENTE_PRH, PRODOCENTE_PROEX, PRODOCENTE_PROPESQ, PRODOCENTE_PPG, PRODOCENTE_PROGRAD };


	/******************************************
	 * M�DULO GRADUA��O:
	 *****************************************/
	
	/** Papel representativo do Departamento de Administra��o Escolar. */
	public static final int DAE = SigaaSubsistemas.GRADUACAO.getId() + 1;
	
	/** Papel representativo da Coordena��o Did�tico Pedag�gica. */
	public static final int CDP = SigaaSubsistemas.GRADUACAO.getId() + 2;
	
	// O PAPEL REFERENTE A COORDENACAO UNICA � SECRETARIA_CENTRO
	//public static final int COORDENACAO_UNICA = SigaaSubsistemas.GRADUACAO.getId() + 3;
	
	/** Papel representativo do Administrador da Coordena��o Did�tico Pedag�gica. */
	public static final int ADMINISTRADOR_DAE = SigaaSubsistemas.GRADUACAO.getId() + 4;
	
	/** Papel representativo de Orientador Acad�mico (realiza orienta��es aos discentes). */
	public static final int ORIENTADOR_ACADEMICO = SigaaSubsistemas.GRADUACAO.getId() + 5;
	
	/** Papel representativo de Secretaria de Gradua��o. */
	public static final int SECRETARIA_GRADUACAO = SigaaSubsistemas.GRADUACAO.getId() + 6;
	
	/** Papel indicador de Consulta a Dados da Gaadua��o. */
	public static final int CONSULTA_DADOS_GRADUACAO = SigaaSubsistemas.GRADUACAO.getId() + 7;
	
	/** Papel representativo de Gestor dos conv�nios de ensino do tipo Prob�sica. */
	public static final int GESTOR_PROBASICA = SigaaSubsistemas.GRADUACAO.getId() + 8;	
	
	/** Papel intermedi�rio entre os papeis DAE e ADMINISTRADOR_DAE  */
	public static final int CHEFIA_DAE = SigaaSubsistemas.GRADUACAO.getId() + 10;
	
	/** Papel respons�vel consultar informa��es sobre ofertas de vagas das turmas. */
	public static final int CONSULTOR_OFERTAS_TURMAS = SigaaSubsistemas.GRADUACAO.getId() + 12;
	
	/** Papel respons�vel pelo cadastro de discentes aprovados em processos seletivos. */
	public static final int CADASTRA_DISCENTE_GRADUACAO = SigaaSubsistemas.GRADUACAO.getId() + 13;

	
	/*********************************************************
	 * M�DULO RESID�NCIAS EM SA�DE:
	 *******************************************************/
	
	/** Papel representativo de Gestor do Complexo Hospitalar. */
	public static final int GESTOR_COMPLEXO_HOSPITALAR = SigaaSubsistemas.COMPLEXO_HOSPITALAR.getId() + 1;
	
	/** Papel representativo da Secretaria de Resid�ncia M�dica. */
	public static final int SECRETARIA_RESIDENCIA = SigaaSubsistemas.COMPLEXO_HOSPITALAR.getId() + 2;
	
	/** Papel representativo do Coordenador de Programa de Resid�ncia M�dica. */
	public static final int COORDENADOR_PROGRAMA_RESIDENCIA = SigaaSubsistemas.COMPLEXO_HOSPITALAR.getId() + 3;

	
	/*********************************************************
	 * PORTAL DA AVALIA��O INSTITUCIONAL:
	 *******************************************************/
	
	/** Papel representativo da Comiss�o de Avalia��o da Avalia��o Institucional. */
	public static final int COMISSAO_AVALIACAO = SigaaSubsistemas.PORTAL_AVALIACAO_INSTITUCIONAL.getId() + 1;
	/** Papel espec�fico para bolsista que ajuda nos trabalhos da comiss�o de avalia��o. */
	public static final int BOLSISTA_AVALIACAO_INSTITUCIONAL = SigaaSubsistemas.PORTAL_AVALIACAO_INSTITUCIONAL.getId() + 2;

	
	/*******************************************************
	 * M�DULO STRICTO SENSU(P�s-gradua��o):
	 *******************************************************/
	
	/** Papel representativo de um Programa de P�s-Gradua��o em Stricto Sensu. */
	public static final int PPG = SigaaSubsistemas.STRICTO_SENSU.getId() + 1;
	
	/** Papel representativo de um Orientador de discentes de Stricto Sensu  . */
	public static final int ORIENTADOR_STRICTO = SigaaSubsistemas.STRICTO_SENSU.getId() + 2;
	
	/** Papel representativo do Stricto Sensu (P�s-gradua��o). */
	public static final int ADMINISTRADOR_STRICTO = SigaaSubsistemas.STRICTO_SENSU.getId() + 3;
	
	/** Papel representativo de um Co-Orientador de discentes de Stricto Sensu. */
	public static final int CO_ORIENTADOR_STRICTO = SigaaSubsistemas.STRICTO_SENSU.getId() + 4;
	
	/** Papel representativo Membro da Coordena��o de Apoio T�cnico-Pedag�gico da Doc�ncia Assistida Stricto Sensu. */
	public static final int MEMBRO_APOIO_DOCENCIA_ASSISTIDA = SigaaSubsistemas.STRICTO_SENSU.getId() + 5;

	
	/********************************************************
	 * M�DULO EDUCA��O A DIST�NCIA:
	 *********************************************************/
	
	/** Papel representativo da Secretaria de Educa��o a Dist�ncia. */
	public static final int SEDIS = SigaaSubsistemas.SEDIS.getId() + 1;
	
	/** Papel representativo do Coordenador Pedag�gico do Ensino a Dist�ncia. */
	public static final int COORDENADOR_PEDAGOGICO_EAD = SigaaSubsistemas.SEDIS.getId() + 2;
	
	/** Papel representativo do Coordenador de Tutoria do Ensino a Dist�ncia. */
	public static final int COORDENADOR_TUTORIA_EAD = SigaaSubsistemas.SEDIS.getId() + 3;
	
	/** Papel representativo do Coordenador Geral do Ensino a Dist�ncia. */
	public static final int COORDENADOR_GERAL_EAD = SigaaSubsistemas.SEDIS.getId() + 4;

	/** Papel representativo do Coordenador Geral do Ensino a Dist�ncia. */
	public static final int TUTOR_EAD = SigaaSubsistemas.SEDIS.getId() + 5;
	
	/** Papel representativo do usu�rio que ser� utilizado para as consultas ao WebService da Ead */
	public static final int CONSULTOR_DADOS_EAD = SigaaSubsistemas.SEDIS.getId() + 6;

	
	/**********************************************************
	 * COMUNIDADES VIRTUAIS
	********************************************************/
	
	/** Papel representativo do Gestor de Comunidades Virtuais. */
	public static final int GESTOR_COMUNIDADADES_VIRTUAIS = SigaaSubsistemas.COMUNIDADE_VIRTUAL.getId() + 1;
	

	/********************************************************************************
	 * M�DULOS BIBLIOTECA E AFINS (Esse perfil n�o � do sistema de bibliotecas da UFRN ):
	 ********************************************************************************/
	public static final int CONSULTADOR_ACADEMICO = SigaaSubsistemas.CONSULTA.getId() + 1;

	
	/********************************************************
	 * PLANEJAMENTO e RH:
	 *********************************************************/
	
	/** Papel representativo do Portal do Planejamento. */
	public static final int PORTAL_PLANEJAMENTO = SigaaSubsistemas.PORTAL_PLANEJAMENTO.getId() + 1;
	/** Papel que permite o acesso ao relat�rio de desempenho acad�mico dos servidores-alunos. */
	public static final int ACOMPANHA_DESEMPENHO_SERVIDORES_NA_POSGRADUACAO = SigaaSubsistemas.PORTAL_PLANEJAMENTO.getId() + 2;

	
	/********************************************************
	 * Portal do CPDI (Comiss�o Permanente de Desenvolvimento Institucional):
	 *********************************************************/
	
	/** Papel representativo do Membro de Comiss�o Permanente de Desenvolvimento Institucional */
	public static final int MEMBRO_CPDI = SigaaSubsistemas.PORTAL_CPDI.getId() + 1;

	
	/********************************************************
	 * M�DULO VESTIBULAR:
	 *********************************************************/
	
	/** Papel representativo do Processo Seletivo Vestibular. */
	public static final int VESTIBULAR           = SigaaSubsistemas.VESTIBULAR.getId() + 1;
	
	/** Papel representativo do Bolsista ou Auxiliar em tarefas administrativas do Vestibular. */
	public static final int BOLSISTA_VESTIBULAR  = SigaaSubsistemas.VESTIBULAR.getId() + 2;
	
	/** Papel representativo do Docente. */
	public static final int DOCENTE              = SigaaSubsistemas.VESTIBULAR.getId() + 3;

	/** Papel respons�vel por gerenciar o cadastramento dos discentes aprovados no vestibular e gerar as novas convoca��es. */
	public static final int GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS = SigaaSubsistemas.VESTIBULAR.getId() + 4;
	
	
	/********************************************************
	 * M�DULO ASSIST�NCIA AO ESTUDANTE:
	 *********************************************************/

	/** Habilita o M�dulo Assist�ncia ao Estudante. */
	public static final int SAE = SigaaSubsistemas.SAE.getId() + 1;
	
	/** Papel representativo do Coordenador do M�dulo Assist�ncia ao Estudante, acesso a tudo. */
	public static final int SAE_COORDENADOR = SigaaSubsistemas.SAE.getId() + 2;
	
	/** Usado pelo pessoal do R.U. (app desktop). */
	public static final int CONTROLADOR_ACESSO_RESTAURANTE = SigaaSubsistemas.SAE.getId() + 3;
	
	/** Indica quem vai poder definir os dias que os discentes ter�o direito a acessar o R.U.*/
	public static final int SAE_DEFINIR_DIAS_ALIMENTACAO = SigaaSubsistemas.SAE.getId() + 5;
	
	/** Permite o usu�rio consultar as ades�es que o discente fez ao cadastro �nico. */
	public static final int SAE_VISUALIZAR_CADASTRO_UNICO = SigaaSubsistemas.SAE.getId() + 6;
	
	/** Permite o usu�rio acessar o ranking de pontua��o dos discentes. */
	public static final int SAE_VISUALIZAR_RANKING = SigaaSubsistemas.SAE.getId() + 7;

	/** Permite o usu�rio gerenciar os cart�es de benef�cio. */
	public static final int SAE_GESTOR_CARTAO_ALIMENTACAO = SigaaSubsistemas.SAE.getId() + 8;
	
	/** Permite o usu�rio gere relat�rios de acesso ao RU. */
	public static final int SAE_VISUALIZA_ACESSO_RU = SigaaSubsistemas.SAE.getId() + 9;
	
	/*********************************************************
	 * SISTEMA DE CADASTRO DE PESSOAS:
	 *********************************************************/
	
	/** Papel representativo do Gestor de Cadastro de Identifica��o de Pessoas. */
	public static final int GESTOR_CADASTRO_IDENTIFICACAO_PESSOA = SigaaSubsistemas.SAE.getId() + 4;


	/*********************************************************
	 * M�DULO AMBIENTES VIRTUAIS:
	 *********************************************************/
	
	/** Papel representativo do M�dulo Ambientes Virtuais. */
	public static final int AMBIENTE_VIRTUAL = SigaaSubsistemas.AMBIENTES_VIRTUAIS.getId() + 1;
	
	/** Papel representativo do Gestor do M�dulo Ambientes Virtuais. */
	public static final int GESTOR_AMBIENTES_VIRTUAIS = SigaaSubsistemas.AMBIENTES_VIRTUAIS.getId() + 2;


	/********************************************************
	 * SISTEMA DE BIBLIOTECAS DA UFRN:
	 *********************************************************/

	/** Papel representativo para acessar as opera��es gerais da cataloga��o  (na maioria das vezes s� visualizar as informa��es da cataloga��o). */
	public static final int BIBLIOTECA_SETOR_CATALOGACAO = SigaaSubsistemas.BIBLIOTECA.getId() + 1;
	
	/** Papel representativo para acessar todas as opera��es de cataloga��o (pode catalogar). */
	public static final int BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO = SigaaSubsistemas.BIBLIOTECA.getId() + 2;
	
	/** Papel representativo para catalogar exemplares sem tombamento. Uma maneira de restringir a cataloga��o sem tombamento. */
	public static final int BIBLIOTECA_SETOR_CATALOGACAO_SEM_TOMBAMENTO = SigaaSubsistemas.BIBLIOTECA.getId() + 3;
	
	/** Papel que d� permiss�o aos usu�rio de inclu�rem e alterar as informa��es de materiais do sistema. N�o � permitido a esse papel alterar
	 * os dados das cataloga��es.
	 */
	public static final int BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS = SigaaSubsistemas.BIBLIOTECA.getId() + 4;
	

	
	/** Permite acessar as opera��es gerais da circula��o. */
	public static final int BIBLIOTECA_SETOR_CIRCULACAO = SigaaSubsistemas.BIBLIOTECA.getId() + 11;
	
	/** Permite ao usu�rio al�m das opera��es b�sicas, realizar algumas opera��es especiais dentro de circula��o.*/
	public static final int BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO = SigaaSubsistemas.BIBLIOTECA.getId() + 12;
	
	/** Papel espec�fico para quem trabalha apenas fazendo o checkout na biblioteca. */
	public static final int BIBLIOTECA_SETOR_CIRCULACAO_CHECKOUT = SigaaSubsistemas.BIBLIOTECA.getId() + 13;

	/** Papel espec�fico para quem precisa etimir do documento de quita��o do usu�rio. A aba de circula��o vai est� ativada apenas com essa op��o */
	public static final int BIBLIOTECA_EMITE_DECLARACAO_QUITACAO =  SigaaSubsistemas.BIBLIOTECA.getId() + 14;
	
	
	
	/** Papel quer permite ao usu�rio visualizar os dados do setor de aquisi��o. */
	public static final int BIBLIOTECA_SETOR_AQUISICAO = SigaaSubsistemas.BIBLIOTECA.getId() + 21;
	
	/** Papel quer permite ao usu�rio realizar todas as opera��es  do setor de aquisi��o.  */
	public static final int BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO = SigaaSubsistemas.BIBLIOTECA.getId() + 22;

	
	
	/** Pode visualizar os dados do setor de informa��o e refer�ncia.  */
	public static final int BIBLIOTECA_SETOR_INFO_E_REF = SigaaSubsistemas.BIBLIOTECA.getId() + 31;
	
	/** Pode realizar todas as opera��es dentro do setor de informa��o e refer�ncia.  */
	public static final int BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO = SigaaSubsistemas.BIBLIOTECA.getId() + 32;

	
	
	/** Pode ver os relat�rios do sistema de bibliotecas. */
	public static final int BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO = SigaaSubsistemas.BIBLIOTECA.getId() + 41;
	
	
	
	/** Papel representativo para realizar opera��es que n�o s�o de um setor espec�fico para a sua biblioteca. */
	public static final int BIBLIOTECA_ADMINISTRADOR_LOCAL = SigaaSubsistemas.BIBLIOTECA.getId() + 51;

	/** Papel representativo para realizar opera��es que n�o s�o de um setor espec�fico em TODAS as biblioteca.  */
	public static final int BIBLIOTECA_ADMINISTRADOR_GERAL = SigaaSubsistemas.BIBLIOTECA.getId() + 52;

	/** Papel especial que realiza alguns configura��es no sistema, papel n�o atribuido a bibliotec�rios e sim ao setor de inform�tica . */
	public static final int BIBLIOTECA_ADMINISTRADOR_SISTEMA = SigaaSubsistemas.BIBLIOTECA.getId() + 53;
	
	
	
	/** Papel representativo para visualizar os dados dentro do setor de interc�mbio. */
	public static final int BIBLIOTECA_SETOR_INTERCAMBIO = SigaaSubsistemas.BIBLIOTECA.getId() + 61;
	
	/** Papel representativo para realizar todas as opera��es dentro do setor de interc�mbio. */
	public static final int BIBLIOTECA_SETOR_INTERCAMBIO_BIBLIOTECARIO = SigaaSubsistemas.BIBLIOTECA.getId() + 62;
	
	
	
	/** Papel representativo para opera��es com a BDTD. */
	public static final int BIBLIOTECA_GESTOR_BDTD = SigaaSubsistemas.BIBLIOTECA.getId() + 63;
	
	
	
	
	
	/********************************************************************
	 *  Outros pap�is relacionados �s funcionalidades das bibliotecas:
	 ********************************************************************/
	
	/** Grupo de pap�is que d�o permiss�o ao usu�rio de solicitar levantamento de infra-estrutura. */
	public static final int[] BIBLIOTECA_SOLICITAR_LEVANTAMENTO_INFRA = new int[] {
			CHEFE_DEPARTAMENTO, DIRETOR_CENTRO};
	
	
	/********************************************************
	 * INFRA-ESTRUTURA F�SICA:
	 *********************************************************/
	
	/** Papel representativo para gerenciar os espa�os f�sicos e as reservas para sua utiliza��o de qualquer espa�o f�sico da institu���o. */
	public static final int GESTOR_INFRA_ESTRUTURA_FISICA_GLOBAL = SigaaSubsistemas.INFRA_ESTRUTURA_FISICA.getId() + 1;
	
	/** Papel representativo para gerenciar os espa�os f�sicos e as reservas para a unidade onde tem permiss�o. */
	public static final int GESTOR_INFRA_ESTRUTURA_FISICA = SigaaSubsistemas.INFRA_ESTRUTURA_FISICA.getId() + 2;
	
	/** Papel representativo do Respons�vel das reservas para utiliza��o de um espa�o f�sico onde tem permiss�o. */
	public static final int RESPONSAVEL_RESERVA_ESPACO_FISICO = SigaaSubsistemas.INFRA_ESTRUTURA_FISICA.getId() + 3;

	
	/********************************************************
	 * PORTAL DE RELAT�RIOS:
	 *********************************************************/
	
	/** Papel representativo para visualizar os relat�rios do sistema. */
	public static final int PORTAL_RELATORIOS = SigaaSubsistemas.PORTAL_RELATORIOS.getId() + 1;
	
		
	/********************************************************
	 * M�DULO DIPLOMAS:
	 *********************************************************/
	
	/** Papel representativo para registrar diplomas de gradua��o. */
	public static final int GESTOR_DIPLOMAS_GRADUACAO = SigaaSubsistemas.REGISTRO_DIPLOMAS.getId() + 1;
	
	/** Papel representativo para registrar diplomas de stricto sensu. */
	public static final int GESTOR_DIPLOMAS_STRICTO = SigaaSubsistemas.REGISTRO_DIPLOMAS.getId() + 2;
	
	/** Papel representativo para registrar diplomas de lato sensu. */
	public static final int GESTOR_DIPLOMAS_LATO = SigaaSubsistemas.REGISTRO_DIPLOMAS.getId() + 3;
	
	/** Papel representativo para administrar o m�dulo de registro de diplomas. */
	public static final int ADMINISTRADOR_DIPLOMAS = SigaaSubsistemas.REGISTRO_DIPLOMAS.getId() + 4;
	
	/*********************************************************
	 * M�DULO DE CONV�NIOS DE EST�GIOS:
	 *********************************************************/
	
	/** Papel representativo do Gestor de conv�nios de est�gios da Pr� Reitoria de Planejamento e Coordena��o Geral. */
	public static final int GESTOR_CONVENIOS_ESTAGIO_PROPLAN = SigaaSubsistemas.CONVENIOS_ESTAGIO.getId() + 1;
	
	/** Papel representativo do Coordenador de Est�gios. */
	public static final int COORDENADOR_ESTAGIOS = SigaaSubsistemas.CONVENIOS_ESTAGIO.getId() + 2;
	
	
	/*********************************************************
	 * M�DULO DE NECESSIDADES EDUCACIONAIS ESPECIAIS - NEE:
	 *********************************************************/
	
	/** Papel representativo do Gestor de Necessidades Especiais. */
	public static final int GESTOR_NEE = SigaaSubsistemas.NEE.getId() + 1;
	
	
	/*********************************************************
	 * PORTAL DE EST�GIOS:
	 *********************************************************/

	/** Papel representativo do Portal Concedente de Est�gio. */
	public static final int PORTAL_CONCEDENTE_ESTAGIO = SigaaSubsistemas.PORTAL_CONCEDENTE_ESTAGIO.getId() + 1;
	
	
	/*********************************************************
	 * PROGRAMA DE ATUALIZA��O PEDAG�GICA:
	 *********************************************************/

	/** Papel representativo do Portal Concedente de Est�gio. */
	public static final int GESTOR_PAP = SigaaSubsistemas.PROGRAMA_ATUALIZACAO_PEDAGOGICA.getId() + 1;
	
	
	/*********************************************************
	 * M�DULO DE OUVIDORIA:
	 *********************************************************/
	
	/** Papel representativo do M�dulo de Ouvidoria. */
	public static final int OUVIDOR = SigaaSubsistemas.OUVIDORIA.getId() + 1;
	
	/** Papel representativo da secretaria da Ouvidoria. */
	public static final int SECRETARIO_OUVIDOR = SigaaSubsistemas.OUVIDORIA.getId() + 2;
	
	/** Papel tempor�rio representativo de uma pessoa designada a responder uma manifesta��o atrav�s do m�dulo de ouvidoria. */
	public static final int DESIGNADO_OUVIDORIA = SigaaSubsistemas.OUVIDORIA.getId() + 3;
	
	
	
	/******************************************
	 * M�DULO M�DIO:
	 *****************************************/
	/** Papel representativo do Gestor do Ensino M�dio. */
	public static final int GESTOR_MEDIO = SigaaSubsistemas.MEDIO.getId() + 1;

	/** Papel representativo do Coordenador de Ensino m�dio. */
	public static final int COORDENADOR_MEDIO = SigaaSubsistemas.MEDIO.getId() + 2;
	
	/** Papel representativo da secretaria de Ensino m�dio. */
	public static final int SECRETARIA_MEDIO = SigaaSubsistemas.MEDIO.getId() + 3;
	
	/** Papel tempor�rio representativo do familiar de Ensino m�dio. */
	public static final int FAMILIAR_MEDIO = SigaaSubsistemas.MEDIO.getId() + 4;
	
	/** Papel representativo para permitir consultas gerais no Ensino m�dio. */
	public static final int PEDAGOGICO_MEDIO = SigaaSubsistemas.MEDIO.getId() + 5;
	
	
	/******************************************
	 * M�DULO TRADU��O DE DOCUMENTOS:
	 *****************************************/
	/** Papel representativo do Gestor do m�dulo de Tradu��o de Documentos. */
	public static final int GESTOR_TRADUCAO_DOCUMENTOS = SigaaSubsistemas.RELACOES_INTERNACIONAIS.getId() + 1;
	
	/** Papel representativo do tradutor de dados acad�micos. */
	public static final int TRADUTOR_DADOS_ACADEMICOS = SigaaSubsistemas.RELACOES_INTERNACIONAIS.getId() + 2;
	
	
	/******************************************
	 * M�DULO DO METR�POLE DIGITAL 
	 *****************************************/
	
	/**Papel representativo do Gestor do m�dulo do Instituto Metr�pole Digital **/
	public static final int GESTOR_METROPOLE_DIGITAL = SigaaSubsistemas.METROPOLE_DIGITAL.getId() + 1;  // 11601
	
	/**Papel representativo ao Tutor do IMD **/
	public static final int TUTOR_IMD = SigaaSubsistemas.METROPOLE_DIGITAL.getId() + 2; // 11602
	
	/**Papel representativo ao coordenador de tutores do IMD **/
	public static final int COORDENADOR_TUTOR_IMD = SigaaSubsistemas.METROPOLE_DIGITAL.getId() + 3; // 11603
	
	/**Papel representativo ao coordenador de p�lo do IMD **/
	public static final int COORDENADOR_POLO_IMD = SigaaSubsistemas.METROPOLE_DIGITAL.getId() + 4; // 11604
	
	/**Papel representativo ao Assistente Social do IMD **/
	public static final int ASSISTENTE_SOCIAL_IMD = SigaaSubsistemas.METROPOLE_DIGITAL.getId() + 5; // 11605
	
	/** Papel representativo para permitir que fa�a convoca��o do processo seletivo do IMD. */
	public static final int GESTOR_CONVOCACAO_PROCESSO_SELETIVO_IMD = SigaaSubsistemas.METROPOLE_DIGITAL.getId() + 8; //11608 
	
	/** Papel representativo para permitir que fa�a cadastramento do processo seletivo do IMD. */
	public static final int GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_IMD = SigaaSubsistemas.METROPOLE_DIGITAL.getId() + 9; //11609
	

	/******************************************
	 * M�DULO DO ENSINO EM REDE
	 *****************************************/
	
	public static final int COORDENADOR_GERAL_REDE = SigaaSubsistemas.ENSINO_REDE.getId() + 1;  // 30001
	// Papel tempor�rio
	public static final int COORDENADOR_UNIDADE_REDE = SigaaSubsistemas.ENSINO_REDE.getId() + 2;  // 30001
		
}