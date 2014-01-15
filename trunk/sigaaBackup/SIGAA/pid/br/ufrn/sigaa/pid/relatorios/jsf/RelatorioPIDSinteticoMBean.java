/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 03/05/2010
 *
 */
package br.ufrn.sigaa.pid.relatorios.jsf;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pid.dao.PlanoIndividualDocenteDao;
import br.ufrn.sigaa.pid.dominio.PlanoIndividualDocente;

/**
 * MBean Responsável por Gerar o Relatório Sintético por Departamento do PID.
 * @author Arlindo
 *
 */
@Component("relatorioPID") @Scope("session")
public class RelatorioPIDSinteticoMBean  extends SigaaAbstractController<RelatorioPIDSinteticoMBean>{
	/** JSP do Relatório Sintético por Departamento */
	private final String JSP_RELATORIO_SINTETICO_DEPARTAMENTO = "/pid/relatorios/relatorio_sintetico_departamento.jsp";
	
	/** Lista com os dados do relatório. */
	private Collection<PlanoIndividualDocente> listagem = new ArrayList<PlanoIndividualDocente>();
	/** Construtor da Classe */
	public RelatorioPIDSinteticoMBean() {
	}

	/**
	 * Gera o Relatório Sintético por Departamento.
	 * <br/><br/>
	 * Método Chamado pela Seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String gerarRelatorioSintetico() throws DAOException{
		PlanoIndividualDocenteDao dao = getDAO(PlanoIndividualDocenteDao.class);
		try {
			listagem = dao.find(null,null,null,getUsuarioLogado().getServidor().getUnidade());
			if (listagem.isEmpty()){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}			
			final int CARGA_HORARIA_DEDICACAO_EXCLUSIVA = 40;
			for (PlanoIndividualDocente plano : listagem){
				if ( plano.getServidor().isDedicacaoExclusiva() )
					plano.getServidor().setRegimeTrabalho(CARGA_HORARIA_DEDICACAO_EXCLUSIVA);				
			}
		} finally {
			if (dao != null)
				dao.close();
		}		
		return forward(JSP_RELATORIO_SINTETICO_DEPARTAMENTO);
	}

	public Collection<PlanoIndividualDocente> getListagem() {
		return listagem;
	}

	public void setListagem(Collection<PlanoIndividualDocente> listagem) {
		this.listagem = listagem;
	}
}
