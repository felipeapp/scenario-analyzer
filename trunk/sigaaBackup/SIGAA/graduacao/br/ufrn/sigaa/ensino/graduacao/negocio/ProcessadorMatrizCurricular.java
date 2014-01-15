/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 07/02/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.SituacaoCursoHabil;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.Habilitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;

/**
 * Processador responsável pelo cadastro e alteração de uma matriz curricular.
 * @author André
 *
 */
public class ProcessadorMatrizCurricular extends ProcessadorCadastro {

	/** Executa o cadastro/alteração da matriz curricular.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		validate(movimento);
		MovimentoCadastro mc = (MovimentoCadastro) movimento;
		MatrizCurricular m = (MatrizCurricular) mc.getObjMovimentado();
		if (m.getAtivo() == null) {
			m.setAtivo(true);
		}
		if (mc.getCodMovimento().equals(SigaaListaComando.CADASTRAR_MATRIZ_CURRICULAR))
			criar(mc);
		else if (mc.getCodMovimento().equals(SigaaListaComando.ALTERAR_MATRIZ_CURRICULAR))
			alterar(mc);
		return mc.getObjMovimentado();
	}

	/** Persiste o objeto movimentado.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#criar(br.ufrn.arq.dominio.MovimentoCadastro)
	 */
	@Override
	protected Object criar(MovimentoCadastro mov) throws NegocioException, ArqException {
		criarHabilitacao(mov);
		return super.criar(mov);
	}


	/** Altera o objeto movimentado.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#alterar(br.ufrn.arq.dominio.MovimentoCadastro)
	 */
	@Override
	protected Object alterar(MovimentoCadastro mov) throws NegocioException, ArqException {
		criarHabilitacao(mov);
		inativarOrAtivarCurriculos(mov);
		return super.alterar(mov);
	}
	
	/** Persiste a habilitação, caso seja uma nova.
	 * @param mov
	 * @throws DAOException
	 */
	private void criarHabilitacao(MovimentoCadastro mov) throws DAOException {
		MatrizCurricular m = (MatrizCurricular) mov.getObjMovimentado();
		Habilitacao h = m.getHabilitacao();
		GenericDAO dao = getGenericDAO(mov);
		try {
			if (h != null && h.getId() == 0) {
				
				if (h.getAreaSesu().getId() == 0) {
					h.setAreaSesu(null);
				}
				
				dao.create(h);
			}
		} finally {
			dao.close();
		}
	}

	/** Inativa os currículos pertencentes a matriz curricular, para as matrizes inativas.
	 * @param mov
	 * @throws DAOException
	 */
	private void inativarOrAtivarCurriculos(MovimentoCadastro mov) throws DAOException {
		MatrizCurricular m = (MatrizCurricular) mov.getObjMovimentado();
		EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class, mov);
		
		try {
			Collection<Curriculo> curriculos = dao.findByMatriz(m.getId(), null);
			for (Curriculo c : curriculos) {
				if (m.getAtivo() != null && (m.getAtivo()!= c.getAtivo()) && !m.getAtivo() ){
					c.setAtivo(m.getAtivo());
					dao.updateNoFlush(c);
				}
			}
		} finally {
			dao.close();
		}
	}
	
	/** Valida os dados da matriz antes de persistir.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro mc = (MovimentoCadastro) mov;
		MatrizCurricular m = (MatrizCurricular) mc.getObjMovimentado();
		MatrizCurricularDao dao = getDAO(MatrizCurricularDao.class, mov);
		try {
			if (dao.jaExiste(m)){
				throw new NegocioException("A matriz curricular não pode ser cadastrada.<br>" +
						"Já existe uma matriz com o mesmo curso, turno, grau acadêmico, habilitação ou ênfase.");
			}
			if (m.getId() > 0 && m.getSituacao() != null && m.getSituacao().getId() == SituacaoCursoHabil.EXTINTO) {
				DiscenteGraduacaoDao ddao = getDAO(DiscenteGraduacaoDao.class, mov);
				Collection<DiscenteGraduacao> ativos = ddao.findByMatriz(m.getId(), StatusDiscente.ATIVO, StatusDiscente.FORMANDO
						, StatusDiscente.TRANCADO, StatusDiscente.AFASTADO, StatusDiscente.GRADUANDO);
				if (!isEmpty(ativos)) {
					throw new NegocioException("Não é permitido alterar a situação da matriz para EXTINTO." +
					" Ainda existem alunos ativos vinculados a ela");
				}
			}
		} finally {
			dao.close();
		}
		checaNulos(m);
	}

	/** Anula os atributos que não serão persistidos.
	 * @param m
	 */
	private void checaNulos(MatrizCurricular m) {
		if (m.getRegimeLetivo().getId() == 0)
			m.setRegimeLetivo(null);
		if (m.getTipoSistemaCurricular().getId() == 0)
			m.setTipoSistemaCurricular(null);
		if (m.getSituacao().getId() == 0)
			m.setSituacao(null);
		if (m.getGrauAcademico().getId() == 0)
			m.setGrauAcademico(null);
		if (m.getTurno().getId() == 0)
			m.setTurno(null);
		if (m.getSituacaoDiploma().getId() == 0)
			m.setSituacaoDiploma(null);
		if (!m.getPossuiHabilitacao())
			m.setHabilitacao(null);
	}
}
