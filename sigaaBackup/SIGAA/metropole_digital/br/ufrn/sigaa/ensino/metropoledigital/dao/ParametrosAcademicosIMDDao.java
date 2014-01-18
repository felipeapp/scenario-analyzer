package br.ufrn.sigaa.ensino.metropoledigital.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.ParametrosAcademicosIMD;
/**
 * DAO com consultas utilizadas no cadastramento dos Parâmetros Acadêmicos do IMD.
 *  
 * @author Rafael Silva
 *
 */
public class ParametrosAcademicosIMDDao extends GenericSigaaDAO{
	
	
	/**
	 * Recupera os Parâmetros Acadêmicos Ativos. Caso não existam parâmetros ativos irá retornar null.
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public ParametrosAcademicosIMD getParametrosAtivo() throws HibernateException, DAOException{
		String projecao = "id, vigente, frequenciaMinimaAprovacao, notaMinimaRecuperacao, notaReprovacaoComponente, mediaAprovacao, dataCadastro, dataInativacao";
		
		String hql = "SELECT "+projecao + " FROM ParametrosAcademicosIMD where vigente = true";
		
		Query q = getSession().createQuery(hql);
		
		List<ParametrosAcademicosIMD> parametros = (List<ParametrosAcademicosIMD>) HibernateUtils.parseTo(q.list(), projecao, ParametrosAcademicosIMD.class);
		if (parametros.isEmpty()) {
			return null;
		}		
		return parametros.get(0);
	}

}
