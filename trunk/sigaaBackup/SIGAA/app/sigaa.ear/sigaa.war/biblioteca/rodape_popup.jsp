<%@page import="br.ufrn.arq.util.AmbienteUtils"%>

<%-- Rodape para ser incluido em pagina de popup  --%>

</div> <%-- Fim do div 'conteudo-popup' --%>
		
		<div class="clear"> </div>
			<br/><br/>
			<center>
				<br/>
				<a href="javascript:window.close();" class="naoImprimir" id="close"><img src="<%= request.getContextPath() %>/img/fechar.jpg" width="85" height="16" alt="Fechar" border="0"/></a>
			</center>
			<br/><br/><br/>
			
			<div id="rodape" class="naoImprimir">
				<p>	${ configSistema['siglaSigaa']} | ${ configSistema['nomeResponsavelInformatica']} - ${ configSistema['telefoneHelpDesk'] } | Copyright &copy; <%= br.ufrn.arq.util.UFRNUtils.getCopyright(2006) %> - ${configSistema['siglaInstituicao']} - <%= br.ufrn.arq.util.AmbienteUtils.getNomeServidorComInstancia() %></p>
			</div>
			
	</div>  <%-- Fim do div 'container-popup' --%>
	

	</body>
	
</html>