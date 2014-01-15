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
import br.ufrn.arq.util.Formatador;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.Responsavel;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ouvidoria.dominio.HistoricoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.StatusManifestacao;

/**
 * Processador para operações em um histórico de manifestação pela Unidade Responsável e Designado para Resposta.
 * 
 * @author Bernardo
 *
 */
public class ProcessadorHistoricoManifestacaoResponsavel extends AbstractProcessador {

    @Override
    public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
	
		if(SigaaListaComando.ENVIAR_RESPOSTA_OUVIDORIA.equals(mov.getCodMovimento())) {
		    MovimentoCadastro movimento = (MovimentoCadastro) mov;
		    
		    cadastrarRespostaOuvidoria(movimento);
		    
		    atualizarStatusManifestacao(movimento, StatusManifestacao.PARECER_CADASTRADO);
		}
		if(SigaaListaComando.ENVIAR_RESPOSTA_UNIDADE.equals(mov.getCodMovimento())) {
		    MovimentoCadastro movimento = (MovimentoCadastro) mov;
		    HistoricoManifestacao historico = movimento.getObjMovimentado();
		    Responsavel responsavel = (Responsavel) movimento.getObjAuxiliar();
	    	Manifestacao manifestacao = historico.getManifestacao();
		    
		    manifestacao = getGenericDAO(mov).refresh(manifestacao);
		    
		    cadastrarRespostaUnidade(movimento);
		    
		    atualizarStatusManifestacao(movimento, StatusManifestacao.AGUARDANDO_PARECER);
		    
		    notificarRespostaUnidade(responsavel.getServidor().getPessoa(), manifestacao, historico);
		}
		
		return null;
    }
    
    /**
     * Envia resposta para a ouvidoria.
     * 
     * @param mov
     * @throws DAOException
     */
    private void cadastrarRespostaOuvidoria(MovimentoCadastro mov) throws DAOException {
		HistoricoManifestacao historico = mov.getObjMovimentado();
		
		historico.setDataResposta(new Date());
		
		getGenericDAO(mov).updateFields(HistoricoManifestacao.class, historico.getId(), 
			new String[] {"resposta", "dataResposta"}, new Object[] {historico.getResposta(), new Date()});
    }
    
    /**
     * Envia resposta para a unidade responsável.
     * 
     * @param mov
     * @throws DAOException
     */
    private void cadastrarRespostaUnidade(MovimentoCadastro mov) throws DAOException {
		HistoricoManifestacao historico = mov.getObjMovimentado();
		
		historico.setDataRespostaUnidade(new Date());
		
		getGenericDAO(mov).updateFields(HistoricoManifestacao.class, historico.getId(), 
			new String[] {"respostaUnidade", "dataRespostaUnidade"}, new Object[] {historico.getRespostaUnidade(), new Date()});
    }

    /**
     * Atualiza o status da manifestação de acordo com o informado.
     * 
     * @param mov
     * @param status
     * @throws DAOException
     */
    private void atualizarStatusManifestacao(MovimentoCadastro mov, int status) throws DAOException {
		HistoricoManifestacao historico = mov.getObjMovimentado();
		Manifestacao manifestacao = historico.getManifestacao();
		
		getGenericDAO(mov).updateField(Manifestacao.class, manifestacao.getId(), "statusManifestacao", StatusManifestacao.getStatusManifestacao(status));
    }
    
    /**
     * Notifica à unidade responsável a resposta da manifestação.
     * 
     * @param pessoa
     * @param manifestacao
     * @param historico
     */
    private void notificarRespostaUnidade(PessoaGeral pessoa, Manifestacao manifestacao, HistoricoManifestacao historico) {
    	MailBody mail = new MailBody();
		mail.setContentType(MailBody.HTML);
		mail.setAssunto("Resposta de Manifestação");
		mail.setMensagem(getMensagemRespostaUnidade(pessoa, manifestacao, historico));
		mail.setEmail(pessoa.getEmail());
		mail.setNome(pessoa.getNome());
		Mail.send(mail);
    }
    
    /**
     * Monta a mensagem a ser enviada ao repsonsável pela unidade que recebeu a resposta.
     * 
     * @param pessoa
     * @param manifestacao
     * @param historico
     * @return
     */
    private String getMensagemRespostaUnidade(PessoaGeral pessoa, Manifestacao manifestacao, HistoricoManifestacao historico) {
    	String mensagem = "Prezado(a) " + pessoa.getNome() + ", <br /><br />" + 
							"Informamos que a pessoa designada a responder a manifestação sob número/ano: " + manifestacao.getNumeroAno() + " o fez, e a manifestação agora aguarda seu parecer para retornar à Ouvidoria." +
							"<br />O prazo para resposta dessa manifestação é o dia " + Formatador.getInstance().formatarData(historico.getPrazoResposta()) +
							".<br />A resposta enviada pela pessoa designada foi a seguinte:<br /><br />" +
							historico.getRespostaUnidade() +
							"<br />";
		
    	mensagem += "<br /><br /> <i> Esta mensagem é automática e não deve ser respondida </i>";
		
		return mensagem;
	}

    @Override
    public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		HistoricoManifestacao historico = movimento.getObjMovimentado();
		
		if(SigaaListaComando.ENVIAR_RESPOSTA_OUVIDORIA.equals(mov.getCodMovimento())) {
		    if(isEmpty(historico.getResposta()))
		    	erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Resposta");
		}
		if(SigaaListaComando.ENVIAR_RESPOSTA_UNIDADE.equals(mov.getCodMovimento())) {
			if(isEmpty(historico.getRespostaUnidade()))
		    	erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Resposta");
		}
		
		checkValidation(erros);
    }

}
