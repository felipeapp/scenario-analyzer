/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.DocenteExternoDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoSolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.SolicitacaoMatriculaHelper;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Processador respons�vel pelas opera��es
 * de solicita��es de matr�cula de p�s-gradua��o
 *
 * @author Ricardo Wendell
 *
 */
public class ProcessadorMatriculaStricto extends AbstractProcessador {

	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);

		MovimentoSolicitacaoMatricula movimento = (MovimentoSolicitacaoMatricula) mov;

		if (SigaaListaComando.SOLICITAR_MATRICULA_STRICTO.equals(mov.getCodMovimento())) {
			validarMatriculaAtividade(movimento);
			cadastrarSolicitacoes(movimento);
			notificarOrientador(movimento);
		} else if (SigaaListaComando.CANCELAR_SOLICITAO_MATRICULA_STRICTO.equals(mov.getCodMovimento())) {
			validarCancelamentoSolicitacao(movimento);
			cancelarSolicitacao(movimento);

		}

		return null;
	}

	/**
	 * Cancelar as solicita��o passadas atrav�s do movimento
	 *
	 * @param movimento
	 * @throws DAOException
	 */
	private void cancelarSolicitacao(MovimentoSolicitacaoMatricula movimento) throws DAOException {
		SolicitacaoMatriculaDao solicitacaoDao = getDAO(SolicitacaoMatriculaDao.class, movimento);
		try {
			// Cancelar as solicita��es selecionadas
			for (SolicitacaoMatricula solicitacao : movimento.getSolicitacoes() ) {
				solicitacao.setStatus(SolicitacaoMatricula.EXCLUIDA);
				solicitacao.setDataAlteracao(new Date());
				solicitacao.setRegistroAlteracao(movimento.getUsuarioLogado().getRegistroEntrada());
				solicitacaoDao.update(solicitacao);
			}
		} finally {
			if (solicitacaoDao != null)
				solicitacaoDao.close();
		}
	}

	/**
	 * Cadastrar as solicita��es de atividade passadas pelo movimento
	 *
	 * @param movimento
	 * @throws DAOException
	 */
	private void cadastrarSolicitacoes(MovimentoSolicitacaoMatricula movimento) throws DAOException {
		// Criar as solicita��es a partir das atividades selecionadas
		SolicitacaoMatriculaDao solicitacaoDao = getDAO(SolicitacaoMatriculaDao.class, movimento);
		try {
			CalendarioAcademico calendario = movimento.getCalendarioAcademicoAtual();
			
			//Recupera o n�mero da solicita��o 
			Integer numeroSolicitacao = getNumeroSolicitacao (movimento.getDiscente().getDiscente(), calendario, movimento);
	
			for (ComponenteCurricular atividade : movimento.getAtividades()) {
				SolicitacaoMatricula solicitacao = new SolicitacaoMatricula();
				solicitacao.setTurma(null);
				solicitacao.setDiscente(movimento.getDiscente().getDiscente());
				solicitacao.setAtividade(atividade);
				solicitacao.setAno(calendario.getAno());
				solicitacao.setPeriodo(calendario.getPeriodo());
				solicitacao.setRematricula(movimento.isRematricula());
				solicitacao.setStatus(SolicitacaoMatricula.SUBMETIDA);
				solicitacao.setDataSolicitacao(new Date());
				solicitacao.setDataCadastro(new Date());
				solicitacao.setRegistroCadastro(movimento.getUsuarioLogado().getRegistroEntrada());
				solicitacao.setAnulado(false);
				solicitacao.setNumeroSolicitacao (numeroSolicitacao);
	
				solicitacaoDao.create(solicitacao);
			}
		} finally {
			if (solicitacaoDao != null)
				solicitacaoDao.close();
		}
	}
	
	
	
	
	
	
	/** 
	 * Recupera o n�mero da solicita��o 
	 * 
	 * @param discente 
	 * @param mov 
	 * @return 
	 * @throws DAOException
	 *  
	 */ 
	
	private Integer getNumeroSolicitacao(Discente discente, CalendarioAcademico cal, MovimentoSolicitacaoMatricula mov) throws DAOException { 
		
		SolicitacaoMatriculaDao sdao = getDAO (SolicitacaoMatriculaDao.class, mov); 
		
		try{ 
			
			Integer numSolicitacao = null;	
			
			// retorna as solicita��es removidas e 
			// remove do movimento as que estiverem no banco 
			Collection<SolicitacaoMatricula> jaCadastradas = sdao.findByDiscenteAnoPeriodo( mov.getDiscente(), cal.getAno(), cal.getPeriodo(), null, SolicitacaoMatricula.SUBMETIDA, SolicitacaoMatricula.ORIENTADO,SolicitacaoMatricula.VISTA, SolicitacaoMatricula.ATENDIDA, SolicitacaoMatricula.NEGADA); 
			
			// Evita que traga as solicita��es que possuem matr�culas j� consolidadas! 
			jaCadastradas = SolicitacaoMatriculaHelper.filtrarSomenteMatriculadas(jaCadastradas); 
			
			if(!ValidatorUtil.isEmpty(jaCadastradas)) { 
				SolicitacaoMatricula sol = jaCadastradas.iterator().next(); 
				numSolicitacao = sol.getNumeroSolicitacao();			
			} 
			
			if(ValidatorUtil.isEmpty(numSolicitacao)) 
				numSolicitacao = sdao.getSequenciaSolicitacoes (); 
			
			return numSolicitacao; 
			
		} finally {
			sdao.close();		
		}	
	
	}
		

	


	
	
	
	

	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

	/**
	 * Validar a sele��o de componentes para a solicita��o de matr�cula
	 *
	 * @param movimento
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void validarMatriculaAtividade(MovimentoSolicitacaoMatricula movimento) throws DAOException, NegocioException {
		ListaMensagens mensagens = new ListaMensagens();
		DiscenteAdapter discente = movimento.getDiscente();

		ComponenteCurricularDao componenteDao = getDAO(ComponenteCurricularDao.class, movimento);
		try {

		//as linhas abaixo foram comentadas, pois existia warning de "never read"
//		DiscenteDao discenteDao = getDAO(DiscenteDao.class, movimento);
//		Collection<ComponenteCurricular> componentes = discenteDao.findComponentesCurricularesConcluidos(discente);
		
		for (ComponenteCurricular atividade : movimento.getAtividades() ){
			atividade = componenteDao.refresh(atividade);
			if ( atividade.isQualificacao() ) {
				validarMatriculaQualificacao(discente, atividade, mensagens);
			}
			if ( atividade.isTese() ){
				validarMatriculaDefesa(movimento, atividade, movimento.getCalendarioAcademicoAtual(), mensagens);
			}
			
			
			//MatriculaGraduacaoValidator.validarPreRequisitos(null, null, mensagens.getMensagens());
			
		}
		} finally {
			if (componenteDao != null)
				componenteDao.close();
		}

		checkValidation(mensagens);
	}


	/**
	 * Validar matr�cula em qualifica��es
	 *
	 * @param discente
	 * @param atividade
	 * @param mensagens
	 * @throws DAOException
	 */
	private void validarMatriculaQualificacao(DiscenteAdapter discente, ComponenteCurricular atividade, ListaMensagens mensagens) throws DAOException {
		MatriculaStrictoValidator.isPassivelSolicitacaoQualificacao(discente, mensagens);
		MatriculaStrictoValidator.isPassivelSolicitacaoQualificacao(discente, atividade, mensagens);
	}

	/**
	 * Validar matr�culas em defesas
	 *
	 * @param discente
	 * @param atividade
	 * @param calendarioAcademico 
	 * @param mensagens
	 * @throws DAOException
	 */
	private void validarMatriculaDefesa(MovimentoSolicitacaoMatricula movimento, ComponenteCurricular atividade, CalendarioAcademico calendarioAcademico, ListaMensagens mensagens) throws DAOException {
		DiscenteAdapter discente = movimento.getDiscente();

		MatriculaStrictoValidator.isPassivelSolicitacaoDefesa(discente, mensagens);
		MatriculaStrictoValidator.isPassivelSolicitacaoDefesa(discente, atividade, calendarioAcademico, mensagens);
	}

	/**
	 * Validar a sele��o de solicita��es para cancelamento
	 *
	 * @param movimento
	 * @throws NegocioException
	 */
	private void validarCancelamentoSolicitacao(MovimentoSolicitacaoMatricula movimento) throws NegocioException {
		ListaMensagens mensagens = new ListaMensagens();

		for (SolicitacaoMatricula solicitacao : movimento.getSolicitacoes()) {
			// Validar status da solicita��o para remo��o
			if (solicitacao.foiAnalisada()) {
				mensagens.addErro("N�o � poss�vel cancelar a solicita��o de matr�cula para " +
						solicitacao.getComponente().getCodigo() +
						" pois ela j� foi analisada pelo orientador ou coordena��o do programa.");
			}
		}

		checkValidation(mensagens);
	}

	/**
	 * Notificar o orientador da matr�cula em atividades
	 *
	 * @param mov
	 * @param movimento
	 * @throws DAOException
	 */
	public static void notificarOrientador( MovimentoSolicitacaoMatricula movimento ) throws DAOException {

		// Carregar informa��es de orienta��o
		OrientacaoAcademicaDao orientacaoDao = getDAO(OrientacaoAcademicaDao.class, movimento);
		UsuarioDao usuarioDao = getDAO(UsuarioDao.class, movimento);
		DocenteExternoDao dExternoDao = getDAO(DocenteExternoDao.class, movimento);
		try {
			OrientacaoAcademica orientacao =  orientacaoDao.findOrientadorAtivoByDiscente(movimento.getDiscente().getId());
			if (orientacao == null) {
				return;
			}

			// Pegar email do orientador
			Usuario usuarioOrientador = null;
			if (orientacao.isExterno() ) {
				usuarioOrientador = dExternoDao.findUsuarioByDocenteExterno(orientacao.getDocenteExterno());
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
			if (email != null ) {
				MailBody mail = new MailBody();
	
				//destinat�rio: usu�rio que cadastrou a solicita��o
				//nome: nome do destinat�rio
				mail.setEmail( email );
				mail.setNome( orientacao.getNomeOrientador() );
				
				CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(movimento.getDiscente());
				String prazo = null;
				if (cal != null && cal.isPeriodoMatriculaRegular() && cal.getInicioCoordenacaoAnaliseMatricula() != null && cal.getFimCoordenacaoAnaliseMatricula() != null) {
					prazo = " O prazo para an�lise � de "+
							"<b>"+Formatador.getInstance().formatarData(cal.getInicioCoordenacaoAnaliseMatricula())+"</b> at� "+
							"<b>"+Formatador.getInstance().formatarData(cal.getFimCoordenacaoAnaliseMatricula())+"</b>.";
				} else if (cal != null && cal.isPeriodoReMatricula() && cal.getInicioCoordenacaoAnaliseReMatricula() != null && cal.getFimCoordenacaoAnaliseReMatricula() != null) {
					prazo = " O prazo para an�lise � de "+
							"<b>"+Formatador.getInstance().formatarData(cal.getInicioCoordenacaoAnaliseReMatricula())+"</b> at� "+
							"<b>"+Formatador.getInstance().formatarData(cal.getFimCoordenacaoAnaliseReMatricula())+"</b>.";
				}
				//assunto e mensagem
				mail.setAssunto("SIGAA - Solicita��o de Matr�culas em Componentes Curriculares");
				mail.setMensagem("Caro(a) " + orientacao.getNomeOrientador() +", <br><br>" +
						"O discente " + movimento.getDiscente().getNome() +
						" efetuou uma solicita��o de matr�cula no SIGAA. <br/>" +
						" Como seu orientando, esta solicita��o dever� receber sua an�lise para" +
						" que as matr�culas sejam efetivadas. <br/><br/>"+
						"Para realizar esta an�lise por favor acesse a op��o 'Analisar Solicita��es de Matr�cula', " +
						"dispon�vel no seu Portal do Docente, sob menu Ensino sub menu Orienta��o Acad�mica.<br>"+
						(prazo != null ? prazo : "")+
						"<br/><br/>");
	
				//usu�rio que esta enviando e email para resposta.
				mail.setReplyTo( movimento.getUsuarioLogado().getEmail() ); //email do coordenador que esta aprovando/orientando o trancamento
				mail.setContentType(MailBody.HTML);
				Mail.send(mail);
	
			}
		} finally {
			if (orientacaoDao != null)
				orientacaoDao.close();
			if (usuarioDao != null)
				usuarioDao.close();
			if (dExternoDao != null)
				dExternoDao.close();
		}		
	}

}
