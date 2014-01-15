/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 23/03/2011
 */
package br.ufrn.sigaa.relatoriosgestao.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.relatoriosgestao.dao.RelatorioUnidadesAcademicasDao;

/**
 * Este MBean tem como finalidade de auxiliar na geração de relatórios de Unidades Acadêmicas
 * 
 * @author Arlindo 
 *
 */
@Component("relatorioUnidadesAcademicasMBean") @Scope("request")
public class RelatorioUnidadesAcademicasMBean extends SigaaAbstractController<Unidade> {
	
	/** Lista com o resultado do relatório */
	private List<Map<String, Object>> listagem = new ArrayList<Map<String,Object>>();
	
	/** Lista com o detalhamento das informações */
	private List<Unidade> detalhamento = new ArrayList<Unidade>();
	
	/** Tipo da Unidade Selecionada */
	private String tipoUnidade;
	
	/**
	 * Inicia o relatório de Totais de Unidades Acadêmicas
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/relatorios/abas/ensino.jsp</li/>
	 * </ul>
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String iniciar() throws HibernateException, DAOException{
		
		RelatorioUnidadesAcademicasDao dao = getDAO(RelatorioUnidadesAcademicasDao.class);
		try {
			listagem = dao.findTotalUnidadeAcademica();
			
			if (ValidatorUtil.isEmpty(listagem)){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward("/portais/relatorios/unidades_academicas/lista.jsp");
	}

	/**
	 * Detalha o relatório conforme o tipo que foi clicado
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/portais/relatorios/unidades_academicas/lista.jsp</li/>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String detalhar() throws HibernateException, DAOException{
		
		int tipo = getParameterInt("id", 0);
		
		RelatorioUnidadesAcademicasDao dao = getDAO(RelatorioUnidadesAcademicasDao.class);
		try {
			detalhamento = dao.findUnidadesByTipo(tipo);
			
			if (tipo > 0)
				tipoUnidade = TipoUnidadeAcademica.getDescricao(tipo);
			else
				tipoUnidade = "TODOS";
			
		} finally {
			if (dao != null)
				dao.close();
		}		
		
		return forward("/portais/relatorios/unidades_academicas/detalhe.jsp");
	}

	public List<Map<String, Object>> getListagem() {
		return listagem;
	}

	public void setListagem(List<Map<String, Object>> listagem) {
		this.listagem = listagem;
	}

	public List<Unidade> getDetalhamento() {
		return detalhamento;
	}

	public void setDetalhamento(List<Unidade> detalhamento) {
		this.detalhamento = detalhamento;
	}

	public String getTipoUnidade() {
		return tipoUnidade;
	}

	public void setTipoUnidade(String tipoUnidade) {
		this.tipoUnidade = tipoUnidade;
	}
}
