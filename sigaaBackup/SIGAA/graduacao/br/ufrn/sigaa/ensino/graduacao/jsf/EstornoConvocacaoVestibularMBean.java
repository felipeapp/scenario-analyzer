/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 16/12/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRange;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.graduacao.CadastramentoDiscenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivoDiscente;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/**
 * Controller responsável pelo estorno de uma convocação de discente aprovado no
 * vestibular.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
@Component("estornoConvocacaoVestibularMBean") 
@Scope("request")
public class EstornoConvocacaoVestibularMBean extends SigaaAbstractController<ConvocacaoProcessoSeletivoDiscente> {

	/** Processo seletivo a buscar. */
	private ProcessoSeletivoVestibular processoSeletivo;
	/** Matriz curricular a buscar. */
	private MatrizCurricular matrizCurricular;
	/** Matrizes ofertadas no processo seletivo.. */
	private Collection<SelectItem> matrizesCombo;
	
	/** Construtor padrão. */
	public EstornoConvocacaoVestibularMBean() {
	}
	
	/**
	 * Inicia o estorno de uma concocação/cancelamento de discente. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * @throws SegurancaException 
	 * @throws ArqException 
	 */
	public String iniciar() throws SegurancaException {
		obj = new ConvocacaoProcessoSeletivoDiscente();
		processoSeletivo = new ProcessoSeletivoVestibular();
		matrizCurricular = new  MatrizCurricular();
		matrizesCombo = null;
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS);
		return forward("/graduacao/cadastramento_discente/form_estorno.jsp");
	}
	
	/**
	 * Busca os discentes de acordo com os dados informados. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/cadastramento_discente/form_estorno.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String buscar() throws HibernateException, ArqException {
		ValidatorUtil.validateRequiredId(processoSeletivo.getId(), "Processo Seletivo Vestibular", erros);
		if (!isEmpty(processoSeletivo)) {
			if (processoSeletivo.isEntradaDoisPeriodos())
				validateRange(processoSeletivo.getPeriodoEntrada(), 1, 2, "Período de Entrada", erros);
		}
		if(!hasErrors()){
			CadastramentoDiscenteDao dao = getDAO(CadastramentoDiscenteDao.class);
			resultadosBusca = dao.findConvocacoes(matrizCurricular.getId(), processoSeletivo.getId(), processoSeletivo.getAnoEntrada(), processoSeletivo.getPeriodoEntrada(), StatusDiscente.EXCLUIDO);
			if(ValidatorUtil.isEmpty(resultadosBusca))
				addMensagemErro("Não foram encontrados discentes de acordo com os parâmetros de busca informados.");
		}
		prepareMovimento(SigaaListaComando.ESTORNAR_CONVOCACAO_VESTIBULAR);
		setOperacaoAtiva(SigaaListaComando.ESTORNAR_CONVOCACAO_VESTIBULAR.getId());
		return null;
	}
	
	/**
	 * Seleciona uma convocação para estornar. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/cadastramento_discente/form_estorno.jsp</li>
	 * </ul>
	 */
	public String selecionaConvocacao() throws DAOException {
		int id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, ConvocacaoProcessoSeletivoDiscente.class);
		if (obj == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			obj = new ConvocacaoProcessoSeletivoDiscente();
			return null;
		}
		if (obj.getDiscente().getStatus() != StatusDiscente.EXCLUIDO) {
			addMensagemErro("Esta operação é para estorno de discentes que foram excluídos.");
		}
		return forward("/graduacao/cadastramento_discente/confirma_estorno.jsp");
	}
	
	/**
	 * Estorna a convocação. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/cadastramento_discente/confirma_estorno.jsp</li>
	 * </ul>
	 */
	public String confirmaEstorno() {
		if (!checkOperacaoAtiva(SigaaListaComando.ESTORNAR_CONVOCACAO_VESTIBULAR.getId())) 
			return null;
		MovimentoCadastro mov = new MovimentoCadastro(obj);
		mov.setCodMovimento(SigaaListaComando.ESTORNAR_CONVOCACAO_VESTIBULAR);
		try {
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			return forward("/graduacao/cadastramento_discente/form_estorno.jsp");
		}
		return cancelar();
	}
	
	/**
	 * Carrega as matrizes curriculares de acordo com o processo seletivo selecionado. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/cadastramento_discente/form_cadastramento.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws DAOException
	 */
	public void changeProcessoSeletivo(ValueChangeEvent evt) throws DAOException {
		Integer idProcessoSeletivo = (Integer) evt.getNewValue();
		processoSeletivo = getGenericDAO().findByPrimaryKey(idProcessoSeletivo, ProcessoSeletivoVestibular.class);
		if(processoSeletivo == null) {
			processoSeletivo = new ProcessoSeletivoVestibular();
		}
		matrizesCombo = null;
	}
	
	/**
	 * Retorna uma coleção de itens com as matrizes curriculares manipuladas pelo caso de uso.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getMatrizesCombo() throws DAOException {
		if (matrizesCombo == null) {
			Collection<SelectItem> itens = new ArrayList<SelectItem>();
			CadastramentoDiscenteDao dao = getDAO(CadastramentoDiscenteDao.class);
			Collection<MatrizCurricular> matrizes = dao .findMatrizes(processoSeletivo);
			if (matrizes != null)
				for (MatrizCurricular matriz : matrizes) {
					itens.add(new SelectItem(matriz.getId(), matriz
							.getDescricao()));
				}
			matrizesCombo = itens;
		}
		return matrizesCombo;
	}

	public ProcessoSeletivoVestibular getProcessoSeletivo() {
		return processoSeletivo;
	}

	public void setProcessoSeletivo(ProcessoSeletivoVestibular processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	public MatrizCurricular getMatrizCurricular() {
		return matrizCurricular;
	}

	public void setMatrizCurricular(MatrizCurricular matrizCurricular) {
		this.matrizCurricular = matrizCurricular;
	}
	
}
