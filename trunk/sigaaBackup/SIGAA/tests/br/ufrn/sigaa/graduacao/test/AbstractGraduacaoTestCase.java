package br.ufrn.sigaa.graduacao.test;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.ParametrosGestoraAcademicaDao;
import br.ufrn.sigaa.arq.test.MassaTesteIds;
import br.ufrn.sigaa.arq.test.SigaaTestCase;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

public class AbstractGraduacaoTestCase extends SigaaTestCase {

	protected DiscenteGraduacao discenteAtivo;

	protected Curso cursoAtivo;


	@Override
	protected void setUp() throws Exception {
		super.setUp();

		discenteAtivo = getGenericDAO().findByPrimaryKey(getIntMassa(MassaTesteIds.GRAD_DISCENTE_REGULAR), DiscenteGraduacao.class);

		cursoAtivo = new Curso(getIntMassa(MassaTesteIds.GRAD_CURSO));
	}


	public ParametrosGestoraAcademica getParametrosGraduacao() throws DAOException {
		ParametrosGestoraAcademicaDao d = new ParametrosGestoraAcademicaDao();
		return  d.findByUnidade(605, NivelEnsino.GRADUACAO);
	}


}
