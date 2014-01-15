/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 08/11/2012
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
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividade;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.ModalidadeParticipantePeriodoInscricaoAtividade;
import br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante;
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.questionario.dominio.Questionario;

/**
 *
 * <p>Dao exclusivo para consultas em inscrições de sub atividades </p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
public class InscricaoSubAtividadeExtensaoDao  extends GenericSigaaDAO{

	
	
	
	/**
	 * <p>Retorna as inscrições ativias do participante na sub atividade da atividade passada.</p>
	 * 
	 * @param idsGRUsCursosEEventosQuitadas
	 * @throws DAOException 
	 * @throws  
	 */
	public List<InscricaoAtividadeParticipante> findInscricoesAtivasParticipanteSubAtividadeDaAtividade(int idAtividade, int idCadastroParticipante) throws DAOException {
		
		String projecao = " participante.id, participante.statusInscricao ";
		
		String hql = " SELECT " +projecao+
				 " FROM InscricaoAtividadeParticipante participante "
				+" WHERE participante.inscricaoAtividade.subAtividade.atividade.id = :idAtividade "
				+" AND participante.cadastroParticipante.id = :idCadastroParticipante "
				+" AND participante.statusInscricao NOT IN "+UFRNUtils.gerarStringIn(StatusInscricaoParticipante.getStatusInativos());
		
	
			
		Query q = getSession().createQuery(hql);
		q.setInteger("idAtividade", idAtividade);
		q.setInteger("idCadastroParticipante", idCadastroParticipante);
		
		@SuppressWarnings("unchecked")
		Collection<InscricaoAtividadeParticipante> parseTo = HibernateUtils.parseTo(q.list(), projecao, InscricaoAtividadeParticipante.class, "participante");
		return new ArrayList<InscricaoAtividadeParticipante>( parseTo);
	
	}
	
	
	
	/**
	 * Retorna os períodos de inscrições existentes para a sub atividade informada.
	 * 
	 * @param idAtividade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<InscricaoAtividade> findAllPeriodosInscricoesDaSubAtividade(int  idSubAtividade) throws DAOException {
		
			StringBuilder hql = new StringBuilder();
			hql.append(" SELECT inscricao.id as id, inscricao.periodoInicio as periodoInicio, inscricao.periodoFim as periodoFim, ");
			hql.append(" inscricao.quantidadeVagas as quantidadeVagas, inscricao.metodoPreenchimento, ");
			hql.append(" inscricao.sequencia as sequencia, atividade.id as idA, atividade.tipoAtividadeExtensao.id, "); // informações para gerar o código da inscrição 
			
			// Conta a quantidade de inscrições APROVADAS para essa atividade  //
			hql.append("( SELECT COUNT( DISTINCT inscricaoParticipante.id ) " +
				" FROM InscricaoAtividadeParticipante inscricaoParticipante 	" +
				" INNER JOIN inscricaoParticipante.inscricaoAtividade inscricaoAtividade " +
				" WHERE inscricaoAtividade.id = inscricao.id AND inscricaoParticipante.statusInscricao.id = :idStatusAprovado )  as numeroInscritosAprovados,");
			
			// Conta a quantidade de inscrições REALIZADAS AINDA NÃO APROVADAS para essa atividade  //
			hql.append("( SELECT COUNT( DISTINCT inscricaoParticipante.id ) " +
				" FROM InscricaoAtividadeParticipante inscricaoParticipante 	" +
				" INNER JOIN inscricaoParticipante.inscricaoAtividade inscricaoAtividade " +
				" WHERE inscricaoAtividade.id = inscricao.id AND inscricaoParticipante.statusInscricao.id = :idStatusInscrito ) as numeroInscritos ");
			
			hql.append(" FROM InscricaoAtividade inscricao ");			
			hql.append(" INNER JOIN inscricao.subAtividade subAtiv ");
			hql.append(" INNER JOIN subAtiv.atividade atividade");

			hql.append(" WHERE inscricao.ativo = trueValue() "); // inscrição não canceladas 
			hql.append(" AND subAtiv.id = :idSubAtividade ");
	
			hql.append(" ORDER BY inscricao.periodoFim ");
			
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idSubAtividade", idSubAtividade);
			query.setInteger("idStatusAprovado", StatusInscricaoParticipante.APROVADO);
			query.setInteger("idStatusInscrito", StatusInscricaoParticipante.INSCRITO);
			
			List<Object> lista = query.list();			
			
			ArrayList<InscricaoAtividade> result = new ArrayList<InscricaoAtividade>();			
			
			InscricaoAtividade inscricao = new InscricaoAtividade();
			for (Object dados : lista) {

				Object[] colunas = (Object[]) dados;
				
				inscricao = new InscricaoAtividade();
				inscricao.setSubAtividade(new SubAtividadeExtensao(idSubAtividade));
				
				inscricao.setId((Integer) colunas[0]);
				inscricao.setPeriodoInicio((Date) colunas[1]);
				inscricao.setPeriodoFim((Date) colunas[2]);					
				inscricao.setQuantidadeVagas((Integer) colunas[3]);
				inscricao.setMetodoPreenchimento((Integer) colunas[4]);
				inscricao.setSequencia((Integer) colunas[5]);
				inscricao.getSubAtividade().setAtividade(new AtividadeExtensao((Integer) colunas[6]));
				inscricao.getSubAtividade().getAtividade().setTipoAtividadeExtensao( new TipoAtividadeExtensao(   (Integer) colunas[7])  );
			
				inscricao.setQuantidadeInscritosAprovados(  ((Long) colunas[8] ).intValue() ); // esse aqui sempre existe, sendo com confirmação ou sem
				inscricao.setQuantidadeInscritos(   ((Long) colunas[9] ).intValue() );
				
				
				result.add(inscricao);
			}
			
			return result;			
	}

	
	
	/***
	 * <p>Método que retorna a maior quantidade de vagas abertas para as subs atividade da atividade passa.</p>
	 * <p>Esse somantório não pode ser maior que a quantidade de vagas abertas para a atividade.</p>
	 * 
	 * 
	 * <p>Por exemplo:<br/>
	 * 
	 *  Caso a atividade tenha 500 vagas abertas a maior quantidade de vagas de  todas as sub sub atividade pode ser no máximo 500:<br/>
	 * </p>
	 *  
	 *  
	 *  <p>
	 * 		Pode ter:<br/>
	 * 
	 *  sub atividade 1 com 500 vagas<br/>
	 *  sub atividade 2 com 400 vagas<br/>
	 *  sub atividade 3 com 300 vagas<br/>
	 *  
	 * </p>
	 * 
	 * <p>
	 * 		Não pode ter:<br/>
	 * 
	 * 		sub atividade 1 com 501 vagas<br/> 
	 * </p>
	 *
	 * @param idAtividade
	 * @return
	 * @throws DAOException
	 */
	public Integer maiorQuantidadeVagasSubAtividadesDaAtividade(int idAtividade) throws DAOException {
	
		String hql = " SELECT SUM (inscricao.quantidadeVagas ) as totalVagas " +
				" FROM InscricaoAtividade  inscricao "+
				" INNER JOIN inscricao.subAtividade sub "+
				" WHERE inscricao.ativo = :true AND sub.ativo = :true AND sub.atividade.id = :idAtividade  "+
				" GROUP BY sub.id "+          // Soma a quantidade de vagas de cada sub atividade, independente do período de inscrição aberto
				" ORDER BY 1 DESC"; // A maior quantidade de vagas primeiro 
		
	
		
		Query query = getSession().createQuery(hql);
		query.setInteger("idAtividade", idAtividade);
		
		query.setBoolean("true", true);
		query.setMaxResults(1); // Retorna somente a maior quantidade
		
		Long qtdMaiorVasgasSubAtividade = (Long) query.uniqueResult();
		
		if(qtdMaiorVasgasSubAtividade == null)
			return 0;
		else		
			return qtdMaiorVasgasSubAtividade.intValue();				
	}
	
	
	
	/***
	 * <p>Retorna as inscrições ativas para a sub atividade</p>
	 * 
	 *
	 * @param idAtividade
	 * @return
	 * @throws DAOException
	 */
	public List<InscricaoAtividade> findInscricoesSubAtividades(int idSubAtividade) throws DAOException {
	
		String hql = " SELECT inscricao.id, inscricao.periodoInicio,  inscricao.periodoFim " +
				" FROM InscricaoAtividade  inscricao "+
				" INNER JOIN inscricao.subAtividade sub "+
				" WHERE inscricao.ativo = :true AND sub.ativo = :true AND sub.id = :idSubAtividade  "; 
		
		Query query = getSession().createQuery(hql);
		query.setInteger("idSubAtividade", idSubAtividade);
		query.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<Object> lista = query.list();			
		
		ArrayList<InscricaoAtividade> result = new ArrayList<InscricaoAtividade>();			
		
		
		for (Object dados : lista) {

			Object[] colunas = (Object[]) dados;
		
			InscricaoAtividade inscricao = new InscricaoAtividade();
			inscricao = new InscricaoAtividade();
			inscricao.setSubAtividade(new SubAtividadeExtensao(idSubAtividade));
			
			inscricao.setId((Integer) colunas[0]);
			inscricao.setPeriodoInicio((Date) colunas[1]);
			inscricao.setPeriodoFim((Date) colunas[2]);		
			
			result.add(inscricao);
		}
		
		return result;
	}
	
	
	
	
	/***
	 * <p>Retorna as inscrições aberts para a atividade</p>
	 * 
	 *
	 * @param idAtividade
	 * @return
	 * @throws DAOException
	 */
	public InscricaoAtividade findInformacoesInscricoesSubAtividadeParaAlteracao(int idInscricao) throws DAOException {
	
		String hql = " SELECT inscricao.periodoInicio,  inscricao.periodoFim, " +
				" inscricao.quantidadeVagas, inscricao.metodoPreenchimento, inscricao.sequencia, " +
		        " inscricao.instrucoesInscricao, inscricao.observacoes, inscricao.envioArquivoObrigatorio, "+
				" questionario.id, inscricao.cobrancaTaxaMatricula, inscricao.dataVencimentoGRU ,"+
		        " modalidadesParticipantes.id, modalidadesParticipantes.taxaMatricula, modalidadesParticipantes.ativo, modalidade.id, modalidade.nome, modalidade.ativo "+
				" FROM InscricaoAtividade  inscricao "+
				" INNER JOIN inscricao.subAtividade subatividade "+
				" LEFT JOIN inscricao.modalidadesParticipantes modalidadesParticipantes "+ // a classe associativa
				" LEFT JOIN modalidadesParticipantes.modalidadeParticipante modalidade "+
				" LEFT JOIN inscricao.questionario questionario "+
				
				" WHERE inscricao.id = :idInscricao ANd inscricao.ativo = :true "; 
		
		Query query = getSession().createQuery(hql);
		query.setInteger("idInscricao", idInscricao);
		query.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<Object> lista = query.list();			
		
		InscricaoAtividade inscricao = new InscricaoAtividade();
		
		for (Object dados : lista) {

			Object[] colunas = (Object[]) dados;
			
			// se a primeira vez cria a inscrição 
			if(inscricao.getId() <= 0 ){
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
			
			// Depois só adiciona a modalidade //
			if(colunas[11] != null && (Boolean) colunas[13].equals(true) ){
				inscricao.adicionaMolidadeParticipante(
					new ModalidadeParticipantePeriodoInscricaoAtividade( (Integer) colunas[11], (BigDecimal) colunas[12],  (Boolean) colunas[13], 
							(Integer) colunas[14], (String) colunas[15], (Boolean) colunas[16],
									new InscricaoAtividade(idInscricao)));
			}
			
		}
		
		return inscricao;
	}
	
	
}
