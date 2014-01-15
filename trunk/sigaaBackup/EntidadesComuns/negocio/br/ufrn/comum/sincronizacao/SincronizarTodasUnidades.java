/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 06/04/2009
 */
package br.ufrn.comum.sincronizacao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.comum.dominio.AmbienteOrganizacionalUnidade;
import br.ufrn.comum.dominio.AreaAtuacaoUnidade;
import br.ufrn.comum.dominio.ClassificacaoUnidade;
import br.ufrn.comum.dominio.NivelOrganizacional;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.TipoUnidadeOrganizacional;
import br.ufrn.comum.dominio.UnidadeGeral;

/**
 * Sincroniza dados da tabela unidade, usa como base a tabela do SIPAC e joga para sistemas_comuns e sigaa
 * 
 * @author Raphaela Galhardo Fernandes
 * @author Gleydson Lima
 *
 */
public class SincronizarTodasUnidades {

	/** Conexão do banco com o qual se irá partir a sincronzizacao das unidades */
	private static Connection conOrigem;
	
	/** Conexão do banco de destino para onde vai ser feita a sincronzizacao das unidades */
	private static Connection conDestino;
	
	public SincronizarTodasUnidades() throws SQLException{
		
	}
	
	public static void main(String[] args) throws Exception {

		try{
			
			conOrigem = Database.getInstance().getSipacConnection();
			conDestino = Database.getInstance().getSigaaConnection();
			
			SincronizadorUnidades sincronizaDados = SincronizadorUnidades.usandoSistema(new MovimentoCadastro(), Sistema.SIGAA);
			
			String sqlConsulta = "select * from comum.unidade order by id_unidade";
			
			Statement stConsultaAdm = conOrigem.createStatement();
			
			ResultSet rs = stConsultaAdm.executeQuery(sqlConsulta);
			
			int i = 1;
			while (rs.next()){
				
				UnidadeGeral unidade = SincronizarTodasUnidades.getUnidade(rs);
				sincronizaDados.sincronizarUnidade(unidade);
				System.out.println(i++);
			}
			
			System.out.println("FIM");
		}finally{
			Database.getInstance().close(conOrigem);
			Database.getInstance().close(conDestino);
		}
		
	}
	
	private static UnidadeGeral getUnidade(ResultSet rs) throws SQLException{
		
		
		UnidadeGeral unidade = new UnidadeGeral(rs.getInt("id_unidade"));
		unidade.setAvaliacao(rs.getBoolean("avaliacao"));
		unidade.setCategoria(rs.getInt("categoria"));
		unidade.setCnpj(rs.getLong("cnpj"));
		unidade.setCodigoSiapecad(rs.getLong("codigo_siapecad"));
		unidade.setCodigo(rs.getLong("codigo_unidade"));
		unidade.setCompradora(rs.getBoolean("compradora"));
		unidade.setCompradoraEngenharia(rs.getBoolean("compradora_engenharia"));
		unidade.setDataCadastro(rs.getDate("data_cadastro"));
		unidade.setDataCriacao(rs.getDate("data_criacao"));
		unidade.setDataExtincao(rs.getDate("data_extincao"));
		unidade.setEmail(rs.getString("email"));
		unidade.setFuncaoRemunerada(rs.getBoolean("funcao_remunerada"));
		unidade.setGestoraFrequencia(rs.getBoolean("gestora_frequencia"));
		unidade.setHierarquia(rs.getString("hierarquia"));
		unidade.setHierarquiaOrganizacional(rs.getString("hierarquia_organizacional"));
		unidade.setAmbienteOrganizacional(new AmbienteOrganizacionalUnidade());
		unidade.getAmbienteOrganizacional().setId(rs.getInt("id_ambiente_organizacional"));
		unidade.setAreaAtuacao(new AreaAtuacaoUnidade());
		unidade.getAreaAtuacao().setId(rs.getInt("id_area_atuacao"));
		unidade.setClassificacaoUnidade(new ClassificacaoUnidade());
		unidade.getClassificacaoUnidade().setId(rs.getInt("id_classificacao_unidade"));
		unidade.setGestora(new UnidadeGeral(rs.getInt("id_gestora")));
		unidade.setGestoraAcademica(new UnidadeGeral(rs.getInt("id_gestora_academica")));
		unidade.setNivelOrganizacional(new NivelOrganizacional());
		unidade.getNivelOrganizacional().setId(rs.getInt("id_nivel_organizacional"));
		unidade.setIdResponsavelPatrimonial(rs.getInt("id_responsavel"));
		unidade.setTipoOrganizacional(new TipoUnidadeOrganizacional());
		unidade.getTipoOrganizacional().setId(rs.getInt("id_tipo_organizacional"));
		unidade.setResponsavelOrganizacional(new UnidadeGeral(rs.getInt("id_unid_resp_org")));
		unidade.setIdUsuarioCadastro(rs.getInt("id_usuario_cadastro"));
		unidade.setMetas(rs.getBoolean("metas"));
		unidade.setNome(rs.getString("nome"));
		unidade.setNomeAscii(rs.getString("nome_ascii"));
		unidade.setNomeCapa(rs.getString("nome_capa"));
		unidade.setCodigoUnidadeGestoraSIAFI(rs.getInt("codigo_unidade_gestora_siafi"));
		unidade.setCodigoGestaoSIAFI(rs.getInt("codigo_gestao_siafi"));
		unidade.setOrganizacional(rs.getBoolean("organizacional"));
		unidade.setPrazoEnvioBolsaFim(rs.getInt("prazo_envio_bolsa_fim"));
		unidade.setPrazoEnvioBolsaInicio(rs.getInt("prazo_envio_bolsa_inicio"));
		unidade.setPresidenteComissao(rs.getString("presidente_comissao"));
		unidade.setSequenciaModalidadeCompra(rs.getInt("sequencia_modalidade_compra"));
		unidade.setSigla(rs.getString("sigla"));
		unidade.setSiglaAcademica(rs.getString("sigla_academica"));
		unidade.setUnidadeSipac(rs.getBoolean("sipac"));
		unidade.setSubmetePropostaExtensao(rs.getBoolean("submete_proposta_extensao"));
		unidade.setTelefone(rs.getString("telefones"));
		unidade.setTemplateParecerDL(rs.getInt("template_parecer_dl"));
		unidade.setTipo(rs.getInt("tipo"));
		unidade.setTipoAcademica(rs.getInt("tipo_academica"));
		unidade.setTipo(rs.getInt("tipo"));
		unidade.setTipoFuncaoRemunerada(rs.getInt("tipo_funcao_remunerada"));
		unidade.setUnidadeAcademica(rs.getBoolean("unidade_academica"));
		unidade.setUnidadeOrcamentaria(rs.getBoolean("unidade_orcamentaria"));
		unidade.setUnidadeResponsavel(new UnidadeGeral(rs.getInt("unidade_responsavel")));
		unidade.setEndereco(rs.getString("endereco"));
		unidade.setUf(rs.getString("uf"));
		unidade.setCep(rs.getString("cep"));
		
		return unidade;
	} 

}

