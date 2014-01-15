<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib prefix="cewolf" uri="/tags/cewolf" %>
<script type="text/javascript" src="/shared/javascript/ext-1.1/adapter/jquery/jquery.js"></script>
<script type="text/javascript" charset="ISO-8859">
					var J = jQuery.noConflict();
</script>
<h2> Quantitativo Geral de Defesas por Ano</h2>
<f:view>
	<div id="parametrosRelatorio">
		<table>
			<tr>
				<th> Ano Início: </th>
				<td>
					${relatoriosStricto.anoInicio}
				</td>
			</tr>
			<tr>
				<th> Ano Fim: </th>
				<td>
					${relatoriosStricto.anoFim}
				</td>
			</tr>		
		</table>
	</div>
	
	<div class="descricaoOperacao">
		Serão exibidos os quantitativos apenas dos anos que possuírem defesas.
		O Ano que estiver em destaque, é o que possui o maior número de defesas.
	</div>
		

	<c:set var="lista" value="#{relatoriosStricto.listaDiscente}"/>
		
	<br/>
	
	<table class="tabelaRelatorioBorda" align="center" width="30%">
		<thead>
			<tr>
				<th width="40px" style="text-align: center;">Ano</th>
				<th width="60px" style="text-align: right;">Quantidade</th>
			</tr>
		</thead>
		<c:forEach var="resultado" items="#{lista}">						    				
			<tr>
			    <td style="text-align: center;"><h:outputText value="#{resultado.ano}"/></td>
			    <td style="text-align: right;" class="quant"><h:outputText value="#{resultado.total}"/></td>
			    <c:set var="total" value="#{total + resultado.total}"/>
			</tr>
		</c:forEach>		
	</table>	
	
	<c:if test="${relatoriosStricto.anoInicio != relatoriosStricto.anoFim}">
		<jsp:useBean id="dados" class="br.ufrn.sigaa.ensino.stricto.relatorios.jsf.GraficoQuantitativoDefesasAno" scope="page" /> 
		<br/>
		<center>
			<cewolf:chart id="QuantitativoDefesasAno" type="verticalbar3d" xaxislabel="Ano" yaxislabel="Quantidade"> 
				<cewolf:colorpaint color="#D3E1F1"/> 
				<cewolf:data> 
					<cewolf:producer id="dados"> 
						<cewolf:param name="lista" value="${lista}"/>
					</cewolf:producer>
				</cewolf:data> 
			</cewolf:chart> 
			<cewolf:img chartid="QuantitativoDefesasAno" renderer="/cewolf" width="400" height="250"/> 
		</center>
	</c:if>		
	
</f:view>

<script type="text/javascript">
    var valor = -1;
    var maior;     
	J(".quant").each(function(){		
		if (parseInt( J(this).html() )> valor){
			valor = parseInt( J(this).html() );
			maior = J(this); 
		} 
	});
	maior.parent().css("background-color", "#D3E1F1");
	maior.parent().css("font-weight", "bold");
</script>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
