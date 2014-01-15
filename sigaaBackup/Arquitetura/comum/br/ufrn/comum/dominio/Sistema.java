/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 01/11/2007
 */
package br.ufrn.comum.dominio;

import java.util.HashMap;
import java.util.Map;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Constantes usadas para identificar o sistema que requisita a arquitetura
 *
 * @author Gleydson Lima
 *
 */
public class Sistema implements PersistDB {

	private static Map<Integer, Boolean> SISTEMAS_ATIVOS = new HashMap<Integer, Boolean>();
	private static Map<Integer, Boolean> CAIXA_POSTAL_ATIVA = new HashMap<Integer, Boolean>();
	private static Map<Integer, Boolean> MEMORANDOS_ATIVOS = new HashMap<Integer, Boolean>();
	private static Map<Integer, Boolean> QUESTIONARIOS_ATIVOS = new HashMap<Integer, Boolean>();
	private static Map<Integer, Boolean> PORTAL_PUBLICO_ATIVO = new HashMap<Integer, Boolean>();
	
	public static final int SIPAC = 1;

	public static final int SIGAA = 2;

	public static final int COMUM = 3;

	public static final int SCO = 4;

	public static final int PROTOCOLO = 5;

	public static final int IPROJECT = 6;

	public static final int SIGRH = 7;

	public static final int SIGADMIN = 8;
	
	public static final int SIGED = 9;
	
	public static final int AMBIENTES = 10;
	
	public static final int PORTAIS = 11;
	
	public static final int SIGPP = 12;
	
	public static final int SIGELEICAO = 13;
	
	public static final int SIGCONCURSO = 14;
	
	public static final int TRANSPARENCIA_ATIVA = 15;

	public static final int SIGEVENTO = 16;
	
	public static final int SISTEMAS_GOVERNO_FEDERAL = 1001;
	
	public static final int SUCUPIRA = 17;
	
	public static final int REDE_SOCIAL = 18;
	
	public static final int INSTITUCIONAL = 19;
	
	private int id;
	
	/** Nome do sistema */
	private String nome;

	/** Se o sistema está ativo ou não */
	private boolean ativo;

	/** Indica se um sistema está em manutenção */
	private boolean manutencao;
	
	/** Indica se a caixa postal está ativa ou não no sistema */
	private boolean caixaPostalAtiva;
	
	/** Indica se os memorandos eletrônicos estão ativos ou não no sistema */
	private boolean memorandosEletronicosAtivos;
	
	/** Nome JNDI do datasource que será utilizado para acesso ao banco de dados do sistema */
	private String nomeDataSourceJndi;
	
	/** Caminho para o arquivo de configuração do hibernate do sistema */
	private String caminhoArquivoConfiguracaoHibernate;
	
	/** Indica se o sistema será controlado no que diz respeito a se seus usuários estão online ou não */
	private boolean controlarUsuariosOnline;
	
	/** Indica se o portal público está ativo para o sistema em questão */
	private boolean portalPublicoAtivo;
	
	public Sistema() {
	}
	
	public Sistema(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	@Override
	public String toString() {
		return nome != null ? nome : "";
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isManutencao() {
		return manutencao;
	}

	public void setManutencao(boolean manutencao) {
		this.manutencao = manutencao;
	}
	
	public static boolean isSigaaAtivo() {
		return isSistemaAtivo(SIGAA);
	}
	
	public static boolean isAdministrativoAtivo() {
		return isSipacAtivo() || isSigrhAtivo();
	}
	
	public static int getSistemaAdministrativoAtivo() {
		if (isSipacAtivo()) return SIPAC;
		else if (isSigrhAtivo()) return SIGRH;
		return 0;
	}
	
	public static boolean isSipacAtivo() {
		return isSistemaAtivo(SIPAC);
	}
	
	public static boolean isSigEleicaoAtivo() {
		return isSistemaAtivo(SIGELEICAO);
	}
	
	public static boolean isSigrhAtivo() {
		return isSistemaAtivo(SIGRH);
	}
	
	public static boolean isSigppAtivo() {
		return isSistemaAtivo(SIGPP);
	}
	
	public static boolean isSigadminAtivo() {
		return isSistemaAtivo(SIGADMIN);
	}
	
	public static boolean isSigedAtivo() {
		return isSistemaAtivo(SIGED);
	}
	
	public static boolean isPortaisAtivo() {
		return isSistemaAtivo(PORTAIS);
	}
	
	public static boolean isIprojectAtivo() {
		return isSistemaAtivo(IPROJECT);
	}
	
	public static boolean isProtocoloAtivo() {
		return isSistemaAtivo(PROTOCOLO);
	}

	public static boolean isSistemaAtivo(int sistema) {
		if (SISTEMAS_ATIVOS.get(sistema) == null)
			return true;
		return SISTEMAS_ATIVOS.get(sistema);		
	}
	
	public static boolean isCaixaPostalAtiva(int sistema) {
		if (CAIXA_POSTAL_ATIVA.get(sistema) == null)
			return true;
		return CAIXA_POSTAL_ATIVA.get(sistema);
	}
	
	public static boolean isMemorandosAtivos(int sistema) {
		if (MEMORANDOS_ATIVOS.get(sistema) == null)
			return true;
		return MEMORANDOS_ATIVOS.get(sistema);
	}
	
	public static boolean isQuestionariosAtivos(int sistema) {
		if (QUESTIONARIOS_ATIVOS.get(sistema) == null)
			return true;
		return QUESTIONARIOS_ATIVOS.get(sistema);
	}
	
	public static boolean isPortalPublicoAtivo(int sistema) {
		if (PORTAL_PUBLICO_ATIVO.get(sistema) == null)
			return true;
		return PORTAL_PUBLICO_ATIVO.get(sistema);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sistema other = (Sistema) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public boolean isCaixaPostalAtiva() {
		return caixaPostalAtiva;
	}

	public void setCaixaPostalAtiva(boolean caixaPostalAtiva) {
		this.caixaPostalAtiva = caixaPostalAtiva;
	}

	public boolean isMemorandosEletronicosAtivos() {
		return memorandosEletronicosAtivos;
	}

	public void setMemorandosEletronicosAtivos(boolean memorandosEletronicosAtivos) {
		this.memorandosEletronicosAtivos = memorandosEletronicosAtivos;
	}

	public String getNomeDataSourceJndi() {
		return nomeDataSourceJndi;
	}

	public void setNomeDataSourceJndi(String nomeDataSourceJndi) {
		this.nomeDataSourceJndi = nomeDataSourceJndi;
	}

	public String getCaminhoArquivoConfiguracaoHibernate() {
		return caminhoArquivoConfiguracaoHibernate;
	}

	public void setCaminhoArquivoConfiguracaoHibernate(String caminhoArquivoConfiguracaoHibernate) {
		this.caminhoArquivoConfiguracaoHibernate = caminhoArquivoConfiguracaoHibernate;
	}

	public boolean isControlarUsuariosOnline() {
		return controlarUsuariosOnline;
	}

	public void setControlarUsuariosOnline(boolean controlarUsuariosOnline) {
		this.controlarUsuariosOnline = controlarUsuariosOnline;
	}
	
	public boolean isPortalPublicoAtivo() {
		return portalPublicoAtivo;
	}

	public void setPortalPublicoAtivo(boolean portalPublicoAtivo) {
		this.portalPublicoAtivo = portalPublicoAtivo;
	}

	public static void setSistemasAtivos(Map<Integer, Boolean> sistemasAtivos) {
		SISTEMAS_ATIVOS = sistemasAtivos;
	}
	
	public static void setCaixaPostalAtiva(Map<Integer, Boolean> caixaPostalAtiva) {
		CAIXA_POSTAL_ATIVA = caixaPostalAtiva;
	}
	
	public static void setMemorandosAtivos(Map<Integer, Boolean> memorandosAtivos) {
		MEMORANDOS_ATIVOS = memorandosAtivos;
	}
	
	public static void setQuestionariosAtivos(Map<Integer, Boolean> questionariosAtivos) {
		QUESTIONARIOS_ATIVOS = questionariosAtivos;
	}
	
	public static void setPortalPublicoAtivo(Map<Integer, Boolean> portalPublicoAtivo) {
		PORTAL_PUBLICO_ATIVO = portalPublicoAtivo;
	}
	
}
