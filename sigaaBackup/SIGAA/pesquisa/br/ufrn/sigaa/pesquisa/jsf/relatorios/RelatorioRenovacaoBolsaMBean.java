/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 02/02/2010
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf.relatorios;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.pesquisa.RelatorioBolsaPibicDao;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.AbstractRelatorioGraduacaoMBean;
import br.ufrn.sigaa.mensagens.MensagensPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;

/**
 * Controlador responsável pela geração do relatório quantitativo das renovações de bolsa.
 * 
 * @author Jean Guerethes
 */
@Component("relatorioRenovacaoBolsaMBean") @Scope("request")
public class RelatorioRenovacaoBolsaMBean extends AbstractRelatorioGraduacaoMBean {

	/**identificador de distribuição de cotas de bolsas*/	
	private Integer cotaBolsaAtual;
	/**identificador de distribuição de cotas de bolsas*/
	private Integer cotaBolsaAnterior;
	
	/** Dados do relatório. */
	private List<Map<String,Object>> listaBolsaPibic = new ArrayList<Map<String,Object>>();
	/**define o caminho dos links do relatorio de renovação de bolsa*/
	public static final String CONTEXTO = "/pesquisa/relatorios/bolsa_pibic/";
	/**informações referentes à vigência da distribuição de cotas de bolsas inicial*/
	private CotaBolsas cotaInicial;
	/**informações referentes à vigência da distribuição de cotas de bolsas final*/
	private CotaBolsas cotaFinal;

	/**
	 * Serve para direcionar o usuário para a tela, onde é possível a seleção da bolsa para posteriormente ser possível a 
	 * geração do relatório.
	 * 
	 * JSP:
	 * <br>
	 * /SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/pesquisa/menu/relatorio.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String selecionaQuantitativoBolsaPibic() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA,SigaaPapeis.PORTAL_PLANEJAMENTO);
		return forward(CONTEXTO + "seleciona_bolsa_pibic.jsf");
	}
	
	/**
	 * Responsável pela validação dos dados inseridos e pela realização da consulta dos
	 * Bolsistas.  
	 * 
	 * JSP:
	 * <br>
	 *  /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/relatorios/bolsa_pibic/seleciona_bolsa_pibic.jsp
	 *  
	 * @return
	 * @throws DAOException
	 */
	public String findQuantitativoBolsaPibic() throws DAOException{
		
		if (cotaBolsaAnterior == 0 && cotaBolsaAtual == 0) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Cota Bolsa Atual");
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Cota Bolsa Inicial");
			return forward(CONTEXTO + "seleciona_bolsa_pibic.jsf");
		}
		
		if (cotaBolsaAnterior == 0 || cotaBolsaAtual == 0) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, cotaBolsaAnterior == 0 ? "Cota Bolsa Inicial" : "Cota Bolsa Atual");
			return forward(CONTEXTO + "seleciona_bolsa_pibic.jsf");
		}
	
		GenericDAO dao1 = getDAO(GenericDAOImpl.class);
		
		cotaInicial = dao1.findByPrimaryKey(cotaBolsaAnterior, CotaBolsas.class); 
		cotaFinal  = dao1.findByPrimaryKey(cotaBolsaAtual, CotaBolsas.class);
		dao1.close();
		
		RelatorioBolsaPibicDao dao = getDAO(RelatorioBolsaPibicDao.class);
		listaBolsaPibic = dao.findBolsaPibic(cotaBolsaAtual, cotaBolsaAnterior);
		
		if (listaBolsaPibic.size() == 0) {
			addMensagem(MensagensPesquisa.SEM_COTAS_RELATORIO_BOLSA_PIBIC);
			return forward(CONTEXTO + "seleciona_bolsa_pibic.jsf" );
		}
		dao.close();
		return forward(CONTEXTO + "relatorio_quantitativo_bolsa_pibic.jsf");
	}
	
	public Collection<SelectItem> getAllCotas() {
		return getAll(CotaBolsas.class, "id", "descricao");
	}

	public Integer getCotaBolsaAtual() {
		return cotaBolsaAtual;
	}

	public void setCotaBolsaAtual(Integer cotaBolsaAtual) {
		this.cotaBolsaAtual = cotaBolsaAtual;
	}

	public Integer getCotaBolsaAnterior() {
		return cotaBolsaAnterior;
	}

	public void setCotaBolsaAnterior(Integer cotaBolsaAnterior) {
		this.cotaBolsaAnterior = cotaBolsaAnterior;
	}

	public List<Map<String, Object>> getListaBolsaPibic() {
		return listaBolsaPibic;
	}

	public void setListaBolsaPibic(List<Map<String, Object>> listaBolsaPibic) {
		this.listaBolsaPibic = listaBolsaPibic;
	}

	public CotaBolsas getCotaInicial() {
		return cotaInicial;
	}

	public void setCotaInicial(CotaBolsas cotaInicial) {
		this.cotaInicial = cotaInicial;
	}

	public CotaBolsas getCotaFinal() {
		return cotaFinal;
	}

	public void setCotaFinal(CotaBolsas cotaFinal) {
		this.cotaFinal = cotaFinal;
	}

}