/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/06/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.AlteracaoMatricula;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.EstornoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.medio.dao.MatriculaDiscenteSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.SituacaoMatriculaSerie;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.MatriculaComponenteHelper;
import br.ufrn.sigaa.ensino.negocio.ProcessadorDiscente;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Processador respons�vel pelas opera��es de movimenta��o de discentes do ensino m�dio.
 * Opera��es de movimenta��o de discentes s�o aquelas que alteram o status do
 * mesmo, quando do afastamento, cancelamento, retorno, conclus�o de curso,
 * abandono, etc.
 * 
 * @author Arlindo
 */
public class ProcessadorAfastamentoDiscenteMedio  extends ProcessadorDiscente {
	
	/**
	 * Executa o processamento de afastamento
	 */
	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException,
			RemoteException {
		
		GenericDAO dao =  getGenericDAO(movimento);
		try {
			/* Realiza o afastamento do discente */
			if (SigaaListaComando.AFASTAR_DISCENTE_MEDIO.equals(movimento.getCodMovimento())) {		
				//valida os dados informados 
				validate(movimento);
				//verifica se o discente possui empr�stimos pendentes
				verificaEmprestimoPendente(movimento);
				//cria o registro de movimenta��o
				criar((MovimentoCadastro) movimento);
				// alterar o status do discente
				alterarStatusDiscente((MovimentoCadastro) movimento);
				
			} else if (SigaaListaComando.ESTORNAR_AFASTAMENTO_DISCENTE_MEDIO.equals(movimento.getCodMovimento()))
				// estorna afastamento
				estornar((MovimentoCadastro) movimento);
			
		} finally {
			dao.close();
		}

		return null;
		
	}
	
	/** 
	 *  <p>Verifica se o discente possui empr�stimo pendente na biblioteca feitos com o v�nculo de discente 
	 *        <i>(pol�tica de empr�stimo para aluno , aluno de p�s  ou m�dio/t�cnico)</i>.</p>
	 *  
	 *  <p>Caso exista, o discente n�o vai poder cancelar o seu v�nculo com a institui��o at� que os empr�stimos sejam devolvidos.</p>
	 * 
	 * @param movimento
	 */
	private void verificaEmprestimoPendente(Movimento movimento) throws NegocioException,	ArqException {
		
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		MovimentacaoAluno movimentacaoAluno = (MovimentacaoAluno) mov.getObjMovimentado();
		
		checkValidation( VerificaSituacaoUsuarioBibliotecaUtil.verificaEmprestimoPendenteDiscente(movimentacaoAluno.getDiscente().getDiscente()));
	}
	
	/**
	 * Registra a altera��o do status do aluno de acordo com o tipo de
	 * afastamento que est� sendo cadastrado. Esse registro deve ser feito no
	 * cadastro e na altera��o dos afastamentos.
	 *
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws RemoteException
	 * @throws Exception
	 */
	public void alterarStatusDiscente(MovimentoCadastro mov) throws ArqException, NegocioException, RemoteException {
		MovimentacaoAluno afastamento = (MovimentacaoAluno) mov.getObjMovimentado();
		afastamento.setUsuarioRetorno(null);
		DiscenteAdapter discente = afastamento.getDiscente();
		GenericDAO dao = getGenericDAO(mov);
		
		try{
			TipoMovimentacaoAluno tipo = dao.findByPrimaryKey(afastamento
					.getTipoMovimentacaoAluno().getId(), TipoMovimentacaoAluno.class);
	
			// o status do aluno ser� alterado dependendo do tipo de afastamento
			// cadastrado pra ele, quanto o tipo do afastamento for de AFASTAMENTO PERMANENTE
			if (tipo.getStatusDiscente() != null && tipo.getStatusDiscente() == StatusDiscente.EXCLUIDO) {
				cancelarMatriculaSerie(mov, SituacaoMatriculaSerie.CANCELADO);
				cancelarMatricula(mov, SituacaoMatricula.EXCLUIDA);
				// registra altera��o de status do aluno
				persistirAlteracaoStatus(discente, tipo.getStatusDiscente(), mov);
	
			// se o afastamento resultar no cancelamento do aluno, deve-se cancelar
			// as matr�culas correntes do mesmo
			} else if (tipo.getStatusDiscente() != null && tipo.getStatusDiscente() == StatusDiscente.CANCELADO) {
				cancelarMatriculaSerie(mov, SituacaoMatriculaSerie.CANCELADO);
				cancelarMatricula(mov, SituacaoMatricula.CANCELADO);
				// registra altera��o de status do aluno
				persistirAlteracaoStatus(discente, tipo.getStatusDiscente(), mov);

			// se o afastamento resultar no trancamento do aluno o status do aluno ser� alterado, 
			// para TRANCADO se o tipo do afastamento for de AFASTAMENTO TEMPOR�RIO.
			} else if (tipo.getStatusDiscente() != null && tipo.getStatusDiscente() == StatusDiscente.TRANCADO) {
				cancelarMatriculaSerie(mov, SituacaoMatriculaSerie.TRANCADO);
				cancelarMatricula(mov, SituacaoMatricula.TRANCADO);
				CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente);
				
				// s� altera o status pra trancado se o trancamento for do ano atual
				if (afastamento.getAnoReferencia() == cal.getAno()) {
					// registra altera��o de status do aluno
					persistirAlteracaoStatus(discente, tipo.getStatusDiscente(), mov);
				}
			} else if(tipo.getStatusDiscente() != null) {
				// registra altera��o de status do aluno
				persistirAlteracaoStatus(discente, tipo.getStatusDiscente(), mov);
			}
		
		} finally{
			dao.close();
		}

	}	
	
	/** Cancela o programa
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException 
	 */
	private void cancelarMatricula(MovimentoCadastro mov, SituacaoMatricula situacao) throws NegocioException, ArqException, RemoteException {
		MovimentacaoAluno afastamento = (MovimentacaoAluno) mov.getObjMovimentado();
		DiscenteAdapter discente = afastamento.getDiscente();
		MatriculaComponenteDao matDao = getDAO(MatriculaComponenteDao.class, mov);
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, mov);
		try {
			// cancelando matr�culas
			int periodo = 0;
			Collection<MatriculaComponente> matriculadas = matDao.findByDiscente(discente, afastamento.getAnoReferencia(), 
					periodo, SituacaoMatricula.MATRICULADO, SituacaoMatricula.EM_ESPERA, SituacaoMatricula.REPROVADO_FALTA, SituacaoMatricula.REPROVADO, 
					SituacaoMatricula.REPROVADO_MEDIA_FALTA);
			
			for (MatriculaComponente matricula : matriculadas) {
				MatriculaComponenteHelper.alterarSituacaoMatricula(matricula, situacao, mov, dao);
			}
			// cancelando solicita��es de matr�cula
			//cancelarSolicitacoesMatricula(mov);
			
		} finally {
			dao.close();
			matDao.close();
		}

	}	
	
	/** Cancela as matr�culas em s�rie do discente
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException 
	 */
	private void cancelarMatriculaSerie(MovimentoCadastro mov, SituacaoMatriculaSerie situacao) throws NegocioException, ArqException, RemoteException {
		MovimentacaoAluno afastamento = (MovimentacaoAluno) mov.getObjMovimentado();
		DiscenteAdapter discente = afastamento.getDiscente();
		MatriculaDiscenteSerieDao dao = getDAO(MatriculaDiscenteSerieDao.class, mov);
		try {
			// cancelando matr�culas de discente em s�rie
			Collection<MatriculaDiscenteSerie> seriesMatriculadas = 
				dao.findAllMatriculasByDiscente((DiscenteMedio)discente, afastamento.getAnoReferencia(), 
					SituacaoMatriculaSerie.MATRICULADO, SituacaoMatriculaSerie.REPROVADO, SituacaoMatriculaSerie.TRANCADO);
			
			for (MatriculaDiscenteSerie matriculaSerie : seriesMatriculadas) {
				MatriculaDiscenteSerieHelper.alterarSituacaoMatriculaSerie(matriculaSerie, situacao, mov, dao);
			}
			
		} finally {
			dao.close();
		}
		
	}	
	
	/**
	 * Desativa registro de afastamento e recupera status do aluno de quando ele
	 * foi afastado.
	 * @throws RemoteException
	 * @throws ArqException
	 */
	protected Object estornar(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
		MatriculaComponenteDao mcdao = null;
		MovimentacaoAlunoDao dao = null;
		try {
			mcdao = getDAO(MatriculaComponenteDao.class, mov);
			dao = getDAO(MovimentacaoAlunoDao.class, mov);
			MovimentacaoAluno afastamento = (MovimentacaoAluno) mov.getObjMovimentado();
			Integer status = afastamento.getStatusRetorno();
			afastamento = dao.findByPrimaryKey(afastamento.getId(), MovimentacaoAluno.class);
	
			if (status == null) {
				throw new NegocioException("N�o � poss�vel efetuar estorno para esse discente");
			}
			persistirAlteracaoStatus(afastamento.getDiscente(), status, mov);
			dao.updateField(MovimentacaoAluno.class, afastamento.getId(), "ativo", false);
	
			EstornoMovimentacaoAluno estorno = new EstornoMovimentacaoAluno();
			estorno.setDataEstorno(new Date());
			estorno.setMovimentacao(afastamento);
			estorno.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			dao.create(estorno);
	
			 // SE A MOVIMENTA��O J� ESTIVER RETORNADA N�O DEVE DESFAZER AS MATRICULAS, PRORROGA��ES, DISCENTE AFETADAS PELA MOVIMENTA��O
			if( afastamento.getDataCadastroRetorno() == null ){
				// estornar todas as matr�culas que foram afetadas por essa movimenta��o
				Collection<AlteracaoMatricula> alteracoes = dao.findByExactField(AlteracaoMatricula.class, "movimentacaoAluno.id", afastamento.getId());
				for (AlteracaoMatricula alteracao : alteracoes) {
					MatriculaComponenteHelper.alterarSituacaoMatricula(
							alteracao.getMatricula(), alteracao.getSituacaoAntiga(), mov, mcdao);
				}
	
				// estornar conclus�o
				if (afastamento.isConclusao()) {
					// estornar data de cola��o setada
					dao.updateField(Discente.class, afastamento.getDiscente().getId(), "dataColacaoGrau", null);
				}
			}
	
			return afastamento;
		} finally { 
			if (mcdao != null) mcdao.close();
			if (dao != null) dao.close();
		}
	}	
	
	/** Persiste o objeto movimentado.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#criar(br.ufrn.arq.dominio.MovimentoCadastro)
	 */
	@Override
	protected Object criar(MovimentoCadastro mov) throws NegocioException, ArqException{
		MovimentacaoAluno afastamento = (MovimentacaoAluno) mov.getObjMovimentado();

		if (afastamento.isConclusao()) {
			GenericDAO dao = null;
			try {
				dao = getGenericDAO(mov);
				dao.updateField(Discente.class, afastamento.getDiscente().getId(),
						"dataColacaoGrau", afastamento.getDataColacaoGrau());
			} finally {
				if (dao != null) dao.close();
			}
		}
		
		try {
			afastamento.setUsuarioCadastro((Usuario) mov.getUsuarioLogado());
			afastamento.setAtivo(Boolean.TRUE);
			afastamento.setUsuarioRetorno(null);
			afastamento.setDataOcorrencia(new Date());
			super.criar(mov);
		} catch (Exception e) {
			throw new ArqException(e);
		} finally{
			// Inserindo o tipo de discente correto no objMovimentado a ser retornado.
			DiscenteDao discenteDao = getDAO(DiscenteDao.class, mov);
			afastamento.setDiscente(discenteDao.findByPK(afastamento.getDiscente().getId()));
			discenteDao.close();
		}
		
		return afastamento;
	}	
	
	/**
	 * Verifica se o aluno j� tem um afastamento sem retorno. Essa valida��o
	 * s� deve ocorrer para o cadastro simples de afastamento.
	 * @throws NegocioException
	 * @throws ArqException
	 */
	@Override
	public void validate(Movimento movimento) throws NegocioException, ArqException {
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		MovimentacaoAluno movimentacaoAluno = (MovimentacaoAluno) mov.getObjMovimentado();

		// verifica se o aluno j� tem o mesmo tipo de movimenta��o no semestre escolhido
		MovimentacaoAlunoDao dao = getDAO(MovimentacaoAlunoDao.class, mov);
		try {
			Collection<MovimentacaoAluno> movimentacoes = dao.findByDiscente(movimentacaoAluno.getDiscente(), movimentacaoAluno.getAnoReferencia(),
					0, movimentacaoAluno.getTipoMovimentacaoAluno());
			
			if (movimentacoes != null && movimentacoes.size() > 0) {
				throw new NegocioException("O aluno j� possui movimenta��o do tipo "
						+ movimentacaoAluno.getTipoMovimentacaoAluno().getDescricao() + " em " + movimentacaoAluno.getAnoReferencia());
			}
			
		} finally {
			dao.close();
		}
		
		DiscenteAdapter d = movimentacaoAluno.getDiscente();
		// s� pode afastar aluno ativo
		if (!movimentacaoAluno.isTrancamento() && StatusDiscente.isAfastamento(d.getStatus())) {

			//pode cancelar programa de discente trancado
			if( movimentacaoAluno.isCancelamento() && d.getStatus() == StatusDiscente.TRANCADO )
				return;

			// se o aluno estiver afastado
			throw new NegocioException("N�o � poss�vel afastar um discente j� afastado da institui��o");
		} else if (movimentacaoAluno.isTrancamento() &&
				(d.getStatus() == StatusDiscente.CANCELADO || d.getStatus() == StatusDiscente.CONCLUIDO)) {
			throw new NegocioException("N�o � poss�vel trancar progama de discentes conclu�dos ou cancelados");
		}
		
		ListaMensagens erros = new ListaMensagens();

		ValidatorUtil.validateRequired(movimentacaoAluno.getDiscente(), "Discente", erros);
		ValidatorUtil.validateRequired(movimentacaoAluno.getTipoMovimentacaoAluno(), "Tipo", erros);
		ValidatorUtil.validateRequired(movimentacaoAluno.getAnoReferencia(), "Ano", erros);
		ValidatorUtil.validateMinValue(movimentacaoAluno.getAnoReferencia(), 1900, "Ano", erros);
		
		if (movimentacaoAluno.getTipoMovimentacaoAluno().getId() == TipoMovimentacaoAluno.CONCLUSAO) 
			ValidatorUtil.validateRequired( movimentacaoAluno.getDataColacaoGrau(), "Data de Cola��o", erros);
		
		checkValidation( erros );		
		
	}	

}
