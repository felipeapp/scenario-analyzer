/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.PortaArquivosDao;
import br.ufrn.sigaa.ava.dominio.PastaArquivos;

/**
 * Processador que cadastra uma pasta em um porta-arquivos.
 * 
 * @author davidpereira
 *
 */
public class ProcessadorCadastroPastaTurma extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		PortaArquivosDao dao = getDAO(PortaArquivosDao.class, mov);
		MovimentoArquivoTurma aMov = (MovimentoArquivoTurma) mov;

		try {
			PastaArquivos pasta = dao.findPastaByUsuarioNome(aMov.getUsuario(), aMov.getTurma().getDisciplina().getCodigo(), null);

			if (pasta == null) {
				pasta = new PastaArquivos();
				pasta.setData(new Date());
				pasta.setNome(aMov.getTurma().getDisciplina().getCodigo());
				pasta.setUsuario(aMov.getUsuario());
				pasta.setPai(null);
				dao.create(pasta);
			}

			return pasta;

		} finally {
			dao.close();
		}

	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
