/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

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
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoOperacaoMatricula;
import br.ufrn.sigaa.ensino.jsf.BuscaTurmaMBean;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean usado para alterações de situações de matrículas
 * @author david pereira
 * @author André Dantas
 */
@SuppressWarnings("serial")
@Component("alteracaoStatusMatricula")
@Scope("session")
public class AlteracaoStatusMatriculaMBean extends SigaaAbstractController<Object> implements OperadorDiscente {

	/**
	 * Discente escolhido
	 */
	private DiscenteAdapter discente;

	/**
	 * Todas as matrículas do discente escolhido
	 */
	private List<MatriculaComponente> matriculas;

	/**
	 * Atributo utilizado quando a alteração já é para uma situação específica
	 */
	private SituacaoMatricula novaSituacao;

	/**
	 * Matrículas escolhidas a serem alteradas
	 */
	private List<MatriculaComponente> matriculasEscolhidas;

	/**
	 * Indica se é trancamento
	 */
	private boolean trancamento;
	
	/**
	 * Turma selecionada.
	 */
	private Turma turma;
	
	/** Define o link para a tela de seleção de matrículas */
	public static final String JSP_SELECAO_MATRICULA = "/graduacao/alteracao_status_matricula/selecao_matriculas.jsp";

	public SituacaoMatricula getNovaSituacao() {
		return novaSituacao;
	}

	public void setNovaSituacao(SituacaoMatricula situacaoPreDefinida) {
		this.novaSituacao = situacaoPreDefinida;
	}

	/**
	 * Construtor da Classe
	 */
	public AlteracaoStatusMatriculaMBean() {
		clear();
	}

	/**
	 * Inicia o Trancamento de Matrícula<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menus/programa.jsp</li>
	 *   <li>/sigaa.war/graduacao/secretaria.jsp</li>
	 *   <li>/sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/discente.jsp</li>
	 *   <li>/sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String iniciarTrancamentoMatricula() throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.SECRETARIA_CENTRO, SigaaPapeis.GESTOR_LATO,
				SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
		clear();
		setNovaSituacao(SituacaoMatricula.TRANCADO);
		trancamento = true;
		return iniciar(OperacaoDiscente.TRANCAMENTO_MATRICULA);
	}

	/**
	 * Inicia o Cancelamento de Matrícula.<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ead/menu.jsp</li>
	 *   <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String iniciarCancelamentoMatricula() throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.COORDENADOR_GERAL_EAD);
		clear();
		setNovaSituacao(SituacaoMatricula.CANCELADO);
		trancamento = true;
		return iniciar(OperacaoDiscente.CANCELAMENTO_MATRICULA, true);
	}

	/**
	 * Iniciar fluxo geral para alteração de status de matricula<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menus/cdp.jsp</li>
	 *   <li>/sigaa.war/graduacao/menus/programa.jsp</li>
	 *   <li>/sigaa.war/infantil/menu.jsp</li>
	 *   <li>/sigaa.war/lato/menu_coordenador.jsp</li>
	 *   <li>/sigaa.war/stricto/menus/discente.jsp</li>
	 *   <li>/sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/discente.jsp</li>
	 *   <li>/sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/discente.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO,
				SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.CDP,
				SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO, SigaaPapeis.GESTOR_INFANTIL,
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
		clear();
		trancamento = false;
		return iniciar(OperacaoDiscente.ALTERACAO_STATUS_MATRICULA);
	}

	/**
	 * Inicia o processo de alteração de status da matrícula.
	 * 
	 * @param operacao
	 * @return
	 * @throws SegurancaException
	 */
	private String iniciar(int operacao) throws SegurancaException {
		return iniciar(operacao, false);
	}

	/**
	 * Inicia o processo de alteração de status da matrícula.
	 * 
	 * @param operacao
	 * @param ead
	 * @return
	 * @throws SegurancaException
	 */
	private String iniciar(int operacao, boolean ead) throws SegurancaException {

		try {
			prepareMovimento(SigaaListaComando.ALTERAR_STATUS_MATRICULA);
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErroPadrao();
			return null;
		}

		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setEad(ead);
		buscaDiscenteMBean.setCodigoOperacao(operacao);
		return buscaDiscenteMBean.popular();
	}
	
	
	/**
	 * Inicia a Alteração de Situação de Matrícula dos Alunos da Turma Selecionada.<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/alteracao_status_matricula/selecao_discentes.jsp</li>
	 *   <li>/sigaa.war/graduacao/menus/programa.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws DAOException 
	 */
	public String iniciarAlterarSituacaoTurma() throws DAOException{
		BuscaTurmaMBean turma = (BuscaTurmaMBean) getMBean("buscaTurmaBean");
		return turma.popularBuscaGeral();
	}
	
	/**
	 * Seleciona a Turma e lista os Discentes para alteração de Situação.<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ensino/turma/busca_turma.jsp</li>
	 *   <li>/sigaa.war/graduacao/alteracao_status_matricula/view_discentes_selecionados.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws ArqException 
	 */
	public String selecionarAlterarSituacaoMatricula() throws ArqException{
		int idTurma = getParameterInt("id", 0);
		
		if (idTurma > 0)
			turma = new Turma(idTurma); 			
		else
		if (idTurma <= 0 && ValidatorUtil.isEmpty(turma)){			
			addMensagemWarning("Turma não selecionada!");
			return null;
		}
		
		TurmaDao dao = getDAO(TurmaDao.class);	
		
		try {
			matriculas = (List<MatriculaComponente>) dao.findParticipantesTurma(turma.getId());
			
			if (matriculas == null || matriculas.isEmpty()) {
				addMensagemErro("Nenhum discente matriculado na Turma selecionada!");
				return null;
			}
			
			/* Atribui "não selecionado" para todos */
			for (MatriculaComponente m : matriculas){
				m.setSelected(false);
				m.setNovaSituacaoMatricula(new SituacaoMatricula(m.getSituacaoMatricula().getId(), m.getSituacaoMatricula().getDescricao()));				
			}
			
			turma = dao.findByPrimaryKey(turma.getId(), Turma.class);
			
			prepareMovimento(SigaaListaComando.ALTERAR_STATUS_GERAL_TURMA);
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return telaSelecaoDiscentes();
	}

	/**
	 * Volta para a busca de discentes<br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/alteracao_status_matricula/selecao_matriculas.jsp</li>
	 * </ul>	
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String voltarBuscaDiscente() throws SegurancaException, DAOException {
		if (trancamento) {
			return iniciarTrancamentoMatricula();
		} else {
			return iniciar();
		}

	}

	/**
	 * Seleciona o Discente que será alterado.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	public String selecionaDiscente() {


		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);
		try {
			if (novaSituacao == null || novaSituacao.getId() == 0){
				Collection<SituacaoMatricula> situacoes = SituacaoMatricula.getSituacoesTodas();
				matriculas = (List<MatriculaComponente>) matriculaDao.findByDiscenteOtimizado(discente, TipoComponenteCurricular.getAll(), situacoes);
			}
			else if (novaSituacao.equals(SituacaoMatricula.TRANCADO) || novaSituacao.equals(SituacaoMatricula.CANCELADO)) {
				Collection<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>();
				situacoes.add(SituacaoMatricula.MATRICULADO);
				situacoes.add(SituacaoMatricula.EM_ESPERA);
				
				matriculas = (List<MatriculaComponente>) matriculaDao.findByDiscenteOtimizado(discente, TipoComponenteCurricular.getAll(), situacoes);
			}

			if (matriculas == null || matriculas.isEmpty()) {
				addMensagemErro("O discente selecionado não encontra-se matriculado em disciplinas");
				return null;
			}

		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("Um erro ocorreu durante a preparação para efetuar esta operação. Por favor,contacte o suporte através do \"Abrir Chamado\"");
			return null;
		}
		return telaSelecaoMatriculas();
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	/**
	 * Limpa os dados da classe.
	 */
	private void clear() {
		matriculas = new ArrayList<MatriculaComponente>();
		novaSituacao = new SituacaoMatricula();
		discente = new Discente();
	}
	
	/**
	 * Seleciona os Discentes que terão a situação de matricula alterada.<br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/alteracao_status_matricula/selecao_discentes.jsp</li>
	 * </ul>	
	 * @return
	 * @throws DAOException
	 */
	public String selecionarDiscentes() throws DAOException{
		TurmaDao dao = getDAO(TurmaDao.class);
		List<SituacaoMatricula> situacoes = CollectionUtils.toList(SituacaoMatricula.getSituacoesTodas());
		List<MatriculaComponente> matriculasTurma = (List<MatriculaComponente>) dao.findParticipantesTurma(turma.getId(), situacoes);
		boolean statusMatriculaRepetido = false;
		matriculasEscolhidas = new ArrayList<MatriculaComponente>();
		for (MatriculaComponente mc : matriculas){
			//verifica se algum aluno possui outra matricula com a mesma situacao
			for(MatriculaComponente mat : matriculasTurma){
				if(mc.getDiscente().getId() == mat.getDiscente().getId()&& mc.getNovaSituacaoMatricula().getId() == mat.getSituacaoMatricula().getId() && mc.getId() != mat.getId()
					&& !SituacaoMatricula.getSituacoesInativas().contains(mc.getNovaSituacaoMatricula())){
					mc.getNovaSituacaoMatricula().setId(mc.getSituacaoMatricula().getId());
					addMensagemErro("O Aluno "+ mc.getDiscente().getPessoa().getNome() + " já possui este status na turma "
						+ mc.getTurma().getCodigo() + " da disciplina "+ mc.getComponenteDescricao()+".");
					statusMatriculaRepetido = true;
				}
			}
				
			if (mc.getNovaSituacaoMatricula().getId() != mc.getSituacaoMatricula().getId()){
				mc.setNovaSituacaoMatricula(SituacaoMatricula.getSituacao(mc.getNovaSituacaoMatricula().getId())); 
				matriculasEscolhidas.add(mc);
			}
			
		}
		
		if (ValidatorUtil.isEmpty(matriculasEscolhidas)){
			if(!statusMatriculaRepetido)
				addMensagemErro("Altere a Situação de Matrícula de pelo menos um Discente.");
			return null;
		}			
		setOperacaoAtiva(SigaaListaComando.ALTERAR_STATUS_GERAL_TURMA.getId());
		return telaViewSelecionados();
	}

	/**
	 * Seleciona as matrículas que terão o situação alterada.<br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/alteracao_status_matricula/selecao_matriculas.jsp</li>
	 * </ul>	
	 * @return
	 * @throws DAOException
	 */
	public String selecionarMatriculas() throws DAOException {
		String[] linhas = getCurrentRequest().getParameterValues("matriculas");
		if ( linhas == null || linhas.length == 0 ) {
			addMensagemErro("Ao menos uma matrícula deve ser escolhida");
			return null;
		}
		matriculasEscolhidas = new ArrayList<MatriculaComponente>(0);
		GenericDAO dao = getGenericDAO();
		try {
			for (String linha : linhas) {
				MatriculaComponente mat = matriculas.get(new Integer(linha));
				matriculasEscolhidas.add(dao.findByPrimaryKey(mat.getId(), MatriculaComponente.class) );
			}					
		} finally {
			if (dao != null)
				dao.close();
		}
		checarAlertaCoRequisitos();
		setOperacaoAtiva(SigaaListaComando.ALTERAR_STATUS_MATRICULA.getId());
		return telaSelecaoStatus();
	}
	
	/**
	 * Para as operações de trancamento e cancelamento,
	 * verifica se os componentes selecionados violarão
	 * a restrição dos co-requisitos, caso existam, e emite
	 * um alerta para o usuário.
	 * @throws DAOException
	 */
	private void checarAlertaCoRequisitos() throws DAOException {
		if(trancamento) {
			ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
			List<ComponenteCurricular> list = new ArrayList<ComponenteCurricular>(0);
			try {
				List<Integer> idsComponentes = new ArrayList<Integer>();
				for(MatriculaComponente mc: matriculasEscolhidas){
					idsComponentes.add(mc.getComponente().getId());
				}
				list.addAll( dao.findComponentesReferenciamExpressaoCoRequisito(idsComponentes) );
			} finally {
				if (dao != null)
					dao.close();
			}
			if( !list.isEmpty() ){
				StringBuffer sb = new StringBuffer();
				sb.append("<ul>");
				for(ComponenteCurricular cc: list)
					sb.append("<li>" + cc.getDescricaoResumida() + "</li>");
				sb.append("</ul>");
				addMensagemWarning("Para que seja mantida a restrição de co-requisito, " +
						"o(s) seguinte(s) componente(s) também deve(m) ser selecionado(s): " + sb.toString());
			}
		}
	}

	/**
	 * Direciona o usuário para a tela de seleção de discentes.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	public String telaSelecaoDiscentes() {
		return forward("/graduacao/alteracao_status_matricula/selecao_discentes.jsp");
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
		return forward("/graduacao/alteracao_status_matricula/view_discentes_selecionados.jsp");
	}

	/**
	 * Direciona o usuário para a tela de seleção de status das matrículas.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	public String telaSelecaoStatus() {
		return forward("/graduacao/alteracao_status_matricula/selecao_status.jsp");
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
		return forward(JSP_SELECAO_MATRICULA);
	}

	/**
	 * Realizar o trancamento da disciplina selecionada<br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/alteracao_status_matricula/selecao_status.jsp</li>
	 * </ul>	
	 * @return
	 * @throws ArqException
	 */
	public String efetuarAlteracaoStatus() throws ArqException {
		String url = alterarStatus(SigaaListaComando.ALTERAR_STATUS_MATRICULA);
		if (url == null)
			return cancelar();
		return redirectJSF(url);
	}
	
	/**
	 * Realizar alteração dos status dos discentes selecionados<br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/alteracao_status_matricula/view_discentes_selecionados.jsp</li>
	 * </ul>	
	 * @return
	 * @throws ArqException
	 */
	public String efetuarAlteracaoStatusGeral() throws ArqException {
		alterarStatus(SigaaListaComando.ALTERAR_STATUS_GERAL_TURMA);
		return cancelar();
	}	
	
	/**
	 * Valida o status da matricula
	 * @throws NegocioException 
	 */
	private void validaNovaSituacao() throws NegocioException {

		for (MatriculaComponente mEscolhida : matriculasEscolhidas) {
			for (MatriculaComponente mc : matriculas)
				if(mc.getTurma() != null && mEscolhida.getTurma() !=null)
					if (mEscolhida.getTurma().getId() == mc.getTurma().getId() && novaSituacao.getId() == mc.getSituacaoMatricula().getId()  
						&& novaSituacao.getId() != mEscolhida.getSituacaoMatricula().getId()) {
						throw new NegocioException("O Aluno "+ discente.getPessoa().getNome() + " já possui este status na turma "
													+ mc.getTurma().getCodigo() + " da disciplina "+ mEscolhida.getComponenteDescricao()+".");
					}

		}
	}

	/**
	 * Alterar os Status dos discentes selecionados.
	 * @param comando
	 * @throws ArqException
	 * @return
	 */
	private String alterarStatus(Comando comando) throws ArqException{
		if(!isOperacaoAtiva(SigaaListaComando.ALTERAR_STATUS_MATRICULA.getId()) && !isOperacaoAtiva(SigaaListaComando.ALTERAR_STATUS_GERAL_TURMA.getId())){
			addMensagemErro("Atenção! Esta operação foi concluída anteriormente. Por favor, reinicie o processo.");
			return null;			
		}
			
		MovimentoOperacaoMatricula movMatricula = new MovimentoOperacaoMatricula();
		movMatricula.setMatriculas(matriculasEscolhidas);
		movMatricula.setNovaSituacao(novaSituacao);
		movMatricula.setCodMovimento(comando);

		try {
			if(comando == SigaaListaComando.ALTERAR_STATUS_MATRICULA && !SituacaoMatricula.getSituacoesInativas().contains(novaSituacao))
				validaNovaSituacao();
			execute(movMatricula, getCurrentRequest() );
			if (comando.equals(SigaaListaComando.ALTERAR_STATUS_MATRICULA)){
				addMessage("Alteração do status da(s) matrícula(s) de " + discente.getNome() +
						" foi realizada com sucesso!",
						TipoMensagemUFRN.INFORMATION);				
			} else {
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			}
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens( e.getListaMensagens() );
			if (novaSituacao == null)
				novaSituacao = new SituacaoMatricula();
			return JSP_SELECAO_MATRICULA;
		}
		return null;		
	}

	public List<MatriculaComponente> getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(List<MatriculaComponente> matriculas) {
		this.matriculas = matriculas;
	}

	/**
	 * Retorna a Lista de Situações de Matrícula<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/alteracao_status_matricula/selecao_status.jsp</li>
	 *   <li>/sigaa.war/graduacao/alteracao_status_matricula/selecao_discentes.jsp</li>
	 * </ul>	
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getSituacoes() throws DAOException {
		if( getAcessoMenu().isAlgumUsuarioStricto() ){
			List<SelectItem> situacoes = new ArrayList<SelectItem>();
			situacoes.add( new SelectItem( SituacaoMatricula.MATRICULADO.getId(), "Matriculado" ) );
			situacoes.add( new SelectItem( SituacaoMatricula.CANCELADO.getId(), "Cancelado" ) );
			situacoes.add( new SelectItem( SituacaoMatricula.EXCLUIDA.getId(), "Excluído" ) );
			situacoes.add( new SelectItem( SituacaoMatricula.REPROVADO.getId(), "Reprovado" ) );
			situacoes.add( new SelectItem( SituacaoMatricula.TRANCADO.getId(), "Trancado" ) );
			return situacoes;
		}
		GenericDAO dao = getGenericDAO();
		return toSelectItems(dao.findAllAtivos(SituacaoMatricula.class, "descricao"), "id", "descricao");
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public List<MatriculaComponente> getMatriculasEscolhidas() {
		return matriculasEscolhidas;
	}

	public void setMatriculasEscolhidas(List<MatriculaComponente> matriculasEscolhidas) {
		this.matriculasEscolhidas = matriculasEscolhidas;
	}

	public boolean isTrancamento() {
		return trancamento;
	}

	public void setTrancamento(boolean trancamento) {
		this.trancamento = trancamento;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}
}
