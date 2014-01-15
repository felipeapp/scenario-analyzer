/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p> Classe utilit�ria para inscri��es em atividades de extens�o </p>
 *
 * @author jadson
 *
 */
public class InscricaoAtividadeHelper {

	
	/**
	 * <p> M�todo centralizado para gerar as GRUs para as inscri��es em cursos e eventos do m�dulo de extens�o. </p>
	 * <br/>
	 * <p> Gera um GRU simples </p> 
	 *  
	 * <p> Para ser gerada a GRU precisa de uma "Configura��o GRU" para a unidade de recebimento.</p>
	 *  
	 * <p>  Por padr�o a unidade de recebimento vai ser a universidade.</p>
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
				throw new NegocioException("A emiss�o da GRU s� pode ser realizada para participantes que possuem CPF. " +
			               "Entre em contato com o coordenador da a��o para efetuar o pagamento, n�o h� como realizar o pagamento pelo sistema. ");
			}
			
			if(! inscricao.getInscricaoAtividade().isCobrancaTaxaMatricula() ){
				throw new NegocioException("N�o � preciso emitir a GRU, pois o per�odo de inscri��o cuja inscri��o foi efetuada n�o exigia cobran�a de taxa.");
			}	
			
			AtividadeExtensao atv = null;
			if( inscricao.getInscricaoAtividade().isInscricaoAtividade() ) {	
				atv = inscricao.getInscricaoAtividade().getAtividade();
			} else {
				atv = inscricao.getInscricaoAtividade().getSubAtividade().getAtividade();
			}		
			
			
			Unidade unidadeOrcamentaria  = atv.getProjeto().getUnidadeOrcamentaria();
			if(unidadeOrcamentaria == null || unidadeOrcamentaria.getId() <= 0 )
				throw new NegocioException("N�o � poss�vel emitir a GRU, pois o projeto ainda n�o possui unidade or�ament�ria.");
			
			
			/** S� vai existir 1 configura��o de GRU para Cursos e Eventos de extens�o que ser� a UFRN.
			 * Ai na GRU � impresso a unidade or�ament�ria do projeto */
			Integer idUnidadeGRU = ParametroHelper.getInstance().getParametroInt(ParametrosGRU.ID_UNIDADE_FAVORECIDA_PADRAO);
			
			ConfiguracaoGRU configGRU = GuiaRecolhimentoUniaoHelper
				.getConfiguracaoGRUByTipoArrecadacao(TipoArrecadacao.CURSO_E_EVENTOS_EXTENSAO, idUnidadeGRU);
			
			if (isEmpty(configGRU)){
				throw new NegocioException("N�o � poss�vel emitir a GRU pois n�o foi encontrada nenhuma configura��o de GRU para as atividades de extens�o no sistema.");
			}
			
			// Caso exista, ser� a data informada, sen�o ser� por padr�o a data de in�cio do evento //
			Date dataVencimentoGRU = new Date();
			
			if(inscricao.getInscricaoAtividade().getDataVencimentoGRU() != null)
				dataVencimentoGRU = inscricao.getInscricaoAtividade().getDataVencimentoGRU();
			else {
				dataVencimentoGRU = atv.getProjeto().getDataInicio();
			}
			
			// Cria uma GRU cobran�a para ser paga at� a data de vencimento //
			
			GuiaRecolhimentoUniao gru = GuiaRecolhimentoUniaoHelper.createGRUCobranca(
					configGRU.getId(),
					inscricao.getCadastroParticipante().getCpf(),
					inscricao.getCadastroParticipante().getNome(),
					
					inscricao.getCadastroParticipante().getLogradouro()+" "+inscricao.getCadastroParticipante().getNumero()
					+" "+inscricao.getCadastroParticipante().getBairro()
					+", "+inscricao.getCadastroParticipante().getMunicipio().getNome()+"/"+inscricao.getCadastroParticipante().getUnidadeFederativa().getSigla()
					+" - "+inscricao.getCadastroParticipante().getCep(), // endere�o sacado
					
					" ", // instru��es, n�s�o utilizadas na gru cobran�a.
					
					new TipoArrecadacao(TipoArrecadacao.CURSO_E_EVENTOS_EXTENSAO), 
					unidadeOrcamentaria.getId(),                                   // Aqui vai a unidade or�arment�ria do curso e vento.
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
