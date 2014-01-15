/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 21/09/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.medio.dao.MatriculaDiscenteSerieDao;
import br.ufrn.sigaa.ensino.medio.dao.SerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.medio.dominio.SituacaoMatriculaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;
import br.ufrn.sigaa.ensino.medio.negocio.MovimentoAlteracaoStatusMatriculaMedio;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;

/**
 * Managed Bean responsável pelo controle das operações de matrícula do discente em série.
 * 
 * 
 * @author Rafael Gomes
 *
 */
@Component @Scope("request")
public class MatriculaDiscenteSerieMBean extends SigaaAbstractController<MatriculaDiscenteSerie>{
	
	/** Valor do ano a ser considerado na consulta de discentes. */
	private Integer ano;
	/** Curso selecionado para ser considerado na consulta de discentes.*/
	private CursoMedio curso;
	/** Série informada para ser considerada na consulta de discentes */
	private Serie serie;
	/** Lista utilizada para tratar as matriculas do discente em série, considerando as formas de consolidação 
	 * para matrícula do discente na série.*/
	List<MatriculaDiscenteSerie> listaDiscentes;
	/** Lista de Séries utilizada para a alteração da situação da série dos alunos pré-reprovados.*/
	List<Serie> listaSerie = new ArrayList<Serie>();
	/** Componentes listados para a seleção. */
	private Collection<SelectItem> series;
	/** Matrículas escolhidas a serem alteradas */
	private List<MatriculaDiscenteSerie> matriculasEscolhidas;
	

	/**
	 * Construtor 
	 */
	public MatriculaDiscenteSerieMBean() {
		init();
	}
	
	/**
	 * Método de inicialização de atributos;  
	 * @throws DAOException 
	 */
	private void init() {
		curso = new CursoMedio();
		serie = new Serie();
		serie.setCursoMedio(new CursoMedio());
		listaDiscentes = new ArrayList<MatriculaDiscenteSerie>();
		series = new ArrayList<SelectItem>();
		listaSerie = new ArrayList<Serie>();
	}
	
	/** 
	 * Prepara e redireciona o usuário para a operação de alteração de situação de matrículas de discentes pré-reprovados em série,
	 * após o processamento de consolidação de série.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war\medio\menus\turma.jsp</li>
	 * </ul>
	 * */
	public String iniciarPreReprovados() throws Exception {
		checkRole(SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO);
		setOperacaoAtiva(SigaaListaComando.CONSOLIDAR_MATRICULA_DISCENTE_SERIE.getId());
		
		CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		ano = calendario.getAno();
		init();
		
		return telaBuscaSerie();
	}
	
	@Override
	public String buscar() throws Exception {
		
		ValidatorUtil.validateRequired(ano, "Ano", erros);
		ValidatorUtil.validateRequiredId(serie.getCursoMedio().getId(), "Curso", erros);
		
		if (hasErrors())
			return null;
	
		MatriculaDiscenteSerieDao dao = getDAO(MatriculaDiscenteSerieDao.class);
		if(serie.getId() > 0)
			dao.initialize(serie);
		
		listaSerie = new ArrayList<Serie>();
		listaSerie = (List<Serie>) dao.findSerieWithMatriculaByAno(ano, serie.getCursoMedio().getId(), serie.getId(), SituacaoMatriculaSerie.REPROVADO);
		
		if (listaSerie.isEmpty())
			addMensagemWarning("Não há alunos REPROVADOS, para os dados de busca informados.");
		
		return null;
	}
	
	/**
	 * Seleciona a Série e lista os Discentes para alteração de Situação.<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/medio/matriculaDiscemteSerie/lista_serie.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws ArqException 
	 */
	public String selecionarSerie() throws ArqException{
		int idSerie = getParameterInt("id", 0);
		
		if (idSerie <= 0){			
			addMensagemWarning("Série não selecionada!");
			return null;
		} 
		
		MatriculaDiscenteSerieDao mdsDao = getDAO(MatriculaDiscenteSerieDao.class);
		
		try {
			serie = mdsDao.findSerieWithMatriculaByAno(ano, 0, idSerie, SituacaoMatriculaSerie.REPROVADO).iterator().next(); 
			if (serie == null || serie.getTurmas().isEmpty()) {
				addMensagemErro("Nenhuma Turma localizada para a Série selecionada!");
				return null;
			}
			
			/* Atribui "não selecionado" para todos e remover os alunos com situação diferente de reprovado. */
			for (TurmaSerie ts : serie.getTurmas()){
				for (Iterator<MatriculaDiscenteSerie> iterator = ts.getAlunos().iterator(); iterator.hasNext();) {
					MatriculaDiscenteSerie m = iterator.next();
					if (m.getSituacaoMatriculaSerie().getId() == SituacaoMatriculaSerie.REPROVADO.getId() && !m.getMatriculasDisciplinas().isEmpty()){
						m.setSelected(false);
						m.setNovaSituacaoMatricula(new SituacaoMatriculaSerie());
					} else {
						iterator.remove();
					}
				}
			}
			
			/* Remove as turmas sem alunos com situação de reprovado. */
			for (Iterator<TurmaSerie> it = serie.getTurmas().iterator(); it.hasNext();) {
				TurmaSerie t = it.next();
				if (t.getAlunos().isEmpty()){
					it.remove();
				}	
			}
			
			if (serie.getTurmas().isEmpty()){
				addMensagemWarning("Não há alunos REPROVADOS, para a série selecionada.");
				return null;
			}	
			
			prepareMovimento(SigaaListaComando.ALTERAR_STATUS_SERIE_DEPENDENCIA);
			
		} finally {
			if (mdsDao != null)
				mdsDao.close();
		}
		
		return telaSelecaoDiscentes();
	}
	
	/**
	 * Seleciona os Discentes que terão a situação de matricula alterada para Aprovado em Dependência.<br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/medio/alteracao_status_matricula/selecao_discentes.jsp</li>
	 * </ul>	
	 * @return
	 * @throws DAOException
	 */
	public String selecionarDiscentes() throws DAOException{
		matriculasEscolhidas = new ArrayList<MatriculaDiscenteSerie>();
		for (TurmaSerie ts : serie.getTurmas()){
			for (MatriculaDiscenteSerie m : ts.getAlunos()){
				if (m.isSelected()){
					m.setNovaSituacaoMatricula(SituacaoMatriculaSerie.APROVADO_DEPENDENCIA);
					matriculasEscolhidas.add(m);
				}
			}	
		}
		
		if (ValidatorUtil.isEmpty(matriculasEscolhidas)){
			addMensagemErro("Selecione pelo menos um Discente.");
			return null;
		}			
		
		return telaViewSelecionados();
	}
	
	/**
	 * Realizar alteração dos status dos discentes selecionados<br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/medio/matriculaDiscenteSerie/discentes_selecionados.jsp</li>
	 * </ul>	
	 * @return
	 * @throws Exception 
	 */
	public String efetuarAlteracaoStatusGeral() throws Exception {
		alterarStatus(SigaaListaComando.ALTERAR_STATUS_SERIE_DEPENDENCIA);
		return iniciarPreReprovados();
	}
	
	/**
	 * Alterar os Status dos discentes selecionados.
	 * @param comando
	 * @throws ArqException
	 */
	private void alterarStatus(Comando comando) throws ArqException{
		try {
			MovimentoAlteracaoStatusMatriculaMedio movMatricula = new MovimentoAlteracaoStatusMatriculaMedio();
			movMatricula.setMatriculas(matriculasEscolhidas);
			movMatricula.setCodMovimento(getUltimoComando());
			
			execute(movMatricula);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens( e.getListaMensagens() );
			return;
		}		
	}
	
	/**
	 * Direciona o usuário para a tela de seleção de discentes.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/matriculaDiscenteSerie/lista.jsp.</li>
	 * </ul>
	 */
	public String telaSelecaoDiscentes() {
		return forward("/medio/matriculaDiscenteSerie/selecao_discentes.jsp");
	}
	
	/**
	 * Direciona o usuário para a tela de visualização dos discentes selecionados.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * @return
	 */
	public String telaViewSelecionados(){
		return forward("/medio/matriculaDiscenteSerie/discentes_selecionados.jsp");
	}
	
	/**
	 * Direciona o usuário para a tela de seleção de discentes.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/matriculaDiscenteSerie/lista.jsp.</li>
	 * </ul>
	 */
	private String telaBuscaSerie() {
		return forward("/medio/matriculaDiscenteSerie/lista_serie.jsp");
	}
	
	/** 
	 * Carrega as séries pertencentes ao curso de ensino médio selecionado na jsp..
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/medio/matriculaDiscenteSerie/lista_serie.jsp</li>
	 * </ul>
 	 * @param e
	 * @throws DAOException
	 */
	public void carregarSeriesByCurso(ValueChangeEvent e) throws DAOException {
		SerieDao dao = getDAO( SerieDao.class );
		if( e != null && (Integer)e.getNewValue() > 0 )
			serie.setCursoMedio( dao.findByPrimaryKey((Integer)e.getNewValue(), CursoMedio.class) );
		else {
			series = new ArrayList<SelectItem>(0);
			return;
		}	
		serie.getCursoMedio().setNivel(getNivelEnsino());
		if (serie.getCursoMedio() != null)
			series = toSelectItems(dao.findByCurso(serie.getCursoMedio()), "id", "descricaoCompleta");
	}
	
	/**
	 * Retorna a Lista de Situações de Matrícula<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/medio/matriculaDiscenteSerie/selecao_discentes.jsp</li>
	 * </ul>	
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getSituacoes() throws DAOException {
		GenericDAO dao = getGenericDAO();
		try {
			Collection<SituacaoMatriculaSerie> situacoesAlteracaoMatricula = SituacaoMatriculaSerie.getSituacoesMatriculadoOuConcluido();
			situacoesAlteracaoMatricula.add(SituacaoMatriculaSerie.CANCELADO);
			return toSelectItems(situacoesAlteracaoMatricula, "id", "descricao");
		} finally {
			if (dao != null)
				dao.close();
		}
	}	
	
	public Integer getAno() {
		return ano;
	}
	
	public void setAno(Integer ano) {
		this.ano = ano;
	}
	
	public CursoMedio getCurso() {
		return curso;
	}
	
	public void setCurso(CursoMedio curso) {
		this.curso = curso;
	}
	
	public Serie getSerie() {
		return serie;
	}
	
	public void setSerie(Serie serie) {
		this.serie = serie;
	}
	
	public List<MatriculaDiscenteSerie> getListaDiscentes() {
		return listaDiscentes;
	}
	
	public void setListaDiscentes(List<MatriculaDiscenteSerie> listaDiscentes) {
		this.listaDiscentes = listaDiscentes;
	}

	public Collection<SelectItem> getSeries() {
		return series;
	}

	public void setSeries(Collection<SelectItem> series) {
		this.series = series;
	}

	public List<Serie> getListaSerie() {
		return listaSerie;
	}

	public void setListaSerie(List<Serie> listaSerie) {
		this.listaSerie = listaSerie;
	}

	public List<MatriculaDiscenteSerie> getMatriculasEscolhidas() {
		return matriculasEscolhidas;
	}

	public void setMatriculasEscolhidas(
			List<MatriculaDiscenteSerie> matriculasEscolhidas) {
		this.matriculasEscolhidas = matriculasEscolhidas;
	}
}
