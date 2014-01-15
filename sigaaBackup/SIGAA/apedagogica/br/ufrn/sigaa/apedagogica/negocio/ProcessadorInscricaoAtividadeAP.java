package br.ufrn.sigaa.apedagogica.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.apedagogica.dao.AtividadeAtualizacaoPedagogicaDAO;
import br.ufrn.sigaa.apedagogica.dao.ConsultaParticipanteAtividadeDAO;
import br.ufrn.sigaa.apedagogica.dao.ParticipanteAtividadeAPDAO;
import br.ufrn.sigaa.apedagogica.dominio.AtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.GrupoAtividadesAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.MensagensAP;
import br.ufrn.sigaa.apedagogica.dominio.NotificacaoParticipanteAtividade;
import br.ufrn.sigaa.apedagogica.dominio.ParticipanteAtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.StatusParticipantesAtualizacaoPedagogica;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.nee.dominio.RecursoNee;

/**
 * Classe que processa todas informa��es referentes a inscri��o na particpa��o das atividades de atualiza��o pedag�gica.
 * @author M�rio Rizzi
 *
 */
public class ProcessadorInscricaoAtividadeAP extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		validate(mov);
		
		if( mov.getCodMovimento().equals(SigaaListaComando.INSCREVER_ATIVIDADE_ATUALIZACAO_PEDAGOGICA) ){
			inscrever(mov);
		}else if( mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_SITUACAO_PARTICIPANTE_ATUALIZACAO_PEDAGOGICA) ){
			alterarSituacaoParticipante(mov);
		}else if( mov.getCodMovimento().equals(SigaaListaComando.REMOVER_ATIVIDADE_ATUALIZACAO_PEDAGOGICA) ){
			removerParticipante(mov);
		}
		
		return mov;
	}

	/**
	 * Altera a situa��o dos particpantes em lote. 
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException 
	 */
	private void alterarSituacaoParticipante(Movimento mov)throws 
		NegocioException, ArqException, RemoteException {
	
		MovimentoInscricaoAtividadeAP movInscricao = (MovimentoInscricaoAtividadeAP) mov;
		Collection<ParticipanteAtividadeAtualizacaoPedagogica> participantesSituacaoAlterada = 
			movInscricao.getParticipantesSituacaoAlterada();
		
		try {
			
			Connection con =  Database.getInstance().getSigaaConnection();
			Statement st = con.createStatement();
			
			for (ParticipanteAtividadeAtualizacaoPedagogica p : participantesSituacaoAlterada) {
				StringBuilder sqlUpdate = new StringBuilder();
				sqlUpdate.append( " UPDATE apedagogica.participante_atividade_atualizacao_pedagogica SET situacao = " );
				sqlUpdate.append( p.getSituacao() );
				sqlUpdate.append( " WHERE id_participante_atividade_atualizacao_pedagogica = " );
				sqlUpdate.append( p.getId() );
				st.addBatch( sqlUpdate.toString() );
				
			}
			
			st.executeBatch();
			notificarParticipantesConcluidos(mov, participantesSituacaoAlterada);
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new NegocioException("Erro ao atualizar a situa��o dos particpantes");
		}
		
	}
	
	/**
	 * Notifica os participantes que conclu�ram a atividade. 
	 * @param participantesSituacaoAlterada
	 * @throws RemoteException 
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	private void notificarParticipantesConcluidos
		(Movimento mov, Collection<ParticipanteAtividadeAtualizacaoPedagogica> participantesSituacaoAlterada)
		throws NegocioException, ArqException, RemoteException{
		
		if( !isEmpty(participantesSituacaoAlterada) ){
			
			AtividadeAtualizacaoPedagogica  atividade = participantesSituacaoAlterada.iterator().next().getAtividade();
			
			NotificacaoParticipanteAtividade notificacao = new NotificacaoParticipanteAtividade();
			notificacao.setTitulo("Certificado de Participa��o no Programa de Atualiza��o Pedag�gica");
			notificacao.setAtividade(atividade);
			notificacao.setMensagem( "O certificado de participa��o no(a) " + atividade.getNome() + " no " +
									atividade.getDescricaoPeriodo() + " do Programa de Atualiza��o Pedag�gica " +
									" foi disponibilizado no caminho SIGAA > Portal do Docente > PAP > " +
									" Consultar Situa��o da Inscri��o." );	
			
			//Popula os dados do movimento para envio da notifica��o aos participantes.
			MovimentoNotificarParticipanteAP movNotificacao = new MovimentoNotificarParticipanteAP();
			movNotificacao.setCodMovimento( SigaaListaComando.NOTIFICAR_PARTICIPANTES );
			movNotificacao.setNotificacao( notificacao );
			movNotificacao.setSistema(mov.getSistema());
			movNotificacao.setSubsistema(mov.getSubsistema());
			movNotificacao.setUsuarioLogado(mov.getUsuarioLogado());
			
			movNotificacao.setParticipantes( new ArrayList<ParticipanteAtividadeAtualizacaoPedagogica>() );
			
			ParticipanteAtividadeAPDAO dao = getDAO(ParticipanteAtividadeAPDAO.class, movNotificacao);
			participantesSituacaoAlterada = dao.findByParticipantes(participantesSituacaoAlterada);
			for (ParticipanteAtividadeAtualizacaoPedagogica p : participantesSituacaoAlterada) {
				if( p.isConcluido() ){
					p.setSelecionado(true);
					movNotificacao.getParticipantes().add(p);
				}
			}
	
			ProcessadorNotificarParticipanteAP processadorNotificacao = new ProcessadorNotificarParticipanteAP();
			processadorNotificacao.execute( movNotificacao );
			
		}
		
	}
	
	/**
	 * Persisti as participa��es do docente em lote.
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void inscrever(Movimento mov)throws NegocioException, ArqException {
		
		MovimentoInscricaoAtividadeAP movInscricao = (MovimentoInscricaoAtividadeAP) mov;
		ParticipanteAtividadeAtualizacaoPedagogica participante = movInscricao.getParticipante();
		Collection<AtividadeAtualizacaoPedagogica> atividadesSelecionadas= movInscricao.getAtividadesSelecionadas();
		GenericDAO dao = getGenericDAO(movInscricao);

		try {
			
			for (AtividadeAtualizacaoPedagogica a : atividadesSelecionadas) {
				
				ParticipanteAtividadeAtualizacaoPedagogica novoParticipante = UFRNUtils.deepCopy(participante);
				
				if (novoParticipante.getRecursosNee()!=null && !novoParticipante.getRecursosNee().isEmpty()){
					for (RecursoNee r : novoParticipante.getRecursosNee())
						r.setId(0);
				}
			
				novoParticipante.setId(0);
				novoParticipante.setAtividade(a);
				novoParticipante.setSituacao(StatusParticipantesAtualizacaoPedagogica.INSCRITO.getId());
				dao.create(novoParticipante);
			}			
		
			
		}finally{
			dao.close();
		}

	}
	
	/**
	 * Valida as informa��es para opera��o {@link SigaaListaComando#ALTERAR_SITUACAO_PARTICIPANTE_ATUALIZACAO_PEDAGOGICA}
	 * @param mov
	 */
	private ListaMensagens validateAlteracaoSituacaoParticipante(Movimento mov){
		
		if( !mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_SITUACAO_PARTICIPANTE_ATUALIZACAO_PEDAGOGICA) )
			return null;
		
		MovimentoInscricaoAtividadeAP movInscricao = (MovimentoInscricaoAtividadeAP) mov;
		Collection<ParticipanteAtividadeAtualizacaoPedagogica> participantesSituacaoAlterada = movInscricao.getParticipantesSituacaoAlterada();
		ListaMensagens erros = new ListaMensagens();
		
		if( isEmpty(participantesSituacaoAlterada) )
			erros.addMensagem(MensagensAP.SITUACAO_SEM_ALTERACAO);
		
		return erros;
		
	}
	
	/**
	 * Valida as informa��es para opera��o {@link SigaaListaComando#INSCREVER_ATIVIDADE_ATUALIZACAO_PEDAGOGICA}
	 * @param mov
	 * @throws DAOException 
	 */
	private ListaMensagens validateInscricaoAtividade(Movimento mov) throws DAOException{
		
		if( !mov.getCodMovimento().equals(SigaaListaComando.INSCREVER_ATIVIDADE_ATUALIZACAO_PEDAGOGICA) )
			return null;
		
		MovimentoInscricaoAtividadeAP movInscricao = (MovimentoInscricaoAtividadeAP) mov;
		Collection<AtividadeAtualizacaoPedagogica> atividadesSelecionadas= movInscricao.getAtividadesSelecionadas();
		ListaMensagens erros = new ListaMensagens();
		
		//Verifica se ao menos uma atividade foi selecionada.
		if( isEmpty(atividadesSelecionadas) )
			erros.addErro("Para concluir a inscri��o o usu�rio deve selecionar ao menos uma atividade.");
		else{
			
			//Verifica se o docente j� est� inscrito para atividade selecionada.
			GrupoAtividadesAtualizacaoPedagogica grupoSelecionado = atividadesSelecionadas.iterator().next().getGrupoAtividade();
			ConsultaParticipanteAtividadeDAO participacaoDAO =	getDAO(ConsultaParticipanteAtividadeDAO.class, movInscricao);
			AtividadeAtualizacaoPedagogicaDAO grupoDAO = getDAO( AtividadeAtualizacaoPedagogicaDAO.class, movInscricao);
			
			try{
				
				List<AtividadeAtualizacaoPedagogica> atividadesVagas = CollectionUtils.toList( grupoDAO.findAtividadesByGrupo( grupoSelecionado.getId() ) );
				for (AtividadeAtualizacaoPedagogica a : atividadesSelecionadas) {
					//Verifica se a atividade selecionada n�o possui mais vagas.
					int posAtividadadeVaga = atividadesVagas.indexOf( a );
					if( !movInscricao.isGestorPAP() && posAtividadadeVaga >= 0 && atividadesVagas.get(posAtividadadeVaga).isVagasEsgotadas() )
						erros.addErro("A atividade " + a.getNome() + " selecionada n�o possui mais vagas dispon�veis.");
				}
				
				//Verifica choque de hor�rios entre atividades parelalas na semana de atualiza��o pedag�gica
				for (AtividadeAtualizacaoPedagogica a : atividadesSelecionadas) {
					if( !isEmpty(erros) )
						break;
					for (AtividadeAtualizacaoPedagogica a2 : atividadesSelecionadas) {
						if( 	!isEmpty(a.getHorarioInicio()) && !isEmpty(a2.getHorarioInicio()) &&
								!isEmpty(a2.getHorarioFim()) &&  !a.equals(a2) && a.getInicio().equals(a2.getInicio()) &&
								( CalendarUtils.isDentroPeriodo(a2.getInicio(), a2.getFim(), a.getInicio()) 
										|| CalendarUtils.isDentroPeriodo(a2.getInicio(), a2.getFim(), a.getFim()) )	)
						{
							int inicio = Integer.valueOf( a.getHorarioInicio().replace(":", "") );
							int inicio2 = Integer.valueOf( a2.getHorarioInicio().replace(":", "") );
							int fim2 = Integer.valueOf( a2.getHorarioFim().replace(":", "") );
							if( inicio >= inicio2 && inicio <= fim2 ){
								erros.addErro("N�o � poss�vel inscrever nas atividades " + a.getNome() + " e " + a2.getNome() + 
									", pois ocorrem no mesmo dia e hor�rio.");
								break;
							}	
						}	
					}		
				}
				
				List<ParticipanteAtividadeAtualizacaoPedagogica> participacoes =
					CollectionUtils.toList( participacaoDAO.findGeral(null, movInscricao.getParticipante().getDocente().getId(), grupoSelecionado.getId(), null, null) );
				if( !isEmpty(participacoes) ){
					for (ParticipanteAtividadeAtualizacaoPedagogica p : participacoes) {
						//Verifica se docente j� est� inscrito na atividade
						if(  isEmpty(erros) &&  movInscricao.getCodMovimento().equals(SigaaListaComando.INSCREVER_ATIVIDADE_ATUALIZACAO_PEDAGOGICA) 
								&& atividadesSelecionadas.contains(p.getAtividade()) )
							erros.addErro("Participante j� inscrito para atividade: " + p.getAtividade().getNome() + "<br>");
					}
				}
			}finally{
				grupoDAO.close();
				participacaoDAO.close();
			}
			
		}
		
		return erros;
		
	}
	

	private void removerParticipante(Movimento mov) throws DAOException {
		
		MovimentoInscricaoAtividadeAP movInscricao = (MovimentoInscricaoAtividadeAP) mov;
		ParticipanteAtividadeAtualizacaoPedagogica participante = movInscricao.getParticipante();
		Collection<AtividadeAtualizacaoPedagogica> atividadesSelecionadas= movInscricao.getAtividadesSelecionadas();
		ParticipanteAtividadeAPDAO pDao = getDAO(ParticipanteAtividadeAPDAO.class, movInscricao);
		
		if (atividadesSelecionadas!=null && !atividadesSelecionadas.isEmpty()) {
			
			List<Integer> idsAtividade = new ArrayList<Integer>();
			for (AtividadeAtualizacaoPedagogica a : atividadesSelecionadas)
				idsAtividade.add(a.getId());
			
			ArrayList<ParticipanteAtividadeAtualizacaoPedagogica> participantesARemover = (ArrayList<ParticipanteAtividadeAtualizacaoPedagogica>) pDao.findByAtividadesDocente(idsAtividade, participante.getDocente().getId());
			
			if (participantesARemover!=null && !participantesARemover.isEmpty())
				for (ParticipanteAtividadeAtualizacaoPedagogica p : participantesARemover){
					pDao.remove(p);
				}	
		}
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens erros = new ListaMensagens();
		ListaMensagens errosAlteracaoSituacao = validateAlteracaoSituacaoParticipante(mov);
		ListaMensagens errosInscricaoAtividade = validateInscricaoAtividade(mov);
		
		if( !isEmpty(errosAlteracaoSituacao) )
			erros.addAll( errosAlteracaoSituacao );
		if( !isEmpty(errosInscricaoAtividade) )
			erros.addAll( errosInscricaoAtividade );
		
		checkValidation(erros);
		
	}
	
}
