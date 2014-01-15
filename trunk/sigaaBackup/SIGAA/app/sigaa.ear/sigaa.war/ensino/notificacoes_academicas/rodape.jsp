<br/>
  </div>
  
  <div id="rodape" style="width: 990px; margin: 0 auto;">
   <p>	${ configSistema['siglaSigaa']} | ${ configSistema['nomeResponsavelInformatica']} - ${ configSistema['telefoneHelpDesk'] } | Copyright &copy; <%= br.ufrn.arq.util.UFRNUtils.getCopyright(2006) %> - ${configSistema['siglaInstituicao']} - <%= br.ufrn.arq.util.AmbienteUtils.getNomeServidorComInstancia() %></p>
  </div>

<div id="mensagem">
	<div class="x-window-header"></div>
	<div class="x-window-body"></div>
</div>

 </body>
</html>

<script type="text/javascript" src="/shared/loadScript?src=javascript/ext-2.0.a.1/ext-base.js"></script>
<script type="text/javascript" src="/shared/loadScript?src=javascript/ext-2.0.a.1/ext-all.js"></script>

<c:if test="${ sessionScope.alertErro != null }">
<script type="text/javascript">
	alert('${ sessionScope.alertErro }');
</script>
<c:remove var="alertErro" scope="session"/>
</c:if>
<script language="javascript">
Relogio.init(<%= session.getMaxInactiveInterval() / 60 %>);
</script>