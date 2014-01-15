<%@page import="br.ufrn.arq.seguranca.SigaaSubsistemas"%>

<dl>
	<dt>
		<div class="opcao Acervo">
		<h3>Consultar Acervo</h3>
		<a href="/sigaa/public/biblioteca/buscaPublicaAcervo.jsf?aba=p-biblioteca">Consulte o acervo das bibliotecas da ${ configSistema['siglaInstituicao'] }.</a>
		</div>
	</dt>
	
	<dt>
		<div class="opcao Acervo">
		<h3>Consultar Acervo de Artigos </h3>
		<a href="/sigaa/public/biblioteca/buscaPublicaAcervoArtigos.jsf?aba=p-biblioteca">Consulte no acervo das bibliotecas da ${ configSistema['siglaInstituicao'] } artigos de revistas, jornais, entre outros.</a>
		</div>
	</dt>
		<dt>
		<div class="opcao Emprestimo">
		<h3>Consultar Empr�stimos</h3>
		<a href="/sigaa/verTelaLogin.do?urlRedirect=/sigaa/biblioteca/circulacao/visualizaMeusEmprestimos.jsf&subsistemaRedirect=<%=SigaaSubsistemas.BIBLIOTECA.getId()%>">
		Verifique aqui empr�stimos efetuados e os prazos de devolu��o. (Requer Autentica��o)
		</a>
		</div>
	</dt>
	
	<dt>
		<div class="opcao Renovar">
		<h3>Renovar Empr�stimos</h3>
		<a href="/sigaa/verTelaLogin.do?urlRedirect=/sigaa/biblioteca/circulacao/renovaMeusEmprestimos.jsf&subsistemaRedirect=<%=SigaaSubsistemas.BIBLIOTECA.getId()%>">
		Efetue a renova��o do empr�stimo. <br/>(Requer Autentica��o)
		</a>
		</div>
	</dt>
	
	<dt>
		<div class="opcao Aquisicao">
		<h3>Novas Compras</h3>
		<a href="/sigaa/public/biblioteca/buscaPublicaAquisicoes.jsf?aba=p-biblioteca">Confira as compras de livros mais recentes.</a>
		</div>
	</dt>
	
	<dt>
		<div class="opcao Aquisicao">
		<h3>Novas Aquisi��es</h3>
		<a href="/sigaa/public/biblioteca/buscaPublicaNovasAquisicoes.jsf?aba=p-biblioteca">Confira as aquisi��es de materiais mais recentes que se encontram dispon�veis no acervo.</a>
		</div>
	</dt>
	
	<dt>
		<div class="opcao Mobile">
		<h3>Biblioteca Mobile</h3>
		<%-- <a href="/sigaa/public/biblioteca/mobile.jsf"> --%>
		<a href="http://info.ufrn.br/wikisistemas/doku.php?id=suporte:manuais:biblioteca_mobile&s[]=mobile&s[]=manuais">
		Portabilidade, veja como acessar a biblioteca atrav�s de um dispositivo m�vel.
		</a>
		</div>
	</dt>

	<dt>
		<div class="opcao BDTD">
		<h3>BDTD - ${ configSistema['siglaInstituicao'] }</h3>
		<a href="http://bdtd.bczm.ufrn.br/tedesimplificado/">
		Disponibiliza��o, via Internet, as teses e disserta��es produzidas no �mbito da ${ configSistema['siglaInstituicao'] }.</a>
		</div>
	</dt>

	<dt>
		<div class="opcao IBICT">
		<h3>BDTD - Nacional (IBICT) </h3>
		<a href="http://bdtd.ibict.br/">
		Integra��o dos sistemas de informa��o de teses e disserta��es existentes no pa�s.</a>
		</div>
	</dt>
	
</dl>