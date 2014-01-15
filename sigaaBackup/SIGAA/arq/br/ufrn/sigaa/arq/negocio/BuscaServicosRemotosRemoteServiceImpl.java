/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.sigaa.arq.negocio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jws.WebService;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import br.ufrn.integracao.dto.ServicoRemotoDTO;
import br.ufrn.integracao.interfaces.BuscaServicosRemotosRemoteService;

/**
 * Implementação do serviço remoto para listagem de todos
 * os serviços remotos dos sistemas.
 * @author David Pereira
 *
 */
@WebService
public class BuscaServicosRemotosRemoteServiceImpl implements BuscaServicosRemotosRemoteService, ApplicationContextAware {

	private ApplicationContext ac;
	
	@Override @SuppressWarnings("unchecked")
	public List<ServicoRemotoDTO>[] listarServicosRemotos() {
		@SuppressWarnings("rawtypes")
		List[] listas = new List[2];
		listas[0] = new ArrayList<ServicoRemotoDTO>();
		listas[1] = new ArrayList<ServicoRemotoDTO>();
		
		Map<String, HttpInvokerServiceExporter> beansExporter = ac.getBeansOfType(HttpInvokerServiceExporter.class);
		for (Entry<String, HttpInvokerServiceExporter> entry : beansExporter.entrySet()) {
			ServicoRemotoDTO dto = new ServicoRemotoDTO();
			dto.setNomeBean(entry.getKey());
			dto.setNomeInterface(entry.getValue().getServiceInterface().getSimpleName());
			dto.setNomeImpl(entry.getValue().getService().getClass().getName());
			listas[0].add(dto);
		}
		
		Map<String, HttpInvokerProxyFactoryBean> beansInvoker = ac.getBeansOfType(HttpInvokerProxyFactoryBean.class);
		for (Entry<String, HttpInvokerProxyFactoryBean> entry : beansInvoker.entrySet()) {
			ServicoRemotoDTO dto = new ServicoRemotoDTO();
			dto.setNomeBean(entry.getKey());
			dto.setNomeInterface(entry.getValue().getServiceInterface().getSimpleName());
			dto.setUrl(entry.getValue().getServiceUrl());
			listas[1].add(dto);
		}
		
		return listas;
	}

	@Override
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		this.ac = ac;
	}
	
}
