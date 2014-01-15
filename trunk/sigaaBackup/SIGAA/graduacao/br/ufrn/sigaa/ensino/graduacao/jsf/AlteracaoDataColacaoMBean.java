/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * MBean usado para as operações de alteração das datas de colação de grau.
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
	 * Redirecionar para DiscenteGraduacaoMBean para a busca de discentes de graduação
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
	 * Seleciona um discente para alterar a data de colação do mesmo.
	 * <br>Não Chamado por JSP.
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
	 * Muda a movimentação de conclusão de programa
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
				addMensagemErro("O aluno selecionado não possui movimentação de conclusão de programa.");
			}
			setConfirmButton("Confirmar Alteração");
		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("Não foi possível carregar o discente escolhido");
		}		
	}

	
	/**
	 * Altera a data da Colação
	 *
	 * @return
	 * 
	 * JSP: SIGAA/app/sigaa.ear/sigaa.war/graduacao/alteracao_data_colacao/form.jsp
	 */
	public String alterarData(){
		try {
			ValidatorUtil.validateRequired(obj.getAnoReferencia(), "Ano", erros);
			ValidatorUtil.validateRange(obj.getAnoReferencia(), ANO_CONCLUSAO_MINIMO, CalendarUtils.getAnoAtual() + 1, "Ano", erros);
			ValidatorUtil.validateRequired(obj.getPeriodoReferencia(), "Período", erros);
			ValidatorUtil.validateRange(obj.getPeriodoReferencia(), 1, 2, "Período", erros);
			ValidatorUtil.validateRequired(obj.getDiscente().getDataColacaoGrau(), "Data de Colação", erros);
			// valida a data de colação pelo calendário acdêmico
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendarioExato(obj.getAnoReferencia(), obj.getPeriodoReferencia(), new Unidade(getUnidadeGestora()), NivelEnsino.GRADUACAO, null, null, null, null);
			if (cal != null) {
				validateMinValue(obj.getDiscente().getDataColacaoGrau(), cal.getFimPeriodoLetivo(), "Data de colação", erros);
			} else {
				// não há calendário cadastrado. calcula o calendário como sendo o último dia do semestre expecificado
				// Julho = 7 / Janeiro = 1
				int mes = obj.getPeriodoReferencia() == 2 ? 1 : 7;
				int ano = obj.getPeriodoReferencia() == 2 ? obj.getAnoReferencia() + 1 : obj.getAnoReferencia();
				Date data = CalendarUtils.adicionaDias(CalendarUtils.getInstance(1, mes, ano).getTime(), -1);
				validateMinValue(obj.getDiscente().getDataColacaoGrau(), data, "Data de colação", erros);
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
			
			addMessage("Dados da saída do discente alterados com sucesso!", TipoMensagemUFRN.INFORMATION);
			
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
