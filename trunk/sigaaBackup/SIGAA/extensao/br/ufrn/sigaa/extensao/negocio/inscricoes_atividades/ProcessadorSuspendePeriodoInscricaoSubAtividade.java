/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 10/12/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio.inscricoes_atividades;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividade;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante;

/**
 *
 * <p>Na suspens�o de um per�odo de inscri��o para uma sub atividade, o per�odo de inscri��o � desativado 
 *    e todos os particpantes n�o aprovados ser�o recusados. Um email de aviso deve ser enviado. </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorSuspendePeriodoInscricaoSubAtividade extends AbstractProcessador{

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		validate( mov);
		
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		InscricaoAtividade inscricao = (InscricaoAtividade) movimento.getObjMovimentado();
		
		
		@SuppressWarnings("unchecked")
		List<InscricaoAtividadeParticipante> inscricoesParticipante = (List<InscricaoAtividadeParticipante>) movimento.getColObjMovimentado();
		
		GenericDAO dao = null;
				
		try{
			dao = getGenericDAO(movimento);
			dao.updateFields(InscricaoAtividade.class, inscricao.getId()
					, new String[]{"motivoCancelamento", "ativo"}
					, new Object[]{inscricao.getMotivoCancelamento(), false} );
			
			
			if(inscricoesParticipante != null && inscricoesParticipante.size() > 0){
				String sql = " UPDATE extensao.inscricao_atividade_participante set id_status_inscricao_participante = "+StatusInscricaoParticipante.RECUSADO
						+ " WHERE id_inscricao_atividade_participante IN "+UFRNUtils.gerarStringIn(inscricoesParticipante)
						+ " AND id_status_inscricao_participante = "+StatusInscricaoParticipante.INSCRITO;
				
				dao.update( sql );
			}
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return null;
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {

		ListaMensagens erros = new ListaMensagens();
		
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		InscricaoAtividade inscricao = (InscricaoAtividade) movimento.getObjMovimentado();
		
		@SuppressWarnings("unchecked")
		List<InscricaoAtividadeParticipante> inscricoesParticipante = (List<InscricaoAtividadeParticipante>) movimento.getColObjMovimentado();
		
		if( CalendarUtils.descartarHoras( inscricao.getPeriodoFim() ).before( CalendarUtils.descartarHoras( new Date() ) ) ){
			erros.addErro("O per�odo de inscri��o j� foi finalizado, n�o � mais necess�rio suspend�-lo.");
		}
		
		if(StringUtils.isEmpty(inscricao.getMotivoCancelamento()) && inscricoesParticipante != null && inscricoesParticipante.size() > 0 ){
			erros.addErro("� preciso preencher o motivo do cancelamento que ser� informado ao participantes cujas inscri��es ser�o canceladas.");
		}
		
		checkValidation(erros);
	}

}
