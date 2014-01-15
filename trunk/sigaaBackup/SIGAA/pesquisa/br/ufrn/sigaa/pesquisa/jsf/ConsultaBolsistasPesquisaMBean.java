/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 12/07/2007
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.MembroProjetoDiscenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.LinhaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/**
 * MBean para efetuar consultas de bolsistas de iniciação científica a partir
 * da área pública do sigaa.
 * 
 * @author Leonardo Campos
 *
 */
public class ConsultaBolsistasPesquisaMBean extends SigaaAbstractController<MembroProjetoDiscente> {

	public final String JSP_CONSULTA_BOSISTAS = "/public/pesquisa/consulta_bolsistas.jsp";

	// Filtros utilizados na Consulta de Bolsistas de pesquisa.
	private boolean filtroGrupo;
	private boolean filtroCentro;
	private boolean filtroUnidade;
	private boolean filtroAluno;
	private boolean filtroOrientador;
	private boolean filtroModalidade;
	private boolean filtroCurso;
	private boolean filtroSexo;

	private MembroProjeto membroProjeto;
	private Unidade centro = new Unidade();
	private Unidade unidade = new Unidade();

	public ConsultaBolsistasPesquisaMBean(){
		obj = new MembroProjetoDiscente();
		obj.setDiscente(new Discente());
		obj.getDiscente().getPessoa().setSexo('M');
		obj.setPlanoTrabalho(new PlanoTrabalho());
		obj.getPlanoTrabalho().setOrientador(new Servidor());
		obj.getPlanoTrabalho().setProjetoPesquisa(new ProjetoPesquisa());
		obj.getPlanoTrabalho().getProjetoPesquisa().setLinhaPesquisa(new LinhaPesquisa());
		obj.getPlanoTrabalho().setTipoBolsa(new TipoBolsaPesquisa());

		membroProjeto = new MembroProjeto();
		membroProjeto.setServidor( new Servidor() );
	}

	/**
	 * Realiza um busca por todas bolsas de pesquisa, com os filtros desejados.
	 * <br><br>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/public/pesquisa/consulta_bolsistas.jsp
	 */
	public String buscar() throws DAOException {
		MembroProjetoDiscenteDao membroDao = getDAO(MembroProjetoDiscenteDao.class);
		Collection<MembroProjetoDiscente> lista;

		Integer idGrupoPesquisa = null;
		Integer idCentro = null;
		String nomeUnidade = null;
		String nomeAluno = null;
		String nomeOrientador = null;
		Integer idCurso = null;
		Integer modalidade = null;
		Character sexo = null;

		ListaMensagens erros = new ListaMensagens();

		// Definição dos filtros e validações
		if(filtroGrupo){
			idGrupoPesquisa = obj.getPlanoTrabalho().getProjetoPesquisa().getLinhaPesquisa().getGrupoPesquisa().getId();
			ValidatorUtil.validateRequiredId(idGrupoPesquisa, "Grupo de Pesquisa", erros);
		}
		if(filtroCentro){
			idCentro = centro.getId();
			ValidatorUtil.validateRequiredId(idCentro, "Centro", erros);
		}
		if(filtroUnidade){
			nomeUnidade = unidade.getNome();
			ValidatorUtil.validateRequired(nomeUnidade, "Unidade", erros);
		}
		if(filtroAluno){
			nomeAluno = obj.getDiscente().getNome();
			ValidatorUtil.validateRequired(nomeAluno, "Aluno", erros);
		}
		if(filtroOrientador){
			nomeOrientador = membroProjeto.getServidor().getNome();
			ValidatorUtil.validateRequired(nomeOrientador, "Orientador", erros);
		}
		if(filtroModalidade){
			modalidade = obj.getPlanoTrabalho().getTipoBolsa().getId();
			ValidatorUtil.validateRequiredId(modalidade, "Modalidade da Bolsa", erros);
		}
		if(filtroCurso){
			idCurso = obj.getDiscente().getCurso().getId();
			ValidatorUtil.validateRequiredId(idCurso, "Curso", erros);
		}
		if(filtroSexo)
				sexo = obj.getDiscente().getPessoa().getSexo();


		if (erros.isEmpty()) {
			lista = membroDao.filter(
					idGrupoPesquisa,
					idCentro,
					nomeUnidade,
					nomeAluno,
					nomeOrientador,
					modalidade,
					idCurso,
					sexo,
					null,
					null,
					false);

			if (!lista.isEmpty()) {
				setResultadosBusca(lista);
			} else {
				addMessage("Nenhum aluno foi encontrado de acordo com os critérios de busca utilizados.", TipoMensagemUFRN.WARNING);
			}

		} else {
			addMensagens(erros);
		}

		return forward(JSP_CONSULTA_BOSISTAS);

	}

	/** 
	 * Serve para cancelar a operação que está sendo realizada.  
	 * <br><br>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/public/pesquisa/consulta_bolsistas.jsp
	 */
	@Override
	public String cancelar() {
		super.cancelar();
		return forward("/public/home.jsp");
	}

	public Collection<SelectItem> getAllModalidadesCombo(){
		return toSelectItems(TipoBolsaPesquisa.getTipos());
	}

	public Unidade getCentro() {
		return centro;
	}

	public void setCentro(Unidade centro) {
		this.centro = centro;
	}

	public boolean isFiltroAluno() {
		return filtroAluno;
	}

	public void setFiltroAluno(boolean filtroAluno) {
		this.filtroAluno = filtroAluno;
	}

	public boolean isFiltroCentro() {
		return filtroCentro;
	}

	public void setFiltroCentro(boolean filtroCentro) {
		this.filtroCentro = filtroCentro;
	}

	public boolean isFiltroCurso() {
		return filtroCurso;
	}

	public void setFiltroCurso(boolean filtroCurso) {
		this.filtroCurso = filtroCurso;
	}

	public boolean isFiltroGrupo() {
		return filtroGrupo;
	}

	public void setFiltroGrupo(boolean filtroGrupo) {
		this.filtroGrupo = filtroGrupo;
	}

	public boolean isFiltroModalidade() {
		return filtroModalidade;
	}

	public void setFiltroModalidade(boolean filtroModalidade) {
		this.filtroModalidade = filtroModalidade;
	}

	public boolean isFiltroOrientador() {
		return filtroOrientador;
	}

	public void setFiltroOrientador(boolean filtroOrientador) {
		this.filtroOrientador = filtroOrientador;
	}

	public boolean isFiltroSexo() {
		return filtroSexo;
	}

	public void setFiltroSexo(boolean filtroSexo) {
		this.filtroSexo = filtroSexo;
	}

	public boolean isFiltroUnidade() {
		return filtroUnidade;
	}

	public void setFiltroUnidade(boolean filtroUnidade) {
		this.filtroUnidade = filtroUnidade;
	}

	public MembroProjeto getMembroProjeto() {
		return membroProjeto;
	}

	public void setMembroProjeto(MembroProjeto membroProjeto) {
		this.membroProjeto = membroProjeto;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}
}
