package br.ufrn.integracao.biometria;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.remoting.RemoteAccessException;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.integracao.interfaces.IdentificacaoBiometricaRemoteService;

import com.griaule.grfingerjava.GrFingerJavaException;
import com.griaule.grfingerjava.MatchingContext;
import com.griaule.grfingerjava.Template;

/**
 * Classe usada para opera��es de armazenamento e busca de biometria.
 * 
 * @author Gleydson Lima
 * @author David Pereira
 * @autor Jadson
 * @version 1.1 - alterando a l�gica de identifica��o das digitais para guardar em cache das digitais cadastradas no sistema, j� que � 
 *     preciso possuir todas para ralizar a ver
 */
@WebService
public class IdentificacaoDigital implements IdentificacaoBiometricaRemoteService {

	/**
	 * A consulta padr�o utilizada para recuperar as informa��es das digitais cadastradas no sistema.
	 */
	private static final String CONSULTA_PADRAO = " SELECT cpf, dedo_coletado, digital, data_cadastro FROM comum.identificacao_pessoa ";
	
	/**
	 * Lista que guarda um cache das digitas, porque buscar todas a todo tempo fica invi�vel
	 */
	private static List<IdentificacaoBiometria> digitais;

	/**
	 * A tempo em que o cache das digitais foi atualizado pela �ltima vez 
	 */
	private static Date lastRefreshTime;
	
	/**
	 * <p>Guarda a data da �ltima digital alterada, recuperar todas as digitais a partir dessa data.</p>
	 * 
	 * <p>  Essa regras tem que ser seguida em todo lugar do sistema para a l�gica do cache funcionar: 
	 * 		<strong> Quando uma digital � atualizada, a data de cadastro tamb�m � atualizada. 
	 *    </strong> 
	 * </p>
	 * 
	 * <p>    
	 *    @see IdentificacaoPessoaRemoteServiceImpl#gravarOuAtualizarIdentificacao()
	 *    @see PessoaDao#updateIdentificacao()
	 *    
	 * </p>
	 */
	private static Date dataUltimaAlteracao;
	
	
	/**
	 * Tempo EM SEGUNDOS que o sistema vai verificar se existem novas digitais no cache
	 */
	private static final int REFRESH_TIME_CACHE_DIGITAIS = 30;
	
	
	
	
	/**
	 * <p>Utilizado para comprarar as digitais</p>
	 * 
	 * 
	 * <p>Fingerprint SDK context used for capture / extraction / matching of
	 * fingerprints./p>
	 * 
	 * 
	 * 
	 */
	private MatchingContext fingerprintSDK;

	
	
	
	/**
	 * M�todo que deve ser utilizado para realizar a identifia��o de uma digital
	 * @see br.ufrn.integracao.interfaces.IdentificacaoBiometricaRemoteService#identificar(byte[])
	 */
	@Override
	public long identificar(byte[] digital) {
		return identify(digital);
	}

	
	
	
	
	/**
	 * Identifies the current fingerprint on the DB.
	 */
	private long identify(byte[] digital) {

		
		/* ********************************************************************
		 *  C�digo comum a todos os usu�rios                                  *
		 * ********************************************************************/
		
		if(digitais == null  ||  expirouTempoAtualizaCache()   ){
			
			/* ********************************************************************
			 *  C�digo executa um usu�rio por vez                                 *
			 * ********************************************************************/
			carregaDigitais(); // executa um usu�rio por vez
		}
		
		
		
		/* *****************************************************************************************************************
		 *  Percorre todas as digitais que est�o em mem�rio para verifica se alguma casa com a digital captura do usu�rio   *
		 * *****************************************************************************************************************/
		
		
		try {
			fingerprintSDK = new MatchingContext();
		} catch (GrFingerJavaException e1) {
			throw new RemoteAccessException(e1.getMessage());
		}

		Template template = new Template(digital);

		// Starts identification process by supplying query template.
		try {
			fingerprintSDK.prepareForIdentification(template);
		} catch (GrFingerJavaException e1) {
			throw new RemoteAccessException(e1.getMessage());
		}

		try {

			for (IdentificacaoBiometria id : digitais) {
				// Reads the current template data on a buffer
				byte[] templateBuffer = id.getDigital();
				// And creates a new Template
				Template referenceTemplate = new Template(templateBuffer);

				// Compares current template.
				boolean matched = fingerprintSDK.identify(referenceTemplate);

				// If the templates match, display matching
				// minutiae/segments/directions.
				if (matched) {
					return id.getCpf();
				}
			}

		} catch (GrFingerJavaException e) {
			e.printStackTrace();
			return 0;
		} finally {
			try {
				fingerprintSDK.destroy();
			} catch (GrFingerJavaException e) {
				e.printStackTrace();
			}
		}
		
		return -1;

	}

	/**
	 * M�todo que verifica se j� est� no tempo de atualizar o cache
	 *
	 * @return
	 */
	private boolean expirouTempoAtualizaCache(){
		if(lastRefreshTime == null) 
			return true;
		else 
			return  ( System.currentTimeMillis() - lastRefreshTime.getTime() ) > ( REFRESH_TIME_CACHE_DIGITAIS * 1000 );
	}
	
	
	
	/**
	 *  <p>Carrega as  digitais dos usu�rio para manter em cache e otimizar a valida��o das digitais, j� que � preciso 
	 *  possuir todas as digitais para realizar a verifica��o.</p>
	 *  
	 *  <p>Obs.: Apenas uma thread por vez executa nesse m�todo para n�o ter problemas de concorrencia na atualiza��o dos dados comuns a todos os usu�rios.</p>
	 *
	 */
	@SuppressWarnings("unchecked")
	public synchronized static void carregaDigitais() {

		System.out.println("Entrando na regi�o cr�tica ... ");
		
		if ( digitais == null ) { // Busca todas as digitais do sistema e coloca em cache na mem�ria
			
			digitais = new ArrayList<IdentificacaoBiometria>();
			
			JdbcTemplate jt = new JdbcTemplate(Database.getInstance().getComumDs());
			
			List<Map<String, Object>> digitaisQuery = jt.queryForList(CONSULTA_PADRAO);
			
			for (Map<String, Object> m : digitaisQuery) {
				
				Long cpf = (Long) m.get("cpf");
				String dedoCadastrado = (String) m.get("dedo_coletado");
				byte[] digital = (byte[]) m.get("digital");
				Date dataCadastroDigital = (Date) m.get("data_cadastro");
				
				atualizaUltimaDataCadastroDigital(dataCadastroDigital);
				
				digitais.add( new IdentificacaoBiometria( cpf, dedoCadastrado, digital, dataCadastroDigital ) );
			}
			
		}else{ // se expirou o tempo de atualizar o cache
			
			JdbcTemplate jt = new JdbcTemplate(Database.getInstance().getComumDs());
			
			String consultaAtualizaCache = "";
			
			if(dataUltimaAlteracao == null){
				consultaAtualizaCache = CONSULTA_PADRAO;
			}else{
				consultaAtualizaCache = CONSULTA_PADRAO+" WHERE data_cadastro >  '"+dataUltimaAlteracao+"'"; // S� novas digitais ou as atualizadas
			}
			
			List<Map<String, Object>> digitaisQuery = jt.queryForList(consultaAtualizaCache);
			
			// Para todas digitais cadastradas ou atualizadas depois que a rotina executou pela ?ltima vez
			for (Map<String, Object> m : digitaisQuery) {
				
				Long cpf = (Long) m.get("cpf");
				String dedoCadastrado = (String) m.get("dedo_coletado");
				byte[] digital = (byte[]) m.get("digital");
				Date dataCadastroDigital = (Date) m.get("data_cadastro");
				
				atualizaUltimaDataCadastroDigital(dataCadastroDigital);
				
				IdentificacaoBiometria iTemp = new IdentificacaoBiometria(cpf, dedoCadastrado);
				
				if(digitais.contains( iTemp )){ // A digital foi atualizada pelo usu�rio
				
					
					iTemp = digitais.get( digitais.indexOf( iTemp ));
					
					iTemp.setDigital(digital);
					iTemp.setDataCadastro( dataCadastroDigital );
					
				}else{ // Uma nova digital foi cadastrada no sistema
					
					digitais.add( new IdentificacaoBiometria( cpf, dedoCadastrado, digital, dataCadastroDigital ) );
				}
				
			}
			
			
			
		}
		
		lastRefreshTime = new Date();
		
		IdentificacaoDigital.class.notifyAll(); // libera para outros thread entrarem na regi�o cr�tica	
		
		System.out.println("Saindo na regi�o cr�tica ! ");
	}
	
	
	/**
	 * Atualiza a data da �ltima digital cadastrada no sistema
	 *
	 * @param dataCadastroDigital
	 */
	private static void atualizaUltimaDataCadastroDigital(Date dataCadastroDigital){
		if(dataUltimaAlteracao == null)
			dataUltimaAlteracao = dataCadastroDigital;
		else{
			if(  dataCadastroDigital.after(dataUltimaAlteracao) )
				dataUltimaAlteracao = dataCadastroDigital;
		}
	}

}