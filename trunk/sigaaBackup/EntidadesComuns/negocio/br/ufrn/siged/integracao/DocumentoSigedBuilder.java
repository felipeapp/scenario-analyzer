package br.ufrn.siged.integracao;

import java.util.Map;

import br.ufrn.comum.dominio.ConfiguracaoOperacaoSiged;
import br.ufrn.integracao.siged.dto.DocumentoDTO;

/**
 *  Interface padr�o para constru��o das caracter�sticas de um documento do SIGED.
 *  No m�todo build cada opera��o registrada deve configurar as pastas e os descritores do documento.
 * 
 * @author Gleydson
 *
 */
public interface DocumentoSigedBuilder {

	public DocumentoDTO build(ConfiguracaoOperacaoSiged config, Map<String,Object> args);
		
}
