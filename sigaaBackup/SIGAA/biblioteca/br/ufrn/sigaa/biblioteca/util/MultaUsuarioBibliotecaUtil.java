/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 10/03/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.util;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.gru.dominio.ConfiguracaoGRU;
import br.ufrn.comum.gru.dominio.GuiaRecolhimentoUniao;
import br.ufrn.comum.gru.dominio.TipoArrecadacao;
import br.ufrn.comum.gru.negocio.GuiaRecolhimentoUniaoHelper;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.MultaUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * <p>Classe utilit�rio para trabalhar com multas</p>
 *
 * @author jadson
 *
 */
public class MultaUsuarioBibliotecaUtil {

	
	
	/**
	 * O valor do par�metro da multa no sistema � informado no padr�o brasileiro "1.000.000,00". Mas para gerar o valor num�rico correto da multa
	 * precisa formatar a string de acordo com o padr�o americano: "1000000.00".
	 * 
	 * @param valorMulta
	 * @return
	 */
	public static BigDecimal extraiValorNumeroMulta(String valorMulta){
		if(StringUtils.isEmpty(valorMulta))  
			valorMulta = "0";
		
		/* Passa de "1.000,00" para  "1000.00" ( retira todos os pontos, depois substitui v�rgula por ponto) */
		return new BigDecimal(valorMulta.replace(".", "").replace(",", ".")); 
	}
	
	
	
	/**
	 *  <p>Classe que monta as informa��es para o usu�rio indentificar a que se refere a multa que est� sendo paga.</p>
	 *  
	 *  <p>Deve ser impressa no comprovante de pagamento e na GRU gerada.</p>
	 *
	 * @param multa  Os dados da multa que v�o ser utilizados neste m�todo j� devem est� preenchidos. Normalmente j� s�o preenchidos nos m�todos do MultaUsuariosBibliotecaDao.
	 * @return
	 */
	public static String montaInformacoesMultaComprovante(MultaUsuarioBiblioteca multa, boolean emHTML){
		
		StringBuffer infoMulta = new StringBuffer();
		
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		if(multa.isManual()){
			infoMulta.append("Usu�rio que cadastrou a multa: "+multa.getUsuarioCadastro().getPessoa().getNome()  +getNovaLinha(emHTML));
			infoMulta.append("Data do cadastro: "+format.format(   multa.getDataCadastro()) +getNovaLinha(emHTML));
			infoMulta.append("Motivo: "+multa.getMotivoCadastro()  +getNovaLinha(emHTML));
		}else{
			
			Date dataDevolucao =  multa.getEmprestimo().getDataDevolucao();  // Pode ser nulo caso o usu�rio fique multado por comunicar o material perdido
			
			infoMulta.append("Material Emprestado: "+multa.getEmprestimo().getMaterial().getCodigoBarras() +getNovaLinha(emHTML));
			infoMulta.append("Data do empr�stimio: "+format.format(   multa.getEmprestimo().getDataEmprestimo()) +getNovaLinha(emHTML));
			infoMulta.append("Prazo do empr�stimio: "+format.format(   multa.getEmprestimo().getPrazo() )+getNovaLinha(emHTML));
			
			if(dataDevolucao != null){
				infoMulta.append("Data da devolu��o: "+format.format( dataDevolucao ) +getNovaLinha(emHTML));
			}
		}
		
		return infoMulta.toString();
	}

	/**
	 * Retorna nova linha, dependendo se vai ser impresso em PDF ou na p�gina JSP.
	 * Utilizado na gera��o da GRU
	 *
	 * @param emHTML
	 * @return
	 */
	private static String getNovaLinha(boolean emHTML){
		if(emHTML)
			return "<br/>";
		else
			return "\n";
	}
	
	
	/**
	 * <p> M�todo centralizado para gerar as GRUs para multas da biblioteca </p>
	 * <br/>
	 * <p> <strong> IMPORTANTE: </strong> no lugar do CPF da pessoa, vai colocar o id da multa para o qual a GRU foi gerada.
	 * Assim quando o sistema trabalhar com o recebimento autom�tico das multas, ser� poss�vel verificar qual a multa que a GRU pagou.</p>
	 * <p>No recebimento manual � o pr�prio usu�rio que inclui o pagamento no sistema que realiza essa opera��o</p>
	 *
	 * @param multa
	 * @param usuarioBiblioteca
	 * @return
	 * @throws NegocioException 
	 * @throws DAOException 
	 */
	public static GuiaRecolhimentoUniao montaInformacoesGRUMultaBiblioteca(MultaUsuarioBiblioteca multa) throws NegocioException, DAOException{
		
		GenericDAO dao = null;
		GenericDAO daoComum = null;
		try{
			dao = DAOFactory.getGeneric(Sistema.SIGAA);
			daoComum = DAOFactory.getGeneric(Sistema.COMUM);
		
			if(! multa.getUsuarioBiblioteca().ehPessoa()){
				throw new NegocioException("Apenas pessoas f�sicas podem emitir a GRU para pagamento da multa.");
			}
			
			Pessoa pessoa = dao.findByPrimaryKey(multa.getUsuarioBiblioteca().getPessoa().getId(), Pessoa.class, "nome", "cpf_cnpj");
			
			if(pessoa.getCpf_cnpj() == null ){
				throw new NegocioException("N�o � poss�vel emitir a GRU pois, o usu�rio n�o possui CPF, o pagamento deve ser realizado na biblioteca.");
			}
			
			if(multa.getValor() == null ){
				throw new NegocioException("N�o � poss�vel emitir a GRU pois, o valor n�o foi informado.");
			}
			
			
			StringBuilder descricaoGRU = new StringBuilder(" Referente aos pagamentos : ");
			SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			
			if(multa.isManual())
				descricaoGRU.append(multa.getValorFormatado()+" ( MANUAL em "+formatador.format(multa.getDataCadastro())+" )"+", ");
			else
				descricaoGRU.append(multa.getValorFormatado()+" ("+multa.getEmprestimo().getMaterial().getCodigoBarras()+" em "+formatador.format(multa.getEmprestimo().getDataEmprestimo())+" ) "+", ");
			
			
			/* ********************************************************************************
			 *  Gera um GRU simples sem data de vencimento 
			 *  
			 *  Para ser gerada a GRU precisa ser criada uma "Configura��o GRU" para a unidade da *** BIBLIOTECA CENTRAL ***
			 *  
			 *  Para as multas autom�ticas a biblioteca de recolhimento � a biblioteca do material.
			 *  Para as multas manuais o usu�rio informa no momento do cadastro qual a biblioteca que ir� receber a multa. 
			 *  
			 *  Caso a biblioteca central tamb�m n�o tenha a configura��o de GRU criada, o usu�rio n�o vai conseguir emitir a GRU.
			 *  
			 * ********************************************************************************/
			
			// OBSERVA��O: se altera alguma coisa aqui, lembre de alterar o m�todo que gera para v�rias multa !!!!
			
			ConfiguracaoGRU configGRU = null;
			Biblioteca bibliotecaRecolhimentoMulta =  null;
			
			
			if(! multa.isManual()){
				bibliotecaRecolhimentoMulta =  multa.getEmprestimo().getMaterial().getBiblioteca();
			}else{ 
				bibliotecaRecolhimentoMulta =  multa.getBibliotecaRecolhimento();
			}
			
			Biblioteca bibliotecaCentral = BibliotecaUtil.getBibliotecaCentral(new String[]{"id", "unidade.id"});
			configGRU = GuiaRecolhimentoUniaoHelper.getConfiguracaoGRUByTipoArrecadacao(TipoArrecadacao.PAGAMENTO_MULTAS_BIBLIOTECA, bibliotecaCentral.getUnidade().getId());
			
			if (isEmpty(configGRU)){
				throw new NegocioException("N�o � poss�vel emitir a GRU pois n�o foi encontrada nenhuma configura��o de GRU.");
			}
				
			
			GuiaRecolhimentoUniao gru = GuiaRecolhimentoUniaoHelper.createGRUSimples(
					configGRU.getId(),
					pessoa.getCpf_cnpj(),
					pessoa.getNome(), "\n\tSenhor caixa, n�o receber pagamento parcial.\n\n"
					+descricaoGRU, 
					// Se existir biblioteca de recolhimento ent�o vai para ela, se n�o, a biblioteca central agrade�e //
					( bibliotecaRecolhimentoMulta != null ? bibliotecaRecolhimentoMulta.getUnidade().getId() : bibliotecaCentral.getUnidade().getId() ), 
					new TipoArrecadacao(TipoArrecadacao.PAGAMENTO_MULTAS_BIBLIOTECA), 
					configGRU.getTipoArrecadacao().getCodigoRecolhimento().getCodigo(),
					String.format("%1$tm/%1$tY", new Date()),  // Compet�ncia
					null,                                      // GRU DA BIBLIOTECA N�O TEM VENCIMENTO !!!
					multa.getValor().doubleValue());
			return gru;
			
		}finally{
			if(dao != null) dao.close();
			if(daoComum != null) daoComum.close();
		}
	}
	
	
	
	
	
	/**
	 * <p> M�todo centralizado para gerar as GRUs para multas da biblioteca </p>
	 * <br/>
	 * <p> <strong> IMPORTANTE: </strong> no lugar do CPF da pessoa, vai colocar o id da multa para o qual a GRU foi gerada.
	 * Assim quando o sistema trabalhar com o recebimento autom�tico das multas, ser� poss�vel verificar qual a multa que a GRU pagou.</p>
	 * <p>No recebimento manual � o pr�prio usu�rio que inclui o pagamento no sistema que realiza essa opera��o</p>
	 *
	 * @param multa
	 * @param usuarioBiblioteca
	 * @return
	 * @throws NegocioException 
	 * @throws DAOException 
	 */
	public static GuiaRecolhimentoUniao montaInformacoesGRUMultasBibliotecas(List<MultaUsuarioBiblioteca> multasAbertas) throws NegocioException, DAOException{
		
		GenericDAO dao = null;
		GenericDAO daoComum = null;
		try{
			dao = DAOFactory.getGeneric(Sistema.SIGAA);
			daoComum = DAOFactory.getGeneric(Sistema.COMUM);
		
			UsuarioBiblioteca usuarioBiblioteca = null;
			
			// Pega apenas o primeiro usu�rio biblioteca porque t� interessado apenas na pessoa   //
			// Uma uma GRU n�o possui multas de mais de uma pessoa por vez                        //
			if(multasAbertas != null && multasAbertas.size() > 0){
				usuarioBiblioteca = multasAbertas.get(0).getUsuarioBiblioteca(); 
			}else{
				throw new NegocioException("Selecione pelo menos uma multa para emitir a GRU.");
			}
			
			Pessoa pessoa = dao.findByPrimaryKey(usuarioBiblioteca.getPessoa().getId(), Pessoa.class, "nome", "cpf_cnpj");
			
			
			if(pessoa.getCpf_cnpj() == null ){
				throw new NegocioException("N�o � poss�vel emitir a GRU pois, o usu�rio n�o possui CPF, o pagamento deve ser realizado na biblioteca.");
			}
			
			
			BigDecimal valorTotalMultas = new BigDecimal(0);
			
			StringBuilder descricaoGRU = new StringBuilder(" Referente aos pagamentos : ");
			SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			
			
			for (MultaUsuarioBiblioteca multa : multasAbertas) {
				if(multa.getValor() == null ){
					throw new NegocioException("N�o � poss�vel emitir a GRU pois, o valor n�o foi informado.");
				}else{
					valorTotalMultas = valorTotalMultas.add(multa.getValor());
					if(multa.isManual())
						descricaoGRU.append(multa.getValorFormatado()+" ( MANUAL em "+formatador.format(multa.getDataCadastro())+" )"+", ");
					else
						descricaoGRU.append(multa.getValorFormatado()+" ("+multa.getEmprestimo().getMaterial().getCodigoBarras()+" em "+formatador.format(multa.getEmprestimo().getDataEmprestimo())+" ) "+", ");
				}
			}
			
			/* A multa usado para obter as inforam��es na unidade de recolhimento. 
			 * Todos as multas passadas para esse caso precisam ter a mesma unidade de recolhimento. */
			if(multasAbertas.size() == 0)
				throw new NegocioException("Selecione pelo menos uma multa para gerar a GRU.");
			
			
			MultaUsuarioBiblioteca multaPadrao = multasAbertas.get(0);
			
			
			/* ********************************************************************************
			 *  Gera um GRU simples sem data de vencimento 
			 *  
			 *  Para ser gerada a GRU precisa ser criada uma "Configura��o GRU" para a unidade da *** BIBLIOTECA CENTRAL ***
			 *  
			 *  Para as multas autom�ticas a biblioteca de recolhimento � a biblioteca do material.
			 *  Para as multas manuais o usu�rio informa no momento do cadastro qual a biblioteca que ir� receber a multa. 
			 *  
			 *  Caso a biblioteca central tamb�m n�o tenha a configura��o de GRU criada, o usu�rio n�o vai conseguir emitir a GRU.
			 *  
			 * ********************************************************************************/
			
			
			//  OBSERVA��O: se altera alguma coisa aqui, lembre de alterar o m�todo que gera para uma multa !!!!
			
			ConfiguracaoGRU configGRU = null;
			Biblioteca bibliotecaRecolhimentoMulta =  null;
			
			if(! multaPadrao.isManual()){
				bibliotecaRecolhimentoMulta =  multaPadrao.getEmprestimo().getMaterial().getBiblioteca();
			}else{ // se a multa � manual, a unidade de recolhimento � informado na pr�pria multa
				bibliotecaRecolhimentoMulta =  multaPadrao.getBibliotecaRecolhimento();
			}
			
			Biblioteca bibliotecaCentral = BibliotecaUtil.getBibliotecaCentral(new String[]{"id", "unidade.id"});
			configGRU = GuiaRecolhimentoUniaoHelper.getConfiguracaoGRUByTipoArrecadacao(TipoArrecadacao.PAGAMENTO_MULTAS_BIBLIOTECA, bibliotecaCentral.getUnidade().getId());
			
			if (isEmpty(configGRU)){
				throw new NegocioException("N�o � poss�vel emitir a GRU pois n�o foi encontrada nenhuma configura��o de GRU.");
			}
			
			GuiaRecolhimentoUniao gru = GuiaRecolhimentoUniaoHelper.createGRUSimples(
					configGRU.getId(),
					pessoa.getCpf_cnpj(),
					pessoa.getNome(),
					"\n\tSenhor caixa, n�o receber pagamento parcial.\n\n"
					+descricaoGRU, 
					( bibliotecaRecolhimentoMulta != null ? bibliotecaRecolhimentoMulta.getUnidade().getId() : bibliotecaCentral.getUnidade().getId() ),
					new TipoArrecadacao(TipoArrecadacao.PAGAMENTO_MULTAS_BIBLIOTECA), 
					configGRU.getTipoArrecadacao().getCodigoRecolhimento().getCodigo(),
					String.format("%1$tm/%1$tY", new Date()),  // Compet�ncia
					null,                                      // GRU DA BIBLIOTECA N�O TEM VENCIMENTO !!!
					valorTotalMultas.doubleValue());
			
			return gru;
			
		}finally{
			if(dao != null) dao.close();
			if(daoComum != null) daoComum.close();
		}
	}
	
	
	
	
	
	
	
	
	
}
