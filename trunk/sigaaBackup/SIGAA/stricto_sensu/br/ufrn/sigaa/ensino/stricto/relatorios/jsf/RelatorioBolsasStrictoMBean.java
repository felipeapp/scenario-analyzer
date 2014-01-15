/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.relatorios.jsf;

import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.graduacao.RelatorioDiscenteSqlDao;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.DiscentesBolsas;

/**
 * Controller responsável pela geração do Relatório de Discentes Bolsistas de Stricto Sensu .
 * 
 * @author Victor Hugo
 *
 */
@Component("relatorioBolsasStrictoBean")
@Scope("request")
public class RelatorioBolsasStrictoMBean extends RelatoriosStrictoMBean {

	/** Data de início das bolsas, ao qual o relatório se restringe. */ 
	private Date dataInicial;
	/** Data de fim das bolsas, ao qual o relatório se restringe. */
	private Date dataFinal;
	/** Dados de discentes bolsistas para exibição no relatório. */
	private List<DiscentesBolsas> dadosRelatorio;

	/** Construtor padrão. */
	public RelatorioBolsasStrictoMBean() {
		dataInicial = null;
		dataFinal = null;
		dadosRelatorio = null;
	}
	
	/**
	 * Inicia a geração do relatório.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/stricto/menus/relatorios.jsp</li>
	 * <li>/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciar() {
		passivelSelecionarTodas = getUsuarioLogado().isUserInRole(SigaaPapeis.PPG) ;
		
        setTitulo("Bolsistas de Pós-Graduação");
        setPassivelSelecionarTodas( passivelSelecionarTodas );
        setTipoRelatorio( BOLSISTAS_POR_PROGRAMA );

        dataInicial = null;
		dataFinal = null;
		dadosRelatorio = null;
		
        if ( !passivelSelecionarTodas ) {
        	setUnidade( getProgramaStricto() );
        }
        
        return forward("/stricto/relatorios/form_discente_bolsista.jsp");
    }

	/**
	 * Gera o Relatório de Discentes Bolsistas de Stricto Sensu.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/stricto/relatorios/form_discente_bolsista.jsp</li>
	 * </ul>
	 * @see br.ufrn.sigaa.ensino.stricto.relatorios.jsf.RelatoriosStrictoMBean#gerarRelatorio()
	 */
	public String gerarRelatorioBolsistas() throws DAOException {
		try {
			ValidatorUtil.validateRequired(dataInicial, "Data inicial", erros);
			ValidatorUtil.validateRequired(dataInicial, "Data final", erros);
			if (hasErrors())
				return null;
			RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
			dadosRelatorio = dao.findDiscentesBolsas(getUnidade().getId(), dataInicial, dataFinal);
			if (ValidatorUtil.isEmpty(dadosRelatorio)) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			return forward("/stricto/relatorios/relatorio_bolsistas.jsp");
		} catch (ArqException e) {
			throw new DAOException(e);
		}
	}

	/** Retorna a data de início das bolsas, ao qual o relatório se restringe. 
	 * @return
	 */
	public Date getDataInicial() {
		return dataInicial;
	}

	/** Seta a data de início das bolsas, ao qual o relatório se restringe.
	 * @param dataInicial
	 */
	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	/** Retorna a data de fim das bolsas, ao qual o relatório se restringe. 
	 * @return
	 */
	public Date getDataFinal() {
		return dataFinal;
	}

	/** Seta a data de fim das bolsas, ao qual o relatório se restringe.
	 * @param dataFinal
	 */
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	/** Retorna os dados de discentes bolsistas para exibição no relatório. 
	 * @return
	 */
	public List<DiscentesBolsas> getDadosRelatorio() {
		return dadosRelatorio;
	}

	/** Seta os dados de discentes bolsistas para exibição no relatório.
	 * @param dadosRelatorio
	 */
	public void setDadosRelatorio(List<DiscentesBolsas> dadosRelatorio) {
		this.dadosRelatorio = dadosRelatorio;
	}
	
}
