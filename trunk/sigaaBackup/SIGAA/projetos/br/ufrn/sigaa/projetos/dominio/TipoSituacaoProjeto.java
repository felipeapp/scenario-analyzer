/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Representa todas as situações possíveis de um Projeto de
 * Extensão, Pesquisa ou de Monitoria.
 * Devem conter os mesmos valores da tabela public.tipo_situacao_projeto
 *
 * @author ilueny santos
 *
 */
@Entity
@Table(name = "tipo_situacao_projeto", schema = "projetos", uniqueConstraints = {})
public class TipoSituacaoProjeto implements PersistDB {


	/**Constantes que representam o status dos projetos de Monitoria */	
	/** Após submissão (aguardando parecer dos departamentos). */
	public static final int MON_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS			= 1;
	
	/** Aguardando a prograd distribuir pra comissão de monitoria. */
	public static final int MON_AGUARDANDO_DISTRIBUICAO_DO_PROJETO				= 2;  
	
	/** Comissão de monitoria avaliando. */
	public static final int MON_AGUARDANDO_AVALIACAO	 						= 3; 
	
	/** Aprovado pela comissão. */
	public static final int MON_RECOMENDADO				 						= 4;
	
	/** Reprovado pela comissão de monitoria. */
	public static final int MON_NAO_RECOMENDADO			 						= 5;
	
	/** Solicitou reconsideração após a avaliação dos membros da comissão de monitoria. */
	public static final int MON_SOLICITOU_RECONSIDERACAO_AVALIACAO				= 6;
	
	/** Projeto finalizado. */
	public static final int MON_CONCLUIDO				 						= 7;
	
	/** Cadastro não concluído, falta enviar pra prograd. */
	public static final int MON_CADASTRO_EM_ANDAMENTO		 					= 8;
	
	/** Cadastro concluído e não aprovado por algum departamento envolvido. */
	public static final int MON_NAO_AUTORIZADO_PELOS_DEPARTAMENTOS_ENVOLVIDOS	= 9;
	
	/** Aprovado pela comissão e está em execução, faltando envio de relatórios pra concluir ou renovar. */
	public static final int MON_EM_EXECUCAO			 							= 10;
	
	/** Comissão de monitoria avaliou o relatório final/parcial e NÃO autorizou a renovação do projeto (coordenador só poderá fazer novo projeto depois de um período x). */
	public static final int MON_NAO_RENOVADO_PELA_COMISSAO_MONITORIA			= 11;
	
	/** Os dois avaliadores deram notas muito diferentes ao projeto ele será redistribuído para um membro da prograd avaliar. <br/>
	 * Agora um cálculo é realizado para verificar se o projeto foi avaliado com discrepância de notas.
	 */
	@Deprecated
	public static final int MON_AVALIADO_COM_DISCREPANCIA_DE_NOTAS				= 12;
	
	/** Após avaliação do relatório parcial do projeto, o mesmo foi renovado. */
	public static final int MON_RENOVADO_SEM_ALTERACOES							= 13;
	
	/** Reconsideração para análise de preenchimento de requisitos formais. */
	public static final int MON_SOLICITOU_RECONSIDERACAO_CADASTRO				= 14;
	
	/** Prograd analisando o pedido de reconsideração. */
	public static final int MON_ANALISANDO_SOLICITACAO_DE_RECONSIDERACAO		= 15;
	
	/** Cancelado. */
	public static final int MON_CANCELADO										= 16;
	
	/** Projetos cadastrados e não enviados a prograd. */
	public static final int MON_PRAZO_DE_SUBMISSAO_FINALIZADO					= 17; 
	
	/** Relatório parcial/final enviado para prograd, aguardando a distribuição para análise do relatório pela comissão de monitoria. */
	public static final int MON_AGUARDANDO_DISTRIBUICAO_DO_RELATORIO			= 18; 
	
	/** Comissão de monitoria avaliou o relatório final/parcial e autorizou a renovação do projeto mas o coordenador não quer renovar o mesmo (coordenador não é punido). */
	public static final int MON_NAO_RENOVADO_PELA_COORDENACAO_DO_PROJETO		= 19;
	
	/** O projeto estava em andamento e ficou sem nenhum monitor ativo, ficará suspenso ate que o um novo monitor entre. */
	public static final int MON_SUSPENSO_POR_NAO_TER_MONITOR_ATIVO				= 20;
	
	/** Projeto removido pela prograd...*/
	public static final int MON_REMOVIDO										= 21; 
	
	/** Após avaliação do relatório parcial do projeto, o mesmo foi renovado com sugestão de redução de bolsas.*/
	public static final int MON_RENOVADO_COM_REDUCAO_BOLSAS						= 22;
	
	/**Chefe do departamento que autoriza a proposta devolve-a para o coordenador para edição.*/
	public static final int MONITORIA_PROPOSTA_DEVOLVIDA_PARA_COORDENADOR		= 23;
	
	/** Após a avaliação dos avaliadores escilhidos na distribuição de projetos. */
	public static final int MON_AVALIADO										= 24; 

	/** Reconsideração Aprovada. O projeto será reavaliado. */
	public static final int MON_RECONSIDERACAO_APROVADA							= 25; 

	/** Reconsideração Reprovada. O projeto permanace com a situaçã anterior à solicitação de reconsideração. */
	public static final int MON_RECONSIDERACAO_NAO_APROVADA						= 26; 

	
	/** Grupo de situações possíveis para um projeto de monitoria ativo. */
	public static final Integer[] MON_GRUPO_ATIVO	= {MON_EM_EXECUCAO, MON_RENOVADO_COM_REDUCAO_BOLSAS,MON_RENOVADO_SEM_ALTERACOES, MON_AGUARDANDO_DISTRIBUICAO_DO_RELATORIO, MON_CONCLUIDO};
	
	/** Grupo de situações possíveis para um projeto de monitoria válido. */
	public static final Integer[] MON_GRUPO_VALIDO	= {MON_EM_EXECUCAO, MON_RENOVADO_COM_REDUCAO_BOLSAS,MON_RENOVADO_SEM_ALTERACOES, MON_AGUARDANDO_DISTRIBUICAO_DO_RELATORIO, MON_CONCLUIDO, MON_RECOMENDADO};

	/** Grupo de situações possíveis para um projeto de monitoria  inválido. */
	public static final Integer[] MON_GRUPO_INVALIDO= {MON_NAO_RECOMENDADO, MON_NAO_AUTORIZADO_PELOS_DEPARTAMENTOS_ENVOLVIDOS,MON_CANCELADO, MON_REMOVIDO};
	
	
	

	/**Constantes que representam o status dos projetos de Extensão */
	
	/** Ação de extensão foi gravada no banco e pode ser concluída depois. */
	public static final int EXTENSAO_CADASTRO_EM_ANDAMENTO					= 101; 
	
	/** Ação de extensão autorizada e submetida à Pró-Reitoria. Será analisada por membros do comitê de extensão. */
	public static final int EXTENSAO_SUBMETIDO								= 102;
	
	/** Ação aprovada pelo comitê de extensão. Coordenador aceitou a execução da ação. */
	public static final int EXTENSAO_EM_EXECUCAO							= 103;
	
	/** Ação reprovada pelo comitê de extensão. */	
	public static final int EXTENSAO_REPROVADO								= 104; 
	
	/** Ação de extensão foi concluída. */
	public static final int EXTENSAO_CONCLUIDO								= 105;
	
	/** Ação foi enviada para autorização dos departamentos de todos os docentes envolvidos. */
	public static final int EXTENSAO_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS	= 106; 
	
	/** Ação não foi autorizada por um ou mais departamentos dos docentes envolvidos. */
	public static final int EXTENSAO_NAO_AUTORIZADO_DEPARTAMENTOS			= 107;
	
	/** Ação removida do sistema pela Pró-Reitoria de Extensão ou pelo docente. */
	public static final int EXTENSAO_REMOVIDO								= 108;
	
	/** Avaliadores externos e membros do comitê avaliando a proposta. */
	public static final int EXTENSAO_AGUARDANDO_AVALIACAO	 				= 109;
	
	/** Registro aprovado por membro da Pró-Reitoria de Extensão. */
	public static final int EXTENSAO_REGISTRO_APROVADO		 				= 110;
	
	/** Registro reprovado por membro da Pró-Reitoria de Extensão. */
	public static final int EXTENSAO_REGISTRO_REPROVADO		 				= 111; 
	
	/** Chefe do departamento que autoriza a proposta devolve-a para o coordenador para edição. */
	public static final int EXTENSAO_PROPOSTA_DEVOLVIDA_PARA_COORDENADOR	= 112; 
	
	/** Proex devolve proposta para o chefe reavaliar (houve um equivoco na avaliação do projeto pelo chefe do departamento.). */
	public static final int EXTENSAO_PROPOSTA_DEVOLVIDA_PARA_DEPARTAMENTO	= 113; 
	
	/** Projeto foi reprovado no processo de avaliação normal, o coordenador solicitou reconsideração da avaliação. */
	public static final int EXTENSAO_ANALISANDO_SOLICITACAO_RECONSIDERACAO	= 114;
	
	/** Reconsideração aprovada, proposta de ação foi aberto para reedição. */
	public static final int EXTENSAO_RECONSIDERACAO_APROVADA				= 115;
	
	/** Reconsideração foi reprovada, proposta não aceita definitivamente. */
	public static final int EXTENSAO_RECONSIDERACAO_NAO_APROVADA			= 116; 

	/** Projeto encerrado com pendências. ex. pendência de relatório final. */
	public static final int EXTENSAO_PENDENTE_DE_RELATORIO			  		= 117; 
	
	/** Grupo de que representa ações de extensão ativas. */
	public static final Integer[] EXTENSAO_GRUPO_ATIVO = { EXTENSAO_EM_EXECUCAO };
    
	/** Grupo de que representa ações de extensão que não podem gerenciar participantes. */
	public static final Integer[] EXTENSAO_GRUPO_NAO_PERMITIR_VISUALIZAR_PARTICIPANTES = {
	    EXTENSAO_AGUARDANDO_AVALIACAO, EXTENSAO_CADASTRO_EM_ANDAMENTO,
	    EXTENSAO_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS,
	    EXTENSAO_PROPOSTA_DEVOLVIDA_PARA_COORDENADOR,
	    EXTENSAO_PROPOSTA_DEVOLVIDA_PARA_DEPARTAMENTO, EXTENSAO_SUBMETIDO,
	    EXTENSAO_ANALISANDO_SOLICITACAO_RECONSIDERACAO };
    
	/** Grupo de que representa ações de extensão válidas. */
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
    
	/** Grupo de que representa ações de extensão inválidas. */
	public static final Integer[] EXTENSAO_GRUPO_INVALIDO = {
	    EXTENSAO_REPROVADO, EXTENSAO_NAO_AUTORIZADO_DEPARTAMENTOS,
	    EXTENSAO_REMOVIDO, EXTENSAO_REGISTRO_REPROVADO};	

	
	
	/** Constantes que representam o status dos projetos de Pesquisa */
	// Status iniciais
	/** Cadastro do projeto em andamento. */
	public static final int  GRAVADO						= 200;
	/** Projeto submetido. Pronto para distribuir para avaliação. Apenas para projetos Internos. */
	public static final int  SUBMETIDO 						= 201; 
	
	// Status intermediários
	/** Projeto cadastrado. Apenas para projetos externos. */
	public static final int  CADASTRADO						= 210;
	/** Projeto distribuído automaticamente pelo sistema. */
	public static final int  DISTRIBUIDO_AUTOMATICAMENTE 	= 211;
	/** Projeto avaliação insuficiente. */
	public static final int  AVALIACAO_INSUFICIENTE		 	= 212;
	/** Projeto distribuído manualmente. */
	public static final int  DISTRIBUIDO_MANUALMENTE 		= 213;
	/** Projeto aprovado, utilizado somente para o histórico. */
	public static final int  APROVADO						= 214;
	/** Projeto em execução. */
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
	/** Projeto encerrado com pendências. ex. pendência de relatório final. */
	public static final int  PENDENTE_DE_RELATORIO_FINAL    = 226;

	/** Grupo de situações de projetos de pesquisa válidos. */
	public static final Integer[] PESQ_GRUPO_VALIDO	= {EM_ANDAMENTO, RENOVADO, FINALIZADO};

	
	/** Constantes que representam o status dos projetos de ação acadêmica associada (PROJETO BASE) */
	/** Proposta de projeto foi gravada no banco e pode ser concluída depois. */
	public static final int PROJETO_BASE_CADASTRO_EM_ANDAMENTO			= 301;
	
	/** Proposta autorizada e submetida à aprovação. será analisada. */
	public static final int PROJETO_BASE_SUBMETIDO					= 302;
	 
	/** Projeto base aprovado e em execução. */
	public static final int PROJETO_BASE_EM_EXECUCAO				= 303;
	
	/** Projeto reprovado pelos avaliadores. */
	public static final int PROJETO_BASE_REPROVADO					= 304; 
	
	/** Projeto concluído. */
	public static final int PROJETO_BASE_CONCLUIDO					= 305;	
	
	/** Projeto removido do sistema. */
	public static final int PROJETO_BASE_REMOVIDO					= 306; 
	
	/** Projeto em fase de avaliação. */
	public static final int PROJETO_BASE_AGUARDANDO_AVALIACAO			= 307; 
	
	/** Projeto aprovado com recursos para execução. */
	public static final int PROJETO_BASE_APROVADO_COM_RECURSOS			= 308; 
	
	/** Projeto aprovado sem recursos para execução. */
	public static final int PROJETO_BASE_APROVADO_SEM_RECURSOS			= 309; 
	
	/** Projeto não concordou com avaliação do comitê e solicitou reconsideração. */
	public static final int PROJETO_BASE_ANALISANDO_PEDIDO_RECONSIDERACAO		= 310;
	
	/** Projeto não concordou com avaliação do comitê que teve a reconsideração aprovada. */
	public static final int PROJETO_BASE_PEDIDO_RECONSIDERACAO_APROVADO		= 311; 
	
	/** Projeto não concordou com avaliação do comitê que teve a reconsideração Negada. */
	public static final int PROJETO_BASE_PEDIDO_RECONSIDERACAO_NEGADO		= 312; 
	
	/** Coordenação do projeto negou a execução do projeto.(Somente para acompanhamento no histórico). */
	public static final int PROJETO_BASE_COORDENACAO_NEGOU_EXECUCAO			= 313; 
	
	/** Encerrado após dias estabelecidos pela Pró-reitoria de Extensão para o envio da proposta de projeto. */
	public static final int PROJETO_BASE_ENCERRADO_COM_PENDENCIAS			= 314; 
	
	/** Coordenação do projeto aceitou a execução do projeto.(Somente para acompanhamento no histórico). */
	public static final int PROJETO_BASE_COORDENACAO_ACEITOU_EXECUCAO		= 315;
	
	/** Situação normalmente utilizada quando o coordenador nega a execução do projeto. */
	public static final int PROJETO_BASE_CANCELADO					= 316; 
	
	/** Quando o coordenador envia o projeto para aprovação dos departamentos envolvidos. */
	public static final int PROJETO_BASE_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS	= 317;
	
	/** Tempo de cadastro expirado após dias estabelecidos pela proex para o envio da proposta.	 */
	public static final int PROJETO_BASE_TEMPO_DE_CADASTRO_EXPIRADO			= 318; 
	
	/** Indica que a avaliação do projeto foi concluída. O próximo passo seria definir se o projeto foi aprovado, se receberá rescursos ou não. */
	public static final int PROJETO_BASE_AVALIADO						= 319;
	
	/** Indica que o projeto foi registrado, mas aguarda a conclusão do processo junto outros departamentos da IFES, normalmente está relacionado a projetos com convênio. */
	public static final int PROJETO_BASE_REGISTRADO						= 320; 

	/** Lista de situações para ações acadêmicas associadas que podem solicitar reconsideração. */
	public static final Integer[] PROJETO_BASE_GRUPO_SOLICITAR_RECONSIDERACAO	= {PROJETO_BASE_APROVADO_COM_RECURSOS, 
	    											PROJETO_BASE_APROVADO_SEM_RECURSOS, MON_NAO_RECOMENDADO, REPROVADO, EXTENSAO_REPROVADO, PROJETO_BASE_REPROVADO};
	
	/** Lista de situações para ações acadêmicas associadas inválidas */
	public static final Integer[] PROJETO_BASE_GRUPO_INVALIDO	= {PROJETO_BASE_REPROVADO, PROJETO_BASE_REMOVIDO, PROJETO_BASE_COORDENACAO_NEGOU_EXECUCAO, PROJETO_BASE_CANCELADO};
	
	//Grupos comuns a ensino(monitoria), pesquisa e extensão.
	/** Situações comuns para projetos em execução de ensino, pesquisa e extensão. */
	public static final Integer[] PROJETOS_GRUPO_EM_EXECUCAO = { MON_EM_EXECUCAO, EM_ANDAMENTO, EXTENSAO_EM_EXECUCAO, PROJETO_BASE_EM_EXECUCAO, EXTENSAO_PENDENTE_DE_RELATORIO };
	/** Situações comuns para projetos concluídos de ensino, pesquisa e extensão. */
	public static final Integer[] PROJETOS_GRUPO_CONCLUIDO = { MON_CONCLUIDO, FINALIZADO, EXTENSAO_CONCLUIDO, PROJETO_BASE_CONCLUIDO};
	/** Situações comuns para projetos reprovados de ensino, pesquisa e extensão. */
	public static final Integer[] PROJETOS_GRUPO_REPROVADO = { MON_NAO_RECOMENDADO, REPROVADO, EXTENSAO_REPROVADO, PROJETO_BASE_REPROVADO};
	
	/** Grupos válidos na busca dos projetos de Extensão no RID. */ 
	public static final Integer[] SITUACOES_VALIDAS_RID = {
		EXTENSAO_EM_EXECUCAO, EXTENSAO_CONCLUIDO, EXTENSAO_REGISTRO_APROVADO, 
		PROJETO_BASE_EM_EXECUCAO, PROJETO_BASE_CONCLUIDO};	
	
	/** Situações válidas para ações acadêmicas associadas.*/
	public static final Integer[] SITUACOES_VALIDAS_ASSOCIADOS = { TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO, TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO };
	
	// Fields
	/** Chave primária.*/
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_tipo_situacao_projeto")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;

	/** Descrição do tipo de situação. */
	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 100)
	private String descricao;

	/**
	 * Discrimina o contexto da situação em questão, se é de a situação é referente a pesquisa, monitoria ou extensão.
	 * os valores possíveis são:
	 * M - monitoria;
	 * P - pesquisa
	 * E - extensão.
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
	 * Retorna a descrição da situação.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDescricao();
	}

}
