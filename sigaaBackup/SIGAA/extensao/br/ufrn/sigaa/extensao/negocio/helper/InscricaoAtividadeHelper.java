/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 03/05/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio.helper;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Date;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.comum.gru.dominio.ConfiguracaoGRU;
import br.ufrn.comum.gru.dominio.GuiaRecolhimentoUniao;
import br.ufrn.comum.gru.dominio.ParametrosGRU;
import br.ufrn.comum.gru.dominio.TipoArrecadacao;
import br.ufrn.comum.gru.negocio.GuiaRecolhimentoUniaoHelper;
import br.ufrn.sigaa.arq.dao.extensao.InscricaoAtividadeParticipanteDao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;

/**
 * <p> Classe utilitária para inscrições em atividades de extensão </p>
 *
 * @author jadson
 *
 */
public class InscricaoAtividadeHelper {

	
	/**
	 * <p> Método centralizado para gerar as GRUs para as inscrições em cursos e eventos do módulo de extensão. </p>
	 * <br/>
	 * <p> Gera um GRU simples </p> 
	 *  
	 * <p> Para ser gerada a GRU precisa de uma "Configuração GRU" para a unidade de recebimento.</p>
	 *  
	 * <p>  Por padrão a unidade de recebimento vai ser a universidade.</p>
	 *  
	 * @param multa
	 * @param usuarioBiblioteca
	 * @return
	 * @throws NegocioException 
	 * @throws DAOException 
	 */
	public static GuiaRecolhimentoUniao criaGRUPagamentoIncricaoCursosEvento(InscricaoAtividadeParticipante inscricao) throws NegocioException, DAOException{
		
		InscricaoAtividadeParticipanteDao dao = null;
		
		try{
		
			dao = DAOFactory.getInstance().getDAO(InscricaoAtividadeParticipanteDao.class);		
			if(inscricao.getCadastroParticipante().getCpf() == null){
				throw new NegocioException("A emissão da GRU só pode ser realizada para participantes que possuem CPF. " +
			               "Entre em contato com o coordenador da ação para efetuar o pagamento, não há como realizar o pagamento pelo sistema. ");
			}
			
			if(! inscricao.getInscricaoAtividade().isCobrancaTaxaMatricula() ){
				throw new NegocioException("Não é preciso emitir a GRU, pois o período de inscrição cuja inscrição foi efetuada não exigia cobrança de taxa.");
			}	
			
			AtividadeExtensao atv = null;
			if( inscricao.getInscricaoAtividade().isInscricaoAtividade() ) {	
				atv = inscricao.getInscricaoAtividade().getAtividade();
			} else {
				atv = inscricao.getInscricaoAtividade().getSubAtividade().getAtividade();
			}		
			
			
			Unidade unidadeOrcamentaria  = atv.getProjeto().getUnidadeOrcamentaria();
			if(unidadeOrcamentaria == null || unidadeOrcamentaria.getId() <= 0 )
				throw new NegocioException("Não é possível emitir a GRU, pois o projeto ainda não possui unidade orçamentária.");
			
			
			/** Só vai existir 1 configuração de GRU para Cursos e Eventos de extensão que será a UFRN.
			 * Ai na GRU é impresso a unidade orçamentária do projeto */
			Integer idUnidadeGRU = ParametroHelper.getInstance().getParametroInt(ParametrosGRU.ID_UNIDADE_FAVORECIDA_PADRAO);
			
			ConfiguracaoGRU configGRU = GuiaRecolhimentoUniaoHelper
				.getConfiguracaoGRUByTipoArrecadacao(TipoArrecadacao.CURSO_E_EVENTOS_EXTENSAO, idUnidadeGRU);
			
			if (isEmpty(configGRU)){
				throw new NegocioException("Não é possível emitir a GRU pois não foi encontrada nenhuma configuração de GRU para as atividades de extensão no sistema.");
			}
			
			// Caso exista, será a data informada, senão será por padrão a data de início do evento //
			Date dataVencimentoGRU = new Date();
			
			if(inscricao.getInscricaoAtividade().getDataVencimentoGRU() != null)
				dataVencimentoGRU = inscricao.getInscricaoAtividade().getDataVencimentoGRU();
			else {
				dataVencimentoGRU = atv.getProjeto().getDataInicio();
			}
			
			// Cria uma GRU cobrança para ser paga até a data de vencimento //
			
			GuiaRecolhimentoUniao gru = GuiaRecolhimentoUniaoHelper.createGRUCobranca(
					configGRU.getId(),
					inscricao.getCadastroParticipante().getCpf(),
					inscricao.getCadastroParticipante().getNome(),
					
					inscricao.getCadastroParticipante().getLogradouro()+" "+inscricao.getCadastroParticipante().getNumero()
					+" "+inscricao.getCadastroParticipante().getBairro()
					+", "+inscricao.getCadastroParticipante().getMunicipio().getNome()+"/"+inscricao.getCadastroParticipante().getUnidadeFederativa().getSigla()
					+" - "+inscricao.getCadastroParticipante().getCep(), // endereço sacado
					
					" ", // instruções, nãsão utilizadas na gru cobrança.
					
					new TipoArrecadacao(TipoArrecadacao.CURSO_E_EVENTOS_EXTENSAO), 
					unidadeOrcamentaria.getId(),                                   // Aqui vai a unidade orçarmentária do curso e vento.
					configGRU.getGrupoEmissaoGRU().getAgencia(),
					configGRU.getTipoArrecadacao().getCodigoRecolhimento().getCodigo(),
					configGRU.getGrupoEmissaoGRU().getConvenio(),
					inscricao.getValorTaxaMatricula().doubleValue(), // valor
					dataVencimentoGRU);
			return gru;
			
		}finally{
			if(dao != null) dao.close();
			
		}
	}
	
}
