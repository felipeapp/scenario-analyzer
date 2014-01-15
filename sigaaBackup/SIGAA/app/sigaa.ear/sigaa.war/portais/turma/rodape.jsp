</div>

<div class="clear"> </div>
<c:if test="${param.ajaxRequest == null}">
</div> <%-- Fim do div 'conteudo' --%>
	<br />

	<div id="rodape">
		<p>	${ configSistema['siglaSigaa']} | ${ configSistema['nomeResponsavelInformatica']} - ${ configSistema['telefoneHelpDesk'] } | Copyright &copy; <%= br.ufrn.arq.util.UFRNUtils.getCopyright(2006) %> - ${configSistema['siglaInstituicao']} - <%= br.ufrn.arq.util.AmbienteUtils.getNomeServidorComInstancia() %></p>
	</div>


</div>  <%-- Fim do div 'container' --%>

<div id="painel-mensagem-envio"> </div>

</c:if>

</body>
</html>