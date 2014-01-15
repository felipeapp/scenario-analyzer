/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 09/10/2006
 *
 */
package br.ufrn.sigaa.ensino.latosensu.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.hibernate.HibernateException;

import br.ufrn.academico.dominio.CargoAcademico;
import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.gru.dominio.ConfiguracaoGRU;
import br.ufrn.sigaa.arq.dao.ead.PoloCursoDao;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.dao.ensino.SecretariaUnidadeDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CorpoDocenteCursoLatoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CorpoDocenteDisciplinaLatoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CursoLatoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.FormaAvaliacaoPropostaDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.FormaSelecaoPropostaDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.UnidadeCursoLatoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.PoloCurso;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.SecretariaUnidade;
import br.ufrn.sigaa.ensino.latosensu.dominio.CorpoDocenteCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.CorpoDocenteDisciplinaLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.FormaAvaliacao;
import br.ufrn.sigaa.ensino.latosensu.dominio.FormaAvaliacaoProposta;
import br.ufrn.sigaa.ensino.latosensu.dominio.FormaSelecao;
import br.ufrn.sigaa.ensino.latosensu.dominio.FormaSelecaoProposta;
import br.ufrn.sigaa.ensino.latosensu.dominio.HistoricoSituacao;
import br.ufrn.sigaa.ensino.latosensu.dominio.PropostaCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;
import br.ufrn.sigaa.ensino.latosensu.dominio.TipoPassoPropostaLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.UnidadeCursoLato;
import br.ufrn.sigaa.ensino.negocio.ProcessadorCoordenacaoCurso;
import br.ufrn.sigaa.ensino.negocio.ProcessadorSecretariaUnidade;
import br.ufrn.sigaa.ensino.negocio.dominio.CoordenacaoCursoMov;
import br.ufrn.sigaa.ensino.negocio.dominio.SecretariaUnidadeMov;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador para tratar dos comandos CRUD de CursoLato
 *
 * @author Leonardo
 * @author Jean Guerethes
 *
 */
public class ProcessadorCursoLato extends AbstractProcessador {

	/** Armazena os erros apresentado durante o cadastrado, atualização ou remoção de um Curso Lato Sensu */
	ListaMensagens erros = new ListaMensagens();
	
	/**
	 * Responsável pela decisão e execução da ação a ser tomada.
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		MovimentoCadastro movc = (MovimentoCadastro) mov;

		CursoLato curso = (CursoLato) movc.getObjMovimentado();

		GenericDAO dao = getGenericDAO(mov);

		CursoLatoDao daoLato = getDAO(CursoLatoDao.class,mov);
		
		FormaSelecaoPropostaDao formaSelecaoDao = getDAO(FormaSelecaoPropostaDao.class, mov); 
		
		FormaAvaliacaoPropostaDao formaAvalicacaoDao = getDAO(FormaAvaliacaoPropostaDao.class, mov);

		try {
			if ( movc.getCodMovimento().equals(SigaaListaComando.PERSISTIR_CURSO_LATO) 
					|| movc.getCodMovimento().equals(SigaaListaComando.NOVA_PROPOSTA_EXISTENTE) ) {

				validate(mov);
				
				if (!erros.isEmpty()) 
					return null;
				
				curso.setTipoOfertaCurso(null);
				PropostaCursoLato proposta = curso.getPropostaCurso();
				if(proposta.getUsuario() == null)
					proposta.setUsuario((Usuario)mov.getUsuarioLogado());
				if(proposta.getDataCadastro() == null)
					proposta.setDataCadastro(new Date(System.currentTimeMillis()));
				if(proposta.getCoordenador() == null)
					proposta.setCoordenador( proposta.getUsuario().getServidor() );
				
				proposta.getSituacaoProposta().setId(SituacaoProposta.INCOMPLETA);
				
				curso.setNivel('L');
				//persiste a proposta
				persist(proposta, mov, dao);
				persist(curso, movc, dao);
				
				if ( movc.getCodMovimento().equals(SigaaListaComando.NOVA_PROPOSTA_EXISTENTE) )
					cadastrarCoordenacaoSecretario(movc, curso, formaAvalicacaoDao);
				
				// Cadastrar o Polo do curso. (desde que o curso seja a distância)
				for (PoloCurso poloCurso : curso.getPolosCursos()) {
					if (poloCurso.getPolo() != null ) {
						poloCurso.setCurso(curso);
						dao.create(poloCurso);
					}
				}

				// Cadastra as Outras Unidades Envolvidas.
				for (UnidadeCursoLato unidadeCursoLato : curso.getUnidadesCursoLato()) {
						unidadeCursoLato.setCurso(curso);
						dao.create(unidadeCursoLato);
				}
				
				cadastrarHistorico(curso, mov);
			}
			
			if ( movc.getCodMovimento().equals(SigaaListaComando.ALTERAR_PROPOSTA_CURSO_LATO) ) {
				
				validate(mov);

				if (!erros.isEmpty())
					return erros;
				
				if (curso.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_DADOS_GERAIS)) {
					cadastrarTelaDadosGerais(curso, dao, mov);
					carregarSecretarioAtual(curso, mov, dao);
				} else if (curso.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_CONFIGURACAO_GRU)) {
					GenericDAO comumDao = DAOFactory.getGeneric(Sistema.COMUM);
					try {
						ConfiguracaoGRU configInscricao = curso.getConfiguracaoGRUInscricao();
						ConfiguracaoGRU configMensalidade = curso.getConfiguracaoGRUMensalidade();
						if (configInscricao != null) {
							if (configInscricao.getGrupoEmissaoGRU().getId() == 0) comumDao.create(configInscricao.getGrupoEmissaoGRU());
							if (configInscricao.getId() == 0) comumDao.create(configInscricao);
							curso.setIdConfiguracaoGRUInscricao(configInscricao.getId());
						} else
							curso.setIdConfiguracaoGRUInscricao(null);
						if (configMensalidade != null) {
							if (configMensalidade.getGrupoEmissaoGRU().getId() == 0) comumDao.create(configMensalidade.getGrupoEmissaoGRU());
							if (configMensalidade.getId() == 0) comumDao.create(configMensalidade);
							curso.setIdConfiguracaoGRUMensalidade(configMensalidade.getId());
						}else
							curso.setIdConfiguracaoGRUMensalidade(null);
					} catch (HibernateException e) {
						throw new DAOException(e);
					} finally {
						comumDao.close();
					}
					daoLato.update(curso);
				} else if (curso.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_COORDENACAO_CURSO)) {
					cadastrarCoordenacaoSecretario(mov, curso, dao);
				}
				else if (curso.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_OBJETIVO_IMPORTACIA)) {
					persist(curso.getPropostaCurso(), mov, dao);
				}
				else if (curso.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_PROCESSO_SELETIVO)) {
					cadastrarProcessoSeletivo(movc, curso, formaSelecaoDao, formaAvalicacaoDao);
					persist(curso.getPropostaCurso(), mov, dao);
					persist(curso, mov, dao);
				}
			}
			
			//Remoção de Curos Lato Sensu
			if (movc.getCodMovimento().equals(SigaaListaComando.REMOVER_CURSO_LATO)) {
				dao.updateField(PropostaCursoLato.class, curso.getPropostaCurso().getId(), "situacaoProposta", SituacaoProposta.EXCLUIDA);
				dao.updateField(CursoLato.class, curso.getId(), "ativo", Boolean.FALSE);
				cadastrarHistorico(curso, mov);
			}
			
		} finally {
			dao.close();
			daoLato.close();
			formaSelecaoDao.close();
		}

		return null;
	}

	/**
	 * Realiza o cadastro dos dados gerais do Curso Lato Sensu.
	 * 
	 * @param curso
	 * @param dao
	 * @param mov
	 * @throws DAOException
	 */
	private void cadastrarTelaDadosGerais(CursoLato curso, GenericDAO dao, Movimento mov) throws DAOException {
		PoloCursoDao poloCursoDao = getDAO(PoloCursoDao.class, mov);
		UnidadeCursoLatoDao unidCursoLatoDao = getDAO(UnidadeCursoLatoDao.class, mov);

		CursoLato cursoLatoAntigo = dao.findByPrimaryKey(curso.getId(), CursoLato.class);
		dao.detach(cursoLatoAntigo);
		dao.detach(cursoLatoAntigo.getPropostaCurso());
		
		dao.update(curso.getPropostaCurso());
		dao.update(curso);
		
		Collection<PoloCurso> polosCursos = new ArrayList<PoloCurso>();
		try {
			polosCursos = poloCursoDao.findAllCursoPolo(curso);	
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			poloCursoDao.close();	
		}

		// Remove os Polos removidos da proposta.
		for (PoloCurso poloCurso : polosCursos) {
			if (!(curso.getPolosCursos().contains(poloCurso))) 
				dao.remove(poloCurso);
		}
		
		// Cria os Polos adicionados na proposta.
		for (PoloCurso poloCurso : curso.getPolosCursos()) {
			if (!polosCursos.contains(poloCurso)){ 
				poloCurso.setCurso(curso);
				dao.create(poloCurso);
			}
		}
		
		Collection<UnidadeCursoLato> undCursoLato = new ArrayList<UnidadeCursoLato>();
		try {
			undCursoLato = unidCursoLatoDao.findAllUnidadeCursoLato(curso);	
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			unidCursoLatoDao.close();	
		}
		
		// Remove as Unidades removidas da proposta.
		for (UnidadeCursoLato undCurso : undCursoLato) {
			if (!(curso.getUnidadesCursoLato().contains(undCurso))) 
				dao.remove(undCurso);
		}
		
		// Cria as Unidades adicionados na proposta.
		for (UnidadeCursoLato undCurso : curso.getUnidadesCursoLato()) {
			if (!undCursoLato.contains(undCurso)){ 
				undCurso.setCurso(curso);
				dao.create(undCurso);
			}
		}
		
		if (curso.getPropostaCurso().getSituacaoProposta().getId() != 
			cursoLatoAntigo.getPropostaCurso().getSituacaoProposta().getId()) 
			cadastrarHistorico(curso, mov);	
		
	}
	
	/**
	 * Caso a prosposta já possua um secretário esse método é responsável por carregar todas as suas informações
	 * 
	 * @return
	 * @throws DAOException
	 */
	private SecretariaUnidade carregarSecretarioAtual(CursoLato curso, Movimento mov, GenericDAO dao) throws DAOException{
		SecretariaUnidadeDao secretariaDao = getDAO(SecretariaUnidadeDao.class, mov);
		try {
			Collection<SecretariaUnidade> secretariosCurso = secretariaDao.findByCurso(curso.getId());
			for (SecretariaUnidade secretariaUnidade : secretariosCurso) {
				if (secretariaUnidade.isAtivo()) {
					curso.setSecretario( new SecretariaUnidade() );
					curso.setSecretario( secretariaUnidade );
					curso.getSecretario().getUsuario();
					curso.getSecretario().setUsuario(dao.findAndFetch(secretariaUnidade.getUsuario().getId(), Usuario.class,"pessoaSigaa"));
					curso.getSecretario().getUsuario().setPessoa(dao.findByPrimaryKey(curso.getSecretario().getUsuario().getPessoa()
							.getId(), Pessoa.class));
				}
			}
			return curso.getSecretario();
		} finally {
			secretariaDao.close();
		}
	}

	/**
	 * Responsável pelo cadastro do Coordenador e do Secretário do Curso Lato Sensu.
	 * 
	 * @param mov
	 * @param cursoLato
	 * @param dao
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private void cadastrarCoordenacaoSecretario(Movimento mov, CursoLato cursoLato, GenericDAO dao) throws NegocioException, ArqException, RemoteException{
		
		CoordenacaoCursoDao coordenacaoCursoDao = getDAO(CoordenacaoCursoDao.class, mov);
		CoordenacaoCursoMov coordCursoMov;
		CorpoDocenteCursoLato corpoDocenteCursoLato =  new CorpoDocenteCursoLato();
		ProcessadorCoordenacaoCurso procCoordCurso;
		ProcessadorSecretariaUnidade procSecretaria;
		try {
			Collection<CoordenacaoCurso> coordenadoresCurso = coordenacaoCursoDao.findCoordViceByCursoNivel(cursoLato.getId(), NivelEnsino.LATO);
			for (CoordenacaoCurso coordenacaoCurso : coordenadoresCurso) {
				if (coordenacaoCurso.getCargoAcademico().getId() == CargoAcademico.COORDENACAO) 
					cursoLato.setCoordenadorAntigo(coordenacaoCurso);
				else
					cursoLato.setViceCoordenadorAntigo(coordenacaoCurso);
			}
		} finally {
			coordenacaoCursoDao.close();
		}
		
		coordCursoMov = new CoordenacaoCursoMov();
		cursoLato.getCoordenador().getCurso().setId(cursoLato.getId());
		cursoLato.getCoordenador().getCargoAcademico().setId(CargoAcademico.COORDENACAO);
		cursoLato.getCoordenador().validate();
		
		if (cursoLato.getCoordenador().getServidor().getId() != cursoLato.getCoordenadorAntigo().getServidor().getId()) {
			procCoordCurso = new ProcessadorCoordenacaoCurso();
			coordCursoMov.setCoordenador(cursoLato.getCoordenador());
			coordCursoMov.setCoordenadorAntigo(cursoLato.getCoordenadorAntigo());
			cursoLato.getEquipeLato().setDocente(cursoLato.getCoordenadorAntigo().getServidor());
			
			corpoDocenteCursoLato.setServidor(cursoLato.getCoordenador().getServidor());
			corpoDocenteCursoLato.setCursoLato(cursoLato);
			corpoDocenteCursoLato.setDocenteExterno(null);
			adicionarServidorCoordenacao(corpoDocenteCursoLato, coordCursoMov, cursoLato, dao);
			coordCursoMov.setSistema(Sistema.SIGAA);
			coordCursoMov.setUsuarioLogado(mov.getUsuarioLogado());

			if (cursoLato.getCoordenador().getId() != 0 && cursoLato.getCoordenadorAntigo().getId() != 0) {
				coordCursoMov.setCodMovimento(SigaaListaComando.SUBSTITUIR_COORDENADOR);	
			}else{
				coordCursoMov.setCodMovimento(SigaaListaComando.IDENTIFICAR_COORDENADOR);
			}
			
			coordCursoMov.getCoordenador().setId(0);
			procCoordCurso.execute(coordCursoMov);
		} else {
			procCoordCurso = new ProcessadorCoordenacaoCurso();
			coordCursoMov.setSistema(Sistema.SIGAA);
			coordCursoMov.setUsuarioLogado(mov.getUsuarioLogado());
			coordCursoMov.setCoordenador(cursoLato.getCoordenador());
			coordCursoMov.setCodMovimento(SigaaListaComando.ALTERAR_COORDENADOR);
			procCoordCurso.execute(coordCursoMov);
		}
		
		coordCursoMov = new CoordenacaoCursoMov();
		cursoLato.getViceCoordenador().getCargoAcademico().setId(CargoAcademico.VICE_COORDENACAO);
		cursoLato.getViceCoordenador().getCurso().setId(cursoLato.getId());
		cursoLato.getViceCoordenador().validate();
		
		if (cursoLato.getViceCoordenador().getServidor().getId() != cursoLato.getViceCoordenadorAntigo().getServidor().getId()) {
			procCoordCurso = new ProcessadorCoordenacaoCurso();
			coordCursoMov.setCoordenador(cursoLato.getViceCoordenador());
			coordCursoMov.setCoordenadorAntigo(cursoLato.getViceCoordenadorAntigo());
			cursoLato.getEquipeLato().setDocente(cursoLato.getViceCoordenadorAntigo().getServidor());
			corpoDocenteCursoLato =  new CorpoDocenteCursoLato();
			corpoDocenteCursoLato.setServidor(cursoLato.getViceCoordenador().getServidor());
			corpoDocenteCursoLato.setCursoLato(cursoLato);
			corpoDocenteCursoLato.setDocenteExterno(null);
			adicionarServidorCoordenacao(corpoDocenteCursoLato, coordCursoMov, cursoLato, dao);
			coordCursoMov.setSistema(Sistema.SIGAA);
			coordCursoMov.setUsuarioLogado(mov.getUsuarioLogado());
			
			if (cursoLato.getViceCoordenador().getId() != 0 && cursoLato.getViceCoordenadorAntigo().getId() != 0) 
				coordCursoMov.setCodMovimento(SigaaListaComando.SUBSTITUIR_COORDENADOR);	
			else {
				coordCursoMov.setCodMovimento(SigaaListaComando.IDENTIFICAR_COORDENADOR);
			}
			
			coordCursoMov.getCoordenador().setId(0);
			procCoordCurso.execute(coordCursoMov);
		} else {
			procCoordCurso = new ProcessadorCoordenacaoCurso();
			coordCursoMov.setSistema(Sistema.SIGAA);
			coordCursoMov.setUsuarioLogado(mov.getUsuarioLogado());
			coordCursoMov.setCoordenador(cursoLato.getViceCoordenador());
			coordCursoMov.setCodMovimento(SigaaListaComando.ALTERAR_COORDENADOR);
			procCoordCurso.execute(coordCursoMov);
		}
		
		carregarSecretarioAntigo(mov, cursoLato, dao);
		if (!(cursoLato.getSecretario().equals(cursoLato.getSecretarioAntigo()))) {
			procSecretaria = new ProcessadorSecretariaUnidade();
			SecretariaUnidadeMov movSec = new SecretariaUnidadeMov();
			movSec.setCodMovimento(SigaaListaComando.IDENTIFICAR_SECRETARIO);	

			cursoLato.getSecretario().setCurso(cursoLato);
			cursoLato.getSecretario().setUnidade(null);
			cursoLato.getSecretarioAntigo().setFim(new Date());
			movSec.setSecretario(cursoLato.getSecretario());
			movSec.setSecretarioAntigo(cursoLato.getSecretarioAntigo());
			movSec.setSistema(Sistema.SIGAA);
			movSec.setUsuarioLogado(mov.getUsuarioLogado());
			procSecretaria.execute(movSec);
		}
		
		corpoDocenteCursoLato = new CorpoDocenteCursoLato();
	}
	
	/**
	 * Responsável pela adição do servidor como Coordenador da Proposta de Curso Lato Sensu. 
	 * 
	 * @param corpoDocenteCursoLato
	 * @param mov
	 * @param cursoLato
	 * @param dao
	 * @throws DAOException
	 */
	public void adicionarServidorCoordenacao(CorpoDocenteCursoLato corpoDocenteCursoLato, CoordenacaoCursoMov mov, CursoLato cursoLato, GenericDAO dao) throws DAOException{
		if (corpoDocenteCursoLato.getServidor().getId() != 0) {
			CorpoDocenteCursoLatoDao corpoDocenteCursoLatoDao = getDAO(CorpoDocenteCursoLatoDao.class, mov);
			CorpoDocenteDisciplinaLatoDao corpoDocenteDisciplinaLatoDao = getDAO(CorpoDocenteDisciplinaLatoDao.class, mov); 
			CorpoDocenteCursoLato corpoDocenteOld = null;
			try {
				boolean temDisciplina = corpoDocenteDisciplinaLatoDao.validarDocenteMinistraDisciplina(cursoLato.getEquipeLato().getDocente(), cursoLato.getPropostaCurso());
				boolean jaCadastrado = corpoDocenteCursoLatoDao.validarDocenteDisciplina(corpoDocenteCursoLato.getServidor(), cursoLato);
				if (!temDisciplina) {
					corpoDocenteOld = corpoDocenteCursoLatoDao.findByDocenteCurso(cursoLato, cursoLato.getEquipeLato().getDocente().getId());
					if (corpoDocenteOld != null	
							&& corpoDocenteOld.getServidor().getId() != corpoDocenteCursoLato.getServidor().getId() && !temDisciplina) {
						dao.detach(corpoDocenteOld);
						cursoLato.setEquipeLato( new CorpoDocenteDisciplinaLato() );
					} else if (!jaCadastrado) {
						dao.create(corpoDocenteCursoLato);
					}	
				}else if (!jaCadastrado) 
					dao.create(corpoDocenteCursoLato);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				corpoDocenteCursoLatoDao.close();
				corpoDocenteDisciplinaLatoDao.close();
			}
		}
	}
	
	/**
	 * Responsável por carregar o antigo Secretário do Curso.
	 * 
	 * @param mov
	 * @param cursoLato
	 * @param dao
	 * @throws DAOException
	 */
	private void carregarSecretarioAntigo(Movimento mov, CursoLato cursoLato, GenericDAO dao) throws DAOException{
		SecretariaUnidadeDao secretariaDao = getDAO(SecretariaUnidadeDao.class, mov);
		try {
			Collection<SecretariaUnidade> secretariosCurso = secretariaDao.findByCurso(cursoLato.getId());
			for (SecretariaUnidade secretariaUnidade : secretariosCurso) {
				if (secretariaUnidade.isAtivo()) {
					cursoLato.setSecretarioAntigo( new SecretariaUnidade() );
					cursoLato.setSecretarioAntigo( secretariaUnidade );
					cursoLato.getSecretarioAntigo().getUsuario();
					cursoLato.getSecretarioAntigo().setUsuario(dao.findAndFetch(secretariaUnidade.getUsuario().getId(), 
							Usuario.class,"pessoaSigaa"));
					cursoLato.getSecretarioAntigo().getUsuario().setPessoa(dao.findByPrimaryKey(
							cursoLato.getSecretarioAntigo().getUsuario().getPessoa().getId(), Pessoa.class));
				}
			}
		} finally {
			secretariaDao.close();
		}
	}
	
	/**
	 * Cadastrar sempre o histórico das Alterações da Situação da proposta.
	 * 
	 * @param curso
	 * @param mov
	 * @throws DAOException
	 */
	private void cadastrarHistorico(CursoLato curso, Movimento mov) throws DAOException{
		HistoricoSituacao historicoSituacaoProposta = new HistoricoSituacao();
		historicoSituacaoProposta.setProposta(curso.getPropostaCurso());
		historicoSituacaoProposta.setUsuario(curso.getPropostaCurso().getUsuario());
		historicoSituacaoProposta.setSituacao(curso.getPropostaCurso().getSituacaoProposta());
		historicoSituacaoProposta.setDataCadastro(new Date());
		getGenericDAO(mov).create(historicoSituacaoProposta);
	}

	/**
	 * Método responsável pelo cadastro do processo Seletivo.
	 * 
	 * @param mov
	 * @param curso
	 * @param dao
	 * @param formaAvalicacaoDao
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private void cadastrarProcessoSeletivo(Movimento mov, CursoLato curso, FormaSelecaoPropostaDao dao, FormaAvaliacaoPropostaDao formaAvalicacaoDao) throws NegocioException, ArqException, RemoteException{
		try {
			Collection<FormaSelecaoProposta> formasSelecoes = dao.findByFormaSelecaoProposta(curso.getPropostaCurso().getId()); 
			FormaSelecaoProposta fsp = null;

			//Remover Forma de Seleção não mais presente.
			for (FormaSelecaoProposta formaSelecao : formasSelecoes) {
				if ( FormaSelecaoProposta.compareToRemocao(curso.getFormaSelecao(), formaSelecao.getFormaSelecao().getId()) ) {
					remove(formaSelecao, mov, dao);
				}
			}
			
			//Adiciona as Novas formas de Seleção Adicionadas.
			for (String formaSelecaoProposta : curso.getFormaSelecao()) {
				if ( FormaSelecaoProposta.compareTo(formaSelecaoProposta, formasSelecoes)) {
					fsp = new FormaSelecaoProposta();
					fsp.setProposta(curso.getPropostaCurso());
					fsp.setFormaSelecao(new FormaSelecao(Integer.parseInt(formaSelecaoProposta)));
					persist(fsp, mov, dao);
				}
			}
		} finally {
			dao.close();
		}

		try {
			Collection<FormaAvaliacaoProposta> formasAvaliacoes = formaAvalicacaoDao.findByFormaAvaliacaoProposta(curso.getPropostaCurso().getId()); 
			FormaAvaliacaoProposta fap = null;

			//Remove as Formas de Avaliação não mais presente.
			for (FormaAvaliacaoProposta formaAvaliacao : formasAvaliacoes) {
				if ( FormaAvaliacaoProposta.compareToRemocao(curso.getFormasAvaliacao(), formaAvaliacao.getFormaAvaliacao().getId()) ) {
					remove(formaAvaliacao, mov, dao);
				}
			}
			
			//Adiciona as Forma de Avaliação Adicionadas.
			for (String formaAvaliacaoProposta : curso.getFormasAvaliacao()) {
				if ( FormaAvaliacaoProposta.compareTo(formaAvaliacaoProposta, formasAvaliacoes)) {
					fap = new FormaAvaliacaoProposta();
					fap.setProposta(curso.getPropostaCurso());
					fap.setFormaAvaliacao(new FormaAvaliacao(Integer.parseInt(formaAvaliacaoProposta)));
					persist(fap, mov, dao);
				}
			}
		} finally {
			formaAvalicacaoDao.close();
		}
	
	}
	
	/**
	 * Responsável pela validação das informações do Curso Lato
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro movc = (MovimentoCadastro) mov;
		CursoLato curso = (CursoLato) movc.getObjMovimentado();
		
		if (curso.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_DADOS_GERAIS)) 
			CursoLatoValidator.validaDadosBasicos(curso, erros, getGenericDAO(mov));
		if (curso.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_COORDENACAO_CURSO)) 
			CursoLatoValidator.validaCoordenacaoCurso(curso, erros, getGenericDAO(mov));
		
	}

	/**
	 * Atualiza ou cria um novo objeto no banco dependendo se ele possui ou não id
	 * @param obj
	 * @throws DAOException
	 */
	private void persist(PersistDB obj, Movimento mov, GenericDAO dao) throws DAOException {
		if( obj.getId() > 0 )
			dao.update(obj);
		else
			dao.create(obj);
	}

	/**
	 * remove um objeto no banco
	 * @param obj
	 * @throws DAOException
	 */
	private void remove(PersistDB obj, Movimento mov, GenericDAO dao) throws DAOException {
		dao.remove(obj);
	}
	
}