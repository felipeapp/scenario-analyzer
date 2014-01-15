/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 08/08/2008
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator.validarMatriculaTurmasBloco;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ead.HorarioTutoriaDao;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.SecretariaUnidadeDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.RenovacaoAtividadePosDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.HorarioTutoria;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.OrientacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SecretariaUnidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoEnsinoIndividual;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.calculos.IntegralizacoesHelper;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.negocio.MatriculaComponenteHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.RenovacaoAtividadePos;
import br.ufrn.sigaa.ensino.stricto.negocio.ProcessadorMatriculaStricto;

/**
 *	Processador responsável por realizar CADASTRAMENTO e ANALISE de SOLICITACAO DE MATRICULA.
 * @author amdantas
 * @author Victor Hugo
 */
public class ProcessadorSolicitacaoMatricula extends AbstractProcessador {

	/** Executa a solicitação de matrícula.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException {
		
		// Verifica se o aluno concorda com o termo. Aplicado para matrículas apartir de 2010.1 devido a introdução do novo regulamento
		// dos curso de graduação
		if(mov.getCodMovimento().equals(SigaaListaComando.CONCORDAR_REGULAMENTO)){
			Connection con = null;
			Statement st = null;
			ResultSet rs = null;
			try {
				con = Database.getInstance().getSigaaConnection();
				st = con.createStatement();
				MovimentoSolicitacaoMatricula movimento = (MovimentoSolicitacaoMatricula) mov;
				DiscenteAdapter discente = movimento.getDiscente();
				
				// Primeiro verifica se já existe registro no banco de que o discente concordou com o novo regulamento
				rs = st.executeQuery("SELECT id_discente FROM graduacao.concordancia_regulamento WHERE id_discente="+discente.getId());
				if(!rs.next())
					st.executeUpdate( "INSERT INTO graduacao.concordancia_regulamento (id_discente, data) " +
						"VALUES ("+discente.getId()+
						", '"+new Date()+"')" );
				
			} catch (SQLException e) {
				e.printStackTrace();
				throw new ArqException("Erro ao registrar concordância com o novo regulamento!");
			} finally {
				try {
					if (rs != null) rs.close();
					if (st != null) st.close();
					if(con != null) Database.getInstance().close(con);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		
		if( !mov.getCodMovimento().equals(SigaaListaComando.ALUNO_EAD_DEFININDO_HORARIO_TUTORIA) ){
			validate(mov);
		}
		
		if (mov.getCodMovimento().equals(SigaaListaComando.SOLICITACAO_MATRICULA)) {
			MovimentoSolicitacaoMatricula movimento = (MovimentoSolicitacaoMatricula) mov;
			validarSolicitacao(movimento);
			
			if (movimento.getAcao() == MovimentoSolicitacaoMatricula.SUBMETER_NOVAS)
				return submeterNovas(mov);
			else if (movimento.getAcao() == MovimentoSolicitacaoMatricula.SUBMETER_CADASTRADAS)
				return submeterCadastradas(mov);
			else if (movimento.getAcao() == MovimentoSolicitacaoMatricula.CADASTRAR)
				return cadastrarNovas(mov);
			else if (movimento.getAcao() == MovimentoSolicitacaoMatricula.SUBMETER_JA_ORIENTADAS)
				return submeterJaOrientadas(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.ANALISAR_SOLICITACAO_MATRICULA)) {
			return analisar(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.ANULAR_SOLICITACAO_MATRICULA)) {
			return anular(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.ANULAR_SOLICITACAO_MATRICULA_AUTOMATICO)) {
			return anularAutomatico(mov);
		}else if( mov.getCodMovimento().equals(SigaaListaComando.ALUNO_EAD_DEFININDO_HORARIO_TUTORIA) ){
			MovimentoSolicitacaoMatricula movimento = (MovimentoSolicitacaoMatricula) mov;
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(movimento.getDiscente());
			cadastrarHorarioEAD(movimento, cal);
		}

		return null;
	}

	/** Verifica se o papel está dentro de Administrador Dae, Dae e Gestor Técnico, e caso verdadeiro anula as solicitações.
	 * 
	 * @param mov
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private Object anular(Movimento mov) throws ArqException, NegocioException {
		checkRole(new int[] { SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR }, mov);
		return anularAutomatico(mov);
	}

	/**
	 * Anula as solicitações de matrícula sem verificar o papel.
	 * 
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private Object anularAutomatico(Movimento mov) throws DAOException {
		MovimentoSolicitacaoMatricula movimento = (MovimentoSolicitacaoMatricula) mov;
		GenericDAO dao = getGenericDAO(mov);
		UsuarioDao udao = getDAO(UsuarioDao.class, movimento);
		
		try{
			
			StringBuilder descricaoTurmas = new StringBuilder();
			Formatador fmt = Formatador.getInstance();
			for (SolicitacaoMatricula sol : movimento.getSolicitacoes()) {
				if (sol.getAnulado()) {
					sol.setRegistroAnulacao(mov.getUsuarioLogado().getRegistroEntrada());
					descricaoTurmas.append(sol.getTurma().getDisciplina().getDescricaoResumida() + " - Turma "+
							sol.getTurma().getCodigo() + ", realizada em "+fmt.formatarDataHoraSec(sol.getDataCadastro())+".\n");
					descricaoTurmas.append("Motivo: "+sol.getObservacaoAnulacao()+"\n");
					dao.decrNumField(Turma.class, sol.getTurma().getId(), "totalSolicitacoes");
				}
				dao.updateNoFlush(sol);
			}
			
			if (!isEmpty(movimento.getSolicitacoes())) {
				
				//enviando email para aluno para notificar que algumas de suas solicitações foram anuladas
				MailBody mail = new MailBody();
				
				Usuario usuarioDiscente = udao.findByDiscente(movimento.getDiscente().getDiscente());
				
				String emailDiscente = null;
				if (usuarioDiscente != null)
					emailDiscente = usuarioDiscente.getEmail(); //pega email da pessoa do discente
				else
					emailDiscente = movimento.getDiscente().getPessoa().getEmail();
				if (emailDiscente != null ) {
					mail.setContentType(MailBody.TEXT_PLAN);
					mail.setEmail( emailDiscente );
					mail.setNome( movimento.getDiscente().getNome() );
					mail.setAssunto("Anulação de Solicitação de Matrículas");
					mail.setMensagem("Caro(a) " + movimento.getDiscente().getNome() +", " +
							"\n\n Suas solicitações de matrícula nas seguintes turmas foram anuladas:\n" +
							descricaoTurmas+
							"\n\n" +
							"Para maiores informações, por favor entre em contato com " + 
							(movimento.getDiscente().getNivel() == NivelEnsino.TECNICO ? "a Coordenação." : "o DAE.") +
							"\n\n" +
							"Mensagem Gerada Automaticamente pelo SIGAA - Favor não Responder." );
					
					//usuário que esta enviando e email para resposta.
					mail.setReplyTo( mov.getUsuarioLogado().getEmail() );
					Mail.send(mail);
					
				}
				
			}	
			
		}finally{
			dao.close();
			udao.close();
		}
		
		
		
		return null;
	}

	/**
	 * Persiste a análise de matrícula realizada pelo coordenador.
	 * <ul>
	 * <li>Se for EAD as matriculas aprovadas irão direto para o status
	 * MATRICULADO. E as negadas estarão negadas DEFINITIVAMENTE.</li>
	 * <li>Se for aluno PRESENCIAL as matriculas irão todas para o status
	 * EM_ESPERA independente se ela foi aceita ou negada.</li>
	 * <li>Se foi negada a solicitação deve conter uma observação.</li>
	 * <li>Se for analise de atividade (qualificação ou defesa) deve ser criado
	 * o registro em MatriculaComponente, caso o aluno já esteja matriculado
	 * deve ser registrado a renovação em RenovacaoAtividade.</li>
	 *</ul>
	 * 
	 * @param mov
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws RemoteException
	 */
	public Object analisar(Movimento mov) throws ArqException, NegocioException {
		checkRole(new int[]{SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.GESTOR_TECNICO,
				SigaaPapeis.COORDENADOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO,
				SigaaPapeis.ORIENTADOR_ACADEMICO, SigaaPapeis.ORIENTADOR_STRICTO, 
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS,
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR, SigaaPapeis.TUTOR_EAD, SigaaPapeis.DAE,
				SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO}, mov);
		
		MovimentoSolicitacaoMatricula movimento = (MovimentoSolicitacaoMatricula) mov;
		GenericDAO dao = getGenericDAO(mov);
		try {
			for (SolicitacaoMatricula solicitacao : movimento.getSolicitacoes()) {
				
				solicitacao.setDataAnalise(new Date());
				solicitacao.setRegistroEntrada( mov.getUsuarioLogado().getRegistroEntrada() );
				
				solicitacao.setDataAlteracao(new Date());
				solicitacao.setRegistroAlteracao(mov.getUsuarioLogado().getRegistroEntrada());
				dao.update(solicitacao);
			}

			// Caso tenha sido informada, cadastrar a orientação geral de matrícula
			OrientacaoMatricula orientacao = movimento.getOrientacao();
			if (orientacao != null && !StringUtils.isEmpty(orientacao.getOrientacao())) {
				orientacao.setDiscente(movimento.getDiscente().getDiscente());
				orientacao.setAno( movimento.getCalendarioAcademicoAtual().getAno() );
				orientacao.setPeriodo( movimento.getCalendarioAcademicoAtual().getPeriodo() );

				Usuario usuario = (Usuario) mov.getUsuarioLogado();

				if ( usuario.getServidorAtivo() != null )  {
					orientacao.setOrientador( usuario.getServidorAtivo() );
				} else if( usuario.getDocenteExternoAtivo() != null ){
					orientacao.setOrientadorExterno( usuario.getDocenteExternoAtivo() );
				}else if( usuario.getTutor() != null ){
					orientacao.setTutor( usuario.getTutor() );
				} else{
					throw new NegocioException("Não foi possível identificar se o usuário logado é um servidor, " +
							"docente externo ou um tutor. Portanto, a análise da matrícula não poderá ser concluída. " +
							"Entre em contato com a administração dos sistemas para solucionar este problema.");
				}



				if (orientacao.getId() == 0) {
					dao.create( orientacao );
				} else {
					dao.update( orientacao );
				}
			}

			if (movimento.getDiscente().isStricto()) {
				matricularDiscentesStricto(movimento);
			}

		}  finally {
			dao.close();
		}

		// Enviar email para aluno para notificar que o coordenador viu ou orientou a solicitação de trancamento
		Curso curso = movimento.getDiscente().getCurso();
		if (curso !=  null && !curso.isADistancia()) {
			enviarEmail(movimento);
		}

		return mov;
	}

	/**
	 * Caso seja análise de solicitação de matricula em ATIVIDADES DE PÓS. Se o
	 * coordenador/orientador aprovar o aluno deve ser MATRICULADO
	 * AUTOMATICAMENTE. Caso o aluno JÁ TENHA matrícula na atividade que está
	 * solicitando, esta deve ser apenas RENOVADA.
	 * 
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private void matricularDiscentesStricto(MovimentoSolicitacaoMatricula mov) throws NegocioException, ArqException {

		if( !mov.getUsuarioLogado().isUserInRole( SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.ORIENTADOR_STRICTO, SigaaPapeis.SECRETARIA_POS ) )
			throw new SegurancaException();

		// só há solicitação de matricula e analise de solicitação de matricula em atividades para o nível STRICTO
		if( !mov.getSolicitacoes().isEmpty() && !mov.getSolicitacoes().iterator().next().getDiscente().isStricto() )
			return;

		matricularDiscentesStrictoTurma(mov);
		matricularDiscentesStrictoAtividades(mov);

	}

	/**
	 * Matricula os alunos de STRICTO nas turmas aprovadas.
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public void matricularDiscentesStrictoTurma(MovimentoSolicitacaoMatricula mov) throws NegocioException, ArqException{
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, mov);

			try{
			
				for (SolicitacaoMatricula solicitacao : mov.getSolicitacoes()) {
		
		
					if( !solicitacao.isTipoAtividade() ){
		
						// se for disciplina deve matricular criar a turma e chamar o processador de matricula padrão
						if ( solicitacao.isAtendida() ) {
		
							// verificando se há vaga na turma 
							ListaMensagens erros = new ListaMensagens();
		
							if (solicitacao.getMatriculaGerada() == null) {
								MatriculaGraduacaoValidator.validarCapacidadeTurma(solicitacao.getTurma(), erros);
								checkValidation(erros);
								Calendar c = Calendar.getInstance();
								c.setTime(new Date());
								// cria uma matricula a partir da solicitação
								MatriculaComponente matricula = solicitacao.criarMatriculaMatriculado();
								matricula.setRegistroEntrada( mov.getUsuarioLogado().getRegistroEntrada() );
								matricula.setMes( c.get( Calendar.MONTH ) + 1 );
								matricula.setAnoInicio(CalendarUtils.getAnoAtual());
								dao.create(matricula);
		
								solicitacao.setMatriculaGerada(matricula);
								dao.update(solicitacao);
							}
						} else {							
							if(solicitacao.isAguardandoOutroPrograma() && solicitacao.getMatriculaGerada() != null && solicitacao.getId()!=0) {								
								getGenericDAO(mov).updateField(SolicitacaoMatricula.class, solicitacao.getId(), "status", SolicitacaoMatricula.ATENDIDA);
							}						
							// se não tiver matricula não faz nada
							// se tiver criado tem que excluir, caso a solicitação seja negada pelo coordenador do curso do discente
							// ou pelo coordenador do programa responsável pelo componente curricular.		
							if ((solicitacao.isNegada() || solicitacao.isNegadaOutroPrograma()) && solicitacao.getMatriculaGerada() != null
									|| solicitacao.isExcluida() && solicitacao.getMatriculaGerada() != null ) {
								MatriculaComponente matricula = solicitacao.getMatriculaGerada();
								MatriculaComponenteHelper.alterarSituacaoMatricula(matricula, SituacaoMatricula.EXCLUIDA, mov, dao);
		
								solicitacao.setMatriculaGerada(null);
								dao.update(solicitacao);
							}
						}
					}
				}	
				
			}finally{	
				dao.close();
			}	
		
	}
	
	/**
	 * Matricula os alunos de STRICTO nas turmas aprovadas.
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public void matricularDiscenteIMD (MovimentoSolicitacaoMatricula mov) throws NegocioException, ArqException {
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, mov);
			try {
				
				DiscenteAdapter discente = null;
				
				for (SolicitacaoMatricula solicitacao : mov.getSolicitacoes()) {
					if (solicitacao.getMatriculaGerada() == null) {
						Calendar c = Calendar.getInstance();
						c.setTime(new Date());
						// cria uma matricula a partir da solicitação
						MatriculaComponente matricula = solicitacao.criarMatriculaMatriculado();
						matricula.setRegistroEntrada( mov.getUsuarioLogado().getRegistroEntrada() );
						matricula.setMes( c.get( Calendar.MONTH ) + 1 );
						matricula.setAnoInicio(CalendarUtils.getAnoAtual());
						dao.create(matricula);
						discente = matricula.getDiscente();
					}
				}
				
				discente.setStatus(StatusDiscente.ATIVO);
				DiscenteHelper.alterarStatusDiscente(discente, discente.getStatus(), null, mov, dao);
				
			} finally {	
				dao.close();
			}	
	}

	/**
	 * Matrícula os alunos de STRICTO nas atividades aprovadas.
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private void matricularDiscentesStrictoAtividades(MovimentoSolicitacaoMatricula mov) throws NegocioException, ArqException {

		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, mov);
		RenovacaoAtividadePosDao daoRenovacao = getDAO(RenovacaoAtividadePosDao.class, mov);

		try {
			for (SolicitacaoMatricula solicitacao : mov.getSolicitacoes()) {
				
				// este trecho trata apenas das solicitações de ATIVIDADE 
				if( !solicitacao.isTipoAtividade() )
					continue;
				
				// matriculando em exame de proficiência ou atividade complementar
				if( solicitacao.getAtividade().isProficiencia() || solicitacao.getAtividade().isComplementarStricto() ) {
					
					if( solicitacao.isAtendida() ){
						
						// já foi atendida 
						if( solicitacao.getMatriculaGerada() != null )
							continue;
						
						Collection<MatriculaComponente> proficienciasAtivComplementares = dao.findByDiscenteEDisciplina(solicitacao.getDiscente(), solicitacao.getComponente(), SituacaoMatricula.MATRICULADO, SituacaoMatricula.APROVADO, SituacaoMatricula.APROVEITADO_CUMPRIU, SituacaoMatricula.APROVEITADO_DISPENSADO, SituacaoMatricula.APROVEITADO_TRANSFERIDO );
						// Se ele já pagou a proficiência ou atividade complementar não pode pagar novamente *
						if( !isEmpty( proficienciasAtivComplementares ) ){
							throw new NegocioException("O aluno não pode pagar " + solicitacao.getComponente().getDescricaoResumida() + " pois já cursou ou está matriculado nela.");
						} else {
							
							Calendar c = Calendar.getInstance();
							c.setTime(new Date());
							
							MatriculaComponente matriculaAtividade = new MatriculaComponente();
							matriculaAtividade.setComponente( solicitacao.getAtividade() );
							matriculaAtividade.setDetalhesComponente(solicitacao.getAtividade().getDetalhes());
							matriculaAtividade.setAnoInicio( CalendarUtils.getAnoAtual() );
							matriculaAtividade.setAno( solicitacao.getAno().shortValue() );
							matriculaAtividade.setPeriodo( solicitacao.getPeriodo().byteValue() );
							matriculaAtividade.setDiscente( solicitacao.getDiscente() );
							matriculaAtividade.setSituacaoMatricula( SituacaoMatricula.MATRICULADO );
							matriculaAtividade.setRegistroEntrada( mov.getUsuarioLogado().getRegistroEntrada() );
							matriculaAtividade.setDataCadastro( new Date() );
							matriculaAtividade.setMes( c.get( Calendar.MONTH ) + 1 );
							dao.create(matriculaAtividade);
							
							solicitacao.setMatriculaGerada(matriculaAtividade);
							dao.update( solicitacao );
							
						}
						
					} else{
						
						if( solicitacao.getMatriculaGerada() != null ){
							MatriculaComponenteHelper.alterarSituacaoMatricula(solicitacao.getMatriculaGerada(), SituacaoMatricula.EXCLUIDA, mov, dao);
							solicitacao.setMatriculaGerada(null);
							dao.update( solicitacao );
						}
						
					}
					
				}
				
				
				// matriculando em defesa ou qualificação
				if( solicitacao.getAtividade().isQualificacao() || solicitacao.getAtividade().isTese() ){
					// se for atividade deve verificar se vai renovar ou criar uma nova atividade e chamar o processador de registro de atividade
					RenovacaoAtividadePos renovacao = daoRenovacao.findBySolicitacaoMatriculaStatus(solicitacao.getId(), null);
					MatriculaComponente atividadeRenovada = dao.findAtividadeMatriculadoByDiscenteComponente(solicitacao.getDiscente(), solicitacao.getComponente());
					
					if( solicitacao.isAtendida() ){
						
						// já foi atendida 
						if( solicitacao.getMatriculaGerada() != null )
							continue;
						
						// se a solicitação for APROVADA pelo coordenador/orientador, 
						// deve criar a MatriculaComponente com o status MATRICULADO
						// caso a MatriculaComponente já existe deve renovar com a entidade RenovacaoAtividadePos
						
						// se o aluno ja tiver matriculado na atividade deve setar a MatriculaComponente nesta solicitação e criar uma renovação
						if( atividadeRenovada != null ){
							
							solicitacao.setMatriculaGerada(atividadeRenovada);
							
							if( renovacao == null
									&& (atividadeRenovada.getAno() != solicitacao.getAno().shortValue()
											|| atividadeRenovada.getPeriodo() != solicitacao.getPeriodo().byteValue() ) ){
								// se não tiver renovação deve criar uma 
								renovacao = new RenovacaoAtividadePos();
								renovacao.setAno( solicitacao.getAno() );
								renovacao.setPeriodo( solicitacao.getPeriodo() );
								renovacao.setSolicitacaoMatricula(solicitacao);
								renovacao.setDiscente(solicitacao.getDiscente());
								renovacao.setAtivo(true);
								dao.create(renovacao);
							} else if( renovacao != null && !renovacao.isAtivo() ){
								// se tiver renovação deve ativar ou negar de acordo com a opção do coordenador 
								renovacao.setAtivo(true);
								dao.update(renovacao);
							}
							
						}else{
							// se não tem MatriculaComponente para renovar deve criar uma nova
							
							Calendar c = Calendar.getInstance();
							c.setTime(new Date());
							
							MatriculaComponente matriculaAtividade = new MatriculaComponente();
							matriculaAtividade.setComponente( solicitacao.getAtividade() );
							matriculaAtividade.setDetalhesComponente(solicitacao.getAtividade().getDetalhes());
							matriculaAtividade.setAnoInicio( CalendarUtils.getAnoAtual() );
							matriculaAtividade.setAno( solicitacao.getAno().shortValue() );
							matriculaAtividade.setPeriodo( solicitacao.getPeriodo().byteValue() );
							matriculaAtividade.setDiscente( solicitacao.getDiscente() );
							matriculaAtividade.setSituacaoMatricula( SituacaoMatricula.MATRICULADO );
							matriculaAtividade.setRegistroEntrada( mov.getUsuarioLogado().getRegistroEntrada() );
							matriculaAtividade.setDataCadastro( new Date() );
							matriculaAtividade.setMes( c.get( Calendar.MONTH ) + 1 );
							//matriculaAtividade.setTipoIntegralizacao( TipoIntegralizacao.OBRIGATORIA );
							dao.create( matriculaAtividade );
							
							solicitacao.setMatriculaGerada(matriculaAtividade);
							
						}
						
					} else if( solicitacao.isNegada() ){
						// se a solicitação for NEGADA pelo coordenador/orientador
						// Se o aluno já tiver sido matriculado deve excluir a matricula
						// caso a matricula tenha sido renovada deve desativar a renovação
						
						if( solicitacao.getMatriculaGerada() != null ){
							
							MatriculaComponente matriculaGerada = solicitacao.getMatriculaGerada();
							// se a matricula gerada tiver sido criada agora, ou seja, o discente não estava matriculado nesta atividade
							// em períodos anteriores, então a matricula gerada deve ser excluída 
							if(renovacao == null){
								MatriculaComponenteHelper.alterarSituacaoMatricula(matriculaGerada, SituacaoMatricula.EXCLUIDA, mov, dao);
							} else{
								renovacao.setAtivo(false);
								dao.update(renovacao);
							}
							solicitacao.setMatriculaGerada(null);
						}
					}
					dao.update(solicitacao);
				}
			}		
		} finally {
			daoRenovacao.close();
			dao.close();
		}
	}

	/**
	 * Envia um email para o orientador acadêmico, avisando-o de que seu
	 * orientando solicitou uma matrícula em componente curricular.
	 * 
	 * @param movimento
	 * @throws DAOException
	 */
	private void enviarEmail( MovimentoSolicitacaoMatricula movimento ) throws DAOException {
		
		UsuarioDao udao = getDAO(UsuarioDao.class, movimento);
		Usuario usuarioDiscente = udao.findByDiscente(movimento.getDiscente().getDiscente());
		CoordenacaoCursoDao daoCoord = getDAO( CoordenacaoCursoDao.class, movimento );
		SecretariaUnidadeDao daoSecre = getDAO( SecretariaUnidadeDao.class, movimento );

		try{
			
			String emailDiscente = null;
			if (usuarioDiscente != null)
				emailDiscente = usuarioDiscente.getEmail(); //pega email da pessoa do discente
			else
				emailDiscente = movimento.getDiscente().getPessoa().getEmail();
			if (emailDiscente != null ) {
				MailBody mail = new MailBody();
				//destinatário: usuário que cadastrou a solicitação
				//nome: nome do destinatário
				mail.setEmail( emailDiscente );
				mail.setNome( movimento.getDiscente().getNome() );
	
				//assunto e mensagem
				mail.setAssunto("SIGAA - Orientação de Matrículas em Componentes Curriculares");
				mail.setMensagem("Caro(a) " + movimento.getDiscente().getNome() +", \n\n" +
						"Suas matrículas foram analisadas pela coordenação de seu curso.\n" +
						"As orientações cadastradas estão disponíveis em sua página no SIGAA, " +
						"na opção 'Ensino -> Matrícula On-line -> Ver Orientações de Matrícula'.\n\n");
	
				//usuário que esta enviando e email para resposta.
				mail.setReplyTo( movimento.getUsuarioLogado().getEmail() ); //email do coordenador que esta aprovando/orientando o trancamento
				mail.setContentType(MailBody.TEXT_PLAN);
				Mail.send(mail);
	
			}
	
			// se tiver alguma solicitação com a situação AGUARDANDO_OUTRO_PROGRAMA então o coordenador 
			// do outro programa deve ser notificado que existe uma solicitação de matrícula aguardando análise 
			for( SolicitacaoMatricula sol : movimento.getSolicitacoes() ){
				if( sol.isAguardandoOutroPrograma() ){
					MailBody mail = new MailBody();
					
					mail.setAssunto( "SIGAA - Orientação de Matrículas de Alunos de Outros Programas" );
					mail.setMensagem( "Caro Usuário, \n\n" +
							"   Existem solicitações de matrícula em disciplinas do seu programa " +
							"realizadas por discentes de outros programas aguardando análise. \n" +
							"   Para realizar a análise acesse o portal da coordenação de " +
							"programas Stricto no menu Matrículas -> Analisar matrícula de alunos de outros programas." +
							"\n\nMensagem Gerada Automaticamente pelo SIGAA - Favor não Responder" ) ;
					mail.setReplyTo( movimento.getUsuarioLogado().getEmail() );
					mail.setContentType(MailBody.TEXT_PLAN);
					
					Unidade programa = sol.getComponente().getUnidade();
					Collection<CoordenacaoCurso> coordenadores = daoCoord.findByPrograma(programa.getId(), TipoUnidadeAcademica.PROGRAMA_POS, true, null);
					for( CoordenacaoCurso cc : coordenadores ){
						mail.setNome( cc.getServidor().getNome() );
						mail.setEmail(cc.getEmailContato());
						Mail.send(mail);
					}
					
					Collection<SecretariaUnidade> secretarios = daoSecre.findByUnidade(programa.getId(),null);
					for( SecretariaUnidade su : secretarios ){
						mail.setNome( su.getUsuario().getNome() );
						mail.setEmail(su.getUsuario().getEmail());
						Mail.send(mail);
					}
	
					break;
				}
			}
			
		}finally{
			udao.close();
			daoCoord.close();
			daoSecre.close();
		}
	}

	/** Cadastra uma nova solicitação de matrícula.
	 * @param m
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public Object cadastrarNovas(Movimento m) throws ArqException, NegocioException {
		return criarSolicitacoes(m, SolicitacaoMatricula.CADASTRADA);
	}

	/** Submete uma nova solicitação de matrícula.
	 * @param m
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public Object submeterNovas(Movimento m) throws ArqException, NegocioException {
		//validação de usuário
		MovimentoSolicitacaoMatricula mov = (MovimentoSolicitacaoMatricula) m;
		Usuario usr = (Usuario) mov.getUsuarioLogado();

		int papeis[] = new int[] {SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.COORDENADOR_CURSO,
				SigaaPapeis.SECRETARIA_POS , SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_CENTRO,
				SigaaPapeis.SECRETARIA_COORDENACAO,	SigaaPapeis.SECRETARIA_GRADUACAO};

		if (!m.getUsuarioLogado().isUserInRole(papeis)
				&& mov.getDiscente().getId() != usr.getDiscenteAtivo().getId()) {
			throw new NegocioException("As solicitações só podem ser realizadas pelo próprio discente");
		}

		return criarSolicitacoes(m, SolicitacaoMatricula.SUBMETIDA);
	}

	/** Submete uma solicitação de matrícula que foi orientada pelo orientador acadêmico.
	 * @param m
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public Object submeterJaOrientadas(Movimento m) throws ArqException, NegocioException {
		MovimentoSolicitacaoMatricula mov = (MovimentoSolicitacaoMatricula) m;
		Usuario usr = (Usuario) mov.getUsuarioLogado();
		DiscenteAdapter discente = mov.getDiscente();
		DiscenteGraduacao dg = discente.isGraduacao() ? (DiscenteGraduacao) discente : null;

		int papeis[] = new int[] {SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.COORDENADOR_CURSO,
				SigaaPapeis.SECRETARIA_POS , SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_CENTRO,
				SigaaPapeis.SECRETARIA_COORDENACAO,	SigaaPapeis.SECRETARIA_GRADUACAO};

		if (!m.getUsuarioLogado().isUserInRole(papeis)
				&& (discente.isGraduacao() && dg.getTutorEad() != null && !dg.getTutorEad().equals(usr.getTutor()) )
				&& discente.getId() != usr.getDiscenteAtivo().getId()) {
			throw new NegocioException("As solicitações só podem ser realizadas pelo próprio discente");
		}

		return criarSolicitacoes(m, SolicitacaoMatricula.VISTA);
	}

	/** Persiste no banco as solicitações de matrícula.
	 * @param mov
	 * @param status
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private Object criarSolicitacoes(Movimento mov, Integer status) throws ArqException, NegocioException {

		MovimentoSolicitacaoMatricula movSol = (MovimentoSolicitacaoMatricula) mov;
		SolicitacaoMatriculaDao sdao = getDAO(SolicitacaoMatriculaDao.class, mov);
		MatriculaComponenteDao mdao = getDAO(MatriculaComponenteDao.class , mov);
		GenericDAO dao = getGenericDAO(movSol);

		Integer numSolicitacao = null;
		try {
			// nesse ponto o movimento só possui as turmas adicionadas (
			CalendarioAcademico cal = movSol.getCalendarioParaMatricula();
			
			// retorna as solicitações removidas e
			// remove do movimento as que estiverem no banco
			Collection<SolicitacaoMatricula> jaCadastradas = sdao.findByDiscenteAnoPeriodo(
					movSol.getDiscente(), cal.getAno(), cal.getPeriodo(), null,
					SolicitacaoMatricula.SUBMETIDA, SolicitacaoMatricula.ORIENTADO,SolicitacaoMatricula.VISTA,
					SolicitacaoMatricula.ATENDIDA, SolicitacaoMatricula.NEGADA);
			ArrayList<SolicitacaoMatricula> praRemover = new ArrayList<SolicitacaoMatricula>(0);
			ArrayList<SolicitacaoMatricula> novos = new ArrayList<SolicitacaoMatricula>(0);
			
			// Registrar as desistências das turmas já matriculadas do aluno.
			removerMatriculasComponentePorDesistencia(movSol.getMatriculas(), mov);
			
			// Evita que traga as solicitações que possuem matrículas já consolidadas!
			jaCadastradas = SolicitacaoMatriculaHelper.filtrarSomenteMatriculadas(jaCadastradas);
			
			// Cria lista das solicitações que serão removidas
			for (SolicitacaoMatricula sol : jaCadastradas) {
				numSolicitacao = sol.getNumeroSolicitacao();
				boolean ausentePorIndeferimento = sol.isInDeferida();
				if ( sol.getTurma() != null && !movSol.getTurmas().contains(sol.getTurma()) && !ausentePorIndeferimento) {
					sol.setRegistroAlteracao(mov.getUsuarioLogado().getRegistroEntrada());
					praRemover.add(sol);
				}
			}

			// Cria lista somente com as solicitações novas. Não pega todas porque as antigas já foram persistidas.
			for (Turma t : movSol.getTurmas()) {
				boolean novaSol = true;
				for (SolicitacaoMatricula sol : jaCadastradas) {
					if (sol.getTurma() != null && sol.getTurma().getId() == t.getId() && !sol.isInDeferida()) {
						novaSol = false;
						break;
					}
				}
				if (t != null && novaSol) {
					SolicitacaoMatricula novo = new SolicitacaoMatricula();
					novo.setTurma(t);
					novos.add(novo);
				}
			}

			if ((status == SolicitacaoMatricula.SUBMETIDA || status  == SolicitacaoMatricula.VISTA)&&  numSolicitacao == null) {
				numSolicitacao = sdao.getSequenciaSolicitacoes();
			}

			for (SolicitacaoMatricula solicitacao : novos) {
				solicitacao.setDiscente(movSol.getDiscente().getDiscente());
				solicitacao.setStatus(movSol.getDiscente().isMetropoleDigital() ? SolicitacaoMatricula.VISTA : status);
				solicitacao.setDataSolicitacao(new Date());
				solicitacao.setAno(cal.getAno());
				solicitacao.setPeriodo(cal.getPeriodo());
				solicitacao.setDataCadastro(new Date());
				solicitacao.setRematricula(movSol.isRematricula());
				solicitacao.setAnulado(false);
				solicitacao.setRegistroCadastro(mov.getUsuarioLogado().getRegistroEntrada());
				SolicitacaoMatricula desistente = sdao.findDesistente(solicitacao.getTurma().getId(),
						solicitacao.getDiscente().getId());
				
				if (desistente == null) {
					if (numSolicitacao != null)
						solicitacao.setNumeroSolicitacao(numSolicitacao);
					dao.createNoFlush(solicitacao);
				} else {
					int situacaoEnviada = movSol.getDiscente().isDiscenteEad() ? SolicitacaoMatricula.VISTA : SolicitacaoMatricula.SUBMETIDA;

					dao.updateField(SolicitacaoMatricula.class, desistente.getId(), "status", situacaoEnviada);
					MatriculaComponenteHelper.alterarSituacaoMatricula(desistente.getMatriculaGerada(), SituacaoMatricula.MATRICULADO, movSol, mdao);
				}
				dao.incrNumField(Turma.class, solicitacao.getTurma().getId(), "totalSolicitacoes");
			}

			if (!isEmpty(praRemover)) {
				for (SolicitacaoMatricula sol : praRemover) {
					if (sol.isProcessada()) {
						if (!movSol.getDiscente().isStricto()) {
							MatriculaComponenteHelper.alterarSituacaoMatricula(sol.getMatriculaGerada(), SituacaoMatricula.DESISTENCIA, movSol, mdao);
						} else {
							notificarOrientadorAcademico(movSol, true);
						}
					}
				}
				if (!movSol.getDiscente().isStricto()) {
					SolicitacaoMatriculaHelper.alterarStatusSolicitacao(praRemover, SolicitacaoMatricula.EXCLUIDA);
				} else {
					// separa as solicitações que foram atendidas das que ainda não foram
					ArrayList<SolicitacaoMatricula> excluir = new ArrayList<SolicitacaoMatricula>();
					ArrayList<SolicitacaoMatricula> solicitarExclusao = new ArrayList<SolicitacaoMatricula>();
					for (SolicitacaoMatricula sol : praRemover) {
						if (isEmpty(sol.getMatriculaGerada()))
							excluir.add(sol);
						else
							solicitarExclusao.add(sol);
					}
					SolicitacaoMatriculaHelper.alterarStatusSolicitacao(excluir, SolicitacaoMatricula.EXCLUIDA);
					SolicitacaoMatriculaHelper.alterarStatusSolicitacao(solicitarExclusao, SolicitacaoMatricula.EXCLUSAO_SOLICITADA);
				}
			}
			
			if (!isEmpty(novos)) {
				if ( !isEmpty(movSol.getHorariosTutoria() ) )
					cadastrarHorarioEAD(movSol, cal);

				// No caso de discentes de pós-graduação, o orientador deve ser notificado por e-mail
				if (movSol.getDiscente().isStricto()) {
					ProcessadorMatriculaStricto.notificarOrientador(movSol);
				} else if (movSol.getDiscente().isGraduacao()) {
					notificarOrientadorAcademico(movSol, false);
				} else if (movSol.getDiscente().isMetropoleDigital()){
					movSol.setSolicitacoes(novos);
					matricularDiscenteIMD(movSol);
				}
			}
			
			if (movSol.getDiscente().isGraduacao())
				removerSolicitacaoEnsinoIndividualizado(movSol);
			movSol.setNumSolicitacao(numSolicitacao);
		} finally {
			dao.close();
			sdao.close();
			mdao.close();
		}
		return movSol;
	}
	
	/**
	 * Notifica o orientador acadêmico da matrícula do discente.
	 *
	 * @param movimento
	 * @throws DAOException
	 */
	public static void notificarOrientadorAcademico(MovimentoSolicitacaoMatricula movimento, boolean removeMatricula) throws DAOException {
		// Carregar informações de orientação
		OrientacaoAcademicaDao orientacaoDao = getDAO(OrientacaoAcademicaDao.class, movimento);
		OrientacaoAcademica orientacao = null;
		if (movimento.getDiscente().isStricto() || movimento.getDiscente().isLato())
			orientacao = orientacaoDao.findOrientadorAtivoByDiscente(movimento.getDiscente().getId());
		else
			orientacao = orientacaoDao.findOrientadorAcademicoAtivoByDiscente(movimento.getDiscente().getId());
		UsuarioDao usuarioDao = getDAO(UsuarioDao.class, movimento);
		
		try {
			if (orientacao != null) {
				// Pegar email do orientador
				Usuario usuarioOrientador = null;
				if (orientacao.isExterno() ) {
					usuarioOrientador = usuarioDao.findByDocenteExterno(orientacao.getDocenteExterno().getId());
				} else {
					usuarioOrientador = usuarioDao.findByServidor(orientacao.getServidor());
				}
				String email = null;
				if (usuarioOrientador != null) {
					email = usuarioOrientador.getEmail(); //pega email da pessoa do discente
				} else {
					email = orientacao.getPessoa().getEmail();
				}
				// Enviar e-mail
				CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(movimento.getDiscente());
				if (email != null && cal.isPeriodoOrientacaoCoordenacao()) {
					
					MailBody mail = new MailBody();
					//destinatário: usuário que cadastrou a solicitação
					//nome: nome do destinatário
					mail.setEmail( email );
					mail.setNome( orientacao.getNomeOrientador() );
					
					String prazo = null;
					if (cal != null && cal.getInicioCoordenacaoAnaliseMatricula() != null && cal.getFimCoordenacaoAnaliseMatricula() != null)
						prazo = " O prazo para análise é de "+
									"<b>"+Formatador.getInstance().formatarData(cal.getInicioCoordenacaoAnaliseMatricula())+"</b>"
									+" até "+
									"<b>"+Formatador.getInstance().formatarData(cal.getFimCoordenacaoAnaliseMatricula())+"</b>.";
					//assunto e mensagem
					mail.setAssunto("SIGAA - Solicitação de Matrículas em Componentes Curriculares");
					mail.setMensagem("Caro(a) " + orientacao.getNomeOrientador() +", <br><br>" +
							"O discente " + movimento.getDiscente().getNome() +
							(removeMatricula?" removeu":" efetuou") + " uma solicitação de matrícula no SIGAA. <br/>" +
							" Como seu orientando acadêmico, analise as solicitações de matrículas do discente. <br/><br/>" +
							"Para realizar esta análise por favor acesse a opção 'Analisar Solicitações de Matrícula', " +
							"disponível no seu Portal do Docente, sob menu Ensino no sub menu Orientação Acadêmica.<br>"+
							(prazo != null ? prazo : "")+
							"<br/><br/>"						
					);
					
					//usuário que esta enviando e email para resposta.
					mail.setReplyTo( movimento.getUsuarioLogado().getEmail() ); //email do coordenador que esta aprovando/orientando o trancamento
					mail.setContentType(MailBody.HTML);
					Mail.send(mail);
				}
			}
		} finally {
			orientacaoDao.close();
			usuarioDao.close();
		}
	}

	/** Cadastra um horário de tutoria de Ensino a Distância.
	 * @param movMatricula
	 * @param cal
	 * @throws ArqException
	 */
	private void cadastrarHorarioEAD(MovimentoSolicitacaoMatricula movMatricula, CalendarioAcademico cal) throws ArqException {
		
		List<HorarioTutoria> horarios = movMatricula.getHorariosTutoria();
		
		
		/**
		 * Removendo  os HorarioTurma que existiam e o usuário desmarcou
		 */
		HorarioTutoriaDao dao = getDAO(HorarioTutoriaDao.class, movMatricula);
		List<Integer> dias = dao.findDiasSemanaTutoriaByDiscente(movMatricula.getDiscente().getId(), cal.getAno(), cal.getPeriodo());
		boolean remover = true;
		for( Integer dia : dias ){
			remover = true;
			
			for( HorarioTutoria h : horarios ){
				if(  dia.equals( h.getDiaSemana() ) )
					remover = false;
			}
			
			if( remover )
				dao.removerHorarioTutoriaByDiscenteDiaAnoPeriodo(movMatricula.getDiscente().getId(), dia, cal.getAno(), cal.getPeriodo());
		}
		
		try {
			for( HorarioTutoria h : horarios ){
				h.setAno(cal.getAno());
				h.setPeriodo(cal.getPeriodo());
				if (h.getId() == 0)
					dao.create(h);
				else
					dao.update(h);
			}
			
		} finally {
			dao.close();
		}
	}

	/**
	 * Submete todas as solicitações QUE JA ESTIVEREM CADASTRADAS.
	 * @param mov
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public Object submeterCadastradas(Movimento mov) throws ArqException, NegocioException {

		MovimentoSolicitacaoMatricula movSol = (MovimentoSolicitacaoMatricula) mov;
		SolicitacaoMatriculaDao dao = getDAO(SolicitacaoMatriculaDao.class, mov);
		try {
			int numero = dao.getSequenciaSolicitacoes();
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(movSol.getDiscente());
			Collection<SolicitacaoMatricula> jaCadastradas = dao.findByDiscenteAnoPeriodo(movSol.getDiscente(), cal.getAno(),
					cal.getPeriodo(), null, SolicitacaoMatricula.CADASTRADA);
			for (SolicitacaoMatricula sol : jaCadastradas) {
				sol.setStatus(SolicitacaoMatricula.SUBMETIDA);
				sol.setNumeroSolicitacao(numero);
				dao.update(sol);
				dao.incrNumField(Turma.class, sol.getTurma().getId(), "totalSolicitacoes");
			}


			return numero;
		} finally {
			dao.close();
		}
	}

	/** Valida os dados.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento m) throws NegocioException, ArqException {
		// TODO validar
		MovimentoSolicitacaoMatricula mov = (MovimentoSolicitacaoMatricula) m;
		validarPeriodo(mov);
		
	}

	/** Valida o período de solicitação de matrícula.
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void validarPeriodo(MovimentoSolicitacaoMatricula mov) throws NegocioException, ArqException {

		try {
			if (mov.getCodMovimento().equals(SigaaListaComando.ANALISAR_SOLICITACAO_MATRICULA)) {
				if (!mov.getDiscente().isStricto() && !mov.getCalendarioAcademicoAtual().isPeriodoAnaliseCoordenador()) {
					throw new NegocioException("Não é permitido realizar análise de solicitações de matrículas fora dos períodos " +
					"determinados no calendário do programa do aluno.");
				}
			}
		} catch (NegocioException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException("Ocorreu um erro na validação do período para Análise de solicitações de matrículas");
		}

	}

	/** Valida a solicitação de matrícula.
	 * @param mov
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void validarSolicitacao(MovimentoSolicitacaoMatricula mov) throws ArqException, NegocioException {
		ListaMensagens erros = new ListaMensagens();

		if (mov.getDiscente().isGraduacao() && mov.getDiscente().isRegular()) {
			// Validar limite de extra-curriculares
			validarLimiteCreditosExtra(mov, erros);

			// verifica se o aluno está matriculado todas as sub-unidades de algum bloco no qual tentou realizar matricula
			validarMatriculaTurmasBloco(mov.getTurmas(), erros);
		}

		checkValidation(erros);
	}

	/** Valida o limite de créditos extras para a solicitação de matrícula.
	 * @param mov
	 * @param erros
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws DAOException
	 */
	private void validarLimiteCreditosExtra(MovimentoSolicitacaoMatricula mov, ListaMensagens erros) throws ArqException, NegocioException, DAOException {

		DiscenteGraduacao discente = (DiscenteGraduacao) mov.getDiscente();
		CalendarioAcademico cal = mov.getCalendarioAcademicoAtual();
		TurmaDao dao = getDAO(TurmaDao.class, mov);
		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class, mov);
		DiscenteGraduacaoDao dgDao = getDAO(DiscenteGraduacaoDao.class, mov);
		
		try {
			// Preparar matrículas a partir das turmas selecionadas
			Collection<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();
			for (Turma turma : mov.getTurmas()) {
				
				MatriculaComponente matricula = new MatriculaComponente();
				matricula.setId( turma.getId() );
				matricula.setDiscente(discente.getDiscente());
				matricula.setTurma(turma);
				
				ComponenteCurricular componente = dao.refresh(turma.getDisciplina());
				if (!componente.isSubUnidade()) {
					matricula.setComponente(componente);
				} else {
					matricula.setComponente(componente.getBlocoSubUnidade());
				}
				
				if (!matriculas.contains(matricula)) {
					matriculas.add(matricula);
				}
			}
			
			// Calcular integralizações dos componentes solicitados e matriculados
			IntegralizacoesHelper.analisarTipoIntegralizacao(discente, matriculas, mov);
			
			// Validar total de créditos extra-curriculares
			HashMap<Integer, Integer> mapa = new HashMap<Integer, Integer>();
			List<Integer> idsTurmasMatriculadas = dao.findIdsTurmasMatriculadasByDiscenteAnoPeriodo(discente.getId(), cal.getAno(), cal.getPeriodo());
			int creditosExtra = DiscenteHelper.getTotalCreditosExtra(matriculas, mapa);
			
			if( !isEmpty(mapa) && !isEmpty( idsTurmasMatriculadas ) ){
				for(Integer idTurma : idsTurmasMatriculadas)
					mapa.remove(idTurma);
			}
			
			creditosExtra = 0;
			for( Integer i : mapa.values() ){
				creditosExtra += i;
			}
			
			
			if (creditosExtra > 0) {
				dgDao.calcularIntegralizacaoExtras(discente, SituacaoMatricula.getSituacoesPagasEMatriculadas());
				short totalExtraIntegralizados = discente.getCrExtraIntegralizados() != null ? discente.getCrExtraIntegralizados() : 0;
				
				int somaCreditosExtra = totalExtraIntegralizados;
				for( Integer i : mapa.values() ){
					somaCreditosExtra += i;
				}
				
				discente.setCrExtraIntegralizados( (short) somaCreditosExtra );
				MatriculaGraduacaoValidator.validarLimiteCreditosExtra(discente, erros,mov.getTurmas());
				
				discente.setCrExtraIntegralizados(totalExtraIntegralizados);
			}
		} finally {
			dao.close();
			matriculaDao.close();
			dgDao.close();
		}
	}

	/**
	 * Efetua a anulação das solicitações de matrícula passadas como parâmetro.
	 * 
	 * @param usuarioLogado
	 * @param motivo
	 * @param solicitacoes
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public void anularSolicitacoes(UsuarioGeral usuarioLogado, String motivo, List<SolicitacaoMatricula> solicitacoes) throws ArqException, NegocioException {
		MovimentoSolicitacaoMatricula movAnular = new MovimentoSolicitacaoMatricula();
		
		SolicitacaoMatriculaDao solicitacaoDao = getDAO(SolicitacaoMatriculaDao.class, movAnular);
		
		try {
			for (SolicitacaoMatricula solicitacao : solicitacoes) {
				SolicitacaoMatricula solicitacaoCompleta = solicitacaoDao.findByPrimaryKey(solicitacao.getId(), SolicitacaoMatricula.class);
				solicitacaoCompleta.setObservacaoAnulacao(motivo);
				solicitacaoCompleta.setAnulado(true);
				
				Collection<SolicitacaoMatricula> anular = new ArrayList<SolicitacaoMatricula>();
				anular.add(solicitacaoCompleta);
				
				movAnular.setSolicitacoes(anular);
				movAnular.setDiscente(solicitacaoCompleta.getDiscente());
				movAnular.setSistema(Sistema.SIGAA);
				movAnular.setUsuarioLogado(usuarioLogado);
				movAnular.setCodMovimento(SigaaListaComando.ANULAR_SOLICITACAO_MATRICULA_AUTOMATICO);
				
				anular(movAnular);
			}
		} finally {
			solicitacaoDao.close();
		}
	}
	
	/**
	 * Realiza o cancelamento das solicitações de ensino individualizado para o ano e período vigente, 
	 * quando o aluno fizer qualquer alteração no seu plano de matrícula, e houver uma solicitação de ensino individualizado, 
	 * estas solicitações serão removidas e o sistema informará o discente destas ações. 
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public void removerSolicitacaoEnsinoIndividualizado(Movimento mov) throws NegocioException, ArqException{
		MovimentoSolicitacaoMatricula movSol = (MovimentoSolicitacaoMatricula) mov;
		DiscenteAdapter discente = movSol.getDiscente();
		CalendarioAcademico cal = movSol.getCalendarioParaMatricula();
		
		SolicitacaoEnsinoIndividual sei = new SolicitacaoEnsinoIndividual();
		sei.setAno(cal.getAno());
		sei.setPeriodo(cal.getPeriodo());
		sei.setDiscente((DiscenteGraduacao) discente);
		
		MovimentoCadastro movCadastro = (MovimentoCadastro) mov;
		movCadastro.setObjMovimentado(sei);
		movCadastro.setCodMovimento(SigaaListaComando.CANCELAR_SOLICITACAO_ENSINO_INDIVIDUAL_DISCENTE);
		ProcessadorSolicitacaoEnsinoIndividual processador = new ProcessadorSolicitacaoEnsinoIndividual();
		try {
			movCadastro = (MovimentoCadastro) processador.execute(movCadastro);
			movSol.setMensagens(movCadastro.getMensagens());
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new ArqException("Erro ao remover as solicitações de ensino individualizado do discente, para o semestre atual!");
		}
	}
	
	/**
	 * Método utilizado para alterar a situação das matrículas em componentes, consideradas como desistência pelo discente ingressante.
	 * @param mastriculas
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void removerMatriculasComponentePorDesistencia(Collection<MatriculaComponente> matriculas, Movimento mov) throws NegocioException, ArqException{
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, mov);
		try {
			if (matriculas != null){
				for (MatriculaComponente matricula : matriculas) {
					MatriculaComponenteHelper.alterarSituacaoMatricula(matricula, SituacaoMatricula.DESISTENCIA, mov, dao);
				}
			}	
		} finally {
			dao.close();
		}
	}
}