/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/10/2009
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.ensino.stricto.BancaPosDao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.ensino.stricto.relatorios.jsf.RelatoriosStrictoMBean;

/**
 * MBean responsável pelo relatório de participantes das
 * bancas (qualificação e defesa) do nível Stricto Sensu.
 * 
 * @author Bráulio
 */
@Component ("participantesDasBancas")
@Scope ("request")
public class ParticipantesDasBancasMBean extends RelatoriosStrictoMBean {

	private Integer anoBanca;
	
	private Integer periodoBanca;
	
	/**Lista de bancas que serão manipuladas no relatório gerado */
	private Map<String, List<BancaPos> > bancas;
	
	/**
	 * Método para inicializar o Bean.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/stricto/menu_coordenador.jsp</li>
	 *   </ul>
	 */
	public String iniciar() {
		anoBanca = CalendarUtils.getAnoAtual();
		periodoBanca = this.getPeriodoAtual();
		this.titulo = "Participantes das Bancas";
		return forward("/stricto/relatorios/form_participantes_bancas.jsp");
	}
	
	/**
	 * Método para gerar o relatório relacionado as bancas.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/stricto/relatorio/form_participantes_bancas.jsp</li>
	 *   </ul>
	 *
	 * @throws DAOException
	 */
	@Override
	public String gerarRelatorio() throws DAOException {
		BancaPosDao dao = getDAO(BancaPosDao.class);

		Unidade programa;
		programa = unidade;
		
		if ( anoBanca <= 0 )
			addMensagem( MensagensArquitetura.VALOR_MAIOR_ZERO, "Ano");
		if ( periodoBanca <= 0 )
			addMensagem( MensagensArquitetura.VALOR_MAIOR_ZERO, "Período");
		if ( hasErrors() )
			return null;
		
		bancas = dao.findByPrograma( programa, anoBanca, periodoBanca );
		
		if ( bancas.isEmpty() ) {
			addMensagem( MensagensArquitetura.BUSCA_SEM_RESULTADOS );
			return null;
		}
		
		return forward("/stricto/relatorios/relatorio_participantes_banca.jsp");
	}

	public Map<String, List<BancaPos> > getBancas() {
		return bancas;
	}

	public void setBancas(Map<String, List<BancaPos> > bancas) {
		this.bancas = bancas;
	}

	public Integer getAnoBanca() {
		return anoBanca;
	}

	public void setAnoBanca(Integer anoBanca) {
		this.anoBanca = anoBanca;
	}

	public Integer getPeriodoBanca() {
		return periodoBanca;
	}

	public void setPeriodoBanca(Integer periodoBanca) {
		this.periodoBanca = periodoBanca;
	}

}
