/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 29/12/2009
 * Autor: Geyson
 * 
 */
package br.ufrn.sigaa.extensao.relatorio.jsf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.PublicoAtingidoRelatorioDao;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.AbstractRelatorioGraduacaoMBean;

/**
 * MBean responsável por relatório de público atingido com base em todos os relatórios submetidos pelos coordenadores de ações de extensão.
 * 
 */
@Component("relatorioPublicoAtingido") @Scope("request")
public class PublicoAtingidoRelatorioMBean extends  AbstractRelatorioGraduacaoMBean {
	
	private final String CONTEXTO ="/extensao/RelatorioPublicoAtingido/";
	private final String JSP_SELECIONA = "seleciona";
	private final String JSP_RELATORIO = "relatorio";
	
	private boolean filtroAno;
	private Integer anoPublico;
	private Date dataInicio;
	private Date dataFim;
	
	

	/** Lista de dados do relatório. */
	private List<Map<String,Object>> lista = new ArrayList<Map<String,Object>>();
	
	/** Construtor padrão. */
	public PublicoAtingidoRelatorioMBean(){
		
	}
	
	/**
	 * Faz o redirecionamento para a tela de preenchimento dos dados para a geração do relatório
	 * <br><br>
	 * JSP: sigga/extesao/menu.jsp
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarRelatorioPublicoAtingido() throws SegurancaException{
		checkListRole();
		return forward(CONTEXTO+JSP_SELECIONA); 
	}
	
	/**
	 * Gera relatório de público atingido com base em todos os relatórios submetidos pelos coordenadores de ações de extensão.
	 * JSP: sigga/extensao/RelatorioPublicoAtingido/seleciona.jsp
	 * @return
	 * @throws DAOException 
	 */
	public String gerarRelatorio() throws DAOException{
		PublicoAtingidoRelatorioDao dao = getDAO(PublicoAtingidoRelatorioDao.class);
		
		ValidatorUtil.validaData(Formatador.getInstance().formatarData(dataInicio), "Data Inicial do Projeto", erros);
		ValidatorUtil.validaData(Formatador.getInstance().formatarData(dataFim), "Data Final do Projeto", erros);
		ValidatorUtil.validaDataAnteriorIgual(dataInicio, dataFim, "Data Inicial do Projeto", erros);
		
		if(!hasErrors()){
			lista = dao.relatorioPublicoAtingido(dataInicio, dataFim);
			if (lista.size() > 0) {
				return forward(JSP_RELATORIO);
			}
			else
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
		}
		return null;
	}
	
	public boolean isFiltroAno() {
		return filtroAno;
	}

	public void setFiltroAno(boolean filtroAno) {
		this.filtroAno = filtroAno;
	}

	public Integer getAnoPublico() {
		return anoPublico;
	}

	public void setAnoPublico(Integer anoPublico) {
		this.anoPublico = anoPublico;
	}

	public List<Map<String, Object>> getLista() {
		return lista;
	}

	public void setLista(List<Map<String, Object>> lista) {
		this.lista = lista;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	
	

}
