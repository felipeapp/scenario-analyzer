/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/08/2013
 *
 */
package br.ufrn.sigaa.ensino_rede.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoCoordenacaoGeralRede;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoCoordenadorUnidadeRede;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino_rede.dao.DiscenteAssociadoDao;
import br.ufrn.sigaa.ensino_rede.dao.DocenteRedeDao;
import br.ufrn.sigaa.ensino_rede.dao.TurmaRedeDao;
import br.ufrn.sigaa.ensino_rede.dominio.ComponenteCurricularRede;
import br.ufrn.sigaa.ensino_rede.dominio.DadosCursoRede;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.MatriculaComponenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.ProgramaRede;
import br.ufrn.sigaa.ensino_rede.dominio.SituacaoDocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.TurmaRede;
import br.ufrn.sigaa.ensino_rede.negocio.MovimentoCadastroTurmaRede;

/**
 * Managed bean responsável pela criação de turmas.
 *
 * @author Diego Jácome
 *
 */
@SuppressWarnings("serial")
@Component("turmaRedeMBean") @Scope("session")
public class TurmaRedeMBean extends SigaaAbstractController<TurmaRede> implements SelecionaCampus {

	/** Atalho para a view do listar componentes. */
	private static final String JSP_LISTAR_COMPONENTES = "/ensino_rede/modulo/turma/listarComponentes.jsp";
	
	/** Atalho para a view dos dados gerais da turma. */
	private static final String JSP_DADOS_GERAIS = "/ensino_rede/modulo/turma/dadosGerais.jsp";
	
	/** Atalho para a view do listar discentes. */
	private static final String JSP_LISTAR_DISCENTES = "/ensino_rede/modulo/turma/listarDiscentes.jsp";
	
	/** Atalho para a view do listar docentes. */
	private static final String JSP_LISTAR_DOCENTES = "/ensino_rede/modulo/turma/listarDocentes.jsp";
	
	/** Atalho para a view da confirmacao da criação de turmas. */
	private static final String JSP_CONFIRMAR = "/ensino_rede/modulo/turma/confirmar.jsp";

	/** Atalho para a view de buscar turmas. */
	private static final String JSP_BUSCAR_TURMAS = "/ensino_rede/modulo/turma/buscar.jsp";
	
	/** Atalho para a view da turma. */
	private static final String JSP_VIEW_TURMA = "/ensino_rede/modulo/turma/viewTurma.jsp";
	
	/** Campus da turma que ser'cadastrada */
	private CampusIes campus;
	
	/** Lista de componentes curriculares */
	private ArrayList<ComponenteCurricularRede> componentes;
	
	/** Lista de discentes associados ao campus */
	private ArrayList<DiscenteAssociado> discentes;
	
	/** Matrículas associadas a turma*/
	private ArrayList<MatriculaComponenteRede> matriculas;
	
	/** Lista de discentes que serão matriculados na turma */
	private ArrayList<DiscenteAssociado> discentesEscolhidos;
	
	/** Lista de discentes que já estão matriculados na turma */
	private ArrayList<DiscenteAssociado> discentesMatriculados;
	
	/** Lista de discentes que serão desmatriculados na turma */
	private ArrayList<DiscenteAssociado> discentesRemovidos;
	
	/** Lista de docentes associados ao campus */
	private ArrayList<DocenteRede> docentes;
	
	/** Lista de docentes da turma */
	private ArrayList<DocenteRede> docentesEscolhidos;
	
	/** Lista de docentes que já estão associados na turma */
	private ArrayList<DocenteRede> docentesAssociados;
	
	/** Lista de discentes que serão dessociados na turma */
	private ArrayList<DocenteRede> docentesRemovidos;
	
	/** Lista de turma para alteração */
	private ArrayList<TurmaRede> turmas;
	
	/** Ano para filtragem dos discentes da turma */
	private Integer anoFiltro;
	
	/** Período para filtragem dos discentes da turma */
	private Integer periodoFiltro;

	/** Programa do coordenador */
	private ProgramaRede programa;
	
	// FILTROS PARA BUSCA DE TURMA
	
	/** Id do campus das turmas buscadas */
	private Integer idIes;
	
	/** Ano da turma buscada */
	private Integer anoTurmaFiltro;
	
	/** Período da turma buscada */
	private Integer periodoTurmaFiltro;
	
	/** Código dos componentes da turma buscada */
	private String codigoComponente;
	
	/** Nome dos componentes da turma buscada */
	private String nomeComponente;	
	
	/** Se é pra filtrar por ano e período */
	private boolean checkAnoPeriodo;
	
	/** Se é pra filtrar por código do componente */
	private boolean checkCodComponente;

	/** Se é pra filtrar por nome do componente */
	private boolean checkNomeComponente;
	
	// VARIÁVES QUE INDICAM O CONTEXTO DA OPERAÇÃO 
	
	/** Se está alterando ou cadastrando uma nova turma */
	private boolean alterar;
	
	/** Se está acessando via coordenador da unidade */
	private boolean coordenadorUnidade;
	
	/**
	 * Construtor da Classe
	 */
	public TurmaRedeMBean() {
		clearAll();
	}
	
	/**
	 * Limpa todos os dados do MBean.
	 */
	private void clearAll () {
		programa = null;
		turmas = new ArrayList<TurmaRede>();
		idIes = null;
		clear();
	}

	/**
	 * Limpa os dados do MBean.
	 */
	private void clear() {
		obj = new TurmaRede();
		campus = new CampusIes();
		componentes = new ArrayList<ComponenteCurricularRede>();
		discentes = new ArrayList<DiscenteAssociado>();
		docentes =  new ArrayList<DocenteRede>();
		discentesMatriculados = new ArrayList<DiscenteAssociado>();
		discentesRemovidos = new ArrayList<DiscenteAssociado>();
		docentesRemovidos = new ArrayList<DocenteRede>();
		discentesEscolhidos = new ArrayList<DiscenteAssociado>();
		docentesEscolhidos =  new ArrayList<DocenteRede>();
		anoTurmaFiltro = 0;
		periodoTurmaFiltro = 0;
		codigoComponente = null;
		nomeComponente = null;
	}
	
	/**
	 * Iniciar fluxo geral para criação de turmas<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/menu.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */
	public String iniciarCadastrar() throws ArqException {
		
		checkRole(SigaaPapeis.COORDENADOR_GERAL_REDE);
		clearAll();
		alterar = false;
		coordenadorUnidade = false;
		
		setOperacaoAtiva(SigaaListaComando.CADASTRO_TURMA_REDE.getId());
		prepareMovimento(SigaaListaComando.CADASTRO_TURMA_REDE);
		
		SelecionaCampusIesMBean mBean = getMBean("selecionaCampusRedeMBean");
		mBean.setRequisitor(this);
		
		return mBean.iniciar();
	}
	
	/**
	 * Iniciar fluxo geral para criação de turmas pelo portal do coordenador de ensino em rede, 
	 * não passa pela tela de seleção de campus.<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/portal/menu_coordenador_rede.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */
	public String iniciarCadastrarPortal() throws ArqException {
		
		checkRole(SigaaPapeis.COORDENADOR_GERAL_REDE, SigaaPapeis.COORDENADOR_UNIDADE_REDE);
		clearAll();	
		alterar = false;
		coordenadorUnidade = true;

		GenericDAO dao = null;
		
		try {
		
			setOperacaoAtiva(SigaaListaComando.CADASTRO_TURMA_REDE.getId());
			prepareMovimento(SigaaListaComando.CADASTRO_TURMA_REDE);
			
			dao = getGenericDAO();
			TipoVinculoCoordenadorUnidadeRede vinculo = (TipoVinculoCoordenadorUnidadeRede) getUsuarioLogado().getVinculoAtivo().getTipoVinculo();
			programa = vinculo.getCoordenacao().getDadosCurso().getProgramaRede();
			campus = vinculo.getCoordenacao().getDadosCurso().getCampus();
			componentes = (ArrayList<ComponenteCurricularRede>) dao.findByExactField(ComponenteCurricularRede.class, "programa.id", programa.getId(), "codigo , nome");

		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward(JSP_LISTAR_COMPONENTES);
	}
	
	@Override
	public String selecionaCampus() throws ArqException {
		
		GenericDAO dao = null;
		
		try {
			
			dao = getGenericDAO();
			TipoVinculoCoordenacaoGeralRede vinculo = (TipoVinculoCoordenacaoGeralRede) getUsuarioLogado().getVinculoAtivo().getTipoVinculo();
			programa = vinculo.getCoordenacao().getProgramaRede();
			componentes = (ArrayList<ComponenteCurricularRede>) dao.findByExactField(ComponenteCurricularRede.class, "programa.id", programa.getId(), "codigo , nome");
			
		} finally {
			if (dao != null)
				dao.close();
		}
		return forward(JSP_LISTAR_COMPONENTES);
	}

	/**
	 * Prepara os discentes para matrícula.<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/listarComponentes.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 * @throws SegurancaException
	 */
	public String submeterComponente () throws HibernateException, DAOException {
		
		GenericDAO dao = null;
		
		obj = new TurmaRede();
		discentes = new ArrayList<DiscenteAssociado>();
		docentes =  new ArrayList<DocenteRede>();
		discentesEscolhidos = new ArrayList<DiscenteAssociado>();
		discentesMatriculados = new ArrayList<DiscenteAssociado>();
		discentesRemovidos = new ArrayList<DiscenteAssociado>();
		docentesEscolhidos =  new ArrayList<DocenteRede>();
		
		try {
			
			int mesAtual = CalendarUtils.getMesAtual();
			obj.setAno(CalendarUtils.getAnoAtual());
			obj.setPeriodo(mesAtual <= 5 ? 1 : 2);
			anoFiltro = CalendarUtils.getAnoAtual();
			periodoFiltro = mesAtual <= 5 ? 1 : 2;
			
			Integer idComponente = getParameterInt("idComponente");
			dao = getGenericDAO();
			obj.setComponente(dao.findByPrimaryKey(idComponente, ComponenteCurricularRede.class));
			
			// Seta o campus da turma. O dados do curso serão sobreescritos pelos dados do discente
			obj.setDadosCurso(new DadosCursoRede());
			obj.getDadosCurso().setCampus(campus);
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward(JSP_DADOS_GERAIS);
	}
	
	/**
	 * Prepara os discentes para matrícula.<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/listarComponentes.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 * @throws SegurancaException
	 */
	public String submeterDadosGerais () throws HibernateException, DAOException {
		
		DiscenteAssociadoDao dao = null;
		
		try {
			
			validarDadosGerais();
			
			if (hasErrors())
				return null;
			
			dao = getDAO(DiscenteAssociadoDao.class);
			
			discentes = dao.findDiscenteByCampus(campus.getId(), anoFiltro, periodoFiltro);
			carregarDiscentes();
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward(JSP_LISTAR_DISCENTES);
	}
	
	/**
	 * Prepara os discentes para matrícula.<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/listarComponentes.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 * @throws SegurancaException
	 */
	public String buscarDiscentes () throws HibernateException, DAOException {
		
		DiscenteAssociadoDao dao = null;
		
		try {
						
			dao = getDAO(DiscenteAssociadoDao.class);
					
			discentes = dao.findDiscenteByCampus(campus.getId(), anoFiltro, periodoFiltro);
			carregarDiscentes();
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward(JSP_LISTAR_DISCENTES);
	}
	
	/**
	 * Prepara os discentes para matrícula.<br/><br/>
	 * Método não invocado por JSPs:
	 * @return
	 * @throws DAOException 
	 */
	private void carregarDiscentes() throws DAOException {

		DiscenteAssociadoDao dDao = null;
		TurmaRedeDao tDao = null;
		
		try {
		
			dDao = getDAO(DiscenteAssociadoDao.class);
			tDao = getDAO(TurmaRedeDao.class);
			
			ArrayList<Integer> idDiscentesAproveitados = dDao.findDiscentePorSituacao(obj.getComponente(), discentes, SituacaoMatricula.getSituacoesPagasEMatriculadas());
			
			// Remove os discentes que estão pagando ou já pagaram a disciplina da lista de discentes selecionáveis
			if (!isEmpty(discentes) && !isEmpty(idDiscentesAproveitados)){
				Iterator<DiscenteAssociado> it = discentes.iterator();			
				while (it.hasNext()){
					DiscenteAssociado d = it.next();
					for (Integer idAprovado : idDiscentesAproveitados){
						if (d.getId() == idAprovado.intValue())
							it.remove();
					}
				}
			}
			
			// Remove os discentes que já forma escolhidos da lista de discentes discentes selecionáveis.
			if (!isEmpty(discentes) && !isEmpty(discentesEscolhidos)){
				Iterator<DiscenteAssociado> it = discentes.iterator();
				while (it.hasNext()){
					DiscenteAssociado d = it.next();
					if (discentesEscolhidos.contains(d))
						it.remove();
				}
			}
			
			if (!alterar && isEmpty(discentes) && !isEmpty(idDiscentesAproveitados))
				addMensagemErro("Todos os discentes encontrados na busca já pagaram ou estão pagando a disciplina");
			
			if (alterar){
				
				if (isEmpty(discentes) && isEmpty(discentesEscolhidos) && !isEmpty(idDiscentesAproveitados))
					addMensagemErro("Todos os discentes encontrados na busca já pagaram a disciplina");
				
				
				// Carrega todos os discentes que já estão matriculados
				if (isEmpty(discentesMatriculados)){
					ArrayList<MatriculaComponenteRede> matriculados = (ArrayList<MatriculaComponenteRede>) tDao.findParticipantesTurma(obj.getId(), SituacaoMatricula.getSituacoesMatriculadas());
					discentesMatriculados = new ArrayList<DiscenteAssociado>();
					if (!isEmpty(matriculados)){
						for (MatriculaComponenteRede m : matriculados)
							discentesMatriculados.add(m.getDiscente());
					}						
				}
				
				if (isEmpty(discentesEscolhidos))
					discentesEscolhidos = new ArrayList<DiscenteAssociado>();				
				
				if (isEmpty(discentesRemovidos))
					discentesRemovidos = new ArrayList<DiscenteAssociado>();
				
				// Adiciona os discentes que estão matriculados na lista de discentes escolhidos 
				if (!isEmpty(discentesMatriculados)){
					
					for (DiscenteAssociado m : discentesMatriculados){
						
						// Se o discente já está inserido na lista de discentes escolhidos
						boolean inserido = false;
						// Se o discente está marcado para ser removido
						boolean removido = false;

						if (discentesEscolhidos.contains(m))
							inserido = true;
						
						if (discentesRemovidos.contains(m))
							removido = true;										
						
						if (!inserido && !removido)
							discentesEscolhidos.add(m);
						
						if (removido){
							boolean carregado = false;
							if (discentes.contains(m))
									carregado = true;
							if (!carregado)
								discentes.add(m);
						}	
					}	
				}
				
				// Garante que na lista de discentes escolhidos não existe nenhum discente removido.
				if (!isEmpty(discentesEscolhidos) && !isEmpty(discentesRemovidos)){
					
					Iterator<DiscenteAssociado> it = discentesEscolhidos.iterator();
					while (it.hasNext()){
						DiscenteAssociado d = it.next();
						if(discentesRemovidos.contains(d))
							it.remove();
					}
				}
			}
			
			if ( discentes != null ) {
				Collections.sort(discentes, new Comparator<DiscenteAssociado>(){
					public int compare(DiscenteAssociado d1, DiscenteAssociado d2) {
						int retorno = 0;
						// Verifica o ano
						retorno = d2.getAnoIngresso() - d1.getAnoIngresso();				
						// Verifica o período
						if( retorno == 0 )
							retorno = d2.getPeriodoIngresso() - d1.getPeriodoIngresso();
						if (retorno == 0)					
							retorno = d1.getNome().compareTo(d2.getNome());
						return retorno;
					}
				});
			}	
			
			if ( discentesEscolhidos != null ) {
				Collections.sort(discentesEscolhidos, new Comparator<DiscenteAssociado>(){
					public int compare(DiscenteAssociado d1, DiscenteAssociado d2) {
						int retorno = 0;
						// Verifica o ano
						retorno = d2.getAnoIngresso() - d1.getAnoIngresso();				
						// Verifica o período
						if( retorno == 0 )
							retorno = d2.getPeriodoIngresso() - d1.getPeriodoIngresso();
						if (retorno == 0)					
							retorno = d1.getNome().compareTo(d2.getNome());
						return retorno;
					}
				});
			}
			
		} finally {
			if ( dDao != null )
				dDao.close();
			if ( tDao != null )
				tDao.close();
		}
	}

	/**
	 * Adiciona os discentes escolhidos na lista
	 * 	<ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/listarDiscentes.jsp</li>
	 * </ul>	 
	 */
	public String adicionarDiscentes() {
		
		String[] selecionados = getParameterValues("selecionados");
		
		if (selecionados == null){
			addMensagemErro("Nenhum discente selecionado.");
			return null;
		}
		
		if (isEmpty(discentesEscolhidos))
			discentesEscolhidos = new ArrayList<DiscenteAssociado>();
		
		if (selecionados != null){
			for (int i = 0; i < selecionados.length; i++) {
				
				int id = Integer.parseInt(selecionados[i]);
				boolean possuiDiscente = false;
				
				for (DiscenteAssociado d : discentesEscolhidos)
					if (d.getId() == id)
						possuiDiscente = true;
				
				if (!possuiDiscente){
					for (DiscenteAssociado d : discentes) 
						if (d.getId() == id)
							discentesEscolhidos.add(d);		
				}
				
				// Removendo o discente da lista de discentes não escolhidos.
				if (!isEmpty(discentes)){
					Iterator<DiscenteAssociado> it = discentes.iterator();
					while (it.hasNext()){
						DiscenteAssociado d = it.next();
						if (d.getId() == id)
							it.remove();	
					}
				}
				
				// Removendo o discente da lista de discentes que serão desmatriculados.
				if (!isEmpty(discentesRemovidos)){
					Iterator<DiscenteAssociado> it = discentesRemovidos.iterator();
					while (it.hasNext()){
						DiscenteAssociado d = it.next();
						if (d.getId() == id)
							it.remove();	
					}
				}
			}
		}
		return forward(JSP_LISTAR_DISCENTES);
	}
	
	/**
	 * Remove os discentes escolhidos:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/listarDiscentes.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 * @throws SegurancaException
	 */
	public String removerDiscente () throws HibernateException, DAOException {
		
		Integer idDiscente = getParameterInt("idDiscente");
		
		if (discentesRemovidos == null)
			discentesRemovidos = new ArrayList<DiscenteAssociado>();
		
		Iterator<DiscenteAssociado> it = discentesEscolhidos.iterator();
		while (it.hasNext()){
			DiscenteAssociado d = it.next();
			if (d.getId() == idDiscente){
				discentesRemovidos.add(d);
				it.remove();
			}	
		}		
		
		buscarDiscentes();
		return forward(JSP_LISTAR_DISCENTES);
	}
	
	
	/**
	 * Carrega os discentes que serão matriculados e vai pra página de seleção dos docentes:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/listarDiscentes.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 * @throws SegurancaException
	 */
	public String submeterDiscentes () throws HibernateException, DAOException {
		
		DocenteRedeDao dao = null;
		
		try {
					
			if (isEmpty(discentesEscolhidos))
				addMensagemErro("É necessário adicionar pelo menos um discente.");
			
			if (hasErrors())
				return null;
			
			dao = getDAO(DocenteRedeDao.class);
			docentes = dao.findDocentesByCampus(campus, programa, SituacaoDocenteRede.getSituaoesValidas());
			carregarDocentes();
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward(JSP_LISTAR_DOCENTES);
	}
	
	/**
	 * Prepara os discentes para matrícula.<br/><br/>
	 * Método não invocado por JSPs:
	 * @return
	 * @throws DAOException 
	 */
	private void carregarDocentes() throws DAOException {
		
		DocenteRedeDao dDao = null;
		
		try {
		
			dDao = getDAO(DocenteRedeDao.class);
			
			// Remove os docentes que já forma escolhidos da lista de docentes selecionáveis.
			if (!isEmpty(docentes) && !isEmpty(docentesEscolhidos)){
				Iterator<DocenteRede> it = docentes.iterator();
				while (it.hasNext()){
					DocenteRede d = it.next();
					if (docentesEscolhidos.contains(d))
						it.remove();
				}
			}
			
			if (alterar){
				
				// Carrega todos os docentes que já estão associados
				if (isEmpty(docentesAssociados))
					docentesAssociados = dDao.findDocentesByTurma(obj.getId());
				
				if (isEmpty(docentesEscolhidos))
					docentesEscolhidos = new ArrayList<DocenteRede>();
				
				
				if (isEmpty(docentesRemovidos))
					docentesRemovidos = new ArrayList<DocenteRede>();
				
				// Adiciona os docentes associados na lista de docentes escolhidos 
				if (!isEmpty(docentesAssociados)){
					
					for (DocenteRede a : docentesAssociados){
						
						// Se o docente  já está inserido na lista de docentes escolhidos
						boolean inserido = false;
						// Se o docente está marcado para ser removido
						boolean removido = false;

						if (docentesEscolhidos.contains(a))
							inserido = true;
					
						if (docentesRemovidos.contains(a))
							removido = true;						
						
						if (!inserido && !removido)
							docentesEscolhidos.add(a);
						
						if (removido){
							boolean carregado = false;
							if (docentes.contains(a))
								carregado = true;
							if (!carregado)
								docentes.add(a);
						}	
					}	
				}
				
				// Garante que na lista de docentes escolhidos não existe nenhum docente escolhido.
				if (!isEmpty(docentes) && !isEmpty(docentesEscolhidos)){
					
					Iterator<DocenteRede> it = docentes.iterator();
					while (it.hasNext()){
						DocenteRede d = it.next();
						if (docentesEscolhidos.contains(d))
							it.remove();
					}
				}
				
				// Garante que na lista de docentes escolhidos não existe nenhum docente removido.
				if (!isEmpty(docentesEscolhidos) && !isEmpty(docentesRemovidos)){
					
					Iterator<DocenteRede> it = docentesEscolhidos.iterator();
					while (it.hasNext()){
						DocenteRede d = it.next();
						if (docentesRemovidos.contains(d))
							it.remove();
					}
				}
			}
			
			if ( docentes != null ) {
				Collections.sort(docentes, new Comparator<DocenteRede>(){
					public int compare(DocenteRede d1, DocenteRede d2) {
						int retorno = 0;	
						retorno = d1.getNome().compareTo(d2.getNome());
						return retorno;
					}
				});
			}	
			
			if ( docentesEscolhidos != null ) {
				Collections.sort(docentesEscolhidos, new Comparator<DocenteRede>(){
					public int compare(DocenteRede d1, DocenteRede d2) {
						int retorno = 0;	
						retorno = d1.getNome().compareTo(d2.getNome());
						return retorno;
					}
				});
			}	
		} finally {
			if ( dDao != null )
				dDao.close();
		}		
	}

	/**
	 * Carrega os docentes que lecionarão na turma e vai pra página de confirmação de cadastro da turma:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/listarDocentes.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 * @throws SegurancaException
	 */
	public String submeterDocentes () throws HibernateException, DAOException {

		adicionarDocente(true);				
		return forward(JSP_CONFIRMAR);
	}

	/**
	 * Adiciona os docentes escolhidos na lista
	 * 	<ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/listarDocentes.jsp</li>
	 * </ul>	 
	 */
	public String adicionarDocente() {
		return adicionarDocente(false);
	}
	
	/**
	 * Adiciona os docentes escolhidos na lista
	 * Não invocado por JSPs	 
	 */
	private String adicionarDocente(boolean submeter) {
		String[] selecionados = getParameterValues("selecionados");
		
		if (!submeter && selecionados == null){
			addMensagemErro("Nenhum docente selecionado.");
			return null;
		}
		
		if (isEmpty(docentesEscolhidos))
			docentesEscolhidos = new ArrayList<DocenteRede>();

		if (!isEmpty(docentes) && !isEmpty(selecionados)){
			
			for (int i = 0; i < selecionados.length; i++) {
				
				int id = Integer.parseInt(selecionados[i]);
				boolean possuiDocente = false;
				
				for (DocenteRede d : docentesEscolhidos)
					if (d.getId() == id)
						possuiDocente = true;
				
				if (!possuiDocente)
					for (DocenteRede d : docentes) 
						if (d.getId() == id)
							docentesEscolhidos.add(d);			
				
				// Removendo o docente da lista de docentes não escolhidos.
				if (!isEmpty(docentes)){
					Iterator<DocenteRede> it = docentes.iterator();
					while (it.hasNext()){
						DocenteRede d = it.next();
						if (d.getId() == id)
							it.remove();	
					}
				}	
				
				// Removendo o docente da lista de docentes que serão desassociados.
				if (!isEmpty(docentesRemovidos)){
					Iterator<DocenteRede> it = docentesRemovidos.iterator();
					while (it.hasNext()){
						DocenteRede d = it.next();
						if (d.getId() == id)
							it.remove();	
					}
				}			
			}
		}
		return forward(JSP_LISTAR_DOCENTES);

	}
	
	/**
	 * Remove os docentes escolhidos:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/listarDocentes.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 * @throws SegurancaException
	 */
	public String removerDocente () throws HibernateException, DAOException {
		
		Integer idDocente = getParameterInt("idDocente");

		if (docentesRemovidos == null)
			docentesRemovidos = new ArrayList<DocenteRede>();
		
		Iterator<DocenteRede> it = docentesEscolhidos.iterator();
		while (it.hasNext()){
			DocenteRede d = it.next();
			if (d.getId() == idDocente){
				docentesRemovidos.add(d);
				it.remove();
			}	
		}		
		
		submeterDiscentes();
		return forward(JSP_LISTAR_DOCENTES);
	}
		
	
	/**
	 * Cria a turma de ensino em rede, incluindo suas matriculas e os docentesTurmas associados.
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/confirmar.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws HibernateException 
	 * @throws ArqException 
	 * @throws SegurancaException
	 */
	public String confirmar () throws HibernateException, ArqException {
		
		if(!isOperacaoAtiva(SigaaListaComando.CADASTRO_TURMA_REDE.getId())){
			addMensagemErro("Atenção! Esta operação foi concluída anteriormente. Por favor, reinicie o processo.");
			return null;			
		}
				
		MovimentoCadastroTurmaRede mov = new MovimentoCadastroTurmaRede();
		mov.setTurma(obj);
		mov.setDiscentes(discentesEscolhidos);
		mov.setDocentes(docentesEscolhidos);
		mov.setCampus(campus);
		mov.setCodMovimento(SigaaListaComando.CADASTRO_TURMA_REDE);
			
		try {

			execute(mov);
			addMensagemInformation("Turma "+obj.getDescricaoSemDocente()+" cadastrada com sucesso.");

		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens( e.getListaMensagens() );
		} finally {
			setOperacaoAtiva(null);
		}
		
		return cancelar();
	}
	
	// OPERAÇÕES NA BUSCA DE TURMA
	
	/**
	 * Iniciar fluxo geral para consulta e alteração de turmas<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/menu.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */
	public String iniciarAlterar () throws ArqException {
		
		clearAll();
		alterar = true;
		coordenadorUnidade = false;
		
		int mesAtual = CalendarUtils.getMesAtual();
		anoTurmaFiltro = CalendarUtils.getAnoAtual();
		periodoTurmaFiltro = mesAtual <= 5 ? 1 : 2;
		
		setOperacaoAtiva(SigaaListaComando.ALTERAR_TURMA_REDE.getId());
		prepareMovimento(SigaaListaComando.ALTERAR_TURMA_REDE);
		
		TipoVinculoCoordenacaoGeralRede vinculo = (TipoVinculoCoordenacaoGeralRede) getUsuarioLogado().getVinculoAtivo().getTipoVinculo();
		programa = vinculo.getCoordenacao().getProgramaRede();
				
		return forward(JSP_BUSCAR_TURMAS);
	}
	
	/**
	 * Iniciar fluxo geral para alteração de turmas pelo portal do coordenador de ensino em rede, 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/portal/menu_coordenador_rede.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */
	public String iniciarAlterarCoordenacao () {
		
		clearAll();
		alterar = true;
		coordenadorUnidade = true;

		TipoVinculoCoordenadorUnidadeRede vinculo = (TipoVinculoCoordenadorUnidadeRede) getUsuarioLogado().getVinculoAtivo().getTipoVinculo();
		programa = vinculo.getCoordenacao().getDadosCurso().getProgramaRede();
		campus = vinculo.getCoordenacao().getDadosCurso().getCampus();
		idIes = campus.getInstituicao().getId();
		
		return forward(JSP_BUSCAR_TURMAS);
	}
	
	/**
	 * Busca as turmas que serão alteradas
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/buscar.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */
	public String buscarTurmas () throws HibernateException, DAOException {
		
		TurmaRedeDao tDao = null;
		
		try {
			
			if (!checkAnoPeriodo) { anoTurmaFiltro = null; periodoTurmaFiltro = null; }
			if (!checkCodComponente) codigoComponente = null;
			if (!checkNomeComponente) nomeComponente = null;
			
			if (checkAnoPeriodo && (anoTurmaFiltro == null || periodoTurmaFiltro == null))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano-Período");
			if (checkCodComponente && isEmpty(codigoComponente))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Código do Componente");
			if (checkNomeComponente && isEmpty(nomeComponente))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome do Componente");

			if (hasErrors())
				return null;
			
			tDao = getDAO(TurmaRedeDao.class);
			turmas= (ArrayList<TurmaRede>) tDao.buscar(programa.getId(), idIes, anoTurmaFiltro, periodoTurmaFiltro, codigoComponente, nomeComponente);
			
			if (isEmpty(turmas))
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			
		} finally {			
			if (tDao != null)
				tDao.close();
		}
		
		return forward(JSP_BUSCAR_TURMAS);
	}
	
	/**
	 * Acessa a turma que será alterada.
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/buscar.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */
	public String alterarTurma () throws DAOException {
		
		clear();
		
		Integer idTurma = getParameterInt("idTurma");
		TurmaRedeDao tDao = null;
		
		try {
			
			tDao = getDAO(TurmaRedeDao.class);
			obj = tDao.findByPrimaryKey(idTurma, TurmaRede.class);
			obj.setDocentesTurmas(tDao.findDocentesTurmaByTurma(obj.getId()));
			campus = tDao.findByExactField(CampusIes.class, "instituicao.id", idIes, true);

		} finally {			
			if (tDao != null)
				tDao.close();
		}
		
		return forward(JSP_DADOS_GERAIS);
	}
	
	/**
	 * Altera a turma de ensino em rede, incluindo suas matriculas e os docentesTurmas associados.
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/confirmar.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws HibernateException 
	 * @throws ArqException 
	 * @throws SegurancaException
	 */
	public String confirmarAlterar () throws ArqException {
		
		if(!isOperacaoAtiva(SigaaListaComando.ALTERAR_TURMA_REDE.getId())){
			addMensagemErro("Atenção! Esta operação foi concluída anteriormente. Por favor, reinicie o processo.");
			return null;			
		}
				
		MovimentoCadastroTurmaRede mov = new MovimentoCadastroTurmaRede();
		mov.setTurma(obj);
		mov.setDiscentes(discentesEscolhidos);
		mov.setDocentes(docentesEscolhidos);
		mov.setDiscentesRemovidos(discentesRemovidos);
		mov.setDocentesRemovidos(docentesRemovidos);
		mov.setCodMovimento(SigaaListaComando.ALTERAR_TURMA_REDE);
			
		try {

			execute(mov);
			addMensagemInformation("Turma "+obj.getDescricaoSemDocente()+" alterada com sucesso.");

		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens( e.getListaMensagens() );
		} finally {
			setOperacaoAtiva(null);
		}
		
		if (coordenadorUnidade)
			return iniciarAlterarCoordenacao();
		else	
			return iniciarAlterar();
	}
	
	/**
	 * Carrega os dados da turma para a visualização.
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/buscar.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */
	public String viewTurma () throws DAOException {
		
		Integer idTurma = getParameterInt("idTurma");
		TurmaRedeDao tDao = null;
		DocenteRedeDao dDao = null;

		try {
			
			tDao = getDAO(TurmaRedeDao.class);
			dDao = getDAO(DocenteRedeDao.class);
			obj = tDao.findByPrimaryKey(idTurma, TurmaRede.class);
			obj.setDocentesTurmas(tDao.findDocentesTurmaByTurma(obj.getId()));
			
			matriculas = (ArrayList<MatriculaComponenteRede>) tDao.findParticipantesTurma(obj.getId(), SituacaoMatricula.getSituacoesAtivas());	
			docentesAssociados = dDao.findDocentesByTurma(obj.getId());

		} finally {			
			if (tDao != null)
				tDao.close();
			if (dDao != null)
				dDao.close();
		}
		
		return forward(JSP_VIEW_TURMA);
	}
	
	
	/**
	 * Retorna para tela de seleção de componentes
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/listarComponentes.jsp</li>
	 * </ul>
	 */
	public String telaSelecaoCampus () {
		return forward("/ensino_rede/busca_geral/campus_ies/lista_campus.jsp");
	}
	
	/**
	 * Retorna para tela de seleção de busca de turmas
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/dadosGerais.jsp</li>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/listarDiscentes.jsp</li>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/listarDocentes.jsp</li>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/confirmar.jsp</li>
	 * </ul>
	 */
	public String telaBuscarTurmas () {
		return forward(JSP_BUSCAR_TURMAS);
	}
	
	/**
	 * Retorna para tela de seleção de componentes
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/dadosGerais.jsp</li>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/listarDiscentes.jsp</li>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/listarDocentes.jsp</li>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/confirmar.jsp</li>
	 * </ul>
	 */
	public String telaSelecaoComponentes () {
		return forward(JSP_LISTAR_COMPONENTES);
	}
	
	/**
	 * Retorna para tela de Dados Gerais
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/listarDiscentes.jsp</li>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/listarDocentes.jsp</li>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/confirmar.jsp</li>
	 * </ul>	
	 */
	public String telaDadosGerais () {
		return forward(JSP_DADOS_GERAIS);
	}
	
	/**
	 * Retorna para tela de seleção de discentes
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/listarDocentes.jsp</li>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/confirmar.jsp</li>
	 * </ul>	
	 */
	public String telaSelecaoDiscentes () {
		return forward(JSP_LISTAR_DISCENTES);
	}
	
	/**
	 * Retorna para tela de docentes
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/listarDiscentes.jsp</li>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/listarDocentes.jsp</li>
	 *   <li>/sigaa.war/ensino_rede/modulo/turma/confirmar.jsp</li>
	 * </ul>	
	 */
	public String telaSelecaoDocentes () {
		return forward(JSP_LISTAR_DOCENTES);
	}
	
	/**
	 * Faz as validações dos dados gerais da turma.
	 * Método não invocado por JSPs 
	 * @return
	 */
	private void validarDadosGerais() {

		if(isEmpty(obj.getAno()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		if(isEmpty(obj.getPeriodo()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Período");
		if(isEmpty(obj.getDataInicio()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Início");
		if(isEmpty(obj.getDataFim()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data Fim");
		
		if (!isEmpty(obj.getPeriodo()) && obj.getPeriodo() > 4)
			addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Período");

		
		if (obj.getDataInicio() != null && obj.getDataFim() != null){
			if (obj.getDataInicio().after(obj.getDataFim()))
				addMensagem(MensagensArquitetura.DATA_INICIO_MENOR_FIM, "Início e Fim da Turma");
		}
		
	}
	
	@Override
	public void setCampus(CampusIes campus) throws ArqException {
		this.campus = campus;		
	}
	
	public CampusIes getCampus () {
		return this.campus;
	}

	public void setComponentes(ArrayList<ComponenteCurricularRede> componentes) {
		this.componentes = componentes;
	}

	public ArrayList<ComponenteCurricularRede> getComponentes() {
		return componentes;
	}

	public void setDiscentes(ArrayList<DiscenteAssociado> discentes) {
		this.discentes = discentes;
	}

	public ArrayList<DiscenteAssociado> getDiscentes() {
		return discentes;
	}

	public void setDocentes(ArrayList<DocenteRede> docentes) {
		this.docentes = docentes;
	}

	public ArrayList<DocenteRede> getDocentes() {
		return docentes;
	}

	public void setDiscentesEscolhidos(ArrayList<DiscenteAssociado> discentesEscolhidos) {
		this.discentesEscolhidos = discentesEscolhidos;
	}

	public ArrayList<DiscenteAssociado> getDiscentesEscolhidos() {
		return discentesEscolhidos;
	}

	public void setDocentesEscolhidos(ArrayList<DocenteRede> docentesEscolhidos) {
		this.docentesEscolhidos = docentesEscolhidos;
	}

	public ArrayList<DocenteRede> getDocentesEscolhidos() {
		return docentesEscolhidos;
	}

	public void setAnoFiltro(Integer anoFiltro) {
		this.anoFiltro = anoFiltro;
	}

	public Integer getAnoFiltro() {
		return anoFiltro;
	}

	public void setPeriodoFiltro(Integer periodoFiltro) {
		this.periodoFiltro = periodoFiltro;
	}

	public Integer getPeriodoFiltro() {
		return periodoFiltro;
	}

	public void setTurmas(ArrayList<TurmaRede> turmas) {
		this.turmas = turmas;
	}

	public ArrayList<TurmaRede> getTurmas() {
		return turmas;
	}

	public void setIdIes(Integer idIes) {
		this.idIes = idIes;
	}

	public Integer getIdIes() {
		return idIes;
	}

	public void setAnoTurmaFiltro(Integer anoTurmaFiltro) {
		this.anoTurmaFiltro = anoTurmaFiltro;
	}

	public Integer getAnoTurmaFiltro() {
		return anoTurmaFiltro;
	}

	public void setPeriodoTurmaFiltro(Integer periodoTurmaFiltro) {
		this.periodoTurmaFiltro = periodoTurmaFiltro;
	}

	public Integer getPeriodoTurmaFiltro() {
		return periodoTurmaFiltro;
	}

	public void setCodigoComponente(String codigoComponente) {
		this.codigoComponente = codigoComponente;
	}

	public String getCodigoComponente() {
		return codigoComponente;
	}

	public void setNomeComponente(String nomeComponente) {
		this.nomeComponente = nomeComponente;
	}

	public String getNomeComponente() {
		return nomeComponente;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}

	public boolean isAlterar() {
		return alterar;
	}

	public void setDiscentesRemovidos(ArrayList<DiscenteAssociado> discentesRemovidos) {
		this.discentesRemovidos = discentesRemovidos;
	}

	public ArrayList<DiscenteAssociado> getDiscentesRemovidos() {
		return discentesRemovidos;
	}

	public void setDiscentesMatriculados(ArrayList<DiscenteAssociado> discentesMatriculados) {
		this.discentesMatriculados = discentesMatriculados;
	}

	public ArrayList<DiscenteAssociado> getDiscentesMatriculados() {
		return discentesMatriculados;
	}

	public void setDocentesAssociados(ArrayList<DocenteRede> docentesAssociados) {
		this.docentesAssociados = docentesAssociados;
	}

	public ArrayList<DocenteRede> getDocentesAssociados() {
		return docentesAssociados;
	}

	public void setDocentesRemovidos(ArrayList<DocenteRede> docentesRemovidos) {
		this.docentesRemovidos = docentesRemovidos;
	}

	public ArrayList<DocenteRede> getDocentesRemovidos() {
		return docentesRemovidos;
	}

	public void setCoordenadorUnidade(boolean coordenadorUnidade) {
		this.coordenadorUnidade = coordenadorUnidade;
	}

	public boolean isCoordenadorUnidade() {
		return coordenadorUnidade;
	}

	public void setCheckAnoPeriodo(boolean checkAnoPeriodo) {
		this.checkAnoPeriodo = checkAnoPeriodo;
	}

	public boolean isCheckAnoPeriodo() {
		return checkAnoPeriodo;
	}

	public void setCheckCodComponente(boolean checkCodComponente) {
		this.checkCodComponente = checkCodComponente;
	}

	public boolean isCheckCodComponente() {
		return checkCodComponente;
	}

	public void setCheckNomeComponente(boolean checkNomeComponente) {
		this.checkNomeComponente = checkNomeComponente;
	}

	public boolean isCheckNomeComponente() {
		return checkNomeComponente;
	}

	public void setMatriculas(ArrayList<MatriculaComponenteRede> matriculas) {
		this.matriculas = matriculas;
	}

	public ArrayList<MatriculaComponenteRede> getMatriculas() {
		return matriculas;
	}

}
