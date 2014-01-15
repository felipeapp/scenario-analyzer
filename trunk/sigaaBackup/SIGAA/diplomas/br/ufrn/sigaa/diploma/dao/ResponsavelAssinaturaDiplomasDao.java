/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Classe respons�vel por consultas espec�ficas �
 * {@link ResponsavelAssinaturaDiplomas dados dos respons�veis por assinaturas
 * de diplomas}.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
public class ResponsavelAssinaturaDiplomasDao extends GenericSigaaDAO {

	/**
	 * Inabilita todos os registros de {@link ResponsavelAssinaturaDiplomas}.<br/>
	 * <b>ATEN��O:</b> esta consulta � chamada ao inserir um registro novo, o
	 * qual ser� ativo. N�o usar sem, na mesma transa��o, definir um registro
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
	
	/** Retorna todos registros de respons�veis por assinaturas no diploma de um ou mais n�veis de ensino.
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
