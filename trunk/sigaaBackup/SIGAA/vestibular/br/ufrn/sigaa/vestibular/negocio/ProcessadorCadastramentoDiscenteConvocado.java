/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 06/11/2012
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.beanutils.BeanUtils;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.negocio.PessoaMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.PlanoMatriculaIngressantes;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoAfastamentoAluno;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoMatriculaGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.ProcessadorMatriculaGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.SolicitacaoMatriculaHelper;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.negocio.MovimentacaoAlunoValidator;
import br.ufrn.sigaa.ensino.negocio.ProcessadorDiscente;
import br.ufrn.sigaa.ensino.negocio.ProcessadorMovimentacaoAluno;
import br.ufrn.sigaa.ensino.negocio.dominio.MovimentoMovimentacaoAluno;
import br.ufrn.sigaa.negocio.ProcessadorPessoa;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivoDiscente;

/** Processador responsável por cadastrar um discente aprovado em Processo Seletivo (Vestibular, SiSU, etc.) e, 
 * caso selecionado pelo usuário, matricular em um conjunto de turmas.
 * 
 * @author Édipo Elder F. de Melo
 *
 */
public class ProcessadorCadastramentoDiscenteConvocado extends AbstractProcessador {

	/** Altera o status do discente e matricula em turmas.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		DiscenteDao dao = getDAO(DiscenteDao.class, mov);
		SolicitacaoMatriculaDao sDao = getDAO(SolicitacaoMatriculaDao.class, mov);
		ConvocacaoProcessoSeletivoDiscente cpsd = ((MovimentoCadastro) mov).getObjMovimentado();
		DiscenteGraduacao discente = cpsd.getDiscente();
		Pessoa pessoa = discente.getPessoa();
		int novoStatus = discente.getStatus();
		try {
			dao.detach(pessoa);
			discente = dao.refresh(discente);
			discente.setStatus(novoStatus);
			if (novoStatus  == StatusDiscente.CADASTRADO) {
				if (isEmpty(discente.getMatricula())) {
					ProcessadorDiscente procDiscente = new ProcessadorDiscente();
					procDiscente.gerarMatricula(discente, dao);
					dao.updateDiscente(discente.getId(), "matricula", discente.getMatricula());
				}
			}
//			dao.updateDiscente(discente.getId(), "status", discente.getStatus());
			DiscenteHelper.alterarStatusDiscente(discente, discente.getStatus(), mov, dao);
//			dao.update(discente.getDiscente());
			// efetua a matrícula em componentes curriculares
			if (discente.getStatus() == StatusDiscente.CADASTRADO) {
				matriculaTurmas(mov, discente, dao);
				// verifica se o discente passou para ativo
				Discente d = dao.findByPrimaryKey(discente.getId(), Discente.class);
				// se ativo, cancela os vínculos anteiores.
				if (d.getStatus() == StatusDiscente.ATIVO)
					cancelaVinculosAnteriores(dao, sDao, discente, mov);
			}
			// atualiza dados pessoais
			Comando comando = SigaaListaComando.ALTERAR_PESSOA;
			PessoaMov movPessoa = new PessoaMov();
			movPessoa.setCodMovimento(comando);
			movPessoa.setPessoa(pessoa);
			movPessoa.setUsuarioLogado(mov.getUsuarioLogado());
			movPessoa.setSistema(mov.getSistema());
			ProcessadorPessoa procPessoa = new ProcessadorPessoa();
			procPessoa.execute(movPessoa);	
			discente.setPessoa(pessoa);
		} finally {
			dao.close();
			sDao.close();
		}
		return discente;
	}

	/** Cancela os outros vínculos ativos que o discente possa ter. O vínculo será cancelado se o discente cadastrado estiver com status ATIVO
	 * @param dao
	 * @param discente
	 * @param movimento
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException 
	 */
	public void cancelaVinculosAnteriores(DiscenteDao dao, SolicitacaoMatriculaDao sDao, DiscenteGraduacao discente, Movimento movimento) throws NegocioException, ArqException, RemoteException {
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente); 
		int[] tiposValidos = {Discente.REGULAR};
		int[] statusValidos = { StatusDiscente.ATIVO, StatusDiscente.FORMANDO,
				StatusDiscente.TRANCADO,  StatusDiscente.ATIVO_DEPENDENCIA };
		@SuppressWarnings("unchecked")
		Collection<DiscenteGraduacao> discentes = (Collection<DiscenteGraduacao>) dao.findOtimizado(discente.getPessoa().getCpf_cnpj(), null, null, null, null, null, statusValidos, tiposValidos, 0, NivelEnsino.GRADUACAO, false);
		if (!isEmpty(discentes)) {
			// remove do resultado o vínculo atual
			Iterator<? extends DiscenteAdapter> iterator = discentes.iterator();
			while (iterator.hasNext())
				if (iterator.next().getId() == discente.getId())
					iterator.remove();
			if (!isEmpty(discentes)) {
				
				ArrayList<Integer> idsDiscentes = new ArrayList<Integer>();
				for (DiscenteGraduacao d : discentes)
					idsDiscentes.add(d.getDiscente().getId());
				if (idsDiscentes != null && !idsDiscentes.isEmpty()){
					ArrayList<SolicitacaoMatricula> solicitacoes = (ArrayList<SolicitacaoMatricula>) sDao.findByDiscentesAnoPeriodo(idsDiscentes, cal.getAno(), cal.getPeriodo());
					SolicitacaoMatriculaHelper.anularSolicitacoes(movimento.getUsuarioLogado(), "Cancelamento de Vínculo por entrada Regular", solicitacoes);
				}
				
				for (DiscenteGraduacao vinculoAnterior : discentes) {
					
					if (vinculoAnterior.isTrancado())
						retornarAluno(vinculoAnterior, movimento, cal);
					
					
					MovimentacaoAluno obj = new MovimentacaoAluno();
					obj.setAnoOcorrencia(cal.getAno());
					obj.setPeriodoOcorrencia(cal.getPeriodo());
					obj.setAnoReferencia(cal.getAno());
					obj.setPeriodoReferencia(cal.getPeriodo());
					obj.setDiscente(vinculoAnterior);
					obj.setTipoMovimentacaoAluno(new TipoMovimentacaoAluno(TipoMovimentacaoAluno.CANCELAMENTO_POR_EFETIVACAO_NOVO_CADASTRO));
					MovimentoMovimentacaoAluno mov = new MovimentoMovimentacaoAluno();
					mov.setObjMovimentado(obj);
					mov.setCodMovimento(SigaaListaComando.AFASTAR_ALUNO);
					mov.setUsuarioLogado(movimento.getUsuarioLogado());
					mov.setSistema(movimento.getSistema());
					ProcessadorMovimentacaoAluno proc = new ProcessadorMovimentacaoAluno();
					proc.execute(mov);
				}
			}
		}
	}

	/**
	 * Retorna um discente trancado
	 * 
	 * @param vinculoAnterior
	 * @param movimento 
	 * @param cal 
	 * @throws ArqException 
	 * @throws NegocioException 
	 * @throws RemoteException 
	 */
	private void retornarAluno(DiscenteGraduacao vinculoAnterior, Movimento movimento, CalendarioAcademico cal) throws NegocioException, ArqException, RemoteException {
		
		MovimentacaoAlunoDao dao = getDAO(MovimentacaoAlunoDao.class, movimento);
		
		ListaMensagens erros = new ListaMensagens();
		
		Usuario user = new Usuario();
		
		try {
			BeanUtils.copyProperties(user, movimento.getUsuarioLogado());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		MovimentacaoAluno afastamento = MovimentacaoAlunoValidator.localizarUltimoTrancamentoDiscente(vinculoAnterior.getDiscente(), user, dao, erros , cal);
		
		afastamento.setDataRetorno(new Date());
		afastamento.setValorMovimentacao(0);
		afastamento.setTipoRetorno(MovimentacaoAluno.ADMINISTRATIVO);
		afastamento.setObservacao("Aluno retornado pela rotina do cadastramento de discente.");
		afastamento.setAnoOcorrencia(CalendarUtils.getAnoAtual());
		afastamento.setPeriodoOcorrencia(cal.getPeriodo());
		
		MovimentoAfastamentoAluno mov = new MovimentoAfastamentoAluno();
		mov.setCodMovimento(SigaaListaComando.RETORNAR_ALUNO_AFASTADO);
		mov.setObjMovimentado(afastamento);
		mov.setUsuarioLogado(movimento.getUsuarioLogado());
		mov.setSistema(movimento.getSistema());		
		ProcessadorMovimentacaoAluno proc = new ProcessadorMovimentacaoAluno();
		proc.execute(mov);
		
		// atualizando o novo status do discente
		vinculoAnterior.setStatus(afastamento.getDiscente().getStatus());
		
	}

	/** Caso o usuário tenha selecionado um Plano de Matrícula de Ingressantes, o discente será matriculado nas turmas do plano.
	 * @param mov
	 * @param discente
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public void matriculaTurmas(Movimento mov, DiscenteGraduacao discente, GenericDAO dao) throws DAOException, NegocioException, ArqException, RemoteException {
		PlanoMatriculaIngressantes planoMatricula = (PlanoMatriculaIngressantes) ((MovimentoCadastro) mov).getObjAuxiliar();
		if(!isEmpty(planoMatricula)) {
			try {
				planoMatricula = dao.findByPrimaryKeyLock(planoMatricula.getId(), PlanoMatriculaIngressantes.class);
				if (planoMatricula != null && planoMatricula.hasVagas()) {
					CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendario(discente);
					Collection<Turma> turmas = new LinkedList<Turma>();
					for (Turma turma : planoMatricula.getTurmas()) {
						turma.setMatricular(true);
						turmas.add(turma);
					}
					MovimentoMatriculaGraduacao movimento = new MovimentoMatriculaGraduacao();
					movimento.setDiscente(discente);
					movimento.setTurmas(turmas);
					movimento.setCalendarioAcademicoAtual(calendario);
					movimento.setMatriculaEAD(discente.isEAD());
					movimento.setMatriculaFerias(false);
					movimento.setRestricoes(null);
					movimento.setCodMovimento(SigaaListaComando.MATRICULAR_GRADUACAO);
					movimento.setUsuarioLogado(mov.getUsuarioLogado());
					movimento.setAtualizarStatusDiscenteETiposIntegralizacao(true);
					movimento.setSistema(mov.getSistema());
					movimento.setSituacao(SituacaoMatricula.MATRICULADO);
					movimento.setMatriculaIngressante(true);
					ProcessadorMatriculaGraduacao procMatricula = new ProcessadorMatriculaGraduacao();
					procMatricula.execute(movimento);
					// verifica se o discente já foi atendido anteriormente por este plano. 
					// É necesário porque o discente pode ter sido estornado e cadastrado novamente
					if (!planoMatricula.getDiscentesAtendidos().contains(discente.getDiscente())) {
						planoMatricula.addDiscente(discente.getDiscente());
						dao.update(planoMatricula);
					}
				}
			} finally {
				dao.close();
			}
		}
	}

	/** Valida os dados antes de executar.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
