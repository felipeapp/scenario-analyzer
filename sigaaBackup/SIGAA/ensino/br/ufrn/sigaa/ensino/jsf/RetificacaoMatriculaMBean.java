/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 16/10/2007
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.DefaultExtensionPointFactory;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.RetificacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacao;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacaoFactory;
import br.ufrn.sigaa.ensino.stricto.negocio.ConceitoNotaHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;

/**
 * MBean para realizar retificação de matriculas de componente curriculares
 *
 * @author Andre M Dantas
 *
 */
@SuppressWarnings("serial")
@Component("retificacaoMatricula")
@Scope("session")
public class RetificacaoMatriculaMBean extends SigaaAbstractController<MatriculaComponente> implements OperadorDiscente {

	/** Página de matrículas do discente. */
	private static final String JSP_MATRICULAS_DISCENTE = "/ensino/retificacao_matricula/matriculas_discente.jsp";

	/** Página de matrícula escolhida. */
	private static final String JSP_MATRICULA_ESCOLHIDA = "/ensino/retificacao_matricula/matricula_escolhida.jsp";

	/** Página de confirmação. */
	private static final String JSP_CONFIRMACAO = "/ensino/retificacao_matricula/confirmacao.jsp";

	/** Armazena o discente escolhido para a retificação. */
	private DiscenteAdapter discenteEscolhido;

	/** Guarda a matrícula original. */
	private MatriculaComponente matriculaOriginal;

	/** Armazena a matrícula modificada. */
	private MatriculaComponente matriculaModificada;

	/** Armazena as matrículas concluídas pelo discente. */
	private Collection<MatriculaComponente> matriculasConcluidas;

	/** Armazena o histórico de retificações de uma matrícula. */
	private Collection<RetificacaoMatricula> historicoRetificacoes;

	/** Armazena o {@link MetodoAvaliacao}. */
	private int metodoAvaliacao;

	public RetificacaoMatriculaMBean() {
	}

	/**
	 * Busca o aluno preparando a retificação de matrícula.
	 * <br /><br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>SIGAA/app/sigaa.ear/sigaa.war/ensino/retificacao_matricula/matriculas_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String buscarAluno() throws ArqException {
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.RETIFICACAO_MATRICULA);
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Verifica se o usuário pode realizar a operação e redireciona o fluxo para a busca de aluno.
	 * <br /><br />
	 * Chamado pelas JSPs:
	 * <ul>
	 * <li>SIGAA/app/sigaa.ear/sigaa.war/ensino/retificacao_matricula/matricula_escolhida.jsp</li>
	 * <li>SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/aluno.jsp</li>
	 * <li>SIGAA/app/sigaa.ear/sigaa.war/lato/menu_coordenador.jsp</li>
	 * <li>SIGAA/app/sigaa.ear/sigaa.war/stricto/menus/discente.jsp</li>
	 * <li>SIGAA/app/sigaa.ear/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * <li>SIGAA/app/sigaa.ear/sigaa.war/ensino/lato_sensu/menu/discente.jsp</li>
	 * <li>SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/menu/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE, 
				SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO,
				SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO,
				SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS,
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR, SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO);
		return buscarAluno();
	}

	/**
	 * Seleciona a matrícula e direciona o fluxo para a tela com a matrícula escolhida.
	 * <br /><br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>SIGAA/app/sigaa.ear/sigaa.war/ensino/retificacao_matricula/matriculas_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String selecionarMatricula() throws ArqException {
		Integer id = getParameterInt("id");
		try {
			
			if (discenteEscolhido == null) {
				addMensagemErro("Discente não selecionado. Por favor, reinicie o processo.");
				return iniciar();
			}			
			
			GenericDAO dao = getGenericDAO();

			matriculaOriginal = dao.findByPrimaryKey(id, MatriculaComponente.class);
			matriculaOriginal.setDetalhesComponente( dao.refresh( matriculaOriginal.getDetalhesComponente() )  );
			matriculaOriginal.getComponente().getDetalhes().getChTotal();
			if( matriculaOriginal.getComponente().getUnidade().getGestoraAcademica() != null )
				matriculaOriginal.getComponente().getUnidade().getGestoraAcademica().getNome();
			matriculaOriginal.getComponente().getDetalhes().getId();
			matriculaOriginal.getComponenteDescricaoResumida();
			matriculaOriginal.getComponenteDescricao();
			if( matriculaOriginal.getTurma() != null )
				matriculaOriginal.getTurma().getDocentesTurmas().iterator();
			// copiar só  o q vai poder ser alterado na jsp
			matriculaModificada = new MatriculaComponente();
			matriculaModificada.setValoresRetificacao(matriculaOriginal);

			metodoAvaliacao = getParametros().getMetodoAvaliacao();

			// carrega histórico
			historicoRetificacoes = getGenericDAO().findByExactField(RetificacaoMatricula.class,
					"matriculaAlterada.id", matriculaOriginal.getId(), "desc", "data");
			
			prepareMovimento(SigaaListaComando.RETIFICAR_MATRICULA);
			
		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("Não foi possível carregar a matrícula do componente escolhido");
			return null;
		}

		return telaMatriculaEscolhida();
	}

	/**
	 * Direciona o usuário para a tela de matrícula escolhida.
	 * <br /><br />
	 * Não chamado por JSPs.
	 * @return
	 */
	public String telaMatriculaEscolhida() {
		return forward(JSP_MATRICULA_ESCOLHIDA);
	}

	/**
	 * Submete os dados da retificação e direciona o usuário para a página de confirmação.
	 * <br /><br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>SIGAA/app/sigaa.ear/sigaa.war/ensino/retificacao_matricula/matricula_escolhida.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String confirmar() throws DAOException {

		if( isConceito() ){
			String param = getParameter("conceitoAtualizado").trim();
			Double c = Double.valueOf(param);
			matriculaModificada.setConceito( c );
		}

		if (!isSubmissaoValida())
			return null;

		matriculaModificada.setDetalhesComponente( getGenericDAO().findByPrimaryKey( matriculaOriginal.getComponente().getDetalhes().getId() , ComponenteDetalhes.class) );
		
		ParametrosGestoraAcademica params = ParametrosGestoraAcademicaHelper.getParametros(getDiscenteEscolhido());
		
		if (params == null) 
			addMensagemErro("O sistema não foi capaz de localizar o parâmetro da gestora acadêmica para o discente: " + getDiscenteEscolhido() + ". Entre em contato com o suporte.");
		
		if (hasErrors())
			return null;
		
		if (matriculaOriginal.getTurma() != null){
			//Caso for turma de EAD, recarrega o Polo e Curso para evitar LazyInitializationException...
			if (matriculaOriginal.getTurma().getDisciplina().isGraduacao()){
				if (matriculaOriginal.getTurma().getIdPolo() != null)
					matriculaOriginal.getTurma().setPolo( getGenericDAO().findByPrimaryKey(matriculaOriginal.getTurma().getIdPolo(), Polo.class) );
				if (matriculaOriginal.getTurma().isEad()){
					Collection<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();
					matriculaOriginal.getDiscente().setCurso( getGenericDAO().refresh( matriculaOriginal.getDiscente().getCurso() ));
					matriculas.add(matriculaOriginal);
					matriculaOriginal.getTurma().setMatriculasDisciplina(matriculas);					
				}
			}					
		}
		
		EstrategiaConsolidacao estrategia = null;
		EstrategiaConsolidacaoFactory factory = (EstrategiaConsolidacaoFactory) DefaultExtensionPointFactory.getImplementation(ParametrosGerais.IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY);
		
		// Tenta pegar a estrategia pelo curso, se for aluno especial entra no else e pega pela turma. Aluno especial não se matricula em atividade, somente em turma, então nao dará erro.
		if (discenteEscolhido.getCurso() != null ) {
			Curso curso = getGenericDAO().findByPrimaryKey(discenteEscolhido.getCurso().getId(), Curso.class);
			estrategia = factory.getEstrategia(discenteEscolhido, params);
		} else {
			estrategia = factory.getEstrategia(matriculaOriginal.getTurma(), params);
		}
		
		if( !matriculaOriginal.isAproveitadoDispensado() && !matriculaOriginal.getSituacaoMatricula().equals(SituacaoMatricula.EXCLUIDA) && !matriculaOriginal.getSituacaoMatricula().equals(SituacaoMatricula.CANCELADO) ){
			matriculaModificada.setEstrategia(estrategia);
			matriculaModificada.setMetodoAvaliacao(getParametros().getMetodoAvaliacao());
			matriculaModificada.consolidar();
		}
		
		SituacaoMatricula modificada = matriculaModificada.getSituacaoMatricula();
		matriculaModificada.setSituacaoMatricula(getGenericDAO().findByPrimaryKey(modificada.getId(), SituacaoMatricula.class));
		
		addMessage("ATENÇÃO! Confira os dados antes de confirmar a operação.", TipoMensagemUFRN.WARNING);
		return forward(JSP_CONFIRMACAO);
	}

	/**
	 * Verifica se os dados da matrícula estão válidos e adiciona as respectivas mensagens de erro.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	private boolean isSubmissaoValida() throws DAOException {

		if (matriculaModificada.getNumeroFaltas() == null || matriculaModificada.getNumeroFaltas() < 0)
			addMensagemErro("Informe o número de faltas válido ");

		if (isAptidao() && matriculaModificada.getApto() == null) {
			addMensagemErro("Informe a aptidão");
		} else if ( isConceito() ) {
			//ConceitoNota conceito = getGenericDAO().findByPrimaryKey(ConceitoNota.A, ConceitoNota.class);
			if(matriculaModificada.getConceito() == null || matriculaModificada.getConceito() <= 0 || matriculaModificada.getConceito() > ConceitoNotaHelper.getValorConceito("A"))
				addMensagemErro("Informe o conceito.");
			else if( matriculaModificada.getConceito().equals(matriculaOriginal.getConceito()) ){
					addMensagemErro("Selecione um conceito diferente do original.");
			}

		} else if (isNota() && (matriculaModificada.getMediaFinal() == null
				|| matriculaModificada.getMediaFinal() < 0 || matriculaModificada.getMediaFinal() > 10)) {
			addMensagemErro("Informe uma média final válida");
		}

		return !hasErrors();
	}

	/**
	 * Realiza o processo de retificação da matrícula.
	 * <br /><br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>SIGAA/app/sigaa.ear/sigaa.war/ensino/retificacao_matricula/confirmacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String processarRetificacao() throws ArqException {
		erros = new ListaMensagens();

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.RETIFICAR_MATRICULA);
		mov.setObjMovimentado(matriculaModificada);
		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		addMessage("Retificação de registro de matrícula do discente " + discenteEscolhido.getMatriculaNome()
				+ " no componente " + matriculaModificada.getComponente().getDescricaoResumida()
				+ " foi realizado com sucesso", TipoMensagemUFRN.INFORMATION);

		prepareMovimento(SigaaListaComando.RETIFICAR_MATRICULA);
		return selecionaDiscente();
	}

	/**
	 * Seleciona o discente e busca suas matrículas concluídas.
	 * <br /><br />
	 * Não chamado por JSPs.
	 * @return
	 */
	public String selecionaDiscente() {
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);
		try {
			
			Collection<SituacaoMatricula> situacaoes = new ArrayList<SituacaoMatricula>();
			situacaoes.add(SituacaoMatricula.APROVADO);
			situacaoes.add(SituacaoMatricula.REPROVADO_FALTA);
			situacaoes.add(SituacaoMatricula.REPROVADO_MEDIA_FALTA);
			situacaoes.add(SituacaoMatricula.REPROVADO);
			situacaoes.add(SituacaoMatricula.APROVEITADO_CUMPRIU);
			situacaoes.add(SituacaoMatricula.APROVEITADO_DISPENSADO);
			situacaoes.add(SituacaoMatricula.APROVEITADO_TRANSFERIDO);
			
			Collection<TipoComponenteCurricular> tipos = new ArrayList<TipoComponenteCurricular>();
			tipos.add(new TipoComponenteCurricular(TipoComponenteCurricular.ATIVIDADE));			
			tipos.add(new TipoComponenteCurricular(TipoComponenteCurricular.DISCIPLINA));
			tipos.add(new TipoComponenteCurricular(TipoComponenteCurricular.MODULO));
			
			matriculasConcluidas = dao.findByDiscenteOtimizado(discenteEscolhido, tipos, situacaoes);
			if (matriculasConcluidas == null || matriculasConcluidas.isEmpty()) {
				addMensagemErro("O discente escolhido não possui matrículas consolidadas");
				return null;
			}
			metodoAvaliacao = getParametros().getMetodoAvaliacao();
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("Não foi possível carregar as matrículas do aluno escolhido");
			return null;
		}

		return forward(JSP_MATRICULAS_DISCENTE);
	}

	/**
	 * Retorna as {@link SituacaoMatricula} para retificações de aproveitamentos.
	 * <br /><br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>SIGAA/app/sigaa.ear/sigaa.war/ensino/retificacao_matricula/matricula_escolhida.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getSituacoesRetificacaoAproveitamentos(){
		try {
			Collection<SituacaoMatricula> situacoes = SituacaoMatricula.getSituacoesAproveitadas();
			situacoes.add(SituacaoMatricula.CANCELADO);
			situacoes.add(SituacaoMatricula.EXCLUIDA);

			return toSelectItems(situacoes, "id", "descricao");
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<SelectItem>();
		}
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discenteEscolhido = discente;
	}

	public DiscenteAdapter getDiscenteEscolhido() {
		return discenteEscolhido;
	}

	public void setDiscenteEscolhido(DiscenteAdapter discenteEscolhido) {
		this.discenteEscolhido = discenteEscolhido;
	}

	public MatriculaComponente getMatriculaModificada() {
		return matriculaModificada;
	}

	public void setMatriculaModificada(MatriculaComponente matriculaModificada) {
		this.matriculaModificada = matriculaModificada;
	}

	public MatriculaComponente getMatriculaOriginal() {
		return matriculaOriginal;
	}

	public void setMatriculaOriginal(MatriculaComponente matriculaOriginal) {
		this.matriculaOriginal = matriculaOriginal;
	}


	public int getMetodoAvaliacao() {
		return metodoAvaliacao;
	}

	public void setMetodoAvaliacao(int metodoAvaliacao) {
		this.metodoAvaliacao = metodoAvaliacao;
	}

	/**
	 * Direciona o usuário para a seleção de matrículas.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>sigaa.war/ensino/retificacao_matricula/matricula_escolhida.jsp</li>
	 * </ul>
	 * @return
	 */
	public String voltarSelecaoMatricula() {
		return forward(JSP_MATRICULAS_DISCENTE);
	}

	/**
	 * Direciona o usuário para a tela de matrícula escolhida.
	 * <br />
	 * Não chamado por JSPs.
	 * @return
	 */
	public String voltarAlteracaoMatricula() {
		return forward(JSP_MATRICULA_ESCOLHIDA);
	}

	public boolean isConceito() {
		return metodoAvaliacao == MetodoAvaliacao.CONCEITO;
	}

	public boolean isNota() {
		return metodoAvaliacao == MetodoAvaliacao.NOTA;
	}

	public boolean isAptidao() {
		return metodoAvaliacao == MetodoAvaliacao.COMPETENCIA;
	}

	public void setMatriculasConcluidas(Collection<MatriculaComponente> matriculasConcluidas) {
		this.matriculasConcluidas = matriculasConcluidas;
	}

	public Collection<MatriculaComponente> getMatriculasConcluidas() {
		return matriculasConcluidas;
	}

	public Collection<RetificacaoMatricula> getHistoricoRetificacoes() {
		return historicoRetificacoes;
	}

	public void setHistoricoRetificacoes(Collection<RetificacaoMatricula> historicoRetificacoes) {
		this.historicoRetificacoes = historicoRetificacoes;
	}

	/**
	 * Retorna os parâmetros do discente selecionado.
	 * <br /><br />
	 * Não chamado por JSPs.
	 * @return
	 * @throws DAOException
	 */
	public ParametrosGestoraAcademica getParametros() throws DAOException{
		return ParametrosGestoraAcademicaHelper.getParametros(discenteEscolhido);
	}
}
