/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '21/12/2009'
 *
 */
package br.ufrn.sigaa.extensao.dao.inscricoes_atividades;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividade;
import br.ufrn.sigaa.extensao.dominio.ModalidadeParticipantePeriodoInscricaoAtividade;
import br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante;
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.questionario.dominio.Questionario;

/**
 * DAO responsável pelas consultas às inscrições das ações de extensão.
 * 
 * @author Daniel Augusto 
 */
public class InscricaoAtividadeExtensaoDao extends GenericSigaaDAO {
	
	
	/**
	 * <p>Retorna os períodos de inscrições existentes para a atividade informada.</p>
	 * 
	 * <p><strong>IMPORTANTE:</strong> Apenas para a atividade, não retorna inscrições em sub atividades.  
	 * Para isso use o método InscricaoSubAtividadeExtensaoDao#findAllPeriodosInscricoesDaSubAtividade()</p>
	 * 
	 * @param idAtividade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<InscricaoAtividade> findAllPeriodoInscricaoDaAtividade(int idAtividade) throws DAOException {
		
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT inscricao.id as id, inscricao.periodoInicio as periodoInicio, inscricao.periodoFim as periodoFim,");
			hql.append("  inscricao.quantidadeVagas as quantidadeVagas, inscricao.metodoPreenchimento, ");
			hql.append(" inscricao.sequencia as sequencia, atividade.id as idA, atividade.tipoAtividadeExtensao.id, "); // informações para gerar o código da inscrição 
			
			// Conta a quantidade de inscrições APROVADAS para essa atividade  //
			hql.append("( SELECT COUNT( DISTINCT inscricaoParticipante.id ) " +
				" FROM InscricaoAtividadeParticipante inscricaoParticipante 	" +
				" INNER JOIN inscricaoParticipante.inscricaoAtividade inscricaoAtividade " +
				" WHERE inscricaoAtividade.id = inscricao.id AND inscricaoParticipante.statusInscricao.id = :idStatusAprovado ) as numeroInscritosAprovados, ");
			
			// Conta a quantidade de inscrições REALIZADAS AINDA NÃO APROVADAS para essa atividade  //
			hql.append("( SELECT COUNT( DISTINCT inscricaoParticipante.id ) " +
				" FROM InscricaoAtividadeParticipante inscricaoParticipante 	" +
				" INNER JOIN inscricaoParticipante.inscricaoAtividade inscricaoAtividade " +
				" WHERE inscricaoAtividade.id = inscricao.id AND inscricaoParticipante.statusInscricao.id = :idStatusInscrito ) as numeroInscritos " );
			
			
			hql.append(" FROM InscricaoAtividade inscricao ");			
			hql.append(" INNER JOIN inscricao.atividade atividade ");

			hql.append(" WHERE inscricao.ativo = trueValue() AND atividade.id = :idAtividade  ");
			hql.append(" ORDER BY inscricao.periodoFim DESC ");
			
			Query query = getSession().createQuery(hql.toString());
			
			query.setInteger("idAtividade", idAtividade);
			query.setInteger("idStatusAprovado", StatusInscricaoParticipante.APROVADO);
			query.setInteger("idStatusInscrito", StatusInscricaoParticipante.INSCRITO);

			List<Object> lista = query.list();			
			
			ArrayList<InscricaoAtividade> result = new ArrayList<InscricaoAtividade>();			
			
			
			
			for (Object dados : lista) {
				
				Object[] colunas = (Object[]) dados;				

				InscricaoAtividade inscricao = new InscricaoAtividade();
				
				inscricao = new InscricaoAtividade();
				inscricao.setId( (Integer) colunas[0]);
				inscricao.setPeriodoInicio((Date) colunas[1]);
				inscricao.setPeriodoFim((Date) colunas[2]);					
				inscricao.setQuantidadeVagas((Integer) colunas[3]);
				inscricao.setMetodoPreenchimento((Integer) colunas[4]);
				inscricao.setSequencia((Integer) colunas[5]);
				inscricao.setAtividade(new AtividadeExtensao((Integer) colunas[6]));
				inscricao.getAtividade().setTipoAtividadeExtensao( new TipoAtividadeExtensao(   (Integer) colunas[7])  );
				
				
				inscricao.setQuantidadeInscritosAprovados(   ((Long) colunas[8] ).intValue()  ); // esse aqui sempre existe, sendo com confirmação ou sem
				inscricao.setQuantidadeInscritos(   ((Long) colunas[9] ).intValue()  );
				
				
				result.add(inscricao);
				
			}
			
			return result;			

	}
	
	
	/**
	 * <p>Retorna as informações do período de inscrição passado geralmente para validar no momento 
	 * da aprovação ou reprovação de um participante.</p>
	 * 
	 * @param idsInscricaoAtividades
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<InscricaoAtividade> findInformacoesPeriodosInscricaoParaValidacao(Set<Integer> idsInscricoesAtividade) throws DAOException {
		
			StringBuilder hql = new StringBuilder();
			hql.append(" SELECT inscricao.id as id, inscricao.periodoInicio as periodoInicio, inscricao.periodoFim as periodoFim,");
			hql.append("  inscricao.quantidadeVagas as quantidadeVagas, inscricao.metodoPreenchimento, inscricao.cobrancaTaxaMatricula, ");
			
			// Conta a quantidade de inscrições APROVADAS para essa atividade  //
			hql.append("( SELECT COUNT( DISTINCT inscricaoParticipante.id ) " +
				" FROM InscricaoAtividadeParticipante inscricaoParticipante 	" +
				" INNER JOIN inscricaoParticipante.inscricaoAtividade inscricaoAtividade " +
				" WHERE inscricaoAtividade.id = inscricao.id AND inscricaoParticipante.statusInscricao.id = :idStatusAprovado ) as numeroInscritosAprovados, ");
			
			// Conta a quantidade de inscrições REALIZADAS AINDA NÃO APROVADAS para essa atividade  //
			hql.append("( SELECT COUNT( DISTINCT inscricaoParticipante.id ) " +
				" FROM InscricaoAtividadeParticipante inscricaoParticipante 	" +
				" INNER JOIN inscricaoParticipante.inscricaoAtividade inscricaoAtividade " +
				" WHERE inscricaoAtividade.id = inscricao.id AND inscricaoParticipante.statusInscricao.id = :idStatusInscrito ) as numeroInscritos " );
			
			
			hql.append(" FROM InscricaoAtividade inscricao ");
			hql.append(" WHERE inscricao.id = :idsInscricoesAtividade AND inscricao.ativo = trueValue()  ");
			
			Query query = getSession().createQuery(hql.toString());
			
			query.setParameterList("idsInscricoesAtividade", idsInscricoesAtividade);
			query.setInteger("idStatusAprovado", StatusInscricaoParticipante.APROVADO);
			query.setInteger("idStatusInscrito", StatusInscricaoParticipante.INSCRITO);

			List<Object> lista = query.list();			
			
			ArrayList<InscricaoAtividade> result = new ArrayList<InscricaoAtividade>();			
			
			
			for (Object dados : lista) {
				
				Object[] colunas = (Object[]) dados;				

				InscricaoAtividade inscricao = new InscricaoAtividade();
				
				inscricao = new InscricaoAtividade();
				inscricao.setId( (Integer) colunas[0]);
				inscricao.setPeriodoInicio((Date) colunas[1]);
				inscricao.setPeriodoFim((Date) colunas[2]);					
				inscricao.setQuantidadeVagas((Integer) colunas[3]);
				inscricao.setMetodoPreenchimento((Integer) colunas[4]);
				inscricao.setCobrancaTaxaMatricula((Boolean) colunas[5]);
				
				
				inscricao.setQuantidadeInscritosAprovados(   ((Long) colunas[6] ).intValue()  ); // esse aqui sempre existe, sendo com confirmação ou sem
				inscricao.setQuantidadeInscritos(   ((Long) colunas[7] ).intValue()  );
				
				
				result.add(inscricao);
				
			}
			
			return result;			

	}
	
	
	
	/***
	 * <p>Método que retorna a quantiade de vagasde todos os períodos de inscrições existentes 
	 * para atividade passada. Caso o usuário passe uma listagem de períodos, retorna a quantidade 
	 * de vagas existentes nos períodos menos os períodos passados. </p>
	 * 
	 * <p>Usado geralmente para validação.</p>
	 *
	 * @param idAtividade
	 * @return
	 * @throws DAOException
	 */
	public Integer countQtdVagasPeriodoInscricaoAtividade(int idAtividade, Integer... idsPeriodosInscricao) throws DAOException {
	
		String hql = " SELECT SUM ( periodoInscricao.quantidadeVagas ) as quantidadeVagas" +
				" FROM InscricaoAtividade  periodoInscricao "+
				" WHERE periodoInscricao.ativo = :true AND periodoInscricao.atividade.id = :idAtividade AND periodoInscricao.atividade.ativo = :true "; 
		
		if(idsPeriodosInscricao != null && idsPeriodosInscricao.length > 0)
			hql += " AND periodoInscricao.id NOT IN ( :idsPeriodosInscricao ) ";
		
		Query query = getSession().createQuery(hql);
		query.setInteger("idAtividade", idAtividade);
		query.setBoolean("true", true);
		
		if(idsPeriodosInscricao != null && idsPeriodosInscricao.length > 0)
			query.setParameterList("idsPeriodosInscricao", idsPeriodosInscricao);
		
		Long qtd = (Long) query.uniqueResult();
		
		if(qtd == null)
			return 0;
		else		
			return qtd.intValue();	
		
		
	}
	
	
	
	/***
	 * <p>Retorna as inscrições aberts para a atividade</p>
	 * 
	 *
	 * @param idAtividade
	 * @return
	 * @throws DAOException
	 */
	
	public List<InscricaoAtividade> findInscricoesAtividades(int idAtividade) throws DAOException {
	
		String hql = " SELECT inscricao.id, inscricao.periodoInicio,  inscricao.periodoFim " +
				" FROM InscricaoAtividade  inscricao "+
				" INNER JOIN inscricao.atividade atividade "+
				" WHERE inscricao.ativo = :true AND atividade.ativo = :true AND atividade.id = :idAtividade  "; 
		
		Query query = getSession().createQuery(hql);
		query.setInteger("idAtividade", idAtividade);
		query.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<Object> lista = query.list();			
		
		ArrayList<InscricaoAtividade> result = new ArrayList<InscricaoAtividade>();			
		
		InscricaoAtividade inscricao = new InscricaoAtividade();
		
		for (Object dados : lista) {

			Object[] colunas = (Object[]) dados;
			
			inscricao = new InscricaoAtividade();
			inscricao.setAtividade(new AtividadeExtensao(idAtividade));
			
			inscricao.setId((Integer) colunas[0]);
			inscricao.setPeriodoInicio((Date) colunas[1]);
			inscricao.setPeriodoFim((Date) colunas[2]);	
			result.add(inscricao);
		}
		
		return result;
	}
	
	
	
	/***
	 * <p>Retorna as informações do período de inscricao para alteração. </p>
	 * 
	 *
	 * @param idAtividade
	 * @return
	 * @throws DAOException
	 */
	public InscricaoAtividade findInformacoesInscricoesAtividadeParaAlteracao(int idInscricao) throws DAOException {
	
		String hql = " SELECT inscricao.periodoInicio,  inscricao.periodoFim, " +
				" inscricao.quantidadeVagas, inscricao.metodoPreenchimento, inscricao.sequencia, " +
		        " inscricao.instrucoesInscricao, inscricao.observacoes, inscricao.envioArquivoObrigatorio, "+
				" inscricao.questionario.id, inscricao.cobrancaTaxaMatricula, inscricao.dataVencimentoGRU,"+
		        " modalidadesParticipantes.id, modalidadesParticipantes.taxaMatricula, modalidadesParticipantes.ativo, modalidade.id, modalidade.nome, modalidade.ativo "+
				" FROM InscricaoAtividade  inscricao "+
				" INNER JOIN inscricao.atividade atividade "+
				" LEFT JOIN inscricao.modalidadesParticipantes modalidadesParticipantes "+ // a classe associativa
				" LEFT JOIN modalidadesParticipantes.modalidadeParticipante modalidade "+

				" WHERE inscricao.id = :idInscricao  AND inscricao.ativo = :true "; 
		
		Query query = getSession().createQuery(hql);
		query.setInteger("idInscricao", idInscricao);
		query.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<Object> lista = query.list();			
		
		InscricaoAtividade inscricao = new InscricaoAtividade();
		
		for (Object dados : lista) {

			Object[] colunas = (Object[]) dados;
			
			// se a primeira vez cria a inscrição 
			if(inscricao.getId() <=0 ){
				inscricao.setId( idInscricao);
				inscricao.setPeriodoInicio((Date) colunas[0]);
				inscricao.setPeriodoFim((Date) colunas[1]);	
				
				inscricao.setQuantidadeVagas((Integer) colunas[2]);
				inscricao.setMetodoPreenchimento((Integer) colunas[3]);
				inscricao.setSequencia((Integer) colunas[4]);
				
				inscricao.setInstrucoesInscricao(  (String) colunas[5] );
				inscricao.setObservacoes(  (String) colunas[6] );
				inscricao.setEnvioArquivoObrigatorio(  (Boolean) colunas[7] );
				inscricao.setAtivo(true);
				
				if(colunas[8]  != null)
					inscricao.setQuestionario( new Questionario( (Integer) colunas[8]) );
				
				inscricao.setCobrancaTaxaMatricula( (Boolean) colunas[9]  );
				inscricao.setDataVencimentoGRU( (Date) colunas[10]  );
			}
			
			// Depois só adiciona as modalidades ativas //
			if(colunas[11] != null  && (Boolean) colunas[13].equals(true) ){
				inscricao.adicionaMolidadeParticipante(
					new ModalidadeParticipantePeriodoInscricaoAtividade( (Integer) colunas[11], (BigDecimal) colunas[12], (Boolean) colunas[13], 
							(Integer) colunas[14], (String) colunas[15], (Boolean) colunas[16],
									new InscricaoAtividade(idInscricao)));
			}
			
		}
		
		return inscricao;
	}
	

	
	/***
	 * <p>Retorna as informações do período de inscricao para emissão da GRU. </p>
	 * 
	 *
	 * @param idAtividade
	 * @return
	 * @throws DAOException
	 */
	public InscricaoAtividade findInformacoesInscricoesAtividadeParaEmissaoGRU(int idInscricao) throws DAOException {
	
		String hql = " SELECT inscricao.id, inscricao.cobrancaTaxaMatricula, inscricao.dataVencimentoGRU,  "+
		        " atividade1.id, atividade2.id, projeto1.dataInicio, projeto2.dataInicio, unidade1.id, unidade2.id "+
				" FROM InscricaoAtividade  inscricao "+
				" LEFT JOIN inscricao.atividade atividade1 "+
				" LEFT JOIN inscricao.subAtividade subAtividade "+
				" LEFT JOIN subAtividade.atividade atividade2 "+
				" LEFT JOIN atividade1.projeto projeto1 "+
				" LEFT JOIN atividade2.projeto projeto2 "+
				" LEFT JOIN projeto1.unidadeOrcamentaria unidade1 "+
				" LEFT JOIN projeto2.unidadeOrcamentaria unidade2 "+
				" WHERE inscricao.id = :idInscricao  AND inscricao.ativo = :true "; 
		
		Query query = getSession().createQuery(hql);
		query.setInteger("idInscricao", idInscricao);
		query.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<Object> lista = query.list();			
		
		InscricaoAtividade inscricao = new InscricaoAtividade();
		
		for (Object dados : lista) {

			Object[] colunas = (Object[]) dados;
			
			inscricao.setId( (Integer) colunas[0] );
			inscricao.setCobrancaTaxaMatricula( (Boolean) colunas[1] );
			inscricao.setDataVencimentoGRU( (Date) colunas[2]);	
			
			if(colunas[3] != null){
				inscricao.setAtividade( new AtividadeExtensao( (Integer) colunas[3]) );
				inscricao.getAtividade().setProjeto( new Projeto());
				inscricao.getAtividade().getProjeto().setDataInicio( (Date) colunas[5] );
				
				if(colunas[7] != null){
					inscricao.getAtividade().getProjeto().setUnidadeOrcamentaria( new Unidade( (Integer) colunas[7] )  );
				}
			}
			
			if(colunas[4] != null){
				inscricao.setSubAtividade(new SubAtividadeExtensao());
				inscricao.getSubAtividade().setAtividade( new AtividadeExtensao( (Integer) colunas[4]) );
				inscricao.getSubAtividade().getAtividade().setProjeto( new Projeto());
				inscricao.getSubAtividade().getAtividade().getProjeto().setDataInicio( (Date) colunas[6] );
				if(colunas[8] != null){
					inscricao.getSubAtividade().getAtividade().getProjeto().setUnidadeOrcamentaria( new Unidade( (Integer) colunas[8] )  );
				}
			}
			
			inscricao.setAtivo(true);
		}
		
		return inscricao;
	}
	
	
	
//	/**
//	 * Realiza a busca de todos as inscrições ou apenas as com período de inscrição aberto.
//	 * 
//	 * @param apenasAbertas
//	 * @return
//	 * @throws DAOException
//	 */
//	public Collection<InscricaoAtividade> findInscricoesAtividadesByFilter(boolean apenasAbertas) throws DAOException {
//		return findInscricoesAtividadesByFilter(null, null, null, null, null, apenasAbertas, null, null);
//	}
//	
//	/**
//	 * Realiza a busca dos período de inscrição por filtro.
//	 * 
//	 * @param titulo
//	 * @param idTipoAtividade
//	 * @param idAreaTematica
//	 * @param idDocente
//	 * @param idFuncao
//	 * @param apenasAbertas
//	 * @param inicio
//	 * @param fim
//	 * @return
//	 * @throws DAOException
//	 */
//	@SuppressWarnings("unchecked")
//	public Collection<InscricaoAtividade> findInscricoesAtividadesByFilter(String titulo, Integer idTipoAtividade, Integer idAreaTematica, 
//			Integer idDocente, Integer idFuncao, boolean apenasAbertas, Date inicio, Date fim) throws DAOException {
//		
//		try {
//			StringBuilder hql = new StringBuilder();
//			hql.append("SELECT DISTINCT inscricao.id as id, inscricao.periodoInicio as periodoInicio,");
//			hql.append(" inscricao.periodoFim as periodoFim, inscricao.quantidadeVagas as quantidadeVagas, ");
//			
//			hql.append("(select count(iap.id) from InscricaoAtividadeParticipante iap " +
//					" where iap.ativo = trueValue() and iap.inscricaoAtividade.id = inscricao.id and iap.statusInscricao.id = :idStatusConfirmados) as numeroConfirmados, ");
//
//			hql.append("(select count(iap.id) from InscricaoAtividadeParticipante iap " +
//			" where iap.ativo = trueValue() and iap.inscricaoAtividade.id = inscricao.id and iap.statusInscricao.id = :idStatusAceitos) as numeroAceitos, ");
//
//			hql.append(" atv.id, atv.tipoAtividadeExtensao, pr.id, pr.titulo, coord.id, pes.id, pes.nome " +
//					"FROM InscricaoAtividade as inscricao " +
//					"JOIN inscricao.atividade atv " +
//					"JOIN atv.projeto pr " +
//					"JOIN pr.coordenador coord " +
//					"JOIN coord.pessoa pes " +
//					"JOIN coord.funcaoMembro funcao ");
//			
//			hql.append(" WHERE inscricao.ativo = trueValue()");
//			
//			// Use as consultas mais restritivas possíveis //
//			hql.append(" AND atv.ativo = trueValue()");
//			hql.append(" AND pr.ativo = trueValue()");
//			
//			hql.append(" AND inscricao.subAtividade IS NULL ");  // NÃO RETORNA AS INSCRIÇÕES QUE SÃO DE MINI ATIVIDADES.
//			
//			if (apenasAbertas) {
//				hql.append(" AND (cast(inscricao.periodoInicio, date) <= current_date " +
//						"AND cast(inscricao.periodoFim, date) >= current_date)");
//			}
//			if (titulo != null)
//				hql.append(" AND " + UFRNUtils.toAsciiUpperUTF8("pr.titulo") + 
//						" LIKE " + UFRNUtils.toAsciiUTF8(":titulo"));
//			
//			if (idTipoAtividade != null && idTipoAtividade > 0)
//				hql.append(" AND atv.tipoAtividadeExtensao.id = :tipo");
//			
//			if (idAreaTematica != null && idAreaTematica > 0)
//				hql.append(" AND atv.areaTematicaPrincipal.id = :idArea");
//			
//			if (idDocente != null && idDocente > 0)
//				hql.append(" AND coord.servidor.id = :idDocente AND funcao.id = :idFuncao");
//			
//			if ((inicio != null) && (fim != null)) 
//				hql.append(" AND " + HibernateUtils.generateDateIntersection("inscricao.periodoInicio", 
//					"inscricao.periodoFim",	":inicio", ":fim"));
//			hql.append(" ORDER BY inscricao.periodoFim asc");		
//			
//			Query query = getSession().createQuery(hql.toString());
//			
//			query.setInteger("idStatusConfirmados", StatusInscricaoParticipante.CONFIRMADO);
//			query.setInteger("idStatusAceitos", StatusInscricaoParticipante.APROVADO);
//			
//			
//			if (titulo != null)
//				query.setString("titulo", "%" + titulo.toUpperCase() + "%");
//			
//			if (idTipoAtividade != null && idTipoAtividade > 0)
//				query.setInteger("tipo", idTipoAtividade);
//			
//			if (idAreaTematica != null && idAreaTematica > 0)
//				query.setInteger("idArea", idAreaTematica);
//			
//			if (idDocente != null && idDocente > 0) {
//				query.setInteger("idDocente", idDocente);
//				query.setInteger("idFuncao", idFuncao);
//			}
//			if ((inicio != null) && (fim != null)) {
//				query.setDate("inicio", inicio);
//				query.setDate("fim", fim);
//			}
//			
//			List<Object> lista = query.list();
//			ArrayList<InscricaoAtividade>  result = new ArrayList<InscricaoAtividade>();
//
//			InscricaoAtividade inscricao = new InscricaoAtividade();
//			for (int a = 0; a < lista.size(); a++) {
//			    int col = 0;
//			    Object[] colunas = (Object[]) lista.get(a);				
//			    inscricao = new InscricaoAtividade();
//			    inscricao.setId((Integer) colunas[col++]);
//			    inscricao.setPeriodoInicio((Date) colunas[col++]);
//			    inscricao.setPeriodoFim((Date) colunas[col++]);					
//			    inscricao.setQuantidadeVagas((Integer) colunas[col++]);
//			    inscricao.setNumeroConfirmados(((Long) colunas[col++]).intValue());
//			    inscricao.setNumeroAceitos(((Long) colunas[col++]).intValue());
//			    inscricao.setAtividade(new AtividadeExtensao((Integer) colunas[col++]));
//			    inscricao.getAtividade().setTipoAtividadeExtensao((TipoAtividadeExtensao) colunas[col++]);
//			    inscricao.getAtividade().setProjeto(new Projeto((Integer) colunas[col++]));
//			    inscricao.getAtividade().getProjeto().setTitulo((String) colunas[col++]);
//			    inscricao.getAtividade().getProjeto().setCoordenador(new MembroProjeto((Integer) colunas[col++]));
//			    inscricao.getAtividade().getProjeto().getCoordenador().setPessoa(new Pessoa((Integer) colunas[col++]));
//			    inscricao.getAtividade().getProjeto().getCoordenador().getPessoa().setNome(((String) colunas[col++]));
//			    result.add(inscricao);
//			}
//
//			return result;
//
//		} catch (Exception e) {
//			throw new DAOException(e.getMessage(), e);
//		}
//	}
	
	
	
	
	
	
//	/**
//	 * Realiza a busca dos período de inscrição por filtro.
//	 * 
//	 * @param titulo
//	 * @param idTipoAtividade
//	 * @param idAreaTematica
//	 * @param idDocente
//	 * @param idFuncao
//	 * @param apenasAbertas
//	 * @param inicio
//	 * @param fim
//	 * @return
//	 * @throws DAOException
//	 */
//	@SuppressWarnings("unchecked")
//	public Collection<InscricaoAtividade> findInscricoesSubAtividadesAbertas(String titulo, Integer idTipoAtividade, Integer idAreaTematica, 
//			Integer idDocente, Integer idFuncao, boolean apenasAbertas, Date inicio, Date fim) throws DAOException {
//		
//		try {
//			StringBuilder hql = new StringBuilder();
//			hql.append("SELECT DISTINCT inscricao.id as id, inscricao.periodoInicio as periodoInicio,");
//			
//			hql.append(" inscricao.periodoFim as periodoFim, inscricao.quantidadeVagas as quantidadeVagas, ");
//			
//			hql.append("(select count(iap.id) from InscricaoAtividadeParticipante iap " +
//					" where iap.ativo = trueValue() and iap.inscricaoAtividade.id = inscricao.id and iap.statusInscricao.id = :idStatusConfirmados) as numeroConfirmados, ");
//
//			hql.append("(select count(iap.id) from InscricaoAtividadeParticipante iap " +
//			" where iap.ativo = trueValue() and iap.inscricaoAtividade.id = inscricao.id and iap.statusInscricao.id = :idStatusAceitos) as numeroAceitos, ");
//
//			hql.append(" atv.id, atv.tipoAtividadeExtensao, atv.sequencia, pr.id, pr.titulo, pr.ano, coord.id, pes.id, pes.nome, subAtv.id, subAtv.titulo  " +
//					"FROM InscricaoAtividade as inscricao " +
//					"JOIN inscricao.subAtividade subAtv " +
//					"JOIN subAtv.atividade atv " +
//					"JOIN atv.projeto pr " +
//					"JOIN pr.coordenador coord " +
//					"JOIN coord.pessoa pes " +
//					"JOIN coord.funcaoMembro funcao ");
//			
//			hql.append(" WHERE inscricao.ativo = trueValue()");	
//			
//			// Use as consultas mais restritivas possíveis //
//			hql.append(" AND atv.ativo = trueValue()");
//			hql.append(" AND pr.ativo = trueValue()");
//			hql.append(" AND subAtv.ativo = trueValue()");
//			
//			hql.append(" AND (cast(inscricao.periodoInicio, date) <= current_date " +
//						"AND cast(inscricao.periodoFim, date) >= current_date)");
//			
//			if (apenasAbertas) {
//				hql.append(" AND (cast(inscricao.periodoInicio, date) <= current_date " +
//						"AND cast(inscricao.periodoFim, date) >= current_date)");
//			}
//			if (titulo != null)
//				hql.append(" AND " + UFRNUtils.toAsciiUpperUTF8("pr.titulo") + 
//						" LIKE " + UFRNUtils.toAsciiUTF8(":titulo"));
//			
//			if (idTipoAtividade != null && idTipoAtividade > 0)
//				hql.append(" AND atv.tipoAtividadeExtensao.id = :tipo");
//			
//			if (idAreaTematica != null && idAreaTematica > 0)
//				hql.append(" AND atv.areaTematicaPrincipal.id = :idArea");
//			
//			if (idDocente != null && idDocente > 0)
//				hql.append(" AND coord.servidor.id = :idDocente AND funcao.id = :idFuncao");
//			
//			if ((inicio != null) && (fim != null)) 
//				hql.append(" AND " + HibernateUtils.generateDateIntersection("inscricao.periodoInicio", 
//					"inscricao.periodoFim",	":inicio", ":fim"));
//			
//			hql.append(" ORDER BY inscricao.periodoFim asc");					
//			
//			Query query = getSession().createQuery(hql.toString());			
//			query.setInteger("idStatusConfirmados", StatusInscricaoParticipante.CONFIRMADO);
//			query.setInteger("idStatusAceitos", StatusInscricaoParticipante.APROVADO);
//			
//			if (titulo != null)
//				query.setString("titulo", "%" + titulo.toUpperCase() + "%");
//			
//			if (idTipoAtividade != null && idTipoAtividade > 0)
//				query.setInteger("tipo", idTipoAtividade);
//			
//			if (idAreaTematica != null && idAreaTematica > 0)
//				query.setInteger("idArea", idAreaTematica);
//			
//			if (idDocente != null && idDocente > 0) {
//				query.setInteger("idDocente", idDocente);
//				query.setInteger("idFuncao", idFuncao);
//			}
//			if ((inicio != null) && (fim != null)) {
//				query.setDate("inicio", inicio);
//				query.setDate("fim", fim);
//			}
//			
//			List<Object> lista = query.list();
//			
//			ArrayList<InscricaoAtividade>  result = new ArrayList<InscricaoAtividade>();
//
//			InscricaoAtividade inscricao = new InscricaoAtividade();
//			for (int a = 0; a < lista.size(); a++) {
//			    int col = 0;
//			    Object[] colunas = (Object[]) lista.get(a);				
//			    inscricao = new InscricaoAtividade();
//			    inscricao.setId((Integer) colunas[col++]);
//			    inscricao.setPeriodoInicio((Date) colunas[col++]);
//			    inscricao.setPeriodoFim((Date) colunas[col++]);					
//			    inscricao.setQuantidadeVagas((Integer) colunas[col++]);
////			    inscricao.setNumeroConfirmados(((Long) colunas[col++]).intValue());
//			    inscricao.setNumeroAceitos(((Long) colunas[col++]).intValue());
//			    inscricao.setAtividade(new AtividadeExtensao((Integer) colunas[col++]));
//			    inscricao.getAtividade().setTipoAtividadeExtensao((TipoAtividadeExtensao) colunas[col++]);
//			    inscricao.getAtividade().setSequencia( colunas[col] != null ? (Integer) colunas[col++] : 0);
//			    inscricao.getAtividade().setProjeto(new Projeto((Integer) colunas[col++]));
//			    inscricao.getAtividade().getProjeto().setTitulo((String) colunas[col++]);
//			    inscricao.getAtividade().getProjeto().setAno((Integer) colunas[col++]);
//			    inscricao.getAtividade().getProjeto().setCoordenador(new MembroProjeto((Integer) colunas[col++]));
//			    inscricao.getAtividade().getProjeto().getCoordenador().setPessoa(new Pessoa((Integer) colunas[col++]));
//			    inscricao.getAtividade().getProjeto().getCoordenador().getPessoa().setNome(((String) colunas[col++]));
//			    inscricao.getSubAtividade().setId(((Integer) colunas[col++]));
//			    inscricao.getSubAtividade().setTitulo(((String) colunas[col++]));
//			    result.add(inscricao);
//			}
//
//			return result;
//
//		} catch (Exception e) {
//			throw new DAOException(e.getMessage(), e);
//		}
//	}
	
	
	
	
	
	
//	/**
//	 * Retorna a inscrição de uma determinada atividade que possua o período de inscrição aberto.
//	 * <p>Apenas inscrição para atividades, para sub atividades utilize o método: {@link this#findInscricaoAbertaBySubAtividade()}  </p>
//	 * 
//	 * 
//	 * @param idAtividade
//	 * @return
//	 * @throws DAOException
//	 */
//	public InscricaoAtividade findInscricaoAbertaByAtividade(Integer idAtividade) throws DAOException {
//		
//		try {
//			StringBuilder hql = new StringBuilder();
//			hql.append("SELECT inscricao.id as id, inscricao.periodoInicio as periodoInicio,");
//			hql.append(" inscricao.periodoFim as periodoFim, inscricao.sequencia as sequencia,");
//			hql.append(" inscricao.atividade as atividade FROM InscricaoAtividade inscricao");
//			hql.append(" WHERE inscricao.atividade.id = :idAtividade AND inscricao.ativo = trueValue() AND inscricao.subAtividade IS NULL ");
//			hql.append(" AND (cast(inscricao.periodoInicio, date) <= current_date");
//			hql.append(" AND cast(inscricao.periodoFim, date) >= current_date)");
//			
//			Query query = getSession().createQuery(hql.toString());
//			query.setInteger("idAtividade", idAtividade);
//			query.setMaxResults(1);
//			return (InscricaoAtividade) query.setResultTransformer(
//					new AliasToBeanResultTransformer(InscricaoAtividade.class)).uniqueResult();
//
//		} catch (Exception e) {
//			throw new DAOException(e.getMessage(), e);
//		}
//	}
//	
//	
//	/**
//	 * Retorna a inscrição de uma determinada subAtividade que possua o período de inscrição aberto.
//	 * 
//	 * @param idAtividade
//	 * @return
//	 * @throws DAOException
//	 */
//	public InscricaoAtividade findInscricaoAbertaBySubAtividade(Integer idSubAtividade) throws DAOException {
//		
//		try {
//			StringBuilder hql = new StringBuilder();
//			hql.append("SELECT inscricao.id as id, inscricao.periodoInicio as periodoInicio,");
//			hql.append(" inscricao.periodoFim as periodoFim, inscricao.sequencia as sequencia,");
//			hql.append(" inscricao.subAtividade as subAtividade  FROM InscricaoAtividade inscricao");
//			hql.append(" WHERE inscricao.subAtividade.id = :idSubAtividade AND inscricao.ativo = trueValue()");
//			hql.append(" AND (cast(inscricao.periodoInicio, date) <= current_date");
//			hql.append(" AND cast(inscricao.periodoFim, date) >= current_date)");
//			
//			Query query = getSession().createQuery(hql.toString());
//			query.setInteger("idSubAtividade", idSubAtividade);
//			query.setMaxResults(1);
//			return (InscricaoAtividade) query.setResultTransformer(
//					new AliasToBeanResultTransformer(InscricaoAtividade.class)).uniqueResult();
//
//		} catch (Exception e) {
//			throw new DAOException(e.getMessage(), e);
//		}
//	}
//	
//	
//	
//	/**
//	 * Retorna todas as inscrições em atividades ativas
//	 *
//	 * @return
//	 * @throws DAOException
//	 */
//	@SuppressWarnings("unchecked")
//	public Collection<InscricaoAtividade> findInscricoesSelecaoParticipantes(Integer idCoordenador) throws DAOException {
//		
//		try {
//			StringBuilder hql = new StringBuilder();
//			hql.append("SELECT inscricao.id as id, inscricao.periodoInicio as periodoInicio,");
//			hql.append(" inscricao.periodoFim as periodoFim, inscricao.quantidadeVagas as quantidadeVagas,");
//			hql.append(" inscricao.sequencia as sequencia,");			
//			hql.append(" atividade.id as idA,");
//			hql.append(" projeto.id as idPj, projeto.titulo as titulo,");
//			hql.append(" participante.id as idP, statusInscricao as statusInscricao, participante.ativo as ativo");
//			
//			hql.append(" FROM InscricaoAtividade inscricao ");			
//			hql.append(" INNER JOIN inscricao.atividade atividade");
//			hql.append(" INNER JOIN atividade.projeto projeto");
//			hql.append(" INNER JOIN projeto.coordenador coord ");
//			hql.append(" LEFT JOIN inscricao.inscricoesParticipantes participante");
//			hql.append(" LEFT JOIN participante.statusInscricao statusInscricao");
//			
//			hql.append(" WHERE inscricao.ativo = trueValue()");
//			hql.append(" AND coord.servidor.id = " + idCoordenador + " and coord.funcaoMembro = :idFuncaoCoordenador ");
//			hql.append(" AND ((cast(inscricao.periodoInicio, date) <= :dataAtual");
//			hql.append(" AND cast(inscricao.periodoFim, date) >= :dataAtual)");			
//			hql.append(" OR ((SELECT SUM(p.id) FROM InscricaoAtividadeParticipante p");
//			hql.append(" WHERE p.inscricaoAtividade.id=inscricao.id AND p.statusInscricao.id = :status) > 0))");			
//			hql.append(" ORDER BY inscricao.id");
//			
//			Query query = getSession().createQuery(hql.toString());
//			query.setInteger("status", StatusInscricaoParticipante.CONFIRMADO);
//			query.setDate("dataAtual", new Date());
//			query.setInteger("idFuncaoCoordenador", FuncaoMembro.COORDENADOR);
//			
//			List<Object> lista = query.list();			
//			
//			ArrayList<InscricaoAtividade> result = new ArrayList<InscricaoAtividade>();			
//			
//			int idOld = 0;
//			InscricaoAtividade inscricao = new InscricaoAtividade();
//			for (int a = 0; a < lista.size(); a++) {
//
//				int col = 0;
//				Object[] colunas = (Object[]) lista.get(a);				
//
//				int idNew = (Integer) colunas[col++];
//				if (idOld != idNew) {
//
//					idOld = idNew;
//					inscricao = new InscricaoAtividade();
//					inscricao.setId(idNew);
//					inscricao.setPeriodoInicio((Date) colunas[col++]);
//					inscricao.setPeriodoFim((Date) colunas[col++]);					
//					inscricao.setQuantidadeVagas((Integer) colunas[col++]);
//					inscricao.setSequencia((Integer) colunas[col++]);
//					inscricao.setAtividade(new AtividadeExtensao((Integer) colunas[col++]));
//					inscricao.getAtividade().setProjeto(new Projeto((Integer) colunas[col++]));
//					inscricao.getAtividade().getProjeto().setTitulo((String) colunas[col++]);
//					result.add(inscricao);
//				}
//
//				col = 8;
//				if (colunas[col] != null) {
//				    	InscricaoAtividadeParticipante iap = new InscricaoAtividadeParticipante();
//					iap.setId((Integer) colunas[col++]);
//					iap.setStatusInscricao((StatusInscricaoParticipante) colunas[col++]);
//					iap.setAtivo((Boolean) colunas[col++]);
//					inscricao.getInscricoesParticipantes().add(iap);					
//				}
//			}
//			
//			return result;
//
//		} catch (Exception e) {
//			throw new DAOException(e.getMessage(), e);
//		}
//	}
//	
//	/** busca por id de uma inscrição. */
//	public InscricaoAtividade findByPrimaryKey(int id) throws DAOException {
//		try {
//			StringBuilder hql = new StringBuilder();
//			hql.append("SELECT inscricao.id as id, inscricao.envioArquivoObrigatorio as envioArquivoObrigatorio, inscricao.periodoInicio as periodoInicio,");
//			hql.append(" inscricao.periodoFim as periodoFim, inscricao.quantidadeVagas as quantidadeVagas,");
//			hql.append(" inscricao.instrucoesInscricao as instrucoesInscricao, inscricao.sequencia as sequencia,");
//			hql.append(" inscricao.observacoes as observacoes, inscricao.atividade as atividade FROM");
//			hql.append(" InscricaoAtividade inscricao WHERE inscricao.id = :id AND inscricao.ativo = trueValue()");
//			
//			Query query = getSession().createQuery(hql.toString());
//			query.setInteger("id", id);
//			return (InscricaoAtividade) query.setResultTransformer(
//					new AliasToBeanResultTransformer(InscricaoAtividade.class)).uniqueResult();
//
//		} catch (Exception e) {
//			throw new DAOException(e.getMessage(), e);
//		}
//	}
//	
//	/** calcula o total de vagas reservadas para um curso/evento */ 
//	public Integer totalVagasInscricoes (Integer idAtividade) throws DAOException{
// 
//		 String sql = 	" select sum(ia.quantidade_vagas) " +
//		 				" from extensao.inscricao_atividade ia " +
//		 				" inner join extensao.atividade at using(id_atividade) " +
//		 				" inner join extensao.curso_evento ce on (ce.id_curso_evento = at.id_curso_evento) " +
//		 				" where id_atividade = "+ idAtividade +" and ia.ativo = true"; 
//		
//		return getJdbcTemplate().queryForInt(sql);
//		
//	}
//	
//	
//	/** calcula o total de vagas reservadas para um curso/evento */ 
//	public Integer totalVagasInscricoesSubAtividade (Integer idSubAtividade) throws DAOException{
// 
//		 String sql = 	" select sum(ia.quantidade_vagas) " +
//		 				" from extensao.inscricao_atividade ia " +
//		 				" inner join extensao.atividade at using(id_atividade) " +
//		 				" inner join extensao.curso_evento ce on (ce.id_curso_evento = at.id_curso_evento) " +
//		 				" where id_sub_atividade_extensao = "+ idSubAtividade +" and ia.ativo = true"; 
//		
//		return getJdbcTemplate().queryForInt(sql);
//		
//	}
//	
//	/**
//	 * Retorna todas as inscrições ativas a partir da Atividade
//	 * 
//	 * @param idAtividade
//	 * @return
//	 * @throws HibernateException
//	 * @throws DAOException
//	 */
//	public Collection<InscricaoAtividade> inscricaoAtividadeByAtividade(Integer idAtividade) throws DAOException{
//		String hql = "SELECT insc.atividade.id, insc.atividade.tipoAtividadeExtensao.id, insc.atividade.projeto.id, " +
//				" insc.atividade.projeto.titulo, insc.id, insc.dataCadastro, insc.periodoInicio, insc.periodoFim, " +
//				" insc.quantidadeVagas, part.id, part.statusInscricao.id " +
//				" FROM InscricaoAtividadeParticipante part " +
//				" INNER JOIN part.inscricaoAtividade insc " +
//				" WHERE insc.atividade.id = :idAtividade AND insc.ativo = trueValue() " +
//				" ORDER BY insc.periodoFim DESC ";
//		
//		Query query = getSession().createQuery(hql.toString());
//		query.setInteger("idAtividade", idAtividade);
//		
//		List lista = query.list();
//		
//		ArrayList<InscricaoAtividade> result = new ArrayList<InscricaoAtividade>();
//		Integer idInsc = 0;
//		InscricaoAtividade insc = new InscricaoAtividade();
//		
//		for(int i=0; i<lista.size(); i++){
//			int col = 0;
//			Object[] colunas = (Object[]) lista.get(i);
//			
//			if( !idInsc.equals((Integer)colunas[4]) ){
//				// Primeira inscrição inserida sempre é nula
//				result.add(insc);
//				
//				idInsc = (Integer)colunas[4];
//				
//				AtividadeExtensao atv = new AtividadeExtensao();
//				atv.setId((Integer) colunas[col++]);
//				atv.getTipoAtividadeExtensao().setId((Integer)colunas[col++]);
//				
//				Projeto pro = new Projeto();
//				pro.setId((Integer)colunas[col++]);
//				pro.setTitulo((String)colunas[col++]);
//				
//				atv.setProjeto(pro);
//				
//				insc = new InscricaoAtividade();
//				insc.setId((Integer) colunas[col++]);
//				insc.setDataCadastro((Date) colunas[col++] );
//				insc.setPeriodoInicio((Date) colunas[col++]);
//				insc.setPeriodoFim((Date)colunas[col++]);
//				insc.setQuantidadeVagas((Integer)colunas[col++]);
//				insc.setAtividade(atv);
//				
//			}
//			col=9;
//				
//			InscricaoAtividadeParticipante part = new InscricaoAtividadeParticipante();
//			part.setId((Integer)colunas[col++]);
//			part.getStatusInscricao().setId((Integer)colunas[col++]);
//			insc.getInscricoesParticipantes().add(part);
//			
//		}
//		// Adiciona a última inscrição
//		result.add(insc);
//		// Remove a primeira inscrição nula
//		result.remove(0);
//		return result;
//	}
//	
//	/**
//	 * Retorna todas as inscrições ativas a partir da Atividade
//	 * 
//	 * @param idAtividade
//	 * @return
//	 * @throws HibernateException
//	 * @throws DAOException
//	 */
//	public Collection<InscricaoAtividade> inscricaoAtividadeBySubAtividade(Integer idSubAtividade) throws DAOException{
//		String hql = "SELECT sub.id, sub.titulo, sub.atividade.id, sub.atividade.tipoAtividadeExtensao.id, " +
//				" sub.atividade.projeto.id, sub.atividade.projeto.titulo, insc.id, insc.dataCadastro, insc.periodoInicio, insc.periodoFim, " +
//				" insc.quantidadeVagas, part.id, part.statusInscricao.id " +
//				" FROM InscricaoAtividadeParticipante part " +
//				" INNER JOIN part.inscricaoAtividade insc " +
//				" INNER JOIN insc.subAtividade sub " +
//				" WHERE insc.subAtividade.id = :idSubAtividade AND insc.ativo = trueValue() " +
//				" ORDER BY insc.periodoFim DESC ";
//		
//		Query query = getSession().createQuery(hql.toString());
//		query.setInteger("idSubAtividade", idSubAtividade);
//		
//		List lista = query.list();
//		
//		ArrayList<InscricaoAtividade> result = new ArrayList<InscricaoAtividade>();
//		Integer idInsc = 0;
//		InscricaoAtividade insc = new InscricaoAtividade();
//		
//		for(int i=0; i<lista.size(); i++){
//			int col = 0;
//			Object[] colunas = (Object[]) lista.get(i);
//			
//			if( !idInsc.equals((Integer)colunas[6]) ){
//				// Primeira inscrição inserida sempre é nula
//				result.add(insc);
//				
//				idInsc = (Integer)colunas[6];
//				
//				SubAtividadeExtensao sub = new SubAtividadeExtensao();
//				sub.setId((Integer) colunas[col++]);
//				sub.setTitulo((String) colunas[col++]);
//				
//				AtividadeExtensao atv = new AtividadeExtensao();
//				atv.setId((Integer) colunas[col++]);
//				atv.getTipoAtividadeExtensao().setId((Integer)colunas[col++]);
//				
//				sub.setAtividade(atv);
//				
//				Projeto pro = new Projeto();
//				pro.setId((Integer)colunas[col++]);
//				pro.setTitulo((String)colunas[col++]);
//				
//				atv.setProjeto(pro);
//				
//				insc = new InscricaoAtividade();
//				insc.setId((Integer) colunas[col++]);
//				insc.setDataCadastro((Date) colunas[col++] );
//				insc.setPeriodoInicio((Date) colunas[col++]);
//				insc.setPeriodoFim((Date)colunas[col++]);
//				insc.setQuantidadeVagas((Integer)colunas[col++]);
//				insc.setSubAtividade(sub);
//				
//			}
//			col=11;
//				
//			InscricaoAtividadeParticipante part = new InscricaoAtividadeParticipante();
//			part.setId((Integer)colunas[col++]);
//			part.getStatusInscricao().setId((Integer)colunas[col++]);
//			insc.getInscricoesParticipantes().add(part);
//			
//		}
//		// Adiciona a última inscrição
//		result.add(insc);
//		// Remove a primeira inscrição nula
//		result.remove(0);
//		return result;
//	}
//	
//	/**
//	 * <p>Retorna todas as inscrições ativas a partir do coordenador</p>
//	 * 
//	 * 
//	 * <p> Esse método é a junção dos métodos {@link AtividadeExtensaoDao#findCursoEventoByCoordenador(Servidor)} 
//	 * com o método {@link this#inscricaoAtividadeByAtividade(Integer)}, para agilizar o processo de pesquisa do usuário </p>
//	 * 
//	 * @param idAtividade
//	 * @return
//	 * @throws HibernateException
//	 * @throws DAOException
//	 */
//	public Collection<InscricaoAtividade> findAllInscricaoAtividadeByCoordenador(Servidor servidor) throws DAOException{
//		
//		
//		String hql = "SELECT insc.atividade.id, insc.atividade.tipoAtividadeExtensao.id, insc.atividade.projeto.id, " +
//				" insc.atividade.projeto.titulo, insc.id, insc.dataCadastro, insc.periodoInicio, insc.periodoFim, " +
//				" insc.quantidadeVagas, part.id, part.statusInscricao.id " +
//				" FROM InscricaoAtividadeParticipante part " +
//				" INNER JOIN part.inscricaoAtividade insc " +
//				" INNER JOIN insc.atividade atividadeExtensao " +
//				" INNER JOIN atividadeExtensao.projeto.coordenador coordenador "+
//				" WHERE insc.ativo = trueValue() " +
//				" AND coordenador.servidor.id = :idServidor and coordenador.ativo = trueValue() " +
//				" AND atividadeExtensao.projeto.tipoProjeto.id = :idTipoProjeto "+
//				" AND atividadeExtensao.situacaoProjeto.id in (:idSituacaoExecucao) "+
//				" AND atividadeExtensao.cursoEventoExtensao IS NOT NULL"+		
//				" ORDER BY insc.periodoFim DESC ";
//		
//		Query query = getSession().createQuery(hql.toString());
//		query.setInteger("idServidor", servidor.getId());
//		query.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);
//		query.setParameterList("idSituacaoExecucao", TipoSituacaoProjeto.PROJETOS_GRUPO_EM_EXECUCAO);
//		
//		List lista = query.list();
//		
//		ArrayList<InscricaoAtividade> result = new ArrayList<InscricaoAtividade>();
//		Integer idInsc = 0;
//		InscricaoAtividade insc = new InscricaoAtividade();
//		
//		for(int i=0; i<lista.size(); i++){
//			int col = 0;
//			Object[] colunas = (Object[]) lista.get(i);
//			
//			if( !idInsc.equals((Integer)colunas[4]) ){
//				// Primeira inscrição inserida sempre é nula
//				result.add(insc);
//				
//				idInsc = (Integer)colunas[4];
//				
//				AtividadeExtensao atv = new AtividadeExtensao();
//				atv.setId((Integer) colunas[col++]);
//				atv.getTipoAtividadeExtensao().setId((Integer)colunas[col++]);
//				
//				Projeto pro = new Projeto();
//				pro.setId((Integer)colunas[col++]);
//				pro.setTitulo((String)colunas[col++]);
//				
//				atv.setProjeto(pro);
//				
//				insc = new InscricaoAtividade();
//				insc.setId((Integer) colunas[col++]);
//				insc.setDataCadastro((Date) colunas[col++] );
//				insc.setPeriodoInicio((Date) colunas[col++]);
//				insc.setPeriodoFim((Date)colunas[col++]);
//				insc.setQuantidadeVagas((Integer)colunas[col++]);
//				insc.setAtividade(atv);
//				
//			}
//			col=9;
//				
//			InscricaoAtividadeParticipante part = new InscricaoAtividadeParticipante();
//			part.setId((Integer)colunas[col++]);
//			part.getStatusInscricao().setId((Integer)colunas[col++]);
//			insc.getInscricoesParticipantes().add(part);
//			
//		}
//		// Adiciona a última inscrição
//		result.add(insc);
//		// Remove a primeira inscrição nula
//		result.remove(0);
//		return result;
//	}
//	
//	
//	/**
//	 * <p>Retorna todas as inscrições ativas de  subAtividade a partir do coordenador</p>
//	 * 
//	 * 
//	 * 
//	 * 
//	 * @param idAtividade
//	 * @return
//	 * @throws HibernateException
//	 * @throws DAOException
//	 */
//	public Collection<InscricaoAtividade> findAllInscricaoSubAtividadeByCoordenador(Servidor servidor) throws DAOException{
//		
//		
//		String hql = "SELECT   atividadeExtensao.id, tipoAtv.id, atividadeExtensao.projeto.id, " +
//				" atividadeExtensao.projeto.titulo, insc.id, insc.sequencia, insc.dataCadastro, insc.periodoInicio, insc.periodoFim, " +
//				" insc.quantidadeVagas, subAtv.titulo, subAtv.id, part.id, part.statusInscricao.id   " +				
//				" FROM InscricaoAtividade insc " +
//				" INNER JOIN insc.subAtividade  subAtv " +
//				" INNER JOIN subAtv.atividade atividadeExtensao " +
//				" INNER JOIN atividadeExtensao.projeto.coordenador coordenador "+
//				" INNER JOIN atividadeExtensao.tipoAtividadeExtensao tipoAtv "+
//				" INNER JOIN insc.inscricoesParticipantes part " +				
//				" WHERE insc.ativo = trueValue() " +
//				" AND coordenador.servidor.id = :idServidor and coordenador.ativo = trueValue() " +
//				" AND atividadeExtensao.projeto.tipoProjeto.id = :idTipoProjeto "+
//				" AND atividadeExtensao.situacaoProjeto.id in (:idSituacaoExecucao) "+
//				" AND atividadeExtensao.cursoEventoExtensao IS NOT NULL "+		
//				" ORDER BY insc.id desc, subAtv.id DESC ";
//		
//		Query query = getSession().createQuery(hql.toString());
//		query.setInteger("idServidor", servidor.getId());
//		query.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);
//		query.setParameterList("idSituacaoExecucao", TipoSituacaoProjeto.PROJETOS_GRUPO_EM_EXECUCAO);
//		
//		List lista = query.list();
//		
//		ArrayList<InscricaoAtividade> result = new ArrayList<InscricaoAtividade>();
//		Integer idInsc = 0;
//		InscricaoAtividade insc = new InscricaoAtividade();
//		
//		for(int i=0; i<lista.size(); i++){
//			int col = 0;
//			Object[] colunas = (Object[]) lista.get(i);
//			
//			if( !idInsc.equals((Integer)colunas[4]) ){
//				// Primeira inscrição inserida sempre é nula
//				result.add(insc);
//				
//				idInsc = (Integer)colunas[4];
//				
//				AtividadeExtensao atv = new AtividadeExtensao();
//				atv.setId((Integer) colunas[col++]);
//				atv.getTipoAtividadeExtensao().setId((Integer)colunas[col++]);
//				
//				Projeto pro = new Projeto();
//				pro.setId((Integer)colunas[col++]);
//				pro.setTitulo((String)colunas[col++]);
//				
//				atv.setProjeto(pro);
//				
//				insc = new InscricaoAtividade();
//				insc.setId((Integer) colunas[col++]);
//				insc.setSequencia((Integer) colunas[col++]);
//				insc.setDataCadastro((Date) colunas[col++] );
//				insc.setPeriodoInicio((Date) colunas[col++]);
//				insc.setPeriodoFim((Date)colunas[col++]);
//				insc.setQuantidadeVagas((Integer)colunas[col++]);
//				insc.getSubAtividade().setTitulo((String)colunas[col++]);
//				insc.getSubAtividade().setId((Integer)colunas[col++]);
//				
//				insc.setAtividade(atv);
//				
//			}
//			col=12;
//				
//			InscricaoAtividadeParticipante part = new InscricaoAtividadeParticipante();
//			part.setId((Integer)colunas[col++]);
//			part.getStatusInscricao().setId((Integer)colunas[col++]);
//			insc.getInscricoesParticipantes().add(part);
//			
//		}
//		// Adiciona a última inscrição
//		result.add(insc);
//		// Remove a primeira inscrição nula
//		result.remove(0);
//		return result;
//	}
//	
//	
//	
//	/**
//	 * Retornas as informações de a "Inscrição Atividade"  exige ou não a combrança de taxa
//	 * 
//	 * @param idInscricao
//	 * @return
//	 * @throws DAOException
//	 */
//	@SuppressWarnings("unchecked")
//	public CursoEventoExtensao findCursoEventoExtensaoByInscricaoAtividade(int idInscricaoAtividade) throws DAOException {
//		
//		try {
//
//			String projecao = " inscricao.atividade.cursoEventoExtensao.id " +
//					", inscricao.atividade.cursoEventoExtensao.cobrancaTaxaMatricula " +
//					", inscricao.atividade.cursoEventoExtensao.taxaMatricula ";
//			
//			String hql = " SELECT  "+projecao
//			+" FROM InscricaoAtividade inscricao "
//			+" WHERE inscricao.id = :idInscricao";
//						
//			
//			Query query = getSession().createQuery(hql);
//			query.setInteger("idInscricao", idInscricaoAtividade);
//			
//			List<Object[]> dados = query.list();
//			if(dados.size() == 1){
//				Object[] dadoCursoEvento = dados.get(0);
//				CursoEventoExtensao cursoEvento =  new CursoEventoExtensao();
//				cursoEvento.setId((Integer)dadoCursoEvento[0]);
//				cursoEvento.setCobrancaTaxaMatricula((Boolean)dadoCursoEvento[1]);
//				cursoEvento.setTaxaMatricula((Double)dadoCursoEvento[2]);
//				return cursoEvento;
//			}else
//				return null;
//
//		} catch (Exception e) {
//			throw new DAOException(e.getMessage(), e);
//		}
//	}
//	
//	
//	/**
//	 * Retorna a quantidade de inscritos de um processo seletivo ativos e que estejam aberto.
//	 * 
//	 * @param adesao
//	 * @return
//	 * @throws DAOException
//	 */
//	public int findQtdInscritosByQuestionario(Integer idQuestionario) throws DAOException {
//		
//		Date dataAtual = new Date(System.currentTimeMillis());
//		return getJdbcTemplate().queryForInt(
//				" SELECT COUNT(i.id_inscricao_atividade_participante) " +
//				" FROM extensao.inscricao_atividade_participante i " +
//				" INNER JOIN extensao.inscricao_atividade ia USING(id_inscricao_atividade) " +				
//				" WHERE ia.periodo_inicio <= '" + dataAtual + "' AND ia.periodo_fim >= '" + dataAtual + "'" +
//				" AND ia.ativo = trueValue() AND ia.id_questionario = " + idQuestionario);
//
//	}
//	
//	
//	/** calcula a soma das vagas reservadas para um mini-curso */ 
//	public Integer somaVagasInscricoesSubAtividade (Integer idSubAtividade) throws DAOException{
// 
//		 String sql = 	" select sum(ia.quantidade_vagas) " +
//		 				" from extensao.inscricao_atividade ia " +
//		 				" where id_sub_atividade_extensao = "+ idSubAtividade +" and ia.ativo = true"; 
//		
//		return getJdbcTemplate().queryForInt(sql);
//		
//	}
}
