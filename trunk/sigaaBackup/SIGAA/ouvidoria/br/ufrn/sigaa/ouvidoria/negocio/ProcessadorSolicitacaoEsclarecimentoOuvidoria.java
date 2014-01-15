package br.ufrn.sigaa.ouvidoria.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ouvidoria.dao.HistoricoManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaSolicitante;
import br.ufrn.sigaa.ouvidoria.dominio.HistoricoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.InteressadoNaoAutenticado;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.StatusManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.TipoHistoricoManifestacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador utilizado para enviar solicitação de esclarecimentos para o interessado.
 * 
 * Quando a ouvidoria precisa de mais informações para responder uma manifestação de um usuário,
 * é possível para ela enviar um e-mail para o interessado para sanar suas dúvidas. Este processador
 * é responsável por esta operação.
 * 
 * @author Diego Jácome
 *
 */
public class ProcessadorSolicitacaoEsclarecimentoOuvidoria extends AbstractProcessador {

    @Override
    public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		
		if (SigaaListaComando.SOLICITAR_ESCLARECIMENTO.equals(mov.getCodMovimento())){
			
			HistoricoManifestacao historico = movimento.getObjMovimentado();
			Manifestacao manifestacao = historico.getManifestacao();
			
			if(!manifestacao.isRespondida())
				enviarSolicitacaoEsclarecimento(movimento);
			
		} else if (SigaaListaComando.RESPONDER_ESCLARECIMENTO.equals(mov.getCodMovimento())) {
			
			enviarRespostaEsclarecimento(movimento);	
			
		} else if (SigaaListaComando.CONFIRMAR_CODIGO_ACESSO_OUVIDORIA.equals(mov.getCodMovimento())) {
			
			confirmarCodigoAcesso(movimento);
			
		}
		
		return null;
    }

	/**
     * Esse método tem o objetivo de retornar o último status da manifestações, com exceção do status ESPERANDO_ESCLARECIMENTO.
     * Pois o objetivo do processamento é retornar a manifestação pur seu último status antes da solicitação de esclarimento.
     * Ex: Se foi um designado que pediu esclarecimento, então a solicitação volta para ele.
     * 
     * @param mov
     * @throws DAOException
     */
	private HistoricoManifestacao getUltimoHistorico(MovimentoCadastro movimento) throws DAOException {
		
		HistoricoManifestacaoDao hDao = null;
		
		try {
			HistoricoManifestacao historico = movimento.getObjMovimentado();
			hDao = getDAO(HistoricoManifestacaoDao.class, movimento);
			ArrayList<HistoricoManifestacao> historicos = (ArrayList<HistoricoManifestacao>) hDao.getAllHistoricosByManifestacao(historico.getManifestacao().getId());
			HistoricoManifestacao ultimoHistorico = null;
			
			if (historicos != null){
				for (HistoricoManifestacao h : historicos){
					if (h.getTipoHistoricoManifestacao().getId() != TipoHistoricoManifestacao.ESCLARECIMENTO_OUVIDORIA_INTERESSADO &&
						h.getTipoHistoricoManifestacao().getId() != TipoHistoricoManifestacao.INTERESSADO_OUVIDORIA &&
						h.getTipoHistoricoManifestacao().getId() != TipoHistoricoManifestacao.INTERESSADO_RESPONSAVEL &&
						(ultimoHistorico == null || ultimoHistorico.getDataCadastro().getTime() >= h.getDataCadastro().getTime()))
						ultimoHistorico = h;
				}
			}
		
			return ultimoHistorico;
		} finally {
			if (hDao != null)
				hDao.close();
		}
	}

    /**
     * Cadastra um histórico de manifestação de acordo com o tipo passado.
     * 
     * @param mov
     * @param tipo
     * @throws DAOException
     */
    private void cadastrarHistorico(MovimentoCadastro mov, TipoHistoricoManifestacao tipo) throws DAOException {
		HistoricoManifestacao historico = mov.getObjMovimentado();
		Manifestacao manifestacao = historico.getManifestacao();
		historico.setManifestacao(manifestacao);
		historico.setTipoHistoricoManifestacao(tipo);		
		getGenericDAO(mov).create(historico);
    }

    /**
     * Atualiza os dados da manifestação de acordo com a necessidade.
     * Pode atualizar somente o status da manfiestação, ou o status e o campo 'anonima'.
     * 
     * @param mov
     * @param status
     * @throws DAOException
     */
    private void atualizarDadosManifestacao(MovimentoCadastro mov, int status) throws DAOException {
		Manifestacao manifestacao = ((HistoricoManifestacao) mov.getObjMovimentado()).getManifestacao();
		
		if(SigaaListaComando.SOLICITAR_ESCLARECIMENTO.equals(mov.getCodMovimento())) {
	    	if (manifestacao.getInteressadoManifestacao().getCategoriaSolicitante().getId() == CategoriaSolicitante.COMUNIDADE_EXTERNA){
	    		
	    		// Faz verificações de segurança para que não ocorra NP
	    		if (manifestacao.getInteressadoManifestacao() == null)
	    			manifestacao = getGenericDAO(mov).refresh(manifestacao);
	    		if (manifestacao.getInteressadoManifestacao().getDadosInteressadoManifestacao() == null)
	    			manifestacao.setInteressadoManifestacao(getGenericDAO(mov).refresh(manifestacao.getInteressadoManifestacao()));
	    		if (manifestacao.getInteressadoManifestacao().getDadosInteressadoManifestacao().getInteressadoNaoAutenticado() == null)
	    			manifestacao.getInteressadoManifestacao().setDadosInteressadoManifestacao(getGenericDAO(mov).refresh(manifestacao.getInteressadoManifestacao().getDadosInteressadoManifestacao()));

	    		InteressadoNaoAutenticado naoAutenticado = manifestacao.getInteressadoManifestacao().getDadosInteressadoManifestacao().getInteressadoNaoAutenticado();
	    		String codigoAcesso = geraSenhaAutomatica();
	    		getGenericDAO(mov).updateField(InteressadoNaoAutenticado.class, naoAutenticado.getId(), "codigoAcesso", codigoAcesso);
	    	}
		}
		
		getGenericDAO(mov).updateField(Manifestacao.class, manifestacao.getId(), "statusManifestacao", StatusManifestacao.getStatusManifestacao(status));
    }

    /**
     * Processa o solicitação de esclarecimento ao usuário.
     * 
     * @param movimento
     * @throws DAOException
     */
	private void enviarSolicitacaoEsclarecimento(MovimentoCadastro movimento) throws DAOException {
		cadastrarHistoricoEsclarecimento(movimento);
		
		atualizarDadosManifestacao(movimento, StatusManifestacao.ESPERANDO_ESCLARECIMENTO);
		
		HistoricoManifestacao historico = movimento.getObjMovimentado();
		Manifestacao manifestacao = historico.getManifestacao();
		
		manifestacao = getGenericDAO(movimento).refresh(manifestacao);
		
		pedidoEsclarecimentoUsuario(manifestacao.getInteressadoManifestacao().getDadosInteressadoManifestacao().getPessoaEmail(), manifestacao, historico);
	}
    
    /**
     * Processa o resposta de esclarecimento ao usuário.
     * 
     * @param movimento
     * @throws DAOException
     */
	private void enviarRespostaEsclarecimento(MovimentoCadastro movimento) throws DAOException {
		
		HistoricoManifestacao ultimoHistorico = getUltimoHistorico(movimento);
		cadastrarHistoricoRespostaEsclarecimento(movimento,ultimoHistorico);
		
		/** Comentado pois o fluxo até agora só passa pela ouvidoria */
		
//		if (ultimoHistorico == null || ultimoHistorico.getPessoaResponsavel() == null)
			atualizarDadosManifestacao(movimento, StatusManifestacao.ESCLARECIDO_OUVIDORIA);
//		else
//			atualizarDadosManifestacao(movimento, StatusManifestacao.ESCLARECIDO_RESPONSAVEL);

		HistoricoManifestacao historico = movimento.getObjMovimentado();
		Manifestacao manifestacao = historico.getManifestacao();
		
		manifestacao = getGenericDAO(movimento).refresh(manifestacao);
	}
	
	/**
	 * Cadastra um histórico para a unidade responsável por resposta.
	 * 
	 * @param mov
	 * @throws DAOException
	 */
    private void cadastrarHistoricoRespostaEsclarecimento(MovimentoCadastro mov, HistoricoManifestacao ultimoHistorico) throws DAOException {
		
    	HistoricoManifestacao historico = mov.getObjMovimentado();
		Manifestacao manifestacao = historico.getManifestacao();
		
		historico.setManifestacao(manifestacao);
		
		if(SigaaListaComando.RESPONDER_ESCLARECIMENTO.equals(mov.getCodMovimento())) {
		    historico.setDataResposta(new Date());
		    
		    /** Trecho comentado porque por enquanto o fluxo vai retornar direto para Ouvidoria */
		    
		    /* 
		    if (ultimoHistorico != null){
		    	historico.setPessoaResponsavel(ultimoHistorico.getPessoaResponsavel());
		    	historico.setUnidadeResponsavel(ultimoHistorico.getUnidadeResponsavel());
		    	historico.setPrazoResposta(ultimoHistorico.getPrazoResposta());
		    	historico.setDelegacoesUsuarioResposta(ultimoHistorico.getDelegacoesUsuarioResposta());
		    	
		    	if (ultimoHistorico.getPessoaResponsavel() != null) 
		    		historico.setTipoHistoricoManifestacao(TipoHistoricoManifestacao.getTipoHistoricoManifestacao(TipoHistoricoManifestacao.INTERESSADO_RESPONSAVEL));
		    	else 
		    		historico.setTipoHistoricoManifestacao(TipoHistoricoManifestacao.getTipoHistoricoManifestacao(TipoHistoricoManifestacao.INTERESSADO_OUVIDORIA));
		    } else
		    */
	    		historico.setTipoHistoricoManifestacao(TipoHistoricoManifestacao.getTipoHistoricoManifestacao(TipoHistoricoManifestacao.INTERESSADO_OUVIDORIA));
		}
		
		getGenericDAO(mov).create(historico);	
		
	}
	
	/**
	 * Cadastra um histórico para a unidade responsável por resposta.
	 * 
	 * @param mov
	 * @throws DAOException
	 */
    private void cadastrarHistoricoEsclarecimento(MovimentoCadastro movimento) throws DAOException {
    	cadastrarHistorico(movimento, TipoHistoricoManifestacao.getTipoHistoricoManifestacao(TipoHistoricoManifestacao.ESCLARECIMENTO_OUVIDORIA_INTERESSADO));
	}

    /**
     * Notifica ao interessado a solicitação de esclarecimento.
     * 
     * @param pessoa
     * @param manifestacao
     * @param historico
     */
	private void pedidoEsclarecimentoUsuario(Pessoa pessoa, Manifestacao manifestacao, HistoricoManifestacao historico) {		
    	MailBody mail = new MailBody();
		mail.setContentType(MailBody.HTML);
		mail.setAssunto("Solicitação de Esclarecimento da Ouvidoria " + manifestacao.getNumeroAno());
		mail.setMensagem(getMensagemEsclarecimento(pessoa, manifestacao, historico));
		mail.setEmail(pessoa.getEmail());
		mail.setNome(pessoa.getNome());
		Mail.send(mail);		
	}
	
    /**
     * Monta a mensagem a ser enviada para notificar a alteração no prazo de resposta da manifestação.
     * 
     * @param pessoa
     * @param manifestacao
     * @param historico
     * @return
     */
	private String getMensagemEsclarecimento(Pessoa pessoa,	Manifestacao manifestacao, HistoricoManifestacao historico) {
		
    	String mensagem = "Prezado(a) " + pessoa.getNome() + ", <br /><br />" + 
				"Informamos que sua manifestação cadastrada sob o ano/protocolo " + manifestacao.getNumeroAno() + " nescessita de mais esclarecimentos. "+
				"<br />Os dados da manifestação cadastrada foram os seguintes:" +
				"<br /><br />- <b>Categoria do Assunto:</b>  " +
				manifestacao.getAssuntoManifestacao().getCategoriaAssuntoManifestacao().getDescricao() +
				"<br /><br />- <b>Assunto:</b>  " +
				manifestacao.getAssuntoManifestacao().getDescricao() +
				"<br /><br />- <b>Tipo da Manifestação:</b>  " +
				manifestacao.getTipoManifestacao().getDescricao() +
				"<br /><br />- <b>Título:</b>  " +
				manifestacao.getTitulo() +
				"<br /><br />- <b>Texto:</b>  " +
				manifestacao.getMensagem() +
				"<br /><br /><hr /><br />Os esclarecimentos que a ouvidoria nescessita para responder sua manifestação são os seguintes:<br /><br />" +
				historico.getResposta() +
				"<br />";

    	if (manifestacao.getInteressadoManifestacao().getCategoriaSolicitante().getId() == CategoriaSolicitante.COMUNIDADE_EXTERNA){
    		
    		InteressadoNaoAutenticado interessado = manifestacao.getInteressadoManifestacao().getDadosInteressadoManifestacao().getInteressadoNaoAutenticado();
    		
    		String linkAcesso = ParametroHelper.getInstance().getParametro(ParametrosGerais.ENDERECO_ACESSO)
    				+"/sigaa/link/public/ouvidoria/confirmarCodigoAcessoOuvidoria/"+interessado.getCodigoAcesso()+"/"+interessado.getId();
    		
    		mensagem += "<br /><br />Para responder este pedido de esclarecimento, acesse o <b>Portal Público do Sigaa -> Ouvidoria -> Responder Solicitação de Esclarecimento</b>" +
    						" e preencha o formulário com este endereço de e-mail e o seguinte código de acesso:<br /><br />" +
    						interessado.getCodigoAcesso();
    	 	mensagem += "<br /><br />Para que o código de acesso passe a ser válido é necessário acessar o link abaixo:<br />";
    		mensagem += "<a href="+linkAcesso+">"+linkAcesso+"</a> <br/><br/>";

    	} else {
    		mensagem += "<br />Para responder este pedido de esclarecimento, acesse o <b>Portal Discente / Docente -> Outros -> Ouvidoria -> Acompanhar Manifestação -> Responder Solicitação de Esclarecimento</b>";
    	}
    	
		mensagem += "<br /><br /> <i> Esta mensagem é automática e não deve ser respondida </i>";
		
		return mensagem;
	}    
    	
	/**
	 * Gera uma senha automatica que é enviada pelo e-mail para permitir o interessado da manifestação acessar a 
	 * manifestação através do Portal Público e registrar o esclarecimento.  
	 * @return
	 */
	public String geraSenhaAutomatica(){
		String senhaTemp = UFRNUtils.geraSenhaAleatoria();
		if(senhaTemp.length() > 10)
			return senhaTemp.substring(0, 10);
		else
			return senhaTemp;
	}
	
	/**
     * Esse método tem o objetivo passar pra MD5 o código de acesso do usuário para garantir que apenas a pessoa que recebeu o e-mail
     * possa verificar as manifestações
     * 
     * @param mov
     * @throws DAOException
     */
    private void confirmarCodigoAcesso(MovimentoCadastro movimento) throws NegocioException, ArqException {
		
    	validate(movimento);
		InteressadoNaoAutenticado interessado =  movimento.getObjMovimentado();		
		GenericDAO dao = null;
		
		/// Gera o hash da senha gerada pelo sistema e atualiza o campo senha que o usuário usa para se logar no sitema ///
		String senha = UFRNUtils.toMD5(interessado.getCodigoAcesso());
		interessado.setSenha(senha);
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		try{
			dao = getGenericDAO(movimento);
			
			// se o usuário confirmar a alteração de senha, confirma também o cadastro caso não tenha feito isso ainda //
			dao.updateField(InteressadoNaoAutenticado.class, interessado.getId(),"senha", interessado.getSenha());
			
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	@Override
    public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens erros = new ListaMensagens();
		
		if(SigaaListaComando.SOLICITAR_ESCLARECIMENTO.equals(mov.getCodMovimento())) {
			MovimentoCadastro movimento = (MovimentoCadastro) mov;
			HistoricoManifestacao historico = movimento.getObjMovimentado();
			
		    if(isEmpty(historico.getResposta()))
		    	erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Resposta");
		}
		
		if(SigaaListaComando.CONFIRMAR_CODIGO_ACESSO_OUVIDORIA.equals(mov.getCodMovimento())) {
			MovimentoCadastro movimento = (MovimentoCadastro) mov;
			InteressadoNaoAutenticado interessado = movimento.getObjMovimentado();
			
			if (isEmpty(interessado.getId()))
				erros.addErro("Usuário interessado inválido");
			if (isEmpty(interessado.getCodigoAcesso()))
				erros.addErro("Código de acesso inválido");
		}
		
		checkValidation(erros);
    }

}