/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/07/2011
 *
 */
package br.ufrn.sigaa.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosTurmaVirtual;

/**
 * Classe para realizar a autorização do Twitter
 * 
 * @author Eric Moura
 * @author Diego Jácome
 */
public class OAuthTwitter {
	
	/** Twitter que será autorizado o acesso. */
	private Twitter twitter;
	
	/** Inicializa a classe */
	public OAuthTwitter(){
		init();
	}
	
	/** Inicializa o Twitter. */
	private void init(){
		TwitterFactory twitterFactory = new TwitterFactory();
		twitter = twitterFactory.getInstance();
		twitter.setOAuthConsumer(ParametroHelper.getInstance().getParametro(ParametrosTurmaVirtual.TWITTER_CONSUMER_KEY), ParametroHelper.getInstance().getParametro(ParametrosTurmaVirtual.TWITTER_CONSUMER_SECRET));
	}
	
	/** Carrega as configurações e retorna o Twitter relacionad ao accessToken. */
	public static Twitter carregarConfiguracao(AccessToken accessToken){
		
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

	    configurationBuilder.setOAuthConsumerKey(ParametroHelper.getInstance().getParametro(ParametrosTurmaVirtual.TWITTER_CONSUMER_KEY));
	    configurationBuilder.setOAuthConsumerSecret(ParametroHelper.getInstance().getParametro(ParametrosTurmaVirtual.TWITTER_CONSUMER_SECRET));
	    Configuration configuration = configurationBuilder.build();
		
		TwitterFactory factory = new TwitterFactory(configuration);
		return factory.getInstance(accessToken);
	}
	
	/** Retorna o twitter. */
	public Twitter getTwitter(){
		init();
		
		return twitter;
	}	
}
