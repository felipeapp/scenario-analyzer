package br.ufrn.sigaa.ouvidoria.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;

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
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.Formatador;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ouvidoria.dominio.DelegacaoUsuarioResposta;
import br.ufrn.sigaa.ouvidoria.dominio.HistoricoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.StatusManifestacao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador para tratar processamento de delegação de uma pessoa para responder uma manifestação.
 * 
 * @author Bernardo
 *
 */
public class ProcessadorDelegacaoUsuarioResposta extends AbstractProcessador {

    @Override
    public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		
		if(SigaaListaComando.DESIGNAR_PESSOA_MANIFESTACAO.equals(mov.getCodMovimento())) {
		    MovimentoCadastro movimento = (MovimentoCadastro) mov;
		    
		    designarPessoaResposta(movimento);
		    
		    atualizarStatusManifestacao(movimento, StatusManifestacao.DESIGNADA_RESPONSAVEL);
		    
		    HistoricoManifestacao historico = (HistoricoManifestacao) movimento.getObjAuxiliar();
		    DelegacaoUsuarioResposta delegacao = movimento.getObjMovimentado();
		    Manifestacao manifestacao = historico.getManifestacao();
		    
		    notificarDesignacao(delegacao.getPessoa(), manifestacao, historico, mov);
		}
		else if(SigaaListaComando.DESIGNAR_OUTRA_PESSOA_MANIFESTACAO.equals(mov.getCodMovimento())) {
		    MovimentoCadastro movimento = (MovimentoCadastro) mov;
		    
		    Pessoa p = cancelarDesignacaoAtiva(movimento);
		    
		    designarPessoaResposta(movimento);
		    
		    HistoricoManifestacao historico = (HistoricoManifestacao) movimento.getObjAuxiliar();
		    DelegacaoUsuarioResposta delegacao = movimento.getObjMovimentado();
		    Manifestacao manifestacao = historico.getManifestacao();
		    
		    notificarCancelamentoDesignacao(p, manifestacao, mov);
		    notificarDesignacao(delegacao.getPessoa(), manifestacao, historico, mov);
		}
		
		return null;
    }

    /**
     * Cancela a designação ativa existente no histórico da manifestação.
     * 
     * @param mov
     * @return
     * @throws DAOException
     */
	private Pessoa cancelarDesignacaoAtiva(MovimentoCadastro mov) throws DAOException {
    	HistoricoManifestacao historico = (HistoricoManifestacao) mov.getObjAuxiliar();
    	DelegacaoUsuarioResposta delegacao = recuperarDelegacaoAtiva(historico);
    	
    	getGenericDAO(mov).updateField(DelegacaoUsuarioResposta.class, delegacao.getId(), "ativo", false);
    	
    	return isEmpty(delegacao) ? null : delegacao.getPessoa();
	}

	/**
	 * Recupera a delegação ativa associada ao histórico.
	 * 
	 * @param historico
	 * @return
	 */
	private DelegacaoUsuarioResposta recuperarDelegacaoAtiva(HistoricoManifestacao historico) {
		DelegacaoUsuarioResposta delegacao = null;
    	
    	for (DelegacaoUsuarioResposta d : historico.getDelegacoesUsuarioResposta()) {
    		if(d.isAtivo()) {
    			delegacao = d;
    			break;
    		}
    	}
		return delegacao;
	}

	/**
	 * Cadastra a designação de pessoa para resposta.
	 * 
	 * @param mov
	 * @throws DAOException
	 */
	private void designarPessoaResposta(MovimentoCadastro mov) throws DAOException {
		DelegacaoUsuarioResposta delegacao = mov.getObjMovimentado();
		HistoricoManifestacao historico = (HistoricoManifestacao) mov.getObjAuxiliar();
		
		delegacao.setHistoricoManifestacao(historico);
		
		getGenericDAO(mov).create(delegacao);
    }
    
	/**
	 * Atualiza o status da manifestação de acordo com o passado.
	 * 
	 * @param mov
	 * @param status
	 * @throws DAOException
	 */
    private void atualizarStatusManifestacao(MovimentoCadastro mov, int status) throws DAOException {
		HistoricoManifestacao historico = (HistoricoManifestacao) mov.getObjAuxiliar();
		Manifestacao manifestacao = historico.getManifestacao();
		
		getGenericDAO(mov).updateField(Manifestacao.class, manifestacao.getId(), "statusManifestacao", StatusManifestacao.getStatusManifestacao(status));
		getGenericDAO(mov).updateField(HistoricoManifestacao.class, historico.getId(), "lido", false);
    }
    
    /**
     * Notifica à pessoa designada o cadastro da designação.
     * 
     * @param pessoa
     * @param manifestacao
     * @param historico
     * @param mov
     * @throws DAOException
     */
    private void notificarDesignacao(PessoaGeral pessoa, Manifestacao manifestacao, HistoricoManifestacao historico, Movimento mov) throws DAOException {
    	pessoa = getGenericDAO(mov).findByPrimaryKey(pessoa.getId(), Pessoa.class, "id", "nome", "email");
    	
    	MailBody mail = new MailBody();
		mail.setContentType(MailBody.HTML);
		mail.setAssunto("Designação de Manifestação Para Resposta");
		mail.setMensagem(getMensagemDesignacao(pessoa, manifestacao, historico));
		mail.setEmail(pessoa.getEmail());
		mail.setNome(pessoa.getNome());
		Mail.send(mail);
    }

    /**
     * Monta a mensagem a ser enviada à pessoa que foi designada para responder uma manifestação.
     * 
     * @param pessoa
     * @param manifestacao
     * @param historico
     * @return
     */
    private String getMensagemDesignacao(PessoaGeral pessoa, Manifestacao manifestacao, HistoricoManifestacao historico) {
    	String mensagem = "Prezado(a) " + pessoa.getNome() + ", <br /><br />" + 
							"Informamos que sua unidade de lotação o(a) designou para responder a manifestação de número "+ manifestacao.getNumeroAno() +". O prazo para resposta dessa manifestação é o dia " + Formatador.getInstance().formatarData(historico.getPrazoResposta()) +
							".<br />Os dados da manifestação cadastrada foram os seguintes:" +
							"<br /><br /><b>Categoria do Assunto:</b> " + manifestacao.getAssuntoManifestacao().getCategoriaAssuntoManifestacao().getDescricao() +
							"<br /><b>Assunto:</b> " +	manifestacao.getAssuntoManifestacao().getDescricao() +
							"<br /><b>Tipo da Manifestação:</b>  " + manifestacao.getTipoManifestacao().getDescricao() +
							"<br /><b>Título:</b>  " + manifestacao.getTitulo() +
							"<br /><b>Texto:</b> " +	manifestacao.getMensagem() +
							"<br /><br /><hr /><br />A mensagem enviada pela ouvidoria no momento do encaminhamento para a unidade foi a seguinte:<br /><br />" +
							historico.getSolicitacao() +
							"<br />";
		
    	mensagem += "<br /><br /><br />ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA " +
   		     		RepositorioDadosInstitucionais.get("siglaSigaa")+".<br />POR FAVOR, NÃO RESPONDÊ-LO. <br/><br/><br/>";
		
		return mensagem;
	}
    
    /**
     * Notifica à pessoa designada o cancelamento da designação
     * 
     * @param pessoa
     * @param manifestacao
     * @param mov
     * @throws DAOException
     */
    private void notificarCancelamentoDesignacao(Pessoa pessoa, Manifestacao manifestacao, Movimento mov) throws DAOException {
    	pessoa = getGenericDAO(mov).findByPrimaryKey(pessoa.getId(), Pessoa.class, "id", "nome", "email");
    	
    	MailBody mail = new MailBody();
		mail.setContentType(MailBody.HTML);
		mail.setAssunto("Cancelamento de Designação de Resposta");
		mail.setMensagem(getMensagemCancelamentoDesignacao(pessoa, manifestacao));
		mail.setEmail(pessoa.getEmail());
		mail.setNome(pessoa.getNome());
		Mail.send(mail);
    }

    /**
     * Monta a mensagem a ser enviada quando uma designação de resposta é cancelada.
     * 
     * @param pessoa
     * @param manifestacao
     * @return
     */
    private String getMensagemCancelamentoDesignacao(Pessoa pessoa, Manifestacao manifestacao) {
    	String mensagem = "Prezado(a) " + pessoa.getNome() + ", <br /><br />" + 
							"Informamos que sua unidade de lotação cancelou sua designação de resposta para a manifestação de número/ano " + manifestacao.getNumeroAno() +
							"<br />Qualquer dúvida, favor se dirigir ao responsável por sua unidade.";
		
    	mensagem += "<br /><br /> <i> Esta mensagem é automática e não deve ser respondida </i>";
		
		return mensagem;
	}

	@Override
    public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		DelegacaoUsuarioResposta delegacao = movimento.getObjMovimentado();
		HistoricoManifestacao historico = (HistoricoManifestacao) movimento.getObjAuxiliar();
		
		if(isEmpty(delegacao.getPessoa()))
		    erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Pessoa");
		if(isEmpty(historico))
		    erros.addErro("Erro ao recuperar os dados do histórico da manifestação. Por favor reinicie a operação.");
		
		if(SigaaListaComando.DESIGNAR_OUTRA_PESSOA_MANIFESTACAO.equals(mov.getCodMovimento())) {
			DelegacaoUsuarioResposta delegacaoAtiva = recuperarDelegacaoAtiva(historico);
			
			if(delegacaoAtiva.getPessoa().getId() == delegacao.getPessoa().getId())
				erros.addErro("Não é possível designar a manifestação para a mesma pessoa.");
		}
		
		checkValidation(erros);
    }

}
