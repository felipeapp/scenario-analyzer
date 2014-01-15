<%@page import="br.ufrn.arq.util.AmbienteUtils"%>
</div> <%-- Fim do div relatorio  --%>
	<div class="clear"> </div>
	<br/>
	<div id="relatorio-rodape">
		<p>
			<table width="100%">
				<tr>
					<td class="voltar" align="left"><a href="javascript:history.back();"> Voltar </a></td>
					<td width="70%"  align="center">
					${ configSistema['siglaSigaa']} | ${ configSistema['nomeResponsavelInformatica']} - ${ configSistema['telefoneHelpDesk'] } | Copyright &copy; <%= br.ufrn.arq.util.UFRNUtils.getCopyright(2006) %> - ${configSistema['siglaInstituicao']} - <%= br.ufrn.arq.util.AmbienteUtils.getNomeServidorComInstancia() %>
					</td>
					<td class="naoImprimir" align="right">
						<a onclick="javascript:window.print();" href="#">Imprimir</a>
					</td>
					<td class="naoImprimir" align="right">
						<a onclick="javascript:window.print();" href="#">							
							<img alt="Imprimir" title="Imprimir" src="/shared/javascript/ext-1.1/docs/resources/print.gif"/>
						</a>
					</td>
				</tr>
			</table>
		</p>

	</div>
</div>  <%-- Fim do div 'container' --%>

</body>
</html>
