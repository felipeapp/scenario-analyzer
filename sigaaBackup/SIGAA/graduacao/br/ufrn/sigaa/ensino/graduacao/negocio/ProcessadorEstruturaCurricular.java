/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 12/02/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.OptativaCurriculoSemestre;

/**
 * Processador para regras do cadastro de currículos de matrizes curriculares
 *
 * @author André
 *
 */
public class ProcessadorEstruturaCurricular extends ProcessadorCadastro {

	
	//Resolução do CONSEPE (Capítulo III, Art. 16)
	@SuppressWarnings("unused")
	private static final Float PERCENTUAL_MINIMO = new Float(0.2);
	
	
	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		validate(movimento);

		if (movimento.getCodMovimento().equals(SigaaListaComando.CADASTRAR_CURRICULO)) {
			return criar((MovimentoCadastro) movimento);
		} else if (movimento.getCodMovimento().equals(SigaaListaComando.ALTERAR_CURRICULO)) {
			determinarSituacao(movimento);
			return alterar((MovimentoCadastro) movimento);
		} else if (movimento.getCodMovimento().equals(SigaaListaComando.INATIVAR_ATIVAR_CURRICULO)) {
			return inativarOuAtivar(movimento);
		}


		return null;
	}

	/**
	 * Torna um currículo inativo.
	 */
	private Object inativarOuAtivar(Movimento movimento) throws DAOException {
		Curriculo c = (Curriculo) ((MovimentoCadastro) movimento).getObjMovimentado();
		
		getGenericDAO(movimento).updateField(Curriculo.class, c.getId(), "ativo", c.getAtivo());
		
		return c;
	}

	/**
	 * Determina a situação de um currículo, ou seja, se ele estará aberto ou fechado.
	 * Ver constantes: Curriculo.SITUACAO_ABERTO, Curriculo.SITUACAO_FECHADO
	 * 
	 * @see Curriculo
	 * @param movimento
	 */
	private void determinarSituacao(Movimento movimento) {
		Curriculo c = (Curriculo) ((MovimentoCadastro) movimento).getObjMovimentado();
		
		if (c.getCurso().getNivel() == NivelEnsino.GRADUACAO) {
			TreeSet<Integer> niveisPreenchidos = new TreeSet<Integer>();
			int chOptativaMinimaAdicionada = 0;
			for (CurriculoComponente cc : c.getCurriculoComponentes()) {
				if (!cc.getObrigatoria())
					chOptativaMinimaAdicionada += cc.getComponente().getChTotal();
				niveisPreenchidos.add(cc.getSemestreOferta());
			}
		
			boolean todosNiveisPreenchidos = true;
			for (int i = 1; i <= c.getSemestreConclusaoIdeal(); i++) {
				if (!niveisPreenchidos.contains(i)) {
					todosNiveisPreenchidos = false;
					break;
				}
			}
			if (chOptativaMinimaAdicionada >= c.getChOptativasMinima() && todosNiveisPreenchidos) {
				c.setSituacao(Curriculo.SITUACAO_FECHADO);
			}
		}

	}

	/**
	 * Persiste um currículo;
	 */
	@Override
	protected Object criar(MovimentoCadastro mov) throws NegocioException, ArqException {
		Curriculo c = (Curriculo) (mov).getObjMovimentado();
		boolean ativo = new Boolean(c.getAtivo());

		atualizaMesesStricto(c);
		c.setSituacao(Curriculo.SITUACAO_ABERTO);
		CurriculoHelper.calcularTotaisCurriculo(c);
		
		Object o = super.criar(mov);
		
		//inativa a estrutura curricular caso selecionado 
		//no formulário de cadastro o valor ativo = não
		if(!ativo){
			c.setAtivo(ativo);
			inativarOuAtivar(mov);
		}
		
		criarOptativasCurriculoSemestre(mov);
		
		return o;
	}

	/**
	 * Persiste as informações de qual a carga horária de optativas que
	 * os alunos deveriam cursar em cada nível do currículo.
	 */
	@SuppressWarnings("unchecked")
	private void criarOptativasCurriculoSemestre(MovimentoCadastro mov) throws DAOException {
		Curriculo c = mov.getObjMovimentado();
		if (c.isGraduacao() && c.getAtivo()) {
			GenericDAO dao = null;
			try {
				dao = getGenericDAO(mov);
				List<OptativaCurriculoSemestre> opts = (List<OptativaCurriculoSemestre>) mov.getColObjMovimentado();
				for (OptativaCurriculoSemestre o : opts) {
					dao.createOrUpdate(o);
				}
			} finally {
				if (dao != null)
					dao.close();
			}
		}
	}

	/**
	 * Atualiza os meses de conclusão de um currículo stricto sensu.
	 */
	private void atualizaMesesStricto(Curriculo c) {
		if (NivelEnsino.isAlgumNivelStricto(c.getCurso().getNivel())) {
			c.setMesesConclusaoIdeal(c.getSemestreConclusaoIdeal());
			c.setMesesConclusaoMaximo(c.getSemestreConclusaoMaximo());
			c.setMesesConclusaoMinimo(c.getSemestreConclusaoMinimo());
		}
	}

	/**
	 * Valida o currículo que vai ser cadastrado.
	 * JSP: Não é chamado. 
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		Curriculo c = (Curriculo) ((MovimentoCadastro) mov).getObjMovimentado();
		
		if (    ( c.isGraduacao() && c.getChTotalMinima() < 0
				|| c.getChAAE() < 0 || c.getChAtividadeObrigatoria() < 0
				|| c.getChNaoAtividadeObrigatoria() < 0 || c.getChOptativasMinima() < 0
				|| c.getChPraticos() < 0 || c.getChTeoricos() < 0) ) {
			throw new NegocioException("A carga horária não pode ser negativa");
		}

	}
	
	/**
	 * Checa se o anoEntradaVigor é maior que a constante persistida em 
	 * DB sistemas_com , schema public, table parametros
	 * @param anoEntrada
	 * @return
	 * @throws ArqException
	 */
	@SuppressWarnings("unused")
	private boolean checkAnoEntradaVigor(Integer anoEntrada) throws ArqException{
		
		//Ano limite segundo resolução do CONSEPE
		int anoResolucaoConsepe = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.ANO_RESOLUCAO_CONSEPE);	
		return (anoEntrada == null || anoEntrada >= anoResolucaoConsepe);
				
	}

	/**
	 * Verifica se um currículo possui um determinado 
	 * CurriculoComponente em sua estrutura.
	 */
	private boolean temCurriculoComponente(Curriculo c, CurriculoComponente cc) {
		for (CurriculoComponente cur : c.getCurriculoComponentes()) {
			if (cur.getId() == cc.getId())
				return true;
		}
		return false;
	}

	/**
	 * Altera um currículo.
	 */
	@Override
	protected Object alterar(MovimentoCadastro mov) throws NegocioException, ArqException {
		EstruturaCurricularDao dao = null;
		try {
			// remove os componentes que estão no banco e não estão no currículo
			// alterado
			Curriculo c = (Curriculo) (mov).getObjMovimentado();
			atualizaMesesStricto(c);
			// se for alterado a lista de componente ou então houver alguma mudança entre as disciplinas obrigatórias ou optativas 
			//  será necessário zerar as integralizações de todos os discentes associados a este currículo. 
			//  Este atributo é utilizado para verificar se houve alguma alteração neste sentido.
			boolean zerarIntegralizacoesDiscentes = false;

			dao = getDAO(EstruturaCurricularDao.class, mov);
			List<CurriculoComponente> doBanco = (List<CurriculoComponente>) dao.findCurriculoComponentesByCurriculo(c.getId());
			Collection<CurriculoComponente> praRemover = new ArrayList<CurriculoComponente>(0);
			for (CurriculoComponente cc : doBanco) {
				if (!temCurriculoComponente(c, cc)) {
					zerarIntegralizacoesDiscentes = true;
					praRemover.add(cc);
				}
			}
			for (CurriculoComponente cc : praRemover) {
				CurriculoComponenteValidator.verificarGrupoOptativa(cc);
				dao.remove(cc);
				dao.detach(cc);
			}
			int chObrigatoria = c.getChTotalMinima();
			int chOptativas = c.getChOptativasMinima();
			//calcularTotais(c, mov);
			CurriculoHelper.calcularTotaisCurriculo(c);
			
			criarOptativasCurriculoSemestre(mov);
			
			if( c.getCurso().getNivel() != NivelEnsino.GRADUACAO ){
				zerarIntegralizacoesDiscentes = false;
			}else if( !zerarIntegralizacoesDiscentes ) {
				// se não tiver nenhum componente pra remover do currículo tem que verificar se tem algum pra adicionar
				// se não tiver nenhum pra adicionar tem que verificar se algum foi atualizado
				// se qualquer uma destas condições ocorrer as integralizações dos discentes devem ser zeradas 
				for( CurriculoComponente cc : c.getCurriculoComponentes() ){
					if( cc.getId() == 0 ){
						zerarIntegralizacoesDiscentes = true;
						break;
					}
				}
				
				// se não tiver nenhum pra adicionar tem que verificar se algum foi atualizado 
				if( !zerarIntegralizacoesDiscentes ){
					
					for( CurriculoComponente cc : c.getCurriculoComponentes() ){
						
						int posicao = doBanco.indexOf(cc);
						if( posicao >= 0 ){
							if( !doBanco.get(posicao).getObrigatoria().equals( cc.getObrigatoria() ) ){
								zerarIntegralizacoesDiscentes = true;
								break;
							}
						}
						
					}
				}
			}
			
			
			// se a carga horária do currículo for alterada, deve zerar a data da última de atualização de 
			// todos os alunos associados a este currículo
			if ( ((c.getChTotalMinima() != chObrigatoria || c.getChOptativasMinima() != chOptativas) || zerarIntegralizacoesDiscentes)
					&& c.getCurso().getNivel() == NivelEnsino.GRADUACAO) {
				DiscenteGraduacaoDao discenteDao = null;
				try {
					discenteDao = getDAO(DiscenteGraduacaoDao.class, mov);
					discenteDao.zerarUltimasAtualizacoes(c);
				} finally {
					if (discenteDao != null)
						discenteDao.close();
				}
			}
			
			if( zerarIntegralizacoesDiscentes )
				zerarIntegralizacoesDiscentes(c, mov);
			
			UFRNUtils.anularAtributosVazios(mov.getObjMovimentado(), "tccDefinitivo");
			
			return super.alterar(mov);
		} finally {
			if (dao != null)
				dao.close();
		}
		
	}

	/**
	 * zera as integralizações dos discentes associados ao currículo
	 * @param curriculo
	 * @param mov
	 * @throws DAOException 
	 */
	private void zerarIntegralizacoesDiscentes( Curriculo curriculo, Movimento mov ) throws DAOException {
		MatriculaComponenteDao dao = null;
		try {
			dao = getDAO(MatriculaComponenteDao.class, mov);
			dao.zerarTipoIntegralizacoes(curriculo);
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	
	
}
