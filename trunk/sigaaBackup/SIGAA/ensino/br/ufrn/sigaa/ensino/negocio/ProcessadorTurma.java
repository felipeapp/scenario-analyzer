/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/03/2009
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.agenda.dominio.Agenda;
import br.ufrn.sigaa.agenda.dominio.Evento;
import br.ufrn.sigaa.agenda.util.AgendaUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.avaliacao.AvaliacaoInstitucionalDao;
import br.ufrn.sigaa.arq.dao.ensino.FrequenciaAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.infantil.TurmaInfantilDao;
import br.ufrn.sigaa.arq.dao.graduacao.ReservaCursoDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.TopicoAulaDao;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.AlteracaoTurma;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.HorarioDocente;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dao.TurmaGraduacaoDao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscentesSolicitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.RestricoesMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoEnsinoIndividual;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.ensino.graduacao.dominio.TurmaSolicitacaoTurma;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoMatriculaGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoOperacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.ProcessadorAlteracaoStatusMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.ProcessadorMatriculaGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.SolicitacaoMatriculaHelper;
import br.ufrn.sigaa.ensino.latosensu.dominio.RegistroAlteracaoLato;
import br.ufrn.sigaa.ensino.negocio.dominio.TurmaMov;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.pid.dao.PlanoIndividualDocenteDao;
import br.ufrn.sigaa.pid.dominio.PlanoIndividualDocente;

/**
 * Processador para cadastrar turmas de ensino (técnico, graduação, etc)
 *
 * @author Gleydson
 *
 */
public class ProcessadorTurma extends ProcessadorCadastro {

	/** Persiste os dados da turma.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		TurmaMov tMov = (TurmaMov) mov;
		removeDocentesTransientes(tMov.getTurma());
		tMov.setObjMovimentado(tMov.getTurma());
		if ( mov.getCodMovimento().equals(SigaaListaComando.CONVERTER_TURMA_REGULAR_ENSINO_INDIVIDUAL) ) {
			return converteTurmaRegularEnsinoIndividual((MovimentoCadastro) mov);
		} else if ( mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_TURMA) ) {
			validate(mov);
			return criarTurma((MovimentoCadastro) mov);
		} else if ( mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_TURMA) ) {
			validate(mov);
			return alterar((MovimentoCadastro) mov);
		} else if ( mov.getCodMovimento().equals(SigaaListaComando.REMOVER_TURMA) ) {
			return remover((MovimentoCadastro) mov);
		}
		return null;
	}

	/** Remove da lista de docentes da turma os docentes, externos ou não, transientes. 
	 * @param turma
	 */
	private void removeDocentesTransientes(Turma turma) {
		if (!isEmpty(turma.getDocentesTurmas())) {
			Iterator<DocenteTurma> it = turma.getDocentesTurmas().iterator();
			while (it.hasNext()) {
				DocenteTurma dt = it.next();
				if (isEmpty(dt.getDocente()) && !isEmpty(dt.getDocenteExterno()))
					dt.setDocente(null);
				else if (!isEmpty(dt.getDocente()) && isEmpty(dt.getDocenteExterno()))
					dt.setDocenteExterno(null);
				else
					it.remove();
			}
		}
	}

	/** Valida os dados da turma antes de persistir os dados.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {

		TurmaMov tMov = (TurmaMov) mov;
		Turma turma = tMov.getTurma();

		if(turma.getDisciplina() != null && turma.getDisciplina().getUnidade() != null){
			turma.getDisciplina().setUnidade( getDAO(UnidadeDao.class, mov).findByPrimaryKey(turma.getDisciplina().getUnidade().getId(), Unidade.class) );
		}

		char nivelEnsino = turma.getDisciplina().getNivel();
		ListaMensagens erros = new ListaMensagens();

		if (nivelEnsino != NivelEnsino.INFANTIL && nivelEnsino != NivelEnsino.LATO && nivelEnsino != NivelEnsino.RESIDENCIA)
			TurmaValidator.validaDadosBasicos(turma, (Usuario) mov.getUsuarioLogado(), erros);
		if(nivelEnsino == NivelEnsino.LATO)
			TurmaValidator.validaDadosBasicosLatoSensu(turma, erros);
		if(nivelEnsino == NivelEnsino.RESIDENCIA)
			TurmaValidator.validaDadosBasicosResidenciaSaude(turma, erros);
		if(nivelEnsino != NivelEnsino.GRADUACAO && nivelEnsino != NivelEnsino.TECNICO && nivelEnsino != NivelEnsino.STRICTO && nivelEnsino != NivelEnsino.MEDIO && nivelEnsino != NivelEnsino.RESIDENCIA)
			TurmaValidator.validaDocentes(turma.getDocentesTurmas(), erros);
		if (nivelEnsino == NivelEnsino.TECNICO){
			TurmaValidator.validaDocentesInativosTecnico(turma, turma.getDocentesTurmas(), erros);
		}
		if (nivelEnsino != NivelEnsino.INFANTIL && nivelEnsino != NivelEnsino.LATO && nivelEnsino != NivelEnsino.MEDIO && !turma.isDistancia()) {

			if (turma.getDisciplina().isDistancia() && nivelEnsino != NivelEnsino.GRADUACAO ){				
				erros.getMensagens().add(new MensagemAviso("O componente curricular (EAD) não é compatível com a modalidade de educação da turma (presencial)",TipoMensagemUFRN.ERROR));
			}
			if( mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_TURMA) ){
				// Se ação for alterar E não tiver sido alterado o horário da turma
				// E a turma tiver codmergpa então NÃO DEVE realizar a validação de horário
				Turma turmaOriginal = getGenericDAO(mov).findByPrimaryKey( turma.getId() , Turma.class);
				if( turmaOriginal.getDescricaoHorario() == null && turma.getDescricaoHorario() != null )
					turmaOriginal.setDescricaoHorario( turma.getDescricaoHorario() );

				if ( turmaOriginal.getDescricaoHorario() != null && turma.getDescricaoHorario() != null &&
						!(turma.getDescricaoHorario().trim().equals( turmaOriginal.getDescricaoHorario().trim() )) ) {
					TurmaValidator.validaHorarios(turma, erros, mov.getUsuarioLogado());
					TurmaValidator.validaHorariosDiscentesTurma(turma, mov.getUsuarioLogado() ,erros);
				}

			} else{ // Se for cadastro sempre valida horário
				TurmaValidator.validaHorarios(turma, erros, mov.getUsuarioLogado());
			}

			HashMap<Integer, ParametrosGestoraAcademica> cacheParam = new HashMap<Integer, ParametrosGestoraAcademica>();
			for( DocenteTurma dt : turma.getDocentesTurmas() ){
				TurmaValidator.validaHorariosDocenteTurma(turma, dt, erros, mov.getUsuarioLogado(), cacheParam);
			}

		}

		// Checagem de CH da disciplina com as horas somadas a partir dos horários de aula
		double totalHrs = turma.getDisciplina().getChTotalAula();
		if (totalHrs > turma.getDisciplina().getChTotalAula()) {
			erros.getMensagens().add(new MensagemAviso("Não foi possível realizar a operação.<br/>" +
					"A soma de horas dos horários da turma ("+totalHrs+" h) é maior que a " +
					"carga horária da disciplina ("+turma.getDisciplina().getChTotal()+" h)",
					TipoMensagemUFRN.ERROR));
		}
		
		// Validando turmas de ensino Individualizado, para permitir a criação de uma única turma de ensino individualizado por componente por semestre. 
		if (turma.isTurmaEnsinoIndividual()){
			TurmaGraduacaoDao dao = getDAO(TurmaGraduacaoDao.class, mov);
			try {
				List<Turma> turmasEnsinoIndividual = new ArrayList<Turma>();  
				turmasEnsinoIndividual = dao.findByComponenteTipoAnoPeriodo(turma.getDisciplina().getId(), Turma.ENSINO_INDIVIDUAL, turma.getAno(), turma.getPeriodo());
				for (Turma t : turmasEnsinoIndividual) {
					if (t.getId() != turma.getId()) {
						if (!turmasEnsinoIndividual.isEmpty() && turma.getTurmaAgrupadora().getId() != t.getId()){
							erros.getMensagens().add(new MensagemAviso("Não foi possível realizar a operação.<br/>" +
									"Somente é permitido a criação de uma única turma de ensino individualizado por componente e semestre.",
									TipoMensagemUFRN.ERROR));
						}
					}
				}
			} finally {
				dao.close();
			}
		}
		
		checkValidation(erros);

	}
	

	/**
	 * Persiste a turma
	 * @param mov
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws RemoteException
	 */
	protected Object criarTurma(MovimentoCadastro mov) throws ArqException, NegocioException, RemoteException {
		
		TurmaMov tMov = (TurmaMov) mov;
		ReservaCursoDao dao =  getDAO(ReservaCursoDao.class, mov);
		Collection<Turma> turmasCriadas = new ArrayList<Turma>();
		try {
			// Se for cadastro de turma o código deve ser setado automaticamente 
			if( tMov.getTurma().getId() == 0 ){
				if(tMov.getTurma().getDisciplina().getNivel() == NivelEnsino.INFANTIL)
					tMov.getTurma().setCodigo(gerarCodigoTurmaInfantil(tMov, tMov.getTurma().getAno(), tMov.getTurma().getDisciplina().getId(),  tMov.getTurma().getDescricaoHorario()));
				else
					if(tMov.getTurma().getDisciplina().getNivel() != NivelEnsino.MEDIO )
						tMov.getTurma().setCodigo(gerarCodigoTurma(tMov, tMov.getTurma().getDisciplina(), (short)tMov.getTurma().getAno(), (byte)tMov.getTurma().getPeriodo()));
			}
	
			// Ajustando as solicitações e reservas atendidas */
			ajustandoReservasTurma(tMov, getDAO(ReservaCursoDao.class, mov));
	
			
			if (tMov.getTurma().getEspecializacao() != null && tMov.getTurma().getEspecializacao().getId() == 0)
				tMov.getTurma().setEspecializacao(null);
			if (tMov.getTurma().getCampus() != null && tMov.getTurma().getCampus().getId() == 0)
				tMov.getTurma().setCampus(null);
	
			// Criando turma agrupadora 
			criarTurmaAgrupadora( tMov);
			
			if (tMov.getPolos() != null && tMov.getPolos().length > 0) {
				try {
					for (Integer idPolo : tMov.getPolos()) {
						Polo polo = dao.findByPrimaryKey(idPolo, Polo.class);
						Turma turma = (Turma) UFRNUtils.deepCopy(tMov.getTurma());
						turma.setCodigo(gerarCodigoTurmaEad(tMov, tMov.getTurma().getDisciplina(), (short)tMov.getTurma().getAno(), (byte)tMov.getTurma().getPeriodo(),polo));
						turma.setPolo(polo);
						turma.setLocal( polo.getCidade().getNome() + "/" + polo.getCidade().getUnidadeFederativa().getSigla() );
						
						dao.create(turma);
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new ArqException(e);
				}
			} else {
				// se definido criar apenas uma turma
				if (tMov.getQuantidadeSubturmas() == null || tMov.getQuantidadeSubturmas() == 0) {
					dao.create(tMov.getTurma());
				} else {
					// caso sejam criadas várias subturmas
					for (int i = 0; i < tMov.getQuantidadeSubturmas(); i++) {
						criarTurmaAgrupadora( tMov);
						Turma turma = (Turma) UFRNUtils.deepCopy(tMov.getTurma());
						dao.detach(turma);
						turma.setId(0);
						dao.create(turma);
						turmasCriadas.add(turma);
					}
				}
			}
	
			// Se for solicitação de ensino individual deve matricular os alunos solicitantes da turma de ensino individual na turma criada
			if( tMov.getSolicitacaoEnsinoIndividualOuFerias() != null ){
				// se definido criar apenas uma turma
				if (tMov.getQuantidadeSubturmas() == null || tMov.getQuantidadeSubturmas() == 0) {
					matricularAlunos(tMov, dao);
					TurmaSolicitacaoTurma tst = new TurmaSolicitacaoTurma();
					tst.setTurma( tMov.getTurma() );
					tst.setSolicitacao( tMov.getSolicitacaoEnsinoIndividualOuFerias() );
					dao.create(tst);
				} else {
					for (Turma turmaCriada : turmasCriadas) {
						TurmaSolicitacaoTurma tst = new TurmaSolicitacaoTurma();
						tst.setTurma( turmaCriada );
						tst.setSolicitacao( tMov.getSolicitacaoEnsinoIndividualOuFerias() );
						dao.create(tst);
					}
				}
				dao.updateField(SolicitacaoTurma.class, tMov.getSolicitacaoEnsinoIndividualOuFerias().getId(), "situacao", SolicitacaoTurma.ATENDIDA);
				if ( !isEmpty( tMov.getSolicitacaoEnsinoIndividualOuFerias().getDiscentes() ) )
					notificarDiscentes(tMov);
			}
	
			if( tMov.getRegistroAlteracaoLato() != null ){
				RegistroAlteracaoLato registroLato = tMov.getRegistroAlteracaoLato();
				registroLato.setDisciplina(tMov.getTurma().getDisciplina());
				registroLato.setTurma(tMov.getTurma());
				registroLato.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
				dao.create(registroLato);
			}
		} finally {
			dao.close();
		}
		
		return tMov.getTurma();
	}

	/**
	 * Se o componente da turma permitir a criação de sub-turmas então
	 * é necessário criar a turma agrupadora caso ela não exista.
	 * @param mov
	 * @throws DAOException 
	 */
	private void criarTurmaAgrupadora(TurmaMov mov) throws DAOException {
		Turma turma = mov.getTurma();
		
		if( turma.getDisciplina().isAceitaSubturma() ){
			TurmaDao dao = getDAO(TurmaDao.class, mov);

			try {
				if( turma.getTurmaAgrupadora() == null ){
					Turma agrupadora = new Turma();
					agrupadora.setAgrupadora(true);
					agrupadora.setAno( turma.getAno() );
					agrupadora.setPeriodo( turma.getPeriodo() );
					agrupadora.setDisciplina(turma.getDisciplina());
					agrupadora.setTipo( turma.getTipo() );
					agrupadora.setSituacaoTurma( new SituacaoTurma(SituacaoTurma.ABERTA) );
					if (turma.isEad())
						agrupadora.setCampus(null);
					else
						agrupadora.setCampus( turma.getCampus() );
					agrupadora.setDataInicio( turma.getDataInicio() );
					agrupadora.setDataFim( turma.getDataFim() );
					agrupadora.setDistancia(turma.isDistancia());
					// define o código da turma agrupadora, verificando as turmas já criadas
					Collection<Turma> turmasCriadas = dao.findByDisciplinaAnoPeriodoNivelSituacao(turma.getDisciplina(), turma.getAno(), turma.getPeriodo(), null, (SituacaoTurma[]) null);
					String codigo = "";
					int k = 1;
					if (!isEmpty(turmasCriadas)) {
						// varre a lista de turmas criadas, buscando um "buraco" no código. Exemplo: 01, 02, 04, ...
						for (Turma tc : turmasCriadas) {
							if (tc.isAgrupadora()) {
								if (StringUtils.extractInteger(tc.getCodigo()) != k)
									break;
								k++;
							}
						}
						codigo = StringUtils.leftPad(String.valueOf(k), 2, '0');
					} else {
						codigo = "01";
					}
					
					agrupadora.setCodigo(codigo);
					dao.create(agrupadora);
					
					//ajustando código da primeira sub-turma da turma agrupadora
					turma.setCodigo( codigo + "A" );
					turma.setTurmaAgrupadora(agrupadora);
				} else{
					List<Turma> subTurmas = dao.findSubturmasByTurmaFetchDocentes(turma.getTurmaAgrupadora());
					char letra = 'A';
					// varre a lista de subturmas, verificando se há um "buraco" nas letras das subturmas.
					for (Turma subTurma : subTurmas) {
						char letraSubTurma = subTurma.getCodigo().charAt(subTurma.getCodigo().length() - 1);
						if (letra != letraSubTurma)
							break;
						else letra++;
					}
					turma.setCodigo( turma.getTurmaAgrupadora().getCodigo()  + letra);
				}
			} finally {
				dao.close();
			}
		}
	}

	/**
	 * Notifica os alunos presentes na solicitação da turma da criação da mesma
	 * @param tMov
	 * @throws NegocioException 
	 * @throws DAOException 
	 */
	private void notificarDiscentes(TurmaMov tMov) throws NegocioException, DAOException {
		
		if( !tMov.getTurma().isTurmaFerias() )
			return;
			
		UsuarioDao dao = getDAO(UsuarioDao.class, tMov);
		
		try {
			Set<DiscentesSolicitacao> discentes = new HashSet<DiscentesSolicitacao>();
			discentes.addAll( dao.findByExactField(DiscentesSolicitacao.class, "solicitacaoTurma.id", tMov.getSolicitacaoEnsinoIndividualOuFerias().getId()) );
			if( !isEmpty(tMov.getSolicitacoes()) ){
				for( SolicitacaoTurma s : tMov.getSolicitacoes() ){
					discentes.addAll( dao.refresh(s).getDiscentes() );
				}
			}
			
			if( isEmpty( discentes ) ){
				throw new NegocioException("Não é possível continuar com a criação da turma pois não há nenhum discentes associado à turma.");
			}
			
			String assunto = "Turma de férias criada";
			String msgInicio = "Caro Aluno, <br> A turma de férias da disciplina ";
			String msgFinal = ", solicitada por você, foi criada. Para que você seja matriculado é necessário que você acesse o SIGAA (" + RepositorioDadosInstitucionais.getLinkSigaa() + ") no menu " 
				+ " Ensino >  Matricula on-line > Turma de férias. Nesta operação você deverá concordar com o termo que está disponível" 
				+ " caso contrário sua solicitação de matrícula na turma será cancelada.<br>" 
				+ " Observe que ao concordar com o termo a sua matrícula na turma não estará garantida, pois ainda acontecerá um processamento das solicitações. "
				+ " <br><br>Mensagem Gerada Automaticamente pelo SIGAA - Favor não Responder ";
			
			MailBody mail = new MailBody();
			mail.setContentType(MailBody.HTML);
			mail.setAssunto(assunto);
			
			
			
			for( DiscentesSolicitacao ds : discentes ){
				
				dao.lock(ds);
				Usuario usr = dao.findByDiscente(ds.getDiscenteGraduacao().getDiscente());
				
				String mensagem = msgInicio  + ds.getSolicitacaoTurma().getComponenteCurricular().getCodigoNome() + msgFinal;
				
				mail.setEmail( usr.getEmail() );
				mail.setMensagem( mensagem );
				Mail.send(mail);
				
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Este método ajusta as reservas da turma que está sendo criada ou alterada
	 * - faz o vínculo entre as turmas e as solicitações atendidas através de TurmaSolicitacaoTurma
	 * - seta a situação da(s) reserva(s) para ANTENDIDO ou ANTENDIDO_PARCIALMENTE
	 * - Seta na reserva a turma que foi criada a partir da reserva
	 *
	 * @param mov
	 * @param dao
	 * @throws DAOException
	 */
	private void ajustandoReservasTurma( TurmaMov mov, ReservaCursoDao dao ) throws DAOException{

		if ( mov.getTurma().getCodmergpa() != null ) {
			return; // turma migrada, não mexer nas reservas
		}

		// Criando o vínculo entre a turma e a solicitação que a originou
		if (mov.getSolicitacoes() != null) {
			mov.getTurma().setTurmasSolicitacaoTurmas( new HashSet<TurmaSolicitacaoTurma>(0) );
			for( SolicitacaoTurma st : mov.getSolicitacoes() ){

				// Caso a solicitação seja nulo não faz nada. só acontece quando o DAE cria uma reserva em uma turma, neste caso não há solicitação 
				if( st == null )
					continue;

				// Se já existe o vínculo não faz nada 
				TurmaSolicitacaoTurma jaExiste = null;
				jaExiste = dao.findByTurmaSolicitacao(mov.getTurma().getId(), st.getId());

				if( !isEmpty(jaExiste)  )
					continue;
				
				// Senão, cria 
				TurmaSolicitacaoTurma tst = new TurmaSolicitacaoTurma();
				tst.setTurma( mov.getTurma() );
				tst.setSolicitacao( st );
				mov.getTurma().getTurmasSolicitacaoTurmas().add( tst );
				
				// No caso de turma de férias deve alterar todas as solicitações para a situação atendido 
				if( mov.getTurma().isTurmaFerias() && !isEmpty(mov.getSolicitacoes()) )
					dao.updateField(SolicitacaoTurma.class, st.getId(), "situacao", SolicitacaoTurma.ATENDIDA);
			}

			
		}
		
		// Atualizando a situação das solicitações atendidas com a criação desta turma!
		if( (mov.getTurma().isTurmaRegular() || mov.getTurma().isTurmaFerias())  && mov.getTurma().getReservas() != null) {

			Map<SolicitacaoTurma, Collection<ReservaCurso>> mapa = new HashMap<SolicitacaoTurma, Collection<ReservaCurso>>();

			for( ReservaCurso rc : mov.getTurma().getReservas() ){
				if (rc.getVagasSolicitadas() == null)
					rc.setVagasSolicitadas((short) 0);
				// Setando data e usuário de atendimento 
				if( rc.getVagasReservadas() > 0 && rc.getDataAtendimento() == null ){
					rc.setDataAtendimento(new Date());
					rc.setUsuarioAtendimento( (Usuario) mov.getUsuarioLogado() );
				}
				
				// Verifica se a reserva de curso contempla vagas para ingressantes.
				rc.setPossuiVagaIngressantes(rc.getVagasReservadasIngressantes() > 0);

				if( rc.getSolicitacao() == null || ( !isEmpty( rc.getSolicitacao() ) &&  rc.getSolicitacao().getSituacao() == SolicitacaoTurma.ATENDIDA ) )
					continue;

				SolicitacaoTurma sol = rc.getSolicitacao();
				if( mapa.get( sol ) == null )
					mapa.put( sol, new ArrayList<ReservaCurso>() );
				mapa.get(sol).add( rc );

			}

			for( SolicitacaoTurma s : mapa.keySet() ){

				SolicitacaoTurma tempSol = dao.findByPrimaryKey(s.getId(), SolicitacaoTurma.class);

				int situacao = SolicitacaoTurma.ATENDIDA;
				if( mapa.get(s).size() < tempSol.getReservas().size() ){
					for( ReservaCurso rCurso : tempSol.getReservas() ){
						if( isEmpty( rCurso.getTurma() ) && !mapa.get(s).contains( rCurso ) )
							situacao = SolicitacaoTurma.ATENDIDA_PARCIALMENTE;
					}
				}
				dao.detach(tempSol);
				dao.updateField(SolicitacaoTurma.class, s.getId(), "situacao", situacao); 

			}
		}
		
		// Se for turma de férias as reservas devem ser anuladas, pois em turma de férias não existe reserva de curso 
		if( mov.getTurma().isTurmaFerias() ){
			mov.getTurma().setReservas(null);
		}

		// removendo do banco as reservas que foram removidadas pelo usuário
		// esta operação deveria ser feita pelo hibernate (cascade=CascadeType.ALL) mas não está sendo feita
		Collection<ReservaCurso> persistidas = dao.findByExactField(ReservaCurso.class, "turma.id", mov.getTurma().getId());
		if (!isEmpty(persistidas)) {
			for (ReservaCurso banco : persistidas) {
				boolean remove = true;
				for (ReservaCurso rTurma : mov.getTurma().getReservas()) {
					if (banco.getId() == rTurma.getId() ){
						remove = false;
						break;
					}
				}
				if (remove)
					dao.remove(banco);
			}
		}
	}

	/**
	 * Matricula os alunos da solicitação na turma criada
	 * utilizado no caso de turma de ensino individual onde existe uma lista de
	 * discentes interessados na solicitação da turma
	 *
	 * ESTE MÉTODO TAMBÉM ASSOCIA A TURMA CRIADA COM A ENTIDADE SolicitacaoEnsinoIndividual, CRIADO A PARTIR DA SOLICITAÇÃO INICIAL DO DISCENTE
	 * @param mov
	 * @param dao
	 * @throws RemoteException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void matricularAlunos(TurmaMov mov, GenericDAO dao) throws NegocioException, ArqException, RemoteException{
		
		if( !mov.getSolicitacaoEnsinoIndividualOuFerias().isTurmaEnsinoIndividual() )
			return;
		
		MovimentoMatriculaGraduacao matriculaMov = new MovimentoMatriculaGraduacao();
		matriculaMov.setCodMovimento( SigaaListaComando.MATRICULA_TURMA_FERIAS_ENSINO_INDIVIDUAL );
		matriculaMov.setSistema( mov.getSistema() );
		matriculaMov.setSituacao( SituacaoMatricula.MATRICULADO );
		matriculaMov.setSubsistema( mov.getSubsistema() );
		matriculaMov.setTurmas( new ArrayList<Turma>() );
		matriculaMov.getTurmas().add( mov.getTurma() );
		matriculaMov.setUsuarioLogado( mov.getUsuarioLogado() );
		matriculaMov.setRestricoes( new RestricoesMatricula() );
		mov.getTurma().setMatricular(true);


		for(  DiscentesSolicitacao ds : mov.getSolicitacaoEnsinoIndividualOuFerias().getDiscentes() ){

			dao.lock( ds.getDiscenteGraduacao() );
			matriculaMov.setDiscente( ds.getDiscenteGraduacao() );
			matriculaMov.setCalendarioAcademicoAtual( CalendarioAcademicoHelper.getCalendario( ds.getDiscenteGraduacao() ) );

			ProcessadorMatriculaGraduacao procMatricula = new ProcessadorMatriculaGraduacao();
			procMatricula.execute(matriculaMov);


		}


		// Vinculando as matrículas geradas à SolicitacaoEnsinoIndividual criada pelo discente
		MatriculaComponenteDao daoMat = getDAO( MatriculaComponenteDao.class, mov);
		
		try {
			Collection<MatriculaComponente> matriculasGeradas = daoMat.findMatriculadosAleatoriosByTurma(mov.getTurma(), null);
	
			Collection<SolicitacaoEnsinoIndividual> solicitacoesAlunos = dao.findByExactField(SolicitacaoEnsinoIndividual.class, "solicitacaoTurma.id", mov.getSolicitacaoEnsinoIndividualOuFerias().getId());
			for( MatriculaComponente matGerada : matriculasGeradas ){
				for( SolicitacaoEnsinoIndividual sei : solicitacoesAlunos ){
	
					if( sei.getDiscente().equals( matGerada.getDiscente() ) ){
						dao.updateField(SolicitacaoEnsinoIndividual.class, sei.getId(), "matriculaGerada", matGerada.getId());
					}
	
				}
			}
		} finally {
			daoMat.close();
		}
	}

	/**  Altera os dados da turma.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#alterar(br.ufrn.arq.dominio.MovimentoCadastro)
	 */
	@Override
	protected Object alterar(MovimentoCadastro mov) throws DAOException, NegocioException {
		TurmaMov tMov = (TurmaMov) mov;
		FrequenciaAlunoDao frequenciaDao = getDAO(FrequenciaAlunoDao.class, mov);
		MatriculaComponenteDao dao = null;
		TopicoAulaDao topicoDao = null;
		AvaliacaoInstitucionalDao daoAI = null;
		try {
			dao = getDAO(MatriculaComponenteDao.class, tMov);

			Turma turmaMOV =  tMov.getTurma();
			Turma turmaBD = dao.findByPrimaryKey(turmaMOV.getId(), Turma.class);

			// se já houver alunos matriculados, não é permitido alterar ano, período e componente da turma
			long matriculados = dao.findTotalMatriculasByTurmaAtivos(turmaBD);
			if (matriculados > 0) {
				if (turmaBD.getDisciplina().getId() != turmaMOV.getDisciplina().getId()
						|| turmaBD.getAno() != turmaBD.getAno()
						|| turmaBD.getPeriodo() != turmaBD.getPeriodo()) {
					NegocioException e = new NegocioException();
					e.addErro("Não é permitido alterar dados como componente, ano e período " +
							"de turmas que já possuam alunos matriculados");
					throw e;
				}

			}


			if( !turmaMOV.equals( turmaBD ) ){
				TurmaHelper.criarAlteracaoStatusTurma(turmaMOV, tMov);
			}

			// evitando lazy exception
			Collection<DocenteTurma> docentesDB = turmaBD.getDocentesTurmas();
			if (docentesDB != null)  {
				docentesDB.iterator();
				for (DocenteTurma dt : docentesDB) 
					if (dt.getHorarios() != null)
						dt.getHorarios().iterator();
			}
			Collection<HorarioTurma> horariosDB = turmaBD.getHorarios();
			if (horariosDB != null)
				horariosDB.iterator();
			dao.detach(turmaBD);
			//removendo do banco os docentes que não estão mais na coleção
			// os laços abaixo são necessários uma vez que o método DocenteTurma.equals() compara horários
			// e aqui desejamos apenas se possuem o mesmo ID
			Collection<DocenteTurma> docentesPraRemover = new ArrayList<DocenteTurma>();
			if (docentesDB != null)  {
				for (DocenteTurma dtOrig : docentesDB) {
					boolean contem = false;
					for (DocenteTurma dtNew : turmaMOV.getDocentesTurmas()) {
						if(  dtNew.getId() == dtOrig.getId() ||
								(dtNew.getDocente() != null && dtOrig.getDocente() != null)
								&& ( dtNew.getDocente().getId() == dtOrig.getDocente().getId() )
								|| (dtNew.getDocenteExterno() != null && dtOrig.getDocenteExterno() != null)
								&& ( dtNew.getDocenteExterno().getId() == dtOrig.getDocenteExterno().getId() )){
							contem = true;
							break;
						}
					}
					if (!contem)
						docentesPraRemover.add(dtOrig);
				}
			}
			
			// lista de servidores que poderão ter o PID alterado
			Collection<Servidor> servidores = new ArrayList<Servidor>(1);
			
			// remove avaliações institucionais dos docentes
			daoAI = getDAO(AvaliacaoInstitucionalDao.class, tMov);
			if (mov.getUsuarioLogado().isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE)) {
				for (DocenteTurma dt : docentesPraRemover) {
					daoAI.removerAvaliacaoInstitucional(dt, dt.getTurma().getAno(), dt.getTurma().getPeriodo());
				}
			}
			
			// remove os docenteTurma dos topicoAula
			topicoDao = getDAO(TopicoAulaDao.class, mov);
			topicoDao.removeDocentesTurma(docentesPraRemover);
			for (DocenteTurma dt : docentesPraRemover) {
				dao.remove(dt);
				if (!isEmpty(dt.getDocente()))
					servidores.add(dt.getDocente());
				else if (!isEmpty(dt.getDocenteExterno().getServidor()))
					servidores.add(dt.getDocenteExterno().getServidor());
			}
			
			// notifica a alteração os servidores que possuem PID já cadastrado.
			notificaAlteracaoPid(servidores, tMov);
			
			atualizaHorariosDocente(turmaMOV, dao);

			if(turmaMOV.getHorarios() != null) {
				atualizarHorarios(dao, turmaMOV, horariosDB);
			}

			if (tMov.getTurma().getEspecializacao() != null && tMov.getTurma().getEspecializacao().getId() == 0)
				tMov.getTurma().setEspecializacao(null);

			// Ajustando as solicitações e reservas atendidas 
			ajustandoReservasTurma(tMov, getDAO(ReservaCursoDao.class, mov));

			if (tMov.getTurma().getCampus() != null && tMov.getTurma().getCampus().getId() == 0)
				tMov.getTurma().setCampus(null);

			// Se não houver mudança de agrupadora, nao precisa verificar
			if (turmaBD.getTurmaAgrupadora() != null && turmaMOV.getTurmaAgrupadora() != null 
					&& turmaBD.getTurmaAgrupadora().getId() != turmaMOV.getTurmaAgrupadora().getId()
					|| turmaMOV.getDisciplina().isAceitaSubturma() && 
					 // ou a turma a cadastrar não está agrupada, ou a turma persistida 
 					 // (caso ocorre quando muda o componente para aceitar subturma)
					 ( turmaMOV.getTurmaAgrupadora() == null || turmaBD.getTurmaAgrupadora() == null)) {
					criarTurmaAgrupadora(tMov);
			}
			//Caso tenha alterado o horário desativa as frequências da turma.
			if ( turmaBD.getDescricaoHorario() != null && turmaMOV.getDescricaoHorario() != null &&		
					!turmaBD.getDescricaoHorario().equals(turmaMOV.getDescricaoHorario())){
				frequenciaDao.desativarFrequencias(turmaBD,mov.getUsuarioLogado().getRegistroEntrada());				
			}
			
			// Altera o local da turma do pólo
			if (!isEmpty(tMov.getTurma().getPolo())) {
				Polo polo = dao.findAndFetch(tMov.getTurma().getPolo().getId(), Polo.class, "cidade");
				tMov.getTurma().setLocal( polo.getCidade().getNome() + "/" + polo.getCidade().getUnidadeFederativa().getSigla() );
			} else {
				tMov.getTurma().setPolo(null);
			}
			
			dao.update(tMov.getTurma());
			persistirRegistroDeAlteracaoTurma(tMov,dao);
			
			
			// Se não houver mudança de agrupadora, nao precisa verificar
			if (turmaBD.getTurmaAgrupadora() != null && turmaMOV.getTurmaAgrupadora() != null) {
				if ( turmaBD.getTurmaAgrupadora().getId() != turmaMOV.getTurmaAgrupadora().getId())
					removeTurmaAgrupadoraSemSubTurma(dao, turmaMOV);
			}
			return tMov.getTurma();
		} finally {
			if (dao != null)
				dao.close();
			if (frequenciaDao != null)
				frequenciaDao.close();
			if (topicoDao != null)
				topicoDao.close();
			if (daoAI != null)
				daoAI.close();
		}
	
	}
	
	
	/** Notifica alteração nos Planos Individuais do Docente
	 * @param servidores
	 * @param tMov
	 * @throws DAOException
	 */
	private void notificaAlteracaoPid(Collection<Servidor> servidores, TurmaMov tMov) throws DAOException {
		PlanoIndividualDocenteDao pidDao = getDAO(PlanoIndividualDocenteDao.class, tMov);
		String assunto = "Alteração no Plano Individual do Docente";
		String msg = "<p>Houve alteração no cadastro da turma " + tMov.getTurma().getDescricaoResumida()+
				" e consequente alteração em seu Plano Individual do Docente.<br/>\n" +
				"Solicitamos que acesse o Portal do Docente e atualize seu Plano para que o mesmo reflita as turmas atualmente cadastradas.</p>\n" +
				"<br/>Mensagem Gerada Automaticamente pelo SIGAA - Favor não Responder ";
		try {
			Collection<PlanoIndividualDocente> pids = pidDao.findPIDByServidoresAnoPeriodo(servidores, tMov.getTurma().getAno(), tMov.getTurma().getPeriodo());
			if (!isEmpty(pids)) {
				for (PlanoIndividualDocente pid : pids) {
					MailBody mail = new MailBody();
					mail.setContentType(MailBody.HTML);
					mail.setAssunto(assunto);
					mail.setEmail( pid.getServidor().getPessoa().getEmail() );
					if (pid.getServidor().getPessoa().isSexoFeminino())
						mail.setMensagem("<p>Cara "+pid.getServidor().getPessoa().getNomeAbreviado() +", </p><br/>" + msg);
					else
						mail.setMensagem("<p>Caro "+pid.getServidor().getPessoa().getNomeAbreviado() +", </p><br/>" + msg);
					Mail.send(mail);
				}
			}
		} finally {
			pidDao.close();
		}
	}

	/** 
	 * Persiste a AlteracaoTurma caso a turma tenha sido alterada em alguns dados específicos.
	 * 
	 * @throws DAOException 
	 */
	private void persistirRegistroDeAlteracaoTurma(Movimento movimento, GenericSigaaDAO dao) throws DAOException {	
		TurmaMov mov = (TurmaMov) movimento;		
		AlteracaoTurma alteracao = mov.getAlteracaoTurma();
		
		if(alteracao!=null) {
			alteracao.setDataAlteracao(new Date());
			alteracao.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			dao.create(alteracao);			
		}		
		mov.setAlteracaoTurma(null);
	}
	

	/** Remove os horários do docente que estão em banco e não serão mais utilizados.
	 * @param turmaMOV
	 * @param dao
	 * @throws DAOException 
	 */
	private void atualizaHorariosDocente(Turma turmaMOV, GenericDAO dao) throws DAOException {
		if (turmaMOV.getDocentesTurmas() != null) {
			Collection<HorarioDocente> remover = new ArrayList<HorarioDocente>();
			for (DocenteTurma dt : turmaMOV.getDocentesTurmas()) {
				DocenteTurma dtDb = dao.findByPrimaryKey(dt.getId(), DocenteTurma.class);
				if (dtDb != null) {
					if (dtDb.getHorarios() != null) dtDb.getHorarios().iterator();
					dao.detach(dtDb);
					for (HorarioDocente hdDb : dtDb.getHorarios()) {
						boolean contem = false;
						for (HorarioDocente hd : dt.getHorarios()) {
							if (hd.equals(hdDb)) {
								contem = true;
								break;
							}
						}
						if (!contem)
							remover.add(hdDb);
					}
				}
			}
			for (HorarioDocente hd : remover)
				dao.remove(hd);
		}
	}

	/**
	 * Atualiza os horários da turma caso tenham sido apagados na view.
	 * Este método identifica os HorarioTurma que foram excluídos na view e remove no banco de dados.
	 * 
	 * @param dao
	 * @param turmaMOV
	 * @param horariosDB
	 * @throws DAOException
	 */
	private void atualizarHorarios(MatriculaComponenteDao dao, Turma turmaMOV,
			Collection<HorarioTurma> horariosDB) throws DAOException {

		// ID com os horários pra manter
		Set<Integer> horarioPraManter = new HashSet<Integer>();
		
		// Cria uma lista SOMENTE com os horários que não foram mexidos na view
		for (HorarioTurma ht : turmaMOV.getHorarios()) {
			for (HorarioTurma htBD : horariosDB) {
				if (ht.getId() == htBD.getId())
					horarioPraManter.add(ht.getId());
			}
		}
		
		// Se o horário tiver sido excluído na view, será excluído do banco.
		// Tudo o que não for pra manter, será excluído
		for (HorarioTurma ht : horariosDB) {
			if (!horarioPraManter.contains(ht.getId()))
				dao.remove(ht);
		}
	}


	/** Remove a turma.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#remover(br.ufrn.arq.dominio.MovimentoCadastro)
	 */
	@Override
	protected Object remover(MovimentoCadastro mov) throws NegocioException, ArqException {
		TurmaMov tMov = (TurmaMov) mov;
		Turma turma = tMov.getTurma();
		
		GenericDAO dao =  getGenericDAO(mov);
		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class, tMov);
		SolicitacaoMatriculaDao solicitacaoDao = getDAO(SolicitacaoMatriculaDao.class, tMov);

		try {
			turma = dao.refresh(turma);
	
			long totalMatriculasAtivas = matriculaDao.findTotalMatriculasByTurmaSituacao(turma, 
					SituacaoMatricula.MATRICULADO.getId(), SituacaoMatricula.APROVADO.getId(), 
					SituacaoMatricula.REPROVADO.getId(), SituacaoMatricula.REPROVADO_FALTA.getId(), SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId());
			
			long totalMatriculas = matriculaDao.findTotalMatriculasByTurma( turma, true);
			
			boolean chefePodeRemover = OperacaoTurmaValidator.isPermiteRemoverTurma(turma)
					&& totalMatriculasAtivas < ParametroHelper.getInstance().getParametroInt(
									ParametrosGraduacao.QTD_MAX_DISCENTES_PERMITE_REMOCAO_TURMA_PELO_CHEFE);
			if( totalMatriculasAtivas > 0) {
				if (mov.getUsuarioLogado().isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.GESTOR_LATO)
						|| chefePodeRemover) {
					List<MatriculaComponente> matriculas = matriculaDao.findAtivasByTurma(turma);
					for (MatriculaComponente matricula : matriculas) {
						matricula.setSituacaoMatricula(SituacaoMatricula.EXCLUIDA);
						dao.updateField(MatriculaComponente.class, matricula.getId(), "situacaoMatricula", SituacaoMatricula.EXCLUIDA);
					}
				}else {
					throw new NegocioException("Não é possível excluir esta turma pois ela possui alunos.");
				}
			}
			
			if( totalMatriculas == 0 )
				turma.setSituacaoTurma( new SituacaoTurma(SituacaoTurma.EXCLUIDA) );
			else
				turma.setSituacaoTurma( new SituacaoTurma(SituacaoTurma.INTERROMPIDA) );
	
			TurmaHelper.criarAlteracaoStatusTurma(turma, tMov);
	
			desfazerAtendimentoSolicitacoes(turma, dao, tMov);
	
			if( turma.isTurmaEnsinoIndividual()  ){
				desfazerMatriculas(turma, dao, tMov);
			}
			
			//Se for Stricto anular as solicitações de matrícula para a turma excluída
			List<SolicitacaoMatricula> solicitacoes = null;
			if(turma.isStricto()){
				solicitacoes = (List<SolicitacaoMatricula>) solicitacaoDao.findByTurma(turma.getId(), false, SolicitacaoMatricula.SUBMETIDA, SolicitacaoMatricula.CADASTRADA);
			} else if (turma.isGraduacao()) {
				solicitacoes = (List<SolicitacaoMatricula>) solicitacaoDao.findByTurma(turma.getId(), false, SolicitacaoMatricula.SUBMETIDA, SolicitacaoMatricula.VISTA, SolicitacaoMatricula.ORIENTADO);
			}
				
			if(!isEmpty(solicitacoes)){
				SolicitacaoMatriculaHelper.anularSolicitacoes(tMov.getUsuarioLogado(), "A Turma referente à solicitação foi excluída.", solicitacoes);
			}
			// Atualiza a turma e cria o registro da alteração
			dao.update(turma);
			
			//cancela as solicitações de turmas da turma
			if (!isEmpty(turma.getTurmasSolicitacaoTurmas())) {
				for (TurmaSolicitacaoTurma tst : turma.getTurmasSolicitacaoTurmas()){
					tst.getSolicitacao().setSituacao(SolicitacaoTurma.REMOVIDA);
					dao.update(tst.getSolicitacao());
				}
			}
			
		} finally {
			dao.close();
			matriculaDao.close();
			solicitacaoDao.close();
		}

		return turma;
	}

	/**
	 * Este método desfaz o atendimento das solicitações de de uma turma
	 * para ser utilizado na remoção de turmas onde todas as solicitações devem voltar para o estado original
	 * @throws DAOException
	 */
	private void desfazerAtendimentoSolicitacoes( Turma turma, GenericDAO dao, TurmaMov mov) throws DAOException{

		Collection<TurmaSolicitacaoTurma> turmasSolicitacoes = dao.findByExactField(TurmaSolicitacaoTurma.class, "turma.id", turma.getId());
		turma.setTurmasSolicitacaoTurmas(null);

		for( TurmaSolicitacaoTurma tst : turmasSolicitacoes ){

			// Retornando reservas
			for( ReservaCurso rc : tst.getSolicitacao().getReservas() ){
				if( !isEmpty(rc.getTurma()) && (rc.getTurma().getId() == turma.getId()) ){
					ReservaCurso reserva = dao.refresh(rc);
					reserva.setDataAtendimento(null);
					reserva.setUsuarioAtendimento(null);
					reserva.setTurma(null);
					reserva.setVagasReservadas((short)0);
					reserva.setVagasOcupadas((short)0);
					reserva.setVagasReservadasIngressantes((short)0);
					reserva.setVagasOcupadasIngressantes((short)0);
					dao.update(reserva);
					dao.detach(reserva);
				}
			}

			if( tst.getSolicitacao().getTurmasSolicitacaoTurmas().size() == 1 ){
				dao.updateField(SolicitacaoTurma.class, tst.getSolicitacao().getId(), "situacao", SolicitacaoTurma.ABERTA);
			}else{
				dao.updateField(SolicitacaoTurma.class, tst.getSolicitacao().getId(), "situacao", SolicitacaoTurma.ATENDIDA_PARCIALMENTE);
			}

			dao.remove(tst);

		}

	}

	/**
	 * Exclui as matrículas quando a turma de ensino individual é excluída.
	 * @param turma
	 * @param dao
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void desfazerMatriculas(Turma turma, GenericDAO dao, TurmaMov mov) throws NegocioException, ArqException{

		// Só tem que desfazer as matrículas caso seja turma de ensino individual 
		if( !(turma.isTurmaEnsinoIndividual() || turma.isTurmaFerias()) )
			return;

		MatriculaComponenteDao mcDao = getDAO(MatriculaComponenteDao.class, mov);
		
		try {
			Collection<MatriculaComponente> matriculas = mcDao.findByTurma( turma.getId() );
	
			if( isEmpty(matriculas) ) // Se não tiver alunos matriculados na turma não deve desfazer nada 
				return;
	
			MovimentoOperacaoMatricula movMat = new MovimentoOperacaoMatricula();
			movMat.setMatriculas(matriculas);
			movMat.setNovaSituacao(SituacaoMatricula.EXCLUIDA);
			movMat.setCodMovimento(SigaaListaComando.ALTERAR_STATUS_MATRICULA);
			movMat.setSistema(mov.getSistema());
			movMat.setUsuarioLogado( mov.getUsuarioLogado() );
			movMat.setCancelamentoMatriculasEnsinoIndividual(true);
	
			ProcessadorAlteracaoStatusMatricula procMatricula = new ProcessadorAlteracaoStatusMatricula();
			try {
				procMatricula.execute(movMat);
			} catch (NegocioException e) {
				throw e;
			} catch (ArqException e) {
				throw e;
			} catch (Exception e) {
				throw new ArqException(e);
			}
		} finally {
			mcDao.close();
		}
	}

	/**
	 * Gera o código da turma
	 * @param mov
	 * @param componenteCurricular
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	private String gerarCodigoTurma(Movimento mov, ComponenteCurricular componenteCurricular, short ano, byte periodo) throws DAOException {
		TurmaMov tMov = (TurmaMov) mov;
		TurmaDao turmaDao = getDAO(TurmaDao.class, mov);
		try {
			if (tMov.getTurma().isTurmaEnsinoIndividual())
				return ParametroHelper.getInstance().getParametro(ParametrosGraduacao.CODIGO_TURMA_ENSINO_INDIVIDUALIZADO);
			
			int qtdTurmas = turmaDao.countTurmasByComponenteAnoPeriodo(componenteCurricular.getId(), ano, periodo);
			qtdTurmas++;
			if( qtdTurmas < 10 )
				return "0" + qtdTurmas;
			else
				return qtdTurmas + "";
		} finally {
			turmaDao.close();
		}
	}
	
	/**
	 * Método auxiliar utilizado para gerar o código para a turma do ensino infantil segundo a seguinte regra:
	 * Primeira turma daquele ano/nível/turno recebe código "A"; segunda turma para o mesmo ano/nível/turno
	 * recebe código "B", e assim por diante.
	 * 
	 * @param mov
	 * @param ano
	 * @param nivel
	 * @param turno
	 * @return
	 * @throws DAOException
	 */
	private String gerarCodigoTurmaInfantil(Movimento mov, int ano, int nivel, String turno) throws DAOException {
		int numeroTurmas = 0;
		TurmaInfantilDao dao = getDAO(TurmaInfantilDao.class, mov);

		try {
			numeroTurmas = dao.countTurmasByAnoNivelTurno(ano, nivel, turno);
		} finally {
			dao.close();
		}
		
		return UFRNUtils.inteiroToAlfabeto(++numeroTurmas);
	}
	
	
	/**
	 * Gera o código da turma de EAD.
	 * @param mov
	 * @param componenteCurricular
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	private String gerarCodigoTurmaEad(Movimento mov, ComponenteCurricular componenteCurricular, short ano, byte periodo, Polo polo) throws DAOException, NegocioException {
		TurmaDao turmaDao = getDAO(TurmaDao.class, mov);
		try {
			if(polo != null && polo.getCodigo() == null){			
				throw new  NegocioException("Não foi possivel cadastrar a(s) turma(s) selecionadas(s)"+
			" pois o pólo " + polo.getCidade().getNome() + " não possui código!");				
			}
			Collection<Turma> turmas = turmaDao.turmasEadByComponentePoloAnoPeriodo(componenteCurricular.getId(), ano, periodo,polo.getId());
			int qtdTurmas = turmas.size();
			
			if( qtdTurmas == 0 ) {
				return polo.getCodigo();
			} else  {
				if( qtdTurmas == 1 ) {
					Turma t = turmas.iterator().next();				
					turmaDao.updateField(Turma.class, t.getId(), "codigo", polo.getCodigo()+"A");
					return polo.getCodigo()+"B";
				}
				
				char letra = 'A';
				letra += qtdTurmas;
				
				return polo.getCodigo() + letra;
				
			}			
		} finally {
			turmaDao.close();
		}
	}
	
	
	/** Remove do banco turmas agrupadoras que não possuem mais subTurmas. 
	 * @param dao
	 * @param turma 
	 */
	private void removeTurmaAgrupadoraSemSubTurma(GenericDAO dao, Turma turma) {
		// tenta remover a turma. Caso haja algum objeto associado à turma, ela não será removida (a exceção será silenciada neste caso)
		try {
			Object params[] = {SituacaoTurma.EXCLUIDA, turma.getDisciplina().getId(), turma.getDisciplina().getId()};
			dao.update("update ensino.turma " +
					" set id_situacao_turma =?" +
					" where id_turma in (select id_turma" +
					" from ensino.turma" +
					" where agrupadora = trueValue()" +
					" and turma.id_disciplina = ?" +
					" except" +
					" select id_turma_agrupadora " +
					" from ensino.turma" +
					" where turma.id_disciplina = ?" +
					" and id_turma_agrupadora is not null)", params);
		} catch (Exception e){
			// não faz nada caso dê erro ao remover a turma agrupadora. 
		}
	}
	
	/** Converte uma turma regular em turma de ensino individual
	 * @param mov
	 * @throws DAOException 
	 */
	private Turma converteTurmaRegularEnsinoIndividual(MovimentoCadastro mov) throws DAOException {
		GenericDAO dao = getGenericDAO(mov);
		try {
			Turma turma = mov.getObjMovimentado();
			turma.setTipo(Turma.ENSINO_INDIVIDUAL);
			dao.update(turma);
			return turma;
		} finally {
			dao.close();
		}
	}
}
