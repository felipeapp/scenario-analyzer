/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '27/04/2007'
 *
 */
package br.ufrn.sigaa.prodocente.producao.jsf;

import java.util.Collection;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.prodocente.ProducaoDao;
import br.ufrn.sigaa.parametros.dominio.ParametrosPortalDocente;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;

public class AcervoDigitalMBean extends AbstractControllerProdocente<Object> {

	private Collection<Producao> resultado;

	/** campos da busca */
	private boolean checkTitulo;
	private String titulo;
	
	private boolean checkTipoProducao;
	private Integer tipoProducao = -1;
	
	private boolean checkAreaConhecimento;
	private Integer areaConhecimento = -1;
	
	private boolean checkAnoPublicacao;
	private Integer anoPublicacao = CalendarUtils.getAnoAtual();
	
	private boolean checkDepartamento;
	private Integer departamento = -1;

	public void buscar(ActionEvent evt) throws DAOException, NegocioException {

		if (!checkTitulo && !checkTipoProducao && !checkAreaConhecimento &&!checkAnoPublicacao && !checkDepartamento) {
			addMensagemErro("Informe um criterio de busca");
		} else {
		
			
			String tituloTemp = null;
			Integer tipoProducaoTemp = null;
			Integer areaConhecimentoTemp = null;
			Integer anoPublicacaoTemp = null;
			Integer departamentoTemp = null;
			
			if (checkTitulo) {
				if (titulo.equals("")) {
					addMensagemErro("Campo titulo não pode ser vazio");
					throw new NegocioException("Campo titulo não pode ser vazio.");
				}
				tituloTemp = titulo;
			}
			if (checkTipoProducao)
				tipoProducaoTemp = tipoProducao;
			if (checkAreaConhecimento)
				areaConhecimentoTemp = areaConhecimento;
			if (checkAnoPublicacao)
				anoPublicacaoTemp =	anoPublicacao;
			if (checkDepartamento)
				departamentoTemp = departamento;
			
			ProducaoDao dao = getDAO(ProducaoDao.class);
			resultado = dao.findByAcervo(tituloTemp,tipoProducaoTemp,areaConhecimentoTemp,anoPublicacaoTemp, departamentoTemp);
		}
	}

	/**
	 * Método responsável por redirecionar o usuário a página de busca da página pública do docente (www.docente.ufrn.br).
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/prodocente/acervo_digital.jsp</li>
	 * </ul>
	 */
	public String getLinkPaginaPublicaDocente() {
		return ParametroHelper.getInstance().getParametro(ParametrosPortalDocente.URL_DOCENTE);
	}
	
	public void changePage(ValueChangeEvent evt) throws DAOException, NegocioException {
		PagingInformation paginacao = (PagingInformation) getMBean("paginacao");
		paginacao.changePage(evt);
		buscar(null);
	}

	public String verAcervoDigital() {
		resetBean("paginacao");
		return forward("/prodocente/acervo_digital.jsf");
	}

	public Integer getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Integer departamento) {
		this.departamento = departamento;
	}

	public Collection<Producao> getResultado() throws DAOException {
		return resultado;
	}

	public void setResultado(Collection<Producao> resultado) {
		this.resultado = resultado;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Integer getTipoProducao() {
		return tipoProducao;
	}

	public void setTipoProducao(Integer tipoProducao) {
		this.tipoProducao = tipoProducao;
	}

	public Integer getAreaConhecimento() {
		return areaConhecimento;
	}

	public void setAreaConhecimento(Integer areaConhecimento) {
		this.areaConhecimento = areaConhecimento;
	}

	public Integer getAnoPublicacao() {
		return anoPublicacao;
	}

	public void setAnoPublicacao(Integer anoPublicacao) {
		this.anoPublicacao = anoPublicacao;
	}

	public boolean isCheckTitulo() {
		return checkTitulo;
	}

	public void setCheckTitulo(boolean checkTitulo) {
		this.checkTitulo = checkTitulo;
	}

	public boolean isCheckTipoProducao() {
		return checkTipoProducao;
	}

	public void setCheckTipoProducao(boolean checkTipoProducao) {
		this.checkTipoProducao = checkTipoProducao;
	}

	public boolean isCheckAreaConhecimento() {
		return checkAreaConhecimento;
	}

	public void setCheckAreaConhecimento(boolean checkAreaConhecimento) {
		this.checkAreaConhecimento = checkAreaConhecimento;
	}

	public boolean isCheckAnoPublicacao() {
		return checkAnoPublicacao;
	}

	public void setCheckAnoPublicacao(boolean checkAnoPublicacao) {
		this.checkAnoPublicacao = checkAnoPublicacao;
	}

	public boolean isCheckDepartamento() {
		return checkDepartamento;
	}

	public void setCheckDepartamento(boolean checkDepartamento) {
		this.checkDepartamento = checkDepartamento;
	}

}
