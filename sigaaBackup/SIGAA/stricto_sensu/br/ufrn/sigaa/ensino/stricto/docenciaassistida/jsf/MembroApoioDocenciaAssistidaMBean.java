/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 17/05/2011
 */
package br.ufrn.sigaa.ensino.stricto.docenciaassistida.jsf;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.MembroApoioDocenciaAssistida;

/**
 * MBean responsável por cadastrar os membros de apoio à docência assistida
 * 
 * @author Arlindo Rodrigues
 *
 */
@Component("membroApoioDocenciaAssistidaMBean") @Scope("request")
public class MembroApoioDocenciaAssistidaMBean extends SigaaAbstractController<MembroApoioDocenciaAssistida> {
	
	/** Lista de membros cadastrados */
	private Collection<MembroApoioDocenciaAssistida> listagem = new ArrayList<MembroApoioDocenciaAssistida>();
	
	/**
	 * Inicializa os objetos
	 */
	private void initObj(){
		obj = new MembroApoioDocenciaAssistida();		
		obj.setUsuario(new Usuario());
	}
	
	/**
	 * Construtor padrão
	 */
	public MembroApoioDocenciaAssistidaMBean() {
		initObj();
	}
	
	/**
	 * Inicia o cadastro de membros
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/menus/bolsas_reuni.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {	
		setConfirmButton("Cadastrar");
		prepareMovimento(ArqListaComando.CADASTRAR);
		return forward(getFormPage());
	}
	
	/**
	 * Valida os dados antes de realizar o cadastro
	 * Método não chamado por JSP's
	 */
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException,
			SegurancaException, DAOException {
		
		if (ValidatorUtil.isEmpty(obj.getUsuario()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Usuário");
		
	 	if (!ValidatorUtil.isEmpty(getGenericDAO().findByExactField(MembroApoioDocenciaAssistida.class, "usuario.id", obj.getUsuario().getId())))
	 		addMensagemErro("O usuário informado foi cadastrado.");
	}
	
	@Override
	public String remover() throws ArqException {
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}
	
	/**
	 * Caminho do form 
	 */
	@Override
	public String getFormPage() {
		return "/stricto/membro_apoio_docencia_assistida/form.jsf";
	}
	
	/**
	 * Caminho da listagem
	 */
	@Override
	public String getListPage() {
		return "/stricto/membro_apoio_docencia_assistida/form.jsf";
	}
	
	/**
	 * Retorna a lista de membros cadastrados
	 * @return
	 * @throws ArqException
	 */
	public Collection<MembroApoioDocenciaAssistida> getListagem() throws ArqException {
		if (ValidatorUtil.isEmpty(listagem))
			listagem = getAll();	
		return listagem;
	}

	public void setListagem(Collection<MembroApoioDocenciaAssistida> listagem) {
		this.listagem = listagem;
	}
}
