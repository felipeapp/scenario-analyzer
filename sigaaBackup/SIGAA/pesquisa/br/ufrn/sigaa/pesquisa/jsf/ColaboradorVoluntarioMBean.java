/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 02/04/2008
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;


import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.sigaa.arq.dao.pesquisa.ColaboradorVoluntarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.ColaboradorVoluntario;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Controlador para efetuar as operações sobre os colaboradores voluntários de pesquisa.
 * 
 * @author Leonardo Campos
 *
 */
@Component("colaboradorVoluntario") @Scope("request")
public class ColaboradorVoluntarioMBean extends SigaaAbstractController<ColaboradorVoluntario> {

	public ColaboradorVoluntarioMBean(){
		initObj();
	}

	private void initObj() {
		obj = new ColaboradorVoluntario();
		obj.setServidor(new Servidor());
	}
	
	@Override
	public String getFormPage() {
		return "/pesquisa/colaborador_voluntario/form.jsf";
	}
	
	@Override
	public String getListPage() {
		return "/pesquisa/colaborador_voluntario/lista.jsf";
	}
	
	/**
	 * É responsável pela checagem da permissão do usuário, verificando assim 
	 * se ele possue ou não permissão para acessar tal funcionalidade.
	 * <br><br>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/pesquisa/menu/consultores.jsp
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		prepareMovimento(SigaaListaComando.CADASTRAR_COLABORADOR_VOLUNTARIO);
		return forward(getFormPage());
	}
	
	/**
	 * Serve para efetuar o cadastro de um Colaborador Voluntário.
	 * <br><br>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/colaborador_voluntario/form.jsp 
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
		NegocioException {

	checkChangeRole();
	
	if (getConfirmButton().equalsIgnoreCase("remover")) {
		return remover();
	} else {
	
		beforeCadastrarAndValidate();
	
		erros = new ListaMensagens();
		ListaMensagens lista = obj.validate();

		if (lista != null && !lista.isEmpty()) {
			erros.addAll(lista.getMensagens());
		}
	
		String descDominio = null;
		try {
			descDominio = ReflectionUtils.evalProperty(obj, "descricaoDominio");
		} catch (Exception e) {
		}
	
		if (!hasErrors()) {
	
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
	
			if (obj.getId() == 0) {
				mov.setCodMovimento(SigaaListaComando.CADASTRAR_COLABORADOR_VOLUNTARIO);
				try {
					executeWithoutClosingSession(mov,
							(HttpServletRequest) FacesContext
									.getCurrentInstance()
									.getExternalContext().getRequest());
					if (descDominio != null && !descDominio.equals("")) {
						addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, descDominio);
					} else {
						addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
					}
					prepareMovimento(SigaaListaComando.CADASTRAR_AVALIADOR_CIC);
				} catch (Exception e) {
					addMensagemErro(e.getMessage());
					e.printStackTrace();
					return forward(getFormPage());
				}
	
				afterCadastrar();
	
				String forward = forwardCadastrar();
				if (forward == null) {
					return redirectJSF(getCurrentURL());
				} else {
					return redirectJSF(forward);
				}
	
			} else {
				mov.setCodMovimento(ArqListaComando.ALTERAR);
				try {
					executeWithoutClosingSession(mov,
							(HttpServletRequest) FacesContext
									.getCurrentInstance()
									.getExternalContext().getRequest());
					if (descDominio != null && !descDominio.equals("")) {
						addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, descDominio);
					} else {
						addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
					}
				} catch (Exception e) {
					addMensagemErro("Erro Inesperado: " + e.getMessage());
					e.printStackTrace();
					return forward(getFormPage());
				}
	
				afterCadastrar();
	
				String forward = forwardCadastrar();
				if (forward == null) {
					return redirectJSF(getCurrentURL());
				} else {
					return redirectJSF(forward);
				}
			}
	
		} else {
	
			return null;
		}
	}
}
	
	/**
	 * Serve para logo após um novo cadastro é instanciado novamente o 
	 * 
	 */
	@Override
	protected void afterCadastrar() throws ArqException {
		super.afterCadastrar();
		initObj();
		prepareMovimento(SigaaListaComando.CADASTRAR_COLABORADOR_VOLUNTARIO);
	}
	
	/**
	 * Esse caso de uso serve para listar dos os Colaboradores Voluntário que estiverem ativos
	 * <br><br>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/pesquisa/menu/consultores.jsp
	 */
	@Override
	public String listar() throws ArqException {
		ColaboradorVoluntarioDao dao = getDAO(ColaboradorVoluntarioDao.class);
		setResultadosBusca(dao.findColaboradoresAtivos());
		return forward(getListPage());
	}
	
	/**
	 * Serve para direcionar o usuário para a tela do formulário para que dessa forma seja 
	 * efetuada a desativação do colaborador voluntário 
	 * <br><br> 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/colaborador_voluntario/lista.jsp
	 */
	@Override
	public String preRemover() {

		try {
			prepareMovimento(ArqListaComando.DESATIVAR);

			int id = getParameterInt("id");
			Integer idInt = new Integer(id);
			obj.setId(idInt);
			
			obj = getGenericDAO().findByPrimaryKey(obj.getId(), ColaboradorVoluntario.class);
		} catch (Exception e) {
			addMensagemErroPadrao();
			e.printStackTrace();
		}

		afterPreRemover();
		setReadOnly(true);

		setConfirmButton("Remover");

		return forward(getFormPage());

	}
	
	/**
	 * Serve para remover o colaborador Voluntário.
	 * <br><br>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/colaborador_voluntario/form.jsf;
	 */
	@Override
	public String remover() throws ArqException {

		beforeRemover();

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);

		if (obj.getId() == 0) {
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
			return null;
		} else {

			mov.setCodMovimento(ArqListaComando.DESATIVAR);
			try {
				executeWithoutClosingSession(mov,
						(HttpServletRequest) FacesContext.getCurrentInstance()
								.getExternalContext().getRequest());
				addMessage("Colaborador removido com sucesso!",
						TipoMensagemUFRN.INFORMATION);
			} catch (NegocioException e) {
				addMensagemErro(e.getMessage());
				return forward(getFormPage());
			} catch (Exception e) {
				addMensagemErroPadrao();
				e.printStackTrace();
				return forward(getFormPage());
			}

			afterRemover();

			return listar();

		}
	}
}