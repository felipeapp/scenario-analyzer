/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 23/11/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio.inscricoes_atividades;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.InscricaoAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividade;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;
import br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante;
import br.ufrn.sigaa.extensao.dominio.StatusPagamentoInscricao;
import br.ufrn.sigaa.extensao.dominio.TipoParticipacaoAcaoExtensao;
import br.ufrn.sigaa.extensao.negocio.ProcessadorConfirmaPagamentoGRUAtividadesExtensaoManualmente;

/**
 *
 * <p>Processador que contém as regras para aprovar de participantes em atividades de extensão pelo coordenador. </p>
 *
 * <p>A principal regra é que ao ser aprovada deve-se obrigatoriamente criar um participante na atividade de extensão.
 *    Todas inscrição aprovada deve possuir um participante, senão é inconsistência do sistema.
 * </p>
 *
 * <p> <i> Caso seja aprovado o pagamento da taxa de inscrição é realizado automaticamente. </i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorAprovarParticipantesInscritosAtividade  extends AbstractProcessador{

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {

		validate(mov);
		
		MovimentoAprovarParticipantesInscritosAtividade movi = (MovimentoAprovarParticipantesInscritosAtividade) mov;
		
		List<InscricaoAtividadeParticipante> inscricoesSelecionadas = movi.getInscricoesSelecioandas();
		
		AtividadeExtensao atividadeSelecionada = movi.getAtividadeSelecionada();
		
		GenericDAO dao = null;
			
		try{
			dao = getGenericDAO(movi);
			
			List<Integer> idsInscricoesAprovadas = new ArrayList<Integer>();
			
			List<InscricaoAtividadeParticipante> inscricoesConfirmarPagamento = new ArrayList<InscricaoAtividadeParticipante>();
			
			for (InscricaoAtividadeParticipante inscricaoParticipante : inscricoesSelecionadas) {
				idsInscricoesAprovadas.add( inscricaoParticipante.getId() );
				
				// Se cobra taxa de matrícula, ao aprovar confirma automaticamente o pagamento
				if(inscricaoParticipante.getInscricaoAtividade().isCobrancaTaxaMatricula() 
						&& inscricaoParticipante.getStatusPagamento() == StatusPagamentoInscricao.EM_ABERTO ){
					
					inscricoesConfirmarPagamento.add( inscricaoParticipante );
				}
			}
			
			// cria o participante ativo para a inscrição aprovada //
			for (InscricaoAtividadeParticipante inscricao : inscricoesSelecionadas) {
				ParticipanteAcaoExtensao participante = new ParticipanteAcaoExtensao();
				participante.setInscricaoAtividadeParticipante(inscricao);
				participante.setCadastroParticipante(inscricao.getCadastroParticipante());
				participante.setAtividadeExtensao(atividadeSelecionada);
				participante.setTipoParticipacao(new TipoParticipacaoAcaoExtensao(TipoParticipacaoAcaoExtensao.PARTICIPANTE)); // por padrão na inscrição todo mundo é aluno
				participante.setAtivo(true);
				dao.create(participante);
				
				dao.update(" UPDATE extensao.inscricao_atividade_participante set id_participante_acao_extensao = ? " +
						" where id_inscricao_atividade_participante = ? ", participante.getId(), inscricao.getId());
			}
			
			// Aprova todas de uma vez
			dao.update(" UPDATE extensao.inscricao_atividade_participante set id_status_inscricao_participante = ? " +
					" where id_inscricao_atividade_participante in "
					+UFRNUtils.gerarStringIn(idsInscricoesAprovadas), StatusInscricaoParticipante.APROVADO);
			
			
			
			// Ao aprovar, confirma automaticamente o pagamento das que ainda não foram pagas.
			if(inscricoesConfirmarPagamento.size() > 0){
				
				ProcessadorConfirmaPagamentoGRUAtividadesExtensaoManualmente processador 
					= new ProcessadorConfirmaPagamentoGRUAtividadesExtensaoManualmente();
				
				MovimentoConfirmaPagamentoGRUAtividadesExtensaoManualmente movimento = new MovimentoConfirmaPagamentoGRUAtividadesExtensaoManualmente(inscricoesConfirmarPagamento);
				movimento.setUsuarioLogado(movi.getUsuarioLogado());
				movimento.setSistema(movi.getSistema());
				
				processador.execute(movimento);
			}
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return null;
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoAprovarParticipantesInscritosAtividade movi = (MovimentoAprovarParticipantesInscritosAtividade) mov;
		
		List<InscricaoAtividadeParticipante> inscricoesSelecioandas = movi.getInscricoesSelecioandas();
		
		AtividadeExtensao atividadeSelecionada = movi.getAtividadeSelecionada();
	
		InscricaoAtividadeExtensaoDao dao = null;
		
		try{
			dao = getDAO(InscricaoAtividadeExtensaoDao.class, movi);
		
			/* ************************************************************
			 * Recupera em uma só consulta as informações do períodos de inscrição
			 * que os participantes se inscriveram, para validar
			 * 
			 * ************************************************************/
			Set<Integer> idsPeriodosInscricoes = new HashSet<Integer>();		
		
			for (InscricaoAtividadeParticipante inscricaoParticipante : inscricoesSelecioandas) {
				idsPeriodosInscricoes.add(inscricaoParticipante.getInscricaoAtividade().getId());
			}
			
			List<InscricaoAtividade> periodosInscricoes = new ArrayList<InscricaoAtividade>();		
			
			periodosInscricoes.addAll( dao.findInformacoesPeriodosInscricaoParaValidacao(idsPeriodosInscricoes) );
			
			
			if(! atividadeSelecionada.isAtivo()){
				erros.addErro(" A atividade de extensão para a qual as inscrições foram realizadas não está mais ativa no sistema. ");
				checkValidation(erros);
			}
			
			// Para cadas inscrição do participante, verifica se ela pode ser aprovadas
			for (InscricaoAtividadeParticipante inscricaoAtividadeParticipante : inscricoesSelecioandas) {
				
				// Seta os dados necessários do período de inscrição para validar  //
				inscricaoAtividadeParticipante.setInscricaoAtividade( 
						periodosInscricoes.get( periodosInscricoes.indexOf( inscricaoAtividadeParticipante.getInscricaoAtividade() ) ) );
				
				if( inscricaoAtividadeParticipante.getInscricaoAtividade().getQuantidadeVagas() 
						- inscricaoAtividadeParticipante.getInscricaoAtividade().getQuantidadeInscritosAprovados() < 1 ){
					erros.addErro(" O período de inscrição "+inscricaoAtividadeParticipante.getInscricaoAtividade().getPeriodoInicioFormatado()
							+" a "+inscricaoAtividadeParticipante.getInscricaoAtividade().getPeriodoFimFormatado()
							+" para a atividade encontra-se com o número de vagas esgotado.");
				}
				
				if(inscricaoAtividadeParticipante.getStatusInscricao().getId() == StatusInscricaoParticipante.CANCELADO ){
					erros.addErro(" Não é possível aprovar a inscrição do participante: \""
							+inscricaoAtividadeParticipante.getCadastroParticipante().getNome()
							+"\", pois a inscrição dele já está cancelada por ele próprio.");
					
				}
				
				if(inscricaoAtividadeParticipante.getStatusInscricao().getId() == StatusInscricaoParticipante.APROVADO ){
					erros.addErro(" Não é possível aprovar a inscrição do participante: \""
							+inscricaoAtividadeParticipante.getCadastroParticipante().getNome()
							+"\", pois a inscrição dele já está aprovada.");
					
				}
				
				// para o coordenador não cometer o erro de aprovar inscrições não pagas,  nesse caso são necessários dois passos, 1º confirmar, 2º aprovar
				if(inscricaoAtividadeParticipante.getInscricaoAtividade().isCobrancaTaxaMatricula() &&
						( inscricaoAtividadeParticipante.getStatusPagamento() != StatusPagamentoInscricao.CONFIRMADO_AUTOMATICAMENTE
						&& inscricaoAtividadeParticipante.getStatusPagamento() != StatusPagamentoInscricao.CONFIRMADO_MANUALMENTE ) ){
					erros.addErro(" Não é possível aprovar a inscrição do participante: \""
							+inscricaoAtividadeParticipante.getCadastroParticipante().getNome()
							+"\", pois o pagamento não foi confirmado.");
					
				}
				
				
				if(inscricaoAtividadeParticipante.getInscricaoAtividade().isCobrancaTaxaMatricula() &&
						inscricaoAtividadeParticipante.getStatusPagamento() == StatusPagamentoInscricao.ESTORNADO ){
					erros.addErro(" Não é possível aprovar a inscrição do participante: \""
							+inscricaoAtividadeParticipante.getCadastroParticipante().getNome()
							+"\", pois o pagamento já foi estornado.");
					
				}
				
			}
		
		}finally{
			if(dao != null) dao.close();
		
			checkValidation(erros);
		}
		
		
	}

}
