package br.ufrn.sigaa.pesquisa.jsf.relatorios;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.pesquisa.RelatorioCNPQDao;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.AbstractRelatorioGraduacaoMBean;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.relatorios.ColunaItemRelatorioCNPQ;
import br.ufrn.sigaa.pesquisa.relatorios.LinhaRelatorioCNPQ;

/**
 * Maneger Bean responsável pela geração dos relatório CNPq
 * 
 * @author Jean Guerethes
 */
@Component("relatoriosCNPQMBean") @Scope("session")
public class RelatoriosCNPQMBean extends AbstractRelatorioGraduacaoMBean {
	
	private boolean exibirTipoBolsa;
	private boolean exibirEditalPesquisa;
	private int relatorio;
	private String nomeRelatorio;
	private TipoBolsaPesquisa tipoBolsaPesquisa;
	private EditalPesquisa editalPesquisa;
	private RelatorioCNPQDao dao;
	private Collection<LinhaRelatorioCNPQ> corpoDiscenteDocente;

	public static final int CORPO_DISCENTE = 1;
	public static final int NUMERO_BOLSAS_INSTITUICAO = 2;
	public static final int PESQUISADORES_PRODUTIVIDADE_CNPQ = 3;
	public static final int BOLSISTAS_GRANDE_AREA = 4;
	public static final int ENVOLVIDOS_COM_GRUPO_PESQUISA = 5;
	
	private void clear() {
		tipoBolsaPesquisa = new TipoBolsaPesquisa();
		editalPesquisa = new EditalPesquisa();
		corpoDiscenteDocente = new ArrayList<LinhaRelatorioCNPQ>();
		exibirEditalPesquisa = false;
		exibirTipoBolsa = false;
	}
	
	@Override
	public String getViewPage() {
		if ( corpoDiscenteDocente.isEmpty() ) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		} else		
			return forward(getDirBase() + "/rel_corpo_discente.jsp");
	}
	
	@Override
	public String getDirBase() {
		return "/pesquisa/relatorio_cnpq";
	}
	
	public String iniciarCorpoDiscente() {
		clear();
		nomeRelatorio = "CORPO DISCENTE DA INSTITUIÇÃO";
		relatorio = CORPO_DISCENTE;
		exibirTipoBolsa = true;
		return forward(getDirBase() + "/form_corpo_discente.jsp");
	}

	public String iniciarBolsasInstituicao() {
		clear();
		nomeRelatorio = "NÚMERO DE BOLSAS DE OUTROS PROGRAMAS DE INICIAÇÃO AO DESENVOLVIMENTO";
		relatorio = NUMERO_BOLSAS_INSTITUICAO;
		exibirTipoBolsa = true;
		return forward(getDirBase() + "/form_corpo_discente.jsp");
	}

	public String iniciarPesquisadoresCNPQ() throws DAOException {
		clear();
		nomeRelatorio = "PESQUISADORES PRODUTIVIDADE CNPq";
		relatorio = PESQUISADORES_PRODUTIVIDADE_CNPQ;
		exibirEditalPesquisa = true;
		return forward(getDirBase() + "/form_corpo_discente.jsp");
	}

	public String iniciarRelatorioGrandeArea() throws DAOException {
		clear();
		nomeRelatorio = "BOLSISTAS DISTRIBUÍDAS POR ÁREA DE CONHECIMENTO";
		relatorio = BOLSISTAS_GRANDE_AREA;
		exibirTipoBolsa = true;
		return forward(getDirBase() + "/form_corpo_discente.jsp");
	}

	public String iniciarEnvolvidosComPesquisa() throws DAOException {
		clear();
		nomeRelatorio = "PESQUISA CIENTÍFICA DESENVOLVIDA NA INSTITUIÇÃO";
		relatorio = ENVOLVIDOS_COM_GRUPO_PESQUISA;
		exibirTipoBolsa = true;
		return forward(getDirBase() + "/form_corpo_discente.jsp");
	}
	
	public void gerarRelatorio() throws DAOException {
		if ( relatorio == CORPO_DISCENTE )
			gerarCorpoDiscente();
		if ( relatorio == NUMERO_BOLSAS_INSTITUICAO )
			gerarNumeroBolsasProgramasIniciacaoDesenvolvimento();
		if ( relatorio == PESQUISADORES_PRODUTIVIDADE_CNPQ )
			gerarNumeroPesquisadoresCNPQ();
		if ( relatorio == BOLSISTAS_GRANDE_AREA )
			gerarRelatorioBolsistaAreaConhecimento();
		if (relatorio == ENVOLVIDOS_COM_GRUPO_PESQUISA)
			gerarEnvolvidosComPesquisa();
	}
	
	public String gerarCorpoDiscente() throws DAOException {
		dao = getDAO(RelatorioCNPQDao.class);
		try {
			tipoBolsaPesquisa = dao.findByPrimaryKey(tipoBolsaPesquisa.getId(), TipoBolsaPesquisa.class);
			corpoDiscenteDocente = dao.findCorpoDiscente(tipoBolsaPesquisa);
		} finally {
			dao.close();
		}
		
		return getViewPage();
	}

	public String gerarCorpoDocente() throws DAOException {
		dao = getDAO(RelatorioCNPQDao.class);
		try {
			exibirEditalPesquisa = false;
			exibirTipoBolsa = false;
			corpoDiscenteDocente = dao.findCorpoDocente();
			nomeRelatorio = "CORPO DOCENTE DA INSTITUIÇÃO";
		} finally {
			dao.close();
		}
		
		return getViewPage();
	}

	/**
	 * Gera uma relatório com os grupos de pesquisa linhas de pesquisa e com o número de doutores envolvidos com Pesquisa.
	 */
	public String gerarEnvolvidosComPesquisa() throws DAOException {
		dao = getDAO(RelatorioCNPQDao.class);
		try {
			exibirEditalPesquisa = false;
			exibirTipoBolsa = true;
			tipoBolsaPesquisa = dao.findByPrimaryKey(tipoBolsaPesquisa.getId(), TipoBolsaPesquisa.class);
			corpoDiscenteDocente = dao.findGruposELinhasPesquisa(tipoBolsaPesquisa);
			nomeRelatorio = "PESQUISA CIENTÍFICA DESENVOLVIDA NA INSTITUIÇÃO";
		} finally {
			dao.close();
		}
		
		return getViewPage();
	}

	public String gerarNumeroBolsasProgramasIniciacaoDesenvolvimento() throws DAOException {
		dao = getDAO(RelatorioCNPQDao.class);
		try {
			tipoBolsaPesquisa = dao.findByPrimaryKey(tipoBolsaPesquisa.getId(), TipoBolsaPesquisa.class);
			corpoDiscenteDocente = dao.findNumeroBolsasIniciacaoCientificaInstituicao(tipoBolsaPesquisa);
		} finally {
			dao.close();
		}
		
		return getViewPage();
	}

	public String gerarNumeroPesquisadoresCNPQ() throws DAOException {
		dao = getDAO(RelatorioCNPQDao.class);
		try {
			editalPesquisa = getGenericDAO().findByPrimaryKey(editalPesquisa.getId(), EditalPesquisa.class);
			corpoDiscenteDocente = dao.findPesquisadoresBolsasAprovadas(editalPesquisa);
		} finally {
			dao.close();
		}
		
		return getViewPage();
	}

	public String gerarRelatorioBolsistaAreaConhecimento() throws DAOException {
		dao = getDAO(RelatorioCNPQDao.class);
		try {
			tipoBolsaPesquisa = dao.findByPrimaryKey(tipoBolsaPesquisa.getId(), TipoBolsaPesquisa.class);
			corpoDiscenteDocente = dao.findBolsistaAreaConhecimento(tipoBolsaPesquisa);
		} finally {
			dao.close();
		}
		
		return getViewPage();
	}
	
	public String gerarComiteExterno() throws DAOException {
		dao = getDAO(RelatorioCNPQDao.class);
		try {
			corpoDiscenteDocente = dao.findComiteExterno();
			nomeRelatorio = "COMPONENTES DO COMITÊ EXTERNO";
		} finally {
			dao.close();
		}
		
		return getViewPage();
	}

	public String gerarComiteInstitucional() throws DAOException {
		dao = getDAO(RelatorioCNPQDao.class);
		try {
			corpoDiscenteDocente = dao.findComiteInstituicional();
			nomeRelatorio = "COMPONENTES DO COMITÊ INSTITUCIONAL"; 
		} finally {
			dao.close();
		}
		
		return getViewPage();
	}
	
	public String gerarIndicadoresGerais() throws DAOException {
		dao = getDAO(RelatorioCNPQDao.class);
		try {
			corpoDiscenteDocente = dao.indicadoresGerais();
			nomeRelatorio = "Indicadores Gerais"; 
		} finally {
			dao.close();
		}
		return getViewPage();
	}
	
	public String getCabecalho() {
		String cabecalho = "";
		for (LinhaRelatorioCNPQ linha : corpoDiscenteDocente) {
			Set<ColunaItemRelatorioCNPQ> chave = linha.getCabecalho().keySet();
			for ( ColunaItemRelatorioCNPQ elementCabecalho : chave ) {
				if ( !cabecalho.equals( elementCabecalho ) ) {
					if (linha.getCabecalho().get(elementCabecalho))
						cabecalho += "<td style='text-align: right;'>" + elementCabecalho + "</td>";
					else
						cabecalho += "<td>" + elementCabecalho + "</td>";
				} else if ( cabecalho.equals( "" ) )
					cabecalho += "<td> </td>";
			}
			return cabecalho;
		}
		return cabecalho;
	}

	public String getValores() {
		String valores = "";
		for (LinhaRelatorioCNPQ linha : corpoDiscenteDocente) {
				if ( linha.getDescricaoLinha() != null && !linha.getDescricaoLinha().equals("") )
					valores += "<tr><td><b>" + linha.getDescricaoLinha() + "</b></td>";
				else
					valores += "<tr>";
			
			for (String elementValores : linha.getValoresRelatorio()) {
				if ( elementValores.equals("-") )
					valores += "<td style='text-align: center;'>" + elementValores + "</td>";
				else if ( StringUtils.hasNumber(elementValores, elementValores.length()) || elementValores.contains(" / ") )
					valores += "<td style='text-align: right;'>" + elementValores + "</td>";
				else
					valores += "<td style='text-align: left;'>" + elementValores + "</td>";
			}
		}
		valores += "</tr>";
		return valores;
	}
	
	public boolean isPibic(){
		return tipoBolsaPesquisa.getId() == TipoBolsaPesquisa.PIBIC; 
	}

	public boolean isPibit(){
		return tipoBolsaPesquisa.getId() == TipoBolsaPesquisa.PIBIT; 
	}

	public TipoBolsaPesquisa getTipoBolsaPesquisa() {
		return tipoBolsaPesquisa;
	}

	public void setTipoBolsaPesquisa(TipoBolsaPesquisa tipoBolsaPesquisa) {
		this.tipoBolsaPesquisa = tipoBolsaPesquisa;
	}

	public String getNomeRelatorio() {
		return nomeRelatorio;
	}

	public String getNomeRelatorioCabecalho() {
		return StringUtils.capitaliseAllWords( nomeRelatorio.toLowerCase() );
	}
	
	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}

	public Collection<LinhaRelatorioCNPQ> getCorpoDiscenteDocente() {
		return corpoDiscenteDocente;
	}

	public void setCorpoDiscenteDocente(
			Collection<LinhaRelatorioCNPQ> corpoDiscenteDocente) {
		this.corpoDiscenteDocente = corpoDiscenteDocente;
	}

	public EditalPesquisa getEditalPesquisa() {
		return editalPesquisa;
	}

	public void setEditalPesquisa(EditalPesquisa editalPesquisa) {
		this.editalPesquisa = editalPesquisa;
	}

	public boolean isExibirTipoBolsa() {
		return exibirTipoBolsa;
	}

	public void setExibirTipoBolsa(boolean exibirTipoBolsa) {
		this.exibirTipoBolsa = exibirTipoBolsa;
	}

	public boolean isExibirEditalPesquisa() {
		return exibirEditalPesquisa;
	}

	public void setExibirEditalPesquisa(boolean exibirEditalPesquisa) {
		this.exibirEditalPesquisa = exibirEditalPesquisa;
	}
	
}