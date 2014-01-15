package br.ufrn.sigaa.pesquisa.negocio;

import java.util.Collection;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.pesquisa.dominio.StatusCotaPlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoStatusPlanoTrabalho;

/**
 * Classe auxiliar que realiza alguma operações tais como: 
 *  montar um OutGroup para a sua utilização no Struts.   
 * 
 * @author Jean Guerethes
 */
public class PlanoTrabalhoHelper {

	/** Serve para montar o OutGroup para os caso de plano de trabalho de pesquisa */
	public static String getOutGroupStrutsTipoBolsa( boolean gerarSelect, Collection<TipoBolsaPesquisa> bolsas, int tipoBolsa ) throws DAOException{
		String result = "";
		if (gerarSelect) 
			result += "<select id='idTipoBolsa' name='idTipoBolsa'>";

		try {
			int idBolsaAtual = 0;
			for (TipoBolsaPesquisa tipoBolsaPesquisa : bolsas) {
				if ( idBolsaAtual != tipoBolsaPesquisa.getEntidadeFinanciadora().getId() ) {
					if (idBolsaAtual != 0)
						result += "</optgroup>";

					result += "<optgroup label='" + tipoBolsaPesquisa.getEntidadeFinanciadora().getNome() + "'>";
				}
			
				if (tipoBolsa == tipoBolsaPesquisa.getId() )
					result += "<option value='" + tipoBolsaPesquisa.getId() + "' selected='selected'>" + tipoBolsaPesquisa.getDescricaoResumida() + "</option>"; 
				else	
					result += "<option value='" + tipoBolsaPesquisa.getId() + "'>" + tipoBolsaPesquisa.getDescricaoResumida() + "</option>";
				
				idBolsaAtual = tipoBolsaPesquisa.getEntidadeFinanciadora().getId();
			}
		} finally {
			result += "</optgroup>";
			if (gerarSelect)
				result += "</select>";
		}
		return result;
	}

	/** Serve para montar o OutGroup para os status do plano de trabalho */
	public static String getOutGroupStrutsStatus( Collection<StatusCotaPlanoTrabalho> status, int statusPlano ) throws DAOException{
		String result = "<select id='idStatusBolsa' name='idStatusBolsa'>";
		try {
			int ordemAtual = 0;
			for (StatusCotaPlanoTrabalho statusCPT : status) {
				if ( ordemAtual != statusCPT.getOrdemStatus() ) {
					if (ordemAtual != 0)
						result += "</optgroup>";

					result += "<optgroup label='" + statusCPT.getDescricaoOrdemStatusCota() + "'>";
				}
			
				if ( statusPlano == statusCPT.getStatusPlanoTrabalho() )
					result += "<option value='" + statusCPT.getStatusPlanoTrabalho() + "' selected='selected'>" + TipoStatusPlanoTrabalho.getDescricao( statusCPT.getStatusPlanoTrabalho() )  + "</option>"; 
				else	
					result += "<option value='" + statusCPT.getStatusPlanoTrabalho() + "'>" + TipoStatusPlanoTrabalho.getDescricao( statusCPT.getStatusPlanoTrabalho() )  + "</option>";
				
				ordemAtual = statusCPT.getOrdemStatus();
			}
		} finally {
			result += "</optgroup>";
			result += "</select>";
		}
		return result;
	}

}