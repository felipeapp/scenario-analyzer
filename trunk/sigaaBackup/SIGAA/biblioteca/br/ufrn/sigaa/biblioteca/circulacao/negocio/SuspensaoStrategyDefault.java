/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 02/03/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dao.biblioteca.SuspensaoUsuarioBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PunicaoAtrasoEmprestimoBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SuspensaoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.SituacaoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 * <p> Estratégia padrão de criação e verificação das suspensão utilizadas na UFRN.</p>
 * 
 * @author jadson
 *
 */
public class SuspensaoStrategyDefault extends PunicaoAtrasoEmprestimoStrategy{

	/**
	 * O padrão utilizado no sistema para o prazo de suspensão
	 */
	private static final String PATTERN_DEFAULT = "dd/MM/yyyy";
	
	
	/**
	 *  Ver comentários da classe pai.<br/>
	 * 
	 * @throws DAOException 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.negocio.PunicaoAtrasoEmprestimoStrategy#calcularPunicao()
	 */
	@Override
	public PunicaoAtrasoEmprestimoBiblioteca criarPunicaoAutomatica(Emprestimo e, Date prazoDevolucao, Date dataQueFoiDevolvido) throws DAOException {
		
		SuspensaoUsuarioBibliotecaDao suspensaoDao = null;
		SuspensaoUsuarioBiblioteca suspensaoCriada = null;
		
		try{
		
			/* ************************************************************************************
			 *   
			 *   Calcula a quantidade de dias que o usuário ficará suspenso
			 *   
			 * ************************************************************************************/
			int diasASuspender = calculaDiasSuspensao(e, e.getPrazo(), new Date());
			
			
			suspensaoDao =  DAOFactory.getInstance().getDAO(SuspensaoUsuarioBibliotecaDao.class);
			Date dataFinalSuspensao = suspensaoDao.findFimSuspensao( e.getUsuarioBiblioteca().ehPessoa() ?  e.getUsuarioBiblioteca().getPessoa().getId() : null
							, e.getUsuarioBiblioteca().ehBiblioteca() ? e.getUsuarioBiblioteca().getBiblioteca().getId() : null );
			
			Calendar hoje = Calendar.getInstance();
			
			// Se já existia uma suspensão ativa, a data inicial da suspensão será no final da última suspensão.
			if (dataFinalSuspensao != null)
				hoje.setTime(dataFinalSuspensao);
			else // Senão, a suspensão começa amanhã.						
				hoje.setTime(new Date());
			
			// Cria uma suspensão.
			suspensaoCriada = new SuspensaoUsuarioBiblioteca();
			
			// Seta a data de início da suspensão e a quantidade de dias a suspender.
			suspensaoCriada.criaNovaSuspensao(hoje.getTime(), diasASuspender);
			
			// Seta o empréstimo que causou essa suspensão.
			suspensaoCriada.setEmprestimo(e);
			
			SimpleDateFormat formatador  = new SimpleDateFormat(PATTERN_DEFAULT);
			suspensaoCriada.setMensagemComprovante("Usuario suspenso ate: \t" + formatador.format(suspensaoCriada.getDataFim()));
			
			suspensaoCriada.setSituacaoGeradaPelaPunicao(SituacaoUsuarioBiblioteca.ESTA_SUSPENSO);
			
		}finally{
			if(suspensaoDao != null) suspensaoDao.close();
		}
		
		return suspensaoCriada;
	}

	
	/**
	 * <p>Método que recebe um empréstimo e retorna a quantidade de dias que
	 * o usuário do mesmo deverá ficar suspenso por ele, de acordo com o prazo,
	 * a data atual e o tipo do empréstimo </p>
	 * 
	 * <p><strong>Importante: </strong>  Esse método deve ser o único utilizado para calcular os dias de suspensão para centrar esse 
	 * calculo e não gerar erros.</p>
	 * 
	 * <p>
	 * <pre>
	 * As regra são:
	 * 
	 *     Empréstimos Contados em DIAS: qtdDiasAtraso * QUANTIDADE_DIAS_SUSPENSAO_USUARIO_POR_DIA_ATRASO
	 *     Empréstimos Contados em HORAS:   <= 24 = 1 dia,    > 24  = 1 dia +  (qtdDiasAtraso * QUANTIDADE_DIAS_SUSPENSAO_USUARIO_POR_DIA_ATRASO)
	 * </pre>
	 * </p>
	 * @param e
	 * @return
	 */
	public int calculaDiasSuspensao(Emprestimo e, final Date dataLimite, final Date dataDevolucao){
		
		final int quantidadeDiasSuspensaoUsuarioAtrasoPorDia = ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.QUANTIDADE_DIAS_SUSPENSAO_USUARIO_ATRASO_POR_DIA);
		
		final int quantidadeDiasSuspensaoUsuarioAtrasoPorHora = ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.QUANTIDADE_DIAS_SUSPENSAO_USUARIO_ATRASO_POR_HORA);
		
		final int qtdDiasEmAtraso = CirculacaoUtil.calculaDiasEmAtrasoBiblioteca(dataLimite, dataDevolucao);
	

		if (e.getPoliticaEmprestimo().isPrazoContadoEmDias()){
			return qtdDiasEmAtraso * quantidadeDiasSuspensaoUsuarioAtrasoPorDia;
		}else{
		
			if (e.getPoliticaEmprestimo().isPrazoContadoEmHoras()){
				
				if(qtdDiasEmAtraso == 0){ // Se atrazou dentro do mesmo dia		
					return quantidadeDiasSuspensaoUsuarioAtrasoPorHora;
				}else{ // Passou mais de uma dia, entrão recebe a suspensão anterior + a suspensão normal por dia de atraso.
					return quantidadeDiasSuspensaoUsuarioAtrasoPorHora + (qtdDiasEmAtraso*quantidadeDiasSuspensaoUsuarioAtrasoPorDia);
				}
				
			}
			
		}
		
		return 0; 
	}


	/**
	 *  Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.negocio.PunicaoAtrasoEmprestimoStrategy#verificaPunicoesUsuario(int)
	 */
	@Override
	public SituacaoUsuarioBiblioteca verificaPunicoesUsuario(Integer idPessoa, Integer idBiblioteca) throws DAOException {

		SuspensaoUsuarioBibliotecaDao suspensaoDao = null;
		
		try{
			suspensaoDao = DAOFactory.getInstance().getDAO(SuspensaoUsuarioBibliotecaDao.class);
			Date fimSuspensao = suspensaoDao.findFimSuspensao(idPessoa, idBiblioteca);
			
			if(fimSuspensao != null){
				SituacaoUsuarioBiblioteca situacaoSuspenso = SituacaoUsuarioBiblioteca.ESTA_SUSPENSO;
				
				SimpleDateFormat formatador  = new SimpleDateFormat(PATTERN_DEFAULT);
				situacaoSuspenso.setDescricaoCompleta("Usuário está suspenso até a data de: "+formatador.format(fimSuspensao));
				return situacaoSuspenso;
			}else
				return null;
		
		
		}finally{
			if(suspensaoDao != null ) suspensaoDao.close();
		}
	}


	/**
	 *  Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.negocio.PunicaoAtrasoEmprestimoStrategy#getValorFormatado(java.lang.Object)
	 */
	@Override
	public String getValorFormatado(Object valor) {
		SimpleDateFormat formatador  = new SimpleDateFormat(PATTERN_DEFAULT);
		return formatador.format(valor);
	}


	/**
	 *  Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.negocio.PunicaoAtrasoEmprestimoStrategy#desfazPunicoesUsuario(int)
	 */
	@Override
	public void desfazPunicoesUsuario(int idEmprestimo) throws DAOException {
		
		SuspensaoUsuarioBibliotecaDao suspensaoDao = null;
		
		try{
			suspensaoDao = DAOFactory.getInstance().getDAO(SuspensaoUsuarioBibliotecaDao.class);
			SuspensaoUsuarioBiblioteca s = suspensaoDao.findByExactField (SuspensaoUsuarioBiblioteca.class, "emprestimo.id", idEmprestimo, true);
			if(s != null) // se existe suspensão, remove a multa suspensão pelo empréstimo
				suspensaoDao.remove(s);
		
		}finally{
			if(suspensaoDao != null ) suspensaoDao.close();
		}
	}
	
	
	
}
