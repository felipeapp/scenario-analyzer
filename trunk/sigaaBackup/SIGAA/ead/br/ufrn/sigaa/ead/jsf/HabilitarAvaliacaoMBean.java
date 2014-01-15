/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 21/11/2007
 */
package br.ufrn.sigaa.ead.jsf;

import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.MetodologiaAvaliacao;
import br.ufrn.sigaa.ead.dominio.SemanaAvaliacao;
import br.ufrn.sigaa.ead.negocio.MetodologiaAvaliacaoHelper;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/**
 * Managed Bean para habilitar ou desabilitar a avaliação de aulas por curso.
 * 
 * @author David Pereira
 *
 */
@Component("habilitarAvaliacao") @Scope("session")
public class HabilitarAvaliacaoMBean extends SigaaAbstractController<MetodologiaAvaliacao> {

	/** Representa os cursos */
	private DataModel cursosModel; 
	
	/** Representa os componentes */
	private DataModel componentesModel;

	/** Representa as semanas de aula que podem ser habilitadas para avaliação do tutor*/
	private DataModel semanasModel;
	
	/** Curso selecionado para habilitar as avaliações */
	private Curso cursoSelecionado;
	
	/** Componente selecionado para habilitar as avaliações */
	private ComponenteCurricular componenteSelecionado;
	
	/**
	 * Inicia o bean carregando todos os cursos a distância
	 * 
	 * habilitarAvaliacao.iniciar
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/SIGAA/app/sigaa.ear/sigaa.war/ead/MetodologiaAvaliacao/habilitar.jsp</li>
	 *    <li>/SIGAA/app/sigaa.ear/sigaa.war/ead/MetodologiaAvaliacao/listaComponentes.jsp</li>
	 *    <li>/SIGAA/app/sigaa.ear/sigaa.war/ead/menu.jsp</li>
	 *   </ul>
	 * @return
	 * @throws DAOException
	 */
	public String iniciar() throws DAOException {
		cursosModel = new ListDataModel(getDAO(CursoDao.class).findAllCursosADistancia());
		return forward("/ead/MetodologiaAvaliacao/listaCursos.jsp");
	}
	
	/**
	 * Faz uma busca pela metodologia de avaliação de acordo com o curso selecionado
	 *
	 * habilitarAvaliacao.selecionaCurso
 	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/SIGAA/app/sigaa.ear/sigaa.war/ead/MetodologiaAvaliacao/listaCursos.jsp</li>
	 *   </ul>
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public String selecionaCurso() throws DAOException, NegocioException {
		cursoSelecionado = (Curso) cursosModel.getRowData();
		CalendarioAcademico calendario = getCalendarioVigente();
		
		obj = MetodologiaAvaliacaoHelper.getMetodologia(cursoSelecionado, getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo());
		if (obj == null)
			throw new NegocioException("Não há metodologia de avaliação cadastrada para o curso selecionado.");
			
		if (!obj.isUmaProva()) {
			semanasModel = new ListDataModel(obj.getSemanasAvaliacao()); 
			return forward("/ead/MetodologiaAvaliacao/habilitar.jsp");
		} else {
			List<ComponenteCurricular> componentes = getDAO(ComponenteCurricularDao.class).findComponentesPorCursoGraduacao(cursoSelecionado, calendario.getAno(), calendario.getPeriodo(), false);
			semanasModel = new ListDataModel(SemanaAvaliacao.fromComponentes(obj, componentes));
			return forward("/ead/MetodologiaAvaliacao/listaComponentes.jsp");
		}
	}
	
	/**
	 * Habilita a aula
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/MetodologiaAvaliacao/habilitar.jsp</li>
	 * <li>/sigaa.war/ead/MetodologiaAvaliacao/listaComponentes.jsp</li>
	 * </ul>
	 * @return
	 */
	public String habilitarAula() {
		return alterarStatusSemana(SigaaListaComando.HABILITAR_AVALIACAO_SEMANA);
	}
	
	/**
	 * Desabilita a aula
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/MetodologiaAvaliacao/habilitar.jsp</li>
	 * <li>/sigaa.war/ead/MetodologiaAvaliacao/listaComponentes.jsp</li>
	 * </ul>
	 * @return
	 */
	public String desabilitarAula() {
		return alterarStatusSemana(SigaaListaComando.DESABILITAR_AVALIACAO_SEMANA);
	}
	
	/**
	 * Redireciona para a listagem dos componentes
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/MetodologiaAvaliacao/habilitar.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaComponente() {
		return forward("/ead/MetodologiaAvaliacao/listaComponentes.jsp");
	}
	
	/**
	 * Altera o status da semana
	 * @param comando
	 * @return
	 */
	private String alterarStatusSemana(Comando comando) {
		try {
			prepareMovimento(comando);
			SemanaAvaliacao semana = (SemanaAvaliacao) semanasModel.getRowData();
			semana.setCodMovimento(comando);
			obj = (MetodologiaAvaliacao) execute(semana);
			
			if (!obj.isUmaProva())
				semanasModel = new ListDataModel(obj.getSemanasAvaliacao());
			else {
				CalendarioAcademico calendario = getCalendarioVigente();
				List<ComponenteCurricular> componentes = getDAO(ComponenteCurricularDao.class).findComponentesPorCursoGraduacao(cursoSelecionado, calendario.getAno(), calendario.getPeriodo(), false);
				semanasModel = new ListDataModel(SemanaAvaliacao.fromComponentes(obj, componentes));
			}
				
		} catch(NegocioException e) {
			addMensagemErro(e.getMessage());
		} catch(Exception e) {
			addMensagemErroPadrao();
		}
		
		return null;
	}
	
	/**
	 * Retorna os cursos
	 * @return
	 * @throws DAOException
	 */
	public DataModel getCursosModel() throws DAOException {
		return cursosModel;
	}

	public Curso getCursoSelecionado() {
		return cursoSelecionado;
	}

	public DataModel getSemanasModel() {
		return semanasModel;
	}

	public ComponenteCurricular getComponenteSelecionado() {
		return componenteSelecionado;
	}

	public void setComponenteSelecionado(ComponenteCurricular componenteSelecionado) {
		this.componenteSelecionado = componenteSelecionado;
	}

	public DataModel getComponentesModel() {
		return componentesModel;
	}

	public void setComponentesModel(DataModel componentesModel) {
		this.componentesModel = componentesModel;
	}

}
