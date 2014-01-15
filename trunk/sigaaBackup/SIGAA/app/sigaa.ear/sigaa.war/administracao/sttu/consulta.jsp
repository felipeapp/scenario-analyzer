<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

	<h2>Consulta personalizada para listagem da STTU</h2>
	<form action="/sigaa/arquivoSttu" method="post">
		<table class="formulario" width="600">
			<caption>Consulta personalizada</caption>
			<tr>
				<td align="center" colspan="3">
					<textarea name="consulta" cols="90"></textarea>
				</td>
			</tr>
			<tr>
				<th width="7%">Nível</th>
				<td>
					<select name="nivel">
						<option value="T">Técnico</option>
						<option value="G">Graduação</option>
						<option value="L">Lato</option>
						<option value="S">Stricto</option>
					</select>
				</td>
				<td>
					<input type="checkbox" name="log"> Gerar Arquivo Log </input>
				</td>
			</tr>
			<tfoot>
			<tr>
				<td align="center" colspan="3">
					<input type="submit" value="Gerar Lista" />
				</td>
			</tr>
			</tfoot>
		</table>
	</form>

	
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
