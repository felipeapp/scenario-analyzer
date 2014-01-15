/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/12/2008
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 *
 * Representa todas as situa��es poss�veis de um Projeto de
 * Extens�o, Pesquisa ou de Monitoria.
 * Devem conter os mesmos valores da tabela public.tipo_situacao_projeto
 *
 * @author ilueny santos
 *
 */
@Entity
@Table(name = "tipo_situacao_projeto", schema = "projetos", uniqueConstraints = {})
public class TipoSituacaoProjeto implements PersistDB {


	/**Constantes que representam o status dos projetos de Monitoria */	
	/** Ap�s submiss�o (aguardando parecer dos departamentos). */
	public static final int MON_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS			= 1;
	
	/** Aguardando a prograd distribuir pra comiss�o de monitoria. */
	public static final int MON_AGUARDANDO_DISTRIBUICAO_DO_PROJETO				= 2;  
	
	/** Comiss�o de monitoria avaliando. */
	public static final int MON_AGUARDANDO_AVALIACAO	 						= 3; 
	
	/** Aprovado pela comiss�o. */
	public static final int MON_RECOMENDADO				 						= 4;
	
	/** Reprovado pela comiss�o de monitoria. */
	public static final int MON_NAO_RECOMENDADO			 						= 5;
	
	/** Solicitou reconsidera��o ap�s a avalia��o dos membros da comiss�o de monitoria. */
	public static final int MON_SOLICITOU_RECONSIDERACAO_AVALIACAO				= 6;
	
	/** Projeto finalizado. */
	public static final int MON_CONCLUIDO				 						= 7;
	
	/** Cadastro n�o conclu�do, falta enviar pra prograd. */
	public static final int MON_CADASTRO_EM_ANDAMENTO		 					= 8;
	
	/** Cadastro conclu�do e n�o aprovado por algum departamento envolvido. */
	public static final int MON_NAO_AUTORIZADO_PELOS_DEPARTAMENTOS_ENVOLVIDOS	= 9;
	
	/** Aprovado pela comiss�o e est� em execu��o, faltando envio de relat�rios pra concluir ou renovar. */
	public static final int MON_EM_EXECUCAO			 							= 10;
	
	/** Comiss�o de monitoria avaliou o relat�rio final/parcial e N�O autorizou a renova��o do projeto (coordenador s� poder� fazer novo projeto depois de um per�odo x). */
	public static final int MON_NAO_RENOVADO_PELA_COMISSAO_MONITORIA			= 11;
	
	/** Os dois avaliadores deram notas muito diferentes ao projeto ele ser� redistribu�do para um membro da prograd avaliar. <br/>
	 * Agora um c�lculo � realizado para verificar se o projeto foi avaliado com discrep�ncia de notas.
	 */
	@Deprecated
	public static final int MON_AVALIADO_COM_DISCREPANCIA_DE_NOTAS				= 12;
	
	/** Ap�s avalia��o do relat�rio parcial do projeto, o mesmo foi renovado. */
	public static final int MON_RENOVADO_SEM_ALTERACOES							= 13;
	
	/** Reconsidera��o para an�lise de preenchimento de requisitos formais. */
	public static final int MON_SOLICITOU_RECONSIDERACAO_CADASTRO				= 14;
	
	/** Prograd analisando o pedido de reconsidera��o. */
	public static final int MON_ANALISANDO_SOLICITACAO_DE_RECONSIDERACAO		= 15;
	
	/** Cancelado. */
	public static final int MON_CANCELADO										= 16;
	
	/** Projetos cadastrados e n�o enviados a prograd. */
	public static final int MON_PRAZO_DE_SUBMISSAO_FINALIZADO					= 17; 
	
	/** Relat�rio parcial/final enviado para prograd, aguardando a distribui��o para an�lise do relat�rio pela comiss�o de monitoria. */
	public static final int MON_AGUARDANDO_DISTRIBUICAO_DO_RELATORIO			= 18; 
	
	/** Comiss�o de monitoria avaliou o relat�rio final/parcial e autorizou a renova��o do projeto mas o coordenador n�o quer renovar o mesmo (coordenador n�o � punido). */
	public static final int MON_NAO_RENOVADO_PELA_COORDENACAO_DO_PROJETO		= 19;
	
	/** O projeto estava em andamento e ficou sem nenhum monitor ativo, ficar� suspenso ate que o um novo monitor entre. */
	public static final int MON_SUSPENSO_POR_NAO_TER_MONITOR_ATIVO				= 20;
	
	/** Projeto removido pela prograd...*/
	public static final int MON_REMOVIDO										= 21; 
	
	/** Ap�s avalia��o do relat�rio parcial do projeto, o mesmo foi renovado com sugest�o de redu��o de bolsas.*/
	public static final int MON_RENOVADO_COM_REDUCAO_BOLSAS						= 22;
	
	/**Chefe do departamento que autoriza a proposta devolve-a para o coordenador para edi��o.*/
	public static final int MONITORIA_PROPOSTA_DEVOLVIDA_PARA_COORDENADOR		= 23;
	
	/** Ap�s a avalia��o dos avaliadores escilhidos na distribui��o de projetos. */
	public static final int MON_AVALIADO										= 24; 

	/** Reconsidera��o Aprovada. O projeto ser� reavaliado. */
	public static final int MON_RECONSIDERACAO_APROVADA							= 25; 

	/** Reconsidera��o Reprovada. O projeto permanace com a situa�� anterior � solicita��o de reconsidera��o. */
	public static final int MON_RECONSIDERACAO_NAO_APROVADA						= 26; 

	
	/** Grupo de situa��es poss�veis para um projeto de monitoria ativo. */
	public static final Integer[] MON_GRUPO_ATIVO	= {MON_EM_EXECUCAO, MON_RENOVADO_COM_REDUCAO_BOLSAS,MON_RENOVADO_SEM_ALTERACOES, MON_AGUARDANDO_DISTRIBUICAO_DO_RELATORIO, MON_CONCLUIDO};
	
	/** Grupo de situa��es poss�veis para um projeto de monitoria v�lido. */
	public static final Integer[] MON_GRUPO_VALIDO	= {MON_EM_EXECUCAO, MON_RENOVADO_COM_REDUCAO_BOLSAS,MON_RENOVADO_SEM_ALTERACOES, MON_AGUARDANDO_DISTRIBUICAO_DO_RELATORIO, MON_CONCLUIDO, MON_RECOMENDADO};

	/** Grupo de situa��es poss�veis para um projeto de monitoria  inv�lido. */
	public static final Integer[] MON_GRUPO_INVALIDO= {MON_NAO_RECOMENDADO, MON_NAO_AUTORIZADO_PELOS_DEPARTAMENTOS_ENVOLVIDOS,MON_CANCELADO, MON_REMOVIDO};
	
	
	

	/**Constantes que representam o status dos projetos de Extens�o */
	
	/** A��o de extens�o foi gravada no banco e pode ser conclu�da depois. */
	public static final int EXTENSAO_CADASTRO_EM_ANDAMENTO					= 101; 
	
	/** A��o de extens�o autorizada e submetida � Pr�-Reitoria. Ser� analisada por membros do comit� de extens�o. */
	public static final int EXTENSAO_SUBMETIDO								= 102;
	
	/** A��o aprovada pelo comit� de extens�o. Coordenador aceitou a execu��o da a��o. */
	public static final int EXTENSAO_EM_EXECUCAO							= 103;
	
	/** A��o reprovada pelo comit� de extens�o. */	
	public static final int EXTENSAO_REPROVADO								= 104; 
	
	/** A��o de extens�o foi conclu�da. */
	public static final int EXTENSAO_CONCLUIDO								= 105;
	
	/** A��o foi enviada para autoriza��o dos departamentos de todos os docentes envolvidos. */
	public static final int EXTENSAO_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS	= 106; 
	
	/** A��o n�o foi autorizada por um ou mais departamentos dos docentes envolvidos. */
	public static final int EXTENSAO_NAO_AUTORIZADO_DEPARTAMENTOS			= 107;
	
	/** A��o removida do sistema pela Pr�-Reitoria de Extens�o ou pelo docente. */
	public static final int EXTENSAO_REMOVIDO								= 108;
	
	/** Avaliadores externos e membros do comit� avaliando a proposta. */
	public static final int EXTENSAO_AGUARDANDO_AVALIACAO	 				= 109;
	
	/** Registro aprovado por membro da Pr�-Reitoria de Extens�o. */
	public static final int EXTENSAO_REGISTRO_APROVADO		 				= 110;
	
	/** Registro reprovado por membro da Pr�-Reitoria de Extens�o. */
	public static final int EXTENSAO_REGISTRO_REPROVADO		 				= 111; 
	
	/** Chefe do departamento que autoriza a proposta devolve-a para o coordenador para edi��o. */
	public static final int EXTENSAO_PROPOSTA_DEVOLVIDA_PARA_COORDENADOR	= 112; 
	
	/** Proex devolve proposta para o chefe reavaliar (houve um equivoco na avalia��o do projeto pelo chefe do departamento.). */
	public static final int EXTENSAO_PROPOSTA_DEVOLVIDA_PARA_DEPARTAMENTO	= 113; 
	
	/** Projeto foi reprovado no processo de avalia��o normal, o coordenador solicitou reconsidera��o da avalia��o. */
	public static final int EXTENSAO_ANALISANDO_SOLICITACAO_RECONSIDERACAO	= 114;
	
	/** Reconsidera��o aprovada, proposta de a��o foi aberto para reedi��o. */
	public static final int EXTENSAO_RECONSIDERACAO_APROVADA				= 115;
	
	/** Reconsidera��o foi reprovada, proposta n�o aceita definitivamente. */
	public static final int EXTENSAO_RECONSIDERACAO_NAO_APROVADA			= 116; 

	/** Projeto encerrado com pend�ncias. ex. pend�ncia de relat�rio final. */
	public static final int EXTENSAO_PENDENTE_DE_RELATORIO			  		= 117; 
	
	/** Grupo de que representa a��es de extens�o ativas. */
	public static final Integer[] EXTENSAO_GRUPO_ATIVO = { EXTENSAO_EM_EXECUCAO };
    
	/** Grupo de que representa a��es de extens�o que n�o podem gerenciar participantes. */
	public static final Integer[] EXTENSAO_GRUPO_NAO_PERMITIR_VISUALIZAR_PARTICIPANTES = {
	    EXTENSAO_AGUARDANDO_AVALIACAO, EXTENSAO_CADASTRO_EM_ANDAMENTO,
	    EXTENSAO_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS,
	    EXTENSAO_PROPOSTA_DEVOLVIDA_PARA_COORDENADOR,
	    EXTENSAO_PROPOSTA_DEVOLVIDA_PARA_DEPARTAMENTO, EXTENSAO_SUBMETIDO,
	    EXTENSAO_ANALISANDO_SOLICITACAO_RECONSIDERACAO };
    
	/** Grupo de que representa a��es de extens�o v�lidas. */
	public static final Integer[] EXTENSAO_GRUPO_VALIDO = {
	    EXTENSAO_CADASTRO_EM_ANDAMENTO, EXTENSAO_SUBMETIDO,
	    EXTENSAO_EM_EXECUCAO, EXTENSAO_CONCLUIDO,
	    EXTENSAO_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS,
	    EXTENSAO_AGUARDANDO_AVALIACAO,
	    EXTENSAO_PROPOSTA_DEVOLVIDA_PARA_COORDENADOR,
	    EXTENSAO_REGISTRO_APROVADO,
	    EXTENSAO_PROPOSTA_DEVOLVIDA_PARA_DEPARTAMENTO,
	    EXTENSAO_ANALISANDO_SOLICITACAO_RECONSIDERACAO,
	    EXTENSAO_RECONSIDERACAO_APROVADA };
    
	/** Grupo de que representa a��es de extens�o inv�lidas. */
	public static final Integer[] EXTENSAO_GRUPO_INVALIDO = {
	    EXTENSAO_REPROVADO, EXTENSAO_NAO_AUTORIZADO_DEPARTAMENTOS,
	    EXTENSAO_REMOVIDO, EXTENSAO_REGISTRO_REPROVADO};	

	
	
	/** Constantes que representam o status dos projetos de Pesquisa */
	// Status iniciais
	/** Cadastro do projeto em andamento. */
	public static final int  GRAVADO						= 200;
	/** Projeto submetido. Pronto para distribuir para avalia��o. Apenas para projetos Internos. */
	public static final int  SUBMETIDO 						= 201; 
	
	// Status intermedi�rios
	/** Projeto cadastrado. Apenas para projetos externos. */
	public static final int  CADASTRADO						= 210;
	/** Projeto distribu�do automaticamente pelo sistema. */
	public static final int  DISTRIBUIDO_AUTOMATICAMENTE 	= 211;
	/** Projeto avalia��o insuficiente. */
	public static final int  AVALIACAO_INSUFICIENTE		 	= 212;
	/** Projeto distribu�do manualmente. */
	public static final int  DISTRIBUIDO_MANUALMENTE 		= 213;
	/** Projeto aprovado, utilizado somente para o hist�rico. */
	public static final int  APROVADO						= 214;
	/** Projeto em execu��o. */
	public static final int  EM_ANDAMENTO					= 215;
	
	// Status finais
	/** Projeto renovado. */
	public static final int  RENOVADO						= 221;
	/** Projeto finalizado. */
	public static final int  FINALIZADO						= 222;
	/** Projeto reprovado. */
	public static final int  REPROVADO						= 223;
	/** Projeto desativado. */
	public static final int  DESATIVADO						= 224;
	/** Projeto removido do sistema. */
	public static final int  EXCLUIDO						= 225;
	/** Projeto encerrado com pend�ncias. ex. pend�ncia de relat�rio final. */
	public static final int  PENDENTE_DE_RELATORIO_FINAL    = 226;

	/** Grupo de situa��es de projetos de pesquisa v�lidos. */
	public static final Integer[] PESQ_GRUPO_VALIDO	= {EM_ANDAMENTO, RENOVADO, FINALIZADO};

	
	/** Constantes que representam o status dos projetos de a��o acad�mica associada (PROJETO BASE) */
	/** Proposta de projeto foi gravada no banco e pode ser conclu�da depois. */
	public static final int PROJETO_BASE_CADASTRO_EM_ANDAMENTO			= 301;
	
	/** Proposta autorizada e submetida � aprova��o. ser� analisada. */
	public static final int PROJETO_BASE_SUBMETIDO					= 302;
	 
	/** Projeto base aprovado e em execu��o. */
	public static final int PROJETO_BASE_EM_EXECUCAO				= 303;
	
	/** Projeto reprovado pelos avaliadores. */
	public static final int PROJETO_BASE_REPROVADO					= 304; 
	
	/** Projeto conclu�do. */
	public static final int PROJETO_BASE_CONCLUIDO					= 305;	
	
	/** Projeto removido do sistema. */
	public static final int PROJETO_BASE_REMOVIDO					= 306; 
	
	/** Projeto em fase de avalia��o. */
	public static final int PROJETO_BASE_AGUARDANDO_AVALIACAO			= 307; 
	
	/** Projeto aprovado com recursos para execu��o. */
	public static final int PROJETO_BASE_APROVADO_COM_RECURSOS			= 308; 
	
	/** Projeto aprovado sem recursos para execu��o. */
	public static final int PROJETO_BASE_APROVADO_SEM_RECURSOS			= 309; 
	
	/** Projeto n�o concordou com avalia��o do comit� e solicitou reconsidera��o. */
	public static final int PROJETO_BASE_ANALISANDO_PEDIDO_RECONSIDERACAO		= 310;
	
	/** Projeto n�o concordou com avalia��o do comit� que teve a reconsidera��o aprovada. */
	public static final int PROJETO_BASE_PEDIDO_RECONSIDERACAO_APROVADO		= 311; 
	
	/** Projeto n�o concordou com avalia��o do comit� que teve a reconsidera��o Negada. */
	public static final int PROJETO_BASE_PEDIDO_RECONSIDERACAO_NEGADO		= 312; 
	
	/** Coordena��o do projeto negou a execu��o do projeto.(Somente para acompanhamento no hist�rico). */
	public static final int PROJETO_BASE_COORDENACAO_NEGOU_EXECUCAO			= 313; 
	
	/** Encerrado ap�s dias estabelecidos pela Pr�-reitoria de Extens�o para o envio da proposta de projeto. */
	public static final int PROJETO_BASE_ENCERRADO_COM_PENDENCIAS			= 314; 
	
	/** Coordena��o do projeto aceitou a execu��o do projeto.(Somente para acompanhamento no hist�rico). */
	public static final int PROJETO_BASE_COORDENACAO_ACEITOU_EXECUCAO		= 315;
	
	/** Situa��o normalmente utilizada quando o coordenador nega a execu��o do projeto. */
	public static final int PROJETO_BASE_CANCELADO					= 316; 
	
	/** Quando o coordenador envia o projeto para aprova��o dos departamentos envolvidos. */
	public static final int PROJETO_BASE_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS	= 317;
	
	/** Tempo de cadastro expirado ap�s dias estabelecidos pela proex para o envio da proposta.	 */
	public static final int PROJETO_BASE_TEMPO_DE_CADASTRO_EXPIRADO			= 318; 
	
	/** Indica que a avalia��o do projeto foi conclu�da. O pr�ximo passo seria definir se o projeto foi aprovado, se receber� rescursos ou n�o. */
	public static final int PROJETO_BASE_AVALIADO						= 319;
	
	/** Indica que o projeto foi registrado, mas aguarda a conclus�o do processo junto outros departamentos da IFES, normalmente est� relacionado a projetos com conv�nio. */
	public static final int PROJETO_BASE_REGISTRADO						= 320; 

	/** Lista de situa��es para a��es acad�micas associadas que podem solicitar reconsidera��o. */
	public static final Integer[] PROJETO_BASE_GRUPO_SOLICITAR_RECONSIDERACAO	= {PROJETO_BASE_APROVADO_COM_RECURSOS, 
	    											PROJETO_BASE_APROVADO_SEM_RECURSOS, MON_NAO_RECOMENDADO, REPROVADO, EXTENSAO_REPROVADO, PROJETO_BASE_REPROVADO};
	
	/** Lista de situa��es para a��es acad�micas associadas inv�lidas */
	public static final Integer[] PROJETO_BASE_GRUPO_INVALIDO	= {PROJETO_BASE_REPROVADO, PROJETO_BASE_REMOVIDO, PROJETO_BASE_COORDENACAO_NEGOU_EXECUCAO, PROJETO_BASE_CANCELADO};
	
	//Grupos comuns a ensino(monitoria), pesquisa e extens�o.
	/** Situa��es comuns para projetos em execu��o de ensino, pesquisa e extens�o. */
	public static final Integer[] PROJETOS_GRUPO_EM_EXECUCAO = { MON_EM_EXECUCAO, EM_ANDAMENTO, EXTENSAO_EM_EXECUCAO, PROJETO_BASE_EM_EXECUCAO, EXTENSAO_PENDENTE_DE_RELATORIO };
	/** Situa��es comuns para projetos conclu�dos de ensino, pesquisa e extens�o. */
	public static final Integer[] PROJETOS_GRUPO_CONCLUIDO = { MON_CONCLUIDO, FINALIZADO, EXTENSAO_CONCLUIDO, PROJETO_BASE_CONCLUIDO};
	/** Situa��es comuns para projetos reprovados de ensino, pesquisa e extens�o. */
	public static final Integer[] PROJETOS_GRUPO_REPROVADO = { MON_NAO_RECOMENDADO, REPROVADO, EXTENSAO_REPROVADO, PROJETO_BASE_REPROVADO};
	
	/** Grupos v�lidos na busca dos projetos de Extens�o no RID. */ 
	public static final Integer[] SITUACOES_VALIDAS_RID = {
		EXTENSAO_EM_EXECUCAO, EXTENSAO_CONCLUIDO, EXTENSAO_REGISTRO_APROVADO, 
		PROJETO_BASE_EM_EXECUCAO, PROJETO_BASE_CONCLUIDO};	
	
	/** Situa��es v�lidas para a��es acad�micas associadas.*/
	public static final Integer[] SITUACOES_VALIDAS_ASSOCIADOS = { TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO, TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO };
	
	// Fields
	/** Chave prim�ria.*/
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_tipo_situacao_projeto")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;

	/** Descri��o do tipo de situa��o. */
	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 100)
	private String descricao;

	/**
	 * Discrimina o contexto da situa��o em quest�o, se � de a situa��o � referente a pesquisa, monitoria ou extens�o.
	 * os valores poss�veis s�o:
	 * M - monitoria;
	 * P - pesquisa
	 * E - extens�o.
	 */
	@Column(name = "contexto")
	private Character contexto;

	// Constructors

	/** default constructor */
	public TipoSituacaoProjeto() {
	}

	public TipoSituacaoProjeto(int situacao) {
		id = situacao;
	}

	public TipoSituacaoProjeto(int situacao, String descricao) {
		this.id = situacao;
		this.descricao = descricao;
	}

	// Property accessors
	public int getId() {
		return id;
	}

	public void setId(int idTipoSituacaoProjeto) {
		id = idTipoSituacaoProjeto;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Character getContexto() {
		return contexto;
	}

	public void setContexto(Character contexto) {
		this.contexto = contexto;
	}

	/**
	 * Retorna a descri��o da situa��o.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDescricao();
	}

}
