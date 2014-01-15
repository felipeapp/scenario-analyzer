/*
 * Superintendência de Informática - UFRN
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * 01/02/2007
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.dao.ensino.SecretariaUnidadeDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoEnsinoIndividualDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoTurmaDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.SecretariaUnidade;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscentesSolicitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoEnsinoIndividual;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Processador responsável por cadastrar as solicitações de abertura de turma
 * e suas respectivas reservas
 *
 * @author Leonardo
 *
 */
public class ProcessadorSolicitacaoTurma extends AbstractProcessador {

	/** Persiste a solicitação de turma.
	 * @see br.ufrn.arq.ejb.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento sMov) throws NegocioException, ArqException, RemoteException {
		validate(sMov);
		SolicitacaoTurma mov = (SolicitacaoTurma) sMov;

		if(mov.getCodMovimento() == SigaaListaComando.SOLICITAR_ABERTURA_TURMA) {
			// criando turma!
			return cadastrarSolicitacao(mov);
		} else if(mov.getCodMovimento() == SigaaListaComando.ATUALIZAR_SOLICITACOES_TURMA){
			// atendendo solicitação de alteração de horário
			return alterarSolicitacao(mov);
		} else if(mov.getCodMovimento() == SigaaListaComando.SOLICITAR_ALTERACAO_HORARIO){
			// solicitando alteração de horário de solicitação de turma
			return solicitarAlteracaoHorario(mov);
		} else if( mov.getCodMovimento() == SigaaListaComando.REMOVER_SOLICITACAO_TURMA ){
			// removendo (desativando) solicitação de turma
			return removerSolicitacao(mov);
		} else if( mov.getCodMovimento() == SigaaListaComando.NEGAR_SOLICITACAO_TURMA ){
			// negando solicitação de turma
			return negarSolicitacao(mov);
		} else if( mov.getCodMovimento() == SigaaListaComando.SUGERIR_SOLICITACAO_TURMA ){
			// Realizando a sugestão de solicitação de turma
			return sugerirSolicitacaoTurma(mov);	
		} else
			return null;
	}

	/**
	 * Nega uma solicitação de turma.
	 * Esta operação pode ser invocada pelo chefe/secretário do departamento do componente da turma.
	 * @param mov
	 * @return
	 * @throws ArqException
	 * @throws SegurancaException
	 * @throws NegocioException
	 */
	private Object negarSolicitacao(SolicitacaoTurma mov) throws SegurancaException, ArqException, NegocioException {
		checkRole(SigaaPapeis.GESTOR_TURMAS_UNIDADE, mov);

		if( !mov.isPassivelNegacaoSolicitacao() ){
			NegocioException e = new NegocioException();
			e.addErro("Esta solicitação já foi processada e não pode ser negada.");
			throw e;
		}

		// Atualiza o status da solicitação para NEGADA
		GenericDAO dao = getGenericDAO(mov);
		try {
			Date agora = new Date();
			mov.setSituacao( SolicitacaoTurma.NEGADA );
			mov.setDataRemocao(agora);
			dao.update(mov);
	
			Collection<SolicitacaoEnsinoIndividual> solicitacoesAlunos = dao.findByExactField(SolicitacaoEnsinoIndividual.class, "solicitacaoTurma.id", mov.getId());
			// Verifica se há solicitações de discentes associadas. Caso sim, estas serão negadas.
			if (solicitacoesAlunos != null) {
				for (SolicitacaoEnsinoIndividual sol : solicitacoesAlunos) {
					sol.setSituacao(SolicitacaoEnsinoIndividual.NEGADA);
					dao.update(sol);
				}
			}
			RegistroEntrada registroEntrada = dao.findByPrimaryKey(mov.getRegistroEntrada().getId(), RegistroEntrada.class);
	
			//TODO COLOCAR MENSAGEM EM UTF-8 novaString = new String(stringVelha, "UTF-8")
			//TODO UFRNUtils ou StringUtils
			MailBody mail = new MailBody();
			mail.setContentType(MailBody.HTML);
			// destinatário: usuário que cadastrou a solicitação
			// nome: nome do destinatário
			mail.setEmail(  registroEntrada.getUsuario().getEmail() );
			mail.setNome( registroEntrada.getUsuario().getNome() );
			// assunto e mensagem
			mail.setAssunto("Negada Criação da Turma - " + mov.getComponenteCurricular().getDescricaoResumida());
			mail.setMensagem("Caro Coordenador, <br> um de seus pedidos de solicitação de turma foi negado. " +
					"<br>Acesse o sistema para verificar o motivo poelo qual sua solicitação de turma não pôde ser atendida.");
			// usuário que esta enviando e email para resposta.
			mail.setReplyTo( mov.getUsuarioLogado().getEmail() );
			Mail.send(mail);
		
		} finally {
			dao.close();
		}
		return mov;
	}

	/**
	 * Remove (inativa) uma solicitação de turma.
	 * @param mov
	 * @return
	 * @throws ArqException
	 * @throws SegurancaException
	 * @throws NegocioException
	 */
	private Object removerSolicitacao(SolicitacaoTurma mov) throws SegurancaException, ArqException, NegocioException {
		checkRole(new int[] {SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO}, mov);

		if( !mov.isPodeRemover() ){
			throw new NegocioException("Esta solicitação já foi processada e não pode ser removida.");
		}

		GenericDAO dao = getGenericDAO(mov);
		try {
			Date agora = new Date();
			mov.setSituacao( SolicitacaoTurma.REMOVIDA );
			mov.setDataRemocao(agora);
			dao.updateField(SolicitacaoTurma.class, mov.getId(), "dataRemocao", agora);
			dao.updateField(SolicitacaoTurma.class, mov.getId(), "situacao", SolicitacaoTurma.REMOVIDA);
	
			if( mov.isTurmaEnsinoIndividual() || mov.isTurmaFerias() )
				retornarSolicitacoesEnsinoIndividual(mov, dao);
		
		} finally {
			dao.close();
		}
		return mov;
	}

	/**
	 * Quando uma solicitação de turma de ensino individual é removida as Solicitações de ensino
	 * individual associadas à turma devem ser retornadas para ABERTA para que possam ser atendidas novamente.
	 * Este método retorna as solicitações de ensino individual associadas a solicitação de turma quando a mesma é removida.
	 * @return
	 * @throws DAOException
	 */
	private void retornarSolicitacoesEnsinoIndividual(SolicitacaoTurma mov, GenericDAO dao) throws DAOException{

		Collection<SolicitacaoEnsinoIndividual> solicitacoes = dao.findByExactField(SolicitacaoEnsinoIndividual.class, "solicitacaoTurma.id", mov.getId());
		for( SolicitacaoEnsinoIndividual sol : solicitacoes ){
			sol.setSolicitacaoTurma(null);
			sol.setDataAtendimento(null);
			sol.setSituacao( SolicitacaoEnsinoIndividual.SOLICITADA );
			sol.setRegistroEntradaAtendente( null );
			dao.updateNoFlush( sol );
		}

	}

	/**
	 * Método para alterar solicitação de turma, incluindo realizar o atendimento da solicitação da alteração de horário
	 * da solicitação de turma.
	 * realizado pela coordenação de curso.
	 * @param mov
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private Object alterarSolicitacao(SolicitacaoTurma mov) throws SegurancaException, ArqException, NegocioException {
		checkRole(new int[] {SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO}, mov);
		SolicitacaoTurmaDao dao = getDAO(SolicitacaoTurmaDao.class, mov);
		try {
			ajustarSolicitacoesEnsinoIndividual(mov);
			dao.update(mov);
		} finally {
			dao.close();
		}
		return mov;
	}

	/**
	 * solicita alteração do horário de uma solicitação de turma. realizado pelo chefe de departamento.
	 * @param mov
	 * @return
	 * @throws ArqException
	 * @throws SegurancaException
	 */
	private Object solicitarAlteracaoHorario(SolicitacaoTurma mov) throws SegurancaException, ArqException {
		checkRole(SigaaPapeis.GESTOR_TURMAS_UNIDADE, mov);
		GenericDAO dao = getGenericDAO(mov);
		try {
			mov.setSituacao(SolicitacaoTurma.SOLICITADA_ALTERACAO);
			dao.update(mov);
			RegistroEntrada registroEntrada = dao.findByPrimaryKey(mov.getRegistroEntrada().getId(), RegistroEntrada.class);
			MailBody mail = new MailBody();
			// destinatário: usuário que cadastrou a solicitação
			// nome: nome do destinatário
			mail.setEmail( registroEntrada.getUsuario().getEmail() );
			mail.setNome( registroEntrada.getUsuario().getNome() );
			// assunto e mensagem
			mail.setAssunto("Alteração de horário de solicitação de turma - " + mov.getComponenteCurricular().getDescricaoResumida());
			mail.setMensagem("Caro Coordenador, <br> um de seus pedidos de solicitação de turma foi retornado " +
					"para que seja alterado o horário da solicitação. <br>Verifique suas solicitações e realize a alteração do horário por favor. ");
			// usuário que esta enviando e email para resposta.
			mail.setReplyTo( mov.getUsuarioLogado().getEmail() );
			Mail.send(mail);
		
		} finally {
			dao.close();
		}
		return mov;
	}

	/**
	 * Cadastra uma solicitação de turma.
	 * realizado pela coordenação de curso
	 * @param mov
	 * @return
	 * @throws ArqException
	 * @throws SegurancaException
	 * @throws NegocioException
	 */
	private Object cadastrarSolicitacao(SolicitacaoTurma mov) throws SegurancaException, ArqException, NegocioException{
		checkRole(new int[] {SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO}, mov);
		GenericDAO dao = getGenericDAO(mov);
		UsuarioDao udao = getDAO(UsuarioDao.class, mov);
		ServidorDao servidorDao = getDAO(ServidorDao.class, mov);
		SecretariaUnidadeDao suDao = getDAO(SecretariaUnidadeDao.class, mov);
		try {
			mov.setSituacao( SolicitacaoTurma.ABERTA );
			if ( mov.isTurmaFerias() && !(mov.getVagas() > 0)) {
				mov.setVagas( (short) mov.getSolicitacoesEnsinoIndividualAtendidas().size() );
			}
			
			if( mov.isTurmaEnsinoIndividual() 
				|| (mov.getSolicitacoesEnsinoIndividualAtendidas() != null && mov.getVagas() < mov.getSolicitacoesEnsinoIndividualAtendidas().size()) ){
				mov.setVagas( (short) mov.getSolicitacoesEnsinoIndividualAtendidas().size() );
			}
			dao.create(mov);
	
			ajustarSolicitacoesEnsinoIndividual(mov);
			// notificando os chefes e secretários de departamento a solicitação de turma de ensino individualizado
			if( mov.isTurmaEnsinoIndividual() || mov.isTurmaFerias() ){
	
				Unidade departamento = mov.getComponenteCurricular().getUnidade();
	
	
				Collection<Servidor> chefes = servidorDao.findChefesByDepartamento(departamento.getId());
				Collection<SecretariaUnidade> secretarias = suDao.findByUnidade(departamento.getId(),null);
	
				// enviar email
				MailBody mail = new MailBody();
				mail.setContentType(MailBody.TEXT_PLAN);
				mail.setAssunto("Nova solicitação de ensino individualizado");
				mail.setMensagem("Caro(a) Usuário(a), " +
						"\n\n Há solicitações de turmas de ensino individualizado pendentes." +
						" Para visualizá-las vá em 'Gerenciar Solicitações de Turmas' no SIGAA." +
						"\n\n" +
						"Para maiores informações, por favor entre em contato com o DAE.");
	
				//usuário que esta enviando e email para resposta.
				mail.setReplyTo( mov.getUsuarioLogado().getEmail() );
	
				Collection<Usuario> usuariosNotificar = new ArrayList<Usuario>();
				for( Servidor s : chefes ){
					Usuario usr = udao.findByServidor(s);
					usuariosNotificar.add(usr);
				}
				for( SecretariaUnidade su : secretarias ){
					usuariosNotificar.add( dao.refresh( su.getUsuario() ) );
				}
	
				for( Usuario usr : usuariosNotificar ){
					if( !isEmpty(usr.getEmail()) ){
						mail.setEmail( usr.getEmail() );
						mail.setNome( usr.getPessoa().getNome() );
						Mail.send(mail);
					}
				}
			}
		
		} finally {
			dao.close();
			udao.close();
			servidorDao.close();
			suDao.close();
		}
		return mov;
	}
	
	/**
	 * Cadastra uma sugestão de solicitação de turma.
	 * realizado pela coordenação de curso
	 * @param mov
	 * @return
	 * @throws ArqException
	 * @throws SegurancaException
	 * @throws NegocioException
	 */
	private Object sugerirSolicitacaoTurma(SolicitacaoTurma mov) throws SegurancaException, ArqException, NegocioException{
		checkRole(new int[] {SigaaPapeis.CHEFE_DEPARTAMENTO}, mov);
		GenericDAO dao = getGenericDAO(mov);
		CoordenacaoCursoDao daoCoord = getDAO( CoordenacaoCursoDao.class, mov );
		
		try {
			mov.setSituacao( SolicitacaoTurma.SUGESTAO_DEPARTAMENTO );
			
			dao.create(mov);
	
			// notificando os coordenadores de Curso que foram criadas sugestões de solicitação de turma.
			CoordenacaoCurso coordenador = daoCoord.findUltimaByCurso( mov.getCurso() );
			
			// enviar email
			MailBody mail = new MailBody();
			mail.setContentType(MailBody.TEXT_PLAN);
			mail.setAssunto("Nova sugestão de solicitação de turma");
			mail.setMensagem("Caro(a) Usuário(a), " +
					"\n\n Foi criada uma sugestão de turma para o componente "+ mov.getComponenteCurricular().getDescricao() +
					", realizado pelo chefe do departamento." +
					" Para visualizá-la vá em 'Turma >> Visualizar Solicitações Enviadas' no Portal do Coord. de Graduação." +
					"\n\n");
	
			//usuário que esta enviando e email para resposta.
			mail.setReplyTo( mov.getUsuarioLogado().getEmail() );
			
			if (coordenador != null && coordenador.getEmailContato() != null ) {
				mail.setNome( coordenador.getServidor().getNome() );
				mail.setEmail(coordenador.getEmailContato());
				Mail.send(mail);
			}

		} finally {
			dao.close();
			daoCoord.close();
		}
		return mov;
	}

	/**
	 * Seta, para atendida, as solicitações de ensino individual atendidas na solicitação da turma.
	 * Caso seja alteração na solicitação, irá ajustar as solicitações de ensino individual, ou seja, remover quando tiver de remover e adicionar as novas.
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void ajustarSolicitacoesEnsinoIndividual(SolicitacaoTurma mov) throws DAOException, NegocioException{

		if(isEmpty(mov.getSolicitacoesEnsinoIndividualAtendidas()))
			return;

		SolicitacaoTurmaDao stDao = getDAO(SolicitacaoTurmaDao.class, mov);
		SolicitacaoEnsinoIndividualDao seiDao = getDAO(SolicitacaoEnsinoIndividualDao.class, mov);

		try {
			Collection<DiscentesSolicitacao> discentesSolicitacao = stDao.findDiscentesSolicitacaoBySoliciacao(mov.getId());
	
			if( !mov.getCodMovimento().equals( SigaaListaComando.SOLICITAR_ABERTURA_TURMA ) ) {
				// removendo os DiscentesSolicitacao que já está persistido no banco porém não está mais na solicitação de turma que foi alterada
				for (Iterator<DiscentesSolicitacao> it = discentesSolicitacao.iterator(); it.hasNext();) {
					DiscentesSolicitacao ds = it.next();
	
					if( !mov.getDiscentes().contains(ds) ){
						stDao.remove(ds);
						stDao.detach(ds.getSolicitacaoTurma());
						it.remove();
					}
	
					Collection<SolicitacaoEnsinoIndividual> solicitacoesRetornar = seiDao.findByDiscenteComponenteAnoPeriodoSituacao(ds.getDiscenteGraduacao().getId(), mov.getTipo(), null, (int) mov.getAno(), (int) mov.getPeriodo(), SolicitacaoEnsinoIndividual.ATENDIDA);
					
					if (isNotEmpty(solicitacoesRetornar)) {
						int cont = 0;
						for (Iterator<SolicitacaoEnsinoIndividual> iterator = solicitacoesRetornar.iterator(); iterator.hasNext();) {
							SolicitacaoEnsinoIndividual s = iterator.next();
							if (s.getSolicitacaoTurma().getId() != ds.getSolicitacaoTurma().getId())
								++cont;
						}					
						
						if( cont > 1 ){
							throw new NegocioException("Não é possível realizar esta operação pois este discente deve possuir apenas uma solicitação de ensino individual neste período e ele possui " + solicitacoesRetornar.size() );
						} else {
							
							SolicitacaoEnsinoIndividual solicitacaoRetornar = null;
							
							for (SolicitacaoEnsinoIndividual s : solicitacoesRetornar) {
								if (s.getSolicitacaoTurma().getId() == ds.getSolicitacaoTurma().getId()) {
									solicitacaoRetornar = s;
									break;
								}
							}
							
							solicitacaoRetornar.setSolicitacaoTurma(null);
							solicitacaoRetornar.setDataAtendimento(null);
							solicitacaoRetornar.setRegistroEntradaAtendente(null);
							solicitacaoRetornar.setSituacao( SolicitacaoEnsinoIndividual.SOLICITADA );
							seiDao.update( solicitacaoRetornar );
						}
					}
					
				}
			}
	
			if( !isEmpty( mov.getSolicitacoesEnsinoIndividualAtendidas() ) ){
				for( SolicitacaoEnsinoIndividual solicitacao : mov.getSolicitacoesEnsinoIndividualAtendidas() ){
					solicitacao.setDataAtendimento(new Date());
					solicitacao.setSituacao( SolicitacaoEnsinoIndividual.ATENDIDA );
					solicitacao.setRegistroEntradaAtendente( mov.getRegistroEntrada() );
					solicitacao.setSolicitacaoTurma( mov );
					stDao.update( solicitacao );
				}
			}
			
		} finally {
			stDao.close();
			seiDao.close();
		}

	}

	/** Valida a solicitação.
	 * @see br.ufrn.arq.ejb.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {

		SolicitacaoTurma solicitacao = (SolicitacaoTurma) mov;
		if(mov.getCodMovimento() ==  SigaaListaComando.SOLICITAR_ABERTURA_TURMA)
			validaSolicitacaoAbertura(solicitacao);
		else if(mov.getCodMovimento() == SigaaListaComando.ATUALIZAR_SOLICITACOES_TURMA)
			validaListaSolicitacoes(solicitacao);
		else if(mov.getCodMovimento() == SigaaListaComando.SOLICITAR_ALTERACAO_HORARIO)
			validaSolicitacaoAlteracaoHorario(solicitacao);
	}

	/**
	 * valida a solicitação de alteração de horário de uma solicitação
	 * a solicitação deve estar com a situação diferente de: ATENDIDA, NEGADA
	 * @param mov
	 * @throws NegocioException
	 */
	private void validaSolicitacaoAlteracaoHorario(SolicitacaoTurma mov) throws NegocioException {
		if( mov.getSituacao() == SolicitacaoTurma.ATENDIDA )
			throw new NegocioException("Esta solicitação já foi atendida e não pode ser solicitado alteração de horário.");
		if( mov.getSituacao() == SolicitacaoTurma.NEGADA )
			throw new NegocioException("Esta solicitação já foi finalizada e não pode ser solicitado alteração de horário.");
	}

	/**
	 * Método responsável pela validação da lista de solicitações de turma.
	 * @param mov
	 */
	private void validaListaSolicitacoes(SolicitacaoTurma mov) {
	}

	/**
	 * Método responsável pela validação da lista de solicitações de abertura de turma.
	 * @param mov
	 */
	private void validaSolicitacaoAbertura(SolicitacaoTurma mov) {
	}

}
