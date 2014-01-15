/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/11/2009
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MobilidadeEstudantilDao;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.BancaPosDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.HomologacaoTrabalhoFinalDao;
import br.ufrn.sigaa.arq.dao.graduacao.IndiceAcademicoDao;
import br.ufrn.sigaa.arq.dao.prodocente.TrabalhoFimCursoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.complexohospitalar.dominio.DiscenteResidenciaMedica;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Historico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MobilidadeEstudantil;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.TipoTrabalhoConclusao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.GrupoOptativas;
import br.ufrn.sigaa.ensino.graduacao.dominio.ParticipacaoEnade;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoGenerico;
import br.ufrn.sigaa.ensino.graduacao.negocio.CurriculoHelper;
import br.ufrn.sigaa.ensino.graduacao.negocio.ProcessadorCalculosDiscente;
import br.ufrn.sigaa.ensino.graduacao.negocio.calculos.IntegralizacoesHelper;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.DiscenteLato;
import br.ufrn.sigaa.ensino.metropoledigital.negocio.MetropoleDigitalHelper;
import br.ufrn.sigaa.ensino.negocio.consolidacao.ConsolidacaoStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.AproveitamentoCredito;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.HomologacaoTrabalhoFinal;
import br.ufrn.sigaa.ensino.stricto.negocio.ProcessadorCalculosDiscenteStricto;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.negocio.ProcessadorCalculosDiscenteTecnico;
import br.ufrn.sigaa.negocio.MovimentoCalculoHistorico;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.prodocente.atividades.dominio.TrabalhoFimCurso;

/** 
 * Processador responsável pelo cálculo do histórico de um discente.
 * @author Édipo Elder F. Melo
 *
 */
public class ProcessadorCalculaHistorico extends AbstractProcessador {

	/** Executa o cálculo do histórico do discente.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {
		return calculaHistorico(mov);
	}
	
	/** Calcula o histórico de um discente.
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException 
	 * @throws RemoteException
	 */
	public Historico calculaHistorico(Movimento mov) throws NegocioException, ArqException, RemoteException {
		DiscenteDao discenteDao = getDAO(DiscenteDao.class, mov);
		MovimentacaoAlunoDao movDao = getDAO(MovimentacaoAlunoDao.class, mov);
		HomologacaoTrabalhoFinalDao homologacaoDao = getDAO(HomologacaoTrabalhoFinalDao.class, mov);
		TrabalhoFimCursoDao trabalhoFimDao = getDAO(TrabalhoFimCursoDao.class, mov);
		
		Historico historico = new Historico();
		DiscenteAdapter discente = ((MovimentoCalculoHistorico) mov).getDiscente();
		boolean recalcula = ((MovimentoCalculoHistorico) mov).isRecalculaCurriculo();
		
		try {

			discente = discenteDao.findByPK(discente.getId());
			if (!isEmpty(discente.getDiscente().getIndices()))
				discente.getDiscente().getIndices().iterator();
			
			if (discente.isGraduacao()) {
				DiscenteGraduacao grad = (DiscenteGraduacao) discente;
				if (recalcula) {
					processarReCalculoCurriculo(grad, mov);
				}
				
				TrabalhoFimCurso trabalhoFinal = trabalhoFimDao.findByOrientando(grad);
				if(trabalhoFinal != null){
					trabalhoFinal.getOrientadorNome();
					trabalhoFinal.getTitulo();
					if(trabalhoFinal.getTipoTrabalhoConclusao() == null)
						trabalhoFinal.setTipoTrabalhoConclusao(new TipoTrabalhoConclusao(13));
					trabalhoFinal.getTipoTrabalhoConclusao().getDescricao();
					grad.setTrabalhoConclusaoCurso(trabalhoFinal);
				}
				
				grad.setAntecipacoes(movDao.countAntecipacoesByDiscente(discente));
				
				discenteDao.detach(grad);
			}
			
			if (discente.isStricto()) {
				DiscenteStricto stricto = (DiscenteStricto) discente;
				if (recalcula) {
					processarReCalculoCurriculo(stricto, mov);
				}
				float media = (float) getDAO(IndiceAcademicoDao.class, mov).calculaIraDiscenteStricto(discente.getId());
				stricto.setMediaGeral(media);
				// se o discente tiver concluído, seta a homologação
				if (stricto.isConcluido()) {
					HomologacaoTrabalhoFinal homologacao = homologacaoDao.findUltimoByDiscente(stricto.getId());
					stricto.setHomologacaoTrabalhoFinal(homologacao);
				}
			}
					
			if (discente.isTecnico() || discente.isFormacaoComplementar()) {
				DiscenteTecnico tecnico = (DiscenteTecnico) discente;
				if (recalcula) {
					processarReCalculoCurriculo(tecnico, mov);
				}
				float media = (float) getDAO(IndiceAcademicoDao.class, mov).calculaIraDiscente(discente.getId());
				tecnico.setMediaGeral(media);
				tecnico.getEstruturaCurricularTecnica().getCodigo();
				tecnico.getEstruturaCurricularTecnica().getAnoEntradaVigor();
				tecnico.getEstruturaCurricularTecnica().getPeriodoEntradaVigor();
				if(tecnico.getTurmaEntradaTecnico().getEspecializacao() != null)
					tecnico.getTurmaEntradaTecnico().getEspecializacao().getDescricao();
			}
			
			if (discente.isLato()) {
				DiscenteLato dl = (DiscenteLato) discente;
				float media = (float) getDAO(IndiceAcademicoDao.class, mov).calculaIraDiscente(discente.getId());
				dl.setMediaGeral(media);
		
				CursoLato cursoLato = (CursoLato) discente.getCurso();
				dl.setMetodoAvaliacao(cursoLato.getPropostaCurso().getMetodoAvaliacao()); 
				
				TrabalhoFimCurso trabalhoFinal = trabalhoFimDao.findByOrientando(discente);
				if(trabalhoFinal != null){
					trabalhoFinal.getOrientadorNome();
					trabalhoFinal.getTipoTrabalhoConclusao().getDescricao();
					dl.setTrabalhoFinal(trabalhoFinal);
				}
			}
			
			if(discente.isResidencia()){
				DiscenteResidenciaMedica residente = (DiscenteResidenciaMedica) discente;
				float media = (float) getDAO(IndiceAcademicoDao.class, mov).calculaMediaGeralDiscente(discente.getId());
				residente.setMediaGeral(media);
			}
		
			historico.setObservacoesDiscente(discenteDao.findObservacoesDiscente(discente));
	
			Curso curso = null;
			
			if (discente.getCurso() == null && discente.getTipo() == Discente.ESPECIAL) {
				curso = new Curso();
				curso.setNome("ALUNO ESPECIAL");
				curso.setUnidade(null);
			} else if (discente.getCurso() == null && discente.isRegular()) {
				curso = new Curso();
				curso.setNome("SEM CURSO");
				curso.setUnidade(null);
			} else if (discente.getCurso() == null && discente.getTipo() == Discente.EM_ASSOCIACAO) {
					curso = new Curso();
					curso.setNome("ALUNO EM ASSOCIAÇÂO (REDE)");
					curso.setUnidade(null);	
			} else {
				curso = discenteDao.findByPrimaryKey(discente.getCurso().getId(), Curso.class);
			}
		
			curso.getDescricaoCompleta();
		
			discente.setCurso(curso);
			
			historico.setDiscente(discente);
			
			if(curso.getId() > 0)
				carregarDadosReconhecimento(historico, mov);
		
			if (!StatusDiscente.getStatusComVinculo().contains(discente.getStatus())) {
				if (discente.isConcluido()) {
					discente.setMovimentacaoSaida(movDao.findConclusaoByDiscente(discente.getId()));
				}
		
				if (discente.getMovimentacaoSaida() == null) {
					discente.setMovimentacaoSaida(movDao.findUltimoAfastamentoByDiscente(discente.getId(), true, false));
				}
			}
			
			if (discente.isTecnico() || discente.isFormacaoComplementar()) {
				discente.setProrrogacoes(movDao
						.countProrrogacoesByDiscenteTecnico(discente));
			} else {
				discente.setProrrogacoes(movDao
					.countProrrogacoesByDiscente(discente));
			}
			
			historico.setDataHistorico(new Date());
		
			List<MatriculaComponente> disciplinas = discenteDao.findDisciplinasConcluidasMatriculadas(discente.getId(), true, !MetropoleDigitalHelper.isMetropoleDigital(discente));
			
			if (discente.isLato()) {
				CursoLato cursoLato = (CursoLato) discente.getCurso();
				int metodoAvaliacao = cursoLato.getPropostaCurso().getMetodoAvaliacao();
				for (MatriculaComponente mc : disciplinas) {
					mc.setMetodoAvaliacao(metodoAvaliacao);
				}
			}
			
			
			if (discente.isStricto()) {				
				for (MatriculaComponente mc : disciplinas) {
					mc.setEstrategia(new ConsolidacaoStricto());
					ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(mc.getTurma(), new Unidade(mc.getComponente().getUnidade().getGestoraAcademica().getId()));
					mc.setMetodoAvaliacao(param.getMetodoAvaliacao());
				}
			}
			
			if(discente.isGraduacao()){
				// trata as reprovações de sub-unidades do bloco
				List<MatriculaComponente> blocosReprovados = new ArrayList<MatriculaComponente>();
				for (MatriculaComponente matricula : disciplinas) {
					// se for um bloco e tiver reprovado
					if (matricula.getComponente().isBloco() && 
							SituacaoMatricula.getSituacoesReprovadas().contains(matricula.getSituacaoMatricula())) {
						blocosReprovados.add(matricula);
					}
				}
				
				// remove as matrículas dos componentes da subunidade
				for (MatriculaComponente matriculaBloco : blocosReprovados) {
					ComponenteCurricular bloco = discenteDao.refresh(matriculaBloco.getComponente());
					for (ComponenteCurricular subUnidade : bloco.getSubUnidades()) {
						Iterator<MatriculaComponente> matriculaIterator = disciplinas.iterator();
						while (matriculaIterator.hasNext()) {
							MatriculaComponente matricula = matriculaIterator.next();
							if (matricula.getComponente().equals(subUnidade)
									&& matricula.getAno().shortValue() == matriculaBloco.getAno().shortValue() 
									&& matricula.getPeriodo().shortValue() == matriculaBloco.getPeriodo().shortValue()) {
								matriculaIterator.remove();
							}
						}
					}
				}
			}
			
			historico.setMatriculasDiscente(disciplinas);
			// Carrega as disciplinas que estão na estrutura curricular do aluno
			// e que ainda não foram pagas
			Collection<ComponenteCurricular> componentesPendentes = new ArrayList<ComponenteCurricular>();
			List<TipoGenerico> equivalenciasDiscente = new ArrayList<TipoGenerico>();
			componentesPendentes = verificarComponentesPendentes(mov, discenteDao, discente, curso, disciplinas, componentesPendentes, equivalenciasDiscente);
			
			historico.setDisciplinasPendentesDiscente(componentesPendentes);
			historico.setEquivalenciasDiscente(equivalenciasDiscente);
		
				// MOBILIDADE ESTUDANTIL DO ALUNO.
			if (discente.isGraduacao())
				historico.setMobilidadeEstudantil(mobilidadeEstudantil(discente, mov));
				
			if(curso.getId() > 0)
				historico.setTrancamentos(discenteDao.findTrancamentosByDiscente(discente));
		
			if (historico.isGraduacao()) {
				carregarDadosDiscenteGraduacao(historico, mov);
			} else if (historico.isStricto()) {
				if(mov.getSistema() == 0)
					mov.setSistema(Sistema.SIGAA);				
				carregarDadosDiscenteStricto(historico, mov);
			}
			else if (historico.isResidencia() ) {
				carregarDadosDiscenteResidenciaMedica(historico, mov);
			}
			
			if (discente.getTipo() == Discente.ESPECIAL) 
				discente.setCurso(null);
			
			// evita erro de NullPointerException ou LazyException ao tentar acessar alguns dados pessoais do discente, no Jasper
			historico.getDiscente().setPessoa(discenteDao.refresh(discente.getPessoa()));
			historico.getDiscente().getPessoa().prepararDados();
			historico.getDiscente().getPessoa().getPais().getNome();
			historico.getDiscente().getPessoa().getMunicipioUf();
			if (historico.getDiscente().getPessoa().getEnderecoContato() != null) {
				historico.getDiscente().getPessoa().getEnderecoContato().getDescricao();
				if (historico.getDiscente().getPessoa().getEnderecoContato().getMunicipio() != null && historico.getDiscente().getPessoa().getEnderecoContato().getMunicipio().getId() != 0)
					historico.getDiscente().getPessoa().getEnderecoContato().getMunicipio().getNomeUF();
					
			}
			if (historico.getDiscente().isGraduacao() && ((DiscenteGraduacao) historico.getDiscente()).getPolo() != null) {
				((DiscenteGraduacao) historico.getDiscente()).getPolo().getDescricao();
			}
			
		} finally {
			discenteDao.close();
			movDao.close();
			homologacaoDao.close();
			trabalhoFimDao.close();
		}
		
		return historico;
	}

	/**
	 * Realiza a verificação se o discente possui componentes pendentes
	 * 
	 * @param mov
	 * @param discenteDao
	 * @param discente
	 * @param curso
	 * @param disciplinas
	 * @param componentesPendentes
	 * @param equivalenciasDiscente
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public Collection<ComponenteCurricular> verificarComponentesPendentes(Movimento mov, DiscenteDao discenteDao, DiscenteAdapter discente, Curso curso, List<MatriculaComponente> disciplinas, 
			Collection<ComponenteCurricular> componentesPendentes, List<TipoGenerico> equivalenciasDiscente) throws DAOException, ArqException {
		// Se o discente estiver concluído não busca por componentes pendentes
		if (!discente.isConcluido()) {
			if (discente.isTecnico() || discente.isFormacaoComplementar())
				componentesPendentes = discenteDao.findComponentesPendentesTecnico(discente.getId(), disciplinas);
			else if (discente.isLato()) 
				componentesPendentes = new ArrayList<ComponenteCurricular>();
			else if (curso.getId() > 0) {
				componentesPendentes = analisarComponentesPendentesObrigatorios(discenteDao, discente, disciplinas, equivalenciasDiscente);
				analisarComponentesEquivalenciaOptativas(discenteDao, discente, disciplinas, equivalenciasDiscente);
			}
		}

		if (discente.isGraduacao()) {
			DiscenteGraduacao grad = (DiscenteGraduacao) discente;
			List<MatriculaComponente> concluidas = discenteDao.findDisciplinasConcluidasMatriculadas(discente.getId(), true);
			Map<GrupoOptativas, Integer> grupos = IntegralizacoesHelper.verificaGruposOptativas(grad, SituacaoMatricula.getSituacoesPagas(), concluidas, mov);
			for (Entry<GrupoOptativas, Integer> entry : grupos.entrySet()) {
				if (entry.getValue() > 0) {
					ComponenteCurricular cc = new ComponenteCurricular();
					cc.setNome("GRUPO DE OPTATIVAS - " + entry.getKey().getDescricao() + " (CH Mínima: " + entry.getKey().getChMinima() + " h)");
					cc.getDetalhes().setCodigo("-");
					cc.setChTotal(entry.getValue());
					componentesPendentes.add(cc);
				}
			}
			// ENADE -> É listado no Histórico como Componente Pendente
			participacaoEnade(grad, disciplinas, componentesPendentes);
		}
		
		if (isEmpty(componentesPendentes))
			componentesPendentes = Collections.emptyList();
		return componentesPendentes;
	}

	/**
	 * Retorna os componentes obrigatorios pendetes e faz análise textual das equivalências
	 * 
	 * @param discenteDao
	 * @param discente
	 * @param disciplinas
	 * @param equivalenciasDiscente
	 * @throws DAOException
	 */
	private void analisarComponentesEquivalenciaOptativas(DiscenteDao discenteDao, DiscenteAdapter discente, List<MatriculaComponente> disciplinas, List<TipoGenerico> equivalenciasDiscente) throws DAOException {
		Collection<ComponenteCurricular> componentesParaAnalise = discenteDao.findComponentesDaGradeQueDiscenteNaoPagou(discente.getId(), disciplinas, false);
		IntegralizacoesHelper.analisarEquivalenciasPorExtenso(discente.getId(), disciplinas, equivalenciasDiscente, componentesParaAnalise);
	}

	/**
	 * Faz análise textual das equivalências
	 * 
	 * @param discenteDao
	 * @param discente
	 * @param disciplinas
	 * @param equivalenciasDiscente
	 * @return
	 * @throws DAOException
	 */
	private Collection<ComponenteCurricular> analisarComponentesPendentesObrigatorios(DiscenteDao discenteDao, DiscenteAdapter discente, List<MatriculaComponente> disciplinas, List<TipoGenerico> equivalenciasDiscente) throws DAOException {
		Collection<ComponenteCurricular> componentesPendentes = discenteDao.findByDisciplinasCurricularesPendentes(discente.getId(), disciplinas, equivalenciasDiscente);
		return componentesPendentes;
	}

	/** Insere no histórico do discente a participação no ENADE.
	 * @param grad
	 * @param pagas
	 * @param componentesPendentes
	 * @throws DAOException
	 */
	public void participacaoEnade(DiscenteGraduacao grad, List<MatriculaComponente> pagas, Collection<ComponenteCurricular> componentesPendentes) throws DAOException {
		// caso o ingressante e concluinte sejam nulos e o discente tiver concluído, não inclui informação no histórico
		if (isEmpty(grad.getParticipacaoEnadeIngressante()) 
				&& isEmpty(grad.getParticipacaoEnadeConcluinte())
				&& (grad.isConcluido() || grad.getDiscente().isCancelado()))
			return;
		// ingressante
		boolean pendente;
		if (grad.getAnoIngresso() >= ParticipacaoEnade.ANO_INICIO_ENADE_INGRESSANTE) {
			pendente = grad.getParticipacaoEnadeIngressante() == null || grad.getParticipacaoEnadeIngressante().isParticipacaoPendente();
			ComponenteCurricular ingressante = new ComponenteCurricular();
			if (isEmpty(grad.getParticipacaoEnadeIngressante()))
				ingressante.setNome("ENADE INGRESSANTE PENDENTE");
			else {
				StringBuilder str = new StringBuilder(
						grad .getParticipacaoEnadeIngressante().getDescricao());
				if (!isEmpty(grad.getDataProvaEnadeIngressante())) 
						str.append(". DATA DA PROVA: ")
						.append(Formatador.getInstance().formatarData(
							grad.getDataProvaEnadeIngressante()));
				ingressante.setNome(str.toString());
			}
			ingressante.setCodigo("ENADE");
			ingressante.getDetalhes().setCodigo("ENADE");
			if (pendente) {
				componentesPendentes.add(ingressante);
			} else {
				MatriculaComponente mc = new MatriculaComponente();
				mc.setAno(grad.getAnoIngresso().shortValue());
				mc.setPeriodo(grad.getPeriodoIngresso().byteValue());
				mc.setComponente(ingressante);
				mc.setSituacaoMatricula(new SituacaoMatricula("--"));
				pagas.add(0, mc);
			}
		}
		// concluinte
		if (grad.getDataColacaoGrau() == null || CalendarUtils.getAno(grad.getDataColacaoGrau()) >= ParticipacaoEnade.ANO_INICIO_ENADE_CONCLUINTE) {
			pendente = grad.getParticipacaoEnadeConcluinte() == null || grad.getParticipacaoEnadeConcluinte().isParticipacaoPendente();
			ComponenteCurricular concluinte = new ComponenteCurricular();
			if (isEmpty(grad.getParticipacaoEnadeConcluinte()))
				concluinte.setNome("ENADE CONCLUINTE PENDENTE");
			else {
				StringBuilder str = new StringBuilder(
						grad .getParticipacaoEnadeConcluinte().getDescricao());
				if (!isEmpty(grad.getDataProvaEnadeConcluinte())) 
						str.append(". DATA DA PROVA: ")
						.append(Formatador.getInstance().formatarData(
							grad.getDataProvaEnadeConcluinte()));
				concluinte.setNome(str.toString());
			}
			concluinte.setCodigo("ENADE");
			concluinte.getDetalhes().setCodigo("ENADE");
			if (pendente) {
				componentesPendentes.add(concluinte);
			} else {
				int ano = grad.getDataProvaEnadeConcluinte() != null ? CalendarUtils.getAno(grad.getDataProvaEnadeConcluinte()) : ( grad.getDataColacaoGrau() != null ? CalendarUtils.getAno(grad.getDataColacaoGrau()) : 0 );
				int periodo = grad.getDataProvaEnadeConcluinte() != null ? CalendarUtils.getMesByData(grad.getDataProvaEnadeConcluinte()) / 7 : ( grad.getDataColacaoGrau() != null ? CalendarUtils.getMesByData(grad.getDataColacaoGrau()) / 7 : 0 );
				MatriculaComponente mc = new MatriculaComponente();
				if (ano > 0)
					mc.setAno((short) ano);
				if (periodo > 0)
					mc.setPeriodo((byte) periodo);
				mc.setComponente(concluinte);
				mc.setSituacaoMatricula(new SituacaoMatricula("--"));
				pagas.add(mc);
			}
		}
	}

	/** Insere no histórico a mobilidade estudantil do discente .
	 * @param discente
	 * @param mov
	 * @throws DAOException
	 */
	public List<TipoGenerico> mobilidadeEstudantil(DiscenteAdapter discente, Movimento mov) throws DAOException {
		MobilidadeEstudantilDao mobilidadeDao = getDAO(MobilidadeEstudantilDao.class, mov);
		try {
			List<TipoGenerico> mobilidadeEstudantil = new ArrayList<TipoGenerico>();
			List<MobilidadeEstudantil> listaMobilidade = mobilidadeDao.findByDiscente(discente,true);
			for (MobilidadeEstudantil mobilidade : listaMobilidade){
				StringBuilder sb = new StringBuilder();
				sb.append("Foi autorizado a Mobilidade Estudantil do tipo "+mobilidade.getDescricaoTipo()+
						", "+mobilidade.getDescricoesSubTipo()+", "+(mobilidade.isInterna()?" no Campus "+
								mobilidade.getCampusDestino().getNome():" na Instituição de Ensino Superior "+mobilidade.getIesExterna()+
								(mobilidade.getSubtipo() == MobilidadeEstudantil.INTERNACIONAL?", localizada no(a) "+mobilidade.getPaisExterna().getNome()+" na Cidade de "+mobilidade.getCidade():""))+
						", durante o(s) Período(s) Letivo(s) de "+mobilidade.getAno()+"."+mobilidade.getPeriodo()); 
				
				if (mobilidade.getNumeroPeriodos() > 1){
					sb.append(" à ");
					StringBuilder periodoFinal = new StringBuilder();
					periodoFinal.append(DiscenteHelper.somaSemestres(mobilidade.getAno(), mobilidade.getPeriodo(), mobilidade.getNumeroPeriodos() - 1));
					periodoFinal.insert(4, ".");
					sb.append(periodoFinal.toString());
				}
				sb.append(".");
				
				sb.append("\r Observação: "+mobilidade.getObservacao());
				mobilidadeEstudantil.add(new TipoGenerico(sb.toString()));
			}
			return mobilidadeEstudantil;
		} finally {
			mobilidadeDao.close();
		}
	}
	
	/** Valida os dados do processamento.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

	/**
	 * Carrega informações específicas dos discentes de graduação. 
	 * Período atual, polo, trabalho de conclusão e outros.
	 * 
	 * @param historico
	 * @throws DAOException
	 */
	private void carregarDadosDiscenteGraduacao(Historico historico, Movimento mov) throws DAOException {
		DiscenteDao discenteDao = getDAO(DiscenteDao.class, mov);
		Discente discente = historico.getDiscente().getDiscente();
		DiscenteGraduacao dg = (DiscenteGraduacao) discenteDao.findByPK(discente.getId());
		
		try {
			if (dg.getPolo() != null)
				dg.getPolo().getDescricao();
			
			if (discente.getPeriodoAtual() == null) {
				CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente);
				dg.setSemestreAtual(discenteDao.calculaPeriodoAtualDiscente(dg, cal.getAno(), cal.getPeriodo()));
			} else {
				dg.setSemestreAtual(discente.getPeriodoAtual());
			}
		} finally {
			if ( discenteDao != null ) discenteDao.close(); 
		}
	}

	/**
	 * Carrega dados específicos de alunos stricto. 
	 * Orientador, trancamentos, nome do programa, linha de pesquisa e outros.
	 * 
	 * 
	 * @param historico
	 * @return
	 * @throws DAOException
	 */
	private DiscenteAdapter carregarDadosDiscenteStricto(Historico historico, Movimento mov) throws DAOException {
		DiscenteDao dao = getDAO(DiscenteDao.class, mov);
		BancaPosDao bpdao = getDAO(BancaPosDao.class, mov);
		DiscenteStricto ds = (DiscenteStricto) historico.getDiscente();
	
		try {
			historico.setTrancamentos(dao.findTrancamentosByDiscente(ds));
		
			Curso curso = ds.getCurso();
			if (curso != null) {
				if (curso.getUnidade() != null)
					curso.getUnidade().getNome();
				if (curso.getTipoCursoStricto() != null)
					curso.getTipoCursoStricto().getDescricao();
		
				ds.getCurso().getNomeCursoStricto();
			}
			if (ds.getCurriculo() != null)
				ds.getCurriculo().getCodigo();
			if (ds.getArea() != null)
				ds.getArea().getDenominacao();
			if (ds.getLinha() != null)
				ds.getLinha().getDenominacao();
			ds.setOrientacao(DiscenteHelper.getUltimoOrientador(ds.getDiscente()));
		
			// Listar aproveitamentos de crédito do discente e transformar em
			// matrícula para exibição no histórico
			List<AproveitamentoCredito> aproveitamentos = (List<AproveitamentoCredito>) getGenericDAO(mov).findByExactField (AproveitamentoCredito.class, new String[] { "discente.id", "ativo"}, new Object[] { ds.getId(), Boolean.TRUE });
			for (AproveitamentoCredito aproveitamento : aproveitamentos) {
				if (aproveitamento.isAtivo()) {
					MatriculaComponente mc = new MatriculaComponente();
					ComponenteCurricular cc = new ComponenteCurricular();
					cc.setNome("APROVEITAMENTO DE CRÉDITOS");
					cc.setChTotal(aproveitamento.getCreditos() * 15);
					cc.getDetalhes().setCrTotal(aproveitamento.getCreditos());
					mc.setSituacaoMatricula(SituacaoMatricula.APROVEITADO_CUMPRIU);
		
					mc.setComponente(cc);
					if (historico.getMatriculasDiscente() == null)
						historico.setMatriculasDiscente(new ArrayList<MatriculaComponente>());
					historico.getMatriculasDiscente().add(mc);
				}
			}
		
			// Mês atual
		
			// Os meses de trancamentos
		
			// Buscar as atividades disponibilizadas para o currículo do discente
			// Informações da banca de defesa
			
			ds.setBancaDefesa(bpdao.findMaisRecenteByTipo(ds, BancaPos.BANCA_DEFESA));								
			if (ds.getBancaDefesa() != null &&  ds.getBancaDefesa().getMatriculaComponente() != null 
					&& (ds.getBancaDefesa().getMatriculaComponente().isAprovado() || ds.getBancaDefesa().getMatriculaComponente().isAproveitadoDispensado()) ) {
				ds.getBancaDefesa().getTitulo();
				ds.getBancaDefesa().getPalavraChave();
				ds.getBancaDefesa().getMembrosBanca().iterator();
				ds.setDataDefesa(ds.getBancaDefesa().getData());
				ds.getBancaDefesa().getMembrosBanca().iterator();
			} else {
				ds.setBancaDefesa(null);
			}
			// Calcula os créditos para os componentes das áreas de concentração comum e específica do discente.
			CurriculoHelper.calcularCrTotalAreaConcentracaoCurriculo(ds);
		} finally {
			if ( dao != null ) dao.close();
			if ( bpdao != null ) bpdao.close();	
		}
		
		return ds;
	}
	
	/**
	 *	Carrega dados específicos de alunos da Residência médica.
	 * @return
	 * @throws DAOException 
	 */
	private DiscenteAdapter carregarDadosDiscenteResidenciaMedica(Historico historico, Movimento mov) throws DAOException {
		DiscenteDao dao = getDAO(DiscenteDao.class, mov);
		DiscenteResidenciaMedica ds = (DiscenteResidenciaMedica) historico.getDiscente();
	
		try {
			historico.setTrancamentos(dao.findTrancamentosByDiscente(ds));
		
			Curso curso = ds.getCurso();
			if (curso != null) {
				if (curso.getUnidade() != null)
					curso.getUnidade().getNome();
		
				ds.getCurso().getNome();
			}
			if (ds.getCurriculo() != null)
				ds.getCurriculo().getCodigo();
	
			// Listar aproveitamentos de crédito do discente e transformar em
			// matrícula para exibição no histórico
			List<AproveitamentoCredito> aproveitamentos = (List<AproveitamentoCredito>) getGenericDAO(mov).findByExactField(AproveitamentoCredito.class, "discente.id", ds.getId());
			for (AproveitamentoCredito aproveitamento : aproveitamentos) {
				if (aproveitamento.isAtivo()) {
					MatriculaComponente mc = new MatriculaComponente();
					ComponenteCurricular cc = new ComponenteCurricular();
					cc.setNome("APROVEITAMENTO DE CRÉDITOS");
					cc.setChTotal(aproveitamento.getCreditos() * 15);
					mc.setSituacaoMatricula(SituacaoMatricula.APROVEITADO_CUMPRIU);
		
					mc.setComponente(cc);
					if (historico.getMatriculasDiscente() == null)
						historico.setMatriculasDiscente(new ArrayList<MatriculaComponente>());
					historico.getMatriculasDiscente().add(mc);
				}
			}
		} finally {	
			if ( dao != null ) dao.close();
		}	
		
		return ds;
	}
	
	/**
	 * Carrega informações sobre o reconhecimento do curso do aluno
	 * 
	 * @param historico
	 * @throws DAOException
	 */
	private void carregarDadosReconhecimento(Historico historico, Movimento mov)
			throws DAOException {
		DiscenteDao discenteDao = getDAO(DiscenteDao.class, mov);
		Discente discente = historico.getDiscente().getDiscente();
		
		try {
			Curso curso = discente.getCurso();
		
			Object[] dadosRec = discenteDao.findDadosReconhecimentoCurso(discente.getId());
			if (dadosRec == null) {
				Object[] dadosAut = discenteDao.findDadosAutorizacaoCurso(discente.getId());
		
				if (dadosAut == null) {
					curso.setReconhecimento(true);
					curso.setReconhecimentoPortaria(null);
					curso.setDataDecreto(null);
					curso.setDou(null);
				} else {
					curso.setReconhecimento(false);
					curso.setReconhecimentoPortaria((String) dadosAut[0]);
					curso.setDataDecreto((Date) dadosAut[1]);
					curso.setDou((Date) dadosAut[2]);
				}
		
			} else {
				curso.setReconhecimento(true);
				curso.setReconhecimentoPortaria((String) dadosRec[0]);
				curso.setDataDecreto((Date) dadosRec[1]);
				curso.setDou((Date) dadosRec[2]);
			}
		} finally {
			if ( discenteDao != null ) discenteDao.close();
		}	
	}

	
	/**
	 * Recalcula os totais de um discente.
	 * 
	 * @param discente
	 * @throws ArqException
	 * @throws RemoteException 
	 * @throws NegocioException 
	 */
	private void processarReCalculoCurriculo(DiscenteAdapter discente, Movimento mov)
			throws ArqException, NegocioException, RemoteException {
		if (discente.getNivel() == NivelEnsino.GRADUACAO) {
			DiscenteGraduacao d = (DiscenteGraduacao) discente;
			// Só recalcula para alunos de graduação, regulares, ativos (ou
			// formandos) e ainda não
			// teve atualização nos totais
			if (StatusDiscente.getStatusComVinculo().contains(d.getStatus())) {
				if (d.getUltimaAtualizacaoTotais() == null || isEmpty(d.getDiscente().getIndices())) {
					MovimentoCadastro movimento = new MovimentoCadastro();
					movimento.setUsuarioLogado(mov.getUsuarioLogado());
					movimento.setCodMovimento(SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE);
					movimento.setObjAuxiliar(new Object[] { false, true });
					movimento.setObjMovimentado(d);
					ProcessadorCalculosDiscente processador = new ProcessadorCalculosDiscente();
					processador.execute(movimento);
				}
			}
		} else if (discente.isStricto()) {
			// Só recalcula para alunos com vínculo
			MovimentoCadastro movimento = new MovimentoCadastro();
			movimento.setUsuarioLogado(mov.getUsuarioLogado());
			movimento.setCodMovimento(SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE_STRICTO);
			movimento.setObjMovimentado(discente);
			ProcessadorCalculosDiscenteStricto processador = new ProcessadorCalculosDiscenteStricto();
			processador.execute(movimento);
		
		} else if (discente.isTecnico()) {
			// Só recalcula para alunos com vínculo
			MovimentoCadastro movimento = new MovimentoCadastro();
			movimento.setSistema(Sistema.SIGAA);
			movimento.setUsuarioLogado(mov.getUsuarioLogado());
			movimento.setCodMovimento(SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE_TECNICO);
			movimento.setObjMovimentado(discente);
			ProcessadorCalculosDiscenteTecnico processador = new ProcessadorCalculosDiscenteTecnico();
			processador.execute(movimento);
		}
	}
}
