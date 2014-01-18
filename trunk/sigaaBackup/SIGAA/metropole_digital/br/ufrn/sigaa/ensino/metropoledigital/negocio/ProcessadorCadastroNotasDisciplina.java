package br.ufrn.sigaa.ensino.metropoledigital.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ensino.metropoledigital.dao.LancamentoNotasDisciplinaDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.NotaIMD;

/**
 * Processador Utilizado no cadastro de notas por disciplinas do IMD
 * 
 * @author Rafael Silva
 *
 */
public class ProcessadorCadastroNotasDisciplina extends AbstractProcessador{

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {		
		
		LancamentoNotasDisciplinaDao dao = new LancamentoNotasDisciplinaDao();
		MovimentoCadastroNotasDisciplina movCadastroNotas = (MovimentoCadastroNotasDisciplina) mov;		
		try {
			for (NotaIMD nota : movCadastroNotas.getNotasIMD()) {
		
				if (nota.getParticipacaoTotal().getId()!=0) {
					nota.getParticipacaoTotal().setUnidade((byte)1);
					dao.createOrUpdate(nota.getParticipacaoTotal());
				}else if(nota.getParticipacaoTotal().getNota()!=null){
					nota.getParticipacaoTotal().setUnidade((byte)1);
					nota.getParticipacaoTotal().setMatricula(nota.getMatriculaComponente());
					dao.createOrUpdate(nota.getParticipacaoTotal());
				}
				
				if (nota.getAtividadeOnline().getId()!=0) {
					nota.getAtividadeOnline().setUnidade((byte)2);
					dao.createOrUpdate(nota.getAtividadeOnline());
				}else if(nota.getAtividadeOnline().getNota() !=null){
					nota.getAtividadeOnline().setUnidade((byte)2);
					nota.getAtividadeOnline().setMatricula(nota.getMatriculaComponente());
					dao.createOrUpdate(nota.getAtividadeOnline());					
				}
				
				if (nota.getProvaEscrita().getId()!=0) {
					nota.getProvaEscrita().setUnidade((byte)3);
					dao.createOrUpdate(nota.getProvaEscrita());
				}else if(nota.getProvaEscrita().getNota()!=null){
					nota.getProvaEscrita().setUnidade((byte)3);
					nota.getProvaEscrita().setMatricula(nota.getMatriculaComponente());
					dao.createOrUpdate(nota.getProvaEscrita());
				}									
			}
		} finally {
			dao.close();
		}							
		return movCadastroNotas;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub
		
	}

}
