/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 17/04/2012
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dao.TurmaGraduacaoDao;
import br.ufrn.sigaa.ensino.negocio.dominio.TurmaMov;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;

/**
 * Controller responsável por converter uma turma regular em uma turma de ensino
 * individual. Este caso ocorre quando o departamento de controle acadêmico
 * deseja que turmas com poucos alunos, até cinco por exemplo, sejam convertidas
 * em ensino individual.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
@Component("converterTurmaRegularIndividualMBean")
@Scope("request")
public class ConverterTurmaRegularIndividualMBean extends SigaaAbstractController<Turma> {

	/** Quantidade máxima de discentes matriculados na turma de ensino individual. */
	private int qtdMatriculado;

	/** Inicia o caso de uso para converter turmas regulares em turmas de ensino individual.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/programa.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 */
	public String iniciarConversaoTurma() throws SegurancaException, DAOException {
		obj = new Turma();
		obj.setAno(getCalendarioVigente().getAno());
		obj.setPeriodo(getCalendarioVigente().getPeriodo());
		qtdMatriculado = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.QTD_MAX_DISCENTES_TURMA_ENSINO_INDIVIDUAL);
		return listarTurmasComPoucosDiscentes();
	}
	
	/** Lista as turmas de um ano-período que possuem poucos discentes matriculados.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/turma/conversao/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 */
	public String listarTurmasComPoucosDiscentes() throws SegurancaException, DAOException {
		checkChangeRole();
		// valida os dados da busca
		validateRequired(obj.getAno(), "Ano", erros);
		validateRequired(obj.getPeriodo(), "Período", erros);
		validateMaxValue(qtdMatriculado, ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.QTD_MAX_DISCENTES_TURMA_ENSINO_INDIVIDUAL), "Quantidade de Discentes Matriculados", erros);
		// valida período para conversão.
		if (getCalendarioVigente().getFimProcessamentoReMatricula() == null)
			addMensagemErro("O período de processamento da rematrícula não está definido no calendário acadêmico.");
		else if (!getCalendarioVigente().getFimProcessamentoReMatricula().before(new Date())) 
			addMensagemErro("A conversão de turmas só é permitida após o processamento das rematrículas.");
		if (hasErrors())
			return null;
		TurmaGraduacaoDao dao = getDAO(TurmaGraduacaoDao.class);
		resultadosBusca = dao.findByQuantidadeMatriculado(obj.getAno(), obj.getPeriodo(), qtdMatriculado, Turma.REGULAR);
		if (isEmpty(resultadosBusca)) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		setOperacaoAtiva(SigaaListaComando.CONVERTER_TURMA_REGULAR_ENSINO_INDIVIDUAL.getId());
		return forward("/graduacao/turma/conversao/lista.jsp");
	}
	
	/** Seleciona uma turma para converter em Ensino Individual.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/turma/conversao/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String selecionarTurma() throws SegurancaException, DAOException {
		checkChangeRole();
		if (!isOperacaoAtiva(SigaaListaComando.CONVERTER_TURMA_REGULAR_ENSINO_INDIVIDUAL.getId()))
			return null;
		populateObj(true);
		if (obj == null) {
			obj = new Turma();
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;
		}
		return forward("/graduacao/turma/conversao/confirma.jsp");
	}
	
	/** Confirma a conversão da turma regular em turma de ensino individual.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/turma/conversao/confirma.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String confirmar() throws SegurancaException, ArqException, NegocioException {
		checkChangeRole();
		if (!isOperacaoAtiva(SigaaListaComando.CONVERTER_TURMA_REGULAR_ENSINO_INDIVIDUAL.getId()))
			return null;
		TurmaMov mov = new TurmaMov();
		mov.setTurma(obj);
		mov.setCodMovimento(SigaaListaComando.CONVERTER_TURMA_REGULAR_ENSINO_INDIVIDUAL);
		try {
			prepareMovimento(SigaaListaComando.CONVERTER_TURMA_REGULAR_ENSINO_INDIVIDUAL);
			execute(mov);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		} catch (Exception e) {
			tratamentoErroPadrao(e);
			notifyError(e);
			return null;
		}
		return listarTurmasComPoucosDiscentes();
	}

	/** Cancela a conversão de turma regular em ensino individual.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/turma/conversao/lista.jsp</li>
	 * <li>/graduacao/turma/conversao/confirma.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractController#cancelar()
	 */
	@Override
	public String cancelar() {
		removeOperacaoAtiva();
		return super.cancelar();
	}
	
	/** Valida as permissões para alterar a turma.
	 * <br/> Método não invocado por JSP's
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE);
	}

	public int getQtdMatriculado() {
		return qtdMatriculado;
	}

	public void setQtdMatriculado(int qtdMatriculado) {
		this.qtdMatriculado = qtdMatriculado;
	}
}
