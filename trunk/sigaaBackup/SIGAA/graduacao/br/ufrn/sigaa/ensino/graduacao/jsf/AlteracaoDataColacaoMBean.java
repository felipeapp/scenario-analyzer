/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 05/12/2007
 * 
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;

import java.util.Date;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean usado para as opera��es de altera��o das datas de cola��o de grau.
 * 
 * @author leonardo
 *
 */

public class AlteracaoDataColacaoMBean extends SigaaAbstractController<MovimentacaoAluno> implements OperadorDiscente {

	private final int ANO_CONCLUSAO_MINIMO = 1900;
	
	public AlteracaoDataColacaoMBean(){
		obj = new MovimentacaoAluno();
	}
	
	/**
	 * Redirecionar para DiscenteGraduacaoMBean para a busca de discentes de gradua��o
	 *
	 * @return
	 * 
	 * JSP: SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/administracao.jsp
	 */
	public String buscarDiscente() throws SegurancaException {
		checkRole(new int[]{SigaaPapeis.ADMINISTRADOR_DAE});
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ALTERAR_DATA_COLACAO);
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Seleciona um discente para alterar a data de cola��o do mesmo.
	 * <br>N�o Chamado por JSP.
	 * @return
	 */
	public String selecionaDiscente() throws ArqException {
		try {
			if(hasErrors())	return null;
			prepareMovimento(SigaaListaComando.ALTERAR_DATA_COLACAO);
			return forward("/graduacao/alteracao_data_colacao/form.jsp");
		} catch(Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErroPadrao();
			return null;
		}
	}

	/**
	 * Muda a movimenta��o de conclus�o de programa
	 *
	 * @return
	 */
	
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		try {
			MovimentacaoAlunoDao dao = getDAO(MovimentacaoAlunoDao.class);
			MovimentacaoAluno movimentacaoConclusao = dao.findConclusaoByDiscente(discente.getId());
			if(movimentacaoConclusao != null){
				obj = movimentacaoConclusao;
				obj.setDiscente( dao.findByPrimaryKey(discente.getId(), Discente.class) );
			} else {
				addMensagemErro("O aluno selecionado n�o possui movimenta��o de conclus�o de programa.");
			}
			setConfirmButton("Confirmar Altera��o");
		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("N�o foi poss�vel carregar o discente escolhido");
		}		
	}

	
	/**
	 * Altera a data da Cola��o
	 *
	 * @return
	 * 
	 * JSP: SIGAA/app/sigaa.ear/sigaa.war/graduacao/alteracao_data_colacao/form.jsp
	 */
	public String alterarData(){
		try {
			ValidatorUtil.validateRequired(obj.getAnoReferencia(), "Ano", erros);
			ValidatorUtil.validateRange(obj.getAnoReferencia(), ANO_CONCLUSAO_MINIMO, CalendarUtils.getAnoAtual() + 1, "Ano", erros);
			ValidatorUtil.validateRequired(obj.getPeriodoReferencia(), "Per�odo", erros);
			ValidatorUtil.validateRange(obj.getPeriodoReferencia(), 1, 2, "Per�odo", erros);
			ValidatorUtil.validateRequired(obj.getDiscente().getDataColacaoGrau(), "Data de Cola��o", erros);
			// valida a data de cola��o pelo calend�rio acd�mico
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendarioExato(obj.getAnoReferencia(), obj.getPeriodoReferencia(), new Unidade(getUnidadeGestora()), NivelEnsino.GRADUACAO, null, null, null, null);
			if (cal != null) {
				validateMinValue(obj.getDiscente().getDataColacaoGrau(), cal.getFimPeriodoLetivo(), "Data de cola��o", erros);
			} else {
				// n�o h� calend�rio cadastrado. calcula o calend�rio como sendo o �ltimo dia do semestre expecificado
				// Julho = 7 / Janeiro = 1
				int mes = obj.getPeriodoReferencia() == 2 ? 1 : 7;
				int ano = obj.getPeriodoReferencia() == 2 ? obj.getAnoReferencia() + 1 : obj.getAnoReferencia();
				Date data = CalendarUtils.adicionaDias(CalendarUtils.getInstance(1, mes, ano).getTime(), -1);
				validateMinValue(obj.getDiscente().getDataColacaoGrau(), data, "Data de cola��o", erros);
			}
			if(hasErrors()){
				return null;
			}
			obj.setAnoOcorrencia(CalendarUtils.getAnoAtual());
			obj.setPeriodoOcorrencia(getPeriodoAtual());
			
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.ALTERAR_DATA_COLACAO);
			mov.setObjMovimentado(obj);
			
			execute(mov, getCurrentRequest());
			
			addMessage("Dados da sa�da do discente alterados com sucesso!", TipoMensagemUFRN.INFORMATION);
			
			return "menuGraduacao";
		} catch (NegocioException e) {
			e.printStackTrace();
			notifyError(e);
			addMensagens( e.getListaMensagens() );
			return null;
		}catch(Exception e) {
			e.printStackTrace();
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}
	}

}
