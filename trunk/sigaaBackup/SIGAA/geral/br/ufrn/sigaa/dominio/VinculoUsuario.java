/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '25/10/2007'
 * 
 */
package br.ufrn.sigaa.dominio;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.comum.dominio.Responsavel;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.vinculo.processamento.ProcessarVinculos;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculo;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoConcedenteEstagio;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoCoordenadorPolo;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoDiscente;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoDocenteExterno;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoFamiliar;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoGenerico;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoResponsavel;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoSecretario;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoServidor;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoTutor;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoTutorIMD;
import br.ufrn.sigaa.ead.dominio.CoordenacaoPolo;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.SecretariaUnidade;
import br.ufrn.sigaa.ensino.medio.dominio.UsuarioFamiliar;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutoriaIMD;
import br.ufrn.sigaa.estagio.dominio.ConcedenteEstagioPessoa;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Classe que representa um tipo de vínculo que um usuário pode ter
 * com a universidade. Objetos desta classe não são persistidos, são utilizado
 * apenas na hora do logon para que o usuário decida qual dos seus vínculos
 * ele irá utilizar na sessão.
 *
 * @author David Pereira
 *
 */
@SuppressWarnings("serial")
public class VinculoUsuario implements Serializable {

	/**
	 * Tipo do vínculo
	 */
	private TipoVinculo tipoVinculo;

	/**
	 * Número do vínculo do usuário.
	 */
	private int numero;

	/**
	 * Indica se o vínculo é prioritário ou não.
	 */
	private boolean prioritario;

	/**
	 * Unidade do vínculo.
	 */
	private Unidade unidade;
	
	public VinculoUsuario(int numero, Unidade unidadeUsuario, boolean prioritario, TipoVinculo tipoVinculo) {
		this.numero = numero;
		this.unidade = unidadeUsuario;
		this.prioritario = prioritario;
		this.tipoVinculo = tipoVinculo;
	}	

	public VinculoUsuario(TipoVinculo tipoVinculo) {
		this.tipoVinculo = tipoVinculo;
	}

	/**
	 * Retorna o discente vinculado ao usuário.
	 * 
	 * @return Discente vinculado ao usuário.
	 */
	public DiscenteAdapter getDiscente() {
		if (tipoVinculo.isDiscente())
			return ((TipoVinculoDiscente) tipoVinculo).getDiscente();
		return null;
	}
	
	/**
	 * Retorna o discente vinculado ao usuário do familiar logado.
	 * 
	 * @return Discente vinculado ao usuário.
	 */
	public UsuarioFamiliar getFamiliar() {
		if (tipoVinculo.isFamiliar())
			return ((TipoVinculoFamiliar) tipoVinculo).getUsuarioFamiliar();
		return null;
	}	

	/**
	 * Retorna o servidor vinculado ao usuário.
	 * 
	 * @return Servidor vinculado ao usuário.
	 */
	public Servidor getServidor() {
		if (tipoVinculo.isServidor())
			return ((TipoVinculoServidor) tipoVinculo).getServidor();
		
		return null;
	}

	/**
	 * Retorna o docente externo vinculado ao usuário.
	 * 
	 * @return Docente externo vinculado ao usuário.
	 */
	public DocenteExterno getDocenteExterno() {
		if (tipoVinculo.isDocenteExterno())
			return ((TipoVinculoDocenteExterno) tipoVinculo)
					.getDocenteExterno();

		return null;
	}

	/**
	 * Retorna o coordenador de pólo vinculado ao usuário.
	 * 
	 * @return Coordenador de pólo vinculado ao usuário.
	 */
	public CoordenacaoPolo getCoordenacaoPolo() {
		if (tipoVinculo.isCoordenacaoPolo())
			return ((TipoVinculoCoordenadorPolo) tipoVinculo).getCoordenacao();

		return null;
	}

	/**
	 * Retorna o Concedente de Estágio vinculado ao usuário.
	 * 
	 * @return Concedente de Estágio vinculado ao usuário.
	 */
	public ConcedenteEstagioPessoa getConcedenteEstagioPessoa() {
		if (tipoVinculo.isConcedenteEstagio())
			return ((TipoVinculoConcedenteEstagio) tipoVinculo).getConcedente();

		return null;
	}

	/**
	 * Retorna o tutor EAD vinculado ao usuário.
	 * 
	 * @return Tutor EAD vinculado ao usuário.
	 */
	public TutorOrientador getTutor() {
		if (tipoVinculo.isTutor())
			return ((TipoVinculoTutor) tipoVinculo).getTutor();

		return null;
	}
	
	
	/**
	 * Retorna o tutor IMD vinculado ao usuário.
	 * 
	 * @return Tutor EAD vinculado ao usuário.
	 */
	public TutoriaIMD getTutoriaIMD() {
		if (tipoVinculo.isTutorIMD())
			return ((TipoVinculoTutorIMD) tipoVinculo).getTutoria();

		return null;
	}
	
	/** 
	 * Indica se o vínculo é de discente.
	 * @return
	 */
	public boolean isVinculoDiscente() {
		return tipoVinculo.isDiscente();
	}

	/** 
	 * Indica se o vínculo é de servidor.
	 * @return
	 */
	public boolean isVinculoServidor() {
		return tipoVinculo.isServidor();
	}

	/** 
	 * Indica se o vínculo é somente de servidor.
	 * @return
	 */
	public boolean isSomenteVinculoServidor() {
		return tipoVinculo.isServidor() && !tipoVinculo.isResponsavel();
	}	
	
	/**
	 * Indica se o vínculo é de secretaria.
	 * @return
	 */
	public boolean isVinculoSecretaria() {
		return tipoVinculo.isSecretario();
	}
	
	/** 
	 * Indica se o vínculo é de docente externo. 
	 * @return
	 */
	public boolean isVinculoDocenteExterno() {
		return tipoVinculo.isDocenteExterno();
	}

	/** 
	 * Indica se o vínculo é de tutor EAD.
	 * @return
	 */
	public boolean isVinculoTutorOrientador() {
		return tipoVinculo.isTutor();
	}

	public boolean isVinculoTutorIMD() {
		return tipoVinculo.isTutorIMD() && 
				unidade.getId() == ParametroHelper.getInstance().getParametroInt(ParametrosTecnico.ID_UNIDADE_INSTITUTO_METROPOLE_DIGITAL);
	}
	
	/** 
	 * Indica se o vínculo é de coordenador de pólo. 
	 * @return
	 */
	public boolean isVinculoCoordenacaoPolo() {
		return tipoVinculo.isCoordenacaoPolo();
	}
	
	/**
	 *  Indica se o vínculo é de Concedente de Estágio. 
	 * @return
	 */
	public boolean isVinculoConcedenteEstagio() {
		return tipoVinculo.isConcedenteEstagio();
	}	

	public int getNumero() {
		return numero;
	}	
	
	public boolean isPrioritario() {
		return prioritario;
	}

	/** 
	 * Retorna uma descrição textual do tipo de vínculo (servidor, Discente, etc.).
	 * @return
	 */
	public String getTipo() {
		return tipoVinculo.getTipo();
	}

	/** 
	 * Retorna o identificador do vínculo associado. Ex.: No caso de servidor, retorna o SIAPE. No caso de discente, retorna a matrícula.
	 * @return
	 */
	public Object getIdentificador() {
		return tipoVinculo.getIdentificador();
	}

	/** Indica se o vínculo é ativo.
	 * @return
	 */
	public boolean isAtivo() {
		return tipoVinculo.isAtivo();
	}

	/** 
	 * Retorna uma descrição textual de outras informações do vínculo. Ex.: unidade, instituição, pólo, curso.
	 * @return
	 */
	public String getOutrasInformacoes() {
		return tipoVinculo.getOutrasInformacoes();
	}

	/**
	 * Buscar todos os possíveis vínculos de um usuário com a universidade.
	 * @param usuario
	 * @param req
	 * @return
	 * @throws ArqException
	 */
	public static List<VinculoUsuario> processarVinculosUsuario(Usuario usuario, HttpServletRequest req) throws ArqException {

		ProcessarVinculos processar = new ProcessarVinculos(usuario);
		processar.executar(req);
		
		ordenarVinculos(processar.getDados().getVinculos());
		
		return processar.getDados().getVinculos();
	}

	/**
	 * Popula o vínculo escolhido como ativo
	 * 
	 * @param usuario
	 * @throws DAOException
	 */
	public static void popularVinculoAtivo(Usuario usuario) throws DAOException {
		List<VinculoUsuario> vinculos = usuario.getVinculos();
		VinculoUsuario vinculoAtivo = usuario.getVinculoAtivo();
		
		vinculos.remove(vinculoAtivo);
		
		vinculoAtivo = vinculoAtivo.getTipoVinculo().getEstrategia().popular(vinculoAtivo);
		
		vinculos.add(vinculoAtivo);
		usuario.setVinculoAtivo(vinculoAtivo);		
		
		ordenarVinculos(vinculos);
	}
	
	/**
	 * Orderna os vínculos
	 * 
	 * @param vinculos
	 */
	public static void ordenarVinculos(List<VinculoUsuario> vinculos) {
		
		// Agrupa pela ordem de importância
		Collections.sort(vinculos, new Comparator<VinculoUsuario>() {
			
			public int compare(VinculoUsuario v1, VinculoUsuario v2) {
				if (v1.getTipoVinculo().getOrdem() > v2.getTipoVinculo().getOrdem()) {
					return 1;
				} else if (v1.getTipoVinculo().getOrdem() < v2.getTipoVinculo().getOrdem()) {
					return -1;
				} else {
					if (v1.getNumero() > v2.getNumero())
						return 1;
					else 
						return -1;
				}
			}
		});			
	}
	
	/** 
	 * Retorna a unidade associada ao usuário do vínculo. 
	 * @return
	 */
	public Unidade getUnidade() {
		return unidade;
	}
	
	/** 
	 * Retorna a unidade do responsável vinculado ao usuário.
	 * @return Responsável por unidade vinculado ao usuário.
	 */
	public UnidadeGeral getUnidadeResponsavel() {
		if (tipoVinculo.isResponsavel()) {
			return getResponsavel().getUnidade();
		}
		return null;
	}

	/** 
	 * Indica se o vínculo é nulo. O vínculo é dito nulo quando o número do vínculo é igual a zero.
	 * @return
	 */
	public boolean isNull() {
		return numero == 0;
	}
	
	/** 
	 * Indica se o vínculo é nulo. O vínculo é dito nulo quando o número do vínculo é igual a zero.
	 * @return
	 */
	public boolean isNotNull() {
		return !isNull();
	}	
	
	/** 
	 * Retorna o Responsável por unidade vinculado ao usuário.
	 * @return Responsável por unidade vinculado ao usuário.
	 */
	public Responsavel getResponsavel() {
		if (tipoVinculo.isResponsavel())
			return ((TipoVinculoResponsavel) tipoVinculo).getResponsavel();
		
		return null;
	}
	
	/**
	 * Retorna um vinculo vazio. Utilizado quando o usuário não possui nenhum vínculo.
	 * @throws DAOException 
	 */
	public static VinculoUsuario nenhumVinculo(Unidade unidade) {
		VinculoUsuario vinculo = new VinculoUsuario(new TipoVinculoGenerico());
		vinculo.unidade = unidade;
		return vinculo;
	}

	/** Retorna uma secretaria se o tipo de vínculo do usuário for secretário. */
	public SecretariaUnidade getSecretariaUnidade() {
		if (tipoVinculo.isSecretario())
			return ((TipoVinculoSecretario) tipoVinculo).getSecretaria();
		
		return null;
	}

	public TipoVinculo getTipoVinculo() {
		return tipoVinculo;
	}
	
}
