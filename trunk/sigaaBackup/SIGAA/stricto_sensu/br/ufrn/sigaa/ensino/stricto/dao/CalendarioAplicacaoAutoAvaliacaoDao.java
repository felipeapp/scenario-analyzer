/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 10/08/2013
 *
 */
package br.ufrn.sigaa.ensino.stricto.dao;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.stricto.dominio.CalendarioAplicacaoAutoAvaliacao;

/**
 * Realiza consultas específicas ao Calendário de Aplicação da Auto Avaliação 
 * @author Édipo Elder F. de Melo
 *
 */
public class CalendarioAplicacaoAutoAvaliacaoDao extends GenericSigaaDAO {

	/** Retorna os calendários ativos para preenchimento no período.
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Collection<CalendarioAplicacaoAutoAvaliacao> findAllAtivosPeriodo() throws HibernateException, DAOException {
		String sql = "from CalendarioAplicacaoAutoAvaliacao" +
				" where ativo = true" +
				" and dataInicio <= :hoje" +
				" and dataFim >= :hoje";
		Query q = getSession().createQuery(sql);
		q.setDate("hoje", new Date());
		@SuppressWarnings("unchecked")
		Collection<CalendarioAplicacaoAutoAvaliacao> lista = q.list();
		return lista;
	}

	/**
	 * Retorna uma coleção de calendários de aplicação da Auto Avaliação com a quantidade de respostas até o momento.
	 * @throws DAOException 
	 * @throws HibernateException 
	 * 
	 */
	public Collection<CalendarioAplicacaoAutoAvaliacao> findAllOtimizado(int tipoAutoAplicacao) throws HibernateException, DAOException {
		return findAllOtimizado(tipoAutoAplicacao, 0, 0);
	}
	
	/**
	 * Retorna uma coleção de calendários de aplicação da Auto Avaliação com a quantidade de respostas até o momento.
	 * @param tipoAutoAplicacao
	 * @param idPrograma
	 * @param idCurso
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<CalendarioAplicacaoAutoAvaliacao> findAllOtimizado(int tipoAutoAplicacao, int idPrograma, int idCurso) throws HibernateException, DAOException {
		String projecao = "cal.id_calendario_aplicacao_auto_avaliacao as cal.id," +
				" cal.id_questionario as cal.questionario.id," +
				" q.titulo as cal.questionario.titulo," +
				" cal.data_inicio as cal.dataInicio," +
				" cal.data_fim as cal.dataFim," +
				" count(resp.id_respostas_auto_avaliacao) as cal.qtdRespostas";
		String sql = "select "+
				HibernateUtils.removeAliasFromProjecao(projecao)+
				" from stricto_sensu.calendario_aplicacao_auto_avaliacao cal" +
				" inner join questionario.questionario q using (id_questionario)" +
				" left join stricto_sensu.respostas_auto_avaliacao resp using (id_calendario_aplicacao_auto_avaliacao)" +
				" left join stricto_sensu.calendario_aplicacao_curso using (id_calendario_aplicacao_auto_avaliacao)" +
				" left join stricto_sensu.calendario_aplicacao_programa using (id_calendario_aplicacao_auto_avaliacao)" +
				" where cal.ativo = true" +
				" and q.id_tipo_questionario = :tipoAutoAplicacao" +
				(idCurso != 0 || idPrograma != 0 ? " and (aplicavel_a_todos" : "")+
				(idCurso > 0 ? " or calendario_aplicacao_curso.id_curso = :idCurso)" : "")+
				(idPrograma > 0? " or calendario_aplicacao_programa.id_unidade = :idPrograma)": "") + 
				" group by 1,2,3,4,5" +
				" order by 4 desc, 5 desc";
		Query q = getSession().createSQLQuery(sql);
		q.setInteger("tipoAutoAplicacao", tipoAutoAplicacao);
		if (idPrograma > 0) q.setInteger("idPrograma", idPrograma);
		if (idCurso > 0) q.setInteger("idCurso", idCurso);
		@SuppressWarnings("unchecked")
		List<Object[]>lista = q.list();
		// converte de bigint para integer
		for (Object[] obj : lista)
			obj[5] = ((BigInteger)obj[5]).intValue();
		return HibernateUtils.parseTo(lista, projecao, CalendarioAplicacaoAutoAvaliacao.class, "cal");
	}

}
