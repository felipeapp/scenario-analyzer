/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 12/02/2010'
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.monitoria.RelatorioRendimentoComponenteDao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.AbstractRelatorioGraduacaoMBean;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.mensagens.MensagensGraduacao;
import br.ufrn.sigaa.mensagens.MensagensMonitoria;
import br.ufrn.sigaa.pessoa.dominio.Docente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * MBean responsável por relatório de rendimentos de componentes que participam de projetos de monitoria
 * @author geyson
 *
 */
@Component("relatorioRendimentoComponente") @Scope("request")
public class RelatorioRendimentoComponenteMBean extends AbstractRelatorioGraduacaoMBean {
	
	/** Atributo utilizado para salvar o contexto */
	private static final String CONTEXTO ="/monitoria/Relatorios/";
	/** Atributo utilizado para salvar o nome da JSP de seleção do Relatório de Rendimento */
	private static final String JSP_SELECIONA = "seleciona_rendimento_componente";
	/** Atributo utilizado para salvar o nome da JSP do Relatório de Rendimento */
	private static final String JSP_REL = "rel_rendimento_componente";
	
	/** Atributo utilizado para representar o Departamento do Componente */
	private Unidade departamentoComponente;
	/** Atributo utililizado para representar o Docente */
	private Servidor docente = new Servidor();
	
	/** Atributo utilizado para verificar se há filtro por Docente */
	private boolean filtroDocente;
	
	
	/** Lista de dados do relatório. */
	private List<Map<String,Object>> lista = new ArrayList<Map<String,Object>>();
	
	/** Construtor padrão. */
	public RelatorioRendimentoComponenteMBean(){
		setDepartamentoComponente(new Unidade());
		ano = CalendarUtils.getAnoAtual();
		periodo = getPeriodoAtual();
		filtroDocente = false;
	}
	
	
	/**
	 * Faz o redirecionamento para a tela de preenchimento dos dados para a geração do relatório
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/index.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarRelatorioRendimentoComponente() throws SegurancaException{
		checkListRole();
		return forward(CONTEXTO+JSP_SELECIONA); 
	}
	
	/**
	 * Gera um relatório tendo como entrada um departamento. 
	 * <br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/Relatorios/seleciona_rendimento_componente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioRendimentoComponente() throws DAOException{
		RelatorioRendimentoComponenteDao dao = getDAO(RelatorioRendimentoComponenteDao.class);
		UnidadeDao daoUnidade = getDAO(UnidadeDao.class);
		
			/* Verifica se o filtro por Código da disciplina foi setado e se for setado, verifica se o código foi digitado */
			if(!isFiltroCodigo()){
				this.codigo = null;
			}else if(this.codigo.isEmpty()) {
				addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Código da Disciplina");
				return null;
			}
			/* Verifica se o filtro por Docente foi setado e se for setado, verifica se o nome do docente foi digitado */
			if(!isFiltroDocente()){
				docente.setId(0);
			}else if(docente.getId() <= 0){
				addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Docente");
				return null;
			}
		
			/* Verifica campo obrigatório: Departamento */
			if(departamentoComponente.getId() <= 0 ){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Departamento"); 
				return null;
			}else
				departamentoComponente =  daoUnidade.findByPrimaryKey(departamentoComponente.getId(), Unidade.class);
			
			/* Verifica campo obrigatório: Período */
			if(ano == null || anoFim == null){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,"Período");
				return null;
			}
				
			if(!hasErrors()){
				lista = dao.relatorioRedemintosComponentes(departamentoComponente.getId(), ano, anoFim, docente.getId(), this.codigo);
				if (lista.size() > 0) {
					return forward(JSP_REL);
				}
				else
					addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
					return null;
			}
			return null;
			

	}

	public Unidade getDepartamentoComponente() {
		return departamentoComponente;
	}


	public void setDepartamentoComponente(Unidade departamentoComponente) {
		this.departamentoComponente = departamentoComponente;
	}


	public List<Map<String, Object>> getLista() {
		return lista;
	}

	public void setLista(List<Map<String, Object>> lista) {
		this.lista = lista;
	}


	public boolean isFiltroDocente() {
		return filtroDocente;
	}


	public void setFiltroDocente(boolean filtroDocente) {
		this.filtroDocente = filtroDocente;
	}


	public Docente getDocente() {
		return docente;
	}


	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

}
