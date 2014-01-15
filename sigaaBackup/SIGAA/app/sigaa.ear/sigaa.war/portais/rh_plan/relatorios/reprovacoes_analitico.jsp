<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<script type="text/javascript" src="/shared/javascript/ext-1.1/adapter/jquery/jquery.js"></script>
<script type="text/javascript" charset="ISO-8859">
					var J = jQuery.noConflict();
</script>
<style>
<!--
table.borda tr td {border: 1px solid #888 ; }
tr.componentes td {padding: 2px ; border-bottom: 1px dashed #888}
tr td.centro {font-weight: bold; text-align: center}
tr.itemRel td {padding: 1px 0 0 ;}
-->
</style>
<f:view>
	<hr>
	<h:outputText value="#{relatoriosPlanejamento.create}"/>
	<table width="100%">
		<caption><b>Relação das disciplinas com maiores índices de reprovação por Centro e Departamento (Analitico)</b></caption>
		<tr class="itemRel">
			<th>Centro:</th>
			<td><b>${empty relatoriosPlanejamento.centro.nome ? 'TODOS' : relatoriosPlanejamento.centro.nome}</b></td>
		</tr>
		<tr class="itemRel">
			<th>Período:</th>
			<td><b><h:outputText value="#{relatoriosPlanejamento.anoInicio}"/> - <h:outputText value="#{relatoriosPlanejamento.anoFim}"/></b></td>
		</tr>
	</table>
	<hr>
	<table width="100%" style="font-size: 10px">
		<caption><b>Legenda</b></caption>
			<tr>
				<td><b>Tu:</b><i> Código da Turma</i> </td>
				<td><b>Tot:</b> <i>Total de alunos matriculados</i></td>
				<td><b>Tr:</b> <i>Nº de alunos trancados</i></td>
			</tr>
			<tr>
				<td><b>Ap:</b> <i>Nº de alunos aprovados</i></td>
				<td><b>Rp:</b> <i>Nº de alunos reprovados</i></td>
				<td><b>Perc:</b> <i>Percentual de reprovacoes</i></td>
			</tr>
	</table>
	<hr>
	<table class="borda" cellspacing="1" width="100%" style="font-size: 10px;">
		<c:set var="idCentro" value="0"/>
		<c:set var="idDepartamento" value="0"/>
		<c:set var="idDisciplina" value="0"/>		
		<c:forEach var="item" items="${relatoriosPlanejamento.listaReprovacoes}">
		
			<c:if test="${item.centro.id != idCentro }">
			
				<tr>
					<td class="centro" colspan="7">${ item.centro.nome }</td>
				</tr>
				<tr>
					<td align="center" width="10%"><b>Tu</b></td>
					<td>&nbsp;</td>
					<td align="center" width="5%"><b>Tot</b></td>
					<td align="center" width="5%"><b>Tr</b></td>
					<td align="center" width="5%"><b>Ap</b></td>
					<td align="center" width="5%"><b>Rp</b></td>
					<td align="center" width="5%"><b>Perc</b></td>
				</tr>			
			
			</c:if>		
			
			<c:if test="${item.departamento.id != idDepartamento }">			
				<tr>
					<td colspan="7" valign="center">${ item.departamento.nome }</td>
				</tr>						
			</c:if>				
			
			<c:if test="${item.disciplina.id != idDisciplina }">			
				<tr class="componentes" >
					<td colspan="2" style="padding-left: 10px">${ item.disciplina.nome }</td>
					<td align="center">${ item.total }</td>
					<td align="center">${ item.trancados }</td>
					<td align="center">${ item.aprovados }</td>
					<td align="center">${ item.reprovados }</td>
					<td align="right" class="percentuais" nowrap="nowrap"><span><fmt:formatNumber pattern="#0.00" value="${ item.percentual }"/></span>%</td>
				</tr>						
			</c:if>									

			<c:set var="idCentro" value="${item.centro.id}"/>
			<c:set var="idDepartamento" value="${ item.departamento.id }"/>
			<c:set var="idDisciplina" value="${ item.disciplina.id }"/>	

			<c:forEach var="t" items="${item.turmas}">
				<tr class="componentes" >
					<td style="padding-left: 20px">T${ t.codigo }</td>
					<td>${ t.docentesNomes }</td>
					<td align="center">${ t.qtdMatriculados }</td>
					<td align="center">${ t.qtdTrancados }</td>
					<td align="center">${ t.qtdAprovados }</td>
					<td align="center">${ t.qtdReprovados }</td>
					<td align="right"> <fmt:formatNumber pattern="#0.00" value="${ 100*(t.qtdReprovados/t.qtdMatriculados) }"/>% </td>
				</tr>	
			</c:forEach>
		</c:forEach>
	</table>
</f:view>

<script type="text/javascript">
    var valor = -1;
    var maior; 
	J(".percentuais > span").each(function(){		
		if (parseInt( J(this).html() )  > valor){
			valor = parseInt( J(this).html() );
			maior = J(this); 
		} 
	});
	maior.parent().parent().css("font-weight", "bold");
	
</script>


<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>