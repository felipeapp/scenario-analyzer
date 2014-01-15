package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.sigaa.ensino.dao.ValidacaoInscricaoSelecaoDao;
import br.ufrn.sigaa.ensino.dominio.InscricaoSelecao;

public class RestricaoInscritoVinculoInstituicaoValidator implements RestricaoInscricaoValidator{

	@Override
	public void validate(InscricaoSelecao inscricao, ListaMensagens lista) {
		
		Long cpf = inscricao.getPessoaInscricao().getCpf();
		String passaporte = inscricao.getPessoaInscricao().getPassaporte(); 
		ValidacaoInscricaoSelecaoDao validacaoDAO =  
				DAOFactory.getInstance().getDAO(ValidacaoInscricaoSelecaoDao.class);
		
		try{
			Boolean existeInscricao = validacaoDAO.existeInscricao(inscricao);
			if( existeInscricao ){
				lista.addErro( "Já existe uma inscrição realizada neste processo seletivo para este " + 
							( isEmpty(cpf)?"passaporte.":"CPF") );
			}
			
			Boolean existePessoaVinculo = validacaoDAO.possuiVinculoDiscenteServidor(cpf, passaporte);
			if( !existePessoaVinculo ){
				lista.addErro( "A inscrição é permitida somente para a Comunidade Interna " + 
						RepositorioDadosInstitucionais.getSiglaInstituicao() );
			}
		} catch (DAOException e) {
			e.printStackTrace();
		}finally{
			validacaoDAO.close();
		}
		
		
	}
	
}
