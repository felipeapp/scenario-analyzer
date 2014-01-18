/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Created on 22/09/2006
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.DefaultExtensionPointFactory;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.FrequenciaAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.dominio.DiarioClasse;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.RetificacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.jsf.GerarDiarioClasse;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacao;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacaoFactory;
import br.ufrn.sigaa.ensino.negocio.dominio.TurmaMov;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;

/**
 * Processador para realizar a consolidação uma turma de qualquer nível de ensino.
 *
 * @author David Ricardo
 *
 */
public class ProcessadorConsolidacaoTurma extends ProcessadorConsolidacao {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		validate(mov);
		TurmaMov tMov = (TurmaMov) mov;
		Usuario usuario = (Usuario) mov.getUsuarioLogado();
		Turma turma = tMov.getTurma();
		
		UsuarioDao uDao = getDAO(UsuarioDao.class, mov);
		TurmaDao tDao = getDAO(TurmaDao.class, mov);
		FrequenciaAlunoDao freqDao = getDAO(FrequenciaAlunoDao.class, mov);
		AvaliacaoDao avDao = getDAO(AvaliacaoDao.class, mov);
		MatriculaComponenteDao matriculaComponenteDao = getDAO(MatriculaComponenteDao.class, tMov);

		try {
			ParametrosGestoraAcademica param = getParametros(turma);
			EstrategiaConsolidacaoFactory factory = (EstrategiaConsolidacaoFactory) DefaultExtensionPointFactory.getImplementation(ParametrosGerais.IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY);
			EstrategiaConsolidacao estrategia = factory.getEstrategia(turma, param);
			boolean existemAlunosEmRecuperacao = false;
			
			if (!isEmpty(turma.getMatriculasDisciplina())) {
				
				//Valida as notas digitadas pelo usuário. As regras de validação são definidas na estratégia de consolidação
				estrategia.validaNotas(turma);
				
				//Registra qualquer alteração nas nota_unidade das matricula_componente da turma.
				TurmaHelper.criarAlteracoesNotasEmTurma(turma, tMov);
				

				for (MatriculaComponente matricula : turma.getMatriculasDisciplina()) {
					if (tMov.isParcial() && matricula.isEmRecuperacao()) {
						existemAlunosEmRecuperacao = true;
						continue;
					}
					
					matricula.setEstrategia(estrategia);
					matricula.setMetodoAvaliacao(param.getMetodoAvaliacao());
					
					if (!matricula.isConsolidada()) {
						// Salvar notas e avaliações
						if (!isEmpty(matricula.getNotas())) {
							for (NotaUnidade nota : matricula.getNotas()) {
								nota.setMatricula(matricula);
								tDao.update(nota);
	
								if (!isEmpty(nota.getAvaliacoes())) {
									for (Avaliacao aval : nota.getAvaliacoes()) {
										aval.setUnidade(nota);
										tDao.update(aval);
									}
								} else {
									nota.setAvaliacoes(null);
								}
							}
						}
	
						if (!matricula.isEmRecuperacao() && !matricula.isEad())
							matricula.setRecuperacao(null);
	
						// Consolidar a turma
						if (mov.getCodMovimento().equals(SigaaListaComando.CONSOLIDAR_TURMA)) {
							matricula.consolidar();
							tDao.updateField(MatriculaComponente.class, matricula.getId(), "situacaoMatricula", matricula.getSituacaoMatricula());
							tDao.updateField(MatriculaComponente.class, matricula.getId(), "usuarioConsolidacao", mov.getUsuarioLogado());
							tDao.updateField(MatriculaComponente.class, matricula.getId(), "dataConsolidacao", new Date());
							
							MatriculaComponenteHelper.criarApenasRegistroAlteracaoMatricula(matricula, matriculaComponenteDao, tMov, SituacaoMatricula.MATRICULADO);
							//Gerar retificações neste ponto, pois pode ocorrer consolidação parcial. 
							gerarRetificacoesReconsolidacaoTurma(tMov, turma, tDao);
							
							// Se a disciplina atual fizer parte de um bloco, processar o bloco completamente
							if (matricula.getTurma().getDisciplina().getBlocoSubUnidade() != null) {
								processarMatriculasBloco(mov, matricula);
							}
							
						}
	
						// Atualizar média final, número de faltas, apto, conceito e recuperação
						if ( !matricula.isEad() && matricula.isMetodoNota() )
							matricula.setMediaFinal(matricula.calculaMediaFinal());
						
						tDao.updateField(MatriculaComponente.class, matricula.getId(), "numeroFaltas", matricula.getNumeroFaltas());
						tDao.updateField(MatriculaComponente.class, matricula.getId(), "recuperacao", matricula.getRecuperacao());
						
						if (matricula.getMediaFinal() != null || matricula.isMetodoConceito())
							tDao.updateField(MatriculaComponente.class, matricula.getId(), "mediaFinal", matricula.getMediaFinal());
						if (matricula.getApto() != null)
							tDao.updateField(MatriculaComponente.class, matricula.getId(), "apto", matricula.getApto());

						if (mov.getCodMovimento().equals(SigaaListaComando.CONSOLIDAR_TURMA) && matricula.getDiscente().isStricto()) {
							tDao.updateField(MatriculaComponente.class, matricula.getId(), "anoFim", CalendarUtils.getAnoAtual());
							tDao.updateField(MatriculaComponente.class, matricula.getId(), "mesFim", CalendarUtils.getMesAtual1a12());
						}

						// Refazer os cálculos do discente na consolidação
						DiscenteAdapter discente = matricula.getDiscente();
						if (mov.getCodMovimento().equals(SigaaListaComando.CONSOLIDAR_TURMA) && ( discente.getNivel() == NivelEnsino.GRADUACAO || NivelEnsino.isAlgumNivelStricto(discente.getNivel()) ) ) {
							if (discente.getNivel() == NivelEnsino.GRADUACAO)
								tDao.updateField(DiscenteGraduacao.class, discente.getId(), "ultimaAtualizacaoTotais", null);
							else
								tDao.updateField(DiscenteStricto.class, discente.getId(), "ultimaAtualizacaoTotais", null);
						}
					}
				}
			}

			// Se está consolidando a turma definitivamente
			if ((usuario.getVinculoAtivo().isVinculoServidor() || usuario.getVinculoAtivo().isVinculoDocenteExterno()) && mov.getCodMovimento().equals(SigaaListaComando.CONSOLIDAR_TURMA) && 
					!tMov.isConsolidacaoIndividual() && (!tMov.isParcial() || (tMov.isParcial() && !existemAlunosEmRecuperacao))) {
					
					List<Turma> turmas = new ArrayList<Turma>();
					
					if (turma.isAgrupadora() || turma.getTurmaAgrupadora() != null){ 
						int turmasAbertas = tDao.countSubTurmasAbertas(turma.getTurmaAgrupadora());
						if (turma.isAgrupadora() || turmasAbertas == 1)  
							turmas = tDao.findTurmasByTurma(turma);
						else
							turmas.add(turma);
					}
					else
						turmas.add(turma);
			
				Turma turmaAgrupadora = null;
				List<Turma> subturmas = new ArrayList<Turma>();
				
				for (Turma t : turmas){
					t.consolidar();
					t.setUsuarioConsolidacao(usuario);
					t.setDataConsolidacao(new Date());
					
					//AlteracaoStatusTurma alteracao = AlteracaoStatusTurma.create(turma, (Usuario) mov.getUsuarioLogado());
					TurmaHelper.criarAlteracaoStatusTurma(t, tMov);
	
					//dao.create(alteracao);
					t.setHorarios(null);
					tDao.update(t);
					
					// Se for a agrupadora ou não tiver subturmas,
					if (t.isAgrupadora() || t.getTurmaAgrupadora() == null)
						turmaAgrupadora = t;
					else
						subturmas.add(t);
				}
				
				if (turmaAgrupadora != null){
					turmaAgrupadora.setSubturmas(subturmas);
					turma = turmaAgrupadora;
				}
				
				// Salva o diário de classe
				salvarDiarioClasse(turma, uDao, tDao, freqDao, avDao, param);
			}
		} finally {
			matriculaComponenteDao.close();
			tDao.close();
			uDao.close();
			freqDao.close();
			avDao.close();
		}

		return turma;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		TurmaMov tMov = (TurmaMov) mov;
		Turma turma = tMov.getTurma();

		Usuario usuario = (Usuario) mov.getUsuarioLogado();
		
		// Se o usuário não for docente ou docente externo e estiver tentando consolidar a turma, não pode.
		if (!usuario.getVinculoAtivo().isVinculoServidor() && !usuario.getVinculoAtivo().isVinculoDocenteExterno() && mov.getCodMovimento().equals(SigaaListaComando.CONSOLIDAR_TURMA) && !tMov.isConsolidacaoIndividual())
			throw new NegocioException("Apenas docentes podem consolidar uma turma.");
		
		if (isEmpty(turma)) 
			throw new NegocioException("Não existe um processo de consolidação ativo.");
		
		// O Administrador DAE pode consolidar qualquer turma sem ter que realizar a avaliação institucional.
		boolean administradorDAE = tMov.getUsuarioLogado().isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE);
		
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, mov);
		
		try {
			if (mov.getCodMovimento().equals(SigaaListaComando.CONSOLIDAR_TURMA) && !tMov.isConsolidacaoIndividual() && !turma.isMigradoGraduacao()) {
				ConsolidacaoValidator.validar(dao, turma, getParametros(turma), tMov.getMetodoAvaliacao(), mov.getCodMovimento(), administradorDAE, tMov.isParcial(), tMov.getConfig(), usuario);
			}
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Encontra os parâmetros acadêmicos para a turma passada
	 */
	private ParametrosGestoraAcademica getParametros(Turma turma) throws DAOException {
		char nivel = turma.getDisciplina().getNivel();
		ParametrosGestoraAcademica param = null;
		
		if (nivel == NivelEnsino.GRADUACAO) {
			param = ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalGraduacao();
		} else {
			param = ParametrosGestoraAcademicaHelper.getParametros(turma);
		}
		return param;
	}

	/**
	 * Persiste as informações do diário de classe da turma após a sua consolidação.
	 */
	private void salvarDiarioClasse(Turma turma, UsuarioDao uDao, TurmaDao tDao, FrequenciaAlunoDao freqDao, AvaliacaoDao avDao, ParametrosGestoraAcademica param) throws ArqException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream ();
			
			int unidade = 0;
			
			if (turma.getDisciplina().getNivel() == NivelEnsino.GRADUACAO)
				unidade = UnidadeGeral.UNIDADE_DIREITO_GLOBAL;
			else
				unidade = turma.getDisciplina().getUnidade().getId();
	
			CalendarioAcademico calendarioVigente;
			
			if ( turma.isLato() )
				calendarioVigente = CalendarioAcademicoHelper.getCalendario(null, null, new Unidade(Unidade.UNIDADE_DIREITO_GLOBAL), NivelEnsino.LATO, null, null, null);
			else
				calendarioVigente = CalendarioAcademicoHelper.getCalendario(turma, new br.ufrn.sigaa.dominio.Unidade(unidade));
			
			GerarDiarioClasse gdc = new GerarDiarioClasse(turma, calendarioVigente, param, tDao, freqDao, avDao, uDao, false);
			gdc.gerar(baos);
			
			int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
			
			EnvioArquivoHelper.inserirArquivo(idArquivo, baos.toByteArray(), "application/pdf", "diario_"+turma.getDisciplina().getCodigo()+"_"+turma.getAnoPeriodo()+"_"+turma.getCodigo()+"_.pdf");
	
			DiarioClasse dc = new DiarioClasse();
			dc.setTurma(turma);
			dc.setIdArquivo(idArquivo);
			dc.setCodigoHash(UFRNUtils.toSHA1Digest(gdc.getStringCodigo()).substring(0,10));
			
			tDao.create(dc);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new ArqException(e);
		}
	}
	
	/**
	 * Gera as retificações da reconsolidação de turma.
	 * 
	 * @param tMov
	 * @param turma
	 * @param tDao
	 * @throws DAOException
	 */
	private void gerarRetificacoesReconsolidacaoTurma(TurmaMov tMov, Turma turma, TurmaDao tDao ) throws DAOException {		
		Collection<RetificacaoMatricula> retificacoesReconsolidacao = tMov.getRetificacoesReconsolidacao();
		if(TurmaHelper.isReconsolidacaoTurma(turma) && !ValidatorUtil.isEmpty(retificacoesReconsolidacao)) {
			for(RetificacaoMatricula retificacao : retificacoesReconsolidacao) {
				retificacao.setData(new Date());
				retificacao.setRegistroEntrada(tMov.getUsuarioLogado().getRegistroEntrada());
				tDao.create(retificacao);
			}
		}
		tMov.setRetificacoesReconsolidacao(new ArrayList<RetificacaoMatricula>());
	}
}
