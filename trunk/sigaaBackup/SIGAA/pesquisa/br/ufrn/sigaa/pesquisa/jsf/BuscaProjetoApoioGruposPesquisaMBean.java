/**
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoApoioGrupoPesquisaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoApoioGrupoPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Controlador responsável pela consulta de projetos de apoio a novos pesquisadores.
 * 
 * @author Leonardo Campos
 *
 */
@Component("buscaProjetoApoioGruposPesquisaMBean")
@Scope("session")
public class BuscaProjetoApoioGruposPesquisaMBean extends
		SigaaAbstractController<ProjetoApoioGrupoPesquisa> {

	/** Indica se o checkbox referente ao ano está selecionado. */
	private boolean checkAno;
	/** Indica se o checkbox referente ao título está selecionado. */
	private boolean checkTitulo;
	/** Indica se o checkbox referente ao coordenador está selecionado. */
	private boolean checkCoordenador;
	/** Indica se o checkbox referente ao status está selecionado. */
	private boolean checkStatus;
	
	/** Armazena o valor do ano informado para a busca. */
	private Integer buscaAno;
	/** Armazena o título informado para a busca. */
	private String buscaTitulo;
	/** Armazena o coordenador informado para a busca. */
	private Servidor buscaCoordenador;
	/** Armazena o status informado para a busca. */
	private Integer buscaStatus;
	
	/** Armazena os resultados da busca efetuada. */
	private Collection<ProjetoApoioGrupoPesquisa> resultadosBusca = new ArrayList<ProjetoApoioGrupoPesquisa>();
	
	/** Construtor padrão. */
	public BuscaProjetoApoioGruposPesquisaMBean() {
		clear();
	}

	private void clear() {
		checkAno = false;
		checkTitulo = false;
		checkCoordenador = false;
		
		buscaAno = null;
		buscaTitulo = null;
		buscaCoordenador = new Servidor();
		buscaStatus = null;
		
		resultadosBusca = new ArrayList<ProjetoApoioGrupoPesquisa>();
	}
	
	
	/**
	 * Encaminha para o formulário de busca;
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\pesquisa\ProjetoApoioGrupoPesquisa\view.jsp</li>
	 *   </ul>
	 * @return
	 */
	public String telaBusca(){
		return forward("/pesquisa/ProjetoApoioGrupoPesquisa/busca.jsp");
	}
	
	/**
	 * Inicia uma nova busca.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\pesquisa\menus\projetos.jsp</li>
	 *   </ul>
	 * @return
	 */
	public String iniciar(){
		clear();
		return telaBusca();
	}
	
	/**
	 * Efetua a busca de acordo com os critérios informados.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\pesquisa\ProjetoApoioGrupoPesquisa\busca.jsp</li>
	 *   </ul>
	 */
	public String buscar() throws DAOException{
		
		Integer ano = null;
		String titulo = null;
		Integer idCoordenador = null;
		Integer status = null;
		
		if(checkAno) {
			ano = buscaAno;
			ValidatorUtil.validateRequired(ano, "Ano", erros);
		}
		if(checkTitulo) {
			titulo = buscaTitulo;
			ValidatorUtil.validateRequired(titulo, "Título", erros);
		}
		if(checkCoordenador) {
			idCoordenador = buscaCoordenador.getId();
			ValidatorUtil.validateRequiredId(idCoordenador, "Coordenador", erros);
		}
		if(checkStatus) {
			status = buscaStatus;
			ValidatorUtil.validateRequiredId(status, "Status", erros);
		}
		
		if(hasOnlyErrors())
			return null;
		
		ProjetoApoioGrupoPesquisaDao dao = getDAO(ProjetoApoioGrupoPesquisaDao.class);
		try {
			resultadosBusca = dao.filter(ano, titulo, idCoordenador, status);
		} finally {
			dao.close();
		}
		
		if(ValidatorUtil.isEmpty(resultadosBusca))
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		
		return null;
	}

	public boolean isCheckAno() {
		return checkAno;
	}

	public void setCheckAno(boolean checkAno) {
		this.checkAno = checkAno;
	}

	public boolean isCheckTitulo() {
		return checkTitulo;
	}

	public void setCheckTitulo(boolean checkTitulo) {
		this.checkTitulo = checkTitulo;
	}

	public boolean isCheckCoordenador() {
		return checkCoordenador;
	}

	public void setCheckCoordenador(boolean checkCoordenador) {
		this.checkCoordenador = checkCoordenador;
	}

	public Integer getBuscaAno() {
		return buscaAno;
	}

	public void setBuscaAno(Integer buscaAno) {
		this.buscaAno = buscaAno;
	}

	public String getBuscaTitulo() {
		return buscaTitulo;
	}

	public void setBuscaTitulo(String buscaTitulo) {
		this.buscaTitulo = buscaTitulo;
	}

	public Servidor getBuscaCoordenador() {
		return buscaCoordenador;
	}

	public void setBuscaCoordenador(Servidor buscaCoordenador) {
		this.buscaCoordenador = buscaCoordenador;
	}

	public boolean isCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(boolean checkStatus) {
		this.checkStatus = checkStatus;
	}

	public Integer getBuscaStatus() {
		return buscaStatus;
	}

	public void setBuscaStatus(Integer buscaStatus) {
		this.buscaStatus = buscaStatus;
	}

	public Collection<ProjetoApoioGrupoPesquisa> getResultadosBusca() {
		return resultadosBusca;
	}

	public void setResultadosBusca(
			Collection<ProjetoApoioGrupoPesquisa> resultadosBusca) {
		this.resultadosBusca = resultadosBusca;
	}


	
}
