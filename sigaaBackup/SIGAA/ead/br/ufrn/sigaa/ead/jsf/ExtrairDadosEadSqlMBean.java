package br.ufrn.sigaa.ead.jsf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ead.ExtrairDadosEadSqlDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ead.dominio.TabelaDadosEadSql;

/**
 * Managed Bean que gerencia a extração de dados relacionados à EAD em formato SQL.
 * 
 * @author Fred_Castro
 *
 */

@Component("extrairDadosEadSql")
@Scope("request")
public class ExtrairDadosEadSqlMBean extends SigaaAbstractController <TabelaDadosEadSql> {
	
	private List <TabelaDadosEadSql> tabelas;
	
	public static final String PAGINA_ESCOLHER_TABELAS = "/ead/relatorios/formEscolherTabelasSql.jsp";
	
	public boolean metropoleDigital;
	
	public ExtrairDadosEadSqlMBean() throws ArqException {}
	
	/**
	 * Gera o arquivo contendo os inserts com os dados das tabelas selecionadas.
	 * Método chamado a partir da seguinte JSP: /ead/relatorios/formEscolherTabelasSql.jsp
	 * 
	 * @return
	 * @throws ArqException
	 * @throws IOException 
	 */
	public String gerar () throws ArqException, IOException {
		
		// TODO Quando for informado qual papel será necessário, chegar aqui se o usuário pode pegar os dados da metrópole digital.
//		if (!metropoleDigital)
			checkRole(SigaaPapeis.SEDIS);
//		else
//			checkRole();
		
		ExtrairDadosEadSqlDao dao = null;
		
		try {
			dao = getDAO(ExtrairDadosEadSqlDao.class);
			
			HttpServletResponse response = getCurrentResponse();
			

			List <byte []> arquivos = new ArrayList <byte []> ();
			
			for (TabelaDadosEadSql t : tabelas)
				if (t.isSelecionada())
					arquivos.add(dao.extraiDadoTabelaEad(t.getId(), metropoleDigital).getBytes());
			
			if (!arquivos.isEmpty()){
				response.setCharacterEncoding("iso-8859-1");
				response.setHeader("Content-disposition", "attachment; filename=\"insertsEad_"+((int)(Math.random()*1000))+".sql\"");
				
				for (byte [] a : arquivos)
					response.getOutputStream().write(a, 0, a.length);
				
				FacesContext.getCurrentInstance().responseComplete();
			} else
				addMensagemErro("Selecione pelo menos uma tabela.");
				
			return null;
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Exibe o formulário para exibir as tabelas para que o usuário selecione quais deseja extrair.
	 * Método chamado a partir da seguinte JSP: /ead/menu.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String selecionarTabelasSedis () throws ArqException {
		tabelas = TabelaDadosEadSql.gerarLista(false);
		metropoleDigital = false;
		return forward (PAGINA_ESCOLHER_TABELAS);
	}
	
	/**
	 * Exibe o formulário para exibir as tabelas para que o usuário selecione quais deseja extrair.
	 * Método chamado a partir da seguinte JSP: /ead/menu.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String selecionarTabelasMetropoleDigital () throws ArqException {
		tabelas = TabelaDadosEadSql.gerarLista(true);
		metropoleDigital = true;
		return forward (PAGINA_ESCOLHER_TABELAS);
	}
		
	public List<TabelaDadosEadSql> getTabelas() {
		return tabelas;
	}
	
	public void setTabelas(List<TabelaDadosEadSql> tabelas) {
		this.tabelas = tabelas;
	}

	public boolean isMetropoleDigital() {
		return metropoleDigital;
	}

	public void setMetropoleDigital(boolean metropoleDigital) {
		this.metropoleDigital = metropoleDigital;
	}
}