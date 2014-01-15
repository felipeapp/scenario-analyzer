/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 12/08/2013
 *
 */
package br.ufrn.sigaa.ensino_rede.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoCoordenacaoGeralRede;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.negocio.ConsolidacaoUtil;
import br.ufrn.sigaa.ensino_rede.dao.ProgramaRedeDao;
import br.ufrn.sigaa.ensino_rede.dao.TurmaRedeDao;
import br.ufrn.sigaa.ensino_rede.dominio.MatriculaComponenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.ProgramaRede;
import br.ufrn.sigaa.ensino_rede.dominio.TurmaRede;

/**
 * Managed bean para alteração da situação de matrículas de turmas do ensino em rede.
 *
 * @author Diego Jácome
 *
 */
@Component("alterarSituacaoMatriculaRede")
@Scope("request")
public class AlterarSituacaoMatriculaRedeMBean extends SigaaAbstractController<MatriculaComponenteRede> {

	/** Atalho para a view do listar turmas. */
	private static final String JSP_LISTAR_TURMAS = "/ensino_rede/modulo/alterar_situacao/listarTurmas.jsp";
	/** Atalho para a view de alterar situação das matrículas das turmas. */
	private static final String JSP_ALTERAR_SITUACAO = "/ensino_rede/modulo/alterar_situacao/alterarSituacao.jsp";
	/** Atalho para a view de confirmação da alteração da situação das matrículas das turmas. */
	private static final String JSP_CONFIRMAR_ALTERACAO = "/ensino_rede/modulo/alterar_situacao/confirmar.jsp";
	
	/** Instituição de Ensino de onde as turmas irão ser buscadas */
	private InstituicoesEnsino ies;
	
	/** Campus de onde as turmas irão ser buscadas */
	private CampusIes campus;
	
	/** Ano das turmas buscadas */
	private Integer ano;
	
	/** Periodo das turmas buscadas */
	private Integer periodo;
	
	/** Coleção de SelectItem de campus */
	private Collection<SelectItem> campusCombo;
	
	/** Turmas de ensino em rede que terão alunos consolidados. */
	private ArrayList<TurmaRede> turmas;
	
	/** Turma que será consolidada. */
	private TurmaRede turma;
	
	/** Matriculas dos discentes da turma da rede de ensino. */
	private List<MatriculaComponenteRede> matriculasRede;
	
	/** Matriculas escolhidas para a modificação do status. */
	private List<MatriculaComponenteRede> matriculasEscolhidas;

	/** Se está consolidando a turma. */
	private boolean consolidacao = false;
	
	/** Se está acessando via portal da unidade. */
	private boolean acessoPortal = false;
	
	/**
	 * Construtor da Classe
	 */
	public AlterarSituacaoMatriculaRedeMBean() {
		clear();
	}
	
	public void  clear() {
		ies = new InstituicoesEnsino();
		campus = new CampusIes();
		turmas = new ArrayList<TurmaRede> ();	
		campusCombo = new ArrayList<SelectItem>();
	}
	
	/**
	 * Iniciar fluxo geral para alteração de status de matricula<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/alterar_situacao/listarTurmas.jsp</li>
	 *   <li>/sigaa.war/ensino_rede/modulo/alterar_situacao/alterarSituacao.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws SegurancaException 
	 * @throws SegurancaException
	 */
	public String iniciarConsolidacao() throws SegurancaException {
		
		checkRole(SigaaPapeis.COORDENADOR_GERAL_REDE);
		
		clear();
		consolidacao = true;
		acessoPortal = false;
		return forward(JSP_LISTAR_TURMAS);
	}	
	
	/**
	 * Iniciar fluxo geral para alteração de status de matricula através do portal do coordenador da unidade<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/portal/portal.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws SegurancaException 
	 * @throws SegurancaException
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String iniciarConsolidacaoPortal() throws SegurancaException, HibernateException, DAOException {
		
		checkRole(SigaaPapeis.COORDENADOR_UNIDADE_REDE);
		
		clear();
		consolidacao = true;
		acessoPortal = true;
		return selecionarTurma();
	}	
	
	/**
	 * Busca as turmas que serão consolidadas de acordo com os parâmetros de busca.<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/alterar_situacao/listarTurmas.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws SegurancaException
	 */
	public String buscar() throws DAOException {
		
		TurmaRedeDao dao = null;
		
		try {
		
		if (isEmpty(ano))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		if (isEmpty(periodo))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Período");
		if (isEmpty(ies))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Instituição");
		if (isEmpty(campus))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Campus");
		
		if (hasErrors())
			return null;
		
		dao = getDAO(TurmaRedeDao.class);
		turmas = (ArrayList<TurmaRede>) dao.findTurmasAbertasByCampusAnoPeriodo(campus.getId(), ano, periodo);
		
		if (isEmpty(turmas)) {
			addMensagemErro("Não foram encontradas turmas ABERTAS com esses parâmetros.");
			return null;
		}
		
		}finally {
			if (dao!=null)
				dao.close();
		}	
		
		return forward(JSP_LISTAR_TURMAS);
	}
	
	/**
	 * Busca as matrículas que terão o status modificado.<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/alterar_situacao/listarTurmas.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws SegurancaException 
	 * @throws SegurancaException
	 */
	public String selecionarTurma () throws HibernateException, DAOException, SegurancaException {
		
		checkRole(SigaaPapeis.COORDENADOR_GERAL_REDE, SigaaPapeis.COORDENADOR_UNIDADE_REDE);
		
		Integer idTurma = getParameterInt("idTurma");
		TurmaRedeDao dao = null;
		
		try{
			
			dao = getDAO(TurmaRedeDao.class);
			turma = dao.findAndFetch(idTurma, TurmaRede.class,"docentesTurmas");

			matriculasRede = (List<MatriculaComponenteRede>) dao.findParticipantesTurma(idTurma, ConsolidacaoUtil.getSituacaoesAConsolidar());
			
			for (MatriculaComponenteRede m : matriculasRede) {
				m.setSelected(false);
				m.setNovaSituacaoMatricula(new SituacaoMatricula(m.getSituacao().getId(), m.getSituacao().getDescricao()));
			}
			
			if (!turma.isAberta()){
				matriculasEscolhidas = matriculasRede;
				return forward(JSP_CONFIRMAR_ALTERACAO);
			}
		}finally{
			if (dao!=null)
				dao.close();
		}
		
		return forward(JSP_ALTERAR_SITUACAO);

	}
	
	/**
	 * Busca as matrículas que terão o status modificado.<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/alterar_situacao/alterarSituacao.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws SegurancaException
	 */
	public String selecionarMatriculas () throws HibernateException, DAOException {
		
		boolean matriculaSelecionada = possuiMatriculasSelecionadas();
		
		if ( !matriculaSelecionada ) {
			addMensagemErro("Ao menos uma matrícula deve ser escolhida");
			return null;
		}
		
		matriculasEscolhidas = new ArrayList<MatriculaComponenteRede>();
		for(MatriculaComponenteRede m : matriculasRede){
			if (m.isSelected()){
				m.setSituacao(SituacaoMatricula.getSituacao(m.getNovaSituacaoMatricula().getId()));
				m.setNovaSituacaoMatricula(SituacaoMatricula.getSituacao(m.getNovaSituacaoMatricula().getId()));
				matriculasEscolhidas.add(m);
			}	
		}
		
		setOperacaoAtiva(SigaaListaComando.ALTERAR_SITUACAO_MATRICULAS_REDE.getId());
		return forward(JSP_CONFIRMAR_ALTERACAO);

	}

	/**
	 * verifica se possui matrículas selecionadas para alteração, no caso da consolidação de turma, todas as matrículas serão selecionadas.<br/><br/>
	 * Método não chamado por JSPs:
	 * @return
	 * @throws SegurancaException
	 */
	private boolean possuiMatriculasSelecionadas() {
		
		boolean matriculaSelecionada = false;
		if ( !consolidacao ){
			for(MatriculaComponenteRede m : matriculasRede)	{
				if (m.isSelected()){
					matriculaSelecionada = true;
					break;
				}
			}
		} else {
			matriculaSelecionada = true;
			for(MatriculaComponenteRede m : matriculasRede)	{
				m.setSelected(true);
			}
		}
		return matriculaSelecionada;
	}
	
	/**
	 * Altera as matrículas selecionadas.<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/alterar_situacao/confirmar.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws ArqException 
	 * @throws SegurancaException
	 */
	public String confirmar () throws ArqException {
		
		if(!isOperacaoAtiva(SigaaListaComando.ALTERAR_SITUACAO_MATRICULAS_REDE.getId())){
			addMensagemErro("Atenção! Esta operação foi concluída anteriormente. Por favor, reinicie o processo.");
			return null;			
		}
			
		prepareMovimento(SigaaListaComando.ALTERAR_SITUACAO_MATRICULAS_REDE);
		MovimentoCadastro movMatricula = new MovimentoCadastro();
		movMatricula.setObjMovimentado(turma);
		movMatricula.setColObjMovimentado(matriculasEscolhidas);
		movMatricula.setObjAuxiliar(consolidacao);
		movMatricula.setCodMovimento(SigaaListaComando.ALTERAR_SITUACAO_MATRICULAS_REDE);

		try {

			execute(movMatricula);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);

		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens( e.getListaMensagens() );
		} finally {
			setOperacaoAtiva(null);
		} 
		
		return cancelar();
	}
	
	/**
	 * Carrega o combo de campus de acordo com a instituição.<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/alterar_situacao/listarTurmas.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws SegurancaException
	 */
	public void carregarCampus(ValueChangeEvent e) throws DAOException { 
		Integer id = (Integer) e.getNewValue();
		GenericDAO dao = getGenericDAO();
		List<CampusIes> listaCampus = (List<CampusIes>) dao.findByExactField(CampusIes.class, "instituicao.id", id); 
		campusCombo = toSelectItems(listaCampus, "id", "nome");		
	}
	
	/**
	 * Retorna a Lista de instituições do programa em rede<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/alterar_situacao/listarTurmas.jsp</li>
	 * </ul>	
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getInstituicoesCombo() throws DAOException {
		
		TipoVinculoCoordenacaoGeralRede vinculo = (TipoVinculoCoordenacaoGeralRede) getUsuarioLogado().getVinculoAtivo().getTipoVinculo();
		ProgramaRede p = vinculo.getCoordenacao().getProgramaRede();
		ProgramaRedeDao pDao = null;
		List<SelectItem> combo = new ArrayList<SelectItem>();

		try {
			
			pDao = getDAO(ProgramaRedeDao.class);
			List<InstituicoesEnsino> instituicoes = pDao.findInstituicoesByPrograma(p.getId());
			
			combo = toSelectItems(instituicoes,"id","nome");
		} finally {
			if (pDao != null)
				pDao.close();
		}
		
		return combo;

	} 
	
	/**
	 * Retorna a Lista de Situações de Matrícula<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/alterar_situacao/alterarSituacao.jsp</li>
	 * </ul>	
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getSituacoesConsolidacao() throws DAOException {
	
		List<SelectItem> situacoes = new ArrayList<SelectItem>();
		situacoes.add( new SelectItem( SituacaoMatricula.APROVADO.getId(), "Aprovado" ) );
		situacoes.add( new SelectItem( SituacaoMatricula.REPROVADO.getId(), "Reprovado" ) );
		situacoes.add( new SelectItem( SituacaoMatricula.REPROVADO_FALTA.getId(), "Reprovado por Falta" ) );
		situacoes.add( new SelectItem( SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId(), "Reprovado por Média e Falta" ) );
		return situacoes;

	} 
	
	public String voltarAlterarSituacao () {
		return forward(JSP_ALTERAR_SITUACAO);
	}
	
	public Collection<SelectItem> getCampusCombo() {
		return campusCombo;
	}
	
	public InstituicoesEnsino getIes() {
		return ies;
	}

	public void setIes(InstituicoesEnsino ies) {
		this.ies = ies;
	}

	public CampusIes getCampus() {
		return campus;
	}

	public void setCampus(CampusIes campus) {
		this.campus = campus;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public ArrayList<TurmaRede> getTurmas() {
		return turmas;
	}

	public void setTurmas(ArrayList<TurmaRede> turmas) {
		this.turmas = turmas;
	}

	public void setCampusCombo(Collection<SelectItem> campusCombo) {
		this.campusCombo = campusCombo;
	}

	public void setMatriculasRede(List<MatriculaComponenteRede> matriculasRede) {
		this.matriculasRede = matriculasRede;
	}

	public List<MatriculaComponenteRede> getMatriculasRede() {
		return matriculasRede;
	}

	public void setMatriculasEscolhidas(List<MatriculaComponenteRede> matriculasEscolhidas) {
		this.matriculasEscolhidas = matriculasEscolhidas;
	}

	public List<MatriculaComponenteRede> getMatriculasEscolhidas() {
		return matriculasEscolhidas;
	}

	public void setConsolidacao(boolean consolidacao) {
		this.consolidacao = consolidacao;
	}

	public boolean isConsolidacao() {
		return consolidacao;
	}

	public void setTurma(TurmaRede turma) {
		this.turma = turma;
	}

	public TurmaRede getTurma() {
		return turma;
	}

	public void setAcessoPortal(boolean acessoPortal) {
		this.acessoPortal = acessoPortal;
	}

	public boolean isAcessoPortal() {
		return acessoPortal;
	}	
	
}
