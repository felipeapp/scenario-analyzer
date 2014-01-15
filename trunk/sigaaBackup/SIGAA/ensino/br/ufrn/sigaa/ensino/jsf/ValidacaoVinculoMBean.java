/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 29/01/2013
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dao.ValidacaoVinculoDao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.ValidacaoVinculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * MBean responsável de fazer a validação do vínculo de um discente ingressante.
 * @author Diego Jácome
 */
@Component("validacaoVinculo")
@Scope("request")
public class ValidacaoVinculoMBean extends SigaaAbstractController<ValidacaoVinculo> {

	/** Link para a página de confirmação */
	private static final String LIST_PAGE = "/ensino/validacao_vinculo/busca_ingressante.jsp";
	/** Link para a página de listagem */
	private static final String VIEW = "/ensino/validacao_vinculo/dados_ingressante.jsp";
	
	/** Discente Buscado */
	private DiscenteAdapter discente;
	
	/** Discente Selecionado */
	private DiscenteAdapter discenteSelecionado;
	
	/** Lista de discentes ingressantes buscados. */
	private List<DiscenteAdapter> discentes;
	
	/** Calendario Academico Vigente*/
	private CalendarioAcademico cal;
	
	/** Curso do coordenador, utilizado quando o caso de uso é acessado pela coordenaçao */
	private Curso curso;
	
	/** Lista de matrizes curriculares */
	private List<SelectItem> matrizesCurriculares = new ArrayList<SelectItem>();
	
	/** Identifica se o caso de uso vai ser utilizado pra confirmar ou desconfirmar o vínculo*/
	private Boolean confirmar = true;
	
	/** Indica se filtra a busca pela matrícula. */
	private boolean filtroMatricula;
	/** Indica se filtra a busca pelo nome do discente */
	private boolean filtroNomeDiscente;
	/** Indica se filtra a busca pelo cpf. */
	private boolean filtroCpf;
	/** Indica se filtra a busca por nome do curso. */
	private boolean filtroNomeCurso;
	/** Indica se filtra a busca por matriz curricular. */
	private boolean filtroMatriz;
	
	/**
	 * Inicia o caso de uso de validação de vínculo
	 * <br/> <br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *      <li>/graduacao/menus/programa.jsp </li>
	 *      <li>/graduacfiltroMatriculaao/menu_coordenador.jsp </li>
	 * </ul>
	 * @return
	 */
	public String iniciar () throws SegurancaException, HibernateException, DAOException {
		
		ValidacaoVinculoDao vDao = null;
		filtroMatricula = false;
		filtroNomeDiscente = false;
		filtroCpf = false;
		filtroNomeCurso = false;
		filtroMatriz = false;
		
		try {
			checkRole(SigaaPapeis.DAE, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO);
			cal = getCalendarioVigente();
			// Não valida o calendário para o DAE
			if (!cal.isPeriodoValidacaoVinculoIngressante() && getUsuarioLogado().isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO)) {
				addMensagemErro(CalendarioAcademico.getDescricaoPeriodo("Validação de Vínculo de Ingressante",
						cal.getInicioValidacaoVinculoIngressante(), cal.getFimValidacaoVinculoIngressante()));
				return null;
			}
			
			discente = new DiscenteGraduacao();
			discente.setPessoa(new Pessoa());
			discente.setCurso(new Curso());
			DiscenteGraduacao dg = (DiscenteGraduacao) discente;
			dg.setMatrizCurricular(new MatrizCurricular());
			discentes = new ArrayList<DiscenteAdapter>();
			matrizesCurriculares = new ArrayList<SelectItem>(); 

			if (getUsuarioLogado().isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO)){
				curso = getCursoAtualCoordenacao();
				filtroNomeCurso = true;
				matrizesCurriculares.addAll(toSelectItems(getGenericDAO().findByExactField(
						MatrizCurricular.class, "curso.id", curso.getId()), "id", "descricao"));
				vDao = getDAO(ValidacaoVinculoDao.class);
				discentes = (List<DiscenteAdapter>) vDao.findDiscentesIngressantes (cal.getAno(),cal.getPeriodo(),curso.getId());
			}	
			
		} finally {
			if (vDao!=null)
				vDao.close();
		}
		
		return forward(LIST_PAGE);
	}

	/**
	 * Filtra os discentes ingressantes
	 * <br/> <br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *      <li>/ensino/validacao_vinculo/busca_ingressante.jsp </li>
	 * </ul>
	 * @return
	 */
	public String filtrarDiscente() throws HibernateException, DAOException{
		
		ValidacaoVinculoDao vDao = null;
		
		try {
			
			vDao = getDAO(ValidacaoVinculoDao.class);
			DiscenteGraduacao dg = (DiscenteGraduacao) discente;
			if (getUsuarioLogado().isUserInRole(SigaaPapeis.DAE)){
				
				if (
					(!filtroMatricula || (filtroMatricula && isEmpty(discente.getMatricula()))) &&
					(!filtroNomeDiscente || (filtroNomeDiscente && isEmpty(discente.getPessoa().getNome()))) && 
					(!filtroCpf || (filtroCpf && isEmpty(discente.getPessoa().getCpf_cnpj()))) && 
					(!filtroNomeCurso || (filtroNomeCurso && isEmpty(discente.getCurso().getId()))) && 
					(!filtroMatriz || (filtroMatriz && isEmpty(dg.getMatrizCurricular().getId())))
				){
					addMensagemErro("É necessário selecionar um dos critérios de busca");
					return null;
				}	
				discentes = (List<DiscenteAdapter>) vDao.findDiscentesIngressantes (cal.getAno(),cal.getPeriodo(),
						filtroMatricula ? discente.getMatricula() : null,
						filtroNomeDiscente ? discente.getPessoa().getNome() : null,
						filtroCpf ? discente.getPessoa().getCpf_cnpj() : null,
						filtroNomeCurso ? discente.getCurso().getId() : null,
						filtroMatriz ? dg.getMatrizCurricular().getId() : null);
			}
			if (getUsuarioLogado().isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO)){
				discentes = (List<DiscenteAdapter>) vDao.findDiscentesIngressantes (cal.getAno(),cal.getPeriodo(),
						filtroMatricula ? discente.getMatricula() : null,
						filtroNomeDiscente ? discente.getPessoa().getNome() : null,
						filtroCpf ? discente.getPessoa().getCpf_cnpj() : null,
						curso.getId(),
						filtroMatriz ? dg.getMatrizCurricular().getId() : null);
			}
			if (discentes == null || discentes.isEmpty())
				addMensagemErro("Nenhum discente ingressante encontrado com os critérios de busca selecionados.");
				
		} finally {
			if (vDao!=null)
				vDao.close();
		}
		
		return forward(LIST_PAGE);		
	}
	
	/**
	 * Seleciona o discente e carrega o movimento de confirmação de vínculo
	 * <br/> <br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *      <li>/ensino/validacao_vinculo/busca_ingressante.jsp </li>
	 * </ul>
	 * @return
	 */
	public String preConfirmarVinculo() throws ArqException{
		
		prepareMovimento(ArqListaComando.CADASTRAR);
		confirmar = true;
		
		Integer id = getParameterInt("idDiscente");
		for (DiscenteAdapter d : discentes){
			if (d.getId() == id){
				discenteSelecionado = d;
				break;
			}	
		}
		return forward(VIEW);
	}
	
	/**
	 * Seleciona o discente e carrega o movimento de desconfirmação de vínculo
	 * <br/> <br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *      <li>/ensino/validacao_vinculo/busca_ingressante.jsp </li>
	 * </ul>
	 * @return
	 */
	public String preDesconfirmarVinculo() throws ArqException{
		
		prepareMovimento(ArqListaComando.REMOVER);
		confirmar = false;
		
		Integer id = getParameterInt("idDiscente");
		for (DiscenteAdapter d : discentes){
			if (d.getId() == id){
				discenteSelecionado = d;
				break;
			}	
		}		
		return forward(VIEW);
	}
	
	/**
	 * Faz a validação do vínculo do discente 
	 * <br/> <br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *      <li>/ensino/validacao_vinculo/dados_ingressante.jsp </li>
	 * </ul>
	 * @return
	 */
	public String confirmarVinculo () throws NegocioException, ArqException{
		
		obj = new ValidacaoVinculo();
		obj.setDiscente(discenteSelecionado.getDiscente());
		obj.setUsuarioValidacao(getUsuarioLogado());
		obj.setDataValidacao(new Date());
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(ArqListaComando.CADASTRAR);

		execute(mov);
				
		return filtrarDiscente();		
	}
	
	/**
	 * Remove a validação do vínculo do discente 
	 * <br/> <br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *      <li>/ensino/validacao_vinculo/dados_ingressante.jsp </li>
	 * </ul>
	 * @return
	 */
	public String desconfirmarVinculo () throws NegocioException, ArqException{
		
		obj = getGenericDAO().findByExactField(ValidacaoVinculo.class, "discente.id", discenteSelecionado.getId(), true);
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(ArqListaComando.REMOVER);

		execute(mov);
	
		return filtrarDiscente();		
	}
	
	public String voltar () {
		return forward(LIST_PAGE);		
	}
	
	/**
	 * Carrega o combo das matrizes curriculares
	 * <br/> <br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *      <li>/ensino/validacao_vinculo/busca_ingressante.jsp </li>
	 * </ul>
	 * @return
	 */
	public void carregarMatrizes () throws DAOException {
		Integer id = discente.getCurso().getId();
		matrizesCurriculares = new ArrayList<SelectItem>();	
		if(!ValidatorUtil.isEmpty(id)){
			matrizesCurriculares.addAll(toSelectItems(getGenericDAO().findByExactField(
			MatrizCurricular.class, "curso.id", id), "id", "descricao"));
		}else 	
			matrizesCurriculares.add(new SelectItem(0, "Selecione"));
	}
	
	public List<SelectItem> getMatrizesCurriculares() {
		return matrizesCurriculares;
	}

	public void setMatrizesCurriculares(List<SelectItem> matrizesCurriculares) {
		this.matrizesCurriculares = matrizesCurriculares;
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	public List<DiscenteAdapter> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(List<DiscenteAdapter> discentes) {
		this.discentes = discentes;
	}

	public void setConfirmar(Boolean confirmar) {
		this.confirmar = confirmar;
	}

	public Boolean getConfirmar() {
		return confirmar;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setDiscenteSelecionado(DiscenteAdapter discenteSelecionado) {
		this.discenteSelecionado = discenteSelecionado;
	}

	public DiscenteAdapter getDiscenteSelecionado() {
		return discenteSelecionado;
	}

	public void setFiltroNomeDiscente(boolean filtroNomeDiscente) {
		this.filtroNomeDiscente = filtroNomeDiscente;
	}

	public boolean isFiltroNomeDiscente() {
		return filtroNomeDiscente;
	}

	public void setFiltroCpf(boolean filtroCpf) {
		this.filtroCpf = filtroCpf;
	}

	public boolean isFiltroCpf() {
		return filtroCpf;
	}

	public void setFiltroNomeCurso(boolean filtroNomeCurso) {
		this.filtroNomeCurso = filtroNomeCurso;
	}

	public boolean isFiltroNomeCurso() {
		return filtroNomeCurso;
	}

	public void setFiltroMatriz(boolean filtroMatriz) {
		this.filtroMatriz = filtroMatriz;
	}

	public boolean isFiltroMatriz() {
		return filtroMatriz;
	}

	public void setFiltroMatricula(boolean filtroMatricula) {
		this.filtroMatricula = filtroMatricula;
	}

	public boolean isFiltroMatricula() {
		return filtroMatricula;
	}

	
}
