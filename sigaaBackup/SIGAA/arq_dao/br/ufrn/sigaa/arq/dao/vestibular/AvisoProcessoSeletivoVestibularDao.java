/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '16/04/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.vestibular;

import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.vestibular.dominio.AvisoProcessoSeletivoVestibular;

/**
 * Classe responsável por consultas específicas aos Avisos dos Processos
 * Seletivos e Vestibulares.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class AvisoProcessoSeletivoVestibularDao extends GenericSigaaDAO {

	/** Retorna uma coleção de avisos cadastrados para um processo seletivo.
	 * @param idProcessoSeletivo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AvisoProcessoSeletivoVestibular> findAllByProcessoSeletivo(int idProcessoSeletivo) throws HibernateException, DAOException{
		String sql = "select id_aviso" +
				" from vestibular.aviso" +
				" where id_processo_seletivo = :idProcessoSeletivo" +
				" order by inicio_divulgacao";
		Query q = getSession().createSQLQuery(sql);
		q.setInteger("idProcessoSeletivo", idProcessoSeletivo);
		return q.list();
	}
}
