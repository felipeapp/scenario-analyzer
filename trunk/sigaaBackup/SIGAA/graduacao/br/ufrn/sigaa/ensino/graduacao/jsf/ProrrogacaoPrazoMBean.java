/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/02/2007
 *
 */

package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean para registro de prorrogação de prazo de conclusão de curso de um
 * discente de graduação
 * @author André
 */
@Component("prorrogacao") @Scope("session")
public class ProrrogacaoPrazoMBean extends SigaaAbstractController<MovimentacaoAluno> implements
		OperadorDiscente {

	/** Link para a jsp responsável pela prorrogação do prazo. */
	public static final String JSP_PRORROGACAO_PRAZO = "/graduacao/discente/prorrogacao_prazo.jsp";
	
	/** Link para a jsp responsável pela antecipação do prazo. */
	public static final String JSP_ANTECIPACAO_PRAZO = "/graduacao/discente/antecipacao_prazo.jsp";
	
	/** Link para a jsp responsável pela exibição das prorrogações de prazo do discente.  */
	public static final String JSP_PRORROGACOES_DISCENTE = "/graduacao/discente/prorrogacoes_discente.jsp";
	
	/** Coleção de selectItens para armazenar semestres.*/
	private Collection<SelectItem> semestres;

	/** Coleção de movimentação de aluno para armazenar as prorrogações de prazo de conclusão do aluno.*/
	private Collection<MovimentacaoAluno> prorrogacoes;

	/** Atributo responsável por armazenar o comando utilizado no processo.*/
	private Comando comando;

	public ProrrogacaoPrazoMBean() {
		obj = new MovimentacaoAluno();
	}

	/** Método responsável por inicializar o objeto do controller.
	 * <br>
	 * Método Chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> Não chamado por JSP. </li></ul>
	 */
	public void initObj(){
		obj = new MovimentacaoAluno();
		obj.getTipoMovimentacaoAluno().setId(TipoMovimentacaoAluno.PRORROGACAO_ADMINISTRATIVA);
	}

	public Collection<SelectItem> getSemestres() {
		return semestres;
	}

	public void setSemestres(Collection<SelectItem> semestres) {
		this.semestres = semestres;
	}

	public boolean isGraduacao() {
		return obj.getDiscente().isGraduacao();
	}

	/**
	 * Método responsável por realizar a prorrogação do prazo limite de conclusão de curso do discente.
	 * <br>
	 * Método Chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/discente/prorrogacao_prazo.jsp </li></ul>
	 * @return
	 * @throws ArqException
	 */
	public String prorrogarPrazo() throws ArqException {
		if (obj.getTipoMovimentacaoAluno().getId() == 0)
			addMensagemErro("Informe o tipo de prorrogação.");
		if( obj.getValorMovimentacao() == null || obj.getValorMovimentacao() <= 0)
			addMensagemErro("Informe o tempo total da prorrogação de prazo.");

		if( hasErrors() )
			return null;

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.PRORROGACAO_PRAZO);
		mov.setObjMovimentado(obj);
		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		}
		afterCadastrar();
		DiscenteAdapter discente;
		String prazo;
		if (obj.getDiscente().isTecnico()) {
			addMessage("Prorrogação de Prazo do(a) discente "+obj.getDiscente().getNome()+" registrado com sucesso!", TipoMensagemUFRN.INFORMATION);
		}
		if (obj.getDiscente().isStricto()) {
			discente = getGenericDAO().findByPrimaryKey(obj.getDiscente().getId(), DiscenteStricto.class);
			prazo = ((DiscenteStricto) discente).getAnoMesPrazoConclusao();
		} else {
			discente = getGenericDAO().findByPrimaryKey(obj.getDiscente().getId(), Discente.class);
			prazo = discente.getPrazoConclusao().toString();
		}
		addMessage("Prorrogação de Prazo do(a) discente "+obj.getDiscente().getNome()+" registrado com sucesso!" +
				" Seu novo prazo de conclusão é "+ prazo, TipoMensagemUFRN.INFORMATION);
		return cancelar();
	}
	
	/**
	 * Método responsável por realizar a antecipação do prazo limite de conclusão de curso para discente de graduação.
	 * <br>
	 * Método Chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/discente/antecipacao_prazo.jsp </li></ul>
	 * @return
	 * @throws ArqException
	 */
	public String anteciparPrazo() throws ArqException {
		prepareMovimento(SigaaListaComando.ANTECIPACAO_PRAZO);
		setOperacaoAtiva(SigaaListaComando.ANTECIPACAO_PRAZO.getId());
		
		if (obj.getTipoMovimentacaoAluno().getId() == 0)
			addMensagemErro("Informe o tipo de antecipação.");
		if( obj.getValorMovimentacao() == null || obj.getValorMovimentacao() < 0)
			addMensagemErro("Informe o tempo total da antecipação de prazo.");
		
		if( hasErrors() )
			return null;

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.ANTECIPACAO_PRAZO);
		mov.setObjMovimentado(obj);
		try {
			execute(mov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		}
	
		afterCadastrar();
		addMessage("Antecipação de Prazo do(a) discente "+obj.getDiscente().getNome()+" registrado com sucesso!" +
				" Seu novo prazo de conclusão é "+ obj.getDiscente().getDiscente().getPrazoConclusao(), TipoMensagemUFRN.INFORMATION);
		
		return cancelar();
	}

	/**
	 * Método responsável por popular a tela de prorrogação de prazo de conclusão conforme a tela de busca de discente.
	 * <br>
	 * Método Chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /SIGAA/app/sigaa.ear/sigaa.war/graduacao/busca_discente.jsp </li></ul>
	 */
	public String selecionaDiscente() throws ArqException {
		MovimentacaoAlunoDao dao = getDAO(MovimentacaoAlunoDao.class);

		DiscenteAdapter d = obj.getDiscente();
		obj.setDiscente(d);
		
	
		if( SigaaListaComando.PRORROGACAO_PRAZO.equals(comando) ){
			if (d.isGraduacao() && d.getPrazoConclusao() == null) {
				addMensagemErro("Não foi possível carregar o prazo de conclusão desse Discente");
				return null;
			}

			if (d.isTecnico()) {
				obj.getTipoMovimentacaoAluno().setId(TipoMovimentacaoAluno.PRORROGACAO_ADMINISTRATIVA);
			}
			
			if( d.isGraduacao() ){
				
				obj.getTipoMovimentacaoAluno().setId(TipoMovimentacaoAluno.PRORROGACAO_ADMINISTRATIVA);
				
				int ano = d.getPrazoConclusao() / 10;
				int sem = d.getPrazoConclusao() - (ano * 10);
				
				semestres = new ArrayList<SelectItem>();
				for (int i = 1; i <= 9; i++) {
					String anoSem = Formatador.getInstance().formatarAnoSemestre(
							DiscenteHelper.somaSemestres(ano, sem, i));
					String label = i + " Semestre" + ((i > 1) ? "s" : "") + " (" + anoSem + ")";
					semestres.add(new SelectItem(i, label));
				}
				
			}

			return forward(JSP_PRORROGACAO_PRAZO);
			
		}else if( SigaaListaComando.ANTECIPACAO_PRAZO.equals(comando) ){
			if (d.isGraduacao() && d.getPrazoConclusao() == null) {
				addMensagemErro("Não foi possível carregar o prazo de conclusão desse Discente");
				return null;
			}
			if( d.isGraduacao() ){
				
				initObj();
				obj.getTipoMovimentacaoAluno().setId(TipoMovimentacaoAluno.ANTECIPACAO_ADMINISTRATIVA);
				
				int anoAtual = CalendarUtils.getAnoAtual();
				int semAtual = getPeriodoAtual();
		
				int anoPrazo = d.getPrazoConclusao() / 10;
				int semPrazo = d.getPrazoConclusao() - (anoPrazo * 10);
				
				int qtdSem = (anoPrazo - anoAtual)*2 + semPrazo - (semAtual);
				
				semestres = new ArrayList<SelectItem>();
				for (int i = qtdSem - 1; i >= 0; i--) {
					String anoSem = Formatador.getInstance().formatarAnoSemestre(
							DiscenteHelper.somaSemestres(anoAtual, semAtual, i));
					String label = qtdSem-i + " Semestre" + ((qtdSem-i > 1) ? "s" : "") + " (" + anoSem + ")";
					semestres.add(new SelectItem(qtdSem-i, label));
				}
				
			}

			obj.setDiscente(d.getDiscente());
			return forward(JSP_ANTECIPACAO_PRAZO);	
			
		} else if( SigaaListaComando.CANCELAR_PRORROGACAO_PRAZO.equals(comando) ){
			prepareMovimento(SigaaListaComando.CANCELAR_PRORROGACAO_PRAZO);

			prorrogacoes = dao.findProrrogacoesByDiscente(d);

			if( prorrogacoes == null || prorrogacoes.isEmpty() ){
				addMensagemErro("O aluno selecionado não possui nenhuma prorrogação de prazo.");
				return null;
			}

			return forward(JSP_PRORROGACOES_DISCENTE);
		}else{
			addMensagemErroPadrao();
			return null;
		}

	}

	/** Método responsável por setar o discente após a busca do discente.*/
	public void setDiscente(DiscenteAdapter discente) {
		obj.setDiscente(discente);
	}

	/**
	 * Redireciona o usuário para a busca de discente para iniciar a operação de prorrogar o prazo de conclusão.
	 * <br>
	 * Método Chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/menus/programa.jsp </li>
	 *  <li>/sigaa.war/stricto/menus/discente.jsp</li>
	 *  <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String buscarDiscente() throws ArqException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.SECRETARIA_POS , SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.GESTOR_TECNICO);
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.PRORROGACAO_PRAZO);
		prepareMovimento(SigaaListaComando.PRORROGACAO_PRAZO);
		comando = SigaaListaComando.PRORROGACAO_PRAZO;
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Redireciona o usuário para a busca de discente para iniciar a operação de antecipar o prazo de conclusão.
	 * <br>
	 * Método Chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/menus/programa.jsp </li></ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String buscarDiscenteAnteciparPrazo() throws SegurancaException {
		checkRole(SigaaPapeis.DAE);
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ANTECIPACAO_PRAZO);
		comando = SigaaListaComando.ANTECIPACAO_PRAZO;
		return buscaDiscenteMBean.popular();
	}
	
	/**
	 * Vai para a busca de discente para iniciar a operação de cancelar prorrogação de prazo.
	 * <br>
	 * Método Chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/graduacao/discente/prorrogacoes_discente.jsp </li>
	 * 	<li>/sigaa.war/graduacao/menus/programa.jsp </li>
	 *  <li>/sigaa.war/stricto/menus/discente.jsp</li>
	 *  <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String buscarDiscenteCancelarProrrogacao() throws SegurancaException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.SECRETARIA_POS , SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO);
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.CANCELAR_PRORROGACAO_PRAZO);
		comando = SigaaListaComando.CANCELAR_PRORROGACAO_PRAZO;
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Coleção com o retorno dos tipos de movimentação de aluno.
	 * @return
	 */
	public Collection<SelectItem> getTipos() {
		Collection<TipoMovimentacaoAluno> tipos = new ArrayList<TipoMovimentacaoAluno>(0);
		if( SigaaListaComando.ANTECIPACAO_PRAZO.equals(comando) ){
			tipos.add(new TipoMovimentacaoAluno(TipoMovimentacaoAluno.ANTECIPACAO_ADMINISTRATIVA, "ADMINISTRATIVA"));
			tipos.add(new TipoMovimentacaoAluno(TipoMovimentacaoAluno.ANTECIPACAO_JUDICIAL, "JUDICIAL"));
		}else{
			tipos.add(new TipoMovimentacaoAluno(TipoMovimentacaoAluno.PRORROGACAO_ADMINISTRATIVA, "ADMINISTRATIVA"));
			tipos.add(new TipoMovimentacaoAluno(TipoMovimentacaoAluno.PRORROGACAO_JUDICIAL, "JUDICIAL"));
		}
		return toSelectItems(tipos, "id", "descricao");

	}

	/**
	 * Chama o processador para cancelar a prorrogação de prazo selecionada
	 * <br>
	 * Método Chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>/sigaa.war/graduacao/discente/prorrogacoes_discente.jsp </li></ul>
	 * @return
	 * @throws DAOException
	 */
	public String cancelarProrrogacao() throws DAOException{
		Integer id = getParameterInt("id");

		if( id == null || id == 0 ){
			addMensagemErro("Nenhuma prorrogação de prazo foi selecionada para que seja cancelada.");
			return null;
		}

		GenericDAO dao = getGenericDAO();
		MovimentacaoAluno prorrogacao = dao.findByPrimaryKey(id, MovimentacaoAluno.class);

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.CANCELAR_PRORROGACAO_PRAZO);
		mov.setObjMovimentado(prorrogacao);

		try {
			execute(mov, getCurrentRequest());
			addMessage("Cancelamento de prorrogação de prazo realizado com sucesso.", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens(e.getListaMensagens());
			return null;
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		}

		return cancelar();
	}

	public Comando getComando() {
		return comando;
	}

	public void setComando(Comando comando) {
		this.comando = comando;
	}

	public Collection<MovimentacaoAluno> getProrrogacoes() {
		return prorrogacoes;
	}

	public void setProrrogacoes(Collection<MovimentacaoAluno> prorrogacoes) {
		this.prorrogacoes = prorrogacoes;
	}

}
