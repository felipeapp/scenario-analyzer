/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>Contém as regras de negócio para quando o coordenador inclui um participante diretamente 
 * na  atividade de extensão, se que esse participante tenha feito uma inscrição.  </p>
 *
 * <p> <i> O coordenador não pode incluir 2 vezes o mesmo participante na atividade.</i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorIncluiParticipanteAtividadeExtensaoCoordenador  extends AbstractProcessador{

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
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
	 * Ver comentários da classe pai.<br/>
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
			
			if(! participante.getAtividadeExtensao().isAtivo()){
				erros.addErro(" A atividade de extensão \""+participante.getAtividadeExtensao().getTitulo()+"\" não está mais ativa no sistema. ");
				checkValidation(erros);
			}
			
			int qtd  = dao.countQtdParticipanteAtivoNaAtividade(participante.getCadastroParticipante().getId(), participante.getAtividadeExtensao().getId());
		
			if(qtd > 0){
				erros.addErro(" O participante \""+participante.getCadastroParticipante().getNome()+"\" já foi incluído nessa atividade");
			}
			
		}finally{
			if(dao != null) dao.close();
			checkValidation(erros);
		}
	}

}
