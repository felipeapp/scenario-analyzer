/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 30/05/2007
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.HibernateException;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.SolicitacaoTrancamentoMatriculaDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MotivoTrancamento;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SolicitacaoTrancamentoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoOperacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.ProcessadorAlteracaoStatusMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.TrancamentoMatriculaValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.TrancamentoMatriculaMov;
import br.ufrn.sigaa.parametros.dominio.ParametrosEAD;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Processador para realizar solicitação, análise, cancelamento e consolidação
 * de trancamento de matrícula.
 * 
 * @author Victor Hugo
 * @author David Pereira
 * 
 */
public class ProcessadorTrancamentoMatricula extends AbstractProcessador {

	public Object execute(Movimento tMov) throws NegocioException, ArqException,
			RemoteException {

		validate(tMov);
		TrancamentoMatriculaMov mov = (TrancamentoMatriculaMov) tMov;

		if( mov.getCodMovimento() == SigaaListaComando.SOLICITAR_TRANCAMENTO_MATRICULA ){
			return solicitarTrancamento(mov);
		}else if( mov.getCodMovimento() == SigaaListaComando.ANALISAR_SOLICITACAO_TRANCAMENTO ){
			analisarSolicitacaoTrancamento(mov);
		} else if( mov.getCodMovimento() == SigaaListaComando.CANCELAR_SOLICITACAO_TRANCAMENTO ){
			cancelarSolicitacaoTrancamento(mov);
		} else if( mov.getCodMovimento() == SigaaListaComando.CONSOLIDAR_SOLICITACOES_TRANCAMENTO ){
			consolidarSolicitacoesTrancamento(mov);
		}

		return null;
	}

	/**
	 * Este método tranca as matrículas que estão com solicitações pendentes.
	 * Ou seja, tranca as matrículas cuja solicitação de trancamento foi feita 7 (sete) dias atrás.
	 * ou então tranca todas as matrículas com solicitações pendentes caso seja o último dia do trancamento.
	 * @param mov
	 * @throws DAOException
	 */
	private void consolidarSolicitacoesTrancamento(TrancamentoMatriculaMov mov) throws DAOException {

		try {
			//Collection<SolicitacaoTrancamentoMatricula> solicitacoesParaTrancar = new ArrayList<SolicitacaoTrancamentoMatricula>(0);
			Collection<SolicitacaoTrancamentoMatricula> solicitacoesParaTrancar = findPendentesProcessamento( mov );

			

			if( solicitacoesParaTrancar != null && !solicitacoesParaTrancar.isEmpty() ){
				/** se tiver solicitações para serem trancadas monta o movimento de alterar status matrícula e chama o processador responsável */
				MovimentoOperacaoMatricula matMov = new MovimentoOperacaoMatricula();
				matMov.setAutomatico(true);
				matMov.setSistema( mov.getSistema() );
				matMov.setUsuarioLogado( new Usuario( Usuario.USUARIO_SISTEMA ) );
				matMov.setNovaSituacao( SituacaoMatricula.TRANCADO );
				matMov.setSolicitacoesTrancamento(solicitacoesParaTrancar);
				matMov.setCodMovimento( SigaaListaComando.ALTERAR_STATUS_MATRICULA );
				StringBuilder mensagem =  new StringBuilder("Consolidação de trancamentos pendentes realizado com sucesso. <br>");

				ProcessadorAlteracaoStatusMatricula processador = new ProcessadorAlteracaoStatusMatricula();
				processador.execute(matMov);


				// ENVIANDO EMAIL PRA NOTIFICAR DA REALIZACAO DA TAREFA
				String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
				//String email = "sigaa@info.ufrn.br";
				String assunto = "TRANCAMENTOS " + Formatador.getInstance().formatarData(new Date());

				mensagem.append( solicitacoesParaTrancar.size() + " matriculas em disciplinas foram trancadas.<br>");
				mensagem.append("Host: " + NetworkUtils.getLocalName() + "<br><br>");

				mensagem.append( "<br><b>Solicitações trancadas:</b> <br>");
				for( SolicitacaoTrancamentoMatricula stm : solicitacoesParaTrancar ){
					mensagem.append( stm.getId() + " " +
							stm.getMatriculaComponente().getDiscente().getPessoa().getNome() + " " +
							"("+stm.getMatriculaComponente().getDiscente().getNivel() + ") <br> ");
					mensagem.append( stm.getMatriculaComponente().getComponente().getDescricao() + "<br><br>");
				}

				MailBody mail = new MailBody();
				mail.setEmail( email );
				mail.setAssunto(assunto);
				mail.setMensagem( mensagem.toString() );
				Mail.send(mail);

			}

		} catch (Exception e) {
			/** se der algum erro nesta rotina manda email pra administração pra notificar do erro. */
			e.printStackTrace();
			
			Throwable cause = null;
			if (e.getCause() != null)
				cause = e.getCause();
			else
				cause = e;

			StringBuilder sbErro = new StringBuilder();
			
			sbErro.append("===DADOS DA EXCEÇÃO DISPARADA=== ");
			sbErro.append("<br>");
			sbErro.append("Exceção: ");
			sbErro.append(e.toString());
			sbErro.append("<br><br>");
			
			
			while (cause != null) {
				sbErro.append("Cause: ");
				sbErro.append(String.valueOf(cause) + "\n");
				sbErro.append("CAUSE STACK TRACE:");
				sbErro.append("\n");
				String trace = Arrays.toString(cause.getStackTrace());
				trace = trace.replace(",", "\n");
				sbErro.append(trace);
				sbErro.append("\n\n");

				cause = cause.getCause();
			}
			
			String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
			//String email = "sigaa@info.ufrn.br";
			String assunto = "Erro SIGAA - TRANCAMENTO TIMER: " + e.getMessage();
			String mensagem =  "Server: " + NetworkUtils.getLocalName() + "<br><br>" + sbErro.toString();
			
			//enviando email para administração do sistema para notificar do erro
			MailBody mail = new MailBody();
			mail.setEmail( email );
			mail.setAssunto(assunto);
			mail.setMensagem( mensagem );
			mail.setContentType(MailBody.TEXT_PLAN);
			
			Mail.send  (mail);

		}
	}

	/**
	 * Busca as solicitações de trancamento que ainda não foram processadas.
	 * @param dao
	 * @param object
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	private Collection<SolicitacaoTrancamentoMatricula> findPendentesProcessamento( TrancamentoMatriculaMov mov ) throws DAOException, NegocioException {
		
		// caso definido em parâmetro, limita a quantidade de solicitações a processar.
		int limite = ParametroHelper.getInstance().getParametroInt(ParametrosGerais.QUANTIDADE_SOLICITACOES_TRANCAMENTO_PROCESSAR);
		if (limite > 0){
			
			Collection<SolicitacaoTrancamentoMatricula> todasPendentes = new ArrayList<SolicitacaoTrancamentoMatricula>();
			todasPendentes.addAll( findPendentesProcessamentoGraduacaoPresencial(mov) );
			if( todasPendentes.size() < limite )
				todasPendentes.addAll( findPendentesProcessamentoGraduacaoEAD(mov) );
			if( todasPendentes.size() < limite )
				todasPendentes.addAll( findPendentesProcessamentoStricto(mov) );
	
			//se tem alguma solicitação para ser trancada
			if( todasPendentes.size() > 0 ){
				Collection<SolicitacaoTrancamentoMatricula> processar = new ArrayList<SolicitacaoTrancamentoMatricula>();
				Iterator<SolicitacaoTrancamentoMatricula> iterator = todasPendentes.iterator();
				while (limite > 0 && iterator.hasNext()) {
					processar.add(iterator.next());
					limite--;
				}
				return processar;
			}
			
		}
	
		return new ArrayList<SolicitacaoTrancamentoMatricula>();			
		
	}

	/**
	 * Busca as solicitações de trancamentos pêndentes de trancamento de stricto.
	 * Retorna somente as solicitações que foram vistas pelo coordenador/orientador e apenas as que estão com o prazo de 7 dias ultrapassado.
	 * tem q verificar o calendário de uma a uma comparando com o calendário do programa do aluno.
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private Collection<SolicitacaoTrancamentoMatricula> findPendentesProcessamentoStricto( TrancamentoMatriculaMov mov ) throws DAOException {
		
		SolicitacaoTrancamentoMatriculaDao dao = getDAO(SolicitacaoTrancamentoMatriculaDao.class, mov);
		Date hoje = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		
		//lista de solicitações de stricto que deverão ser trancadas, analisando o calendário e parametro de cada programa.
		Collection<SolicitacaoTrancamentoMatricula> pendentesStricto = new ArrayList<SolicitacaoTrancamentoMatricula>();
		
		Collection<SolicitacaoTrancamentoMatricula> solicitacoesVistasStricto;
		try{
			//todas as solicitações de stricto que foram vistas pelo coordenador/orientador
			solicitacoesVistasStricto = dao.findByData(null, NivelEnsino.STRICTO, ModalidadeEducacao.PRESENCIAL, SolicitacaoTrancamentoMatricula.VISTO);
		}finally{
			dao.close();
		}
		
		for( SolicitacaoTrancamentoMatricula stm : solicitacoesVistasStricto ){
			
			CalendarioAcademico calPrograma = CalendarioAcademicoHelper.getCalendario( stm.getMatriculaComponente().getDiscente() );
			ParametrosGestoraAcademica paramPrograma = ParametrosGestoraAcademicaHelper.getParametros(stm.getMatriculaComponente().getDiscente());
			
			Calendar prazoLimiteTrancamentoPrograma = Calendar.getInstance();
			if( ParametroHelper.getInstance().getParametroBoolean(ParametrosGraduacao.UTILIZAR_PRAZO_TRANCAMENTO_TURMA) 
					&& calPrograma.getFimTrancamentoTurma() != null ){
				prazoLimiteTrancamentoPrograma.setTime( calPrograma.getFimTrancamentoTurma() );
			}	
			prazoLimiteTrancamentoPrograma.add(Calendar.DAY_OF_MONTH, 1);
			prazoLimiteTrancamentoPrograma.setTime( DateUtils.truncate(prazoLimiteTrancamentoPrograma.getTime(), Calendar.DAY_OF_MONTH) );
			
			Calendar hojeMenosPrazoPrograma = Calendar.getInstance();
			hojeMenosPrazoPrograma.setTime(hoje);
			hojeMenosPrazoPrograma.add(Calendar.DAY_OF_MONTH, -paramPrograma.getTempoSolicitacaoTrancamento());
			
			/** se for um dia depois do prazo final de trancamento tranca TODAS as solicitações pendentes de stricto */
			if( prazoLimiteTrancamentoPrograma != null && hoje.compareTo( prazoLimiteTrancamentoPrograma.getTime() ) >= 0 ){
				
				if( hojeMenosPrazoPrograma.getTime().compareTo( stm.getDataCadastro() ) >= 0 ){
					pendentesStricto.add( stm );
				}
			} else { /** SENAO, tranca apenas as solicitações que já se passaram 7 dias */
				pendentesStricto.add( stm );
			}
			
		}
		return pendentesStricto;
		
	}
	
	
	/**
	 * Busca as solicitações de trancamento pendentes de trancamento da graduação a distância
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private Collection<SolicitacaoTrancamentoMatricula> findPendentesProcessamentoGraduacaoEAD( TrancamentoMatriculaMov mov ) throws DAOException{
		
		SolicitacaoTrancamentoMatriculaDao dao = getDAO(SolicitacaoTrancamentoMatriculaDao.class, mov);
		ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalGraduacao();
		
		Date hoje = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		Calendar hojeMenosPrazo = Calendar.getInstance();
		hojeMenosPrazo.setTime(hoje);
		hojeMenosPrazo.add(Calendar.DAY_OF_MONTH, -param.getTempoSolicitacaoTrancamento());
		
		Collection<SolicitacaoTrancamentoMatricula> pendentesGraduacaoEAD;
		try{
			pendentesGraduacaoEAD = dao.findByData(hojeMenosPrazo.getTime(), NivelEnsino.GRADUACAO, ModalidadeEducacao.A_DISTANCIA, SolicitacaoTrancamentoMatricula.SOLICITADO, SolicitacaoTrancamentoMatricula.VISTO);
		}finally{
			dao.close();
		}
		
		return pendentesGraduacaoEAD;
	}
	
	/**
	 * Busca as solicitações de trancamento pendentes de trancamento da graduação presencial
	 * Busca apenas as que já ultrapassaram o prazo definido no calendário de 7 dias.
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private Collection<SolicitacaoTrancamentoMatricula> findPendentesProcessamentoGraduacaoPresencial( TrancamentoMatriculaMov mov ) throws DAOException{
		
		SolicitacaoTrancamentoMatriculaDao dao = getDAO(SolicitacaoTrancamentoMatriculaDao.class, mov);
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalGraduacao();

		Calendar prazoLimiteTrancamento = Calendar.getInstance();
		if( ParametroHelper.getInstance().getParametroBoolean(ParametrosGraduacao.UTILIZAR_PRAZO_TRANCAMENTO_TURMA) 
				&& cal.getFimTrancamentoTurma() != null ){
			prazoLimiteTrancamento.setTime( cal.getFimTrancamentoTurma() );
		}	
		prazoLimiteTrancamento.add(Calendar.DAY_OF_MONTH, 1);
		prazoLimiteTrancamento.setTime( DateUtils.truncate(prazoLimiteTrancamento.getTime(), Calendar.DAY_OF_MONTH) );
		
		Date hoje = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		Calendar hojeMenosPrazo = Calendar.getInstance();
		hojeMenosPrazo.setTime(hoje);
		hojeMenosPrazo.add(Calendar.DAY_OF_MONTH, -param.getTempoSolicitacaoTrancamento());
		
		//solicitações pendentes
		Collection<SolicitacaoTrancamentoMatricula> pendentesGraduacaoPresencial;
		
		try{
			/** se for um dia depois do prazo final de trancamento tranca TODAS as solicitações pendentes de graduação presencial,
			 * tranca as que passaram 7 dias de graduação a distância e tranca as que foram vista de STRICTO */
			if( prazoLimiteTrancamento != null && hoje.compareTo( prazoLimiteTrancamento.getTime() ) >= 0 ){
				pendentesGraduacaoPresencial = dao.findByData(null, NivelEnsino.GRADUACAO, ModalidadeEducacao.PRESENCIAL, SolicitacaoTrancamentoMatricula.SOLICITADO, SolicitacaoTrancamentoMatricula.VISTO);
			} else { /** SENAO, tranca apenas as solicitações que já se passaram 7 dias */
				pendentesGraduacaoPresencial = dao.findByData(hojeMenosPrazo.getTime(), NivelEnsino.GRADUACAO, ModalidadeEducacao.PRESENCIAL, SolicitacaoTrancamentoMatricula.SOLICITADO, SolicitacaoTrancamentoMatricula.VISTO);
			}
		}finally{
			dao.close();
		}
		
		return pendentesGraduacaoPresencial;
	}
	
	/**
	 * Grava a análise das solicitações de trancamento realizadas pelo coordenador
	 * @param mov
	 * @throws ArqException
	 * @throws SegurancaException
	 * @throws NegocioException
	 * @throws RemoteException
	 */
	private void analisarSolicitacaoTrancamento(TrancamentoMatriculaMov mov) throws SegurancaException, ArqException, NegocioException, RemoteException {
		checkRole(new int[] {SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.COORDENADOR_GERAL_EAD, SigaaPapeis.DAE,
				SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.ORIENTADOR_ACADEMICO,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.ORIENTADOR_STRICTO}, mov);

		Discente discente = null;
		//Usuario usuarioDiscente = null;

		// Lista de solicitações que devem ser efetivamente trancadas. Usada apenas no ensino a distância.
		List<SolicitacaoTrancamentoMatricula> solicitacoesParaTrancar = new ArrayList<SolicitacaoTrancamentoMatricula>();

		GenericDAO dao = getGenericDAO(mov);
		for ( SolicitacaoTrancamentoMatricula solicitacao : mov.getSolicitacoes() ) {

			// Popular discente da solicitação
			if ( discente == null ) {
				discente = solicitacao.getMatriculaComponente().getDiscente().getDiscente();
			}

			// Validar status da solicitação
			if ( solicitacao.getSituacao() != SolicitacaoTrancamentoMatricula.SOLICITADO ) {
				throw new NegocioException("Não é possível de atender a uma solicitação de trancamento de matrícula duas vezes.");
			}

			// No caso de ensino a distância a solicitação deve ir para TRANCADO ou RECUSADO.
			// Quando for para TRANCADO, a matrícula deve ser trancada.
			if (mov.isADistancia() && solicitacao.isParecerFavoravelSolicitacao()) {
				solicitacao.setSituacao(SolicitacaoTrancamentoMatricula.TRANCADO);
				solicitacoesParaTrancar.add(solicitacao);
			} else if ( (mov.isADistancia() || discente.isStricto()) && !solicitacao.isParecerFavoravelSolicitacao()) {
				solicitacao.setSituacao(SolicitacaoTrancamentoMatricula.RECUSADO);
			} else {
				solicitacao.setSituacao( SolicitacaoTrancamentoMatricula.VISTO );
			}

			solicitacao.setDataAtendimento( new Date() );
			solicitacao.setRegistroAtendendor( mov.getUsuarioLogado().getRegistroEntrada() );

			dao.update(solicitacao);
		}

		// Se for o caso de atendimento no ensino a distância, que é feito pelo coordenador, o trancamento deve ser efetuado nesse mesmo passo.
		if (mov.isADistancia() && !solicitacoesParaTrancar.isEmpty()) {
			MovimentoOperacaoMatricula matMov = new MovimentoOperacaoMatricula();
			matMov.setAutomatico(true);
			matMov.setADistancia(true);
			matMov.setSistema( mov.getSistema() );
			matMov.setUsuarioLogado( new Usuario( Usuario.USUARIO_SISTEMA ) );
			matMov.setNovaSituacao( SituacaoMatricula.TRANCADO );
			matMov.setSolicitacoesTrancamento(solicitacoesParaTrancar);
			matMov.setCodMovimento( SigaaListaComando.ALTERAR_STATUS_MATRICULA );

			ProcessadorAlteracaoStatusMatricula processador = new ProcessadorAlteracaoStatusMatricula();
			processador.execute(matMov);
		}

		if (!mov.isADistancia() && discente != null) {
			//enviando email para aluno para notificar que o coordenador
			//viu ou orientou a solicitação de trancamento
			MailBody mail = new MailBody();

			String emailDiscente = discente.getPessoa().getEmail(); //pega email da pessoa do discente
			//if( emailDiscente == null && usuarioDiscente != null ) 
			// se não tiver email pega do usuário do discente
			//	emailDiscente = usuarioDiscente.getEmail();
			if (emailDiscente != null ) {

				//destinatário: usuário que cadastrou a solicitação
				//nome: nome do destinatário
				mail.setEmail( emailDiscente );
				mail.setNome( discente.getNome() );

				String vistoPor = " pela coordenação do curso";
				if (discente.isStricto()) {
					vistoPor = " por seu orientador";
				}

				//assunto e mensagem
				mail.setAssunto("Orientação de Trancamento em Disciplina");
				mail.setMensagem("Caro " + discente.getPessoa().getNome() + ", <br><br>" +
						"Sua solicitação de trancamento foi vista " + vistoPor + ". <br>" +
						"Você pode acompanhar o andamento ou desistir do trancamento em 'Exibir Andamento do Trancamento' no menu" +
						" Ensino -> Trancamento de Matrícula, localizado no Portal do Discente.<br><br> ");

				//usuário que está enviando e email para resposta.
				mail.setReplyTo( mov.getUsuarioLogado().getEmail() ); //email do coordenador que está aprovando/orientando o trancamento
				Mail.send(mail);

			}
		}

	}

	/**
	 * Persiste as solicitações do trancamento de matrícula
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private Integer solicitarTrancamento(TrancamentoMatriculaMov mov) throws DAOException {
		GenericSigaaDAO dao = getDAO(GenericSigaaDAO.class, mov);
		int numero = dao.getNextSeq( "ensino.trancamento_matricula_seq" );
		Discente discente = null;
		for( SolicitacaoTrancamentoMatricula solicitacao : mov.getSolicitacoes() ){
			solicitacao.setSituacao( SolicitacaoTrancamentoMatricula.SOLICITADO );
			solicitacao.setDataCadastro( new Date() );
			solicitacao.setRegistroEntrada( mov.getUsuarioLogado().getRegistroEntrada() );
			solicitacao.setNumeroSolicitacao( numero );
			dao.create(solicitacao);

			if( discente == null )
				discente = solicitacao.getMatriculaComponente().getDiscente().getDiscente();
		}

		/** NOTIFICANDO ORIENTADOR ACADEMICO DO TRANCAMENTO */
		OrientacaoAcademicaDao orientacaoDao = getDAO(OrientacaoAcademicaDao.class, mov);
		UsuarioDao usuarioDao = getDAO(UsuarioDao.class, mov);
		try {
				OrientacaoAcademica orientacao = null;
			if (discente != null) {
				if (discente.isStricto())
				orientacao = orientacaoDao.findOrientadorAtivoByDiscente( discente.getId() );
				else
					orientacao = orientacaoDao.findOrientadorAcademicoAtivoByDiscente( discente.getId() );
			}
	
			String emailOrientador = null;
			if( orientacao != null ){
				Usuario usuario = null;
				if (orientacao.getServidor() != null) {
					usuario = usuarioDao.findByServidor(orientacao.getServidor());
				} else {
					usuario = usuarioDao.findByDocenteExterno(orientacao.getDocenteExterno().getId());
				}
	
				if( usuario != null )
					emailOrientador = usuario.getEmail();
			}
	
			if( emailOrientador != null && emailOrientador.trim().length() > 0 ){
				String saudacao = "Caro Orientador Acadêmico, ";
				if (discente != null && discente.isStricto()) {
					saudacao = "Caro Orientador de Pós-Graduação, ";
				}
				notificarSolicitacaoTrancamento(emailOrientador, saudacao, discente, mov);
			}
		} finally {
			orientacaoDao.close();
			usuarioDao.close();
		}

		return numero;
	}

	/**
	 * Cancela uma solicitação de trancamento realizada pelo aluno
	 * @param mov
	 * @throws DAOException
	 */
	private void cancelarSolicitacaoTrancamento(TrancamentoMatriculaMov mov) throws DAOException {

		SolicitacaoTrancamentoMatricula solicitacao = mov.getSolicitacao();
		GenericDAO dao = getGenericDAO(mov);
		dao.initialize(solicitacao);

		solicitacao.setDataCancelamento( new Date() );
		solicitacao.setSituacao( SolicitacaoTrancamentoMatricula.CANCELADO );

		dao.update(solicitacao);

		for( SolicitacaoTrancamentoMatricula stm : mov.getSolicitacoes() ){
			dao.initialize(stm);
			stm.setDataCancelamento( new Date() );
			stm.setSituacao( SolicitacaoTrancamentoMatricula.CANCELADO );
			dao.update(stm);
		}

	}


	public void validate(Movimento tMov) throws NegocioException, ArqException {
		TrancamentoMatriculaMov mov = (TrancamentoMatriculaMov) tMov;
		if( mov.getCodMovimento() == SigaaListaComando.SOLICITAR_TRANCAMENTO_MATRICULA ){
			validarSolicitacaoTrancamento(mov);
		}else if( mov.getCodMovimento() == SigaaListaComando.ANALISAR_SOLICITACAO_TRANCAMENTO ){
			//analisarSolicitacaoTrancamento(mov);
			
		} else if( mov.getCodMovimento() == SigaaListaComando.CANCELAR_SOLICITACAO_TRANCAMENTO ){
			validarCancelarSolicitacaoTrancamento(mov);
		} else if( mov.getCodMovimento() == SigaaListaComando.CONSOLIDAR_SOLICITACOES_TRANCAMENTO ){
			
		}
	}

	/**
	 * Valida o cancelamento da solicitação de trancamento realizado pelo aluno
	 * @param mov
	 * @throws NegocioException
	 * @throws DAOException
	 * @throws HibernateException
	 */
	private void validarCancelarSolicitacaoTrancamento(TrancamentoMatriculaMov mov) throws NegocioException, HibernateException, DAOException {
		ListaMensagens erros = new ListaMensagens();
		if( ((Usuario)mov.getUsuarioLogado()).getDiscenteAtivo() == null || ((Usuario)mov.getUsuarioLogado()).getDiscenteAtivo().getId() != mov.getSolicitacao().getMatriculaComponente().getDiscente().getId() ){
			erros.addErro("Esta operação só pode ser realizada pelo prórpio discente que fez a solicitação de trancamento.");
		}

		List<SolicitacaoTrancamentoMatricula> corequisitos = TrancamentoMatriculaValidator.validaCancelamentoSolicitacao(
				mov.getSolicitacao(), mov.getCalendarioAcademicoAtual(),  erros);
		if( corequisitos != null && !corequisitos.isEmpty()  ){

			if( mov.getSolicitacoes() == null || mov.getSolicitacoes().isEmpty() || !mov.getSolicitacoes().containsAll(corequisitos) )
				erros.addErro("Ao cancelar uma solicitação de trancamento para uma disciplinas que possuem co-requisitos que também estão solicitados os trancamentos é necessario cancelar as solicitaçoes dos co-requisitos também.");
		}
		checkValidation(erros);
	}

	/**
	 * Valida solicitações de trancamento
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void validarSolicitacaoTrancamento(TrancamentoMatriculaMov mov) throws DAOException, NegocioException {

		ListaMensagens listaErros = new ListaMensagens();
		
		if( mov.getSolicitacoes() == null ){
			listaErros.addErro("Não há nenhuma solicitação de trancamento.");
			//ValidatorUtil.addMensagemErro(, listaErros.getMensagens());
		}

		Collection<MatriculaComponente> matriculasParaTrancar = new ArrayList<MatriculaComponente>();
		Discente discente = null;

		for( SolicitacaoTrancamentoMatricula solicitacao : mov.getSolicitacoes() ){
			matriculasParaTrancar.add( solicitacao.getMatriculaComponente() );
			if( discente == null )
				discente = solicitacao.getMatriculaComponente().getDiscente().getDiscente();
		}

		if( discente != null) {
			if ( !discente.isGraduacao() && !discente.isStricto()  )
				listaErros.addErro("Discentes do nível " + discente.getNivelDesc() +" não estão autorizados a efetuar trancamentos on-line.");
	
			if( !mov.isADistancia() && discente.getCurso() != null && discente.getCurso().isADistancia() && !ParametroHelper.getInstance().getParametroBoolean(ParametrosEAD.PERMITE_TRANCAR_COMPONENTE) )
				listaErros.addErro("Procure seu tutor para realizar o trancamento das disciplinas que deseja.");
		}
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente);

		/**
		 * verificando se já foi solicitado trancamento ou trancada alguma das matrículas selecionadas.
		 */
		TrancamentoMatriculaValidator.validarSolicitacao(discente, matriculasParaTrancar, cal.getAno(), cal.getPeriodo(), listaErros);


		/**
		 * verificando se as matrículas selecionadas possuem co-requisitos para que estes sejam trancados também
		 */
		List<MatriculaComponente> listaCorequisitos = TrancamentoMatriculaValidator.verificarCorequisitos(discente, matriculasParaTrancar);
		if( listaCorequisitos != null && !listaCorequisitos.isEmpty() ){
			StringBuffer msg = new StringBuffer("Não é possível efetuar o trancamento. Você está matriculado(a) na(s) seguinte(s) disciplinas co-requisitos da(s) disciplina(s) selecionada(s) para ser(em) trancada(s): <br>" );
			for( MatriculaComponente mc : listaCorequisitos ){
					msg.append( mc.getComponente().getDescricaoResumida() + ";<br>" );
			}
			listaErros.addErro(msg.toString());
			//ValidatorUtil.addMensagemErro(msg.toString(), listaErros.getMensagens());
		}

		Collection<MatriculaComponente> listasubunidades = TrancamentoMatriculaValidator.verificarSubunidades(matriculasParaTrancar);
		if( !isEmpty(listasubunidades) ){
			StringBuffer msg = new StringBuffer("Não é possível efetuar o trancamento. Você está matriculado(a) no(s) seguinte(s) " +
					"subunidades do(s) componente(s) de bloco selecionado(s) para ser(em) trancado(s): <br>" );
			for( MatriculaComponente mc : listasubunidades ){
				msg.append( mc.getComponente().getDescricaoResumida() + ";<br>" );
			}
			listaErros.addErro(msg.toString());
			//ValidatorUtil.addMensagemErro(msg.toString(), listaErros.getMensagens());
		}

		// Validação
		ListaMensagens erros = new ListaMensagens();
		for (MatriculaComponente mat : matriculasParaTrancar ) {
			TrancamentoMatriculaValidator.validar(mat, erros);
		}

		listaErros.addAll(erros);
		checkValidation(listaErros);

	}

	/**
	 * Envia email para notificar os interessados da solicitação de trancamento
	 * @param emal
	 * @param discente
	 * @param mov
	 * @throws DAOException
	 */
	private void notificarSolicitacaoTrancamento( String email, String saudacao, Discente discente, TrancamentoMatriculaMov mov ) throws DAOException{

		SolicitacaoTrancamentoMatricula sol = mov.getSolicitacoes().iterator().next();
		Turma turma = getGenericDAO(mov).findByPrimaryKey(sol.getMatriculaComponente().getTurma().getId(), Turma.class);
		ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametros(turma);
		Date dataTrancamento = sol.getDataCadastro();
		Date prazoAnalise = null;
		
		if (turma.isGraduacao())
			prazoAnalise = CalendarUtils.adicionaDias(dataTrancamento, parametros.getTempoSolicitacaoTrancamento());
		
		GenericSigaaDAO dao = getDAO(GenericSigaaDAO.class, mov);
		String assunto = "Trancamento de Matrícula - " + discente.getPessoa().getNome();
		StringBuffer mensagem = new StringBuffer(1000);

			mensagem.append(saudacao +
				"<br><br> O aluno " + discente.getPessoa().getNome() + " trancou as seguintes disciplinas: <br><br> ");

		for ( SolicitacaoTrancamentoMatricula solicitacao : mov.getSolicitacoes() ) {

			mensagem.append(solicitacao.getMatriculaComponente().getComponente() + "<br>Turma: " + solicitacao.getMatriculaComponente().getTurma().getCodigo());
			mensagem.append("<br> Motivo:");

			MotivoTrancamento mot = dao.findByPrimaryKey(solicitacao.getMotivo().getId(), MotivoTrancamento.class);
			mensagem.append(mot.getDescricao());
			if ( mot.getId() == MotivoTrancamento.OUTROS ) {
				mensagem.append("<br>Justificativa:<br><br>: " + solicitacao.getJustificativa());
			}
			mensagem.append("<br><br>");

		}
		
		mensagem.append("Entre no SIGAA (" + RepositorioDadosInstitucionais.getLinkSigaa() + ") para orientar este trancamento.<br>");
		mensagem.append("Data do trancamento:  " + Formatador.getInstance().formatarData(CalendarUtils.descartarHoras(dataTrancamento)) + "<br>");
		
		
		if (prazoAnalise != null)
			mensagem.append("Prazo máximo para análise: " + Formatador.getInstance().formatarData(CalendarUtils.descartarHoras(prazoAnalise)) + "<br><br>");

		mensagem.append("<br><br>Mensagem Gerada Automaticamente pelo SIGAA - Favor não Responder");

		MailBody mail = new MailBody();
		mail.setContentType(MailBody.HTML);
		mail.setEmail( email );
		mail.setAssunto(assunto);
		mail.setMensagem( mensagem.toString() );
		Mail.send(mail);

	}
}
