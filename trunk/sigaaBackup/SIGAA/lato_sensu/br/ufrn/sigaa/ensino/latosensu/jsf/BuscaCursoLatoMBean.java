package br.ufrn.sigaa.ensino.latosensu.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CursoLatoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;
import br.ufrn.sigaa.ensino.latosensu.relatorios.LinhaConsultaCursoGeral;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

@Component() @Scope("request")
public class BuscaCursoLatoMBean extends SigaaAbstractController<CursoLato> {

	//Campos para Buscar
	private boolean filtroCurso;
	private boolean filtroCoordenador;
	private boolean filtroAno;
	private boolean filtroTodos;
	private boolean filtroSituacao;
	private boolean filtroAreaConhecimento;
	
	private Servidor servidor;
	private Curso curso; 
	private SituacaoProposta situacaoProposta;
	private AreaConhecimentoCnpq area;
	
	private Collection<LinhaConsultaCursoGeral> listaCursoLato = new ArrayList<LinhaConsultaCursoGeral>();
	
	private Integer ano;
	
	/** Construtor padrão */
	public BuscaCursoLatoMBean() {
		clear();
	}

	private void clear() {
		obj = new CursoLato();
		servidor = new Servidor();
		curso = new Curso();
		situacaoProposta = new SituacaoProposta();
		area = new AreaConhecimentoCnpq();
	}

	/**
	 * Remoção da proposta. 
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String remocao() throws ArqException, NegocioException{
		CursoLatoMBean mBean = (CursoLatoMBean) getMBean("cursoLatoMBean");
		mBean.remocao();
		listaCursoLato.clear();
		return redirectMesmaPagina();
	}
	
	/**
	 * Realiza uma busca pelo(s) parametro(s) informado.
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public String buscar() throws Exception {
		listaCursoLato.clear();
		CursoLatoDao dao = getDAO(CursoLatoDao.class);
		
		if (!filtroAno && !filtroAreaConhecimento && 
				!filtroCoordenador && !filtroCurso && 
					!filtroSituacao && !filtroTodos) {
			
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			return null;
		}
		
		if (!filtroAno) 
			ano = null;
		else
			validateMinValue(ano, 1900, "Ano", erros);
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
			validateRequired(area, "Área", erros);
		
		if (hasErrors())
			return null;
		
		try {
			listaCursoLato = dao.filter( ano, servidor, curso, situacaoProposta, area, false );	
			if ( isEmpty(servidor) )
				servidor = new Servidor();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dao.close();
		}
		return getCurrentURL();
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

}