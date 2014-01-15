/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/04/2009
 *
 */
package br.ufrn.sigaa.espacofisico.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.espacofisico.EspacoFisicoDao;
import br.ufrn.sigaa.arq.dao.espacofisico.GestorEspacoFisicoDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.espacofisico.dominio.EspacoFisico;
import br.ufrn.sigaa.espacofisico.dominio.GestorEspacoFisico;
import br.ufrn.sigaa.espacofisico.struts.EntrarInfraEstruturaAction;
import br.ufrn.sigaa.rh.dominio.Designacao;

/**
 * MBean responsável por armazenar as unidades que o usuário é responsável.
 * Essas informações são carregas ao entrar no módulo de infra-estrutura
 * 
 * @see EntrarInfraEstruturaAction
 * @author Henrique André
 *
 */
@Component @Scope("session")
public class GestorUnidadesMBean extends SigaaAbstractController<GestorEspacoFisico> {
	
	public static final int NAO_INICIADO = 0;
	public static final int INICIADO = 1;
	
	private int estado;

	/**
	 * Todos os espaços físicos que o usuário é gestor
	 */
	private List<EspacoFisico> espacosFisicos;
	/**
	 * Todas as Unidades que o gestor é responsável
	 */
	private List<UnidadeGeral> unidades;

	/**
	 * Construtor Padrão
	 */
	public GestorUnidadesMBean() {
		estado = NAO_INICIADO;
	}
	
	/**
	 * Verifica se as unidades e espaços do usuário foram carregados.
	 * Caso não tenham sido, eles serão carregados.
	 * 
	 * JSP: Método não invocado por jsp´s.
	 * 
	 * @throws DAOException
	 */
	public void carregarEspacosUnidades() throws DAOException {
		
		if (isIniciado())
			return ;

		unidades = new ArrayList<UnidadeGeral>();
		espacosFisicos = new ArrayList<EspacoFisico>();
		
		carregarUnidades();
		carregarEspacosFisicos();
		
		estado = INICIADO;
	}
	
	/**
	 * Carrega os espaços físicos que o usuário tem acesso
	 * @throws DAOException
	 */
	private void carregarEspacosFisicos() throws DAOException {
		GestorEspacoFisicoDao dao = getDAO(GestorEspacoFisicoDao.class);
		List<GestorEspacoFisico> espacosGerenciados = dao.findByUsuario(getUsuarioLogado().getId());

		// carrega espaços compartilhados
		for (GestorEspacoFisico gestorEspacoFisico : espacosGerenciados) {
			
			if (gestorEspacoFisico.getEspacoFisico() == null) {
				Unidade unidade = gestorEspacoFisico.getUnidade();
				unidades.add(unidade);
				
				EspacoFisicoDao espacoFisicoDao = getDAO(EspacoFisicoDao.class);
				List<EspacoFisico> lista = espacoFisicoDao.findAllByUnidade(unidade.getId());
				espacosFisicos.addAll(lista);
			} else {
				espacosFisicos.add(gestorEspacoFisico.getEspacoFisico());
			}
		}
		
		if (getAcessoMenu().isChefeDepartamento() || 
				getAcessoMenu().isChefeUnidade() || 
				getAcessoMenu().isDiretorCentro() || 
				getAcessoMenu().isUnidadeEspecializada()) {
			
			EspacoFisicoDao espacoDao = getDAO(EspacoFisicoDao.class);

			int[] ids = new int[unidades.size()];
			
			for (int i = 0; i < unidades.size(); i++) {
				UnidadeGeral unidadeGeral = unidades.get(i);
				ids[i] = unidadeGeral.getId();
			}
			
			if (ids.length > 0) {
				espacosFisicos.addAll(espacoDao.findAllByUnidade(ids));
			}
		}
		
	}
	
	/**
	 * Unidades que o usuário possui permissão
	 * 
	 * @throws DAOException
	 */
	private void carregarUnidades() throws DAOException {
		
		if (getServidorUsuario() == null)
			return ;
		
		ServidorDao dao = getDAO(ServidorDao.class);
		Collection<Designacao> designacoes = dao.findDesignacoesAtivas(getServidorUsuario());
		
		UnidadeDao unidadeDao = getDAO(UnidadeDao.class);
		
		for (Designacao d : designacoes) {
			Collection<UnidadeGeral> filhas = unidadeDao.findBySubUnidades(d.getUnidadeDesignacao());
			unidades.add(d.getUnidadeDesignacao());
			unidades.addAll(filhas);
		}
	}
	
	public List<EspacoFisico> getEspacosFisicos() {
		return espacosFisicos;
	}

	public void setEspacosFisicos(List<EspacoFisico> espacosFisicos) {
		this.espacosFisicos = espacosFisicos;
	}

	public List<UnidadeGeral> getUnidades() {
		return unidades;
	}

	public void setUnidades(List<UnidadeGeral> unidades) {
		this.unidades = unidades;
	}

	/**
	 * Combo com as unidades que o usuário é responsável
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/infra_fisica/espaco_fisico/form.jsp</li>
	 * 	<li>sigaa.war/infra_fisica/gestor/busca.jsp</li>
	 * 	<li>sigaa.war/infra_fisica/gestor/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<SelectItem> getUnidadesCombo() {
		return toSelectItems(unidades, "id", "nome");
	}
	
	/**
	 * Combo com as unidades que o usuário é responsável
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/infra_fisica/gestor/busca.jsp</li>
	 * 	<li>sigaa.war/infra_fisica/gestor/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<SelectItem> getEspacosCombo() {
		return toSelectItems(espacosFisicos, "id", "codigo");
	}

	/**
	 * Indica se as unidades e espaços já foram carregados
	 * JSP: Método não invocado por jsp´s.
	 * @return
	 */
	public boolean isIniciado() {
		if (estado == INICIADO)
			return true;
		
		return false;
	}
	
}
