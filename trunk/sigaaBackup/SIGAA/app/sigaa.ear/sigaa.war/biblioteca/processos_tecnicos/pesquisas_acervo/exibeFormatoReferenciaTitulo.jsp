

<%-- P�gina que repesenta uma aba da p�gina de informa��es completas de um t�tulo catalogr�fico --%>

<div style="margin-top:20px; margin-left:100px; text-align:justify; width:770px; overflow: hidden; ">
	${geraInformacoesBibliograficasTituloMBean.obj.formatoReferencia}
</div>

<%-- posibilita o usu�rio imprimir o formato de refer�ncia --%>
<table style="border:none; width: 10%;  margin-top: 10px; margin-left:auto; margin-right:auto;">
	<tr>
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