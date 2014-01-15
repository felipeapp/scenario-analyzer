/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 11/11/2009
 *
 */
package br.ufrn.sigaa.projetos.relatorios.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.AbstractRelatorioGraduacaoMBean;
import br.ufrn.sigaa.projetos.dominio.Edital;

/**
 * Controller responsável pela geração de diversos relatórios de projetos.
 * 
 *  @author Jean Guerethes
 */

@Component("relatoriosAcaoAcademica") 
@Scope("request")
public class RelatorioProjetoMBean extends AbstractRelatorioGraduacaoMBean{

	private String CONTEXTO = "/projetos/Relatorios/";
	
	private String RELATORIO_QUANT_PROJ_REUNI = "relatorio_quant_proj_sub_reuni.jsp";
	private String SELECIONA_QUANT_PROJ_REUNI = "seleciona_quant_proj_sub_reuni.jsp";
	
	private Integer parametro;
	
	private Edital edital;
	
	/** Dados do relatório. */
	private List<Map<String,Object>> listaRelatorio = new ArrayList<Map<String,Object>>();
	
	
	/**
	 * Serve para chegar se o usuário tem os papéis necessários para gerar o relatório 
	 * quantitativo de projetos submetidos para o edital REUNI.
	 * <br>
	 * Invocado por:<ul><li> SIGAA/app/sigaa.ear/sigaa.war/portais/docente/menu_docente.jsp</li></ul> 
	 */
	public String iniciarRelatorioQuantProjSubReuni() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA, SigaaPapeis.GESTOR_EXTENSAO, 
				SigaaPapeis.GESTOR_PESQUISA, SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
		return forward(CONTEXTO + SELECIONA_QUANT_PROJ_REUNI);
	}
	
	/**
	 * Responsável pela geração do relatório quantitativo de projetos submetidos para o edital REUNI,
	 * <br.
	 * Invocado por: <ul><li>/sigaa.war/projetos/relatorios/seleciona_quant_proj_sub_reuni.jsp</li></ul>
	 */
	public String gerarRelatorioQuantProjSubReuni() throws DAOException {
		ProjetoDao dao = getDAO(ProjetoDao.class);
		
		if (parametro == 0) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Edital");
			return null;
		}

		listaRelatorio = dao.findQuantitativoTurmaDepartamento(parametro);
		
		GenericDAO dao1 = getDAO(GenericDAOImpl.class);
		edital = dao1.findByPrimaryKey(parametro, Edital.class);
		dao1.close();
		
		if (listaRelatorio.size() == 0) {
			addMensagemErro("Não foi encotrado nenhum registro.");
			return forward(CONTEXTO + SELECIONA_QUANT_PROJ_REUNI);
		}
		return forward(CONTEXTO + RELATORIO_QUANT_PROJ_REUNI);
	}
	
	public String getCONTEXTO() {
		return CONTEXTO;
	}

	public void setCONTEXTO(String cONTEXTO) {
		CONTEXTO = cONTEXTO;
	}

	public List<Map<String, Object>> getListaRelatorio() {
		return listaRelatorio;
	}

	public void setListaRelatorio(List<Map<String, Object>> listaRelatorio) {
		this.listaRelatorio = listaRelatorio;
	}

	public Integer getParametro() {
		return parametro;
	}

	public void setParametro(Integer parametro) {
		this.parametro = parametro;
	}

	public Edital getEdital() {
		return edital;
	}

	public void setEdital(Edital edital) {
		this.edital = edital;
	}

}
