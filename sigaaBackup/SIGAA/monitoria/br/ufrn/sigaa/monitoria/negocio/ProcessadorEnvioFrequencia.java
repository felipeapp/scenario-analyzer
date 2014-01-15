package br.ufrn.sigaa.monitoria.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.EnvioFrequencia;


/**
 * 
 * Processador responsável pelo cadastro da configuração para envio de 
 * relatórios de atividades do monitor (frequência).
 * 
 * @author ilueny santos
 *
 */
public class ProcessadorEnvioFrequencia extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);		
		if( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.CADASTRAR_ENVIO_FREQUENCIA)){
		    criarNovaAutorizacaoEnvioFrequencia((MovimentoCadastro)mov);
		}
		return null;
	}

	
	/**
	 * Criar ou atualizar Atividades do monitor
	 * 
	 * @param mov
	 * @return
	 * @throws ArqException
	 */
	private void criarNovaAutorizacaoEnvioFrequencia(MovimentoCadastro mov) throws ArqException{
		GenericDAO dao = getGenericDAO(mov);
		try {
			EnvioFrequencia antiga= (EnvioFrequencia) mov.getObjMovimentado();
			dao.detach(antiga);
			
			//Inativando a autorização antiga.
			dao.updateField(EnvioFrequencia.class, antiga.getId(), "ativo", Boolean.FALSE);
			
			//criando novas autorizações de envio de freqüência
			EnvioFrequencia nova = new EnvioFrequencia();
			nova.setId(0);
			nova.setAno(antiga.getAno());
			nova.setAnoFimProjetosPermitidos(antiga.getAnoFimProjetosPermitidos());
			nova.setAnoInicioProjetosPermitidos(antiga.getAnoInicioProjetosPermitidos());
			nova.setAtivo(true);
			nova.setDataCadastro(new Date());
			nova.setDataFimEntradaMonitorPermitido(antiga.getDataFimEntradaMonitorPermitido());
			nova.setDataFimRecebimento(antiga.getDataFimRecebimento());
			nova.setDataInicioEntradaMonitorPermitido(antiga.getDataInicioEntradaMonitorPermitido());
			nova.setDataInicioRecebimento(antiga.getDataInicioRecebimento());
			nova.setMes(antiga.getMes());
			nova.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			dao.create(nova);
				
		}catch (DAOException e) {
			throw new DAOException(e);					
		}finally {
			dao.close();
		}
		
	}
	
							
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens erros = new ListaMensagens();
		EnvioFrequencia f = (EnvioFrequencia) ((MovimentoCadastro)mov).getObjMovimentado();
		for(MensagemAviso m : f.validate().getMensagens()) {
			erros.addMensagem(m);
		}		
		checkValidation(erros);		
	}
	
}
