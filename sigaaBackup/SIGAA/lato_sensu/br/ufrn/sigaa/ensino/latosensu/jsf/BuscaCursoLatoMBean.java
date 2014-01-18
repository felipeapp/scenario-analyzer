package br.ufrn.sigaa.ensino.latosensu.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CursoLatoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;
import br.ufrn.sigaa.ensino.latosensu.relatorios.LinhaConsultaCursoGeral;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Managed bean para a realização de buscas por cursos Latu Sensu
 *
 */
@Component() @Scope("session")
public class BuscaCursoLatoMBean extends SigaaAbstractController<CursoLato> {

	//Campos para Buscar
	/**Filtra a busca pelo nome do curso*/
	private boolean filtroCurso;
	/**Filtra a busca pelo código do curso*/
	private boolean filtroCodigo;
	/**Filtra a busca pelo nome do coordenador*/
	private boolean filtroCoordenador;
	/**Filtra a busca pelo ano de inicio do curso*/
	private boolean filtroAno;
	/**Não usa filtros na busca.*/
	private boolean filtroTodos;
	/**Filtra a busca pela situação da proposta do curso*/
	private boolean filtroSituacao;
	/**Filtra a busca pela area de conhecimento do  curso*/
	private boolean filtroAreaConhecimento;
	/**Filtra a busca pelo periodo de inicio e fim do curso.*/
	private boolean filtroPeriodo;
	/**parametro usado na busca: Coordenador do curso*/
	private Servidor servidor;
	/**parametro usado na busca:curso*/
	private Curso curso; 
	/**parametro usado na busca: status das propostas de curso*/
	private SituacaoProposta situacaoProposta;
	/**parametro usado na busca: Area de conhecimento do curso*/
	private AreaConhecimentoCnpq area;
	/**parametro usado na busca: Data de inicio do curso*/
	private Date dataInicio;
	/**parametro usado na busca: Data final do curso*/
	private Date dataFim;
	/**Guarda uma lista de objetos retornados pela busca*/
	private Collection<LinhaConsultaCursoGeral> listaCursoLato = new ArrayList<LinhaConsultaCursoGeral>();
	/**parametro usado na busca: Ano de inicio do curso*/
	private Integer ano;
	/** Constante de visualização da tela de busca de  propostas do curso */
	private final static String JSP_BUSCA_PROPOSTAS = "/lato/proposta_curso/busca.jsp";
	/** parametro usado para a busca pelo código*/
	private String codigoCurso;
	
	public boolean isFiltroPeriodo() {
		return filtroPeriodo;
	}

	public void setFiltroPeriodo(boolean filtroPeriodo) {
		this.filtroPeriodo = filtroPeriodo;
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
	
	/** Construtor padrão */
	public BuscaCursoLatoMBean() {
		clear();
	}

	/**
	 * Inicializa os atributos para evitar futuros erro. 
	 */
	private void clear() {
		obj = new CursoLato();		
		servidor = new Servidor();
		curso = new Curso();
		situacaoProposta = new SituacaoProposta();
		area = new AreaConhecimentoCnpq();
		listaCursoLato=new ArrayList<LinhaConsultaCursoGeral>();
		codigoCurso="";
		getUsuarioLogado().setCursoLato(null);
	}

	/**
	 * Remoção da proposta. 
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/busca.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String remocao() throws ArqException, NegocioException{
		CursoLatoMBean mBean = (CursoLatoMBean) getMBean("cursoLatoMBean");
		mBean.remocao();
		Integer idCursoLato = getParameterInt("id");
		if(!hasErrors())
			for(Iterator<LinhaConsultaCursoGeral> it = listaCursoLato.iterator(); it.hasNext();){
				LinhaConsultaCursoGeral cursoLato = it.next();
				if (cursoLato.getIdCurso().equals(idCursoLato)){
					it.remove();
					break;
				}
			}
				
		return redirectMesmaPagina();
	}
	
	/**
	 * Realiza uma busca pelo(s) parametro(s) informado(s).
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/busca.jsp</li>
	 * </ul> 
	 * @return
	 * @throws Exception
	 */
	@Override
	public String buscar() throws Exception {
		listaCursoLato.clear();
		CursoLatoDao dao = getDAO(CursoLatoDao.class);
		
		if (!filtroAno && !filtroAreaConhecimento && 
				!filtroCoordenador && !filtroCurso && 
					!filtroSituacao && !filtroTodos && !filtroPeriodo && !filtroCodigo) {
			
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			return null;
		}
		
		if(!filtroCodigo){
			codigoCurso="";
		}else{
			if(ValidatorUtil.isEmpty( codigoCurso ) ){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,"Código do Curso");
			}else{
				if(!isCodigoProposta(codigoCurso) ){
					addMensagemErro("Código do Curso: Formato Inválido.");	
				}
			}
		}
		
		if (!filtroAno) 
			ano = null;
		else{
			validateRequired(ano, "Ano", erros);
			validateMinValue(ano, 1900, "Ano", erros);			
		}
		if(!filtroPeriodo){
			dataInicio = null;
			dataFim = null;
		}else{
			ValidatorUtil.validaOrdemTemporalDatas(dataInicio, dataFim, true, "Período do Curso", erros);
			validateRequired(dataInicio, "Período do Curso - Data Inicial", erros);
			validateRequired(dataFim, "Período do Curso - Data Final", erros);
			
		}
		if (!filtroCoordenador) 
			servidor = null;
		else
			validateRequired(servidor, "Coordenador", erros);
		if (!filtroCurso) 
			curso = new Curso();
		else
			validateRequired(curso.getNome(), "Curso", erros);
		if (!filtroSituacao) 
			situacaoProposta = new SituacaoProposta();
		else
			validateRequired(situacaoProposta, "Situação da Proposta", erros);
		if (!filtroAreaConhecimento) 
			area = new AreaConhecimentoCnpq();
		else 
			validateRequired(area, "Área Conhecimento", erros);
		
		if (hasErrors()){
			servidor = new Servidor();
			return null;
		}
		
		Integer anoCodigo = anoByCodigo(codigoCurso);
		Integer sequenciaCodigo =  sequenciaByCodigo(codigoCurso);
		
		try {
			listaCursoLato = dao.filter( ano,dataInicio,dataFim, servidor, curso, situacaoProposta, area, sequenciaCodigo , anoCodigo , false );	
			if ( isEmpty(servidor) )
				servidor = new Servidor();
			if(listaCursoLato.isEmpty())
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dao.close();
		}
		return getCurrentURL();
	}

	
	/**
	 * Verifica se o código foi digitado corretamente.
	 * @param codigo
	 * @return
	 */
	public Boolean isCodigoProposta(String codigo) {
    	
    	if(codigo == null || codigo.length() < 8)
    		return false;
    	
    	String prefixo = codigo.substring(0, 2);    	
    	if(!prefixo.equals("PC"))
    		return false;
    	
    	
    	int indiceInicioAno = codigo.length()-4;
    	String ano = codigo.substring(indiceInicioAno);
    	
    	    	
    	if(codigo.charAt(indiceInicioAno-1)!= '-')
    		return false;
    	
    	int indiceInicioMeio = 2; 
    	int indiceFimMeio = indiceInicioAno-2;
    	String meio = codigo.substring(indiceInicioMeio, indiceFimMeio+1);
    	
    	try {
    		Integer.parseInt(ano);    		
    	}
    	catch (NumberFormatException e) {
			return false;
		}
    	
    	try {    		
    		Integer.parseInt(meio);
    	}
    	catch (NumberFormatException e) {
			return false;					
		}
    	
    	return true;
    }
    
	/** 
	 * Retorna o ano do código
	 * Não invocado por JSP.
	 * @param codigo
	 * @return
	 */
    private Integer anoByCodigo(String codigo) {
    	
    	if( !isCodigoProposta(codigo) )
    		return null;
    	
    	String ano = codigo.substring(codigo.length()-4);
    	return Integer.parseInt(ano);    	
    }
    
    /**
     * Retorna a sequência de um curso latus
     * <br />
     * <ul>
     *  	<li>Método não invocado por JSP´s</li>
     * </ul>
     * @param codigo
     * @return
     */
    public Integer sequenciaByCodigo(String codigo) {
    	if( !isCodigoProposta(codigo) )
        	return null;
    	
    	int indiceInicioAno = codigo.length()-4;
    	int indiceInicioMeio = 2; 
    	int indiceFimMeio = indiceInicioAno-2;
    	String meio = codigo.substring(indiceInicioMeio, indiceFimMeio+1);
    	return Integer.parseInt(meio);
    	
    }   
    
	@Override
	public String listar() throws ArqException {
		clear();
		return getListPage();
	}
	
	@Override
	public String getListPage() {
		return forward("/lato/proposta_curso/busca.jsp");
	}

	/**
	 * Retorna para a tela de busca.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/alterar_situacao.jsp</li>
	 * </ul>
	 */
	public String voltaTelaBusca(){
		return getListPage();
	}
	
	public Collection<LinhaConsultaCursoGeral> getListaCursoLato() {
		return listaCursoLato;
	}

	public void setListaCursoLato(Collection<LinhaConsultaCursoGeral> listaCursoLato) {
		this.listaCursoLato = listaCursoLato;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public boolean isFiltroCurso() {
		return filtroCurso;
	}

	public void setFiltroCurso(boolean filtroCurso) {
		this.filtroCurso = filtroCurso;
	}

	public boolean isFiltroCoordenador() {
		return filtroCoordenador;
	}

	public void setFiltroCoordenador(boolean filtroCoordenador) {
		this.filtroCoordenador = filtroCoordenador;
	}

	public boolean isFiltroAno() {
		return filtroAno;
	}

	public void setFiltroAno(boolean filtroAno) {
		this.filtroAno = filtroAno;
	}

	public boolean isFiltroTodos() {
		return filtroTodos;
	}

	public void setFiltroTodos(boolean filtroTodos) {
		this.filtroTodos = filtroTodos;
	}

	public boolean isFiltroSituacao() {
		return filtroSituacao;
	}

	public void setFiltroSituacao(boolean filtroSituacao) {
		this.filtroSituacao = filtroSituacao;
	}

	public boolean isFiltroAreaConhecimento() {
		return filtroAreaConhecimento;
	}

	public void setFiltroAreaConhecimento(boolean filtroAreaConhecimento) {
		this.filtroAreaConhecimento = filtroAreaConhecimento;
	}

	public SituacaoProposta getSituacaoProposta() {
		return situacaoProposta;
	}

	public void setSituacaoProposta(SituacaoProposta situacaoProposta) {
		this.situacaoProposta = situacaoProposta;
	}

	public AreaConhecimentoCnpq getArea() {
		return area;
	}

	public void setArea(AreaConhecimentoCnpq area) {
		this.area = area;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public static String getJspBuscaPropostas() {
		return JSP_BUSCA_PROPOSTAS;
	}

	public boolean isFiltroCodigo() {
		return filtroCodigo;
	}

	public void setFiltroCodigo(boolean filtroCodigo) {
		this.filtroCodigo = filtroCodigo;
	}

	public String getCodigoCurso() {
		return codigoCurso;
	}

	public void setCodigoCurso(String codigoCurso) {
		this.codigoCurso = codigoCurso;
	}

}