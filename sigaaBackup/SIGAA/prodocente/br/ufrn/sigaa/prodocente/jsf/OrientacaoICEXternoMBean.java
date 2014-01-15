/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '08/04/2009'
 *
 */
package br.ufrn.sigaa.prodocente.jsf;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.prodocente.atividades.dominio.OrientacaoICExterno;

/**
 * Managed bean para gerenciar Orientações IC Externo
 * 
 * @author Jean Guerethes
 * @author Daniel Augusto
 */

@Component("orientacoesICExternoBean")
@Scope("session")
public class OrientacaoICEXternoMBean extends AbstractControllerAtividades<OrientacaoICExterno> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6317121967248285788L;

	private final String JSP_CADASTRO = "/prodocente/atividades/OrientacaoICExterno/form.jsp";

	private final String JSP_REDIRECIONA = "/prodocente/atividades/OrientacaoICExterno/lista.jsp";

	private String confirmButton = "Cadastrar";
	
	public OrientacaoICEXternoMBean() {
		clear();
	}

	private void clear() {
		obj = new OrientacaoICExterno();
		obj.setInstituicao(new InstituicoesEnsino());
	}

	/**
	 * Usado na jsp Menu que tem como função redirecionar para
	 * /prodocente/atividades/OrientacaoICExterno/form.jsf
	 * @throws ArqException 
	 */
	public String direcionar() throws ArqException {
		return forward(JSP_REDIRECIONA);
	}
	
	/**
	 * Usando na jsp /prodocente/atividades/OrientacaoICExterno/lista.jsf" 
	 * onde redireciona o usuário para a tela de cadastro 
	 * @throws ArqException 
	 */
	public String direcionaCadastrar() throws ArqException {
		clear();
		prepareMovimento(ArqListaComando.CADASTRAR);
		confirmButton = "Cadastrar";
		return forward(JSP_CADASTRO);
	}
	
	/**
	 * Serve para testar se Tipo Bolsa é igual a 3, caso seja, 
	 * é necessário informar o nome da bolsa.
	 * 
	*/
	public String validacao() throws SegurancaException, ArqException, NegocioException {
		if (obj.getDataFim() == null)
			addMensagemErro("Data do Fim da Orientação: Campo obrigatório não informado ou data inválida");
		if (obj.getDataInicio() == null)
			addMensagemErro("Data de Início da Orientação: Campo obrigatório não informado ou data inválida");
		if (obj.getInstituicao().getId() == 0)
			addMensagemErro("Instituição de Ensino: Campo obrigatório não informado");
		ValidatorUtil.validaDataInicioFim(obj.getDataInicio(), obj.getDataFim(), 
				"A Data do Fim não pode preceder a Data de Início", erros.getMensagens());
		if ((obj.getTipoBolsa() == 3) && (obj.getTipoBolsaOutra().equals("")))
			addMensagemErro("Informe o nome da bolsa.");
		if (obj.getNomeOrientando() == null || "".equals(obj.getNomeOrientando()))
			addMensagemErro("Nome do Orientando: Campo obrigatório não informado");
		
		if (hasErrors()) return redirectMesmaPagina();
		
		obj.setOrientador(getServidorUsuario());
		return super.cadastrar();
	}

	
	/**
	 * 
	 * Retorna todas as Orientações de Bolsista de Iniciação científica Externo.
	 * 
	 * Chamado pela(s) JSP(s):
	 * sigaa.war/prodocente/atividades/OrientacaoICExterno/lista.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<OrientacaoICExterno> getAllServidor() throws DAOException {
		GenericDAO dao = getGenericDAO();
		return dao.findByExactField(OrientacaoICExterno.class, "orientador", getUsuarioLogado().getServidor().getId());
	}
	
	public String cancelar(){
		clear();
		return forward(JSP_REDIRECIONA);
	}

	@Override
	public String getUrlRedirecRemover() {
		return JSP_REDIRECIONA;
	}

	@Override
	public String getFormPage() {
		return JSP_CADASTRO;
	}
	
	@Override
	public String getListPage() {
		return JSP_REDIRECIONA;
	}
	
	public String getConfirmButton() {
		return confirmButton;
	}

	public void setConfirmButton(String confirmButton) {
		this.confirmButton = confirmButton;
	}

}