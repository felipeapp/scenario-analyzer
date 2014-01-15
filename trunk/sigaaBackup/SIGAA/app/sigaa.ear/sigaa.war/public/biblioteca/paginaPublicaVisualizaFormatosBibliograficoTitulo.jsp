<%@include file="/biblioteca/cabecalho_popup.jsp"%>

<%-- Mostra o título em formato de referência e ficha catalográfica nas buscas públicas do sistema --%>

<style>
	strong { font-weight: bold;}
</style>

<h2> Dados do Formato Bibliográfico do Título Selecionado </h2>

${geraInformacoesBibliograficasTituloMBean.gerarInformacoesTitulo}
<div style="margin-top:20px; margin-bottom:20px; margin-left:10%; text-align:justify; width:80%; font-size: 12px; font-family: Verdana,sans-serif; text-decoration: none; border: 1px solid; padding: 5px; background-color: transparent; overflow: hidden; ">
	${geraInformacoesBibliograficasTituloMBean.obj.formatoReferencia}
</div>



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
	
	/* Para fazer as tabelas da ficha ficarem com a cor de findo da linha*/
	table.listagem tbody {
    	background-color: transparent;
	}
	
</style>

<div  style="width:4.5cm; height:7.5cm; margin-left:auto; margin-right:auto; float: left;" class="naoImprimir">
	<table style="border:none; width: 100%; height: 100%;" class="none">
		<body>
			<tr>
				<td style="vertical-align:center; text-align:right; font-weight: bold;">
				7.5cm
				</td>
			</tr>
		</body>
	</table>
</div>

<div style="border:1px solid #000000; width:12.5cm; min-height:7.5cm; margin-left: 6.5cm; margin-right:auto; ">
	
	<table style="border:none; font-family: monospace; font-size: 12px;">
		<tr>
			<td id="margin_esquerda_ficha" style="width: 0.1cm;"></td>
			
			<td style="vertical-align:top;text-align:left;width:1.6cm;">${geraInformacoesBibliograficasTituloMBean.obj.numeroChamada}</td>
			
			<td id="margin_separacao_classificacao_dados" style="width: 0.1cm;"></td>
			
			<td style="vertical-align:top;text-align:left; width:10.7cm;"> <%-- A primeira margem começa a 1cm da borda  --%>
				<br>                                                                     <%-- Sempre começa um uma linham em branco antes --%>
				${geraInformacoesBibliograficasTituloMBean.obj.fichaCatalografica}
			</td>
			
			<td id="margin_direita_ficha" style="width: 10px;"></td>
		</tr>
		
	</table>
	
</div>



<div style="width:12.5cm; margin-left:auto;margin-right:auto; text-align: center; font-weight: bold; margin-top: 1cm; margin-bottom: 1cm;" class="naoImprimir">
12.5cm
</div>

<br />

	<center>
		<a href="javascript:window.close();" class="naoImprimir" id="close"><img src="<%= request.getContextPath() %>/img/fechar.jpg" width="85" height="16" alt="Fechar" border="0"/></a>
	</center>
	
<br />
<%@include file="/public/include/rodape.jsp" %>