/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 01/11/2006
 *
 */

package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ParametrosGestoraAcademicaDao;
import br.ufrn.sigaa.arq.dao.ead.TutoriaAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.TutoriaAluno;
import br.ufrn.sigaa.ensino.dominio.BancaDefesa;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.RegistroAtividade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.StatusBanca;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.RestricoesMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator;
import br.ufrn.sigaa.ensino.graduacao.negocio.RegistroAtividadeValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.RegistroAtividadeMov;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.ensino.stricto.dominio.RenovacaoAtividadePos;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.negocio.MovimentoCalculoHistorico;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.prodocente.atividades.dominio.Estagio;
import br.ufrn.sigaa.prodocente.atividades.dominio.TrabalhoFimCurso;

/**
 * Processador para matricular, consolidar e validar registros de atividades acad�micas espec�ficas.
 * 
 * @author Andr�
 * @author Ricardo Wendell
 */
public class ProcessadorRegistroAtividade extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		if (mov.getCodMovimento().equals(SigaaListaComando.MATRICULAR_ATIVIDADE)) {
			validate(mov);
			validarPeriodoDiscente(mov);
			matricular(mov);
			realizarCalculosDiscente(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.CONSOLIDAR_ATIVIDADE)) {
			consolidar(mov);
			cadastrarDadosProducaoIntelectual(mov);
			realizarCalculosDiscente(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.VALIDAR_ATIVIDADE)) {
			validate(mov);
			validarPeriodoDiscente(mov);
			validarRegistroAtividade(mov);
			cadastrarDadosProducaoIntelectual(mov);
			realizarCalculosDiscente(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.EXCLUIR_MATRICULA_ATIVIDADE)) {
			removerDadosProducaoIntelectual(mov);
			removerDadosBanca(mov);
			excluir(mov);
			realizarCalculosDiscente(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.RENOVAR_MATRICULA_ATIVIDADE)) {
			renovar(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_PERIODO_ATIVIDADE)) {
			alterar(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_ATIVIDADE_GRADUACAO)) {
			validarPeriodoDiscente(mov);
			alterar(mov);
			cadastrarDadosProducaoIntelectual(mov);
			realizarCalculosDiscente(mov);
		}
		return null;
	}


	/**
	 * Renova a matr�cula em uma atividade acad�mica espec�fica (Trabalho Final de Curso, por exemplo).
	 * @param mov
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void renovar(Movimento mov) throws SegurancaException, ArqException, NegocioException {
		RegistroAtividadeMov movimento = (RegistroAtividadeMov) mov;
		verificarPermissoes(movimento);
		
		// Validar renova��o
		ListaMensagens erros = new ListaMensagens();
		RegistroAtividadeValidator.validarRenovacao(movimento.getMatricula(), erros);
		checkValidation(erros);
		
		CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendario(movimento.getMatricula().getDiscente());
		
		// Cadastrar renova��o
		RenovacaoAtividadePos renovacao = new RenovacaoAtividadePos();
		renovacao.setAno( calendario.getAno() );
		renovacao.setPeriodo( calendario.getPeriodo() );
		renovacao.setMatricula( movimento.getMatricula() );
		renovacao.setDiscente(movimento.getMatricula().getDiscente());
		renovacao.setAtivo(true);
		getGenericDAO(movimento).create(renovacao);
	}

	/**
	 * Verifica se o usu�rio logado tem permiss�o para executar a opera��o corrente (passado como par�metro).
	 * @param mov
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void verificarPermissoes(RegistroAtividadeMov mov)
			throws SegurancaException, ArqException, DAOException,
			NegocioException {
		
		TutoriaAlunoDao tutoriaDao = getDAO(TutoriaAlunoDao.class, mov);
		try {
			if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_PERIODO_ATIVIDADE)) {
				checkRole(new int[]{SigaaPapeis.PPG}, mov);						
			} else if (!mov.isRegistroTutor() && !mov.isOrientador()) {
				checkRole(new int[]{SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.COORDENADOR_GERAL_EAD, SigaaPapeis.SECRETARIA_COORDENACAO,
						SigaaPapeis.DAE, SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS,
						SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO,
						SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR,
						SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA,SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA}, mov);
			} else if (mov.isRegistroTutor()) {
				TutoriaAluno tutoria = tutoriaDao.findUltimoByAluno( mov.getMatricula().getDiscente().getId());
				Usuario u = (Usuario) mov.getUsuarioLogado();
				if (tutoria == null || u.getVinculoAtivo().getTutor() == null || (u.getVinculoAtivo().getTutor() != null && u.getVinculoAtivo().getTutor().getId() != tutoria.getTutor().getId() )) {
					throw new NegocioException("Este aluno n�o pode ser matriculado pois n�o est� sob sua tutoria.");
				}
			}
		} finally {
			tutoriaDao.close();
		}
	}

	/**
	 * Exclui a matr�cula na atividade selecionada
	 * @param mov
	 * @throws ArqException
	 * @throws SegurancaException
	 * @throws NegocioException
	 */
	private Object excluir(Movimento m) throws SegurancaException, ArqException, NegocioException {
		RegistroAtividadeMov mov = (RegistroAtividadeMov) m;
		verificarPermissoes(mov);

		MatriculaComponente matricula =  mov.getMatricula();
		try {
			MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, mov);
			SituacaoMatricula novaSituacao = SituacaoMatricula.EXCLUIDA;
			MatriculaComponenteHelper.alterarSituacaoMatricula(matricula, novaSituacao, mov, dao);
			removerSolicitacaoMatricula(mov); 
		} catch (Exception e) {
			throw new ArqException(e);
		}
		return matricula;
	}
	
	/** 
	 * Inativa a solicita��o de matr�cula associada ao registro de matr�cula de atividade exclu�do. 
	 * @param mov 
	 * @throws DAOException	   
	 */
	private void removerSolicitacaoMatricula(RegistroAtividadeMov mov) throws DAOException { 
		MatriculaComponente matricula = mov.getMatricula(); 
		GenericDAO dao = getGenericDAO(mov); 
		try {			
			SolicitacaoMatricula solicitacao = (SolicitacaoMatricula) dao.findByExactField(SolicitacaoMatricula.class, "matriculaGerada.id", matricula.getId(),true);			
			if(!isEmpty(solicitacao)) 
				dao.updateField(SolicitacaoMatricula.class, solicitacao.getId(), "status", SolicitacaoMatricula.EXCLUIDA);			
		}
		finally { 
			dao.close();		
		}		
	}

	/**
	 * Realiza os c�lculos do discente para atualizar suas integraliza��es, ou seja, a soma dos cr�ditos e cargas hor�rias
	 * cursados(as)/pendentes, considerando a nova situa��o das suas matr�culas nos componentes curriculares 
	 * @param mov
	 * @throws RemoteException 
	 */
	private void realizarCalculosDiscente(Movimento m) throws NegocioException, ArqException, RemoteException {
		
		
		RegistroAtividadeMov mov = (RegistroAtividadeMov) m;
		MatriculaComponente matricula = mov.getMatricula();
			
		if (matricula.getDiscente().getTipo() == Discente.ESPECIAL) return;
		
		Discente d = getGenericDAO(mov).findByPrimaryKey(matricula.getDiscente().getDiscente().getId(), Discente.class);
		
		if (d.isGraduacao())
			getGenericDAO(mov).updateField(DiscenteGraduacao.class, d.getId(), "ultimaAtualizacaoTotais", null);
	
		MovimentoCalculoHistorico movHistorico = new MovimentoCalculoHistorico();
		movHistorico.setUsuarioLogado(mov.getUsuarioLogado());
		movHistorico.setSistema(mov.getSistema());
		movHistorico.setRecalculaCurriculo(true);
		movHistorico.setDiscente(d);
		movHistorico.setCodMovimento(SigaaListaComando.CALCULAR_HISTORICO_DISCENTE);
		
		
		new ProcessadorCalculaHistorico().execute(movHistorico);			
			
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		RegistroAtividade registro = ( (RegistroAtividadeMov) mov ).getMatricula().getRegistroAtividade();

		if( !registro.isMatriculaCompulsoria() ){
			validarTipoDiscente(mov);
			validarStatusDiscente(mov);
			validarAtividade(mov);
		} else {
			if (registro.getCoordenador() != null && registro.getCoordenador().getId() == 0) {
				registro.setCoordenador(null);
			}

		}
	}

	/**
	 * Apenas discentes do tipo REGULAR e ESPECIAL (com forma de ingresso 'ALUNO ESPECIAL')
	 * podem ser matriculados 
	 * @param m movimento que cont�m o discente e a matr�cula
	 * @throws NegocioException
	 */
	private void validarTipoDiscente(Movimento m) throws NegocioException {
		RegistroAtividadeMov mov = (RegistroAtividadeMov) m;
		MatriculaComponente matricula = mov.getMatricula();
		DiscenteAdapter d = matricula.getDiscente();
		ListaMensagens erros = new ListaMensagens();
		if ( !RegistroAtividadeValidator.isPermiteRegistrarAtividade(d, erros) ) {
			throw new NegocioException(erros.getMensagens().iterator().next().getMensagem());
		}
	}

	/**
	 * Quando a atividade tem orientador, na consolida��o da atividade tamb�m s�o cadastrados os 
	 * dados da produ��o intelectual do orientador. Esta opera��o persiste as informa��es 
	 * passadas na produ��o intelectual do orientador,
	 * de acordo com o tipo de atividade orientada: Est�gio ou Trabalho de Fim de Curso.
	 * 
	 * @param m
	 * @throws ArqException
	 */
	public void cadastrarDadosProducaoIntelectual(Movimento m) throws ArqException {
		RegistroAtividadeMov mov = (RegistroAtividadeMov) m;
		GenericDAO dao = getGenericDAO(mov);

		try {
			if (mov.getEstagio() != null &&  mov.getEstagio().getServidor() != null && mov.getEstagio().getServidor().getId() != 0 ) {
				mov.getEstagio().setMatricula(mov.getMatricula());
				//Atualiza os dados do est�gio quando a operaao for de altera��o
				if( mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_ATIVIDADE_GRADUACAO) )
					dao.update(mov.getEstagio());
				dao.create(mov.getEstagio());
			} 
			if (mov.getTrabalhoFimCurso() != null &&  (( mov.getTrabalhoFimCurso().getServidor() != null 
					&& mov.getTrabalhoFimCurso().getServidor().getId() != 0 ) || (mov.getTrabalhoFimCurso().getDocenteExterno() != null 
							&& mov.getTrabalhoFimCurso().getDocenteExterno().getId() != 0))) {
				mov.getTrabalhoFimCurso().setMatricula(mov.getMatricula());
				
				//Caso o aluno de gradua��o tenha sido reprovado, n�o registra os dados da produ��o intelectual.
				if ( mov.getMatricula().getComponente().getNivel() == NivelEnsino.GRADUACAO && mov.getMatricula().isReprovado() ){
					return;
				}	
				//Atualiza os dados do tcc quando a operaao for de altera��o
				if( mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_ATIVIDADE_GRADUACAO) )
					dao.update(mov.getTrabalhoFimCurso());
				dao.create(mov.getTrabalhoFimCurso() );
			}
			
		} finally {
			dao.close();
		}
	}

	/**
	 * Matr�cula e consolida na mesma hora
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws SegurancaException
	 */
	private Object validarRegistroAtividade(Movimento m) throws SegurancaException, ArqException, NegocioException {
		RegistroAtividadeMov mov = (RegistroAtividadeMov) m;
		verificarPermissoes(mov);
		validate(m);

		MatriculaComponente matricula = mov.getMatricula();
		RegistroAtividade registro = matricula.getRegistroAtividade();

		Usuario usuario = (Usuario) mov.getUsuarioLogado();

		GenericDAO dao = getGenericDAO(mov);
		try {
			// Preparar matr�cula para persist�ncia
			matricula.setTurma(null);
			matricula.setRecuperacao(null);
			matricula.setComponente(registro.getMatricula().getComponente());
			matricula.setDetalhesComponente(registro.getMatricula().getDetalhesComponente());
			matricula.setAno(registro.getMatricula().getAno().shortValue());
			matricula.setPeriodo(registro.getMatricula().getPeriodo().byteValue());

			// Salvar informa��es sobre o cadastro da matr�cula
			matricula.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			matricula.setDataCadastro(new Date());

			
			if ( matricula.getDiscente().isGraduacao()){
				DiscenteGraduacao grad = (DiscenteGraduacao) getDAO(DiscenteDao.class, mov).findByPK(matricula.getDiscente().getId());
				if(!RegistroAtividadeValidator.validarDocentesEnvolvidos(usuario, true, grad) ) {
					if ( registro.getCoordenador() != null && registro.getCoordenador().getId() == 0 ) {
						registro.setCoordenador(null);
					}
				}
			}
			
			if ( matricula.getDiscente().isTecnico()){
				DiscenteTecnico discenteTecnico = (DiscenteTecnico) getDAO(DiscenteDao.class, mov).findByPK(matricula.getDiscente().getId());
				if(!RegistroAtividadeValidator.validarDocentesEnvolvidosTecnico(usuario, true, discenteTecnico) ) {
					if ( registro.getCoordenador() != null && registro.getCoordenador().getId() == 0 ) {
						registro.setCoordenador(null);
					}
				}
			}

			// Persistir registro de atividade
			matricula.setRegistroAtividade(null);
			dao.create(matricula);
			dao.create(registro);
			matricula.setRegistroAtividade(registro);
			dao.update(matricula);

		} catch (Exception e) {
			throw new ArqException(e);
		} finally {
			dao.close();
		}
		return matricula;
	}

	/**
	 * Efetua a consolida��o do registro de atividade movimentado
	 * @param m
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private Object consolidar(Movimento m) throws SegurancaException, ArqException, NegocioException {
		RegistroAtividadeMov mov = (RegistroAtividadeMov) m;
		verificarPermissoes(mov);

		MatriculaComponente matricula =  mov.getMatricula();
		try {
			MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, mov);
			SituacaoMatricula novaSituacao = matricula.getSituacaoMatricula();
			MatriculaComponenteHelper.alterarSituacaoMatricula(matricula, novaSituacao, mov, dao);

			if ( matricula.getRegistroAtividade().getId() == 0 ) {
				dao.create(matricula.getRegistroAtividade());
			}

			dao.clearSession();
			dao.update(matricula);

		} catch (Exception e) {
			throw new ArqException(e);
		}
		return matricula;
	}
	
	/**
	 * Respons�vel por alterar a matr�cula do discente na atividade.
	 * @param m
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private Object alterar(Movimento m) throws SegurancaException, ArqException, NegocioException{
		RegistroAtividadeMov mov = (RegistroAtividadeMov) m;
		verificarPermissoes(mov);

		MatriculaComponente matricula =  mov.getMatricula();
		try {
			MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, mov);
			dao.update(matricula);
			
			if( mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_ATIVIDADE_GRADUACAO) 
					&& !isEmpty( matricula.getRegistroAtividade().getId() ) ){
				dao.update(matricula.getRegistroAtividade());
			}	
			
		} catch (Exception e) {
			throw new ArqException(e);
		}
		return matricula;		
	}

	/**
	 * Efetua a matr�cula do discente na atividade selecionada
	 * @param m
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private Object matricular(Movimento m) throws SegurancaException, ArqException, NegocioException {

		RegistroAtividadeMov mov = (RegistroAtividadeMov) m;

		verificarPermissoes(mov);

		MatriculaComponente matricula =  mov.getMatricula();
		RegistroAtividade registro = matricula.getRegistroAtividade();

		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, mov);

		DiscenteGraduacaoDao dgdao = getDAO(DiscenteGraduacaoDao.class, mov);
		try {
			// Preparar matr�cula para persist�ncia
			matricula.setSituacaoMatricula(SituacaoMatricula.MATRICULADO);
			matricula.setTurma(null);
			matricula.setRecuperacao(null);
			matricula.setRegistroAtividade(registro);
			matricula.setComponente(registro.getMatricula().getComponente());
			matricula.setDetalhesComponente(registro.getMatricula().getDetalhesComponente());
			if(	matricula.getAnoInicio() == null)
				matricula.setAnoInicio(CalendarUtils.getAnoAtual());
			matricula.setAno(registro.getMatricula().getAno().shortValue());
			matricula.setPeriodo(registro.getMatricula().getPeriodo().byteValue());

			// Salvar informa��es sobre o cadastro da matr�cula
			matricula.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			matricula.setDataCadastro(new Date());

			// Persistir registro de atividade
			matricula.setRegistroAtividade(null);
			dao.create(matricula);

			dao.create(registro);
			matricula.setRegistroAtividade(registro);
			dao.update(matricula);

		} catch (Exception e) {
			throw new ArqException(e);
		} finally {
			dgdao.close();
			dao.close();
		}
		return matricula;
	}

	/**
	 * S� � permitido registrar atividades apenas para alunos ativos, formandos ou cadastrados
	 * Este m�todo realiza esta valida��o
	 * @param m
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void validarStatusDiscente(Movimento m) throws NegocioException, ArqException  {
		RegistroAtividadeMov mov = (RegistroAtividadeMov) m;
		MatriculaComponente matricula = mov.getMatricula();
		DiscenteAdapter discente = matricula.getDiscente();
		if (discente.getStatus() != StatusDiscente.ATIVO  && discente.getStatus() != StatusDiscente.FORMANDO
				&& discente.getStatus() != StatusDiscente.CADASTRADO && ( mov.getUsuarioLogado().isUserInRole(SigaaPapeis.PPG) && discente.getStatus() != StatusDiscente.CONCLUIDO ) )
			throw new NegocioException("� permitido registrar atividades apenas para alunos ativos, formandos ou cadastrados");
	}

	/**
	 * Realiza as principais valida��es sobre a matr�cula dos alunos nas atividades, verifica se o aluno tem o pre-requisito, equival�ncia e co-requisito, se
	 * n�o ultrapassou o limite de cr�ditos por semestre, limite de cr�ditos extra, etc...
	 * @param m
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void validarAtividade(Movimento m) throws NegocioException, ArqException {
		RegistroAtividadeMov mov = (RegistroAtividadeMov) m;
		MatriculaComponente matricula = mov.getMatricula();
		RegistroAtividade registro = matricula.getRegistroAtividade();
		DiscenteAdapter discente = matricula.getDiscente();

		MatriculaComponenteDao matdao = getDAO(MatriculaComponenteDao.class, mov);
		DiscenteDao ddao = getDAO(DiscenteDao.class, mov);
		try {
			// Busca todos os componentes de turmas aprovadas (ou aproveitadas) antes
			Collection<ComponenteCurricular> componentesConcluidos  = ddao.findComponentesCurricularesConcluidos(discente);
			Collection<ComponenteCurricular> componentesMatriculados = matdao.findComponentesMatriculadosByDiscente(discente);

			// Se for uma consolida��o, desconsiderar o componente atual
			if (mov.getCodMovimento().equals(SigaaListaComando.CONSOLIDAR_ATIVIDADE) ) {
				componentesMatriculados.remove(matricula.getComponente());
			}

			// criando uma cole��o de todos os componentes (conclu�dos e das atividades do semestre)
			Collection<ComponenteCurricular> todosComponentes = new ArrayList<ComponenteCurricular>(componentesConcluidos);
			todosComponentes.addAll(componentesMatriculados);

			DiscenteAdapter adapter = ddao.findByPK(discente.getId());

			ListaMensagens msgs = MatriculaGraduacaoValidator.validarComponenteIndividualmente(adapter,
					registro.getMatricula().getComponente(), matricula.getAno(), matricula.getPeriodo(),
					componentesMatriculados,
					todosComponentes,
					RestricoesMatricula.getRestricoesRegistroAtividade(registro));

			// Validar permiss�o de realizar matr�cula compuls�ria
			if ( registro.isMatriculaCompulsoria() && !mov.getUsuarioLogado().isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE) ) {
				msgs.addErro("Seu usu�rio n�o possui permiss�o para realizar registros compuls�rios de atividades acad�micas");
			}

			checkValidation(msgs);

		} finally {
			ddao.close();
			matdao.close();
		}

	}

	/**
	 * Valida se o ano.periodo do discente � v�lido:
	 *  - O ano.per�odo do registro da atividade n�o pode ser inferior ao ano.per�odo de ingresso do discente
	 *  - O discente n�o pode ter afastamento no ano.periodo  do registro da atividade
	 *  - O ano.per�odo do registro da atividade n�o pode ser superior ao ano.per�odo atual
	 * @param m
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void validarPeriodoDiscente(Movimento m) throws ArqException, NegocioException {


		RegistroAtividadeMov mov = (RegistroAtividadeMov) m;
		MatriculaComponente matricula = mov.getMatricula();
		RegistroAtividade registro = matricula.getRegistroAtividade();
		DiscenteAdapter discente = matricula.getDiscente();

		MovimentacaoAlunoDao movdao = getDAO(MovimentacaoAlunoDao.class, mov);
		ParametrosGestoraAcademicaDao pdao = getDAO(ParametrosGestoraAcademicaDao.class, mov);
		try {

			int anoPeriodoRegistro = new Integer(registro.getMatricula().getAno() + "" +  registro.getMatricula().getPeriodo());
			
			RegistroAtividadeValidator.validarAfastamentos(registro.getMatricula(), discente, anoPeriodoRegistro);
			
			if( ( (RegistroAtividadeMov) m ).getMatricula().getRegistroAtividade().isMatriculaCompulsoria() ){
				return;
			}
			
			int anoPeriodoDiscente = new Integer(discente.getAnoIngresso() +  "" + discente.getPeriodoIngresso());
			boolean anoPeriodoCompulsorio = m.getUsuarioLogado().isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE);
			if (!anoPeriodoCompulsorio) {
				if (anoPeriodoRegistro < anoPeriodoDiscente)
					throw new NegocioException("Ano e per�odo inv�lidos: O discente " +
							"ingressou em " + discente.getAnoPeriodoIngresso());
			}

			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente);
			int anoPeriodoAtual = new Integer(cal.getAno() + "" +  cal.getPeriodo());
			int anoPeriodoFerias = 0;
			if( cal.getAnoFeriasVigente() != null && cal.getPeriodoFeriasVigente() != null){
				anoPeriodoFerias = new Integer(cal.getAnoFeriasVigente() + "" +  cal.getPeriodoFeriasVigente());
			}
			
			if (anoPeriodoRegistro > anoPeriodoAtual && (anoPeriodoFerias == 0 || anoPeriodoRegistro != anoPeriodoFerias) ){
				throw new NegocioException("Ano e per�odo inv�lidos: s� � permitido registrar atividades no m�ximo" +
						" at� o semestre atual ou per�odo de f�rias correspondente: " + cal.getAnoPeriodo() 
						+ (cal.getAnoPeriodoFeriasVigente() !=  null ? " ou " + cal.getAnoPeriodoFeriasVigente() : ""));
			}

		} finally  {
			pdao.close();
			movdao.close();
		}
	}
	
	/**
	 * Inativa a banca quando a matr�cula do componente for exclu�da.
	 * @param m
	 * @throws ArqException
	 * @throws RemoteException 
	 * @throws NegocioException 
	 */
	private void removerDadosBanca(Movimento m) throws ArqException, NegocioException, RemoteException {

		RegistroAtividadeMov mov = (RegistroAtividadeMov) m;
		GenericDAO dao = getGenericDAO(mov);
		MatriculaComponente matricula = mov.getMatricula();
		
		try {
			int tipo = matricula.getComponente().getTipoAtividade().getId();
			Class<?> classe = null;
			
			if ( tipo == TipoAtividade.TESE || tipo == TipoAtividade.QUALIFICACAO ){
				classe = BancaPos.class;
			}else if( tipo == TipoAtividade.TRABALHO_CONCLUSAO_CURSO ){
				classe = BancaDefesa.class;
			}
			if( classe != null ){
				PersistDB obj = (PersistDB) dao.findByExactField(classe, "matriculaComponente", matricula.getId(), true );
				if( !isEmpty(obj) )
					dao.updateField( obj.getClass(), obj.getId(), "status", StatusBanca.CANCELADO );
			}
		
		} finally {
			dao.close();
		}
		
	}
	
	/**
	 * Remove a matr�cula em uma atividade acad�mica espec�fica.
	 * @param m
	 * @throws ArqException
	 */
	private void removerDadosProducaoIntelectual(Movimento m) throws ArqException {
		
		RegistroAtividadeMov mov = (RegistroAtividadeMov) m;
		GenericDAO dao = getGenericDAO(mov);
		MatriculaComponente matricula = mov.getMatricula();
		final int tipo = matricula.getComponente().getTipoAtividade().getId();
		
		try {
			switch (tipo) {
			
			case TipoAtividade.ESTAGIO:
				Estagio estagio = dao.findByExactField(Estagio.class, "matricula", matricula.getId(), true);
				if (estagio != null)
					dao.updateField(Estagio.class, estagio.getId(), "ativo", false);
				break;
			case TipoAtividade.TRABALHO_CONCLUSAO_CURSO:
				TrabalhoFimCurso trabalho = dao.findByExactField(TrabalhoFimCurso.class, "matricula",matricula.getId(), true);
				if (trabalho != null)
					dao.updateField(TrabalhoFimCurso.class, trabalho.getId(), "ativo", false);
				break;
			}
		} finally {
			dao.close();
		}
	}
}