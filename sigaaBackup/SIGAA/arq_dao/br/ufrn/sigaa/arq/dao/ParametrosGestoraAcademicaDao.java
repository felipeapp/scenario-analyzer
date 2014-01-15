/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Classe responsável por consultas específicas aos
 * {@link ParametrosGestoraAcademica Parâmetros da Gestora Acadêmica}.
 * 
 * @author Andre M Dantas
 * 
 */
public class ParametrosGestoraAcademicaDao extends GenericSigaaDAO {

	/** Cache de parâmetros, para otimização nas consultas. */
	Hashtable<Integer, ParametrosGestoraAcademica> cache = new Hashtable<Integer, ParametrosGestoraAcademica>();

	/** Tempo de cada cache utilizado para otimizar a consulta dos parâmetros. */
	Hashtable<Integer, Long> cacheTime = new Hashtable<Integer, Long>();

	/** Tempo máximo que o cache deverá ser atualizado. */
	public static final int cacheExpire = 60 * 1000 * 30;

	/** Retorna os parâmetros de uma Unidade e um nível de ensino.
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

	/** Retorna os parâmetros da gestora de uma unidade, acordo com a modalidade de educação e nível de ensino.
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

	/** Retorna os parâmetros da gestora de uma unidade de acordo com o nível de ensino e convênio.
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

	/** Retorna os parâmetros da gestora de uma unidade de acordo com o nível de ensino, curso, modalidade de educação e convênio.
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
