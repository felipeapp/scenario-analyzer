/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '16/01/2007'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.jsf;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.prodocente.ProducaoDao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.relatorios.MesAno;
import br.ufrn.sigaa.prodocente.producao.jsf.AbstractControllerProdocente;
import br.ufrn.sigaa.prodocente.relatorios.dominio.RelatorioQuantitativoBuilder;

/**
 * MBean responsável por gerar relatório quantitativo de produção intelectual em
 * uma determinada unidade
 *
 * @author André
 *
 */
@Component("prodQuantitativo") @Scope("request")
public class RelatorioQuantitativoMBean extends AbstractControllerProdocente<Object> {

	private final String JSP_RESULTADO = "/prodocente/producao/relatorios/quantitativo/resultado.jsp";

	private Integer anoInicial, anoFinal, mesInicial, mesFinal;

	private Unidade unidade = new Unidade();

	private boolean detalharDocentes;

	private Map<String, Map<String, Map<MesAno, Long>>> resultadoDetalhado;

	private Map<String, Map<MesAno, Long>> resultado;

	private RelatorioQuantitativoBuilder relatorioBuilder = new RelatorioQuantitativoBuilder();
	
	public RelatorioQuantitativoMBean() {
		unidade = getUsuarioLogado().getVinculoAtivo().getUnidade();
	}
	
	public boolean isDetalharDocentes() {
		return detalharDocentes;
	}

	public void setDetalharDocentes(boolean detalharDocentes) {
		this.detalharDocentes = detalharDocentes;
	}

	public Integer getAnoFinal() {
		return anoFinal;
	}

	public void setAnoFinal(Integer anoFinal) {
		this.anoFinal = anoFinal;
	}

	public Integer getAnoInicial() {
		return anoInicial;
	}

	public void setAnoInicial(Integer anoInicial) {
		this.anoInicial = anoInicial;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	/**
	 * Responsável por gerar o Relatório quantitativo de produção intelectual em uma determinada unidade.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/quantitativo/form.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorio() throws DAOException {
		resultado = null;
		resultadoDetalhado = null;
		
		if (getAnoInicial() > getAnoFinal()) {
			addMensagemErro("Por favor, escolha um intervalo de ano base correto.");
			return null;
		}

		ProducaoDao dao = getDAO(ProducaoDao.class);
		if (unidade.getId() != -1) {
			unidade = dao.findByPrimaryKey(unidade.getId(), Unidade.class);
		} else {
			unidade.setNome(RepositorioDadosInstitucionais.getSiglaInstituicao());
		}
		
		// Construindo as datas inicial e final para passar como parâmetros pro DAO
		Calendar cal = Calendar.getInstance();		
		cal.set(Calendar.YEAR, getAnoInicial());
		cal.set(Calendar.MONTH, getMesInicial() -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		Date dataInicial = cal.getTime();
		
		cal.set(Calendar.YEAR, getAnoFinal());
		cal.set(Calendar.MONTH, getMesFinal()-1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date dataFinal = cal.getTime();		
		
		if (isDetalharDocentes()) {
			relatorioBuilder.builderTableDetalhado(dao.generateQuantitativosDocentes(dataInicial, dataFinal, getUnidade().getId()), dataInicial, dataFinal);
		} else {
			relatorioBuilder.builderTable(dao.generateQuantitativos(dataInicial, dataFinal, getUnidade().getId()), dataInicial, dataFinal);
		}

		if(relatorioBuilder.getRelatorio().isEmpty()){
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}

		return forward(JSP_RESULTADO);
	}
	
	public ArrayList<Integer> getFaixaAnos() {
		int tam = anoFinal - anoInicial + 1;
		ArrayList<Integer> faixa = new ArrayList<Integer>(tam);
		for (int i = 0; i < tam; i++) {
			faixa.add(anoFinal - i);
		}
		return faixa;
	}

	/**
	 * Retorna o cabeçalho do relatório.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/quantitativo/resultado.jsp</li> 
	 *	</ul>
	 * @return
	 */
	public String getCabecalho() {
		StringBuilder sb = new StringBuilder();
		Map<MesAno, Long> mapa = resultado.values().iterator().next();
		for(MesAno m: mapa.keySet()){
			sb.append("<td>"+m+"</td>\n");
		}
		return sb.toString();
	}	
	
	/**
	 * Retorna o cabeçalho detalhado do relatório.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/producao/relatorios/quantitativo/resultado.jsp</li> 
	 *	</ul>
	 * @return
	 */
	public String getCabecalhoDetalhado() {
		StringBuilder sb = new StringBuilder();
		Map<MesAno, Long> mapa = resultadoDetalhado.values().iterator().next().values().iterator().next();
		for(MesAno m: mapa.keySet()){
			sb.append("<td>"+m+"</td>\n");
		}
		return sb.toString();
	}		
	
	/**
	 * Redirenciona para a página do formulário.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *		<li>sigaa.war/portais/rh_plan/abas/producao_docente.jsp</li>
	 *		<li>sigaa.war/prodocente/abas/relatorios.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/menu/prodocente.jsp</li>
	 *	</ul>
	 * @return
	 */
	public String verFormulario() {
		anoFinal = CalendarUtils.getAnoAtual();
		anoInicial = anoFinal -1;
		return forward("/prodocente/producao/relatorios/quantitativo/form.jsp");
	}

	public Integer getMesInicial() {
		return mesInicial;
	}

	public void setMesInicial(Integer mesInicial) {
		this.mesInicial = mesInicial;
	}

	public Integer getMesFinal() {
		return mesFinal;
	}

	public void setMesFinal(Integer mesFinal) {
		this.mesFinal = mesFinal;
	}

	public Map<String, Map<String, Map<MesAno, Long>>> getResultadoDetalhado() {
		return resultadoDetalhado;
	}

	public void setResultadoDetalhado(
			Map<String, Map<String, Map<MesAno, Long>>> resultadoDetalhado) {
		this.resultadoDetalhado = resultadoDetalhado;
	}

	public Map<String, Map<MesAno, Long>> getResultado() {
		return resultado;
	}

	public void setResultado(Map<String, Map<MesAno, Long>> resultado) {
		this.resultado = resultado;
	}

	@Override
	public List<SelectItem> getAnos() throws DAOException {
		return super.getAnos();
	}
	
	@Override
	public List<SelectItem> getMeses() {
		return super.getMeses();
	}

	public RelatorioQuantitativoBuilder getRelatorioBuilder() {
		return relatorioBuilder;
	}

	public void setRelatorioBuilder(RelatorioQuantitativoBuilder relatorioBuilder) {
		this.relatorioBuilder = relatorioBuilder;
	}
	
}
