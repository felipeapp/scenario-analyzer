/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/05/2009
 *
 */
package br.ufrn.sigaa.diploma.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.diploma.dao.RegistroDiplomaDao;
import br.ufrn.sigaa.diploma.dao.ResponsavelAssinaturaDiplomasDao;
import br.ufrn.sigaa.diploma.dominio.RegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.RegistroDiplomaColetivo;
import br.ufrn.sigaa.diploma.dominio.ResponsavelAssinaturaDiplomas;
import br.ufrn.sigaa.ensino.dominio.CargoAcademico;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.mensagens.MensagensGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/** Processa o registro de diplomas coletivo.
 * @author Édipo Elder F. Melo
 *
 */
public class ProcessadorRegistroDiplomaColetivo extends AbstractProcessador {

	/** Realiza o cadastro dos diplomas.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);
		RegistroDiplomaDao registroDao = getDAO(RegistroDiplomaDao.class, mov);
		CoordenacaoCursoDao coordenacaoDao = getDAO(CoordenacaoCursoDao.class, mov);
		ResponsavelAssinaturaDiplomasDao assinaturaDao = getDAO(ResponsavelAssinaturaDiplomasDao.class, mov);
		try {
			RegistroDiplomaColetivo registroColetivo = ((MovimentoCadastro) mov).getObjMovimentado();
			// seta o coordenador do curso e assinaturas
			ResponsavelAssinaturaDiplomas assinaturas = assinaturaDao.findAtivo(registroColetivo.getLivroRegistroDiploma().getNivel());
			// mapa "cache" de coordenação por curso.
			Map<Integer, CoordenacaoCurso> mapaCoordenacao = new HashMap<Integer, CoordenacaoCurso>();
			for (RegistroDiploma registro : registroColetivo.getRegistrosDiplomas()) {
				CoordenacaoCurso coordenador = mapaCoordenacao.get(registro.getDiscente().getCurso().getId());
				if (coordenador == null) {
					coordenador = coordenacaoDao.findAtivoByData(registro.getDataColacao(), registro.getDiscente().getCurso());
					// caso não haja coordenação ativa, pega o último coordenador
					if (coordenador == null) {
						Collection<CoordenacaoCurso> coordenadores = coordenacaoDao.findByCurso(
									registro.getDiscente().getCurso().getId(), 0, 
									registro.getDiscente().getCurso().getNivel(), null,
									CargoAcademico.COORDENACAO);
						if (!isEmpty(coordenadores))
							coordenador = coordenadores.iterator().next(); 
					}
					mapaCoordenacao.put(registro.getDiscente().getCurso().getId(), coordenador);
				}
				registro.setCoordenadorCurso(coordenador);
				// Assinaturas no diploma
				registro.setAssinaturaDiploma(assinaturas);
			}
			if (registroColetivo.getPolo() != null && registroColetivo.getPolo().getId() == 0)
				registroColetivo.setPolo(null);
			registroDao.create(registroColetivo);
			for (RegistroDiploma registro : registroColetivo.getRegistrosDiplomas()) {
				// número de registro
				int numeroRegistro = registroDao.requisitaNumeroRegistro(registro.getLivroRegistroDiploma().isRegistroExterno(), registro.getLivroRegistroDiploma().getNivel());
				registro.setNumeroRegistro(numeroRegistro);
				// registro de entrada
				registro.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
				registroDao.createOrUpdate(registro.getFolha());
				registroDao.create(registro);
			}
			return registroColetivo;
		} finally {
			registroDao.close();
			coordenacaoDao.close();
			assinaturaDao.close();
		}
	}

	/** Valida os dados antes do registro.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		RegistroDiplomaDao registroDiplomaDao = getDAO(RegistroDiplomaDao.class, mov);
		try {
			RegistroDiplomaColetivo obj = ((MovimentoCadastro) mov).getObjMovimentado();
			obj.validate();
			boolean possuiDiscenteNaoRegistrado = false;
			Collection<Discente> discentes = new ArrayList<Discente>();
			for (RegistroDiploma registro : obj.getRegistrosDiplomas()) {
				discentes.add(registro.getDiscente());
			}
			Collection<Discente> registrados = registroDiplomaDao.verificaDiscentesRegistrados(discentes );
			if (registrados != null) {
				for (Discente discente : discentes) {
					if (!registrados.contains(discente)) {
						possuiDiscenteNaoRegistrado = true;
						break;
					} 
				}
			} else {
				throw new NegocioException("Há discentes com diplomas registrados.");	
			}
			// se todos da turma foram registrados, retorne
			if (!possuiDiscenteNaoRegistrado) {
				throw new NegocioException(MensagensGraduacao.TODOS_DISCENTES_DA_TURMA_FORAM_REGISTRADOS);
			}
		} finally {
			registroDiplomaDao.close();
		}
		
	}

}
