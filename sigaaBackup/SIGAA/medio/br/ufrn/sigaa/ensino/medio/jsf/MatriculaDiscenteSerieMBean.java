/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Managed Bean respons�vel pelo controle das opera��es de matr�cula do discente em s�rie.
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
	/** S�rie informada para ser considerada na consulta de discentes */
	private Serie serie;
	/** Lista utilizada para tratar as matriculas do discente em s�rie, considerando as formas de consolida��o 
	 * para matr�cula do discente na s�rie.*/
	List<MatriculaDiscenteSerie> listaDiscentes;
	/** Lista de S�ries utilizada para a altera��o da situa��o da s�rie dos alunos pr�-reprovados.*/
	List<Serie> listaSerie = new ArrayList<Serie>();
	/** Componentes listados para a sele��o. */
	private Collection<SelectItem> series;
	/** Matr�culas escolhidas a serem alteradas */
	private List<MatriculaDiscenteSerie> matriculasEscolhidas;
	

	/**
	 * Construtor 
	 */
	public MatriculaDiscenteSerieMBean() {
		init();
	}
	
	/**
	 * M�todo de inicializa��o de atributos;  
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
	 * Prepara e redireciona o usu�rio para a opera��o de altera��o de situa��o de matr�culas de discentes pr�-reprovados em s�rie,
	 * ap�s o processamento de consolida��o de s�rie.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemWarning("N�o h� alunos REPROVADOS, para os dados de busca informados.");
		
		return null;
	}
	
	/**
	 * Seleciona a S�rie e lista os Discentes para altera��o de Situa��o.<br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/medio/matriculaDiscemteSerie/lista_serie.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws ArqException 
	 */
	public String selecionarSerie() throws ArqException{
		int idSerie = getParameterInt("id", 0);
		
		if (idSerie <= 0){			
			addMensagemWarning("S�rie n�o selecionada!");
			return null;
		} 
		
		MatriculaDiscenteSerieDao mdsDao = getDAO(MatriculaDiscenteSerieDao.class);
		
		try {
			serie = mdsDao.findSerieWithMatriculaByAno(ano, 0, idSerie, SituacaoMatriculaSerie.REPROVADO).iterator().next(); 
			if (serie == null || serie.getTurmas().isEmpty()) {
				addMensagemErro("Nenhuma Turma localizada para a S�rie selecionada!");
				return null;
			}
			
			/* Atribui "n�o selecionado" para todos e remover os alunos com situa��o diferente de reprovado. */
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
			
			/* Remove as turmas sem alunos com situa��o de reprovado. */
			for (Iterator<TurmaSerie> it = serie.getTurmas().iterator(); it.hasNext();) {
				TurmaSerie t = it.next();
				if (t.getAlunos().isEmpty()){
					it.remove();
				}	
			}
			
			if (serie.getTurmas().isEmpty()){
				addMensagemWarning("N�o h� alunos REPROVADOS, para a s�rie selecionada.");
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
	 * Seleciona os Discentes que ter�o a situa��o de matricula alterada para Aprovado em Depend�ncia.<br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
	 * Realizar altera��o dos status dos discentes selecionados<br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
	 * Direciona o usu�rio para a tela de sele��o de discentes.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/matriculaDiscenteSerie/lista.jsp.</li>
	 * </ul>
	 */
	public String telaSelecaoDiscentes() {
		return forward("/medio/matriculaDiscenteSerie/selecao_discentes.jsp");
	}
	
	/**
	 * Direciona o usu�rio para a tela de visualiza��o dos discentes selecionados.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 * @return
	 */
	public String telaViewSelecionados(){
		return forward("/medio/matriculaDiscenteSerie/discentes_selecionados.jsp");
	}
	
	/**
	 * Direciona o usu�rio para a tela de sele��o de discentes.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/matriculaDiscenteSerie/lista.jsp.</li>
	 * </ul>
	 */
	private String telaBuscaSerie() {
		return forward("/medio/matriculaDiscenteSerie/lista_serie.jsp");
	}
	
	/** 
	 * Carrega as s�ries pertencentes ao curso de ensino m�dio selecionado na jsp..
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Retorna a Lista de Situa��es de Matr�cula<br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
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
