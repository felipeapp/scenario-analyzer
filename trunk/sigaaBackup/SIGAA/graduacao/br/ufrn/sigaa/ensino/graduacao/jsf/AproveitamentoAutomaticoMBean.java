/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Jun 1, 2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoIntegralizacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.AproveitamentoValidator;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoAproveitamentoAutomatico;

/**
 * MBeam responsável pelo caso de uso de aproveitamento automático de disciplinas
 * @author Victor Hugo
 *
 *
 */
@Component("aproveitamentoAutomatico")
@Scope("session")
public class AproveitamentoAutomaticoMBean extends SigaaAbstractController<Object> implements OperadorDiscente {

	/** JSP do formulário de discente Origem para a transação de aproveitamento automático. */
	public static final String FORM_DISCENTE_ORIGEM = "/graduacao/aproveitamento_automatico/discente_origem.jsp";
	/** JSP do formulário de destino para a transação de aproveitamento automático. */
	public static final String FORM_DISCENTE_DESTINO = "/graduacao/aproveitamento_automatico/discente_destino.jsp";
	/** JSP do formulário de listagem das informações a respeito do discente em processo de aproveitamento automático. */
	public static final String FORM_INFO_DISCENTES = "/graduacao/aproveitamento_automatico/info_discentes.jsp";
	/** JSP do formulário da listagem e confirmação das disciplinas aproveitadas automaticamente.*/  
	public static final String FORM_DISCIPLINAS_APROVEITADAS = "/graduacao/aproveitamento_automatico/disciplinas.jsp";

	/**
	 * discente origem do aproveitamento
	 */
	private DiscenteGraduacao discenteOrigem = new DiscenteGraduacao();

	/**
	 * possíveis discentes de origem do aproveitamento carregado a partir do discente de destino
	 */
	private Collection<DiscenteGraduacao> discentes;

	/**
	 * discente destino do aproveitamento
	 */
	private DiscenteGraduacao discenteDestino = new DiscenteGraduacao();

	/**
	 * componentes que serão aproveitadas
	 */
	private Collection<ComponenteCurricular> componentesAproveitados;

	/**
	 * matrículas a serem aproveitadas
	 */
	private Collection<MatriculaComponente> matriculas;

	/**
	 * Método responsável por iniciar o caso de uso de aproveitamento automático de disciplina.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/aproveitamento_automatico/discente_origem.jsp</li>
	 * <li>sigaa.war/graduacao/menus/aluno.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException{
		checkRole(SigaaPapeis.DAE);

		discenteDestino = new DiscenteGraduacao();
		discenteOrigem = new DiscenteGraduacao();
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.APROVEITAMENTO_AUTOMATICO);
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Exibe as informações do discente de destino do aproveitamento e 
	 * exibe os possíveis discentes que se pode realizar o aproveitamento.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/aproveitamento_automatico/discente_origem.jsp</li>
	 * <li>sigaa.war/graduacao/menus/aluno.jsp</li>
	 * <li>sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String selecionaDiscente() throws ArqException{

		DiscenteDao dao = getDAO(DiscenteDao.class);

		ListaMensagens erros = new ListaMensagens();

		if( erros != null && !erros.isEmpty() ){
			addMensagens(erros);
			return null;
		}

		try {
			discentes = new ArrayList<DiscenteGraduacao>(0);
			Collection<DiscenteAdapter> outrosDiscentes= dao.findByDadosPessoaisMesmoNivel(getDiscenteDestino());
			for (DiscenteAdapter d : outrosDiscentes) {
				this.discentes.add((DiscenteGraduacao) d);
			}
		} catch (DAOException e) {
			addMensagemErroPadrao();
			notifyError(e);
			e.printStackTrace();
			return null;
		}


		if( discentes != null || !discentes.isEmpty() ){
			for (Iterator<DiscenteGraduacao> iter = discentes.iterator(); iter.hasNext();) {
				if( iter.next().getId() == discenteDestino.getId() )
					iter.remove();
			}
		}

		if( discentes == null || discentes.isEmpty() ){
			addMensagemErro("Não há outros cursos que seja possível realizar o aproveitamento automático para o discente selecionado.");
			return null;
		}

		prepareMovimento(SigaaListaComando.APROVEITAMENTO_AUTOMATICO);

		// carregando dados do curso
		discenteDestino.getCurso().toString();

		return forward(FORM_DISCENTE_ORIGEM);
	}

	/**
	 * Seleciona o discente de origem do aproveitamento, realiza as validações necessárias e exibe informações dos 2 discentes.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/aproveitamento_automatico/discente_origem.jsp</li>
	 * </ul>
	 * @return
	 */
	public String selecionarDiscenteOrigem(){

		Integer id = getParameterInt("idDiscente");

		GenericDAO dao = getGenericDAO();

		try {
			discenteOrigem = dao.findByPrimaryKey(id, DiscenteGraduacao.class);
		} catch (DAOException e) {
			addMensagemErro( "Falha ao carregar discente de origem do aproveitamento. Contacte a administração." );
			notifyError(e);
			e.printStackTrace();
		}

		ListaMensagens erros = new ListaMensagens();
		AproveitamentoValidator.validaAproveitamentoAutomatico(discenteOrigem, discenteDestino, erros);

		if (!erros.isEmpty()) {
			addMensagens(erros);
			return null;
		}

		return forward(FORM_INFO_DISCENTES);
	}

	/**
	 * Exibe as disciplinas que poderão ser aproveitada dos discentes selecionados.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/aproveitamento_automatico/info_discentes.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String exibirComponentes() throws ArqException, NegocioException{

		componentesAproveitados = new ArrayList<ComponenteCurricular>(0);
		DiscenteDao ddao = getDAO(DiscenteDao.class);

		Collection<ComponenteCurricular> pagosOrigem =	ddao.findComponentesCurriculares(discenteOrigem, SituacaoMatricula.APROVADO,
				SituacaoMatricula.APROVEITADO_TRANSFERIDO, SituacaoMatricula.APROVEITADO_CUMPRIU);

		Collection<ComponenteCurricular> jaPagosPeloDestino =	ddao.findComponentesCurriculares(
				discenteDestino, SituacaoMatricula.APROVADO,
				SituacaoMatricula.APROVEITADO_TRANSFERIDO, SituacaoMatricula.APROVEITADO_CUMPRIU, SituacaoMatricula.APROVEITADO_DISPENSADO);
		TreeSet<Integer> idsPagosDestino = new TreeSet<Integer>();
		for (ComponenteCurricular comp: jaPagosPeloDestino) {
			idsPagosDestino.add(comp.getId());
		}

		for(ComponenteCurricular comp : pagosOrigem) {
			// só tenta aproveitar se não tiver entre os já pagos pelo Destino E
			// E não for atividade E não for equivalente
			if (!comp.isAtividade() &&  !idsPagosDestino.contains(comp.getId()) &&
					(comp.getEquivalencia() == null || !ExpressaoUtil.eval(comp.getEquivalencia(), idsPagosDestino))) {
				componentesAproveitados.add(comp);
			}
		}


		if( componentesAproveitados == null || componentesAproveitados.isEmpty() ){
			addMensagemErro("Não há nenhum componente que possa ser aproveitado" +
					" do discente selecionado.");
			return null;
		}

		MatriculaComponenteDao mcdao = getDAO(MatriculaComponenteDao.class);
		matriculas = mcdao.findByDiscenteOtimizado(discenteOrigem, componentesAproveitados,
				SituacaoMatricula.APROVADO, SituacaoMatricula.APROVEITADO_CUMPRIU, SituacaoMatricula.APROVEITADO_TRANSFERIDO);
		
		// Alterando para NULL o tipo de integralização das matrículas do currículo de origem, para passar pela análise de integralização.
		for (MatriculaComponente mc : matriculas) {
			mc.setTipoIntegralizacao(null);
		}
		
		prepareMovimento(SigaaListaComando.ANALISAR_MATRICULAS_APROVEITAMENTO_AUTOMATICO);
		MovimentoAproveitamentoAutomatico mov = new MovimentoAproveitamentoAutomatico();
		mov.setDiscenteDestino(getDiscenteDestino());
		mov.setMatriculas(matriculas);
		mov.setCodMovimento(SigaaListaComando.ANALISAR_MATRICULAS_APROVEITAMENTO_AUTOMATICO);
		
		
		matriculas = execute(mov);
		
		// agora remove as matrículas consideradas EXTRA-CURRICULARES
		Collection<MatriculaComponente> extras = new ArrayList<MatriculaComponente>(0);
		for (MatriculaComponente mat : matriculas) {
			if (mat.getTipoIntegralizacao().equals(TipoIntegralizacao.EXTRA_CURRICULAR))
				extras.add(mat);
		}
		matriculas.removeAll(extras);

		if( matriculas.isEmpty() ){
			addMensagemErro("Não há nenhum componente que possa ser aproveitado" +
					" do discente selecionado.");
			return null;
		}

		return forward(FORM_DISCIPLINAS_APROVEITADAS);
	}

	/**
	 * Método responsável por invocar o modelo e persiste o aproveitamento.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/aproveitamento_automatico/disciplinas.jsp</li>
	 * </ul>
	 * @throws DAOException
	 */
	public String gravarAproveitamento() throws DAOException{

		String[] selecionados = getParameterValues("selecao");
		if (selecionados == null) {
			addMensagemErro("É necessário selecionar no mínimo um componente.");
			return null;
		}

		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);
		Collection<MatriculaComponente> matriculasParaAproveitar = dao.findByIds( UFRNUtils.gerarStringIn(selecionados) );

		MovimentoAproveitamentoAutomatico mov = new MovimentoAproveitamentoAutomatico();
		mov.setCodMovimento(SigaaListaComando.APROVEITAMENTO_AUTOMATICO);
		mov.setMatriculas(matriculasParaAproveitar);
		mov.setDiscenteDestino(discenteDestino);
		mov.setDiscenteOrigem(discenteOrigem);

		try {
			execute(mov, getCurrentRequest());
			addMessage("Aproveitamento Automático Realizado com Sucesso!", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}

		return cancelar();
	}

	/**
	 * Método responsável por fazer o redirecionamento do formulário 
	 * de dados de Origem do discente em aproveitamento.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/aproveitamento_automatico/info_discentes.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaDiscenteOrigem(){
		return forward( FORM_DISCENTE_ORIGEM );
	}

	/**
	 * Método responsável por fazer o redirecionamento do formulário 
	 * de dados de Origem do discente em aproveitamento.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/aproveitamento_automatico/disciplinas.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaDiscenteDestino(){
		return forward( FORM_DISCENTE_DESTINO );
	}

	/**
	 * Método responsável por fazer o redirecionamento do formulário 
	 * de dados de Origem do discente em aproveitamento.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/aproveitamento_automatico/info_discentes.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaInfoDiscentes(){
		return forward( FORM_INFO_DISCENTES );
	}

	public DiscenteGraduacao getDiscenteDestino() {
		return discenteDestino;
	}

	public void setDiscenteDestino(DiscenteGraduacao discenteDestino) {
		this.discenteDestino = discenteDestino;
	}

	public DiscenteGraduacao getDiscenteOrigem() {
		return discenteOrigem;
	}

	public void setDiscenteOrigem(DiscenteGraduacao discenteOrigem) {
		this.discenteOrigem = discenteOrigem;
	}

	public Collection<DiscenteGraduacao> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Collection<DiscenteGraduacao> discentes) {
		this.discentes = discentes;
	}

	public Collection<ComponenteCurricular> getComponentesAproveitados() {
		return componentesAproveitados;
	}

	public void setComponentesAproveitados(Collection<ComponenteCurricular> componentesAproveitados) {
		this.componentesAproveitados = componentesAproveitados;
	}

	/**
	 * Insere o discente em retorno da busca de discente.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		discenteDestino = getGenericDAO().findByPrimaryKey(discente.getId(), DiscenteGraduacao.class);
	}

	public Collection<MatriculaComponente> getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(Collection<MatriculaComponente> matriculas) {
		this.matriculas = matriculas;
	}

}
