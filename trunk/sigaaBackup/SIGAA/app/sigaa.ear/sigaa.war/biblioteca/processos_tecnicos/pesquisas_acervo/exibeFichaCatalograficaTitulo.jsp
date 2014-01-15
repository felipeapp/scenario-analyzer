

<%-- 
-- Página que repesenta a aba que imprime a ficha catalográfica
-- Observação:  qualquer coisa que alterar nessa página, mudar também na página pública paginaPublicaVisualizaFormatosBibliograficoTitulo.jsp
-- que é uma cópia dessa.
--%>

<style>
	.indent1{
		text-indent:25px;
	}
	
	.indent3{
		text-indent:50px;
	}
	
	.indent4{
		text-indent:-25px;
		margin-left:25px;
	}
</style>

<div style="width:4.5cm; height:7.5cm; margin-left:auto; margin-right:auto; float: left;" class="naoImprimir">
<table style="border:none; width: 100%; height: 100%">
	<tr>
		<td style="vertical-align:center; text-align:right; font-weight: bold;">
		7.5cm
		</td>
	</tr>
</table>
</div>

<div style="border:1px solid #000000; width:12.5cm; min-height:7.5cm; margin-left: 6.5cm; margin-right:auto;  "> <%-- retirando por overflow: hidden; enquanto  --%>
	
	<table style="border:none; font-family: monospace; font-size: 12px;">
		<tr>
			<td id="margin_esquerda_ficha" style="width: 0.1cm;"></td>
			
			<td style="vertical-align:top;text-align:left;width:1.5cm;">${geraInformacoesBibliograficasTituloMBean.obj.numeroChamada}</td>
			
			<td id="margin_separacao_classificacao_dados" style="width: 0.1cm;"></td>
			
			<td style="vertical-align:top;text-align:left; width:10.8cm;"> <%-- A primeira margem começa a 1cm da borda--%>
				<br>                                                                     <%-- Sempre começa um uma linham em branco antes --%>
				${geraInformacoesBibliograficasTituloMBean.obj.fichaCatalografica}
			</td>
			
			<td id="margin_direita_ficha" style="width: 10px;"></td>
		</tr>
		
	</table>
	
</div>



<div style="width:12.5cm; margin-left:auto;margin-right:auto; text-align: center; font-weight: bold; margin-top: 2cm;" class="naoImprimir">
12.5cm
</div>

<%-- posibilita o usuário imprimir a ficha catalográfica --%>
<table style="border:none; width: 10%;  margin-top: 30px; margin-left:auto;margin-right:auto;">
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
