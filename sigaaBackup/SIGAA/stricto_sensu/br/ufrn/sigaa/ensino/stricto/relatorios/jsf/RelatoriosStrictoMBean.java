/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 29/01/2009
 */
package br.ufrn.sigaa.ensino.stricto.relatorios.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.web.tags.SelectAnoTag;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.ensino.stricto.BancaPosDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.DiscenteStrictoDao;
import br.ufrn.sigaa.arq.dao.graduacao.RelatorioDiscenteSqlDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.RelatorioDiscenteMBean;

/**
 * MBean responsável pelas consultas e geração dos relatórios da PPG
 *
 * @author Victor Hugo
 *
 */
@Component("relatoriosStricto") @Scope("session")
public class RelatoriosStrictoMBean extends SigaaAbstractController<RelatoriosStrictoMBean> {

	/** Unidade referente aos relatórios. Obs.: Usar id -1 para listar todos. */
	protected Unidade unidade;
	
	// Atributos para auxilio na geração dos relatórios
	/** Ano indicado para geração do relatório. */
	private Integer ano = CalendarUtils.getAnoAtual(); 
	/** Período indicado para geração do relatório. */
	private Integer periodo = getPeriodoAtual();
	/** Ano de início indicado para geração do relatório. */
	private Integer anoInicio;
	/** Ano final indicado para geração do relatório. */
	private Integer anoFim;
	
	// JSPs dos Relatórios 
	/** Link do formulário do relatório Quantitativo Geral de Alunos Matriculados Por Mês. */
	private static final String JSP_SELECIONA_RELATORIO_QUANT_MATRICULADOS_POR_MES = "/stricto/relatorios/seleciona_quantitativo_matriculados_por_mes.jsp";
	/** Link do formulário do relatório Quantitativo Geral de Defesas por Ano. */
	private static final String JSP_SELECIONA_RELATORIO_QUANT_DEFESAS_POR_ANO = "/stricto/relatorios/seleciona_quantitativo_defesas_por_ano.jsp";
	/** Link do relatório Quantitativo Geral de Alunos Matriculados Por Mês. */
	private static final String JSP_RELATORIO_QUANT_MATRICULADOS_POR_MES = "/stricto/relatorios/quantitativo_matriculados_por_mes.jsp";
	/** Link do relatório Quantitativo Geral de Defesas por Ano. */
	private static final String JSP_RELATORIO_QUANT_DEFESAS_POR_ANO = "/stricto/relatorios/quantitativo_defesas_por_ano.jsp";
	/** Link do relatório Quantitativo Detalhado de Defesas no Ano. */
	private static final String JSP_RELATORIO_QUANT_DEFESAS_POR_ANO_DETALHADO = "/stricto/relatorios/quantitativo_defesas_por_ano_detalhado.jsp";
	/** Link da Lista dos Alunos não Matriculados On-Line. */
	private static final String JSP_RELATORIO_ALUNOS_NAO_MATRICULADOS_ONLINE = "/stricto/relatorios/lista_alunos_nao_matriculados_online.jsp";
	
	/** Dados do relatório. */
	private List<Map<String,Object>> listaDiscente = new ArrayList<Map<String,Object>>();	

    /**
     * Define o tipo da unidade que deve ser exibida no filtro
     * 1 - PROGRAMAS
     * 2 - CENTROS
     */
	protected int tipoUnidade;

    // Constantes com os tipos das unidades do filtro do relatório
	/** Define unidades do tipo Programa. */
    public static final int PROGRAMA = 1;
    /** Define unidades do tipo Centro. */
    public static final int CENTRO = 2;
    /** Constante que define o valor do ano mínimo ser emitido o relatório. */
    private static final int ANO_MINIMO_ENTRADA_EM_VIGOR = 1930;

    /**
     * Este atributo indica se deve aparecer no combo de seleção da unidade a opção 'todos'
     * para listar as informações de todas as unidades
     */
    public boolean passivelSelecionarTodas;

    /** Define o tipo de relatório que está sendo exibido */
    protected int tipoRelatorio;

    //Tipos de relatório
    /** Relatório de orientações. */
    public static final int ORIENTACOES_POR_PROGRAMA = 4;
    /** Relatório de alunos matriculados em atividades. */
    public static final int MATRICULADOS_ATIVIDADES_POR_PROGRAMA = 5;
    /** Relatório de bolsistas. */
    public static final int BOLSISTAS_POR_PROGRAMA = 6;

    // Detalhes da exibição do formulário (dependem de cada tipo de formulário)
    /** Armazena o título a ser usado para o relatório. */
    protected String titulo;
    /** Indica se pode ser feita a seleção de unidades ou se ela será selecionada de acordo com o usuário logado. */
    protected boolean selecaoUnidade = true;
    //protected String relatório;
    /** Formato de exportação do relatório. */
    protected String formato;
    /** Mapa dos parâmetros */
    protected HashMap<String, Object> parametros = new HashMap<String, Object>();
    /** {@link InputStream} utilizado para geração do relatório. */
    protected InputStream relatorio;

    /** Sistema de origem dos dados de cada relatório. */
    protected int origemDados = Sistema.SIGAA;

    /** Indica se o relatório será datelhado. */
	private boolean detalhado;

    public RelatoriosStrictoMBean() {
        clear();
    }

    /**
     *  Inicializar os dados do formulário
     */
    protected void clear() {

    	formato = "pdf";
    	unidade = new Unidade();
    	passivelSelecionarTodas = false;

    	if (  getAcessoMenu().isPpg() ) {
    		unidade.setId(-1);
    		selecaoUnidade = true;
    	} else {
    		unidade = getProgramaStricto();
    		selecaoUnidade = false;
    	}

    }

    /**
     * Vai pra tela inicial de relatório
     * 
     * JSP: Não invocado por JSP.
     * @return
     */
    public String iniciarRelatorioOrientacoes() {

		clear();
		titulo = "Orientações do programa";
		tipoRelatorio = ORIENTACOES_POR_PROGRAMA;
		return forward("/stricto/relatorios/relatorios_por_programa.jsp");

	}

    /**
     * Vai pra tela inicial de relatório<br/>
     * Método chamado pela seguinte JSP:
     * <ul>
     *   <li>/stricto/menus/relatorios.jsp</li>
     * </ul>
     * @return
     */
    public String iniciarRelatorioMatriculadosAtividades() {

		clear();
		titulo = "Alunos Matriculados em Atividades";
		tipoRelatorio = MATRICULADOS_ATIVIDADES_POR_PROGRAMA;
		passivelSelecionarTodas = false;
		RelatorioDiscenteMBean relDiscenteMBean = getMBean("relatorioDiscente");
		return relDiscenteMBean.carregarRelatorioMatriculadosAtividadeStricto();
	}

    /**
     * Gera o relatório de acordo com tipoRelatorio
     * 
     * JSP: Não invocado por JSP. 
     * 
     * @return
     * @throws DAOException
     * @throws SegurancaException
     */
    public String gerarRelatorioExterno() throws DAOException, SegurancaException {
    	System.out.println(formato);
    	switch (tipoRelatorio) {
		case ORIENTACOES_POR_PROGRAMA:
			return null;
		case MATRICULADOS_ATIVIDADES_POR_PROGRAMA:
			passivelSelecionarTodas =false;
			RelatorioDiscenteMBean relDiscenteMBean = getMBean("relatorioDiscente");
			return relDiscenteMBean.carregarRelatorioMatriculadosAtividadeStricto();
		case BOLSISTAS_POR_PROGRAMA:
			RelatorioBolsasStrictoMBean relatorioBolsasBean = getMBean("relatorioBolsasStrictoBean");
			return relatorioBolsasBean.gerarRelatorio();

		default:
			return null;
		}
    }

    /**
     * Realizar a geração do relatório, de acordo com os critérios selecionados
     *
     * JSP: Não invocado por JSP.
     * 
     * @return
     * @throws DAOException
     */
    public String gerarRelatorio() throws DAOException {

    	// Validar campos do formulário
        if ( !validate() ) {
            return null;
        }

        // Gerar relatório
        Connection con = null;
        try {

            // Preencher relatório
            con = getConexao();
            JasperPrint prt = JasperFillManager.fillReport(relatorio, parametros, con );

			if (prt.getPages().size() == 0) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}

            // Exportar relatório de acordo com o formato escolhido
            String nomeArquivo = "indicativo."+formato;
            JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), formato);
            FacesContext.getCurrentInstance().responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
            notifyError(e);
            addMensagemErro("Ocorreu um erro durante a geração deste relatório. Por favor,contacte o suporte através do \"Abrir Chamado\"");
            return null;
        } finally {
            Database.getInstance().close(con);
        }

        return null;
    }
    
    /**
     * Redireciona para informar o ano e período, para gerar o Relatório de Quantitativo 
     * de Alunos da Pós-Graduação Matriculados<br/>
     * Método chamado pela seguinte JSP:
     * <ul>
     *   <li>/portais/relatorios/abas/ensino.jsp</li>
     * </ul>
     * @return
     */
    public String iniciarRelatorioQuantitativoAlunosMatriculadosMes(){
    	ano = getCalendarioVigente().getAno();
    	periodo = getCalendarioVigente().getPeriodo();
    	return forward(JSP_SELECIONA_RELATORIO_QUANT_MATRICULADOS_POR_MES);	
    }
    
    /**
     * Gera o Relatório de Quantitativo de Alunos da Pós-Graduação Matriculados mês a mês.<br/>
     * Método chamado pela seguinte JSP:
     * <ul>
     *   <li>/stricto/relatorios/seleciona_quantitativo_matriculados_por_mes.jsp  </li>
     * </ul>
     * @return
     * @throws DAOException
     */
	public String gerarRelatorioQuantitativoAlunosMatriculadosMes() throws DAOException{
		if (ano == null || ano == 0){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
			return null;
		}
		
		if (ano.toString().length() < 4){
			addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Ano");
			return null;			
		}
		
		if (ano.intValue() > CalendarUtils.getAnoAtual()){
			addMensagem(MensagensArquitetura.DATA_ANTERIOR_IGUAL, "Ano", CalendarUtils.getAnoAtual());
			return null;			
		}
		
		if (ano.intValue() < ANO_MINIMO_ENTRADA_EM_VIGOR){
			addMensagem(MensagensArquitetura.DATA_POSTERIOR_A, "Ano", ANO_MINIMO_ENTRADA_EM_VIGOR);
			return null;
		}
		
		DiscenteStrictoDao dao = getDAO(DiscenteStrictoDao.class);
		try{
			listaDiscente = dao.findQuantitativoAlunosMatriculadosMes(ano);	
			
			//Contabilizando a quantidade de alunos ativos por mês.
			List<Map<String, Object>> lista = dao.findAlunosVinculadosByAno(ano);
			Set<ObjAux> listAux = new TreeSet<ObjAux>();
			for (Map<String, Object> map : lista) {
				ObjAux aux = new ObjAux();
				aux.setDiscente( map.get("iddiscente") != null ? Integer.parseInt(map.get("iddiscente").toString()) : null );
				aux.setStatus( map.get("status") != null ? Integer.parseInt(map.get("status").toString()) : null );
				aux.setGrupo( map.get("grupo") != null ? map.get("grupo").toString() : null );
				aux.setMovimentacaoAluno( map.get("idmovimentacaoaluno") != null ? Integer.parseInt(map.get("idmovimentacaoaluno").toString()) : null );
				aux.setAnoIngresso( map.get("dataingresso") != null ? Integer.parseInt(map.get("dataingresso").toString().substring(0, 4)) : null );
				aux.setMesIngresso( map.get("dataingresso") != null ? Integer.parseInt(map.get("dataingresso").toString().substring(4)) : null );
				aux.setAnoOcorrenciaMovimentacao( map.get("dataocorrenciamovimentacao") != null ? Integer.parseInt(map.get("dataocorrenciamovimentacao").toString().substring(0, 4)) : null );
				aux.setMesOcorrenciaMovimentacao( map.get("dataocorrenciamovimentacao") != null ? Integer.parseInt(map.get("dataocorrenciamovimentacao").toString().substring(4)) : null );
				aux.setAnoSaida( map.get("datasaida") != null ? Integer.parseInt(map.get("datasaida").toString().substring(0, 4)) : null );
				aux.setMesSaida( map.get("datasaida") != null ? Integer.parseInt(map.get("datasaida").toString().substring(4)) : null );
				listAux.add(aux);
			}
			
			for (int i = listaDiscente.size(); i < 12; i++) {
				Map<String,Object> mapDiscente = new HashMap<String, Object>();
				mapDiscente.put("mes", i+1);
				mapDiscente.put("total", 0);
				listaDiscente.add(mapDiscente);
			}
			
			for (Map<String,Object> mapDiscente : listaDiscente) {
				int inc = 0;
				for (ObjAux o: listAux) {
					boolean dataIngressoMenor 	= (o.anoIngresso < ano || (o.anoIngresso.equals(ano) && o.mesIngresso < Integer.parseInt(mapDiscente.get("mes").toString()) ) );
					boolean dataSaidaMaior 		= (o.anoSaida == null || (o.anoSaida != null && ( o.anoSaida > ano || (o.anoSaida.equals(ano) && o.mesSaida > Integer.parseInt(mapDiscente.get("mes").toString())) )));
					if ( dataIngressoMenor && dataSaidaMaior ){
						inc++;
					}
				}
				mapDiscente.put("ativo", inc );
			}
			
			if (listaDiscente.isEmpty()){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		} finally {
			if (dao != null)
				dao.close();				
		}
		return forward(JSP_RELATORIO_QUANT_MATRICULADOS_POR_MES);		
	}		
	
	/**
	 * Redireciona para informar o ano inicio e fim, para gerar o Relatório de Quantitativo 
	 * de Defesas da Pós-Graduação<br/>
     * Método chamado pela seguinte JSP:
     * <ul>
     *   <li>/portais/relatorios/abas/ensino.jsp</li>
     * </ul>
	 * @return
	 */
	public String iniciarRelatorioQuantativoDefesasAnos(){
		anoInicio = getCalendarioVigente().getAno() -1;
		anoFim = getCalendarioVigente().getAno();
		detalhado = false;
		return forward(JSP_SELECIONA_RELATORIO_QUANT_DEFESAS_POR_ANO); 
	}
	
	/**
	 * Redireciona para informar o ano inicio e fim, para gerar o Relatório de Quantitativo 
	 * de Defesas da Pós-Graduação<br/>
     * Método chamado pela seguinte JSP:
     * <ul>
     *   <li>/portais/relatorios/abas/ensino.jsp</li>
     * </ul>
	 * @return
	 */
	public String iniciarRelatorioQuantativoDefesasAnosDetalhado(){
		anoInicio = getCalendarioVigente().getAno();
		anoFim = getCalendarioVigente().getAno();
		detalhado = true;
		return forward("/stricto/relatorios/seleciona_quantitativo_defesas_por_ano_detalhado.jsp"); 
	}
	
    /**
     * Gera o Relatório de Quantitativo de Defesas da Pós-Graduação ano a ano.<br/>
     * Método chamado pela seguinte JSP:
     * <ul>
     *   <li>/stricto/relatorios/seleciona_quantitativo_defesas_por_ano.jsp</li>
     * </ul>
     * @return
     * @throws DAOException
     */
	public String gerarRelatorioQuantitativoDefesasMatriculadosAnos() throws DAOException{
		if (anoInicio == null || anoInicio == 0){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano Início");
			return null;
		}
		
		if (anoFim == null || anoFim == 0){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano Fim");
			return null;
		}		
				
		BancaPosDao dao = getDAO(BancaPosDao.class);
		if (detalhado) {
			listaDiscente = dao.findQuantitativoDefesasAnoDetalhado(anoInicio);
			if (isEmpty(listaDiscente)) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			return forward(JSP_RELATORIO_QUANT_DEFESAS_POR_ANO_DETALHADO);
		} else {
			listaDiscente = dao.findQuantitativoDefesasAno(anoInicio, anoFim);			
			if (isEmpty(listaDiscente)) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			return forward(JSP_RELATORIO_QUANT_DEFESAS_POR_ANO);
		}
	}		
	
	/**
	 * Repassa para a view, o resultado da consulta de Lista Alunos Ativos não matriculados online.<br/>
     * Método chamado pelas seguintes JSPs:
     * <ul>
     *   <li>stricto/menu_coordenador.jsp</li>
     *   <li>ensino/movimentacao_aluno/form_afastamento.jsp</li>
     * </ul>
	 * @return
	 */    
    public String gerarRelatorioAlunosNaoMatriculadosOnLine() throws ArqException{  	    	    	
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		try {				
			CalendarioAcademico cal = getCalendarioVigente();
			ano = cal.getAno();
			periodo = cal.getPeriodo();
			unidade = getProgramaStricto();
					
			listaDiscente = dao.findAlunosNaoMatriculadosOnLine(ano, periodo, unidade.getId());
			
			if (listaDiscente.size() == 0)
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);					
			else {
				for ( Map<String,Object>vlistaDiscente : listaDiscente){
					vlistaDiscente.put("nivel", NivelEnsino.getDescricao(vlistaDiscente.get("nivel").toString().toCharArray()[0]));
				}					
				return forward(JSP_RELATORIO_ALUNOS_NAO_MATRICULADOS_ONLINE);
			}							
		} finally {
			if (dao != null)
				dao.close();						
		}
		return null;
    }	  	
	

    /**
     * Retorna a conexão de acordo com a origem dos dados específica
     * de cada relatório
     *
     * @return
     * @throws SQLException
     */
    private Connection getConexao() throws SQLException {
        Connection con = null;
        switch (origemDados) {
            case Sistema.SIGAA: con = Database.getInstance().getSigaaConnection(); break;
            case Sistema.SIPAC: con = Database.getInstance().getSipacConnection(); break;
        }
        return con;
    }


    /**
     * Validar dados do formulário
     *
     * @return
     */
    private boolean validate() {
        ListaMensagens erros = new ListaMensagens();


        addMensagens(erros);
        return erros.isEmpty();
    }

    public String getFormato() {
        return this.formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public HashMap<String, Object> getParametros() {
		return parametros;
	}

	public void setParametros(HashMap<String, Object> parametros) {
		this.parametros = parametros;
	}

	public InputStream getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(InputStream relatorio) {
		this.relatorio = relatorio;
	}

	public String getTitulo() {
        return this.titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getOrigemDados() {
        return this.origemDados;
    }

    public void setOrigemDados(int origemDados) {
        this.origemDados = origemDados;
    }

	public boolean isPrograma() {
		return tipoUnidade == PROGRAMA;
	}

	public boolean isCentro() {
		return tipoUnidade == CENTRO;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public int getTipoUnidade() {
		return tipoUnidade;
	}

	public void setTipoUnidade(int tipoUnidade) {
		this.tipoUnidade = tipoUnidade;
	}

	public int getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(int tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public boolean isSelecaoUnidade() {
		return selecaoUnidade;
	}

	public void setSelecaoUnidade(boolean selecaoUnidade) {
		this.selecaoUnidade = selecaoUnidade;
	}

	public boolean isPassivelSelecionarTodas() {
		return passivelSelecionarTodas;
	}

	public void setPassivelSelecionarTodas(boolean passivelSelecionarTodas) {
		this.passivelSelecionarTodas = passivelSelecionarTodas;
	}

	/**
	 * Pega uma instância do mbean relatoriosStricto
	 * @return
	 */
	protected RelatoriosStrictoMBean getInstance() {
		return getMBean("relatoriosStricto");
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public List<Map<String, Object>> getListaDiscente() {
		return listaDiscente;
	}

	public void setListaDiscente(List<Map<String, Object>> listaDiscente) {
		this.listaDiscente = listaDiscente;
	}

	public Integer getAnoInicio() {
		return anoInicio;
	}

	public void setAnoInicio(Integer anoInicio) {
		this.anoInicio = anoInicio;
	}

	public Integer getAnoFim() {
		return anoFim;
	}

	public void setAnoFim(Integer anoFim) {
		this.anoFim = anoFim;
	}

	/** objeto auxiliar para criação de relatórios de stricto sensu.*/
	class ObjAux implements Comparable<ObjAux> {
		/** identificador do discente do objeto auxiliar para criação de relatório.*/
		Integer discente;
		/** status do discente do objeto auxiliar para criação de relatório.*/
		Integer status;
		/** grupo do tipo de movimentação do discente do objeto auxiliar para criação de relatório.*/
		String grupo;
		/** registro de movimentação do discente do objeto auxiliar para criação de relatório.*/
		Integer movimentacaoAluno;
		/** ano de ingresso do discente do objeto auxiliar para criação de relatório.*/
		Integer anoIngresso;
		/** mês de ingresso do discente do objeto auxiliar para criação de relatório.*/
		Integer mesIngresso;
		/** ano de ocorrência de saída do discente do objeto auxiliar para criação de relatório.*/
		Integer anoOcorrenciaMovimentacao;
		/** mês de ocorrência de saída do discente do objeto auxiliar para criação de relatório.*/
		Integer mesOcorrenciaMovimentacao;
		/** ano de saída do discente do objeto auxiliar para criação de relatório.*/
		Integer anoSaida;
		/** mês de saída do discente do objeto auxiliar para criação de relatório.*/
		Integer mesSaida;
		
		public void setDiscente(Integer discente) {
			this.discente = discente;
		}
		public void setStatus(Integer status) {
			this.status = status;
		}
		public void setGrupo(String grupo) {
			this.grupo = grupo;
		}
		public void setMovimentacaoAluno(Integer movimentacaoAluno) {
			this.movimentacaoAluno = movimentacaoAluno;
		}
		public void setAnoIngresso(Integer anoIngresso) {
			this.anoIngresso = anoIngresso;
		}
		public void setMesIngresso(Integer mesIngresso) {
			this.mesIngresso = mesIngresso;
		}
		public void setAnoOcorrenciaMovimentacao(Integer anoOcorrenciaMovimentacao) {
			this.anoOcorrenciaMovimentacao = anoOcorrenciaMovimentacao;
		}
		public void setMesOcorrenciaMovimentacao(Integer mesOcorrenciaMovimentacao) {
			this.mesOcorrenciaMovimentacao = mesOcorrenciaMovimentacao;
		}
		public void setAnoSaida(Integer anoSaida) {
			this.anoSaida = anoSaida;
		}
		public void setMesSaida(Integer mesSaida) {
			this.mesSaida = mesSaida;
		}
	
		@Override
		public int compareTo(ObjAux o) {
			if (discente != null && o != null && o.discente != null)
				return discente.compareTo(o.discente);
			return 0;
		}
	}
	
	@Override
	public List<SelectItem> getAnos() throws Exception {
		ArrayList<SelectItem> anos = new ArrayList<SelectItem>();
		for (int i = SelectAnoTag.ANO_INCIO_PADRAO; i <= CalendarUtils.getAnoAtual(); i++) {
			anos.add(new SelectItem(String.valueOf(i), String.valueOf(i)));
		}
		return anos;
	}

}
