/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 09/11/2007
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxLength;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.time.DateUtils;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.ensino.DocenteExternoDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.BancaPosDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.DadosDefesaDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.DiscenteStrictoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.EquipeProgramaDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.jsf.HistoricoMBean;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.stricto.dao.MembroBancaPosDAO;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.ensino.stricto.dominio.DadosDefesa;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.EquipePrograma;
import br.ufrn.sigaa.ensino.stricto.dominio.MembroBancaPos;
import br.ufrn.sigaa.ensino.stricto.dominio.ParametrosProgramaPos;
import br.ufrn.sigaa.ensino.stricto.negocio.MovimentoBancaAlunoConcluido;
import br.ufrn.sigaa.ensino.stricto.negocio.ParametrosProgramaPosHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Cadastro de {@link BancaPos Banca de defesa/qualifica��o de p�s-gradua��o} e seus {@link MembroBancaPos membros}.
 * @author Andr� Dantas
 */
@Component("bancaPos") @Scope("session")
public class BancaPosMBean extends SigaaAbstractController<BancaPos> implements OperadorDiscente {

	/** Membro da banca de p�s. */
	private MembroBancaPos membroBanca;

	/** Cole��o de bancas cadastradas do discente. */
	private Collection<BancaPos> bancasDoDiscente;

	/** Cole��o de trabalhos finais do discente. */
	private Collection<SelectItem> trabalhosFinais;

	/** Discente que est� defendendo o trabalho. */
	private DiscenteStricto discente;

	/** Lista de SelectItem de sub�reas de conhecimento. */
	private List<SelectItem> subAreas = new ArrayList<SelectItem>();

	/** Lista de SelectItem de especialidades. */
	private List<SelectItem> especialidades = new ArrayList<SelectItem>();

	/** Lista de SelectItem de �reas de conhecimento. */
	private List<SelectItem> areas = new ArrayList<SelectItem>();

	/** Indica se a opera��o � de cadastro de banca. */
	private boolean cadastrar;
	
	/** Indica se � cadastro de defesa antiga. */
	private boolean defesaAntiga;

	/** Grande �rea de conhecimento do CNPq. */
	private AreaConhecimentoCnpq grandeArea;

	/** �rea de conhecimento do CNPq*/
	private AreaConhecimentoCnpq area;

	/** Sub-�rea de conhecimento do CNPq. */
	private AreaConhecimentoCnpq subArea;

	/** Especialidade da sub-�rea de conhecimento. */
	private AreaConhecimentoCnpq especialidade;
	
	/** Arquivo anexo de banca de defesa antiga. */
	private UploadedFile arquivo;
	
	/** Cole��o de selectItem de atividades que o discente est� matriculado ou cumpriu. */
	private Collection<SelectItem> matriculasComponenteCombo;

	/** Indica que no formul�rio, o usu�rio deve escolher a matr�cula na atividade correspondente.*/
	private boolean escolheAtividade;
	
	/** Indica que no formul�rio, o usu�rio deve escolher a matr�cula na atividade correspondente.*/
	private boolean cadastroNovoMembro;
	
	/** Construtor padr�o. */
	public BancaPosMBean() {
		initObj();
		initMembro();
	}

	/** Inicia os atributos da classe. */
	private void initObj() {
		obj = new BancaPos();
		obj.setDadosDefesa(new DadosDefesa());
		obj.getDadosDefesa().setArea(new AreaConhecimentoCnpq());
		obj.setMatriculaComponente(new MatriculaComponente());
		
		obj.setMembrosBanca(new ArrayList<MembroBancaPos>(0));
		obj.setTipoBanca(BancaPos.BANCA_QUALIFICACAO);
		obj.setDefesaAntiga(false);

		grandeArea = new AreaConhecimentoCnpq();
		area = new AreaConhecimentoCnpq();
		subArea = new AreaConhecimentoCnpq();
		especialidade = new AreaConhecimentoCnpq();
		matriculasComponenteCombo = null;
	}

	/** Inicializa os atributos de membro da banca. */
	private void initMembro() {
		initMembro(MembroBancaPos.EXAMINADOR_INTERNO);
	}
	
	
	/** Inicializa os atributos de membro da banca. */
	private void initMembro(int funcao) {
		cadastroNovoMembro = false;
		membroBanca = new MembroBancaPos();
		membroBanca.setFuncao(funcao);
		membroBanca.setBanca(getObj());
		membroBanca.setInstituicao(new InstituicoesEnsino());
		membroBanca.setMaiorFormacao(new Formacao());
		membroBanca.setDocenteExternoPrograma(new Servidor());
		Pessoa p = new Pessoa();
		p.setMunicipio(null);
		p.setTipoEtnia(null);
		p.setPais(null);
		p.setTipoRedeEnsino(null);
		p.setEstadoCivil(null);
		p.setTituloEleitor(null);
		p.setIdentidade(null);
		p.setTipoRaca(null);
		p.setUnidadeFederativa(null);
		p.setEnderecoContato(null);
		p.setSexo(PessoaGeral.SEXO_MASCULINO);
		membroBanca.setPessoaMembroExterno(p);
	}

	/** Retorna o link para o formul�rio de dados da banca. <br/>
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
     *   <li>/stricto/banca_pos/resumo.jsp</li>
     *   <li>/stricto/banca_pos/membros.jsp</li>
     * </ul>
	 * @return
	 */
	public String telaDadosBanca() {
		return forward("/stricto/banca_pos/dados_banca.jsp");
	}

	/** Retorna o link para o formul�rio de membros da banca.<br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/stricto/banca_pos/resumo.jsp</li>
	 * </ul>     
	 * @return
	 */
	public String telaMembros() {
		return forward("/stricto/banca_pos/membros.jsp");
	}
	
	/**
	 * Redireciona a opera��o para a tela de cancelamento de banca.
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/sigaa.war/stricto/banca_pos/alterar_remover.jsp</li>
	 * </ul>   
	 * @return
	 * @throws DAOException 
	 */
	public String telaCancelamento() throws DAOException{
		int idBancaPosRemocao = getParameterInt("idBancaPosRemocao");
		obj = getGenericDAO().findByPrimaryKey(idBancaPosRemocao, BancaPos.class);
		
		if ( isEmpty(bancasDoDiscente) || isEmpty(obj) ) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return cancelar();
		}		
		
		// verifica se h� mais de uma banca cadastrada para o discente
		int numBancas = 0;
		for (BancaPos outraBanca : bancasDoDiscente) {
			if (outraBanca.getStatus() != BancaPos.CANCELADA && obj.getId() != outraBanca.getId() && 
					obj.getTipoBanca() == outraBanca.getTipoBanca() &&
					( obj.getMatriculaComponente() != null && outraBanca.getMatriculaComponente() != null
					&& obj.getMatriculaComponente().getId() == outraBanca.getMatriculaComponente().getId()
					|| obj.getMatriculaComponente() == null && outraBanca.getMatriculaComponente() == null))
				numBancas++;
		}
		if( StatusDiscente.getStatusConcluinteStricto().contains(obj.getDadosDefesa().getDiscente().getStatus())
				&& numBancas == 0){
			addMensagemErro("N�o � poss�vel cancelar bancas de discentes com os seguintes status "+StatusDiscente.getDescricao(StatusDiscente.CONCLUIDO)+", "+
					StatusDiscente.getDescricao(StatusDiscente.EM_HOMOLOGACAO)+" ou "+StatusDiscente.getDescricao(StatusDiscente.DEFENDIDO) );
			return null;
		}
		
		discente = obj.getDadosDefesa().getDiscente();
		return forward("/stricto/banca_pos/cancelar_banca.jsp");
	}

	/** Chamado a partir do BuscaDiscenteMBean
	 * M�todo n�o invocado por JSP.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	public String selecionaDiscente() throws ArqException {
		
		if( defesaAntiga ){
			DadosDefesaDao dao = getDAO( DadosDefesaDao.class );
			Collection<DadosDefesa> defesas = dao.findByDiscente(discente);
			if( !isEmpty( defesas ) ){
				addMensagemErro( "Este discente j� possui defesa cadastrada." );
				return null;
			}
		}
		// se for opera��o de cadastro de banca
		if (cadastrar) {
			return iniciaCadastro();
		} else {
			// caso contr�rio, � opera��o de altera��o / remo��o de banca
			initObj();
			return listaBancas();
		}
	}

	/** Retorna a lista de bancas cadastradas.
	 * <br /><br />
	 * M�todo n�o invocado por JSP
	 * @return
	 * @throws ArqException
	 */
	private String listaBancas() throws ArqException {
		if (discente  == null) {
			addMensagemErro("N�o foi poss�vel carregar dados do discente selecionado");
			return null;
		}

		prepareMovimento(SigaaListaComando.CADASTRAR_BANCA_POS);
		BancaPosDao bpdao = getDAO(BancaPosDao.class);
		bancasDoDiscente = bpdao.findByDiscente(discente);
		for(BancaPos b: bancasDoDiscente){
			if(b.getMatriculaComponente() != null){
				b.getMatriculaComponente().getComponenteDescricao();
				if(b.getMatriculaComponente().getSituacaoMatricula() != null)
					b.getMatriculaComponente().getSituacaoMatricula().getDescricao();
			}
		}
		// banca atual
		BancaPos banca = bpdao.findMaisRecenteByTipo(discente, BancaPos.BANCA_DEFESA);
		discente.setBancaDefesa(banca);
	
		prepareMovimento(ArqListaComando.REMOVER); // prepara o movimento antes de entrar na p�gina da listagem
		
		return forward("/stricto/banca_pos/alterar_remover.jsp");
	}
	
	/**
	 * Lista todas as bancas pendentes de aprova��o do programa do coordenador logado.
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/stricto/menu_coordenador.jsp</li>
     * </ul>  
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public String listarBancasPendentesAprovacao() throws DAOException, SegurancaException{

		if (!isPortalDocente())
			checkRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.PPG, SigaaPapeis.DOCENTE);
		
		BancaPosDao dao = getDAO(BancaPosDao.class);
		try {
			if (!isPortalDocente() && !isTurmaVirtual()){
				bancasDoDiscente = dao.findDefesasByProgramaAndStatus(getProgramaStricto().getId(), BancaPos.PENDENTE_APROVACAO);
				carregarEnderecosLattes();
			}else {
				int idOrientador = 0;
				boolean docenteExterno = false;
				
				if (getServidorUsuario() != null)
					idOrientador = getServidorUsuario().getId();
				
				if (getUsuarioLogado().getVinculoAtivo().getDocenteExterno() != null){
					idOrientador = getUsuarioLogado().getVinculoAtivo().getDocenteExterno().getId();
					docenteExterno = true;
				}
				
				bancasDoDiscente = dao.findBancasPendentesByOrientador(idOrientador, docenteExterno);
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		return forward("/stricto/banca_pos/lista_bancas_pendentes.jsp");
	}

	/**
	 * Carrega os endere�os do curr�culo lattes nos discentes das bancas buscadas.
	 * 
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void carregarEnderecosLattes() throws HibernateException, DAOException {
		
		BancaPosDao bDao = null;
		try {
			
			List<Integer> idsDiscente = new ArrayList<Integer>();
			for (BancaPos b : bancasDoDiscente){
				if (b.getDadosDefesa()!= null && b.getDadosDefesa().getDiscente()!= null)
					idsDiscente.add(b.getDadosDefesa().getDiscente().getId());
			}
			
			bDao = getDAO(BancaPosDao.class);
			List<PerfilPessoa> perfis = new ArrayList<PerfilPessoa>();
			
			if (idsDiscente != null && !idsDiscente.isEmpty())
				perfis = bDao.getPerfisPessoas(idsDiscente);
			
			if (perfis!= null){
				for (PerfilPessoa p : perfis){
					for (BancaPos b : bancasDoDiscente){
						if (b.getDadosDefesa()!= null && b.getDadosDefesa().getDiscente()!= null && b.getDadosDefesa().getDiscente().getId() == p.getIdDiscente())
							b.getDadosDefesa().getDiscente().getDiscente().getPessoa().setPerfil(p);
					}
				}
			}
			
		} finally {
			if (bDao != null)
				bDao.close();
		}
		
	}

	/** Inicia o processo de cadastro de banca de p�s.
	 * <br /><br />
	 * M�todo n�o invocado por JSP.
	 * @return
	 * @throws DAOException
	 */
	private String iniciaCadastro() throws DAOException {
		if (discente  == null) {
			addMensagemErro("N�o foi poss�vel carregar dados do discente selecionado.");
			return null;
		}
			
		MatriculaComponenteDao mdao = getDAO(MatriculaComponenteDao.class);
		DiscenteStrictoDao dao = getDAO(DiscenteStrictoDao.class);
		try {
			TipoAtividade tipo = new TipoAtividade();
			if (obj.isQualificacao())
				tipo.setId(TipoAtividade.QUALIFICACAO);
			else {
				tipo.setId(TipoAtividade.TESE);
			
				if (!defesaAntiga){
					int crIntegralizados = dao.calculaCrTotaisIntegralizados(discente);
					int crObrigatorio = dao.calculaCrTotalCurriculo(discente);
					
					if (crIntegralizados < crObrigatorio){
						addMensagemErro("N�o � poss�vel cadastrar uma banca de "+obj.getTipoDescricao() +
								" sem o discente cumprir o total de cr�ditos exigidos no curr�culo, " +
								"que � de "+crObrigatorio+" cr�ditos, e possui "+crIntegralizados+" cr�ditos integralizados.");
						return null;					
					}
				}
			}
						
			Collection<MatriculaComponente> matriculas = mdao.findAtividades(discente, tipo, SituacaoMatricula.getSituacoesPagasEMatriculadasArray());
			if (isEmpty(matriculas) && !defesaAntiga) {
				addMensagemErro("N�o � poss�vel cadastrar uma banca de "+obj.getTipoDescricao() +
						" sem discente estar matriculado ou ter conclu�do uma atividade desse tipo.");
				return null;
			} else if (matriculas.size() > 1) {
				escolheAtividade = true;
			} else {
				escolheAtividade = false;
				if (defesaAntiga){
					obj.setMatriculaComponente(null);
				} else 
					obj.setMatriculaComponente(matriculas.iterator().next());
			}
			
			if (obj.getTipoBanca() == BancaPos.BANCA_DEFESA){
				Collection<MatriculaComponente> proficienciasPagas = mdao.findAtividadesByDiscente(discente, new TipoAtividade(TipoAtividade.PROFICIENCIA), SituacaoMatricula.getSituacoesPagas() );
				
				//Retorna os par�metros de quantidade m�nima de atividade de profici�ncia 
				//que os alunos de MESTRADO e DOUTORADO devem cursar antes de cadastrar em banca de defesa. 
				int minProficienciaMestrado = ParametroHelper.getInstance().getParametroInt(ParametrosStrictoSensu.QUANTIDADE_MINIMA_PROFICIENCIA_MESTRADO);
				int minProficienciaDoutorado = ParametroHelper.getInstance().getParametroInt(ParametrosStrictoSensu.QUANTIDADE_MINIMA_PROFICIENCIA_DOUTORADO);
				
				// Verificar se o discente cumpriu alguma atividade de profici�ncia.
				if (!defesaAntiga && ((minProficienciaDoutorado > 0) || (minProficienciaMestrado > 0))){			
					if ((discente.getNivel() == NivelEnsino.DOUTORADO && proficienciasPagas.size() < minProficienciaDoutorado) || (discente.getNivel() == NivelEnsino.MESTRADO && proficienciasPagas.size() < minProficienciaMestrado)){
						addMensagemErro("Somente � permitido cadastrar em defesas ap�s a conclus�o de "+(discente.getNivel() == NivelEnsino.DOUTORADO ? minProficienciaDoutorado : minProficienciaMestrado)+" atividade(s) de profici�ncia.");
						return null;				
					}				
				}											
			}		
			
			BancaPosDao bpdao = getDAO(BancaPosDao.class);
			// � opera��o de cadastro ?
			if (obj.getId() == 0) {
				// j� existe banca cadastrada para a(s) matr�cula(s) em componente?
				boolean todasMatriculasComBanca = true;
				for (MatriculaComponente matricula : matriculas) {
					BancaPos bancaCadastrada = bpdao.findByMatriculaComponente(matricula);
					if (isEmpty(bancaCadastrada)) {
						todasMatriculasComBanca = false;
						break;
					}
				}
				if (todasMatriculasComBanca && !isEmpty(matriculas) ) {
					addMensagemErro("O discente j� possui bancas cadastradas para cada atividade de " + (obj.isQualificacao() ? "qualifica��o" : "tese") + " matriculada ou conclu�da.");
					return null;
				}
				// caso j� exista banca anterior, recupera alguns dados
				DadosDefesaDao ddao = getDAO(DadosDefesaDao.class);
				DadosDefesa dados = ddao.findMaisRecenteByDiscente(getDiscente());
				if (dados != null) {
					// Sem isso sobrep�e os dados da banca mais recente. 
					// Dessa forma ir� cadastrar os dados da defesa para a banca em quest�o.
					dados.setId(0); 
					
					obj.setDadosDefesa(dados);
		
					grandeArea = new AreaConhecimentoCnpq();
					if (obj.getGrandeArea() != null)
						grandeArea = obj.getGrandeArea();
					area = new AreaConhecimentoCnpq();
					if (obj.getArea() != null)
						area = obj.getArea();
					subArea = new AreaConhecimentoCnpq();
					if (obj.getSubArea() != null)
						subArea = obj.getSubArea();
					especialidade = new AreaConhecimentoCnpq();
					if (obj.getEspecialidade() != null)
						especialidade = obj.getEspecialidade();
				} else {
					obj.getDadosDefesa().setDiscente(discente);
				}
	
				carregaAreas(null);
				carregaSubAreas(null);
				carregaEspecialidades(null);
			} else {
				// � opera��o de altera��o de dados da banca
			}
		} finally {
			if (mdao != null)
				mdao.close();
		}

		return telaDadosBanca();
	}
	
	/** Retorna a cole��o de selectItem de atividades que o discente est� matriculado ou cumpriu.
	 * <br /><br />
	 * M�todo chamado pela seguinte JSP: 
	 * <ul>
	 * <li>/stricto/banca_pos/dados_banca.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getMatriculasComponenteCombo() throws DAOException{
		matriculasComponenteCombo = null;
		if (matriculasComponenteCombo == null) {
			MatriculaComponenteDao mdao = getDAO(MatriculaComponenteDao.class);
			TipoAtividade tipo = new TipoAtividade();
			if (obj.isQualificacao())
				tipo.setId(TipoAtividade.QUALIFICACAO);
			else
				tipo.setId(TipoAtividade.TESE);
			Collection<MatriculaComponente> matriculas = mdao.findAtividades(discente, tipo, SituacaoMatricula.getSituacoesPagasEMatriculadasArray());
			matriculasComponenteCombo = new ArrayList<SelectItem>();
			for (MatriculaComponente matricula : matriculas) {
				matricula.setSituacaoMatricula(mdao.refresh(matricula.getSituacaoMatricula()));
				matriculasComponenteCombo.add(new SelectItem(matricula.getId(), matricula.getComponenteCodigoNome()+ " (" +
						matricula.getAnoPeriodo() + ") - " + matricula.getSituacaoMatricula().getDescricao()));
			}
		}
		return matriculasComponenteCombo;
	}

	/** Seta o discente selecionado na busca por discente.
	 * <br /><br />
	 * M�todo n�o invocado por JSP.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 * 
	 */
	public void setDiscente(DiscenteAdapter discente) {
		try {
			this.discente = (DiscenteStricto) getDAO(DiscenteDao.class).findByPK(discente.getId());
			this.discente.setOrientacao(DiscenteHelper.getOrientadorAtivo(this.discente.getDiscente()));
		} catch (Exception e) {
			discente = null;
			e.printStackTrace();
		}
	}

	/** Inicia o cadastro de uma banca de qualifica��o.<br/><br />
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
     *   <li>/stricto/menus/discente.jsp</li>
     *   <li>/stricto/menu_coordenador.jsp</li>
     * </ul>  
	 * @return
	 * @throws ArqException
	 */
	public String iniciarQualificacao() throws ArqException {
		return iniciar(BancaPos.BANCA_QUALIFICACAO, true, false);
	}

	/** Inicia o cadastro de uma banca de defesa.<br/>
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
     *   <li>/stricto/menus/discente.jsp</li>
     *   <li>/stricto/menu_coordenador.jsp</li>
     * </ul>  
	 * @return
	 * @throws ArqException
	 */
	public String iniciarDefesa() throws ArqException {
		return iniciar(BancaPos.BANCA_DEFESA, true, false);
	}
	
	/** Inicia o cadastro de uma banca de defesa de um aluno conclu�do.<br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/stricto/menu_coordenador.jsp</li>
     * </ul>  
	 * @return
	 * @throws ArqException
	 */
	public String iniciarDefesaAlunoConcluido() throws ArqException{
		return iniciar(BancaPos.BANCA_DEFESA, true, true);
	}
	
	/**
	 * Carrega os dados do discente selecionado para que o orientador realize 
	 * opera��es de cadastro e altera��o de banca de p�s
	 * @throws SegurancaException
	 */
	private void carregaDiscenteSelecionadoOrientador() throws SegurancaException{
		if (!getAcessoMenu().isOrientadorStricto())
			throw new SegurancaException("Usu�rio n�o autorizado a realizar esta opera��o.");
		
		int idDiscente = getParameterInt("idDiscente", 0);
		
		if (idDiscente > 0){
			DiscenteAdapter discente = new DiscenteStricto();
			discente.setId(idDiscente);
			setDiscente(discente);
			
			if (!this.getDiscente().getOrientacao().getPessoa().equals(getUsuarioLogado().getPessoa())){
				addMensagemErro("N�o � poss�vel cadastrar uma banca para um discente que n�o � seu Orientando.");
				return;
			}
		}
		
		if (idDiscente == 0 || isEmpty(this.discente)){
			addMensagemErro("Discente n�o informado!");
			return;
		}		
	}
	
	/**
	 * Inicia cadastro de banca com o acesso pelo orientador do discente
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/sigaa.war/stricto/orientacao/orientandos.jsp</li>
     * </ul>  
	 * @param tipo
	 * @return
	 * @throws ArqException
	 */
	private String iniciarBancaOrientador(int tipo) throws ArqException{

		carregaDiscenteSelecionadoOrientador();
		
		this.cadastrar = true;
		this.defesaAntiga = false;
		this.escolheAtividade = false;
		
		setConfirmButton("Cadastrar Banca");
		initObj();
		initMembro();
		
		obj.setTipoBanca(tipo);
		obj.setDefesaAntiga(defesaAntiga);
		
		prepareMovimento(SigaaListaComando.CADASTRAR_BANCA_POS_ORIENTADOR);		
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_BANCA_POS_ORIENTADOR.getId());

		return iniciaCadastro();		
	}
	
	/**
	 * Inicia a banca de defesa com o acesso pelo orientador do discente
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/sigaa.war/stricto/orientacao/orientandos.jsp</li>
     * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarDefesaOrientador() throws ArqException{
		return iniciarBancaOrientador(BancaPos.BANCA_DEFESA);
	}
	
	/**
	 * Inicia a banca de qualifica��o com o acesso pelo orientador do discente
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/sigaa.war/stricto/orientacao/orientandos.jsp</li>
     * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarQualificacaoOrientador() throws ArqException{
		return iniciarBancaOrientador(BancaPos.BANCA_QUALIFICACAO);
	}	
	
	/**
	 * Lista as banca de defesa com o acesso pelo orientador do discente selecionado
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/sigaa.war/stricto/orientacao/orientandos.jsp</li>
     * </ul>
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String listarBancasOrientador() throws DAOException, ArqException{
		
		carregaDiscenteSelecionadoOrientador();
		
		cadastrar = false;
		this.defesaAntiga = false;		
		
		prepareMovimento(SigaaListaComando.ALTERAR_BANCA_POS);
		prepareMovimento(SigaaListaComando.REMOVER_BANCA_POS);
		
		return listaBancas();
	}

	/** Inicia o cadastro de uma banca de p�s.
	 * @param tipo
	 * @param cadastrar
	 * @param defesaAntiga
	 * @return
	 * @throws ArqException
	 */
	private String iniciar(int tipo, boolean cadastrar, boolean defesaAntiga) throws ArqException {
		this.cadastrar = cadastrar;
		this.defesaAntiga = defesaAntiga;
		this.escolheAtividade = false;
		
		checkRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.PPG);
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");		
		
		if( this.defesaAntiga ){
			buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.CADASTRO_DEFESA_ALUNO_CONCLUIDO);
			prepareMovimento(SigaaListaComando.CADASTRAR_DEFESA_ALUNO_CONCLUIDO);
			setOperacaoAtiva(SigaaListaComando.CADASTRAR_DEFESA_ALUNO_CONCLUIDO.getId());
		}else{
			buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.BANCA_POS);
			prepareMovimento(SigaaListaComando.CADASTRAR_BANCA_POS);
			setOperacaoAtiva(SigaaListaComando.CADASTRAR_BANCA_POS.getId());
		}
		
		setConfirmButton("Cadastrar Banca");
		initObj();
		obj.setTipoBanca(tipo);
		obj.setDefesaAntiga(defesaAntiga);
		initMembro();
		return buscaDiscenteMBean.popular();
	}
	
	/** Carrega as {@link AreaConhecimentoCnpq �reas de conhecimento}.<br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/stricto/banca_pos/dados_banca.jsp</li>
     * </ul>  
	 * @param evt
	 * @throws DAOException
	 */
	public void carregaAreas(ValueChangeEvent evt) throws DAOException {

		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		areas = new ArrayList<SelectItem>();
		subAreas = new ArrayList<SelectItem>();
		especialidades = new ArrayList<SelectItem>();
		if (evt == null) {
			areas = toSelectItems(dao.findAreas(grandeArea), "id", "nome");
		} else {

			if (((Integer) evt.getNewValue()) != null && ((Integer) evt.getNewValue()) != 0) {
				AreaConhecimentoCnpq grandeArea = new AreaConhecimentoCnpq((Integer) evt.getNewValue());
				areas = toSelectItems(dao.findAreas(grandeArea), "id", "nome");
			} 
		}
	}

	/** Carrega as {@link AreaConhecimentoCnpq sub�reas de conhecimento}.<br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/stricto/banca_pos/dados_banca.jsp</li>
     * </ul>  
	 * @param evt
	 * @throws DAOException
	 */
	public void carregaSubAreas(ValueChangeEvent evt) throws DAOException {

		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		subAreas = new ArrayList<SelectItem>();
		especialidades = new ArrayList<SelectItem>();
		if (evt == null) {
			subAreas = toSelectItems(dao.findSubAreas(area.getId()), "id", "nome");
		} else {

			if (((Integer) evt.getNewValue()) != null && ((Integer) evt.getNewValue()) != 0) {
				subAreas = toSelectItems(dao.findSubAreas((Integer) evt.getNewValue()), "id", "nome");
			} 
		}
	}

	/** Carrega a lista de {@link AreaConhecimentoCnpq especialidades da sub-�rea de conhecimento}.<br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/stricto/banca_pos/dados_banca.jsp</li>
     * </ul>  
	 * @param evt
	 * @throws DAOException
	 */
	public void carregaEspecialidades(ValueChangeEvent evt) throws DAOException {

		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		especialidades = new ArrayList<SelectItem>();
		
		if (evt == null) {
			if (subArea != null && subArea.getId() > 0)
				especialidades = toSelectItems(dao.findEspecialidade(subArea), "id", "nome");
		} else {
			if (((Integer) evt.getNewValue()) != null && ((Integer) evt.getNewValue()) != 0) {
				AreaConhecimentoCnpq subArea = new AreaConhecimentoCnpq((Integer) evt.getNewValue());
				especialidades = toSelectItems(dao.findEspecialidade(subArea), "id", "nome");
			}
		}
	}
	
	
	/** Carrega os dados da pessoa selecionada como novo
	 * membro da banca.
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/stricto/banca_pos/membros.jsp</li>
     * </ul>  
	 * @param e
	 * @throws ArqException
	 */
	public void carregaMembroExterno(ActionEvent e)  throws ArqException {
		
		initMembro( MembroBancaPos.EXAMINADOR_EXTERNO_A_INSTITUICAO );
		Pessoa pessoa = 
				(Pessoa) e.getComponent().getAttributes().get("pessoaExterna");
		if( !isEmpty(pessoa) ){
			MembroBancaPos m = getGenericDAO().findByExactField(MembroBancaPos.class, 
					"pessoaMembroExterno.id", pessoa.getId(), true );
			if( !isEmpty(m) ){
				membroBanca.setInstituicao( m.getInstituicao() );
				membroBanca.setMaiorFormacao( m.getMaiorFormacao() );
				membroBanca.setAnoConclusao( m.getAnoConclusao() );
			}
		}
		membroBanca.setPessoaMembroExterno( pessoa );
			
	}
	
	/**
	 * Popula se o membro da banca � ou n�o estrangeiro.
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/stricto/banca_pos/membros.jsp</li>
     * </ul>  
	 * @param e
	 */
	public void carregaNacionalidade(ValueChangeEvent e){
		Boolean nacionalidade = (Boolean) 
		e.getNewValue();
		getMembroBanca().getPessoaMembroExterno().setInternacional(nacionalidade);
	}
	
	
	/**
	 * Inicializa o {@link membroBanca} para um novo preenchimento no 
	 * formul�rio. 
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/stricto/banca_pos/membros.jsp</li>
     * </ul>  
	 * @param e
	 */
	public void clearMembroBancaPos(ActionEvent e){
		initMembro( membroBanca.getFuncao() );
	}


	/** Adiciona um membro � lista de {@link MembroBancaPos membros da banca}.<br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/stricto/banca_pos/membros.jsp</li>
     * </ul>  
	 * @param e
	 * @throws ArqException
	 */
	public void adicionarMembroBanca(ActionEvent e)  throws ArqException {
		erros = new ListaMensagens();
		
		if ( membroBanca.isExterno() && membroBanca.getPessoaMembroExterno() != null ) {			
			PessoaDao daoPessoa = getDAO(PessoaDao.class);
			try{
				if ( membroBanca.getPessoaMembroExterno().getId() == 0  && !isEmpty(membroBanca.getPessoaMembroExterno().getCpf_cnpj()) ){
					Pessoa chkPessoa = daoPessoa.findByCpf(membroBanca.getPessoaMembroExterno().getCpf_cnpj());
					if ( !isEmpty(chkPessoa) ){
						DocenteExterno chkDocenteExterno = getDAO(DocenteExternoDao.class).findByPessoa(chkPessoa, chkPessoa.getTipo(), null);
						if ( !isEmpty(chkDocenteExterno) ){
							addMensagemErro("O CPF informado pertence a um Docente Externo, por favor localize-o na op��o \"Buscar membro\".");
							return;
						}else
							membroBanca.setPessoaMembroExterno( chkPessoa );
					}	
				}
			} finally {
				if (daoPessoa != null){
					daoPessoa.close();
				}
			}							
		}
		
		erros.addAll(membroBanca.validate());
	
		if( cadastroNovoMembro ){ 
			if ( !membroBanca.getPessoaMembroExterno().isInternacional() ){
				ValidatorUtil.validateCPF_CNPJ(membroBanca.getPessoaMembroExterno().getCpf_cnpj(), "CPF", erros);
			}else{
				if( isEmpty( membroBanca.getPessoaMembroExterno().getCpf_cnpj() ) 
						&& isEmpty( membroBanca.getPessoaMembroExterno().getPassaporte() ) ){
					erros.addErro("CPF ou Passaporte: Um dos campos deve ser preenchido para nacionalidade estrangeira");
				}
				if( membroBanca.getPessoaMembroExterno().getCpf_cnpj() == 0 ){
					membroBanca.getPessoaMembroExterno().setCpf_cnpj(null);
				}
			}
		}
		
		
		if (hasErrors())
			return ;
		
		GenericDAO dao = getGenericDAO();
		if (membroBanca.isPresidenteBanca()) {
			if (membroBanca.getDocentePrograma() != null){
				membroBanca.setMaiorFormacao(membroBanca.getDocentePrograma().getFormacao());
			}	
			membroBanca.setInstituicao( membroBanca.isServidor() ? new InstituicoesEnsino(InstituicoesEnsino.UFRN) : membroBanca.getDocenteExternoInstituicao().getInstituicao());
			membroBanca.setDocenteExternoPrograma(null);
			membroBanca.setPessoaMembroExterno(null);
		} else if (membroBanca.isExaminadorInterno()) {
			if (membroBanca.getDocentePrograma() != null){
				membroBanca.setMaiorFormacao(membroBanca.getDocentePrograma().getFormacao());
				membroBanca.setDocentePrograma(dao.findByPrimaryKey(membroBanca.getDocentePrograma().getId(), Servidor.class));
			}
			membroBanca.setInstituicao( membroBanca.isServidor() ? new InstituicoesEnsino(InstituicoesEnsino.UFRN) : membroBanca.getDocenteExternoInstituicao().getInstituicao());
			membroBanca.setPessoaMembroExterno(null);
			membroBanca.setDocenteExternoPrograma(null);
		} else if (membroBanca.getDocenteExternoPrograma() != null && membroBanca.getDocenteExternoPrograma().getId() != 0) {

			String tipoDocente = getParameter("tipoAjaxDocente_1");
			
			if (membroBanca.isExaminadorExternoPrograma()) {
				if( "externo".equals(tipoDocente) ){
					membroBanca.setDocenteExternoInstituicao(dao.findByPrimaryKey(membroBanca.getDocenteExternoPrograma().getId(), DocenteExterno.class));
					membroBanca.setMaiorFormacao(membroBanca.getDocenteExternoInstituicao().getFormacao());
					membroBanca.setInstituicao(membroBanca.getDocenteExternoInstituicao().getInstituicao());
					membroBanca.setDocenteExternoPrograma(null);
					membroBanca.setDocentePrograma(null);
				} else{
					membroBanca.setDocenteExternoPrograma(dao.findByPrimaryKey(membroBanca.getDocenteExternoPrograma().getId(), Servidor.class));
					membroBanca.setMaiorFormacao(membroBanca.getDocenteExternoPrograma().getFormacao());
					membroBanca.setInstituicao(new InstituicoesEnsino(InstituicoesEnsino.UFRN));
					membroBanca.setDocenteExternoInstituicao(null);
					membroBanca.setDocentePrograma(null);
				}
			}
			
		} else if (membroBanca.getDocenteExternoInstituicao() != null) {
			membroBanca.setMaiorFormacao(membroBanca.getDocenteExternoInstituicao().getFormacao());
			membroBanca.setInstituicao(membroBanca.getDocenteExternoInstituicao().getInstituicao());
			membroBanca.setDocenteExternoPrograma(null);
		} else {
			if (!membroBanca.isExaminadorExternoPrograma()) {
				membroBanca.setInstituicao(dao.findByPrimaryKey(membroBanca.getInstituicao().getId(), InstituicoesEnsino.class));
				membroBanca.setDocenteExternoPrograma(null);
			}

			if (membroBanca.getPessoaMembroExterno().isInternacional() && membroBanca.getPessoaMembroExterno().getCpf_cnpj() == null) {
				if (isEmpty(membroBanca.getPessoaMembroExterno().getPassaporte())) {
					addMensagemErro("O passaporte do membro � obrigat�rio.");
					if (!membroBanca.isExaminadorExternoPrograma())
						membroBanca.setDocenteExternoPrograma(new Servidor());
					return;
				}
			} else {
				validarCpf();
				if (hasErrors()) {
					return;
				}
			}
		}

		if (membroBanca.getFuncao() == MembroBancaPos.PRESIDENTE_BANCA) {
			if (CollectionUtils.countMatches(obj.getMembrosBanca(), PRESIDENTE_BANCA) > 0) {
				addMensagemErro("O presidente da banca j� foi selecionado.");
				initMembro();
				return;
			}
		}

		if (CollectionUtils.countMatches(obj.getMembrosBanca(), new Predicate() {
				public boolean evaluate(Object obj) {
					MembroBancaPos membro = (MembroBancaPos) obj;
					if (membro.getPessoa().isInternacional() && !isEmpty(membro.getPessoa().getPassaporte()))
						return membro.getPessoa().getPassaporte().equals(membroBanca.getPessoa().getPassaporte());
					else if (membro.getPessoa().getCpf_cnpj() != null && membro.getPessoa().getCpf_cnpj() > 0)
						return membro.getPessoa().getCpf_cnpj().equals(membroBanca.getPessoa().getCpf_cnpj());
					else return false;
				}
			}) > 0) {
			addMensagemErro("A pessoa selecionada j� foi adicionada � banca.");
			initMembro();
			return;
		}

		obj.getMembrosBanca().add(membroBanca);
		Collections.sort(obj.getMembrosBanca(), new Comparator<MembroBancaPos>() {
			public int compare(MembroBancaPos o1, MembroBancaPos o2) {
				return o1.getNome().compareTo(o2.getNome());
			}
		});
		initMembro( membroBanca.getFuncao() );
	}
	

	/**
	 * M�todo que valida o CPF do objeto populado em membroBanca.
	 * <br />
	 * N�o � chamado por JSPs.
	 * @return
	 */
	public ListaMensagens validarCpf(){
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateCPF_CNPJ(membroBanca.getPessoaMembroExterno().getCpf_cnpj(), "CPF", erros);
		return erros;
	}
	
	/** Remove um membro da lista de {@link MembroBancaPos membros da banca}.<br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/stricto/banca_pos/membros.jsp</li>
     * </ul>  
	 * @param e
	 * @throws ArqException
	 */
	public String removerMembroBanca(ActionEvent e)  throws ArqException {
		Pessoa pessoa = (Pessoa) e.getComponent().getAttributes().get("pessoaMembroBancaPos");
		if ( !obj.removeMembroBanca(pessoa) ) {
			addMensagem( MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO );
			return null;
		}
		return telaMembros();
	}

	/** Submete os dados de {@link MembroBancaPos membros da banca}.<br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/stricto/banca_pos/membros.jsp</li>
     * </ul>  
	 * @return
	 * @throws DAOException 
	 */
	public String submeterMembros() throws DAOException {
		
		if( isEmpty(discente) ){
			addMensagemErro( "A opera��o j� havia sido conclu�da. Por favor, reinicie os procedimentos." );
			return cancelar();
		}
		
		erros = new ListaMensagens();
		validar();
		if (obj.getMembrosBanca() == null || obj.getMembrosBanca().size() == 0)
			addMensagemErro("� necess�rio adicionar ao menos um membro na banca");

		if (hasErrors())
			return null;

		obj.setMatriculaComponente(getGenericDAO().refresh(obj.getMatriculaComponente()));
		return telaResumo();
	}

	/** Retorna o link para a tela de resumo da banca.
	 * <br />
	 * M�todo n�o invocado por JSP 
	 * @return
	 */
	private String telaResumo() {
		return forward("/stricto/banca_pos/resumo.jsp");
	}

	/** Retorna uma cole��o de SelectItem de tipos de banca.
	 * @return
	 */
	public Collection<SelectItem> getTipos() {
		ArrayList<SelectItem> tipos = new ArrayList<SelectItem>(0);
		tipos.add(new SelectItem(BancaPos.BANCA_QUALIFICACAO, "QUALIFICA��O"));
		tipos.add(new SelectItem(BancaPos.BANCA_DEFESA, "DEFESA"));
		return tipos;
	}

	/**
	 * Retorna uma cole��o de SelecItem de fun��es de membros da banca
	 * (Presidente, Examinador interno, etc.)
	 * 
	 * @return
	 */
	public Collection<SelectItem> getFuncoesCombo() {
		ArrayList<SelectItem> tipos = new ArrayList<SelectItem>(0);
		tipos.add(new SelectItem(MembroBancaPos.PRESIDENTE_BANCA, "PRESIDENTE"));
		tipos.add(new SelectItem(MembroBancaPos.EXAMINADOR_INTERNO, "EXAMINADOR INTERNO"));
		tipos.add(new SelectItem(MembroBancaPos.EXAMINADOR_EXTERNO_AO_PROGRAMA, "EXAMINADOR EXTERNO AO PROGRAMA"));
		tipos.add(new SelectItem(MembroBancaPos.EXAMINADOR_EXTERNO_A_INSTITUICAO, "EXAMINADOR EXTERNO � INSTITUI��O"));
		return tipos;
	}

	/** Submete os dados gerais da banca.<br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/stricto/banca_pos/dados_banca.jsp</li>
     * </ul>  
	 * @return
	 * @throws DAOException
	 */
	public String submeterDadosGerais() throws DAOException {
		
		if( isEmpty(discente) ){
			addMensagemErro( "A opera��o j� havia sido conclu�da. Por favor, reinicie os procedimentos." );
			return cancelar();
		}
		
		if (!isEmpty(area))
			obj.getDadosDefesa().setArea(area);
		
		obj.setUsuario(getUsuarioLogado());
		erros = new ListaMensagens();
		validar();
		
		if (isEmpty(grandeArea))
			erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,"Grande �rea");	
		if (isEmpty(area))
			erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,"�rea");
				
		if (hasErrors()) {
			return null;
		}

		BancaPosDao dao = getDAO(BancaPosDao.class);
		DadosDefesa trab = obj.getDadosDefesa();

		AreaConhecimentoCnpq areaUsada = null;
		if (areaUsada == null && especialidade != null && especialidade.getId() != 0)
			areaUsada = especialidade;
		if (areaUsada == null && subArea != null && subArea.getId() != 0)
			areaUsada = subArea;
		if (areaUsada == null && area != null && area.getId() != 0)
			areaUsada = area;
		if (areaUsada == null && grandeArea != null && grandeArea.getId() != 0)
			areaUsada = grandeArea;

		if (areaUsada != null)
			trab.setArea(dao.findByPrimaryKey(areaUsada.getId(), AreaConhecimentoCnpq.class));

		// j� existe banca cadastrada para a matr�cula componente?
		//TODO: findByMatriculaComponente precisa de proje��o
		if (!isEmpty(obj.getMatriculaComponente()) && !defesaAntiga){
			BancaPos bancaCadastrada = dao.findByMatriculaComponente(obj.getMatriculaComponente());
			if (bancaCadastrada != null &&  
					(obj.getId() == 0 || obj.getId() != bancaCadastrada.getId()) &&	
					obj.getMatriculaComponente().getId() == bancaCadastrada.getMatriculaComponente().getId()) {
				obj.setMatriculaComponente(dao.refresh(obj.getMatriculaComponente()));
				addMensagemErro("J� existe uma banca cadastrada correspondente � atividade " + obj.getMatriculaComponente().getComponenteCodigoNome()+".");
				return null;
			}			
		}
		
		if (!isEmpty(obj.getHora())){
			Calendar cHora = Calendar.getInstance();
			cHora.setTime( obj.getHora());
			
			obj.setData( CalendarUtils.configuraTempoDaData(obj.getData(), cHora.get(Calendar.HOUR_OF_DAY), cHora.get(Calendar.MINUTE), 0, 0));
		} else {
			obj.setData( CalendarUtils.configuraTempoDaData(obj.getData(), 0, 0, 0, 0));
		}
		
		obj.setMatriculaComponente(dao.refresh(obj.getMatriculaComponente()));
		if( defesaAntiga )
			return telaResumo();
		else
			return telaMembros();
	}
	
	/** 
	 *  Esse m�todo serve para verificar se os campos obrigat�rios foram preenchidos
	 * 	M�todo n�o invocado por JSPs
	 */
	private void validar () throws DAOException {
		
		if (isEmpty(obj.getMatriculaComponente()) && !isDefesaAntiga())
			validateRequired(obj.getMatriculaComponente(), "Atividade Matriculada", erros);
		validateRequired(obj.getTipoBanca(), "Tipo de Banca", erros);
		validateRequired(obj.getLocal(), "Local", erros);
		validateMaxLength(obj.getLocal(), 100, "Local", erros);
		validateRequired(obj.getTitulo(), "T�tulo", erros);
		validateRequired(obj.getPaginas(), "P�ginas", erros);
		if( obj.getPaginas() != null)
			ValidatorUtil.validaFloatPositivo( Float.valueOf( obj.getPaginas() ), "P�ginas", erros);
		
		validateRequired(obj.getData(), "Data", erros);
		if (!isDefesaAntiga())
			validateRequired(obj.getHora(), "Hora", erros);
		validateRequired(obj.getResumo(), "Resumo", erros);
		validateRequired(obj.getPalavraChave(), "Palavra Chave", erros);
		
		if (obj.getData() != null) {
		
			Integer prazoMaximo = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.PRAZO_MAXIMO_CADASTRO_BANCA);
			ParametrosProgramaPos parametrosProgramaPos = ParametrosProgramaPosHelper.getParametros(discente);
	
			if (obj.isQualificacao()){
				if (parametrosProgramaPos.getPrazoMinCadastroBancaQualificacao()!=null){
					prazoMaximo = parametrosProgramaPos.getPrazoMinCadastroBancaQualificacao();
				}	
			} else if (obj.isDefesa()) {
				if (parametrosProgramaPos.getPrazoMinCadastroBancaDefesa()!=null){
					prazoMaximo = parametrosProgramaPos.getPrazoMinCadastroBancaDefesa();
				}
			}
			
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, prazoMaximo);
			Date dataSemHoras = DateUtils.truncate(cal.getTime(), Calendar.DAY_OF_MONTH);
			
			if (obj.getData().before(dataSemHoras)) {
				if (parametrosProgramaPos.getPrazoMinCadastroBancaQualificacao()!=null && obj.isValidarPrazoQualificacao())
					erros.addErro("A realiza��o da banca de qualifica��o deve se dar com no m�nimo "+prazoMaximo+" dias ap�s o cadastro de sua banca.");
						
				if (obj.isValidarPrazoCadastro())
					erros.addErro("A realiza��o da banca de defesa deve se dar com no m�nimo "+prazoMaximo+" dias ap�s o cadastro de sua banca.");
			}		
		}	
	
	}
	
	/** Processa os dados e persiste em banco.<br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/stricto/banca_pos/resumo.jsp</li>
     * </ul>   
	 * @return
	 * @throws ArqException
	 */
	public String confirmar() throws ArqException {
		try {
			if (!isOperacaoAtiva(SigaaListaComando.CADASTRAR_DEFESA_ALUNO_CONCLUIDO.getId()) 
					&& !isOperacaoAtiva(SigaaListaComando.CADASTRAR_BANCA_POS.getId())
					&& !isOperacaoAtiva(SigaaListaComando.CADASTRAR_BANCA_POS_ORIENTADOR.getId())){
				addMensagemErro("O procedimento que voc� tentou realizar j� foi processado anteriormente." +
				" Para realiz�-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
				redirectJSF(getSubSistema().getLink());
				return null;
			}
			
			if( defesaAntiga ){
				
				if( !checkOperacaoAtiva(SigaaListaComando.CADASTRAR_DEFESA_ALUNO_CONCLUIDO.getId()) )
					return cancelar();
				if( cadastrar ) obj.setStatus(BancaPos.ATIVO);
				MovimentoBancaAlunoConcluido mov = new MovimentoBancaAlunoConcluido();
				mov.setCodMovimento( SigaaListaComando.CADASTRAR_DEFESA_ALUNO_CONCLUIDO );
				mov.setBanca( obj );
				mov.setArquivo(arquivo);
				execute( mov );
				addMessage("Defesa " + (cadastrar ? "cadastrada" : "atualizada" )+ " com sucesso!", TipoMensagemUFRN.INFORMATION);				
				
			}else{
				Unidade programa = null;
				if (  isPortalCoordenadorStricto() )
					programa = getProgramaStricto();
				else if ( isPortalPpg() || isPortalDocente() )
					programa = discente.getUnidade();
				
				if (cadastrar && getUltimoComando().equals( SigaaListaComando.CADASTRAR_BANCA_POS_ORIENTADOR ))
					obj.setStatus(BancaPos.PENDENTE_APROVACAO);
				else if( cadastrar ) obj.setStatus(BancaPos.ATIVO);
				
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setCodMovimento(getUltimoComando());
				mov.setObjMovimentado(obj);
				mov.setObjAuxiliar(programa);
				
				execute(mov, getCurrentRequest());
				addMessage("Banca " + (cadastrar ? "cadastrada" : "atualizada" )+ " com sucesso!", TipoMensagemUFRN.INFORMATION);
			}
			
			setOperacaoAtiva(null);
			if (getUltimoComando().equals( SigaaListaComando.CADASTRAR_BANCA_POS_ORIENTADOR ))
				return listaBancas();
			
			return cancelar();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
	}

	/** Retorna o membro da banca de p�s. 
	 * @return
	 */
	public MembroBancaPos getMembroBanca() {
		return membroBanca;
	}

	/** Seta o membro da banca de p�s. 
	 * @param membroBanca
	 */
	public void setMembroBanca(MembroBancaPos membroBanca) {
		this.membroBanca = membroBanca;
	}

	/** Retorna a cole��o de bancas cadastradas do discente. 
	 * @return
	 */
	public Collection<BancaPos> getBancasDoDiscente() {
		return bancasDoDiscente;
	}

	/** Seta a cole��o de bancas cadastradas do discente.
	 * @param bancasDoDiscente
	 */
	public void setBancasDoDiscente(Collection<BancaPos> bancasDoDiscente) {
		this.bancasDoDiscente = bancasDoDiscente;
	}


	/** Retorna a cole��o de trabalhos finais do discente. 
	 * @return
	 */
	public Collection<SelectItem> getTrabalhosFinais() {
		return trabalhosFinais;
	}

	/** Seta a cole��o de trabalhos finais do discente. 
	 * @param trabalhosFinais
	 */
	public void setTrabalhosFinais(Collection<SelectItem> trabalhosFinais) {
		this.trabalhosFinais = trabalhosFinais;
	}

	/** Retorna o discente que est� defendendo o trabalho. 
	 * @return
	 */
	public DiscenteStricto getDiscente() {
		return discente;
	}

	/** Seta o discente que est� defendendo o trabalho.
	 * @param discente
	 */
	public void setDiscente(DiscenteStricto discente) {
		this.discente = discente;
	}

	/** Retorna a lista de SelectItem de sub�reas de conhecimento. 
	 * @return
	 */
	public List<SelectItem> getSubAreas() {
		return subAreas;
	}

	/** Seta a lista de SelectItem de sub�reas de conhecimento.
	 * @param subAreas
	 */
	public void setSubAreas(List<SelectItem> subAreas) {
		this.subAreas = subAreas;
	}

	/** Retorna uma lista de SelectItem de membros da equipe do programa.
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public List<SelectItem> getEquipeDocente() throws DAOException, NegocioException {
		Unidade programa = getProgramaStricto();
		if( isUserInRole(SigaaPapeis.PPG) || isPortalDocente() )
			programa = discente.getGestoraAcademica();

		if (programa == null) {
			throw new NegocioException("N�o foi poss�vel descobrir o programa de p�s-gradua��o para o seu usu�rio.");
		} else {
			EquipeProgramaDao dao = getDAO( EquipeProgramaDao.class );
			List<SelectItem> lista = new ArrayList<SelectItem>();
			for (EquipePrograma equipe : dao.findByPrograma( programa.getId() )){
				lista.add(new SelectItem(equipe.getId(), equipe.getDescricao()));
			}
			return lista;
		}
	}

	/** Seta o docente da equipe do programa.
	 * @param id 
	 * @throws DAOException
	 */
	public void setDocenteEquipe(int id) throws DAOException {
		EquipePrograma docente = getGenericDAO().findByPrimaryKey(id, EquipePrograma.class);
		if (docente != null) {
			if (docente.getServidor() != null)
				membroBanca.setDocentePrograma(docente.getServidor());
			else
				membroBanca.setDocenteExternoInstituicao(docente.getDocenteExterno());
		}
	}

	/** Retorna o docente da equipe do programa. 
	 * @return
	 * @throws DAOException
	 */
	public int getDocenteEquipe() throws DAOException {
		EquipePrograma docente = new EquipePrograma();
		if (membroBanca.getDocentePrograma() != null) {
			Collection<EquipePrograma> equipe = getGenericDAO().findByExactField(EquipePrograma.class, "servidor.id", membroBanca.getDocentePrograma().getId());
			if (!isEmpty(equipe))
				docente = equipe.iterator().next();
		} else if (membroBanca.getDocenteExternoInstituicao() != null) {
			Collection<EquipePrograma> equipe = getGenericDAO().findByExactField(EquipePrograma.class, "docenteExterno.id", membroBanca.getDocenteExternoInstituicao().getId());
			if (!isEmpty(equipe))
				docente = equipe.iterator().next();
		}
		return docente.getId();
	}

	/** Lista as bancas de p�s gradua��o.<br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/stricto/menu_coordenador.jsp</li>
     * </ul>   
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#listar()
	 */
	@Override
	public String listar() throws ArqException {
		cadastrar = false;
		this.defesaAntiga = false;
		checkRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.PPG);
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.LISTAR_BANCA_POS);
		prepareMovimento(SigaaListaComando.ALTERAR_BANCA_POS);
		prepareMovimento(SigaaListaComando.REMOVER_BANCA_POS);
		
		return buscaDiscenteMBean.popular();
	}

	/** Altera as informa��es da banca.<br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/stricto/banca_pos/alterar_remover.jsp</li>
     * </ul> 
	 * @return
	 * @throws ArqException, DAOException 
	 */
	public String alterarBanca() throws ArqException, DAOException {
		initMembro();
		if (discente == null) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return cancelar();
		}
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		if (obj == null) obj = new BancaPos();
		obj = dao.findByPrimaryKey(obj.getId(), BancaPos.class);
		if (obj == null) {
			obj = new BancaPos();
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return cancelar();
		}
		obj.setUsuario(getUsuarioLogado());
		obj.getMembrosBanca().iterator();
		subAreas = toSelectItems(dao.findAreas(obj.getArea()), "id", "nome");

		grandeArea = new AreaConhecimentoCnpq();
		if (obj.getGrandeArea() != null)
			grandeArea = obj.getGrandeArea();
		area = new AreaConhecimentoCnpq();
		if (obj.getArea() != null)
			area = obj.getArea();
		subArea = new AreaConhecimentoCnpq();
		if (obj.getSubArea() != null)
			subArea = obj.getSubArea();
		especialidade = new AreaConhecimentoCnpq();
		if (obj.getEspecialidade() != null)
			especialidade = obj.getEspecialidade();

		carregaAreas(null);
		carregaSubAreas(null);
		carregaEspecialidades(null);
		
		if (isPortalDocente()){
			setOperacaoAtiva(SigaaListaComando.CADASTRAR_BANCA_POS_ORIENTADOR.getId());
			prepareMovimento(SigaaListaComando.CADASTRAR_BANCA_POS_ORIENTADOR);			
		} else {
			setOperacaoAtiva(SigaaListaComando.CADASTRAR_BANCA_POS.getId());
			prepareMovimento(SigaaListaComando.CADASTRAR_BANCA_POS);
		}
		
		if (obj.getMatriculaComponente() == null) {
			this.matriculasComponenteCombo = null;
			escolheAtividade = true;
			obj.setMatriculaComponente(new MatriculaComponente());
		} else {
			escolheAtividade = false;
		}
		
		obj.setHora(obj.getData());

		return telaDadosBanca();
	}
	
	/** 
	 * Aprova a banca de defesa.<br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/stricto/banca_pos/alterar_remover.jsp</li>
     * </ul> 
	 * @return
	 * @throws ArqException
	 * @throws RemoteException
	 * @throws ArqException 
	 */
	public String aprovarBanca() throws RemoteException, ArqException {

		try {
			if (obj == null) obj = new BancaPos();
			
			MovimentoCadastro mov = new MovimentoCadastro();
			
			int id = getParameterInt("id");
			obj = getGenericDAO().findByPrimaryKey(id, BancaPos.class);
			
			Unidade programa = obj.getDadosDefesa().getDiscente().getUnidade();
			mov.setObjAuxiliar(programa);			
			
			prepareMovimento(SigaaListaComando.APROVAR_BANCA_POS);
			mov.setCodMovimento(SigaaListaComando.APROVAR_BANCA_POS);
			mov.setObjMovimentado(obj);

			execute(mov);
					
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO,"Banca de P�s-Gradua��o");
			
			if (isPortalCoordenadorStricto())
				return listarBancasPendentesAprovacao();
			
			return listaBancas();			
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return cancelar();
		}
		
	}	
	
	/** Remove a banca de defesa.<br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/stricto/banca_pos/alterar_remover.jsp</li>
     * </ul> 
	 * @return
	 * @throws ArqException
	 * @throws RemoteException
	 * @throws ArqException 
	 */
	public String removerBanca() throws RemoteException, ArqException {

		try {
			
			MovimentoCadastro mov = new MovimentoCadastro();
			
			if( isPortalPpg() ){
				int idBancaPosRemocao = getParameterInt("idBancaPosRemocao");
				obj = getGenericDAO().findByPrimaryKey(idBancaPosRemocao, BancaPos.class);
				prepareMovimento(ArqListaComando.REMOVER);
				mov.setCodMovimento(ArqListaComando.REMOVER);
			}else{
				
				if (obj == null) obj = new BancaPos();
				
				if( isEmpty(obj.getStatus()) || obj.getStatus().equals(BancaPos.CANCELADA) ){
					addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
					return cancelar();
				}
				
				confirmaSenha();
				
				ValidatorUtil.validateRequired(obj.getObservacao(), "Justificativa", erros);
				ValidatorUtil.validateMaxLength(obj.getObservacao(), 800, "Justificativa", erros);
				
				if( hasErrors() )
					return null;
				
				prepareMovimento(SigaaListaComando.CANCELAR_BANCA_POS);
				mov.setCodMovimento(SigaaListaComando.CANCELAR_BANCA_POS);
				obj.setStatus(BancaPos.CANCELADA);
			}
			
			mov.setObjMovimentado(obj);

			execute(mov);
			
			BancaPosDao bpdao = getDAO(BancaPosDao.class);
			bancasDoDiscente = bpdao.findByDiscente(discente);
			
			if( !hasErrors() ){
				removeOperacaoAtiva();
				addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO,"Banca de P�s-Gradua��o");
			}
			
			return cancelar();
			
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return listaBancas();
		}
		
	}
	
	/** 
	 * Retornar todas as pessoas associadas a docente externo ou membros de banca de p�s
	 * para sele��o no campo autocomplete do cadastro de membro de banca de p�sgradua��o.
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
     *   <li>/stricto/banca_pos/membros.jsp</li>
     * </ul>  
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public Collection<Pessoa> autoCompleteMembroExterno(Object event) throws DAOException{
		MembroBancaPosDAO dao = getDAO(MembroBancaPosDAO.class);
		String nome = event.toString();
 		Collection<Pessoa> lista = dao.findPessoaByNome(nome);
 		return lista;
	}
	

	/** Define o predicado PRESIDENTE_BANCA.
	 * Para mais informa��es sobre Predicate:{@link http://commons.apache.org/collections/api-3.1/org/apache/commons/collections/Predicate.html} 
	 * 
	 * N�o � chamado por JSPs.
	 */
	public static final Predicate PRESIDENTE_BANCA = new Predicate() {
		public boolean evaluate(Object obj) {
			MembroBancaPos membro = (MembroBancaPos) obj;
			return membro.getFuncao() == MembroBancaPos.PRESIDENTE_BANCA;
		}
	};

	/**
	 * 
	 * Chamado para mostrar o hist�rico
	 * 
	 * Chamado por:
	 * sigaa.war/stricto/banca_pos/lista_bancas_pendentes.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String verHistorico() throws ArqException {
		int id = getParameterInt("id", 0);
		if (id > 0){
			BancaPos banca = getGenericDAO().findByPrimaryKey(id,BancaPos.class,"dadosDefesa","dadosDefesa.discente");
			HistoricoMBean historico = new HistoricoMBean();
			historico.setDiscente(getGenericDAO().refresh(banca.getDadosDefesa().getDiscente()));
			return historico.selecionaDiscente();
		} else 
			return null;

	}
	
	/** Retorna a lista de SelectItem de especialidades. 
	 * @return
	 */
	public List<SelectItem> getEspecialidades() {
		return especialidades;
	}

	/** Seta a lista de SelectItem de especialidades.
	 * @param especialidades
	 */
	public void setEspecialidades(List<SelectItem> especialidades) {
		this.especialidades = especialidades;
	}

	/** Retorna a lista de SelectItem de �reas de conhecimento. 
	 * @return
	 */
	public List<SelectItem> getAreas() {
		return areas;
	}

	/** Seta a lista de SelectItem de �reas de conhecimento.
	 * @param areas
	 */
	public void setAreas(List<SelectItem> areas) {
		this.areas = areas;
	}

	/** Retorna a grande �rea de conhecimento do CNPq. 
	 * @return
	 */
	public AreaConhecimentoCnpq getGrandeArea() {
		return grandeArea;
	}

	/** Seta a grande �rea de conhecimento do CNPq.
	 * @param grandeArea
	 */
	public void setGrandeArea(AreaConhecimentoCnpq grandeArea) {
		this.grandeArea = grandeArea;
	}

	/** Retorna a �rea de conhecimento do CNPq
	 * @return
	 */
	public AreaConhecimentoCnpq getArea() {
		return area;
	}

	/** Seta a �rea de conhecimento do CNPq
	 * @param area
	 */
	public void setArea(AreaConhecimentoCnpq area) {
		this.area = area;
	}

	/** Retorna a sub-�rea de conhecimento do CNPq. 
	 * @return
	 */
	public AreaConhecimentoCnpq getSubArea() {
		return subArea;
	}

	/** Seta a sub-�rea de conhecimento do CNPq.
	 * @param subArea
	 */
	public void setSubArea(AreaConhecimentoCnpq subArea) {
		this.subArea = subArea;
	}

	/** Retorna a especialidade da sub-�rea de conhecimento. 
	 * @return
	 */
	public AreaConhecimentoCnpq getEspecialidade() {
		return especialidade;
	}

	/** Seta a especialidade da sub-�rea de conhecimento.
	 * @param especialidade
	 */
	public void setEspecialidade(AreaConhecimentoCnpq especialidade) {
		this.especialidade = especialidade;
	}

	/** Indica se � cadastro de defesa antiga. 
	 * @return
	 */
	public boolean isDefesaAntiga() {
		return defesaAntiga;
	}

	/** Seta se � cadastro de defesa antiga. 
	 * @param defesaAntiga
	 */
	public void setDefesaAntiga(boolean defesaAntiga) {
		this.defesaAntiga = defesaAntiga;
	}

	/** Retorna o arquivo anexo de banca de defesa antiga. 
	 * @return
	 */
	public UploadedFile getArquivo() {
		return arquivo;
	}

	/** Seta o arquivo anexo de banca de defesa antiga.
	 * @param arquivo
	 */
	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	/** Indica que no formul�rio, o usu�rio deve escolher a matr�cula na atividade correspondente.
	 * @return
	 */
	public boolean isEscolheAtividade() {
		return escolheAtividade;
	}

	/** Seta que no formul�rio, o usu�rio deve escolher a matr�cula na atividade correspondente.
	 * @param escolheAtividade
	 */
	public void setEscolheAtividade(boolean escolheAtividade) {
		this.escolheAtividade = escolheAtividade;
	}

	public boolean isCadastroNovoMembro() {
		return cadastroNovoMembro;
	}

	public void setCadastroNovoMembro(boolean cadastroNovoMembro) {
		this.cadastroNovoMembro = cadastroNovoMembro;
	}
	
}