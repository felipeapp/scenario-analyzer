/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/03/2007
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.arq.web.tags.SelectAnoTag;
import br.ufrn.sigaa.arq.dao.pesquisa.EditalPesquisaDao;
import br.ufrn.sigaa.pesquisa.dominio.Cotas;	
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;

/**
 * Classe com as validações do Edital de Pesquisa
 * 
 * @author Leonardo Campos
 */
public class EditalPesquisaValidator extends AbstractMovimentoAdapter{

	/**
	 * Método responsável pela validação do edital.
	 * 
	 * Invocado pelas classes: ProcessadorEditalPesquisa.java 
	 *  					   EditalPesquisaMBean.java
	 * @param edital
	 * @param arquivoEdital
	 * @param lista
	 * @throws DAOException 
	 */
	public static void validaEdital(EditalPesquisa edital, ListaMensagens lista, EditalPesquisaDao dao) throws DAOException{
		
		if(edital.getAno() != null){
			Collection<Integer> anos = new ArrayList<Integer>();
			for (int i = SelectAnoTag.ANO_INCIO_PADRAO; i <= CalendarUtils.getAnoAtual() + 4; i++) {
				anos.add(i);
			}
			
			if (edital.getAno() != null && !anos.contains(edital.getAno()))
				lista.addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Ano do Edital");
			
		}else
			ValidatorUtil.validateRequired(edital.getAno(), "Ano do Edital", lista);
		
		// Validar descrição do edital
		ValidatorUtil.validateRequired(edital.getDescricao(), "Descrição", lista);

		// Validar período de submissão
		ValidatorUtil.validateRequired(edital.getInicioSubmissao(), "Início do período de submissões", lista);
		ValidatorUtil.validateRequired(edital.getFimSubmissao(), "Fim do período de submissões", lista);

		ValidatorUtil.validaOrdemTemporalDatas(edital.getInicioSubmissao(), edital.getFimSubmissao(), false, "Período Submissão", lista);
		
		//Validar período execução
		ValidatorUtil.validateRequired(edital.getInicioExecucaoProjetos(), "Início do período de Execução", lista);
		ValidatorUtil.validateRequired(edital.getFimExecucaoProjetos(), "Fim do período de Execução", lista);
		
		ValidatorUtil.validaOrdemTemporalDatas(edital.getInicioExecucaoProjetos(), edital.getFimExecucaoProjetos(), false, "Período Execução", lista);
		
		if ( edital.getFimSubmissao() != null && edital.getInicioExecucaoProjetos() != null ) {
			if ( edital.getFimSubmissao().getTime() > edital.getInicioExecucaoProjetos().getTime() ) {
				lista.addErro("O inicio do período de Execução deve ser posterior ao período de Submissão.");
			}
		}
		
		if(edital.isDistribuicaoCotas())
			ValidatorUtil.validaDoublePositivo(edital.getFppiMinimo(), "FPPI Mínimo", lista);
		
		if( edital.getInicioSubmissao() != null && edital.getFimSubmissao() != null){
			if( edital.getInicioSubmissao().compareTo( edital.getFimSubmissao() ) >= 0  ){
				lista.addErro("A data de início do período de submissão deve ser menor do que a data de fim.");
			}
		}
		ValidatorUtil.validateRequiredId(edital.getTitulacaoMinimaCotas(), "Titulação mínima para a solicitação de cotas", lista);
		ValidatorUtil.validateRequiredId(edital.getCota().getId(), "Cota", lista);
		ValidatorUtil.validateRequiredId(edital.getCategoria().getId(), "Categoria", lista);
		
		if (edital.isDistribuicaoCotas() && (edital.getCotas() == null || edital.getCotas().isEmpty()))
			lista.addErro("Por favor, adicione pelo menos um cota.");
																	
		if ( edital.getValorMinimoIndiceChecagem() != null && !( edital.getValorMinimoIndiceChecagem() > 0 && edital.getValorMinimoIndiceChecagem() < 100000 ) ) {
			lista.addErro("Validar índice mínimo: O valor deve ser maior do que 0 e menor do que 100000.");
		}
		
		if ( !edital.getCodigo().equals("") ) {
			boolean hasCodigoEdital = dao.hasCodigo(edital.getCodigo());
		
			if (hasCodigoEdital) {
				lista.addErro("Código já cadastrado para outro projeto de pesquisa.");
			}
		}
		
	}
	
	/**
	 * Método responsável pela validação do arquivo do edital.
	 * 
	 * @param edital
	 * @param arquivoEdital
	 * @param lista
	 */
	public static void validaArquivoEdital(EditalPesquisa edital, UploadedFile arquivoEdital ,ListaMensagens lista){
		// Validar arquivo do edital
		if(arquivoEdital == null)
			lista.addErro("Por favor, selecione um Arquivo para o envio.");
	}
	
	/**
	 * Método responsável pela validação e adição de uma cota.
	 * 
	 * Invocado pela classe: PublicarEditalPesquisaAction.java
	 * 
	 * @param editalForm
	 * @param lista
	 */
	public static void validaAdicionaCota(EditalPesquisa edital, TipoBolsaPesquisa tipoBolsa, Integer quantidade, ListaMensagens lista){
		ValidatorUtil.validateRequiredId(tipoBolsa != null ? tipoBolsa.getId() : 0, "Tipo da bolsa", lista);
		if(quantidade != null)
			ValidatorUtil.validaInt(quantidade, "Quantidade", lista);
		else
			ValidatorUtil.validateRequired(quantidade, "Quantidade", lista);
		
		ValidatorUtil.validaDoublePositivo(edital.getFppiMinimo(), "FPPI Mínimo", lista);
		
		for(Cotas cota : edital.getCotas()){
			if(cota.getTipoBolsa().getId() == tipoBolsa.getId()){
				lista.addErro("Este tipo de bolsa já foi adicionado ao Edital. Por favor, selecione outro tipo.");
				break;
			}
		}
	}
}
