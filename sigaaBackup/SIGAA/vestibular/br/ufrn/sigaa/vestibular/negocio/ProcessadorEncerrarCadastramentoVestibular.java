/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 24/01/2013
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.dao.vestibular.ConvocacaoProcessoSeletivoDiscenteDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dao.PlanoMatriculaIngressantesDao;
import br.ufrn.sigaa.ensino.dominio.PlanoMatriculaIngressantes;
import br.ufrn.sigaa.ensino.graduacao.dominio.CancelamentoConvocacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoConvocacaoVestibular;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.negocio.ProcessadorDiscente;
import br.ufrn.sigaa.mensagens.TemplatesDocumentos;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivoDiscente;
import br.ufrn.sigaa.vestibular.dominio.EfetivacaoCadastramentoReserva;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/**
 * Processador responsável por cadastrar ou excluir candidatos aprovados no
 * vestibular que fizeram o PRÉ-CADASTRO na instituição. Será calculado o número
 * de vagas disponíveis por curso e aplicado como ponto de corte entre os
 * discentes com status PRÉ CADASTRADO.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
public class ProcessadorEncerrarCadastramentoVestibular extends AbstractProcessador {

	/** Cadastra/exclui discentes com status PRÉ CADASTRO de acordo com o número de vagas no curso. 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		Boolean excluirPendente = (Boolean) ((MovimentoCadastro) mov).getObjAuxiliar();
		if (excluirPendente != null && excluirPendente)
			excluirDiscentesPendenteCadastro(mov);
		else if (mov.getCodMovimento().equals(SigaaListaComando.ENCERRAR_CADASTRAMENTO_VESTIBULAR))
			encerrarCadastramentoDiscentes(mov);
		else
			efetivarCadastramentoDiscentes(mov);
		return null;
	}
	
	/** Exclui os discentes com status PENDENTE DE CADASTRO
	 * @param mov
	 * @throws ArqException 
	 */
	private void excluirDiscentesPendenteCadastro(Movimento mov) throws ArqException {
		ConvocacaoProcessoSeletivoDiscenteDao dao = getDAO(ConvocacaoProcessoSeletivoDiscenteDao.class, mov);
		try {
			ProcessoSeletivoVestibular processoSeletivo = ((MovimentoCadastro) mov).getObjMovimentado();
			Collection<DiscenteGraduacao> discenteExcluidos = dao.findDiscentesPendentesCadastro(processoSeletivo);
			if (!isEmpty(discenteExcluidos)) {
				for (DiscenteGraduacao discente : discenteExcluidos) {
					DiscenteHelper.alterarStatusDiscente(discente, StatusDiscente.EXCLUIDO, "DISCENTE EXCLUÍDO POR NÃO CADASTRAMENTO", mov, dao);
				}
			}
		} finally {
			dao.close();
		}
	}

	/** Efetiva o cadastramento de discentes.
	 * @param mov
	 * @throws HibernateException
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws RemoteException
	 */
	private void efetivarCadastramentoDiscentes(Movimento mov) throws HibernateException, ArqException, NegocioException, RemoteException {
		PlanoMatriculaIngressantesDao dao = getDAO(PlanoMatriculaIngressantesDao.class, mov);
		DiscenteDao discenteDao = getDAO(DiscenteDao.class, mov);
		SolicitacaoMatriculaDao solicitacaoDao = getDAO(SolicitacaoMatriculaDao.class, mov);
		try {
			// busca se houve efetivação de cadastro na data atual, caso sim, será usado este
			EfetivacaoCadastramentoReserva efetivacao = (EfetivacaoCadastramentoReserva) 
					dao.getSession().createCriteria(EfetivacaoCadastramentoReserva.class).add(Restrictions.eq("dataCadastro", new Date())).uniqueResult();
			if (efetivacao == null) 
				efetivacao = new EfetivacaoCadastramentoReserva();
			else
				efetivacao.getConvocacoes().iterator();
			Collection<ConvocacaoProcessoSeletivoDiscente> convocacoes = ((MovimentoConvocacaoVestibular) mov).getConvocacoes();
			// cadastra os discentes
			int count = 0;
			ProcessadorCadastramentoDiscenteConvocado procCadastro = new ProcessadorCadastramentoDiscenteConvocado();
			for (ConvocacaoProcessoSeletivoDiscente convocacao : convocacoes) {
				System.out.println("Cadastrando " + (++count) + " de " + convocacoes.size());
				PlanoMatriculaIngressantes planoMatriculaIngressante = selecionaPlanoMatricula(dao, convocacao);
				MovimentoCadastro movCadastro = new MovimentoCadastro(convocacao);
				movCadastro.setCodMovimento(SigaaListaComando.CADASTRAR_DISCENTE_CONVOCADO);
				movCadastro.setObjAuxiliar(planoMatriculaIngressante);
				movCadastro.setUsuarioLogado(mov.getUsuarioLogado());
				movCadastro.setSistema(mov.getSistema());
				DiscenteGraduacao discente = convocacao.getDiscente();
				if (discente.getStatus()  == StatusDiscente.CADASTRADO) {
					if (isEmpty(discente.getMatricula())) {
						ProcessadorDiscente procDiscente = new ProcessadorDiscente();
						procDiscente.gerarMatricula(discente, dao);
						discenteDao.updateDiscente(discente.getId(), "matricula", discente.getMatricula());
					}
					procCadastro.cancelaVinculosAnteriores(discenteDao,solicitacaoDao,discente,movCadastro);
				}
				DiscenteHelper.alterarStatusDiscente(discente, discente.getStatus(), movCadastro, discenteDao);
				// efetua a matrícula em componentes curriculares
				if (discente.getStatus() == StatusDiscente.CADASTRADO) {
					discente = dao.refresh(discente);
					procCadastro.matriculaTurmas(movCadastro, discente, dao);
				}
				// cancela os que não serão cadastrados
				if (convocacao.getCancelamento() != null) {
					dao.create(convocacao.getCancelamento());
				}
				notificaDiscente(convocacao);
				efetivacao.addConvocacao(convocacao);
			}
			dao.createOrUpdate(efetivacao);
		} finally {
			dao.close();
			discenteDao.close();
			solicitacaoDao.close();
		}
	}
	
	/** Encerra o cadastramento de discentes.
	 * @param mov
	 * @throws HibernateException
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws RemoteException
	 */
	private void encerrarCadastramentoDiscentes(Movimento mov) throws HibernateException, ArqException, NegocioException, RemoteException {
		DiscenteDao dao = getDAO(DiscenteDao.class, mov);
		try {
			Collection<CancelamentoConvocacao> cancelamentos = ((MovimentoConvocacaoVestibular) mov).getCancelamentos();
			// cadastra os discentes
			int count = 0;
			for (CancelamentoConvocacao cancelamento : cancelamentos) {
				System.out.println("Cancelando " + (++count) + " de " + cancelamentos.size());
				DiscenteGraduacao discente = cancelamento.getConvocacao().getDiscente();
				DiscenteHelper.alterarStatusDiscente(discente, StatusDiscente.EXCLUIDO, mov, dao);
				dao.create(cancelamento);
			}
		} finally {
			dao.close();
		}
	}
	
	/** Envia um e-mail notificando o cadastro/exclusão do discente.
	 * @param convocacao
	 */
	private void notificaDiscente(ConvocacaoProcessoSeletivoDiscente convocacao) {
		int status = convocacao.getDiscente().getStatus();
		if (status == StatusDiscente.PENDENTE_CADASTRO || status == StatusDiscente.PRE_CADASTRADO)
			return;
		Pessoa p = convocacao.getDiscente().getPessoa();
		Map<String, String> params = new HashMap<String, String>();
		params.put("SAUDACAO", "Prezad" + (p.getSexo() == Pessoa.SEXO_FEMININO ? "a " : "o ") + p.getNome());
		params.put("CURSO", convocacao.getMatrizCurricular().getDescricao());
		String nome = String.valueOf( p.getNome() );
		String email = String.valueOf( p.getEmail() );
		String template = null;
		if (status == StatusDiscente.CADASTRADO || status == StatusDiscente.ATIVO) {
			params.put("ASSUNTO", "SIGAA - CONFIRMAÇÃO DE CADASTRO");	
			template = TemplatesDocumentos.EMAIL_CONFIRMACAO_DISCENTE_PRECADASTRADO;
		} else if (status == StatusDiscente.CANCELADO || status == StatusDiscente.EXCLUIDO){
			params.put("ASSUNTO", "SIGAA - CANCELAMENTO DE CADASTRO");	
			template = TemplatesDocumentos.EMAIL_EXCLUSAO_DISCENTE_PRECADASTRADO;
		}
		Mail.enviaComTemplate(nome, email, template, params);
	}

	/** Retorna o primeiro plano de matrícula com vagas disponível para o discente.
	 * @param dao
	 * @param convocacao
	 * @return
	 * @throws DAOException
	 */
	private PlanoMatriculaIngressantes selecionaPlanoMatricula (PlanoMatriculaIngressantesDao dao, ConvocacaoProcessoSeletivoDiscente convocacao) throws DAOException {
		DiscenteGraduacao discente = convocacao.getDiscente();
		Collection<PlanoMatriculaIngressantes> planos = dao.findByMatrizCurricular(discente.getMatrizCurricular().getId(),
				discente.getAnoIngresso(), discente.getPeriodoIngresso());
		if (!isEmpty(planos)) {
			for (PlanoMatriculaIngressantes plano : planos) {
				if (plano.hasVagas())
					return plano;
			}
		}
		return null;
	}
	
	/** Valida os dados para a conclusão do encerramento do cadastro
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS, mov);
	}

}
