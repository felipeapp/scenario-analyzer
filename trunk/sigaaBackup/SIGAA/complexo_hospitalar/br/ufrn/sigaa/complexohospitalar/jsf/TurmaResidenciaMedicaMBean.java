package br.ufrn.sigaa.complexohospitalar.jsf;

import java.util.Date;
import java.util.HashSet;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.ensino.graduacao.jsf.ComponenteCurricularMBean;
import br.ufrn.sigaa.ensino.jsf.BuscaTurmaMBean;
import br.ufrn.sigaa.ensino.jsf.TurmaMBean;
import br.ufrn.sigaa.ensino.negocio.TurmaValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.TurmaMov;

/**
 * Bean responsável por operações em turmas do nível de residência médica.
 * 
 * @author bernardo
 *
 */
@Component("turmaResidenciaMedica") @Scope("session")
public class TurmaResidenciaMedicaMBean extends TurmaMBean {
	
	/** Define o link para o formulário de dados gerais. */
	public static final String JSP_DADOS_GERAIS = "/complexo_hospitalar/turma/dados_gerais.jsp";
	/** Define o link para o resumo dos dados da turma. */
	public static final String JSP_RESUMO = "/complexo_hospitalar/turma/resumo.jsp";
	/** Define o link para para o formulário de confirmação de remoção de turma. */
	public static final String JSP_CONFIRMA_REMOCAO  = "/complexo_hospitalar/turma/confirma_remocao.jsp";
	
	@Override
	public ListaMensagens validarSelecaoComponenteCurricular(ComponenteCurricular componente) throws ArqException {
		return null;
	}

	@Override
	public String retornarSelecaoComponente() {
		// TODO redirecionar o usuário para o formulário que invocou a seleção de componentes.
		return cancelar();
	}

	@Override
	public String formSelecaoComponente() {
		try {
			ComponenteCurricularMBean mBean = getMBean("componenteCurricular");
			
			return mBean.buscarComponente(this, "Cadastro de Turmas", null, false, false, TipoComponenteCurricular.DISCIPLINA, TipoComponenteCurricular.MODULO, TipoComponenteCurricular.ATIVIDADE);
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
			return null;
		}
	}

	@Override
	public String formDadosGerais() {
		return forward(JSP_DADOS_GERAIS);
	}

	@Override
	public String formConfirmacao() {
		return forward(JSP_RESUMO);
	}

	@Override
	public String formConfirmacaoRemover() {
		return forward(JSP_CONFIRMA_REMOCAO);
	}

	@Override
	public void beforeSelecionarComponente() throws ArqException {
		obj.setAno(getCalendario().getAno());
		obj.setPeriodo(getCalendario().getPeriodo());
		obj.setDataInicio(getCalendario().getInicioPeriodoLetivo());
		obj.setDataFim(getCalendario().getFimPeriodoLetivo());
		obj.setSituacaoTurma(new SituacaoTurma( SituacaoTurma.A_DEFINIR_DOCENTE ));
		obj.setTipo(Turma.REGULAR);
	}

	@Override
	public void beforeDefinirDocentesTurma() throws ArqException {
	}

	@Override
	public void beforeDadosGerais() throws ArqException {
	}

	@Override
	public void beforeConfirmacao() throws ArqException {
	}

	@Override
	public void beforeAtualizarTurma() throws ArqException {
	}

	@Override
	public void validaDadosGerais(ListaMensagens erros) throws DAOException {
		if( obj.getDisciplina() == null || obj.getDisciplina().getId() <= 0 ){
			addMensagemErro("Selecione um componente curricular.");
		} else 	if (!obj.getDisciplina().isAtivo()) {
			addMensagemErro("O componente curricular " + obj.getDisciplina().getDescricaoResumida() + " não está ativo.");
		}
				
		if(hasErrors()) return;

		TurmaDao dao = getDAO(TurmaDao.class);
		obj.setDisciplina( dao.findByPrimaryKey(obj.getDisciplina().getId(), ComponenteCurricular.class) );
		
		Unidade unidade = dao.findByPrimaryKey(obj.getDisciplina().getUnidade().getId(), Unidade.class);
		obj.getDisciplina().setUnidade(unidade);

		TurmaValidator.validaDadosBasicosResidenciaSaude(obj, erros);

		if (hasOnlyErrors())
			return;
	}

	@Override
	public void validaDocentesTurma(ListaMensagens erros) throws DAOException {
	}

	@Override
	public void validaHorariosTurma(ListaMensagens erros) throws DAOException {
	}

	@Override
	public void checkRoleAtualizarTurma() throws ArqException {
	}

	@Override
	public void checkRolePreRemover() throws ArqException {
		if (obj.getQtdMatriculados() > 0) {
			addMensagemErro("Não é possível remover uma turma com discentes matriculados.");
		}
	}

	@Override
	public void checkRoleCadastroTurmaSemSolicitacao(ListaMensagens erros) throws SegurancaException {
		//TODO verificar se deve existir validação de período de cadastro pra algum papel
		checkRole(SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA, SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA);
	}

	@Override
	public String cadastrar() throws ArqException {
		if (getConfirmButton().equalsIgnoreCase("remover")) {
			return remover();
		} else {
			
			Comando cmd = SigaaListaComando.CADASTRAR_TURMA;
			TurmaMov mov = new TurmaMov();
			if (getConfirmButton().equalsIgnoreCase("alterar")) {
				cmd = SigaaListaComando.ALTERAR_TURMA;
				mov.setAlteracaoTurma(getAlteracaoTurma());
			}
			if (obj.getCurso() != null && obj.getCurso().getId() == 0)
				obj.setCurso(null);
			
			mov.setCodMovimento(cmd);
			mov.setTurma(obj);
			mov.setSolicitacaoEnsinoIndividualOuFerias(null);
			mov.setSolicitacoes( new HashSet<SolicitacaoTurma>() );

			for( ReservaCurso r : obj.getReservas() ){
				mov.getSolicitacoes().add( r.getSolicitacao() );
			}
			try {
				execute(mov, getCurrentRequest());
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				e.printStackTrace();
				return null;
			}
			if (cmd.equals(SigaaListaComando.CADASTRAR_TURMA)) {
				if( obj.getDisciplina().isSubUnidade() )
					addMessage("ATENÇÃO! A turma criada foi de uma subunidade de um bloco e só estará disponível para matrícula após todas as subunidades do bloco terem sido criadas.", TipoMensagemUFRN.INFORMATION);
				
				addMessage("Disciplina " + obj.getDescricaoSemDocente() + " cadastrada com sucesso!", TipoMensagemUFRN.INFORMATION);
			} else {
				addMessage("Disciplina " + obj.getDescricaoSemDocente() + " alterada com sucesso!", TipoMensagemUFRN.INFORMATION);
			}
			removeOperacaoAtiva();
	
			if (cmd.equals(SigaaListaComando.ALTERAR_TURMA)) {
				BuscaTurmaMBean mBean = getMBean("buscaTurmaBean");
				return mBean.buscarGeral();
			} else {
				return cancelar();
			}
		}
	}

	@Override
	public boolean isPodeAlterarHorarios() {
		return obj.isAberta() || (obj.getId() == 0 || !isMatriculada());
	}

	@Override
	public boolean isDefineHorario() {
		return true;
	}

	@Override
	public boolean isDefineDocentes() {
		return true;
	}

	@Override
	public char getNivelEnsino() {
		return NivelEnsino.RESIDENCIA;
	}
	
	/** 
	 * Indica se a operação corrente é de edição do código da turma.
	 */
	public boolean isEditarCodigoTurma() {
		return obj.getId() != 0;
	}

	@Override
	public String definePeriodosTurma(Date inicio, Date fim)
			throws ArqException {
		// TODO Auto-generated method stub
		return null;
	}

}
