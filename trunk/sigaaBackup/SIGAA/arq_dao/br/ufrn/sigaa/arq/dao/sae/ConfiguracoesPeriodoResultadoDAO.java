/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '24/03/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.sae;

import java.util.List;

import org.hibernate.HibernateException;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.dominio.AnoPeriodoReferenciaSAE;
import br.ufrn.sigaa.assistencia.dominio.CalendarioBolsaAuxilio;

/**
 * DAO com m�todo para buscar o calend�rio de inscri��o de bolsa
 * aux�lio e o per�odo de divulga��o do processo seletivo
 * 
 * @author Agostinho
 *
 */
public class ConfiguracoesPeriodoResultadoDAO extends GenericSigaaDAO {
	
    public List<CalendarioBolsaAuxilio> verificarResultadoProcessoSeletivo(int idTipoBolsa) throws HibernateException, DAOException {
         return (List<CalendarioBolsaAuxilio>)
             getSession().createQuery("select calendario from CalendarioBolsaAuxilio " +
             		"calendario where calendario.tipoBolsaAuxilio.id = ?").setInteger(0, idTipoBolsa).list();
	}

    public AnoPeriodoReferenciaSAE findAnoPeriodoResultadoSAE() throws HibernateException, DAOException {
        return (AnoPeriodoReferenciaSAE) getSession().createQuery("select a from AnoPeriodoReferenciaSAE a").uniqueResult();
    }
}
