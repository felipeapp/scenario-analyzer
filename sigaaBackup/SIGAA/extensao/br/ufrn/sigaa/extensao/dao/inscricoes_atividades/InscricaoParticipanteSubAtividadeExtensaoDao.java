/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 14/11/2012
 * 
 */
package br.ufrn.sigaa.extensao.dao.inscricoes_atividades;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.CadastroParticipanteAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividade;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.ModalidadeParticipante;
import br.ufrn.sigaa.extensao.dominio.ModalidadeParticipantePeriodoInscricaoAtividade;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;
import br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante;
import br.ufrn.sigaa.extensao.dominio.StatusPagamentoInscricao;
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoSubAtividadeExtensao;
import br.ufrn.sigaa.extensao.jsf.inscricoes_atividades.GerenciarInscritosCursosEEventosExtensaoMBean.CamposOrdenacaoListaInscricoes;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;

/**
 * <p>Dao para consultas específicas em inscrições de participantes em mini atividades </p>
 *
 * @author jadson
 *
 */
public class InscricaoParticipanteSubAtividadeExtensaoDao extends GenericSigaaDAO{

	
	
//	/**
//	 * <p>Quanta a quantidade de inscrições ativas da sub atividade.</p>
//	 *  
//	 * @param idCadastroParticipante
//	 * @param idAtividade
//	 * @return
//	 * @throws DAOException
//	 */
//	public int countQtdInscricoesParticipanteNoPeriodoSubAtividade(int idPeriodoInscricaoSubAtividade) throws DAOException {
//		
//		String hql = " SELECT COUNT(DISTINCT inscricao.id ) " +
//				" FROM InscricaoAtividadeParticipante inscricao " +
//				" WHERE inscricao.inscricaoAtividade.subAtividade.id = :idSubAtividade " +
//				" AND inscricao.statusInscricao in "+UFRNUtils.gerarStringIn(StatusInscricaoParticipante.getStatusAtivos());
//		
//		Query q = getSession().createQuery(hql);
//		q.setInteger("idSubAtividade", idSubAtividade);
//		
//		return ((Long)q.uniqueResult()).intValue();
//	}
	
	
	
	/**
	 * <p>Retorna todos os participantes de um determinado mini cursos ou minievento de extenção agrupados por status da inscrição. </p>
	 * 
	 * <p>Esse método geralmente é usado pelo caso de uso de gerenciar participantes, que é o caso de uso quando o coordenador 
	 *  vai aprovar a participação etc... </p>
	 * 
	 * <p>Essa consulta tem que ser muito bem otimizada porque a quantidade de participantes pode ser grande.</p>
	 * 
	 * @param idInscricao
	 * @param idStatus
	 * @return
	 * @throws DAOException
	 */
	
	public List<InscricaoAtividadeParticipante> findAllParticipantesBySubAtividade(Integer idSubAtividade, CamposOrdenacaoListaInscricoes campoOrdenacao, StatusInscricaoParticipante... status) throws DAOException {
		
			long tempo = System.currentTimeMillis();
		
			StringBuilder hql = new StringBuilder();

			
			hql.append(" SELECT participante.id as id, statusInscricao.id as idStatusInscricao, statusInscricao.descricao as descricaoStatusInscricao, ");
			hql.append(" participante.dataCadastro as dataCadastro, ");
			hql.append(" participante.inscricaoAtividade.id as idPeriodoInscricao, "); // no momento de aprovar tem que saber qual foi  o período inscrição que ele se inscreveu
			hql.append(" participante.instituicao as instituicao, participante.idGRUPagamento as idGRUPagamento, participante.statusPagamento as statusPagamento, ");
			hql.append(" participante.idArquivo as idArquivo, participante.descricaoArquivo as descricaoArquivo, ");
			hql.append(" cadastroParticipante.id as idCadastro, cadastroParticipante.cpf as cpf, cadastroParticipante.passaporte as passaporte, cadastroParticipante.nome as nomeInscrito, cadastroParticipante.email as email, ");
			hql.append(" ( SELECT questionario.id FROM QuestionarioRespostas questionario WHERE questionario.inscricaoAtividadeParticipante.id = participante.id ) as idQuestionario ");
			
			hql.append(" FROM InscricaoAtividadeParticipante participante ");
			hql.append(" INNER JOIN participante.statusInscricao statusInscricao");
			hql.append(" INNER JOIN participante.cadastroParticipante cadastroParticipante");
			
			hql.append(" WHERE participante.inscricaoAtividade.subAtividade.id = :idSubAtividade"); // Recupera todas as inscrição para a sub atividade
			hql.append(" AND participante.inscricaoAtividade.ativo = :true ");                      // Cujo o período de inscrição não foi suspenso
			
			if(status != null && status.length > 0){
				hql.append(" AND statusInscricao.id IN "+UFRNUtils.gerarStringIn(status));
			}
			
			hql.append(" ORDER BY participante.statusInscricao.id ASC, "+campoOrdenacao.getCampoOrdenacao());
			
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idSubAtividade", idSubAtividade);
			query.setBoolean("true", true);
			
			@SuppressWarnings("unchecked")
			List<Object[]> dadosInscricoesParticipante = query.list();
			
			List<InscricaoAtividadeParticipante> retorno = new ArrayList<InscricaoAtividadeParticipante>();
			
			for (Object[] dadosRecuperados : dadosInscricoesParticipante) {
				InscricaoAtividadeParticipante inscricao = new InscricaoAtividadeParticipante();
				inscricao.setId( (Integer) dadosRecuperados[0]);
				inscricao.setStatusInscricao( new StatusInscricaoParticipante((Integer) dadosRecuperados[1], (String) dadosRecuperados[2]) );
				inscricao.setDataCadastro( (Date) dadosRecuperados[3] );
				inscricao.setInscricaoAtividade(  new InscricaoAtividade((Integer) dadosRecuperados[4]) );
				
				inscricao.setInstituicao((String) dadosRecuperados[5]);
				inscricao.setIdGRUPagamento((Integer) dadosRecuperados[6]);
				inscricao.setStatusPagamento((StatusPagamentoInscricao) dadosRecuperados[7]);
				inscricao.setIdArquivo((Integer) dadosRecuperados[8]);
				inscricao.setDescricaoArquivo((String) dadosRecuperados[9]);
				
				inscricao.setCadastroParticipante( new CadastroParticipanteAtividadeExtensao((Integer) dadosRecuperados[10], (Long) dadosRecuperados[11], (String) dadosRecuperados[12], (String) dadosRecuperados[13], (String) dadosRecuperados[14])  );
				
				if(dadosRecuperados[15] != null)
					inscricao.setQuestionarioRespostas( new QuestionarioRespostas((Integer) dadosRecuperados[15]));
				retorno.add(inscricao);
			}
			
			System.out.println(" <<<<<<<<<<<<<<<<<<<< "+(System.currentTimeMillis()-tempo)+" ms >>>>>>>>>>>>>>>>>>>>");
			
			return retorno;
			
	}
	
	
	/**
	 * <p>Retorna os dados das mini atividades de extensão que estão com período de inscrição abertos. 
	 * Para a atividade que o usuário se inscreveu e foi selecionada</p>
	 * 
	 * <p>Os dados mais básicos possíveis, porque eles serão mostrados em uma listagem. E a quantidade pode ser grande.</p>
	 * 
	 * @param titulo
	 * @param idTipoAtividade
	 * @param idAreaTematica
	 * @param idDocente
	 * @param idFuncao
	 * @param apenasAbertas
	 * @param inicio
	 * @param fim
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<InscricaoAtividade> findSubAtividadesComPeriodosDeInscricoesAbertos(int idAtividadePai, int idCadastraParticipanteLogado) throws DAOException {
		
		long tempo = System.currentTimeMillis();
		
		try {
		
			StringBuilder hql = new StringBuilder();
			hql.append(" SELECT DISTINCT periodoInscricao.id as idPeriodoInscricao, periodoInscricao.periodoInicio as periodoInicio,");
			hql.append(" periodoInscricao.periodoFim as periodoFim, periodoInscricao.quantidadeVagas as quantidadeVagas, ");
			
			// Uma consulta interna para contar a quantidade de inscrições confirmadas realizadas //
			hql.append("( SELECT COUNT( DISTINCT iap.id) FROM InscricaoAtividadeParticipante iap " +
					" where iap.inscricaoAtividade.id = periodoInscricao.id AND iap.statusInscricao.id = :idStatusInscritosAprovados) as numeroAprovados, ");

			// Uma consulta interna para contar a quantidade de inscrições aceitas realizadas //
			hql.append("( SELECT COUNT( DISTINCT iap.id) FROM InscricaoAtividadeParticipante iap " +
			" where iap.inscricaoAtividade.id = periodoInscricao.id and iap.statusInscricao.id = :idStatusInscritos) as numeroInscritos, ");

			// Informação das sub atividades //
			hql.append(" subAtividade.id as idSubAtividade, subAtividade.titulo, subAtividade.tipoSubAtividadeExtensao.descricao, ");
			
			// Informação das atividades e do coordenador //
			hql.append(" atividade.id as idAtividade, atividade.tipoAtividadeExtensao, projeto.id, projeto.titulo, coordenador.id, pessoa.id, pessoa.nome, ");
			
			// Verifica se já estou inscrito //
			hql.append(" ( SELECT COUNT( DISTINCT participante.id ) " +
			" FROM InscricaoAtividadeParticipante participante " +
			" WHERE participante.inscricaoAtividade.subAtividade.id  = subAtividade.id " + // aqui verifica se o usuário já realizou a inscrição em qualquer período, mesmo um que já tenha encerrado.
			" AND participante.cadastroParticipante.id  = :idCadastraParticipanteLogado AND participante.statusInscricao IN (:statusAtivos) )  as minhasInscricoes ");
			
			hql.append(" FROM InscricaoAtividade as periodoInscricao " +
					" JOIN periodoInscricao.subAtividade subAtividade " +
					" JOIN subAtividade.atividade atividade " +
					" JOIN atividade.projeto projeto " +
					" JOIN projeto.coordenador coordenador " +
					" JOIN coordenador.pessoa pessoa " +
					" JOIN coordenador.funcaoMembro funcao ");
			
			// AINDA NÃO FOI SUSPENSO PELO COORDENADOR //
			hql.append(" WHERE periodoInscricao.ativo = trueValue()"); 
			
			// E PERTENCE A ATIVIDEDE PASSADA //
			hql.append(" AND atividade.id = :idAtividadePai "); 
			
			// APENAS PERÍODOS DE INSCRIÇÃO DE ATIVIDADES SUBATIVIDADES E PROJETOS QUE CONTINUEM ATIVOS  //
			hql.append(" AND subAtividade.ativo = trueValue() AND atividade.ativo = trueValue() AND projeto.ativo = trueValue()");

			// NÃO RETORNA AS INSCRIÇÕES QUE SÃO DE ATIVIDADES.
			hql.append(" AND periodoInscricao.atividade IS NULL ");  
			
			// RECUPERA ASPENAS AS ABERTAS //
			// ou seja, que já começaram e que ainda não terminaram
			hql.append(" AND periodoInscricao.periodoInicio <= :hoje AND periodoInscricao.periodoFim >= :hoje)");
			
			hql.append(" ORDER BY periodoInscricao.periodoFim asc ");		
			
			Query query = getSession().createQuery(hql.toString());
			query.setDate("hoje", new Date());
			query.setInteger("idAtividadePai", idAtividadePai);
			query.setInteger("idCadastraParticipanteLogado", idCadastraParticipanteLogado);
			query.setParameterList("statusAtivos", StatusInscricaoParticipante.getStatusAtivos());
			
			// inscrições realizadas pelo usuário e aprovadas pelo coordenador, ou automaticamente, caso não exija aprovação //
			query.setInteger("idStatusInscritosAprovados", StatusInscricaoParticipante.APROVADO);
			// inscrições realizadas pelo usuário, mas não aprovadas pelo coordenador //
			query.setInteger("idStatusInscritos", StatusInscricaoParticipante.INSCRITO);
			
			
			List<Object> lista = query.list();
			
			ArrayList<InscricaoAtividade>  result = new ArrayList<InscricaoAtividade>();

			
			
			for (int a = 0; a < lista.size(); a++) {
			    int col = 0;
			    Object[] colunas = (Object[]) lista.get(a);				
			    
			    InscricaoAtividade peridosInscricaoAbertos = new InscricaoAtividade();
			    peridosInscricaoAbertos = new InscricaoAtividade();
			    peridosInscricaoAbertos.setId((Integer) colunas[col++]);
			    peridosInscricaoAbertos.setPeriodoInicio((Date) colunas[col++]);
			    peridosInscricaoAbertos.setPeriodoFim((Date) colunas[col++]);					
			    
			    peridosInscricaoAbertos.setQuantidadeVagas ( (Integer) colunas[col++] );
			    
			    // NÃO É SALVO NO BANCO CALCULADO "ON LINE"  // 
			    peridosInscricaoAbertos.setQuantidadeInscritosAprovados(  (( Long) colunas[col++]).intValue() );
			    peridosInscricaoAbertos.setQuantidadeInscritos( ( (Long) colunas[col++]).intValue() );
			   
			    peridosInscricaoAbertos.setSubAtividade(new SubAtividadeExtensao((Integer) colunas[col++]));
			    peridosInscricaoAbertos.getSubAtividade().setTitulo( (String) colunas[col++] );
			    peridosInscricaoAbertos.getSubAtividade().setTipoSubAtividadeExtensao( new TipoSubAtividadeExtensao((String) colunas[col++] ) );
			    
			    peridosInscricaoAbertos.getSubAtividade().setAtividade(new AtividadeExtensao((Integer) colunas[col++]));
			    peridosInscricaoAbertos.getSubAtividade().getAtividade().setTipoAtividadeExtensao((TipoAtividadeExtensao) colunas[col++]);
			    peridosInscricaoAbertos.getSubAtividade().getAtividade().setProjeto(new Projeto((Integer) colunas[col++]));
			    peridosInscricaoAbertos.getSubAtividade().getAtividade().getProjeto().setTitulo((String) colunas[col++]);
			    peridosInscricaoAbertos.getSubAtividade().getAtividade().getProjeto().setCoordenador(new MembroProjeto((Integer) colunas[col++]));
			    peridosInscricaoAbertos.getSubAtividade().getAtividade().getProjeto().getCoordenador().setPessoa(new Pessoa((Integer) colunas[col++]));
			    peridosInscricaoAbertos.getSubAtividade().getAtividade().getProjeto().getCoordenador().getPessoa().setNome(((String) colunas[col++]));
			    
				peridosInscricaoAbertos.setEstouInscrito( (( Long) colunas[col++]).intValue() > 0  );
			    
			    result.add(peridosInscricaoAbertos);
			}

			return result;

		}finally{
			System.out.println(" ######### Consulta Demorou: "+( System.currentTimeMillis() - tempo )+" ms ##########");
		}
	}
	
	
	
	
	
	/**
	 * <p>Esse método retorna Todas as informações necessária para o participante gerenciar sua inscrição. 
	 * Incluindo as informações para o participante emtir o certificado e a GRU para pagamento. </p>
	 * 
	 * @param titulo
	 * @param idTipoAtividade
	 * @param idAreaTematica
	 * @param idDocente
	 * @param idFuncao
	 * @param apenasAbertas
	 * @param inicio
	 * @param fim
	 * @return
	 * @throws DAOException
	 */

	public InscricaoAtividadeParticipante findInformacoesInscricaoParticipanteSubAtividade(int idInscricaoParticipante) throws DAOException {
		
		String projecao = " inscricao.id_inscricao_atividade_participante as idInscricao, statusInscricao.id_status_inscricao_participante, statusInscricao.descricao statusInscricao, "+
				          " inscricao.instituicao, inscricao.valor_taxa_matricula, inscricao.id_gru_pagamento, inscricao.status_pagamento, modalidade.nome, "+
				          
				          " periodoInscricao.periodo_inicio, periodoInscricao.periodo_fim, periodoInscricao.cobranca_taxa_matricula, periodoInscricao.data_vencimento_gru, periodoInscricao.observacoes, periodoInscricao.motivo_cancelamento, periodoInscricao.ativo,  "+
				         
				          " subatividade.id_sub_atividade_extensao as idSubAtividade, subatividade.titulo as tituloSubAtividade, subatividade.ativo as subAtiva, tipoSubAtividade.descricao, subatividade.inicio, subatividade.fim, " +
				          " atividade.id_atividade as idAtividade, projeto.titulo as tituloAtividade, " +
				          " projeto.data_inicio as inicioAtividade, projeto.data_fim as fimAtividade, projeto.ativo, projeto.id_tipo_situacao_projeto, projeto.autorizar_certificado_gestor, " +  //informações para emissão de certificado e declaração" +
				          " tipoAtividade.descricao as tipoAtividade, projeto.id_unidade_orcamentaria, "+
				          " coordenador.id_membro_projeto idCordenador, pessoa.id_pessoa idPessoaCoordenador, pessoa.nome as nomeCoordenador, "+
				          " participante.id_participante_acao_extensao, participante.frequencia, participante.autorizacao_declaracao, participante.autorizacao_certificado, participante.observacao_certificado,  participante.ativo as participanteAtivo ";	
		
		String sql = " SELECT DISTINCT " +projecao+
					 " FROM extensao.inscricao_atividade_participante inscricao "+
					 " INNER JOIN extensao.status_inscricao_participante statusInscricao                                       ON statusInscricao.id_status_inscricao_participante = inscricao.id_status_inscricao_participante "+
					 " LEFT JOIN extensao.modalidade_participante_periodo_inscricao_atividade modalidadeParticipante           ON modalidadeParticipante.id_modalidade_participante_periodo_inscricao_atividade = inscricao.id_modalidade_participante_periodo_inscricao_atividade  " +
 					 " LEFT JOIN extensao.modalidade_participante modalidade                                                   ON modalidade.id_modalidade_participante = modalidadeParticipante.id_modalidade_participante  " +
					 " INNER JOIN extensao.inscricao_atividade periodoInscricao                                                ON periodoInscricao.id_inscricao_atividade=inscricao.id_inscricao_atividade  " +
					 " INNER JOIN extensao.sub_atividade_extensao subatividade                                                 ON subatividade.id_sub_atividade_extensao = periodoInscricao.id_sub_atividade_extensao  " +
					 " INNER JOIN extensao.tipo_sub_atividade_extensao tipoSubAtividade                                        ON tipoSubAtividade.id_tipo_sub_atividade_extensao = subatividade.id_tipo_sub_atividade_extensao  "+
					 " INNER JOIN extensao.atividade atividade                                                                 ON atividade.id_atividade = subatividade.id_atividade  " +
					 " INNER JOIN extensao.tipo_atividade_extensao tipoAtividade                                               ON tipoAtividade.id_tipo_atividade_extensao = atividade.id_tipo_atividade_extensao  " +
					 " INNER JOIN projetos.projeto projeto                                                                     ON projeto.id_projeto = atividade.id_projeto  "+ 
					 " JOIN projetos.membro_projeto coordenador                                                                ON coordenador.id_membro_projeto = projeto.id_coordenador " +
					 " JOIN comum.pessoa pessoa                                                                                ON coordenador.id_pessoa =pessoa.id_pessoa "+
					 " LEFT JOIN extensao.participante_acao_extensao participante                                              ON participante.id_participante_acao_extensao = inscricao.id_participante_acao_extensao ";
		
		sql += " WHERE inscricao.id_inscricao_atividade_participante  = :idInscricaoParticipante  ";
		
		Query query = getSession().createSQLQuery(sql);
		query.setInteger("idInscricaoParticipante", idInscricaoParticipante);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.list();
		
		
		
		InscricaoAtividadeParticipante retorno = new InscricaoAtividadeParticipante();
		
		for (Object[] objects : lista) {
			          
			retorno.setId( (Integer) objects[0]);
			retorno.setStatusInscricao( new StatusInscricaoParticipante((Integer) objects[1], (String) objects[2]));
			
			retorno.setInstituicao( (String) objects[3] );
			
			if( objects[4]  != null ) retorno.setValorTaxaMatricula( (BigDecimal) objects[4]  );
			if( objects[5]  != null ) retorno.setIdGRUPagamento( (Integer) objects[5]  );
			if( objects[6]  != null ) retorno.setStatusPagamento( StatusPagamentoInscricao.getStatusMulta( ((Short) objects[6]).intValue() )  );
			
			if( objects[7] != null ){
				ModalidadeParticipantePeriodoInscricaoAtividade modalidade = new ModalidadeParticipantePeriodoInscricaoAtividade();
				modalidade.setModalidadeParticipante( new ModalidadeParticipante(0, (String) objects[7], true) );
				retorno.setMolidadeParticipante( modalidade );
			}
			
			InscricaoAtividade periodoInscricao = new InscricaoAtividade();
			periodoInscricao.setPeriodoInicio( (Date) objects[8]  );
			periodoInscricao.setPeriodoFim( (Date) objects[9]  );
			periodoInscricao.setCobrancaTaxaMatricula( (Boolean) objects[10]  );
			periodoInscricao.setDataVencimentoGRU( (Date) objects[11]  );
			periodoInscricao.setObservacoes( (String) objects[12] );
			periodoInscricao.setMotivoCancelamento( (String) objects[13] );
			periodoInscricao.setAtivo( (Boolean) objects[14] );
			
			periodoInscricao.setSubAtividade(   new SubAtividadeExtensao( (Integer) objects[15], (String) objects[16] ) );
			periodoInscricao.getSubAtividade().setAtivo( (Boolean) objects[17]);
			periodoInscricao.getSubAtividade().setTipoSubAtividadeExtensao(new TipoSubAtividadeExtensao((String) objects[18]));
			
			periodoInscricao.getSubAtividade().setInicio(( Date) objects[19] );
			periodoInscricao.getSubAtividade().setFim(( Date) objects[20] );
			
			periodoInscricao.getSubAtividade().setAtividade(  new AtividadeExtensao( (Integer) objects[21], (String) objects[22], 0)  );
			
			periodoInscricao.getSubAtividade().getAtividade().getProjeto().setDataInicio(( Date) objects[23]);
			periodoInscricao.getSubAtividade().getAtividade().getProjeto().setDataFim( (Date) objects[24]);
			
			periodoInscricao.getSubAtividade().getAtividade().getProjeto().setAtivo( (Boolean) objects[25]);
			periodoInscricao.getSubAtividade().getAtividade().getProjeto().setSituacaoProjeto( new TipoSituacaoProjeto( (Integer) objects[26] ) );
			periodoInscricao.getSubAtividade().getAtividade().getProjeto().setAutorizarCertificadoGestor( (Boolean) objects[27]);
			
			periodoInscricao.getSubAtividade().getAtividade().setTipoAtividadeExtensao(new TipoAtividadeExtensao(0, (String) objects[28]));
			
			if( objects[29] != null ) periodoInscricao.getSubAtividade().getAtividade().getProjeto().setUnidadeOrcamentaria( new Unidade( (Integer) objects[29])); // para emitir a GRU
			
			periodoInscricao.getSubAtividade().getAtividade().getProjeto().setCoordenador(  new MembroProjeto(  (Integer) objects[30] ) );
			periodoInscricao.getSubAtividade().getAtividade().getProjeto().getCoordenador().setPessoa( new Pessoa ((Integer) objects[31] , (String) objects[32] ));
			
			retorno.setInscricaoAtividade(periodoInscricao);
			
			if(objects[33] != null ) {  // se a inscrição tiver sido aprovada deve ter participante associado //
				retorno.setParticipanteExtensao( new ParticipanteAcaoExtensao());
				retorno.getParticipanteExtensao().setId( (Integer) objects[33] );
				retorno.getParticipanteExtensao().setFrequencia( (Integer) objects[34] );
				retorno.getParticipanteExtensao().setAutorizacaoDeclaracao( (Boolean) objects[35] );
				retorno.getParticipanteExtensao().setAutorizacaoCertificado( (Boolean) objects[36] );
				retorno.getParticipanteExtensao().setObservacaoCertificado( (String) objects[37] );
				retorno.getParticipanteExtensao().setAtivo( (Boolean) objects[38] );
				
				retorno.getParticipanteExtensao().setSubAtividade(periodoInscricao.getSubAtividade()); // para emissão do certificado
			}
			
		}
		
		
		return retorno;
	}
	
	
	
	/***
	 * <p>Retorna as inscrições do participante ativas para a sub atividade da atividade passada</p>
	 * 
	 *
	 * @param idAtividade
	 * @return
	 * @throws DAOException
	 */
	public List<InscricaoAtividadeParticipante> findInscricoesParticipanteSubAtividadesAtivasDaAtivade(int idAtividade, int idCadastroParticipante) throws DAOException {
	
		String hql = " SELECT inscricaoParticipante.id, inscricaoParticipante.statusInscricao.id, inscricaoParticipante.statusInscricao.descricao, participanteExtensao.id " +
				" FROM InscricaoAtividadeParticipante  inscricaoParticipante "+
				" INNER JOIN inscricaoParticipante.inscricaoAtividade inscricao "+
				" INNER JOIN inscricao.subAtividade sub "+
				" INNER JOIN sub.atividade atividade "+
				" LEFT  JOIN inscricaoParticipante.participanteExtensao participanteExtensao "+
				" WHERE inscricao.subAtividade.atividade.id = :idAtividade " +
				" AND inscricaoParticipante.cadastroParticipante = :idCadastroParticipante  "+
				" AND inscricaoParticipante.statusInscricao IN "+UFRNUtils.gerarStringIn(StatusInscricaoParticipante.getStatusAtivos()); 
		
		Query query = getSession().createQuery(hql);
		query.setInteger("idAtividade", idAtividade);
		query.setInteger("idCadastroParticipante", idCadastroParticipante);
		
		@SuppressWarnings("unchecked")
		List<Object> lista = query.list();			
		
		ArrayList<InscricaoAtividadeParticipante> result = new ArrayList<InscricaoAtividadeParticipante>();			
		
		
		for (Object dados : lista) {

			Object[] colunas = (Object[]) dados;
		
			InscricaoAtividadeParticipante inscricao = new InscricaoAtividadeParticipante();
			inscricao.setId((Integer) colunas[0]);
			inscricao.setStatusInscricao(new StatusInscricaoParticipante((Integer) colunas[1], (String) colunas[2]));
			
			if(colunas[3] != null){
				inscricao.setParticipanteExtensao(new ParticipanteAcaoExtensao( (Integer) colunas[3]) );
			}
			
			result.add(inscricao);
		}
		
		return result;
	}
}
