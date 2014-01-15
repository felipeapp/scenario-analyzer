<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
<!--
table.programaRelatorio caption, tituloPrograma {
	font-size: 1.3em;
	font-weight: bold;
	color: #222;
	font-variant: small-caps;
	letter-spacing: 2px;
	padding: 3px;
	border-bottom: 2px solid #444;
}

table.programaRelatorio th{
	font-weight: bold;
	font-size: 1.0em;
	text-align: left;
	padding: 15px 0 3px;
}

table.programaRelatorio td{
	font-size: 1.0em;
	text-align: left;
	padding: 10px 0 3px;
}

table.programaRelatorio td.itemPrograma {
	padding: 8px 10px;
}
-->
</style>
<f:view>
	<table class="visualizacao" align="center">
		<tr>
			<th width="30%">Componente Curricular:</th>
			<td>${programaComponente.obj.componenteCurricular.codigo} -
				${programaComponente.obj.componenteCurricular.nome}</td>
		</tr>
		<tr>
			<th>Créditos:</th>
			<td>${programaComponente.obj.componenteCurricular.crTotal} créditos</td>
		</tr>
		<tr>
			<th>Carga Horária:</th>
			<td>${programaComponente.obj.componenteCurricular.chTotal} horas</td>
		</tr>
		<tr>
			<th>Unidade Responsável:</th>
			<td>${programaComponente.obj.componenteCurricular.unidade.nome}</td>
		</tr>
		<tr>
			<th>Tipo do Componente:</th>
			<td>${programaComponente.obj.componenteCurricular.tipoComponente.descricao}</td>
		</tr>
		<tr>
			<th>Ementa:</th>
			<td>
				<c:choose>
					<c:when test="${not empty programaComponente.obj.componenteCurricular.detalhes.ementa}">
						<ufrn:format type="texto" name="programaComponente" property="obj.componenteCurricular.detalhes.ementa" />
					</c:when>
					<c:otherwise>
						<i> ementa não cadastrada </i>
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
	</table>
	
	<br />
	<h3 align="center">Dados do Programa</h3>
	<hr />
	<br />
	<table class="programaRelatorio" width="100%;">
		<tr>
			<td> 
				<b>Ano-Período:</b> &nbsp;	
				${programaComponente.obj.anoPeriodo}
			</td>
		</tr>
		<tr>
			<td><b>Quantidade de Avaliações:</b> &nbsp;
				${programaComponente.obj.numUnidades}
			</td>
		</tr>
		<tr>
			<th colspan="2">Objetivos:</th>
		</tr>
		<tr>
			<td colspan="2" class="itemPrograma"> 
				<ufrn:format type="texto" name="programaComponente" property="obj.objetivos" />
			</td>
		</tr>
		<tr>
			<th colspan="2">Conteúdo:</th>
		</tr>
		<tr>
			<td colspan="2" class="itemPrograma">
				<ufrn:format type="texto" name="programaComponente" property="obj.conteudo" />
			</td>
		</tr>
		<tr>
			<th colspan="2">Competências e Habilidades:</th>
		</tr>
		<tr>
			<td colspan="2" class="itemPrograma">
				<ufrn:format type="texto" name="programaComponente" property="obj.competenciasHabilidades" />
			</td>
		</tr>
<%-- 
		<tr>
			<th colspan="2">Metodologia </th>
		</tr>
		<tr>
			<td colspan="2" class="itemPrograma"><ufrn:format type="texto" name="programaComponente" property="obj.metodologia"/></td>
		</tr>
		<tr>
			<th colspan="2">Avaliação </th>
		</tr>
		<tr>
			<td colspan="2" class="itemPrograma"><ufrn:format type="texto" name="programaComponente" property="obj.procedimentosAvaliacao"/></td>
		</tr>
		<tr>
			<th colspan="2">Referências </th>
		</tr>
		<tr>
			<td colspan="2" class="itemPrograma"><ufrn:format type="texto" name="programaComponente" property="obj.referencias"/></td>
		</tr>
--%>
	</table>
</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
