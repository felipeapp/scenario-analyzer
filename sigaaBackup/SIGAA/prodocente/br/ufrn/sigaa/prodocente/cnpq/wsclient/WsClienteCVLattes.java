/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '04/03/2013'
 *
 */
package br.ufrn.sigaa.prodocente.cnpq.wsclient;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.axis.client.Service;

import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.parametros.dominio.ParametrosProdocente;
import br.ufrn.sigaa.prodocente.cnpq.wsclient.dominio.WSCurriculoBindingStub;
import br.ufrn.sigaa.prodocente.lattes.dominio.PessoaLattes;

/**
 * Cliente web service que acessa as operações disponibilizadas pelo CNPq para obtenção dos
 * dados do Currículo Lattes dos pesquisadores da instituição.
 *  
 * @author Leonardo Campos
 *
 */
public class WsClienteCVLattes {

	/** Constante contendo a URL do Web Service para extração de currículos lattes do CNPq. */
	public static final String URL_WS_CURRICULO = ParametroHelper.getInstance().getParametro(ParametrosProdocente.URL_WS_CURRICULO_LATTES);
	/** Constante contendo o usuário do Web Service para extração de currículos lattes do CNPq. */
	public static final String USER_WS_CURRICULO = ParametroHelper.getInstance().getParametro(ParametrosProdocente.USER_WS_CURRICULO_LATTES);
	/** Constante contendo a senha do Web Service para extração de currículos lattes do CNPq. */
	public static final String PASSWD_WS_CURRICULO =  lerArquivo(ParametroHelper.getInstance().getParametro(ParametrosProdocente.PASSWD_WS_CURRICULO_LATTES));

	private static String lerArquivo(String arquivo) {
		File f = new File(arquivo);
		String key = "";
		try {
			if(!f.exists())	{
				return null;
			}
			BufferedReader in = new BufferedReader(new FileReader(f));
			String linha;
			while((linha = in.readLine())!=null){
				key = linha;
			}
			in.close();
			return key;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally{
			
		}
	}
	
	/**
	 * Sincroniza os identificadores do CNPq obtendo-os a partir do CPF.
	 * 
	 * @param lista
	 * @throws Exception
	 */
	public static void sincronizarIdentificadoresCNPq(List<PessoaLattes> lista) throws Exception {
		for(PessoaLattes pl: lista){
			if(isEmpty(pl.getIdCnpq()) && !isEmpty(pl.getPessoa().getCpf_cnpjString())) {
				URL url = new URL(URL_WS_CURRICULO);
				Service service = new Service();
				WSCurriculoBindingStub stub = new WSCurriculoBindingStub(url, service);
				stub.setUsername(USER_WS_CURRICULO);
				stub.setPassword(PASSWD_WS_CURRICULO);
				
				String cpfSemPontoTraco = pl.getPessoa().getCpf_cnpjString().replace(".", "").replace("-", "");
				String identificadorCNPq = stub.getIdentificadorCNPq(cpfSemPontoTraco, null, null);
				try {
					Long.parseLong(identificadorCNPq);
				} catch (NumberFormatException e) {
					String erro = "Erro ao obter Identificador CNPq: " + identificadorCNPq;
					System.err.println(erro);
					pl.setObservacoes(erro);
					pl.setAtualizar(Boolean.FALSE);
					continue;
				}
				pl.setAtualizar(Boolean.TRUE);
				pl.setIdCnpq(identificadorCNPq);
			}
		}
	}

	/**
	 * Confere as datas de última atualização dos currículos para indentificação
	 * de quais devem ser recuperados para atualização da base de dados local. 
	 * 
	 * @param lista
	 * @throws Exception
	 */
	public static void verificarDatasUltimaAtualizacao(List<PessoaLattes> lista) throws Exception {
		for(PessoaLattes pl: lista) {
			if(!isEmpty(pl.getIdCnpq())){
				URL url = new URL(URL_WS_CURRICULO);
				Service service = new Service();
				WSCurriculoBindingStub stub = new WSCurriculoBindingStub(url, service);
				stub.setUsername(USER_WS_CURRICULO);
				stub.setPassword(PASSWD_WS_CURRICULO);
				
				String ultimaAtualizacaoCnpq = stub.getDataAtualizacaoCV(pl.getIdCnpq());
				if(!isEmpty(ultimaAtualizacaoCnpq) ){
					Date dataUltimaAtualizacaoCnpq = Formatador.getInstance().parseDataHoraSec(ultimaAtualizacaoCnpq);
					if(isEmpty(dataUltimaAtualizacaoCnpq)){
						String erro = "Erro ao obter Data Atualização CV: " + ultimaAtualizacaoCnpq;
						System.err.println(erro);
						pl.setObservacoes(erro);
						pl.setAtualizar(Boolean.FALSE);
						continue;
					}
					if(isEmpty(pl.getUltimaAtualizacao()) || dataUltimaAtualizacaoCnpq.compareTo(pl.getUltimaAtualizacao()) > 0){
						pl.setUltimaAtualizacao(dataUltimaAtualizacaoCnpq);
						pl.setAtualizar(Boolean.TRUE);
					} else
						pl.setAtualizar(Boolean.FALSE);
				}
			}
		}
	}
	
	/**
	 * Recupera e armazena os currículos compactados para importação.
	 * 
	 * @param lista
	 * @throws Exception
	 */
	public static void extrairCurriculos(List<PessoaLattes> lista) throws Exception {
		for(PessoaLattes pl: lista) {
			if(!isEmpty(pl.getAtualizar()) && pl.getAtualizar() && !isEmpty(pl.getIdCnpq())){
				URL url = new URL(URL_WS_CURRICULO);
				Service service = new Service();
				WSCurriculoBindingStub stub = new WSCurriculoBindingStub(url, service);
				stub.setUsername(USER_WS_CURRICULO);
				stub.setPassword(PASSWD_WS_CURRICULO);
				
				byte[] cvzip = stub.getCurriculoCompactado(pl.getIdCnpq());
				pl.setCvZipado(cvzip);
			}
		}
	}
}
