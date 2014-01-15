/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.ensino.FormaIngressoDao;
import br.ufrn.sigaa.arq.dao.graduacao.RelatorioVagasOfertadasDao;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.OfertaVagasCurso;

/**
 * MBean responsável pela geração de relatórios de vagas ofertadas em função do ano e/ou da forma de ingresso
 * 
 * @author geyson
 *
 */

@Component("relatorioVagasOfertadas") @Scope("request")
public class RelatorioVagasOfertadasMBean extends AbstractRelatorioGraduacaoMBean {
	
	
	private OfertaVagasCurso oferta;
	private FormaIngresso formaIngresso;
	
	private final String CONTEXTO ="/graduacao/relatorios/vagas_ofertadas/";
	private final String JSP_SELECIONA_VAGAS_OFERTADAS = "seleciona_vagas_ofertadas";
	private final String JSP_REL_VAGAS_OFERTADAS = "rel_vagas_ofertadas";
	
	private boolean filtroAno;
	private boolean filtroFormaIngresso;
	
	/** Lista de dados do relatório. */
	private List<Map<String,Object>> lista = new ArrayList<Map<String,Object>>();
	
	
	/** Construtor padrão. */
	RelatorioVagasOfertadasMBean(){
		setOferta(new OfertaVagasCurso());
		setFormaIngresso(new FormaIngresso());
	}
	
	/**
	 * Faz o redirecionamento para a tela de preenchimento dos dados para a geração do relatório
	 * 
	 * JSP: sigaa/portais/rh_plan/menu.jsp
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public String iniciarRelatorioVagasOfertadas() throws DAOException, SegurancaException{
		checkListRole();
		return forward(CONTEXTO + JSP_SELECIONA_VAGAS_OFERTADAS);
	}
					  
	/**
	 * Gera um relatório tendo como entrada o ano e a forma de ingresso. 
	 * 
	 * JSP: sigaa/graduacao/relatorios/vagas_ofertadas/seleciona_vagas_ofertadas.jsp
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioVagasOfertadas() throws DAOException{
		RelatorioVagasOfertadasDao dao = getDAO(RelatorioVagasOfertadasDao.class);
		if(isFiltrosSelecionados()){
			if(isFiltroAno())
				if(oferta.getAno() <= 0 )
					addMensagemErro("Ano: Preencha filtro selecionado.");
			if(isFiltroFormaIngresso())
				if(formaIngresso.getId() <= 0)
					addMensagemErro("Forma de Igresso: Preencha filtro selecionado.");
			if(!hasErrors()){
				validaFiltros();
				lista = dao.relatorioVagasOfertadas(oferta.getAno(), formaIngresso.getId());
				if(lista.size() > 0 ){
					return forward(JSP_REL_VAGAS_OFERTADAS);
				}
				else{
					addMensagemErro("Não foi encontrado nenhum registro com os parâmetro(s) informado(s).");
					return null;
				}
			}				
		}
		else
			addMensagemWarning("Selecione algum campo para gerar o relatório de acordo com os filtros.");
		return null;
	}
	
	/**
	 * Valida se há algum filtro selecionado.
	 * JSP: Não invocado por JSP. 
	 * @return
	 */
	public boolean isFiltrosSelecionados(){
		if(!(isFiltroAno()) && !(isFiltroFormaIngresso()))
			return false;
	
			return true;
	}
	
	/**
	 * Valida filtros e evita que algum parâmetro seja enviado sem o filtro estar como selecionado. 
	 * JSP: Não invocado por JSP.
	 * @throws DAOException
	 */
	public void validaFiltros() throws DAOException{
		if(!isFiltroAno()){
			oferta.setAno(0);
		}
		if(isFiltroFormaIngresso()){
			 FormaIngressoDao dao = getDAO(FormaIngressoDao.class);
			 formaIngresso = dao.findByPrimaryKey(formaIngresso.getId(), FormaIngresso.class);
		}else
			formaIngresso.setId(0);
	}
	
	/** Retorna uma coleção de SelectItem de formas de ingresso.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllTipoEntradaCombo() throws DAOException {
		FormaIngressoDao dao = getDAO(FormaIngressoDao.class);
		Collection<SelectItem> combo = new ArrayList<SelectItem>();
		for (FormaIngresso formaIngresso : dao.findAll(FormaIngresso.class,
				"descricao", "asc")) {
			combo.add(new SelectItem(formaIngresso.getId(), formaIngresso
					.getDescricao()));
		}
		return combo;
	}
	
	/** Retorna uma coleção de SelectItem dos anos cadastrados, incluindo o ano corrente e o próximo ano.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAnosCombo() throws DAOException {
		Collection<SelectItem> combo = new ArrayList<SelectItem>(); 
		int proximoAno = CalendarUtils.getAnoAtual() + 1;
		for (int i = proximoAno; i >= 2000; i--) {
			combo.add(new SelectItem(new Integer(i), String.valueOf(i)));
		}
		return combo;
	}
	
	public OfertaVagasCurso getOferta() {
		return oferta;
	}

	public void setOferta(OfertaVagasCurso oferta) {
		this.oferta = oferta;
	}

	public FormaIngresso getFormaIngresso() {
		return formaIngresso;
	}

	public void setFormaIngresso(FormaIngresso formaIngresso) {
		this.formaIngresso = formaIngresso;
	}
	
	public List<Map<String, Object>> getLista() {
		return lista;
	}

	public void setLista(List<Map<String, Object>> lista) {
		this.lista = lista;
	}

	public boolean isFiltroAno() {
		return filtroAno;
	}

	public void setFiltroAno(boolean filtroAno) {
		this.filtroAno = filtroAno;
	}

	public boolean isFiltroFormaIngresso() {
		return filtroFormaIngresso;
	}

	public void setFiltroFormaIngresso(boolean filtroFormaIngresso) {
		this.filtroFormaIngresso = filtroFormaIngresso;
	}
	
}
