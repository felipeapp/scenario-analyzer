 /*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 31/07/2012
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.bolsas.negocio.IntegracaoBolsas;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.pesquisa.dao.PlanoTrabalhoDao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Valida��es para a sele��o de discente
 *
 * @author Diego J�come
 *
 */
public class MovimentacaoAlunoValidator {

	/**
	 * Faz a valida��o do discente ap�s ele ser selecionado durante a opera��o de retorno de afastamento
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @param mov
	 * @param afastamento
	 * @param usuario
	 * @param dao
	 * @param erros
	 * @throws DAOException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws NegocioException 
	 */
	public static void validarRetornoAfastamentoAluno(MovimentacaoAluno mov, MovimentacaoAluno afastamento, Usuario usuario, MovimentacaoAlunoDao dao, ListaMensagens erros)	throws DAOException, IllegalAccessException, InvocationTargetException {
		
		dao = getDAO(MovimentacaoAlunoDao.class);
		MovimentacaoAluno ultimo = null;
		CalendarioAcademico calendarioVigente = CalendarioAcademicoHelper.getCalendario(mov.getDiscente());
		// busca trancamento no semestre atual
		ultimo = localizarUltimoTrancamentoDiscente(mov.getDiscente().getDiscente(), usuario, dao, erros, calendarioVigente);
		
		if( isUserInRole(usuario, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE, SigaaPapeis.PPG) 
				|| !ultimo.getTipoMovimentacaoAluno().isPermanente() ){
		
			BeanUtils.copyProperties(mov, ultimo);
			BeanUtils.copyProperties(afastamento, ultimo);
		
			// se for discente stricto sugere a data de fim do trancamento baseado no valor da movimenta��o
			if( mov.getDiscente().isStricto() ){
		
				if( mov.getCodmergpapos() != null && mov.isTrancamento() && mov.getInicioAfastamento() == null )
					mov.setInicioAfastamento( mov.getDataOcorrencia() );
		
				if( mov.getInicioAfastamento() != null && mov.getValorMovimentacao() != null ){
		
					Calendar c = Calendar.getInstance();
					c.setTime( mov.getInicioAfastamento() );
					c.add(Calendar.MONTH, mov.getValorMovimentacao());
					mov.setDataRetorno( c.getTime() );
		
				}
			}
					
		}else{
			erros.addErro("N�o � poss�vel retornar um aluno de um afastamento permanente.");
		} 
	}

	/**
	 * Localiza o ultimo tracamento do discente
	 * 
	 * @param mov
	 * @param usuario
	 * @param dao
	 * @param erros
	 * @param calendarioVigente
	 * @return
	 * @throws DAOException
	 */
	public static MovimentacaoAluno localizarUltimoTrancamentoDiscente(Discente discente, Usuario usuario, MovimentacaoAlunoDao dao, ListaMensagens erros, CalendarioAcademico calendarioVigente) throws DAOException {
		
		MovimentacaoAluno ultimo = dao.findTracamentoByDiscente(discente, calendarioVigente.getAno(), calendarioVigente.getPeriodo(), true);
		
		// caso n�o encontrado, busca o ultimo afastamento
		if (ultimo == null) {
			ultimo = dao.findUltimoAfastamentoByDiscente(discente.getId(), null, false);
		}
		if (ultimo == null) {
			// discente migrado que n�o possui movimenta��o
			// criando uma movimenta��o
			erros.addWarning("N�o consta o afastamento desse aluno na base de dados. <br/>Ser� criada uma movimenta��o inicial para o discente a fim de proseguir com a opera��o.");
			ultimo = new MovimentacaoAluno();
			ultimo.setAnoReferencia(1900);
			ultimo.setPeriodoReferencia(1);
			ultimo.setDataOcorrencia(new Date());
			ultimo.setDiscente(discente);
			ultimo.setObservacao("MOVIMENTA��O CADASTRADA PARA DISCENTE MIGRADO");
			ultimo.setUsuarioCadastro(usuario);
			if (discente.isTrancado())
				ultimo.setTipoMovimentacaoAluno(new TipoMovimentacaoAluno(TipoMovimentacaoAluno.TRANCAMENTO));
			else
				ultimo.setTipoMovimentacaoAluno(new TipoMovimentacaoAluno(TipoMovimentacaoAluno.EXCLUIDO));
			ultimo.setTipoMovimentacaoAluno(dao.refresh(ultimo.getTipoMovimentacaoAluno()));
		} 
		
		if (ultimo != null) {
			ultimo.setDiscente(getDAO(DiscenteDao.class).findByPK(discente.getId()));
		}
		return ultimo;
	}
	
	/**
	 * Faz a valida��o do discente ap�s ele ser selecionado durante a opera��o de estorno de afastamento
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @param mov
	 * @param statusretorno
	 * @param statusAnterior
	 * @param dao
	 * @param erros
	 * @throws DAOException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws NegocioException 
	 */
	public static void validarEstornoAfastamentoAluno(MovimentacaoAluno mov, Integer statusRetorno, String statusAnterior, MovimentacaoAlunoDao dao, ListaMensagens erros)	throws DAOException, IllegalAccessException, InvocationTargetException {
		MovimentacaoAluno ultimoAfastamento = dao.findUltimoAfastamentoByDiscente(mov.getDiscente().getId(), null, true);
		
		if (ultimoAfastamento == null) {
			erros.addErro("Esse discente n�o possui afastamento para estornar.");
			return ;
		}
		
		ultimoAfastamento.setDiscente(mov.getDiscente());

		statusRetorno = DiscenteHelper.getUltimoStatus(mov.getDiscente().getDiscente());
		
		BeanUtils.copyProperties(mov, ultimoAfastamento);
		
		if (statusRetorno == null)
			statusRetorno = StatusDiscente.ATIVO;
		
		Discente d = new Discente();
		d.setStatus(statusRetorno);
		statusAnterior = d.getStatusString();
	}
	
	/**
	 * Faz a valida��o do discente ap�s ele ser selecionado durante a opera��o de cancelamento de trancamento
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @param historcoMoviemntacoes
	 * @param erros
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public static void validarCancelarTrancamento(Collection<MovimentacaoAluno> historicoMovimentacoes, ListaMensagens erros) {
		if (historicoMovimentacoes.isEmpty()) {
			erros.addErro("O discente escolhido n�o possui trancamentos para ano-semestre futuros");
		}
	}
	
	/**
	 * Faz a valida��o do discente ap�s ele ser selecionado durante a opera��o de cancelmanto de programa
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @param mov
	 * @param erros
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public static void validarCancelarPrograma(MovimentacaoAluno mov, ListaMensagens erros) {
		// Validar status v�lidos para cancelamento de programa
		List<Integer> statusValidos = Arrays.asList(StatusDiscente.ATIVO, StatusDiscente.CADASTRADO, StatusDiscente.AFASTADO, StatusDiscente.FORMANDO, StatusDiscente.TRANCADO);
		if ( !statusValidos.contains( mov.getDiscente().getStatus() ) ) {
			erros.addErro("O discente escolhido n�o pode ter seu programa cancelado pois n�o possui um v�nculo ativo.");
		}
	}
	
	/**
	 * Faz a valida��o do discente ap�s ele ser selecionado durante a opera��o de conclus�o de discente
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @param mov
	 * @param usuario
	 * @param erros
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public static void validarConcluirAluno(MovimentacaoAluno mov, Usuario usuario, ListaMensagens erros) throws DAOException, ArqException, NegocioException {
		DiscenteDao discDao = null;
		
		try {
			discDao = getDAO(DiscenteDao.class);
			Collection<ComponenteCurricular> disciplinasPendentes = discDao.findComponentesCurricularesPendentesByDiscente(mov.getDiscente().getId());
			if( !isEmpty(disciplinasPendentes) ){
				StringBuffer msg = new StringBuffer("N�o � poss�vel realizar a conclus�o deste discente pois ele possui a(s) disciplina(s) abaixo pendente(s) no hist�rico: <br/>");
				for( ComponenteCurricular cc : disciplinasPendentes ){
					msg.append( "  - " + cc.toString() + "<br/>");
				}
				erros.addErro( msg.toString() );
			}
			
			possuiPendenciasBiblioteca(mov.getDiscente(),erros);
	
			if( !mov.getDiscente().getCurriculo().getMatriz().isPermiteColacaoGrau() )
				mov.setApostilamento(true);
			else
				mov.setApostilamento(false);
			validaPendenciasBolsasMonitoria(mov,usuario,erros);
		} finally {
			if (discDao!= null)
				discDao.close();
		}	
	}
	
	/**
	 * Faz a valida��o do discente ap�s ele ser selecionado durante a opera��o de afastamento de aluno
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @param mov
	 * @param usuario
	 * @param erros
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public static void validarAfastamentoAluno(MovimentacaoAluno mov, Usuario usuario, ListaMensagens erros) throws DAOException, ArqException, NegocioException {
		// verifica empr�stimos na biblioteca sem devolu��o
		possuiPendenciasBiblioteca(mov.getDiscente(), erros);
//		validaPendenciasBolsasMonitoria(mov,usuario,erros);
	}
	
	/**
	 * Faz a valida��o do discente ap�s ele ser selecionado durante a opera��o de conclus�o de aluno stricto
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @param mov
	 * @param erros
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public static void validarConcluirAlunoStricto(MovimentacaoAluno mov, ListaMensagens erros) throws DAOException, NegocioException {
		// verifica empr�stimos na biblioteca sem devolu��o
		possuiPendenciasBiblioteca(mov.getDiscente(), erros);
	}
	
	/**
	 * Faz a valida��o do discente ap�s ele ser selecionado durante a opera��o de trancamento de programa
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @param mov
	 * @param usuario
	 * @param erros
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public static void validarTrancarPrograma(MovimentacaoAluno mov, Usuario usuario, ListaMensagens erros) throws ArqException {
		// valida bolsas de monitiria n�o finalizadas
		validaPendenciasBolsasMonitoria(mov,usuario,erros);
	}
	
	/**
	 * <p>Verifica se o discente possui empr�timos abertos com o v�nculo de discente, se possui n�o ser� 
	 * poss�vel concluir ou trancar o discente enquanto n�o devolver os empr�timos.</p>
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @param discente
	 * @param erros
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public static void possuiPendenciasBiblioteca(DiscenteAdapter discente, ListaMensagens erros) throws DAOException, NegocioException {
		
		erros.addAll(VerificaSituacaoUsuarioBibliotecaUtil.verificaEmprestimoPendenteDiscente(discente.getDiscente()));
	}
	
	/**
	 * Valida pend�ncias de bolsas de monitoria
	 * @throws ArqException
	 */
	public static void validaPendenciasBolsasMonitoria(MovimentacaoAluno mov, Usuario usuario, ListaMensagens erros) throws ArqException {
		
		DiscenteMonitoriaDao dmDao = null;
		PlanoTrabalhoDao ptDao = null;
		
		try {
			dmDao = getDAO(DiscenteMonitoriaDao.class);
			ptDao = getDAO(PlanoTrabalhoDao.class);
			
			if(Sistema.isSipacAtivo()) {			
				if ( (IntegracaoBolsas.verificarCadastroBolsaSIPAC(mov.getDiscente().getMatricula(), null) != 0
						|| isNotEmpty(dmDao.findDiscenteMonitoriaAtivoByDiscente(mov.getDiscente()))
						|| isNotEmpty(ptDao.findAtivoByIdDiscente(mov.getDiscente().getId())))
						&& !isUserInRole(usuario, SigaaPapeis.GESTOR_MONITORIA,SigaaPapeis.ADMINISTRADOR_DAE,SigaaPapeis.DAE) ) {
					erros.addErro("O discente possui bolsa ativa de monitoria e/ou de pesquisa que deve ser finalizada antes de prosseguir.");
				}
			}
		} finally {
			if (dmDao!=null)
				dmDao.close();
			if (ptDao!=null)
				ptDao.close();
		}
	}
	
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}
	
	/**
	 * Retorna true se usu�rio possuir alguns dos papeis passados como
	 * par�metro.
	 * @param papeis
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	private static boolean isUserInRole(Usuario usuario, int... papeis) {

		UsuarioGeral user = usuario;
		for (int papel : papeis) {
			if (user.isUserInRole(papel))
				return true;
		}

		return false;
	}
}
