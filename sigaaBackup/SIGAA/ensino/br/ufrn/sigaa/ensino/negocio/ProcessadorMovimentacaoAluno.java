/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 02/06/2009
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.bolsas.negocio.IntegracaoBolsas;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.dto.InclusaoBolsaAcademicaDTO;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ParametrosGestoraAcademicaDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.dao.projetos.DiscenteProjetoDao;
import br.ufrn.sigaa.arq.dao.sae.BolsaAuxilioDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilioPeriodo;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.bolsas.negocio.MovimentoBolsaAcademica;
import br.ufrn.sigaa.bolsas.negocio.ProcessadorBolsaAcademica;
import br.ufrn.sigaa.diploma.dao.RegistroDiplomaDao;
import br.ufrn.sigaa.diploma.dominio.ObservacaoRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.RegistroDiploma;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.AlteracaoMatricula;
import br.ufrn.sigaa.ensino.dominio.ConstantesTipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.EstornoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoAfastamentoAluno;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoColacaoGrauColetiva;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoOperacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoOrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.negocio.ProcessadorAlteracaoStatusMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.ProcessadorAlterarDataColacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.ProcessadorOrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.negocio.calculos.CalculoPrazoMaximoFactory;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.negocio.DiscenteStrictoCalculosHelper;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.negocio.DiscenteMonitoriaMov;
import br.ufrn.sigaa.monitoria.negocio.ProcessadorDiscenteMonitoria;
import br.ufrn.sigaa.negocio.MovimentoCalculoHistorico;
import br.ufrn.sigaa.parametros.dominio.ParametrosSAE;
import br.ufrn.sigaa.pesquisa.dao.PlanoTrabalhoDao;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.negocio.MovimentoIndicarBolsista;
import br.ufrn.sigaa.pesquisa.negocio.ProcessadorIndicarBolsista;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.ObservacaoDiscente;
import br.ufrn.sigaa.projetos.dominio.DiscenteProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoDiscenteProjeto;
import br.ufrn.sigaa.projetos.negocio.ProcessadorDiscenteProjeto;

/**
 * Processador responsável pelas operações de movimentação de discentes.
 * Operações de movimentação de discentes são aquelas que alteram o status do
 * mesmo, quando do afastamento, cancelamento, retorno, conclusão de curso,
 * abandono, etc.
 * 
 * @author André M Dantas
 */
public class ProcessadorMovimentacaoAluno extends ProcessadorDiscente {

	
	
	/** Processa a movimentação do aluno.
	 * @see br.ufrn.sigaa.ensino.negocio.ProcessadorDiscente#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		GenericDAO dao =  getGenericDAO(movimento);
		MovimentacaoAlunoDao maDao = null;
		try {
			if (SigaaListaComando.AFASTAR_ALUNO.equals(movimento.getCodMovimento())) {		
				
				
				validate(movimento);
				MovimentacaoAluno movimentacaoAluno = ((MovimentoCadastro) movimento).getObjMovimentado();
				
				
				/** 
				 * <p>O aluno pode ser cancelado com pendencias na biblioteca para ele não usar esse pretexto para ficar etenermante com vínculo com a instituição.</p> 
				 * <p>Ps. No mínimo uma mensagem de alerta deve ser mostrada para quem está cancelando.</p>
				 * 
				 * <p>A biblioteca via ter que ir atrás de recuperar esse material (eles têm um relatório para isso)</p>
				 * 
				 */
				if (!movimentacaoAluno.isTrancamentoProgramaPosteriori() && !movimentacaoAluno.isCancelamento())
					validarEmprestimos(movimento, movimentacaoAluno);
				
				
				validarAfastamento((MovimentoCadastro) movimento);
				validarPeriodoDiscente((MovimentoCadastro) movimento);
				
				
				if (movimentacaoAluno.isConclusao() && movimentacaoAluno.getDiscente().isStricto()) {
					validarCumprimentoDeCreditosExigidos((MovimentoCadastro) movimento);
					validarTrabalhosProficiencia((MovimentoCadastro) movimento);
				}
				
				if (movimentacaoAluno.isConclusao())
					atualizaDataColacaoRegistroDiploma((MovimentoCadastro) movimento);
				
				
				criar((MovimentoCadastro) movimento);
				finalizarBolsas((MovimentoCadastro) movimento);
				finalizarOrientacoes((MovimentoCadastro) movimento);
				if (!movimentacaoAluno.isConclusao())
					cancelarMatriculaComponentes((MovimentoCadastro) movimento);
				
				// Tem que ser executado antes da alteração do status do discente!
				recalcularDiscente(movimento, movimentacaoAluno);
				
				alterarStatusDiscente((MovimentoCadastro) movimento);
				
			} else if (SigaaListaComando.ALTERAR_AFASTAMENTO_ALUNO.equals(movimento.getCodMovimento())) {
				alterarStatusDiscente((MovimentoCadastro) movimento);
				alterar((MovimentoCadastro) movimento);
			} else if (SigaaListaComando.ESTORNAR_AFASTAMENTO_ALUNO.equals(movimento.getCodMovimento())) {
				estornar((MovimentoCadastro) movimento);
				reativarOrientacao((MovimentoCadastro) movimento);
			} else if (SigaaListaComando.ESTORNAR_CONCLUSAO_ALUNO.equals(movimento.getCodMovimento())) {
				estornarColetivo((MovimentoColacaoGrauColetiva) movimento);
			} else if (SigaaListaComando.CANCELAR_TRANCAMENTO_PROGRAMA.equals(movimento.getCodMovimento())) {
				cancelarTrancamento((MovimentoAfastamentoAluno) movimento);
				reativarOrientacao((MovimentoCadastro) movimento);
			} else if (SigaaListaComando.PRORROGACAO_PRAZO.equals(movimento.getCodMovimento())) {
				validaProrrogacao(movimento);
				prorrogarPrazoMaximo(movimento);
			} else if (SigaaListaComando.ANTECIPACAO_PRAZO.equals(movimento.getCodMovimento())) {
				validaAntecipacao(movimento);
				anteciparPrazoMaximo(movimento);
			} else if (SigaaListaComando.RETORNAR_ALUNO_AFASTADO.equals(movimento.getCodMovimento())) {
				MovimentoAfastamentoAluno movAfastamento = (MovimentoAfastamentoAluno) movimento;
				cadastrarRetorno(movAfastamento);
	
				if( movAfastamento.getObjMovimentado() != null && ((MovimentacaoAluno) movAfastamento.getObjMovimentado()).getDiscente().isStricto()  ){
					MovimentoCadastro movCadastro = new MovimentoCadastro();
					movCadastro.setObjMovimentado( movAfastamento.getObjMovimentado() );
					movCadastro.setCodMovimento(movAfastamento.getCodMovimento());
					movCadastro.setUsuarioLogado(movAfastamento.getUsuarioLogado());
					movCadastro.setUsuario( movAfastamento.getUsuario() );
					movCadastro.setSistema( movAfastamento.getSistema() );
					movCadastro.setSubsistema( movAfastamento.getSubsistema() );
	
					reativarOrientacao(movCadastro);
				}
	
			} else if (SigaaListaComando.CANCELAR_PRORROGACAO_PRAZO.equals(movimento.getCodMovimento())) {
				validaCancelamentoProrrogacao((MovimentoCadastro) movimento);
				cancelarProrrogacaoPrazo((MovimentoCadastro) movimento);
			} else if (SigaaListaComando.ALTERAR_DATA_COLACAO_COLETIVA.equals(movimento.getCodMovimento())) {
				Collection<DiscenteGraduacao> discentes = ((MovimentoColacaoGrauColetiva) movimento).getDiscentes();
				maDao = getDAO(MovimentacaoAlunoDao.class, movimento);
				for (DiscenteGraduacao discente : discentes){
					MovimentacaoAluno afastamento = maDao.findConclusaoByDiscente(discente.getId(),true);
					ProcessadorAlterarDataColacao processador = new ProcessadorAlterarDataColacao();
					MovimentacaoAluno movConclusao = new MovimentacaoAluno();
					movConclusao.setDiscente(discente);
					movConclusao.setAnoReferencia(afastamento.getAnoReferencia());
					movConclusao.setPeriodoReferencia(afastamento.getPeriodoReferencia());
					movConclusao.setAnoOcorrencia(afastamento.getAnoOcorrencia());
					movConclusao.setPeriodoOcorrencia(afastamento.getPeriodoOcorrencia());
					MovimentoCadastro movAltData = new MovimentoCadastro();
					movAltData.setCodMovimento(SigaaListaComando.ALTERAR_DATA_COLACAO);
					movAltData.setObjMovimentado(movConclusao);
					movAltData.setUsuarioLogado(movimento.getUsuarioLogado());
					movAltData.setSistema( movimento.getSistema() );
					processador.execute(movAltData);
				}
			}
	
			// registra a observação
			if (!SigaaListaComando.ESTORNAR_CONCLUSAO_ALUNO.equals(movimento.getCodMovimento())
				&& !SigaaListaComando.ALTERAR_DATA_COLACAO_COLETIVA.equals(movimento.getCodMovimento()) ){	
				MovimentacaoAluno movAluno = (MovimentacaoAluno)  ((MovimentoCadastro) movimento).getObjMovimentado();
	
				if ( movAluno != null && movAluno.getObservacao() != null && !movAluno.getObservacao().trim().equals("")) {
					ObservacaoDiscente observacao = new ObservacaoDiscente();
					observacao.setData(new Date());
					observacao.setRegistro(movimento.getUsuarioLogado().getRegistroEntrada());
					if ( movAluno != null && movAluno.getId() != 0 )
						observacao.setMovimentacao(movAluno);
					observacao.setObservacao(movAluno.getObservacao());
					observacao.setDiscente(movAluno.getDiscente().getDiscente());
					dao.create(observacao);
					
				}
			}
		} finally {
			dao.close();
			if (maDao != null) maDao.close();
		}

		return null;
	}

	/**
	 * <p> Verifica empréstimos abertos na biblioteca </p>
	 * 
	 * <p>PS.: Na conclusão de discentes de gradução a verificação é no momento da emissão do diploma.</p>
	 * 
	 * <p> Administrador DAE pode movimentar o aluno independente de ter empréstimos na biblioteca ou não. 
	 *  Porém pelo menos uma mensagem de alerta deveria ser informada a ele. A biblioteca possui um relatório para verificar esses casos. </p>
	 * 
	 * @param movimento
	 * @param movimentacaoAluno
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void validarEmprestimos(Movimento movimento, MovimentacaoAluno movimentacaoAluno) throws NegocioException, ArqException {
		if (!movimentacaoAluno.getDiscente().isGraduacao())
			checkValidation( VerificaSituacaoUsuarioBibliotecaUtil.verificaEmprestimoPendenteDiscente(movimentacaoAluno.getDiscente().getDiscente()));
		else 
			if ( !movimentacaoAluno.isConclusao() && ! movimento.getUsuarioLogado().isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE))
				checkValidation( VerificaSituacaoUsuarioBibliotecaUtil.verificaEmprestimoPendenteDiscente(movimentacaoAluno.getDiscente().getDiscente()));
	}
	

	/** Caso o discente possua diploma registrado, atualiza a data de colação do registro de diploma.
	 * @param movimento
	 * @throws DAOException 
	 */
	private void atualizaDataColacaoRegistroDiploma(MovimentoCadastro movimento) throws DAOException {
		RegistroDiplomaDao dao = getDAO(RegistroDiplomaDao.class, movimento);
		try{ 
			MovimentacaoAluno movimentacaoAluno = movimento.getObjMovimentado();
			RegistroDiploma registro = dao.findByDiscente(movimentacaoAluno.getDiscente().getId());
			if (registro != null) {
				ObservacaoRegistroDiploma obs = new ObservacaoRegistroDiploma("Data de colação atualizada de "
						+ Formatador.getInstance().formatarData(registro.getDataColacao())
						+ " para "
						+ Formatador.getInstance().formatarData(movimentacaoAluno.getDataColacaoGrau()));
				registro.addObservacao(obs);
				registro.setDataColacao(movimentacaoAluno.getDataColacaoGrau());
				dao.update(registro);
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Recalcula o discente de Graduação
	 * 
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException 
	 * @throws RemoteException
	 */
	private void recalcularDiscente(Movimento mov, MovimentacaoAluno movimentacaoAluno) throws NegocioException, ArqException, RemoteException {
		
		GenericDAO dao = getGenericDAO(mov);
		
		// Força recálculo para graduação
		if (movimentacaoAluno.getDiscente().isGraduacao())
			dao.updateField(DiscenteGraduacao.class, movimentacaoAluno.getDiscente().getId(), "ultimaAtualizacaoTotais", null);
		
		try {
			Discente d = dao.findByPrimaryKey(movimentacaoAluno.getDiscente().getId(), Discente.class);
			
			MovimentoCalculoHistorico movHistorico = new MovimentoCalculoHistorico();
			movHistorico.setUsuarioLogado(mov.getUsuarioLogado());
			movHistorico.setSistema(mov.getSistema());
			movHistorico.setRecalculaCurriculo(true);
			movHistorico.setDiscente(d);
			movHistorico.setCodMovimento(SigaaListaComando.CALCULAR_HISTORICO_DISCENTE);
			
			
			new ProcessadorCalculaHistorico().execute(movHistorico);		
		} finally {
			dao.close();
		}
		
	}


	/**
	 * Quando um aluno é AFASTADO da UFRN, as suas orientações devem ser FINALIZADAS
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private void finalizarOrientacoes(MovimentoCadastro mov) throws NegocioException, ArqException {
		MovimentacaoAluno afastamento = (MovimentacaoAluno) mov.getObjMovimentado();

		OrientacaoAcademica orientacao = new OrientacaoAcademica();
		orientacao.setDiscente(afastamento.getDiscente().getDiscente());
		orientacao.setFim(afastamento.getInicioAfastamento());
		if (afastamento.isConclusao() || afastamento.isCancelamento() || afastamento.isAbandono())
			orientacao.setFim( afastamento.getDataOcorrencia() );


		MovimentoOrientacaoAcademica movOrientacoes = new MovimentoOrientacaoAcademica();
		movOrientacoes.setOrientacao(orientacao);
		movOrientacoes.setCodMovimento(SigaaListaComando.FINALIZAR_ORIENTACOES_DISCENTE);
		movOrientacoes.setSistema(mov.getSistema());
		movOrientacoes.setSubsistema(mov.getSubsistema());
		movOrientacoes.setUsuarioLogado(mov.getUsuarioLogado());
		(new ProcessadorOrientacaoAcademica()).execute(movOrientacoes);

	}

	/**
	 * Ao retornar um aluno STRICTO de um trancamento de programa, o seu orientador deve ser CADASTRADO NOVAMENTE
	 * @param mov
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public void reativarOrientacao(MovimentoCadastro mov) throws NegocioException, ArqException{
		OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class, mov);
		try {
			MovimentacaoAluno afastamento = (MovimentacaoAluno) mov.getObjMovimentado();
			DiscenteAdapter discente = afastamento.getDiscente();
			if( !discente.isStricto() )
				return; // só retorna orientações para discentes stricto
	
			OrientacaoAcademica orientacao = dao.findUltimaOrientacaoByDiscente( discente.getId() );
	
			if( orientacao == null ) // se não tiver orientação antes, não deve retornar */
				return;
	
			if( SigaaListaComando.ESTORNAR_AFASTAMENTO_ALUNO.equals(mov.getCodMovimento()) || SigaaListaComando.CANCELAR_TRANCAMENTO_PROGRAMA.equals(mov.getCodMovimento()) ){
	
				orientacao.setCancelado(false);
				orientacao.setDataFinalizacao(null);
				dao.update(orientacao);
	
			}else if( SigaaListaComando.RETORNAR_ALUNO_AFASTADO.equals(mov.getCodMovimento()) ){
	
				OrientacaoAcademica novaOrientacao = new OrientacaoAcademica();
				novaOrientacao.setDiscente( orientacao.getDiscente() );
				novaOrientacao.setDocenteExterno( orientacao.getDocenteExterno() );
				novaOrientacao.setServidor( orientacao.getServidor() );
				novaOrientacao.setInicio( afastamento.getDataCadastroRetorno() );
				novaOrientacao.setInicio(new Date());
				novaOrientacao.setTipoOrientacao( orientacao.getTipoOrientacao() );
	
				MovimentoOrientacaoAcademica movOrientacao = new MovimentoOrientacaoAcademica();
				movOrientacao.setOrientacao(novaOrientacao);
				(new ProcessadorOrientacaoAcademica()).execute(movOrientacao);
	
			}
		} finally {
			dao.close();
		}

	}

	/** Finaliza bolsas do discente no SIGAA e SIPAC.
	 * @param movimento
	 * @throws RemoteException
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws RemoteException 
	 */
	private void finalizarBolsas(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
		PlanoTrabalhoDao daoPesquisa = null;
		DiscenteMonitoriaDao monitoriaDao = null;
		DiscenteProjetoDao daoProjeto = null;
		BolsaAuxilioDao bolsaAuxilioDao = null;
		try {
			MovimentacaoAluno afastamento = (MovimentacaoAluno) mov.getObjMovimentado();
			DiscenteAdapter discente = afastamento.getDiscente();
	
			// Verificar se o afastamento ocorrerá no ano-período diferente do qual o discente está cursando
			CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendario(discente);
			if (afastamento.getAnoReferencia() != calendario.getAno() || afastamento.getPeriodoReferencia() != calendario.getPeriodo() ) {
				return;
			}
	
			// Finalizar bolsas de pesquisa
			daoPesquisa = getDAO(PlanoTrabalhoDao.class, mov);
			PlanoTrabalho planoTrabalhoPesquisa =  daoPesquisa.findAtivoByIdDiscente(discente.getId());
				
			if ( planoTrabalhoPesquisa != null ) {
				MovimentoIndicarBolsista movPesquisa = new MovimentoIndicarBolsista();
				movPesquisa.setSistema( mov.getSistema() );
				movPesquisa.setUsuarioLogado( mov.getUsuarioLogado() );
	
				movPesquisa.setCodMovimento( SigaaListaComando.REMOVER_BOLSISTA );
				movPesquisa.setPlanoTrabalho( planoTrabalhoPesquisa );
				movPesquisa.setDataFinalizacao(new Date());
				MembroProjetoDiscente bolsitaFinalizado = new MembroProjetoDiscente();
				bolsitaFinalizado.setMotivoSubstituicao("Cancelamento do Programa");
				movPesquisa.setBolsistaAnterior(bolsitaFinalizado);
	
				ProcessadorIndicarBolsista processadorPesquisa = new ProcessadorIndicarBolsista();
				processadorPesquisa.execute(movPesquisa);
				
			}
	
			// Finalizar bolsas de monitoria
			monitoriaDao = getDAO(DiscenteMonitoriaDao.class, mov);
			Collection<DiscenteMonitoria> discentesMonitoria = monitoriaDao.findDiscenteMonitoriaAtivoByDiscente(discente);
	
			for (DiscenteMonitoria discenteMonitoria:  discentesMonitoria) {
				monitoriaDao.detach(discenteMonitoria);
	
				discenteMonitoria.setDataFim( new Date() );
	
				DiscenteMonitoriaMov movMonitoria = new DiscenteMonitoriaMov();
				movMonitoria.setSistema( mov.getSistema() );
				movMonitoria.setUsuarioLogado( mov.getUsuarioLogado() );
	
				movMonitoria.setDiscenteMonitoria(discenteMonitoria);
				movMonitoria.setCodMovimento(SigaaListaComando.FINALIZAR_DISCENTEMONITORIA);
				movMonitoria.setValidar(false); //Permite a finalização incondicional do monitor.
	
				ProcessadorDiscenteMonitoria processadorMonitoria = new ProcessadorDiscenteMonitoria();
				processadorMonitoria.execute(movMonitoria);
				
			}
			
			// Finalizar bolsas de projeto(Ações Associadas)
			daoProjeto = getDAO(DiscenteProjetoDao.class, mov);
			Collection<DiscenteProjeto> discentesProjeto = new ArrayList<DiscenteProjeto>();
			discentesProjeto = daoProjeto.findByDiscentesEmProjetosBySituacao(discente.getId(), TipoSituacaoDiscenteProjeto.SELECIONADO, TipoSituacaoDiscenteProjeto.ASSUMIU);	
				
			if ( !discentesProjeto.isEmpty() ) {
				MovimentoCadastro movProjeto = new MovimentoCadastro();
				movProjeto.setCodMovimento(SigaaListaComando.FINALIZAR_DISCENTE_PROJETO_PARA_AFASTADO);
				movProjeto.setSistema( mov.getSistema() );
				movProjeto.setUsuarioLogado( mov.getUsuarioLogado() );
				movProjeto.setObjAuxiliar(discentesProjeto);
			
				ProcessadorDiscenteProjeto processadorDiscenteProjeto = new ProcessadorDiscenteProjeto();
				processadorDiscenteProjeto.execute(movProjeto);
			}
			
			boolean possuiBolsaSipac = IntegracaoBolsas.tipoBolsaSipacAtivoByMatricula(discente.getMatricula()).size() > 0;				
			try {				
				if( Sistema.isSipacAtivo() && possuiBolsaSipac ){
					solicitaExclusaoBolsaSIPAC(mov, discente, IntegracaoBolsas.tipoBolsaSipacAtivoByMatricula(discente.getMatricula()));
				}
			} catch (Exception e) {
				// Silencia o erro e adiciona à lista de mensagens do movimento um aviso que não foi realizada a exclusão da bolsa no SIPAC
				if (possuiBolsaSipac)
					mov.getMensagens().addErro("O discente " +discente.getMatriculaNome() + " não teve a bolsa finalizada no SIPAC.");
			}
			
			
			// Finalizar auxílios do discente
			bolsaAuxilioDao = getDAO(BolsaAuxilioDao.class, mov);
			List<BolsaAuxilioPeriodo> listBolsaAuxilioDiscente = (List<BolsaAuxilioPeriodo>) bolsaAuxilioDao
					.findAllBolsasDiscenteAnoPeriodo(discente.getDiscente(),
							calendario.getAno(), calendario.getPeriodo());
			
			boolean possuiBolsaAuxilio = false;
			for (BolsaAuxilioPeriodo bolsaAuxilioPeriodo : listBolsaAuxilioDiscente) {
				bolsaAuxilioPeriodo.getBolsaAuxilio().setSituacaoBolsa(new SituacaoBolsaAuxilio(SituacaoBolsaAuxilio.BOLSA_CANCELADA));
				bolsaAuxilioDao.updateNoFlush(bolsaAuxilioPeriodo.getBolsaAuxilio());
				possuiBolsaAuxilio = true;
			}
			
			// Se o discente possui bolsa auxílio, a PROAE deve ser notificada
			// sobre o motivo do cancelamento da bolsa.
			if (possuiBolsaAuxilio)
				enviaEmailPROAE(discente, afastamento, ParametroHelper.getInstance().getParametro(ParametrosSAE.EMAIL_PROAE), afastamento.isConclusao());
			
		} finally {
			if (daoPesquisa != null) daoPesquisa.close();
			if (monitoriaDao != null) monitoriaDao.close();
			if (bolsaAuxilioDao != null) bolsaAuxilioDao.close();
		}
	}
	
	/**
	 * Envia emails de notificação para os gestores da PROAE e para o discente bolsista.
	 * @param discente
	 * @param afastamento
	 * @param email
	 * @param informarPrazo
	 */
	private void enviaEmailPROAE(DiscenteAdapter discente, MovimentacaoAluno afastamento, String email, boolean informarPrazo){
		MailBody mailGestor = new MailBody();
		MailBody mailAluno = new MailBody();
	    mailGestor.setContentType(MailBody.HTML);
	    mailAluno.setContentType(MailBody.HTML);
	    mailGestor.setFromName("[SIGAA - Aviso automático]");
	    mailAluno.setFromName("[SIGAA - Aviso automático]");
	    mailGestor.setEmail(email);
	    mailAluno.setEmail(discente.getPessoa().getEmail());
	    mailGestor.setAssunto("Mudança de status de bolsista PROAE");
	    mailAluno.setAssunto("Mudança de status de bolsista PROAE");
	    String mensagemGestor = "O discente " + discente.getMatriculaNome() + ", teve a(s) sua(s) bolsa(s) cancelada(s) devido ao cadastro" +
	    		" de uma movimentação do tipo " + afastamento.getTipoMovimentacaoAluno().getDescricao() + ".";
	    if ( informarPrazo ) {
	    	mensagemGestor += "<br/><br/>O discente terá " + ParametroHelper.getInstance().getParametroInt(ParametrosSAE.PRAZO_PARA_ABANDONAR_RESIDENCIA) + 
					" dias para deixar a residência a contar da data da sua colação de " +
					" grau (" + Formatador.getInstance().formatarData(afastamento.getDataColacaoGrau()) +").";
		}
	    String mensagemAluno = "Caro(a) " + discente.getPessoa().getNome() + ", a(s) sua(s) bolsa(s) foi(ram) cancelada(s) devido ao cadastro" +
	    		" de uma movimentação do tipo " + afastamento.getTipoMovimentacaoAluno().getDescricao() + ".";
	    if ( informarPrazo ) {
			mensagemAluno += "<br/><br/>Você terá " + ParametroHelper.getInstance().getParametroInt(ParametrosSAE.PRAZO_PARA_ABANDONAR_RESIDENCIA) + 
					" dias para deixar a residência a contar da data da sua colação de " +
					" grau (" + Formatador.getInstance().formatarData(afastamento.getDataColacaoGrau()) +").";
		}
	    mailGestor.setMensagem(mensagemGestor);
	    mailAluno.setMensagem(mensagemAluno);
	    Mail.send(mailGestor);
	    Mail.send(mailAluno);
	}

	/** Solicita exclusão de bolsas no SIPAC de acordo com o tipo especificado.
	 * @param mov
	 * @param discente
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public void solicitaExclusaoBolsaSIPAC(MovimentoCadastro mov, DiscenteAdapter discente, List<Integer> listTipoBolsas) throws NegocioException, ArqException,	RemoteException {
		
		if (!discente.isRegular())
			return;
			
		MovimentoBolsaAcademica movBolsa = new MovimentoBolsaAcademica();
		Collection<InclusaoBolsaAcademicaDTO> solicitacoesCadastro = new ArrayList<InclusaoBolsaAcademicaDTO>();
		for (Integer tipoBolsa : listTipoBolsas) {
			InclusaoBolsaAcademicaDTO solicitacao = new InclusaoBolsaAcademicaDTO();
			solicitacao.setDataFim(new Date());
			solicitacao.setIdCurso(discente.getCurso().getId());
			solicitacao.setIdPessoa(discente.getPessoa().getId());
			solicitacao.setIdRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada().getId());
			solicitacao.setIdUsuarioCadastro(mov.getUsuarioLogado().getId());
			solicitacao.setMatricula(discente.getMatricula());
			solicitacao.setJustificativa("[BOLSA ENCERRADA DEVIDO A AFASTAMENTO DO ALUNO]");
			solicitacao.setObservacao("[SOLICITAÇÃO REALIZADA VIA SIGAA]");
			solicitacao.setNivel(String.valueOf(discente.getNivel()));
			solicitacao.setTipoBolsa(tipoBolsa);
			solicitacoesCadastro.add(solicitacao);
		}
		
		movBolsa.setApplicationContext(mov.getApplicationContext());
		movBolsa.setSolicitacoes(solicitacoesCadastro);
		movBolsa.setCodMovimento(ArqListaComando.SOLICITAR_EXCLUSAO_BOLSA_ACADEMICA);
		
		ProcessadorBolsaAcademica processadorIBA = new ProcessadorBolsaAcademica();
		processadorIBA.execute(movBolsa);
		mov.getMensagens().addWarning("O discente "+discente.getMatriculaNome()+" teve sua bolsa cancelada no "+RepositorioDadosInstitucionais.get("siglaSipac")+".");
	}

	/**
	 * Valida o cancelamento de prorrogação de prazos.<br>
	 * Regras:<br>
	 * Só pode cancelar prorrogações realizadas no ano.periodo corrente!
	 * Não pode cancelar prorrogações de prazo negativas, ou seja, estorno de prorrogação !
	 * @param movimento
	 * @throws DAOException
	 * @throws NegocioException
	 */ 
	private void validaCancelamentoProrrogacao(MovimentoCadastro movimento) throws DAOException, NegocioException {
		MovimentacaoAluno prorrogacao = (MovimentacaoAluno) movimento.getObjMovimentado();

		ListaMensagens erros = new ListaMensagens();

		if( prorrogacao.getTipoMovimentacaoAluno().getId() != TipoMovimentacaoAluno.PRORROGACAO_ADMINISTRATIVA && prorrogacao.getTipoMovimentacaoAluno().getId() != TipoMovimentacaoAluno.PRORROGACAO_JUDICIAL && prorrogacao.getTipoMovimentacaoAluno().getId() != TipoMovimentacaoAluno.PRORROGACAO_TRANCAMENTO_PROGRAMA ){
			erros.addErro("A movimentação de aluno selecionada não é uma prorrogação de prazo, portanto nao pode ser cancelada.");
		}

		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(prorrogacao.getDiscente());
		if( prorrogacao.getAnoReferencia() == null || prorrogacao.getPeriodoReferencia() == null
				||  prorrogacao.getAnoReferencia() != cal.getAno()
				|| prorrogacao.getPeriodoReferencia() != cal.getPeriodo() ){
			erros.addErro("Só é possível cancelar prorrogações de prazo realizadas no ano/período corrente.");
		}

		if( prorrogacao.getValorMovimentacao() <= 0 ){
			erros.addErro("Não é possível cancelar um estorno de prorrogação");
		}

		checkValidation(erros);

	}

	/**
	 * Cancela uma prorrogação de prazo de conclusão.
	 *
	 * @param movimento
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	private void cancelarProrrogacaoPrazo(MovimentoCadastro movimento) throws ArqException, NegocioException  {
		MovimentacaoAluno prorrogacao = (MovimentacaoAluno) movimento.getObjMovimentado();
		DiscenteDao dao = getDAO(DiscenteDao.class, movimento);

		prorrogacao = dao.findByPrimaryKey(prorrogacao.getId(), MovimentacaoAluno.class);
		prorrogacao.setAtivo(false);
		prorrogacao.setDataEstorno(new Date());
		
		ObservacaoDiscente obs = getGenericDAO(movimento).findByExactField(ObservacaoDiscente.class, "movimentacao", prorrogacao.getId(), true);
		if( obs != null && obs.getId()!=0 )		
			getGenericDAO(movimento).updateField(ObservacaoDiscente.class, obs.getId(), "ativo", false);

		dao.update(prorrogacao);
		try {
			if (prorrogacao.getDiscente().isGraduacao()) {
				CalculoPrazoMaximoFactory.getCalculoGraduacao(prorrogacao.getDiscente()).calcular(prorrogacao.getDiscente(), movimento);
			} else if (prorrogacao.getDiscente().isStricto()){
				// se for discente stricto basta lançar o aproveitamento de chamar os cálculos do discente
				// pós ele recalcula os prazos de acordo com as entradas na tabela de prorrogação
				DiscenteStricto stricto = (DiscenteStricto) dao.findByPK(prorrogacao.getDiscente().getId());
				DiscenteStrictoCalculosHelper.realizarCalculosDiscenteChain(stricto, movimento);
			} 
		} finally {
			dao.close();
		}
	}

	/** Valida os requisitos para efetuar o afastamento do discente.
	 * @param movimento
	 * @throws NegocioException
	 * @throws ArqException 
	 */
	public void validarAfastamento(MovimentoCadastro movimento) throws NegocioException, ArqException {
		GenericDAO dao = getGenericDAO(movimento);
		try {
			MovimentacaoAluno tranc = (MovimentacaoAluno) movimento.getObjMovimentado();
			DiscenteAdapter d = tranc.getDiscente();
			// só pode afastar aluno ativo
			if (!tranc.isTrancamento() && StatusDiscente.isAfastamento(d.getStatus())) {
	
				//pode cancelar programa de discente trancado
				if( tranc.isCancelamento() && d.getStatus() == StatusDiscente.TRANCADO )
					return;
	
				// se o aluno estiver afastado
				throw new NegocioException("Não é possível afastar "+(!isEmpty(d.getMatriculaNome()) ? "o discente " + d.getMatriculaNome() + " porque " : "um discente que") +" já afastado da instituição.");
			} else if (tranc.isTrancamento() &&
					(d.getStatus() == StatusDiscente.CANCELADO || d.getStatus() == StatusDiscente.JUBILADO
						|| d.getStatus() == StatusDiscente.CADASTRADO  || d.getStatus() == StatusDiscente.CONCLUIDO)) {
				throw new NegocioException("Não é possível trancar progama de discentes cadastrados, concluídos ou cancelados");
			}
			if (tranc.getTipoMovimentacaoAluno().getId() == TipoMovimentacaoAluno.CONCLUSAO) {
				// verifica empréstimos na biblioteca sem devolução
				// verificaEmprestimoPendente(movimento);
				// verifica se há implantação de disciplinas em semestres posteriores ao atual
				verificaMatriculaComponenteFutura(movimento);
				// verifica se há pendências na participação do ENADE
				if (d.isGraduacao()) {
					DiscenteGraduacao dg = dao.findAndFetch(d.getId(), DiscenteGraduacao.class, "id", "participacaoEnadeConcluinte", "participacaoEnadeIngressante");
					if (dg.getParticipacaoEnadeConcluinte() == null || dg.getParticipacaoEnadeConcluinte().isParticipacaoPendente()
							|| dg.getParticipacaoEnadeIngressante() == null || dg.getParticipacaoEnadeIngressante().isParticipacaoPendente()) {
						throw new NegocioException("O discente não poderá concluir o programa pois está com participação no ENADE pendente.");
					}
				}
			}
		} finally {
			dao.close();
		}
	}

	/** Cancela um trancamento
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	protected Object cancelarTrancamento(MovimentoAfastamentoAluno mov) throws NegocioException, ArqException {

		MovimentacaoAlunoDao dao = null;
		MatriculaComponenteDao mcdao = null;
		try {
			MovimentacaoAluno tranc = null;
			dao  = getDAO(MovimentacaoAlunoDao.class, mov);
	
			if( mov.isAutoRetorno() ){
				Usuario usr = (Usuario) mov.getUsuarioLogado();
				Discente dsc = usr.getVinculoAtivo().getDiscente().getDiscente();
				if( dsc == null || !dsc.isGraduacao() || !dsc.isTrancado() ){
					throw new NegocioException("Esta operação só pode ser realizada por discentes de Graduação que estão com o status TRANCADO.");
				}
	
				//se for o próprio discente que esteja destrancando seu curso ( mov.isAutoRetorno() == true) então ele só pode fazer dentro do período regular de matrícula
				if( mov.isAutoRetorno() && !mov.getCalendario().isPeriodoMatriculaRegular() ){
					throw new NegocioException("Esta operação só pode ser realizada dentro do período de matrícula que é de "
							+ mov.getCalendario().getInicioMatriculaOnline()
							+ " a " + mov.getCalendario().getFimMatriculaOnline() );
				}
	
				tranc = dao.findTrancamentosByDiscente(dsc.getId(), mov.getCalendario().getAno(), mov.getCalendario().getPeriodo(), false);
	
			}else{
				tranc = (MovimentacaoAluno) mov.getObjMovimentado();
				tranc = dao.findByPrimaryKey(tranc.getId(), MovimentacaoAluno.class);
			}
			// verifica se o aluno não possui um trancamento já acertado para o semestre imediatamente a seguir
			int proximoAnoPeriodo = DiscenteHelper.somaSemestres(tranc.getAnoReferencia(), tranc.getPeriodoReferencia(), 1);
			int proximoAno = proximoAnoPeriodo / 10;
			int proximoPeriodo = proximoAnoPeriodo - proximoAno*10;
			MovimentacaoAluno trancProximoPeriodo =  dao.findTrancamentosByDiscente(tranc.getDiscente().getId(), proximoAno, proximoPeriodo, false);
	
			// se tiver trancamento no próximo período chama este método recursivamente para cancelar também
			if (trancProximoPeriodo != null) {
				MovimentoAfastamentoAluno movProximo = new MovimentoAfastamentoAluno();
				movProximo.setObjMovimentado( trancProximoPeriodo );
				movProximo.setCodMovimento(SigaaListaComando.CANCELAR_TRANCAMENTO_PROGRAMA);
				movProximo.setUsuarioLogado( mov.getUsuarioLogado() );
				//movProximo.setAutoRetorno(false);
				movProximo.setSistema( mov.getSistema() );
				movProximo.setSubsistema( mov.getSubsistema() );
				movProximo.setUsuario( mov.getUsuario() );
	
				cancelarTrancamento(movProximo);
			}
	
			tranc.setUsuarioCancelamento((Usuario)mov.getUsuarioLogado());
			tranc.setDataEstorno(new Date());
			tranc.setAtivo(false);
	
			dao.update(tranc);
	
			// Decrementar uma prorrogação
			mov.setObjMovimentado(tranc);
			DiscenteAdapter d = tranc.getDiscente();
			int qtdProrrogacao = d.isStricto() ? (tranc.getValorMovimentacao() * -1) : -1;
			prorrogarPorTrancamento(mov, qtdProrrogacao, TipoMovimentacaoAluno.PRORROGACAO_TRANCAMENTO_PROGRAMA);
			mcdao  = getDAO(MatriculaComponenteDao.class, mov);
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(d);
			if (cal.getAno() == tranc.getAnoReferencia() && cal.getPeriodo() == tranc.getPeriodoReferencia()) {
				Collection<SituacaoMatricula> todas = SituacaoMatricula.getSituacoesTodas();
				todas.remove(SituacaoMatricula.EXCLUIDA);
				todas.remove(SituacaoMatricula.CANCELADO);				
				int total = mcdao.countMatriculasByDiscente(
						d,tranc.getAnoReferencia(), tranc.getPeriodoReferencia(),todas.toArray(new SituacaoMatricula[todas.size()]));
				if (total > 0) {
					throw new NegocioException("Não é possível cancelar trancamento no semestre que o discente já possui matrículas");
				}
				persistirAlteracaoStatus(tranc.getDiscente(), StatusDiscente.ATIVO, mov);
			}
			return tranc;
		} finally {
			if (mcdao != null) mcdao.close();
			if (dao != null) dao.close();
		}
	}

	/** Persiste o objeto movimentado.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#criar(br.ufrn.arq.dominio.MovimentoCadastro)
	 */
	@Override
	protected Object criar(MovimentoCadastro mov) throws NegocioException, ArqException{
		MovimentacaoAluno afastamento = (MovimentacaoAluno) mov.getObjMovimentado();

		if (afastamento.isConclusao()) {
			GenericDAO dao = null;
			try {
				dao = getGenericDAO(mov);
				dao.updateField(Discente.class, afastamento.getDiscente().getId(),
						"dataColacaoGrau", afastamento.getDataColacaoGrau());
			} finally {
				if (dao != null) dao.close();
			}
		}
		
		try {
			afastamento.setUsuarioCadastro((Usuario) mov.getUsuarioLogado());
			afastamento.setAtivo(Boolean.TRUE);
			afastamento.setUsuarioRetorno(null);
			afastamento.setDataOcorrencia(new Date());
			super.criar(mov);
		} catch (Exception e) {
			throw new ArqException(e);
		} finally{
			// Inserindo o tipo de discente correto no objMovimentado a ser retornado.
			DiscenteDao discenteDao = getDAO(DiscenteDao.class, mov);
			afastamento.setDiscente(discenteDao.findByPK(afastamento.getDiscente().getId()));
			discenteDao.close();
		}
		
		if (afastamento.isCancelamento()) {
			try {
				// SE o afastamento for CANCELAR O ALUNO E ELE TIVER TRANCAMENTOS FUTUROS, os trancamentos futuros devem ser cancelados!
				cancelarTrancamentosFuturos(mov);
			} catch (Exception e) {
				throw new ArqException(e);
			}

			// Cancelar orientação no caso de discente stricto
			if (afastamento.getDiscente().isStricto()) {
				OrientacaoAcademicaDao dao = null;
				try {
					dao = getDAO(OrientacaoAcademicaDao.class, mov);
					OrientacaoAcademica orientacao = dao.findOrientadorAtivoByDiscente(afastamento.getDiscente().getId());
					if (orientacao != null) {
						MovimentoOrientacaoAcademica movOrientacoes = new MovimentoOrientacaoAcademica();
						orientacao.setTipoOrientacao(OrientacaoAcademica.ORIENTADOR);
						movOrientacoes.setCodMovimento(SigaaListaComando.CANCELAR_ORIENTACAO_ACADEMICA);
						movOrientacoes.setSistema(mov.getSistema());
						movOrientacoes.setOrientacao(orientacao);
						movOrientacoes.setSubsistema(mov.getSubsistema());
						movOrientacoes.setUsuarioLogado(mov.getUsuarioLogado());
						(new ProcessadorOrientacaoAcademica()).execute(movOrientacoes);
					}
				} finally {
					if (dao != null) dao.close();
				}
			}
		}


		return afastamento;
	}

	/**
	 * Cancela os trancamentos futuros de um aluno considerando o ano e período do afastamento do movimento passado
	 * @param mov
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws RemoteException
	 */
	private void cancelarTrancamentosFuturos(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
		MovimentacaoAluno afastamento = (MovimentacaoAluno) mov.getObjMovimentado();
		MovimentacaoAlunoDao madao = null;
		try {
			if (afastamento.isCancelamento()) {
	
				// cancelar trancamentos futuros
				madao  = getDAO(MovimentacaoAlunoDao.class, mov);
				Collection<MovimentacaoAluno> futuros = madao.findAfastamentosFuturosByDiscente(afastamento.getDiscente().getId(), afastamento.getAnoReferencia(),
						afastamento.getPeriodoReferencia());
				if (!isEmpty(futuros)) {
					for(MovimentacaoAluno m : futuros) {
						madao.updateField(MovimentacaoAluno.class, m.getId(), "ativo", false);
						madao.updateField(MovimentacaoAluno.class, m.getId(), "usuarioCancelamento", mov.getUsuarioLogado().getId());
						madao.updateField(MovimentacaoAluno.class, m.getId(), "dataOcorrencia", new Date());
					}
	
					prorrogarPorTrancamento(mov, futuros.size() * (-1), TipoMovimentacaoAluno.PRORROGACAO_TRANCAMENTO_PROGRAMA);
				}
			}
		} finally {
			if (madao != null) madao.close();
		}
	}


	/**
	 * Método para cadastrar retorno de aluno.
	 * Quando mov tem apenas uma MovimentacaoAluno em mov.getObjMovimentado() significa que é retorno manual de discente.
	 * Se mov.getObjMovimentado() for null e tiver vários movimentacaoAluno em mov.getColObjMovimentado()
	 * significa que é a operação retornar alunos de trancamento, que retorna TODOS os
	 * alunos TRANCADOS que NÃO possuem trancamento no semestre ATUAL
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException 
	 */
	private Object cadastrarRetorno(MovimentoAfastamentoAluno mov) throws NegocioException, ArqException {
		MovimentacaoAlunoDao dao = null;
		try {
			dao  = getDAO( MovimentacaoAlunoDao.class ,mov);
			Collection<MovimentacaoAluno> afastamentos = new ArrayList<MovimentacaoAluno>();
			
			if( mov.getObjMovimentado() != null ){
				// retorna o movimento atual 
				afastamentos.add( (MovimentacaoAluno) mov.getObjMovimentado() );
				// e estorna os movimentos futuros
				int ano = ((MovimentacaoAluno) mov.getObjMovimentado()).getAnoReferencia();
				int periodo = ((MovimentacaoAluno) mov.getObjMovimentado()).getPeriodoReferencia();
				for (MovimentacaoAluno movAluno : dao.findTrancamentosFuturosByDiscente(((MovimentacaoAluno) mov.getObjMovimentado()).getDiscente().getId(), ano, periodo)) {
					movAluno.setStatusRetorno(StatusDiscente.ATIVO);
					movAluno.setDataCadastroRetorno(new Date());
					dao.update(movAluno);
					MovimentoCadastro movEstorno = new MovimentoCadastro();
					movEstorno.setObjMovimentado(movAluno);
					movEstorno.setCodMovimento(SigaaListaComando.ESTORNAR_AFASTAMENTO_ALUNO);
					movEstorno.setUsuarioLogado(mov.getUsuarioLogado());
					estornar(movEstorno);
				}
				
			} else if( !isEmpty( mov.getColObjMovimentado() ) ){
				// retorna vários movimentos simultaneamente. utilizado para retornar alunos de trancamento 
	
				int anoPassado = mov.getAnoPassado();
				int periodoPassado = mov.getPeriodoPassado();
	
				Collection<Integer> idsDiscentes = new ArrayList<Integer>();
				for( PersistDB d : mov.getColObjMovimentado() ){
					idsDiscentes.add(d.getId());
				}
				afastamentos = dao.findTrancamentosByDiscentes(idsDiscentes, anoPassado, periodoPassado, true);
	
			}
	
			for( MovimentacaoAluno  ma : afastamentos ){
	
				DiscenteAdapter d = ma.getDiscente();
	
				if (d.getStatus() == StatusDiscente.CONCLUIDO) {
					throw new NegocioException("Não é possível retornar aluno concluído");
				}
	
				// se a ocorrência for anterior ao ano-período atual, seta o aluno para ativo.
				if (ma.getAnoReferencia() < CalendarUtils.getAnoAtual() ||
						ma.getAnoReferencia() == CalendarUtils.getAnoAtual() && ma.getPeriodoReferencia() <= CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getPeriodo()) {
					if (d.isGraduacao()) {
						//DiscenteGraduacao dg = dao.findByPrimaryKey(d.getId(), DiscenteGraduacao.class);
						DiscenteGraduacao dg = new DiscenteGraduacao(d.getId());
						dg.setStatus(StatusDiscente.ATIVO);
						dg.setStatus( d.getStatus() );
						dg.setNivel( d.getNivel() );
						dao.updateField( DiscenteGraduacao.class, dg.getId(), "ultimaAtualizacaoTotais", null);
						persistirAlteracaoStatus(d, StatusDiscente.ATIVO, mov);
						d.setStatus(StatusDiscente.ATIVO);
					} else {
						Integer ultimoStatus = DiscenteHelper.getUltimoStatus(d.getDiscente());
						Integer status = null;
						if (ultimoStatus != null &&
								(ultimoStatus == StatusDiscente.FORMANDO || ultimoStatus == StatusDiscente.GRADUANDO)){
							status = ultimoStatus;
						} else {
							status = StatusDiscente.ATIVO;
						}
		
						persistirAlteracaoStatus(d, status, mov);
						d.setStatus(status);
					}
				}
	
				int idUsuario = ((Usuario) mov.getUsuarioLogado()).getId();
				Date dataRetorno = new Date();
	
				if ( ma.getId() != 0 ) {
					// se for igual a zero, é por que o afastamento deste aluno não foi encontrado, mas mesmo assim ele é retornado
	
					dao.updateField(MovimentacaoAluno.class, ma.getId(), "usuarioRetorno", idUsuario);
					dao.updateField(MovimentacaoAluno.class, ma.getId(), "dataCadastroRetorno", dataRetorno);
					if( ma.getDiscente().isStricto() ){
						dao.updateField(MovimentacaoAluno.class, ma.getId(), "valorMovimentacao", ma.getValorMovimentacao());
						dao.updateField(MovimentacaoAluno.class, ma.getId(), "dataRetorno", ma.getDataRetorno());
						if( ma.getCodmergpapos() != null && ma.getInicioAfastamento() != null )
						dao.updateField(MovimentacaoAluno.class, ma.getId(), "inicioAfastamento", ma.getInicioAfastamento());
					} else{
						dao.updateField(MovimentacaoAluno.class, ma.getId(), "dataRetorno", dataRetorno);
						if( !isEmpty( ma.getTipoRetorno() ) )
							dao.updateField(MovimentacaoAluno.class, ma.getId(), "tipoRetorno", ma.getTipoRetorno());
					}
				}
	
			}
			return null;
		} finally {
			if (dao != null) dao.close();
		}
	}

	/**
	 * Desativa registro de afastamento e recupera status do aluno de quando ele
	 * foi afastado.
	 * @throws RemoteException
	 * @throws ArqException
	 */
	protected Object estornar(MovimentoCadastro mov) throws NegocioException, ArqException {
		MatriculaComponenteDao mcdao = null;
		MovimentacaoAlunoDao dao = null;
		DiscenteDao ddDao = null;
		try {
			mcdao = getDAO(MatriculaComponenteDao.class, mov);
			dao = getDAO(MovimentacaoAlunoDao.class, mov);
			ddDao = getDAO(DiscenteDao.class, mov);
			
			MovimentacaoAluno afastamento = (MovimentacaoAluno) mov.getObjMovimentado();
			Integer status = afastamento.getStatusRetorno();
			afastamento = dao.findByPrimaryKey(afastamento.getId(), MovimentacaoAluno.class);
	
			if (status == null) {
				throw new NegocioException("Não é possível efetuar estorno para esse discente");
			}
			
			if (status != afastamento.getDiscente().getStatus())
				persistirAlteracaoStatus(afastamento.getDiscente(), status, mov);
			
			dao.updateField(MovimentacaoAluno.class, afastamento.getId(), "ativo", false);
	
			EstornoMovimentacaoAluno estorno = new EstornoMovimentacaoAluno();
			estorno.setDataEstorno(new Date());
			estorno.setMovimentacao(afastamento);
			estorno.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			dao.create(estorno);
	
			 // SE A MOVIMENTAÇÃO JÁ ESTIVER RETORNADA NÃO DEVE DESFAZER AS MATRICULAS, PRORROGAÇÕES, DISCENTE AFETADAS PELA MOVIMENTAÇÃO
			if( afastamento.getDataCadastroRetorno() == null ){
				// estornar todas as matrículas que foram afetadas por essa movimentação
				Collection<AlteracaoMatricula> alteracoes = dao.findByExactField(AlteracaoMatricula.class, "movimentacaoAluno.id", afastamento.getId());
				for (AlteracaoMatricula alteracao : alteracoes) {
					MatriculaComponenteHelper.alterarSituacaoMatricula(
							alteracao.getMatricula(), alteracao.getSituacaoAntiga(), mov, mcdao);
				}
	
				// estornar conclusão
				if (afastamento.isConclusao()) {
					// estornar data de colação setada
					dao.updateField(Discente.class, afastamento.getDiscente().getId(), "dataColacaoGrau", null);
				}
				// estornar trancamento de programa
				else if (afastamento.getTipoMovimentacaoAluno().getId() == TipoMovimentacaoAluno.TRANCAMENTO && afastamento.getCodmergpa() == null) {
					// estornar prorrogação
					
					afastamento.setDiscente(ddDao.findDiscenteAdapterById(afastamento.getDiscente().getId()));
					
					DiscenteAdapter d = afastamento.getDiscente();
					int qtdProrrogacao = d.isStricto() ? (afastamento.getValorMovimentacao() * -1) : -1;
	
					MovimentoCadastro movCadastro = new MovimentoCadastro();
					movCadastro.setObjMovimentado( afastamento );
					movCadastro.setUsuarioLogado( mov.getUsuarioLogado() );
					movCadastro.setSistema( mov.getSistema() );
					movCadastro.setSubsistema( mov.getSubsistema() );
					movCadastro.setUsuario( mov.getUsuario() );
	
					prorrogarPorTrancamento(movCadastro, qtdProrrogacao  , TipoMovimentacaoAluno.PRORROGACAO_TRANCAMENTO_PROGRAMA);
				}
			}
	
			return afastamento;
		} finally { 
			if (mcdao != null) mcdao.close();
			if (dao != null) dao.close();
			if (ddDao != null) ddDao.close();
		}
	}
	
	/**
	 * Desativa registro de conclusão e recupera status do aluno.
	 * @throws RemoteException
	 * @throws ArqException
	 */
	protected Object estornarColetivo(MovimentoColacaoGrauColetiva mov) throws NegocioException, ArqException {
		MatriculaComponenteDao mcdao = getDAO(MatriculaComponenteDao.class, mov);
		MovimentacaoAlunoDao dao = getDAO(MovimentacaoAlunoDao.class, mov);
		
		try{		
			Collection<DiscenteGraduacao> discentes = mov.getDiscentes();
			
			for (DiscenteGraduacao discente : discentes){
				persistirAlteracaoStatus(discente, mov.getStatusRetorno(), mov);
				
				MovimentacaoAluno movAluno = dao.findConclusaoByDiscente(discente.getId());
				dao.updateField(MovimentacaoAluno.class, movAluno.getId(), "ativo", false);
	
				EstornoMovimentacaoAluno estorno = new EstornoMovimentacaoAluno();
				estorno.setDataEstorno(new Date());
				estorno.setMovimentacao(movAluno);
				estorno.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
				dao.create(estorno);
				
				//  SE A MOVIMENTAÇÃO JÁ ESTIVER RETORNADA NÃO DEVE DESFAZER AS MATRICULAS, PRORROGAÇÕES, DISCENTE AFETADAS PELA MOVIMENTAÇÃO				 
				if( movAluno.getDataCadastroRetorno() == null ){
					// estornar todas as matrículas que foram afetadas por essa movimentação
					Collection<AlteracaoMatricula> alteracoes = dao.findByExactField(AlteracaoMatricula.class, "movimentacaoAluno.id", movAluno.getId());
					for (AlteracaoMatricula alteracao : alteracoes) {
						MatriculaComponenteHelper.alterarSituacaoMatricula(
								alteracao.getMatricula(), alteracao.getSituacaoAntiga(), mov, mcdao);
					}
					
					// estornar conclusão
					if (movAluno.isConclusao()) {
						// estornar data de colação setada					
						dao.updateField(Discente.class, discente.getId(), "dataColacaoGrau", null);					
					}
				}			
			}
			return discentes;
		} finally {
			if (mcdao != null)
				mcdao.close();
			if (dao != null)
				dao.close();		
		}

	}	

	/**
	 * Registra a alteração do status do aluno de acordo com o tipo de
	 * afastamento que está sendo cadastrado. Esse registro deve ser feito no
	 * cadastro e na alteração dos afastamentos.
	 *
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws RemoteException
	 * @throws Exception
	 */
	public void alterarStatusDiscente(MovimentoCadastro mov) throws ArqException, NegocioException {
		MovimentacaoAluno afastamento = (MovimentacaoAluno) mov.getObjMovimentado();
		afastamento.setUsuarioRetorno(null);
		DiscenteAdapter discente = afastamento.getDiscente();
		GenericDAO dao = getGenericDAO(mov);
		
		try{
			TipoMovimentacaoAluno tipo = dao.findByPrimaryKey(afastamento
					.getTipoMovimentacaoAluno().getId(), TipoMovimentacaoAluno.class);
	
			// o status do aluno será alterado dependendo do tipo de afastamento
			// cadastrado pra ele
			if (tipo.getStatusDiscente() != null && tipo.getStatusDiscente() == StatusDiscente.TRANCADO) {
				trancarPrograma(mov);
				CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente);
	
				// só altera o status pra trancado se o trancamento for do ano.periodo atual
				if (afastamento.getAnoReferencia() == cal.getAno()
						&& afastamento.getPeriodoReferencia() == cal.getPeriodo()) {
					// registra alteração de status do aluno
					persistirAlteracaoStatus(discente, tipo.getStatusDiscente(), mov);
				}
	
			}
			// se o afastamento resultar no cancelamento do aluno, deve-se cancelar
			// as matrículas correntes do mesmo
			else  if (tipo.getStatusDiscente() != null && tipo.getStatusDiscente() == StatusDiscente.CANCELADO) {
				cancelarPrograma(mov);
				// registra alteração de status do aluno
				persistirAlteracaoStatus(discente, tipo.getStatusDiscente(), mov);
			} else if(tipo.getStatusDiscente() != null) {
				// registra alteração de status do aluno
				persistirAlteracaoStatus(discente, tipo.getStatusDiscente(), mov);
			}
		
		} finally{
			dao.close();
		}

	}

	/** Cancela o programa
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException 
	 */
	private void cancelarPrograma(MovimentoCadastro mov) throws NegocioException, ArqException {
		MovimentacaoAluno afastamento = (MovimentacaoAluno) mov.getObjMovimentado();
		DiscenteAdapter discente = afastamento.getDiscente();
		MatriculaComponenteDao matDao = getDAO(MatriculaComponenteDao.class, mov);
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, mov);
		try {
			// cancelando matrículas
			Collection<MatriculaComponente> matriculadas = matDao.findByDiscente(discente,
					SituacaoMatricula.MATRICULADO, SituacaoMatricula.EM_ESPERA);
			for (MatriculaComponente matricula : matriculadas) {
				MatriculaComponenteHelper.alterarSituacaoMatricula(matricula, SituacaoMatricula.CANCELADO, mov, dao);
			}
			// cancelando solicitações de matrícula
			cancelarSolicitacoesMatricula(mov);
			
		} finally {
			dao.close();
			matDao.close();
		}

	}

	/** Cancela as solicitações de matrícula.
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private void cancelarSolicitacoesMatricula(MovimentoCadastro mov) throws NegocioException, ArqException {
		SolicitacaoMatriculaDao solDao = null;
		try {
			MovimentacaoAluno afastamento = (MovimentacaoAluno) mov.getObjMovimentado();
			DiscenteAdapter discente = afastamento.getDiscente();
			solDao  = getDAO(SolicitacaoMatriculaDao.class, mov);
			
			Collection<SolicitacaoMatricula> solicitacoes = solDao.findByDiscenteTurmaAnoPeriodo(discente, null, afastamento.getAnoReferencia(), afastamento.getPeriodoReferencia(), null,
					SolicitacaoMatricula.SUBMETIDA, SolicitacaoMatricula.ORIENTADO, SolicitacaoMatricula.VISTA);
			if (solicitacoes != null) {
				for (SolicitacaoMatricula sol : solicitacoes) {
					sol.setObservacaoAnulacao("SOLICITAÇÃO CANCELADA DEVIDO AO AFASTAMENTO DO ALUNO");
					sol.setAnulado(true);
					sol.setRegistroAnulacao(mov.getUsuarioLogado().getRegistroEntrada());			
					//Colocado para evitar erro devido o campo ser Lazy.
					UFRNUtils.anularAtributosVazios(sol, "registroCadastro", "registroAlteracao", "registroEntrada");				
					solDao.update(sol);
				}
			}
		} finally {
			if (solDao != null) solDao.close();
		}
	}

	/** 
	 * Realiza chamadas a métodos auxiliares que irão preparar o trancamento do programa.
	 * 
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private void trancarPrograma(MovimentoCadastro mov) throws NegocioException, ArqException {
		
		MovimentacaoAluno afastamento = (MovimentacaoAluno) mov.getObjMovimentado();
		DiscenteAdapter discente = afastamento.getDiscente();
		MatriculaComponenteDao mcdao = getDAO(MatriculaComponenteDao.class, mov);
		ParametrosGestoraAcademicaDao dao = getDAO(ParametrosGestoraAcademicaDao.class, mov);
		
		try {
			discente = dao.findByPrimaryKey(discente.getId(), Discente.class);
			
			// No caso do Trancamento de Curso, tem mais coisas a serem feitas
			ParametrosGestoraAcademica params = ParametrosGestoraAcademicaHelper.getParametros(discente);
			
			validarTrancamentoPrograma(mov, params, null, null);

			// CANCELAR todas as turmas matriculadas DO ANO.SEMESTRE DO TRANCAMENTO
			Collection<MatriculaComponente> matriculas = afastamento.getMatriculasAlteradas();
			SituacaoMatricula novaSituacao = discente.isGraduacao() ?  SituacaoMatricula.CANCELADO :  SituacaoMatricula.TRANCADO;

			if (isEmpty(matriculas)) {
				matriculas = mcdao.findByDiscente(discente, afastamento.getAnoReferencia(), afastamento.getPeriodoReferencia(),
						SituacaoMatricula.MATRICULADO, SituacaoMatricula.EM_ESPERA, SituacaoMatricula.REPROVADO_FALTA, SituacaoMatricula.REPROVADO, SituacaoMatricula.REPROVADO_MEDIA_FALTA);
			}
			if (!isEmpty(matriculas)  ) {
				for (MatriculaComponente matricula : matriculas) {
					MatriculaComponenteHelper.alterarSituacaoMatricula(matricula, novaSituacao, mov, mcdao);
				}
			}
			
			// cancelando solicitações de matrícula
			cancelarSolicitacoesMatricula(mov);

			// Prorrogar um semestre
			int qtdProrrogacao = discente.isStricto()? afastamento.getValorMovimentacao() : 1;
			prorrogarPorTrancamento(mov,  qtdProrrogacao, TipoMovimentacaoAluno.PRORROGACAO_TRANCAMENTO_PROGRAMA);

		} finally{
			dao.close();
			mcdao.close();
		}
		
	}

	/** 
	 * Valida se o discente está apto a trancar o programa e não superou o limite máximo de trancamentos
	 * 
	 * @param mov
	 * @param params
	 * @param tranc
	 * @param simulacaoSemestres
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public void validarTrancamentoPrograma(MovimentoCadastro mov, ParametrosGestoraAcademica params, MovimentacaoAluno tranc, Integer simulacaoSemestres) throws  ArqException, NegocioException{
		// teste de máximo de trancamentos na unidade
		MovimentacaoAluno afastamento = (MovimentacaoAluno) mov.getObjMovimentado();
		DiscenteAdapter discente = afastamento.getDiscente();
		Integer maxTrancamentos = params.getMaxTrancamentos();
		MovimentacaoAlunoDao afasDao = getDAO(MovimentacaoAlunoDao.class, mov);
		
		if ( maxTrancamentos == null )
			maxTrancamentos = 0;
		
		try {
			
			int trancamentosDiscente = afasDao.findTrancamentosByDiscente(discente.getId(), discente
					.getGestoraAcademica().getId(), discente.getNivel());
	
			// usado para simular a quantidade de semestres que o aluno deseja trancar
			if (simulacaoSemestres != null)
				trancamentosDiscente += simulacaoSemestres;
			
			// caso o usuário seja administrador do DAE, deixar trancar mais de 4. 
			if ( trancamentosDiscente > maxTrancamentos && afastamento.getLimiteTrancamentos() )	 {
				mov.getObjMovimentado().setId(0);
				throw new NegocioException("Não foi possível registrar trancamento para esse discente.<br>"
						+ "O número máximo de trancamentos para essa unidade são " + maxTrancamentos + " trancamentos");
				
			}
	
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente);
			// verificar trancamentos futuros (só pode trancar, p.e. 2010.1 se tiver trancado 2009.2)
			int anoPeriodoMovimentacao = new Integer(afastamento.getAnoReferencia() + "" + afastamento.getPeriodoReferencia());
			if (anoPeriodoMovimentacao > DiscenteHelper.somaSemestres(cal.getAno(), cal.getPeriodo(), 1)) {
				int anoPeriodoAnterior = DiscenteHelper.somaSemestres(afastamento.getAnoReferencia(),
						afastamento.getPeriodoReferencia(), -1);
				int ano = anoPeriodoAnterior / 10;
				int periodo = anoPeriodoAnterior -  (ano * 10) ;
	
				// quando trancamento é passado como diferente de null, é pq ta simulando o trancamento
				if (tranc == null)
					tranc = afasDao.findTrancamentosByDiscente(discente.getId(), ano, periodo, true);
	
				if (tranc == null) {
					throw new NegocioException("Não foi possível registrar trancamento para esse discente.<br>"
							+ "Ele precisa trancar o semestre imediatamente anterior");
				}
			}
		
		} finally{
			afasDao.close();
		}
		
	}

	/**
	 * Verifica se o aluno já tem um afastamento sem retorno. Essa validação
	 * só deve ocorrer para o cadastro simples de afastamento.
	 * @throws NegocioException
	 * @throws ArqException
	 */
	@Override
	public void validate(Movimento movimento) throws NegocioException, ArqException {
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		MovimentacaoAluno movimentacaoAluno = (MovimentacaoAluno) mov.getObjMovimentado();

		// verifica se o aluno já tem o mesmo tipo de movimentação no semestre escolhido
		MovimentacaoAlunoDao dao = getDAO(MovimentacaoAlunoDao.class, mov);
		try {
			Collection<MovimentacaoAluno> movimentacoes = dao.findByDiscente(movimentacaoAluno.getDiscente(), movimentacaoAluno.getAnoReferencia(),
					movimentacaoAluno.getPeriodoReferencia(), movimentacaoAluno.getTipoMovimentacaoAluno());
			
			if (movimentacoes != null && movimentacoes.size() > 0) {
				throw new NegocioException("O discente "+movimentacaoAluno.getDiscente().getMatriculaNome()+" já possui movimentação do tipo "
						+ movimentacaoAluno.getTipoMovimentacaoAluno().getDescricao() + " em " + movimentacaoAluno.getAnoPeriodoReferencia());
			}
			
		} finally {
			dao.close();
		}
		
	}

	/** Valida o período de trancamento do discente.
	 * @param mov
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public void validarPeriodoDiscente(MovimentoCadastro mov) throws ArqException, NegocioException {
		MovimentacaoAlunoDao movdao = getDAO(MovimentacaoAlunoDao.class, mov);
		ParametrosGestoraAcademicaDao pdao = getDAO(ParametrosGestoraAcademicaDao.class, mov);
		DiscenteDao ddao = getDAO(DiscenteDao.class, mov);
		try {
			MovimentacaoAluno afastamento = (MovimentacaoAluno) mov.getObjMovimentado();
			DiscenteAdapter discente =  null;
			
			if(!ValidatorUtil.isEmpty(afastamento.getDiscente()) && 
				   (ValidatorUtil.isEmpty(afastamento.getDiscente().getAnoIngresso()) || 
					ValidatorUtil.isEmpty(afastamento.getDiscente().getPeriodoIngresso()) ) ) {
				discente = ddao.findByPK(afastamento.getDiscente().getId());
			} else {
				discente =  afastamento.getDiscente();
			}
			

			int anoPeriodoDiscente = new Integer(discente.getAnoIngresso() +  "" + discente.getPeriodoIngresso());
			int anoPeriodo = new Integer(afastamento.getAnoReferencia() + "" +  afastamento.getPeriodoReferencia());
			if (anoPeriodo < anoPeriodoDiscente && afastamento.getTipoMovimentacaoAluno().getId() != ConstantesTipoMovimentacaoAluno.NAO_CONFIRMACAO_VINCULO)
				throw new NegocioException("Ano e período inválidos: O discente " +discente.getMatriculaNome() +
						" ingressou em " + discente.getAnoPeriodoIngresso());

		} finally  {
			pdao.close();
			movdao.close();
			ddao.close();
		}
	}
	
	/**
	 * Valida se o discente da pós-graduação cumpriu os créditos exigidos e se não possui créditos pendentes.
	 * O total de créditos integralizados deve ser no mínimo igual ao total de créditos exigidos pelo currículo.
	 * 
	 * @param movimento
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void validarCumprimentoDeCreditosExigidos(MovimentoCadastro movimento) throws ArqException, NegocioException {
		
		MovimentacaoAluno movimentacaoAluno = movimento.getObjMovimentado();
		DiscenteStricto discente = (DiscenteStricto) getDAO(DiscenteDao.class, movimento).findByPK(movimentacaoAluno.getDiscente().getId());
		
		DiscenteStrictoCalculosHelper.realizarCalculosDiscenteChain(discente, movimento);
		
		if (!ValidatorUtil.isEmpty(discente.getCrTotalExigidos())) {
		
			if (discente.getCrTotaisIntegralizados() == null 
					|| discente.getCrTotaisIntegralizados().intValue() < discente.getCrTotalExigidos().intValue()) {
				throw new NegocioException("O total de créditos integralizados (" + ObjectUtils.defaultIfNull(discente.getCrTotaisIntegralizados(), NumberUtils.INTEGER_ZERO) 
						+ ") é menor que o total de créditos exigidos do currículo (" + discente.getCrTotalExigidos() + ")");
			}
		}
	}
	
	/**
	 * Valida se o discente concluiu todos os trabalhos do tipo proficiência necessários. 
	 * A quantidade desse tipo de trabalho pode variar dependendo se é mestrado ou doutorado.
	 * 
	 * @param movimento
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void validarTrabalhosProficiencia(MovimentoCadastro movimento) throws ArqException, NegocioException {
		MatriculaComponenteDao mDao = null;
		try {
			MovimentacaoAluno movimentacaoAluno = movimento.getObjMovimentado();
			DiscenteStricto discente = (DiscenteStricto) getDAO(DiscenteDao.class, movimento).findByPK(movimentacaoAluno.getDiscente().getId());
			
			mDao = getDAO(MatriculaComponenteDao.class, movimento);
			
			Collection<MatriculaComponente> proficienciasAprovadas = mDao.findAtividades(discente, new TipoAtividade(TipoAtividade.PROFICIENCIA), SituacaoMatricula.APROVADO);
			Collection<MatriculaComponente> proficienciasAproveitadas = mDao.findAtividades(discente, new TipoAtividade(TipoAtividade.PROFICIENCIA), SituacaoMatricula.APROVEITADO_CUMPRIU);
			
			int quantidadeProficienciasExigidas = DiscenteHelper.getQuantidadeMinimaTrabalhosProficienciaExigida(discente);
			
			if (!discente.cumpriuTrabalhoProficiencia(proficienciasAprovadas, proficienciasAproveitadas, quantidadeProficienciasExigidas)) {
				throw new NegocioException("O discente não cumpriu a quantidade mínima de tabalhos de proficiência exigida: " + quantidadeProficienciasExigidas);
			}
		} finally {
			if (mDao != null) mDao.close();
		}
	}

	/**
	 * Aluno que usufruirá da prorrogação de prazo deve estar com status ATIVO e
	 * no período letivo regular correspondente ao limite máximo para
	 * integralização curricular.
	 */
	public void validaProrrogacao(Movimento mov) throws NegocioException, ArqException {
		
		MovimentacaoAluno p = (MovimentacaoAluno) ((MovimentoCadastro) mov).getObjMovimentado();
		
		/**
		 * Esta validação só deve ser realizada para graduação.
		 */
		if( p.getDiscente().isGraduacao() ){
			
			if (p.getTipoMovimentacaoAluno().getId() == TipoMovimentacaoAluno.PRORROGACAO_ADMINISTRATIVA ||
					p.getTipoMovimentacaoAluno().getId() == TipoMovimentacaoAluno.PRORROGACAO_JUDICIAL) {
				DiscenteAdapter discente = p.getDiscente();
				CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente);
	
				int anoAtual = cal.getAno();
				int periodoAtual = cal.getPeriodo();
				int anoSemestreAtual = new Integer(anoAtual + "" + periodoAtual);
				if ((discente.getStatus() != StatusDiscente.ATIVO && discente.getStatus() != StatusDiscente.FORMANDO ) || anoSemestreAtual < discente.getPrazoConclusao()) {
					throw new NegocioException("Não foi possível realizar prorrogação.<br>" +
					"Essa operação só é disponível para alunos ativos e no ano-semestre máximo para sua conclusão");
				}
			}
			
		}
		
	}

	/** Prorroga o prazo máximo de trancamento para um discente.
	 * @param movimento
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private Object prorrogarPrazoMaximo(Movimento movimento) throws NegocioException, ArqException {
		DiscenteDao dao = null;
		try {
			MovimentacaoAluno p = (MovimentacaoAluno) ((MovimentoCadastro) movimento).getObjMovimentado();
	
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(p.getDiscente());
	
			p.setAtivo(true);
			
			if (p.getAnoReferencia() == null)
				p.setAnoReferencia(cal.getAno());
			if (p.getPeriodoReferencia() == null)
				p.setPeriodoReferencia(cal.getPeriodo());
			
			p.setAnoOcorrencia(cal.getAno());
			p.setPeriodoOcorrencia(cal.getPeriodo());
			
			p.setDataOcorrencia(new Date());
	
			dao  = getDAO(DiscenteDao.class, movimento);
			DiscenteAdapter d = p.getDiscente();
			
			dao.create(p);
			if ( d.isTecnico() || d.isFormacaoComplementar() ) {
				return p;
			}
			
			if( d.isStricto() ){

				DiscenteStricto stricto = (DiscenteStricto) getDAO(DiscenteDao.class, movimento).findByPK(d.getId());
				// se for discente stricto basta lançar o aproveitamento de chamar os cálculos do discente
				// pós ele recalcula os prazos de acordo com as entradas na tabela de prorrogação
				DiscenteStrictoCalculosHelper.realizarCalculosDiscenteChain(stricto, movimento);
				
			}else{
				CalculoPrazoMaximoFactory.getCalculoGraduacao(d).calcular(d, movimento);
			}
			
			return p;
		} finally {
			if (dao != null) dao.close();
		}
	}

	/** 
	 * Cria a movimentação de prorrogação e popula com os dados do afastamento
	 * 
	 * @param mov
	 * @param numSemestre
	 * @param tipoProrrogacao
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private void prorrogarPorTrancamento(MovimentoCadastro mov, int numSemestre, int tipoProrrogacao) throws NegocioException, ArqException {
		MovimentacaoAluno afastamento = (MovimentacaoAluno) mov.getObjMovimentado();
		if (afastamento.getDiscente().getPrazoConclusao() != null) {
			MovimentacaoAluno prorrogacao = new MovimentacaoAluno();
			prorrogacao.setValorMovimentacao(numSemestre);
			prorrogacao.setTipoMovimentacaoAluno(new TipoMovimentacaoAluno(tipoProrrogacao));
			prorrogacao.setDiscente(afastamento.getDiscente());
			prorrogacao.setAnoReferencia(afastamento.getAnoReferencia());
			prorrogacao.setPeriodoReferencia(afastamento.getPeriodoReferencia());
			
			MovimentoCadastro movProrrogacao = new MovimentoCadastro();
			movProrrogacao.setCodMovimento(SigaaListaComando.PRORROGACAO_PRAZO);
			movProrrogacao.setObjMovimentado(prorrogacao);
			movProrrogacao.setUsuarioLogado(mov.getUsuarioLogado());
			prorrogarPrazoMaximo(movProrrogacao);
		}
	}
	
	/**
	 * Aluno que usufruirá da antecipação de prazo de conclusão deve estar com status ATIVO e
	 * no período letivo regular correspondente ao limite máximo para
	 * integralização curricular.
	 */
	public void validaAntecipacao(Movimento mov) throws NegocioException, ArqException {
		
		MovimentacaoAluno p = (MovimentacaoAluno) ((MovimentoCadastro) mov).getObjMovimentado();
		
		/**
		 * Esta validação só deve ser realizada para graduação.
		 */
		if( p.getDiscente().isGraduacao() ){
			
			if (p.getTipoMovimentacaoAluno().getId() == TipoMovimentacaoAluno.ANTECIPACAO_ADMINISTRATIVA ||
					p.getTipoMovimentacaoAluno().getId() == TipoMovimentacaoAluno.ANTECIPACAO_JUDICIAL) {
				DiscenteAdapter discente = p.getDiscente();
				
				if ((discente.getStatus() != StatusDiscente.ATIVO )) {
					throw new NegocioException("Não foi possível realizar antecipação de prazo de conclusão.<br>" +
					"Essa operação só é disponível para alunos ativos");
				}
			}
			
		}
		
	}
	
	/** Antecipa o prazo máximo de trancamento para um discente.
	 * @param movimento
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private Object anteciparPrazoMaximo(Movimento movimento) throws NegocioException, ArqException {
		DiscenteDao dao = null;
		try {
			MovimentacaoAluno p = (MovimentacaoAluno) ((MovimentoCadastro) movimento).getObjMovimentado();
	
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(p.getDiscente());
	
			p.setAtivo(true);
			p.setAnoReferencia(cal.getAno());
			p.setPeriodoReferencia(cal.getPeriodo());
			p.setAnoOcorrencia(cal.getAno());
			p.setPeriodoOcorrencia(cal.getPeriodo());
			
			p.setDataOcorrencia(new Date());
	
			dao  = getDAO(DiscenteDao.class, movimento);
			DiscenteAdapter d = p.getDiscente();
			
			dao.create(p);
			if( d.isStricto() ){
				// se for discente stricto basta lançar o aproveitamento de chamar os cálculos do discente
				// pós ele recalcula os prazos de acordo com as entradas na tabela de prorrogação
				DiscenteStricto stricto = (DiscenteStricto) getDAO(DiscenteDao.class, movimento).findByPK(d.getId());
				DiscenteStrictoCalculosHelper.realizarCalculosDiscenteChain(stricto, movimento);
			}else{
				CalculoPrazoMaximoFactory.getCalculoGraduacao(d).calcular(d, movimento);
			}
			
			return p;
		} finally {
			if (dao != null) dao.close();
		}
	}



//	/** 
//	 *  <p>Verifica se o discente possui empréstimo pendente na biblioteca feitos com o vínculo de discente 
//	 *        <i>(política de empréstimo para aluno , aluno de pós  ou médio/técnico)</i>.</p>
//	 *  
//	 *  <p>Caso exista, o discente não vai poder cancelar o seu vínculo com a instituição até que os empréstimos sejam devolvidos.</p>
//	 * 
//	 * @param movimento
//	 */
//	private void verificaEmprestimoPendente(Movimento movimento) throws NegocioException,	ArqException {
//		
//		MovimentoCadastro mov = (MovimentoCadastro) movimento;
//		MovimentacaoAluno movimentacaoAluno = (MovimentacaoAluno) mov.getObjMovimentado();
//		
//		checkValidation( VerificaSituacaoUsuarioBibliotecaUtil.verificaEmprestimoPendenteDiscente(movimentacaoAluno.getDiscente().getDiscente()));
//	}

	
	
	/**
	 * Verifica se há matrícula do discente em componente curricular posterior
	 * ao ano/período de conclusão.
	 * 
	 * @param movimento
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void verificaMatriculaComponenteFutura(Movimento movimento)throws NegocioException,	ArqException {
		MatriculaComponenteDao dao = null;
		try {
			MovimentoCadastro mov = (MovimentoCadastro) movimento;
			MovimentacaoAluno movimentacaoAluno = (MovimentacaoAluno) mov.getObjMovimentado();
			if (movimentacaoAluno.isConclusao()) {
				dao = getDAO(MatriculaComponenteDao.class, mov);
				StringBuffer componentes = new StringBuffer(); 
				for (MatriculaComponente matricula : dao.findByDiscente(movimentacaoAluno.getDiscente(), SituacaoMatricula.getSituacoesTodas())){
					if (matricula.getSituacaoMatricula().getId() != SituacaoMatricula.EXCLUIDA.getId()
							&& matricula.getSituacaoMatricula().getId() != SituacaoMatricula.INDEFERIDA.getId()){
						if (matricula.getAno() != null && matricula.getAno().intValue() > movimentacaoAluno.getAnoReferencia().intValue()) {
							// existe matrícula em componente curricular em ano posterior ao da conclusão
							componentes.append((componentes.length() > 0 ? ", ": "") +matricula.getComponenteDescricaoResumida());
						} else if (matricula.getAno() != null && matricula.getAno().intValue() == movimentacaoAluno.getAnoReferencia().intValue()) {
							// verifica se a matrícula é em período posterior ao da conclusão.
							switch (movimentacaoAluno.getPeriodoReferencia()) {
								// se a conclusão é no primeiro período, verifica se há matrícula no 2 º ou no 4º (de férias) períodos.
								case 1:	if (matricula.getPeriodo()!= null && matricula.getPeriodo()  > 1 && matricula.getPeriodo() != 3) 
											componentes.append((componentes.length() > 0 ? ", ": "") +matricula.getComponenteDescricaoResumida());
										break;
								case 2:	// sem problema no último período letivo do ano.
							}
						}
					}
				}
				if (componentes.length() > 0)
					throw new NegocioException("O discente "+movimentacaoAluno.getDiscente().getMatriculaNome()+" possui matrícula posterior ao período de conclusão no(s) seguinte(s) componente(s): " + componentes.toString());
			}
		} finally {
			if (dao != null) dao.close();
		}
	}
	
	/**
	 * Altera o status das matrículas em componentes do discente para CANCELADO.
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException 
	 * @throws RemoteException
	 */
	private void cancelarMatriculaComponentes(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentacaoAluno movimentacaoAluno = (MovimentacaoAluno) mov.getObjMovimentado();
		
		DiscenteAdapter discente = movimentacaoAluno.getDiscente();
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, mov);
		try{
			
			
			List<MatriculaComponente> matriculas = null;
			
			if (movimentacaoAluno.getDiscente().isStricto()){
				matriculas = (List<MatriculaComponente>) dao.findMatriculadasByDiscenteAnoPeriodo(discente, movimentacaoAluno.getAnoReferencia(), movimentacaoAluno.getPeriodoReferencia(), TipoComponenteCurricular.getNaoAtividades());				
				
				for(MatriculaComponente m : matriculas ) {
					m.setDiscente(discente);
				}				
				
			} else {
				matriculas = dao.findAtivasByDiscenteAnoPeriodo(discente, movimentacaoAluno.getAnoReferencia(), movimentacaoAluno.getPeriodoReferencia());
			}
			if (matriculas != null && !matriculas.isEmpty() && !movimentacaoAluno.getDiscente().isTecnico()) {
				MovimentoOperacaoMatricula movMatricula = new MovimentoOperacaoMatricula();
				// seta o discente e as matrículas a serem alteradas.
				movMatricula.setNovaSituacao(SituacaoMatricula.CANCELADO);
				movMatricula.setMatriculas(matriculas);
				// prepara e executa o processador para alterar o status da matrícula
				movMatricula.setCodMovimento(SigaaListaComando.ALTERAR_STATUS_MATRICULA);
				movMatricula.setSistema(mov.getSistema());
				movMatricula.setSubsistema(mov.getSubsistema());
				movMatricula.setUsuarioLogado(mov.getUsuarioLogado());
				movMatricula.setAutomatico(false);
				movMatricula.setTrancamentoProgramaPosteriori(movimentacaoAluno.isTrancamentoProgramaPosteriori());
				(new ProcessadorAlteracaoStatusMatricula()).execute(movMatricula);
			} else {
				for (MatriculaComponente matricula : matriculas) {
					MatriculaComponenteHelper.alterarSituacaoMatricula(matricula, SituacaoMatricula.CANCELADO, mov, dao);
				}
			}
		} finally {
			dao.close();
		}
	}
	
}
