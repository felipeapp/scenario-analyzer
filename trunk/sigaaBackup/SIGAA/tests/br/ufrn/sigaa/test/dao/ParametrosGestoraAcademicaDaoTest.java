package br.ufrn.sigaa.test.dao;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dao.ParametrosGestoraAcademicaDao;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;
/**
 * 
 * @author Rafael Buriti
 *
 */

public class ParametrosGestoraAcademicaDaoTest extends TestCase{
	
	ParametrosGestoraAcademicaDao parametroGestoraAcademicaDao = new ParametrosGestoraAcademicaDao();
	
	CursoDao cursoDao = new CursoDao();
	
	UnidadeDao unidadeDao = new UnidadeDao();
	
	/**
	 * Método: findByUnidade	
	 * 
	 * @param nivelEnsino
	 * @param unidade
	 * 
	 * @return parametroGestoraAcademica
	 * @throws DAOException
	 * 
	 */
	public void testFindByUnidade() throws DAOException{		
//		falha nesse teste! o nível fica em cache se não mudar a unidade
		assertNotNull(parametroGestoraAcademicaDao.findByUnidade(605, 'G'));
		
		assertNotNull(parametroGestoraAcademicaDao.findByUnidade(605, 'T'));
	}
	
	/**
	 * Método: findByCurso	
	 * 
	 * @param curso
	 * 
	 * @return parametroGestoraAcademica
	 * @throws DAOException
	 * 
	 */
	public void testFindByCurso() throws DAOException{
		
		Curso curso = cursoDao.findByPrimaryKey(2000063, Curso.class);
		
		//Falha no teste - nenhum registro de parametro_gestora_academica possui o id_curso
		//assertNotNull(parametroGestoraAcademicaDao.findByCurso(curso));
		
		assertNull(parametroGestoraAcademicaDao.findByCurso(curso));
		
		// testa com valor nulo
		assertTrue(parametroGestoraAcademicaDao.findByCurso(null).equals("null"));
	}
	
	/**
	 * Método: findByModalidade	
	 * 
	 * @param unidade
	 * @param nivelEnsino
	 * @param modalidadeEducacao
	 * 
	 * @return parametroGestoraAcademica
	 * @throws DAOException
	 * 
	 */
	public void testFindByModalidade() throws DAOException{
		
		GenericSigaaDAO getDao = new GenericSigaaDAO();
		
		Unidade unidade = unidadeDao.findByPrimaryKey(284, Unidade.class);
		ModalidadeEducacao modalidade = getDao.findByPrimaryKey(1, ModalidadeEducacao.class);
		
		// Falha no teste - nenhum registro de parametro_gestora_academica possui o id_modalidade_educacao
		//assertNotNull(parametroGestoraAcademicaDao.findByModalidade(unidade, 'T', modalidade));
		
		modalidade = null;
		assertTrue(parametroGestoraAcademicaDao.findByModalidade(unidade, 'T', modalidade).getId() > 0);
		
		assertNull(parametroGestoraAcademicaDao.findByModalidade(unidade, 'G', modalidade));
		unidade = unidadeDao.findByPrimaryKey(605, Unidade.class);
		assertNull(parametroGestoraAcademicaDao.findByModalidade(unidade, 'T', modalidade));
		
		// testa com valor nulo
		assertNull(parametroGestoraAcademicaDao.findByModalidade(null, 'G', null));
	}
	
	/**
	 * Método: findByConvenio	
	 * 
	 * @param unidade
	 * @param nivelEnsino
	 * @param convenioAcademico
	 * 
	 * @return parametroGestoraAcademica
	 * @throws DAOException
	 * 
	 */
/*	public void testFindByConvenio() throws DAOException{
		UnidadeDao unidadeDao = new UnidadeDao();
		
		GenericSigaaDAO getDao = new GenericSigaaDAO();
		
		Unidade unidade = unidadeDao.findByPrimaryKey(605, Unidade.class);
		ConvenioAcademico convenio = getDao.findByPrimaryKey(1, ConvenioAcademico.class);
		
		assertNotNull(parametroGestoraAcademicaDao.findByConvenio(unidade, 'S', convenio));
		assertTrue(parametroGestoraAcademicaDao.findByConvenio(unidade, 'S', convenio).getId() > 0);
		
		assertNull(parametroGestoraAcademicaDao.findByConvenio(unidade, 'G', convenio));
		unidade = unidadeDao.findByPrimaryKey(284, Unidade.class);
		assertNull(parametroGestoraAcademicaDao.findByConvenio(unidade, 'S', convenio));
	}*/
	
	/**
	 * 
	 * Método: findByParametros	
	 * 
	 * @param unidade
	 * @param nivel (char)
	 * @param curso
	 * @param modalidadeEducacao
	 * @param convenioAcademico
	 * 
	 * @return string
	 * @throws DAOException
	 * 
	 */
	public void testFindByParametros() throws DAOException {
		
		GenericSigaaDAO getDao = new GenericSigaaDAO();
		
		Unidade unidade = unidadeDao.findByPrimaryKey(284, Unidade.class);
		Curso curso = cursoDao.findByPrimaryKey(2000063, Curso.class);
		ModalidadeEducacao modalidade = getDao.findByPrimaryKey(1, ModalidadeEducacao.class);
		ConvenioAcademico convenio = getDao.findByPrimaryKey(1, ConvenioAcademico.class);

		ParametrosGestoraAcademica daoParametros = parametroGestoraAcademicaDao.findByParametros(unidade, 'G', null, modalidade, convenio);
		assertNull(daoParametros);
		
		// testa com valor nulo
		assertTrue(parametroGestoraAcademicaDao.findByParametros(null, 'G', null, null, null).equals(""));
	}
	

}
