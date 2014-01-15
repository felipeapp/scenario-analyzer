/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;
import java.util.Calendar;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.negocio.dominio.CoordenacaoCursoMov;

/**
 * Processador que realiza identificação e substituição de coordenadores de
 * curso e de programas de pós-graduação
 *
 * @author leonardo
 *
 */
public class ProcessadorCoordenacaoCurso extends AbstractProcessador {

	/* (non-Javadoc)
	 * @see br.ufrn.arq.ejb.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		CoordenacaoCursoMov ccMov = (CoordenacaoCursoMov) mov;

		if( ccMov.getCodMovimento().equals( SigaaListaComando.ALTERAR_CONTATOS_COORDENADOR ) ){
			return alterarContatos(ccMov);
		}

		if( ccMov.getCodMovimento().equals( SigaaListaComando.ALTERAR_COORDENADOR ) ){
			return alterarCoordenador(ccMov);
		}
		
		
		validate(mov);

		GenericDAO dao = getGenericDAO(mov);
		CoordenacaoCurso coordenador = ccMov.getCoordenador();
		CoordenacaoCurso coordenadorAntigo = ccMov.getCoordenadorAntigo();
		if(coordenadorAntigo.getId() != 0)
			coordenadorAntigo = dao.findByPrimaryKey(coordenadorAntigo.getId(), CoordenacaoCurso.class);

		RegistroEntrada registro = mov.getUsuarioLogado().getRegistroEntrada();

		if(ccMov.getCodMovimento() == SigaaListaComando.IDENTIFICAR_COORDENADOR){
			coordenador.setUsuarioAtribuidor(registro);
			coordenador.setAtivo(true);

			dao.create(coordenador);
		}else if(ccMov.getCodMovimento() == SigaaListaComando.SUBSTITUIR_COORDENADOR){
			coordenador.setUsuarioAtribuidor(registro);
			coordenador.setAtivo(true);
			coordenador.setTelefoneContato1( coordenadorAntigo.getTelefoneContato1() );
			coordenador.setTelefoneContato2( coordenadorAntigo.getTelefoneContato2() );
			coordenador.setRamalTelefone1( coordenadorAntigo.getRamalTelefone1() );
			coordenador.setRamalTelefone2( coordenadorAntigo.getRamalTelefone2() );
			coordenador.setEmailContato( coordenadorAntigo.getEmailContato() );
			coordenador.setPaginaOficialCoordenacao( coordenadorAntigo.getPaginaOficialCoordenacao() );

			Calendar c = Calendar.getInstance();
			c.setTime(coordenador.getDataInicioMandato());
			c.add(Calendar.DAY_OF_MONTH, -1);

			coordenadorAntigo.setDataFimMandato( c.getTime() );
			coordenadorAntigo.setAtivo(false);
			coordenadorAntigo.setUsuarioFinalizador(registro);

			dao.update(coordenadorAntigo);
			dao.detach(coordenadorAntigo);
			dao.create(coordenador);
		}

		return mov;
	}

	/**
	 * Altera uma coordenação
	 * 
	 * @param ccMov
	 * @return
	 * @throws DAOException
	 */
	private CoordenacaoCurso alterarCoordenador(CoordenacaoCursoMov ccMov) throws DAOException {
		CoordenacaoCurso coord = ccMov.getCoordenador();
		GenericDAO dao = getGenericDAO(ccMov);
		try {
			dao.update(coord);
		} finally {
			dao.close();
		}
		return coord;
		
	}

	/**
	 * Altera o email e telefone de contato da coordenação do curso
	 * @param ccMov
	 * @return
	 * @throws DAOException
	 */
	private Object alterarContatos(CoordenacaoCursoMov ccMov) throws DAOException {

		CoordenacaoCurso coord = ccMov.getCoordenador();

		GenericDAO dao = getGenericDAO(ccMov);
		dao.updateField(CoordenacaoCurso.class, coord.getId(), "telefoneContato1", coord.getTelefoneContato1());
		dao.updateField(CoordenacaoCurso.class, coord.getId(), "telefoneContato2", coord.getTelefoneContato2());
		dao.updateField(CoordenacaoCurso.class, coord.getId(), "ramalTelefone1", coord.getRamalTelefone1());
		dao.updateField(CoordenacaoCurso.class, coord.getId(), "ramalTelefone2", coord.getRamalTelefone2());
		dao.updateField(CoordenacaoCurso.class, coord.getId(), "emailContato", coord.getEmailContato());
		dao.updateField(CoordenacaoCurso.class, coord.getId(), "paginaOficialCoordenacao", coord.getPaginaOficialCoordenacao());

		return coord;
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.ejb.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		CoordenacaoCursoMov ccMov = (CoordenacaoCursoMov) mov;
		ListaMensagens erros = new ListaMensagens();

		CoordenacaoCursoDao dao = getDAO(CoordenacaoCursoDao.class, mov);
		
		if( ccMov.getCoordenador().getCurso() != null ){
			CoordenacaoCurso cc = dao.findUltimaByServidorCurso(ccMov.getCoordenador().getServidor(), ccMov.getCoordenador().getCurso());
			
			if( cc != null && cc.getId() != 0 ){
				erros.addErro("Este usuário já está cadastrado como coordenador deste curso.");
			}
		} else if( ccMov.getCoordenador().getUnidade() != null ){
			CoordenacaoCurso cc = dao.findUltimaByServidorPrograma(ccMov.getCoordenador().getServidor(), ccMov.getCoordenador().getUnidade());
			
			if( cc != null && cc.getId() != 0 ){
				erros.addErro("Este usuário já está cadastrado como coordenador deste programa.");
			}
		}

		checkValidation(erros);
		checkValidation(ccMov.getCoordenador().validate());
	}

}
