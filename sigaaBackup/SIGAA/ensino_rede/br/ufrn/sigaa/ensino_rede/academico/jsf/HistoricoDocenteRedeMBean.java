/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
 * Created on 31/08/2013
*/
package br.ufrn.sigaa.ensino_rede.academico.jsf;

import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.ensino_rede.dominio.AlteracaoSituacaoDocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteRede;
import br.ufrn.sigaa.ensino_rede.jsf.EnsinoRedeAbstractController;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaDocente;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaDocenteRedeMBean;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaDocenteRedeMBean.ModoBusca;

/**
 * Managed bean responsável pelas operações sobre os dados do histórico do docente de ensino em rede.
 *
 * @author Diego Jácome
 *
 */
@Component("historicoDocenteRede") @Scope("session")
public class HistoricoDocenteRedeMBean  extends EnsinoRedeAbstractController<DocenteRede> implements SelecionaDocente{

	/** Atalho para a view do docente. */
	private static final String VIEW_DOCENTE = "/ensino_rede/docente_rede/view.jsp";
	
	/** Lista de alterações de um docente do ensino em rede. */
	private List<AlteracaoSituacaoDocenteRede> alteracoes;

	/** Contrutor padrão */
	public HistoricoDocenteRedeMBean() {}
	
	/**
	 * Limpa os dados do bean
	 * Método não invocado por JSP(s)
	 * @throws DAOException 
	 */	
	private void clear() {
		alteracoes = null;
		obj = null;	
	}
	
	/**
	 * Inícia o cadastro do docente
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/menu.jsp</li>
	 * </ul>
	 * @return 
	 * @throws NegocioException 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String iniciarConsultar() throws ArqException, NegocioException {
		clear();
		
		SelecionaDocenteRedeMBean mBean = getMBean("selecionaDocenteRedeMBean");
		mBean.setRequisitor(this);
		mBean.setCampus(getCampusIes());
		mBean.setModoBusca(ModoBusca.DETALHADA);
		mBean.setExibirComboCampus(true);
		mBean.setConsultar(true);
		return mBean.executar();
	}

	/**
	 * Busca os dados de alterações feitas no docente de ensino em rede
	 * Método não invocado por JSP(s)
	 * @throws DAOException 
	 */	
	public void recuperarDadosDocente () throws DAOException {
		
		GenericDAO dao = null;
		
		try {
			
			dao = getGenericDAO();
			alteracoes = (List<AlteracaoSituacaoDocenteRede>) dao.findByExactField(AlteracaoSituacaoDocenteRede.class, "docente.id", obj.getId(), "asc", "dataAlteracao");
			
		} finally {
			if (dao!=null)
				dao.close();
		}
		
	}
	
	public void setAlteracoes(List<AlteracaoSituacaoDocenteRede> alteracoes) {
		this.alteracoes = alteracoes;
	}

	public List<AlteracaoSituacaoDocenteRede> getAlteracoes() {
		return alteracoes;
	}
	
	@Override
	public String selecionaDocenteRede() throws ArqException {
		recuperarDadosDocente();
		return forward(VIEW_DOCENTE);
	}

	@Override
	public void setDocenteRede(DocenteRede docenteRede) throws ArqException {
		obj = docenteRede;		
	}
	
}
