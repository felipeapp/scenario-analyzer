/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p> Este relat�rio mostra os usu�rios que est�o numa certa situa��o: com empr�stimos ativos,
 * com empr�stimos atrasados ou suspensos.
 * 
 * <p> Este relat�rio substitui o <em>Relat�rio de Usu�rios com Empr�stimos em Atraso</em>,
 * pois este cobre a funcionalidade desse �ltimo.
 *
 * @author Br�ulio
 * @author jadson 28/04/2011 incluindo o relat�rios de usu�rios multados
 */
@Component("relatorioSituacaoDosUsuariosMBean")
@Scope("request")
public class RelatorioSituacaoDosUsuariosMBean extends AbstractRelatorioBibliotecaMBean {

	/** P�gina do relat�rio detalhado. Que � o padr�o de todos */
	public static final String PAGINA_DO_RELATORIO = "/biblioteca/controle_estatistico/relatorioSituacaoDosUsuarios.jsp";
	
	/** P�gina do relat�rio b�sico. Usado apenas no relat�rio de usu�rio em atraso. */
	public static final String PAGINA_DO_RELATORIO_BASICO = "/biblioteca/controle_estatistico/relatorioSituacaoDosUsuariosBasico.jsp";
	
	/** Agrupado primeiro por categoria de usu�rio e depois por biblioteca. */
	private Map<String,Map<String,List<RelatorioSituacaoDosUsuarios>>> relatorioSituacaoDosUsuarios;
	
	/** Agrupado primeiro por categoria de usu�rio e depois por biblioteca. */
	private Map<String,Map<String,Map<String,List<RelatorioSituacaoDosUsuarios>>>> relatorioSituacaoDosUsuariosServidores;
	
	public RelatorioSituacaoDosUsuariosMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Configura o relat�rio.
	 * <p>Chamado pela seguinte JSP:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	public void iniciarRelatorioUsuariosSuspensos() throws DAOException, SegurancaException {
		setTitulo("Relat�rio de Usu�rios Suspensos");
		
		
		
		setDescricao(
					"<p>Este relat�rio gera uma lista dos usu�rios que est�o suspensos atualmente no sistema.</p>" +
					"<p>Selecione uma biblioteca ou busque somente as suspens�o manuais.</p>" +
					"<p>Escolhendo-se o filtro \"por biblioteca\", significa que ser�o recuperados os usu�rio que est�o suspensos a partir de empr�stimos de materiais que pertencem � Biblioteca escolhida. </p>" +
					"<p>O <strong>per�odo</strong> filtra os usu�rios que est�o suspensos " +
					"em qualquer um dos dias durante esse per�odo. Por exemplo, um per�odo de " +
					"<em>01/01/2010 a 31/01/2010</em> engloba suspens�es com dura��es que terminam no per�odo escolhido " +
					"<em>(20/12/2009 a 10/01/2010)</em>, est�o dentro do per�odo escolhido <em>(13/01/2010 a 15/01/2010)</em> " +
					"ou come�am no per�odo escolhido <em>( 20/01/2010 a 10/02/2010)</em>.</p>");
		
		setCampoBibliotecaObrigatorio(false);
		setFiltradoPorVariasBibliotecas(true);
		setFiltradoPorVariasCategoriasDeUsuario(true);
		setFiltradoPorPeriodo(true);
		setFiltradoPorBooleano(true);
		setDescricaoDadoBooleano("Incluir suspens�es manuais");
		
		setSituacaoUsuarioBiblioteca(SituacaoUsuarioBiblioteca.ESTA_SUSPENSO);
		
		setInicioPeriodo( new Date() );
		setFimPeriodo( CalendarUtils.createDate(1, 1, CalendarUtils.getAnoAtual() + 5 ) );
		
		mostrarDetalhado = true;
		
		iniciar();
	}
	
	
	
	/**
	 * Configura o relat�rio.
	 * <p>Chamado pela seguinte JSP:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	public void iniciarRelatorioUsuariosComMulta() throws DAOException, SegurancaException {
		setTitulo("Relat�rio de Usu�rios Multados");
		
		campoBibliotecaObrigatorio= false;
		
		setDescricao(
					"<p>Este relat�rio gera uma lista dos usu�rios que est�o multados atualmente no sistema.</p>" +
					"<p>Selecione uma biblioteca ou busque somente as multas manuais.</p>" +
					"<p>Escolhendo-se o filtro \"por biblioteca\", significa que ser�o recuperados os usu�rio que est�o multados a partir de empr�stimos de materiais que pertencem � Biblioteca escolhida. </p>");
		
		setFiltradoPorVariasBibliotecas(true);
		setFiltradoPorVariasCategoriasDeUsuario(true);
		setFiltradoPorBooleano(true);
		setDescricaoDadoBooleano("Incluir multas manuais");
		
		setSituacaoUsuarioBiblioteca(SituacaoUsuarioBiblioteca.ESTA_MULTADO);
		
		mostrarDetalhado = true;
		
		iniciar();
	}
	
	
	
	/**
	 * Configura o relat�rio.
	 * <p>Chamado pela seguinte JSP:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	public void iniciarRelatorioUsuariosEmAtraso() throws DAOException, SegurancaException {
		setTitulo("Relat�rio de Usu�rios em Atraso");
		setDescricao(
				"<p>Este relat�rio mostra os usu�rios que possuem empr�stimos ativos atrasados.</p>" +
				"<p>O <strong>per�odo</strong> filtra os atrasos pela data na qual foi feito o empr�stimo.</p>" +
				"<br/>"+
				"<p><strong>IMPORTANTE: </strong> Para serem exibidos os filtros de \"Situa��es do Servidor\"  e \"Unidade Administrativa do Servidor\"" +
				" � necess�rio selecionar apenas as categorias de usu�rio <i>SERVIDOR T�CNICO-ADMINISTRATIVO</i> ou <i>DOCENTE</i> </p>"+
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
	 * Configura o relat�rio.
	 * <p>Chamado pela seguinte JSP:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	public void iniciarRelatorioUsuariosComEmprestimosAtivos() throws DAOException, SegurancaException {
		setTitulo("Relat�rio de Usu�rios com Empr�stimos Ativos");
		
		setDescricao("<p>Este relat�rio mostra os usu�rios que possuem empr�stimos ativos no sistema.</p>"
				+"<p>O campo <strong>per�odo</strong> recupera pela data em que o empr�stimo foi realizado.</p>"
				+"<p>Pode-se optar por recuperar os empr�stimos atrasados ou n�o.</p>");
		
		setFiltradoPorVariasBibliotecas(true);
		setFiltradoPorVariasCategoriasDeUsuario(true);
		setFiltradoPorPeriodo(true);
		setFiltradoPorBooleano(true);
		setDescricaoDadoBooleano("Incluir empr�stimos atrasados");
		
		setSituacaoUsuarioBiblioteca(SituacaoUsuarioBiblioteca.POSSUI_EMPRESTIMOS_ATIVOS);
		setInicioPeriodo( CalendarUtils.createDate(1, 1, 1995) );
		setFimPeriodo( CalendarUtils.createDate(1, 1, CalendarUtils.getAnoAtual() + 5 ) );
		
		mostrarDetalhado = true;
		
		iniciar();
	}
	
	@Override
	public void configurar() {
		// N�o faz nada, j� que a configura��o � feita em cada m�todo "Iniciar"
		// correspondente a cada relat�rio.
	}

	
	
	/**
	 * <p> Gera o relat�rio de usu�rios numa certa situa��o.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
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
			return forward(PAGINA_DO_RELATORIO_BASICO); // S� redireciona para c� apesas no relat�rio de usu�rios em atraso quando o usu�rio escolhe o b�sico.
	}

	
	/**
	 * M�todo que indica se a consulta atual � feita exclusivamente sobre servidores (servidor t�cnico
	 * administrativo e/ou docente) ou n�o.
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
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos1ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos1ComboBox() {
		return null;
	}



	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos2ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos2ComboBox() {
		return null;
	}

}
