/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '06/12/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.prodocente;

import java.util.Collection;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.prodocente.atividades.dominio.QualificacaoDocente;

/**
 * @author Ricardo Wendell
 *
 */
public class QualificacaoDocenteDao extends GenericSigaaDAO {

	/**
	 * Busca todas as qualificações por Departamento dos servidores
	 *
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<QualificacaoDocente> findByDepartamento(Unidade unidade) throws DAOException {

		String hql = "from QualificacaoDocente q where q.servidor.unidade.id = " + unidade.getId() +
				" and (ativo is null or ativo = trueValue()) order by q.servidor.pessoa.nome, q.dataInicial";

		return getSession().createQuery(hql).list();
	}

}
