/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/09/2009
 *
 */
package br.ufrn.sigaa.extensao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.ufrn.arq.caixa_postal.ASyncMsgDelegate;
import br.ufrn.arq.caixa_postal.Mensagem;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dao.ResponsavelUnidadeDAO;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.comum.dominio.notificacoes.Destinatario;
import br.ufrn.comum.dominio.notificacoes.Notificacao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.AvaliacaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.DiscenteExtensaoDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.AtividadeUnidade;
import br.ufrn.sigaa.extensao.dominio.AvaliacaoAtividade;
import br.ufrn.sigaa.extensao.dominio.CursoEventoExtensao;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.dominio.EditalExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoSelecaoExtensao;
import br.ufrn.sigaa.extensao.dominio.Objetivo;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;
import br.ufrn.sigaa.extensao.dominio.PlanoTrabalhoExtensao;
import br.ufrn.sigaa.extensao.dominio.ProdutoExtensao;
import br.ufrn.sigaa.extensao.dominio.ProgramaExtensao;
import br.ufrn.sigaa.extensao.dominio.ProjetoExtensao;
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAvaliacao;
import br.ufrn.sigaa.extensao.dominio.TipoParecerAvaliacaoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoSituacaoDiscenteExtensao;
import br.ufrn.sigaa.extensao.jsf.helper.AtividadeExtensaoHelper;
import br.ufrn.sigaa.extensao.jsf.helper.ControleFluxoAtividadeExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioBolsistaExtensao;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.AutorizacaoDepartamento;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.projetos.negocio.MembroProjetoValidator;
import br.ufrn.sigaa.projetos.negocio.ProcessadorMembroProjeto;
import br.ufrn.sigaa.projetos.negocio.ProcessadorProjetoBase;
import br.ufrn.sigaa.projetos.negocio.ProjetoBaseValidator;

/**
 * Processador para realizar o cadastro de uma atividade de extens�o.
 * 
 * @author Victor Hugo
 * @author ilueny santos
 * 
 */
public class ProcessadorAtividadeExtensao extends AbstractProcessador {

    public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

	CadastroExtensaoMov ceMov = (CadastroExtensaoMov) mov;

	if (mov.getCodMovimento().equals(SigaaListaComando.SUBMETER_ATIVIDADE_EXTENSAO)) {
	    validate(mov);
	    submeter(ceMov);
	} else if (mov.getCodMovimento().equals(SigaaListaComando.SUBMETER_ATIVIDADE_ASSOCIADA_EXTENSAO)) {
	    validateSalvar(ceMov);
	    submeterAcaoAssociada(ceMov);
	} else if (mov.getCodMovimento().equals(SigaaListaComando.GRAVAR_RASCUNHO_ATIVIDADE_EXTENSAO)) {
	    validateSalvar(ceMov);
	    return gravarTemporariamente(ceMov, true);
	} else if (mov.getCodMovimento().equals(SigaaListaComando.SALVAR_ATIVIDADE_EXTENSAO)) {
	    validateSalvar(ceMov);
	    return salvar(ceMov);
	} else if (mov.getCodMovimento().equals(SigaaListaComando.RENOVAR_ATIVIDADE_EXTENSAO)) {
	    validate(ceMov);
	    renovar(ceMov);
	} else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_ATIVIDADE_EXTENSAO)) {
	    remover(ceMov);
	} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_SITUACAO_ATIVIDADE_EXTENSAO)) {
		validate(ceMov);
	    alterarSituacao(ceMov);
	} else if (mov.getCodMovimento().equals(SigaaListaComando.CONCLUIR_ATIVIDADE_EXTENSAO)) {
	    concluir(ceMov);
	} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_BOLSAS_ATIVIDADE_EXTENSAO)) {
	    validate(ceMov);
	    alterarBolsasConcedidas(ceMov);
	} else if (mov.getCodMovimento().equals(SigaaListaComando.ENVIAR_EMAIL_COORDENADOR_ACAO_SEM_RELATORIO)) {
	    enviarEmailNotificacaoFaltaDeRelatorioFinal(ceMov);
	} else if (mov.getCodMovimento().equals(SigaaListaComando.REATIVAR_ATIVIDADE_EXTENSAO)) {
	    validate(mov);
	    reativarAcaoExtensao(ceMov);
	} else if (mov.getCodMovimento().equals(SigaaListaComando.EXECUTAR_ATIVIDADE_EXTENSAO)) {
	    validate(mov);
	    executarAtividadeExtensao(ceMov);
	} else if (mov.getCodMovimento().equals(SigaaListaComando.NAO_EXECUTAR_ATIVIDADE_EXTENSAO)) {
	    validate(mov);
	    naoExecutarAtividadeExtensao(ceMov);
	}


	return ceMov.getAtividade();
    }

	/**
     * Status de projetos aprovados com ou sem recursos passam a ter status de em execu��o.
     *  
     * @param mov
     * @throws RemoteException 
     * @throws ArqException 
     * @throws NegocioException 
     */
    private void executarAtividadeExtensao(CadastroExtensaoMov mov) throws NegocioException, ArqException, RemoteException {
	AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class, mov);
	MembroProjetoDao memDao = getDAO(MembroProjetoDao.class, mov);
	try{
	    AtividadeExtensao atividade = dao.findByPrimaryKey(mov.getAtividade().getId(), AtividadeExtensao.class);
	    atividade.setMembrosEquipe(memDao.findMembroProjetoAtivoByProjetoPesquisa(atividade.getProjeto().getId(), false));
	    atividade.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_COORDENACAO_ACEITOU_EXECUCAO));
	    dao.initialize(atividade.getSituacaoProjeto());
	    atividade.getProjeto().setSituacaoProjeto(atividade.getSituacaoProjeto());
	    ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atividade);
	    ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_COORDENACAO_ACEITOU_EXECUCAO, atividade.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
	    
	    if (atividade.isProjetoIsolado()) {
		for (MembroProjeto membro : atividade.getMembrosEquipe()) {
		    if (membro.getDataInicio() == null || membro.getDataFim() == null) {
			if (membro.getDataInicio() == null)
			    membro.setDataInicio(atividade.getDataInicio());
			if (membro.getDataFim() == null)	    	     
			    membro.setDataFim(atividade.getDataFim());
			dao.update(membro);
		    }
		}
	    }	    
	    if (atividade.getSequencia() == 0) {
		int seq = AtividadeExtensaoHelper.gerarCodigoAcaoExtensao(atividade, dao);
		atividade.setSequencia(seq);
	    }
	    atividade.setAtivo(true);
	    atividade.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO));
	    dao.initialize(atividade.getSituacaoProjeto());
	    atividade.getProjeto().setSituacaoProjeto(atividade.getSituacaoProjeto());
	    dao.update(atividade);
	    ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atividade);
	    ProjetoHelper.gravarHistoricoSituacaoProjeto(atividade.getSituacaoProjeto().getId(), atividade.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
	}finally{
	    dao.close();
	    memDao.close();
	}
    }

    /**
     * Status de projetos aprovados com ou sem recursos passam a ter status de em execu��o ou n�o.
     * 
     * @param mov
     * @throws DAOException 
     */
    private void naoExecutarAtividadeExtensao(CadastroExtensaoMov mov) throws DAOException {
    	DiscenteExtensaoDao dao = getDAO(DiscenteExtensaoDao.class, mov);
    	try{
    		AtividadeExtensao atividade = mov.getAtividade();
    		
    		//cancelando participa��o da equipe
    		atividade.setMembrosEquipe(dao.findByExactField(MembroProjeto.class, "projeto.id", atividade.getProjeto().getId()));
    		if (atividade.isProjetoIsolado()) {
        		for (MembroProjeto mp : atividade.getMembrosEquipe()) {
        			dao.updateFields(MembroProjeto.class, mp.getId(), new String [] {"dataInicio", "dataFim"}, new Object [] {null, null});
        		}
    		}
    		
    		//cancelando participa��o dos discentes
    		Collection<DiscenteExtensao> discentes = dao.findByAtividade(atividade.getId());
    		if (ValidatorUtil.isNotEmpty(discentes)) {
        		for (DiscenteExtensao de : discentes) {
        			dao.updateFields(DiscenteExtensao.class, de.getId(), new String [] {"dataInicio", "dataFim"}, new Object [] {null, null});
        		}
    		}
    		
    		//mantendo o hist�rico atualizado
    		ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_COORDENACAO_NEGOU_EXECUCAO, atividade.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());

    		//a��o cancelada
    		atividade.getSituacaoProjeto().setId(TipoSituacaoProjeto.PROJETO_BASE_CANCELADO);
    		atividade.setAtivo(true);

    		dao.updateFields(AtividadeExtensao.class, atividade.getId(), 
    				new String[] {"situacaoProjeto.id","ativo"}, 
    				new Object[] {TipoSituacaoProjeto.PROJETO_BASE_CANCELADO, true});	    

    		ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atividade);
    		ProjetoHelper.gravarHistoricoSituacaoProjeto(atividade.getSituacaoProjeto().getId(), atividade.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
    	}finally{
    		dao.close();
    	}
    }


    /**
     * M�todo respons�vel por notificar todos os coordenadores de a��es de extens�o que est�o 
     * com relat�rios finais pendentes de envio. 
     * 
     * @param mov
     * @throws DAOException
     */
    private void enviarEmailNotificacaoFaltaDeRelatorioFinal(CadastroExtensaoMov mov) throws DAOException {
		
    	Collection<AtividadeExtensao> atividades = mov.getAtividades();
		
		if (!ValidatorUtil.isEmpty(atividades)) {
	
		    UsuarioDao dao = getDAO(UsuarioDao.class, mov);
		    JdbcTemplate jt = dao.getJdbcTemplate();
		    
		    Notificacao notificacao = new Notificacao();
		    notificacao.setContentType(MailBody.HTML);
		    notificacao.setTitulo("Notifica��o - Falta de Relat�rio Final de Projeto");
		    notificacao.setMensagem(mov.getMsgCoordenadorPendenteRelatorio());
		    
		    Collection<Destinatario> destinatarios = new ArrayList<Destinatario>();
		    
		    try  {
		    	
				for(AtividadeExtensao atv : atividades) {
		
				    /** @negocio: S� poder� enviar outra notifica��o X dias ap�s o envio da �ltima notifica��o. */
				    if(atv.isPermitidoEnviarEmailNotificacaoFaltaDeRelatorioFinal() 
					    && atv.isSelecionado() && atv.getProjeto().getCoordenador().getPessoa().getEmail() != null ) {
		
				    	Destinatario destinatario = new Destinatario();
				    	
				    	destinatario.setEmail(atv.getProjeto().getCoordenador().getPessoa().getEmail());
				    	destinatario.setUsuario(dao.findByServidor(atv.getProjeto().getCoordenador().getServidor()));
					    
				    	destinatarios.add(destinatario);
				        	    
			
						/** @negocio: 
						 * 	Define a data de envio da �ltima notifica��o por e-mail para o coordenador. 
						 * 	Utilizado no c�lculo que permite envio de novo aviso.*/
						jt.update(" UPDATE extensao.atividade SET data_cobranca_relatorio_final = now() WHERE id_atividade =  " + atv.getId() );
				    }
				    
				}
				
				notificacao.setDestinatariosEmail(destinatarios);
			    notificacao.setDestinatariosMensagem(destinatarios);
			    notificarPorEmail(notificacao, mov.getUsuarioLogado());
			    notificarPorMensagem(notificacao, mov.getUsuarioLogado());
			    
		    }finally {
		    	dao.close();
		    }
	
		}
    }
    
    /**
     * Notifica todos os destinat�rios selecionados por email.
     *
     * @param notificacao
     * @param remetente
     */
    private void notificarPorEmail(Notificacao notificacao, UsuarioGeral remetente) {
		for (Destinatario destinatario : notificacao.getDestinatariosEmail()) {
		    MailBody mail = new MailBody();
		    mail.setContentType(notificacao.getContentType());
	
		    // Definir remetente
		    String nomeRemetente = Notificacao.REMETENTE_DEFAULT; 
		    if (!isEmpty( notificacao.getNomeRemetente())) {
		    	nomeRemetente = notificacao.getNomeRemetente(); 
		    }
		    mail.setFromName( nomeRemetente );
	
		    // Definir email para respostas
		    if (!isEmpty(notificacao.getEmailRespostas())) {
		    	mail.setReplyTo(notificacao.getEmailRespostas());
		    }
	
		    mail.setEmail(destinatario.getEmail()) ;
		    mail.setAssunto(notificacao.getTitulo());
		    mail.setMensagem(notificacao.getMensagem());
		    Mail.send(mail);
		}
    }

    /**
     * Notifica todos os destinat�rios selecionados por mensagens
     * no sistema
     * @param remetente
     *
     * @param destinatario
     */
    private void notificarPorMensagem(Notificacao notificacao, UsuarioGeral remetente) {

		Collection<UsuarioGeral> usuarios = new ArrayList<UsuarioGeral>();
		for (Destinatario destinatario : notificacao.getDestinatariosMensagem()) {
		    usuarios.add(destinatario.getUsuario());
		}

		Mensagem mensagem = new Mensagem();
		mensagem.setTitulo(notificacao.getTitulo());
		mensagem.setMensagem(StringUtils.stripHtmlTags(notificacao.getMensagem()));
		mensagem.setRemetente(remetente);
		mensagem.setAutomatica(false);
		mensagem.setLeituraObrigatoria(true);
		mensagem.setConfLeitura(true);
		mensagem.setNumChamado(0);
		mensagem.setRemovidaRemetente(false);
		mensagem.setRemovidaDestinatario(false);
		mensagem.setPapel(null);
		
	
		ASyncMsgDelegate.getInstance().enviaMensagemUsuarios(mensagem, remetente, usuarios);
		
    }



    /**
     * Altera a situa��o da atividade.
     * 
     * @param mov
     * @return
     * @throws ArqException 
     * @throws NegocioException 
     * @throws RemoteException 
     */
    private void alterarSituacao(CadastroExtensaoMov mov) throws NegocioException, ArqException, RemoteException {
	AtividadeExtensao atividade = mov.getAtividade();

	if (atividade.getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_REMOVIDO) {
	    remover(mov);
	} else if (atividade.getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_CONCLUIDO) {
	    concluir(mov);
	} else {

	    AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class, mov );
	    try {
		atividade.setAtivo(true);		    	
		if(atividade.getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS){			
		    distribuirAcaoParaDepartamentos(mov);
		}

		/** @negocio: Gerando c�digo da a��o de extens�o mesmo quando a situa��o � alterada compulsoriamente. */
		if (atividade.isAprovadoEmExecucao()) {
		    validate(mov);
		    for (MembroProjeto mp : atividade.getMembrosEquipe()) {
			if (mp.getDataInicio() == null) {
			    mp.setDataInicio(atividade.getProjeto().getDataInicio());
			}
			if (mp.getDataFim() == null) {
			    mp.setDataFim(atividade.getProjeto().getDataFim());
			}
			dao.update(mp);
		    }
		    if (atividade.getSequencia() == 0) {
			int seq = AtividadeExtensaoHelper.gerarCodigoAcaoExtensao(atividade, dao);
			atividade.setSequencia(seq);
		    }
		}	
		dao.update(atividade);
		ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atividade);
		ProjetoHelper.gravarHistoricoSituacaoProjeto(atividade.getSituacaoProjeto().getId(), atividade.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
	    } finally {
		dao.close();
	    }
	}
    }


    /**
     * M�todo utilizado na altera��o da quantidade de bolsas da a��o de extens�o.
     * O presidente do comit� de extens�o pode manipular bolsas remanescentes entre a��es em execu��o. 
     * 
     * @param mov
     * @throws DAOException
     */
    private void alterarBolsasConcedidas(CadastroExtensaoMov mov) throws DAOException {
	GenericDAO dao = getGenericDAO(mov);
	try {
	    AtividadeExtensao atividade = mov.getAtividade();
	    ProjetoHelper.gravarHistoricoSituacaoProjeto(atividade.getSituacaoProjeto().getId(), atividade.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
	    dao.update(atividade);
	} finally {
	    dao.close();
	}
    }



    /**
     * Marca um projeto como removido.
     * 
     * @param mov
     * @return
     * @throws DAOException
     * @throws NegocioException 
     */
    private void remover(CadastroExtensaoMov mov) throws DAOException, NegocioException {
    	MembroProjetoDao dao = getDAO(MembroProjetoDao.class,mov);		
	try {
	    // Definir a situa��o da atividade para REMOVIDO
	    AtividadeExtensao atividade = dao.findByPrimaryKey(mov.getAtividade().getId(), AtividadeExtensao.class);
	    atividade.setMembrosEquipe(dao.findMembroProjetoAtivoByProjetoPesquisa(atividade.getProjeto().getId(), false));
	    atividade.setPlanosTrabalho(dao.findByExactField(PlanoTrabalhoExtensao.class, "atividade.id", atividade.getId()));
	    atividade.setAutorizacoesDepartamentos(dao.findByExactField(AutorizacaoDepartamento.class, "atividade.id", atividade.getId()));
	    atividade.setInscricoesSelecao(dao.findByExactField(InscricaoSelecaoExtensao.class, "atividade.id", atividade.getId()));
	    atividade.setParticipantes(dao.findByExactField(ParticipanteAcaoExtensao.class, "atividadeExtensao.id", atividade.getId()));
	    
	    atividade.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_REMOVIDO));
	    atividade.setAtivo(false);
	    dao.update(atividade);
	    ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atividade);
	    ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_REMOVIDO, atividade.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());

	    if (atividade.isProjetoIsolado()) {
		for (MembroProjeto mp : atividade.getMembrosEquipe()) {
		    mp.setAtivo(false);
		    mp.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
		    dao.update(mp);
		}
	    }

	    for (PlanoTrabalhoExtensao pt : atividade.getPlanosTrabalho()) {
		if (pt.getDiscenteExtensao() != null) {
		    DiscenteExtensao d = pt.getDiscenteExtensao();
		    d.setAtivo(false);
		    dao.update(d);
		}
	    }

	    for (AutorizacaoDepartamento autorizacao : atividade.getAutorizacoesDepartamentos()) {
		autorizacao.setAtivo(false);
		dao.update(autorizacao);
	    }

	    for (InscricaoSelecaoExtensao ins : atividade.getInscricoesSelecao()) {
		ins.setAtivo(false);
		dao.update(ins);
	    }

	  
	    for (ParticipanteAcaoExtensao part : atividade.getParticipantesNaoOrdenados()) {
		part.setAtivo(false);
		dao.update(part);
	    }
	   

	} finally {
	    dao.close();
	}
    }

    /**
     * Conclui uma a��o de extens�o.
     * @throws NegocioException 
     * 
     */
    private void concluir(CadastroExtensaoMov mov) throws DAOException, NegocioException {
    	AtividadeExtensao atividade = mov.getAtividade();
    	GenericDAO dao = getGenericDAO(mov);
    	Date hoje = CalendarUtils.descartarHoras(new Date());
    	try {
    		// Definir situa��o da a��o para CONCLUIDA
    		atividade.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_CONCLUIDO));
    		if (atividade.getDataFim() == null || hoje.before(atividade.getDataFim())) {
    			dao.updateField(Projeto.class, atividade.getProjeto().getId(), "dataFim", new Date());
    		}

    		ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atividade);
    		dao.updateField(AtividadeExtensao.class, atividade.getId(), "situacaoProjeto.id", atividade.getSituacaoProjeto().getId());
    		ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_CONCLUIDO, atividade.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());

    		/** @negocio: Finalizando todos os membros da equipe do projeto quando a a��o for isolada. */
    		if (atividade.isProjetoIsolado()) {
    			for (MembroProjeto mp : atividade.getMembrosEquipe()) {
    				if ((mp.getDataFim() == null) || (hoje.before(mp.getDataFim()))) {
    					dao.updateField(MembroProjeto.class, mp.getId(), "dataFim", new Date());
    				}
    			}	    
    		}

    		/** @negocio: Finalizando todos os planos de trabalho da a��o. */
    		for (PlanoTrabalhoExtensao pt : atividade.getPlanosTrabalho()) {
    			pt = dao.refresh(pt);
    			if (pt.getDataFim() == null || hoje.before(pt.getDataFim())) {
    				dao.updateField(PlanoTrabalhoExtensao.class, pt.getId(), "dataFim", atividade.getDataFim());
    			}
    		}
    		/** @negocio: Finalizando todos os discentes da a��o. */
    		for (Iterator<DiscenteExtensao> iterator = atividade.getDiscentesSelecionados().iterator(); iterator.hasNext();) {
    			DiscenteExtensao de = iterator.next();
				if ((de != null) && (de.getSituacaoDiscenteExtensao().getId() != TipoSituacaoDiscenteExtensao.FINALIZADO)) {
    				/** @negocio: 
    				 * 	S� finaliza o discente se estiver enviado o relat�rio final. Se n�o enviou, 
    				 * 	fica pendente, n�o finaliza o discente e impede que ele seja vinculado a outro projeto.
    				 */ 
    				de.setRelatorios(dao.findByExactField(RelatorioBolsistaExtensao.class, "discenteExtensao.id", de.getId()));
    				if (de.isEnviouRelatorioFinal()) {
    					if ((de.getDataFim() == null) || (de.getDataFim().getTime() > atividade.getDataFim().getTime())) {
    						de.setDataFim(atividade.getDataFim());
    						de.setSituacaoDiscenteExtensao(new TipoSituacaoDiscenteExtensao(TipoSituacaoDiscenteExtensao.FINALIZADO));
    						DiscenteExtensaoHelper.gravarHistoricoSituacao(dao, de, mov.getUsuarioLogado().getRegistroEntrada());
    						dao.updateFields(DiscenteExtensao.class, de.getId(),  new String[] { "dataFim","situacaoDiscenteExtensao"}, new Object[]{atividade.getDataFim(), new TipoSituacaoDiscenteExtensao(TipoSituacaoDiscenteExtensao.FINALIZADO)});
    					}
    				}
    			}
    		}
    	} finally {
    		dao.close();
    	}
    }

    /**
     * Submete uma a��o de extens�o aos departamentos de todos os servidores
     * membros da equipe de execu��o para que seja aprovado em plen�ria. Ap�s a
     * aprova��o da proposta pelo chefe o projeto segue para proex dando
     * continuidade ao processo
     * 
     * @param mov
     * @throws RemoteException 
     * @throws ArqException 
     */
    private void submeter(CadastroExtensaoMov mov) throws NegocioException, ArqException, RemoteException {
    	AtividadeExtensao atividade = mov.getAtividade();
    	ProjetoDao dao = getDAO(ProjetoDao.class, mov);
    	try {

    		/** 
    		 * @negocio: 	
    		 * 	Solicita��o da PROEX durante o cadastro de propostas para o edital de 2008.1:
    		 * 	se o autor da proposta for um diretor de centro ou chefe de
    		 * 	departamento, a proposta vai direto para PROEX.
    		 * 	N�o precisa ser enviada pra aprova��o dos departamentos envolvidos.
    		 */
    		Boolean chefeDiretorProponente = ProjetoHelper.isChefeUnidadeProponenteProjeto(mov.getUsuarioLogado(), atividade.getProjeto());

			//@negocio: Se o usu�rio for chefe da mesma unidade para a qual est� submetendo a a��o, esta a��o deve ser submetida diretamente para Pr�-Reitoria.
    		if (chefeDiretorProponente) {
    			atividade.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_SUBMETIDO));
    		}else {

    			AvaliacaoExtensaoDao avaliacaoDao = getDAO(AvaliacaoExtensaoDao.class, mov);
    			try {
    				//em que ponto no fluxo: submetido -> avaliado -> aprovado pelos departamentos -> avaliado pelo presidente com ressalvas -> cadastro em andamento -> submetido para departamentos(2� submiss�o) 
	    			AvaliacaoAtividade avaliacaoPresidenteComite = avaliacaoDao.findAvaliacaoPresidenteByAtividade(atividade.getId());
	    			Collection<AvaliacaoAtividade> avaliacoes = atividade.getAvaliacoes();
	    			AvaliacaoAtividade avaliacaoUnicaMembroComite = new AvaliacaoAtividade();
	    			avaliacaoUnicaMembroComite.setParecer(new TipoParecerAvaliacaoExtensao(0));
	    			if(avaliacoes.size() == 1){
	    				avaliacaoUnicaMembroComite = avaliacoes.iterator().next();
	    			}
	    			
	    			if (( avaliacaoPresidenteComite != null && avaliacaoPresidenteComite.getParecer() != null
							&& avaliacaoPresidenteComite.getParecer().getId() == TipoParecerAvaliacaoExtensao.APROVADO_COM_RECOMENDACAO)
							|| avaliacaoUnicaMembroComite.getParecer() != null
							&& avaliacaoUnicaMembroComite.getParecer().getId() == TipoParecerAvaliacaoExtensao.DEVOLVER_PARA_AJUSTES_COORDENADOR) {
	
	    				atividade.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS));		
	
	    				//AUTORIZA��ES
		    				//Recuperando todas as unidades que j� est�o no banco
		    				Collection<AutorizacaoDepartamento> autorizacoesNoBanco = dao.findByExactField(AutorizacaoDepartamento.class, "atividade.id", atividade.getId());
		
		    				//O coordenador reeditou a proposta, logo deve ser aprovada novamente.
		    				//Permitindo nova autoriza��o dos departamentos.
		    				for (AutorizacaoDepartamento autorizacao : autorizacoesNoBanco) {
		    					if (autorizacao.isAtivo()) {			
		    						autorizacao.setTipoAutorizacao(null);
		    						autorizacao.setDataAutorizacao(null);					
		    						dao.update(autorizacao);	
		    					}
		    				}		
	
	
	    				//DISTRIBUI��O	
		    				//O coordenador reeditou a proposta, logo pode ter inserido novos membros lotados em departamentos cuja autoriza��o
		    				//ainda n�o foi concedida. Distribui-se novamente nos departamentos.
		    				distribuirAcaoParaDepartamentos(mov);
	
	
	    				//AVALIA��ES
		    				//O coordenador reeditou a proposta, logo deve seguir novamente o fluxo, novas avalia��es s�o necess�rias
		    				//e as existentes devem ser desativadas, pois n�o se sabe, por exemplo, se o coordenador solicitou mais recursos.
		    				for(AvaliacaoAtividade av : atividade.getAvaliacoes()) {
		    					if( av.getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_COMITE 
		    							|| av.getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA
		    							|| av.getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_PRESIDENTE_COMITE 
		    							|| av.getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_PROEX) {
		    						av.setAtivo(false);
		    						dao.update(av);
		    					}
		    				}
	
	    			//em que ponto no fluxo: submetido -> distribu�do (1� submiss�o)
	    			} else {
	    				atividade.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS));
	    				distribuirAcaoParaDepartamentos(mov);
	    			}
    			}finally {
    				avaliacaoDao.close();
    			}

    		}
    		

    		/** 
    		 * @negocio: Se a situa��o do projeto for 'aguardando autoriza��o dos departamentos' e todos os departamentos j� 
    		 * aprovaram, a situa��o do projeto passa para 'Submetido'.
    		 */
    		if (atividade.getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS) {
    			boolean todosAnalisados = true;
				for (AutorizacaoDepartamento autorizacao : atividade.getAutorizacoesDepartamentos()) {    				
					if (autorizacao.isAtivo() && autorizacao.getDataAutorizacao() == null) {
						todosAnalisados = false;
						break;
					}
				}
    			if (todosAnalisados) {
    				atividade.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_SUBMETIDO));
    			}
    		}

    		atividade.setDataEnvio(new Date());
    		salvar(mov);
    		dao.clearSession();
    		dao.update(atividade);
    		ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atividade);
    		alterarHistoricoSituacaoAtividade(dao, mov, atividade.getSituacaoProjeto().getId(), false);
    	} finally {
    		dao.close();
    	}
    }

    /**
     * Submete a��o associada para aprova��o dos departamentos envolvidos.
     * 
     * @param mov
     * @throws NegocioException
     * @throws RemoteException 
     * @throws ArqException 
     */
    private void submeterAcaoAssociada(CadastroExtensaoMov mov) throws NegocioException, ArqException, RemoteException {
	ProjetoDao dao = getDAO(ProjetoDao.class, mov);
	try {
	    AtividadeExtensao atividade = mov.getAtividade();
	    dao.refresh(atividade.getProjeto());
	    atividade.getProjeto().setEquipe(dao.findByExactField(MembroProjeto.class, "projeto.id", atividade.getProjeto().getId()));
	    distribuirAcaoParaDepartamentos(mov);
	} finally {
	    dao.close();
	}
    }



    /**
     * Distribui a a��o para os departamentos envolvidos.
     * Gera uma autoriza��o que dever� se aprovada ou reprovada pelos
     * diretores dos centros de todos os docentes envolvidos na a��o.
     * 
     * Todas as propostas s�o encaminhadas aos departamentos
     * (inclusive as solicita��es de registro) para serem
     * autorizadas pelos chefes, mas as solicita��es de registros de
     * a��es feitas antes de 10/04/2008 foram encaminhadas
     * diretamente para a proex.
     * 
     * 
     * @param dao
     * @param acao
     * @throws RemoteException 
     * @throws ArqException 
     * @throws NegocioException 
     */
    public void distribuirAcaoParaDepartamentos(CadastroExtensaoMov mov) throws NegocioException, ArqException, RemoteException {
    	ProjetoDao dao = getDAO(ProjetoDao.class, mov);
    	ResponsavelUnidadeDAO ResponsavelDao = getDAO(ResponsavelUnidadeDAO.class, mov);
    	try {
    		AtividadeExtensao acao = mov.getAtividade();
    		Set<Unidade> unidadesParticipantes = new HashSet<Unidade>();

    		// Recuperando todas as unidades que j� est�o no banco. evitando duplica��o de autoriza��es.
    		Collection<AutorizacaoDepartamento> autorizacoesNoBanco = new ArrayList<AutorizacaoDepartamento>();
    		autorizacoesNoBanco = dao.findAtivosByExactField(AutorizacaoDepartamento.class, "atividade.id", acao.getId());
    		for (AutorizacaoDepartamento autorizacao : autorizacoesNoBanco) {
    			unidadesParticipantes.add(autorizacao.getUnidade());
    		}

    		/** @negocio: A a��o de extens�o deve ser distribu�da para os depto. de todos os servidores envolvidos. */
    		for (MembroProjeto mp : acao.getMembrosEquipe()) {
    			if ((mp.getServidor() != null)	&& (mp.getServidor().getUnidade() != null)) {
    				if (unidadesParticipantes.add(mp.getServidor().getUnidade())) {
    					AutorizacaoDepartamento auto = new AutorizacaoDepartamento();
    					auto.setUnidade(mp.getServidor().getUnidade());
    					auto.setAtividade(acao);
    					auto.setProjeto(acao.getProjeto());
    					auto.setTipoAutorizacao(null);
    					auto.setAtivo(true);
    					enviarEmailChefeDepartamento(auto, ResponsavelDao);
    					dao.create(auto);
    					acao.getAutorizacoesDepartamentos().add(auto);
    				}
    			}
    		}
    		
    		/** @negocio: Removendo autoriza��es de unidades que n�o possuem mais membros na equipe do projeto. */
    		ProcessadorAutorizacaoDepartamento procAuto = new ProcessadorAutorizacaoDepartamento();
    		MovimentoCadastro movAD = new MovimentoCadastro();
    		movAD.setSistema(mov.getSistema());
    		movAD.setObjMovimentado(acao.getProjeto());
    		movAD.setUsuarioLogado(mov.getUsuarioLogado());
    		movAD.setCodMovimento(SigaaListaComando.INATIVAR_AUTORIZACOES_DEPARTAMENTOS);
    		procAuto.execute(movAD);
    		
    	}finally {
    		dao.close();
    		ResponsavelDao.close();
    	}
    }
    
    private void enviarEmailChefeDepartamento( AutorizacaoDepartamento auto, ResponsavelUnidadeDAO dao ) throws DAOException {
    	Date hoje = new Date();
    	Collection<HashMap<String, Object>> responsaveis = dao.getDiretoresByUnidade(auto.getUnidade().getId(), hoje);
		
    	for (HashMap<String, Object> hashMap : responsaveis) {
			MailBody mail = new MailBody();
			mail.setContentType(MailBody.HTML);
			mail.setFromName("Sistema SIGAA/"+RepositorioDadosInstitucionais.getSiglaInstituicao());
			mail.setEmail( (String) hashMap.get("email") );
			mail.setAssunto("Autoriza��o de Projeto de Extens�o");
			mail.setMensagem("Caro(a) " + (String) hashMap.get("nome") + ", <br /><br />"+
					"O projeto: '" + auto.getProjeto().getTitulo() + "'" +
					" est� pendente da sua valida��o." +
					"<br /><br />" +
					"Para realizar a valida��o siga o caminho: <b>" +
					RepositorioDadosInstitucionais.get("siglaSigaa") + " -> Portal do Docente -> Chefia -> Autoriza��es -> Autorizar A��es de Extens�o </b>" +
					"<br /><br />" + 
					"ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA SIGAA. POR FAVOR, N�O RESPOND�-LO.");
			Mail.send(mail);
    	}
	}

	/**
     * Renova uma atividade de extens�o.
     * 
     * @param mov
     * @throws RemoteException 
     * @throws ArqException 
     */
    private void renovar(CadastroExtensaoMov mov) throws NegocioException, ArqException, RemoteException {
	//TODO: Verificar regras sobre renova��o mais detalhadamente.
	mov.getAtividade().setHistoricoSituacao(null);
	submeter(mov);
    }

    /**
     * Salva a atividade de extens�o sem o usu�rio ter que preencher todo o
     * formul�rio. Este � um cadastro com v�rios passos e deve ser poss�vel
     * interromper o processo para continuar depois. � necess�rio o
     * preenchimento de no m�nimo a primeira tela (dados gerais de atividade de
     * extens�o) e a segunda ( os dados gerais do tipo espec�fico de atividade a
     * ser cadastrada)
     * 
     * @return
     * @param mov
     * @throws RemoteException 
     * @throws ArqException 
     */
    private AtividadeExtensao salvar(CadastroExtensaoMov mov) throws NegocioException, ArqException, RemoteException {
	
	    persistirProjetoBase(mov);

	    // tipo de atividade
	    switch (mov.getAtividade().getTipoAtividadeExtensao().getId()) {
	    case TipoAtividadeExtensao.PROJETO:
		persistirProjeto(mov);
		break;
	    case TipoAtividadeExtensao.PROGRAMA:
		persistirPrograma(mov);
		break;
	    case TipoAtividadeExtensao.CURSO:
		persistirCurso(mov);
		break;
	    case TipoAtividadeExtensao.EVENTO:	    
		persistirCurso(mov);
		break;
	    case TipoAtividadeExtensao.PRODUTO:
		persistirProduto(mov);
		break;
	    case TipoAtividadeExtensao.PRESTACAO_SERVICO:
		// TODO ainda n�o definido pela proex
		break; 
	    }

	    persistirAtividade(mov);	    
	    persistirSubAtividades(mov);	    
	    return mov.getAtividade();
    }

    /**
     * Grava atividade durante o cadastro.
     * 
     * @param mov
     * @return
     * @throws NegocioException
     * @throws RemoteException 
     * @throws ArqException 
     */
    private AtividadeExtensao gravarTemporariamente(CadastroExtensaoMov mov, Boolean verificarHistoricoRepetido) 
    throws NegocioException, ArqException, RemoteException {

	ProjetoDao dao = getDAO(ProjetoDao.class, mov);
	try {
		//AtividadeExtensaoHelper.determinaVinculoProgramaExtensao((AtividadeExtensao) mov.getObjMovimentado());
	    if(mov.getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_EXTENSAO) ){
			if(!mov.getAtividade().isEdicaoGestor())
			    mov.getAtividade().setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO));
	    }else
			mov.getAtividade().setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO));
	    	mov.getAtividade().setObjetivo(null);
		    salvar(mov);
		    mov.getAtividade().getProjeto().setSituacaoProjeto(new TipoSituacaoProjeto(mov.getAtividade().getSituacaoProjeto().getId())); 
		    ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, mov.getAtividade());
		    if(mov.getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_EXTENSAO) ){
			if(!mov.getAtividade().isEdicaoGestor())
			    alterarHistoricoSituacaoAtividade(dao, mov, TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO,	verificarHistoricoRepetido);
		    }else
			alterarHistoricoSituacaoAtividade(dao, mov, mov.getAtividade().getProjeto().getSituacaoProjeto().getId(),	verificarHistoricoRepetido);
		    dao.initialize(mov.getAtividade().getSituacaoProjeto());
		    return mov.getAtividade();
	} finally {
	    dao.close();  
	}
    }


    /**
     * Grava um projeto de extens�o no banco.
     * 
     * @param atividade
     * @param dao
     * @throws RemoteException 
     * @throws ArqException 
     * @throws NegocioException 
     */
    private void persistirProjetoBase(CadastroExtensaoMov mov) throws NegocioException, ArqException, RemoteException {
    	GenericDAO dao = getGenericDAO(mov);
		try {
			ControleFluxoAtividadeExtensao control = (ControleFluxoAtividadeExtensao) mov.getObjAuxiliar();
		    AtividadeExtensao atividade = mov.getAtividade();
		    Projeto projeto = atividade.getProjeto();
		    MembroProjeto coordenador = montarInsertInformacoesTela(dao, control, atividade);
		    
		    // A��es Sem financiamento. 
		    if (ValidatorUtil.isEmpty(atividade.getEditalExtensao())) {
		    	atividade.setEditalExtensao(null);
	    	if (ValidatorUtil.isEmpty(atividade.getProgramaEstrategico()))
	    		atividade.setProgramaEstrategico(null);
	    	if (ValidatorUtil.isEmpty(atividade.getExecutorFinanceiro()))
	    		atividade.setExecutorFinanceiro(null);
	
		    //A��es Com financiamento e isoladas possuem o edital principal = edital da a��o.
		    } else if (projeto.isProjetoIsolado()) {
		    	dao.initialize(atividade.getEditalExtensao());
		    	projeto.setEdital(atividade.getEditalExtensao().getEdital());
		    }
	
		    // Salvando projeto Base...
		    ProcessadorProjetoBase procBase = new ProcessadorProjetoBase(); 
		    MovimentoCadastro movP = new MovimentoCadastro();
		    movP.setSistema(mov.getSistema());
		    movP.setObjMovimentado(projeto);
		    movP.setUsuarioLogado(mov.getUsuarioLogado());
		    movP.setObjAuxiliar(mov.getObjAuxiliar());
		    movP.setCodMovimento(SigaaListaComando.SALVAR_PROJETO_BASE);
		    procBase.execute(movP);
		    
		    //Salvador o Coordenador do Projeto
		    if ( control != null ) {
			    if ( control.getPassos()[control.getPassoAtual()].contains("Dados gerais") ) {
			    	salvarCoordenador(coordenador, projeto, mov);
				}
		    }
		    
		}finally {
		    dao.close();
		}
    }

	private MembroProjeto montarInsertInformacoesTela(GenericDAO dao,
			ControleFluxoAtividadeExtensao control, AtividadeExtensao atividade) throws DAOException {
		
		MembroProjeto coordenador = atividade.getProjeto().getCoordenador();
		if ( control != null ) {
		    if ( !control.getPassos()[control.getPassoAtual()].contains("Membros da equipe") )
		    	atividade.getProjeto().getEquipe().clear();
			if ( control.getPassos()[control.getPassoAtual()].contains("Dados gerais") )
				atividade.getProjeto().setCoordenador( dao.findByPrimaryKey(coordenador.getId(), MembroProjeto.class) );
		    if ( !control.getPassos()[control.getPassoAtual()].contains("Dados gerais") 
		    		&& !atividade.getProjeto().getEquipe().contains( coordenador ) ) {
		    	coordenador = dao.findByPrimaryKey(coordenador.getId(), MembroProjeto.class);
		    	atividade.getProjeto().getEquipe().add(coordenador);
		    }
		} else {
			atividade.getProjeto().getEquipe().clear();
		}
		
		return coordenador;
	}

    private void salvarCoordenador(MembroProjeto coordenador, Projeto projeto, CadastroExtensaoMov mov) throws NegocioException, ArqException, RemoteException {
	    MembroProjetoDao dao = getDAO(MembroProjetoDao.class, mov);
	    MembroProjeto membro = null;
    	try {
    		Servidor s = dao.findByPrimaryKey(coordenador.getServidor().getId(), Servidor.class);
			
    		ProcessadorMembroProjeto procMembroProj = new ProcessadorMembroProjeto();
			MovimentoCadastro movCad = new MovimentoCadastro();
			coordenador.setAtivo(true);
			if ( coordenador.getServidor().isDocente() )
				coordenador.setCategoriaMembro(new CategoriaMembro(CategoriaMembro.DOCENTE));
			else
				coordenador.setCategoriaMembro(new CategoriaMembro(CategoriaMembro.SERVIDOR));
			coordenador.setChDedicada(0);
			coordenador.setChExecucao(0);
			coordenador.setChPreparacao(0);
			coordenador.setDataInicio(projeto.getDataInicio());
			coordenador.setDataFim(projeto.getDataFim());
			coordenador.setDiscente(null);
			coordenador.setDocenteExterno(null);
			coordenador.setFuncaoMembro(new FuncaoMembro(FuncaoMembro.COORDENADOR));
			coordenador.setGerenciaParticipantes(true);
			coordenador.setParticipanteExterno(null);
			coordenador.setProjeto(projeto);
			coordenador.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			coordenador.setRemunerado(false);
			coordenador.setPessoa( s.getPessoa() );
			coordenador.setServidor( s );
			projeto.setCoordenador(coordenador);

			movCad.setObjMovimentado(coordenador);
			movCad.setSistema(mov.getSistema());
			movCad.setUsuarioLogado(mov.getUsuarioLogado());
			
			membro = cooordenadorProjeto(projeto, coordenador);
			if ( isEmpty(membro) )
				movCad.setCodMovimento(SigaaListaComando.SALVAR_MEMBRO_PROJETO_BASE);
			else {
				movCad.setObjAuxiliar( dao.findByPrimaryKey(membro.getId(), MembroProjeto.class) );
				movCad.setCodMovimento(SigaaListaComando.ALTERAR_COORDENADOR_PROJETO_BASE);
			}
			dao.detach(membro);
			procMembroProj.execute(movCad);
		
		} finally {
			dao.close();
		}
	}

	/**
	 * Verifica se h� coordenador para o projeto.
	 * 
	 * @param membros
	 * @param coordenadorCandidato
	 * @param lista
	 * @throws DAOException
	 */
	public static MembroProjeto cooordenadorProjeto(Projeto pj, MembroProjeto coordenadorCandidato) throws DAOException {
		if (coordenadorCandidato.getFuncaoMembro() != null) {
		    MembroProjetoDao dao = DAOFactory.getInstance().getDAO(MembroProjetoDao.class);
		    try {
		    	//Lista de todos os membros do projeto
		    	List<MembroProjeto> membrosAtivos = (List<MembroProjeto>) dao.findByProjeto(pj.getId(), true);

				// apenas um coordenador
				if (coordenadorCandidato.isCoordenador()) {
					//Percorre toda lista em busca de coordenadores ativos no mesmo per�odo.
					for (MembroProjeto coordenadorAtual : membrosAtivos){
						if ( coordenadorAtual.isCoordenador()
							&& (coordenadorAtual.getPessoa().getId() != coordenadorCandidato.getPessoa().getId()) 
							&& (CalendarUtils.isIntervalosDeDatasConflitantes(
								coordenadorAtual.getDataInicio(), coordenadorAtual.getDataFim(), coordenadorCandidato.getDataInicio(), coordenadorCandidato.getDataFim()))) {
						    return coordenadorAtual;
						}
					}
	
				}
		    } finally {
		    	dao.close();
		    }
		}
		return null;
	}
    
	/**
     * Grava um projeto de extens�o no banco.
     * 
     * @param atividade
     * @param dao
     * @throws DAOException
     */
    private void persistirProjeto(CadastroExtensaoMov mov) throws DAOException {
	GenericDAO dao = getGenericDAO(mov);
	try {
	    AtividadeExtensao atividade = mov.getAtividade();
	    ProjetoExtensao projetoExtensao = atividade.getProjetoExtensao();
	    if (ValidatorUtil.isEmpty(projetoExtensao.getGrupoPesquisa()))
	    	projetoExtensao.setGrupoPesquisa(null);
	    dao.createOrUpdate(projetoExtensao);

	} catch (Exception e) {
	    throw new DAOException("Erro ao gravar projeto de extens�o.");
	}finally {
	    dao.close();
	}
    }

    /**
     * Grava um curso de extens�o no banco.
     * 
     * @param atividade
     * @param dao
     * @throws DAOException
     */
    private void persistirCurso(CadastroExtensaoMov mov) throws DAOException {
	GenericDAO dao = getGenericDAO(mov);
	try {
	    AtividadeExtensao atividade = mov.getAtividade();
	    CursoEventoExtensao curso = atividade.getCursoEventoExtensao();
	    UFRNUtils.clearTransientObjects(curso);
	    dao.createOrUpdate(curso);
	} catch (Exception e) {
	    throw new DAOException("Erro ao gravar curso de extens�o.");
	}finally {
	    dao.close();
	}
    }
    
    
    /**
     * Grava as subAtividades de um curso/evento no banco.
     * 
     * @param atividade
     * @param dao
     * @throws DAOException
     */
    private void persistirSubAtividades(CadastroExtensaoMov mov) throws DAOException {
    	
    	if(mov.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.EVENTO
    			|| mov.getAtividade().getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.CURSO) {
    	
    		GenericDAO dao = getGenericDAO(mov);
			try {
			    AtividadeExtensao atividade = mov.getAtividade();
			    
			    if(!ValidatorUtil.isEmpty(atividade.getSubAtividadesExtensao())){
				    for(SubAtividadeExtensao subAtv : atividade.getSubAtividadesExtensao()) {
				    	if(subAtv.getId() == 0 && !subAtv.isAtivo()) {
				    		continue;
				    	} else {				    		
				    		dao.createOrUpdate(subAtv);
				    	}
				    }
			    }
			    
			}finally {
			    dao.close();
			}
	
    	}
    }
    

    /**
     * Grava um programa de extens�o.
     * 
     * @param atividade
     * @param dao
     * @throws DAOException
     */
    private void persistirPrograma(CadastroExtensaoMov mov) throws DAOException {
	GenericDAO dao = getGenericDAO(mov);
	try {
	    AtividadeExtensao atividade = mov.getAtividade();
	    ProgramaExtensao programa = atividade.getProgramaExtensao();
	    UFRNUtils.clearTransientObjects(programa);
	    dao.createOrUpdate(programa);
	} catch (Exception e) {
	    throw new DAOException("Erro ao gravar programa de extens�o.");
	}finally {
	    dao.close();
	}
    }

    /**
     * Grava um produto de extens�o.
     * 
     * @param atividade
     * @param dao
     * @throws Exception 
     */
    private void persistirProduto(CadastroExtensaoMov mov) throws DAOException {
	GenericDAO dao = getGenericDAO(mov);
	try {
	    AtividadeExtensao atividade = mov.getAtividade();
	    ProdutoExtensao produto = atividade.getProdutoExtensao();
	    UFRNUtils.clearTransientObjects(produto);		
	    dao.createOrUpdate(produto);
	} catch (Exception e) {
	    throw new DAOException("Erro ao gravar produto de extens�o.");
	}finally {
	    dao.close();
	}
    }

    /**
     * Grava partes comuns de uma a��o de extens�o.
     * Equipe, Or�amento, Publico alvo, Arquivos, etc. 
     * 
     * @param atividade
     * @param dao
     * @throws DAOException
     */
    private void persistirAtividade(CadastroExtensaoMov mov) throws DAOException {
	GenericDAOImpl dao = getDAO(GenericDAOImpl.class, mov);
	try {
	    AtividadeExtensao atividade = mov.getAtividade();

	    UFRNUtils.anularAtributosVazios(atividade, "areaTematicaPrincipal", "executorFinanceiro", "programaEstrategico");
	    
	    // se sem financiamento, configura a proposta retirando todas as
		// solicita��es de recursos...
		if ( atividade.isAutoFinanciado() ) {
			atividade.setFinanciamentoExterno(false);
			atividade.setFinanciamentoInterno(false);
			atividade.setEditalExtensao(null);
			atividade.getProjeto().setEdital(null);
			atividade.setClassificacaoFinanciadora(null);
			atividade.setBolsasSolicitadas(0);
		}
		
		if (!atividade.isFinanciamentoInterno() ) {
			atividade.setEditalExtensao(null);
			atividade.getProjeto().setEdital(null);
		}
	    
	    // Gravando o local de realiza��o da a��o.
	    if ((atividade.getLocalRealizacao() != null) && (atividade.getLocalRealizacao().getId() >= 0)){
		if ((atividade.getLocalRealizacao().getMunicipio() != null)	&& (atividade.getLocalRealizacao().getMunicipio().getId() == 0)) {
		    atividade.getLocalRealizacao().setMunicipio(null);
		}
		dao.getHibernateTemplate().saveOrUpdateAll(atividade.getLocaisRealizacao());
	    }

	    if ( !isEmpty( atividade.getObjetivo() ) ) {
		    for (Objetivo objetivo : atividade.getObjetivo()) {
		    	dao.detach( objetivo );
			}
	    }
	    
	    dao.createOrUpdate(atividade);

	    // Unidades Proponentes
	    if (atividade.getUnidadesProponentes() != null) {
			for (AtividadeUnidade au : atividade.getUnidadesProponentes()) {
			    dao.createOrUpdate(au);
			}
	    }

	}finally {
	    dao.close();
	}
    }

    /**
     * Faz a valida��o da atividade de extens�o a ser cadastrada/atualizada
     */
    public void validate(Movimento mov) throws NegocioException, ArqException {

	CadastroExtensaoMov aMov = (CadastroExtensaoMov) mov;
	ListaMensagens lista = new ListaMensagens();
	AtividadeExtensao atividade = aMov.getAtividade();

	Usuario usuario = (Usuario) mov.getUsuarioLogado();

	// Se o usu�rio n�o for servidor ou docente externo e estiver tentando realizar esta opera��o.
	if (!usuario.getVinculoAtivo().isVinculoServidor() && !usuario.getVinculoAtivo().isVinculoDocenteExterno()) {
		throw new NegocioException("Apenas Docentes ou T�cnicos Administrativos podem realizar esta opera��o.");
	}
	
	if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_BOLSAS_ATIVIDADE_EXTENSAO)) {
	    checkRole(SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO, mov);
	    
	    
	} else if (mov.getCodMovimento().equals(SigaaListaComando.EXECUTAR_ATIVIDADE_EXTENSAO) || mov.getCodMovimento().equals(SigaaListaComando.NAO_EXECUTAR_ATIVIDADE_EXTENSAO) ) {
		if (!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_EXTENSAO) && !mov.getUsuarioLogado().isUserInRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO)) {
			ProjetoBaseValidator.validaExecucaoProjeto(atividade.getProjeto(), mov.getUsuarioLogado().getPessoa(), lista);
		}
		
		
	} else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_ATIVIDADE_EXTENSAO)) {
		if (!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_EXTENSAO) && !mov.getUsuarioLogado().isUserInRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO)) {
			ProjetoBaseValidator.validaRemoverProjeto(atividade.getProjeto(), mov.getUsuarioLogado().getPessoa(), lista);
		}
		
		
	} else if (mov.getCodMovimento().equals(SigaaListaComando.REATIVAR_ATIVIDADE_EXTENSAO)) {
		boolean acaoConcluida = Arrays.asList(TipoSituacaoProjeto.PROJETOS_GRUPO_CONCLUIDO).contains(atividade.getSituacaoProjeto().getId());
		if (!(atividade.isFinalizada() && acaoConcluida)) {
			lista.addErro("Opera��o autorizada somente para A��es Acad�micas Conclu�das.");
		}		
		
		
	} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_SITUACAO_ATIVIDADE_EXTENSAO)) {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO, mov);
		EditalExtensao edital = atividade.getEditalExtensao();
			if ( edital != null && !CalendarUtils.isDentroPeriodoAberto(edital.getInicioSubmissao(), edital.getDataFimAutorizacaoChefe()) && 
					 atividade.getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_PROPOSTA_DEVOLVIDA_PARA_COORDENADOR ) {
				lista.addErro("N�o � poss�vel mudar o status do projeto, pois o per�odo de autoriza��o pelos " +
						"chefes de departamentos j� expirou.");
			}
	}else {		
	    AtividadeExtensaoValidator.validaDadosGerais(atividade, lista, true);
    	
	    if (atividade.getTipoAtividadeExtensao().isProjeto()){
    		AtividadeExtensaoValidator.validaDadosProjeto(atividade, lista);
    		AtividadeExtensaoValidator.validaObjetivos(atividade.getProjetoExtensao(), lista);
    	
    	} else if (aMov.getAtividade().getTipoAtividadeExtensao().isCurso() 
    			|| aMov.getAtividade().getTipoAtividadeExtensao().isEvento()) {
	    	AtividadeExtensaoValidator.validaDadosCursoEvento(atividade, lista);
	    
	   	} else if (atividade.getTipoAtividadeExtensao().isProduto()) {
	    	// o produto possui apenas um membro na equipe, o coordenador.
	    	AtividadeExtensaoValidator.validaDadosProduto(atividade, lista);
	    
	    } else if (atividade.getTipoAtividadeExtensao().isPrograma()) {
	    	atividade.getMembrosEquipe().iterator();
	    	for(MembroProjeto mp :atividade.getMembrosEquipe()) {
	    		if(mp.getServidor()!= null && mp.getServidor().getId() != 0){
	    			getGenericDAO(mov).getSession().refresh(mp.getServidor().getEscolaridade());
	    		}
	    	}
	    	AtividadeExtensaoValidator.validaDadosPrograma(atividade, lista);
	    	AtividadeExtensaoValidator.validaAtividadesPrograma(atividade,	lista);
	    }
	    
	    MembroProjetoValidator.validaCoordenacaoAtiva(atividade.getMembrosEquipe(), lista);	    
	}
	checkValidation(lista);
    }

    /**
     * Altera a situa��o da atividade de extens�o e grava no hist�rico a mudan�a.
     * 
     * @param dao
     * @param mov
     * @param idTipoSituacao
     *            situa��o atual da atividade. verifica se esta situa��o j� esta
     *            gravada no hist�rico
     * @param verificarRepetido
     *            se TRUE verifica se j� tem a situa��o no hist�rico. FALSE
     *            grava novo hist�rico independe de j� existir
     * 
     * @throws DAOException
     */
    private void alterarHistoricoSituacaoAtividade(ProjetoDao dao, CadastroExtensaoMov mov, int idTipoSituacao, boolean verificarRepetido) throws DAOException {
	// pega o ultimo hist�rico da atividade gravado no banco...
	int ultimoHistorico = dao.findLastSituacaoProjeto(mov.getAtividade().getProjeto().getId(), null);
	// verifica se o hist�rico da atividade mudou, se mudou grava
	if ((ultimoHistorico == 0) || (ultimoHistorico != idTipoSituacao) || (!verificarRepetido)) {
	    ProjetoHelper.gravarHistoricoSituacaoProjeto(idTipoSituacao, mov.getAtividade().getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
	}
    }

    /**
     * Valida��es realizadas antes de salvar uma a��o de extens�o.
     * 
     * @param mov
     * @throws NegocioException
     * @throws DAOException
     */
    private void validateSalvar(CadastroExtensaoMov mov) throws NegocioException, DAOException {
	CadastroExtensaoMov aMov = mov;
	ListaMensagens erros = new ListaMensagens();
	AtividadeExtensao atividade = aMov.getAtividade();
	if (!atividade.isProjetoAssociado()) {
	    AtividadeExtensaoValidator.validaDadosGerais(atividade, erros, true);
	}
	checkValidation(erros);
    }




    /**
     * Procedimento respons�vel por reativar uma a��o de extens�o que foi finalizada por engano.
     * Aten��o:
     * Este procedimento n�o recupera a��es removidas. Ele reativa uma a��o que j� estava conclu�da.
     * Neste procedimento a a��o passar� obrigatoriamente para o status 'em execu��o'.
     * 
     * @param mov
     * @throws DAOException
     * @throws NegocioException
     */
    private void reativarAcaoExtensao(CadastroExtensaoMov mov) throws DAOException, NegocioException {
	AtividadeExtensao atividade = mov.getAtividade();
	Date dataNovaFinalizacao = mov.getNovaDataFinalizacaoAcao();
	Date dataUltimafinalizacao = new Date(atividade.getDataFim().getTime());
	GenericDAO dao = getGenericDAO(mov);

	ListaMensagens lista = new ListaMensagens();
	ValidatorUtil.validaData(Formatador.getInstance().formatarData(dataNovaFinalizacao), "Data da Nova Finaliza��o", lista);
	checkValidation(lista);		

	try {
	    // Definir situa��o da a��o para EM EXECU��O
	    atividade.setAtivo(true);
	    atividade = dao.refresh(atividade);
	    atividade.setDataFim(dataNovaFinalizacao);
	    atividade.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO));
	    ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atividade);
	    dao.update(atividade);
	    ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO, atividade.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());

	    /** @negocio: Reativando todos os membros da equipe do projeto quando a a��o foi finalizada */
	    for (MembroProjeto mp : atividade.getMembrosEquipe()) {
		if (mp.getDataFim().getTime() == dataUltimafinalizacao.getTime()) {
		    mp.setDataFim(atividade.getDataFim());
		}
		dao.update(mp);
	    }

	    /** @negocio: Reativando todos os planos de trabalho da a��o ativos no dia da finaliza��o. */
	    for (PlanoTrabalhoExtensao pt : atividade.getPlanosTrabalho()) {
		pt = dao.refresh(pt);
		if (pt.getDataFim().getTime() == dataUltimafinalizacao.getTime()) {
		    pt.setDataFim(atividade.getDataFim());
		}
		dao.update(pt);
	    }

	    /** @negocio: Reativando todos os discentes da a��o ativos no dia da finaliza��o. */
	    for (DiscenteExtensao de : atividade.getDiscentesSelecionados()) {
		if ((de != null) && (de.getSituacaoDiscenteExtensao().getId() == TipoSituacaoDiscenteExtensao.FINALIZADO)) {
		    dao.refresh(de);					
		    if (de.getDataFim().getTime() == dataUltimafinalizacao.getTime()) {
			de.setDataFim(atividade.getDataFim());
			de.setSituacaoDiscenteExtensao(new TipoSituacaoDiscenteExtensao(TipoSituacaoDiscenteExtensao.ATIVO));
			DiscenteExtensaoHelper.gravarHistoricoSituacao(dao, de, mov.getUsuarioLogado().getRegistroEntrada());
			dao.update(de);
		    }
		}
	    }

	} finally {
	    dao.close();
	}
    }



}
