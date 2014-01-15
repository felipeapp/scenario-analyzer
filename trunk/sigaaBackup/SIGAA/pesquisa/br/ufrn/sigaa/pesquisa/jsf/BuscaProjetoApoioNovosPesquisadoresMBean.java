/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '26/09/2012'
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
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoApoioNovosPesquisadoresDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoApoioNovosPesquisadores;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Controlador respons�vel pela consulta de projetos de apoio a novos pesquisadores.
 * 
 * @author Leonardo
 *
 */
@Component("buscaProjetoApoioNovosPesquisadoresMBean")
@Scope("session")
public class BuscaProjetoApoioNovosPesquisadoresMBean extends
		SigaaAbstractController<ProjetoApoioNovosPesquisadores> {

	/** Indica se o checkbox referente ao ano est� selecionado. */
	private boolean checkAno;
	/** Indica se o checkbox referente ao t�tulo est� selecionado. */
	private boolean checkTitulo;
	/** Indica se o checkbox referente ao coordenador est� selecionado. */
	private boolean checkCoordenador;
	/** Indica se o checkbox referente ao status est� selecionado. */
	private boolean checkStatus;
	
	/** Armazena o valor do ano informado para a busca. */
	private Integer buscaAno;
	/** Armazena o t�tulo informado para a busca. */
	private String buscaTitulo;
	/** Armazena o coordenador informado para a busca. */
	private Servidor buscaCoordenador;
	/** Armazena o status informado para a busca. */
	private Integer buscaStatus;
	
	/** Armazena os resultados da busca efetuada. */
	private Collection<ProjetoApoioNovosPesquisadores> resultadosBusca = new ArrayList<ProjetoApoioNovosPesquisadores>();
	
	/** Construtor padr�o. */
	public BuscaProjetoApoioNovosPesquisadoresMBean() {
		clear();
	}
	
	/**
	 * Inicializa os atributos utilizados pelo controlador.
	 */
	private void clear() {
		checkAno = false;
		checkTitulo = false;
		checkCoordenador = false;
		
		buscaAno = null;
		buscaTitulo = null;
		buscaCoordenador = new Servidor();
		buscaStatus = null;
		
		resultadosBusca = new ArrayList<ProjetoApoioNovosPesquisadores>();
	}

	/**
	 * Encaminha para o formul�rio de busca;
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\pesquisa\ProjetoApoioNovosPesquisadores\view.jsp</li>
	 *   </ul>
	 * @return
	 */
	public String telaBusca(){
		return forward("/pesquisa/ProjetoApoioNovosPesquisadores/busca.jsp");
	}
	
	/**
	 * Inicia uma nova busca.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Efetua a busca de acordo com os crit�rios informados.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\pesquisa\ProjetoApoioNovosPesquisadores\busca.jsp</li>
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
			ValidatorUtil.validateRequired(titulo, "T�tulo", erros);
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
		
		ProjetoApoioNovosPesquisadoresDao dao = getDAO(ProjetoApoioNovosPesquisadoresDao.class);
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

	public Collection<ProjetoApoioNovosPesquisadores> getResultadosBusca() {
		return resultadosBusca;
	}

	public void setResultadosBusca(
			Collection<ProjetoApoioNovosPesquisadores> resultadosBusca) {
		this.resultadosBusca = resultadosBusca;
	}


	
	
}
