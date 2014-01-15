/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/09/2006
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoIntegralizacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.AproveitamentoValidator;
import br.ufrn.sigaa.ensino.graduacao.negocio.DiscenteCalculosHelper;
import br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoOperacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.ProcessadorAlteracaoStatusMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.calculos.PerfilInicialFactory;
import br.ufrn.sigaa.ensino.negocio.dominio.AproveitamentoMov;

/**
 * Classe chamada para processar o aproveitamento de disciplinas e cancelamento
 * de aproveitamento
 *
 * @author Andre M Dantas
 *
 */
public class ProcessadorAproveitamento extends ProcessadorCadastro {

	public Object execute(Movimento m) throws NegocioException, ArqException, RemoteException {
		validate(m);

		// esse movimento possui uma cole��o de matr�culas que representam os aproveitamentos
		AproveitamentoMov mov = (AproveitamentoMov) m;
		GenericDAO dao = getGenericDAO(mov);
		try {
			DiscenteAdapter discente = null;
			boolean temAproveitamentoExtra = false;
			for( MatriculaComponente mc : mov.getAproveitamentos()){
				/* Cancela a matr�cula caso j� esteja matriculado no componente aproveitado */
				if (mc.getComponente().isPermiteCancelarMatricula())
					cancelaMatricula(mc, mov);
				
				//atributos default
				mc.setDataCadastro( new Date() );
				mc.setRegistroEntrada( mov.getUsuarioLogado().getRegistroEntrada() );
				discente = mc.getDiscente();
				
				if ( TipoIntegralizacao.EXTRA_CURRICULAR.equals(mc.getTipoIntegralizacao()))
					temAproveitamentoExtra  = true;
				// para persistir � necess�rio setar o tipo de integraliza��o
				dao.create(mc);
			}
			if (discente != null && discente.getNivel() == NivelEnsino.GRADUACAO) {
				DiscenteGraduacao dg = dao.findByPrimaryKey(discente.getId(), DiscenteGraduacao.class);
				DiscenteCalculosHelper.atualizarTodosCalculosDiscente(dg, m);

				dg = dao.findByPrimaryKey(discente.getId(), DiscenteGraduacao.class);

				// valida se foi excedido o limite de extra-curriculares
				if (temAproveitamentoExtra) {
					// s� verifica se est� tentando aproveitar algum extra-curricular
					ListaMensagens erros = new ListaMensagens();
					MatriculaGraduacaoValidator.validarLimiteCreditosExtra(dg, erros, null);
					checkValidation(erros);
				}

				// e em seguida � calculado o perfil inicial
				PerfilInicialFactory.getPerfilInicial(dg).calcular(dg, mov);
			}
		} finally {
			dao.close();
		}

		return null;
	}
	
	/**
	 * Cancela a matr�cula caso j� esteja matriculado no componente aproveitado.
	 * @param matricula
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private void cancelaMatricula(MatriculaComponente matricula, Movimento mov) throws NegocioException, ArqException, RemoteException{
		MatriculaComponenteDao mdao = getDAO(MatriculaComponenteDao.class, mov);
		Collection<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();
			try {			
			matriculas = mdao.findByDiscenteEDisciplina(matricula.getDiscente(), matricula.getComponente(), 
					SituacaoMatricula.MATRICULADO, SituacaoMatricula.EM_ESPERA);					
			if (matriculas != null){																		
				Integer id = (Integer) matriculas.toArray()[0];
				MatriculaComponente matriculaAtual = new MatriculaComponente(id);			
				matriculaAtual = mdao.refresh(matriculaAtual);		
				
				matriculas = new ArrayList<MatriculaComponente>();
				matriculas.add(matriculaAtual);
				
				MovimentoOperacaoMatricula movMatricula = new MovimentoOperacaoMatricula();
				movMatricula.setMatriculas(matriculas);
				movMatricula.setNovaSituacao(SituacaoMatricula.EXCLUIDA);
				movMatricula.setCodMovimento(SigaaListaComando.ALTERAR_STATUS_MATRICULA);
				
				movMatricula.setApplicationContext(mov.getApplicationContext());
				movMatricula.setUsuarioLogado(mov.getUsuarioLogado());
				movMatricula.setSistema(mov.getSistema());
				
				ProcessadorAlteracaoStatusMatricula processador = new ProcessadorAlteracaoStatusMatricula();
				processador.execute(movMatricula);											
			}		
		} finally {
			if (mdao != null)
				mdao.close();
		}
	}

	
	
	public void validate(Movimento mov) throws NegocioException, ArqException {
		AproveitamentoMov amov = (AproveitamentoMov) mov;

		for (MatriculaComponente matricula : amov.getAproveitamentos()) {
			validarCadaAproveitamento(matricula, mov);
		}
	}

	/**
	 * Uma cole��o de aproveitamentos � gerada pelo usu�rio e cada um deles precisa ser validado antes de ser 
	 * completado. Este m�todo valida os aproveitamentos individualmente. 
	 * 
	 * @param matricula
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void validarCadaAproveitamento(MatriculaComponente matricula, Movimento mov) throws NegocioException, ArqException {
		// teste da nota m�nima
		DiscenteAdapter discente = matricula.getDiscente();

		ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(matricula.getDiscente()); 
		if ( matricula.getAno() != null && matricula.getPeriodo() != null ) {	
			String msg =  DiscenteHelper.validarPeriodoDiscente(discente, matricula.getAno(), matricula.getPeriodo());
			if (msg != null)
				throw new NegocioException(msg);
		}
		
		ListaMensagens erros = new ListaMensagens();
		AproveitamentoValidator.validaAproveitamento(discente, matricula, param, erros, mov.getUsuarioLogado());
		if (!erros.getMensagens().isEmpty()) {
			throw new NegocioException(erros.getMensagens().iterator().next().getMensagem());
		}
	}
}
