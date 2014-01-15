<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>

.subtitulo {
	text-align: left;
	background: #EDF1F8;
	color: #333366;
	font-variant: small-caps;
	font-weight: bold;
	letter-spacing: 1px;
	margin: 1px 0;
	border-collapse: collapse;
	border-spacing: 2px;
	font-size: 1em;
	font-family: Verdana, sans-serif;
	font-size: 12px
}


</style>



<f:view>

	<h2><ufrn:subSistema /> > Total de A��es por Local de Realiza��o </h2>
	
	
	
	
	<h:form id="form">	

		<table class="formulario" width="60%">
			<caption>Consultar Relat�rio do Total de A��es por Localidade</caption>
			<tbody>			

				
				<tr>
					<th class="required">Local de Realiza��o:</th>
					<td>
					 	<h:selectOneMenu id="municipioRealizacao" value="#{relatoriosAtividades.localRealizacao.municipio.id}" style="width: 70%;">
							<f:selectItem itemValue="0" itemLabel=" -- SELECIONE --"/>
							<f:selectItems value="#{municipio.allAtivosCombo}"/>
						</h:selectOneMenu>
					</td>
				</tr>		


				<tr>
			    	<th class="required"> <label for="situacaoAcao"> Situa��o da A��o: </label> </th>
			    	<td>
				    	 <h:selectOneMenu  id="buscaSituacao" value="#{relatoriosAtividades.situacaoAtividade.id}">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
				    	 	<f:selectItems value="#{tipoSituacaoProjeto.situacoesAcaoExtensaoValidas}" />
			 			 </h:selectOneMenu>
			    	 
			    	</td>
			    </tr>


				<tr>
					<th class="required">Per�odo de execu��o da a��o:</th>
					<td>
						<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" value="#{relatoriosAtividades.dataInicio}" id="dataInicio" 
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje �" size="10" maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"/>
						a
						<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" value="#{relatoriosAtividades.dataFim}" id="dataFim" 
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje �" size="10" maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"/>
					</td>
				</tr>				
		
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
					<h:commandButton value="Gerar Relat�rio" action="#{ relatoriosAtividades.gerarRelatorioNominalAcoesPorLocalRealizacao }"
							id="BotaoRelatorio" /> 
					<h:commandButton value="Cancelar" action="#{ relatoriosAtividades.cancelar }"
							id="BotaoCancelar" onclick="#{confirm}" /></td>
				</tr>
			</tfoot>
		</table>
		

		<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> </center>		
		
		
		<c:if test="${not empty relatoriosAtividades.resultado}">
		
		<br/>
		<br/>	
		<br/>
		
		<table width="50%" class="formulario">
		    <caption> Locais Encontrados ( ${ fn:length(relatoriosAtividades.resultado) } ) </caption>	
		      <thead>
		      	<tr>
					<th>Local de Realizacao</th>
					<th style="text-align: center;">Total de A��es</th>					
		        </tr>
		 	</thead>		 	
		 	<tbody> 	
	       	
	       	<c:forEach items="#{relatoriosAtividades.resultado}" var="ativ" varStatus="status">      		       	 		
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	                    <td> ${ativ[0]} </td>
	                    <td style="text-align: center;"> ${ativ[1]} </td>            
					</tr>			       
	       </c:forEach>	          
		 	</tbody>
		 </table>	 		 		 		 
		 </c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>