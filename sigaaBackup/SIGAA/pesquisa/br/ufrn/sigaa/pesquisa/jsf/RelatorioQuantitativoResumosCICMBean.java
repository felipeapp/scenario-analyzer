/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 21/10/2009
 * 
 */

package br.ufrn.sigaa.pesquisa.jsf;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.dao.pesquisa.CongressoIniciacaoCientificaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.RelatorioQuantitativoResumosCICDao;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.AbstractRelatorioGraduacaoMBean;
import br.ufrn.sigaa.pesquisa.dominio.CongressoIniciacaoCientifica;

/**
 * MBean respons�vel por relat�rio Quantitativo de Resumos CIC no m�dulo pesquisa.
 * @author geyson
 *
 */
@Component("relatorioResumosCic") @Scope("request")
public class RelatorioQuantitativoResumosCICMBean extends  AbstractRelatorioGraduacaoMBean {
	
	private final String CONTEXTO ="/pesquisa/relatorios/quantitativo_resumos_cic/";
	private final String JSP_SELECIONA_RESUMOS_CIC = "seleciona_quantitativo_resumos_cic";
	private final String JSP_REL_RESUMOS_CIC = "rel_quantitativo_resumos_cic";
	
	private boolean filtroCongresso;
	
	CongressoIniciacaoCientifica congresso;
	
	/** Lista de dados do relat�rio. */
	private List<Map<String,Object>> lista = new ArrayList<Map<String,Object>>();
	
	/** Construtor padr�o. */
	public RelatorioQuantitativoResumosCICMBean(){
		setCongresso(new CongressoIniciacaoCientifica());
	}
	
	/**
	 * Faz o redirecionamento para a tela de preenchimento dos dados para a gera��o do relat�rio
	 * <br/><br/>
	 * JSP: sigga/pesquisa/menu.jsp
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarRelatorioResumosCic() throws SegurancaException{
		checkListRole();
		return forward(CONTEXTO+JSP_SELECIONA_RESUMOS_CIC); 
	}
	
	/**
	 * Gera um relat�rio tendo como entrada o congresso de inicia��o cient�fica. 
	 * <br/><br/>
	 * JSP: /pesquisa/relatorios/quantitativo_resumos_cic/seleciona_quantitativo_resumos_cic.jsp
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioResumosCic() throws DAOException{
		RelatorioQuantitativoResumosCICDao dao = getDAO(RelatorioQuantitativoResumosCICDao.class);
		if(isFiltrosSelecionados()){
			if(isFiltroCongresso())
				if(congresso.getId() <= 0 )
					addMensagemErro("Congresso de  Inicia��o Cient�fica: Preencha filtro selecionado.");
			if(!hasErrors()){
				validaFiltros();
				lista = dao.relatorioRemusosCIC(congresso.getId());
				if (lista.size() > 0) {
					return forward(JSP_REL_RESUMOS_CIC);
				}
				else
					addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
					return null;
			}
		}
		else
			addMensagemWarning("Selecione algum campo para gerar o relat�rio de acordo com o(s) filtro(s).");
		return null;
			

	}
	
	/**
	 * Valida se h� algum filtro selecionado.
	 * <br/><br/>
	 * JSP: N�o invocado por JSP. 
	 * @return
	 */
	public boolean isFiltrosSelecionados(){
		if(!(isFiltroCongresso()))
			return false;
		
			return true;
	}
	
	/**
	 * Valida os filtros e evita que algum par�metro seja enviado sem o filtro estar como selecionado. 
	 * <br/><br/>
	 * JSP: N�o invocado por JSP.
	 * @throws DAOException
	 */
	public void validaFiltros() throws DAOException{
		if(isFiltroCongresso()){
			CongressoIniciacaoCientificaDao dao = getDAO(CongressoIniciacaoCientificaDao.class);
			congresso = dao.findByPrimaryKey(congresso.getId(), CongressoIniciacaoCientifica.class);
 		}
		else
			congresso.setId(0);
	}
	
	
	public List<Map<String, Object>> getLista() {
		return lista;
	}

	public void setLista(List<Map<String, Object>> lista) {
		this.lista = lista;
	}

	public boolean isFiltroCongresso() {
		return filtroCongresso;
	}

	public void setFiltroCongresso(boolean filtroCongresso) {
		this.filtroCongresso = filtroCongresso;
	}

	public CongressoIniciacaoCientifica getCongresso() {
		return congresso;
	}

	public void setCongresso(CongressoIniciacaoCientifica congresso) {
		this.congresso = congresso;
	}
	
}