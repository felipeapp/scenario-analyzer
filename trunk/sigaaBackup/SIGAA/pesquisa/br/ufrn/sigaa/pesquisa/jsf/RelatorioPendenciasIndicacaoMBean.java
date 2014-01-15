/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 13/10/2009
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.EditalPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.RelatorioPendenciasIndicacaoDao;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.AbstractRelatorioGraduacaoMBean;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;


/**
 * MBean respons�vel por relat�rio de Pend�ncias por Indica��o no m�dulo pesquisa.
 * @author geyson
 * @author leonardo
 */
@Component("relatorioPendenciasIndicacao") @Scope("request")
public class RelatorioPendenciasIndicacaoMBean extends  AbstractRelatorioGraduacaoMBean {
	
	/**
	 * Caminho para as views utilizadas no caso de uso.
	 */
	private final String CONTEXTO ="/pesquisa/relatorios/pendecias_indicacao/";
	/**
	 * Nome da view que cont�m o formul�rio com os par�metros para a emiss�o do relat�rio.
	 */
	private final String JSP_SELECIONA_PENDENCIAS_INDICACAO = "seleciona_pendencias_indicacao";
	/**
	 * Nome da view onde o relat�rio � montado e exibido.
	 */
	private final String JSP_REL_PENDENCIAS_INDICACAO = "rel_pendencias_indicacao";
	/**
	 * Edital de pesquisa referente ao qual o relat�rio � emitido.
	 */
	private EditalPesquisa editalPesquisa = new EditalPesquisa();	
	
	/** Lista de dados do relat�rio. */
	private List<Map<String,Object>> lista = new ArrayList<Map<String,Object>>();

	/** Construtor padr�o. */
	RelatorioPendenciasIndicacaoMBean(){
		editalPesquisa = new EditalPesquisa();
	}
	
	/**
	 * Faz o redirecionamento para a tela de preenchimento dos dados para a gera��o do relat�rio
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/pesquisa/menu.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarRelatorioPendenciasIndicacao() throws SegurancaException{
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		editalPesquisa = new EditalPesquisa();
		return forward(CONTEXTO+JSP_SELECIONA_PENDENCIAS_INDICACAO); 
	}
	
	/**
	 * Gera um relat�rio tendo como entrada o per�odo de cotas e o tipo de bolsa. 
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/pesquisa/relatorios/pendecias_indicacao/seleciona_pendencias_indicacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioPendenciasindicacao() throws DAOException{
		RelatorioPendenciasIndicacaoDao dao = getDAO(RelatorioPendenciasIndicacaoDao.class);
		
		ValidatorUtil.validateRequired(editalPesquisa, "Edital", erros);
		
		if(!hasErrors()){
			editalPesquisa = dao.findByPrimaryKey(editalPesquisa.getId(), EditalPesquisa.class);
			lista = dao.findPendenciasIndicacao(editalPesquisa.getId());
			if (lista.size() > 0) {
				return forward(JSP_REL_PENDENCIAS_INDICACAO);
			}
			else
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
		}
		
		return null;
		
	}
	
	/** Retorna uma cole��o de SelectItem de editais de distribui��o de cotas de bolsa.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllEditalPesquisaCombo() throws DAOException {
		EditalPesquisaDao dao = getDAO(EditalPesquisaDao.class);
		Collection<SelectItem> combo = new ArrayList<SelectItem>();
		for (EditalPesquisa edital : dao.findAllDistribuicaoCotas()) {
			combo.add(new SelectItem(edital.getId(), edital.getDescricao()));
		}
		return combo;
	}
	
	public List<Map<String, Object>> getLista() {
		return lista;
	}

	public void setLista(List<Map<String, Object>> lista) {
		this.lista = lista;
	}

	public EditalPesquisa getEditalPesquisa() {
		return editalPesquisa;
	}

	public void setEditalPesquisa(EditalPesquisa editalPesquisa) {
		this.editalPesquisa = editalPesquisa;
	}

	
	
}