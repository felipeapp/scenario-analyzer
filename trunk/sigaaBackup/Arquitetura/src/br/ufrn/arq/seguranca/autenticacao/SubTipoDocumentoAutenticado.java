/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 17/12/2008
 */
package br.ufrn.arq.seguranca.autenticacao;

/**
 * Um tipo de documento autenticado é mais geral, tais como: DECLARACAO.
 * 
 * Um sub-tipo pode ser DECLARACAO_EXTENSAO, DECLARACAO_
 * 
 * A constante deve ter quatro dígitos, sendo o primeiro dígito o número do sistema.
 * Exemplo: o primeiro SubTipo do SIGAA pode ser o 2001.
 * 
 * @author Gleydson Lima
 */
public class SubTipoDocumentoAutenticado {

	// Constantes para documentos do SIPAC (1001 a 1999)
	
	// Constantes para documentos do SIGAA (2001 a 2999)
	public static final int DECLARACAO_PARTICIPANTE_MEMBRO_EQUIPE_EXTENSAO 	= 2001; // 601;
	public static final int DECLARACAO_DISCENTE_MONITORIA 					= 2002; // 602;
	public static final int DECLARACAO_PARTICIPANTE_PUBLICO_ALVO_EXTENSAO 	= 2003; // 603;
	public static final int DECLARACAO_DOCENTE_MONITORIA 					= 2017; 
	
	public static final int DECLARACAO_BOLSISTA_PESQUISA 					= 2004; // 501;
	public static final int DECLARACAO_QUITACAO_BIBLIOTECA 					= 2005; // 502;
	public static final int DECLARACAO_VINCULO_INSTITUICAO 					= 2006; // 503;
	public static final int DECLARACAO_DISCIPLINA_MINISTRADA 				= 2007; // 504;
	public static final int DECLARACAO_GRUPO_PESQUISA        				= 2023; 
	
	public static final int CERTIFICADO_PARTICIPANTE_SID 					= 2008; // 701;
	public static final int CERTIFICADO_PARTICIPANTE_CIC 					= 2009; // 702;
	public static final int CERTIFICADO_PARTICIPANTE_MEMBRO_EQUIPE_EXTENSAO = 2010; // 703;
	public static final int CERTIFICADO_PARTICIPANTE_AVALIADOR_CIC 			= 2011; // 704;
	public static final int CERTIFICADO_PARTICIPANTE_MONITORIA 				= 2012; // 705;
	public static final int CERTIFICADO_PARTICIPANTE_PUBLICO_ALVO_EXTENSAO 	= 2013; // 712;
	public static final int CERTIFICADO_AVALIADOR_EXTENSAO 					= 2014;	// 713;
	public static final int CERTIFICADO_INSCRICAO 							= 2015;
	public static final int CERTIFICADO_DISCENTE_EXTENSAO					= 2016;
	
	public static final int TERMO_PUBLICACAO_TESE_DISSERTACAO				= 2017;
	
	public static final int TERMO_COMPROMISSO_ESTAGIO        				= 2018;
	public static final int TERMO_RESCISAO_ESTAGIO        				    = 2019;
	
	public static final int DECLARACAO_PRAZO_MAXIMO_INTEGRALIZACAO_CURRICULO= 2020;
	
	public static final int CERTIFICADO_PARTICIPANTE_ATUALIZACAO_PEDAGOGICA	= 2021;
	
	public static final int DECLARACAO_DOCENTE_COORDENADOR_PROJETO_PESQUISA = 2022;
	
	public static final int SOLICITACAO_TRANCAMENTO_PROGRAMA				= 2024;
	
	public static final int DECLARACAO_AVALIADOR_ASSOCIADOS					= 2025;
	public static final int DECLARACAO_MEMBROS_ASSOCIADOS					= 2026;
	public static final int DECLARACAO_PARTICIPANTE_MEMBRO_ASSOCIADOS 	    = 2027;
	
	/** Para os certificados emitido pelo SIG Eventos */
	public static final int CERTIFICADO_SIG_EVENTOS					        = 2028;
	
	// Constantes para documentos do SIGPRH (7001 a 7999) 
	public static final int DECLARACAO_AFASTAMENTO 							= 7001; // 706;
	public static final int DECLARACAO_AVERBACAO 							= 7002; // 707;
	public static final int DECLARACAO_DEPENDENTES 							= 7003; // 708;
	public static final int DECLARACAO_ULTIMA_PROGRESSAO 					= 7004; // 709;
	public static final int DECLARACAO_FUNCIONAL 							= 7005; // 710;
	public static final int TERMO_AFASTAMENTO 								= 7006; // 711;
	public static final int DECLARACAO_PENSIONISTA_ESPECIAL					= 7007; // 712;
	public static final int DECLARACAO_ISENCAO_CONTRIBUICAO					= 7008; // 712;
	public static final int DECLARACAO_ATIVIDADES_EM_CONDICOES_ESPECIAIS	= 7009;
	public static final int DECLARACAO_RJU									= 7010; 
	public static final int CERTIDAO_TEMPO_CONTRIBUICAO						= 7011;
	public static final int DECLARACAO_TEMPO_SERVICO						= 7012;
	public static final int CERTIFICADO_CURSO_CAPACITACAO_SERVIDOR			= 7013;
}	
