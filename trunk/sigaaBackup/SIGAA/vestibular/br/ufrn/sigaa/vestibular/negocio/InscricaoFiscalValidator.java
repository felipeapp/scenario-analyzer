/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Valida os requisitos para a inscri��o de Fiscal
 * 
 * @author �dipo Elder
 */
public class InscricaoFiscalValidator {

	/**
	 * Valida se a data atual est� dentro do per�odo de Inscri��o para Fiscal.
	 * 
	 * @param lista
	 *            Lista onde ser� inclu�da a mensagem de erro, caso n�o esteja
	 *            dentro do per�odo de inscri��es para fiscais.
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
					lista.addErro("O per�odo das inscri��es para fiscal do "
							+ processoSeletivo.getNome()
							+ " �: "
							+ formatter.format(processoSeletivo
									.getInicioInscricaoFiscal())
							+ " a "
							+ formatter.format(processoSeletivo
									.getFimInscricaoFiscal()));
				}
			} else {
				lista.addErro(
						"N�o h� processo seletivo com data de inscri��o para fiscal cadastrado");
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Valida a inscri��o do Discente para um Processo Seletivo.
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
				lista.addErro("Somente discentes ativos podem fazer inscri��o para sele��o de fiscais");
				return;
			}
			if (discente.isLato()) {
				lista.addErro("Alunos de lato sensu n�o podem se inscrever para sele��o de fiscais");
				return;
			}
			checaRequisitosComuns(discente.getPessoa(), processoSeletivo, lista);
			// Tem foto? -> colocado no mbean
			// requisitos espec�ficos
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
	 * Valida a inscri��o do servidor.
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
			// Se tem foto, � adicionado no mbean
			// Requisitos espec�ficos
			if (servidor.getAtivo().getId() != Ativo.SERVIDOR_ATIVO) {
				lista.addErro("O servidor deve ser ativo do quadro permanente da Institui��o.");
			}
			if (Cargo.DOCENTE_SUBSTITUTO.contains(servidor.getCargo().getId())) {
				lista.addErro("Somente docentes permanentes podem se inscrever para sele��o de fiscais.");
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
	 * Checa os requisitos para servidores novatos no Processo de Sele��o de
	 * Fiscais:<br/>
	 * <ul>
	 * <li>Escolaridade do servidor</li>
	 * <li>Tempo de servi�o no quadro permanente da institui��o.</li>
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
			lista.addErro("N�o foi poss�vel recuperar a informa��o sobre sua escolaridade para dar continuidade ao processo de inscri��o");
		} else {
			if (servidor.getEscolaridade().equals(Escolaridade.ANALFABETO)
					|| servidor.getEscolaridade().equals(
							Escolaridade.ENSINO_FUNDAMENTAL)) {
				lista.addErro("Para se inscrever, o servidor deve ter o Ensino M�dio completo");
			}
		}
		// tempo de servi�o
		if ( CalendarUtils.calculoAnos(servidor.getDataAdmissao(), new Date()) < 1) {
			// verifica se h� outro servidor associado � pessoa
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
					lista.addErro("O servidor deve ser ativo do quadro permanente da Institui��o h� pelo menos um ano (sua admiss�o foi em "+CalendarUtils.format(servidor.getDataAdmissao(), "dd/MM/yyyy")+").");
				}
			} finally {
				servidorDao.close();
			}
		}
	}

	/**
	 * Verifica os requisitos de fiscais que j� trabalharam em Processos
	 * Seletivos anteriores:<br/>
	 * <ul>
	 * <li>Conceito suficiente nos dois �ltimos processos trabalhados.</li>
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
				lista.addErro("Voc� n�o poder� se inscrever para a sele��o de fiscais, uma vez que teve conceito INSUFICIENTE em trabalho(s) anterior(es)");
				break;
			}
		}
		// tem falta n�o justificada?
		for (Fiscal fiscal : listaFiscal) {
			if (fiscal.getPresenteReuniao() != null && !fiscal.getPresenteReuniao()
					|| fiscal.getPresenteAplicacao() != null && !fiscal.getPresenteAplicacao())
				if (fiscal.getJustificou() != null && !fiscal.getJustificou()) {
				lista.addErro("Voc� tem falta n�o justificada em trabalho(s) anterior(es) como fiscal e n�o ser� poss�vel fazer sua inscri��o");
				break;
			}
		}
	}

	/**
	 * Verifica os requisitos de novos discentes ao Processo de Sele��o de
	 * Fiscais:<br>
	 * <ul>
	 * <li>Ter cursado pelo menos dois semestres letivos (discentes de gradua��o)</li>
	 * <li>N�o ter reprova��o por frequ�ncia no �ltimo semestre cursado (discentes de gradua��o)</li>
	 * <li>N�o ser formando ou graduando (discentes de gradua��o)</li>
	 * <li>Estrar matriculado no semestre atual.</li>
	 * <li>N�o estar no processo de homologa��o de diplomas (discentes de p�s-gradua��o)</li>
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
			// somente p/ Gradua��o
			if (discente.isGraduacao()) {
				DiscenteGraduacao grad = (DiscenteGraduacao) discenteDao.findByPK(discente.getId());
				
				// ter cursado pelo menos dois semestres letivos (8)
				CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente);
				discente.setSemestreAtual(discenteDao.calculaPeriodoAtualDiscente(grad, cal.getAno(), cal.getPeriodo()));
				if (discente.getSemestreAtual() < 3) {
					lista.addErro("O aluno deve ter cursado pelo menos dois semestres letivos na Institui��o ");
				}
				// reprovado por frequ�ncia no �ltimo semestre cursado (9)
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
					lista.addErro("Para se inscrever, o aluno n�o pode ter sido reprovado por freq��ncia, em qualquer disciplina, no �ltimo semestre regular");
				}

				if (discente.getStatus() == StatusDiscente.FORMANDO) {
					lista.addErro("Para se inscrever, o aluno n�o pode ser formando no semestre atual");
				}
				if (discente.getStatus() == StatusDiscente.GRADUANDO) {
					lista.addErro("Para se inscrever, o aluno n�o pode ser graduando no semestre atual");
				}
				// estar matriculado no semestre atual
				// verifica se pelo menos uma situa��o matr�cula n�o � trancada
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
					lista.addErro("Para ser fiscal, voc� n�o pode estar no final do curso de p�s-gradua��o");
				} 
			}

		} finally {
			discenteDao.close();
		}

	}

	/**
	 * Verifica se a Pessoa est� inscrito no Processo de Sele��o de Fiscais.
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
						"Voc� j� est� inscrito para a sele��o de fiscal. Seu n�mero de inscri��o �: "
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
	 * Valida se o candidato pode ou n�o alterar a sua foto.
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
	 * Verifica se a Pessoa est� inscrito no Processo de Seletivo como candidato.
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
						lista.addErro("O Fiscal n�o pode estar inscrito no Processo Seletivo.");
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
	 * Verifica os requisitos comuns a todas inscri��es:<br>
	 * <ul>
	 * <li>Per�odo de inscri��o.</li>
	 * <li>Se j� est� inscrito para sele��o de fiscais.</li>
	 * <li>Se n�o � candidato no processo seletivo.</li>
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
		// est� no per�odo de inscri��o pra fiscal?
		validaPeriodoInscricao(lista);
		// requisitos gerais
		isFiscalInscrito(pessoa, processoSeletivo, lista);
		isInscritoProcessoSeletivo(pessoa, processoSeletivo, lista);
	}

	/**
	 * Verifica se o Discente � tamb�m Servidor.
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
				lista.addErro("N�o foi poss�vel fazer a inscri��o pelo v�nculo de discente. Fa�a sua inscri��o para a sele��o de fiscal com seu v�nculo de servidor no SIGRH");
			}
		} catch (DAOException e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
	}

	/**
	 * Verifica se a inscri��o � recadastro.
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
