package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.assistencia.dao.AnoPeriodoReferenciaSAEDao;
import br.ufrn.sigaa.assistencia.dominio.AnoPeriodoReferenciaSAE;
import br.ufrn.sigaa.ensino.dao.ValidacaoInscricaoSelecaoDao;
import br.ufrn.sigaa.ensino.dominio.InscricaoSelecao;


/**
 * Verifica se a pessoa é discente e possui bolsa para o ano e período do calendário vigente.
 * Esta validação é aplicada para os candidatos no processo seletivo de {@link NivelEnsino#FORMACAO_COMPLEMENTAR}
 * @author Mário Rizzi
 *
 */
public class RestricaoInscritoBolsaSAEValidator implements RestricaoInscricaoValidator{

	@Override
	public void validate(InscricaoSelecao inscricao, ListaMensagens lista) {
		
		Long cpf = inscricao.getPessoaInscricao().getCpf();
		String passaporte = inscricao.getPessoaInscricao().getPassaporte(); 
		ValidacaoInscricaoSelecaoDao validacaoDAO =  
				DAOFactory.getInstance().getDAO(ValidacaoInscricaoSelecaoDao.class);
		
		AnoPeriodoReferenciaSAEDao anoPeriodoSAEDao =  
				DAOFactory.getInstance().getDAO(AnoPeriodoReferenciaSAEDao.class);
		
		Integer ano = null;
		Integer periodo = null;
		
		try{
			AnoPeriodoReferenciaSAE cal = anoPeriodoSAEDao.anoPeriodoVigente();
			
			if( cal != null){
				ano = cal.getAnoAlimentacao();
				periodo  = cal.getPeriodoAlimentacao();
			}
			
			Boolean existeInscricao = validacaoDAO.existeInscricao(inscricao);
			if( existeInscricao ){
				lista.addErro( "Já existe uma inscrição realizada neste processo seletivo para este " + 
							( isEmpty(cpf)?"passaporte.":"CPF") );
			}
			
			Boolean possuiBolsaSAE = validacaoDAO.possuiBolsaSAE(cpf, passaporte, ano, periodo);
			if( !possuiBolsaSAE ){
				lista.addErro( "A inscrição somente é permitida para os discentes assistidos pelo SAE.");
			}
		} catch (DAOException e) {
			e.printStackTrace();
		}finally{
			validacaoDAO.close();
			anoPeriodoSAEDao.close();
		}
		
		
	}
	
}
