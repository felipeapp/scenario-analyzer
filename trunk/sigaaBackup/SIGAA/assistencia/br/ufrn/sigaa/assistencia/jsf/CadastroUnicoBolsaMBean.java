/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 20/03/2009
 *
 */	
package br.ufrn.sigaa.assistencia.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.questionario.QuestionarioDao;
import br.ufrn.sigaa.arq.dao.sae.CadastroUnicoBolsaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.cadunico.dominio.FormularioCadastroUnicoBolsa;
import br.ufrn.sigaa.questionario.dominio.Questionario;
import br.ufrn.sigaa.questionario.dominio.TipoQuestionario;
import br.ufrn.sigaa.questionario.jsf.QuestionarioMBean;

/**
 * MBean respons�vel por gerenciar a configura��o do cadastro �nico
 * 
 * @author Henrique Andr�
 *
 */
@Component("cadastroUnicoBolsa") @Scope("session")
public class CadastroUnicoBolsaMBean extends SigaaAbstractController<FormularioCadastroUnicoBolsa> {

	/**Link para a pagina de configura��o do cadastor �nico.*/
	private static final String PAGINA_CONFIGURACAO = "/sae/cadastro_unico/configurar.jsp";
	
	/**
	 * Lista de question�rios
	 */
	private Collection<Questionario> questionarios = new ArrayList<Questionario>();
	
	/**
	 * Lista de question�rios utilizados para ades�o ao cadastro �nico 
	 */
	private Collection<FormularioCadastroUnicoBolsa> cadastros;
	
	/**
	 * Inicializar obj
	 */
	private void init() {
		obj = new FormularioCadastroUnicoBolsa();
		obj.setQuestionario(new Questionario());
	}	
	
	/**
	 * Inicia configura��o do cadastro
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul> <li> sigaa.war/sae/cadastro_unico/lista.jsp </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarCadastro() throws ArqException {
		Collection<FormularioCadastroUnicoBolsa> formularios = getGenericDAO().findAllAtivos(FormularioCadastroUnicoBolsa.class, "dataCadastro");
		
		if (formularios != null && !formularios.isEmpty()) {
			addMensagemErro("J� existe um registro de cadastro �nico.");
			return null;
		}
				
		setConfirmButton("Cadastrar");
		prepareMovimento(SigaaListaComando.CADASTRAR_PARAMETROS_CADASTRO_UNICO);
		init();
		
		QuestionarioDao questionarioDao = getDAO(QuestionarioDao.class);
		this.questionarios = questionarioDao.findByTipo(TipoQuestionario.QUESTIONARIO_SOCIO_ECONOMICO);
		
		return forward(PAGINA_CONFIGURACAO);
	}

	/**
	 * Inicia configura��o do cadastro
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul> <li>  /sigaa.war/sae/cadastro_unico/lista.jsp </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarAlterar() throws ArqException {
		Integer id = getParameterInt("id");
		
		if (isEmpty(id)) {
			addMensagemErro("Registro n�o localizado.");
			return null;
		}
		
		init();
		
		obj = getGenericDAO().findByPrimaryKey(id, FormularioCadastroUnicoBolsa.class);
		
		if (isEmpty(obj)) {
			addMensagemErro("Registro n�o localizado.");
			return null;
		}
		
		setConfirmButton("Alterar");
		prepareMovimento(SigaaListaComando.ALTERAR_PARAMETROS_CADASTRO_UNICO);
		QuestionarioDao questionarioDao = getDAO(QuestionarioDao.class);
		this.questionarios = questionarioDao.findByTipo(TipoQuestionario.QUESTIONARIO_SOCIO_ECONOMICO);
		
		return forward(PAGINA_CONFIGURACAO);
	}	
	
	/**
	 * Salva o question�rio que o usu�rio escolheu
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul> <li>  /sigaa.war/sae/cadastro_unico/configurar.jsp </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public String persistir() throws SegurancaException, ArqException{
		
		MovimentoCadastro mov = new MovimentoCadastro();

		mov.setObjMovimentado(obj);
		
		if (obj.getId() == 0)
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_PARAMETROS_CADASTRO_UNICO);
		else
			mov.setCodMovimento(SigaaListaComando.ALTERAR_PARAMETROS_CADASTRO_UNICO);
		
		try {
			execute(mov);
			addMensagemInformation("Configura��o realizada com sucesso.");
		} catch (NegocioException e) {
			tratamentoErroPadrao(e, "N�o pode salvar as configura��es. Por favor entre em contato com o suporte.");
			return cancelar();
		}
		
		return cancelar();
	}
	
	@Override
	public String remover() throws ArqException{
		obj = new FormularioCadastroUnicoBolsa();
		try {
			prepareMovimento(ArqListaComando.DESATIVAR);
			super.inativar();
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
			return cancelar();
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
			return cancelar();
		}
		return listar();
	}
	
	
	/**
	 * Monta um combobox com os question�rios 
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul> <li>  /sigaa.war/sae/cadastro_unico/configurar.jsp </ul>
	 * @return
	 */
	public Collection<SelectItem> getQuestionariosCombo() {
		return toSelectItems(questionarios, "id", "titulo");
	}
	
	/**
	 * Chama o m�todo de listar os question�rios e seta o papel que vai ter permiss�o
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul> <li>  /sigaa.war/sae/menu.jsp </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String gerenciarQuestionario() throws ArqException {
		checkRole(SigaaPapeis.SAE_COORDENADOR);
		
		QuestionarioMBean mBean = getMBean("questionarioBean");
		return mBean.gerenciarSocioEconomico();
	}
	
	/**
	 * Diret�rio base
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul> <li>  N�o invocado por JSP. </ul>
	 */
	@Override
	public String getDirBase() {
		return "/sae/cadastro_unico";
	}	
	
	/**
	 * Lista com os todos os formul�rios de cadastro �nico dispon�veis
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul> <li>  /sigaa.war/sae/menu.jsp </ul>
	 */
	@Override
	public String listar() throws ArqException {
		
		CadastroUnicoBolsaDao dao = getDAO(CadastroUnicoBolsaDao.class);
		cadastros = dao.findAllAtivos(FormularioCadastroUnicoBolsa.class, "dataCadastro");
		
		return super.listar();
	}

	public Collection<Questionario> getQuestionarios() {
		return questionarios;
	}

	public void setQuestionarios(Collection<Questionario> questionarios) {
		this.questionarios = questionarios;
	}

	public Collection<FormularioCadastroUnicoBolsa> getCadastros() {
		return cadastros;
	}

	public void setCadastros(Collection<FormularioCadastroUnicoBolsa> cadastros) {
		this.cadastros = cadastros;
	}	
	
}
