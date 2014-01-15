/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 06/05/2010
 *
 */
package br.ufrn.sigaa.arq.dao.ensino;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MobilidadeEstudantil;

/**
 * DAO responsável por gerenciar o acesso a dados da Mobilidade Estudantil.
 * 
 * @author Arlindo Rodrigues
 *
 */
public class MobilidadeEstudantilDao extends GenericSigaaDAO {

	/**
	 * Retorna todos os registros de mobilidade estudantil de um discente passado como parâmetro.
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public List<MobilidadeEstudantil> findByDiscente(DiscenteAdapter discente, Boolean somenteAtivos) throws DAOException{
		String hql = "select me from MobilidadeEstudantil me where me.discente.id = ? ";
		if (somenteAtivos != null){
			hql += " and me.ativo = "+(somenteAtivos?" trueValue() ":" falseValue()");
		}			
		@SuppressWarnings("unchecked")
		List<MobilidadeEstudantil> lista = getSession().createQuery(hql).setInteger(0, discente.getId()).list(); 		
		return lista;
	}
	
	/**
	 * Verifica se o discente possui Mobilidade Estudantil no período informado.
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public boolean possuiMobilidadeAtivaNoPeriodo(DiscenteAdapter discente, int ano, int periodo) throws DAOException{
		String hql = "select me from MobilidadeEstudantil me where me.discente.id = ? and me.ativo = trueValue() ";
		hql += " and "+ano+""+periodo+" between soma_semestres(ano,periodo,0) and soma_semestres(ano,periodo,numero_periodos-1)) ";		
		Query q = getSession().createQuery(hql);
		q.setInteger(0, discente.getId()).setMaxResults(1);
		return (q != null && !q.list().isEmpty());
	}
	
	

}
