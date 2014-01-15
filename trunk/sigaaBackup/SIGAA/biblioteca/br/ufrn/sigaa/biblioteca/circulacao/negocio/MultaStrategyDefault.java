/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 02/03/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dao.biblioteca.MultaUsuariosBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.MultaUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PunicaoAtrasoEmprestimoBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.SituacaoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;
import br.ufrn.sigaa.biblioteca.util.MultaUsuarioBibliotecaUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 *
 * <p> Estrat�gia padr�o para gera��o e verifica��o de multas na biblioteca </p>
 * 
 * @author jadson
 *
 */
public class MultaStrategyDefault extends PunicaoAtrasoEmprestimoStrategy{

	/**
	 * O padr�o utilizado no sistema para valores monet�rios da multa
	 */
	public static final String PATTERN_DEFAULT = "� ###,###,##0.00";
	
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.negocio.PunicaoAtrasoEmprestimoStrategy#criarPunicaoAutomatica(br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo, java.util.Date, java.util.Date)
	 */
	@Override
	public PunicaoAtrasoEmprestimoBiblioteca criarPunicaoAutomatica(Emprestimo e, Date prazoDevolucao, Date dataQueFoiDevolvido) throws DAOException {
		
		MultaUsuariosBibliotecaDao multaDao  = null;
		MultaUsuarioBiblioteca multaGerada = null;
		
		try{
		
			/* ************************************************************************************
			 *   
			 *   Calcula a quantidade de dias que o usu�rio ficar� suspenso
			 *   
			 * ************************************************************************************/
			BigDecimal valorMulta = calculaValorMulta(e, e.getPrazo(), new Date());
			
			
			// Cria uma multa.
			multaGerada = new MultaUsuarioBiblioteca();
			multaGerada.setValor(valorMulta);
			
			// Seta o empr�stimo que causou essa multa.
			multaGerada.setEmprestimo(e);
			
			// Se a biblioteca de recolhimento da multa quando ela � gerada autom�ticamente pelo sistema.
			multaGerada.setBibliotecaRecolhimento(e.getMaterial().getBiblioteca());
			
			DecimalFormat formatador  = new DecimalFormat();
			formatador.applyPattern(PATTERN_DEFAULT);
			
			multaGerada.setMensagemComprovante("Usuario multado em: \t" + formatador.format(valorMulta));
			
			multaGerada.setSituacaoGeradaPelaPunicao(SituacaoUsuarioBiblioteca.ESTA_MULTADO);
			
		}finally{
			if(multaDao != null) multaDao.close();
		}
		
		return multaGerada;
	}

	
	/**
	 * <p>M�todo que recebe um empr�stimo e retorna a quantidade de dias que
	 * o usu�rio do mesmo dever� ficar suspenso por ele, de acordo com o prazo,
	 * a data atual e o tipo do empr�stimo </p>
	 * 
	 * <p><strong>Importante: </strong>  Esse m�todo deve ser o �nico utilizado para calcular os dias de suspens�o para centrar esse 
	 * calculo e n�o gerar erros.</p>
	 * 
	 * <p>
	 * <pre>
	 * As regra s�o:
	 * 
	 *     Empr�stimos Contados em DIAS: qtdDiasAtraso * QUANTIDADE_DIAS_SUSPENSAO_USUARIO_POR_DIA_ATRASO
	 *     Empr�stimos Contados em HORAS:   <= 24 = 1 dia,    > 24  = 1 dia +  (qtdDiasAtraso * QUANTIDADE_DIAS_SUSPENSAO_USUARIO_POR_DIA_ATRASO)
	 * </pre>
	 * </p>
	 * @param e
	 * @return
	 */
	public BigDecimal calculaValorMulta(Emprestimo e, final Date dataLimite, final Date dataDevolucao){

		String valorMultaDia = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.VALOR_MULTA_USUARIO_ATRASO_POR_DIA);
		final BigDecimal valorMultaUsuarioAtrasoPorDia = MultaUsuarioBibliotecaUtil.extraiValorNumeroMulta(valorMultaDia);
			
		final int qtdDiasEmAtraso = CirculacaoUtil.calculaDiasEmAtrasoBiblioteca(dataLimite, dataDevolucao);
		
		
		if (e.getPoliticaEmprestimo().isPrazoContadoEmDias()){	
			return  valorMultaUsuarioAtrasoPorDia.multiply( new BigDecimal(qtdDiasEmAtraso));
		}else{
			
			if (e.getPoliticaEmprestimo().isPrazoContadoEmHoras()){
				
				String valorMultaHora = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.VALOR_MULTA_USUARIO_ATRASO_POR_HORA);
				final BigDecimal valorMultaUsuarioAtrasoPorHora = MultaUsuarioBibliotecaUtil.extraiValorNumeroMulta(valorMultaHora);
				
				if(qtdDiasEmAtraso == 0){ // Se atrazou dentro do mesmo dia		
					return valorMultaUsuarioAtrasoPorHora;
				}else{ // Passou mais de uma dia, entr�o recebe a multa anterior + a multa normal por dia de atraso.
					return valorMultaUsuarioAtrasoPorHora.add( (       valorMultaUsuarioAtrasoPorDia.multiply( new BigDecimal(qtdDiasEmAtraso))    ) );
				}
				
			}
			
		}
		
		return new BigDecimal(0); 
	}
	
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.negocio.PunicaoAtrasoEmprestimoStrategy#verificaPunicoesUsuario(int)
	 */
	@Override
	public SituacaoUsuarioBiblioteca verificaPunicoesUsuario(Integer idPessoa, Integer idBiblioteca) throws DAOException {

		MultaUsuariosBibliotecaDao multaDao = null;
		
		try{
			 multaDao = DAOFactory.getInstance().getDAO(MultaUsuariosBibliotecaDao.class);
			 BigDecimal valorMultasNaoPagas = multaDao.findValorMultasNaoPagaDoUsuario(idPessoa, idBiblioteca);
			
			 if(valorMultasNaoPagas != null && valorMultasNaoPagas.compareTo(new BigDecimal(0)) > 0){
				 SituacaoUsuarioBiblioteca situacaoMulatdo = SituacaoUsuarioBiblioteca.ESTA_MULTADO;
				 
				 DecimalFormat formatador  = new DecimalFormat();
				 formatador.applyPattern(PATTERN_DEFAULT);
				 situacaoMulatdo.setDescricaoCompleta("Usu�rio possui d�bito de "+formatador.format(valorMultasNaoPagas)+" nas Bibliotecas");
				 return situacaoMulatdo;
			 }else
				 return null;
			 
		}finally{
			if(multaDao != null ) multaDao.close();
		}
	}


	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.negocio.PunicaoAtrasoEmprestimoStrategy#getValorFormatado(java.lang.Object)
	 */
	@Override
	public String getValorFormatado(Object valor) {
		DecimalFormat formatador  = new DecimalFormat();
		formatador.applyPattern(PATTERN_DEFAULT);
		return formatador.format(valor);
	}


	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.negocio.PunicaoAtrasoEmprestimoStrategy#desfazPunicoesUsuario(int)
	 */
	@Override
	public void desfazPunicoesUsuario(int idEmprestimo) throws DAOException {
		MultaUsuariosBibliotecaDao multaDao = null;
		
		try{
			multaDao = DAOFactory.getInstance().getDAO(MultaUsuariosBibliotecaDao.class);
			MultaUsuarioBiblioteca multa = multaDao.findByExactField (MultaUsuarioBiblioteca.class, "emprestimo.id", idEmprestimo, true);
		
			if(multa != null) // se existe multa, remove a multa gerada pelo empr�stimo
				multaDao.remove(multa);	
		
		}finally{
			if(multaDao != null ) multaDao.close();
		}
	}
	
}
