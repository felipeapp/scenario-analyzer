/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/05/2008
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.rh.dominio.Cargo;
import br.ufrn.rh.dominio.Escolaridade;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.dao.vestibular.FiscalDao;
import br.ufrn.sigaa.arq.dao.vestibular.InscricaoFiscalDao;
import br.ufrn.sigaa.arq.dao.vestibular.InscricaoVestibularDao;
import br.ufrn.sigaa.arq.dao.vestibular.ProcessoSeletivoVestibularDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.vestibular.dominio.ConceitoFiscal;
import br.ufrn.sigaa.vestibular.dominio.Fiscal;
import br.ufrn.sigaa.vestibular.dominio.InscricaoFiscal;
import br.ufrn.sigaa.vestibular.dominio.InscricaoVestibular;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.jsf.InscricaoSelecaoFiscalVestibularMBean;

/**
 * Valida os requisitos para a inscrição de Fiscal
 * 
 * @author Édipo Elder
 */
public class InscricaoFiscalValidator {

	/**
	 * Valida se a data atual está dentro do período de Inscrição para Fiscal.
	 * 
	 * @param lista
	 *            Lista onde será incluída a mensagem de erro, caso não esteja
	 *            dentro do período de inscrições para fiscais.
	 * @throws DAOException
	 */
	public static void validaPeriodoInscricao(ListaMensagens lista)
			throws DAOException {
		ProcessoSeletivoVestibularDao dao = new ProcessoSeletivoVestibularDao();
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			ProcessoSeletivoVestibular processoSeletivo = dao
					.findUltimoPeriodoInscricaoFiscal();
			if (processoSeletivo != null) {
				if (!CalendarUtils.isDentroPeriodo(processoSeletivo
						.getInicioInscricaoFiscal(), processoSeletivo
						.getFimInscricaoFiscal())) {
					lista.addErro("O período das inscrições para fiscal do "
							+ processoSeletivo.getNome()
							+ " é: "
							+ formatter.format(processoSeletivo
									.getInicioInscricaoFiscal())
							+ " a "
							+ formatter.format(processoSeletivo
									.getFimInscricaoFiscal()));
				}
			} else {
				lista.addErro(
						"Não há processo seletivo com data de inscrição para fiscal cadastrado");
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Valida a inscrição do Discente para um Processo Seletivo.
	 * 
	 * @param discente
	 * @param processoSeletivo
	 * @param lista
	 */
	public static void validaInscricaoDiscente(Discente discente,
			ProcessoSeletivoVestibular processoSeletivo,
			ListaMensagens lista) {
		try {
			if (!discente.isAtivo()) {
				lista.addErro("Somente discentes ativos podem fazer inscrição para seleção de fiscais");
				return;
			}
			if (discente.isLato()) {
				lista.addErro("Alunos de lato sensu não podem se inscrever para seleção de fiscais");
				return;
			}
			checaRequisitosComuns(discente.getPessoa(), processoSeletivo, lista);
			// Tem foto? -> colocado no mbean
			// requisitos específicos
			if (isRecadastro(discente.getPessoa())) {
				checaRequisitosAntigo(discente.getPessoa(), lista);
			} else {
				checaRequisitosDiscenteNovo(discente, lista);
			}
		} catch (DAOException e) {
			lista.addErro(e.getMessage());
		} finally {
		}
	}

	/**
	 * Valida a inscrição do servidor.
	 * 
	 * @param servidor
	 * @param processoSeletivo
	 * @param lista
	 */
	public static void validaInscricaoServidor(Servidor servidor,
			ProcessoSeletivoVestibular processoSeletivo,
			ListaMensagens lista) {
		try {
			checaRequisitosComuns(servidor.getPessoa(), processoSeletivo, lista);
			// Se tem foto, é adicionado no mbean
			// Requisitos específicos
			if (servidor.getAtivo().getId() != Ativo.SERVIDOR_ATIVO) {
				lista.addErro("O servidor deve ser ativo do quadro permanente da Instituição.");
			}
			if (Cargo.DOCENTE_SUBSTITUTO.contains(servidor.getCargo().getId())) {
				lista.addErro("Somente docentes permanentes podem se inscrever para seleção de fiscais.");
			}
			if (isRecadastro(servidor.getPessoa())) {
				checaRequisitosAntigo(servidor.getPessoa(), lista);
			} else {
				checaRequisitosServidorNovo(servidor, lista);
			}
		} catch (DAOException e) {
			lista.addErro(e.getMessage());
		}
	}

	/**
	 * Checa os requisitos para servidores novatos no Processo de Seleção de
	 * Fiscais:<br/>
	 * <ul>
	 * <li>Escolaridade do servidor</li>
	 * <li>Tempo de serviço no quadro permanente da instituição.</li>
	 * </ul>
	 * 
	 * @param servidor
	 * @param lista
	 * @throws DAOException 
	 */
	private static void checaRequisitosServidorNovo(Servidor servidor,
			ListaMensagens lista) throws DAOException {
		// Escolaridade
		if (servidor.getEscolaridade() == null) {
			lista.addErro("Não foi possível recuperar a informação sobre sua escolaridade para dar continuidade ao processo de inscrição");
		} else {
			if (servidor.getEscolaridade().equals(Escolaridade.ANALFABETO)
					|| servidor.getEscolaridade().equals(
							Escolaridade.ENSINO_FUNDAMENTAL)) {
				lista.addErro("Para se inscrever, o servidor deve ter o Ensino Médio completo");
			}
		}
		// tempo de serviço
		if ( CalendarUtils.calculoAnos(servidor.getDataAdmissao(), new Date()) < 1) {
			// verifica se há outro servidor associado à pessoa
			boolean outroVinculoAntigo = false;
			ServidorDao servidorDao = new ServidorDao();
			try {
				for (Servidor servidorAntigo : servidorDao.findByPessoaAndVinculos(servidor.getPessoa().getId(), Ativo.APOSENTADO, Ativo.EXCLUIDO)) {
					if (CalendarUtils.diferencaDias(servidorAntigo.getDataAdmissao(), new Date()) < 365) {
						outroVinculoAntigo = true;
						break;
					}
				}
				if (!outroVinculoAntigo) {
					lista.addErro("O servidor deve ser ativo do quadro permanente da Instituição há pelo menos um ano (sua admissão foi em "+CalendarUtils.format(servidor.getDataAdmissao(), "dd/MM/yyyy")+").");
				}
			} finally {
				servidorDao.close();
			}
		}
	}

	/**
	 * Verifica os requisitos de fiscais que já trabalharam em Processos
	 * Seletivos anteriores:<br/>
	 * <ul>
	 * <li>Conceito suficiente nos dois últimos processos trabalhados.</li>
	 * <li>Falta justificada.</li>
	 * </ul>
	 * 
	 * @param pessoa
	 * @param lista
	 * @throws DAOException
	 */
	private static void checaRequisitosAntigo(Pessoa pessoa, ListaMensagens lista) throws DAOException {
		FiscalDao fiscalDao = new FiscalDao();
		Collection<Fiscal> listaFiscal = fiscalDao.findByPessoa(pessoa, InscricaoSelecaoFiscalVestibularMBean.NUMERO_PROCESSO_SELETIVO_ANTERIOR);
		// Tem conceito insuficiente?
		for (Fiscal fiscal : listaFiscal) {
			if (fiscal.getConceito() != null
					&& fiscal.getConceito() == ConceitoFiscal.INSUFICIENTE) {
				lista.addErro("Você não poderá se inscrever para a seleção de fiscais, uma vez que teve conceito INSUFICIENTE em trabalho(s) anterior(es)");
				break;
			}
		}
		// tem falta não justificada?
		for (Fiscal fiscal : listaFiscal) {
			if (fiscal.getPresenteReuniao() != null && !fiscal.getPresenteReuniao()
					|| fiscal.getPresenteAplicacao() != null && !fiscal.getPresenteAplicacao())
				if (fiscal.getJustificou() != null && !fiscal.getJustificou()) {
				lista.addErro("Você tem falta não justificada em trabalho(s) anterior(es) como fiscal e não será possível fazer sua inscrição");
				break;
			}
		}
	}

	/**
	 * Verifica os requisitos de novos discentes ao Processo de Seleção de
	 * Fiscais:<br>
	 * <ul>
	 * <li>Ter cursado pelo menos dois semestres letivos (discentes de graduação)</li>
	 * <li>Não ter reprovação por frequência no último semestre cursado (discentes de graduação)</li>
	 * <li>Não ser formando ou graduando (discentes de graduação)</li>
	 * <li>Estrar matriculado no semestre atual.</li>
	 * <li>Não estar no processo de homologação de diplomas (discentes de pós-graduação)</li>
	 * </ul>
	 * 
	 * @param discente
	 * @param lista
	 * @throws DAOException
	 */
	private static void checaRequisitosDiscenteNovo(Discente discente,
			ListaMensagens lista) throws DAOException {
		DiscenteDao discenteDao = new DiscenteDao();
		try {

			CalendarioAcademico calendario = CalendarioAcademicoHelper
					.getCalendarioUnidadeGlobalGrad();
			Collection<MatriculaComponente> listaMC;
			// cadastramento
			// somente p/ Graduação
			if (discente.isGraduacao()) {
				DiscenteGraduacao grad = (DiscenteGraduacao) discenteDao.findByPK(discente.getId());
				
				// ter cursado pelo menos dois semestres letivos (8)
				CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente);
				discente.setSemestreAtual(discenteDao.calculaPeriodoAtualDiscente(grad, cal.getAno(), cal.getPeriodo()));
				if (discente.getSemestreAtual() < 3) {
					lista.addErro("O aluno deve ter cursado pelo menos dois semestres letivos na Instituição ");
				}
				// reprovado por frequência no último semestre cursado (9)
				listaMC = discenteDao.findMatriculasComponentesCurriculares(
						discente, SituacaoMatricula.getSituacoesTodas(), null);
				boolean algumaReprovacaoFalta = false;
				int ano = 0, periodo = 0;
				for (MatriculaComponente matriculaComponente : listaMC) {
					if (matriculaComponente.getAno() == null)
						continue;
					if (matriculaComponente.getAno() == calendario.getAno()
							&& matriculaComponente.getPeriodo() == calendario
									.getPeriodo())
						break;
					if (ano != matriculaComponente.getAno()
							|| periodo != matriculaComponente.getPeriodo()) {
						ano = matriculaComponente.getAno();
						periodo = matriculaComponente.getPeriodo();
						algumaReprovacaoFalta = false;
					}
					if (matriculaComponente.getSituacaoMatricula().equals(SituacaoMatricula.REPROVADO_FALTA)
							|| matriculaComponente.getSituacaoMatricula().equals(SituacaoMatricula.REPROVADO_MEDIA_FALTA)) {
						algumaReprovacaoFalta = true;
					}
				}
				if (algumaReprovacaoFalta) {
					lista.addErro("Para se inscrever, o aluno não pode ter sido reprovado por freqüência, em qualquer disciplina, no último semestre regular");
				}

				if (discente.getStatus() == StatusDiscente.FORMANDO) {
					lista.addErro("Para se inscrever, o aluno não pode ser formando no semestre atual");
				}
				if (discente.getStatus() == StatusDiscente.GRADUANDO) {
					lista.addErro("Para se inscrever, o aluno não pode ser graduando no semestre atual");
				}
				// estar matriculado no semestre atual
				// verifica se pelo menos uma situação matrícula não é trancada
				boolean todasTrancadas = true;
				listaMC = discenteDao.findMatriculasByDiscente(discente,
						calendario.getPeriodo(), calendario.getAno());
				for (MatriculaComponente matriculaComponente : listaMC) {
					if (!matriculaComponente.getSituacaoMatricula().equals(
							SituacaoMatricula.TRANCADO)) {
						todasTrancadas = false;
					}
				}
				if (todasTrancadas) {
					lista.addErro("Para se inscrever, o aluno deve estar matriculado em pelo menos uma disciplina, no semestre atual");
				}
			}
			if (discente.isStricto()) {
				DiscenteStricto discenteStricto = (DiscenteStricto) discenteDao.findByPK(discente.getId());
				if (discenteStricto.getStatus() == StatusDiscente.EM_HOMOLOGACAO) {
					lista.addErro("Para ser fiscal, você não pode estar no final do curso de pós-graduação");
				} 
			}

		} finally {
			discenteDao.close();
		}

	}

	/**
	 * Verifica se a Pessoa está inscrito no Processo de Seleção de Fiscais.
	 * 
	 * @param pessoa
	 * @param processoSeletivo
	 * @param lista
	 * @return
	 * @throws DAOException
	 */
	public static boolean isFiscalInscrito(Pessoa pessoa,
			ProcessoSeletivoVestibular processoSeletivo,
			ListaMensagens lista) throws DAOException {
		InscricaoFiscalDao inscricaoFiscalDao = new InscricaoFiscalDao();
		ProcessoSeletivoVestibularDao processoSeletivoDao = new ProcessoSeletivoVestibularDao();
		try {
			InscricaoFiscal inscricao = inscricaoFiscalDao.findByPessoaProcessoSeletivo(pessoa.getId(),processoSeletivo.getId());
			if (inscricao != null) 
				inscricao.setPermiteAlterarFoto(permiteAlterarFoto(inscricao));
			
			if (inscricao != null && !inscricao.isPermiteAlterarFoto()) {
				lista.addErro(
						"Você já está inscrito para a seleção de fiscal. Seu número de inscrição é: "
								+ inscricao.getNumeroInscricao());
				return true;
			}
			return false;
		} finally {
			inscricaoFiscalDao.close();
			processoSeletivoDao.close();
		}
	}

	/**
	 * Valida se o candidato pode ou não alterar a sua foto.
	 * 
	 * @return
	 * @throws ParseException
	 * @throws DAOException
	 */
	private static boolean permiteAlterarFoto(InscricaoFiscal inscricao) {
		if (inscricao.getStatusFoto().isValida() == false 
				&& CalendarUtils.isDentroPeriodo(inscricao.getProcessoSeletivoVestibular().getInicioInscricaoFiscal(), 
						inscricao.getProcessoSeletivoVestibular().getFimInscricaoFiscal())) 
			return true;
		return false;
	}
	
	/**
	 * Verifica se a Pessoa está inscrito no Processo de Seletivo como candidato.
	 * 
	 * @param pessoa
	 * @param lista
	 * @throws DAOException
	 */
	private static void isInscritoProcessoSeletivo(Pessoa pessoa, ProcessoSeletivoVestibular processoSeletivo, ListaMensagens lista) throws DAOException {
		InscricaoVestibularDao dao = new InscricaoVestibularDao();
		try {
			Collection<InscricaoVestibular> inscricoes = dao.findByNomeCpfInscricao(processoSeletivo.getId(), null, pessoa.getCpf_cnpj(), 0, false);
			if (!isEmpty(inscricoes)) {
				for (InscricaoVestibular inscricao : inscricoes) {
					if (inscricao.isValidada()) { 
						lista.addErro("O Fiscal não pode estar inscrito no Processo Seletivo.");
						break;
					}
				}
			}
		} catch (DAOException e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
	}

	/**
	 * Verifica os requisitos comuns a todas inscrições:<br>
	 * <ul>
	 * <li>Período de inscrição.</li>
	 * <li>Se já está inscrito para seleção de fiscais.</li>
	 * <li>Se não é candidato no processo seletivo.</li>
	 * </ul>
	 * 
	 * @param pessoa
	 * @param processoSeletivo
	 * @param lista
	 * @throws DAOException
	 */
	private static void checaRequisitosComuns(Pessoa pessoa,
			ProcessoSeletivoVestibular processoSeletivo,
			ListaMensagens lista) throws DAOException {
		// está no período de inscrição pra fiscal?
		validaPeriodoInscricao(lista);
		// requisitos gerais
		isFiscalInscrito(pessoa, processoSeletivo, lista);
		isInscritoProcessoSeletivo(pessoa, processoSeletivo, lista);
	}

	/**
	 * Verifica se o Discente é também Servidor.
	 * 
	 * @param discente
	 * @param lista
	 */
	public static void checaDiscenteServidor(Discente discente,
			ListaMensagens lista) {
		ServidorDao dao = new ServidorDao();
		try {
			Collection<Servidor> tmp = dao.findByPessoaAndVinculos(discente
					.getPessoa().getId(), Ativo.SERVIDOR_ATIVO);
			if (tmp != null && !tmp.isEmpty()) {
				lista.addErro("Não foi possível fazer a inscrição pelo vínculo de discente. Faça sua inscrição para a seleção de fiscal com seu vínculo de servidor no SIGRH");
			}
		} catch (DAOException e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
	}

	/**
	 * Verifica se a inscrição é recadastro.
	 * 
	 * @param pessoa
	 * @return
	 */
	public static boolean isRecadastro(Pessoa pessoa) {
		FiscalDao fiscalDao = new FiscalDao();
		try {
			Collection<Fiscal> fiscal = fiscalDao.findByPessoa(pessoa, InscricaoSelecaoFiscalVestibularMBean.NUMERO_PROCESSO_SELETIVO_ANTERIOR);
			if (fiscal != null && fiscal.size() > 0)
				return true;
		} catch (DAOException e) {
			return false;
		} finally {
			fiscalDao.close();
		}
		return false;
	}
}
