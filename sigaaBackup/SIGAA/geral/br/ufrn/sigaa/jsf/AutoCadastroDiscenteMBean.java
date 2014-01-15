/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 27/11/2006
 *
 */
package br.ufrn.sigaa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.validateMaxLength;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.interfaces.PermissaoUsuarioSistemaRemoteService;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.negocio.MovimentoAutoCadastroDiscente;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Managed bean para auto-cadastro de discentes
 *
 * @author David Ricardo
 *
 */
@Scope("request") @Component("cadastroDiscente")
public class AutoCadastroDiscenteMBean extends SigaaAbstractController<Object> {

	private Usuario usuario;
	
	private boolean internacional;
	
	private static SelectItem[] niveisEnsinoCombo = new SelectItem[] {
		new SelectItem(NivelEnsino.INFANTIL, NivelEnsino.getDescricao(NivelEnsino.INFANTIL)),
		new SelectItem(NivelEnsino.FORMACAO_COMPLEMENTAR , NivelEnsino.getDescricao(NivelEnsino.FORMACAO_COMPLEMENTAR)),
		new SelectItem(NivelEnsino.MEDIO , NivelEnsino.getDescricao(NivelEnsino.MEDIO)),
		new SelectItem(NivelEnsino.GRADUACAO , NivelEnsino.getDescricao(NivelEnsino.GRADUACAO)),
		new SelectItem(NivelEnsino.LATO , NivelEnsino.getDescricao(NivelEnsino.LATO)),
		new SelectItem(NivelEnsino.MESTRADO , NivelEnsino.getDescricao(NivelEnsino.MESTRADO)),
		new SelectItem(NivelEnsino.DOUTORADO , NivelEnsino.getDescricao(NivelEnsino.DOUTORADO)) ,
		new SelectItem(NivelEnsino.TECNICO , NivelEnsino.getDescricao(NivelEnsino.TECNICO)) ,
		new SelectItem(NivelEnsino.RESIDENCIA , NivelEnsino.getDescricao(NivelEnsino.RESIDENCIA))
	};
	
	public AutoCadastroDiscenteMBean() {
		usuario = new Usuario();
		usuario.setDiscente(new Discente());
		usuario.getDiscente().setNivel('G');
	}

	/** 
	 * Responsável por Cadastrar discente
	 * Método chamado pela seguinte JSP:
	 * <ul> 
	 * 		<li>/public/cadastro/discente.jsp</li>
	 * </ul> 
	 * @return 
	 */
	public String cadastrar() {

		if (usuario.getId() != 0) {
			addMensagemErro("Reinicie os procedimentos.");
			return cancelar();
		}
		
		if (internacional) {
			usuario.getPessoa().setCpf_cnpj(null);
			usuario.getPessoa().getIdentidade().setNumero(null);
		}
		
		MovimentoAutoCadastroDiscente mov = new MovimentoAutoCadastroDiscente();
		mov.setUsr(usuario);
		mov.setIp(getCurrentRequest().getRemoteAddr());
		mov.setRequest(getCurrentRequest());
		mov.setRegistroAcessoPublico(getAcessoPublico());
		mov.setCodMovimento(SigaaListaComando.AUTO_CADASTRO_DISCENTE);
		mov.setUsuarioLogado(new Usuario());
		mov.getUsuarioLogado().setLogin("guest");
		mov.setInternacional(internacional);

		try {
			prepareMovimento(SigaaListaComando.AUTO_CADASTRO_DISCENTE);
			validar();
			
			if (hasErrors())
				return null;

			usuario = executeWithoutClosingSession(mov, getCurrentRequest());

			PermissaoUsuarioSistemaRemoteService service = getMBean("permissaoUsuarioSistemaInvoker");
			service.configurarPermissaoUsuario(usuario.getId(), null, 'D');
			service.configurarPermissaoUsuario(usuario.getId(), Sistema.SIGAA, 'G');
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
	 * Popula os níveis de ensino permitidos no auto-cadastro em um combobox do formulário.
	 * @return
	 */
	public Collection<SelectItem> getNiveisEnsinoCombo(){
		return Arrays.asList(niveisEnsinoCombo);
	}	
	
	/**
	 * Responsável por validar os campos obrigatórios para o cadastro de Discentes.
	 * 
	 * @return
	 */
	private boolean validar() {

		if (usuario.getPessoa().getNome() == null
				|| "".equals(usuario.getPessoa().getNome().trim())) {
			addMensagemErro("É obrigatório informar o nome.");
		}

		if (usuario.getDiscente().getMatricula() == null
				|| usuario.getDiscente().getMatricula() <= 0) {
			addMensagemErro("É obrigatório informar a matrícula.");
		}

		if (usuario.getPessoa().getDataNascimento() == null) {
			addMensagemErro("É obrigatório informar a data de nascimento.");
		}

		if (usuario.getEmail() == null || "".equals(usuario.getEmail().trim())) {
			addMensagemErro("É obrigatório informar o e-mail.");
		}

		if (!internacional) {
			Long cpf  = usuario.getPessoa().getCpf_cnpj();
			ValidatorUtil.validateCPF_CNPJ(cpf == null ? 0 : cpf, "CPF", erros);
			validateRequired(usuario.getPessoa().getIdentidade(), "RG", erros);
		} else {
			validateRequired(usuario.getPessoa().getPassaporte(), "Passaporte", erros);
			validateMaxLength(usuario.getPessoa().getPassaporte(), 20, "Passaporte", erros);
		}


		if (usuario.getDiscente().getAnoIngresso() == null
				|| usuario.getDiscente().getAnoIngresso() <= 0) {
			addMensagemErro("É obrigatório informar o ano de ingresso.");
		}

		if ( (usuario.getDiscente().getPeriodoIngresso() == null
				|| usuario.getDiscente().getPeriodoIngresso() <= 0) 
				&& usuario.getDiscente().getNivel() != NivelEnsino.MEDIO) {
			addMensagemErro("É obrigatório informar o período de ingresso.");
		}
		
		if (usuario.getDiscente().getNivel() == NivelEnsino.MEDIO)
			usuario.getDiscente().setPeriodoIngresso(null);

		if (usuario.getLogin() == null || "".equals(usuario.getLogin().trim())) {
			addMensagemErro("É obrigatório informar o login.");
		}
		
		if (usuario.getLogin() != null && usuario.getLogin().length() > 20) {
			addMensagemErro("O login deve conter no máximo 20 caracteres.");
		}

		if (usuario.getSenha() == null || "".equals(usuario.getSenha().trim())) {
			addMensagemErro("É obrigatório informar a senha.");
		}

		if (usuario.getSenha() != null && usuario.getConfirmaSenha() != null
				&& !usuario.getSenha().equals(usuario.getConfirmaSenha())) {
			addMensagemErro("A senha não confere com a confirmação.");
		}
		
		usuario.getPessoa().setNome(StringUtils.removeEspacosRepetidos(usuario.getNome()));

		return erros.isEmpty();
	}

	/**
	 * Retorna usuário
	 * @return the usuário
	 */
	public Usuario getUsuario() {
		return usuario;
	}

	/**
	 * @param usuario
	 *            the usuário to set
	 */
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	/** 
	 * Retorna lista de discentes Autorizados
	 * Método chamado pela seguinte JSP:
	 * <ul> 
	 * 		<li>/administracao/cadastro/ConfirmarCadastro/discente.jsp</li>
	 * </ul> 
	 */
	public List<Usuario> getDiscentesNaoAutorizados() {
		UsuarioDao dao = getDAO(UsuarioDao.class);

		try {
			prepareMovimento(SigaaListaComando.CONFIRMAR_CADASTRO);
			prepareMovimento(SigaaListaComando.NEGAR_CADASTRO);
			return dao.findDiscentesNaoAutorizados();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Usuario>();
		}
	}
	
	/** 
	 * Responsável por confirmar Cadastro discente
	 * Método chamado pela seguinte JSP:
	 * <ul> 
	 * 		<li>/administracao/cadastro/ConfirmarCadastro/discente.jsp</li>
	 * </ul>  
	 */
	public String confirmar() {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(new Usuario(getParameterInt("id")));
		mov.setCodMovimento(SigaaListaComando.CONFIRMAR_CADASTRO);

		try {
			execute(mov, getCurrentRequest());
			addMessage("Confirmação efetuada com sucesso!",
					TipoMensagemUFRN.INFORMATION);
		} catch (Exception e) {
			e.printStackTrace();
			addMensagemErro(e.getMessage());
		}

		return null;
	}

	/** 
	 * Responsável por negar o cadastro de um discente
	 * Método chamado pela seguinte JSP:
	 * <ul> 
	 * 		<li>/administracao/cadastro/ConfirmarCadastro/discente.jsp</li>
	 * </ul>   
	 */
	public String negar() {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(new Usuario(getParameterInt("id")));
		mov.setCodMovimento(SigaaListaComando.NEGAR_CADASTRO);

		try {
			execute(mov, getCurrentRequest());
			addMessage("Cadastro negado com sucesso!",
					TipoMensagemUFRN.INFORMATION);
		} catch (Exception e) {
			e.printStackTrace();
			addMensagemErro(e.getMessage());
		}

		return null;
	}

	/** 
	 * Responsável por cancelar o cadastro de um discente
	 * Método chamado pela seguinte JSP:
	 * <ul> 
	 * 		<li>/public/cadastro/discente.jsp</li>
	 * </ul>  
	 */
	@Override
	public String cancelar() {
		return redirect("/sigaa");
	}

	public boolean isInternacional() {
		return internacional;
	}

	public void setInternacional(boolean internacional) {
		this.internacional = internacional;
	}
	
}
