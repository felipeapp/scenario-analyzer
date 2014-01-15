package br.ufrn.sigaa.ouvidoria.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Date;

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
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.Formatador;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.Responsavel;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ouvidoria.dominio.HistoricoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.StatusManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.TipoHistoricoManifestacao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador para operações em um histórico de manifestação pela Ouvidoria.
 * 
 * @author Bernardo
 *
 */
public class ProcessadorHistoricoManifestacaoOuvidoria extends AbstractProcessador {

    @Override
    public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		
		if(SigaaListaComando.ENVIAR_RESPOSTA_USUARIO.equals(mov.getCodMovimento())) {
		    enviarRespostaUsuario(movimento);
		}
		else if(SigaaListaComando.ENCAMINHAR_MANIFESTACAO_UNIDADE.equals(mov.getCodMovimento())) {
		    cadastrarHistoricoEncaminhamento(movimento);
		    
		    atualizarDadosManifestacao(movimento, StatusManifestacao.ENCAMINHADA_UNIDADE);
		    
		    HistoricoManifestacao historico = movimento.getObjMovimentado();
		    Responsavel responsavel = (Responsavel) movimento.getObjAuxiliar();
		    Manifestacao manifestacao = historico.getManifestacao();
		    
		    manifestacao = getGenericDAO(mov).refresh(manifestacao);
		    
		    notificarEncaminhamentoUnidade(responsavel.getServidor().getPessoa(), manifestacao, historico);
		}
		else if(SigaaListaComando.ALTERAR_PRAZO_RESPOSTA_MANIFESTACAO.equals(mov.getCodMovimento())) {
			alterarPrazoResposta(movimento);

			Responsavel responsavel = (Responsavel) movimento.getObjAuxiliar();
			HistoricoManifestacao historico = movimento.getObjMovimentado();
			Manifestacao manifestacao = historico.getManifestacao();
			
			notificarAlteracaoPrazoResposta(responsavel.getServidor().getPessoa(), manifestacao, historico);
		}
		else if(SigaaListaComando.FINALIZAR_MANIFESTACAO.equals(mov.getCodMovimento())) {
			HistoricoManifestacao historico = movimento.getObjMovimentado();
			Manifestacao manifestacao = historico.getManifestacao();
			
			if(!manifestacao.isRespondida())
				enviarRespostaUsuario(movimento);
		    
		    atualizarDadosManifestacao(movimento, StatusManifestacao.FINALIZADA);
		}
		
		return null;
    }

    /**
     * Processa o envio de resposta ao usuário.
     * 
     * @param movimento
     * @throws DAOException
     */
	private void enviarRespostaUsuario(MovimentoCadastro movimento) throws DAOException {
		cadastrarHistoricoResposta(movimento);
		
		atualizarDadosManifestacao(movimento, StatusManifestacao.RESPONDIDA);
		
		HistoricoManifestacao historico = movimento.getObjMovimentado();
		Manifestacao manifestacao = historico.getManifestacao();
		
		manifestacao = getGenericDAO(movimento).refresh(manifestacao);
		
		notificarRespostaUsuario(manifestacao.getInteressadoManifestacao().getDadosInteressadoManifestacao().getPessoaEmail(), manifestacao, historico);
	}

    /**
     * Cadastra um histórico de resposta ao manifestante.
     * 
     * @param mov
     * @throws DAOException
     */
	private void cadastrarHistoricoResposta(MovimentoCadastro mov) throws DAOException {
    	cadastrarHistorico(mov, TipoHistoricoManifestacao.getTipoHistoricoManifestacao(TipoHistoricoManifestacao.OUVIDORIA_INTERESSADO));
    }
    
	/**
	 * Cadastra um histórico para a unidade responsável por resposta.
	 * 
	 * @param mov
	 * @throws DAOException
	 */
    private void cadastrarHistoricoEncaminhamento(MovimentoCadastro mov) throws DAOException {
    	cadastrarHistorico(mov, TipoHistoricoManifestacao.getTipoHistoricoManifestacao(TipoHistoricoManifestacao.OUVIDORIA_RESPONSAVEL));
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
		
		if(SigaaListaComando.ENVIAR_RESPOSTA_USUARIO.equals(mov.getCodMovimento())) {
		    historico.setDataResposta(new Date());
		}
		
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
		
		//Se não foi solicitado anonimato no cadastro da manifestação, mas a Ouvidoria decide manter o sigilo do solicitante,
		//o atributo 'anonima' da manifestação deve ser modificado
		if(!manifestacao.isAnonimatoSolicitado() && manifestacao.isAnonima())
			getGenericDAO(mov).updateFields(Manifestacao.class, manifestacao.getId(), new String[]{ "anonima", "statusManifestacao" }, new Object[]{ true, StatusManifestacao.getStatusManifestacao(status) });
		else
			getGenericDAO(mov).updateField(Manifestacao.class, manifestacao.getId(), "statusManifestacao", StatusManifestacao.getStatusManifestacao(status));
    }
    
    /**
     * Notifica ao manifestante que sua manifestação foi respondida pela ouvidoria.
     * 
     * @param pessoa
     * @param manifestacao
     * @param historico
     */
    private void notificarRespostaUsuario(Pessoa pessoa, Manifestacao manifestacao, HistoricoManifestacao historico) {
    	MailBody mail = new MailBody();
		mail.setContentType(MailBody.HTML);
		mail.setAssunto("Resposta da Manifestação " + manifestacao.getNumeroAno());
		mail.setMensagem(getMensagemResposta(pessoa, manifestacao, historico));
		mail.setEmail(pessoa.getEmail());
		mail.setNome(pessoa.getNome());
		Mail.send(mail);
    }

    /**
     * Monta a mensagem a ser enviada ao interessado quando sua manifestação é respondida.
     * 
     * @param pessoa
     * @param manifestacao
     * @param historico
     * @return
     */
    private String getMensagemResposta(Pessoa pessoa, Manifestacao manifestacao, HistoricoManifestacao historico) {
    	String mensagem = "Prezado(a) " + pessoa.getNome() + ", <br /><br />" + 
							"Informamos que sua manifestação cadastrada sob o ano/protocolo " + manifestacao.getNumeroAno() + " foi respondida pela Ouvidoria. "+
							"<br />Os dados da manifestação cadastrada foram os seguintes:" +
							"<br /><br />- <b>Categoria do Assunto:</b>  " +
							manifestacao.getAssuntoManifestacao().getCategoriaAssuntoManifestacao().getDescricao() +
							"<br /><br />- <b>Assunto:</b>  " +
							manifestacao.getAssuntoManifestacao().getDescricao() +
							"<br /><br />- <b>Tipo da Manifestação:</b>  " +
							manifestacao.getTipoManifestacao().getDescricao() +
							"<br /><br />- <b>Título:</b>  " +
							manifestacao.getTitulo() +
							"<br /><br />- <b>Texto:</b>" +
							manifestacao.getMensagem() +
							"<br /><br /><hr /><br />A resposta dada pela ouvidoria para sua manifestação foi a seguinte:<br /><br />" +
							historico.getResposta() +
							"<br />";
		
    	mensagem += "<br /><br /> <i> Esta mensagem é automática e não deve ser respondida </i>";
		
		return mensagem;
	}
    
    /**
     * Notifica à unidade responsável o encaminhamento da manifestação.
     * 
     * @param pessoa
     * @param manifestacao
     * @param historico
     */
    private void notificarEncaminhamentoUnidade(PessoaGeral pessoa, Manifestacao manifestacao, HistoricoManifestacao historico) {
    	MailBody mail = new MailBody();
		mail.setContentType(MailBody.HTML);
		mail.setAssunto("Encaminhamento de Manifestação");
		mail.setMensagem(getMensagemEncaminhamento(pessoa, manifestacao, historico));
		mail.setEmail(pessoa.getEmail());
		mail.setNome(pessoa.getNome());
		Mail.send(mail);
    }

    /**
     * Monta a mensagem a ser enviada ao repsonsável pela unidade que recebeu a manifestação.
     * 
     * @param pessoa
     * @param manifestacao
     * @param historico
     * @return
     */
    private String getMensagemEncaminhamento(PessoaGeral pessoa, Manifestacao manifestacao, HistoricoManifestacao historico) {
    	String mensagem = "Prezado(a) " + pessoa.getNome() + ", <br /><br />" + 
							"Informamos que a Ouvidoria encaminhou para a unidade de sua responsabilidade a manifestação de número "+manifestacao.getNumeroAno() + ". O prazo para resposta dessa manifestação é o dia " + Formatador.getInstance().formatarData(historico.getPrazoResposta()) +
							".<br />Os dados da manifestação cadastrada foram os seguintes:<br/>" +
							"<br /><b>Categoria do Assunto:</b> " + manifestacao.getAssuntoManifestacao().getCategoriaAssuntoManifestacao().getDescricao() +
							"<br /><b>Assunto:</b> " + manifestacao.getAssuntoManifestacao().getDescricao() +
							"<br /><b>Tipo da Manifestação:</b> " + manifestacao.getTipoManifestacao().getDescricao() +
							"<br /><b>Título:</b> " + manifestacao.getTitulo() +
							"<br /><b>Texto:</b> " + manifestacao.getMensagem() +
							"<br /><br /><hr /><br />A mensagem enviada pela ouvidoria no momento do encaminhamento foi a seguinte:<br /><br />" +
							historico.getSolicitacao() +
							"<br />";
		
    	mensagem += "<br /><br /><br />ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA " +
   		     RepositorioDadosInstitucionais.get("siglaSigaa")+".<br />POR FAVOR, NÃO RESPONDÊ-LO. <br/><br/><br/>";

		
		return mensagem;
	}
    
    /**
     * Altera o prazo de resposta previamente definido para a manifestação.
     * 
     * @param movimento
     * @throws DAOException
     */
    private void alterarPrazoResposta(MovimentoCadastro movimento) throws DAOException {
    	HistoricoManifestacao historico = movimento.getObjMovimentado();
    	
    	getGenericDAO(movimento).updateField(HistoricoManifestacao.class, historico.getId(), "prazoResposta", historico.getPrazoResposta());
    }
    
    /**
     * Notifica ao responsável pela unidade que o prazo de resposta dela foi alterado.
     * 
     * @param pessoa
     * @param manifestacao
     * @param historico
     */
    private void notificarAlteracaoPrazoResposta(PessoaGeral pessoa, Manifestacao manifestacao, HistoricoManifestacao historico) {
    	MailBody mail = new MailBody();
		mail.setContentType(MailBody.HTML);
		mail.setAssunto("Alteração de Prazo de Resposta");
		mail.setMensagem(getMensagemAlteracaoPrazo(pessoa, manifestacao, historico));
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
    private String getMensagemAlteracaoPrazo(PessoaGeral pessoa, Manifestacao manifestacao, HistoricoManifestacao historico) {
    	String mensagem = "Prezado(a) " + pessoa.getNome() + ", <br /><br />" + 
							"Informamos que a ouvidoria modificou o prazo de resposta da manifestação cadastrada sob o número/ano " + manifestacao.getNumeroAno() +
							" para o dia " + Formatador.getInstance().formatarData(historico.getPrazoResposta()) +
							". Qualquer dúvida, favor entrar em contato com a Ouvidoria.";
		
    	mensagem += "<br /><br /> <i> Esta mensagem é automática e não deve ser respondida </i>";
		
		return mensagem;
	}

	@Override
    public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		HistoricoManifestacao historico = movimento.getObjMovimentado();
		
		if(SigaaListaComando.ENVIAR_RESPOSTA_USUARIO.equals(mov.getCodMovimento())) {
		    if(isEmpty(historico.getResposta()))
		    	erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Resposta");
		}
		else if(SigaaListaComando.ENCAMINHAR_MANIFESTACAO_UNIDADE.equals(mov.getCodMovimento())) {
			Responsavel responsavel = (Responsavel) movimento.getObjAuxiliar();
			
		    if(isEmpty(historico.getSolicitacao()))
		    	erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Mensagem");
		    if(isEmpty(historico.getPrazoResposta()))
		    	erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Prazo de Resposta");
		    else {
		    	if(historico.getPrazoResposta().getTime() < new Date().getTime())
		    		erros.addErro("Prazo de Resposta: esta data deve ser posterior a hoje.");
		    }
		    if(isEmpty(historico.getUnidadeResponsavel()))
		    	erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Unidade");
		    if(!isEmpty(historico.getUnidadeResponsavel()) && isEmpty(responsavel))
		    	erros.addMensagem(new MensagemAviso("É necessário que a unidade selecionada possua um servidor responsável cadastrado no sistema.", TipoMensagemUFRN.ERROR));
		    
		    if(erros.isErrorPresent()) { // se houver erro, reverter modificação feita para atualizar os dados da manifestação
		    	historico.getManifestacao().setAnonima(historico.getManifestacao().isAnonimatoSolicitado());
		    }
		}
		else if(SigaaListaComando.ALTERAR_PRAZO_RESPOSTA_MANIFESTACAO.equals(mov.getCodMovimento())) {
			if(historico.getPrazoResposta().getTime() < new Date().getTime())
	    		erros.addMensagem(MensagensArquitetura.DATA_POSTERIOR_A, "Novo Prazo de Resposta", "hoje");
		}
		
		checkValidation(erros);
    }

}
