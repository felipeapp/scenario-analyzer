/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Classe com as valida��es do Edital de Pesquisa
 * 
 * @author Leonardo Campos
 */
public class EditalPesquisaValidator extends AbstractMovimentoAdapter{

	/**
	 * M�todo respons�vel pela valida��o do edital.
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
		
		// Validar descri��o do edital
		ValidatorUtil.validateRequired(edital.getDescricao(), "Descri��o", lista);

		// Validar per�odo de submiss�o
		ValidatorUtil.validateRequired(edital.getInicioSubmissao(), "In�cio do per�odo de submiss�es", lista);
		ValidatorUtil.validateRequired(edital.getFimSubmissao(), "Fim do per�odo de submiss�es", lista);

		ValidatorUtil.validaOrdemTemporalDatas(edital.getInicioSubmissao(), edital.getFimSubmissao(), false, "Per�odo Submiss�o", lista);
		
		//Validar per�odo execu��o
		ValidatorUtil.validateRequired(edital.getInicioExecucaoProjetos(), "In�cio do per�odo de Execu��o", lista);
		ValidatorUtil.validateRequired(edital.getFimExecucaoProjetos(), "Fim do per�odo de Execu��o", lista);
		
		ValidatorUtil.validaOrdemTemporalDatas(edital.getInicioExecucaoProjetos(), edital.getFimExecucaoProjetos(), false, "Per�odo Execu��o", lista);
		
		if ( edital.getFimSubmissao() != null && edital.getInicioExecucaoProjetos() != null ) {
			if ( edital.getFimSubmissao().getTime() > edital.getInicioExecucaoProjetos().getTime() ) {
				lista.addErro("O inicio do per�odo de Execu��o deve ser posterior ao per�odo de Submiss�o.");
			}
		}
		
		if(edital.isDistribuicaoCotas())
			ValidatorUtil.validaDoublePositivo(edital.getFppiMinimo(), "FPPI M�nimo", lista);
		
		if( edital.getInicioSubmissao() != null && edital.getFimSubmissao() != null){
			if( edital.getInicioSubmissao().compareTo( edital.getFimSubmissao() ) >= 0  ){
				lista.addErro("A data de in�cio do per�odo de submiss�o deve ser menor do que a data de fim.");
			}
		}
		ValidatorUtil.validateRequiredId(edital.getTitulacaoMinimaCotas(), "Titula��o m�nima para a solicita��o de cotas", lista);
		ValidatorUtil.validateRequiredId(edital.getCota().getId(), "Cota", lista);
		ValidatorUtil.validateRequiredId(edital.getCategoria().getId(), "Categoria", lista);
		
		if (edital.isDistribuicaoCotas() && (edital.getCotas() == null || edital.getCotas().isEmpty()))
			lista.addErro("Por favor, adicione pelo menos um cota.");
																	
		if ( edital.getValorMinimoIndiceChecagem() != null && !( edital.getValorMinimoIndiceChecagem() > 0 && edital.getValorMinimoIndiceChecagem() < 100000 ) ) {
			lista.addErro("Validar �ndice m�nimo: O valor deve ser maior do que 0 e menor do que 100000.");
		}
		
		if ( !edital.getCodigo().equals("") ) {
			boolean hasCodigoEdital = dao.hasCodigo(edital.getCodigo());
		
			if (hasCodigoEdital) {
				lista.addErro("C�digo j� cadastrado para outro projeto de pesquisa.");
			}
		}
		
	}
	
	/**
	 * M�todo respons�vel pela valida��o do arquivo do edital.
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
	 * M�todo respons�vel pela valida��o e adi��o de uma cota.
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
		
		ValidatorUtil.validaDoublePositivo(edital.getFppiMinimo(), "FPPI M�nimo", lista);
		
		for(Cotas cota : edital.getCotas()){
			if(cota.getTipoBolsa().getId() == tipoBolsa.getId()){
				lista.addErro("Este tipo de bolsa j� foi adicionado ao Edital. Por favor, selecione outro tipo.");
				break;
			}
		}
	}
}
