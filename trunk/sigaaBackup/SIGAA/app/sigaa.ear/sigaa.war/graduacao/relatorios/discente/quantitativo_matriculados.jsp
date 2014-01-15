<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555; font-weight: bold; font-size: 14px;}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.foot td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee; font-weight: bold; font-size: 13px; }
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>
<f:view>
	<%@include file="/graduacao/relatorios/cabecalho_curso.jsp"%>

	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: ${ fn:length(resultado) }</b></caption>
	<c:set var="cursoLoop"/>
    <c:set var="municipioLoop"/>
    <c:set var="turnoLoop"/>
    <c:set var="total " value="0"/>
    <c:set var="totalGrupo" value="0"/>
    
    <tr class="header">
		<td><b>Curso/Turno/Cidade/Grau Acadêmico</b></td>
		<td style="text-align: right;"><b>Quantidade de Alunos</b></td>
	</tr>
    
	<c:forEach items="${resultado}" var="linha" varStatus="row">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			<c:set var="municipioAtual" value="${linha.id_municipio}"/>
			<c:set var="turnoAtual" value="${linha.id_turno}"/>

		<c:if test="${cursoLoop != cursoAtual || municipioLoop != municipioAtual  || turnoLoop != turnoAtual}">
			<c:if test="${not empty cursoLoop}">
	    		<tr class="foot">
	    			<td>Total:</td> <td style="text-align: right;">${totalGrupo}</td>
	    		</tr>
			</c:if>
			<c:set var="totalGrupo" value="0"/>
			<c:set var="cursoLoop" value="${cursoAtual}"/>
			<c:set var="municipioLoop" value="${municipioAtual}"/>
			<c:set var="turnoLoop" value="${turnoAtual}"/>
			<tr class="curso">
				<td colspan="2"> ${linha.centro}	-  ${linha.curso_nome} - ${linha.turno_sigla } - ${linha.municipio_nome } </td>
			</tr>
		</c:if>
		 <c:set var="totalGrupo" value="${totalGrupo + linha.qtd_matriculados}"/>
		 <c:set var="total" value="${total + linha.qtd_matriculados}"/>
		<tr class="componentes">
			<td> ${linha.modalidade_aluno} </td>
			<td style="text-align: right;"> ${linha.qtd_matriculados} </td>
		</tr>
	</c:forEach>
   		<tr class="foot">
   			<td>Total:</td> <td style="text-align: right;">${totalGrupo}</td>
   		</tr>
   		<tr class="foot">
   			<td>Total Geral:</td> <td style="text-align: right;">${total}</td>
   		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
