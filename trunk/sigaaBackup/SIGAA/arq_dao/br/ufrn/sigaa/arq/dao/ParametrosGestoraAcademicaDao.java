/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/11/2006
 *
 */
package br.ufrn.sigaa.arq.dao;

import java.util.Hashtable;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;

/**
 * Classe respons�vel por consultas espec�ficas aos
 * {@link ParametrosGestoraAcademica Par�metros da Gestora Acad�mica}.
 * 
 * @author Andre M Dantas
 * 
 */
public class ParametrosGestoraAcademicaDao extends GenericSigaaDAO {

	/** Cache de par�metros, para otimiza��o nas consultas. */
	Hashtable<Integer, ParametrosGestoraAcademica> cache = new Hashtable<Integer, ParametrosGestoraAcademica>();

	/** Tempo de cada cache utilizado para otimizar a consulta dos par�metros. */
	Hashtable<Integer, Long> cacheTime = new Hashtable<Integer, Long>();

	/** Tempo m�ximo que o cache dever� ser atualizado. */
	public static final int cacheExpire = 60 * 1000 * 30;

	/** Retorna os par�metros de uma Unidade e um n�vel de ensino.
	 * 
	 * @param unidade
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public ParametrosGestoraAcademica findByUnidade(int unidade, char nivel)
			throws DAOException {
		try {

			long now = System.currentTimeMillis();

			if (cacheTime.get(unidade) == null) {
				ParametrosGestoraAcademica param = findByParametros(new Unidade(unidade), nivel, null, null, null);
				if (param == null) return null;
				cache.put(unidade, param);
				cacheTime.put(unidade, now);
			} else {
				if ( now - cacheTime.get(unidade) > cacheExpire ) {
					cache.put(unidade, findByParametros(new Unidade(unidade), nivel, null, null, null));
					cacheTime.put(unidade, now);
				}
			}

			return cache.get(unidade);

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/** Retorna os Parametros da Gestora de um curso.
	 * 
	 * @param c
	 * @return
	 * @throws DAOException
	 */
	public ParametrosGestoraAcademica findByCurso(Curso c) throws DAOException {
		return findByParametros(null, null, c, null, null);
	}

	/** Retorna os par�metros da gestora de uma unidade, acordo com a modalidade de educa��o e n�vel de ensino.
	 * 
	 * @param unidade
	 * @param nivel
	 * @param modalidade
	 * @return
	 * @throws DAOException
	 */
	public ParametrosGestoraAcademica findByModalidade(Unidade unidade, Character nivel, ModalidadeEducacao modalidade) throws DAOException {
		return findByParametros(unidade, nivel, null, modalidade, null);
	}

	/** Retorna os par�metros da gestora de uma unidade de acordo com o n�vel de ensino e conv�nio.
	 * 
	 * @param unidade
	 * @param nivel
	 * @param convenio
	 * @return
	 * @throws DAOException
	 */
	public ParametrosGestoraAcademica findByConvenio(Unidade unidade, Character nivel, ConvenioAcademico convenio) throws DAOException {
		return findByParametros(unidade, nivel, null, null, convenio);
	}

	/** Retorna os par�metros da gestora de uma unidade de acordo com o n�vel de ensino, curso, modalidade de educa��o e conv�nio.
	 * 
	 * @param unidade
	 * @param nivel
	 * @param curso
	 * @param modalidade
	 * @param convenio
	 * @return
	 * @throws DAOException
	 */
	public ParametrosGestoraAcademica findByParametros(Unidade unidade, Character nivel, Curso curso,
			ModalidadeEducacao modalidade, ConvenioAcademico convenio) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(ParametrosGestoraAcademica.class);
			if (ValidatorUtil.isEmpty(unidade))
				c.add(Expression.isNull("unidade"));
			else
				c.add(Expression.eq("unidade", unidade));
			if (ValidatorUtil.isEmpty(curso))
				c.add(Expression.isNull("curso"));
			else
				c.add(Expression.eq("curso", curso));
			if (ValidatorUtil.isEmpty(modalidade))
				c.add(Expression.isNull("modalidade"));
			else
				c.add(Expression.eq("modalidade", modalidade));
			if (ValidatorUtil.isEmpty(convenio))
				c.add(Expression.isNull("convenio"));
			else
				c.add(Expression.eq("convenio", convenio));
			if (nivel != null && nivel != ' ' && nivel != 0x00)
				c.add(Expression.eq("nivel", nivel.charValue()));

			return (ParametrosGestoraAcademica) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}


}
