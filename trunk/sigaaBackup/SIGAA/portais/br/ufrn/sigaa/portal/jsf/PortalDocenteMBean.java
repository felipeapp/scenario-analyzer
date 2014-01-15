/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 08/10/2007 
 *
 */
package br.ufrn.sigaa.portal.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.agenda.jsf.AgendaTurmaMBean;
import br.ufrn.sigaa.arq.dao.ParametrosGestoraAcademicaDao;
import br.ufrn.sigaa.arq.dao.ensino.HorarioDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.projetos.EditalDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.struts.LogonSistemasAction;
import br.ufrn.sigaa.ava.dao.PermissaoAvaDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosPortalDocente;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.rh.dominio.Designacao;

/**
 * Managed-Bean Responsável por efetuar operações no portal do docente
 *
 * @author Gleydson
 *
 */

@Component(value="portalDocente")
@Scope(value="session")
public class PortalDocenteMBean extends SigaaAbstractController<PortalDocenteMBean> {

	/** Link do Portal Docente. */
	public static final String PORTAL_DOCENTE = "/portais/docente/docente.jsp";

	// usado para exibir com JSTL
	/** Perfil a ser exibido no portal. */
	private PerfilPessoa perfil;

	/** Armazena as turmas do docente. */
	private Collection<Turma> turmas;

	/** Total de editais publicados. */
	private int totalEditais;

	/** Turmas abertas do docente no semestre atual. */
	private Collection<Turma> turmasAbertas;
	
	/** Turmas habilitadas do docente atual. */
	private List<Turma> turmasHabilitadas;
	
	/** Vinculo escolhido pelo usuário logado. */
	private VinculoUsuario vinculoAtual;
	
	private Collection<Designacao> designacoes;

	public PortalDocenteMBean() throws DAOException {
		getPerfilLogado(getCurrentRequest());
		getDesignacoes();
	}

	/**
	 * Retorna o perfil do servidor do usuário logado
	 * @param req
	 * @return
	 */
	public void getPerfilLogado(HttpServletRequest req){
		Usuario usuarioLogado = (Usuario) req.getSession().getAttribute("usuario");
		if (usuarioLogado != null) {
			VinculoUsuario vinculoAtivo = usuarioLogado.getVinculoAtivo();
			if( vinculoAtivo == null )
				return;
			if (vinculoAtivo.isVinculoServidor()) {
				if (vinculoAtivo.getServidor().getIdPerfil() != null)
					perfil = PerfilPessoaDAO.getDao().get( vinculoAtivo.getServidor().getIdPerfil() );
			} else if (vinculoAtivo.isVinculoDocenteExterno()) {
				if (vinculoAtivo.getDocenteExterno().getIdPerfil() != null)
					perfil = PerfilPessoaDAO.getDao().get( vinculoAtivo.getDocenteExterno().getIdPerfil() );
			}
		}
	}

	/**
	 * Carrega as turmas do professor
	 * <br>
	 * JSPs:<ul><li>/SIGAA/app/sigaa.ear/sigaa.war/portais/tutor/turmas.jsp</li>
	 * 			<li>/SIGAA/app/sigaa.ear/sigaa.war/portais/docente/turmas.jsp</li>
	 * 			<li>/SIGAA/app/sigaa.ear/sigaa.war/portais/cpolo/turmas.jsp</li></ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> getTurmas() throws DAOException {
		Usuario usuario = getUsuarioLogado();
		if (turmas == null)
			vinculoAtual = usuario.getVinculoAtivo();

		if (turmas == null || !vinculoAtual.equals(usuario.getVinculoAtivo())) {
			TurmaDao turmaDao = getDAO(TurmaDao.class);
			
			if ( vinculoAtual.isVinculoServidor() ) {
				turmas = turmaDao.findByDocente(usuario.getServidorAtivo(), null, null, null, false, true);
			} else if ( vinculoAtual.isVinculoDocenteExterno() ) {
				turmas = turmaDao.findByDocenteExterno(usuario.getDocenteExterno(), null, null, false, true);
			}
			
			// Percorre as turmas procurando por subturmas e substituindo pelas agrupadoras.
			// Se o docente estiver associado a duas subturmas da mesma turma, não repete a agrupadora.
			List <Turma> auxTurmas = new ArrayList<Turma>();
			if (turmas != null) {
				auxTurmas = new ArrayList<Turma>(turmas);
				TurmaUtil.ordenarTurmas(auxTurmas);
			}
			
			List <Integer> idsAgrupadoras = new ArrayList <Integer> ();
			List <Integer> indicesTroca = new ArrayList <Integer> ();
			List <Integer> indicesRemocao = new ArrayList <Integer> ();
			List <Turma> turmasATrocar = new ArrayList <Turma> ();
			
			for (int i = 0; i < auxTurmas.size(); i++){
				Turma t = auxTurmas.get(i);
				
				// Se for uma subturma,
				if (t.getTurmaAgrupadora() != null){
					// Se for uma turma agrupadora já encontrada, não repete.
					if (idsAgrupadoras.contains(t.getTurmaAgrupadora().getId()))
						// Adiciona de tras para frente para não bagunçar os índices durante a remoção.
						indicesRemocao.add(0, i);
					// Senão, adiciona a turma para ser trocada.
					else {
						idsAgrupadoras.add(t.getTurmaAgrupadora().getId());
						indicesTroca.add(i);
						turmasATrocar.add(turmaDao.findByPrimaryKeyOtimizado(t.getTurmaAgrupadora().getId()));
					}
				}
			}
			
			if (!turmasATrocar.isEmpty()){
				// Substitui as subturmas por agrupadoras.
				for (int i = 0; i < turmasATrocar.size(); i++){
					auxTurmas.remove((int) indicesTroca.get(i));
					auxTurmas.add(indicesTroca.get(i), turmasATrocar.get(i));
				}
				
				// Remove as turmas repetidas.
				for (int i = 0; i < indicesRemocao.size(); i++)
					auxTurmas.remove((int) indicesRemocao.get(i));
			}
			
			turmas = auxTurmas;
			
			setDirBase("/portais/turma/");
		}

		return turmas;

	}

	/**
	 * Retorna todas as turmas abertas. Passíveis de alteração
	 * <br>
	 * JSPs:<ul><li>/SIGAA/app/sigaa.ear/sigaa.war/ava/ArquivoTurma/form.jsp</li>
	 * 			<li>/SIGAA/app/sigaa.ear/sigaa.war/ava/PortaArquivos/cabecalho.jsp</li>
	 * 			<li>/SIGAA/app/sigaa.ear/sigaa.war/ava/PortaArquivos/selecionaTurma.jsp</li>
	 * 			<li>/SIGAA/app/sigaa.ear/sigaa.war/ava/cabecalho.jsp</li>
	 * 			<li>/SIGAA/app/sigaa.ear/sigaa.war/portais/docente/docente.jsp</li>
	 * 			<li>/SIGAA/app/sigaa.ear/sigaa.war/portais/turma/cabecalho.jsp</li></ul>
	 * @return
	 * @throws ArqException 
	 */
	public synchronized Collection<Turma> getTurmasAbertas() throws ArqException {
		if (turmasAbertas == null && getUsuarioLogado() != null) {
			CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendario(getUsuarioLogado());

			TurmaDao turmaDao = getDAO(TurmaDao.class);
			ParametrosGestoraAcademicaDao paramDao = getDAO(ParametrosGestoraAcademicaDao.class);

			Collection<Turma> abertas = null;
			Collection<Turma> consolidadas = null;
			// Verificar se o usuário é um docente do quadro de docentes da instituição
			if ( getUsuarioLogado().getVinculoAtivo().isVinculoServidor() ) {
				abertas = turmaDao.findByDocenteOtimizado(
						getServidorUsuario(), SituacaoTurma.ABERTA, null, true);
				consolidadas = turmaDao.findByDocente(
						getServidorUsuario(), null, 
						calendario.getAno(), calendario.getPeriodo(), null,
						true, true, SituacaoTurma.CONSOLIDADA);
			} else if ( getUsuarioLogado().getVinculoAtivo().isVinculoDocenteExterno() ){
				// Caso contrário, verificar se é um docente externo
				abertas = turmaDao
						.findByDocenteExternoOtimizado(getUsuarioLogado()
								.getDocenteExterno(), SituacaoTurma.ABERTA,
								true);
				consolidadas = turmaDao.findByDocenteExterno(
						getUsuarioLogado().getDocenteExterno(),
						null, calendario.getAno(),
						calendario.getPeriodo(), null, true, true, SituacaoTurma.CONSOLIDADA);
			}
			
			turmasAbertas = new ArrayList<Turma>();
			if (abertas != null)
				turmasAbertas.addAll(abertas);
			if (consolidadas != null)
				turmasAbertas.addAll(consolidadas);
			
			// recuperando os horários das turmas que permitem horário flexível
			if (!isEmpty(turmasAbertas)) {
				HorarioDao horarioDao = getDAO(HorarioDao.class);
				for (Turma t : turmasAbertas) {
					if (t.getDisciplina().isPermiteHorarioFlexivel()) {
						Turma turma = horarioDao.refresh(t);
						t.setHorarios(turma.getHorarios());
						t.getHorarios().iterator();
					}
				}
			}
				
			List<Turma> agrupadoras = new ArrayList<Turma>();
			for (Iterator<Turma> it = turmasAbertas.iterator(); it.hasNext(); ) {
				Turma t = it.next();
				if (t.isSubTurma()) {
					Turma agrupadora = t.getTurmaAgrupadora();
					
					if (!agrupadoras.contains(agrupadora)) {
						Unidade unidade = agrupadora.getDisciplina().getUnidade();
						if (unidade != null && unidade.getId() != 0) {
							ParametrosGestoraAcademica parametros = paramDao.findByUnidade(unidade.getId(), agrupadora.getDisciplina().getNivel());
							if (parametros != null) {
								agrupadora.getDisciplina().setHorasCredito(parametros.getHorasCreditosAula());
							}
						}
						agrupadora.getHorarios().addAll(t.getHorarios());
						agrupadoras.add(agrupadora);
					} else {
						agrupadora = agrupadoras.get(agrupadoras.indexOf(t.getTurmaAgrupadora()));
						agrupadora.getHorarios().addAll(t.getHorarios());
					}
					
					// Adiciona a subturma na turma agrupadora.
					Turma auxAgrupadora = agrupadoras.get(agrupadoras.indexOf(agrupadora));
					if (auxAgrupadora.getSubturmas() == null)
						auxAgrupadora.setSubturmas(new ArrayList <Turma> ());
					if (!auxAgrupadora.getSubturmas().contains(t))
						auxAgrupadora.getSubturmas().add(t);
					
					it.remove();
				} else {
					Unidade unidade = t.getDisciplina().getUnidade().getGestoraAcademica();
					if (unidade != null && unidade.getId() != 0) {
						ParametrosGestoraAcademica parametros = paramDao.findByUnidade(unidade.getId(), t.getDisciplina().getNivel());
						if (parametros != null) {
							Short horasCreditosAula = parametros.getHorasCreditosAula();
							if (horasCreditosAula != null)
								t.getDisciplina().setHorasCredito(horasCreditosAula);
						}
					}
				}
			}
			
			turmasAbertas.addAll(agrupadoras);
			if ( !isEmpty(turmasAbertas) ) {
				Collections.sort( ((List<Turma>) turmasAbertas), new Comparator<Turma>(){
					public int compare(Turma t1, Turma t2) {
						int result = 0;
						
						result = ((Character)t1.getDisciplina().getNivel()).compareTo(t2.getDisciplina().getNivel());
						
						if( result == 0 )
							result = ((Integer)t1.getAno()).compareTo(t2.getAno());
						
						if( result == 0 )
							result = ((Integer)t1.getPeriodo()).compareTo(t2.getPeriodo());
						
						if( result == 0 )
							result = t1.getDisciplina().getDetalhes().getNome().compareTo( t2.getDisciplina().getDetalhes().getNome() );
						
						return result;
					}
				});
			}
			// Carrega o bean que auxilia a exibição da agenda de horários de uam turma.
			AgendaTurmaMBean agendaBean = getMBean("agendaTurmaBean");
			agendaBean.setTurmasAbertas(turmasAbertas);
		}
		return turmasAbertas;
	}
	
	/**
	 * Lista de editais
	 * <br>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/portais/docente/docente.jsp
	 * @return
	 * @throws DAOException
	 */
	public Collection<Edital> getEditais() throws DAOException {
		EditalDao dao = getDAO(EditalDao.class);
		Collection<Edital> editais = dao.findNaoFinalizados();
		totalEditais = dao.getTotal();
		return editais;
	}

	public int getTotalEditais() {
		return totalEditais;
	}

	public void setTotalEditais(int totalEditais) {
		this.totalEditais = totalEditais;
	}

	/**
	 * Retorna uma coleção de {@link SelectItem} referentes as turmas abertas do professor.
	 * 
	 * @see #getTurmasAbertas()
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getTurmasAbertasCombo() throws ArqException {
		return toSelectItems(getTurmasAbertas(), "id", "descricao");
	}

	/**
	 * Retorna as turmas virtuais para as quais o docente tem permissão
	 * para acessar, apesar de serem turmas nas quais ele não é docente.
	 * <br>JSP: /portais/docente/docente.jsp
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> getTurmasVirtuaisHabilitadas() throws DAOException {
		PermissaoAvaDao dao = getDAO(PermissaoAvaDao.class);
		if ( turmasHabilitadas == null )
			turmasHabilitadas = dao.findTurmasHabilitadasByPessoaOtimizado(getUsuarioLogado().getPessoa());
		return turmasHabilitadas;
	}
	
	public PerfilPessoa getPerfil() {
		return perfil;
	}

	public void setPerfil(PerfilPessoa perfil) {
		this.perfil = perfil;
	}

	public void setTurmasAbertas(Collection<Turma> turmasAbertas) {
		this.turmasAbertas = turmasAbertas;
	}
	
	/**
	 * Método responsável por redirecionar o usuário ao painel de memorandos eletrônicos.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/coordenador.jsp</li>
	 * <li>/sigaa.war/lato/coordenador.jsp</li>
	 * <li>/sigaa.war/portais/docente/docente.jsp</li>
	 * <li>/sigaa.war/stricto//coordenacao.jsp</li>
	 * </ul>
	 */
	public String linkMemorando() throws Exception{
		if( Sistema.isMemorandosAtivos(Sistema.SIGAA) ){
			if( !getUsuarioLogado().getVinculoAtivo().isNull() && !getUsuarioLogado().getVinculoAtivo().isVinculoDiscente() ){
			
				if(!RequestUtils.getParameterBoolean("passaporte", getCurrentRequest()) && Sistema.isSipacAtivo() ) {
					// Unificação das operações com memorandos eletrônicos no SIPAC.
					LogonSistemasAction logonSistemasAction = new LogonSistemasAction();
					if(logonSistemasAction.logonSipac(getCurrentRequest(), getCurrentResponse(), true)) {
						return null;
					}
				}
				
			}
		}else{
			addMensagemErro("O sistema de Memorandos Eletrônicos se encontra desabilitado para o "+ RepositorioDadosInstitucionais.get("siglaSigaa") +"." );
		}
		return null;
	}
	
	/**
	 * Método responsável por redirecionar o usuário a página de busca da página pública do docente (www.docente.ufrn.br).
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ava/lista_participantes.jsp</li>
	 * </ul>
	 */
	public String getLinkPaginaPublicaDocente() {
		return ParametroHelper.getInstance().getParametro(ParametrosPortalDocente.URL_DOCENTE);
	}

	public Collection<Designacao> getDesignacoes() throws DAOException {
		if (isNotEmpty(designacoes)) 
			return designacoes;
			
		
		ServidorDao dao = getDAO(ServidorDao.class);
		
		// Verifica as designações do servidor
		designacoes = dao.findDesignacoesAtivas(getServidorUsuario());
		
		return designacoes;
	}

}
