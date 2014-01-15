package br.ufrn.sigaa.pesquisa.jsf.relatorios;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.pesquisa.RelatoriosPesquisaDao;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.AbstractRelatorioGraduacaoMBean;
import br.ufrn.sigaa.pesquisa.relatorios.LinhaRelatorioCentroDepartamento;

/**
 * Maneger Bean responsável pela geração dos relatório de Pesquisa.
 * 
 * @author Jean Guerethes
 */
@Component @Scope("request")
public class RelatoriosPesquisaMBean extends AbstractRelatorioGraduacaoMBean {

	/** Armazena o quantitativo dos bolsistas por centro e departamento */
	Map<String, Collection<LinhaRelatorioCentroDepartamento>> relatorioQuantCentroDeparta;
	
	/**
	 * Método responsável pela geração do relatório quantitativo de bolsistas por centro, agrupando por
	 * departamento.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioQuantBolsasCentroDepartamento() throws DAOException{
		relatorioQuantCentroDeparta = new HashMap<String, Collection<LinhaRelatorioCentroDepartamento>>();
		RelatoriosPesquisaDao dao = getDAO(RelatoriosPesquisaDao.class);
		try {
			relatorioQuantCentroDeparta = dao.findRelatorioQuantBolsasCentroDepartamento();
		} finally {
			dao.close();
		}
		return forward("/pesquisa/relatorios/relatorio_quant_centro_depart.jsp");
	}

	public int getTotal() {
		return LinhaRelatorioCentroDepartamento.getTotal(relatorioQuantCentroDeparta);
	}
	
	public Map<String, Collection<LinhaRelatorioCentroDepartamento>> getRelatorioQuantCentroDeparta() {
		return relatorioQuantCentroDeparta;
	}

	public void setRelatorioQuantCentroDeparta(
			Map<String, Collection<LinhaRelatorioCentroDepartamento>> relatorioQuantCentroDeparta) {
		this.relatorioQuantCentroDeparta = relatorioQuantCentroDeparta;
	}
		
}