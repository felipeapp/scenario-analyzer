/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 23/07/2010
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioSituacoesUsuarioDao;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.RelatorioSituacaoDosUsuarios;
import br.ufrn.sigaa.biblioteca.dominio.SituacaoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 * <p> Este relatório mostra os usuários que estão numa certa situação: com empréstimos ativos,
 * com empréstimos atrasados ou suspensos.
 * 
 * <p> Este relatório substitui o <em>Relatório de Usuários com Empréstimos em Atraso</em>,
 * pois este cobre a funcionalidade desse último.
 *
 * @author Bráulio
 * @author jadson 28/04/2011 incluindo o relatórios de usuários multados
 */
@Component("relatorioSituacaoDosUsuariosMBean")
@Scope("request")
public class RelatorioSituacaoDosUsuariosMBean extends AbstractRelatorioBibliotecaMBean {

	/** Página do relatório detalhado. Que é o padrão de todos */
	public static final String PAGINA_DO_RELATORIO = "/biblioteca/controle_estatistico/relatorioSituacaoDosUsuarios.jsp";
	
	/** Página do relatório básico. Usado apenas no relatório de usuário em atraso. */
	public static final String PAGINA_DO_RELATORIO_BASICO = "/biblioteca/controle_estatistico/relatorioSituacaoDosUsuariosBasico.jsp";
	
	/** Agrupado primeiro por categoria de usuário e depois por biblioteca. */
	private Map<String,Map<String,List<RelatorioSituacaoDosUsuarios>>> relatorioSituacaoDosUsuarios;
	
	/** Agrupado primeiro por categoria de usuário e depois por biblioteca. */
	private Map<String,Map<String,Map<String,List<RelatorioSituacaoDosUsuarios>>>> relatorioSituacaoDosUsuariosServidores;
	
	public RelatorioSituacaoDosUsuariosMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Configura o relatório.
	 * <p>Chamado pela seguinte JSP:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	public void iniciarRelatorioUsuariosSuspensos() throws DAOException, SegurancaException {
		setTitulo("Relatório de Usuários Suspensos");
		
		
		
		setDescricao(
					"<p>Este relatório gera uma lista dos usuários que estão suspensos atualmente no sistema.</p>" +
					"<p>Selecione uma biblioteca ou busque somente as suspensão manuais.</p>" +
					"<p>Escolhendo-se o filtro \"por biblioteca\", significa que serão recuperados os usuário que estão suspensos a partir de empréstimos de materiais que pertencem à Biblioteca escolhida. </p>" +
					"<p>O <strong>período</strong> filtra os usuários que estão suspensos " +
					"em qualquer um dos dias durante esse período. Por exemplo, um período de " +
					"<em>01/01/2010 a 31/01/2010</em> engloba suspensões com durações que terminam no período escolhido " +
					"<em>(20/12/2009 a 10/01/2010)</em>, estão dentro do período escolhido <em>(13/01/2010 a 15/01/2010)</em> " +
					"ou começam no período escolhido <em>( 20/01/2010 a 10/02/2010)</em>.</p>");
		
		setCampoBibliotecaObrigatorio(false);
		setFiltradoPorVariasBibliotecas(true);
		setFiltradoPorVariasCategoriasDeUsuario(true);
		setFiltradoPorPeriodo(true);
		setFiltradoPorBooleano(true);
		setDescricaoDadoBooleano("Incluir suspensões manuais");
		
		setSituacaoUsuarioBiblioteca(SituacaoUsuarioBiblioteca.ESTA_SUSPENSO);
		
		setInicioPeriodo( new Date() );
		setFimPeriodo( CalendarUtils.createDate(1, 1, CalendarUtils.getAnoAtual() + 5 ) );
		
		mostrarDetalhado = true;
		
		iniciar();
	}
	
	
	
	/**
	 * Configura o relatório.
	 * <p>Chamado pela seguinte JSP:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	public void iniciarRelatorioUsuariosComMulta() throws DAOException, SegurancaException {
		setTitulo("Relatório de Usuários Multados");
		
		campoBibliotecaObrigatorio= false;
		
		setDescricao(
					"<p>Este relatório gera uma lista dos usuários que estão multados atualmente no sistema.</p>" +
					"<p>Selecione uma biblioteca ou busque somente as multas manuais.</p>" +
					"<p>Escolhendo-se o filtro \"por biblioteca\", significa que serão recuperados os usuário que estão multados a partir de empréstimos de materiais que pertencem à Biblioteca escolhida. </p>");
		
		setFiltradoPorVariasBibliotecas(true);
		setFiltradoPorVariasCategoriasDeUsuario(true);
		setFiltradoPorBooleano(true);
		setDescricaoDadoBooleano("Incluir multas manuais");
		
		setSituacaoUsuarioBiblioteca(SituacaoUsuarioBiblioteca.ESTA_MULTADO);
		
		mostrarDetalhado = true;
		
		iniciar();
	}
	
	
	
	/**
	 * Configura o relatório.
	 * <p>Chamado pela seguinte JSP:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	public void iniciarRelatorioUsuariosEmAtraso() throws DAOException, SegurancaException {
		setTitulo("Relatório de Usuários em Atraso");
		setDescricao(
				"<p>Este relatório mostra os usuários que possuem empréstimos ativos atrasados.</p>" +
				"<p>O <strong>período</strong> filtra os atrasos pela data na qual foi feito o empréstimo.</p>" +
				"<br/>"+
				"<p><strong>IMPORTANTE: </strong> Para serem exibidos os filtros de \"Situações do Servidor\"  e \"Unidade Administrativa do Servidor\"" +
				" é necessário selecionar apenas as categorias de usuário <i>SERVIDOR TÉCNICO-ADMINISTRATIVO</i> ou <i>DOCENTE</i> </p>"+
				"<br/>");
		
		filtradoPorVariasBibliotecas = true;
		campoBibliotecaObrigatorio = false;
		filtradoPorVariasCategoriasDeUsuario = true;
		filtradoPorPeriodo = true;
		filtradoPorTipoDeMaterial = true;
		filtradoPorTipoDeEmprestimo = true;
		filtradoPorBasicoVsDetalhado = true;
		filtradoPorVariasSituacoesDeServidor = true;
		filtradoPorVariasUnidades = true;
		
		setSituacaoUsuarioBiblioteca(SituacaoUsuarioBiblioteca.POSSUI_EMPRESTIMOS_ATRASADOS);
		setInicioPeriodo( CalendarUtils.createDate(1, 1, 1995) );
		setFimPeriodo( CalendarUtils.createDate(1, 1, CalendarUtils.getAnoAtual() + 5 ) );
		
		mostrarDetalhado = true;
		
		iniciar();
	}
	
	/**
	 * Configura o relatório.
	 * <p>Chamado pela seguinte JSP:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	public void iniciarRelatorioUsuariosComEmprestimosAtivos() throws DAOException, SegurancaException {
		setTitulo("Relatório de Usuários com Empréstimos Ativos");
		
		setDescricao("<p>Este relatório mostra os usuários que possuem empréstimos ativos no sistema.</p>"
				+"<p>O campo <strong>período</strong> recupera pela data em que o empréstimo foi realizado.</p>"
				+"<p>Pode-se optar por recuperar os empréstimos atrasados ou não.</p>");
		
		setFiltradoPorVariasBibliotecas(true);
		setFiltradoPorVariasCategoriasDeUsuario(true);
		setFiltradoPorPeriodo(true);
		setFiltradoPorBooleano(true);
		setDescricaoDadoBooleano("Incluir empréstimos atrasados");
		
		setSituacaoUsuarioBiblioteca(SituacaoUsuarioBiblioteca.POSSUI_EMPRESTIMOS_ATIVOS);
		setInicioPeriodo( CalendarUtils.createDate(1, 1, 1995) );
		setFimPeriodo( CalendarUtils.createDate(1, 1, CalendarUtils.getAnoAtual() + 5 ) );
		
		mostrarDetalhado = true;
		
		iniciar();
	}
	
	@Override
	public void configurar() {
		// Não faz nada, já que a configuração é feita em cada método "Iniciar"
		// correspondente a cada relatório.
	}

	
	
	/**
	 * <p> Gera o relatório de usuários numa certa situação.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/controle_estatistico/formGeral.jsp (indiretamente)</li></ul>
	 */
	@Override
	public String gerarRelatorio() throws DAOException {
		
		RelatorioSituacoesUsuarioDao dao = getDAO(RelatorioSituacoesUsuarioDao.class);
		
		try {

			Collection<Integer> bibs = UFRNUtils.toInteger(variasBibliotecas);
			Collection<Integer> unids = UFRNUtils.toInteger(variasUnidades);
			Collection<Integer> cats = UFRNUtils.toInteger(variasCategoriasDeUsuario);
			Collection<Integer> sits = UFRNUtils.toInteger(variasSituacoesDeServidor);
			
			if ( isRelatorioUsuariosMultados() )
				relatorioSituacaoDosUsuarios = dao.findUsuariosMultados(bibs, cats, dadoBooleano);
			else
			if ( isRelatorioUsuariosSuspensos() )
				relatorioSituacaoDosUsuarios = dao.findUsuariosSuspensos(bibs, cats, inicioPeriodo, fimPeriodo, dadoBooleano);
			else
				if ( isRelatorioUsuariosEmAtraso() ){
					if (isConsultaServidor()) {
						relatorioSituacaoDosUsuariosServidores = dao.findUsuariosServidoresComEmprestimosAtivos(bibs, cats, sits, unids, inicioPeriodo, fimPeriodo, true, false, tipoDeMaterial, tipoDeEmprestimo);
					} else {
						relatorioSituacaoDosUsuarios = dao.findUsuariosComEmprestimosAtivos(bibs, cats, inicioPeriodo, fimPeriodo, true, false, tipoDeMaterial, tipoDeEmprestimo);
					}
				}else
					if ( isRelatorioUsuariosEmprestimosAtivos() )
						relatorioSituacaoDosUsuarios = dao.findUsuariosComEmprestimosAtivos(bibs, cats, inicioPeriodo, fimPeriodo, dadoBooleano, true, 0, 0);
		} finally {
			if ( dao != null ) dao.close();
		}
		
		
		if ( ( relatorioSituacaoDosUsuariosServidores == null || relatorioSituacaoDosUsuariosServidores.isEmpty() ) 
				&& ( relatorioSituacaoDosUsuarios == null || relatorioSituacaoDosUsuarios.isEmpty() ) ) {
			addMensagem( MensagensArquitetura.BUSCA_SEM_RESULTADOS );
			return null;
		}
		
		
		if ( mostrarDetalhado )
			return forward(PAGINA_DO_RELATORIO);
		else
			return forward(PAGINA_DO_RELATORIO_BASICO); // Só redireciona para cá apesas no relatório de usuários em atraso quando o usuário escolhe o básico.
	}

	
	/**
	 * Método que indica se a consulta atual é feita exclusivamente sobre servidores (servidor técnico
	 * administrativo e/ou docente) ou não.
	 * 
	 * @return
	 */
	public boolean isConsultaServidor() {
		int valorCategoriaUsuario = 0;
		
		for (String s : variasCategoriasDeUsuario) {
			valorCategoriaUsuario = Integer.parseInt(s);
			
			if (valorCategoriaUsuario != VinculoUsuarioBiblioteca.SERVIDOR_TECNO_ADMINISTRATIVO.getValor()
					&& valorCategoriaUsuario != VinculoUsuarioBiblioteca.DOCENTE.getValor()) {
				return false;
			}
		}
		
		return true;
	}

	public Map<String, Map<String, List<RelatorioSituacaoDosUsuarios>>> getResultados() {
		return relatorioSituacaoDosUsuarios;
	}

	public Map<String, Map<String, Map<String, List<RelatorioSituacaoDosUsuarios>>>> getResultadosServidores() {
		return relatorioSituacaoDosUsuariosServidores;
	}


	public boolean isRelatorioUsuariosMultados(){
		return situacaoUsuarioBiblioteca == SituacaoUsuarioBiblioteca.ESTA_MULTADO;
	}
	
	public boolean isRelatorioUsuariosSuspensos(){
		return situacaoUsuarioBiblioteca == SituacaoUsuarioBiblioteca.ESTA_SUSPENSO;
	}
	
	public boolean isRelatorioUsuariosEmAtraso(){
		return situacaoUsuarioBiblioteca == SituacaoUsuarioBiblioteca.POSSUI_EMPRESTIMOS_ATRASADOS;
	}
	
	public boolean isRelatorioUsuariosEmprestimosAtivos(){
		return situacaoUsuarioBiblioteca == SituacaoUsuarioBiblioteca.POSSUI_EMPRESTIMOS_ATIVOS;
	}
	

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos1ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos1ComboBox() {
		return null;
	}



	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos2ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos2ComboBox() {
		return null;
	}

}
