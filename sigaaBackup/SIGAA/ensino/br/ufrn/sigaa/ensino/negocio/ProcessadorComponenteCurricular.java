/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 14/01/2008
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.exception.ConstraintViolationException;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.AlteracaoAtivacaoComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.AlteracaoAtivacaoComponente;
import br.ufrn.sigaa.ensino.dominio.AlteracaoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricularPrograma;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoRecalculoEstruturaCurricular;
import br.ufrn.sigaa.ensino.graduacao.negocio.ProcessadorRecalculoEstruturasCurriculares;
import br.ufrn.sigaa.ensino.latosensu.dominio.CorpoDocenteDisciplinaLato;
import br.ufrn.sigaa.ensino.negocio.dominio.ComponenteCurricularMov;

/**
 * Processador responsável pelas realização das regras de negócio referentes à
 * persistência de componentes curriculares
 * @author Andre M Dantas
 * @author Victor Hugo
 */
public class ProcessadorComponenteCurricular extends ProcessadorCadastro {

	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		
		if (movimento.getCodMovimento().equals(SigaaListaComando.ALTERAR_COMPONENTE_CURRICULAR)) {
			validate(movimento);
			return alterar((MovimentoCadastro) movimento);
		} else if (movimento.getCodMovimento().equals(SigaaListaComando.CADASTRAR_COMPONENTE_CURRICULAR)) {
			validate(movimento);
			return criar((MovimentoCadastro) movimento);
		} else if (movimento.getCodMovimento().equals(SigaaListaComando.REMOVER_COMPONENTE_CURRICULAR)) {
			validateRemover((MovimentoCadastro) movimento);
			return remover((MovimentoCadastro) movimento);
		} else if (movimento.getCodMovimento().equals(SigaaListaComando.NEGAR_CADASTRO_COMPONENTE)) {
			return negarCadastro((MovimentoCadastro) movimento);
		} else if (movimento.getCodMovimento().equals(SigaaListaComando.AUTORIZAR_CADASTRO_COMPONENTE)) {
			return autorizarCadastro((MovimentoCadastro) movimento);
		} else if (movimento.getCodMovimento().equals(SigaaListaComando.DESATIVAR_COMPONENTE_CURRICULAR)) {
			return desativar((MovimentoCadastro) movimento);
		} else if (movimento.getCodMovimento().equals(SigaaListaComando.ATIVAR_DESATIVAR_COMPONENTE_DETALHE)) {
				return ativarDesativarComponenteDetalhe((MovimentoCadastro) movimento);
		}

		return super.execute(movimento);
	}
	
	/**
	 * Persiste as subunidades dos blocos
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException 
	 */
	private ComponenteDetalhes ativarDesativarComponenteDetalhe(MovimentoCadastro mov) throws NegocioException, ArqException {
		ComponenteDetalhes det = (ComponenteDetalhes)mov.getObjMovimentado();		
		getGenericDAO(mov).updateField(ComponenteDetalhes.class, det.getId(), "desconsiderarEquivalencia", det.isDesconsiderarEquivalencia());
		getGenericDAO(mov).updateField(ComponenteDetalhes.class, det.getId(), "equivalenciaValidaAte", det.getEquivalenciaValidaAte());
		det = getGenericDAO(mov).findByPrimaryKey(det.getId(), ComponenteDetalhes.class);
		return det;
	}

	/** Valida os dados para o cadastramento do componente curricular.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ComponenteCurricularMov ccMov =(ComponenteCurricularMov) mov;
		ComponenteCurricular cc = (ComponenteCurricular) ccMov.getObjMovimentado();
		// se ja existe um CC com esse código
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class, mov);
		try{
			boolean jaExiste = dao.jaExisteCodigo(cc.getId(), cc.getCodigo(), cc.getUnidade().getId(), cc.getNivel());
			
			if (jaExiste	) {
				NegocioException e = new NegocioException();
				e.addErro("O componente curricular não pode ser cadastrado.<br>" +
						"Já existe um componente com esse código.");
				throw e;
			}
		} finally {
			if (dao != null) dao.close();
		}

	}

	/**
	 * Anula os atributos transients
	 * @param cc
	 */
	private void checkNulls(ComponenteCurricular cc) {
		if ( cc.getTipoComponente().getId() != TipoComponenteCurricular.ATIVIDADE && !cc.isPassivelTipoAtividade() ) {
			cc.setTipoAtividade(null);
			cc.setFormaParticipacao(null);
			cc.setTipoAtividadeComplementar(null);
		} else {
			if (!ValidatorUtil.isEmpty(cc.getTipoAtividade()) && cc.getTipoAtividade().getId() != TipoAtividade.COMPLEMENTAR)
				cc.setTipoAtividadeComplementar(null);
		}

		if (cc.getEquivalencia() != null && cc.getEquivalencia().trim().equals(""))
			cc.setEquivalencia(null);
		if (cc.getPreRequisito() != null && cc.getPreRequisito().trim().equals(""))
			cc.setPreRequisito(null);
		if (cc.getCoRequisito() != null && cc.getCoRequisito().trim().equals(""))
			cc.setCoRequisito(null);

	}

	/**
	 * Gera o código do componente com base na unidade do mesmo
	 * @param mov
	 * @throws DAOException
	 */
	private void gerarCodigo(ComponenteCurricularMov mov) throws DAOException {
		ComponenteCurricular cc = (ComponenteCurricular) mov.getObjMovimentado();
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class, mov);
		
		try{
			Unidade unidade = dao.findByPrimaryKey(cc.getUnidade().getId(), Unidade.class);
			String sigla = unidade.getSiglaAcademica();
			ComponenteCurricular ultimo = dao.findUltimoCodigo(cc.getUnidade().getId(), cc.getNivel());
	
			Integer maxCodigo = null;
			if (ultimo != null)
				maxCodigo = StringUtils.extractInteger(ultimo.getCodigo());
			else
				maxCodigo = 0;
			maxCodigo++;
			sigla = sigla.substring(0, 3);
	
			/* As unidades da Escola de Enfermagem, Escola de Música e Escola Agrícola de Jundiaí tem componentes tanto pra técnico como
			 * para graduação. Como não há uma unidade pra cada nível, foi preciso especificar no código a sigla dos componentes no caso do
			 * ensino técnico.
			 */
			if (cc.getNivel() == NivelEnsino.TECNICO) {
				if (cc.getUnidade().getId() == 284) // Música
					sigla = "MUT";
				else if (cc.getUnidade().getId() == 205) // ENFERMAGEM
					sigla = "EEN";
				else if (cc.getUnidade().getId() == 351) // Jundiaí
	                sigla = "EAJ";
	
			}
	
			cc.setCodigo(sigla + UFRNUtils.completaZeros(maxCodigo, 4));
		} finally{
			dao.close();
		}
	}

	/**
	 * Método responsável pela criação e persistência dos objetos de ComponenteCurricular e ComponenteDetalhes.
	 */
	@Override
	protected Object criar(MovimentoCadastro mov) throws NegocioException, ArqException {
		ComponenteCurricular cc = (ComponenteCurricular) mov.getObjMovimentado();

		if (mov.getAcao() == ComponenteCurricularMov.ACAO_SOLICITAR_CADASTRO) {
			cc.setStatusInativo(ComponenteCurricular.AGUARDANDO_CONFIRMACAO);
		}
		checkNulls(cc);
		
		ComponenteCurricularTotaisHelper.calcularTotais(cc);
		
		// validação de quantidade de horas (Componentes do tipo ATIVIDADE podem ser cadastrados SEM carga horária)
		if (cc.getChTotal() <= 0 && cc.getTipoComponente().getId() != TipoComponenteCurricular.ATIVIDADE) {
			NegocioException e = new NegocioException();
			e.addErro("Total de carga horária inválida.");
			throw e;
		}
		if (cc.getCodigo() == null || cc.getCodigo().trim().equals(""))
			gerarCodigo((ComponenteCurricularMov) mov);
		cc.getDetalhes().setCodigo(cc.getCodigo());
		
		GenericDAO dao = getGenericDAO(mov);
		try{
			cc.getDetalhes().setComponente(cc.getId());
			cc.getDetalhes().setTipoComponente( cc.getTipoComponente().getId() );
			cc.getDetalhes().setAceitaSubturma( cc.isAceitaSubturma() );
			if (cc.getPrograma() != null)
				cc.getPrograma().setComponenteCurricular(cc);
	
			ComponenteDetalhes det = cc.getDetalhes();
			cc.setDetalhes(null);
			dao.create(cc);
			if (mov.getAcao() == ComponenteCurricularMov.ACAO_SOLICITAR_CADASTRO) {
				cc.setAtivo(false);
				dao.updateField(ComponenteCurricular.class, cc.getId(), "ativo", false);
			}
			cc.setDetalhes(det);
			det.setComponente(cc.getId());
			det.setTipoComponente( cc.getTipoComponente().getId() );
			dao.create(det);
	
			if (cc.isBloco()) {
				persistirSubUnidades(mov, dao);
			}
		} finally{
			dao.close();
		}	
		return cc;
	}

	/**
	 * Persiste as subunidades dos blocos
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException 
	 */
	private void persistirSubUnidades(MovimentoCadastro mov, GenericDAO dao) throws NegocioException, ArqException {
		ComponenteCurricular cc = (ComponenteCurricular) mov.getObjMovimentado();
		for (int i = 0; i < cc.getSubUnidades().size() ;i++) {
			// gerando código para as subunidades
			ComponenteCurricular subUnidade = cc.getSubUnidades().get(i);
			subUnidade.setBlocoSubUnidade(cc);
			subUnidade.setAtivo( cc.isAtivo() );
			subUnidade.setStatusInativo( cc.getStatusInativo() );

			// copiando para as subunidades os pre-requisitos, co-requisitos e equivalências do bloco 
			subUnidade.setCoRequisito( cc.getCoRequisito() );
			subUnidade.setPreRequisito( cc.getPreRequisito() );
			subUnidade.setEquivalencia( cc.getEquivalencia() );
			subUnidade.getDetalhes().setTipoComponente( subUnidade.getTipoComponente().getId() );
			subUnidade.getDetalhes().setCoRequisito( cc.getDetalhes().getCoRequisito() );
			subUnidade.getDetalhes().setPreRequisito( cc.getDetalhes().getPreRequisito() );
			subUnidade.getDetalhes().setEquivalencia( cc.getDetalhes().getEquivalencia() );
			subUnidade.setFormaParticipacao(cc.getFormaParticipacao());
			
			if (isEmpty(subUnidade.getCodigo())) {
				subUnidade.setCodigo(cc.getCodigo()+"." + i);
			} else {
				if (!subUnidade.getCodigo().contains("."))
					throw new NegocioException("O código da subunidade " + subUnidade.getCodigo() + " não está no padrão (separado por ponto).");
			}
			//Transiente
			subUnidade.getDetalhes().setCodigo(subUnidade.getCodigo());
			subUnidade.getDetalhes().setComponente(subUnidade.getId());
			subUnidade.setSubUnidades(null);
			ComponenteDetalhes det = subUnidade.getDetalhes();
			subUnidade.setDetalhes(null);
			if (subUnidade.getId() == 0) {
				dao.create(subUnidade);
				subUnidade.setDetalhes(det);
				det.setComponente(subUnidade.getId());
				dao.create(det);
			} else {
				dao.update(subUnidade);
				subUnidade.setDetalhes(det);
				det.setComponente(subUnidade.getId());
				dao.update(det);
			}
		}

	}

	/** Remove do banco as subunidades de um componente curricular que foram removidas durante o cadastro do mesmo.
	 * @param mov
	 * @param todos
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private void removerSubUnidadesRetiradas(MovimentoCadastro mov, boolean todos) throws DAOException {
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class, mov);
		Collection<ComponenteCurricular> praRemover = null;
		ComponenteCurricular cc = (ComponenteCurricular) mov.getObjMovimentado();
		try{
			if (todos) {
				praRemover = cc.getSubUnidades();
			} else {
				ComponenteCurricular ccBD = dao.findByPrimaryKey(cc.getId(), ComponenteCurricular.class);
				praRemover = CollectionUtils.subtract(ccBD.getSubUnidades(), cc.getSubUnidades());
				dao.detach(ccBD);
			}
			for (ComponenteCurricular subu : praRemover) {
				removerDetalhesCadastrados(subu, dao);
				dao.remove(subu);
			}
		}finally{	
			dao.close();
		}
	}


	/** Altera o componente curricular.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#alterar(br.ufrn.arq.dominio.MovimentoCadastro)
	 */
	@Override
	protected Object alterar(MovimentoCadastro mov) throws NegocioException, ArqException {
		ComponenteCurricular cc = (ComponenteCurricular) mov.getObjMovimentado();
		alterarComponenteDados(cc, mov);
		return cc;
	}

	/**
	 * Altera os dados do componente informado
	 * @param cc
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void alterarComponenteDados(ComponenteCurricular cc, MovimentoCadastro mov) throws NegocioException, ArqException {
		checkNulls(cc);
		if (mov.getAcao() == ComponenteCurricularMov.ACAO_SOLICITAR_CADASTRO) {
			cc.setStatusInativo(ComponenteCurricular.AGUARDANDO_CONFIRMACAO);
		}
		
		ComponenteCurricularTotaisHelper.calcularTotais(cc);
		
		cc.getDetalhes().setCodigo(cc.getCodigo());
		cc.getDetalhes().setTipoComponente( cc.getTipoComponente().getId() );
		cc.getDetalhes().setAceitaSubturma( cc.isAceitaSubturma() );

		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class , mov);
		EstruturaCurricularDao daoEstrutura = getDAO(EstruturaCurricularDao.class, mov);
		ComponenteCurricular ccBD = dao.findByPrimaryKey(cc.getId(), ComponenteCurricular.class);
		try {

			// Não permitir a alteração do código do componente
			cc.setCodigo(ccBD.getCodigo());
			cc.getDetalhes().setCodigo(ccBD.getDetalhes().getCodigo());
			
			// Alterar Status do componente, considerando sua ativação.
			if (ccBD.isAtivo() && !cc.isAtivo())
				desativar(mov);
			else if (!ccBD.isAtivo() && cc.isAtivo())
				autorizarCadastro(mov);
			
			// verificando se tentou aumentar carga horária
			if (cc.getDetalhes().getChTotal() > ccBD.getDetalhes().getChTotal()) {
				long totalJaMatriculados = dao.findTotalJaMatriculadosByComponente(cc.getId());
				if (totalJaMatriculados > 0 && cc.getNivel() != NivelEnsino.RESIDENCIA) {
					NegocioException e = new NegocioException();
					e.addErro("Não é permitido aumentar a carga horária desse componente, pois " +
							"existem alunos com matrículas nele.");
					throw e;
				}
			}

			// verificando detalhes
			if (!cc.getDetalhes().equalsToUpdate(ccBD.getDetalhes())) {
				ComponenteDetalhes detalhes = cc.getDetalhes().clone();
				detalhes.setId(0);
				detalhes.setData(new Date());
				detalhes.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
				if( isEmpty( detalhes.getTipoComponente() ) )
					detalhes.setTipoComponente( cc.getTipoComponente().getId() );
				dao.detach(cc.getDetalhes());
				cc.setDetalhes(detalhes);
			}
			// verificando programa
			if (cc.getPrograma() != null && !cc.getPrograma().equalsToUpdate(ccBD.getPrograma())) {
				ComponenteCurricularPrograma programa = cc.getPrograma().clone();
				programa.setId(0);
				dao.detach(cc.getPrograma());
				cc.setPrograma(programa);
			}

			if (cc.isBloco()) {
				// atualizando as subunidades do bloco
				persistirSubUnidades(mov, dao);
			}

			// removendo subunidades que foram retiradas da coleção
			if (cc.isBloco())
				removerSubUnidadesRetiradas(mov, false);
			// removendo TODAS as subunidades se o componente deixou de ser bloco
			else if (cc.getSubUnidades() != null && cc.getSubUnidades().size() > 0)
				removerSubUnidadesRetiradas(mov, true);

			/**
			 * Recalculando os totais dos currículos que estão relacionados com o componente alterado.
			 * O recalculo dos currículos só é executado caso a carga horária ou alguma das expressões do componente seja alterada 
			 */
			if (!cc.getDetalhes().equalsToCalculoCurriculo(ccBD.getDetalhes())) {
				
				Collection<Integer> curriculosRecalcular = daoEstrutura.findAtivosByComponente( cc.getId() );
				
				MovimentoRecalculoEstruturaCurricular movRecalculo = new MovimentoRecalculoEstruturaCurricular();
				movRecalculo.setCodMovimento(SigaaListaComando.RECALCULAR_ESTRUTURA_CURRICULAR);
				movRecalculo.setUsuarioLogado(mov.getUsuarioLogado());
				movRecalculo.setSistema(mov.getSistema());
				movRecalculo.setSubsistema(mov.getSubsistema());
				movRecalculo.setUsuario( mov.getUsuario() );
				movRecalculo.setRecalcularDiscentes(true);
				ProcessadorRecalculoEstruturasCurriculares processadorRecalculo = new ProcessadorRecalculoEstruturasCurriculares();
				
				for( Integer idCurriculo : curriculosRecalcular ){
					movRecalculo.setId(idCurriculo);
					processadorRecalculo.execute(movRecalculo);
				}
				
			}
			
			dao.detach(ccBD);
			dao.update(cc);

		} catch (NegocioException ne) {
			throw ne;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new NegocioException(e);
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new NegocioException(e);
		} finally {
			if ( daoEstrutura != null ) daoEstrutura.close();
			if ( dao != null ) dao.close();
		}

	}

	/** Remove o componente curricular.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#remover(br.ufrn.arq.dominio.MovimentoCadastro)
	 */
	@Override
	protected Object remover(MovimentoCadastro mov) throws DAOException, NegocioException {
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class, mov);
		ComponenteCurricular cc = (ComponenteCurricular) mov.getObjMovimentado();
		cc = dao.findByPrimaryKey(cc.getId(), ComponenteCurricular.class);
		
		removerHistoricoAtivicao(mov, cc);
		
		Collection<ComponenteCurricularPrograma> programas = dao.findByExactField(
				ComponenteCurricularPrograma.class, "componenteCurricular.id", cc.getId());
		if (programas != null) {
			for (ComponenteCurricularPrograma programa : programas) {
				if (programa.getId() != cc.getPrograma().getId())
					dao.remove(programa);
			}
		}
		try {
			removerDetalhesCadastrados(cc, dao);
	
			// adaptação para o caso da remoção de uma disciplina lato sensu
			if (cc.getNivel() == 'L') {
				Collection<CorpoDocenteDisciplinaLato> equipesLato = dao.findByExactField(CorpoDocenteDisciplinaLato.class, "disciplina.id", cc
						.getId());
				for (CorpoDocenteDisciplinaLato equipe : equipesLato)
					dao.remove(equipe);
			}
	
			if (cc.isBloco()) {
				removerSubUnidadesRetiradas(mov, true);
			}
	
			// removendo alterações desse componente
			Collection<AlteracaoComponenteCurricular> alteracoes = dao.findByExactField(
					AlteracaoComponenteCurricular.class, "componenteCurricular.id", cc.getId());
			for (AlteracaoComponenteCurricular alt : alteracoes) {
				dao.remove(alt);
			}
	
			// só remove esse CC caso ele não tenha mais nenhum outro registro de
			// outra entidade associado a ele.
			dao.remove(cc);
			
		} catch (DAOException e) {
			if (e.getCause() instanceof ConstraintViolationException) {
				NegocioException ne = new NegocioException();
				ne.addErro("Não é possível remover este componente curricular pois existem referências a ele no sistema.");
				throw ne;
			} else {
				throw e;
			}
		} finally {
			dao.close();
		}


		return cc;
	}

	/**
	 * Quando o componente é solicitado e depois é aprovado ou negado, um histórico é gerado. 
	 * É necessário remover o histórico para não violar a chave estrangeira quando o componente for apagado.
	 * 
	 * @param mov
	 * @param cc
	 * @throws DAOException
	 */
	private void removerHistoricoAtivicao(MovimentoCadastro mov, ComponenteCurricular cc)
			throws DAOException {
		AlteracaoAtivacaoComponenteDao ativacaoComponenteDao = getDAO(AlteracaoAtivacaoComponenteDao.class, mov);
		try {
			AlteracaoAtivacaoComponente ativacaoComponente = ativacaoComponenteDao.findByComponenteCurricular(cc.getId());
			if (ativacaoComponente != null)
				ativacaoComponenteDao.remove(ativacaoComponente);
		} finally {
			ativacaoComponenteDao.close();
		}	
	}
	
	/**
	 * Não pode remover se o componente estiver em alguma expressão de equivalência, pré-requisito ou co-requisito
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	private void validateRemover(MovimentoCadastro mov) throws DAOException, NegocioException{
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class, mov);
		ComponenteCurricular cc = (ComponenteCurricular) mov.getObjMovimentado();
		try{
			List<ComponenteCurricular> componentes = dao.findComponentesReferenciamExpressao(cc.getId());
			if( !isEmpty( componentes ) ){
				StringBuilder msg = new StringBuilder( "Não é possível remover este componente curricular pois o(s) seguinte(s) componente(s) o referenciam em suas expressões de equivalência, co-requisito ou pré-requisito: <br/>" );
				for( ComponenteCurricular c : componentes ){
					msg.append( "&nbsp;&nbsp;&nbsp;" +  c.toString() + " <br/> ");
				}
				NegocioException e = new NegocioException();
				e.addErro(msg.toString());
				throw e;
			}
		} finally {
			dao.close();
		}
		
	}

	/**
	 * Remove os detalhes do componente informado
	 * @param cc
	 * @throws DAOException
	 */
	private void removerDetalhesCadastrados(ComponenteCurricular cc, ComponenteCurricularDao dao) throws DAOException {
		cc.setDetalhes(null);
		Collection<ComponenteDetalhes> detalhes = dao.findByExactField(ComponenteDetalhes.class, "componente", cc.getId());
		for (ComponenteDetalhes detalhe : detalhes) {
				dao.remove(detalhe);
		}

	}

	/**
	 * Nega o cadastro do componente, este passar a NÃO ser visível nas consultas do sistema
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private Object negarCadastro(MovimentoCadastro mov) throws DAOException  {
		ComponenteCurricular cc = (ComponenteCurricular) mov.getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);
		try {
			cc.setStatusInativo(ComponenteCurricular.CADASTRO_NEGADO);
			cc.setAtivo(false);
			dao.update(cc);
			
			AlteracaoAtivacaoComponente alt = new AlteracaoAtivacaoComponente(cc, mov.getUsuarioLogado().getRegistroEntrada(), new Date());
			alt.setAntesDepois(dao.findByPrimaryKey(cc.getId(), ComponenteCurricular.class), cc);
			alt.setObservacao(((ComponenteCurricularMov) mov).getObservacaoCadastro());
			dao.create(alt);
		} finally {
			dao.close();
		}
		return cc;
	}

	/**
	 * Autoriza o cadastro de um componente, este passar a ser visível nas consultas do sistema
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private Object autorizarCadastro(MovimentoCadastro mov) throws DAOException  {
		ComponenteCurricular cc = (ComponenteCurricular) mov.getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);
		try {
			cc.setStatusInativo(ComponenteCurricular.COMPONENTE_ATIVO);
			cc.setAtivo(true);
			dao.update(cc);
			
			AlteracaoAtivacaoComponente alt = new AlteracaoAtivacaoComponente(cc, mov.getUsuarioLogado().getRegistroEntrada(), new Date());
			alt.setAntesDepois(dao.findByPrimaryKey(cc.getId(), ComponenteCurricular.class), cc);
			alt.setObservacao(((ComponenteCurricularMov) mov).getObservacaoCadastro());
			dao.create(alt);
		} finally {
			dao.close();
		}
		return cc;
	}

	/**
	 * Método responsável pela operação de desativação de um componente Curricular.
	 */
	@Override
	protected Object desativar(MovimentoCadastro mov) throws DAOException  {
		ComponenteCurricular cc = (ComponenteCurricular) mov.getObjMovimentado();
		cc.setAtivo(false);
		cc.setStatusInativo(ComponenteCurricular.DESATIVADO);
		GenericDAO dao = getGenericDAO(mov);
		try {
			dao.update(cc);
			
			AlteracaoAtivacaoComponente alt = new AlteracaoAtivacaoComponente(cc, mov.getUsuarioLogado().getRegistroEntrada(), new Date());
			alt.setAntesDepois(dao.findByPrimaryKey(cc.getId(), ComponenteCurricular.class), cc);
			dao.create(alt);
		} finally {
			dao.close();
		}
		return cc;
	}

}
