<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib prefix="cewolf" uri="/tags/cewolf" %>
<script type="text/javascript" src="/shared/javascript/ext-1.1/adapter/jquery/jquery.js"></script>
<script type="text/javascript" charset="ISO-8859">
					var J = jQuery.noConflict();
					var V = jQuery.noConflict();
</script>
<h2> Quantitativo Geral de Alunos Matriculados Por Mês</h2>
<f:view>
	<div id="parametrosRelatorio">
		<table>
			<tr>
				<th> Ano: </th>
				<td>
					${relatoriosStricto.ano}
				</td>
			</tr>	
		</table>
	</div>

	<c:set var="lista" value="#{relatoriosStricto.listaDiscente}"/>
	
	<br/>
	

	<table class="tabelaRelatorioBorda" align="center" width="100%">
		<thead>
			<tr>
				<th colspan="14" style="text-align: center;">Quantidade por Mês</th>
			</tr>
			<tr>
				<th width="100px" style="text-align: right;"></th>
				<th width="35px" style="text-align: right;">Jan.</th>
				<th width="35px" style="text-align: right;">Fev.</th>
				<th width="35px" style="text-align: right;">Mar.</th>
				<th width="35px" style="text-align: right;">Abr.</th>
				<th width="35px" style="text-align: right;">Mai.</th>
				<th width="35px" style="text-align: right;">Jun.</th>
				<th width="35px" style="text-align: right;">Jul.</th>
				<th width="35px" style="text-align: right;">Ago.</th>
				<th width="35px" style="text-align: right;">Set.</th>
				<th width="35px" style="text-align: right;">Out.</th>
				<th width="35px" style="text-align: right;">Nov.</th>
				<th width="35px" style="text-align: right;">Dez.</th>
			</tr>
		</thead>
		<tr>
			<td style="font-weight: bold;">Acumulado¹</td>
			<c:forEach var="resultado" items="#{lista}">						    
				<c:if test="${mes == null}">
			    	<c:set var="mes" value="1"/>
			    	<c:set var="TotalMeses" value="0"/>			
				</c:if>		    			    	
					    	
			    <c:forEach begin="${mes}" end="${resultado.mes -1}" step="1">
			    	<td style="text-align: right;">0</td>
			        <c:set var="mes" value="#{mes + 1}"/>
			    </c:forEach>			    			    				
				    	
				<c:set var="mes" value="#{mes + 1}"/>				    	
	
		    	<c:set var="TotalGeral" value="#{TotalGeral + resultado.total}"/>
		    		    			    			    		    			    
			    <td style="text-align: right;"><h:outputText value="#{TotalGeral}"/></td>
			</c:forEach>
			
			<c:if test="${mes < 12}">
			    <c:forEach begin="${mes}" end="12" step="1">
			    	<td style="text-align: right;">0</td>
			    </c:forEach>									
			</c:if>
			
		</tr>
		<tr id="linha">
			<td style="font-weight: bold;">Matrículas no Mês²</td>
			<c:forEach var="resultado" items="#{lista}">						    
				<c:if test="${mes == null}">
			    	<c:set var="mes" value="1"/>
			    	<c:set var="TotalMeses" value="0"/>			
				</c:if>		    			    	
					    	
			    <c:forEach begin="${mes}" end="${resultado.mes -1}" step="1">
			    	<td style="text-align: right;">0</td>
			        <c:set var="mes" value="#{mes + 1}"/>
			    </c:forEach>			    			    				
				    	
				<c:set var="mes" value="#{mes + 1}"/>				    	
	
		    	<c:set var="TotalGeral" value="#{resultado.total}"/>
		    		    			    			    		    			    
			    <td style="text-align: right;"><h:outputText value="#{TotalGeral}"/></td>
			</c:forEach>
			
			<c:if test="${mes < 12}">
			    <c:forEach begin="${mes}" end="12" step="1">
			    	<td style="text-align: right;">0</td>
			    </c:forEach>									
			</c:if>
			
		</tr>
		<tr id="linhaAtivo">
			<td style="font-weight: bold;">Ativos no mês³</td>
			<c:forEach var="resultado" items="#{lista}">						    
				<c:if test="${mes == null}">
			    	<c:set var="mes" value="1"/>
			    	<c:set var="TotalMeses" value="0"/>			
				</c:if>		    			    	
					    	
			    <c:forEach begin="${mes}" end="${resultado.mes -1}" step="1">
			    	<td style="text-align: right;">0</td>
			        <c:set var="mes" value="#{mes + 1}"/>
			    </c:forEach>			    			    				
				    	
				<c:set var="mes" value="#{mes + 1}"/>				    	
	
		    	<c:set var="ativo" value="#{resultado.ativo}"/>
		    		    			    			    		    			    
			    <td style="text-align: right;"><h:outputText value="#{ativo}"/></td>
			</c:forEach>
			
			<c:if test="${mes < 12}">
			    <c:forEach begin="${mes}" end="12" step="1">
			    	<td style="text-align: right;">0</td>
			    </c:forEach>									
			</c:if>
		</tr>
		<tr>
			<td colspan="14">
				<br/>
				<p>¹Total acumulado de matrículas considerando meses anteriores.</p>
				<p>²Quantidade de alunos que fizeram a primeira matrícula no mês.</p>
				<p>³Quantidade de alunos ativos no mês considerando as datas de ingresso e saída/cancelamento.</p>
				<p>Obs.: Os meses com maiores valores encontram-se em destaque no relatório.</p>
			</td>
		</tr>
	</table>
	
	<jsp:useBean id="dados" class="br.ufrn.sigaa.ensino.stricto.relatorios.jsf.GraficoQuantitativoMatriculaMes" scope="page" /> 
	<br/>
	<center>
		<cewolf:chart id="QuantitativoMatriculaMes" type="line" xaxislabel="Mês" yaxislabel="Quantidade"> 
			<cewolf:colorpaint color="#D3E1F1"/> 
			<cewolf:data> 
				<cewolf:producer id="dados"> 
					<cewolf:param name="lista" value="${lista}"/>
				</cewolf:producer>
			</cewolf:data> 
		</cewolf:chart> 
		<cewolf:img chartid="QuantitativoMatriculaMes" renderer="/cewolf" width="640" height="250" align="center"/> 
	</center>	
		
</f:view>

<script type="text/javascript">
    var valor = -1;
    var maior; 
    
	J("#linha > td").each(function(){		
		if (parseInt( J(this).html() )  > valor){
			valor = parseInt( J(this).html() );
			maior = J(this); 
			
		} 
	});
	maior.css("background-color", "#D3E1F1");
	maior.css("font-weight", "bold");

	V("#linhaAtivo > td").each(function(){		
		if (parseInt( V(this).html() )  > valor){
			valor = parseInt( V(this).html() );
			maior = V(this); 
			
		} 
	});
	maior.css("background-color", "#D3E1F1");
	maior.css("font-weight", "bold");
	
</script>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
