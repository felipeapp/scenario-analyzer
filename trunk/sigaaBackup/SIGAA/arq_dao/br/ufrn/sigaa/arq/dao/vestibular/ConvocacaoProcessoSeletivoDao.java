/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '21/12/2010'
 *
 */
package br.ufrn.sigaa.arq.dao.vestibular;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivo;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/**
 * Classe respons�vel por consultas especializadas �s Convoca��es do Processo Seletivo.
 * 
 * @author guerethes
 */
public class ConvocacaoProcessoSeletivoDao extends GenericSigaaDAO {
	
	/**
	 * Procurar os convocados pelo Processo Seletivo.
	 * @param idProcessosSeletivo
	 * @return
	 * @throws DAOException
	 */
	public Collection<ConvocacaoProcessoSeletivo> findByProcessoSeletivo(Integer idProcessosSeletivo) throws DAOException {
		
		Criteria c = getSession().createCriteria(ConvocacaoProcessoSeletivo.class);
		c.add(Restrictions.eq("processoSeletivo.id", idProcessosSeletivo));
		c.addOrder(Order.asc("dataConvocacao"));
		@SuppressWarnings("unchecked")
		Collection<ConvocacaoProcessoSeletivo> lista = c.list();
		return lista;
		
	}
	
	/**
	 * Procurar Convoca��o de Processo Seletivo por descri��o.
	 * @param psVestibular
	 * @param descricao
	 * @return
	 * @throws DAOException
	 */
	public ConvocacaoProcessoSeletivo findByProcessoSeletivoAndDescricao(ProcessoSeletivoVestibular psVestibular, String descricao) throws DAOException {
		
			Criteria c = getSession().createCriteria(ConvocacaoProcessoSeletivo.class);
			c.add(Restrictions.eq("processoSeletivo.id", psVestibular.getId()));
			c.add(Restrictions.eq("descricao", descricao));
			c.addOrder(Order.asc("dataConvocacao"));
			return (ConvocacaoProcessoSeletivo) c.uniqueResult();
		
	}
	
	/**
	 * Retorna uma lista de matrizes curricular para as quais haver� convoca��o
	 * de candidatos aprovados no processo seletivo indicado para preenchimento
	 * de vagas.
	 * 
	 * @param idProcessosSeletivo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<MatrizCurricular> findMatrizesConvocacao(Integer idProcessosSeletivo) throws HibernateException, DAOException{
		String projecao = "matriz.id, matriz.curso.nome, matriz.curso.municipio.nome, matriz.curso.modalidadeEducacao.descricao," +
				" matriz.habilitacao.nome, matriz.enfase.nome, matriz.turno.sigla";
		String hql = "select " + projecao +
				" from MatrizCurricular matriz" +
				" left join matriz.habilitacao" +
				" left join matriz.enfase" +
				" where matriz.id in (" +
				"   select distinct opcaoAprovacao" +
				"   from ResultadoClassificacaoCandidato" +
				"   where inscricaoVestibular.processoSeletivo.id = :idProcessoSeletivo)" +
				" order by matriz.curso.municipio.nome, matriz.curso.nome";
		Query q = getSession().createQuery(hql);
		q.setInteger("idProcessoSeletivo", idProcessosSeletivo);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		Collection<MatrizCurricular> matrizes = HibernateUtils.parseTo(lista, projecao, MatrizCurricular.class, "matriz");
		return matrizes;
	}

}