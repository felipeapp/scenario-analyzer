/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
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
 * Classe que representa um tipo de v�nculo que um usu�rio pode ter
 * com a universidade. Objetos desta classe n�o s�o persistidos, s�o utilizado
 * apenas na hora do logon para que o usu�rio decida qual dos seus v�nculos
 * ele ir� utilizar na sess�o.
 *
 * @author David Pereira
 *
 */
@SuppressWarnings("serial")
public class VinculoUsuario implements Serializable {

	/**
	 * Tipo do v�nculo
	 */
	private TipoVinculo tipoVinculo;

	/**
	 * N�mero do v�nculo do usu�rio.
	 */
	private int numero;

	/**
	 * Indica se o v�nculo � priorit�rio ou n�o.
	 */
	private boolean prioritario;

	/**
	 * Unidade do v�nculo.
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
	 * Retorna o discente vinculado ao usu�rio.
	 * 
	 * @return Discente vinculado ao usu�rio.
	 */
	public DiscenteAdapter getDiscente() {
		if (tipoVinculo.isDiscente())
			return ((TipoVinculoDiscente) tipoVinculo).getDiscente();
		return null;
	}
	
	/**
	 * Retorna o discente vinculado ao usu�rio do familiar logado.
	 * 
	 * @return Discente vinculado ao usu�rio.
	 */
	public UsuarioFamiliar getFamiliar() {
		if (tipoVinculo.isFamiliar())
			return ((TipoVinculoFamiliar) tipoVinculo).getUsuarioFamiliar();
		return null;
	}	

	/**
	 * Retorna o servidor vinculado ao usu�rio.
	 * 
	 * @return Servidor vinculado ao usu�rio.
	 */
	public Servidor getServidor() {
		if (tipoVinculo.isServidor())
			return ((TipoVinculoServidor) tipoVinculo).getServidor();
		
		return null;
	}

	/**
	 * Retorna o docente externo vinculado ao usu�rio.
	 * 
	 * @return Docente externo vinculado ao usu�rio.
	 */
	public DocenteExterno getDocenteExterno() {
		if (tipoVinculo.isDocenteExterno())
			return ((TipoVinculoDocenteExterno) tipoVinculo)
					.getDocenteExterno();

		return null;
	}

	/**
	 * Retorna o coordenador de p�lo vinculado ao usu�rio.
	 * 
	 * @return Coordenador de p�lo vinculado ao usu�rio.
	 */
	public CoordenacaoPolo getCoordenacaoPolo() {
		if (tipoVinculo.isCoordenacaoPolo())
			return ((TipoVinculoCoordenadorPolo) tipoVinculo).getCoordenacao();

		return null;
	}

	/**
	 * Retorna o Concedente de Est�gio vinculado ao usu�rio.
	 * 
	 * @return Concedente de Est�gio vinculado ao usu�rio.
	 */
	public ConcedenteEstagioPessoa getConcedenteEstagioPessoa() {
		if (tipoVinculo.isConcedenteEstagio())
			return ((TipoVinculoConcedenteEstagio) tipoVinculo).getConcedente();

		return null;
	}

	/**
	 * Retorna o tutor EAD vinculado ao usu�rio.
	 * 
	 * @return Tutor EAD vinculado ao usu�rio.
	 */
	public TutorOrientador getTutor() {
		if (tipoVinculo.isTutor())
			return ((TipoVinculoTutor) tipoVinculo).getTutor();

		return null;
	}
	
	
	/**
	 * Retorna o tutor IMD vinculado ao usu�rio.
	 * 
	 * @return Tutor EAD vinculado ao usu�rio.
	 */
	public TutoriaIMD getTutoriaIMD() {
		if (tipoVinculo.isTutorIMD())
			return ((TipoVinculoTutorIMD) tipoVinculo).getTutoria();

		return null;
	}
	
	/** 
	 * Indica se o v�nculo � de discente.
	 * @return
	 */
	public boolean isVinculoDiscente() {
		return tipoVinculo.isDiscente();
	}

	/** 
	 * Indica se o v�nculo � de servidor.
	 * @return
	 */
	public boolean isVinculoServidor() {
		return tipoVinculo.isServidor();
	}

	/** 
	 * Indica se o v�nculo � somente de servidor.
	 * @return
	 */
	public boolean isSomenteVinculoServidor() {
		return tipoVinculo.isServidor() && !tipoVinculo.isResponsavel();
	}	
	
	/**
	 * Indica se o v�nculo � de secretaria.
	 * @return
	 */
	public boolean isVinculoSecretaria() {
		return tipoVinculo.isSecretario();
	}
	
	/** 
	 * Indica se o v�nculo � de docente externo. 
	 * @return
	 */
	public boolean isVinculoDocenteExterno() {
		return tipoVinculo.isDocenteExterno();
	}

	/** 
	 * Indica se o v�nculo � de tutor EAD.
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
	 * Indica se o v�nculo � de coordenador de p�lo. 
	 * @return
	 */
	public boolean isVinculoCoordenacaoPolo() {
		return tipoVinculo.isCoordenacaoPolo();
	}
	
	/**
	 *  Indica se o v�nculo � de Concedente de Est�gio. 
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
	 * Retorna uma descri��o textual do tipo de v�nculo (servidor, Discente, etc.).
	 * @return
	 */
	public String getTipo() {
		return tipoVinculo.getTipo();
	}

	/** 
	 * Retorna o identificador do v�nculo associado. Ex.: No caso de servidor, retorna o SIAPE. No caso de discente, retorna a matr�cula.
	 * @return
	 */
	public Object getIdentificador() {
		return tipoVinculo.getIdentificador();
	}

	/** Indica se o v�nculo � ativo.
	 * @return
	 */
	public boolean isAtivo() {
		return tipoVinculo.isAtivo();
	}

	/** 
	 * Retorna uma descri��o textual de outras informa��es do v�nculo. Ex.: unidade, institui��o, p�lo, curso.
	 * @return
	 */
	public String getOutrasInformacoes() {
		return tipoVinculo.getOutrasInformacoes();
	}

	/**
	 * Buscar todos os poss�veis v�nculos de um usu�rio com a universidade.
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
	 * Popula o v�nculo escolhido como ativo
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
	 * Orderna os v�nculos
	 * 
	 * @param vinculos
	 */
	public static void ordenarVinculos(List<VinculoUsuario> vinculos) {
		
		// Agrupa pela ordem de import�ncia
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
	 * Retorna a unidade associada ao usu�rio do v�nculo. 
	 * @return
	 */
	public Unidade getUnidade() {
		return unidade;
	}
	
	/** 
	 * Retorna a unidade do respons�vel vinculado ao usu�rio.
	 * @return Respons�vel por unidade vinculado ao usu�rio.
	 */
	public UnidadeGeral getUnidadeResponsavel() {
		if (tipoVinculo.isResponsavel()) {
			return getResponsavel().getUnidade();
		}
		return null;
	}

	/** 
	 * Indica se o v�nculo � nulo. O v�nculo � dito nulo quando o n�mero do v�nculo � igual a zero.
	 * @return
	 */
	public boolean isNull() {
		return numero == 0;
	}
	
	/** 
	 * Indica se o v�nculo � nulo. O v�nculo � dito nulo quando o n�mero do v�nculo � igual a zero.
	 * @return
	 */
	public boolean isNotNull() {
		return !isNull();
	}	
	
	/** 
	 * Retorna o Respons�vel por unidade vinculado ao usu�rio.
	 * @return Respons�vel por unidade vinculado ao usu�rio.
	 */
	public Responsavel getResponsavel() {
		if (tipoVinculo.isResponsavel())
			return ((TipoVinculoResponsavel) tipoVinculo).getResponsavel();
		
		return null;
	}
	
	/**
	 * Retorna um vinculo vazio. Utilizado quando o usu�rio n�o possui nenhum v�nculo.
	 * @throws DAOException 
	 */
	public static VinculoUsuario nenhumVinculo(Unidade unidade) {
		VinculoUsuario vinculo = new VinculoUsuario(new TipoVinculoGenerico());
		vinculo.unidade = unidade;
		return vinculo;
	}

	/** Retorna uma secretaria se o tipo de v�nculo do usu�rio for secret�rio. */
	public SecretariaUnidade getSecretariaUnidade() {
		if (tipoVinculo.isSecretario())
			return ((TipoVinculoSecretario) tipoVinculo).getSecretaria();
		
		return null;
	}

	public TipoVinculo getTipoVinculo() {
		return tipoVinculo;
	}
	
}
