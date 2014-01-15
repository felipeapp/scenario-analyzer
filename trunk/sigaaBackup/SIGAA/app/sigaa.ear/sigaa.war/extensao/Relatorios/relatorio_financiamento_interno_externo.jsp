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

	<h2><ufrn:subSistema /> > Relatório das Ações que Receberam Financiamento Interno (${siglasExtensaoMBean.siglaFundoExtensaoPadrao}) ou Financiamento Externo </h2>
	
	
	
	
	<h:form id="form">	

		<table class="formulario" width="80%">
			<caption>Consultar Relatório Gerais de Extensão</caption>
			<tbody>			

				
				<tr>
					<th class="required">Tipo da Ação:</th>
					<td>
					 	<h:selectOneMenu id="buscaTipoAcao" value="#{relatoriosAtividades.tipoAtividadeExtensao.id}">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
					 		<f:selectItems value="#{tipoAtividadeExtensao.allCombo}" />
				    	</h:selectOneMenu>	    	 
					</td>
				</tr>		


				<tr>
			    	<th class="required"> <label for="situacaoAcao"> Situação da Ação: </label> </th>
			    	<td>
				    	 <h:selectOneMenu  id="buscaSituacao" value="#{relatoriosAtividades.situacaoAtividade.id}">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
				    	 	<f:selectItems value="#{tipoSituacaoProjeto.situacoesAcaoExtensaoValidas}" />
			 			 </h:selectOneMenu>
			    	 
			    	</td>
			    </tr>


				<tr>
					<th class="required">Período de execução da ação:</th>
					<td>
						<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" value="#{relatoriosAtividades.dataInicio}" id="dataInicio" 
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é" size="10" maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"/>
						a
						<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" value="#{relatoriosAtividades.dataFim}" id="dataFim" 
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é" size="10" maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"/>
					</td>
				</tr>
				
				<tr>	    			
	    			<th > Área Temática Principal:  </th>
	    			
	    			<td>
						<h:selectOneMenu id="buscaAreaTematica" value="#{relatoriosAtividades.areaTematica.id}" 
							onfocus="javascript:$('formBuscaAtividade:selectBuscaAreaTematicaPrincipal').checked = true;"
							onchange="javascript:setarLabel(this,'nomeArea');">
							<f:selectItem itemValue="0" itemLabel=" -- SELECIONE --" />
							<f:selectItems value="#{areaTematica.allCombo}" />
						</h:selectOneMenu>
	    			</td>
	    		</tr>
	    		
	    		<tr>					
	    			
	    			<th > Edital:  </th>
	    			
	    			<td>	    	
	    	 			<h:selectOneMenu id="buscaEdital" value="#{relatoriosAtividades.edital.id}" 
	    	 				onfocus="javascript:$('formBuscaAtividade:selectBuscaEdital').checked = true;" 
			    	 		onchange="javascript:setarLabel(this,'nomeEdital');">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
	    	 				<f:selectItems value="#{editalExtensao.allCombo}" />
	    	 			</h:selectOneMenu>	    	 
	    	 		</td>
	    		</tr>
		
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
					<h:commandButton value="Gerar Relatório" action="#{ relatoriosAtividades.gerarRelatorioAcoesFinanciamentoInternoExterno }"
							id="BotaoRelatorio" /> 
					<h:commandButton value="Cancelar" action="#{ relatoriosAtividades.cancelar }"
							id="BotaoCancelar" onclick="#{confirm}" /></td>
				</tr>
			</tfoot>
		</table>
		

		<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center>		
		
		
		<c:if test="${not empty relatoriosAtividades.atividadesLocalizadas}">
		
		<br/>
		<br/>
		
		<div class="infoAltRem">			
			<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Ação de Extensão					    		    
		</div>
		<br/>
		
		<table class="listagem">
		    <caption> Ações Encontradas ( ${ fn:length(relatoriosAtividades.atividadesLocalizadas) } ) </caption>
	
		      <thead>
		      	<tr>
					<th width="10%">Código</th>
					<th width="50%">Título</th>
					<th width="20%" style="text-align:center;">Tipo de Financiamento</th>        			        	
		        	<th width="2%"></th>    	
		        </tr>
		 	</thead>		 	
		 	<tbody>		 	
	       	
	       	
	       	
	       	<c:set var="TOTAL_APENAS_INTERNO" value="0"/>
	       	<c:set var="TOTAL_APENAS_EXTERNO" value="0"/>
	       	<c:set var="TOTAL_INTERNO_EXTERNO" value="0"/>
	       	
	       	<c:forEach items="#{relatoriosAtividades.atividadesLocalizadas}" var="ativ" varStatus="status">
	       	
	       			
	       		       	 		
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	                    <td> ${ativ.codigo}</td>
	                    <td> ${ativ.projeto.titulo}</td>
	                    	                                     
	                    <td style="text-align:center;">
	                    	<c:if test="${ativ.projeto.financiamentoInterno && !ativ.projeto.financiamentoExterno }">
	                    		<font color="blue">Interno</font>
	                    		<c:set var="TOTAL_APENAS_INTERNO" value="${TOTAL_APENAS_INTERNO + 1}"/>	                    		
	                    	</c:if>
	                    	
	                    	<c:if test="${ativ.projeto.financiamentoExterno && !ativ.projeto.financiamentoInterno}">
	                    		<font color="red">Externo</font>
	                    		<c:set var="TOTAL_APENAS_EXTERNO" value="${TOTAL_APENAS_EXTERNO + 1}"/>
	                    	</c:if>	                     
	                    	
	                    	<c:if test="${ativ.projeto.financiamentoExterno && ativ.projeto.financiamentoInterno}">
	                    		Interno & Externo
	                    		<c:set var="TOTAL_INTERNO_EXTERNO" value="${TOTAL_INTERNO_EXTERNO + 1}"/>
	                    	</c:if>
	                    </td>
	                    
	                    <td width="2%">					
							<h:commandLink title="Visualizar Ação de Extensão" action="#{atividadeExtensao.view}" id="visualizarAcaooExtensao">
							    <f:param name="id" value="#{ativ.id}"/>
			                	<h:graphicImage url="/img/view.gif"/>
							</h:commandLink>
						</td>
					</tr>			       
	       </c:forEach>	          
		 	</tbody>
		 </table>
		 
		 <br/>
		 <br/>
		 
		 <table class="subFormulario" width="50%">
		    <caption style="text-align: center"> Resumo Geral</caption>
		    
		    <tr> 
		    	<td> <b> Total de Ações com Financiamento Interno: </b>  <td>
		    	<td align="right"> <b> ${TOTAL_APENAS_INTERNO + TOTAL_INTERNO_EXTERNO} </b> </td>		    
		    </tr>
		    
		    <tr> 
		    	<td> <b> Total de Ações com Financiamento Externo: </b>  <td>
		    	<td align="right"> <b> ${TOTAL_APENAS_EXTERNO + TOTAL_INTERNO_EXTERNO} </b> </td>		    
		    </tr>
		    
		    <tr> 
		    	<td> <b> Total de Ações com Financiamento Apenas Interno: </b>  <td>
		    	<td align="right"> <b> ${TOTAL_APENAS_INTERNO} </b> </td>		    
		    </tr>
		    
		    <tr> 
		    	<td> <b> Total de Ações com Financiamento Apenas Externo: </b>  <td>
		    	<td align="right"> <b> ${TOTAL_APENAS_EXTERNO} </b> </td>		    
		    </tr>
		    
		    <tr> 
		    	<td> <b> Total de Ações com Financiamento Interno & Externo: </b>  <td>
		    	<td align="right"> <b> ${TOTAL_INTERNO_EXTERNO} </b> </td>		    
		    </tr>		    
		    
		    <tr> 
		    	<td> <b> Total de Ações Financiadas: </b>  <td>
		    	<td align="right"> <b> ${TOTAL_APENAS_INTERNO + TOTAL_APENAS_EXTERNO + TOTAL_INTERNO_EXTERNO} </b> </td>		    
		    </tr>
		    
		    
		    
		 </table>
		 <br/>
		 
		 		 		 
		 </c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>