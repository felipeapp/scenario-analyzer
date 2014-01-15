/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 01/07/2011
* 
*/
package br.ufrn.sigaa.ensino.medio.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoOperacaoMatricula;
import br.ufrn.sigaa.ensino.medio.dao.MatriculaComponenteMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.TurmaSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.medio.dominio.SituacaoMatriculaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;
import br.ufrn.sigaa.ensino.medio.negocio.MovimentoAlteracaoStatusMatriculaMedio;

/**
 * MBeam usado para alterações de situações de matrículas de discentes do ensino médio
 * 
 * @author Arlindo
 */
@Component("alteracaoStatusMatriculaMedioMBean") @Scope("request")
public class AlteracaoStatusMatriculaMedioMBean extends SigaaAbstractController<MatriculaDiscenteSerie> implements OperadorDiscente {

	/** Discente escolhido  */
	private DiscenteMedio discente;
	/** Todas as matrículas do discente escolhido */
	private List<MatriculaDiscenteSerie> matriculas;
	/** Matrículas escolhidas a serem alteradas */
	private List<MatriculaDiscenteSerie> matriculasEscolhidas;
	/** Todas as matrículas Componente do discente escolhido */
	private List<MatriculaComponente> matriculasComponente;
	/** Todas as matrículas Componente escolhidas a serem alteradas */
	private List<MatriculaComponente> matriculasComponenteEscolhidas;
	/** Filtro da busca pelo código */
	private boolean filtroNumero;
	/** Filtro da busca pelo Curso */
	private boolean filtroCurso;
	/** Collection que irá armazenar a listagem das Series. */
	private List<TurmaSerie> listaSeries= new ArrayList<TurmaSerie>(); 	
	/** Filtro da busca pelo Ano */
	private boolean filtroAno;
	/** Filtro da busca pela Turma */
	private boolean filtroTurma;
	/** Ano informado na busca de série */
	private Integer ano;
	/** Turma informada na busca de série */
	private String turma;	
	/** Série informada na busca */
	private Serie serie;
	/** Turma selecionada */
	private TurmaSerie turmaSerie;
	/** Atributo utilizado quando a alteração já é para uma situação específica */
	private SituacaoMatricula novaSituacao;
	
	/** Limpa os dados da classe. */
	private void clear() {
		matriculas = new ArrayList<MatriculaDiscenteSerie>();
		matriculasEscolhidas = new ArrayList<MatriculaDiscenteSerie>();
		discente = new DiscenteMedio();
		serie = new Serie();
		serie.setCursoMedio(new CursoMedio());
	}	
	
	/** Construtor da Classe */
	public AlteracaoStatusMatriculaMedioMBean() {
		clear();
	}	
	
	/**
	 * Iniciar fluxo geral para alteração de status de matricula<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/medio/menus/discente.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException {
		checkRole(SigaaPapeis.SECRETARIA_MEDIO, SigaaPapeis.GESTOR_MEDIO);
		clear();
		return forward(getListPage());
	}
	
	/**
	 * Iniciar fluxo geral para alteração de status de matricula em uma disciplina<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/medio/menus/discente.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarAlteracaoStatusDisciplina() throws SegurancaException {
		checkRole(new int[] { SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO});
		clear();
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ALTERACAO_STATUS_DISCIPLINA);
		return buscaDiscenteMBean.popular();
	}
	
	/**
	 * Método responsável pela busca de séries de ensino Médio.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/serie/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String buscar() throws ArqException {
		
		if (!filtroAno && !filtroNumero && !filtroTurma && !filtroCurso)
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		
		if (filtroAno && (ano == null || ano < 1900)) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		
		if (filtroNumero && isEmpty(serie.getNumero())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Número de Série");
		
		if (filtroTurma && isEmpty(turma)) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Turma");
		
		if (filtroCurso && isEmpty(serie.getCursoMedio().getId())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
		
		if (hasErrors())
			return null;
	
		TurmaSerieDao dao = getDAO(TurmaSerieDao.class);
		try {
			listaSeries = dao.findBySerie(
					(filtroCurso ? serie.getCursoMedio() : null),
					(filtroAno ? ano : null), 
					(filtroNumero ? serie.getNumero() : null),
					null, 
					(filtroTurma ? turma : null));
			if (listaSeries.isEmpty()) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward(getListPage());
	}	
	
	/**
	 * Seleciona a Turma e lista os Discentes para alteração de Situação.<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/medio/serie/lista.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws ArqException 
	 */
	public String selecionarTurma() throws ArqException{
		int idTurma = getParameterInt("id", 0);
		
		if (idTurma <= 0){			
			addMensagemWarning("Turma não selecionada!");
			return null;
		} 
		
		
		TurmaSerieDao dao = getDAO(TurmaSerieDao.class);	
		
		try {
			matriculas = dao.findParticipantesByTurma(idTurma);
			
			if (matriculas == null || matriculas.isEmpty()) {
				addMensagemErro("Nenhum discente matriculado na Turma selecionada!");
				return null;
			}
			
			/* Atribui "não selecionado" para todos */
			for (MatriculaDiscenteSerie m : matriculas){
				m.setSelected(false);
				m.setNovaSituacaoMatricula(new SituacaoMatriculaSerie());				
			}
			
			turmaSerie = listaSeries.get(listaSeries.indexOf( new TurmaSerie(idTurma) ));
			
			prepareMovimento(SigaaListaComando.ALTERAR_STATUS_MATRICULA_MEDIO);
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return telaSelecaoDiscentes();
	}
	
	/**
	 * Seleciona os Discentes que terão a situação de matricula alterada.<br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/medio/alteracao_status_matricula/selecao_discentes.jsp</li>
	 * </ul>	
	 * @return
	 * @throws DAOException
	 */
	public String selecionarDiscentes() throws DAOException{
		matriculasEscolhidas = new ArrayList<MatriculaDiscenteSerie>();
		for (MatriculaDiscenteSerie m : matriculas){
			if (m.getNovaSituacaoMatricula().getId() > 0){
				m.setNovaSituacaoMatricula(SituacaoMatriculaSerie.getSituacao(m.getNovaSituacaoMatricula().getId())); 
				matriculasEscolhidas.add(m);
			}
		}
		
		if (ValidatorUtil.isEmpty(matriculasEscolhidas)){
			addMensagemErro("Altere a Situação de Matrícula de pelo menos um Discente.");
			return null;
		}			
		
		return telaViewSelecionados();
	}	
	
	/**
	 * Seleciona o Discente que será alterado.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	public String selecionaDiscente() throws ArqException {
		
		MatriculaComponenteMedioDao mDao = null; 
		try {
			mDao = getDAO(MatriculaComponenteMedioDao.class);
			matriculasComponente = mDao.findAllMatriculasByDiscente(discente);
			if (matriculasComponente == null || matriculasComponente.isEmpty()) {
				addMensagemErro("O discente selecionado não encontra-se matriculado em disciplinas");
				return null;
			}
		} finally {
			if (mDao != null)
				mDao.close();
		}
		return forward("/medio/alteracao_status_matricula/selecao_matriculas.jsp");
	}
	
	/**
	 * Seleciona as matrículas que terão o situação alterada.<br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/medio/alteracao_status_matricula/selecao_matriculas.jsp</li>
	 * </ul>	
	 * @return
	 * @throws ArqException 
	 */
	public String selecionarMatriculas() throws ArqException {
		String[] linhas = getCurrentRequest().getParameterValues("matriculas");
		if ( linhas == null || linhas.length == 0 ) {
			addMensagemErro("Ao menos uma matrícula deve ser escolhida");
			return null;
		}
		novaSituacao = new SituacaoMatricula();
		matriculasComponenteEscolhidas = new ArrayList<MatriculaComponente>(0);
		prepareMovimento(SigaaListaComando.ALTERACAO_STATUS_DISCIPLINA);
		GenericDAO dao = getGenericDAO();
		try {
			for (String linha : linhas) {
				MatriculaComponente mat = matriculasComponente.get(new Integer(linha));
				matriculasComponenteEscolhidas.add(dao.findByPrimaryKey(mat.getId(), MatriculaComponente.class) );
			}					
		} finally {
			if (dao != null)
				dao.close();
		}
		return forward("/medio/alteracao_status_matricula/selecao_status.jsp");
	}
	
	/**
	 * Realizar alteração dos status nas matrículas selecionadas<br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/medio/alteracao_status_matricula/selecao_status.jsp</li>
	 * </ul>	
	 * @return
	 * @throws ArqException
	 */
	public String efetuarAlteracaoStatusMatriculas() throws ArqException {
		try {
			MovimentoOperacaoMatricula movMatricula = new MovimentoOperacaoMatricula();
			movMatricula.setMatriculas(matriculasComponenteEscolhidas);
			movMatricula.setNovaSituacao(novaSituacao);
			movMatricula.setCodMovimento(SigaaListaComando.ALTERACAO_STATUS_DISCIPLINA);
			
			execute(movMatricula);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens( e.getListaMensagens() );
			return null;
		}	
		return cancelar();
	}	
	
	/**
	 * Realizar alteração dos status dos discentes selecionados<br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/medio/alteracao_status_matricula/discentes_selecionados.jsp</li>
	 * </ul>	
	 * @return
	 * @throws ArqException
	 */
	public String efetuarAlteracaoStatusGeral() throws ArqException {
		alterarStatus(SigaaListaComando.ALTERAR_STATUS_MATRICULA_MEDIO);
		return cancelar();
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
			movMatricula.setTurmaSerie(turmaSerie);
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
	 *   <li>/sigaa.war/medio/alteracao_status_matricula/lista.jsp.</li>
	 * </ul>
	 */
	public String telaSelecaoDiscentes() {
		return forward("/medio/alteracao_status_matricula/selecao_discentes.jsp");
	}	
	
	/**
	 * Direciona o usuário para a tela de busca de discentes.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/alteracao_status_matricula/selecao_matriculas.jsp.</li>
	 * </ul>
	 */
	public String telaBuscaDiscentes() {
		return forward("/graduacao/busca_discente.jsp");
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
		return forward("/medio/alteracao_status_matricula/discentes_selecionados.jsp");
	}	

	/** Caminho da busca de séries */
	@Override
	public String getListPage() {
		return "/medio/alteracao_status_matricula/lista.jsp";
	}
	
	/**
	 * Retorna a Lista de Situações de Matrícula<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/medio/alteracao_status_matricula/selecao_discentes.jsp</li>
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

	/**
	 * Direciona o usuário para a tela de seleção de matrículas.
	 * <br /><br />
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * <li>sigaa.war/graduacao/alteracao_status_matricula/selecao_status.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaSelecaoMatriculas() {
		return forward("/medio/alteracao_status_matricula/selecao_matriculas.jsp");
	}
	
	/**
	 * Retorna a Lista de Situações de Matrícula<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/medio/alteracao_status_matricula/selecao_status.jsp</li>
	 * </ul>	
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getSituacoesMatricula() throws DAOException {
		List<SelectItem> situacoes = toSelectItems(SituacaoMatricula.getSituacoesMedio(),"id","descricao");
		return situacoes;
	}
	
	public DiscenteMedio getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteMedio discente) {
		this.discente = discente;
	}

	public Serie getSerie() {
		return serie;
	}

	public void setSerie(Serie serie) {
		this.serie = serie;
	}

	public boolean isFiltroNumero() {
		return filtroNumero;
	}

	public void setFiltroNumero(boolean filtroNumero) {
		this.filtroNumero = filtroNumero;
	}

	public boolean isFiltroCurso() {
		return filtroCurso;
	}

	public void setFiltroCurso(boolean filtroCurso) {
		this.filtroCurso = filtroCurso;
	}

	public List<TurmaSerie> getListaSeries() {
		return listaSeries;
	}

	public void setListaSeries(List<TurmaSerie> listaSeries) {
		this.listaSeries = listaSeries;
	}

	public void setMatriculas(List<MatriculaDiscenteSerie> matriculas) {
		this.matriculas = matriculas;
	}

	public void setMatriculasEscolhidas(
			List<MatriculaDiscenteSerie> matriculasEscolhidas) {
		this.matriculasEscolhidas = matriculasEscolhidas;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public String getTurma() {
		return turma;
	}

	public void setTurma(String turma) {
		this.turma = turma;
	}

	public boolean isFiltroAno() {
		return filtroAno;
	}

	public void setFiltroAno(boolean filtroAno) {
		this.filtroAno = filtroAno;
	}

	public boolean isFiltroTurma() {
		return filtroTurma;
	}

	public void setFiltroTurma(boolean filtroTurma) {
		this.filtroTurma = filtroTurma;
	}

	public TurmaSerie getTurmaSerie() {
		return turmaSerie;
	}

	public void setTurmaSerie(TurmaSerie turmaSerie) {
		this.turmaSerie = turmaSerie;
	}

	public List<MatriculaDiscenteSerie> getMatriculas() {
		return matriculas;
	}

	public List<MatriculaDiscenteSerie> getMatriculasEscolhidas() {
		return matriculasEscolhidas;
	}

	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		// TODO Auto-generated method stub
		setDiscente((DiscenteMedio) discente);
	}

	public void setMatriculasComponente(List<MatriculaComponente> matriculasComponente) {
		this.matriculasComponente = matriculasComponente;
	}

	public List<MatriculaComponente> getMatriculasComponente() {
		return matriculasComponente;
	}

	public void setMatriculasComponenteEscolhidas(
			List<MatriculaComponente> matriculasComponenteEscolhidas) {
		this.matriculasComponenteEscolhidas = matriculasComponenteEscolhidas;
	}

	public List<MatriculaComponente> getMatriculasComponenteEscolhidas() {
		return matriculasComponenteEscolhidas;
	}

	public void setNovaSituacao(SituacaoMatricula novaSituacao) {
		this.novaSituacao = novaSituacao;
	}

	public SituacaoMatricula getNovaSituacao() {
		return novaSituacao;
	}
}
