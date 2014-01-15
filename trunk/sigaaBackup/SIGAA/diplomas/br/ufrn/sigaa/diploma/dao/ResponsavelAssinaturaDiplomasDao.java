/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/10/2010
 *
 */
package br.ufrn.sigaa.diploma.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import org.hibernate.HibernateException;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.diploma.dominio.ResponsavelAssinaturaDiplomas;

/**
 * Classe responsável por consultas específicas à
 * {@link ResponsavelAssinaturaDiplomas dados dos responsáveis por assinaturas
 * de diplomas}.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class ResponsavelAssinaturaDiplomasDao extends GenericSigaaDAO {

	/**
	 * Inabilita todos os registros de {@link ResponsavelAssinaturaDiplomas}.<br/>
	 * <b>ATENÇÃO:</b> esta consulta é chamada ao inserir um registro novo, o
	 * qual será ativo. Não usar sem, na mesma transação, definir um registro
	 * ativo.
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public int inabilitaTodos(char nivel) throws HibernateException, DAOException {
		String hql = "update ResponsavelAssinaturaDiplomas set ativo = falseValue() where nivel = :nivel";
		return getSession().createQuery(hql).setCharacter("nivel", nivel).executeUpdate();
	}
	
	/** Retorna o registro de {@link ResponsavelAssinaturaDiplomas} atualmente ativo.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public ResponsavelAssinaturaDiplomas findAtivo(char nivel) throws HibernateException, DAOException {
		String hql = "from ResponsavelAssinaturaDiplomas where ativo = trueValue() and nivel = :nivel";
		return (ResponsavelAssinaturaDiplomas) getSession().createQuery(hql).setCharacter("nivel", nivel).setMaxResults(1).uniqueResult();
	}
	
	/** Retorna todos registros de responsáveis por assinaturas no diploma de um ou mais níveis de ensino.
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<ResponsavelAssinaturaDiplomas> findAll(char ...nivel) throws DAOException {
		String hql = "from ResponsavelAssinaturaDiplomas"
				+ (!isEmpty(nivel) ? " where nivel in "
						+ UFRNUtils.gerarStringIn(nivel) : "")
				+ " order by ativo desc, nivel, dataCadastro desc";
		@SuppressWarnings("unchecked")
		Collection<ResponsavelAssinaturaDiplomas> lista = getSession().createQuery(hql).list();
		return lista;
	}

}
