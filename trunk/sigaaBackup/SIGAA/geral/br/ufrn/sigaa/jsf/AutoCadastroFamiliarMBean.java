/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 22/09/2011
 *
 */
package br.ufrn.sigaa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.validateMaxLength;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.interfaces.PermissaoUsuarioSistemaRemoteService;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.negocio.MovimentoAutoCadastroDiscente;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Managed bean para auto-cadastro de pais de discentes no nível médio
 * 
 * @author Arlindo Rodrigues
 *
 */
@Scope("request") @Component("cadastroFamiliar")
public class AutoCadastroFamiliarMBean extends SigaaAbstractController<Usuario> {
	
	/** Usuario do discente informado */
	private Usuario discente;
	/** Indica se o discente é estrangeiro */
	private boolean internacionalDiscente;
	/** Indica se o familiar é estrangeiro */
	private boolean internacionalPai;		
	
	/**
	 * Construtor padrão
	 */
	public AutoCadastroFamiliarMBean() {
		obj = new Usuario();
		
		discente = new Usuario();
		discente.setDiscente(new Discente());
		discente.getDiscente().setNivel(NivelEnsino.MEDIO);
	}	
	
	/** 
	 * Responsável por Cadastrar o usuário dos pais do discente
	 * Método chamado pela seguinte JSP:
	 * <ul> 
	 * 	 <li>/public/cadastro/pais.jsp</li>
	 * </ul> 
	 * @return 
	 */
	public String cadastrar() {

		if (internacionalPai)
			obj.getPessoa().setCpf_cnpj(null);
		
		if (internacionalDiscente){
			discente.getPessoa().setCpf_cnpj(null);
			discente.getPessoa().getIdentidade().setNumero(null);
		}
		
		MovimentoAutoCadastroDiscente mov = new MovimentoAutoCadastroDiscente();
		mov.setUsr(obj);
		mov.setObjAuxiliar(discente);
		mov.setIp(getCurrentRequest().getRemoteAddr());
		mov.setRequest(getCurrentRequest());
		mov.setRegistroAcessoPublico(getAcessoPublico());
		mov.setCodMovimento(SigaaListaComando.AUTO_CADASTRO_FAMILIAR);
		mov.setUsuarioLogado(new Usuario());
		mov.getUsuarioLogado().setLogin("guest");
		mov.setInternacional(internacionalPai);

		try {
			prepareMovimento(SigaaListaComando.AUTO_CADASTRO_FAMILIAR);
			validar();
			
			if (hasErrors())
				return null;

			obj = executeWithoutClosingSession(mov, getCurrentRequest());

			PermissaoUsuarioSistemaRemoteService service = getMBean("permissaoUsuarioSistemaInvoker");
			service.configurarPermissaoUsuario(obj.getId(), null, 'D');
			service.configurarPermissaoUsuario(obj.getId(), Sistema.SIGAA, 'G');
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			e.printStackTrace();
			return null;
		}
		return redirectJSF("/sigaa/verTelaLogin.do?discente=sucesso");

	}	
	
	/**
	 * Responsável por validar os campos obrigatórios para o cadastro de Discentes.
	 * 
	 * @return
	 */
	private boolean validar() {

		if (discente.getPessoa().getNome() == null
				|| "".equals(discente.getPessoa().getNome().trim()))
			addMensagemErro("É obrigatório informar o nome do aluno.");

		if (discente.getDiscente().getMatricula() == null
				|| discente.getDiscente().getMatricula() <= 0) 
			addMensagemErro("É obrigatório informar a matrícula.");

		if (discente.getPessoa().getDataNascimento() == null) 
			addMensagemErro("É obrigatório informar a data de nascimento.");

		if (!internacionalDiscente) {
			Long cpf  = discente.getPessoa().getCpf_cnpj();
			ValidatorUtil.validateCPF_CNPJ(cpf == null ? 0 : cpf, "CPF do Aluno", erros);
			validateRequired(discente.getPessoa().getIdentidade(), "RG", erros);
		} else {
			validateRequired(discente.getPessoa().getPassaporte(), "Passaporte do Aluno", erros);
			validateMaxLength(discente.getPessoa().getPassaporte(), 20, "Passaporte do Aluno", erros);
		}

		if (discente.getDiscente().getAnoIngresso() == null
				|| discente.getDiscente().getAnoIngresso() <= 0) 
			addMensagemErro("É obrigatório informar o ano de ingresso.");
		
		if (obj.getPessoa().getNome() == null
				|| "".equals(obj.getPessoa().getNome().trim())) 
			addMensagemErro("É obrigatório informar o nome do familiar.");
		
		if (!internacionalPai) {
			Long cpf  = obj.getPessoa().getCpf_cnpj();
			ValidatorUtil.validateCPF_CNPJ(cpf == null ? 0 : cpf, "CPF do Familiar", erros);
		} else {
			validateRequired(obj.getPessoa().getPassaporte(), "Passaporte do Familiar", erros);
			validateMaxLength(obj.getPessoa().getPassaporte(), 20, "Passaporte do Familiar", erros);
		}
		
		if (obj.getEmail() == null || "".equals(obj.getEmail().trim())) 
			addMensagemErro("É obrigatório informar o e-mail.");

		if (obj.getLogin() == null || "".equals(obj.getLogin().trim())) 
			addMensagemErro("É obrigatório informar o login.");
		
		if (obj.getLogin() != null && obj.getLogin().length() > 20) 
			addMensagemErro("O login deve conter no máximo 20 caracteres.");

		if (obj.getSenha() == null || "".equals(obj.getSenha().trim())) 
			addMensagemErro("É obrigatório informar a senha.");

		if (obj.getSenha() != null && obj.getConfirmaSenha() != null
				&& !obj.getSenha().equals(obj.getConfirmaSenha())) 
			addMensagemErro("A senha não confere com a confirmação.");
		
		obj.getPessoa().setNome(StringUtils.removeEspacosRepetidos(obj.getNome()));
		discente.getPessoa().setNome(StringUtils.removeEspacosRepetidos(discente.getNome()));
		
		return erros.isEmpty();
	}	
	
	/** 
	 * Responsável por cancelar o cadastro de um discente
	 * Método chamado pela seguinte JSP:
	 * <ul> 
	 * 		<li>/public/cadastro/pais.jsp</li>
	 * </ul>  
	 */
	@Override
	public String cancelar() {
		return redirect("/sigaa");
	}

	public Usuario getDiscente() {
		return discente;
	}

	public void setDiscente(Usuario discente) {
		this.discente = discente;
	}

	public boolean isInternacionalDiscente() {
		return internacionalDiscente;
	}

	public void setInternacionalDiscente(boolean internacionalDiscente) {
		this.internacionalDiscente = internacionalDiscente;
	}

	public boolean isInternacionalPai() {
		return internacionalPai;
	}

	public void setInternacionalPai(boolean internacionalPai) {
		this.internacionalPai = internacionalPai;
	}
}
