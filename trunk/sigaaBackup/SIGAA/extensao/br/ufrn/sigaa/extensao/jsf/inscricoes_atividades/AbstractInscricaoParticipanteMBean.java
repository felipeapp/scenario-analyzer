/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 14/11/2012
 * 
 */
package br.ufrn.sigaa.extensao.jsf.inscricoes_atividades;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividade;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.ModalidadeParticipantePeriodoInscricaoAtividade;

/**
 *
 * <p>MBean abstracto com as informações comuns entre inscrição em atividades e mini atividades de extensão </p>
 *
 * <p> <i> Muita coisa é igual, então foi criado essse calsse.</i> </p>
 * 
 * @author jadson
 *
 */
public class AbstractInscricaoParticipanteMBean extends SigaaAbstractController<InscricaoAtividadeParticipante>{
	
	
	/** Guarda os períodos de inscrições de atividades de extensão que estejam abertos  */
	protected Collection<InscricaoAtividade> periodosInscricoesAbertos = new ArrayList<InscricaoAtividade>();
	
	/** O valor da modalidade escolhida pelo participante no momento da inscrição. */
	protected int idMolidadeParticipanteSelecionada = -1;
	
	/** Guarda as inscrições anteriores do usuário, caso ele tenha. */
	protected List<InscricaoAtividadeParticipante> inscricoesAnteriores;
	
	
	/** Guarda as inscrições anteriores do usuário, caso ele tenha nas sub atividades. */
	protected List<InscricaoAtividadeParticipante> inscricoesAnterioresSubAtividades;
	
	
	/** Valida se o usuário concordou com os termos do pagamento */
	protected boolean usuarioConcordaCondicoesPagamento = false;
	
	/**
	 * Retorna  a quantidade de períodos de inscrição abertos.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/extensao/paginaListaPeriodosInscricoesAtividadesAbertos.jsp/</li>
	 *   </ul>
	 *
	 * @return
	 */
	public int getQtdPeriodosInscricaoAbertos(){
		if(periodosInscricoesAbertos == null)
			return 0;
		else
			return periodosInscricoesAbertos.size();
	}
	
	
	
	/**
	 *    <p>Recupera para o combo box as modalidade de participantes que estão associados 
	 *      com o período de inscrição aberto.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/formPeriodoInscricaoAtividade.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getModalidadeParticipantesDoPeriodoInscricao() throws DAOException{
		
		Collection <SelectItem> combo = new ArrayList<SelectItem>();

		
		if(obj.getInscricaoAtividade() != null && obj.getInscricaoAtividade().getModalidadesParticipantes() != null){
			for (ModalidadeParticipantePeriodoInscricaoAtividade modalidadeParticipante 
									: obj.getInscricaoAtividade().getModalidadesParticipantes()) {
				
				combo.add(
						new SelectItem(modalidadeParticipante.getModalidadeParticipante().getId()
								, modalidadeParticipante.getModalidadeParticipante().getNome())
				);
				
				
			}
		}
		
		return combo;
	}
	
	
	/**
	 * <p>Método chamado quando o usuário seleciona algum das modalidade de participantes associados com a inscrição.</p>
	 *  
	 *  <p>Neste caso, seta a modalidade e o valor da inscrição o valor escolhido pelo usuário.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/extensao/paginaListaPeriodosInscricoesAtividadesAbertos.jsp</li>
	 *   </ul>
	 *
	 * @param event
	 */
	public void atualizarModalidadeEscolhida(ActionEvent event){
		
		boolean encontrouValor = false; 
		
		if( obj.getInscricaoAtividade().getModalidadesParticipantes() != null){
			for (ModalidadeParticipantePeriodoInscricaoAtividade modalidade : obj.getInscricaoAtividade().getModalidadesParticipantes()) {
				
				if(modalidade.getModalidadeParticipante().getId() == idMolidadeParticipanteSelecionada){
					obj.setValorTaxaMatricula( modalidade.getTaxaMatricula());
					obj.setMolidadeParticipante(modalidade);
					encontrouValor = true;
					break;
				}
				
			}
		}
		
		if(!encontrouValor){
			obj.setValorTaxaMatricula( new BigDecimal(0) );
			obj.setMolidadeParticipante(null);
		}
	}
	
	
	
	public Collection<InscricaoAtividade> getPeriodosInscricoesAbertos() {
		return periodosInscricoesAbertos;
	}

	public void setPeriodosInscricoesAtividadesAbertos(Collection<InscricaoAtividade> periodosInscricoesAbertos) {
		this.periodosInscricoesAbertos = periodosInscricoesAbertos;
	}
	
	public int getIdMolidadeParticipanteSelecionada() {
		return idMolidadeParticipanteSelecionada;
	}
	public void setIdMolidadeParticipanteSelecionada(int idMolidadeParticipanteSelecionada) {
		this.idMolidadeParticipanteSelecionada = idMolidadeParticipanteSelecionada;
	}

	public boolean isUsuarioConcordaCondicoesPagamento() {
		return usuarioConcordaCondicoesPagamento;
	}

	public void setUsuarioConcordaCondicoesPagamento(boolean usuarioConcordaCondicoesPagamento) {
		this.usuarioConcordaCondicoesPagamento = usuarioConcordaCondicoesPagamento;
	}
	
}
