/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 24/04/2012
 */
package br.ufrn.sigaa.ensino.metropoledigital.negocio;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ArrayUtils;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.tecnico.dao.CursoTecnicoDao;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;

/**
 * Métodos utilitários para operações específicas da Metrópole Digital.
 * 
 * @author Leonardo Campos
 *
 */
public class MetropoleDigitalHelper {

	/**
	 * Verifica se o discente passado como argumento é da Metrópole Digital.
	 * @param discente
	 * @return
	 */
	public static boolean isMetropoleDigital(DiscenteAdapter discente) {
		if(discente.getCurso() == null)
			return false;
		
		return discente.getNivel() == NivelEnsino.TECNICO
				&& ArrayUtils.idContains(
						discente.getCurso().getId(),
						ParametroHelper.getInstance().getParametroIntArray(
								ParametrosTecnico.ID_CURSO_METROPOLE_DIGITAL));
	}
	
	/**
	 * Verifica se a turma passada como argumento é da Metrópole Digital.
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public static boolean isMetropoleDigital(Turma turma) throws DAOException {
		CursoTecnicoDao dao = DAOFactory
				.getInstance()
				.getDAO(CursoTecnicoDao.class);
		try {
			return dao
					.contemDisciplina(
							turma.getDisciplina().getId(),
							ParametroHelper.getInstance().getParametroIntArray(
									ParametrosTecnico.ID_CURSO_METROPOLE_DIGITAL));
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Verifica se a disciplina passada como argumento é da Metrópole Digital.
	 * @param disciplina
	 * @return
	 * @throws DAOException
	 */
	public static boolean isMetropoleDigital(ComponenteCurricular disciplina) throws DAOException {
		CursoTecnicoDao dao = DAOFactory.getInstance().getDAO(CursoTecnicoDao.class);
		try {
			return dao.contemDisciplina(disciplina.getId(), ParametroHelper.getInstance().getParametroIntArray(ParametrosTecnico.ID_CURSO_METROPOLE_DIGITAL));
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Retorna o curso do metrópole digital registrado na unidade informada.
	 * 
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public static Curso cursoMetropoleDigital(Unidade unidade) throws DAOException {
		CursoTecnicoDao dao = DAOFactory
				.getInstance()
				.getDAO(CursoTecnicoDao.class);
		try {
			return dao
					.findCursoNaUnidade(
							unidade,
							ParametroHelper.getInstance().getParametroIntArray(
									ParametrosTecnico.ID_CURSO_METROPOLE_DIGITAL));
		} finally {
			dao.close();
		}
	}
}
