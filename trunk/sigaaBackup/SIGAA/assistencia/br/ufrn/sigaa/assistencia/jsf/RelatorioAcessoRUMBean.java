package br.ufrn.sigaa.assistencia.jsf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.assistencia.dao.RelatorioAcessoRUDao;
import br.ufrn.sigaa.assistencia.restaurante.dominio.RegistroAcessoRU;

/**
 * 
 * MBean responsável por relatórios relevantes ao Restaurante Universitário - RU.
 * @author geyson
 *
 */
@Scope(value = "session")
@Component(value = "relatorioAcessoRu")
public class RelatorioAcessoRUMBean extends SigaaAbstractController<RegistroAcessoRU> {
	
	/** Valor selecionado para busca por período inicial*/
	private Date buscaDataInicio;
	/** Valor selecionado para busca por final */
	private Date buscaDataFim;
	
	/** Lista de dados do relatório. */
	private List<Map<String,Object>> acessosRu = new ArrayList<Map<String,Object>>();
	
	/** Lista de dados do relatório. */
	private List<Map<String,Object>> acessosRuDetalhes = new ArrayList<Map<String,Object>>();
	
	/** escolhe relatório detalhado */
	private boolean checkDetalhes = false;
	
	/** construtor */
	public RelatorioAcessoRUMBean(){
		obj = new RegistroAcessoRU();
	}
	
	/**
	 * Inicia a busca para gerar relatórios.
	 * <br>
	 * Chamado por Jsp:
     *  <ul>
     *  	<li>sigaa.war/sae/menu.jsp</li>
     *  </ul>
     *  
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciaRelatorio() throws SegurancaException{
		checkRole(SigaaPapeis.SAE_VISUALIZA_ACESSO_RU);
		return forward(ConstantesNavegacaoSae.SELECIONA_ACESSO_RU);
	}
	
	/**
	 * busca acessos ao RU para gerar relatório.
	 * @param periodo
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String buscarAcessosRu() throws HibernateException, DAOException{
		Date inicio = null;
		Date fim = null;

		ListaMensagens lista = new ListaMensagens();			
		inicio = this.buscaDataInicio;
		fim = this.buscaDataFim;
		if(inicio == null || fim == null){
			lista.addErro("Período: Campo obrigatório não informado.");
			addMensagens(lista);
			return null;
		}
		if(inicio != null && fim != null){
			ValidatorUtil.validaInicioFim(inicio, fim, "Período", lista);
		}

		if (lista.isEmpty()) {
			RelatorioAcessoRUDao dao = getDAO(RelatorioAcessoRUDao.class);
			if(!checkDetalhes){
				acessosRu = dao.relatorioAcessoRUCatraca(inicio, fim);
				if (acessosRu.size() > 0) {
					return forward(ConstantesNavegacaoSae.RELATORIO_ACESSO_RU);
				}
				else{
					addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
					return null;
				}
			}
			else{
				acessosRuDetalhes = dao.relatorioAcessoRUCatracaDetalhes(inicio, fim);
				if (acessosRuDetalhes.size() > 0) {
					return forward(ConstantesNavegacaoSae.RELATORIO_DETALHES_ACESSO_RU);
				}
				else{
					addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
					return null;
				}
			}
		}else {
			addMensagens(lista);
			return null;
		}	

	}
	
	/**
	 * gera relatório acessos ao RU detalhado.
	 * @param periodo
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String relatorioAcessosRuDetalhes() throws HibernateException, DAOException{
		Date inicio = null;
		Date fim = null;

		ListaMensagens lista = new ListaMensagens();			
		inicio = this.buscaDataInicio;
		fim = this.buscaDataFim;
		if(inicio == null || fim == null){
			return null;
		}
		if (lista.isEmpty()) {
			RelatorioAcessoRUDao dao = getDAO(RelatorioAcessoRUDao.class);
			acessosRuDetalhes = dao.relatorioAcessoRUCatracaDetalhes(inicio, fim);
			if (acessosRuDetalhes.size() > 0) {
				return forward(ConstantesNavegacaoSae.RELATORIO_DETALHES_ACESSO_RU);
			}
			else{
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		}else {
			addMensagens(lista);
			return null;
		}	
	}

	public Date getBuscaDataInicio() {
		return buscaDataInicio;
	}

	public void setBuscaDataInicio(Date buscaDataInicio) {
		this.buscaDataInicio = buscaDataInicio;
	}

	public Date getBuscaDataFim() {
		return buscaDataFim;
	}

	public void setBuscaDataFim(Date buscaDataFim) {
		this.buscaDataFim = buscaDataFim;
	}

	public List<Map<String, Object>> getAcessosRu() {
		return acessosRu;
	}

	public void setAcessosRu(List<Map<String, Object>> acessosRu) {
		this.acessosRu = acessosRu;
	}

	public List<Map<String, Object>> getAcessosRuDetalhes() {
		return acessosRuDetalhes;
	}

	public void setAcessosRuDetalhes(List<Map<String, Object>> acessosRuDetalhes) {
		this.acessosRuDetalhes = acessosRuDetalhes;
	}

	public boolean isCheckDetalhes() {
		return checkDetalhes;
	}

	public void setCheckDetalhes(boolean checkDetalhes) {
		this.checkDetalhes = checkDetalhes;
	}


}
