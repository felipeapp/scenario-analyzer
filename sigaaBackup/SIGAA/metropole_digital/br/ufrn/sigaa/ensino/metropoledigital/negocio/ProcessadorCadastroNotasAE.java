package br.ufrn.sigaa.ensino.metropoledigital.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.metropoledigital.dao.LancamentoNotasDisciplinaDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.NotaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.RetornoNotaAEIntegracaoMoodle;

/**
 * Processador responsável por realizar o cadastro das notas das atividades executadas no moodle (AE), 
 * obtidas através da integração com o moodle do IMD.
 * 
 * @author Rafael Silva
 *
 */
public class ProcessadorCadastroNotasAE extends AbstractProcessador{

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		MovimentoCadastroNotasAE movIntegracao = (MovimentoCadastroNotasAE) mov;
		LancamentoNotasDisciplinaDao dao = new LancamentoNotasDisciplinaDao();
		int qtdNotasSincronizadas = 0;
		try {
			if(! movIntegracao.getListaRetornoMoodle().isEmpty()) {
				
				for (NotaIMD notasDiscente : movIntegracao.getListaNotasIMD()) {
					for(RetornoNotaAEIntegracaoMoodle retorno: movIntegracao.getListaRetornoMoodle()) {					
						if(notasDiscente.getDiscente().getMatricula().toString().equalsIgnoreCase(retorno.getMatriculaDiscente())){
							
							if (notasDiscente.getAtividadeOnline().getId()==0) {
								NotaUnidade n = new NotaUnidade();
								n.setUnidade((byte)2);
								n.setAtivo(true);
								n.setMatricula(notasDiscente.getMatriculaComponente());
								n.setNota(retorno.getNotaAe());
								if (n.getDataCadastro()==null) {
									n.setDataCadastro(new Date());
								}						
								notasDiscente.setAtividadeOnline(n);
							}else{
								notasDiscente.getAtividadeOnline().setNota(retorno.getNotaAe());
							}
							
							if (retorno.getNotaAe()!=null && notasDiscente.getAtividadeOnline().getNota()!=null) {
								dao.createOrUpdate(notasDiscente.getAtividadeOnline());
								qtdNotasSincronizadas++;
							}									
							
						}
					}
					
				}
			}			
		} finally {
			dao.close();
		}
 				
		return qtdNotasSincronizadas;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub
		
	}
	
	

}
