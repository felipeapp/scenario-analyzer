/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.reuni.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.Collection;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.comum.dominio.Arquivo;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.EditalBolsasReuni;
import br.ufrn.sigaa.ensino.stricto.reuni.negocio.MovimentoEditalBolsasReuni;

/**
 * ManagedBean responsável pelas operações vinculadas a editais
 * de concessão de bolsas REUNI a alunos da pós-graduação.
 * 
 * @author wendell
 *
 */
@Component("editalBolsasReuniBean") @Scope("request")
public class EditalBolsasReuniMBean extends SigaaAbstractController<EditalBolsasReuni> {

	private UploadedFile arquivoEdital;
	private	ComponenteCurricular componenteCurricular;
	
	public EditalBolsasReuniMBean() {
		clear();
	}

	/**
	 * Limpa os campos necessários para um novo cadastro
	 */
	private void clear() {
		obj = new EditalBolsasReuni();
		componenteCurricular = new ComponenteCurricular();
	}
	
	
	/**
	 * 
	 * Utilizado para visualizar um arquivo anexado ao Edital.
	 * 
	 * Chamado por:
	 * sigaa.war/stricto/edital_bolsas_reuni/lista.jsp 
	 * 
	 * @return
	 */
	public String viewArquivo() {
		try {
			int idArquivo = getParameterInt("idArquivo");
			EnvioArquivoHelper.recuperaArquivo(getCurrentResponse(), idArquivo, false);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Arquivo não encontrado!");
			return null;
		}
		FacesContext.getCurrentInstance().responseComplete();
		return null;
	}
	
	/**
	 * Adiciona o componente selecionado à lista de componentes curriculares prioritários <br>
	 * Chamado por: /strico/edital_bolsas_reuni/form.jsp
	 * 
	 * @param event
	 * @throws DAOException 
	 */
	public void adicionarComponente( ActionEvent event ) {
		try {
			componenteCurricular = (ComponenteCurricular) event.getComponent().getAttributes().get("componente");
			if (!isEmpty(componenteCurricular)) {
				obj.adicionarComponenteCurricular( getGenericDAO().refresh(componenteCurricular) );
				componenteCurricular = new ComponenteCurricular();
			}
		} catch (DAOException e) {
			e.printStackTrace();
			notifyError(e);
		}
	}

	/**
	 * Remove um componente da lista de componentes curriculares prioritários <br>
	 * Chamado por: /strico/edital_bolsas_reuni/form.jsp
	 * 
	 * @param event
	 */
	public void removerComponente( ActionEvent event ) {
		try {
			componenteCurricular = (ComponenteCurricular) event.getComponent().getAttributes().get("componente");
			if (!isEmpty(componenteCurricular)) {
				obj.removerComponenteCurricular( getGenericDAO().refresh(componenteCurricular) );
				componenteCurricular = new ComponenteCurricular();
			}
		} catch (DAOException e) {
			e.printStackTrace();
			notifyError(e);
		}
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.PPG);
	}
	
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkChangeRole();
		clear();
		prepareMovimento(SigaaListaComando.CADASTRAR_EDITAL_BOLSAS_REUNI);
		
		setConfirmButton("Cadastrar");
		return forward(getFormPage());
	}

	/**
	 * Esse método tem como finalidade atualizar, alterar um edital de Bolsa reuni. 
	 *  
	 * JSP:
	 * <br>
	 *  /SIGAA/app/sigaa.ear/sigaa.war/stricto/edital_bolsas_reuni/lista.jsp
	 *  
	 */
	@Override
	public String atualizar() throws ArqException {
		checkChangeRole();
		populateObj(true);
		prepareMovimento(SigaaListaComando.CADASTRAR_EDITAL_BOLSAS_REUNI);
		
		setConfirmButton("Alterar");
		return forward(getFormPage());
	}

	/**
	 * Esse método tem como finalidade realizar o cadastro de um novo edital de bolsa reuni.
	 * 
	 * JSP:
	 * <br>
	 *  /SIGAA/app/sigaa.ear/sigaa.war/stricto/edital_bolsas_reuni/form.jsp
	 * 
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		checkChangeRole();

		MovimentoEditalBolsasReuni mov = new MovimentoEditalBolsasReuni(obj);
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_EDITAL_BOLSAS_REUNI);
		
		// Recuperar dados do arquivo anexado
		if (arquivoEdital != null) {
			Arquivo arquivo;
			try {
				arquivo = new Arquivo( arquivoEdital.getBytes(), arquivoEdital.getInputStream(), 
						arquivoEdital.getContentType(), arquivoEdital.getName(), arquivoEdital.getSize() );
				mov.setArquivoEdital(arquivo);
			} catch (IOException e) {
				throw new ArqException(e);
			}
		}

		try {
			execute(mov);
		} catch (NegocioException ne) {
			addMensagens( ne.getListaMensagens() );
			return null;
		}
		
		addMensagemInformation("As informações do edital foram gravadas com sucesso!");
		return redirect(getListPage());
	}
	
	/**
	 * Esse método tem como finalidade remover um edital de bolsa Reuni.
	 * 
	 * JSP:
	 * <br>
	 * /SIGAA/app/sigaa.ear/sigaa.war/stricto/edital_bolsas_reuni/form.jsp
	 * /SIGAA/app/sigaa.ear/sigaa.war/stricto/edital_bolsas_reuni/lista.jsp
	 * 
	 */
	@Override
	public String remover() throws ArqException {
		setId();
		MovimentoEditalBolsasReuni mov = new MovimentoEditalBolsasReuni(obj);
		mov.setCodMovimento(SigaaListaComando.REMOVER_EDITAL_BOLSAS_REUNI);

		try {
			execute(mov);
		} catch (NegocioException ne) {
			addMensagens( ne.getListaMensagens() );
			return null;
		}
		
		addMensagemInformation("Edital excluído com sucesso!");
		return redirect(getListPage());
	}
	
	/**
	 * Esse método realizar o direcionamento do usuário para a tela de listagem 
	 * 
	 * JSP:
	 * <br>
	 * /SIGAA/app/sigaa.ear/sigaa.war/stricto/edital_bolsas_reuni/form.jsp
	 * 
	 */
	@Override
	public String cancelar() {
		resetBean();
		return forward(getListPage());
	}
	
	/**
	 * Esse método tem a finalidade de listar todos os editais de bolsas Reuni
	 * 
	 * JSP:
	 * <br>
	 * /SIGAA/app/sigaa.ear/sigaa.war/stricto/menus/bolsas_reuni.jsp
	 */
	@Override
	public String listar() throws ArqException {
		return forward(getListPage());
	}
	
	@Override
	public String getFormPage() {
		return "/stricto/edital_bolsas_reuni/form.jsf";
	}

	@Override
	public String getListPage() {
		return "/stricto/edital_bolsas_reuni/lista.jsf";
	}

	public ComponenteCurricular getComponenteCurricular() {
		return componenteCurricular;
	}

	public void setComponenteCurricular(ComponenteCurricular componenteCurricular) {
		this.componenteCurricular = componenteCurricular;
	}

	public UploadedFile getArquivoEdital() {
		return arquivoEdital;
	}

	public void setArquivoEdital(UploadedFile arquivoEdital) {
		this.arquivoEdital = arquivoEdital;
	}

	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return getAll(EditalBolsasReuni.class, "id", "descricao");
	}
}