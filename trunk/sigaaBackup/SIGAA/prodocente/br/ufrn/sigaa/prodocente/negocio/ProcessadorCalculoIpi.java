/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '25/04/2007'
 *
 */
package br.ufrn.sigaa.prodocente.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.integracao.interfaces.FormacaoAcademicaRemoteService;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.pesquisa.negocio.CalculosPesquisa;
import br.ufrn.sigaa.prodocente.relatorios.dominio.EmissaoRelatorio;
import br.ufrn.sigaa.prodocente.relatorios.dominio.EmissaoRelatorioItem;
import br.ufrn.sigaa.prodocente.relatorios.dominio.GrupoItem;
import br.ufrn.sigaa.prodocente.relatorios.dominio.GrupoRelatorioProdutividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.RelatorioProdutividade;

/**
 * Classe utilizada para o calculo do IPI do docente
 *
 * @author Eriquim
 *
 */
public class ProcessadorCalculoIpi extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		GenericDAO dao =  getDAO(mov);

		MovimentoCadastro classificacaoMovimento = (MovimentoCadastro) mov;

		EmissaoRelatorio emissaoRelatorio = (EmissaoRelatorio) classificacaoMovimento.getObjMovimentado();

		FormacaoAcademicaRemoteService serviceFormacao = null;
		serviceFormacao = getBean("formacaoAcademicaInvoker", classificacaoMovimento);
		
		//calcularIpiDocente(emissaoRelatorio, serviceFormacao);
		
		String implementacao = ParametroHelper.getInstance().getParametro(ParametrosPesquisa.IMPLEMENTACAO_COMPORTAMENTOS_PESQUISA);
        CalculosPesquisa calculos = ReflectionUtils.newInstance( implementacao );
        calculos.calcularIpiDocente(emissaoRelatorio, serviceFormacao);

		dao.create(emissaoRelatorio);
		dao.close();

		return emissaoRelatorio;
	}

	public void calcularIpiDocente(EmissaoRelatorio emissaoRelatorio, FormacaoAcademicaRemoteService serviceFormacao) throws ArqException{

		
		
		//monta o relatório do docente
		RelatorioProdutividade relatorioProdutividade = RelatorioHelper.montarRelatorioProdutividade(
					emissaoRelatorio.getClassificacaoRelatorio().getRelatorioProdutividade() ,
					emissaoRelatorio.getServidor(),
					emissaoRelatorio.getClassificacaoRelatorio().getAnoVigencia(), serviceFormacao);

		Collection<EmissaoRelatorioItem> itensEmissaoRelatorio = new ArrayList<EmissaoRelatorioItem>();

		Double ipi = 0.0;

		//Iteração para associarmos os grupos itens do relatório com os itens da emissão do relatório e
		//somamos os pontos obtidos em cada item do relatório respeitando o limite do valor
		//máximo do grupo a qual o item pertence
		for (GrupoRelatorioProdutividade grupoRelatorioProdutividade : relatorioProdutividade.getGrupoRelatorioProdutividadeCollection()) {
			Double pontuacaoGrupo = 0.0;
			for (GrupoItem grupoItem : grupoRelatorioProdutividade.getGrupoItemCollection()) {

				EmissaoRelatorioItem emissaoRelatorioItem = new EmissaoRelatorioItem();
				emissaoRelatorioItem.setGrupoItem(grupoItem);
				emissaoRelatorioItem.setPontos( (double) grupoItem.getTotalPontos());
				emissaoRelatorioItem.setEmissaoRelatorio(emissaoRelatorio);

				itensEmissaoRelatorio.add(emissaoRelatorioItem);

				if(grupoRelatorioProdutividade.getPontuacaoMaxima() == 0 ||
						(pontuacaoGrupo + grupoItem.getTotalPontos()) <= grupoRelatorioProdutividade.getPontuacaoMaxima()){
					pontuacaoGrupo += grupoItem.getTotalPontos();
				} else {
					pontuacaoGrupo = (double) grupoRelatorioProdutividade.getPontuacaoMaxima();
				}
			}
			ipi+=pontuacaoGrupo;
		}

		emissaoRelatorio.setEmissaoRelatorioItemCollection(itensEmissaoRelatorio);
		emissaoRelatorio.setIpi(ipi);

	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
