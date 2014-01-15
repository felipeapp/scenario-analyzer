/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 01/09/2004
 */
package br.ufrn.arq.dominio;

import java.io.Serializable;
import java.util.List;

import org.springframework.context.ApplicationContext;

import br.ufrn.arq.dao.ClosableResource;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Esta interface é implementada por todos os objetos ativadores
 * de comando.
 *
 * @author Gleydson Lima
 */
public interface Movimento extends Serializable, PersistDB {

	/**
	 * Retorna o código de comando para escolha do processador
	 * @return
	 */
    public Comando getCodMovimento();

    public UsuarioGeral getUsuarioLogado();

    public void setUsuarioLogado(UsuarioGeral usuario);

    public int getSubsistema();
    
    public void setSubsistema(int subsistema);

    public int getSistema();
    
    public void setSistema(int sistema);
    
    public ApplicationContext getApplicationContext();
    
    public void setApplicationContext(ApplicationContext applicationContext);

	public void setComandosBloqueados(List<Comando> comandosBloqueados);

	public List<Comando> getComandosBloqueados();
	
	public List<ClosableResource> getClosableResources();
	
	public void addClosableResource(ClosableResource resource);

}
