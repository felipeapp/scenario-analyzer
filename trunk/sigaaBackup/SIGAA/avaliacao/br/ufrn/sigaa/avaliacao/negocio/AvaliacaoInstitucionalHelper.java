package br.ufrn.sigaa.avaliacao.negocio;

import java.util.Collection;

import org.hibernate.HibernateException;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dao.avaliacao.AvaliacaoInstitucionalDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.avaliacao.dao.CalendarioAvaliacaoDao;
import br.ufrn.sigaa.avaliacao.dominio.AvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.CalendarioAvaliacao;
import br.ufrn.sigaa.avaliacao.dominio.FormularioAvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.TipoAvaliacaoInstitucional;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosAvaliacaoInstitucional;

/**
 * Helper que contem regras de negócio da avaliação que são invocados por
 * processadores e MBeans para controlar fluxos
 * 
 * @author Gleydson Lima
 * 
 */
public class AvaliacaoInstitucionalHelper {
	
	/**
	 * Retorna a avaliação institucional do docente para a turma passada.
	 */
	public static AvaliacaoInstitucional getAvaliacao(Usuario usuario, Turma turma) throws DAOException {
		AvaliacaoInstitucionalDao dao = null; 
		AvaliacaoInstitucional avaliacao = null;
		try {
			dao = DAOFactory.getInstance().getDAO(AvaliacaoInstitucionalDao.class, usuario);
			FormularioAvaliacaoInstitucional form = dao.findFormularioDocente(turma.getAno(), turma.getPeriodo(), turma.isEad(), TipoAvaliacaoInstitucional.AVALIACAO_DOCENTE_GRADUACAO);
			if (usuario.getVinculoAtivo().isVinculoDocenteExterno())
				avaliacao = dao.findByDocenteExterno(usuario.getVinculoAtivo().getDocenteExterno(), turma.getAno(), turma.getPeriodo(), form);
			else
				avaliacao = dao.findByDocente(usuario.getServidorAtivo(), turma.getAno(), turma.getPeriodo(), form);
		} finally {
			if (dao != null)
				dao.close();
		}
		return avaliacao;
	}

	/** Determina se um discente está apto a preencher a Avaliação Institucional vigente.
	 * @param d
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public static boolean aptoPreencherAvaliacaoVigente(DiscenteAdapter discente, FormularioAvaliacaoInstitucional formulario) throws DAOException {
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente.getDiscente());
		return aptoPreencherAvaliacaoVigente(discente, formulario, cal.getAnoAnterior(), cal.getPeriodoAnterior());
	}
	
	/** Determina se um discente está apto a preencher a Avaliação Institucional vigente.
	 * @param d
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public static boolean aptoPreencherAvaliacaoVigente(DiscenteAdapter discente, FormularioAvaliacaoInstitucional formulario, int ano, int periodo) throws DAOException {
		boolean obrigatorio = ParametroHelper.getInstance().getParametroBoolean(ParametrosAvaliacaoInstitucional.AVALIACAO_DISCENTE_ATIVA);
		if (obrigatorio && discente.isGraduacao() && discente.isAtivo()) {
			MatriculaComponenteDao matriculaDao = null;
			AvaliacaoInstitucionalDao avaliacaoDao = null;
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente.getDiscente());
			try {
				if ( cal.isPeriodoAvaliacaoInstitucional()) {
					matriculaDao = DAOFactory.getInstance().getDAO(MatriculaComponenteDao.class);
					Collection<SituacaoMatricula> situacoes = SituacaoMatricula.getSituacoesMatriculadoOuConcluido();
					int totalMatriculasTurmas = matriculaDao.countMatriculasTurmasByDiscente(discente.getDiscente(), cal.getAnoAnterior(), cal.getPeriodoAnterior(), situacoes.toArray(new SituacaoMatricula[situacoes.size()]));
					if (totalMatriculasTurmas > 0 ) {
						avaliacaoDao = DAOFactory.getInstance().getDAO(AvaliacaoInstitucionalDao.class); 
						if ( !avaliacaoDao.isAvaliacaoFinalizada(discente.getDiscente(), ano, periodo, formulario.getId()))
							return true;
					}
				}
			} finally {
				if (matriculaDao != null) matriculaDao.close();
				if (avaliacaoDao != null) avaliacaoDao.close();
			}
		}
		
		return false;
	}
	
	/** Retorna o calendário ativo para a Avaliação Institucional do discente.
	 * @param discente
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public static CalendarioAvaliacao getCalendarioAvaliacaoAtivo(DiscenteAdapter discente) throws HibernateException, DAOException {
		if (!discente.isRegular()) return null;
		CalendarioAvaliacaoDao dao = null;
		try {
			dao = DAOFactory.getInstance().getDAO(CalendarioAvaliacaoDao.class);
			CalendarioAvaliacao calendario = dao.findCalendarioAtivo(TipoAvaliacaoInstitucional.AVALIACAO_DISCENTE_GRADUACAO, discente.getCurso().isADistancia());
			return calendario;
		} finally {
			if (dao != null) dao.close();
		}
	}
}
