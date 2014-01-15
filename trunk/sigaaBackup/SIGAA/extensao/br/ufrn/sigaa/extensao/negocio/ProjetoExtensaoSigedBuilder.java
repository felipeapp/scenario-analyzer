/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 26/08/2011
 */
package br.ufrn.sigaa.extensao.negocio;

import java.util.ArrayList;
import java.util.Map;

import br.ufrn.comum.dominio.ConfiguracaoOperacaoSiged;
import br.ufrn.integracao.siged.dto.DescritorDTO;
import br.ufrn.integracao.siged.dto.DocumentoDTO;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.siged.integracao.DocumentoSigedBuilder;

/**
 * Classe utilizada para a construção de um documento do SIGED para o armazenamento
 * de informações de projetos de extensão.
 * 
 * @author David Pereira
 *
 */
public class ProjetoExtensaoSigedBuilder implements DocumentoSigedBuilder {

	@Override
	public DocumentoDTO build(ConfiguracaoOperacaoSiged config, Map<String, Object> params) {
		
		AtividadeExtensao atividade = (AtividadeExtensao) params.get("atividade");
		
		//Pasta raiz do Boletim de Serviço no SIGED
	    DocumentoDTO documento = new DocumentoDTO();
	    documento.setTipoDocumento(config.getIdTipoDocumento());
	    documento.setPastaId(String.format(config.getIdPastaRaiz(), atividade.getAno()));
	    documento.setPastaLabel(String.format(config.getPastaLabelRaiz(), atividade.getAno()));
	    documento.setDescritores(new ArrayList<DescritorDTO>());
	    
	    DescritorDTO descritorTipo = config.getDescritor("tipo");
	    descritorTipo.setValor(String.valueOf(atividade.getTipoAtividadeExtensao().getDescricao()));
	    documento.getDescritores().add(descritorTipo);
	    
	    DescritorDTO descritorTitulo = config.getDescritor("titulo");
	    descritorTitulo.setValor(String.valueOf(atividade.getTitulo()));
	    documento.getDescritores().add(descritorTitulo);
	    
	    DescritorDTO descritorAno = config.getDescritor("ano");
	    descritorAno.setValor(String.valueOf(atividade.getAno()));
	    documento.getDescritores().add(descritorAno);
	    
	    DescritorDTO descritorCodigo = config.getDescritor("codigo");
	    descritorCodigo.setValor(String.valueOf(atividade.getCodigo()));
	    documento.getDescritores().add(descritorCodigo);
	    
	    DescritorDTO descritorDescricao = config.getDescritor("descricao");
	    descritorDescricao.setValor(atividade.getProjeto().getDescricao());
	    documento.getDescritores().add(descritorDescricao);	    
	    
	    String palavrasChaves = "Atividade de Extensão;" + atividade.getTipoAtividadeExtensao().getDescricao() +  ";" + 
	    		atividade.getAno() + ";" + atividade.getTitulo();
	    
	    documento.setPalavrasChaves(palavrasChaves);
	    
	    documento.setLabel("Projeto de Extensão: " + atividade.getCodigo());
	    
		return documento;
	}

}
