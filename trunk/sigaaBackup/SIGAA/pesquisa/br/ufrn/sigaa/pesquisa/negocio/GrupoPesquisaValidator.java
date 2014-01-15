package br.ufrn.sigaa.pesquisa.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.MembroGrupoPesquisa;

/**
 * Classe com as validações do Grupo de Pesquisa
 * @author Jean Guerethes
 */
public class GrupoPesquisaValidator extends AbstractMovimentoAdapter{

	/**
	 * Realiza a validação dos dados gerais do grupo de pesquisa.
	 * @param grupoPesquisa
	 * @param lista
	 * @param portalDocente
	 * @param validaLinha
	 * @throws DAOException
	 */
	public static void validaDadosGerais(GrupoPesquisa grupoPesquisa, ListaMensagens lista, boolean portalDocente, boolean validaLinha) throws DAOException{
	
		ValidatorUtil.validateRequired(grupoPesquisa.getNome(), "Título", lista);
		ValidatorUtil.validateRequiredId(grupoPesquisa.getCoordenador().getId(), "Lider", lista);
		ValidatorUtil.validateRequiredId(grupoPesquisa.getAreaConhecimentoCnpq().getId(), "Sub-área de Conhecimento", lista);
		
		if (!portalDocente)
			ValidatorUtil.validateRequiredId(grupoPesquisa.getStatus(), "Status", lista);

		int minimoLinhas = ParametroHelper.getInstance().getParametroInt(ParametrosPesquisa.MINIMO_LINHAS_NOVO_GRUPO_PESQUISA);
		if ( (grupoPesquisa.getLinhasPesquisa().isEmpty()
				|| grupoPesquisa.getLinhasPesquisa().size() < minimoLinhas) && validaLinha )
			lista.addErro("É necessário informar pelo menos " + minimoLinhas + " Linha(s) de Pesquisa.");
	}
	
	/**
	 * Realiza a validação dos dados dos membros do grupo de pesquisa. 
	 * @param grupoPesquisa
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaMembros(GrupoPesquisa grupoPesquisa, ListaMensagens lista) {
		boolean faltaCurriculo = false;
		for(MembroGrupoPesquisa m: grupoPesquisa.getMembrosPermanentes()) {
			if( ValidatorUtil.isEmpty( m.getEnderecoLattes() ) )
				faltaCurriculo = true;
		}
		if(faltaCurriculo)
			lista.addErro("É necessário informar o Currículo Lattes de todos os membros permanentes.");
	}
	
	/**
	 * Realiza a validação dos dados da descrição detalhada do grupo de pesquisa. 
	 * @param grupoPesquisa
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaDescricaoDetalhada(GrupoPesquisa grupoPesquisa, ListaMensagens lista) throws DAOException{

		ValidatorUtil.validateRequired(grupoPesquisa.getJustificativa(), "Justificativa", lista);
		ValidatorUtil.validateRequired(grupoPesquisa.getInstituicoesIntercambio(), "Instituições de Ensino", lista);
		ValidatorUtil.validateRequired(grupoPesquisa.getInfraestrutura(), "InfraEstrutura", lista);
		ValidatorUtil.validateRequired(grupoPesquisa.getLaboratorios(), "Laboratórios", lista);
	}

}