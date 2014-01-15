/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 20/11/2009
 *
 */

package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.RelatoriosCoordenadorDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator;

/**
 * Managed Bean que controla a gera��o do relat�rio de alunos que ainda
 * n�o cumpriram um certo componente curricular. Os principais m�todos
 * da classe s�o <code>iniciar</code> e <code>gerar</code>.
 *
 * @author Br�ulio Bezerra
 * @since 20/11/2009
 * @version 1.0 Cria��o
 */
@SuppressWarnings("serial")
@Component("relatorioAlunosPendentesDeComponente")
@Scope("request")
public class RelatorioAlunosPendentesDeComponente extends AbstractRelatorioGraduacaoMBean {

	// p�ginas JSP
	/** Prefixo a ser usado nas p�ginas do relat�rio. */
	private static final String PREFIXO_JSP = "/graduacao/relatorios/coordenador/";
	/** Formul�rio de gera��o do relat�rio. */
	private static final String JSP_DO_FORMULARIO = PREFIXO_JSP + "form_relatorio_alunos_pendentes_de_componente.jsp";
	/** JSP do relat�rio gerado. */
	private static final String JSP_DO_RELATORIO  = PREFIXO_JSP + "relatorio_alunos_pendentes_de_componente.jsp";
	/** Atributo para controlar os discente com possibilidade de serem aptos a pagar determinado componente, 
	 * levando em considera��o pr�-requisitos com situa��o MATRICULADO. */
	private boolean filtroPossiveisAptos = false;
	/** Lista utilizada para armazenar as condi��es da gera��o do relat�rio.*/
	private List<String> condicoes;
	/** Armazena os resultados a serem mostrados no relat�rio. */
	private List<DiscenteGraduacao> resultados;
	/** Atributo respons�vel por manter o nome do relat�rio a ser exibido na view;*/
	private String nomeRelatorio;
	
	/**
     * Leva o usu�rio ao formul�rio do relat�rio. Tamb�m inicializa os par�metros do
     * formul�rio.
     * 
     * Chamado por /graduacao/menu_coordenador.jsp
	 *
	 * @author Br�ulio Bezerra
	 * @since 20/11/2009
	 * @version 1.0 Cria��o
	 */
	public String iniciar() throws DAOException {
		// seta valores iniciais dos filtros

		initObj();
		if ( isCoordenadorCurso() )
			curso = getCursoAtualCoordenacao();
		
		ano = null;
		periodo = null;
		nomeRelatorio = "Relat�rio de Alunos Pendentes de Componente Curricular";
		// e vai para o formul�rio
		return forward( JSP_DO_FORMULARIO );
	}
 
	 /**
	 * Leva o usu�rio ao formul�rio do relat�rio.
 	 * Tamb�m inicializa os par�metros.
	 * O filtro 'Listar Apenas Alunos Habilitados
     * a Cursar o Componente' � selecionado automaticamente.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\graduacao\departamento.jsp</li>
	 *    <li>sigaa.war\graduacao\menu_coordenador.jsp</li>
	 *    <li>sigaa.war\docente\menu_docente.jsp</li>
	 *   </ul>
	 *
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	 
	 
	public String iniciarAptos() throws DAOException {
		// seta valores iniciais dos filtros

		initObj();
		if ( isCoordenadorCurso() )
			curso = getCursoAtualCoordenacao();
		setFiltroPreRequisitos(true);

		ano = null;
		periodo = null;
		nomeRelatorio = "Relat�rio de Alunos Aptos a Cursar Determinado Componente Curricular";
		// e vai para o formul�rio
		return forward( JSP_DO_FORMULARIO );
	}

	/**
	 * Valida os filtros, faz a consulta ao banco atrav�s do DAO e redireciona
	 * o usu�rio para o relat�rio.
	 * 
	 * Chamado pela p�gina do formul�rio (indicada em <code>JSP_DO_FORMULARIO</code>)
	 *
	 * @author Br�ulio Bezerra
	 * @since 20/11/2009
	 * @version 1.0 Cria��o
	 * @version 1.1 Otimiza��o da busca
	 */
	public String gerar() throws DAOException, ArqException {
		
		// verifica par�metros passados
		
		if ( disciplina.getId() == 0 )
			addMensagem( MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Componente Curricular" );
		
		if ( hasErrors() )
			return null;
		
		disciplina = getDAO(ComponenteCurricularDao.class).findByPrimaryKey(disciplina.getId(), ComponenteCurricular.class);
		
		if (matrizCurricular.getId() > 0){
			matrizCurricular = getGenericDAO().findByPrimaryKey(matrizCurricular.getId(), MatrizCurricular.class);
		}
		
		Collection<DiscenteGraduacao> discentes = new ArrayList<DiscenteGraduacao>();
		Collection<Integer> idsDiscentes = new ArrayList<Integer>();
		
		// a consulta
		RelatoriosCoordenadorDao dao = getDAO( RelatoriosCoordenadorDao.class );
		Map<Integer, List<Integer>> mapDiscentes = dao.findAlunosPendentesDeComponente(curso.getId(), disciplina,
				ano!=null?ano:0, periodo!=null?periodo:0, isFiltroMatriculado() , isFiltroPreRequisitos(), isFiltroPossiveisAptos(), matrizCurricular.getId());
		
		/* 
		 * Relat�rio de discentes utilizando o filtro de alunos habilitados a cursar o curso, 
		 *de acordo com os pr�-requisitos do componente.
		 */ 
		boolean possuiPreRequisito;
		if (isFiltroPreRequisitos() && disciplina.getPreRequisito() != null && !disciplina.getPreRequisito().trim().equals("")){
			
			Map<Integer, String> equivalencias = ExpressaoUtil.getEquivalenciasExpressao(disciplina.getPreRequisito());
			
			for (Map.Entry<Integer, List<Integer>> map : mapDiscentes.entrySet()) {
				List<Integer> componentes = map.getValue();
				TreeSet<Integer> ids = new TreeSet<Integer>();
			
				for(Integer c : componentes){
					if ( !ValidatorUtil.isEmpty(c))
						ids.add(c);
				}
				
				if (map.getKey().intValue() == 523497){
					System.out.println(523497);
				}
				
				possuiPreRequisito = ExpressaoUtil.evalComTransitividade(disciplina.getPreRequisito(), ids, equivalencias);
				if(possuiPreRequisito){
					idsDiscentes.add(map.getKey());
				}	
			}	
			
		}else{
			for (Map.Entry<Integer, List<Integer>> map : mapDiscentes.entrySet()) {
				idsDiscentes.add(map.getKey());
			}
		}
		if (!idsDiscentes.isEmpty()){
			discentes = dao.findByIds(idsDiscentes);
		}
		// para cada discente, verifica se ele(a) j� cumpriu o componente atrav�s de um equivalente
		resultados = new ArrayList<DiscenteGraduacao>( discentes );
		MatriculaComponenteDao matDao = getDAO( MatriculaComponenteDao.class );
		
		for ( DiscenteGraduacao d : discentes ) {
			
			Collection<SituacaoMatricula> situacaoMatriculas =  SituacaoMatricula.getSituacoesPagas();
						
			Collection<MatriculaComponente> mats = matDao.findByDiscenteOtimizado( d, TipoComponenteCurricular.getAll(), situacaoMatriculas);
			
			Collection<ComponenteCurricular> comps = new ArrayList<ComponenteCurricular>();
			for ( MatriculaComponente mat : mats )
				comps.add( mat.getComponente() );
			
			// Remove os discentes que tenham pago componentes equivalentes ao objeto disciplina. 
			if ( MatriculaGraduacaoValidator.hasEquivalente( disciplina, comps) )
				resultados.remove(d);
		}
		
		// se n�o encontrou nada, avisa ao usu�rio
		if ( resultados.isEmpty() ) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		// ordena por Curso, Matriz Curricular e Nome do discente
		Collections.sort(resultados, new Comparator<DiscenteGraduacao>() {
			public int compare(DiscenteGraduacao d1, DiscenteGraduacao d2) {
				String a = StringUtils.toAsciiAndUpperCase(d1.getMatrizCurricular().getDescricao() + d1.getNome());
				String b = StringUtils.toAsciiAndUpperCase(d2.getMatrizCurricular().getDescricao() + d2.getNome());
				return a.compareTo(b);
			}
		});
		condicoes = new ArrayList<String>();
		if (isFiltroPreRequisitos()) 	condicoes.add("LISTAR ALUNOS APTOS A CURSAR O COMPONENTE");
		if (isFiltroPossiveisAptos()) 	condicoes.add("LISTAR ALUNOS PROV�VEIS A APTOS PARA CURSAR O COMPONENTE");
		if (isFiltroMatriculado())		condicoes.add("LISTAR ALUNOS MATRICULADOS NO COMPONENTE NO PER�ODO ATUAL");
		
		// o redirecionamento 
		return forward( JSP_DO_RELATORIO );
	}

	public boolean isCoordenadorCurso(){
		return isUserInRole(SigaaPapeis.COORDENADOR_CURSO) && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR); 
	}
	
	//// gets e sets ////
	public List<DiscenteGraduacao> getResultados() { return resultados; }

	public boolean isFiltroPossiveisAptos() {
		return filtroPossiveisAptos;
	}

	public void setFiltroPossiveisAptos(boolean filtroPossiveisAptos) {
		this.filtroPossiveisAptos = filtroPossiveisAptos;
	}

	public List<String> getCondicoes() {
		return condicoes;
	}

	public void setCondicoes(List<String> condicoes) {
		this.condicoes = condicoes;
	}

	public String getNomeRelatorio() {
		return nomeRelatorio;
	}

	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}
	
}
