/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 26/03/2009
 *
 */	
package br.ufrn.sigaa.assistencia.jsf;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.sae.AdesaoCadastroUnicoBolsaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.assistencia.cadunico.dominio.AdesaoCadastroUnicoBolsa;
import br.ufrn.sigaa.assistencia.cadunico.dominio.FormularioCadastroUnicoBolsa;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;

/**
 * Mbean responsável por realizar operações no ranking de pontuação
 * 
 * @author Henrique André
 *
 */
@SuppressWarnings("unchecked")
@Component("rankingPontuacao") @Scope("request")
public class RankingPontuacaoMBean extends SigaaAbstractController {

	private List<AdesaoCadastroUnicoBolsa> resultado;
	private FormularioCadastroUnicoBolsa cub;

	/**
	 * Abre o relatório com a pontuação dos discentes
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/sae/menu.jsp </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String iniciar() throws DAOException {

		AdesaoCadastroUnicoBolsaDao dao = getDAO(AdesaoCadastroUnicoBolsaDao.class);
		resultado = dao.findByAnoPeriodo(getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo());
		
		return forward("/sae/cadastro_unico/ranking_pontos.jsp");
	}

	public List<AdesaoCadastroUnicoBolsa> getResultado() {
		return resultado;
	}

	public void setResultado(List<AdesaoCadastroUnicoBolsa> resultado) {
		this.resultado = resultado;
	}

	public FormularioCadastroUnicoBolsa getCub() {
		return cub;
	}

	public void setCub(FormularioCadastroUnicoBolsa cub) {
		this.cub = cub;
	}
	
	public int getAno() throws DAOException{
		return CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getAno();
	}
	
	public int getPeriodo() throws DAOException{
		return CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getPeriodo();
	}
}
