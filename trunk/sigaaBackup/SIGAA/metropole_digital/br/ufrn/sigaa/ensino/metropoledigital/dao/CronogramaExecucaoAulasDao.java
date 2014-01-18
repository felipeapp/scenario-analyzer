/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 22/03/2013
 *
 */
package br.ufrn.sigaa.ensino.metropoledigital.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.CargaHorariaSemanalDisciplina;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.CronogramaExecucaoAulas;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.PeriodoAvaliacao;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;
/**
 * 
 * DAO com consultas utilizadas no cadastramento do cronograma de execução do módulo técnico do IMD. 
 * 
 * @author Rafael Silva
 * @author Rafael Barros
 *
 */
public class CronogramaExecucaoAulasDao extends GenericSigaaDAO{
	
	/**
	 * Realiza pesquisa através da chave primária do cronograma de Execução.
	 * 
	 * @param id
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public CronogramaExecucaoAulas findByPrimaryKey(Integer id) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("select ce from CronogramaExecucaoAulas ce " +
					" inner join ce.modulo m" +
					" inner join ce.curso c" +
					" inner join ce.unidadeTempo tu" +
					" inner join ce.periodosAvaliacao pa" +
					" inner join pa.chsdList chsd" +
					" Where ce.id=:id");
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("id", id);
			
			@SuppressWarnings("unchecked")
			List<CronogramaExecucaoAulas> lista = q.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
			if (lista.size()>0) {
				return (CronogramaExecucaoAulas) lista.get(0);
			}else{
				return new CronogramaExecucaoAulas();
			}
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Lista os cronogramas de execução contendo o módulo e/ou ano e período informado 
	 * 
	 * @param idModulo
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<CronogramaExecucaoAulas> findCronograma(Integer idModulo, Integer ano, Integer periodo) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(CronogramaExecucaoAulas.class);
			Criteria cModulo = c.createCriteria("modulo");
			c.createCriteria("curso");
			c.createCriteria("unidadeTempo");
						
			
			if ((ano != null && ano !=0) && (periodo != null && periodo!=0)){
				c.add(Expression.eq("ano", ano));
				c.add(Expression.eq("periodo", periodo));
			}
			
			if (idModulo != null && idModulo!=0) {
				cModulo.add(Expression.eq("id", idModulo));
			}
			
			
			cModulo.addOrder(Order.asc("descricao"));
			c.addOrder(Order.asc("descricao"));
			
			
			return c.list();
			
		} catch (DAOException e) {
			 throw new DAOException(e);
		}
	}
	
	/**
	 * Lista os periodos de avaliação com as cargas totais do periodo atualizadas vinculadas ao cronograma informado
	 * 
	 * @param idCronograma
	 * @return
	 * @throws DAOException
	 */
	public void findCHTotalPeriodoByCronograma(Integer idCronograma) throws DAOException{
		try {
			
			Criteria cCH = getSession().createCriteria(CargaHorariaSemanalDisciplina.class);
			Criteria cPeriodo = cCH.createCriteria("periodoAvaliacao");
			Criteria cCrono = cPeriodo.createCriteria("cronogramaExecucaoAulas");
			
			cCrono.add(Expression.eq("id", idCronograma));	
			cCH.addOrder(org.hibernate.criterion.Order.asc("periodoAvaliacao"));
			
			Collection<CargaHorariaSemanalDisciplina> lista = null;
			
			if(cCH.list() != null)
			{
				lista = cCH.list();
				
				int idPeriodoAux = 0;
				int somaCHPeriodo = 0;
				PeriodoAvaliacao periodoAux = new PeriodoAvaliacao();
				
				for(CargaHorariaSemanalDisciplina item: lista) {
					if(idPeriodoAux == 0){
						idPeriodoAux = item.getPeriodoAvaliacao().getId();
						periodoAux = item.getPeriodoAvaliacao();
					}
					if(idPeriodoAux == item.getPeriodoAvaliacao().getId()){
						somaCHPeriodo += item.getCargaHoraria();
					} else {
						periodoAux.setChTotalPeriodo(somaCHPeriodo);
						
						String codigoIntegracao = "T" + periodoAux.getCronogramaExecucaoAulas().getAno() + periodoAux.getCronogramaExecucaoAulas().getPeriodo() + "C" + periodoAux.getCronogramaExecucaoAulas().getId() + "S";
						if(periodoAux.getNumeroPeriodo() > 0 && periodoAux.getNumeroPeriodo() < 10){
							codigoIntegracao += "0";
						}
						codigoIntegracao += periodoAux.getNumeroPeriodo();
						
						periodoAux.setCodigoIntegracao(codigoIntegracao);
						createOrUpdate(periodoAux);
						
						somaCHPeriodo = 0;
						idPeriodoAux = item.getPeriodoAvaliacao().getId();
						periodoAux = item.getPeriodoAvaliacao();
					}
				}
				
				if(idPeriodoAux > 0){
					periodoAux.setChTotalPeriodo(somaCHPeriodo);
					
					String codigoIntegracao = "T" + periodoAux.getCronogramaExecucaoAulas().getAno() + periodoAux.getCronogramaExecucaoAulas().getPeriodo() + "C" + periodoAux.getCronogramaExecucaoAulas().getId() + "S";
					if(periodoAux.getNumeroPeriodo() > 0 && periodoAux.getNumeroPeriodo() < 10){
						codigoIntegracao += "0";
					}
					codigoIntegracao += periodoAux.getNumeroPeriodo();
					
					periodoAux.setCodigoIntegracao(codigoIntegracao);
					createOrUpdate(periodoAux);
				}
				
			} 
			
			
		} catch (Exception e) {
	        throw new DAOException(e);
	    }
	}
	
	
	/**
	 * Método responsável pelo retorno de uma coleção de Modulo do curso informado.
	 * 
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public Collection<Modulo> findByCursoTecnico(int idCurso) throws DAOException {

		try {
			Criteria c = getSession().createCriteria(Modulo.class).
					createCriteria("moduloCurriculares").
					createCriteria("estruturaCurricularTecnica").
					add(Expression.eq("ativa", Boolean.TRUE))
					.add(Expression.eq("cursoTecnico", new CursoTecnico(idCurso)));
			
			@SuppressWarnings("unchecked")
			
			List<Modulo> list = c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
			return list;
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
		

}
