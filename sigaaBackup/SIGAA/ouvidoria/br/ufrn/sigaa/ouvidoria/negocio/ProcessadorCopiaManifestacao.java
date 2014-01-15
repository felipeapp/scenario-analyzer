package br.ufrn.sigaa.ouvidoria.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.Responsavel;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ouvidoria.dominio.AcompanhamentoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.HistoricoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;

/**
 * Processador para tratar os processos de c�pia de uma manifesta��o
 * 
 * @author Bernardo
 *
 */
public class ProcessadorCopiaManifestacao extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		if(mov.getCodMovimento().equals(SigaaListaComando.ENVIAR_COPIA_MANIFESTACAO)) {
			MovimentoCadastro movimento = (MovimentoCadastro) mov;
			
			HistoricoManifestacao historico = movimento.getObjMovimentado();
			@SuppressWarnings("unchecked")
			Collection<Responsavel> responsaveis = (Collection<Responsavel>) movimento.getObjAuxiliar();
			
			copiarManifestacao(historico, responsaveis, mov);
			
			notificarCopias(historico, responsaveis);
		}
		
		return null;
	}
	
	/**
	 * Persiste as c�pias da manifesta��o.
	 * 
	 * @param historico
	 * @param responsaveis
	 * @param mov
	 * @throws DAOException
	 */
	private void copiarManifestacao(HistoricoManifestacao historico, Collection<Responsavel> responsaveis, Movimento mov) throws DAOException {
		PessoaDao dao = getDAO(PessoaDao.class, mov);
		
		try {
			AcompanhamentoManifestacao acompanhamento = null;
			
			for (Responsavel responsavel : responsaveis) {
				Long cpf = responsavel.getServidor().getPessoa().getCpf_cnpj();
				
				acompanhamento = new AcompanhamentoManifestacao();
				
				if(isNotEmpty(responsavel.getUnidade())) {
					Unidade u = new Unidade(responsavel.getUnidade().getId());
					acompanhamento.setUnidadeResponsabilidade(u);
				}
				else {
					acompanhamento.setPessoa(dao.findByCpf(cpf));
				}
				acompanhamento.setManifestacao(historico.getManifestacao());
				
				dao.createNoFlush(acompanhamento);
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Notifica o cadastro do acompanhamento �s pessoas que receberam a c�pia.
	 * 
	 * @param historico
	 * @param responsaveis
	 */
	private void notificarCopias(HistoricoManifestacao historico, Collection<Responsavel> responsaveis) {
		for (Responsavel responsavel : responsaveis) {
			PessoaGeral p = responsavel.getServidor().getPessoa();
			if(isNotEmpty(responsavel.getUnidade())) {
				notificarResponsavelUnidade(p, historico);
			}
			else {
				notificarPessoa(p, historico);
			}
		}
	}

	/**
	 * Notifica um respons�vel por unidade que a ouvidoria copiou uma manifesta��o para ele.
	 * 
	 * @param pessoa
	 * @param historico
	 */
	private void notificarResponsavelUnidade(PessoaGeral pessoa, HistoricoManifestacao historico) {
		Manifestacao manifestacao = historico.getManifestacao();
		MailBody mail = new MailBody();
		mail.setContentType(MailBody.HTML);
		mail.setAssunto("Acompanhamento de Manifesta��o da Ouvidoria - " + manifestacao.getNumeroAno());
		mail.setMensagem(getMensagemResponsavelUnidade(pessoa, manifestacao, historico));
		mail.setEmail(pessoa.getEmail());
		mail.setNome(pessoa.getNome());
		Mail.send(mail);
	}
	
	/**
	 * Monta a mensagem a ser enviada ao respons�vel de uma unidade que recebe uma c�pia de manifesta��o.
	 * 
	 * @param pessoa
	 * @param manifestacao
	 * @param historico
	 * @return
	 */
	private String getMensagemResponsavelUnidade(PessoaGeral pessoa, Manifestacao manifestacao, HistoricoManifestacao historico) {
		String mensagem = "Prezado(a) " + pessoa.getNome() + ", <br /><br />" + 
							"Informamos que a Ouvidoria, ao encaminhar a manifesta��o de n�mero "+ manifestacao.getNumeroAno() +" para a unidade respons�vel por resposta, julgou importante enviar uma c�pia de acompanhamento da mesma para sua unidade de responsabilidade." +
							"<br />Os dados da manifesta��o cadastrada foram os seguintes:" +
							"<br /><br /><b>Categoria do Assunto:</b> " + manifestacao.getAssuntoManifestacao().getCategoriaAssuntoManifestacao().getDescricao() +
							"<br /><b>Assunto:</b> " + manifestacao.getAssuntoManifestacao().getDescricao() +
							"<br /><b>Tipo da Manifesta��o:</b> " +	manifestacao.getTipoManifestacao().getDescricao() +
							"<br /><b>T�tulo:</b> " + manifestacao.getTitulo() +
							"<br /><b>Texto:</b> " + manifestacao.getMensagem() +
							"<br /><br /><hr /><br />A mensagem enviada pela ouvidoria no momento do encaminhamento para a unidade respons�vel foi a seguinte:<br /><br />" +
							historico.getSolicitacao() +
							"<br />";
		
		mensagem += "<br /><br /><br />ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA " +
		            RepositorioDadosInstitucionais.get("siglaSigaa")+".<br />POR FAVOR, N�O RESPOND�-LO. <br/><br/><br/>";
		
		return mensagem;
	}

	/**
	 * Notifica uma pessoa que a ouvidoria cadastrou uma c�pia de manifesta��o.
	 * 
	 * @param pessoa
	 * @param historico
	 */
	private void notificarPessoa(PessoaGeral pessoa, HistoricoManifestacao historico) {
		Manifestacao manifestacao = historico.getManifestacao();
		MailBody mail = new MailBody();
		mail.setContentType(MailBody.HTML);
		mail.setAssunto("Acompanhamento de Manifesta��o da Ouvidoria - " + manifestacao.getNumeroAno());
		mail.setMensagem(getMensagemPessoa(pessoa, manifestacao, historico));
		mail.setEmail(pessoa.getEmail());
		mail.setNome(pessoa.getNome());
		Mail.send(mail);
	}

	/**
	 * Monta a mensagem a ser recebida pela pessoa que recebeu a c�pia da manifesta��o.
	 * 
	 * @param pessoa
	 * @param manifestacao
	 * @param historico
	 * @return
	 */
	private String getMensagemPessoa(PessoaGeral pessoa, Manifestacao manifestacao, HistoricoManifestacao historico) {
		String mensagem = "Prezado(a) " + pessoa.getNome() + ", <br /><br />" + 
							"Informamos que a Ouvidoria, ao encaminhar a manifesta��o de n�mero "+ manifestacao.getNumeroAno() +" para a unidade respons�vel por resposta, julgou importante enviar uma c�pia da mesma para seu acompanhamento." +
							"<br />Os dados da manifesta��o cadastrada foram os seguintes:" +
							"<br /><br /><b>Categoria do Assunto:</b> " + manifestacao.getAssuntoManifestacao().getCategoriaAssuntoManifestacao().getDescricao() +
							"<br /><b>Assunto:</b> " + manifestacao.getAssuntoManifestacao().getDescricao() +
							"<br /><b>Tipo da Manifesta��o:</b> " + manifestacao.getTipoManifestacao().getDescricao() +
							"<br /><b>T�tulo:</b> " + manifestacao.getTitulo() +
							"<br /><b>Texto:</b> " + manifestacao.getMensagem() +
							"<br /><br /><hr /><br />A mensagem enviada pela ouvidoria no momento do encaminhamento para a unidade respons�vel foi a seguinte:<br /><br />" +
							historico.getSolicitacao() +
							"<br />";
		
		mensagem += "<br /><br /><br />ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA " +
		            RepositorioDadosInstitucionais.get("siglaSigaa")+".<br />POR FAVOR, N�O RESPOND�-LO. <br/><br/><br/>";
		
		return mensagem;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

}
