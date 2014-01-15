<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<script type="text/javascript" src="/shared/javascript/ext-1.1/adapter/jquery/jquery.js"></script>
<script type="text/javascript" charset="ISO-8859">
					var J = jQuery.noConflict();
</script>
<style>
<!--
table.borda tr td {border: 1px solid #888 ; }
tr.componentes td {padding: 2px ; border-bottom: 1px dashed #888}
tr td.centro {font-weight: bold; text-align: center;}
tr.itemRel td {padding: 1px 0 0 ;}
-->
</style>
<f:view>
	<hr>
	<h:outputText value="#{relatoriosPlanejamento.create}"/>
	<h2>Relação das disciplinas com maiores índices de reprovação por Centro/Unidade Acadêmica</h2>
	<div id="parametrosRelatorio">
		<table>
			<tr>
				<td>Centro/Unidade Acadêmica:</td>
				<td><b>${empty relatoriosPlanejamento.centro.nome ? 'TODOS' : relatoriosPlanejamento.centro.nome}</b></td>
			</tr>
			<tr>
				<td>Período:</td>
				<td><b><h:outputText value="#{relatoriosPlanejamento.anoInicio}"/> - <h:outputText value="#{relatoriosPlanejamento.anoFim}"/></b></td>
			</tr>
		</table>
	</div>
	<br/>
	<table class="borda" cellspacing="1" width="100%" style="font-size: 10px;">
	
		<c:set var="idCentro" value="0"/>
		<c:set var="idDepartamento" value="0"/>
		<c:set var="idDisciplina" value="0"/>		
		<c:forEach var="item" items="${relatoriosPlanejamento.listaReprovacoes}">
		
			<c:if test="${item.centro.id != idCentro }">
			
				<tr>
					<td class="centro" colspan="6">${ item.centro.nome }</td>
				</tr>
				<tr class="linhaCinza">
					<th style="text-align: left;border: 1px solid #888 ; "><b>Componente Curricular</b></th>
					<th style="text-align: right;border: 1px solid #888 ;"><b>Qtd Matriculados</b></th>
					<th style="text-align: right;border: 1px solid #888 ;"><b>Qtd Reprovados</b></th>
					<th style="text-align: right;border: 1px solid #888 ;"><b>Percentual</b></th>
				</tr>			
			
			</c:if>		
			
			<c:if test="${item.departamento.id != idDepartamento }">			
				<tr class="linhaCinza">
					<td colspan="7" class="centro">${ item.departamento.nome }</td>
				</tr>						
			</c:if>				
			
			<c:if test="${item.disciplina.id != idDisciplina }">			
				<tr class="componentes" >
					<td style="padding-left: 10px">${ item.disciplina.nome }</td>
					<td align="center">${ item.total }</td>
					<td align="center">${ item.reprovados }</td>
					<td align="right" class="percentuais" nowrap="nowrap"><span><fmt:formatNumber pattern="#0.00" value="${ item.percentual }"/></span>%</td>
				</tr>						
			</c:if>									

			<c:set var="idCentro" value="${item.centro.id}"/>
			<c:set var="idDepartamento" value="${ item.departamento.id }"/>
			<c:set var="idDisciplina" value="${ item.disciplina.id }"/>	

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