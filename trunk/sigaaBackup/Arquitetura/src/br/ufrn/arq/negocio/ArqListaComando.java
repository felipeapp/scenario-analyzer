/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 16/04/2007
 */
package br.ufrn.arq.negocio;

import br.ufrn.arq.dominio.Comando;

/**
 * Comandos da arquitetura
 *
 * @author Gleydson Lima
 *
 */
public class ArqListaComando {

	public static final String PREFIX = "br.ufrn.";

	/** Movimento de Liberação de um comando */
	public static final Comando PREPARE_MOVIMENTO = new Comando(50, null);

	/** Comando para cadastro do usuário pelo próprio usuário */
	public static final int CADASTRA_USUARIO_AUTO_COD = 11004;
	public static final Comando CADASTRA_USUARIO_AUTO = new Comando(11004, PREFIX + "admin.negocio.ProcessadorAutoCadastroUsuario");
	public static final Comando CONFIRMAR_AUTO_CADASTRO_USUARIO = new Comando(11005, PREFIX + "admin.negocio.ProcessadorAutoCadastroUsuario");
	
	public static final int LOGOFF_COD = 52;
	public static final int LOGON_COD = 51;
	public static final int LOGAR_COMO_COD = 53;

	// GERAL
	public static final Comando CADASTRAR = new Comando(1001, PREFIX + "arq.negocio.ProcessadorCadastro");
	public static final Comando ALTERAR = new Comando(1002, PREFIX + "arq.negocio.ProcessadorCadastro");
	public static final Comando REMOVER = new Comando(1003, PREFIX + "arq.negocio.ProcessadorCadastro");
	public static final Comando DESATIVAR = new Comando(1004, PREFIX + "arq.negocio.ProcessadorCadastro");
	public static final Comando CADASTRAR_PASSAPORTE = new Comando(1001, PREFIX + "arq.negocio.ProcessadorPassaporte");

	public static final Comando MENSAGEM_LIDA = new Comando(1005, PREFIX + "arq.negocio.ProcessadorMensagemBean");
	public static final int MENSAGEM_LIDA_COD = 1005;
	public static final Comando MENSAGEM_ENVIAR = new Comando(1006, PREFIX + "arq.negocio.ProcessadorMensagemBean");
	public static final int MENSAGEM_ENVIAR_COD = 1006;

	public static final Comando MENSAGEM_EXCLUIR = new Comando(1007, PREFIX + "arq.negocio.ProcessadorMensagemBean");
	public static final int MENSAGEM_EXCLUIR_COD = 1007;

	public static final Comando MENSAGEM_EXCLUIR_LOTE = new Comando(1008, PREFIX + "arq.negocio.ProcessadorMensagemBean");
	public static final int MENSAGEM_EXCLUIR_LOTE_COD = 1008;
	
	public static final Comando MENSAGEM_EXCLUIR_LOTE_LIXEIRA = new Comando(1009, PREFIX + "arq.negocio.ProcessadorMensagemBean");
	public static final int MENSAGEM_EXCLUIR_LOTE_LIXEIRA_COD = 1009;
	
	public static final Comando MENSAGEM_EXCLUIR_TODAS_LIXEIRA = new Comando(1010, PREFIX + "arq.negocio.ProcessadorMensagemBean");
	public static final int MENSAGEM_EXCLUIR_TODAS_LIXEIRA_COD = 1010;
	
	public static final Comando MENSAGEM_MARCAR_LIDA_LOTE = new Comando(1011, PREFIX + "arq.negocio.ProcessadorMensagemBean");
	public static final int MENSAGEM_MARCAR_LIDA_LOTE_COD = 1011;
	
	public static final Comando MENSAGEM_MARCAR_TODAS_LIDA = new Comando(1012, PREFIX + "arq.negocio.ProcessadorMensagemBean");
	public static final int MENSAGEM_MARCAR_TODAS_LIDA_COD = 1012;
	
	public static final Comando MENSAGEM_MARCAR_COMO_NAO_LIDA = new Comando(1013, PREFIX + "arq.negocio.ProcessadorMensagemBean");
	public static final int  MENSAGEM_MARCAR_COMO_NAO_LIDA_COD = 1013;
	
	public static final Comando GERAR_EMISSAO_DOCUMENTO_AUTENTICADO = new Comando(2010, PREFIX + "arq.seguranca.autenticacao.ProcessadorGeracaoEmissao");
	
	
	/**
	 * Portal do Docente
	 */
	public static final Comando ATUALIZAR_PERFIL_SERVIDOR 		= new Comando(12001, PREFIX + "portal.negocio.ProcessadorPerfilDocente");

	public static final Comando ATUALIZAR_PERFIL = new Comando(12002, PREFIX + "arq.negocio.ProcessadorPerfilPessoa");

//	COMANDOS DE USUARIO
	public static final Comando ALTERAR_SENHA_USUARIO	= new Comando(12500, "br.ufrn.sigrh.arq.negocio.ProcessadorUsuario");

	public static final Comando PROCESSAR_ERRO	= new Comando(12505, "br.ufrn.arq.erros.gerencia.ProcessadorErro");
	
	
	public static final Comando LOGON_AMBIENTE = new Comando(3001, "br.ufrn.ambientes.negocio.ProcessadorLogonAmbiente");
	
	/**
	 * Solicitação de inclusão de bolsas acadêmicas
	 */
	public static final Comando SOLICITAR_INCLUSAO_BOLSA_ACADEMICA = new Comando(12600, PREFIX + "sigaa.bolsas.negocio.ProcessadorBolsaAcademica");
	public static final Comando SOLICITAR_EXCLUSAO_BOLSA_ACADEMICA = new Comando(12601, PREFIX + "sigaa.bolsas.negocio.ProcessadorBolsaAcademica");
	public static final Comando ATUALIZAR_BOLSA_ACADEMICA = new Comando(12602, PREFIX + "sigaa.bolsas.negocio.ProcessadorBolsaAcademica");

	
}