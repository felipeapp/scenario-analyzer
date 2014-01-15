/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 08/04/2010
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.relatorios.TaxaConclusaoDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.graduacao.dominio.TaxaConclusao;
import br.ufrn.sigaa.ensino.graduacao.negocio.CalculaTaxaConclusao;
import br.ufrn.sigaa.ensino.graduacao.relatorios.dominio.RelatorioAnaliticoInsucessoConclusao;

/**
 * MBean responsável por gerar os Relatórios vinculado a taxa de conclusão de Graduação.
 *
 * @author Arlindo Rodrigues
 *
 */
@Component("relatorioTaxaConclusao") @Scope("session")
public class RelatorioTaxaConclusaoMBean extends AbstractRelatorioGraduacaoMBean {
	
	/** Indica o caminho da JSP da tela de geração do relatório de taxa de conclusão */
	private final String JSP_SELECIONA_TAXA_CONCLUSAO = "/graduacao/relatorios/taxa_conclusao/seleciona_taxa_conclusao.jsp";
	/** Indica o caminho da JSP do relatório de taxa de conclusão */
	private final String JSP_RELATORIO_TAXA_CONCLUSAO = "/graduacao/relatorios/taxa_conclusao/lista_taxa_conclusao.jsp";
	
	/** Indica o caminho da JSP da tela de geração do relatório de taxa de conclusão */
	private final String JSP_SELECIONA_INSUCESSO_CONCLUSAO = "/graduacao/relatorios/taxa_conclusao/seleciona_insucesso_conclusao.jsp";	
	/** Indica o caminho da JSP do Relatório Analítico de Insucesso de Conclusão */
	private final String JSP_RELATORIO_INSUCESSO_CONCLUSAO = "/graduacao/relatorios/taxa_conclusao/lista_insucesso_conclusao.jsp";
	/** Indica o caminho da JSP do Relatório Analítico de Insucesso de Conclusão */
	private final String JSP_DETALHAMENTO_TAXA_CONCLUSAO = "/graduacao/relatorios/taxa_conclusao/detalhe_taxa_conclusao.jsp";	
	/** Link do relatório de alunos ingressantes. */
	private static final String JSP_RELATORIO_INGRESSANTES = "/graduacao/relatorios/taxa_conclusao/lista_ingressantes.jsp";
	
	public int getIntervaloContablizacao() {
		return 4;
	}
	
	/** Atributo para auxiliar na geração do relatório de detalhes dos alunos */
	private String campoLink;	
	
	/** Atributo que identifica a Unidade, para auxiliar na geração do relatório de detalhes dos alunos */
	private int idUnidade;
	/** Ano de base para geração do relatório de taxa de conclusão */
	private int anoBase;
	
	
	/** Lista de dados do relatório de taxa de conclusão. */
	private List<TaxaConclusao> taxaConclusao = new ArrayList<TaxaConclusao>();
	/** Lista de dados do relatório de insucesso de conclusão (Relatório complementar de taxa de conclusão). */	
	private List<RelatorioAnaliticoInsucessoConclusao> listaInsucesso = new ArrayList<RelatorioAnaliticoInsucessoConclusao>();
	/** Lista de Taxa de Conclusão Detalhada */
	private List <Map<String, Object>> taxaConclusaoDetalhada = new ArrayList<Map<String,Object>>();
	
	/** Representa os Lista de Ingressantes. */
	private List<Map<String,Object>> listaDiscente = new ArrayList<Map<String,Object>>();
	
	/** Listagem com o resultado da consulta que será exibida no relatório */
	private List<Map<String,Object>> lista = new ArrayList<Map<String,Object>>();	
	
	/**
	 * Faz o redirecionamento para a tela de preenchimento dos dados para a geração do relatório de taxa de conclusão.
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/portais/rh_plan/abas/graduacao.jsp </li>
	 *   <li>/portais/relatorios/abas/ensino.jsp </li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */	
	public String iniciarRelatorioTaxaConclusao() throws SegurancaException{
		ano = getCalendarioVigente().getAno() -1;
		anoFim = getCalendarioVigente().getAno();
		return forward(JSP_SELECIONA_TAXA_CONCLUSAO);
	}
	
	
	/**
	 * Gera o relatório de taxa de conclusão
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/graduacao/relatorios/taxa_conclusao/seleciona_taxa_conclusao.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioTaxaConclusao() throws DAOException{		
        if (ano == 0)
        	addMensagemErro("O campo Ano Início é obrigatório");
        if (anoFim == 0)
        	addMensagemErro("O campo Ano Fim é obrigatório");
        	
      	if (ano > anoFim )
      		erros.addErro("O ano Inicial deve ser menor que o ano Final");
      	
      	if ((ano < 1900) || (anoFim < 1900)){
      		erros.addErro("Ano Inválido.");
      	}
      	
        if (!hasErrors()){
        	taxaConclusao = CalculaTaxaConclusao.calculaTaxaConclusaoAnos(ano, anoFim);	
        	if (taxaConclusao.size() > 0)
        		return forward(JSP_RELATORIO_TAXA_CONCLUSAO);         	
			else{
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);			
				return null;        	
			}
        } else 
        	return null;
	}
	
	/**
	 * Gera o relatório de concluintes já existente, apenas passando os parâmetros
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/graduacao/relatorios/taxa_conclusao/lista_taxa_conclusao.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioConcluintes () throws DAOException{
		RelatoriosPlanejamentoMBean bean = getMBean("relatoriosPlanejamento");
			
		bean.setAno(getParameterInt("ano"));
		
		//Todos da UFRN
		centro.setId(-1);
		
		bean.setUnidade(centro);	
		
		return bean.gerarRelatorio371JSP();
	}	
	
	
	/**
	 * Faz o redirecionamento para a tela de preenchimento dos dados para a geração do Relatório Analítico de Insucesso de Concluintes.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/portais/relatorios/abas/ensino.jsp </li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */	
	public String iniciarRelatorioInsucessoConcluintes() throws SegurancaException{
		ano = getCalendarioVigente().getAno()-5;
		return forward(JSP_SELECIONA_INSUCESSO_CONCLUSAO);
	}	
	
	/**
	 * Gera o Relatório Analítico de Insucesso de Concluintes.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/portais/relatorios/abas/ensino.jsp </li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioInsucessoConcluintes() throws DAOException{
		anoBase = ano;
    	
		int anoParam = getParameterInt("ano", 0);		
    	if (anoParam > 0)      	
    		anoBase = anoParam;
    	
        if (anoBase == 0){
        	addMensagemErro("O campo Ano é obrigatório");
        }        	
     	
      	if (anoBase < 1900){
      		erros.addErro("Ano Inválido.");
      	}
      	
        if (!hasErrors()){
        	DiscenteDao dao = getDAO(DiscenteDao.class);
        	try{
        		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();
        		resultado = dao.findQuantitativoSituacoesInsucesso(anoBase, getIntervaloContablizacao());
        			
            	if (resultado.size() > 0){
            		listaInsucesso.clear();
            		for (Map<String, Object> linha : resultado){
            			RelatorioAnaliticoInsucessoConclusao relatorio = new RelatorioAnaliticoInsucessoConclusao();
            			
            			Curso curso = new Curso();
            			curso.setId(Integer.parseInt(""+ linha.get("id_curso")));
            			curso.setNome(String.valueOf(linha.get("nome")));
            			
            			relatorio.setCurso(curso);
            			relatorio.setDesistencia(Integer.parseInt(""+linha.get("desistencias")));
            			relatorio.setEntrada(Integer.parseInt(""+linha.get("ingressantes")));
            			relatorio.setMatriculados(Integer.parseInt(""+linha.get("matriculados")));
            			relatorio.setSaida(Integer.parseInt(""+linha.get("concluintes")));
            			relatorio.setSaidaAntesPrevisto(Integer.parseInt(""+linha.get("concluintes_antes")));
            			relatorio.setOutros(Integer.parseInt(""+linha.get("outros")));
            			relatorio.setSigla(String.valueOf(linha.get("sigla")));
            			relatorio.setIdUnidade(Integer.parseInt(""+linha.get("id_unidade")));            			
            			listaInsucesso.add(relatorio);            			
            		}
            		
            		Collections.sort (listaInsucesso, new Comparator<RelatorioAnaliticoInsucessoConclusao>() {  
    		             public int compare(RelatorioAnaliticoInsucessoConclusao o1, RelatorioAnaliticoInsucessoConclusao o2) {      		                 
							return new CompareToBuilder()
							.append(o1.getSigla(), o2.getSigla())
							.append(o1.getCurso().getDescricao(), o2.getCurso().getDescricao())
							.toComparison();    		                 
    		             }
    		        });              		            	
            		return forward(JSP_RELATORIO_INSUCESSO_CONCLUSAO);         	            		
            	}else{
    				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);			
    				return null;        	
    			}        			
        	} finally{
        		if (dao != null)
        			dao.close();
        	}
        } else 
        	return null;		
	}
	
	/**
	 * Retorna a lista de ingressantes diferente de vestibular <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/relatorios/taxa_conclusao/lista_taxa_conclusao</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String listaIngressantes() throws DAOException{
		ano = getParameterInt("ano",0);
		periodo = getParameterInt("semestre", 0);
		
		if (ano == 0 || periodo == 0){
			return null;
		}
		
		TaxaConclusaoDao dao = getDAO(TaxaConclusaoDao.class);
		try {
			listaDiscente = dao.findListaIngressantesTaxaConclusao(ano, periodo);
			if (listaDiscente.isEmpty()){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			
			lista = new ArrayList<Map<String,Object>>();
			for (Map<String, Object> linha : listaDiscente){
				
				int id = Integer.parseInt(""+ linha.get("id_forma_ingresso") );
				String descricao = (String) linha.get("forma_ingresso_descricao");
				
				boolean encontrou = false;
				for (Map<String, Object> l : lista){
					if (((Integer) l.get("id_forma_ingresso")) == id){
						l.put("total", ((Integer) l.get("total")) + 1);	
						encontrou = true;
						break;
					}
				}
				
				if (!encontrou){
					HashMap<String, Object> campos = new HashMap<String, Object>();    				
					campos.put("id_forma_ingresso", id);
					campos.put("descricao", descricao);
					campos.put("total", 1);	
					lista.add(campos);
				}
				
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward(JSP_RELATORIO_INGRESSANTES); 
	}	
	
	/**
	 * Retorna a lista de ingressantes diferente de vestibular <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/relatorios/taxa_conclusao/lista_taxa_conclusao</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String listaVagasOfertadas() throws DAOException{
		ano = getParameterInt("ano",0);
		
		if ( ano == 0 ){
			return null;
		}
		
		TaxaConclusaoDao dao = getDAO(TaxaConclusaoDao.class);
		try {
			lista = dao.findListaVagasOfertadasTaxaConclusao(ano);
			if (lista.isEmpty()){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward("/graduacao/relatorios/taxa_conclusao/lista_vagas_ofertadas.jsp"); 
	}		
	
	/**
	 * Exibe os detalhes dos alunos referente a coluna clicada no relatório de insucesso de conclusão.
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/graduacao/relatorios/taxa_conclusao/lista_insucesso_conclusao.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String exibirDetalhesInsucesso() throws DAOException{
		RelatoriosPlanejamentoMBean bean = getMBean("relatoriosPlanejamento");
		
		bean.setAno(ano);
		
		List<Map<String,Object>> lista = new ArrayList<Map<String,Object>>();
		
		DiscenteDao dao = getDAO(DiscenteDao.class);

		try{
			if (campoLink.equals("entrada")){				
				bean.setTituloRelatorio("Lista de Alunos Ingressantes");				
				lista = dao.findListaDiscentesInsucesso(ano, idCurso,1,idUnidade, getIntervaloContablizacao());
			} else if (campoLink.equals("saida")){
				bean.setTituloRelatorio("Lista de Alunos Concluintes");				
				lista = dao.findListaDiscentesInsucesso(ano, idCurso, 2, idUnidade, getIntervaloContablizacao());
			} else if (campoLink.equals("desistencia")){
				lista = dao.findListaDiscentesInsucesso(ano, idCurso, 3, idUnidade, getIntervaloContablizacao());
			} else if (campoLink.equals("matriculados")){
				bean.setTituloRelatorio("Lista de Alunos Matriculados");				
				lista = dao.findListaDiscentesInsucesso(ano, idCurso, 4 , idUnidade, getIntervaloContablizacao());
			} else if (campoLink.equals("outros")){
				bean.setTituloRelatorio("Lista de Alunos Com Outras Situações");				
				lista = dao.findListaDiscentesInsucesso(ano, idCurso, 5, idUnidade, getIntervaloContablizacao());
			} else if (campoLink.equals("antesSaida")){
				bean.setTituloRelatorio("Lista de Alunos Concluintes em anos Anteriores");				
				lista = dao.findListaDiscentesInsucesso(ano, idCurso, 6, idUnidade, getIntervaloContablizacao());
			} else if (campoLink.equals("sucesso")){
				bean.setTituloRelatorio("Lista de Alunos Concluintes");				
				lista = dao.findListaDiscentesInsucesso(ano, idCurso, 7, idUnidade, getIntervaloContablizacao());
			}								
		} finally {
			if (dao != null)
				dao.close();
		}
		
		bean.setLista(lista);		
		
		return bean.exibirListaAlunos();
	}
	
	/**
	 * Exibe o detalhamento da taxa de conclusão
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/graduacao/relatorios/taxa_conclusao/lista_taxa_conclusao.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String detalhamentoTaxaConclusao() throws DAOException{
		anoBase = getParameterInt("ano", 0);
        if (anoBase == 0){
        	addMensagemErro("Ano inválido!");
        	return null;
        }
		
		TaxaConclusaoDao dao = getDAO(TaxaConclusaoDao.class);
		try {
			taxaConclusaoDetalhada = dao.findTaxaConclusaoPorCurso(anoBase);
			if (ValidatorUtil.isEmpty(taxaConclusaoDetalhada)){
				addMensagemErro(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward(JSP_DETALHAMENTO_TAXA_CONCLUSAO);
	}
	
	/**
	 * Exibe os detalhes dos alunos referente a coluna clicada no relatório de taxa de conclusão.
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/graduacao/relatorios/taxa_conclusao/detalhe_taxa_conclusao.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String exibirDiscentes() throws DAOException{
		
		int ano = getParameterInt("ano", 0);
		int idCurso = getParameterInt("idCurso", 0);
		int idGrau = getParameterInt("idGrau", 0);
		int idTurno = getParameterInt("idTurno", 0);
		int idUnidade = getParameterInt("idUnidade", 0);
		String tipo = getParameter("tipo");
		
		TaxaConclusaoDao dao = getDAO(TaxaConclusaoDao.class);
		try {
			RelatoriosPlanejamentoMBean bean = getMBean("relatoriosPlanejamento");		
			bean.setAno(ano);			
			List<Map<String,Object>> lista = new ArrayList<Map<String,Object>>();
			
			if ("C".equals(tipo)){
				lista = dao.findListaDiscentes(ano, idCurso, idUnidade, 1, idGrau, idTurno);
			} else if ("I".equals(tipo)){
				lista = dao.findListaDiscentes(ano, idCurso, idUnidade, 2, idGrau, idTurno);
			}	
			
			if (ValidatorUtil.isEmpty(lista)){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			
			bean.setLista(lista);					
			return bean.exibirListaAlunos();
		} finally {
			if (dao != null)
				dao.close();
		}
		
	}	
	
	public List<TaxaConclusao> getTaxaConclusao() {
		return taxaConclusao;
	}

	public void setTaxaConclusao(List<TaxaConclusao> taxaConclusao) {
		this.taxaConclusao = taxaConclusao;
	}


	public List<RelatorioAnaliticoInsucessoConclusao> getListaInsucesso() {
		return listaInsucesso;
	}


	public void setListaInsucesso(
			List<RelatorioAnaliticoInsucessoConclusao> listaInsucesso) {
		this.listaInsucesso = listaInsucesso;
	}


	public String getCampoLink() {
		return campoLink;
	}


	public void setCampoLink(String campoLink) {
		this.campoLink = campoLink;
	}


	public int getIdUnidade() {
		return idUnidade;
	}


	public void setIdUnidade(int idUnidade) {
		this.idUnidade = idUnidade;
	}


	public int getAnoBase() {
		return anoBase;
	}


	public void setAnoBase(int anoBase) {
		this.anoBase = anoBase;
	}


	public List<Map<String, Object>> getTaxaConclusaoDetalhada() {
		return taxaConclusaoDetalhada;
	}


	public void setTaxaConclusaoDetalhada(
			List<Map<String, Object>> taxaConclusaoDetalhada) {
		this.taxaConclusaoDetalhada = taxaConclusaoDetalhada;
	}


	public List<Map<String, Object>> getListaDiscente() {
		return listaDiscente;
	}


	public void setListaDiscente(List<Map<String, Object>> listaDiscente) {
		this.listaDiscente = listaDiscente;
	}


	public List<Map<String, Object>> getLista() {
		return lista;
	}


	public void setLista(List<Map<String, Object>> lista) {
		this.lista = lista;
	}
}
