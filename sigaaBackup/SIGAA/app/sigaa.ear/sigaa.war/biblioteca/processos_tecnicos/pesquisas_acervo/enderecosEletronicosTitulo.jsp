<%@include file="/biblioteca/cabecalho_popup.jsp"%>

<%-- Pagina que mostra as endere�os eletr�nicos que o t�tulo possuir em forma de link para o usu�rio pode clicar e abrir      --%>

<h2>  Endere�os Eletr�nicos </h2>


<style type="text/css">
	table.visualizacao {
		border-collapse: collapse;
		margin: 0 auto;
		border: 1px solid #DEDFE3;
		background-color: #EBEBEB;
	}
	
	table.visualizacao tbody {
		background-color: #F9FBFD;
	}
	
	table.visualizacao tbody tr th {
		text-align: right;
		font-weight: bold;
	}
	
	table.visualizacao tbody tr td, table.visualizacao tbody tr th  {
		padding: 3px;
	}
	
</style>

<f:view>

	<table style="visualizacao" width="90%">
		<tr>
			<td>
				<p>
					${mostraEnderecosEletronicosTitulosMBean.enderecosEletronicosFormatadosLink}
				</p>
			</td>
		</tr>
	</table>
</f:view>


<%@include file="/biblioteca/rodape_popup.jsp"%>