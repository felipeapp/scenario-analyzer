/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 31/01/2012
 */
package br.ufrn.sigaa.ensino.negocio;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.tecnico.dao.MatriculaFormacaoComplementarDao;

/**
 * Verifica se a pessoa do discente possui bolsa auxílio ativa no ano-período atual.
 * 
 * @author Leonardo Campos
 *
 */
public class RestricaoBolsaAuxilioValidator implements RestricaoDiscenteMatriculaValidator {

	@Override
	public void validate(DiscenteAdapter discente, ListaMensagens lista) throws ArqException {
		MatriculaFormacaoComplementarDao dao =  
				DAOFactory.getInstance().getDAO(MatriculaFormacaoComplementarDao.class);
		try{
			boolean alunoBolsista = dao.isAlunoBolsista(discente);
			if( !alunoBolsista){
				lista.addErro( "No momento, a matrícula on-line só está liberada para os alunos bolsistas.");
			}
		} finally {
			dao.close();
		}
	}

}
