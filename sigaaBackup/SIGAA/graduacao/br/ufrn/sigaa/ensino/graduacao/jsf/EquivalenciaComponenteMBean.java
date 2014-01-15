/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 30/08/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;

/**
 * MBean responsável pelo controle de equivalências de determinado {@link ComponenteCurricular componente curricular}.
 * 
 * @author Rafael Gomes
 *
 */
@Component
@Scope("session")
public class EquivalenciaComponenteMBean extends SigaaAbstractController<ComponenteCurricular>{

	/** Lista de componentes curriculares encontrados na busca por componentes curriculares. */
	private DataModel listaDetalhes;

	/** Define o link para o formulário de dados gerais do componente curricular. */
	private static final String JSP_ATIVAR_DESATIVAR_COMPONENTE_DETALHES = "/graduacao/componente/ativar_desativar_equivalencia.jsp";
	/** Define o link para a lista de componentes curriculares. */
	private static final String JSP_LISTA = "/graduacao/componente/lista.jsf";
	
	private ComponenteDetalhes ccd;
	
	/** Construtor */
	public EquivalenciaComponenteMBean() {
		obj = new ComponenteCurricular();
		listaDetalhes = new ListDataModel();
		ccd = new ComponenteDetalhes();
	}

	/**
	 * Inicia o processo de ativação/desativação de equivalências do componente.
	 * <br />
	 * Método chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarControleEquivalencia() throws ArqException {
		setId();
		obj = getGenericDAO().findAndFetch(obj.getId(), ComponenteCurricular.class, "detalhes");
		List<ComponenteDetalhes> listCompDet = new ArrayList<ComponenteDetalhes>();
		
		Collection<ComponenteDetalhes> aux = getGenericDAO().findByExactField(ComponenteDetalhes.class, "componente", obj.getId(), "DESC", "data");		
		Boolean possuiEquivalencia = false;		
		ComponenteCurricularDao componenteDao = getDAO(ComponenteCurricularDao.class);
		for(ComponenteDetalhes det :  aux) {			
			if(det.getEquivalencia() != null && !det.getEquivalencia().trim().isEmpty()) {
				listCompDet.add(det);
				det.setEquivalencia(ExpressaoUtil.buildExpressaoFromDB(det.getEquivalencia(), componenteDao, false));
				possuiEquivalencia = true;				
			}
		}		
		
		listaDetalhes = new ListDataModel( listCompDet );
		
		if(!possuiEquivalencia) {
			addMensagemErro("Este Componente Curricular não possui Equivalência a ser Ativada ou Desativada.");
			return null;
		}
		
		return forward(JSP_ATIVAR_DESATIVAR_COMPONENTE_DETALHES);		
	}
	
	/**
	 * Habilita a equivalência selecionada para o componente.
	 * <br />
	 * Método chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/ativar_desativar_componente_detalhes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String habilitarEquivalencia() throws ArqException, NegocioException {
		prepareMovimento(SigaaListaComando.ATIVAR_DESATIVAR_COMPONENTE_DETALHE);		
		MovimentoCadastro mov = new MovimentoCadastro();		
		ComponenteDetalhes det = ( (ComponenteDetalhes) listaDetalhes.getRowData() );
		det.setDesconsiderarEquivalencia(false);
		det.setEquivalenciaValidaAte(null);
		mov.setObjMovimentado(det);		
		mov.setCodMovimento(SigaaListaComando.ATIVAR_DESATIVAR_COMPONENTE_DETALHE);
		execute(mov);
		
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		return null;
	}
	
	/**
	 * Desabilita a equivalência selecionada para o componente.
	 * <br />
	 * Método chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/ativar_desativar_componente_detalhes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String desabilitarEquivalencia() throws ArqException, NegocioException {
		prepareMovimento(SigaaListaComando.ATIVAR_DESATIVAR_COMPONENTE_DETALHE);		
		MovimentoCadastro mov = new MovimentoCadastro();	
		ComponenteDetalhes det = new ComponenteDetalhes();	
		det = ( (ComponenteDetalhes) listaDetalhes.getRowData() );
		det.setEquivalenciaValidaAte(det.getEquivalenciaValidaAte() != null ? det.getEquivalenciaValidaAte() : new Date());
		det.setDesconsiderarEquivalencia(false);
		mov.setObjMovimentado(det);		
		mov.setCodMovimento(SigaaListaComando.ATIVAR_DESATIVAR_COMPONENTE_DETALHE);
		execute(mov);
		
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		return null;
	}
	
	/**
	 * Desconsiderar a equivalência selecionada para o componente nos cálculos do discente.
	 * <br />
	 * Método chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/ativar_desativar_equivalencia.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String desconsiderarEquivalencia() throws ArqException, NegocioException {
		prepareMovimento(SigaaListaComando.ATIVAR_DESATIVAR_COMPONENTE_DETALHE);		
		MovimentoCadastro mov = new MovimentoCadastro();	
		ComponenteDetalhes det = new ComponenteDetalhes();	
		det = ( (ComponenteDetalhes) listaDetalhes.getRowData() );
		det.setEquivalenciaValidaAte(det.getEquivalenciaValidaAte() != null ? det.getEquivalenciaValidaAte() : new Date());
		det.setDesconsiderarEquivalencia(true);
		mov.setObjMovimentado(det);		
		mov.setCodMovimento(SigaaListaComando.ATIVAR_DESATIVAR_COMPONENTE_DETALHE);
		execute(mov);
		
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		return null;
	}
	
	/**
	 * Retorna para a tela do formulário da busca do componente Curricular.
	 * <br />
	 * Chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/ativar_desativar_equivalencia.jsp</li>
	 * <li>/sigaa.war/graduacao/componente/tipo_componente.jsp</li>
	 * </ul>
	 */
	public String formBusca() {
		return forward(JSP_LISTA);
	}

	public DataModel getListaDetalhes() {
		return listaDetalhes;
	}

	public void setListaDetalhes(DataModel listaDetalhes) {
		this.listaDetalhes = listaDetalhes;
	}

	public ComponenteDetalhes getCcd() {
		return ccd;
	}

	public void setCcd(ComponenteDetalhes ccd) {
		this.ccd = ccd;
	}
	


}
