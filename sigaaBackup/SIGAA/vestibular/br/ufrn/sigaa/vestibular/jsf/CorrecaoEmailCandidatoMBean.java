/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 18/08/2010
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.vestibular.PessoaVestibularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;
import br.ufrn.sigaa.vestibular.negocio.MovimentoPessoaVestibular;

/**
 * Controlador para efetuar correções nos e-mails dos candidatos inscritos no vestibular.
 * 
 * @author Leonardo Campos
 *
 */
@Component
@Scope("request")
public class CorrecaoEmailCandidatoMBean extends SigaaAbstractController<PessoaVestibular> {

	/** Mensagem de erro de validação do CPF. */
	private String erroCPF;
	
	/** Indica se deve exibir o painel para digitação do CPF. */
	private boolean exibirPainel;
	
	/** Construtor padrão.  */
	public CorrecaoEmailCandidatoMBean() {
		initObj();
	}

	private void initObj() {
		obj = new PessoaVestibular();		
	}
	
	/**
	 * Checa as permissões de acesso, popula as informações necessárias e encaminha 
	 * o usuário para o início do caso de uso.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/menus/candidatos.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException{
		checkRole(SigaaPapeis.VESTIBULAR);
		initObj();
		exibirPainel = true;
		return formCorrecaoEmail();
	}

	/** Redireciona o usuário para o formulário de correção de email.<br/>Método não invocado por JSP´s.
	 * @return
	 */
	public String formCorrecaoEmail() {
		return forward("/vestibular/correcao_email_candidato.jsp");
	}
	
	/** Submete o CPF digitado no painel. O MBean realiza uma busca por pessoas já cadastradas
	 * que possuam o mesmo CPF e retorna os dados para o formulário.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/vestibular/correcao_email_candidato.jsp</li>
	 *	</ul>
	 * @param e
	 * @throws ArqException
	 */
	public void submeterCPF(ActionEvent e) throws ArqException {

		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateCPF_CNPJ(obj.getCpf_cnpj(), "CPF", erros);
		if (!erros.isEmpty()) {
			erroCPF = "Por favor, informe um CPF válido";
			return;
		}
		PessoaVestibularDao dao = getDAO(PessoaVestibularDao.class);
		prepareMovimento(SigaaListaComando.ATUALIZAR_EMAIL_CANDIDATO);
		PessoaVestibular pessoa = dao.findByCPF(obj.getCpf_cnpj());

		if ( pessoa != null ) {
			obj = pessoa;
			exibirPainel = false;
		} else {
			erroCPF = "Não foi localizado candidato com o CPF informado.";
		}
		
	}
	
	/** Chama o processador para atualizar apenas a informação do email do candidato.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/correcao_email_candidato.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String atualizarEmail() throws ArqException{
		
		ValidatorUtil.validateEmailRequired(obj.getEmail(), "E-Mail", erros);
		
		if(hasErrors())
			return formCorrecaoEmail();
		
		PessoaVestibularDao dao = getDAO(PessoaVestibularDao.class);
		PessoaVestibular pessoa = dao.findByCPF(obj.getCpf_cnpj());
		if (pessoa.getEmail() != null && pessoa.getEmail().equalsIgnoreCase(obj.getEmail())) {
			addMensagemErro("Informe um E-Mail diferente do que já está cadastrado na base de dados para efetuar a atualização.");
			return formCorrecaoEmail();
		} else {
			MovimentoPessoaVestibular mov = new MovimentoPessoaVestibular();
			mov.setPessoaVestibular(obj);
			mov.setCodMovimento(SigaaListaComando.ATUALIZAR_EMAIL_CANDIDATO);
			try {
				execute(mov);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return formCorrecaoEmail();
			}
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			return cancelar();
		}
		
	}

	public String getErroCPF() {
		return erroCPF;
	}

	public void setErroCPF(String erroCPF) {
		this.erroCPF = erroCPF;
	}

	public boolean isExibirPainel() {
		return exibirPainel;
	}

	public void setExibirPainel(boolean exibirPainel) {
		this.exibirPainel = exibirPainel;
	}
}
