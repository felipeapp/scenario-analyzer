package br.ufrn.comum.dao;

import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.gru.dominio.GuiaRecolhimentoUniao;

/**
 * DAO contendo métodos para buscas de Guias de Recolhimento da União
 * @author Weksley Viana
 */
public class GuiaRecolhimentoUniaoDao extends GenericSharedDBDao{
	
	/***
	 * Busca todas as GRUs
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<GuiaRecolhimentoUniao> findGrus() throws HibernateException, DAOException{
		Query q = getSession().createQuery("select gru from GuiaRecolhimentoUniao gru");
		
		return q.list();		
	}
	
	/**
	 * Busca as GRUs não quitadas
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<GuiaRecolhimentoUniao> findGrusNaoQuitadas() throws HibernateException, DAOException{
		Query q = getSession().createQuery("select gru from GuiaRecolhimentoUniao gru where quitada=" + SQLDialect.FALSE);
		
		return q.list();		
	}
	
	/**
	 * Busca as GRUs pelo Nosso Número
	 * @param nossoNumero
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public GuiaRecolhimentoUniao findByNossoNumero(long nossoNumero, boolean tipoSimples) throws HibernateException, DAOException{
		Query q = getSession().createQuery("select gru from GuiaRecolhimentoUniao gru where numeroReferenciaNossoNumero=:nossoNumero and gru.configuracaoGRU.gruSimples=:tipoSimples ");
			
		q.setLong("nossoNumero", nossoNumero);
		q.setBoolean("tipoSimples", tipoSimples);
		return (GuiaRecolhimentoUniao) q.uniqueResult();
	}
	
	/**
	 * Busca as GRUs quitadas pelo Nosso Número
	 * @param nossoNumero
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public GuiaRecolhimentoUniao findByConsolidada(long nossoNumero, boolean tipoSimples) throws HibernateException, DAOException{
		Query q = getSession().createQuery("select gru from GuiaRecolhimentoUniao gru where numeroReferenciaNossoNumero=:nossoNumero " +
				"and gru.configuracaoGRU.gruSimples=:tipoSimples and quitada = " + SQLDialect.TRUE);
			
		q.setLong("nossoNumero", nossoNumero);
		q.setBoolean("tipoSimples", tipoSimples);
		return (GuiaRecolhimentoUniao) (!ValidatorUtil.isEmpty(q.list()) ? q.list().get(0) : null);
	}

}
