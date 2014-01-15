/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Processador para regras do cadastro de curr�culos de matrizes curriculares
 *
 * @author Andr�
 *
 */
public class ProcessadorEstruturaCurricular extends ProcessadorCadastro {

	
	//Resolu��o do CONSEPE (Cap�tulo III, Art. 16)
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
	 * Torna um curr�culo inativo.
	 */
	private Object inativarOuAtivar(Movimento movimento) throws DAOException {
		Curriculo c = (Curriculo) ((MovimentoCadastro) movimento).getObjMovimentado();
		
		getGenericDAO(movimento).updateField(Curriculo.class, c.getId(), "ativo", c.getAtivo());
		
		return c;
	}

	/**
	 * Determina a situa��o de um curr�culo, ou seja, se ele estar� aberto ou fechado.
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
	 * Persiste um curr�culo;
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
		//no formul�rio de cadastro o valor ativo = n�o
		if(!ativo){
			c.setAtivo(ativo);
			inativarOuAtivar(mov);
		}
		
		criarOptativasCurriculoSemestre(mov);
		
		return o;
	}

	/**
	 * Persiste as informa��es de qual a carga hor�ria de optativas que
	 * os alunos deveriam cursar em cada n�vel do curr�culo.
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
	 * Atualiza os meses de conclus�o de um curr�culo stricto sensu.
	 */
	private void atualizaMesesStricto(Curriculo c) {
		if (NivelEnsino.isAlgumNivelStricto(c.getCurso().getNivel())) {
			c.setMesesConclusaoIdeal(c.getSemestreConclusaoIdeal());
			c.setMesesConclusaoMaximo(c.getSemestreConclusaoMaximo());
			c.setMesesConclusaoMinimo(c.getSemestreConclusaoMinimo());
		}
	}

	/**
	 * Valida o curr�culo que vai ser cadastrado.
	 * JSP: N�o � chamado. 
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		Curriculo c = (Curriculo) ((MovimentoCadastro) mov).getObjMovimentado();
		
		if (    ( c.isGraduacao() && c.getChTotalMinima() < 0
				|| c.getChAAE() < 0 || c.getChAtividadeObrigatoria() < 0
				|| c.getChNaoAtividadeObrigatoria() < 0 || c.getChOptativasMinima() < 0
				|| c.getChPraticos() < 0 || c.getChTeoricos() < 0) ) {
			throw new NegocioException("A carga hor�ria n�o pode ser negativa");
		}

	}
	
	/**
	 * Checa se o anoEntradaVigor � maior que a constante persistida em 
	 * DB sistemas_com , schema public, table parametros
	 * @param anoEntrada
	 * @return
	 * @throws ArqException
	 */
	@SuppressWarnings("unused")
	private boolean checkAnoEntradaVigor(Integer anoEntrada) throws ArqException{
		
		//Ano limite segundo resolu��o do CONSEPE
		int anoResolucaoConsepe = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.ANO_RESOLUCAO_CONSEPE);	
		return (anoEntrada == null || anoEntrada >= anoResolucaoConsepe);
				
	}

	/**
	 * Verifica se um curr�culo possui um determinado 
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
	 * Altera um curr�culo.
	 */
	@Override
	protected Object alterar(MovimentoCadastro mov) throws NegocioException, ArqException {
		EstruturaCurricularDao dao = null;
		try {
			// remove os componentes que est�o no banco e n�o est�o no curr�culo
			// alterado
			Curriculo c = (Curriculo) (mov).getObjMovimentado();
			atualizaMesesStricto(c);
			// se for alterado a lista de componente ou ent�o houver alguma mudan�a entre as disciplinas obrigat�rias ou optativas 
			//  ser� necess�rio zerar as integraliza��es de todos os discentes associados a este curr�culo. 
			//  Este atributo � utilizado para verificar se houve alguma altera��o neste sentido.
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
				// se n�o tiver nenhum componente pra remover do curr�culo tem que verificar se tem algum pra adicionar
				// se n�o tiver nenhum pra adicionar tem que verificar se algum foi atualizado
				// se qualquer uma destas condi��es ocorrer as integraliza��es dos discentes devem ser zeradas 
				for( CurriculoComponente cc : c.getCurriculoComponentes() ){
					if( cc.getId() == 0 ){
						zerarIntegralizacoesDiscentes = true;
						break;
					}
				}
				
				// se n�o tiver nenhum pra adicionar tem que verificar se algum foi atualizado 
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
			
			
			// se a carga hor�ria do curr�culo for alterada, deve zerar a data da �ltima de atualiza��o de 
			// todos os alunos associados a este curr�culo
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
	 * zera as integraliza��es dos discentes associados ao curr�culo
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
