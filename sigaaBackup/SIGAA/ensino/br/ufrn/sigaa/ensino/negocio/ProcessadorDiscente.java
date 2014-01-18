/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 17/03/2009
 * 
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dao.UsuarioGeralDAO;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.comum.sincronizacao.SincronizadorPessoas;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ParametrosGestoraAcademicaDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.complexo_hospitalar.DiscenteResidenciaMedicaDao;
import br.ufrn.sigaa.arq.dao.ensino.infantil.DiscenteInfantilDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CursoLatoDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.util.PessoaHelper;
import br.ufrn.sigaa.assistencia.dominio.SituacaoSocioEconomicaDiscente;
import br.ufrn.sigaa.complexohospitalar.dominio.DiscenteResidenciaMedica;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.SequenciaGeracaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.negocio.DiscenteCalculosHelper;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoOrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.negocio.ProcessadorOrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.negocio.calculos.CalculoPrazoMaximoFactory;
import br.ufrn.sigaa.ensino.infantil.dominio.DiscenteInfantil;
import br.ufrn.sigaa.ensino.infantil.dominio.ResponsavelDiscenteInfantil;
import br.ufrn.sigaa.ensino.latosensu.dominio.DiscenteLato;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.negocio.dominio.DiscenteMov;
import br.ufrn.sigaa.ensino.negocio.geracao_matricula.EstrategiaComposicaoMatricula;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.negocio.DiscenteStrictoValidator;
import br.ufrn.sigaa.ensino.stricto.negocio.calculos.AtualizarMesAtualPrazoConclusaoStricto;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.EstruturaCurricularTecnica;
import br.ufrn.sigaa.ensino.tecnico.dominio.ModalidadeCursoTecnico;
import br.ufrn.sigaa.negocio.PessoaValidator;
import br.ufrn.sigaa.negocio.ProcessadorPessoa;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosInfantil;
import br.ufrn.sigaa.parametros.dominio.ParametrosLatoSensu;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.ObservacaoDiscente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador responsável por implementar as regras de negócio
 * para o cadastro e alteração de discentes dos diversos níveis de ensino
 * @author Andre M Dantas
 *
 */
public class ProcessadorDiscente extends ProcessadorCadastro {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		setCurso(mov);
		validate(mov);

		DiscenteMov dMov = (DiscenteMov) mov;
		DiscenteAdapter discente = (DiscenteAdapter) dMov.getObjMovimentado();

		GenericDAO dao = getGenericDAO(dMov);
		try {
        		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_DISCENTE)) {
        		    if (discente.getPessoa().getContaBancaria() != null ) {
        		    	ProcessadorPessoa.persistirContaBancaria(discente.getPessoa(), dao);
					} 
        		    cadastrar((DiscenteMov) mov);
        		    persistirObservacaoDiscente(dMov, dao);	
        		} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_DISCENTE)) {
        		    ProcessadorPessoa.persistirContaBancaria(discente.getPessoa(), dao);
        		    alterar((MovimentoCadastro) mov);
        		} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_DADOS_PESSOAIS_DISCENTE)) {
        		    ProcessadorPessoa.persistirContaBancaria(discente.getPessoa(), dao);
        		    alterarDadosDiscenteGraduacao((MovimentoCadastro) mov);
        		} else if(mov.getCodMovimento().equals(SigaaListaComando.REMOVER_DISCENTE)){
        			excluirDiscente(mov, discente, dao);
        		} else if(mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_FORMA_INGRESSO_DISCENTE)){
        		    alterarFormaIngresso((MovimentoCadastro) mov);
        		} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_DISCENTE_TECNICO)) {
        			alterarDiscenteTecnico((DiscenteMov) mov);
        		} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_DISCENTE_LATO)) {
        			alterarDiscenteLato((DiscenteMov) mov);
        		}else if(mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_DISCENTE_STRICTO)){
        		    alterarDiscenteStricto((DiscenteMov) mov);
        		}else if(mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_DISCENTE_INFANTIL)){
                    alterarDiscenteInfantil((DiscenteMov) mov);
				}else if(mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_DISCENTE_RESIDENCIA)){
					alterarDiscenteResidenciaMedica((DiscenteMov) mov);
				}else if(mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_DISCENTE_MEDIO)){
					alterarDiscenteMedio((DiscenteMov) mov);
				}
		} finally {
		    dao.close();
		}

		return ((DiscenteMov) mov).getObjMovimentado();
	}

	/**
	 * Altera os dados do discente do nível médio
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private void alterarDiscenteMedio(DiscenteMov mov) throws NegocioException, ArqException, RemoteException {
		cadastrar(mov);		
	}

	/** Persiste as observações referentes ao discente
	 * @param mov
	 * @param dao
	 * @throws DAOException
	 */
	private void persistirObservacaoDiscente(DiscenteMov mov, GenericDAO dao) throws DAOException {
		DiscenteAdapter discente = (DiscenteAdapter) mov.getObjMovimentado();
		if(discente != null && discente.getObservacao() != null && !discente.getObservacao().trim().equals("")){
			ObservacaoDiscente observacao = new ObservacaoDiscente();
			observacao.setData(new Date());
			observacao.setRegistro(mov.getUsuarioLogado().getRegistroEntrada());
			observacao.setObservacao(discente.getObservacao());
			observacao.setDiscente(discente.getDiscente());
			dao.create(observacao);
		}
	}

	/**
	 * Altera os dados do discente de stricto.
	 * @param mov
	 * @throws RemoteException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void alterarDiscenteStricto(DiscenteMov mov) throws NegocioException, ArqException, RemoteException {
		GenericDAO dao = getGenericDAO(mov);
		try {
			DiscenteStricto disc = mov.getObjMovimentado();
			disc.setPessoa( dao.findByPrimaryKey( disc.getPessoa().getId() , Pessoa.class) );
			cadastrar(mov);
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Altera os dados do discente de nível de ensino técnico.
	 * @param mov
	 * @throws RemoteException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void alterarDiscenteTecnico(DiscenteMov mov) throws NegocioException, ArqException, RemoteException {
		cadastrar(mov);
	}
	
	/**
	 * Altera os dados do discente de nível de ensino lato-sensu.
	 * @param mov
	 * @throws RemoteException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void alterarDiscenteLato(DiscenteMov mov) throws NegocioException, ArqException, RemoteException {
		cadastrar(mov);
	}
	
	/**
	 * Altera discente de residência médica
	 * @param mov
	 * @throws RemoteException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void alterarDiscenteResidenciaMedica(DiscenteMov mov) throws NegocioException, ArqException, RemoteException {
		GenericDAO dao = getGenericDAO(mov);
		try {
			DiscenteResidenciaMedica disc = mov.getObjMovimentado();
			disc.setPessoa( dao.findByPrimaryKey( disc.getPessoa().getId() , Pessoa.class) );
			cadastrar(mov);
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Altera os dados do discente do ensino infantil
	 * @param mov
	 * @throws RemoteException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void alterarDiscenteInfantil(DiscenteMov mov) throws NegocioException, ArqException, RemoteException {
	    cadastrar(mov);
	}

	/** Exclui o discente
	 * @param mov
	 * @param discente
	 * @param dao
	 * @throws DAOException
	 * @throws ArqException
	 */
	private void excluirDiscente(Movimento mov, DiscenteAdapter discente, GenericDAO dao) throws DAOException, ArqException {
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente);

		/**
		 * Registrando a movimentação do aluno
		 */
		MovimentacaoAluno movimentacao = new MovimentacaoAluno();
		movimentacao.setDiscente(discente.getDiscente());
		movimentacao.setDataOcorrencia(new Date());
		movimentacao.setAnoReferencia( cal.getAno() );
		movimentacao.setPeriodoReferencia( cal.getPeriodo() );
		movimentacao.setAnoOcorrencia( cal.getAno() );
		movimentacao.setPeriodoOcorrencia( cal.getPeriodo() );
		movimentacao.setAtivo(true);
		movimentacao.setUsuarioCadastro( (Usuario) mov.getUsuarioLogado() );
		movimentacao.setTipoMovimentacaoAluno( new TipoMovimentacaoAluno(TipoMovimentacaoAluno.EXCLUIDO) );
		dao.create(movimentacao);
		/*
		 * Registrando em ObservacaoDiscente o motivo da exclusão
		 */
		ObservacaoDiscente observacao = new ObservacaoDiscente();
		observacao.setDiscente(discente.getDiscente());
		observacao.setMovimentacao(movimentacao);
		observacao.setData(new Date());
		observacao.setAtivo(true);
		observacao.setObservacao(discente.getObservacao());
		dao.create(observacao);

		DiscenteHelper.alterarStatusDiscente(discente, StatusDiscente.EXCLUIDO, mov, dao);
	}

	/**
	 * Gravar alterações de dados de discentes de graduação
	 *
	 * @param mov
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private Object alterarDadosDiscenteGraduacao(MovimentoCadastro mov) throws DAOException, NegocioException {

		GenericDAO dao = getGenericDAO(mov);

		DiscenteMov dMov = (DiscenteMov) mov;
		DiscenteAdapter discente = (DiscenteAdapter) dMov.getObjMovimentado();

		processaCPF(discente, dMov);

		try{
			// Cadastrar dados pessoais
			if (discente.getPessoa().getId() == 0) {
				discente.getPessoa().setDataCadastro(new Date());
				discente.getPessoa().setUltimaAtualizacao(new Date());
				dao.create(discente.getPessoa());
				dao.updateField(Discente.class, discente.getId(), "pessoa", discente.getPessoa());
			}
			
			// Se informada, cadastrar situação sócio-econômica do discente
			SituacaoSocioEconomicaDiscente situacaoSocioEconomica = dMov.getSituacaoSocioEconomica();
			if (situacaoSocioEconomica != null) {
				dao.createOrUpdate(situacaoSocioEconomica);
			}
		} finally {
			dao.close();
		}
		return discente;

	}

	/**
	 * Efetua a alteração no discente passado através do movimento
	 */
	@Override
	protected Object alterar(MovimentoCadastro mov) throws NegocioException, ArqException {

		DiscenteMov dMov = (DiscenteMov) mov;
		DiscenteAdapter discente = (DiscenteAdapter) dMov.getObjMovimentado();
		processaCPF(discente, dMov);

		if (discente instanceof DiscenteTecnico) {
			DiscenteTecnico discTecnico = (DiscenteTecnico) discente;
			discTecnico.setPeriodoIngresso(discTecnico.getTurmaEntradaTecnico().getPeriodoReferencia());
			discTecnico.setAnoIngresso(discTecnico.getTurmaEntradaTecnico().getAnoReferencia());
		}

		return super.alterar(mov);

	}

	/**
	 * Remove do banco o discente passado através do movimento
	 */
	@Override
	protected Object remover(MovimentoCadastro mov) throws NegocioException, ArqException  {
		
		GenericDAO dao = getGenericDAO(mov);
		try {
			DiscenteMov dMov = (DiscenteMov) mov;
			DiscenteAdapter discente = (DiscenteAdapter) dMov.getObjMovimentado();
			discente = dao.findByPrimaryKey(discente.getId(), Discente.class);
			
			// Testa se há outros discentes associados a mesma pessoa
			// e em caso positivo remove apenas o discente
			if(discente.getPessoa() != null){
				Collection<Discente> ls = dao.findByExactField(Discente.class, "pessoa.id", discente.getPessoa().getId());
				if(ls.size() > 1){
					((Discente)mov.getObjMovimentado()).setPessoa(null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return super.remover(mov);
	}

	/**
	 * Procedimento de atribuir um curso ao discente, de acordo com seu nível de ensino
	 * @param mov
	 * @throws DAOException 
	 * @throws Exception
	 */
	private void setCurso(Movimento mov) throws DAOException {
		DiscenteMov dMov = (DiscenteMov) mov;
		DiscenteAdapter discente = (DiscenteAdapter) dMov.getObjMovimentado();
		if (discente instanceof DiscenteTecnico) {
			DiscenteTecnico tec = (DiscenteTecnico) discente;
			
			if (tec.getTurmaEntradaTecnico() != null && tec.getTurmaEntradaTecnico().getCursoTecnico() != null){
				tec.setEstruturaCurricularTecnica( getGenericDAO(mov).findByPrimaryKey(tec.getEstruturaCurricularTecnica().getId(), EstruturaCurricularTecnica.class) );
					Curso curso = tec.getTurmaEntradaTecnico().getCursoTecnico();
					discente.setCurso(curso);
			}
		} else if(discente instanceof DiscenteLato) {
			DiscenteLato dl = (DiscenteLato) dMov.getObjMovimentado();
			Curso curso = dl.getTurmaEntrada().getCursoLato();
			discente.setCurso(curso);
		}

	}

	/** Cadastra o discente.
	 * chamado por {@link #execute(Movimento)} e {@link #alterarDiscenteStricto(DiscenteMov)}
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private void cadastrar(DiscenteMov mov) throws NegocioException, ArqException, RemoteException {
		GenericDAO dao = getGenericDAO(mov);
		DiscenteDao discenteDao = getDAO(DiscenteDao.class, mov);

		DiscenteAdapter disc = (DiscenteAdapter) mov.getObjMovimentado();
		
		if(disc.getStatus() <= 0)
			disc.setStatus(StatusDiscente.CADASTRADO);

		try {
			if( ! (mov.getCodMovimento().getId() == SigaaListaComando.ALTERAR_DISCENTE_STRICTO.getId()) ) {
				verificaDiscenteJaExistente(disc, mov);
			}
			
			// testes específicos para cada tipo de aluno
			if (mov.getObjMovimentado() instanceof DiscenteTecnico) {
				cadastrarDiscenteTecnico(disc, dao, mov);
			} else if( mov.getObjMovimentado() instanceof DiscenteLato) {
				cadastrarDiscenteLato(disc, mov);
			} else if( mov.getObjMovimentado() instanceof DiscenteGraduacao) {
				//regra de negócio específicas do cadastro de DiscenteGraduacao
				cadastrarDiscenteGraduacao(disc, dao, mov);
			} else if( mov.getObjMovimentado() instanceof DiscenteStricto) {
				//regras de negócio específicas do cadastro de DiscenteStricto
				cadastrarDiscenteStricto(disc, dao, mov);
			} else if( mov.getObjMovimentado() instanceof DiscenteInfantil) {
			    cadastrarDiscenteInfantil(disc, mov);
			} else if( mov.getObjMovimentado() instanceof DiscenteResidenciaMedica) {
				cadastrarDiscenteResidencia(disc, dao, mov);
			} else if( mov.getObjMovimentado() instanceof DiscenteMedio)
				//regra de negócio específicas do cadastro de DiscenteMedio
				cadastrarDiscenteMedio(disc, dao, mov);

			// Gerar matrícula
			if ( !mov.isDiscenteAntigo() && !mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_DISCENTE_STRICTO)
			        && !mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_DISCENTE_INFANTIL)
			        && !mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_DISCENTE_MEDIO) 
			        && (!mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_DISCENTE_RESIDENCIA))
			        && (!mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_DISCENTE_TECNICO))
			        && (!mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_DISCENTE_LATO))) {
				gerarMatricula(disc, dao);
			} else if ( mov.isDiscenteAntigo() && discenteDao.findByMatricula(disc.getMatricula()) != null ) {
				throw new NegocioException("Já existe um discente cadastrado com a matrícula informada");
			}

			// Limpar objetos transientes
			disc.getPessoa().anularAtributosVazios();

			// busca se já existe uma pessoa com esse cpf
			processaCPF(disc, mov);

			if (disc.getPessoa().getId() == 0) {
				disc.getPessoa().setDataCadastro(new Date());
				disc.getPessoa().setUltimaAtualizacao(new Date());
			}
			// cria o objeto no banco
			if( mov.getCodMovimento().equals( SigaaListaComando.CADASTRAR_DISCENTE ) ) {
				
				//Pega um novo idPessoa do banco comum
				if(disc.getDiscente().getPessoa().getId()==0){
					int idPessoa = SincronizadorPessoas.getNextIdPessoa();
					disc.getDiscente().getPessoa().setId(idPessoa);
				}	
				dao.create(disc.getDiscente());
				disc.setId(disc.getDiscente().getId());
				dao.create(disc);
				
			}
			
			if( mov.getCodMovimento().equals( SigaaListaComando.ALTERAR_DISCENTE_STRICTO )){
				((DiscenteStricto) disc).setArea( dao.refresh( ((DiscenteStricto) disc).getArea() ) );
				dao.update(disc);
				dao.update(disc.getDiscente());
			}
			
			if( mov.getCodMovimento().equals( SigaaListaComando.ALTERAR_DISCENTE_TECNICO )
				|| mov.getCodMovimento().equals( SigaaListaComando.ALTERAR_DISCENTE_LATO )){
					dao.update(disc);
					dao.update(disc.getDiscente());
			}
			if( mov.getCodMovimento().equals( SigaaListaComando.ALTERAR_DISCENTE_INFANTIL )){
				DiscenteInfantil discInfantil = (DiscenteInfantil) disc;
				dao.update(discInfantil.getResponsavel());
				if(discInfantil.getOutroResponsavel() != null)
					dao.update(discInfantil.getOutroResponsavel());
				dao.update(discInfantil);
				dao.update(discInfantil.getDiscente());
			}

			if( mov.getCodMovimento().equals( SigaaListaComando.ALTERAR_DISCENTE_RESIDENCIA) ) {
				dao.update(disc);
				dao.update(disc.getDiscente());
			}
			
			if( mov.getCodMovimento().equals( SigaaListaComando.ALTERAR_DISCENTE_MEDIO )){
				DiscenteMedio discMedio = (DiscenteMedio) disc;
				dao.update(discMedio);
				dao.update(discMedio.getDiscente());
			}
			
			if (disc.getNivel() == NivelEnsino.GRADUACAO && !mov.isDiscenteAntigo() && StatusDiscente.getStatusComVinculo().contains(disc.getStatus()) ) {
				DiscenteCalculosHelper.atualizarTodosCalculosDiscente((DiscenteGraduacao) disc, mov);
			}

			// cadastrar orientações de stricto
			  if (NivelEnsino.isAlgumNivelStricto(disc.getNivel()) &&  mov.getCodMovimento().equals( SigaaListaComando.CADASTRAR_DISCENTE )) {
				cadastrarOrientacoes(disc, mov);
			}


		} finally {
			discenteDao.close();
			dao.close();
		}
	}

	/**
	 * Cadastra os dados do discente do nível médio
	 * @param disc
	 * @param dao
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void cadastrarDiscenteMedio(DiscenteAdapter disc,
			GenericDAO dao, DiscenteMov mov) throws DAOException, NegocioException {

		dao.initialize(disc.getCurso());
		disc.setGestoraAcademica( dao.findByPrimaryKey(disc.getCurso().getUnidade().getId(), Unidade.class));
		disc.setNivel(disc.getCurso().getNivel());

		if( !mov.isDiscenteAntigo() && mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_DISCENTE) )
			disc.setStatus(StatusDiscente.ATIVO);	
		
	}

	/** Cadastra orientações acadêmicas do discente.
	 * chamado por {@link #cadastrar(DiscenteMov)}
	 * @param disc
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private void cadastrarOrientacoes(DiscenteAdapter disc, DiscenteMov mov) throws NegocioException, ArqException, RemoteException {

		if( !mov.getCodMovimento().equals( SigaaListaComando.CADASTRAR_DISCENTE ) ){
			/** se for alteração de discente não deve mexer nas orientações acadêmicas */
			return;
		}
		
		DiscenteStricto discStricto = (DiscenteStricto) disc;

		if( discStricto.getOrientacao() != null && (discStricto.getOrientacao().getServidor() == null || 
				discStricto.getOrientacao().getServidor() != null && discStricto.getOrientacao().getServidor().getId() == 0) )
			discStricto.setOrientacao(null);

		if( discStricto.getCoOrientacao() != null && (discStricto.getCoOrientacao().getServidor() == null || 
				discStricto.getCoOrientacao().getServidor() != null && discStricto.getCoOrientacao().getServidor().getId() == 0) )
			discStricto.setCoOrientacao(null);

		if(  discStricto.getOrientacao() != null ){
			MovimentoOrientacaoAcademica movOrientacoes = new MovimentoOrientacaoAcademica();
			 discStricto.getOrientacao().setTipoOrientacao(OrientacaoAcademica.ORIENTADOR);
			 discStricto.getOrientacao().setDiscente(disc.getDiscente());
			movOrientacoes.setCodMovimento(SigaaListaComando.CADASTRAR_ORIENTACAO_ACADEMICA);
			movOrientacoes.setSistema(mov.getSistema());
			movOrientacoes.setOrientacao( discStricto.getOrientacao() );
			movOrientacoes.setSubsistema(mov.getSubsistema());
			movOrientacoes.setUsuarioLogado(mov.getUsuarioLogado());
			(new ProcessadorOrientacaoAcademica()).execute(movOrientacoes);
		}

		if( discStricto.getCoOrientacao() != null ){
			MovimentoOrientacaoAcademica movCoOrientacoes = new MovimentoOrientacaoAcademica();
			discStricto.getCoOrientacao().setTipoOrientacao(OrientacaoAcademica.CoORIENTADOR);
			discStricto.getCoOrientacao().setDiscente(disc.getDiscente());
			movCoOrientacoes.setCodMovimento(SigaaListaComando.CADASTRAR_ORIENTACAO_ACADEMICA);
			movCoOrientacoes.setSistema(mov.getSistema());
			movCoOrientacoes.setOrientacao(discStricto.getCoOrientacao());
			movCoOrientacoes.setSubsistema(mov.getSubsistema());
			movCoOrientacoes.setUsuarioLogado(mov.getUsuarioLogado());
			(new ProcessadorOrientacaoAcademica()).execute(movCoOrientacoes);
		}

	
	}

	/**
	 * Cadastra um novo Discente do tipo Residência médica
	 * 
	 * @param disc
	 * @param dao
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private void cadastrarDiscenteResidencia(DiscenteAdapter disc, GenericDAO dao,  DiscenteMov mov) throws NegocioException, ArqException, RemoteException {
		//a gestora acadêmica do aluno de pós é o próprio programa de pós-graduação
		dao.initialize(disc.getCurso());
		disc.setGestoraAcademica( dao.findByPrimaryKey(disc.getCurso().getUnidade().getId(), Unidade.class));
		disc.setNivel(disc.getCurso().getNivel());

		if( !mov.isDiscenteAntigo() && mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_DISCENTE) )
			disc.setStatus(StatusDiscente.ATIVO);

		EstruturaCurricularDao daoEstrutura = getDAO(EstruturaCurricularDao.class, mov);
		DiscenteResidenciaMedicaDao daoDR = getDAO(DiscenteResidenciaMedicaDao.class, mov);
		
		try {

			/** APENAS DISCENTES REGULARES POSSUEM CURRICULO */
			if ( disc.isRegular() && isEmpty(disc.getCurriculo()) ) {
				Curriculo c = daoEstrutura.findMaisRecenteByCurso( disc.getCurso().getId() );
				if( c == null ){
					NegocioException e = new NegocioException();
					e.addErro("O discente não pode ser cadastrado pois o curso escolhido não possui um currículo ativo.");
					throw e;
				}
				disc.setCurriculo( c );
			}

			/** NÃO DEVE HAVER DISCENTES COM O MESMO CRM */
			DiscenteResidenciaMedica discR = mov.getObjMovimentado();
			long discentesMesmoCrm = daoDR.findByCrm(discR.getId(), discR.getCrm());
			if((discR.getId() == 0 && discentesMesmoCrm == 1) || discentesMesmoCrm > 1){
				throw new NegocioException("O discente não pode ser cadastrado pois o Nº do Registro no Conselho Profissional informado já possui " +
				"outro registro de discente.");
			}
			
		} finally {
			daoEstrutura.close();
			daoDR.close();
		}


	}
	
	/** Cadastra um discente Stricto.
	 * 
	 * chamado por {@link #cadastrar(DiscenteMov)}
	 * 
	 * @param disc
	 * @param dao
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private void cadastrarDiscenteStricto(DiscenteAdapter disc, GenericDAO dao,  DiscenteMov mov) throws NegocioException, ArqException, RemoteException {
		//a gestora acadêmica do aluno de pós é o próprio programa de pós-graduação
		dao.initialize(disc.getCurso());
		disc.setGestoraAcademica( dao.findByPrimaryKey(disc.getCurso().getUnidade().getId(), Unidade.class));
		disc.setNivel(disc.getCurso().getNivel());

		if( !mov.isDiscenteAntigo() && mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_DISCENTE) )
			disc.setStatus(StatusDiscente.ATIVO);

		/* APENAS DISCENTES REGULARES POSSUEM CURRICULO */
		if ( disc.isRegular() && isEmpty(disc.getCurriculo()) ) {
			EstruturaCurricularDao daoEstrutura = getDAO(EstruturaCurricularDao.class, mov);
			Curriculo c;
			try {
				c = daoEstrutura.findMaisRecenteByCurso( disc.getCurso().getId() );
			} finally{
				daoEstrutura.close();
			}
			if( c == null ){
				NegocioException e = new NegocioException();
				e.addErro("O discente não pode ser cadastrado pois o curso escolhido não possui um currículo ativo.");
				throw e;
			}
			disc.setCurriculo( c );
			
			new AtualizarMesAtualPrazoConclusaoStricto().processar((DiscenteStricto)disc, mov, false);			
			
		}else if( !disc.isRegular() ) {
			/* discente ESPECIAL NAO TEM curso e nem currículo */
			disc.setCurso(null);
			disc.setCurriculo(null);
		}


		DiscenteStricto ds = (DiscenteStricto) disc;
		if (ds.getLinha() != null && ds.getLinha().getId() == 0)
			ds.setLinha(null);
		if (ds.getArea() != null &&  ds.getArea().getId() == 0)
			ds.setArea(null);
		if( !ds.isRegular() && ds.getOrientacao() != null && !ds.getOrientacao().hasOrientadorDefinido() )
			ds.setOrientacao(null);
		if (ds.getProcessoSeletivo() != null &&  ds.getProcessoSeletivo().getId() == 0)
			ds.setProcessoSeletivo(null);

	}

	/**
	 * Prepara cadastro de discentes de graduação
	 *
	 * @param disc
	 * @param dao
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException 
	 */
	private void cadastrarDiscenteGraduacao(DiscenteAdapter disc, GenericDAO dao, DiscenteMov mov) throws NegocioException, ArqException {

		// A gestora acadêmica do aluno de graduação é SEMPRE a UFRN
//		disc.setGestoraAcademica( dao.findByPrimaryKey( UnidadeGeral.UNIDADE_DIREITO_GLOBAL, Unidade.class ) );
		disc.setGestoraAcademica(new Unidade(UnidadeGeral.UNIDADE_DIREITO_GLOBAL));
		
		// não é discente concluído
		if (!disc.isConcluido() && hasCursoAnteriorHabilitacaoDiferente(mov)) {
			((DiscenteGraduacao) disc).setColaGrau(false);
		}
		
		// Grava o arquivo digital do histórico, caso discente antigo
		if (mov.getArquivo() != null) {
			try {
				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo, 
					mov.getArquivo().getBytes(), mov.getArquivo().getContentType(), mov.getArquivo().getName());
				disc.setIdHistoricoDigital(idArquivo);
			} catch (Exception e) {
				throw new ArqException(e);
			}
		}

		DiscenteGraduacao discGraduacao = (DiscenteGraduacao) disc;
		if (disc.getTipo() == Discente.REGULAR) {
			EstruturaCurricularDao daoEstrutura = getDAO(EstruturaCurricularDao.class, mov);
			Curriculo curriculo;
			try {
				curriculo = daoEstrutura.findMaisRecenteByMatrizLeve( discGraduacao.getMatrizCurricular().getId() );
				if( curriculo == null ){
					MatrizCurricular matriz = dao.findByPrimaryKey(discGraduacao.getMatrizCurricular().getId(), MatrizCurricular.class);
					NegocioException e = new NegocioException();
					e.addErro("O discente não pode ser cadastrado pois a matriz curricular "+matriz.getDescricao()+" não possui um curriculo ativo.");
					throw e;
				} else {
					Collection<ComponenteCurricular> componentes = daoEstrutura.findComponentesByCurriculo(curriculo.getId());
					if( isEmpty(componentes) ){
						NegocioException e = new NegocioException();
						e.addErro("O discente não pode ser cadastrado pois o currículo mais recente não possui os componentes cadastrados.");
						throw e;
					}
				}
			} finally {
				daoEstrutura.close();
			}
			
			discGraduacao.setCurriculo( curriculo );
			
			int prazoMaximo = CalculoPrazoMaximoFactory.getCalculoGraduacao(discGraduacao).calcular(discGraduacao, mov);
			disc.setPrazoConclusao(prazoMaximo);
		} else {
			discGraduacao.setCurso(null);
			discGraduacao.setCurriculo(null);
			discGraduacao.setMatrizCurricular(null);
		}
	}

	/** Cadastra discentes de Lato Sensu.
	 * chamado por {@link #cadastrar(DiscenteMov)}
	 * @param disc
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void cadastrarDiscenteLato(DiscenteAdapter disc, Movimento mov) throws DAOException, NegocioException {
		disc.setTipo(Discente.REGULAR);
		DiscenteLato discLato = (DiscenteLato) disc;
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		discLato.setMesEntrada(cal.get(Calendar.MONTH)+1);

		/*
		 * validação da quantidade de alunos do curso
		 */
		if ( !mov.getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_LATO) ) {
			CursoLatoDao daoCurso = getDAO(CursoLatoDao.class, mov);
			int matriculados = 0;
			try {
				matriculados = daoCurso.getNumeroDiscentesMatriculados(discLato.getCursoLato().getId());
				
				discLato.setProcessoSeletivo( daoCurso.findByPrimaryKey(discLato.getProcessoSeletivo().getId(), ProcessoSeletivo.class));
				
				if ( !discLato.getProcessoSeletivo().getVaga().equals( discLato.getCursoLato().getNumeroVagas() ) ) {
					daoCurso.updateField(ProcessoSeletivo.class, discLato.getProcessoSeletivo().getId(), 
							"vaga", discLato.getCursoLato().getNumeroVagas());
					
					discLato.getProcessoSeletivo().setVaga(discLato.getCursoLato().getNumeroVagas());
				}
				
			} finally {
				daoCurso.close();
			}
			
			if( ++matriculados > discLato.getProcessoSeletivo().getVaga() && discLato.getId() == 0 ){
				NegocioException e = new NegocioException();
				e.addErro("O discente não pode ser matriculado nesse curso, pois " +
						"não há mais vagas disponíveis.");
				throw e;
			}
		}
		
	}

	/** Cadastra um Discente Técnico.
	 * chamado por {@link #cadastrar(DiscenteMov)}
	 * @param disc
	 * @param dao
	 * @throws NegocioException
	 * @throws ArqException 
	 */
	private void cadastrarDiscenteTecnico(DiscenteAdapter disc, GenericDAO dao, DiscenteMov mov) throws NegocioException, ArqException {
		// Grava o arquivo digital do histórico, caso discente antigo
		if (mov.getFile() != null) {
			try {
				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo, 
					mov.getFile().getFileData(), mov.getFile().getContentType(), mov.getFile().getFileName());
				disc.setIdHistoricoDigital(idArquivo);
			} catch (Exception e) {
				throw new ArqException(e);
			}
		}
		disc.getPessoa().setGrauFormacao(null);
		disc.setTipo(Discente.REGULAR);
		DiscenteTecnico discTecnico = (DiscenteTecnico) disc;
		discTecnico.setOpcaoPoloGrupo(null);
		
		CursoTecnico curso = (CursoTecnico) discTecnico.getCurso();
		if (curso.getModalidadeCursoTecnico() != null && !discTecnico.isConcluiuEnsinoMedio()
				&& curso.getModalidadeCursoTecnico().getId() == ModalidadeCursoTecnico.SUBSEQUENTE) {
			NegocioException e = new NegocioException();
			e.addErro("O discente não pode ser cadastrado nesse curso, pois "
							+ "ainda não concluiu o ensino médio");
			throw e;
		}
		
		if (curso.getId() != ParametroHelper.getInstance().getParametroInt(ParametrosTecnico.ID_CURSO_METROPOLE_DIGITAL_PARA_CONVOCACAO)){
			if(!mov.isDiscenteAntigo()) {
				discTecnico.setPeriodoIngresso(discTecnico.getTurmaEntradaTecnico().getPeriodoReferencia());
				discTecnico.setAnoIngresso(discTecnico.getTurmaEntradaTecnico().getAnoReferencia());
			}
	
			//setando prazo de conclusão
			EstruturaCurricularTecnica ect = dao.findByPrimaryKey(discTecnico.getEstruturaCurricularTecnica().getId(), EstruturaCurricularTecnica.class);
			int prazoMaximo = DiscenteHelper.somaSemestres(discTecnico.getAnoIngresso(), discTecnico.getPeriodoIngresso(), ect.getPrazoMaxConclusao()-1);
			disc.setPrazoConclusao(prazoMaximo);
		}
	}
	
	/** Cadastra um Discente Infantil.
     * chamado por {@link #cadastrar(DiscenteMov)}
     * @param disc
     * @param dao
     * @throws DAOException
	 * @throws NegocioException 
     * @throws NegocioException
     */
	private void cadastrarDiscenteInfantil(DiscenteAdapter disc, Movimento mov) throws DAOException, NegocioException {
	    ParametroHelper params = ParametroHelper.getInstance();
	    disc.getPessoa().setGrauFormacao(null);
        disc.setTipo(Discente.REGULAR);
        disc.setNivel(NivelEnsino.INFANTIL);
        disc.setFormaIngresso(null);
        disc.setCurso(new Curso(params.getParametroInt(ParametrosInfantil.ID_CURSO_INFANTIL)));
        disc.setGestoraAcademica( new Unidade(params.getParametroInt(ParametrosInfantil.ID_NEI)));
        
        DiscenteInfantil discInfantil = (DiscenteInfantil) disc;
        
        /** NÃO DEVE HAVER DISCENTES COM O MESMO NOME E MESMA DATA DE NASCIMENTO */
        DiscenteInfantilDao dao = getDAO(DiscenteInfantilDao.class, mov);
        ArrayList<DiscenteInfantil> discentesEncontrados = (ArrayList<DiscenteInfantil>) dao.findNomeDataNascimento(discInfantil.getNome(), discInfantil.getPessoa().getDataNascimento());
        if((disc.getId() == 0 && discentesEncontrados.size() == 1) || (discentesEncontrados.size() > 1) ||
        		(discentesEncontrados.size() == 1 && !disc.equals(discentesEncontrados.get(0))))
        	throw new NegocioException("A pessoa informada já foi cadastrada como aluno dessa escola.");
        
        processaCPFResponsavelDiscenteInfantil(discInfantil.getResponsavel(), mov);
        if(discInfantil.getOutroResponsavel().getPessoa().getCpf_cnpj() > 0)
        	processaCPFResponsavelDiscenteInfantil(discInfantil.getOutroResponsavel(), mov);
        else
        	discInfantil.setOutroResponsavel(null);
        
        if (disc.getPessoa().getCpf_cnpj() == 0)
			disc.getPessoa().setCpf_cnpj(null);
	}
	/**
	 * Usa a mesma pessoa para o responsável do discente infantil, caso ela já exista.
	 * chamado por {@link #cadastrarDiscenteInfantil(Discente,Movimento)}
	 * @param responsavel
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void processaCPFResponsavelDiscenteInfantil(ResponsavelDiscenteInfantil responsavel, Movimento mov) throws DAOException, NegocioException {
		
		if (responsavel.getPessoa() == null) {
			throw new NegocioException("O responsável informado não possui dados pessoais associados!");
		}
	    // Guardando as informações vindas do formulário para popular posteriormente, caso necessário
	    String nome = responsavel.getPessoa().getNome();
	    String email = responsavel.getPessoa().getEmail();
	    char sexo = responsavel.getPessoa().getSexo();
	    Date dataNascimento = responsavel.getPessoa().getDataNascimento();
	    Short codigoAreaCelular = responsavel.getPessoa().getCodigoAreaNacionalTelefoneCelular();
	    String celular = responsavel.getPessoa().getCelular();
	    Short codigoAreaFixo = responsavel.getPessoa().getCodigoAreaNacionalTelefoneFixo();
	    String telefoneFixo = responsavel.getPessoa().getTelefone();
	    
        responsavel.getPessoa().setOrigem(NivelEnsino.INFANTIL);
        PessoaDao dao = getDAO(PessoaDao.class, mov);

        try {
            
            // Verificar se foi definido um CPF
            if (responsavel.getPessoa().getCpf_cnpj() != null && responsavel.getPessoa().getCpf_cnpj() > 0) {
                Integer id = null;
                // Verificar se o CPF existe no banco
                id = dao.findIdByCpf(responsavel.getPessoa().getCpf_cnpj());
            	responsavel.getPessoa().setId(id > 0 ? id : 0);
                
                // Verificar se a pessoa já possuía um CPF válido e este foi alterado
                Pessoa pessoaExistente = null;
                if ( responsavel.getPessoa().getId() > 0 ) {
                    pessoaExistente = dao.findCompleto( responsavel.getPessoa().getId() );

                    // A alteração de CPF não é permitida
                    if (pessoaExistente != null
                            && pessoaExistente.getCpf_cnpj() != null
                            && pessoaExistente.getCpf_cnpj() != 0
                            && !pessoaExistente.getCpf_cnpj().equals(responsavel.getPessoa().getCpf_cnpj())) {

                        throw new NegocioException("A alteração para este CPF não é permitida. " +
                        "Caso necessário, contate os administradores do sistema para realizar esta alteração.");
                    }
                }

                if ( id == 0 && pessoaExistente != null ) {
                    id = pessoaExistente.getId();
                }

                if (id != null && id > 0) {
                    Pessoa pessoaComCpfInformado = dao.findByPrimaryKey( id, Pessoa.class );

                    // Se existir a pessoa, comparar alguns dados (id e nome)
                    if (responsavel.getPessoa().getId() == id
                    		|| StringUtils.compareInAscii(responsavel.getPessoa().getNome(), pessoaComCpfInformado.getNome()) ) {
                        
                        // Populando as informações vindas do formulário
                        pessoaComCpfInformado.setNome(nome);
                        pessoaComCpfInformado.setDataNascimento(dataNascimento);
                        pessoaComCpfInformado.setEmail(email);
                        pessoaComCpfInformado.setSexo(sexo);
                        pessoaComCpfInformado.setCodigoAreaNacionalTelefoneCelular(codigoAreaCelular);
                        pessoaComCpfInformado.setCelular(celular);
                        pessoaComCpfInformado.setCodigoAreaNacionalTelefoneFixo(codigoAreaFixo);
                        pessoaComCpfInformado.setTelefone(telefoneFixo);
                                                
                        responsavel.setPessoa(pessoaComCpfInformado);
                    } else {
                        throw new NegocioException("O CPF informado não pode ser associado a este responsável " +
                                " pois pertence a " + pessoaComCpfInformado.getNome());
                    }
                }

            } else {
            	throw new NegocioException("É necessário informar o CPF do responsável.");
            }

            responsavel.getPessoa().setUltimaAtualizacao(new Date());
            responsavel.getPessoa().setEnderecoContato(null);
            ProcessadorPessoa.anularTransientObjects(responsavel.getPessoa());
            responsavel.getPessoa().anularAtributosVazios();
            
            if ( responsavel.getPessoa().getId() != 0 ) {
                // Gravar histórico dos dados pessoais anteriores
                try {
                    PessoaHelper.alteraCriaPessoa( responsavel.getPessoa(), dao, mov.getUsuarioLogado().getRegistroEntrada(), mov.getCodMovimento().getId() );
                } catch (NegocioException e) {
                    e.printStackTrace();
                    throw e;
                }
                // Atualizar dados do responsável
				dao.clearSession();
				dao.update(responsavel.getPessoa());
            } else {
            	responsavel.getPessoa().setId(SincronizadorPessoas.getNextIdPessoa());
            }
        } finally {
            dao.close();
        }
    }

	/**
	 * Verifica se o Discente já existe no nível corrente de ensino
	 * utilizando as informações relacionadas deste Discente,
	 * caso não exista é criado um novo Discente. 
	 * 
	 * @param disc
	 * @param dao
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void processaCPF(DiscenteAdapter disc, Movimento mov) throws DAOException, NegocioException {
		disc.getPessoa().setOrigem(disc.getNivel());
		PessoaDao dao = getDAO(PessoaDao.class, mov);
		UsuarioDao usuarioDao = getDAO(UsuarioDao.class, mov);
		DiscenteDao discenteDao = getDAO(DiscenteDao.class, mov);
		
		if (disc.getPessoa() == null) {
			throw new NegocioException("O discente informado não possui dados pessoas associados!");
		}

		try {
			Integer idPessoaAntigo = null;
			
			// Verificar se foi definido um CPF
			if (disc.getPessoa().getCpf_cnpj() != null && disc.getPessoa().getCpf_cnpj() > 0) {
				Integer id = null;

				// Verificar se a pessoa já possuía um cpf válido e este foi alterado
				Pessoa pessoaExistente = null;
				if ( disc.getPessoa().getId() > 0 ) {
					pessoaExistente = dao.findCompleto( disc.getPessoa().getId() );

					// A alteração é permitida para o DAE (tarefa 13311)
					if (!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.DAE)
							&& pessoaExistente != null
							&& pessoaExistente.getCpf_cnpj() != null
							&& pessoaExistente.getCpf_cnpj() != 0
							&& !pessoaExistente.getCpf_cnpj().equals(disc.getPessoa().getCpf_cnpj())
							&& dao.findByCpf(disc.getPessoa().getCpf_cnpj()) != null) {

						throw new NegocioException("A alteração para este CPF não é permitida. " +
						"Caso necessário, contate os administradores do sistema para realizar esta alteração.");
					}
				}

				// Verificar se o cpf que está se desejando alterar é de uma outra pessoa
				id = dao.findIdByCpf(disc.getPessoa().getCpf_cnpj());

				if ( id == 0 && pessoaExistente != null ) {
					id = pessoaExistente.getId();
				}

				if (id != null && id > 0) {
					Pessoa pessoaComCpfInformado = dao.findByPrimaryKey( id, Pessoa.class );

					// Se existir a pessoa, comparar alguns dados pessoais (nome e nome da mãe)
					if (disc.getPessoa().getId() == id
						|| StringUtils.compareInAscii(disc.getPessoa().getNome(), pessoaComCpfInformado.getNome())
						|| (StringUtils.compareInAscii(disc.getPessoa().getNomeMae(), pessoaComCpfInformado.getNomeMae()) && disc.getPessoa().getDataNascimento().equals(pessoaComCpfInformado.getDataNascimento())) ) {
						
						idPessoaAntigo = disc.getPessoa().getId();
						disc.getPessoa().setId(id);
					} else {
						throw new NegocioException("O CPF informado não pode ser associado a este discente " +
								" pois pertence a " + pessoaComCpfInformado.getNome());
					}
				}

			} else if( !disc.getPessoa().isInternacional() && !NivelEnsino.isEnsinoBasico(disc.getNivel()) ){
				throw new NegocioException("Atenção! Para efetuar a atualização dos dados é necessário definir o CPF do discente");
			}
			ProcessadorPessoa.anularTransientObjects(disc.getPessoa());
			if ( disc.getPessoa().getId() != 0 ) {

				// Gravar histórico dos dados pessoais anteriores
				try {
					PessoaHelper.alteraCriaPessoa( disc.getPessoa(), dao, mov.getUsuarioLogado().getRegistroEntrada(), mov.getCodMovimento().getId() );
				} catch (NegocioException e) {
					e.printStackTrace();
					throw e;
				}

				// Atualizar dados do discente
				disc.getPessoa().setUltimaAtualizacao(new Date());
				dao.clearSession();
				disc.getPessoa().anularAtributosVazios();
				ProcessadorPessoa.anularTransientObjects(disc.getPessoa());
				if (disc.getPessoa().getContaBancaria() != null) 
					dao.update(disc.getPessoa().getContaBancaria());
				dao.update(disc.getPessoa());
				dao.updateField(Discente.class, disc.getId(), "pessoa", disc.getPessoa().getId());

				// Atualizar todos os discentes que possuíam o id pessoa antigo
				if (idPessoaAntigo != null) {
					List<Integer> idsDiscentes = discenteDao.findIdsDiscenteByPessoa(new Pessoa(idPessoaAntigo));
					for (Integer idDiscente : idsDiscentes) {
						dao.updateField(Discente.class, idDiscente, "pessoa", disc.getPessoa().getId());
					}
				} else {
					idPessoaAntigo = disc.getPessoa().getId();
				}
				
				if (!isEmpty(idPessoaAntigo)) {
					// Atualizar usuários do discente, se modificado
					List<Usuario> usuarios = usuarioDao.findByPessoa(new Pessoa(idPessoaAntigo));
					DataSource dsAdministrativo = Database.getInstance().getDataSource(Sistema.getSistemaAdministrativoAtivo());
					UsuarioGeralDAO daoUsuario = new UsuarioGeralDAO(dsAdministrativo, Database.getInstance().getSigaaDs(), Database.getInstance().getComumDs());
					for (Usuario usuario : usuarios ) {
						daoUsuario.atualizarIdPessoa(usuario.getId(), disc.getPessoa().getId(), disc.getPessoa().getCpf_cnpj());
						if (disc.getPessoa().getEmail() != null) {
							daoUsuario.atualizarDadosPessoaisTodosOsBancos(usuario.getId(), disc.getPessoa().getCpf_cnpj(), disc.getPessoa().getEmail(), usuario.getRamal());
						}
					}
				}
				
			}
		} finally {
			dao.close();
			usuarioDao.close();
			discenteDao.close();
		}
	}

	/** Padroniza a matrícula.
	 * @param seq
	 * @param nivel
	 * @param ano
	 * @return matricula padronizada no formato [ANO + NIVEL + SEQUENCIAL] 
	 */
	public Long getPadraoMatricula(int seq, char nivel, int ano) {
		return new Long(ano + "" + NivelEnsino.tabela.get(nivel) + "" + UFRNUtils.completaZeros(seq,5));
	}

	/**
	 * Cria uma matrícula para todo tipo de aluno
	 * @param disc
	 */
	public void gerarMatricula(DiscenteAdapter disc, GenericDAO dao) throws ArqException {
		// não gera número de matrícula para discente pendente de cadastro 
		if (!disc.isMetropoleDigital() && disc.getStatus() == StatusDiscente.PENDENTE_CADASTRO) return;
		String classe = ParametroHelper.getInstance().getParametro(ParametrosGerais.ESTRATEGIA_COMPOSICAO_MATRICULA);
		EstrategiaComposicaoMatricula estrategia = ReflectionUtils.newInstance(classe);
		
		SequenciaGeracaoMatricula sequencia = estrategia.getSequenciaGeracaoMatricula(dao.getSession(), disc.getDiscente());
		
		//	Caso a sequência seja nova, persistir
		if ( sequencia.getId() == 0 ) {
			dao.create(sequencia);
		} else {
			// Se já existir, incrementar a sequência e persistir
			sequencia = (SequenciaGeracaoMatricula)  dao.findByPrimaryKeyLock(sequencia.getId(),SequenciaGeracaoMatricula.class);
			sequencia.incrementarSequencia();
			dao.update(sequencia);
		}

		// Compõe a matrícula a partir da sequência e do discente.
		Long matriculaGerada = estrategia.compoeMatricula(sequencia, disc.getDiscente());
		disc.setMatricula( matriculaGerada );
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		DiscenteMov m = (DiscenteMov) mov;
		ListaMensagens erros = new ListaMensagens();

		/** se o movimento for ALTERAR_FORMA_INGRESSO basta verificar se o discente possui uma situação válida  */
		if(mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_FORMA_INGRESSO_DISCENTE)){
			checkRole(new int[] {SigaaPapeis.DAE, SigaaPapeis.CDP}, m);
			DiscenteAdapter d = m.getObjMovimentado();
			int status = d.getStatus();
			if( status != StatusDiscente.ATIVO && status != StatusDiscente.CADASTRADO && status != StatusDiscente.FORMANDO &&
					status != StatusDiscente.AFASTADO && status != StatusDiscente.GRADUANDO && status != StatusDiscente.TRANCADO ){
				erros.getMensagens().add( new MensagemAviso( "Não é possível alterar a forma de ingresso de discentes com a situação " + d.getStatusString(), TipoMensagemUFRN.ERROR ) );
			}

			return;
		}

		// validações específicas para cada tipo de aluno
		if (m.getObjMovimentado() instanceof DiscenteTecnico) {
			if(!mov.getCodMovimento().equals(SigaaListaComando.REMOVER_DISCENTE)){
				DiscenteTecnico disc = (DiscenteTecnico) m.getObjMovimentado();
				PessoaValidator.validarDadosPessoais(disc.getPessoa(), null, PessoaValidator.DISCENTE, erros);
				DiscenteValidator.validarDadosDiscenteTecnico(disc, null, m.isDiscenteAntigo(), erros);

				if ( disc.getPessoa().getCpf_cnpj() != null && disc.getPessoa().getCpf_cnpj() == 0 ) {
					disc.getPessoa().setCpf_cnpj(null);
				}
			}
		} else if (m.getObjMovimentado() instanceof DiscenteInfantil) {
			if(!mov.getCodMovimento().equals(SigaaListaComando.REMOVER_DISCENTE)){
				DiscenteInfantil inf = (DiscenteInfantil) m.getObjMovimentado();
				if (inf.getOutroResponsavel().getEscolaridade().getId() <=0 )
					inf.getOutroResponsavel().setEscolaridade(null);
			}
		} else if (m.getObjMovimentado() instanceof DiscenteLato) {
			if(!mov.getCodMovimento().equals(SigaaListaComando.REMOVER_DISCENTE)){
				DiscenteLato disc = (DiscenteLato) m.getObjMovimentado();
				PessoaValidator.validarDadosPessoais(disc.getPessoa(), null, PessoaValidator.DISCENTE, erros);
				DiscenteValidator.validarDadosDiscenteLato(disc, null, erros);
			}
		}else if (m.getObjMovimentado() instanceof DiscenteGraduacao) {
			DiscenteGraduacao disc = (DiscenteGraduacao) m.getObjMovimentado();
			PessoaValidator.validarDadosPessoais(disc.getPessoa(), null, PessoaValidator.DISCENTE, erros);
			DiscenteValidator.validarDadosDiscenteGraduacao(disc, m.isDiscenteAntigo(), erros);
		}else if (m.getObjMovimentado() instanceof DiscenteStricto) {
			DiscenteStricto disc = (DiscenteStricto) m.getObjMovimentado();
			if( !mov.getCodMovimento().equals( SigaaListaComando.ALTERAR_DISCENTE_STRICTO ) )
				PessoaValidator.validarDadosPessoais(disc.getPessoa(), null, PessoaValidator.DISCENTE, erros);
			if( !mov.getCodMovimento().equals( SigaaListaComando.ALTERAR_DADOS_PESSOAIS_DISCENTE ) )
				DiscenteStrictoValidator.validarDadosGerais(disc, m.isDiscenteAntigo(), (Usuario) mov.getUsuarioLogado(), mov,erros);
		}else if(m.getObjMovimentado() instanceof DiscenteResidenciaMedica){
			if(!mov.getCodMovimento().equals(SigaaListaComando.REMOVER_DISCENTE)){
				DiscenteResidenciaMedica disc = (DiscenteResidenciaMedica) m.getObjMovimentado();
				PessoaValidator.validarDadosPessoais(disc.getPessoa(), null, PessoaValidator.DISCENTE, erros);
				DiscenteValidator.validarDadosDiscenteResidenciaMedica(disc, erros);
			}
		}

		Pessoa pessoa = ((DiscenteAdapter)m.getObjMovimentado()).getPessoa();
		if (pessoa.getTipoRaca() != null && pessoa.getTipoRaca().getId() == 0)
			pessoa.setTipoRaca(null);
		if (pessoa.getEstadoCivil() != null && pessoa.getEstadoCivil().getId() == 0)
			pessoa.setEstadoCivil(null);
		if (pessoa.getMunicipio() != null && pessoa.getMunicipio().getId() == 0)
			pessoa.setMatricula(null);
		
		checkValidation(erros);

	}



	/**
	 * Toda alteração de status do discente deve ser registrada em
	 * AlteracaoStatusDiscente
	 *
	 * @param discente
	 * @param status
	 * @param mov
	 * @throws ArqException
	 * @throws Exception
	 */
	public void persistirAlteracaoStatus(DiscenteAdapter discente, int status,
			Movimento mov) throws ArqException  {
		ParametrosGestoraAcademicaDao dao = getDAO(ParametrosGestoraAcademicaDao.class, mov);
		try {
			DiscenteHelper.alterarStatusDiscente(discente, status, mov, dao);
		} finally {
			dao.close();
		}
	}

	/**
	 * Altera a forma de ingresso do aluno
	 * @param mov
	 * @throws DAOException
	 */
	private void alterarFormaIngresso(MovimentoCadastro mov) throws DAOException {
		DiscenteAdapter d = (DiscenteAdapter) mov.getObjMovimentado();

		GenericDAO dao = getGenericDAO(mov);
		dao.updateField(Discente.class, d.getId(), "formaIngresso", d.getFormaIngresso() );
		dao.updateField(Discente.class, d.getId(), "periodoIngresso", d.getPeriodoIngresso());
		if (d instanceof DiscenteGraduacao)
			dao.updateField(DiscenteGraduacao.class, d.getId(), "colaGrau", ((DiscenteGraduacao) d).getColaGrau());
	}
	
	/** Determina se o discente vai constar na lista de colação de grau.
	 * @return
	 * @throws DAOException 
	 */
	private boolean hasCursoAnteriorHabilitacaoDiferente(DiscenteMov mov) throws DAOException {
		DiscenteGraduacao discente = mov.getObjMovimentado();
		// busca cadastros anteriores do discente
		DiscenteDao discenteDao = getDAO(DiscenteDao.class, mov);
		
		try{
			MatrizCurricular matrizDiscente = discenteDao.findByPrimaryKey(discente.getMatrizCurricular().getId(), MatrizCurricular.class, 
					"id", "curso.id", "grauAcademico.id" ,"habilitacao.id");
			discente.setMatrizCurricular(matrizDiscente);
			
			Collection<DiscenteGraduacao> discentes = discenteDao.findByPessoaSituacao(discente.getPessoa().getId(), StatusDiscente.CONCLUIDO, StatusDiscente.GRADUANDO);
			for (DiscenteGraduacao dg : discentes) {
				// se mesmo curso && mesmo grau acadêmico && habilitação diferente
				if (dg.getCurso().equals(discente.getCurso())
						&& dg.getMatrizCurricular().getGrauAcademico().equals(discente.getMatrizCurricular().getGrauAcademico())
						&& dg.getMatrizCurricular().getHabilitacao() != null
						&& !dg.getMatrizCurricular().getHabilitacao().equals(discente.getMatrizCurricular().getHabilitacao()))
					return true;
			}
		} finally {
			discenteDao.close();
		}
		return false;
	}
	
	/**
	 * Verifica se o discente que está sendo cadastrado possui outro vínculo ativo de acordo
	 * com as regras específicas de cada nível ensino:
	 * <br/>
	 * <ul>
	 * 	<li>Para o nível técnico, só deve validar se o discente já está cadastrado na mesma unidade gestora acadêmica.</li>
	 * 	<li>Para o nível lato, só deve validar se o discente já está cadastro no mesmo curso.</li>
	 * 	<li>Para os demais níveis, só deve validar somente se não estiver graduando ou em homologação.</li>
	 * </ul>
	 * @param disc
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void verificaDiscenteJaExistente(DiscenteAdapter disc, Movimento mov) throws DAOException, NegocioException {
		
		if (disc.getStatus() != StatusDiscente.PENDENTE_CADASTRO) {
			DiscenteDao ddao = getDAO(DiscenteDao.class, mov);
			
			List<Integer> naoValidarStatus = new ArrayList<Integer>();
			naoValidarStatus.add(StatusDiscente.EM_HOMOLOGACAO);
			naoValidarStatus.add(StatusDiscente.GRADUANDO);
			
			try {
				Collection<DiscenteAdapter> outrosRegistros = ddao.findByDadosPessoaisMesmoNivel(disc);
				for (DiscenteAdapter reg : outrosRegistros) {
					
					if (disc.isFormacaoComplementar() && disc.getCurso().getId() != reg.getCurso().getId()){
						continue;
					} else if ((disc.isTecnico())
							&& (disc.getGestoraAcademica().getId() != reg.getGestoraAcademica().getId()
								|| disc.getCurso().isPermiteAlunosVariosVinculos())) {
						continue;
					} else if(disc.isLato()){
						if( !disc.getCurso().equals(reg.getCurso()) &&
								ParametroHelper.getInstance().getParametroBoolean( 
										ParametrosLatoSensu.PERMITE_CADASTRAR_DISCENTE_COM_MATRICULA_ATIVA ) )
							continue;
						throw new NegocioException("O discente não pode ser cadastrado pois já possui outro " +
									"registro de discente (mat. "+ reg.getMatricula() +") ainda com vínculo no curso " + 
									reg.getCurso().getNomeCompleto());
					} else if (disc.isGraduacao()) {
						if (StatusDiscente.getStatusComVinculo().contains(reg.getStatus()) && !naoValidarStatus.contains(reg.getStatus())) {

							// O discente pode ter um vinculo Regular e outro Especial
							// mas nunca dois do mesmo tipo
							if ( DiscenteGraduacaoValidator.isMesmoTipoDiscente(disc, reg) ) {
								throw new NegocioException("O discente não pode ser cadastrado pois já possui outro " +
										"registro de discente (mat. "+ reg.getMatricula() +") ainda com vínculo em cursos do mesmo nível de ensino.");
							}
						}
					} else {
						if (StatusDiscente.getStatusComVinculo().contains(reg.getStatus()) && !naoValidarStatus.contains(reg.getStatus())) 
							throw new NegocioException("O discente não pode ser cadastrado pois já possui outro " +
									"registro de discente (mat. "+ reg.getMatricula() +") ainda com vínculo em cursos do mesmo nível de ensino.");
					}
						

				}
			} finally {
				ddao.close();
			}
		}
		
	}
}