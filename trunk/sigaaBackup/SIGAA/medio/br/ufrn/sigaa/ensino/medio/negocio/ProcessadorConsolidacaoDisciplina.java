/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 12/08/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.DefaultExtensionPointFactory;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.FrequenciaAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.dominio.DiarioClasse;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.RetificacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.medio.dominio.NotaDisciplina;
import br.ufrn.sigaa.ensino.medio.dominio.NotaSerie;
import br.ufrn.sigaa.ensino.medio.jsf.GerarDiarioClasseMedio;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.MatriculaComponenteHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.negocio.TurmaHelper;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacao;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacaoFactory;
import br.ufrn.sigaa.ensino.negocio.dominio.TurmaMov;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;

/**
 * Processador com métodos comuns a consolidação de disciplinas do ensino médio.
 * 
 * @author Rafael Gomes
 *
 */
public class ProcessadorConsolidacaoDisciplina extends AbstractProcessador{

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);
		TurmaMov tMov = (TurmaMov) mov;
		Usuario usuario = (Usuario) mov.getUsuarioLogado();
		Turma turma = tMov.getTurma();
		@SuppressWarnings("unchecked")
		List<NotaDisciplina> listaNotasDisciplina = (List<NotaDisciplina>) tMov.getObjAuxiliar();
		
		UsuarioDao uDao = getDAO(UsuarioDao.class, mov);
		TurmaDao tDao = getDAO(TurmaDao.class, mov);
		FrequenciaAlunoDao freqDao = getDAO(FrequenciaAlunoDao.class, mov);
		AvaliacaoDao avDao = getDAO(AvaliacaoDao.class, mov);
		MatriculaComponenteDao matriculaComponenteDao = getDAO(MatriculaComponenteDao.class, tMov);
		
		try {
			ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(turma);
			EstrategiaConsolidacaoFactory factory = (EstrategiaConsolidacaoFactory) DefaultExtensionPointFactory.getImplementation(ParametrosGerais.IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY);
			EstrategiaConsolidacao estrategia = factory.getEstrategia(turma, param);
			
			boolean existemAlunosEmRecuperacao = false;
			
			if (!isEmpty(turma.getMatriculasDisciplina())) {
				
				//Valida as notas digitadas pelo usuário. As regras de validação são definidas na estratégia de consolidação
				estrategia.validaNotas(turma);
				
				//Registra qualquer alteração nas nota_unidade das matricula_componente da turma.
				TurmaHelper.criarAlteracoesNotasEmTurma(turma, tMov);
			
				for (NotaDisciplina notasMatricula : listaNotasDisciplina) {
					
					MatriculaComponente matricula = notasMatricula.getMatricula();
					
					if (tMov.isParcial() && matricula.isEmRecuperacao()) {
						existemAlunosEmRecuperacao = true;
						continue;
					}
					
					matricula.setEstrategia(estrategia);
					matricula.setMetodoAvaliacao(param.getMetodoAvaliacao());
					
					if (!matricula.isConsolidada()) {
						// Salvar notas e avaliações
						// Pegando as notas da tela de consolidação, inseridas em NotaSerie					
						if (!isEmpty(notasMatricula.getNotas())) {
							
							for (NotaSerie nota : notasMatricula.getNotas()) {
								nota.getNotaUnidade().setMatricula(matricula);
								nota.getNotaUnidade().setRecuperacao(nota.getRegraNota().isRecuperacao());
								nota.getNotaUnidade().setUnidade( Byte.valueOf(nota.getRegraNota().getOrdem().toString()));
								if (nota.getId() > 0){
									tDao.updateNoFlush(nota.getNotaUnidade());
								} else {
									tDao.createNoFlush(nota.getNotaUnidade());
									tDao.createNoFlush(nota);
								}	

								if (!isEmpty(nota.getNotaUnidade().getAvaliacoes())) {
									for (Avaliacao aval : nota.getNotaUnidade().getAvaliacoes()) {
										aval.setUnidade(nota.getNotaUnidade());
										tDao.updateNoFlush(aval);
									}
								} else {
									nota.getNotaUnidade().setAvaliacoes(null);
								}
							}
						}
	
						if (!matricula.isEmRecuperacao() && !matricula.isEad())
							matricula.setRecuperacao(null);
	
						// Consolidar a turma
						if (mov.getCodMovimento().equals(SigaaListaComando.CONSOLIDAR_DISCIPLINA_MEDIO)) {
							matricula.consolidar();
							if (!tMov.isParcial() || matricula.isAprovado() || (matricula.isReprovado() && !matricula.isEmRecuperacao()) ) {
								tDao.updateField(MatriculaComponente.class, matricula.getId(), "situacaoMatricula", matricula.getSituacaoMatricula());
								tDao.updateField(MatriculaComponente.class, matricula.getId(), "usuarioConsolidacao", mov.getUsuarioLogado());
								tDao.updateField(MatriculaComponente.class, matricula.getId(), "dataConsolidacao", new Date());
								
								MatriculaComponenteHelper.criarApenasRegistroAlteracaoMatricula(matricula, matriculaComponenteDao, tMov, SituacaoMatricula.MATRICULADO);
								//Gerar retificações neste ponto, pois pode ocorrer consolidação parcial. 
								gerarRetificacoesReconsolidacaoTurma(tMov, turma, tDao);
						}	
							
						}
	
						// Atualizar média final, número de faltas, apto, conceito e recuperação
						if ( matricula.isMetodoNota() )
							matricula.setMediaFinal(matricula.calculaMediaFinal());
						
						tDao.updateField(MatriculaComponente.class, matricula.getId(), "numeroFaltas", matricula.getNumeroFaltas());
						tDao.updateField(MatriculaComponente.class, matricula.getId(), "recuperacao", matricula.getRecuperacao());
						
						if (matricula.getMediaFinal() != null || matricula.isMetodoConceito())
							tDao.updateField(MatriculaComponente.class, matricula.getId(), "mediaFinal", matricula.getMediaFinal());
						if (matricula.getApto() != null)
							tDao.updateField(MatriculaComponente.class, matricula.getId(), "apto", matricula.getApto());

						if (mov.getCodMovimento().equals(SigaaListaComando.CONSOLIDAR_DISCIPLINA_MEDIO) && matricula.getDiscente().isStricto()) {
							tDao.updateField(MatriculaComponente.class, matricula.getId(), "anoFim", CalendarUtils.getAnoAtual());
							tDao.updateField(MatriculaComponente.class, matricula.getId(), "mesFim", CalendarUtils.getMesAtual());
						}

					}
					
				}
			}	
			// Se está consolidando a turma definitivamente
			if ((usuario.getVinculoAtivo().isVinculoServidor() || usuario.getVinculoAtivo().isVinculoDocenteExterno()) && mov.getCodMovimento().equals(SigaaListaComando.CONSOLIDAR_DISCIPLINA_MEDIO) && 
					!tMov.isConsolidacaoIndividual() && (!tMov.isParcial() || (tMov.isParcial() && !existemAlunosEmRecuperacao))) {
				List<Turma> turmas = new ArrayList<Turma>();
				
				if (turma.isAgrupadora() || turma.getTurmaAgrupadora() != null)
					turmas = tDao.findTurmasByTurma(turma);
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
					tDao.updateNoFlush(t);
					
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
				salvarDiarioClasse(turma, tDao, param);
			}	
		} finally {
			uDao.close();
			tDao.close();
			freqDao.close();
			avDao.close();
			matriculaComponenteDao.close();
			
		}
		return turma;
	}
	
	/**
	 * Persiste as informações do diário de classe da turma após a sua consolidação.
	 */
	private void salvarDiarioClasse(Turma turma, TurmaDao tDao, ParametrosGestoraAcademica param) throws ArqException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream ();
			
			CalendarioAcademico calendarioVigente = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalMedio();
			
			GerarDiarioClasseMedio gdc = new GerarDiarioClasseMedio(turma, calendarioVigente, param);
			gdc.gerar(baos);
			
			int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
			
			EnvioArquivoHelper.inserirArquivo(idArquivo, baos.toByteArray(), "application/pdf", "diario_"+turma.getDisciplina().getCodigo()+"_"+turma.getAnoPeriodo()+"_"+turma.getCodigo()+"_.pdf");
	
			DiarioClasse dc = new DiarioClasse();
			dc.setTurma(turma);
			dc.setIdArquivo(idArquivo);
			dc.setCodigoHash(UFRNUtils.toSHA1Digest(gdc.getStringCodigo()).substring(0,10));
			
			tDao.createNoFlush(dc);
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
				tDao.createNoFlush(retificacao);
			}
		}
		tMov.setRetificacoesReconsolidacao(new ArrayList<RetificacaoMatricula>());
	}	
		
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

}
