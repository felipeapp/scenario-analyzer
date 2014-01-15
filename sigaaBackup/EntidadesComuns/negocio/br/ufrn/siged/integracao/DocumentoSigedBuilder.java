package br.ufrn.siged.integracao;

import java.util.Map;

import br.ufrn.comum.dominio.ConfiguracaoOperacaoSiged;
import br.ufrn.integracao.siged.dto.DocumentoDTO;

/**
 *  Interface padrão para construção das características de um documento do SIGED.
 *  No método build cada operação registrada deve configurar as pastas e os descritores do documento.
 * 
 * @author Gleydson
 *
 */
public interface DocumentoSigedBuilder {

	public DocumentoDTO build(ConfiguracaoOperacaoSiged config, Map<String,Object> args);
		
}
