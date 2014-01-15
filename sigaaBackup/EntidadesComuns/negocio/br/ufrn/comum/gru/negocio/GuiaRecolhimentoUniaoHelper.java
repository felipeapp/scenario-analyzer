/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 12/05/2011
 *
 */
package br.ufrn.comum.gru.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.Unidade;
import br.ufrn.comum.gru.dominio.AlteracaoGRU;
import br.ufrn.comum.gru.dominio.CodigoRecolhimentoGRU;
import br.ufrn.comum.gru.dominio.ConfiguracaoGRU;
import br.ufrn.comum.gru.dominio.ConstantesGRU;
import br.ufrn.comum.gru.dominio.GuiaRecolhimentoUniao;
import br.ufrn.comum.gru.dominio.TipoArrecadacao;
import br.ufrn.comum.gru.dominio.TipoGRU;

/**
 * Helper para persistir/recuperar Guia de Recolhimento da União - GRU - do
 * banco de dados COMUM.<br/>
 * Esta classe também é utilizada para gerar um arquivo PDF da GRU.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
public class GuiaRecolhimentoUniaoHelper {

	/**
	 * O diretório padrão dos fontes para gerar o pdf das GRUs.
	 */
	private static final String BASE_REPORT_PACKAGE = "/br/ufrn/gru/relatorios/";
	
	
	/** Construtor Padrão privado. */
	private GuiaRecolhimentoUniaoHelper() {
	}
	
	/** Cria (persiste) uma GRU a partir de uma configuração de GRU, com os parâmetros especificados. 
	 * @param idConfiguracaoGRU
	 * @param cpf
	 * @param nomeContribuinte
	 * @param enderecoSacado
	 * @param instrucoes
	 * @param vencimento
	 * @param valor
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public static GuiaRecolhimentoUniao createGRU(int idConfiguracaoGRU,
			long cpf,
			String nomeContribuinte,
			String enderecoSacado,
			String instrucoes,
			String competencia,
			Date vencimento,
			double valor) throws DAOException, NegocioException {
		ConfiguracaoGRU configuracao;
		GuiaRecolhimentoUniao gru;
		GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
		try {
			configuracao = dao.findByPrimaryKey(idConfiguracaoGRU, ConfiguracaoGRU.class);
			if (configuracao.isGruSimples()) {
				gru = createGRUSimples(idConfiguracaoGRU,
						cpf, nomeContribuinte, instrucoes, 
						configuracao.getUnidade().getId(),
						configuracao.getTipoArrecadacao(),
						configuracao.getTipoArrecadacao().getCodigoRecolhimento().getCodigo(),
						competencia,
						vencimento, valor, 
						null, null, null, null, null,
						configuracao.getGrupoEmissaoGRU().getCodigoGestao());
			} else {
				gru = createGRUCobranca(idConfiguracaoGRU,
						cpf, nomeContribuinte, enderecoSacado,
						instrucoes, 
						configuracao.getTipoArrecadacao(),
						configuracao.getUnidade().getId(), 
						configuracao.getGrupoEmissaoGRU().getAgencia(),
						configuracao.getGrupoEmissaoGRU().getCodigoCedente(),
						configuracao.getGrupoEmissaoGRU().getConvenio(),
						valor, vencimento);
			}
		} finally {
			dao.close();
		}
		return gru;
	}
	
	/** Cria (persiste) uma GRU simples com os parâmetros especificados.
	 * @param cpf
	 * @param nomeContribuinte
	 * @param instrucoes
	 * @param idUnidadeFavorecida
	 * @param tipoArrecadacao
	 * @param codigoRecolhimento
	 * @param competencia
	 * @param vencimento
	 * @param valor
	 * @param gestao
	 * @return
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public static GuiaRecolhimentoUniao createGRUSimples(
			int idConfiguracaoGRU,
			long cpf,
			String nomeContribuinte,
			String instrucoes,
			int idUnidadeFavorecida,
			TipoArrecadacao tipoArrecadacao, 
			String codigoRecolhimento,
			String competencia,
			Date vencimento, 
			Double valor) throws NegocioException, DAOException {
		GuiaRecolhimentoUniao gru = setaDadosComuns(idConfiguracaoGRU, TipoGRU.SIMPLES,
				cpf, nomeContribuinte, instrucoes,
				idUnidadeFavorecida, tipoArrecadacao,
				vencimento, valor);
		gru.setCodigoRecolhimento(codigoRecolhimento);
		gru.setCompetencia(competencia);
		validateAndPersist(gru);
		return gru;
	}
	
	/** Cria (persiste) uma GRU simples com os parâmetros especificados.
	 * @param cpf
	 * @param nomeContribuinte
	 * @param instrucoes
	 * @param idUnidadeFavorecida
	 * @param tipoArrecadacao
	 * @param codigoRecolhimento
	 * @param competencia
	 * @param vencimento
	 * @param valor
	 * @param descontoAbatimento
	 * @param outrasDeducoes
	 * @param moraMulta
	 * @param jurosEncargos
	 * @param outrosAcrescimos
	 * @param gestao
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public static GuiaRecolhimentoUniao createGRUSimples(
			int idConfiguracaoGRU,
			long cpf,
			String nomeContribuinte,
			String instrucoes,
			int idUnidadeFavorecida,
			TipoArrecadacao tipoArrecadacao, 
			String codigoRecolhimento,
			String competencia,
			Date vencimento, 
			Double valor, 
			Double descontoAbatimento, 
			Double outrasDeducoes,
			Double moraMulta, 
			Double jurosEncargos, 
			Double outrosAcrescimos,
			String gestao) throws DAOException, NegocioException {
		GuiaRecolhimentoUniao gru = setaDadosComuns(idConfiguracaoGRU, TipoGRU.SIMPLES,
				cpf, nomeContribuinte, instrucoes,
				idUnidadeFavorecida, tipoArrecadacao,
				vencimento, valor);
		gru.setCodigoRecolhimento(codigoRecolhimento);
		gru.setCompetencia(competencia);
		gru.setDescontoAbatimento(descontoAbatimento);
		gru.setOutrasDeducoes(outrasDeducoes);
		gru.setMoraMulta(moraMulta);
		gru.setJurosEncargos(jurosEncargos);
		gru.setOutrosAcrescimos(outrosAcrescimos);
		validateAndPersist(gru);
		return gru;
	}

	/** Valida e persiste a GRU no banco.
	 * @param gru
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private static void validateAndPersist(GuiaRecolhimentoUniao gru) throws DAOException,
			NegocioException {
		ListaMensagens lista = gru.validate();
		if (isEmpty(lista)) {
			persist(gru);
		} else {
			throw new NegocioException(lista);
		}
	}
	
	/** Cria (persiste) uma GRU cobrança com os parâmetros especificados.
	 * @param cpf
	 * @param nomeContribuinte
	 * @param enderecoSacado
	 * @param instrucoes
	 * @param tipoArrecadacao
	 * @param idUnidadeFavorecida
	 * @param agencia
	 * @param codigoCedente
	 * @param valor
	 * @param vencimento
	 * @return
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public static GuiaRecolhimentoUniao createGRUCobranca(
			int idConfiguracaoGRU,
			long cpf,
			String nomeContribuinte,
			String enderecoSacado,
			String instrucoes,
			TipoArrecadacao tipoArrecadacao,
			int idUnidadeFavorecida,
			String agencia,
			String codigoCedente,
			int convenio,
			Double valor,
			Date vencimento
			) throws NegocioException, DAOException {
		GuiaRecolhimentoUniao gru = setaDadosComuns(idConfiguracaoGRU,
				TipoGRU.COBRANCA,
				cpf, nomeContribuinte, instrucoes,
				idUnidadeFavorecida, tipoArrecadacao,
				vencimento, valor);
		gru.setEnderecoSacado(enderecoSacado);
		gru.setAgencia(agencia);
		gru.setCodigoCedente(codigoCedente);
		gru.setConvenio(convenio);
		validateAndPersist(gru);
		return gru;
	}
	
	/** Cria (persiste) uma GRU cobrança com os parâmetros especificados.
	 * @param cpf
	 * @param nomeContribuinte
	 * @param enderecoSacado
	 * @param instrucoes
	 * @param idUnidadeFavorecida
	 * @param tipoArrecadacao
	 * @param vencimento
	 * @param valor
	 * @param descontoAbatimento
	 * @param moraMulta
	 * @param agencia
	 * @param codigoCedente
	 * @param numeroDocumento
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public static GuiaRecolhimentoUniao createGRUCobranca(
			int idConfiguracaoGRU,
			long cpf,
			String nomeContribuinte,
			String enderecoSacado,		
			String instrucoes,
			int idUnidadeFavorecida,
			TipoArrecadacao tipoArrecadacao, 
			Date vencimento, 
			Double valor, 
			Double descontoAbatimento, 
			Double moraMulta, 
			String agencia,
			String codigoCedente,
			int numeroDocumento) throws DAOException, NegocioException {
		GuiaRecolhimentoUniao gru = setaDadosComuns(idConfiguracaoGRU,
				TipoGRU.COBRANCA,
				cpf, nomeContribuinte, instrucoes,
				idUnidadeFavorecida, tipoArrecadacao,
				vencimento, valor);
		gru.setEnderecoSacado(enderecoSacado);
		gru.setDescontoAbatimento(descontoAbatimento);
		gru.setMoraMulta(moraMulta);
		gru.setAgencia(agencia);
		gru.setCodigoCedente(codigoCedente);
		gru.setNumeroDocumento(numeroDocumento);
		validateAndPersist(gru);
		return gru;
	}
		
	/** Seta os dados comuns à GRU Simples e GRU Cobrança.
	 * @param tipo
	 * @param cpf
	 * @param nomeContribuinte
	 * @param instrucoes
	 * @param idUnidadeFavorecida
	 * @param tipoArrecadacao
	 * @param vencimento
	 * @param valor
	 * @return
	 */
	private static GuiaRecolhimentoUniao setaDadosComuns(int idConfiguracaoGRU,
			TipoGRU tipo, long cpf,
			String nomeContribuinte, String instrucoes,
			int idUnidadeFavorecida, TipoArrecadacao tipoArrecadacao,
			Date vencimento, Double valor) {
		GuiaRecolhimentoUniao gru = new GuiaRecolhimentoUniao();
		gru.setConfiguracaoGRU(new ConfiguracaoGRU(idConfiguracaoGRU));
		gru.setTipo(tipo);
		gru.setCpf(cpf);
		gru.setNomeContribuinte(nomeContribuinte);
		gru.setInstrucoes(instrucoes);
		gru.setUnidadeFavorecida(new Unidade(idUnidadeFavorecida));
		gru.setTipoArrecadacao(tipoArrecadacao);
		gru.setVencimento(vencimento);
		gru.setValor(valor);
		gru.setValorTotal(valor);
		return gru;
	}
	
	/** Persite no banco de dados a GRU.
	 * @param gru
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private static void persist(GuiaRecolhimentoUniao gru) throws DAOException, NegocioException {
		GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
		try {
			long sequencia = dao.getNextSeq("gru", "numero_referencia_seq");
			// calcula o DV para que o número de referência seja um CPF válido.
			int fator = 2;
			long soma = 0;
			// 9 dígitos do CPF
			long numero = sequencia;
			// primeiro DV
			while (numero > 0) {
				soma += (numero % 10) * fator++;
				numero /= 10;
			}
			long dv1 = soma % 11 < 2 ? 0 : 11 - (soma % 11);
			// segundo DV
			numero = sequencia*10 + dv1;
			fator = 2; soma = 0;
			while (numero > 0) {
				soma += (numero % 10) * fator++;
				numero /= 10;
			}
			long dv2 = soma % 11 < 2 ? 0 : 11 - (soma % 11);
			gru.setNumeroReferenciaNossoNumero(sequencia * 100 + dv1 * 10 + dv2);
			// cria o código de barras e a linha digitável
			if (gru.isGRUSimples()) {
				geraCodigoBarraSimples(gru);
			} else {
				geraCodigoBarrasCobranca(gru);
			}
			dao.create(gru);
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			dao.close();
		}
	}
	
	/** Recupera do banco de dados, uma GRU com o número de referência especificado.
	 * @param numeroReferencia
	 * @return
	 * @throws DAOException
	 */
	public static GuiaRecolhimentoUniao getGRUByNumeroReferencia(long numeroReferencia) throws DAOException {
		GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
		GuiaRecolhimentoUniao gru;
		try {
			gru = (GuiaRecolhimentoUniao) dao
					.getSession()
					.createCriteria(GuiaRecolhimentoUniao.class)
					.add(Restrictions.eq("numeroReferenciaNossoNumero",numeroReferencia))
					.uniqueResult();
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			dao.close();
		}
		return gru;
	}
	
	/** Recupera do banco de dados, uma GRU com o ID especificado.
	 * @param idGRU
	 * @return
	 * @throws DAOException
	 */
	public static GuiaRecolhimentoUniao getGRUByID(int idGRU) throws DAOException {
		GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
		GuiaRecolhimentoUniao gru;
		try {
			gru = (GuiaRecolhimentoUniao) dao
					.getSession()
					.createCriteria(GuiaRecolhimentoUniao.class)
					.add(Restrictions.eq("id",idGRU))
					.uniqueResult();
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			dao.close();
		}
		return gru;
	}
	
	/** Recupera do banco de dados, uma GRU com o ID especificado.
	 * @param idGRU
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public static Collection<GuiaRecolhimentoUniao> getGRUByID(Collection<Integer> idsGRU) throws DAOException {
		if (isEmpty(idsGRU)) return null;
		GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
		Collection<GuiaRecolhimentoUniao> gru;
		try {
			Criteria q = dao
					.getSession()
					.createCriteria(GuiaRecolhimentoUniao.class)
					.add(Restrictions.in("id",idsGRU));
			gru = q.list();
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			dao.close();
		}
		return gru;
	}
	
	/**
	 * Monta a sequência do código de barras conforme manual da FEBRABAN e Tesouro Nacional
	 * @param gru 
	 * @return
	 */
	private static void geraCodigoBarraSimples(GuiaRecolhimentoUniao gru){
		//Sequência numérica sem o digito verificador geral
		final StringBuilder seqSemDV = new StringBuilder( 43 );
		seqSemDV.append(ConstantesGRU.ARRECADACAO_ORGAO_VALOR);
		seqSemDV.append(StringUtils.leftPad( Formatador.getInstance().formatarMoeda(
				gru.getValorTotal().doubleValue()).toString().replace(".", "").replace(",", ""), 11, "0"));
		// se especificado outro cpf para o código de barras como, por exemplo, número de referência, utilizar o código STN respectivo.
		seqSemDV.append(ConstantesGRU.CODIGO_STN_CPF);
		seqSemDV.append(gru.getCodigoRecolhimento().substring(0, 5));
		seqSemDV.append(ConstantesGRU.UGXGRBB);
		// indica que o código utilizado é um CPF (o número de referência é um CPF "falso")
		seqSemDV.append("1");
		seqSemDV.append(String.format("%014d", gru.getNumeroReferenciaNossoNumero()));
		StringBuilder codigoBarra = new StringBuilder( 44 );
		codigoBarra.append(seqSemDV.subSequence( 0 , 3) );
		codigoBarra.append(modulo11(seqSemDV, true));
		codigoBarra.append(seqSemDV.subSequence( 3 , 43) );
		gru.setCodigoBarras(codigoBarra.toString());
		geraLinhaDigitavelSimples(gru);
	}
	
	/**
	 * Monta a sequência do código de barras para a GRU Cobrança conforme manual da FEBRABAN e Tesouro Nacional
	 * @param gru 
	 * @return
	 * @throws NegocioException 
	 */
	private static void geraCodigoBarrasCobranca(GuiaRecolhimentoUniao gru) throws NegocioException{
		StringBuilder codigoBarra = new StringBuilder( 44 );
		//Sequência numérica sem o digito verificador geral
		StringBuilder seqSemDV = new StringBuilder( 43 );
		seqSemDV.append(ConstantesGRU.COD_BANCO_COMPENSACAO);
		seqSemDV.append(ConstantesGRU.COD_MOEDA_REAL);
		// fator de vencimento: conta os dias a partir de 07/10/1997 (data definida no manual da GRU)
		// subtrai um dia, devido à função incluir o primeiro dia na contagem de dias
		try {
			int dias = CalendarUtils.calculaQuantidadeDiasEntreDatasIntervaloFechado(CalendarUtils.parseDate("1997-10-07"), gru.getVencimento()) - 1;
			seqSemDV.append(String.format("%04d", dias));
		} catch (ParseException e) {
			throw new NegocioException("Erro ao calcular a data de vencimento no código de barras");
		}
		// valor
		seqSemDV.append(StringUtils.leftPad(Formatador.getInstance().formatarMoeda(gru.getValorTotal().doubleValue()).toString().replace(".", "").replace(",", ""), 10, "0"));
		// Zeros, de acordo com a especificações técnicas da FEBRABAN - Federação Brasileira de Bancos
		seqSemDV.append("000000");
		// convênio
		seqSemDV.append(String.format("%07d", gru.getConvenio()));
		// número de inscrição
		seqSemDV.append(String.format("%010d", gru.getNumeroReferenciaNossoNumero()));
		// tipo de carteira/modalidade de cobrança
		seqSemDV.append("18");
		// Dígito verificador
		codigoBarra.append(seqSemDV.subSequence( 0 , 4) );
		codigoBarra.append(modulo11(seqSemDV, false));
		codigoBarra.append(seqSemDV.subSequence( 4 , 43) );
		gru.setCodigoBarras(codigoBarra.toString());
		geraLinhaDigitavelCobranca(gru);
	}
	
	/**
	 * Monta a sequência digitável conforme manual da FEBRABAN e Tesouro Nacional
	 * @param codigoBarras
	 * @return
	 */
	private static void geraLinhaDigitavelSimples(GuiaRecolhimentoUniao gru) {
		String codigoBarras = gru.getCodigoBarras();
		//Primeiro campo e digito verificador
		final StringBuilder primeiroCampo = new StringBuilder( 14 );
        primeiroCampo.append( codigoBarras.subSequence( 0, 11 ) );
        primeiroCampo.append( modulo11( primeiroCampo, true ) );
        primeiroCampo.insert( 11, '-' );
        primeiroCampo.append( ' ' );
        
        //Segundo campo e digito verificador
        final StringBuilder segundoCampo = new StringBuilder( 14 );
        segundoCampo.append( codigoBarras.subSequence( 11, 22 ) );
        segundoCampo.append( modulo11( segundoCampo, true ) );
        segundoCampo.insert( 11, '-' );
        segundoCampo.append( ' ' );

        //Terceiro campo e digito verificador
        final StringBuilder terceiroCampo = new StringBuilder( 14 );
        terceiroCampo.append( codigoBarras.subSequence( 22, 33 ) );
        terceiroCampo.append( modulo11( terceiroCampo, true ) );
        terceiroCampo.insert( 11, '-' );
        terceiroCampo.append( ' ' );
        
        //Quarto campo e digito verificador
        final StringBuilder quartoCampo = new StringBuilder( 14 );
        quartoCampo.append( codigoBarras.subSequence( 33, 44 ) );
        quartoCampo.append( modulo11( quartoCampo, true ) );
        quartoCampo.insert( 11, '-' );
        
        //Linha digital com os quatros campos
        final StringBuilder linhaDigitavel = new StringBuilder( 48 );
        linhaDigitavel.append( primeiroCampo );
        linhaDigitavel.append( segundoCampo );
        linhaDigitavel.append( terceiroCampo );
        linhaDigitavel.append( quartoCampo );

        gru.setLinhaDigitavel(linhaDigitavel.toString());
    }
	
	/**
	 * Monta a sequência digitável conforme manual da FEBRABAN e Tesouro Nacional
	 * @param codigoBarras
	 * @return
	 */
	private static void geraLinhaDigitavelCobranca(GuiaRecolhimentoUniao gru) {
		String codigoBarras = gru.getCodigoBarras();
		StringBuilder digitavel = new StringBuilder();
		StringBuilder parcial = new StringBuilder();
		
		parcial.append(codigoBarras.subSequence(0, 4));
		parcial.append(codigoBarras.subSequence(19, 24));
		//dígito verificador
		digitavel.append(parcial);
		digitavel.append(modulo10(parcial));
		
		parcial = new StringBuilder();
        parcial.append( codigoBarras.subSequence(24, 34) );
        digitavel.append(parcial);
        digitavel.append( modulo10( parcial ) );

        parcial = new StringBuilder();
        parcial.append(codigoBarras.subSequence(34, 44));
        digitavel.append(parcial);
        digitavel.append( modulo10(parcial));
        
        parcial = new StringBuilder();
        parcial.append(codigoBarras.subSequence(4, 19));
        digitavel.append(parcial);
        
        // formata para a representação numérica.
        StringBuilder formatado = new StringBuilder();
        for (int i = 0; i < 47; i++){
        	if (i == 4 || i == 14 || i == 25)
        		formatado.append(digitavel.charAt(i) + ".");
        	else if (i == 9 || i == 20 || i == 31 || i == 32)
        		formatado.append(digitavel.charAt(i) + " ");
        	else 
        		formatado.append(digitavel.charAt(i));
        }
        
        gru.setLinhaDigitavel(formatado.toString());
    }

	
	/**
    * Calcula o digito verificador de módulo 11, conforme manual da
    *  FEBRABAN e Tesouro Nacional
    * @param numero
    * @return
    */
    public static int modulo11(StringBuilder numero, boolean gruSimples){

    	int multiplicador = 2;
		int soma = 0;
		int k;
		int dgV;
		int resto;

		for (int i = numero.length() - 1; i >= 0; i--) {
			k = numero.charAt(i) - '0';
			soma += multiplicador * k;
			if (++multiplicador == 10)
				multiplicador = 2;
		}
		
		//Resto da soma das multiplicações dos 43 números
		resto = (soma % 11);                              
		//aqui, há uma diferença no cálculo para GRU Simples e GRU Cobrança
		if (resto == 1 || resto == 0)
			dgV = gruSimples ? 0 : 1;
		else                                 
			dgV = 11 - resto;                       
		return dgV;
    }
    
    /** Calcula o digito verificador de módulo 10, conforme manual da
     *  FEBRABAN e Tesouro Nacional
      * @param numero
      * @return
      */
     private static int modulo10(StringBuilder numero) {
     	int multiplicador = 2;
 		StringBuilder produtos = new StringBuilder();
 		// multiplica cada algarismo do número por 2,1,2,1,2,1... da direita pra esquerda.
 		for (int i = numero.length() - 1; i >= 0; i--) {
 			int algarismo = numero.charAt(i) - '0';
 			produtos.append(multiplicador * algarismo);
 			if (++multiplicador == 3)
 				multiplicador = 1;
 		}
 		
 		//soma os dígitos das multiplicações (não é a soma dos resultados).
 		int soma = 0;
 		for (int i = 0; i < produtos.length(); i++) {
 			soma += produtos.charAt(i) - '0';
 		}
 		
 		// dígito verificador
 		int dgV;
 		if (soma % 10 > 0) {
 			dgV = 10 - soma % 10;   
 		} else {
 			dgV = 0;
 		}
 		return dgV;
     }
     
     public static Collection<CodigoRecolhimentoGRU> getAllCodigoRecolhimentoGRU() throws DAOException {
    	 Collection<CodigoRecolhimentoGRU> lista;
    	 GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
    	 try {
    		 lista = dao.findAll(CodigoRecolhimentoGRU.class, "codigo", "asc");
    	 } finally {
    		 dao.close();
    	 }
    	 return lista;
     }

     /**
 	 * Gera o a Guia de Recolhimento da União Simples no formato PDF.
 	 * 
 	 * @param outputStream
 	 * @param idGRU
 	 * @param basePackage
 	 * @param novoVencimento Caso seja diferente de null, e posterior ao vencimento atual, o código de barras da GRU será recalculado.
 	 * @param persisteNovoVencimento caso true, se informado uma nova data de vencimento posterior a atual, ela será persistida.
 	 * @throws ArqException
     * @throws NegocioException 
 	 */
 	public static void gerarPDF(OutputStream outputStream, int idGRU, String basePackage, Date novoVencimento) throws ArqException, NegocioException {
 		try {
 			GuiaRecolhimentoUniao gru = getGRUByID(idGRU);
 			if (novoVencimento != null) {
 				gru = defineNovoVencimento(gru, novoVencimento);
 			}
 			if (gru != null) {
	 			// parâmetros do relatório
	 			Map<String, Object> map = new HashMap<String, Object>();
	 			
	 			ArrayList<GuiaRecolhimentoUniao> lista =  new ArrayList<GuiaRecolhimentoUniao>();
	 			lista.add(gru);
				map.put("nomeInstituicao", RepositorioDadosInstitucionais.get("nomeInstituicao").toUpperCase());
				map.put("siglaInstituicao", RepositorioDadosInstitucionais.get("siglaInstituicao").toUpperCase());
	 			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);
	 			InputStream report = null;
	 			report = JasperReportsUtil.getReport(basePackage, gru.getTipo().getNomeArquivoJasper());
	 			JasperPrint prt = JasperFillManager.fillReport(report, map, jrds);
	 			JasperExportManager.exportReportToPdfStream(prt, outputStream);
 			} else {
 				throw new NegocioException("Não foi possível encontrar os dados para gerar a GRU.");
 			}
 		} catch (JRException e) {
 			throw new ArqException(e);
 		}
 		
 	}
 	
 	/** Altera a data de vencimento de uma GRU.
 	 * @param gru GRU a alterar
 	 * @param novoVencimento Caso seja diferente de null, e posterior ao vencimento atual, o código de barras da GRU será recalculado.
 	 * @param persisteNovoVencimento caso true, se informado uma nova data de vencimento posterior a atual, ela será persistida.
 	 * @throws NegocioException 
 	 * @throws DAOException 
 	 */
 	public static GuiaRecolhimentoUniao defineNovoVencimento(GuiaRecolhimentoUniao gru, Date novoVencimento) throws NegocioException, DAOException {
 		if (gru != null && novoVencimento != null) {
 			AlteracaoGRU alteracao = new AlteracaoGRU(gru);
			gru.setVencimento(novoVencimento);
			geraCodigoBarras(gru);
			GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
			try {
				dao.update(gru);
				dao.create(alteracao);
			} finally {
				dao.close();
			}
 		}
 		return gru;
	}
 	
 	/**
 	 * Gera o a Guia de Recolhimento da União Simples no formato PDF para o SIGAA.
 	 * @param outputStream
 	 * @param idGRU
 	 * @throws ArqException
 	 * @throws NegocioException
 	 */
 	public static void gerarPDF(OutputStream outputStream, int idGRU) throws ArqException, NegocioException {
 		gerarPDF(outputStream, idGRU,BASE_REPORT_PACKAGE, null);
 	}

	/**
 	 * Gera o a Guia de Recolhimento da União Simples no formato PDF para o SIGAA.
 	 * @param outputStream
 	 * @param idGRU
 	 * @throws ArqException
 	 * @throws NegocioException
 	 */
 	public static void gerarPDF(OutputStream outputStream, int idGRU, Date novoVencimento) throws ArqException, NegocioException {
 		gerarPDF(outputStream, idGRU,BASE_REPORT_PACKAGE, novoVencimento);
 	}

	/** Retorna uma configuração de GRU de acordo com o tipo de arrecadação.
	 * @param idTipoArrecadacao
	 * @throws DAOException 
	 */
	public static ConfiguracaoGRU getConfiguracaoGRUByTipoArrecadacao(int idTipoArrecadacao, Integer idUnidade) throws DAOException {
		GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
		ConfiguracaoGRU configuracao;
		try {
			Criteria c = dao.getSession()
					.createCriteria(ConfiguracaoGRU.class)
					.add(Restrictions.eq("ativo", true));
			
			c.createCriteria("tipoArrecadacao").add(Restrictions.eq("id",idTipoArrecadacao));
			if (idUnidade != null)
				c.createCriteria("unidade").add(Restrictions.eq("id", idUnidade));
			configuracao = (ConfiguracaoGRU) c.uniqueResult();
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			dao.close();
		}
		return configuracao;
	}

	/** Retorna uma coleção de GRUs que foram quitadas
	 * @param idsGruQuitadas IDs das GRUs a verificar a quitação.
	 * @return
	 * @throws DAOException 
	 */
	public static Collection<Integer> isGRUQuitada(Collection<Integer> idsGruQuitadas) throws DAOException {
		if (isEmpty(idsGruQuitadas))
				return null;
		GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
		Collection<Integer> quitadas = null;
		try {
			Query q = dao.getSession().createQuery("select id from" +
					" GuiaRecolhimentoUniao gru" +
					" where gru.quitada = true" +
					" and id in "
					+ UFRNUtils.gerarStringIn(idsGruQuitadas));
			@SuppressWarnings("unchecked")
			Collection<Integer> lista = q.list();
			quitadas = lista;
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			dao.close();
		}
		return quitadas;
	}
	
	/** Monta a sequência do código de barras conforme manual da FEBRABAN e Tesouro Nacional
	 * @param gru
	 * @throws NegocioException
	 */
	public static void geraCodigoBarras(GuiaRecolhimentoUniao gru) throws NegocioException{
		if (gru.isGRUSimples())
				geraCodigoBarraSimples(gru);
			else
				geraCodigoBarrasCobranca(gru);		
	}
	
}
