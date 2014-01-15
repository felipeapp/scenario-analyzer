/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/03/2007'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.ApplicationContext;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.dto.CursoLatoDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoException;
import br.ufrn.integracao.interfaces.CursoConcursoRemoteService;
import br.ufrn.rh.dominio.Servidor;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CursoLatoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.HistoricoSituacaoDao;
import br.ufrn.sigaa.ensino.dominio.CargoAcademico;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.HistoricoSituacao;
import br.ufrn.sigaa.ensino.latosensu.dominio.PropostaCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;

/**
 * Processador para criar um registro no histórico de situações
 * da proposta e atualizar o status atual da mesma.
 *
 * @author Leonardo
 *
 */
public class ProcessadorHistoricoSituacao extends AbstractProcessador {

	/* (non-Javadoc)
	 * @see br.ufrn.arq.ejb.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);

		GenericDAO dao = getDAO(HistoricoSituacaoDao.class, mov);

		HistoricoSituacao historico = (HistoricoSituacao)((MovimentoCadastro) mov).getObjMovimentado();

		PropostaCursoLato proposta = historico.getProposta();
		proposta.setSituacaoProposta( historico.getSituacao() );

		dao.create(historico);
		dao.update(proposta);

		/*
		 * Realiza criação do projeto de curso e concurso referente ao curso lato da proposta especificada.
		 */
		if(proposta.isSituacao(new int[]{SituacaoProposta.ACEITA}))
			criarProjetoCursoConcurso((MovimentoCadastro) mov, proposta);

		return historico;
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.ejb.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

	/**
	 * Realiza criação do projeto de curso e concurso referente ao curso lato da proposta especificada.
	 * @param mc
	 * @param proposta
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private void criarProjetoCursoConcurso(MovimentoCadastro mc, PropostaCursoLato proposta)
		throws NegocioException, ArqException, RemoteException{

		ParametroHelper param = ParametroHelper.getInstance();

		boolean cadastrarProjeto = Sistema.isSigrhAtivo() &&
			param.getParametroBoolean(ParametrosGerais.PAGAR_CURSO_LATO_SENSU_ATRAVES_DE_CURSOS_E_CONCURSOS);

		if(!cadastrarProjeto)
			return;

		CursoLatoDao dao = getDAO(CursoLatoDao.class, mc);
		CursoLato cursoLato = dao.findByPropostaCursoLato(proposta.getId());
		dao.close();

		CoordenacaoCursoDao coorDao = getDAO(CoordenacaoCursoDao.class, mc);
		Collection<CoordenacaoCurso> coordenacoes =
			coorDao.findByCurso(cursoLato.getId(), 0, NivelEnsino.LATO, null, CargoAcademico.COORDENACAO, CargoAcademico.VICE_COORDENACAO);

		coorDao.close();
		ArrayList<Servidor> servidores = new ArrayList<Servidor>();

		for(CoordenacaoCurso cc : coordenacoes){
			Servidor s = new Servidor();
			s.setId(cc.getServidor().getId());
			s.setSiape(cc.getServidor().getSiape());
			s.setUnidade(cc.getServidor().getUnidade());
			s.setPessoa(cc.getServidor().getPessoa());

			if(!servidores.contains(s))
				servidores.add(s);
		}

		CursoLatoDTO cursoLatoDTO = new CursoLatoDTO();
		cursoLatoDTO.setId(cursoLato.getId());
		cursoLatoDTO.setNome(cursoLato.getNome());
		cursoLatoDTO.setIdUnidade(cursoLato.getUnidade().getId());
		cursoLatoDTO.setServidoresCoordenadores(UFRNUtils.dominioToId(servidores));

		try {
			ApplicationContext context = mc.getApplicationContext();
			CursoConcursoRemoteService service = (CursoConcursoRemoteService) context.getBean("cursoConcursoInvoker");
			service.cadastrarProjetoCursoConcurso(cursoLatoDTO, mc.getUsuarioLogado().getId());
		} catch (NegocioRemotoException e) {
			throw new NegocioException(e.getMessage());
		}
		
	}

}
