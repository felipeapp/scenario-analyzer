/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '14/01/2009'
 *
 */
package br.ufrn.sigaa.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.gru.dominio.CodigoRecolhimentoGRU;
import br.ufrn.comum.gru.dominio.GuiaRecolhimentoUniao;
import br.ufrn.comum.gru.dominio.ParametrosGRU;
import br.ufrn.comum.gru.dominio.TipoArrecadacao;
import br.ufrn.comum.gru.negocio.GuiaRecolhimentoUniaoHelper;
import br.ufrn.sigaa.arq.dao.graduacao.AgendaRevalidacaoDiplomaDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoRevalidacaoDiplomaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.diploma.dominio.ParametrosDiplomas;
import br.ufrn.sigaa.ensino.graduacao.dominio.AgendaRevalidacaoDiploma;
import br.ufrn.sigaa.ensino.graduacao.dominio.EditalRevalidacaoDiploma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoRevalidacaoDiploma;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;

/**
 * Classe responsável pelo processamento dos dados do movimento 
 * das solicitações de revalidação de diplomas
 * 
 * @author Mario Rizzi Rocha
 *
 */
public class ProcessadorSolicitacaoRevalidacaoDiploma extends AbstractProcessador {

	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {

		validate(movimento);
		if(movimento.getCodMovimento() == SigaaListaComando.CADASTRA_REVALIDACAO_DIPLOMA)
			cadastrar(movimento);
		else if(movimento.getCodMovimento() == SigaaListaComando.REAGENDAMENTO_REVALIDACAO_DIPLOMA)
			atualizar(movimento);
		return movimento;
		
	}
	
	/**
	 * Persiste os dados do inscrito para um processo de revalidação de diploma
	 * @param movimento
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public void cadastrar(Movimento movimento) throws NegocioException, ArqException, RemoteException {
	
		MovimentoRevalidacaoDiploma mov = (MovimentoRevalidacaoDiploma) movimento;
		GenericDAO dao = getGenericDAO(mov);
		SolicitacaoRevalidacaoDiploma solicitacao = mov.getRevalidacaoDiploma();
		GenericDAO daoChk = getGenericDAO(mov);
		
		try{
			
			SolicitacaoRevalidacaoDiploma chkSolicitacao = null;
			
			if(mov.getRevalidacaoDiploma().getId()>0){
				chkSolicitacao = 
					daoChk.findByPrimaryKey(mov.getRevalidacaoDiploma().getId(), SolicitacaoRevalidacaoDiploma.class);
			}
			
			if(!ValidatorUtil.isEmpty(chkSolicitacao))
				dao.update(solicitacao);
			else{
				EditalRevalidacaoDiploma edital = solicitacao.getEditalRevalidacaoDiploma();
				
				// cria uma GRU para pagamento da taxa de revalidação de diploma
				double taxaReavaliacaoDiploma  = ParametroHelper.getInstance().getParametroDouble(ParametrosGraduacao.TAXA_REVALIDACAO_DIPLOMA);
				
				int idCodigoRecolhimento = ParametroHelper.getInstance().getParametroInt(ParametrosDiplomas.ID_CODIGO_RECOLHIMENTO_GRU_REVALIDACAO_DIPLOMA);
				CodigoRecolhimentoGRU codigoRecolhimento = new CodigoRecolhimentoGRU(idCodigoRecolhimento);
				
				GuiaRecolhimentoUniao gru = GuiaRecolhimentoUniaoHelper.createGRUSimples(
						idCodigoRecolhimento,
						solicitacao.getCpf(), 
						solicitacao.getNome(), 
						null, 
						ParametroHelper.getInstance().getParametroInt(ParametrosGRU.ID_UNIDADE_FAVORECIDA_PADRAO), 
						new TipoArrecadacao(TipoArrecadacao.REVALIDACAO_DIPLOMA), 
						codigoRecolhimento.getCodigo(),
						null, 
						edital.getFinalAgenda(), 
						taxaReavaliacaoDiploma);
				solicitacao.setIdGru(gru.getId());
				solicitacao.setId(0);
				dao.create(solicitacao);
			}
		}finally{
			dao.close();
			daoChk.close();
		}
		
	}
	
	/**
	 * Persiste os dados alterados do inscrito durante o período de reagendamento
	 * @param movimento
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public void atualizar(Movimento movimento) throws NegocioException, ArqException, RemoteException {
	
		MovimentoRevalidacaoDiploma mov = (MovimentoRevalidacaoDiploma) movimento;
		GenericDAO dao = getGenericDAO(mov);
		SolicitacaoRevalidacaoDiploma solicitacao = mov.getRevalidacaoDiploma();
		
		try{
			
			dao.updateField(SolicitacaoRevalidacaoDiploma.class, solicitacao.getId(), "agendaRevalidacaoDiploma.id", 
					solicitacao.getAgendaRevalidacaoDiploma().getId());
			
		}finally{
			dao.close();
		}
		
	}
	
	@Override
	public void validate(Movimento movimento) throws NegocioException, ArqException {

		MovimentoRevalidacaoDiploma mov = (MovimentoRevalidacaoDiploma) movimento;
		SolicitacaoRevalidacaoDiploma solicitacao = mov.getRevalidacaoDiploma();
		ListaMensagens erros = solicitacao.validate();
		
		SolicitacaoRevalidacaoDiplomaDao solicitacaoDAO = getDAO(SolicitacaoRevalidacaoDiplomaDao.class, mov);
		AgendaRevalidacaoDiplomaDao agendaDAO = getDAO(AgendaRevalidacaoDiplomaDao.class, mov);	
		
		try{
			
			SolicitacaoRevalidacaoDiploma verificaSolCpf = 
			solicitacaoDAO.findByNacionalidadeDocumento(solicitacao, null, solicitacao.getCpf(), solicitacao.getPassaporte()); 

		
			//Verifica se a data e horário selecionados estão disponíveis.
			if(!ValidatorUtil.isEmpty(solicitacao.getEditalRevalidacaoDiploma()) && solicitacao.getEditalRevalidacaoDiploma().getId()>0
						&& !ValidatorUtil.isEmpty(solicitacao.getAgendaRevalidacaoDiploma())){
					solicitacao.getAgendaRevalidacaoDiploma().setEditalRevalidacaoDiploma(solicitacao.getEditalRevalidacaoDiploma());
				if(!agendaDAO.findDataDisponivel(solicitacao.getAgendaRevalidacaoDiploma(), solicitacao.getId())){
						solicitacao.setAgendaRevalidacaoDiploma(new AgendaRevalidacaoDiploma());
						erros.addErro("As vagas de agendamento para a data e/ou horário selecionado(s) estão esgotadas. " +
								" Por favor selecione uma nova data e/ou horário.");
				}	
			}

			
			// Se for cadastro
			if(solicitacao.getId()==0){
				//Verifica a obrigatoriedade do campo CPF
				if(SolicitacaoRevalidacaoDiploma.NACIONALIDADE_PADRAO == solicitacao.getPais().getId() 
						&& solicitacao.getCpf()==0)
					erros.addErro("O campo CPF é obrigatório.");
				
				//Verifica a obrigatoriedade do campo PASSAPORTE
				else if((SolicitacaoRevalidacaoDiploma.NACIONALIDADE_PADRAO != solicitacao.getPais().getId() 
						&& isEmpty(solicitacao.getPassaporte()))
						|| (SolicitacaoRevalidacaoDiploma.NACIONALIDADE_PADRAO != solicitacao.getPais().getId()
							&& solicitacao.getPassaporte()==null))
					erros.addErro("O campo Passaporte é obrigatório.");
				
				//Verifica se já existe um cadastro para o CPF no mesmo processo de revalidação.
				else if(solicitacao.getCpf()!=null && solicitacao.getCpf() > 0  && verificaSolCpf!= null)
					erros.addErro("Já existe uma inscrição para o CPF: " + solicitacao.getCpf() + ".");
				
				//Verifica se já existe um cadastro para o PASSAPORTE no mesmo processo seletivo
				else if(!isEmpty(solicitacao.getPassaporte())  && solicitacaoDAO.
						findByNacionalidadeDocumento(solicitacao, null, null, solicitacao.getPassaporte()) !=  null)
					erros.addErro("Já existe uma inscrição para o Passaporte: " + solicitacao.getPassaporte() + ".");
				
			}
			
		}finally{
			solicitacaoDAO.close();
			agendaDAO.close();
		}
		
		checkValidation(erros);
	}


}
