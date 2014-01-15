/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 28/07/2011
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;

/**
 * Controlador responsável por realizar pequenos ajustes em informações das turmas
 * como aumentar o número de vagas e reservas para uso na matrícula extraordinária.
 * 
 * @author Leonardo Campos
 *
 */
@Component @Scope("request")
public class AjustarTurmaMBean extends SigaaAbstractController<Turma> {

	// Definições das views 
	/** Define o link para o resumo dos dados da turma. */
	public static final String JSP_AJUSTES_TURMA = "/ensino/turma/ajustes_turma.jsp";

	private Integer novasVagas;
	
	/**
	 * Carrega as informações da turma selecionada e redireciona para o
	 * formulário de ajustes de turmas.<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{
		obj = new Turma();
		novasVagas = 0;
		populateObj(true);
		if (obj == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			obj = new Turma();
			return null;
		}

		obj.getReservas().iterator();
		obj.getDocentesTurmas().iterator();
		obj.getHorarios().iterator();

		SolicitacaoMatriculaDao solicitacaoDao = getDAO(SolicitacaoMatriculaDao.class);

		// Buscar se já existem alunos matriculados para a turma selecionada
		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);
		obj.setQtdMatriculados( matriculaDao.findTotalMatriculasByTurmaSituacao(obj, 
				SituacaoMatricula.EM_ESPERA.getId(), 
				SituacaoMatricula.MATRICULADO.getId(), 
				SituacaoMatricula.APROVADO.getId(), 
				SituacaoMatricula.APROVEITADO_CUMPRIU.getId(), 
				SituacaoMatricula.APROVEITADO_DISPENSADO.getId(), 
				SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId(), 
				SituacaoMatricula.REPROVADO.getId(), 
				SituacaoMatricula.REPROVADO_FALTA.getId(), 
				SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId()) );
		obj.setTotalSolicitacoes( solicitacaoDao.countByTurma(obj, SolicitacaoMatricula.getStatusSolicitacoesPendentes(), true ).intValue() );

		prepareMovimento(SigaaListaComando.AJUSTAR_TURMA);
		
		return forward(JSP_AJUSTES_TURMA);
	}
	
	/**
	 * Invoca o processador para persistir as modificações realizadas.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/ensino/turma/ajustes_turma.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String confirmarAjustes() throws ArqException {
		
		ValidatorUtil.validateRequired(obj.getLocal(), "Local", erros);
		ValidatorUtil.validateRequired(novasVagas, "Nº de Vagas a Adicionar", erros);
		ValidatorUtil.validateMinValue(novasVagas, 0, "Nº de Vagas a Adicionar", erros);
		
		obj.setCapacidadeAluno(obj.getCapacidadeAluno() + novasVagas);
		
		if(hasErrors()){
			addMensagens(erros);
			return null;
		}
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.AJUSTAR_TURMA);
		try {
			execute(mov);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		BuscaTurmaMBean bean = getMBean("buscaTurmaBean");
		return bean.popularBuscaGeral();
	}

	public Integer getNovasVagas() {
		return novasVagas;
	}

	public void setNovasVagas(Integer novasVagas) {
		this.novasVagas = novasVagas;
	}
}
