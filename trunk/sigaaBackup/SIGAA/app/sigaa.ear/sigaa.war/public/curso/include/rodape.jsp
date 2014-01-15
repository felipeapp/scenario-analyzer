<%-- rodape --%>
		 </div>
	  </div>
	</div>
	<div id="rodape">
    ${ configSistema['siglaSigaa']} | ${ configSistema['nomeResponsavelInformatica']} - ${ configSistema['telefoneHelpDesk'] } | Copyright &copy; <%= br.ufrn.arq.util.UFRNUtils.getCopyright(2006) %> - ${configSistema['siglaInstituicao']} - <%= br.ufrn.arq.util.AmbienteUtils.getNomeServidorComInstancia() %>
	</div>
	 </div>
  </div>
</div>

<script src="${ctx}/public/programa/js/jquery.js" type="text/javascript"></script>
<script src="${ctx}/public/programa/js/submenu.js" type="text/javascript"></script>
<script type="text/javascript">
       new Submenu('#menu', {
           elementsSelector: '.li-menu',
           submenuSelector: '.sub-menu'
       });
  </script>
</body>
</html>