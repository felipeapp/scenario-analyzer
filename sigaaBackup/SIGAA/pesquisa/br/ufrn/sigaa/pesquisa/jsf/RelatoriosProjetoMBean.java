/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 09/11/2007
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 * MBean para a gera��o de relat�rios relacionados a projetos de pesquisa
 *
 * @author Ricardo Wendell
 */
@SuppressWarnings("unchecked")
public class RelatoriosProjetoMBean extends SigaaAbstractController {

	private int ano, mesesAno;
	private String mes; 
	private int totalPlanoTrabalho;
	
	private List<Map<String,Object>> relatorioProjeto = new ArrayList<Map<String,Object>>();
	
	/** Contrutor Padr�o */
	public RelatoriosProjetoMBean() {

	}

	/**
	 * Relat�rio com as estat�sticas de cadastros
	 * de projetos por ano
	 * <br/>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/relatorios/form_estatisticas_projetos.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String estatisticasCadastro() throws SegurancaException {

		checkRole(SigaaPapeis.GESTOR_PESQUISA);

		ProjetoPesquisaDao projetoDao = getDAO(ProjetoPesquisaDao.class);
		List<Map<String, Object>> estatisticas =  projetoDao.findEstatisticasCadastroProjetos(ano);

		if ( estatisticas.isEmpty() ) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}

		getCurrentRequest().setAttribute("estatisticas", estatisticas);
		return forward("/pesquisa/relatorios/estatisticas_projeto.jsp");
	}

	/**
	 * Relat�rio quantitativo dos projetos e planos de trabalho em um referido m�s e ano.
	 * <br/>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/relatorios/form_quant_projetos.jsp
	 *  
	 * @return
	 * @throws DAOException
	 */
	public String findQuantitativoProjeto() throws DAOException {
	
		if (ano == 0 && mesesAno == 0) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano" );
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "M�s" );
			return null;
		}

		if (ano == 0 || mesesAno == 0) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, mesesAno == 0 ? "M�s" : "Ano" );
			return null;
		}
		
		ProjetoPesquisaDao dao = getDAO(ProjetoPesquisaDao.class);
		relatorioProjeto = dao.findQuantitativoProjetos(ano, mesesAno);
		
		totalPlanoTrabalho = getGenericDAO().count("select count(*) from pesquisa.plano_trabalho where date_part('year', data_cadastro) = "+ano+" and date_part('months', data_cadastro) = " + mesesAno);
		
		mes = CalendarUtils.getNomeMes(mesesAno);
		
		if (relatorioProjeto.size() == 0) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		return forward("/pesquisa/relatorios/relatorio_quant_projetos.jsp");
	}
	
	/*
	 * GETTERS e SETTERS
	 */
	public int getAno() {
		return this.ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getMesesAno() {
		return mesesAno;
	}

	public void setMesesAno(int mesesAno) {
		this.mesesAno = mesesAno;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public List<Map<String, Object>> getRelatorioProjeto() {
		return relatorioProjeto;
	}

	public void setRelatorioProjeto(List<Map<String, Object>> relatorioProjeto) {
		this.relatorioProjeto = relatorioProjeto;
	}

	public int getTotalPlanoTrabalho() {
		return totalPlanoTrabalho;
	}

	public void setTotalPlanoTrabalho(int totalPlanoTrabalho) {
		this.totalPlanoTrabalho = totalPlanoTrabalho;
	}
	
}