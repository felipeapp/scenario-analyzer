/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '21/05/2007'
 *
 */
package br.ufrn.sigaa.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean para realizar consultas de discentes
 * 
 * @author leonardo
 *
 */
@Component("consultaDiscente")
@Scope("request")
public class ConsultaDiscenteMBean extends SigaaAbstractController<Discente> implements OperadorDiscente {

	public ConsultaDiscenteMBean(){
		obj = new Discente();
	}

	public String iniciar() throws Exception {
		checkRole(SigaaPapeis.CONSULTADOR_ACADEMICO);
		return buscarDiscente();
	}
	
	/**
	 * Redirecionar para o Managed Bean para a busca de discentes de graduação
	 *
	 * @return
	 */
	public String buscarDiscente() throws SegurancaException{
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.CONSULTA);
		return buscaDiscenteMBean.popular();
	}
	
	public String selecionaDiscente() {
		return forward("/geral/consulta/info_discente.jsp");
	}

	public void setDiscente(DiscenteAdapter discente) {
		try {
			obj = getGenericDAO().findByPrimaryKey(discente.getId(), Discente.class);
		} catch (DAOException e) {
			e.printStackTrace();
			addMensagemErro("Não foi possível carregar o discente escolhido");
		}
	}
}
