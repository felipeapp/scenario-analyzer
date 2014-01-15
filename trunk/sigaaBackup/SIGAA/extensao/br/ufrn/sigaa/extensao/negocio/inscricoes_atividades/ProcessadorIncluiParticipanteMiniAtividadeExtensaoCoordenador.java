/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 06/12/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio.inscricoes_atividades;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.extensao.ParticipanteAcaoExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;

/**
 *
 * <p>Cont�m as regras de neg�cio para quando o coordenador inclui um participante diretamente 
 * na mini atividade de extens�o, se que esse participante tenha feito uma inscri��o.  </p>
 *
 * <p> <i> A principal regra � que o particiopante s� pode ser cadastrar na mini atividade se 
 * possuir cadastrar na atividade pai.</i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorIncluiParticipanteMiniAtividadeExtensaoCoordenador  extends AbstractProcessador{

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {
		
		validate(mov);
		
		MovimentoCadastro  movi = (MovimentoCadastro) mov;
		
		ParticipanteAcaoExtensao participante = (ParticipanteAcaoExtensao) movi.getObjMovimentado();
		
		GenericDAO dao = null;
		
		try{
			dao = getGenericDAO(movi);
			dao.create(participante);
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
		
		MovimentoCadastro  movi = (MovimentoCadastro) mov;
		
		ParticipanteAcaoExtensao participante = (ParticipanteAcaoExtensao) movi.getObjMovimentado();
		
		ParticipanteAcaoExtensaoDao dao = null;
		
		try{
			

			dao = getDAO(ParticipanteAcaoExtensaoDao.class, movi);
			
			erros.addAll(participante.validate());
			
			if(! participante.getSubAtividade().isAtivo()){
				erros.addErro(" A mini atividade de extens�o \""+participante.getSubAtividade().getTitulo()+"\" n�o est� mais ativa no sistema. ");
				checkValidation(erros);
			}
			
			int qtdMiniAtividade  = dao.countQtdParticipanteNaMiniAtividade(participante.getCadastroParticipante().getId(), participante.getSubAtividade().getId());
			
			if(qtdMiniAtividade > 0){
				erros.addErro(" O participante \""+participante.getCadastroParticipante().getNome()+"\" j� foi inclu�do nessa mini atividade");
			}
			
			
			int qtdAtividade  = dao.countQtdParticipanteAtivoNaAtividade(participante.getCadastroParticipante().getId(), participante.getSubAtividade().getAtividade().getId());
			
			if(qtdAtividade == 0){
				erros.addErro(" O participante \""+participante.getCadastroParticipante().getNome()+"\" s� pode ser inclu�do na mini atividade se ele foi primeiro inclu�do na atividade principal.");
			}
		
		}finally{
			if(dao != null) dao.close();
			
			checkValidation(erros);
		}
	}

}
